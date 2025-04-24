package kr.co.pinup.posts.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.io.ByteArrayInputStream;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class S3ConfigTest {

    private S3Client s3Client;
    private final String bucketName = "pinup-test";
    private final String keyName = "test-file.txt";

    @BeforeEach
    void setUp() {
        s3Client = S3Client.builder()
                .region(Region.of("us-east-1"))
                .endpointOverride(URI.create("http://localhost:4566"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .serviceConfiguration(S3Configuration.builder()
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();

        try {
            if (!doesBucketExist(bucketName)) {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            }
        } catch (S3Exception e) {
            if (e.statusCode() != 409) {
                System.err.println("버킷 생성 중 예외 발생: " + e.awsErrorDetails().errorMessage());
                throw e;
            }
        }
    }

    @Test
    void testUploadFileToS3() throws Exception {
        String content = "This is a test file.";
        byte[] contentBytes = content.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(contentBytes);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentBytes.length));

        // 업로드 후 S3 지연 고려해 최대 10회 재시도
        boolean fileExists = false;
        int retries = 0;
        while (!fileExists && retries < 10) {
            fileExists = doesObjectExist(bucketName, keyName);
            if (!fileExists) {
                Thread.sleep(500);
                retries++;
            }
        }

        assertTrue(fileExists, "S3에 업로드된 파일이 존재해야 합니다.");
    }

    @Test
    void testFileExistsInS3() throws Exception {
        boolean fileExists = false;
        int retries = 0;
        while (!fileExists && retries < 10) {
            fileExists = doesObjectExist(bucketName, keyName);
            if (!fileExists) {
                Thread.sleep(500);
                retries++;
            }
        }

        assertTrue(fileExists, "업로드된 파일이 존재해야 합니다.");
    }

    private boolean doesBucketExist(String bucketName) {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.headBucket(headBucketRequest);
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    private boolean doesObjectExist(String bucketName, String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }
}
