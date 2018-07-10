package cn.edu.sdu.online.isdu.util;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class Logger {

    public static void log(String message) {
        String date = new SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
        File log =
                new File(Environment.getExternalStorageDirectory() + "/iSDU/log/" + date + ".log");
        if (!log.exists()) {
            if (!log.getParentFile().exists()) log.getParentFile().mkdirs();
            try {
                log.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(log);
            fileWriter.write(message);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(Throwable e) {
        String date = new SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
        File log =
                new File(Environment.getExternalStorageDirectory() + "/iSDU/log/" + date + ".log");
        if (!log.exists()) {
            if (!log.getParentFile().exists()) log.getParentFile().mkdirs();
            try {
                log.createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        try {
            PrintWriter fileWriter = new PrintWriter(log);
            e.printStackTrace(fileWriter);
            fileWriter.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
