package sg.ntu.core.util.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class used to write bitmap to SD-Card
 */
public final class WriteBitmap {

    /**
     * @param context
     * @param nameResource
     * @param pathOfImageToWrite
     * @throws java.io.IOException
     */
    public static void writeBitmapFromDrawable(Context context,
                                               String nameResource, String pathOfImageToWrite) throws IOException {
        // get id of bitmap from drawable folder
        int id = context.getResources().getIdentifier(nameResource, "drawable",
                context.getPackageName());
        // get bitmap from id
        Bitmap bitmap = BitmapFactory
                .decodeResource(context.getResources(), id);
        if (bitmap != null) {
            // write bitmap
            writeBitmap(bitmap, pathOfImageToWrite, 100);
        }
    }

    /**
     * @param bitmap
     * @param pathOfImage
     * @param quality
     * @throws java.io.IOException
     */
    @SuppressWarnings("resource")
    public static void writeBitmap(Bitmap bitmap, String pathOfImage,
                                   int quality) throws IOException {
        // create new file in sd card
        File fileImage = null;
        try {
            fileImage = new File(pathOfImage);
            fileImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, arrayOutputStream);

        FileOutputStream fos = new FileOutputStream(fileImage);
        fos.write(arrayOutputStream.toByteArray());
    }

}
