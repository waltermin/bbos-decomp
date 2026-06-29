package net.rim.wica.runtime.comm;

import net.rim.device.api.io.http.HttpHeaders;

public interface Response {
   int REQUEST_EXPIRED_RESPONSE_CODE = 600;
   int UNKNOWN_ERROR_RESPONSE_CODE = 601;
   int REQUEST_CANCELED_RESPONSE_CODE = 602;
   int REQUEST_FAILED_LOST_COVERAGE_RESPONSE_CODE = 603;
   int RESPONSE_TOO_LARGE_RESPONSE_CODE = 603;
   int SERVER_UNREACHABLE_RESPONSE_CODE = 604;

   String getResponseUri();

   int getResponseCode();

   String getContentType();

   String getHeader(String var1);

   HttpHeaders getHeaders();

   boolean hasData();

   byte[] getData();

   boolean isSuccessful();
}
