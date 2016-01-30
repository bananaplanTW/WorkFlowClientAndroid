package com.nicloud.workflowclient.backgroundtask.asyntask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

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
public class LoadImageTask extends AsyncTask<Void, Void, Drawable> {

    private Context mContext;

    private Uri mUri;
    private ImageView mImageView;
    private Drawable mDrawable;


    public LoadImageTask(Context context, Uri uri, ImageView imageView, Drawable drawable) {
        mContext = context;
        mUri = uri;
        mImageView = imageView;
        mDrawable = drawable;
    }

    @Override
    protected Drawable doInBackground(Void... voids) {
        if (RestfulUtils.isConnectToInternet(mContext)) {
            String fileName = Utils.SHA1(mUri.toString());
            if (TextUtils.isEmpty(fileName)) {
                // fallback
                fileName = mUri.toString().substring(
                        mUri.toString().lastIndexOf('/') + 1, mUri.toString().indexOf('%'));
            }
            File file = new File(mContext.getCacheDir(), fileName);
            try {
                InputStream inputStream = null;
                try {
                    if (!file.exists()) {
                        inputStream = new URL(mUri.toString()).openStream();
                        // write image data to storage first to scale bitmap
                        // TODO: NEED to tune performance, IO-bound
                        OutputStream output = new FileOutputStream(file);
                        try {
                            byte[] buffer = new byte[8 * 1024];
                            int read;
                            while ((read = inputStream.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }
                            output.flush();
                        } finally {
                            output.close();
                        }
                    }
                    Bitmap bitmap = Utils.scaleBitmap(mContext, file.getAbsolutePath());
                    return new BitmapDrawable(mContext.getResources(), bitmap);
                } catch (MalformedURLException e) {
                    cancel(true);
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            } catch (IOException e) {
                cancel(true);
                e.printStackTrace();
            }
        } else {
            cancel(true);
        }

        return null;
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();

    }
    @Override
    protected void onPostExecute(Drawable drawable) {
        super.onPostExecute(drawable);
        mImageView.setImageDrawable(drawable);
        mDrawable = drawable;
    }
}
