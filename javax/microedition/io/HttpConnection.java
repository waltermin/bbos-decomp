package javax.microedition.io;

public interface HttpConnection extends ContentConnection {
   String HEAD;
   String GET;
   String POST;
   int HTTP_OK;
   int HTTP_CREATED;
   int HTTP_ACCEPTED;
   int HTTP_NOT_AUTHORITATIVE;
   int HTTP_NO_CONTENT;
   int HTTP_RESET;
   int HTTP_PARTIAL;
   int HTTP_MULT_CHOICE;
   int HTTP_MOVED_PERM;
   int HTTP_MOVED_TEMP;
   int HTTP_SEE_OTHER;
   int HTTP_NOT_MODIFIED;
   int HTTP_USE_PROXY;
   int HTTP_TEMP_REDIRECT;
   int HTTP_BAD_REQUEST;
   int HTTP_UNAUTHORIZED;
   int HTTP_PAYMENT_REQUIRED;
   int HTTP_FORBIDDEN;
   int HTTP_NOT_FOUND;
   int HTTP_BAD_METHOD;
   int HTTP_NOT_ACCEPTABLE;
   int HTTP_PROXY_AUTH;
   int HTTP_CLIENT_TIMEOUT;
   int HTTP_CONFLICT;
   int HTTP_GONE;
   int HTTP_LENGTH_REQUIRED;
   int HTTP_PRECON_FAILED;
   int HTTP_ENTITY_TOO_LARGE;
   int HTTP_REQ_TOO_LONG;
   int HTTP_UNSUPPORTED_TYPE;
   int HTTP_UNSUPPORTED_RANGE;
   int HTTP_EXPECT_FAILED;
   int HTTP_INTERNAL_ERROR;
   int HTTP_NOT_IMPLEMENTED;
   int HTTP_BAD_GATEWAY;
   int HTTP_UNAVAILABLE;
   int HTTP_GATEWAY_TIMEOUT;
   int HTTP_VERSION;

   String getURL();

   String getProtocol();

   String getHost();

   String getFile();

   String getRef();

   String getQuery();

   int getPort();

   String getRequestMethod();

   void setRequestMethod(String var1);

   String getRequestProperty(String var1);

   void setRequestProperty(String var1, String var2);

   int getResponseCode();

   String getResponseMessage();

   long getExpiration();

   long getDate();

   long getLastModified();

   String getHeaderField(String var1);

   int getHeaderFieldInt(String var1, int var2);

   long getHeaderFieldDate(String var1, long var2);

   String getHeaderField(int var1);

   String getHeaderFieldKey(int var1);
}
