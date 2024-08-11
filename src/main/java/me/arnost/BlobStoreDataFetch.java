package me.arnost;

import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@ApplicationScoped
public class BlobStoreDataFetch {

    private final S3Client s3;
    private final int maxScheduled;
    private final int maxErrors;
    private final String bucket;
    private final String key;

    private int scheduleCount = 0;
    private int errorCount = 0;


    private static final Logger log = LoggerFactory.getLogger(BlobStoreDataFetch.class);

    public BlobStoreDataFetch(
            S3Client s3,
            @ConfigProperty(name = "datafetch.s3.bucket") String bucket,
            @ConfigProperty(name = "datafetch.s3.key") String key) {
        this.s3 = s3;
        this.maxScheduled = 3;
        this.maxErrors = 5;
        this.bucket = bucket;
        this.key = key;
    }

    @PostConstruct
    public void initialized() {
        log.info("BlobStoreDataFetch initialized with S3 client {}, bucket {}, key {}", s3, bucket, key);
    }

    public String fetchData() throws IOException {
        GetObjectRequest build = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        ResponseInputStream<GetObjectResponse> responseInputStream = s3.getObject(build);
        byte[] bytes = responseInputStream.readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Scheduled(every = "10s", delayed = "5s")
    public void fetchItOnce() throws IOException {
        if (scheduleCount >= maxScheduled || errorCount >= maxErrors) {
            return;
        }
        if (scheduleCount == 0) {
            HashMap<Object, Object> callerIdentity = callerIdentity();
            log.info("Called identity is {}", callerIdentity);
        }
        final String fetched;
        try {
            fetched = fetchData();
        } catch (Exception e) {
            errorCount++;
            log.info("Failed to fetch data", e);
            if (errorCount == maxErrors) {
                log.warn("Reached max errrors: {}", maxErrors);
            }
            throw e;
        }
        scheduleCount++;
        log.info("Scheduled #{} fetch: {}", scheduleCount, fetched);
        if (scheduleCount == maxScheduled) {
            log.info("This was last schedule");
        }
    }


    public HashMap<Object, Object> callerIdentity() {
        // Create an STS client
        StsClient stsClient = StsClient.builder()
                .region(Region.AWS_GLOBAL)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        // Build the GetCallerIdentity request
        GetCallerIdentityRequest request = GetCallerIdentityRequest.builder().build();

        // Call the STS GetCallerIdentity API
        GetCallerIdentityResponse identityResponse = stsClient.getCallerIdentity(request);
        var response = new HashMap<>();
        response.put("account", identityResponse.account());
        response.put("arn", identityResponse.arn());

        return response;
    }
}
