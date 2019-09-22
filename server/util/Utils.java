package server.util;

public class Utils {
    public static  Integer getIntFromDouble(Object object){

        Double d=Double.parseDouble(object.toString());
       return d.intValue();
    }

    public static long getLongFromDouble(Object object){
        Double d=Double.parseDouble(object.toString());
        return d.longValue();
    }
}
