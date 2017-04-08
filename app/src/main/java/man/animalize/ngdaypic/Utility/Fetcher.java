package man.animalize.ngdaypic.Utility;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Fetcher {
    private int mConnectTimeout = 360; //360秒
    private int mReadTimeout = 600; //600秒

    public byte[] getByte(final String urlString) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
                .readTimeout(mReadTimeout, TimeUnit.SECONDS)
                .build();

        try {
            Request request = new Request.Builder()
                    .url(urlString)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().bytes();
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }

    public String getString(final String urlString) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
                .readTimeout(mReadTimeout, TimeUnit.SECONDS)
                .build();

        try {
            Request request = new Request.Builder()
                    .url(urlString)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }
}

