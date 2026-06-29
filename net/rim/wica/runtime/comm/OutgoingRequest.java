package net.rim.wica.runtime.comm;

import net.rim.device.api.io.http.HttpHeaders;

public interface OutgoingRequest {
   String GET;
   String POST;

   String getUrl();

   void setHeader(String var1, String var2);

   void setHeaders(HttpHeaders var1);

   void setData(byte[] var1);

   void setCustomData(Object var1);

   void setRequestor(Requestor var1);

   Object getCustomData();

   void setRequestMethod(String var1);

   void setMaxAttempts(int var1);

   void setResponseListener(ResponseListener var1);

   int getAttemptCount();

   boolean hasExpired();

   void cancel();
}
