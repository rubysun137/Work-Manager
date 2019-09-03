package com.ruby.workmanager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

public class BlurViewModel extends AndroidViewModel {

    private Uri mImageUri;

    public BlurViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Create the WorkRequest to apply the blur and save the resulting image
     * @param blurLevel The amount to blur the image
     */
    void applyBlur(int blurLevel) {

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