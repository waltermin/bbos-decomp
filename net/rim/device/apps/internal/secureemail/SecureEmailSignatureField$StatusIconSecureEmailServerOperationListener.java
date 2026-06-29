package net.rim.device.apps.internal.secureemail;

import javax.microedition.io.Connection;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerOperationListener;

class SecureEmailSignatureField$StatusIconSecureEmailServerOperationListener implements SecureEmailServerOperationListener {
   private final SecureEmailSignatureField this$0;

   private SecureEmailSignatureField$StatusIconSecureEmailServerOperationListener(SecureEmailSignatureField _1) {
      this.this$0 = _1;
   }

   @Override
   public void updateServerOperationProgress(String currentProgress) {
   }

   @Override
   public void setServerConnection(Connection serverConnection) {
      this.this$0._signatureStatus.setStatus(3);
      this.this$0._signatureStatus.updateStatus();
      this.this$0._trustStatus.setStatus(7);
      this.this$0._trustStatus.updateStatus();
   }

   @Override
   public void clearServerConnection() {
   }

   @Override
   public boolean wasServerConnectionAborted() {
      return false;
   }

   SecureEmailSignatureField$StatusIconSecureEmailServerOperationListener(SecureEmailSignatureField x0, SecureEmailSignatureField$1 x1) {
      this(x0);
   }
}
