import adapters.UserAdapter;
import clients.MongoClient;
import clients.S3Client;
import collections.User;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import configs.Config;
import org.bson.Document;

import java.util.List;
import java.util.logging.Filter;
import java.util.List;


    public class Main {
        public static void main(String[] args) {
            Config config = Config.getInstance();

//
//        User user = new User();
//        try {
//            MongoClient mongoClient = MongoClient.getInstance();
//            user.setAge(24)
//                    .setEmail("coolboyrk980@gmail.com")
//                    .setName("Rahul Shaw")
//                    .setUsername("rksahwRk")
//                    .setPassword("Rahul90");
//            String collection_name = config.get("user_collection");
//            mongoClient.insertDocument(collection_name, UserAdapter.getDocument(user));
//            List<Document> docs = mongoClient.findAllDocument(collection_name);
//            for (Document doc : docs
//            ) {
//                System.out.println(doc);
//            }
//
//            List<Document> documents = mongoClient.findAllDocument(collection_name);
//            for (Document doc : documents
//            ) {
//                System.out.println(doc);
//            }
//
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }

////            List<Document> list = mongoClient.findDocument(collection_name,
////                    Filters.and(Filters.lt("age", 24), Filters.eq("name", "Nitesh Gujjar")));
////        for (Document doc: list
////             ) {
////            System.out.println(doc);
////        }
//        try {
//            UpdateResult res = mongoClient.updateDocument(collection_name, Filters.eq("username", "nitesh33323"),
//                    Updates.unset("password"));
//            System.out.println(res.getModifiedCount() + " " + res.getMatchedCount() + "  " + res.wasAcknowledged());
//            List<Document> docs = mongoClient.findAllDocument(collection_name);
//            for (Document doc : docs
//            ) {
//                System.out.println(doc);
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//
////        DeleteResult res1=mongoClient.deleteDocument(collection_name,Filters.eq("username","nitesh33323"));
////
////        System.out.println( res1.getDeletedCount() + "  " + res.wasAcknowledged());
////        docs = mongoClient.findAllDocument(collection_name);
////        for (Document doc:docs
////        ) {
////            System.out.println(doc);
////        }
//




        try {
            S3Client s3=S3Client.getInstance();
           // s3.fileUpload("F:\\rahul\\2017-09-08-18-09-18-845.jpg","images/ok.jpg");
           // System.out.println("deleting");
            //s3.deleteFile("rahulW209_5d8a4926f0b58c3738cae7f0/IMG_20180915_115935.jpg");
            System.out.println(s3.getFoldersize("rahulW209_5d8a4926f0b58c3738cae7f0/")+" mb");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


////        //s3.createFolder("/a/b");
////        //s3.fileDownload("images/ok.jpg","C:\\Users\\HP_USER\\Desktop\\s3\\");
////        List<String> list = s3.listDirectory("a/b/");
//////       for(String s: list){
//////           System.out.println(s);
//////       }
//////        System.out.println("deleting");
//////       s3.deleteFolder("a/b/");
////        list = s3.listDirectory("a/b/");
////        for(String s: list){
////            System.out.println(s);
////        }
////        s3.folderDownload("a/b/","C:\\Users\\HP_USER\\Desktop\\s3\\");
////
//
//    }
//
//

    }
}
