package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField$ThrowableHandlerData;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField$TrustStatusField;

public class SMIMESignatureField$SMIMETrustStatusField extends SecureEmailSignatureField$TrustStatusField {
   private final SMIMESignatureField this$0;

   public SMIMESignatureField$SMIMETrustStatusField(SMIMESignatureField _1, Application app, int initialStatus, String details) {
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
