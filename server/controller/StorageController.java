package server.controller;

import com.mongodb.client.model.Filters;
import exceptions.BadRequest;
import org.bson.Document;
import org.bson.types.ObjectId;
import server.util.Utils;
import spark.Route;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StorageController extends Controller {
    public static Route listFolderService = (req, res) -> {
        try {
            String username = req.params("username");
            List<Document> documents = mongoClient.findDocument(config.get("user_collection"),
                    Filters.eq("username", username));
            if (documents.size() < 1) {
                throw new BadRequest("user name not exist");
            }
            Document document = documents.get(0);
            ObjectId id = (ObjectId) document.get("_id");
            String path = req.queryParams("path");
            String prefix;
            if (path != null)
                prefix = username + "_" + id + "/" + path;
            else
                prefix = username + "_" + id + "/";

            List<String> filelist = s3Client.listDirectory(prefix);
            for (int i = 0; i < filelist.size(); i++) {
                String temp = filelist.get(i).replace(prefix, "");
                filelist.set(i, temp);
            }
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("objects", filelist);
            return DecorateResponse(map);

        } catch (BadRequest e) {
            logger.error("BadRequest" + e.getMessage());
            return BadRequestResponse(res, e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error" + e.getMessage());
            return InternalServerErrorResponse(res, e.getMessage());
        }

    };

    public static Route deleteFile = (req, res) -> {
        logger.debug("in delete file method");
        try {
            logger.debug("getting username from param");
            Map<String,Object> map=  validateRequestBody("DeleteRequest",req.body());
           String path=(String)map.get("path");
           String type=(String)map.get("type");

            String username = req.params("username");
            List<Document> documents = mongoClient.findDocument(config.get("user_collection"),
                    Filters.eq("username", username));
            if (documents.size() < 1) {
                throw new BadRequest("user name not exist");
            }
            Document document = documents.get(0);
            logger.debug("finding object id of specific domain");
            ObjectId id = (ObjectId) document.get("_id");
            logger.debug("getting path from param");
            path = username + "_" + id.toString() + "/" + path;

            if (type.equals("folder")){
                logger.debug("deleting folder of user");
                s3Client.deleteFolder(path);
            } else if (type.equals("file")){
                logger.debug("deleting file of user");
                s3Client.deleteFile(path);
            } else {
                throw new BadRequest("Type not supported");
            }

            Map<String, Object> map1 = new LinkedHashMap<>();
            map1.put("status", "deleted");
            return DecorateResponse(map1);


        } catch (BadRequest e) {
            logger.error("BadRequest" + e.getMessage());
            return BadRequestResponse(res, e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error" + e.getMessage());
            return InternalServerErrorResponse(res, e.getMessage());
        }
    };

    public static Route downloadFile=(req,res)->{
        try {

            String username=req.params("username");
            String path=req.queryParams("file");
            String userFolder = Utils.getUserfolder(mongoClient, config.get("user_collection"),username);
            byte[] bytes = s3Client.getFileByteArray(userFolder + "path");
            HttpServletResponse raw = res.raw();

            raw.getOutputStream().write(bytes);
            raw.getOutputStream().flush();
            raw.getOutputStream().close();
            return res.raw();

        }catch (Exception e){
            return InternalServerErrorResponse(res, e.getMessage());
        }

    };
    public static Route getFoldersizeService=(req,res)->{
        try {
            logger.debug("in get folderSize method");
            String username=req.params("username");
            String folderpath=req.queryParams("folderpath");
            if(folderpath.equals("/")){
                folderpath="";
            }
            String userFolder = Utils.getUserfolder(mongoClient, config.get("user_collection"),username);
           double size= s3Client.getFoldersize(userFolder+folderpath);
            Map<String,Object> map=new LinkedHashMap<>();
            map.put("size",size);
            return DecorateResponse(map);

        }
        catch (Exception e){
            logger.error("Internal Server Error"+e.getMessage());
            return InternalServerErrorResponse(res,e.getMessage());
        }

    };

}
