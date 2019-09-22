package clients;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mongodb.client.FindIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;
import org.bson.Document;

import configs.Config;
import exceptions.DataBaseException;


public class MongoClient {

    private com.mongodb.MongoClient mongo;
    private String database;

    private static MongoClient obj;

    private MongoClient() throws DataBaseException {
        try {
            Config config = Config.getInstance();
            this.database = config.get("db_name");

            // Creating a Mongo client
            this.mongo = new com.mongodb.MongoClient(config.get("host"), Integer.valueOf(config.get("port")));
            System.out.println("Connected to the mongodb database successfully");
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage() + "Exception occurred in connection");
        }

    }


    public void createCollection(String collectionName) {
        try {
            MongoDatabase database = mongo.getDatabase(this.database);
            database.createCollection(collectionName);
            System.out.println("Collection created successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage() + "Exception occurred in createCollection");
        }

    }


    public MongoCollection<Document> retriveCollection(String collectionName) throws DataBaseException {

        try {
            // Accessing the database
            MongoDatabase database = mongo.getDatabase(this.database);
            // Retieving a collection
            return database.getCollection(collectionName);
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage() + "Exception occurred in retrieveCollection ");
        }

    }


    public void insertDocument(String collectionName, Document document) throws DataBaseException {
        try {
            MongoCollection<Document> collection = this.retriveCollection(collectionName);
            System.out.println("Collection sampleCollection selected successfully");

            collection.insertOne(document);
            System.out.println("Document inserted successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage() + " Exception occurred in insertDocument");
        }


    }


    public List<Document> findAllDocument(String collectionName) throws DataBaseException {
        try {
            MongoCollection<Document> collection = this.retriveCollection(collectionName);
            System.out.println("Collection sampleCollection selected successfully");
            // Getting the iterable object
            FindIterable<Document> iterDoc = collection.find();
            List<Document> list = new ArrayList<Document>();
            // Getting the iterator
            Iterator<Document> it = iterDoc.iterator();

            while (it.hasNext()) {
                list.add(it.next());
            }
            return list;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage() + " Exception occurred in findAllDocument");
        }


    }


    public List<Document> findDocument(String collectionName, Bson query) throws DataBaseException {
        try {
            MongoCollection<Document> collection = this.retriveCollection(collectionName);
            // Getting the iterable object
            FindIterable<Document> iterDoc = collection.find(query);
            List<Document> list = new ArrayList<Document>();
            // Getting the iterator
            Iterator<Document> it = iterDoc.iterator();

            while (it.hasNext()) {
                list.add(it.next());
            }
            return list;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage() + " Exception occurred in findDocument");
        }

    }


    public UpdateResult updateDocument(String collectionName, Bson query, Bson update) throws DataBaseException {

        try {
            MongoCollection<Document> collection = this.retriveCollection(collectionName);
            UpdateResult result = collection.updateOne(query, update);
            return result;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage() + "Exception Occurred in update Document");
        }


    }


    public DeleteResult deleteDocument(String collectionName, Bson query) throws DataBaseException {

        try {
            MongoCollection<Document> collection = this.retriveCollection(collectionName);

            DeleteResult deleteResult = collection.deleteOne(query);
            return deleteResult;

        } catch (Exception e) {
            throw new DataBaseException("Exception occurred in deleteDocument" + e.getMessage());
        }


    }


    public void dropCollection(String collectionName) throws DataBaseException {
        try {
            MongoCollection<Document> collection = this.retriveCollection(collectionName);
            //dropping collection
            collection.drop();
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage() + "Exception occurred in dropCollection");
        }

    }


    public List<String> listCollection() throws DataBaseException {
        try {
            // Accessing the database
            MongoDatabase database = mongo.getDatabase(this.database);
            // Retrieving a collection
            List<String> list = new ArrayList<String>();
            for (String name : database.listCollectionNames()
            ) {
                list.add(name);
            }
            return list;
        } catch (Exception e) {
            throw new DataBaseException(e.getMessage() + "Exception occurred in listCollection");
        }

    }


    public static MongoClient getInstance() throws DataBaseException {
        if (obj == null) {
            obj = new MongoClient();

        }
        return obj;
    }


}
