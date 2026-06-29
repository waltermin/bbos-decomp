package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField$SignatureStatusField;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField$ThrowableHandlerData;

public class PGPSignatureField$PGPSignatureStatusField extends SecureEmailSignatureField$SignatureStatusField {
   private final PGPSignatureField this$0;

   public PGPSignatureField$PGPSignatureStatusField(PGPSignatureField _1, Application app, int initialStatus, String signerName, String details) {
      super(_1, app, initialStatus, signerName, details);
      this.this$0 = _1;
   }

   @Override
   public void handleThrowable(Throwable t, SecureEmailSignatureField$ThrowableHandlerData throwableHandlerData) {
      if (t instanceof Object) {
         throwableHandlerData._status = 2;
         throwableHandlerData._details = PGPResources.getString(8040);
      } else if (t instanceof Object) {
         throwableHandlerData._status = 3;
      } else {
         super.handleThrowable(t, throwableHandlerData);
      }
   }
}
