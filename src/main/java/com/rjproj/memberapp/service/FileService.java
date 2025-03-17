package com.rjproj.memberapp.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.rjproj.memberapp.model.ImageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileService {

    private AmazonS3 amazonS3;

    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public FileService() {

    }

    public String uploadImage(String entity, UUID entityId, ImageType imageType, MultipartFile file) throws IOException {

        // Initialize AWS S3 Client
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        String fileExtension = getFileExtension(file.getOriginalFilename());

        String fileKey = entity + "/" + entityId + "/" + entityId + "-" + imageType.getValue() + "."  + fileExtension;

        InputStream inputStream = file.getInputStream();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        // Upload file to S3
        amazonS3.putObject(bucketName, fileKey, inputStream, metadata);

        // Return the correct S3 URL
        return amazonS3.getUrl(bucketName, fileKey).toString();
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}

