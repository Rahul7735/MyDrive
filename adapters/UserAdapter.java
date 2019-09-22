package adapters;

import collections.User;
import org.bson.Document;

public class UserAdapter {
    public static Document getDocument(User user) {
        return new Document().append("name",user.getName())
                                  .append("password",user.getPassword())
                                   .append("age",user.getAge())
                                   .append("email",user.getEmail())
                                   .append("username",user.getUsername());


    }
}
