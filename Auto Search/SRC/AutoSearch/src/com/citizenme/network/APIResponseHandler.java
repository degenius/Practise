package com.citizenme.network;


/** Interface with the callback delegates to read HttpRequest response */
public interface APIResponseHandler<T> {

    /** Called when a successful HttpResponse is recieved */
    public void onResponseReceived(T response);

    /** Called when an exception occurs while making a HttpRequest */
    public void onExceptionReceived(String exceptionMessage);

}
