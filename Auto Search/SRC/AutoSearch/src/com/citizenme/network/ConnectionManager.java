package com.citizenme.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;

import com.citizenme.autosearch.Log;
import com.citizenme.model.GoogleSearchModel;
import com.google.gson.Gson;

public class ConnectionManager {

    private static ConnectionManager instance;

    // Keeping it hardcoded as of now for this exercise only
    public static final String BASE_URL = "https://www.googleapis.com/"
            + "customsearch/v1?key=AIzaSyD6O1oyxpy9l9sYmPCaTQkkVbYuV-NlMZU&cx=017576662512468239146:omuauf_lfve"
            + "&q=";

    /**
     * create Singleton object of the class
     */
    public synchronized static ConnectionManager createInstance() {
        if (instance != null) {
            return instance;
        }
        if (instance == null) {
            Log.d("ConnectionManager Single Object Created");
            instance = new ConnectionManager();
        }
        return instance;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    // private static String getAbsoluteUrlDiffPort(String relativeUrl) {
    // return BASE_URL_PORT + relativeUrl;
    // }

    private JSONArray getJsonArray(HttpResponse response, String entity) {

        JSONObject jsonObject = parseJson(response);
        JSONArray jArray = null;
        try {
            jArray = jsonObject.getJSONArray(entity);
        } catch (JSONException e) {
            Log.d("Got exception " + e.getMessage());
        }
        return jArray;
    }

    static JSONObject parseJson(HttpResponse response) {

        JSONObject jsonObject = null;
        StringBuilder content = new StringBuilder();
        String line;

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(response.getEntity()
                    .getContent()));
            while (null != (line = br.readLine())) {
                content.append(line);
            }
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        // try parse the string to a JSON object
        try {
            jsonObject = new JSONObject(content.toString());

        } catch (JSONException e) {
            Log.e("Error parsing data " + e.toString());
        }

        return jsonObject;

    }

    public boolean customSearch(String keywordToSearch,
            final APIResponseHandler<GoogleSearchModel> responseListener) {

        AsyncClient.getRequest(getAbsoluteUrl(keywordToSearch),
                new ResponseListener() {

                    @Override
                    public void onResponseReceived(HttpResponse response) {
                        // TODO : result may be verified here later on

                        GoogleSearchModel googleSearchModel = null;

                        JSONObject jsonObject = parseJson(response);
                        Log.d("  sucessfully get response  " + jsonObject);

                        Gson gson = new Gson();
                        googleSearchModel = gson.fromJson(
                                jsonObject.toString(), GoogleSearchModel.class);
                        // Log.d("  items size are "
                        // + googleSearchModel.getItems()[0].getLink());

                        responseListener.onResponseReceived(googleSearchModel);
                    }

                    @Override
                    public void onExceptionReceived(String exceptionMessage) {
                        responseListener.onExceptionReceived(exceptionMessage);
                    }
                });

        return true;
    }

}
