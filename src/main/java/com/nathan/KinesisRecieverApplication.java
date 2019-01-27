package com.nathan;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.nathan.kinesis.DataStreamConsumer;
import com.nathan.kinesis.processor.RecordProcessorFactory;

import java.util.UUID;

@SpringBootApplication
public class KinesisRecieverApplication implements CommandLineRunner {

    private static Log logger = LogFactory.getLog(KinesisRecieverApplication.class);

    private static final String STREAM_NAME = "RecordProcessor";


    public static void main(String[] args) {
        SpringApplication.run(KinesisRecieverApplication.class, args);
    }

    public void run(String... args) throws Exception {
        DataStreamConsumer.newBuilder().appName(KinesisRecieverApplication.class.getName()).
                credentialsProvider("dummt", "dummy").
                recordProcessorFactory(new RecordProcessorFactory()).streamName(STREAM_NAME).signingRegion("us-east-1")
                .build().start();
    }

}
