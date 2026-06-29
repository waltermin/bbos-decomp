package net.rim.device.api.crypto.certificate.status;

import net.rim.device.internal.ui.component.BackgroundDialog;

class CertificateStatusQuery$ProviderUiContextImpl implements ProviderUiContext {
   private String _message;
   private final CertificateStatusQuery this$0;

   public String getErrorMessage() {
      return this._message;
   }

   @Override
   public void setErrorMessage(String message) {
      this._message = message;
   }

   @Override
   public int promptUser(String title, String message, String[] buttonText, int[] returnCodes) {
      if (message != null && buttonText != null && returnCodes != null && buttonText.length != 0 && buttonText.length == returnCodes.length) {
         synchronized (this.this$0._lock) {
            if (this.this$0._thread == null) {
               return -1;
            }
         }

         CertificateStatusQuery$ProviderUiDialog dialog = new CertificateStatusQuery$ProviderUiDialog(title, message, buttonText, returnCodes, 134217728);
         BackgroundDialog.show(dialog);
         return dialog.getCloseReason();
      } else {
         throw new IllegalArgumentException();
      }
   }

   private CertificateStatusQuery$ProviderUiContextImpl(CertificateStatusQuery _1) {
      this.this$0 = _1;
   }

   CertificateStatusQuery$ProviderUiContextImpl(CertificateStatusQuery x0, CertificateStatusQuery$1 x1) {
      this(x0);
   }
}
