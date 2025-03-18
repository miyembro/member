package com.rjproj.memberapp.service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.core.sync.RequestBody;
import com.rjproj.memberapp.model.ImageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileService {

    private final S3Client s3Client;

    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public FileService() {
        // Initialize AWS SDK v2 S3 Client with credentials
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(awsS3AccessKey, awsS3SecretKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))  // Set region from configuration
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))  // Provide credentials
                .build();
    }

    public String uploadImage(String entity, UUID entityId, ImageType imageType, MultipartFile file) throws IOException {
        // Get file extension
        String fileExtension = getFileExtension(file.getOriginalFilename());

        // Generate file key for S3 bucket
        String fileKey = entity + "/" + entityId + "/" + entityId + "-" + imageType.getValue() + "." + fileExtension;

        InputStream inputStream = file.getInputStream();

        // Create the PutObjectRequest
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        // Upload file to S3 using PutObjectRequest and InputStream wrapped as RequestBody
        PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

        // Return the correct S3 URL
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileKey;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
