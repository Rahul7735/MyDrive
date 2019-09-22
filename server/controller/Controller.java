package server.controller;

import clients.MongoClient;
import com.google.gson.Gson;
import configs.Config;
import exceptions.BadRequest;

import server.util.Validator;
import spark.Response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;


public class Controller {
    public static Gson gson;
    public static Validator validator;
     final static Logger logger;
     public static Config config;
     public static MongoClient mongoClient;
    static {
        gson=new Gson();
        validator=new Validator();
        logger=Logger.getLogger("spark");
        config=Config.getInstance();

        try {
            validator.loadMappings();
            mongoClient=MongoClient.getInstance();
        }catch (Exception e){
            logger.error("Error in BaseController"+e.getMessage());
           // System.out.println("Error in loading mapping");
            System.exit(1);
        }
    }

    public static Map<String,Object> validateRequestBody(String resource,String body) throws BadRequest {
        Map request=gson.fromJson(body,Map.class);
        return validator.validate(resource,request);
    }

    public static String BadRequestResponse(Response res,String message){
        Map mapp=new LinkedHashMap();

        Map map=new LinkedHashMap();
        map.put("Code",400);
        map.put("message",message);

        mapp.put("status",map);
        res.status(400);
        return gson.toJson(mapp);
    }

    public static String InternalServerErrorResponse(Response res,String message){

        Map mapp=new LinkedHashMap();

        Map map=new LinkedHashMap();
        map.put("code",500);
        map.put("message",message);

        mapp.put("status",map);
        res.status(500);
        return gson.toJson(mapp);

    }

    public static String DecorateResponse(Map response){
        Map mapp=new LinkedHashMap();

        mapp.put("data",response);

        Map map=new LinkedHashMap();
        map.put("code",200);
        map.put("message","SUCCESS");

        mapp.put("status",map);
        return gson.toJson(mapp);
    }


}
