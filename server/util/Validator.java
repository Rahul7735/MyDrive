package server.util;

import com.google.gson.Gson;
import configs.Config;
import exceptions.BadRequest;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class Validator {

    private Map<String, Mappings> saved;

    public Validator(){
        this.saved = new HashMap<>();
    }

    public void loadMappings() throws BadRequest {

        Config config=Config.getInstance();       //For User
        try{
            Gson gson = new Gson();
            FileReader reader = new FileReader(new File(config.get("mappings_path")));
            Map<String, Map> map = gson.fromJson(reader, Map.class);
            for(Map.Entry<String, Map> entry: map.entrySet()){
                this.saved.put(entry.getKey(), new Mappings(entry.getValue()));
            }
        } catch(Exception e){
            throw new BadRequest("Error in loadMappings" + e.getMessage());
        }
    }




    public Map<String, Object> validate(String type, Map request, Map<String, Object> parsedMap) throws BadRequest {
        Mappings map = this.saved.get(type);
        if(map==null) return null;

        for(Map.Entry<String, Mappings.Schema> entry : map.entrySet()){

            String key = entry.getKey();

            if(request.get(key)==null){
                if(entry.getValue().required)
                    throw new BadRequest("Required Field " + key + ".Not Found");
                else continue;
            }
            else if(entry.getValue().className.equals(Integer.class) && request.get(key).getClass().equals(Double.class)){
                parsedMap.put(key, Utils.getIntFromDouble(request.get(key)));
                continue;
            }
            else if(entry.getValue().className.equals(Long.class) && request.get(key).getClass().equals(Double.class)){
                parsedMap.put(key, Utils.getLongFromDouble(request.get(key)));
                continue;
            }
            else if(!entry.getValue().className.isInstance(request.get(key))){
                throw new BadRequest(entry.getKey() + ": Expected type "+ entry.getValue().className + ". Found " +
                        request.get(key).getClass().getName());
            }
            else if(entry.getValue().className.equals(Map.class)){
                Map<String, Object> mapp = validate(type + "." + entry.getKey(), (Map)request.get(key));
                for(Map.Entry<String, Object> subentry: mapp.entrySet()){
                    parsedMap.put(key + "." + subentry.getKey(), subentry.getValue());
                }
            }
            else
                parsedMap.put(key, request.get(key));
        }

        return parsedMap;

    }
    public Map<String, Object> validate(String type, Map request) throws BadRequest {
        Map<String, Object> parsedMap = new LinkedHashMap<>();
        return validate(type, request, parsedMap);
    }


}