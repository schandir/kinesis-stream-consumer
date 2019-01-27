package com.nathan.kinesis.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;

public class KinesisClient {
    private KinesisClientLibConfiguration _kclConfig;

    public KinesisClient(String appName, String streamName, AWSStaticCredentialsProvider awsStaticCredentialsProvider,
            String workerId, String signingRegion){
        _kclConfig =
                new KinesisClientLibConfiguration(appName, streamName, awsStaticCredentialsProvider,
                        workerId)
                        .withRegionName(signingRegion)
                        .withCommonClientConfig(new ClientConfiguration());

    }

    public KinesisClientLibConfiguration build(){
        return _kclConfig;
    }
}
