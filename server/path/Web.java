package server.path;

import java.util.Stack;

public class Web {

    public static final String SIGNUP="mydrive/v1/user/signup";
    public static final String LOGIN="mydrive/v1/user/login";
    public static final String PROFILE="mydrive/v1/user/profile/:username";
    public static final String DELETE="mydrive/v1/user/delete/:username";
    public static final String CHANGE="mydrive/v1/user/change_password/:username";
    public static final String UPDATE="mydrive/v1/user/update_profile/:_id";


    public static final String GETSTORAGE="mydrive/v1/user/size/:username";
    public static final String LISTFOLDER="mydrive/v1/list/:username";
    public static final String DELETEFILE="mydrive/v1/deletefile/:username";
    public static final String FILEDOWNLOAD="mydrive/v1/downloadfile/:username";



    public static final String DEMOPATH="/demopath";

}
