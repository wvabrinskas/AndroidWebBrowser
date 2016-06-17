package com.test.wvabrinskas.testapplication;
import android.os.AsyncTask;
import android.util.Log;

import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import com.test.wvabrinskas.testapplication.MainActivity;
/**
 * Created by wvabrinskas on 6/17/16.
 */
public class JSONParser extends AsyncTask<String, Void, JSONObject> {

    private static JSONParser ourInstance = new JSONParser();

    public static JSONParser getInstance() {
        return ourInstance;
    }

    public static JSONObject currentPostDataObject = null;
    public static JSONObject currentPostObject = null;
    public static MainActivity controllerActivity = null;

    private Exception exception;

    private static final String getPostURL = "";

    private JSONParser() {
    }

    @Override
    protected JSONObject doInBackground(String[] params) {
        // do above Server call here
        JSONObject post = null;
        try {
            post = readJsonForPostID(params[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return post;
    }

    protected void onPostExecute(JSONObject post) {
        if (this.exception == null) {
            currentPostObject = post;
            Log.d("GotPost","got post"+post);
            try {
                controllerActivity.setup();
            } catch (JSONException e) {
                Log.d("LoadWebview","Couldn't setup webview due to JSON parsing exception");
                e.printStackTrace();
            }
        } else  {
            this.exception.printStackTrace();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static void readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            //return json;
            currentPostDataObject = json;
        } finally {
            is.close();
        }
    }



    public static JSONObject readJsonForPostID(String id) throws IOException, JSONException {
        InputStream is = new URL(getPostURL.concat(id)).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            //return json;
           // currentPostObject = json;
            return json;
        } finally {
            is.close();
        }
    }
}