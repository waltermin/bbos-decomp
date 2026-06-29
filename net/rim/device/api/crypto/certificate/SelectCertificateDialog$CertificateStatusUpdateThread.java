package net.rim.device.api.crypto.certificate;

import net.rim.device.api.system.Application;
import net.rim.device.internal.resource.crypto.CryptoIndicatorImages;
import net.rim.device.internal.ui.Image;

class SelectCertificateDialog$CertificateStatusUpdateThread extends Thread {
   private boolean _stopThread;
   private final SelectCertificateDialog this$0;

   private SelectCertificateDialog$CertificateStatusUpdateThread(SelectCertificateDialog _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.setPriority(1);
      Object appEventLock = Application.getApplication().getAppEventLock();
      int numCertificates = this.this$0._certificates.length;

      for (int i = 0; i < numCertificates; i++) {
         int focusIndex = this.this$0.fieldWithFocusToIndex();
         if (focusIndex != -1 && !this.this$0._imagesLoaded[focusIndex]) {
            Image image = CryptoIndicatorImages.getImage(
               CertificateIconProvider.getCertificateIcon(
                  this.this$0._certificates[focusIndex], this.this$0._keyStore, this.this$0._trustedKeyStore, this.this$0._cryptoSystemProperties
               )
            );
            synchronized (appEventLock) {
               this.this$0._iconImageFields[focusIndex].setImage(image);
            }

            this.this$0._imagesLoaded[focusIndex] = true;
         }

         if (!this.this$0._imagesLoaded[i]) {
            Image image = CryptoIndicatorImages.getImage(
               CertificateIconProvider.getCertificateIcon(
                  this.this$0._certificates[i], this.this$0._keyStore, this.this$0._trustedKeyStore, this.this$0._cryptoSystemProperties
               )
            );
            synchronized (appEventLock) {
               this.this$0._iconImageFields[i].setImage(image);
            }

            this.this$0._imagesLoaded[i] = true;
         }

         if (this._stopThread) {
            return;
         }
      }
   }

   public void stopThread() {
      this._stopThread = true;
   }

   SelectCertificateDialog$CertificateStatusUpdateThread(SelectCertificateDialog x0, SelectCertificateDialog$1 x1) {
      this(x0);
   }
}
