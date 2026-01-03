package kgriffon.virtualstorage;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public class Config {

    public static String storage;
    public static String host;
    public static String port;
    public static String database;
    public static String user;
    public static String password;

    public static Path configPath = FabricLoader.getInstance().getConfigDir();
    public static String properties = configPath + "/virtual-storage.properties";

    public static void loadFile() {
        Properties configs = new Properties();
        try {
            configs.load(new FileInputStream(properties));
        } catch (IOException e) {
            VirtualStorage.LOGGER.error("Can't load file.");
        }

        storage = configs.getProperty("storage");
        host = configs.getProperty("host");
        port = configs.getProperty("port");
        database = configs.getProperty("database");
        user = configs.getProperty("user");
        password = configs.getProperty("password");
    }

    public static boolean exist() {
        return new File(properties).exists();
    }

    public static void writeDefaultConfig() {

        try (Writer writer = new FileWriter(properties)) {
            writer.write("storage=mariadb\n");
            writer.write("host=127.0.0.1\n");
            writer.write("port=3306\n");
            writer.write("database=virtual_storage\n");
            writer.write("user=user\n");
            writer.write("password=1234\n");
        } catch (IOException e) {
            VirtualStorage.LOGGER.info("Can't write file.");
        }
    }

    public static void createConfigFile() {
        File file = new File(properties);
        try {
            file.createNewFile();
        } catch (IOException e) {
            VirtualStorage.LOGGER.error("Can't create file.");
        }
    }

}
