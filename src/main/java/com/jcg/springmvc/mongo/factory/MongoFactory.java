package com.jcg.springmvc.mongo.factory;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoFactory {

	private static Logger log = Logger.getLogger(MongoFactory.class);
	
	private static MongoClient mongo;
	
	private MongoFactory() {
	}
	
	public static MongoClient getMongo() {
		if ( mongo == null ) {
			try {
				log.info("Conectando com MongoDB...");
				MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
				mongo = new MongoClient(connectionString);
			} catch (MongoException e) {
				log.error("Erro ao tentar conectar: "+ e);
			}
		}
		return mongo;
	}
	
	public static MongoDatabase getMongoDatabase(String databaseName) {
		return getMongo().getDatabase(databaseName);
	}
	
	public static MongoCollection<Document> getCollection(String databaseName, String nameCollection) {
		return getMongoDatabase(databaseName).getCollection(nameCollection);
	}
}
