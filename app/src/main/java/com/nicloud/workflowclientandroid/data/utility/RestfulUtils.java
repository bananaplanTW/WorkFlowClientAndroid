package com.nicloud.workflowclientandroid.data.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.nicloud.workflowclientandroid.utility.Utilities;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class RestfulUtils {

    private static final String TAG = "RestfulUtils";


    public static boolean isConnectToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Get the JsonObject from the given url
     * Note: Do not run this method in UI thread
     *
     * @param urlString
     * @return The JsonObject from this url
     */
    public static String getJsonStringFromUrl(String urlString) {
        InputStream inputStream = null;
        String result = null;
        if (urlString != null) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(3000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                conn.connect();
                int responseCode = conn.getResponseCode();
                Log.d("Restful api", "Response Code is : " + responseCode);
                inputStream = conn.getInputStream();

                result = getStringFromInputStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    /**
     * post request
     *
     * @param urlString
     * @param bodyPairs
     * @return
     */
    public static String restfulPostRequest(String urlString, HashMap<String, String> headerPairs, HashMap<String, String> bodyPairs) {
        String result = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        if (urlString != null) {
            try {
                URL url = new URL(urlString);
                String bodyParamsString = URLUtils.buildQueryString(bodyPairs);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                if (headerPairs != null) {
                    Iterator iter = headerPairs.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        conn.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
                    }
                }
                conn.setRequestMethod("POST");
                conn.setReadTimeout(3000);
                conn.setConnectTimeout(3000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.getOutputStream().write(bodyParamsString.getBytes("UTF-8"));
                Log.d("Restful api", "Connecting url " + urlString);
                conn.connect();
                Log.d("Restful api", "Response Code is : " + conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }

                if (inputStream != null) {
                    result = getStringFromInputStream(inputStream);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                conn.disconnect();
            }
        }
        return result;
    }

    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final OkHttpClient client = new OkHttpClient();
    public static String restfulPostFileRequest(String urlString, HashMap<String, String> headerPairs, String filePath) {
        String responseString = "";

        String mimeType = Utilities.getMimeType(filePath);
Log.d(TAG, "uploading file mimeType : " + mimeType);
        File f = new File(filePath);
        Request.Builder builder = new Request.Builder();
        builder.header("Content-Type", mimeType)
                .header("s", "" + f.length())
                .header("fn", f.getName());
        if (headerPairs != null) {
            Iterator iter = headerPairs.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                builder.header((String) entry.getKey(), (String) entry.getValue());
            }
        }
        Request request = builder.url(urlString)
                .post(RequestBody.create(MediaType.parse(mimeType), new File(filePath)))
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }


    public static String restfulGetRequest (String urlString, HashMap<String, String> headerPairs) {
        InputStream inputStream = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (headerPairs != null) {
                Iterator iter = headerPairs.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    conn.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
                }
            }
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d("Restful api", "Response Code is : " + responseCode);
            if (responseCode == 200) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }
            return getStringFromInputStream(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getStringFromInputStream(InputStream in) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            String s;
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ( (s = br.readLine()) != null) {
                sb.append(s);
            }
        } catch (UnsupportedEncodingException e) {
            Log.d("Error","Unsupport UTF-8 data type");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
