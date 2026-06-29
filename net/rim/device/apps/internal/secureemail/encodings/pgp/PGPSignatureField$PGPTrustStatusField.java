package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField$ThrowableHandlerData;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField$TrustStatusField;

public class PGPSignatureField$PGPTrustStatusField extends SecureEmailSignatureField$TrustStatusField {
   private final PGPSignatureField this$0;

   public PGPSignatureField$PGPTrustStatusField(PGPSignatureField _1, Application app, int initialStatus, String details) {
      super(_1, app, initialStatus, details);
      this.this$0 = _1;
   }

   @Override
   public void handleThrowable(Throwable t, SecureEmailSignatureField$ThrowableHandlerData throwableHandlerData) {
      if (t instanceof Object) {
         throwableHandlerData._status = 6;
      } else {
         super.handleThrowable(t, throwableHandlerData);
      }
   }
}
