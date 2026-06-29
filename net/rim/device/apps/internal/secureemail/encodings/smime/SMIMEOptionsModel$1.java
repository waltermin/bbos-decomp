package net.rim.device.apps.internal.secureemail.encodings.smime;

class SMIMEOptionsModel$1 implements Runnable {
   private final SMIMEOptionsModel this$0;

   SMIMEOptionsModel$1(SMIMEOptionsModel _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      SMIMEOptionsModel.access$300(
         this.this$0,
         this.this$0._signingCertField,
         0,
         this.this$0._signingKeyStoreDataArray,
         this.this$0._signingKeyStoreDataArray.length > 0 ? this.this$0._signingKeyStoreDataArray[this.this$0._signingCertField.getSelectedIndex()] : null,
         this.this$0._deviceKeyStore,
         false
      );
      SMIMEOptionsModel.access$600(
         this.this$0,
         this.this$0._encryptionCertField,
         1,
         this.this$0._encryptionKeyStoreDataArray,
         this.this$0._encryptionKeyStoreDataArray.length > 0
            ? this.this$0._encryptionKeyStoreDataArray[this.this$0._encryptionCertField.getSelectedIndex()]
            : null,
         this.this$0._deviceKeyStore,
         false
      );
   }
}
