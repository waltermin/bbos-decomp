package net.rim.device.internal.crypto.pgp;

import java.io.ByteArrayInputStream;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DHPublicKey;
import net.rim.device.api.crypto.DSACryptoSystem;
import net.rim.device.api.crypto.DSAPublicKey;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSAPublicKey;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.pgp.PGPEncodingException;
import net.rim.device.api.util.Persistable;

public class PGPPublicKeyPacket extends PGPPacket implements Persistable {
   private byte _version;
   private long _creationTime;
   private long _expirationTime;
   private byte _publicKeyAlgorithm;
   private byte[] _n;
   private byte[] _e;
   private byte[] _p;
   private byte[] _q;
   private byte[] _g;
   private byte[] _y;
   private byte[] _keyid;
   private byte[] _fingerprint;
   private int _publicKeyLength;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public PGPPublicKeyPacket(int tag, byte[] encoding) throws CryptoUnsupportedOperationException, PGPEncodingException {
      super(tag, encoding);
      int offset = 0;
      this._version = encoding[offset++];
      this._creationTime = PGPUtilities.convertTime(encoding, offset);
      offset += 4;
      this._expirationTime = Long.MAX_VALUE;
      switch (this._version) {
         case 2:
            throw new CryptoUnsupportedOperationException("Ver:" + this._version);
         case 3:
         default:
            int expirationDays = (encoding[offset++] & 255) << 8 | encoding[offset++] & 255;
            if (expirationDays > 0) {
               this._expirationTime = this._creationTime + expirationDays * 24 * 60 * 60 * 1000;
            }

            this._publicKeyAlgorithm = encoding[offset++];
            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               ByteArrayInputStream var39 = new ByteArrayInputStream(encoding, offset, encoding.length - offset);
               this._n = PGPUtilities.readMPI(var39);
               this._e = PGPUtilities.readMPI(var39);
               offset += this._n.length + this._e.length + 4;
               var10 = false;
               break;
            } finally {
               if (var10) {
                  throw new PGPEncodingException("RMIO");
               }
            }
         case 4:
            this._publicKeyAlgorithm = encoding[offset++];
            switch (this._publicKeyAlgorithm) {
               case 1:
               case 2:
               case 3:
                  boolean var25 = false /* VF: Semaphore variable */;

                  try {
                     var25 = true;
                     ByteArrayInputStream var38 = new ByteArrayInputStream(encoding, offset, encoding.length - offset);
                     this._n = PGPUtilities.readMPI(var38);
                     this._e = PGPUtilities.readMPI(var38);
                     offset += this._n.length + this._e.length + 4;
                     var25 = false;
                     break;
                  } finally {
                     if (var25) {
                        throw new PGPEncodingException("RMIO");
                     }
                  }
               case 16:
               case 20:
                  boolean var20 = false /* VF: Semaphore variable */;

                  try {
                     var20 = true;
                     ByteArrayInputStream var37 = new ByteArrayInputStream(encoding, offset, encoding.length - offset);
                     this._p = PGPUtilities.readMPI(var37);
                     this._g = PGPUtilities.readMPI(var37);
                     this._y = PGPUtilities.readMPI(var37);
                     offset += this._p.length + this._g.length + this._y.length + 6;
                     var20 = false;
                     break;
                  } finally {
                     if (var20) {
                        throw new PGPEncodingException("RMIO");
                     }
                  }
               case 17:
                  boolean var15 = false /* VF: Semaphore variable */;

                  try {
                     var15 = true;
                     ByteArrayInputStream e = new ByteArrayInputStream(encoding, offset, encoding.length - offset);
                     this._p = PGPUtilities.readMPI(e);
                     this._q = PGPUtilities.readMPI(e);
                     this._g = PGPUtilities.readMPI(e);
                     this._y = PGPUtilities.readMPI(e);
                     offset += this._p.length + this._q.length + this._g.length + this._y.length + 8;
                     var15 = false;
                     break;
                  } finally {
                     if (var15) {
                        throw new PGPEncodingException("RMIO");
                     }
                  }
               default:
                  throw new CryptoUnsupportedOperationException("Pub:" + this._publicKeyAlgorithm);
            }
      }

      this._publicKeyLength = offset;
      this.calculateFingerprint();
      this.calculateKeyID();
   }

   public long getCreationTime() {
      return this._creationTime;
   }

   public int getPublicKeyAlgorithm() {
      return this._publicKeyAlgorithm;
   }

   public int getVersion() {
      return this._version;
   }

   public long getExpirationTime() {
      return this._expirationTime;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public PublicKey getPublicKey() throws PGPEncodingException {
      try {
         switch (this._publicKeyAlgorithm) {
            case 1:
            case 2:
            case 3:
               RSACryptoSystem rsaCryptoSystem = new RSACryptoSystem(this._n.length * 8);
               return new RSAPublicKey(rsaCryptoSystem, this._e, this._n);
            case 16:
            case 20:
               DHCryptoSystem dhCryptoSystem = new DHCryptoSystem(this._p, this._g);
               return new DHPublicKey(dhCryptoSystem, this._y);
            case 17:
               DSACryptoSystem dsaCryptoSystem = new DSACryptoSystem(this._p, this._q, this._g);
               return new DSAPublicKey(dsaCryptoSystem, this._y);
            default:
               throw new CryptoUnsupportedOperationException("Pub:" + this._publicKeyAlgorithm);
         }
      } catch (Throwable var5) {
         throw new PGPEncodingException(e.toString());
      }
   }

   public byte[] getKeyID() {
      return this._keyid;
   }

   public byte[] getFingerprint() {
      return this._fingerprint;
   }

   private void calculateFingerprint() throws CryptoUnsupportedOperationException {
      switch (this._version) {
         case 2:
            throw new CryptoUnsupportedOperationException("Ver:" + this._version);
         case 3:
         default:
            MD5Digest md5Digest = new MD5Digest();
            md5Digest.update(this._n);
            md5Digest.update(this._e);
            this._fingerprint = md5Digest.getDigest();
            return;
         case 4:
            SHA1Digest sha1Digest = new SHA1Digest();
            sha1Digest.update(153);
            int thisKeyLength = this.getPublicKeyLength();
            sha1Digest.update(thisKeyLength >> 8 & 0xFF);
            sha1Digest.update(thisKeyLength & 0xFF);
            sha1Digest.update(this.getEncoding(), 0, thisKeyLength);
            this._fingerprint = sha1Digest.getDigest();
      }
   }

   private void calculateKeyID() throws CryptoUnsupportedOperationException {
      switch (this._version) {
         case 2:
            throw new CryptoUnsupportedOperationException("Ver:" + this._version);
         case 3:
         default:
            this._keyid = new byte[8];
            System.arraycopy(this._n, this._n.length - 8, this._keyid, 0, 8);
            return;
         case 4:
            this._keyid = new byte[8];
            byte[] fingerprint = this.getFingerprint();
            System.arraycopy(fingerprint, fingerprint.length - 8, this._keyid, 0, 8);
      }
   }

   public int getPublicKeyLength() {
      return this._publicKeyLength;
   }
}
