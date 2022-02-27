package com.zybooks.thebanddatabase;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SaveCheckActivity extends AppCompatActivity {
    private File mPhotoFile;
    private ImageView mPhotoImageView;
    private SeekBar mSeekBar;
    private Button mSaveButton;

    // For adding brightness
    private int mMultColor = 0xffffffff;
    private int mAddColor = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_check);

        mPhotoImageView = findViewById(R.id.photoViewID);

        mSaveButton = findViewById(R.id.saveButtonID);
        mSaveButton.setEnabled(false);

        mSeekBar = findViewById(R.id.brightnessSeekBar);
        mSeekBar.setVisibility(View.INVISIBLE);
    }

    public void takePhotoClick(View view) {
        mPhotoFile = createImageFile();

        // Create a content URI to grant camera app write permission to mPhotoFile
        Uri photoUri = FileProvider.getUriForFile(this,
                "com.zybooks.thebanddatabase.fileprovider", mPhotoFile);

        // Start camera app
        mTakePicture.launch(photoUri);
    }

    private final ActivityResultLauncher<Uri> mTakePicture = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success) {
                    displayPhoto();
                    mSeekBar.setProgress(100);
                    mSeekBar.setVisibility(View.VISIBLE);
                    mSaveButton.setEnabled(true);
                }
            });


    private void displayPhoto() {
        // Get ImageView dimensions
        int targetWidth = mPhotoImageView.getWidth();
        int targetHeight = mPhotoImageView.getHeight();

        // Get bitmap dimensions
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath(), bmOptions);
        int photoWidth = bmOptions.outWidth;
        int photoHeight = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoWidth / targetWidth, photoHeight / targetHeight);

        // Decode the image file into a smaller bitmap that fills the ImageView
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath(), bmOptions);

        // Display smaller bitmap
        mPhotoImageView.setImageBitmap(bitmap);
    }

    private void changeBrightness(int brightness) {
        // TODO: Change brightness
        // 100 is the middle value
        if (brightness > 100) {
            // Add color
            float addMult = brightness / 100.0f - 1;
            mAddColor = Color.argb(255, (int) (255 * addMult), (int) (255 * addMult),
                    (int) (255 * addMult));
            mMultColor = 0xffffffff;
        }
        else {
            // Scale color down
            float brightMult = brightness / 100.0f;
            mMultColor = Color.argb(255, (int) (255 * brightMult), (int) (255 * brightMult),
                    (int) (255 * brightMult));
            mAddColor = 0;
        }
        Log.d(TAG, "changeBrightness: ");
        LightingColorFilter colorFilter = new LightingColorFilter(mMultColor, mAddColor);
        mPhotoImageView.setColorFilter(colorFilter);
    }

    private File createImageFile() {
        // Create a unique image filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFilename = "photo_" + timeStamp + ".jpg";

        // Get file path where the app can save a private image
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, imageFilename);
    }

    public void savePhotoClick(View view) {
        // TODO: Save the altered photo
        // Don't allow Save button to be pressed while image is saving
        mSaveButton.setEnabled(false);

        // Save image in background thread
        ImageSaver imageSaver = new ImageSaver(this);
        imageSaver.saveAlteredPhotoAsync(mPhotoFile, mMultColor, mAddColor, result -> {

            // Show appropriate message
            int message = result ? R.string.photo_saved : R.string.photo_not_saved;
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            // Allow Save button to be clicked again
            mSaveButton.setEnabled(true);
        });
    }

    public static class ImageSaver {

        interface SaveImageCallback {
            void onComplete(boolean result);
        }

        private final Executor mExecutor;
        private final Handler mHandler;
        private final Context mActivityContext;

        public ImageSaver(Context context) {
            mExecutor = Executors.newSingleThreadExecutor();
            mHandler = new Handler(Looper.getMainLooper());
            mActivityContext = context;
        }

        public void saveAlteredPhotoAsync(final File photoFile, final int filterMultColor,
                                          final int filterAddColor, final SaveImageCallback callback) {
            // Call saveAlteredPhoto() on a background thread
            mExecutor.execute(() -> {
                try {
                    saveAlteredPhoto(photoFile, filterMultColor, filterAddColor);
                    notifyResult(callback, true);
                } catch (IOException e) {
                    e.printStackTrace();
                    notifyResult(callback, false);
                }
            });
        }

        private void notifyResult(SaveImageCallback callback, boolean result) {
            // Call onComplete() on the main thread
            mHandler.post(() -> callback.onComplete(result));
        }

        private void saveAlteredPhoto(File photoFile, int filterMultColor, int filterAddColor)
                throws IOException {
            // Read original image
            Bitmap origBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), null);

            // Create a new bitmap with the same dimensions as the original
            Bitmap alteredBitmap = Bitmap.createBitmap(origBitmap.getWidth(), origBitmap.getHeight(), origBitmap
                    .getConfig());

            // Draw original bitmap on canvas and apply the color filter
            Canvas canvas = new Canvas(alteredBitmap);
            Paint paint = new Paint();
            LightingColorFilter colorFilter = new LightingColorFilter(filterMultColor, filterAddColor);
            paint.setColorFilter(colorFilter);
            canvas.drawBitmap(origBitmap, 0, 0, paint);

            // Create an entry for the MediaStore
            ContentValues imageValues = new ContentValues();
            imageValues.put(MediaStore.MediaColumns.DISPLAY_NAME, photoFile.getName());
            imageValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            imageValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            // Insert a new row into the MediaStore
            ContentResolver resolver = mActivityContext.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageValues);
            OutputStream outStream = null;

            try {
                if (uri == null) {
                    throw new IOException("Failed to insert MediaStore row");
                }

                // Save the image using the URI
                outStream = resolver.openOutputStream(uri);
                alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            } finally {
                if (outStream != null) {
                    outStream.close();
                }
            }
        }

    }
}