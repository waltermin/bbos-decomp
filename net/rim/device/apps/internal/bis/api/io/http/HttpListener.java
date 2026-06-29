package net.rim.device.apps.internal.bis.api.io.http;

public interface HttpListener {
   int CLOSED_NORMAL;
   int CLOSED_ERROR;
   int CLOSED_TIMEOUT;
   int RESPONSE_SIZE_UNKNOWN;

   void connectionEstablished(boolean var1);

   void connectionClosed(int var1);

   void sentRequest();

   void readingResponse(int var1);

   void readResponseProgressUpdate(int var1);

   void finishedReadingResponse();
}
