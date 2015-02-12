package man.animalize.ngdaypic.Utility;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Fetcher {
    private int mRetryCount = 4;
    private int mConnectTimeout = 1000 * 10; //10秒
    private int mReadTimeout = 1000 * 60; //60秒

    private byte[] pGetByte(URLConnection con) throws IOException {
        InputStream in = null;
        ByteArrayOutputStream ous = null;

        try {
            in = con.getInputStream();
            ous = new ByteArrayOutputStream();

            final byte buffer[] = new byte[2048];
            int count;
            while ((count = in.read(buffer, 0, 2048)) != -1) {
                ous.write(buffer, 0, count);
            }
            return ous.toByteArray();

        } finally {
            if (in != null) {
                in.close();
            }
            if (ous != null) {
                ous.close();
            }
        }
    }

    public byte[] getByte(final String urlString) {

        URLConnection con;
        try {
            con = new URL(urlString).openConnection();
            con.setConnectTimeout(mConnectTimeout);
            con.setReadTimeout(mReadTimeout);
        } catch (IOException e) {
            return null;
        }

        for (int i = 0; i < mRetryCount; i++) {
            try {
                return pGetByte(con);
            } catch (IOException e) {
            }
        }
        return null;
    }
}

