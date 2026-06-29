package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField$SignatureStatusField;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField$ThrowableHandlerData;

public class SMIMESignatureField$SMIMESignatureStatusField extends SecureEmailSignatureField$SignatureStatusField {
   private final SMIMESignatureField this$0;

   public SMIMESignatureField$SMIMESignatureStatusField(SMIMESignatureField _1, Application app, int initialStatus, String signerName, String details) {
      super(_1, app, initialStatus, signerName, details);
      this.this$0 = _1;
   }

   @Override
   public void handleThrowable(Throwable t, SecureEmailSignatureField$ThrowableHandlerData throwableHandlerData) {
      if (t instanceof Object) {
         throwableHandlerData._status = 2;
         throwableHandlerData._details = SMIMEResources.getString(2108);
         throwableHandlerData._unrecoverableThrowable = true;
      } else if (t instanceof Object) {
         throwableHandlerData._status = 3;
      } else if (t instanceof Object) {
         throwableHandlerData._status = 2;
         throwableHandlerData._details = SMIMEResources.getString(10002);
         throwableHandlerData._unrecoverableThrowable = true;
      } else if (t instanceof Object) {
         throwableHandlerData._status = 2;
         throwableHandlerData._details = SMIMEResources.getString(2106);
         throwableHandlerData._unrecoverableThrowable = true;
      } else {
         super.handleThrowable(t, throwableHandlerData);
      }
   }
}
