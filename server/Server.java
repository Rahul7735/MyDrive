package server;

import static spark.Spark.*;

import server.controller.DemoController;
import server.controller.UserController;
import server.path.Web;

public class Server {
    public static void main(String[] args) {
        port(5000);
        after((req,res)->{
            res.header("Content-Type","application/json");
        });

        get(Web.DEMOPATH, DemoController.demo_service );

        post(Web.SIGNUP, UserController.signUpservice);
        post(Web.LOGIN,UserController.loginService);
        get(Web.PROFILE,UserController.profileService);
        get(Web.DELETE,UserController.deleteService);


    }
}
