package net.rim.device.cldc.io.ssl;

public class SSLConnectionOptions {
   private int _disallowUnmatchedDomainName;
   private int _disallowUntrustedCertificate;
   private int _disallowExpiredCertificate;
   private int _minimumStrongRSAKeySize;
   private int _minimumStrongDSAKeySize;
   private int _minimumStrongDHKeySize;
   private int _minimumStrongECKeySize;
   private String[] _acceptableDomainNames;

   public SSLConnectionOptions() {
      TLSOptionStore defaultOptions = TLSOptionStore.getOptions();
      if (defaultOptions.getPromptForDomainName()) {
         this._disallowUnmatchedDomainName = 2;
      } else {
         this._disallowUnmatchedDomainName = 1;
      }

      if (defaultOptions.getDisableUntrustedConnections()) {
         this._disallowUntrustedCertificate = 0;
      } else if (defaultOptions.getPromptForCertificateTrust()) {
         this._disallowUntrustedCertificate = 2;
      } else {
         this._disallowUntrustedCertificate = 1;
      }

      if (defaultOptions.getDisableInvalidConnections()) {
         this._disallowExpiredCertificate = 0;
      } else {
         this._disallowExpiredCertificate = 2;
      }

      this._minimumStrongRSAKeySize = defaultOptions.getMinimumStrongRSAKeySize();
      this._minimumStrongDSAKeySize = defaultOptions.getMinimumStrongDSAKeySize();
      this._minimumStrongDHKeySize = defaultOptions.getMinimumStrongDHKeySize();
      this._minimumStrongECKeySize = defaultOptions.getMinimumStrongECKeySize();
      this._acceptableDomainNames = null;
   }

   public void disallowUnmatchedDomainName(int trueFalsePrompt) {
      this.assertTrueFalsePrompt(trueFalsePrompt);
      this._disallowUnmatchedDomainName = trueFalsePrompt;
   }

   public void disallowUntrustedCertificate(int trueFalsePrompt) {
      this.assertTrueFalsePrompt(trueFalsePrompt);
      this._disallowUntrustedCertificate = trueFalsePrompt;
   }

   public void disallowExpiredCertificate(int trueFalsePrompt) {
      this.assertTrueFalsePrompt(trueFalsePrompt);
      this._disallowExpiredCertificate = trueFalsePrompt;
   }

   public int disallowUnmatchedDomainName() {
      return this._disallowUnmatchedDomainName;
   }

   public int disallowUntrustedCertificate() {
      return this._disallowUntrustedCertificate;
   }

   public int disallowExpiredCertificate() {
      return this._disallowExpiredCertificate;
   }

   private void assertTrueFalsePrompt(int trueFalsePrompt) {
      if (trueFalsePrompt != 0 && trueFalsePrompt != 1 && trueFalsePrompt != 2) {
         throw new IllegalArgumentException();
      }
   }

   public void setMinimumStrongRSAKeySize(int minimumKeySize) {
      this._minimumStrongRSAKeySize = minimumKeySize;
   }

   public void setMinimumStrongDSAKeySize(int minimumKeySize) {
      this._minimumStrongDSAKeySize = minimumKeySize;
   }

   public void setMinimumStrongDHKeySize(int minimumKeySize) {
      this._minimumStrongDHKeySize = minimumKeySize;
   }

   public void setMinimumStrongECKeySize(int minimumKeySize) {
      this._minimumStrongECKeySize = minimumKeySize;
   }

   public int getMinimumStrongRSAKeySize() {
      return this._minimumStrongRSAKeySize;
   }

   public int getMinimumStrongDSAKeySize() {
      return this._minimumStrongDSAKeySize;
   }

   public int getMinimumStrongDHKeySize() {
      return this._minimumStrongDHKeySize;
   }

   public int getMinimumStrongECKeySize() {
      return this._minimumStrongECKeySize;
   }

   public void setAcceptableDomainNames(String[] domainNames) {
      if (domainNames != null) {
         int numElements = domainNames.length;
         this._acceptableDomainNames = new String[numElements];
         System.arraycopy(domainNames, 0, this._acceptableDomainNames, 0, numElements);
      } else {
         this._acceptableDomainNames = null;
      }
   }

   public String[] getAcceptableDomainNames() {
      if (this._acceptableDomainNames != null) {
         int numElements = this._acceptableDomainNames.length;
         String[] copy = new String[numElements];
         System.arraycopy(this._acceptableDomainNames, 0, copy, 0, numElements);
         return copy;
      } else {
         return null;
      }
   }
}
