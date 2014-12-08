package com.citizenme.network;

import org.apache.http.HttpResponse;

/** Interface with the callback delegates to read HttpRequest response */
public interface ResponseListener {
 
/** Called when a successful HttpResponse is recieved */
   public void onResponseReceived(HttpResponse response);

/** Called when an exception occurs while making a HttpRequest */
   public void onExceptionReceived(String exceptionMessage);
 
}


