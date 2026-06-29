package net.rim.wica.runtime.comm.internal;

import net.rim.wica.runtime.comm.OutgoingRequest;
import net.rim.wica.runtime.comm.Response;
import net.rim.wica.runtime.comm.ResponseListener;

class OutgoingRequestImpl$NoResponseListener implements ResponseListener {
   private OutgoingRequestImpl$NoResponseListener() {
   }

   @Override
   public void processResponse(Response response, OutgoingRequest request) {
   }

   OutgoingRequestImpl$NoResponseListener(OutgoingRequestImpl$1 x0) {
      this();
   }
}
