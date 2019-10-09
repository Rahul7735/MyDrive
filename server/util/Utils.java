package server.util;

import clients.MongoClient;
import com.mongodb.client.model.Filters;
import configs.Config;
import exceptions.BadRequest;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

public class Utils {
    public static  Integer getIntFromDouble(Object object){

        Double d=Double.parseDouble(object.toString());
       return d.intValue();
    }

    public static long getLongFromDouble(Object object){
        Double d=Double.parseDouble(object.toString());
        return d.longValue();
    }

    public static String getUserfolder(MongoClient mongoClient, String collection, String username) throws Exception{
        List<Document> documents = mongoClient.findDocument(collection,
                Filters.eq("username", username));
        if (documents.size() < 1) {
            throw new BadRequest("user name not exist");
        }
        Document document = documents.get(0);
        ObjectId id = (ObjectId) document.get("_id");
        String filepath = username + "_" + id.toString() + "/";
        return filepath;
    }
}
