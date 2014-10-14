package com.wjl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {
	
	@Value("#{mdb.host}")
	private String host;
	
	@Value("#{mdb.port}")
	private int port;
	
	@Value("#{mdb.name}")
	private String name;
	
	public String getDatabaseName() {
		return this.name;
	}

	@Bean
	public Mongo mongo() throws Exception {
		return new Mongo(host, port);
	}
	
	@Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), name);
    }

}
