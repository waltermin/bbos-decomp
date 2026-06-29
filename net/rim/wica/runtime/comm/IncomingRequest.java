package net.rim.wica.runtime.comm;

import net.rim.device.api.io.http.HttpHeaders;

public interface IncomingRequest {
   String getHeader(String var1);

   HttpHeaders getHeaders();

   byte[] getData();
}
