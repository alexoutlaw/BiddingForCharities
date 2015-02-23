package com.vie.biddingforcharities.logic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;

public class GetBitmapTask extends AsyncTask<String, Void, Bitmap> {
    String url;
    int targetHeight, targetWidth;
    Activity parent;
    File cacheDir;
    WeakReference<ImageView> imageViewRef;

    public GetBitmapTask(Activity parent, ImageView imageView) {
        targetHeight = 0;
        targetWidth = 0;
        this.parent = parent;
        this.cacheDir = parent.getCacheDir();
        if(imageView == null)
            imageViewRef = null;
        else
            imageViewRef = new WeakReference<ImageView>(imageView);
    }

    public GetBitmapTask(Activity parent, ImageView imageView, int targetHeight, int targetWidth) {
        this.targetHeight = targetHeight;
        this.targetWidth = targetWidth;
        this.parent = parent;
        this.cacheDir = parent.getCacheDir();
        imageViewRef = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap target = null;
        String url_str = params[0];
        URI uri;

        //Encoding
        try {
            URL url = new URL(url_str);
            uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch(Exception e) {
            e.printStackTrace();
            Log.d("test", "GetBitmapTask URI Error, url_str=" + url_str);
            return null;
        }

        // Get file if cached, else download and decode from server uri
        String fileName = new File(uri.toString()).getName();
        File cacheFile = new File(cacheDir, fileName);
        if(cacheFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            target = BitmapFactory.decodeFile(cacheFile.getAbsolutePath(), options);
        }
        else {

            final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
            final HttpGet getRequest = new HttpGet(uri);

            try {
                HttpResponse response = client.execute(getRequest);
                final int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
                    return null;
                }

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    FileOutputStream outputStream = null;
                    try {
                        inputStream = entity.getContent();
                        target = BitmapFactory.decodeStream(inputStream);

                        // Save to local storage cache
                        outputStream = new FileOutputStream(cacheFile);
                        target.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if(outputStream != null) {
                            outputStream.flush();
                            outputStream.close();
                        }

                        entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                // Could provide a more explicit error message for IOException or IllegalStateException
                getRequest.abort();
                Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
            } finally {
                if (client != null) {
                    client.close();
                }
            }
        }

        // Scale target Bitmap if dims set
        if(target != null) {
            if (targetWidth == 0 && targetHeight == 0) {
                return target;
            } else {
                if (targetHeight == 0) {
                    targetHeight = targetWidth * target.getHeight() / target.getWidth();
                }
                Bitmap scaled = Bitmap.createScaledBitmap(target, targetWidth, targetHeight, false);
                target.recycle();
                return scaled;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(isCancelled()) {
            bitmap = null;
        }

        Log.d("test", "GetBitmapTask onPostExecure: returning bitmap " + bitmap.getHeight() + "x" + bitmap.getWidth());

        if(imageViewRef != null) {
            ImageView imageView = imageViewRef.get();
            if(imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}