package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.crypto.certificate.Certificate;

class KeyStoreBrowserOptionsItem$ElementUpdatedRunnable implements Runnable {
   private Certificate _certificate;
   private final KeyStoreBrowserOptionsItem this$0;

   KeyStoreBrowserOptionsItem$ElementUpdatedRunnable(KeyStoreBrowserOptionsItem _1, Certificate certificate) {
      this.this$0 = _1;
      this._certificate = certificate;
   }

   @Override
   public void run() {
      synchronized (this.this$0._keyStoreItemsSyncObject) {
         int count = this.this$0._keyStoreItems.size();

         for (int i = 0; i < count; i++) {
            KeyStoreBrowserData data = (KeyStoreBrowserData)this.this$0._keyStoreItems.elementAt(i);
            if (data.getCertificate() == this._certificate) {
               if (!this._certificate.isRoot() && !this._certificate.isCA()) {
                  data.invalidate();
                  data.loadDataIfNeeded();
                  int displayedIndex = this.this$0._displayedIndices[i];
                  if (displayedIndex != -1) {
                     this.this$0._listField.invalidate(displayedIndex);
                  }
               } else {
                  this.this$0.loadCertificates(2);
               }
               break;
            }
         }
      }
   }
}
