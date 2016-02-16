package com.nicloud.workflowclient.backgroundtask.asyntask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.utility.utils.RestfulUtils;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by daz on 10/10/15.
 */
public class LoadImage extends LoadDrawable {

    private Drawable mDrawable;


    public LoadImage(Context context, String imageUri, ImageView imageView, Drawable drawable) {
        super(context, imageUri, imageView);
        mDrawable = drawable;
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
        super.onPostExecute(drawable);
        mDrawable = drawable;
    }
}