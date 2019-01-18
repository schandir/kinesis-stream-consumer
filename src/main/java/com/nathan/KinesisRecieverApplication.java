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
import com.nathan.processor.RecordProcessorFactory;

import java.util.UUID;

@SpringBootApplication
public class KinesisRecieverApplication implements CommandLineRunner {

    private static Log logger = LogFactory.getLog(KinesisRecieverApplication.class);

    private static final String STREAM_NAME = "RecordProcessor";


    public static void main(String[] args) {
        SpringApplication.run(KinesisRecieverApplication.class, args);
    }

    public void run(String... args) throws Exception {

        AWSCredentialsProvider credentialsProvider = new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return new BasicAWSCredentials("dummy", "dummy");
            }

            @Override
            public void refresh() {

            }
        };

        String workerId = String.valueOf(UUID.randomUUID());
        KinesisClientLibConfiguration kclConfig =
                new KinesisClientLibConfiguration("KinesisRecieverApplication", STREAM_NAME, credentialsProvider, workerId)
                        .withRegionName("dummy")
                        .withCommonClientConfig(new ClientConfiguration());

        IRecordProcessorFactory recordProcessorFactory = new RecordProcessorFactory();

        // Create the KCL worker with the processor factory
        Worker worker = new Worker(recordProcessorFactory, kclConfig);

        int exitCode = 0;
        try {
            worker.run();
        } catch (Throwable t) {
            logger.error("Caught throwable while processing data.", t);
            exitCode = 1;
        }
        System.exit(exitCode);

    }

}
