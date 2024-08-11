# quarkus-s3-k8s-app

Simple application to run in AWS EKS kubernetes, access S3 object and print it's AWS identity.

Environment variables:

Override

    DATAFETCH_S3_BUCKET=my-test-bucket
    DATAFETCH_S3_KEY=file1.txt


To test locally it's possible to use Access key and Secret access key

    AWS_ACCESS_KEY_ID=...
    AWS_SECRET_ACCESS_KEY=...


Deploy in K8S:


Setup service account

- official documentation https://docs.aws.amazon.com/eks/latest/userguide/associate-service-account-role.html
- short documentation https://rahullokurte.com/how-to-connect-to-s3-from-eks-using-the-iam-role-for-the-service-account

(?Initial my-policy.json for access to bucket wrong? Should it be `"Resource": "arn:aws:s3:::my-pod-secrets-bucket/*"` ? - add `*` at the end?)


Expected log message:

    BlobStoreDataFetch initialized with S3 client software.amazon.awssdk.services.s3.DefaultS3Client@15124d4e, bucket <my-bucket>, key <object-key>
    Called identity is {arn=arn:aws:sts::<account>:assumed-role/<role>/aws-sdk-java-<123456790123456789>, account=<account>}


---


This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/s3-k8s-app-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
