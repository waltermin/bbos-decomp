package net.rim.device.api.crypto.tls;

import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateFactory;
import net.rim.device.api.crypto.certificate.CertificateParsingException;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.Persistable;

class SessionInformation implements Persistable {
   private Object _sessionIDEncoding;
   private Object _masterSecretEncoding;
   private int _cipherSuite;
   private String _certificateType;
   private String[] _certificatePoolTypes;
   private byte[] _certificateEncoding;
   private byte[][] _certificatePoolEncodings;
   private int _hashCode;

   public SessionInformation(byte[] sessionID, byte[] masterSecret, int cipherSuite, Certificate cert, Certificate[] certificatePool) {
      this._sessionIDEncoding = PersistentContent.encode(sessionID);
      this._masterSecretEncoding = PersistentContent.encode(masterSecret);
      this._cipherSuite = cipherSuite;
      if (cert != null) {
         this._certificateType = cert.getType();
         this._certificateEncoding = cert.getEncoding();
      }

      if (certificatePool != null) {
         int certificatePoolLength = certificatePool.length;
         this._certificatePoolTypes = new String[certificatePoolLength];
         this._certificatePoolEncodings = new byte[certificatePoolLength][];

         for (int i = 0; i < certificatePoolLength; i++) {
            this._certificatePoolTypes[i] = certificatePool[i].getType();
            this._certificatePoolEncodings[i] = certificatePool[i].getEncoding();
         }
      }

      int _hashCode = 0;
      CRC32.update(_hashCode, sessionID);
      CRC32.update(_hashCode, HashCodeCalculator.getDigest32(masterSecret));
      CRC32.update(_hashCode, cipherSuite);
   }

   public byte[] getSessionID() {
      return PersistentContent.decodeByteArray(this._sessionIDEncoding);
   }

   public byte[] getMasterSecret() {
      return PersistentContent.decodeByteArray(this._masterSecretEncoding);
   }

   public int getCipherSuite() {
      return this._cipherSuite;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public Certificate getCertificate() {
      if (this._certificateType == null) {
         return null;
      }

      CertificateParsingException e;
      try {
         try {
            return CertificateFactory.getInstance(this._certificateType, this._certificateEncoding);
         } catch (CertificateParsingException var4) {
            e = var4;
         }
      } catch (Throwable var5) {
         throw new RuntimeException(e.toString());
      }

      throw new RuntimeException(e.toString());
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public Certificate[] getCertificatePool() {
      if (this._certificatePoolTypes != null && this._certificatePoolEncodings != null) {
         CertificateParsingException e;
         try {
            try {
               int certificatePoolLength = this._certificatePoolTypes.length;
               Certificate[] certificatePool = new Certificate[certificatePoolLength];

               for (int i = 0; i < certificatePoolLength; i++) {
                  certificatePool[i] = CertificateFactory.getInstance(this._certificatePoolTypes[i], this._certificatePoolEncodings[i]);
               }

               return certificatePool;
            } catch (CertificateParsingException var6) {
               e = var6;
            }
         } catch (Throwable var7) {
            throw new RuntimeException(e.toString());
         }

         throw new RuntimeException(e.toString());
      } else {
         return null;
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof SessionInformation) {
         SessionInformation info = (SessionInformation)obj;
         if (this._hashCode == info._hashCode
            && Arrays.equals(info.getSessionID(), this.getSessionID())
            && Arrays.equals(info.getMasterSecret(), this.getMasterSecret())
            && info.getCipherSuite() == this.getCipherSuite()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int hashCode() {
      return this._hashCode;
   }
}
