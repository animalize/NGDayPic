package man.animalize.ngdaypic.Utility;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileReadWrite {

    private static final String appPath = "DayPic";
    private static String sdCardPath;

    // 组合路径
    private static String combinePath(String path1, String path2) {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }

    public static byte[] readFile(String fileName) {
        if (sdCardPath == null) {
            File sdCard = Environment.getExternalStorageDirectory();
            sdCardPath = sdCard.getAbsolutePath();
        }
        File dir = new File(combinePath(sdCardPath, appPath));
        File file = new File(dir, fileName);

        int size = (int) file.length();
        byte[] bytes = new byte[size];
        BufferedInputStream buf = null;
        try {
            buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();

            return bytes;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (buf != null)
                try {
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static boolean writeFile(byte[] data, String fileName) {
        if (sdCardPath == null) {
            File sdCard = Environment.getExternalStorageDirectory();
            sdCardPath = sdCard.getAbsolutePath();
        }
        File dir = new File(combinePath(sdCardPath, appPath));
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, fileName);

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(data);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isFileExist(String fileName) {
        if (sdCardPath == null) {
            File sdCard = Environment.getExternalStorageDirectory();
            sdCardPath = sdCard.getAbsolutePath();
        }

        File dir = new File(combinePath(sdCardPath, appPath));
        if (!dir.exists())
            return false;

        File file = new File(dir, fileName);
        return file.exists();

    }

    public static boolean delFile(String fileName) {
        if (sdCardPath == null) {
            File sdCard = Environment.getExternalStorageDirectory();
            sdCardPath = sdCard.getAbsolutePath();
        }

        File dir = new File(combinePath(sdCardPath, appPath));
        if (!dir.exists())
            return false;

        File file = new File(dir, fileName);
        if (!file.exists())
            return false;

        return file.delete();
    }
}
