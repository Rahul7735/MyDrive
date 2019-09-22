package server.util;

import java.util.*;

public class Mappings{
    private LinkedHashMap<String, Schema> mapping;

    private static Map<String, String> _class = new HashMap<>();

    static {
        _class.put("String", "java.lang.String");
        _class.put("Integer", "java.lang.Integer");
        _class.put("Long", "java.lang.Long");
        _class.put("Double", "java.lang.Double");
        _class.put("Map", "java.util.Map");
        _class.put("List", "java.util.List");
    }

    public Mappings(Map<String, String> map) throws ClassNotFoundException{

        this.mapping = new LinkedHashMap<>();
        for(Map.Entry<String, String> entry:map.entrySet()){
            String[] tokens = entry.getValue().split("\\|");
            String className = tokens[0];
            boolean required = true;
            if(tokens.length>1)
                required = !tokens[1].equals("optional");
            this.mapping.put(entry.getKey(), new Schema(className, required));
        }
    }

    public Mappings put(String key, Schema schema){
        this.mapping.put(key, schema);
        return this;
    }

    public Schema get(String key){
        return this.mapping.get(key);
    }

    public Set<Map.Entry<String, Schema>> entrySet(){
        return this.mapping.entrySet();
    }


    public static class Schema{
        public Class className;
        public boolean required;

        public Schema(String className, boolean required)throws ClassNotFoundException {
            this.className = Class.forName(_class.get(className));
            this.required = required;
        }
    }

}