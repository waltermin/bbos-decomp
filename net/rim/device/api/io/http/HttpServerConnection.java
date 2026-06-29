package net.rim.device.api.io.http;

import javax.microedition.io.ContentConnection;

public interface HttpServerConnection extends ContentConnection {
   String getRequestMethod();

   String getVersion();

   String getRequestURI();

   String getHeaderField(int var1);

   String getHeaderField(String var1);

   int getHeaderFieldInt(String var1, int var2);

   String getHeaderFieldKey(int var1);

   String getResponseProperty(String var1);

   void setResponseProperty(String var1, String var2);

   void setResponseCode(int var1);
}
