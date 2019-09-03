package com.ruby.workmanager.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ruby.workmanager.Constants;

public class BlurWorker extends Worker {

    private static final String TAG = BlurWorker.class.getSimpleName();

    public BlurWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        String pictureUri = getInputData().getString(Constants.KEY_IMAGE_URI);

        try {
//            Bitmap picture = BitmapFactory.decodeResource(context.getResources(), R.drawable.test);
            if(TextUtils.isEmpty(pictureUri)){
                Log.e(TAG, "Invalid input uri");
                throw new IllegalArgumentException("Invalid input uri");
            }
            ContentResolver resolver = context.getContentResolver();
            Bitmap picture = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(pictureUri)));

            Bitmap blurredPic = WorkerUtils.blurBitmap(picture, context);
            Uri outputUri = WorkerUtils.writeBitmapToFile(context, blurredPic);
            WorkerUtils.makeStatusNotification("output is" + outputUri.toString(), context);

            Data data = new Data.Builder()
                    .putString(Constants.KEY_IMAGE_URI, outputUri.toString())
                    .build();
            return Result.success(data);
        } catch (Throwable throwable) {
            Log.e(TAG, "Error applying blur", throwable);
            return Result.failure();
        }

    }
}
