package com.ruby.workmanager;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.ruby.workmanager.workers.BlurWorker;
import com.ruby.workmanager.workers.CleanupWorker;
import com.ruby.workmanager.workers.SaveImageToFileWorker;

import java.util.List;

public class BlurViewModel extends AndroidViewModel {

    private Uri mImageUri;
    private WorkManager mWorkManager;
    private LiveData<List<WorkInfo>> mWorkInfo;
    private Uri mOutputUri;

    public BlurViewModel(@NonNull Application application) {
        super(application);
        mWorkManager = WorkManager.getInstance(application);
        mWorkInfo = mWorkManager.getWorkInfosByTagLiveData(Constants.TAG_OUTPUT);
//        ListenableFuture info = mWorkManager.getWorkInfosByTag(Constants.TAG_OUTPUT);
//        info.addListener(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, new Executor() {
//            @Override
//            public void execute(Runnable runnable) {
//            }
//        });
    }

    public LiveData<List<WorkInfo>> getWorkInfo() {
        return mWorkInfo;
    }

    public Uri getOutputUri() {
        return mOutputUri;
    }

    public void setOutputUri(String outputUri) {
        mOutputUri = uriOrNull(outputUri);
    }

    /**
     * Create the WorkRequest to apply the blur and save the resulting image
     *
     * @param blurLevel The amount to blur the image
     */
    void applyBlur(int blurLevel) {
//        PeriodicWorkRequest saveRequest = new PeriodicWorkRequest
//                .Builder(SaveImageToFileWorker.class,1, TimeUnit.HOURS)
//                .build();

//        WorkContinuation continuation = mWorkManager.beginWith(OneTimeWorkRequest.from(CleanupWorker.class));

        WorkContinuation continuation = mWorkManager
                .beginUniqueWork(Constants.IMAGE_MANIPULATION_WORK_NAME,
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequest.from(CleanupWorker.class));


        for (int i = 0; i < blurLevel; i++) {
            OneTimeWorkRequest.Builder blurBuilder =
                    new OneTimeWorkRequest.Builder(BlurWorker.class);
            if (i == 0) {
                blurBuilder.setInputData(createInputDataForUri());
            }
            continuation = continuation.then(blurBuilder.build());
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
//                .setRequiresStorageNotLow(true)
                .build();

        OneTimeWorkRequest save =
                new OneTimeWorkRequest.Builder(SaveImageToFileWorker.class)
                        .setConstraints(constraints)
                        .addTag(Constants.TAG_OUTPUT)
                        .build();
        continuation = continuation.then(save);

        continuation.enqueue();

//        mWorkManager.enqueue(OneTimeWorkRequest.from(BlurWorker.class));
    }

    void cancelWork() {
        mWorkManager.cancelUniqueWork(Constants.IMAGE_MANIPULATION_WORK_NAME);
    }

    private Data createInputDataForUri() {
        Data.Builder builder = new Data.Builder();
        if (mImageUri != null) {
            builder.putString(Constants.KEY_IMAGE_URI, mImageUri.toString());
        }
        return builder.build();
    }

    private Uri uriOrNull(String uriString) {
        if (!TextUtils.isEmpty(uriString)) {
            return Uri.parse(uriString);
        }
        return null;
    }

    /**
     * Setters
     */
    void setImageUri(String uri) {
        mImageUri = uriOrNull(uri);
    }

    /**
     * Getters
     */
    Uri getImageUri() {
        return mImageUri;
    }

}