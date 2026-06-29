package net.rim.device.cldc.io.ssl;

import javax.microedition.pki.Certificate;

public class ProxyCertificate implements Certificate {
   private String _subject;
   private String _issuer;
   private String _notBeforeString;
   private String _notAfterString;
   private long _notBefore;
   private long _notAfter = Long.MAX_VALUE;
   private String _serialNumber;
   private String _version;

   public String getNotAfterString() {
      return this._notAfterString;
   }

   public String getNotBeforeString() {
      return this._notBeforeString;
   }

   @Override
   public String getType() {
      return "X.509";
   }

   @Override
   public String getVersion() {
      return this._version;
   }

   @Override
   public String getSigAlgName() {
      throw new RuntimeException();
   }

   @Override
   public long getNotBefore() {
      return this._notBefore;
   }

   @Override
   public String getIssuer() {
      return this._issuer;
   }

   @Override
   public long getNotAfter() {
      return this._notAfter;
   }

   @Override
   public String getSubject() {
      return this._subject;
   }

   @Override
   public String getSerialNumber() {
      return this._serialNumber;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public ProxyCertificate(String issuer, String subject, String notAfter, String notBefore, String serialNumber, String version) {
      if (issuer != null && subject != null && notAfter != null && notBefore != null && serialNumber != null && version != null) {
         this._issuer = issuer;
         this._subject = subject;
         boolean var13 = false /* VF: Semaphore variable */;

         label64:
         try {
            var13 = true;
            this._notAfter = Long.parseLong(notAfter);
            var13 = false;
         } finally {
            if (var13) {
               this._notAfterString = notAfter;
               break label64;
            }
         }

         boolean var10 = false /* VF: Semaphore variable */;

         label60:
         try {
            var10 = true;
            this._notBefore = Long.parseLong(notBefore);
            var10 = false;
         } finally {
            if (var10) {
               this._notBeforeString = notBefore;
               break label60;
            }
         }

         this._serialNumber = serialNumber;
         this._version = version;
      } else {
         throw new IllegalArgumentException();
      }
   }
}
