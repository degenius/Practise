package com.citizenme.network;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.citizenme.autosearch.CitizenMeApplication;
import com.citizenme.autosearch.Log;

public class AsyncClient {
    public static final int timeoutConnection = 10000;

    public static void getRequest(String url, ResponseListener listener) {

        if (!isNetworkAvailable()) {
            listener.onExceptionReceived("Network is not available");
            return;
        }

        String sendUrl = url.replaceAll(" ", "%20");

        try {
            Log.d(" inside get request and requesting URL is " + url);

            HttpGet httpGet = new HttpGet(sendUrl);
            HttpParams httpParameters = new BasicHttpParams();

            // Set the timeout in milliseconds until a connection is
            // established.
            // The default value is zero, that means the timeout is not used.
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 10000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Accept", "application/json");

            // Execute HTTP Get Request
            HttpResponse response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
                    || response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                // Our request to the server was successful
                Log.d("inside getrequest and responose is  " + response);

                listener.onResponseReceived(response);
            } else {

                Log.d("inside getrequest and non 200 responose is  " + response);

                JSONObject jsonObject = ConnectionManager.parseJson(response);

                Log.d("inside getrequest and non 200 JSON responose is  "
                        + jsonObject);

                listener.onExceptionReceived(response.getStatusLine()
                        .getStatusCode()
                        + "-"
                        + response.getStatusLine().getReasonPhrase());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Got Exception in http request " + e.getMessage());
            listener.onExceptionReceived("Got Exception in http request" + "\n"
                    + e.getMessage());
            e.printStackTrace();

        }
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) CitizenMeApplication
                .getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        Log.v("network not available!");
        return false;
    }

}