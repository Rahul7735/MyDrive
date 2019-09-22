package server.controller;


import exceptions.BadRequest;

import spark.Route;



import java.util.Map;

public class DemoController extends Controller {

    public static Route demo_service = (req, res) -> {
        logger.debug("In demo Service1 method");
        try {

            logger.debug("validating Request");
            Map<String, Object> parseMap = validateRequestBody("Demo",req.body());
            for (Map.Entry entry : parseMap.entrySet()) {
                System.out.println(entry.getKey() + "   :  " + entry.getValue() + "type  :" + entry.getValue().getClass().getName());
            }
            //System.out.println(parseMap.get("address.city"));
//            if(1==1)
//            throw new Exception("Something Went Wrong");
            return DecorateResponse(parseMap);

        }catch(BadRequest e){
            logger.error("Bad Request"+e.getMessage());
            return BadRequestResponse(res,e.getMessage());
        }
        catch (Exception e) {
            logger.error("InternalServerError"+e.getMessage());
           return InternalServerErrorResponse(res,e.getMessage());
        }

    };
}
