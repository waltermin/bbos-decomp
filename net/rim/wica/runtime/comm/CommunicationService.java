package net.rim.wica.runtime.comm;

import net.rim.device.cldc.io.utility.URL;
import net.rim.wica.runtime.util.ProgressMonitor;

public interface CommunicationService {
   void registerIncomingRequestListener(IncomingRequestListener var1);

   OutgoingRequest createOutgoingRequestInstance(String var1);

   OutgoingRequest createOutgoingRequestInstance(URL var1);

   void sendRequest(OutgoingRequest var1);

   void sendRequest(OutgoingRequest var1, ProgressMonitor var2);

   void resendDeferredRequests();

   boolean isInCoverage();

   boolean isDestinationServerPolled(URL var1);
}
