package collections;

import com.mongodb.client.model.Filters;


import exceptions.DataBaseException;
import exceptions.ValidationException;
import clients.MongoClient;
import configs.Config;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public class User {

   private String name;
   private String password;
   private Integer age;
   private String username;
   private String email;

   public User(Map<String,Object> parseMap)throws ValidationException,DataBaseException{
       this.setName((String)parseMap.get("name"));
       this.setPassword((String)parseMap.get("password"));
      if (parseMap.get("age")!=null){
          this.setAge((Integer)parseMap.get("age"));
      }
      this.setEmail((String)(parseMap.get("email")));
      this.setUsername((String)parseMap.get("username"));

   }

    public String getName() {
        return name;
    }

    public User setName(String name) throws ValidationException {
        if (name==null){
            throw new ValidationException("Name cannot be null");
        }
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) throws ValidationException {
        if (!isPasswordValid(password)){

            throw  new ValidationException("Password should contain atleast one capital and small and should be" +
                    "atleast 6 letters");
        }
        this.password = password;
        return this;
    }

    private boolean isPasswordValid(String password){
        if (password.length()<6){
            return false;
        }
        boolean small = false;
        boolean capital=false;
        for (Character ch:password.toCharArray()
             ) {
            if (ch>=97 && ch<=122){
                small=true;
            }
            if (ch>=65 && ch<=90){
                capital=true;
            }

        }
        return small && capital;
    }

    public Integer getAge() {
        return age;
    }

    public User setAge(Integer age) throws ValidationException {
        if (age<13){
            throw new ValidationException("Age should be greater than 13");
        }
        this.age = age;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) throws ValidationException, DataBaseException {
        MongoClient mongoClient=MongoClient.getInstance();
        List<Document> res =  mongoClient.findDocument(Config.getInstance().get("user_collection"), Filters.eq("username",username));
        if(res.size()>0){
            throw new ValidationException("Username already exists");
        }
        this.username = username;
        return this;
    }

    public String getEmail() {

        return email;
    }

    public User setEmail(String email) throws ValidationException,DataBaseException {
        if (!email.matches("\\w+@\\w+\\.\\w+")){
            throw new ValidationException("Email is not valid");

        }

        MongoClient mongoClient=MongoClient.getInstance();
        List<Document> res =  mongoClient.findDocument(Config.getInstance().get("user_collection"), Filters.eq("email",email));
        if(res.size()>0){
            throw new ValidationException("Email already exists");
        }

        this.email = email;
        return this;
    }


}
