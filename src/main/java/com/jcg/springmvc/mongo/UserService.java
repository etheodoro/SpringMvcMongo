package com.jcg.springmvc.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jcg.springmvc.mongo.factory.MongoFactory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

@Service("userService")
@Transactional
public class UserService {

	static String dbName = "mydb", dbCollection = "mycollection";
	private static Logger log = Logger.getLogger(UserService.class);

	// Fetch all users from the mongo database.
	public List<User> getAll() {
		List<User> userList = new ArrayList<User>();
		MongoCollection<Document> coll = MongoFactory.getCollection(dbName, dbCollection);

		// Fetching cursor object for iterating on the database records.
		MongoCursor<Document> cursor = coll.find().iterator();	
		try {
			while(cursor.hasNext()) {			
				Document document = cursor.next();
				User user = new User();
				user.setId(document.get("id").toString());
				user.setName(document.get("name").toString());

				// Adding the user details to the list.
				userList.add(user);
			}
		} finally {
			cursor.close();
		}
		log.debug("Total records fetched from the mongo database are= " + userList.size());
		return userList;
	}

	// Add a new user to the mongo database.
	public Boolean add(User user) {
		boolean output = false;
		Random ran = new Random();
		log.debug("Adding a new user to the mongo database; Entered user_name is= " + user.getName());
		try {			
			MongoCollection<Document> coll = MongoFactory.getCollection(dbName, dbCollection);

			// Create a new object and add the new user details to this object.
			Document document = new Document();
			document.put("id", String.valueOf(ran.nextInt(100))); 
			document.put("name", user.getName());			

			// Save a new user to the mongo collection.
			coll.insertOne(document);
			output = true;
		} catch (Exception e) {
			output = false;
			log.error("An error occurred while saving a new user to the mongo database", e);			
		}
		return output;
	}

	// Update the selected user in the mongo database.
	public Boolean edit(User user) {
		boolean output = false;
		log.debug("Updating the existing user in the mongo database; Entered user_id is= " + user.getId());
		try {

			MongoCollection<Document> coll = MongoFactory.getCollection(dbName, dbCollection);
			
			Bson bson = Filters.eq("id", user.getId());
		
			// Create a new object and assign the updated details.
			Document edited = new Document();
			edited.put("id", user.getId()); 
			edited.put("name", user.getName());
			

			// Update the existing user to the mongo database.
			coll.replaceOne(bson, edited);
			output = true;
		} catch (Exception e) {
			output = false;
			log.error("An error has occurred while updating an existing user to the mongo database", e);			
		}
		return output;
	}

	// Delete a user from the mongo database.
	public Boolean delete(String id) {
		boolean output = false;
		log.debug("Deleting an existing user from the mongo database; Entered user_id is= " + id);
		try {
			// Fetching the required user from the mongo database.
			Document item = getDocument(id);

			MongoCollection<Document> coll = MongoFactory.getCollection(dbName, dbCollection);

			// Deleting the selected user from the mongo database.
			coll.deleteOne(item);
			output = true;			
		} catch (Exception e) {
			output = false;
			log.error("An error occurred while deleting an existing user from the mongo database", e);			
		}	
		return output;
	}

	// Fetching a particular record from the mongo database.
	private Document getDocument(String id) {
		MongoCollection<Document> coll = MongoFactory.getCollection(dbName, dbCollection);
		Bson bson = Filters.eq("id", id);

		return coll.find(bson).first();
	}

	// Fetching a single user details from the mongo database.
	public User findUserId(String id) {
		User user = new User();
		MongoCollection<Document> coll = MongoFactory.getCollection(dbName, dbCollection);

		Bson bson = Filters.eq("id", id);
		
		Document document = coll.find(bson).first();		
		user.setId(document.get("id").toString());
		user.setName(document.get("name").toString());

		// Return user object.
		return user;
	}
}