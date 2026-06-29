package net.rim.device.api.smartcard;

import net.rim.device.api.crypto.certificate.CertificateChoiceField;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

class SmartCardUserAuthenticator$ConfigureWorkerThread extends PleaseWaitWorkerThread {
   private int _closeReason;
   private boolean _configureComplete;
   private CertificateChoiceField _field;
   private final SmartCardUserAuthenticator this$0;

   SmartCardUserAuthenticator$ConfigureWorkerThread(SmartCardUserAuthenticator _1, CertificateChoiceField field, int closeReason) {
      this.this$0 = _1;
      this._field = field;
      this._closeReason = closeReason;
   }

   @Override
   protected void doWork() {
      switch (this._closeReason) {
         case 0:
            if (this._field.getSelectedCertificate() == null) {
               BackgroundDialog.showMessage(SmartCardUserAuthenticator._rb.getString(37), -2147483644);
               return;
            } else {
               this._configureComplete = this.this$0.grabDataFromField(this._field, null);
            }
         case -1:
            return;
         case 1:
         default:
            this.this$0.importCertificatesFromSmartCard(null);
      }
   }

   public boolean getConfigureComplete() {
      return this._configureComplete;
   }
}
