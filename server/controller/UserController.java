package server.controller;

import adapters.UserAdapter;
import collections.User;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import exceptions.BadRequest;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import server.util.Utils;
import spark.Route;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserController extends Controller {

    public static Route signUpservice = (req,res) -> {
        logger.debug("In Controller signUpService");
        try {
            logger.debug("Validating request");
            Map<String, Object> parseMap = validateRequestBody("SignUp", req.body());
            User user = new User(parseMap);
            String collection_name = config.get("user_collection");
            logger.debug("inserting document");
            mongoClient.insertDocument(collection_name, UserAdapter.getDocument(user));
            logger.debug("finding document of recent created");
            Document doc = mongoClient.findDocument(collection_name,
                    Filters.eq("username", parseMap.get("username"))).get(0);
            ObjectId id = (ObjectId) doc.get("_id");
            String folder = parseMap.get("username") + "_" + id.toString();
            logger.debug("creating folder for user");
            s3Client.createFolder(folder);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status","created");
            return DecorateResponse(map);


        } catch (BadRequest e) {
            logger.error("BadRequest" + e.getMessage());
            return BadRequestResponse(res, e.getMessage());
        } catch (Exception e) {
            logger.error("InternalServerError" + e.getMessage());
            return InternalServerErrorResponse(res, e.getMessage());
        }
    };

    public static Route loginService = (req, res) -> {
        logger.debug("in Controller LoginService");
        try {

            logger.debug("Validating request");
            Map<String, Object> parseMap = validateRequestBody("Login", req.body());
            if (parseMap.get("username") == null && parseMap.get("email") == null) {
                throw new BadRequest("email or username should be specified");
            }
            logger.debug("retrieving Document");
            String collection_name = config.get("user_collection");
            List<Document> documents;
            if (parseMap.get("email") != null) {
                documents = mongoClient.findDocument(collection_name, Filters.eq("email", parseMap.get("email")));
            } else {
                documents = mongoClient.findDocument(collection_name, Filters.eq("username", parseMap.get("username")));
            }

            if (documents.size() < 1) {
                throw new BadRequest("Invalid Credential");
            }
            String password = documents.get(0).getString("password");
            Map<String, Object> map = new LinkedHashMap<>();
            if (Utils.encode((String)parseMap.get("password")).equals(password)) {
                map.put("status", "success");
            } else {
                map.put("status", "Invalid Credentials");
            }
            return DecorateResponse(map);
        } catch (BadRequest e) {
            logger.error("BadRequest  " + e.getMessage());
            return BadRequestResponse(res, e.getMessage());
        } catch (Exception e) {
            logger.error("InternalServerError " + e.getMessage());
            return InternalServerErrorResponse(res, e.getMessage());
        }
    };

    public static Route profileService = (req, res) -> {
        logger.debug("In profile Service");

        try {

            String username = req.params("username");
            logger.debug("Finding Document of user with username" + username);
            List<Document> documents = mongoClient.findDocument(config.get("user_collection"), Filters.eq("username", username));
            Document document = documents.get(0);
            document.remove("password");
            ObjectId id = (ObjectId) document.get("_id");
            document.put("_id", id.toString());
            return DecorateResponse(document);

        } catch (Exception e) {
            return InternalServerErrorResponse(res, e.getMessage());
        }

    };

    public static Route deleteService = (req, res) -> {
        logger.debug("In delete Service Method");

        try {
            logger.debug("Delete user");
            String username = req.params("username");
            //System.out.println(username);
            DeleteResult deleteResult = mongoClient.deleteDocument(config.get("user_collection"),
                    Filters.eq("username", username));
            Map<String, Object> map = new LinkedHashMap<>();
            if (deleteResult.getDeletedCount() != 0) {
                map.put("status", "deleted");
            } else {
                map.put("status", "failed");
            }
            return DecorateResponse(map);
        } catch (Exception e) {
            logger.error("Internal Server Error" + e.getMessage());
            return InternalServerErrorResponse(res, e.getMessage());
        }

    };

    public static Route changePassword = (req, res) -> {
        logger.debug("In Change Password Method");
        try {

            logger.debug("Validating Request");
            Map<String, Object> parseMap = validateRequestBody("Change", req.body());
            String username = req.params("username");
            if (!parseMap.get("new_password").equals(parseMap.get("confirm_password"))) {
                throw new BadRequest("new_password and confirm_password is not same");
            }
            new User().setPassword((String) parseMap.get("new_password"));
            logger.debug("finding document for user for username");
            List<Document> documents = mongoClient.findDocument(config.get("user_collection"),
                    Filters.eq("username", username));
            if (documents.size()<1){
                throw new BadRequest("User not Found");
            }
            Document document = documents.get(0);
            UpdateResult updateResult;
            logger.debug("matching password with previous_password");
            if (document.get("password").equals(parseMap.get("previous_password"))) {
             updateResult = mongoClient.updateDocument(config.get("user_collection"),
                        Filters.eq("username", username),
                        Updates.set("password", parseMap.get("new_password")));
             Map<String,Object> map=new LinkedHashMap<>();
             if (updateResult.getModifiedCount()!=0)
                 map.put("status","updated");
             else
                 map.put("status","failed");
             return DecorateResponse(map);
            } else
                throw new BadRequest(" current password is not matched");

        } catch (BadRequest e) {
            logger.error("Bad Request" + e.getMessage());
            return BadRequestResponse(res, e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error" + e.getMessage());
            return InternalServerErrorResponse(res, e.getMessage());
        }
    };

    public static Route updateProfile=(req,res)->{

        logger.debug("In ProfileUpdate Method");
        try {
            logger.debug("Validating Request");
            Map<String, Object> parseMap = validateRequestBody("UpdateProfile", req.body());
            String id=req.params("_id");
            //System.out.println(response);
            List<Bson> updateValues = new ArrayList<>();

            if (parseMap.get("username")!=null){
                new User().setUsername((String) parseMap.get("username"));
                updateValues.add(Updates.set("username",parseMap.get("username")));
            }
            if (parseMap.get("email")!=null){
                new User().setEmail((String) parseMap.get("email"));
                updateValues.add(Updates.set("email",parseMap.get("email")));
            }
            if (parseMap.get("name")!=null){
                new User().setName((String) parseMap.get("name"));
                updateValues.add(Updates.set("name",parseMap.get("name")));
            }
            if (parseMap.get("age")!=null){
                new User().setAge((Integer) parseMap.get("age"));
                updateValues.add(Updates.set("age",parseMap.get("age")));
            }

            logger.debug("finding document for user for id "+id);
            List<Document> documents = mongoClient.findDocument(config.get("user_collection"),
                    Filters.eq(new ObjectId(id)));
            if (documents.size()<1){
                throw new BadRequest("User not Found");
            }
            logger.debug("Updating Document");
            UpdateResult updateResult = mongoClient.updateDocument(config.get("user_collection"),
                 Filters.eq(new ObjectId(id)),Updates.combine(updateValues));
            Map<String,Object> map=new LinkedHashMap<>();
            if (updateResult.getModifiedCount()!=0)
                map.put("status","updated");
            else
                map.put("status","failed");
            return DecorateResponse(map);

        }catch (BadRequest e){
            logger.error("BadRequest "+e.getMessage());
            return BadRequestResponse(res,e.getMessage());
        }
         catch (Exception e){
             logger.error("Internal Server Error "+e.getMessage());
            return InternalServerErrorResponse(res,e.getMessage());
        }

    };




}
