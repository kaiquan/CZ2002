package sg.ntu.core.util.bitmap;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore.Images;
import android.util.Base64;
import android.widget.ImageView;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import sg.ntu.core.util.Log;

/**
 * Bitmap util class
 */
public class BitmapUtil {

    /**
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * Scale bitmap from resource
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap scaleBitmap(Resources res, int resId, int reqWidth,
                                     int reqHeight) {
        // create bitmap options
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Scale bitmap from path
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap scaleBitmap(String pathName, int reqWidth,
                                     int reqHeight) {
        // create bitmap options
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static Bitmap scaleBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    /**
     * Crop bitmap
     *
     * @param context
     * @param source
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public static Bitmap cropBitmap(Context context, Bitmap source, int left,
                                    int top, int right, int bottom) {
        Bitmap result = null;
        try {
            Paint paint = new Paint();
            paint.setFilterBitmap(true);

            int targetWidth = source.getWidth();
            int targetHeight = source.getHeight();

            Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                    targetHeight, Config.ARGB_8888);

            Canvas canvas = new Canvas(targetBitmap);
            canvas.drawBitmap(source,
                    new Rect(0, 0, source.getWidth(), source.getHeight()),
                    new Rect(0, 0, targetWidth, targetHeight), paint);

            Matrix matrix = new Matrix();
            matrix.postScale(1f, 1f);
            result = Bitmap.createBitmap(targetBitmap, left, top, right,
                    bottom, matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @param source
     * @param newHeight
     * @param newWidth
     * @return
     */
    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight,
                                         int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width,
        // respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap
        // will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top
                + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our
        // new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight,
                source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    /**
     * @param bitmap
     * @param leftRightThk
     * @param bottomThk
     * @param padTop
     * @return
     */
    public static Bitmap drawShadow(Bitmap bitmap, int leftRightThk,
                                    int bottomThk, int padTop) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int newW = w - (leftRightThk * 2);
        int newH = h - (bottomThk + padTop);

        Config conf = Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(w, h, conf);
        Bitmap sbmp = Bitmap.createScaledBitmap(bitmap, newW, newH, false);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas c = new Canvas(bmp);

        // Left
        int leftMargin = (leftRightThk + 7) / 2;
        Shader lshader = new LinearGradient(0, 0, leftMargin, 0,
                Color.TRANSPARENT, Color.BLACK, TileMode.CLAMP);
        paint.setShader(lshader);
        c.drawRect(0, padTop, leftMargin, newH, paint);

        // Right
        Shader rshader = new LinearGradient(w - leftMargin, 0, w, 0,
                Color.BLACK, Color.TRANSPARENT, TileMode.CLAMP);
        paint.setShader(rshader);
        c.drawRect(newW, padTop, w, newH, paint);

        // Bottom
        Shader bshader = new LinearGradient(0, newH, 0, bitmap.getHeight(),
                Color.BLACK, Color.TRANSPARENT, TileMode.CLAMP);
        paint.setShader(bshader);
        c.drawRect(leftMargin - 3, newH, newW + leftMargin + 3,
                bitmap.getHeight(), paint);
        c.drawBitmap(sbmp, leftRightThk, 0, null);

        return bmp;
    }

    /**
     * @param bitmap
     * @param roundPx
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffffffff;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * @param bitmap
     * @return
     */

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Convert Bitmap to String
     *
     * @param bm
     * @param quality
     * @return
     * @throws OutOfMemoryError
     */
    public static String convertBitmapToString(Bitmap bm, int quality)
            throws OutOfMemoryError {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    /**
     * Convert String to Bitmap
     *
     * @param encodedImage
     * @return
     * @throws OutOfMemoryError
     */
    public static Bitmap convertStringToBitmap(String encodedImage)
            throws OutOfMemoryError {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0,
                decodedString.length);
        return bitmap;
    }

    /**
     * @param context
     * @param inFile
     * @param outFile
     * @param destHeight
     * @param destWidth
     */
    public static void resizeImage(Context context, String inFile,
                                   String outFile, int destHeight, int destWidth) {
        try {
            int inWidth = 0;
            int inHeight = 0;

            InputStream in = new FileInputStream(inFile);

            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;
            // byte per pixel = 4
            int sizeOfFile = inWidth * inHeight * 4;
            // 50M = 52428800
            if (sizeOfFile < 0) { // size of file is exceed 50M
                if (getAvailableMemory(context) - 20480 < 52428800) {
                    Log.e("Out of memory");
                    return;
                }
            } else if (sizeOfFile > getAvailableMemory(context) - 20480) {
                Log.e("Out of memory");
                return;
            }

            // decode full image pre-resized
            in = new FileInputStream(inFile);
            options = new BitmapFactory.Options();
            // calc rought re-size (this is no exact resize)
            options.inSampleSize = Math.max(inWidth / destWidth, inHeight
                    / destHeight);
            options.inPurgeable = true;
            options.inPreferredConfig = Config.RGB_565;
            try {
                // decode full image
                Bitmap roughBitmap = BitmapFactory.decodeStream(in, null,
                        options);

                // calc exact destination size
                Matrix m = new Matrix();
                RectF inRect = new RectF(0, 0, roughBitmap.getWidth(),
                        roughBitmap.getHeight());
                RectF outRect = new RectF(0, 0, destWidth, destHeight);
                m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
                float[] values = new float[9];
                m.getValues(values);

                // resize bitmap
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap,
                        (int) (roughBitmap.getWidth() * values[0]),
                        (int) (roughBitmap.getHeight() * values[4]), true);

                in.close();

                // save image
                FileOutputStream out = new FileOutputStream(outFile);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.close();
            } catch (Exception e) {
                Log.e(e.toString());
            } catch (OutOfMemoryError e) {
                Log.e(e.toString());
            }
        } catch (IOException e) {
            Log.e(e.toString());
        } catch (OutOfMemoryError e) {
            Log.e(e.toString());
        }
    }

    /**
     * @param context
     * @return
     */
    public static long getAvailableMemory(Context context) {
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return mi.availMem;
    }

    /**
     * get orientation of bitmap
     *
     * @param exifOrientation
     * @return
     */
    public static float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /**
     * rotate bitmap
     *
     * @param context
     * @param uri
     * @return
     */
    public static float getRotationInDegreeOfImage(Context context, Uri uri) {
        if (uri.getScheme().equals("content")) {
            String[] projection = {Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(uri, projection,
                    null, null, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } else if (uri.getScheme().equals("file")) {
            try {
                ExifInterface exif = new ExifInterface(uri.getPath());
                float rotation = exifOrientationToDegrees(exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL));
                return rotation;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0f;
    }

    /**
     * get bitmap from image view
     *
     * @param imageView
     * @return
     */
    public static Bitmap getBitmapFromImageView(ImageView imageView) {
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(imageView.getWidth(),
                imageView.getHeight(), Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
        imageView.draw(canvas);
        return bitmap;
    }

    /**
     * get bitmap from image path
     *
     * @param scale
     * @param imagePath
     * @return
     */
    public static Bitmap getBitmapFromPath(Context context, int scale,
                                           String imagePath) {

        Uri uri = Uri.parse(imagePath);

        float rotation = getRotationInDegreeOfImage(context, uri);

        Matrix matrix = new Matrix();
        if (rotation != 0f) {
            matrix.preRotate(rotation);
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = scale;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);

        if (rotation != 0f) {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public static Bitmap getVideoFrame(String uri) {
        Log.d("uri video: " + uri);
        if (Build.VERSION.SDK_INT <= 10) {
            return null;
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(uri);
            return retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            Log.e(e.toString());
        } catch (RuntimeException e) {
            Log.e(e.toString());
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                Log.e(e.toString());
            }
        }
        return null;
    }
}
