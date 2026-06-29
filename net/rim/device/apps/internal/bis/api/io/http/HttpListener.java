package net.rim.device.apps.internal.bis.api.io.http;

public interface HttpListener {
   int CLOSED_NORMAL = 0;
   int CLOSED_ERROR = 1;
   int CLOSED_TIMEOUT = 2;
   int RESPONSE_SIZE_UNKNOWN = -1;

   void connectionEstablished(boolean var1);

   void connectionClosed(int var1);

   void sentRequest();

   void readingResponse(int var1);

   void readResponseProgressUpdate(int var1);

   void finishedReadingResponse();
}
