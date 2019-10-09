package configs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;


public class Config {
    private Properties prop;

    private static Config obj;

    private Config() {

        try {

            InputStream input = new FileInputStream
                    ("C:\\Users\\HP_USER\\IdeaProjects\\s3client\\src\\main\\resources\\config.properties");
            this.prop = new Properties();
            this.prop.load(input);

        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public static Config getInstance() {
        if (obj == null) {
            obj = new Config();
        }
        return obj;
    }

    public String get(String key) {

        return this.prop.getProperty(key);

    }

}
