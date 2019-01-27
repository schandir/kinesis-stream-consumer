package com.nathan.kinesis;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.nathan.kinesis.config.KinesisClient;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class DataStreamConsumer implements Closeable {
    private IRecordProcessorFactory _recordProcessorFactory;
    private Worker _worker;
    private final Executor _executor;
    private final String _appName;
    private final String _streamname;
    private final AWSStaticCredentialsProvider _awsStaticCredentialsProvider;
    private final String _signingRegion;

    public static Builder newBuilder() {
        return new Builder();
    }

    private DataStreamConsumer(Builder builder){
        this._appName = builder._appName;
        this._streamname = builder._streamName;
        this._awsStaticCredentialsProvider = builder._awsStaticCredentialsProvider;
        this._signingRegion = builder._signingRegion;
        this._recordProcessorFactory = builder._recordProcessorFactory;
        _executor = Executors.newSingleThreadExecutor(
                new ThreadFactoryBuilder().setNameFormat("DataStreamConsumer-" + _streamname + "-%d").build());
    }

    public static final class Builder{
        private AWSStaticCredentialsProvider _awsStaticCredentialsProvider;
        private String _appName;
        private String _streamName;
        private String _signingRegion;
        private IRecordProcessorFactory _recordProcessorFactory;
        public Builder credentialsProvider(String accessKey,  String secretKey) {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
            this._awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCreds);
            return this;
        }

        public Builder appName(String appName) {
            this._appName = appName;
            return this;
        }

        public Builder streamName(String streamName) {
            this._streamName = streamName;
            return this;
        }

        public Builder signingRegion(String signingRegion) {
            this._signingRegion = signingRegion;
            return this;
        }

        public Builder recordProcessorFactory(IRecordProcessorFactory processor){
            this._recordProcessorFactory = processor;
            return this;
        }

        public DataStreamConsumer build() {
            return new DataStreamConsumer(this);
        }
    }

    public  DataStreamConsumer start(){
        String workerId = null;
        try {
            workerId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + UUID.randomUUID();
        } catch (UnknownHostException e) {
            workerId = UUID.randomUUID().toString();
        }
        KinesisClientLibConfiguration config = new KinesisClient(_appName, _streamname, _awsStaticCredentialsProvider,
                workerId, _signingRegion).build();
        _worker = new Worker.Builder()
                .recordProcessorFactory(_recordProcessorFactory)
                .config(config)
                .build();

        _executor.execute(_worker);
        return this;
    }
    @Override
    public void close() throws IOException {
        // The executor is a single thread that is tied to this worker. Once the worker shuts down
        // the executor will stop.
        _worker.shutdown();
    }

}
