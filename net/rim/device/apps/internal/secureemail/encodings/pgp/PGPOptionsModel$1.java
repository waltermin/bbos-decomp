package net.rim.device.apps.internal.secureemail.encodings.pgp;

class PGPOptionsModel$1 implements Runnable {
   private final PGPOptionsModel this$0;

   PGPOptionsModel$1(PGPOptionsModel _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      PGPOptionsModel.access$300(
         this.this$0,
         this.this$0._defaultKeyPairField,
         2,
         this.this$0._defaultKeyStoreDataArray,
         this.this$0._defaultKeyStoreDataArray.length > 0 ? this.this$0._defaultKeyStoreDataArray[this.this$0._defaultKeyPairField.getSelectedIndex()] : null,
         this.this$0._pgpKeyStore,
         false
      );
   }
}
