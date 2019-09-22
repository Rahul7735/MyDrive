package clients;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import configs.Config;
import exceptions.CloudException;


public class S3Client {
   private String bucket;
   private AmazonS3 client;

    private static S3Client obj;

    private S3Client() throws CloudException {
        try {
            Config config = Config.getInstance();

            BasicAWSCredentials awsCreds = new BasicAWSCredentials(config.get("access_key_id"), config.get("secret_key_id"));
            this.client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .withRegion(Regions.AP_SOUTH_1)
                    .build();

            this.bucket = config.get("bucket");
        }catch (Exception e){
            throw new CloudException(e.getMessage()+"Exception occurred in connection of S3client");
        }


    }

    public static S3Client getInstance() throws CloudException{
        if (obj == null) {

            obj = new S3Client();
        }
        return obj;
    }

    public void fileUpload(String src, String dest) throws CloudException {

        System.out.println("Uploading file from " + src + " to " + dest);


        try {
            this.client.putObject(this.bucket, dest, new File(src));
        } catch (AmazonServiceException e) {
            throw new CloudException(e.getErrorMessage()+"Exception occurred in file upload");
        }
    }

    public void createFolder(String path)throws CloudException {

        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (!path.endsWith("/")) {
            path = path.concat("/");
        }
        System.out.println("Creating folder on this  " + path);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(0);

            // create empty content
            InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

            // create a PutObjectRequest passing the folder name suffixed by /
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucket, path, emptyContent, metadata);

            // send request to S3 to create folder
            this.client.putObject(putObjectRequest);
        } catch (Exception e) {
           throw new CloudException(e.getMessage()+"Exception occurred in createFolder ");
        }
    }


    public void fileDownload(String src, String dest)throws CloudException {

        try {
            // Adding file name to the destination path
            String[] array = src.split("/");
            int len = array.length;
            dest = dest.concat(array[len - 1]);
            // Get an object and getting its contents.
            System.out.println("Downloading an object");
            S3Object fullObject = this.client.getObject(new GetObjectRequest(this.bucket, src));
            // Get a range of bytes from an object and print the bytes.
            InputStream inputStream = fullObject.getObjectContent();
            File file = new File(dest);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
          throw new CloudException(e.getMessage()+"Exception Occurred in fileDownload");
        }

    }


    public void deleteFile(String filepath) throws CloudException {

        try {
            this.client.deleteObject(this.bucket, filepath);
        } catch (Exception e) {
            throw new CloudException(e.getMessage()+"Exeception occurred in deleteFile");
        }
    }

    public ArrayList<String> listDirectory(String prefix)throws CloudException {
        ArrayList<String> keys = new ArrayList<String>();

        try {

            List<S3ObjectSummary> list = this.client.listObjects(this.bucket, prefix).getObjectSummaries();
            for (S3ObjectSummary objectSummary : list) {
                keys.add(objectSummary.getKey());
            }

        } catch (Exception e) {
            throw new CloudException(e.getMessage()+"Exception occurred in listDirectory");
        }
        return keys;
    }
    public void deleteFolder(String folder) throws CloudException{
        try {


            List<String> list = this.listDirectory(folder);
            for (String key : list) {
                this.deleteFile(key);

            }
        }catch (Exception e){
            throw new CloudException(e.getMessage()+"Exception Occurred in delete Folder");
        }

    }
    public void folderDownload(String folderpath,String dest) throws CloudException{

       try {
           List<String> list = this.listDirectory(folderpath);


           for (String key : list) {
               if (!key.endsWith("/")) {
                   String temp = key;
                   temp = temp.replace(folderpath, "");
                   String[] strings = temp.split("/");
                   String add = "";
                   for (int i = 0; i < strings.length - 1; i++) add += strings[i] + "\\";
                   dest = dest + add;
                   System.out.println(dest);
                   this.fileDownload(key, dest);
               } else {
                   key = key.replace(folderpath, "");
                   if (key == "") continue;
                   File dir = new File(dest + key.replace("/", "\\"));
                   dir.mkdirs();
               }
           }
       }catch (Exception e){
           throw new CloudException(e.getMessage()+"Exception occurred in folder Download");
       }

    }

}