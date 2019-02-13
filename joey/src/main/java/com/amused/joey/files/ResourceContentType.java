package com.amused.joey.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Project: JoeyTools
 * Create : joey
 * Date   : 2019/01/30 17:24
 * Description:
 */
public class ResourceContentType {
    public static boolean isGif(File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            int[] flags = new int[5];
            flags[0] = inputStream.read();
            flags[1] = inputStream.read();
            flags[2] = inputStream.read();
            flags[3] = inputStream.read();
            inputStream.skip(inputStream.available() - 1);
            flags[4] = inputStream.read();
            return flags[0] == 71 && flags[1] == 73 && flags[2] == 70 && flags[3] == 56 && flags[4] == 0x3B;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean isJpg(File file){
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            int b[] = new int[4];
            b[0] = inputStream.read();
            b[1] = inputStream.read();
            inputStream.skip(inputStream.available() - 2);
            b[2] = inputStream.read();
            b[3] = inputStream.read();
            inputStream.close();
            return b[0] == 255 && b[1] == 216 && b[2] == 255 && b[3] == 217;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
