package com.bangkoo.back.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.classmate.Annotations;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    Dotenv dotenv = Dotenv.configure()
            .directory(System.getProperty("user.dir"))  // 현재 프로젝트 루트 기준
            .ignoreIfMissing() // 없어도 예외 안 던지게
            .load();

    String accessKey = dotenv.get("AWS_ACCESS_KEY");
    String secretKey = dotenv.get("AWS_SECRET_KEY");
    String region = dotenv.get("AWS_REGION");

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}
