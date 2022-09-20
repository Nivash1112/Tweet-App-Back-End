package com.tweetapp.configs.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Log4j2
@Configuration
public class MongoDbConfig extends AbstractMongoConfiguration {

    private final String uri;
    private final int maxIdleConnectionTimeInMillis;
    private final String dbName;

    public MongoDbConfig(@Value("${mongodb.uri:URI_CONFIGURATION_MISSING}") final String pUri,
                         @Value("${mongodb.dbname}") final String pDbName,
                         @Value("${mongodb.maxIdleConnectionTimeInMillis:30000}") final int pMaxIdleConnectionTimeInMillis) {
        uri = pUri;
        dbName = pDbName;
        this.maxIdleConnectionTimeInMillis = pMaxIdleConnectionTimeInMillis;
    }

    @Override
    public MongoClient mongoClient() {
        log.debug("Initializing MongoDB uri: {}", uri);
        return new MongoClient(new MongoClientURI(uri, MongoClientOptions.builder()
                .maxConnectionIdleTime(maxIdleConnectionTimeInMillis).writeConcern(WriteConcern.ACKNOWLEDGED)));

    }

    @Override
    protected String getDatabaseName() {
        log.info("Database Name: '{}'", dbName);
        return dbName;
    }
}
