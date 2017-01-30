package man.animalize.ngdaypic.Base;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;

import java.io.ByteArrayOutputStream;

public class PictureUtils {

    @SuppressWarnings("deprecation")
    public static Bitmap getScaledBitmap(byte[] data,
                                         float destWidth,
                                         float destHeight) {
        // read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;

        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (destWidth < destHeight) {
                inSampleSize = Math.round(srcWidth / destWidth);
            } else {
                inSampleSize = Math.round(srcHeight / destHeight);
            }
        }
        //Log.i("缩放", srcWidth + " " + srcHeight + " " + inSampleSize);

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeByteArray(data,
                0,
                data.length,
                options);
        return bitmap;
    }

    // 取得小图标
    public static byte[] getIcon(byte[] data) {
        Bitmap bitmap = getScaledBitmap(data, 48, 32);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap getBitmapForView(Activity a, byte[] data) {
        Display display = a.getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        return getScaledBitmap(data, p.x, p.y);
    }
}

