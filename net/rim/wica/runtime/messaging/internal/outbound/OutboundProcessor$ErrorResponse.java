package net.rim.wica.runtime.messaging.internal.outbound;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.wica.runtime.comm.Response;

final class OutboundProcessor$ErrorResponse implements Response {
   private OutboundProcessor$ErrorResponse() {
   }

   @Override
   public final int getResponseCode() {
      return 601;
   }

   @Override
   public final String getContentType() {
      return null;
   }

   @Override
   public final String getHeader(String name) {
      return null;
   }

   @Override
   public final HttpHeaders getHeaders() {
      return null;
   }

   @Override
   public final boolean hasData() {
      return false;
   }

   @Override
   public final byte[] getData() {
      return null;
   }

   @Override
   public final String getResponseUri() {
      return null;
   }

   @Override
   public final boolean isSuccessful() {
      return false;
   }

   OutboundProcessor$ErrorResponse(OutboundProcessor$1 x0) {
      this();
   }
}
