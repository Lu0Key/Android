package cn.edu.sdu.online.isdu.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileUtil {
    public static String getStringFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return null;

        try {
            Scanner scanner = new Scanner(file);
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNext()) sb.append(scanner.nextLine());
            return sb.toString();
        } catch (FileNotFoundException e) {
            Logger.log(e);
            return "";
        }
    }

    public static void open(String filePath) {

    }
}
