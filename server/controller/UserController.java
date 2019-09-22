package server.controller;

import adapters.UserAdapter;
import collections.User;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import exceptions.BadRequest;
import org.bson.Document;
import org.bson.types.ObjectId;
import spark.Route;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserController extends Controller {

    public static Route signUpservice = (req, res) -> {
        logger.debug("In Controller signUpService");
        try {
            logger.debug("Validating request");
            Map<String, Object> parseMap = validateRequestBody("SignUp", req.body());
            User user = new User(parseMap);
            String collection_name = config.get("user_collection");
            logger.debug("inserting document");
            mongoClient.insertDocument(collection_name, UserAdapter.getDocument(user));
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", "created");
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
            if (parseMap.get("password").equals(password)) {
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
            DeleteResult deleteResult = mongoClient.deleteDocument(config.get("user_collection"), Filters.eq("username", username));
            Map<String, Object> map = new LinkedHashMap<>();
            if (deleteResult.getDeletedCount()!=0) {
                map.put("status", "deleted");
            } else {
                map.put("status", "failed");
            }
            return DecorateResponse(map);
        } catch (Exception e) {
            logger.error("Internal Server Error"+e.getMessage());
            return InternalServerErrorResponse(res, e.getMessage());
        }

    };

}
