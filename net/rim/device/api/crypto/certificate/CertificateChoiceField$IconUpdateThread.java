package net.rim.device.api.crypto.certificate;

class CertificateChoiceField$IconUpdateThread extends Thread {
   private Certificate[] _threadCertificates;
   private int[] _threadIconValues;
   private int _threadEpoch;
   private int _threadStartIndex;
   private final CertificateChoiceField this$0;

   CertificateChoiceField$IconUpdateThread(CertificateChoiceField _1, Certificate[] certificates, int[] iconValues, int threadStartIndex, int epoch) {
      this.this$0 = _1;
      this._threadCertificates = certificates;
      this._threadIconValues = iconValues;
      this._threadStartIndex = threadStartIndex;
      this._threadEpoch = epoch;
   }

   @Override
   public void run() {
      this.setPriority(1);
      int length = this._threadCertificates != null ? this._threadCertificates.length : 0;
      int updateIndex = this._threadStartIndex;

      for (int i = 0; i < length; i++) {
         if (this._threadEpoch != this.this$0._epoch) {
            return;
         }

         Certificate currentCertificate = this._threadCertificates[updateIndex];
         if (currentCertificate != null) {
            this._threadIconValues[updateIndex] = CertificateIconProvider.getCertificateIcon(
               currentCertificate, this.this$0._keyStore, this.this$0._trustedKeyStore, this.this$0._cryptoSystemProperties
            );
            this.this$0.invalidate();
         }

         if (++updateIndex >= length) {
            updateIndex -= length;
         }
      }
   }
}
