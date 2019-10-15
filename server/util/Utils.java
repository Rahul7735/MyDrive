package server.util;

import clients.MongoClient;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.mongodb.client.model.Filters;
import com.sun.javafx.scene.traversal.Algorithm;
import configs.Config;
import exceptions.BadRequest;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
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
    public static String encode(String clearText) throws NoSuchAlgorithmException {
        return new String(
                Base64.getEncoder().encode(MessageDigest.getInstance("SHA-256").digest(clearText.getBytes(StandardCharsets.UTF_8))));
    }

    public static String getAuthToken(String payload, String secret, String timeout) throws JWTCreationException {
        Date expiration = new Date(System.currentTimeMillis()+Long.parseLong(timeout)*1000);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create()
                .withClaim("payload", payload)
                .withExpiresAt(expiration)
                .withIssuedAt(new Date())
                .sign(algorithm);
        return token;
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
