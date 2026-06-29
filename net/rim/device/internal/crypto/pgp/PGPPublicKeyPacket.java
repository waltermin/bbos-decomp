package net.rim.device.internal.crypto.pgp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DSACryptoSystem;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSACryptoSystem;
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
   public PGPPublicKeyPacket(int tag, byte[] encoding) {
      super(tag, encoding);
      int offset = 0;
      this._version = encoding[offset++];
      this._creationTime = PGPUtilities.convertTime(encoding, offset);
      offset += 4;
      this._expirationTime = Long.MAX_VALUE;
      switch (this._version) {
         case 2:
            throw new Object(((StringBuffer)(new Object("Ver:"))).append(this._version).toString());
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
               ByteArrayInputStream var39 = new Object(encoding, offset, encoding.length - offset);
               this._n = PGPUtilities.readMPI((InputStream)var39);
               this._e = PGPUtilities.readMPI((InputStream)var39);
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
                     ByteArrayInputStream var38 = new Object(encoding, offset, encoding.length - offset);
                     this._n = PGPUtilities.readMPI((InputStream)var38);
                     this._e = PGPUtilities.readMPI((InputStream)var38);
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
                     ByteArrayInputStream var37 = new Object(encoding, offset, encoding.length - offset);
                     this._p = PGPUtilities.readMPI((InputStream)var37);
                     this._g = PGPUtilities.readMPI((InputStream)var37);
                     this._y = PGPUtilities.readMPI((InputStream)var37);
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
                     ByteArrayInputStream e = new Object(encoding, offset, encoding.length - offset);
                     this._p = PGPUtilities.readMPI((InputStream)e);
                     this._q = PGPUtilities.readMPI((InputStream)e);
                     this._g = PGPUtilities.readMPI((InputStream)e);
                     this._y = PGPUtilities.readMPI((InputStream)e);
                     offset += this._p.length + this._q.length + this._g.length + this._y.length + 8;
                     var15 = false;
                     break;
                  } finally {
                     if (var15) {
                        throw new PGPEncodingException("RMIO");
                     }
                  }
               default:
                  throw new Object(((StringBuffer)(new Object("Pub:"))).append(this._publicKeyAlgorithm).toString());
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
   public PublicKey getPublicKey() {
      try {
         switch (this._publicKeyAlgorithm) {
            case 1:
            case 2:
            case 3:
               RSACryptoSystem rsaCryptoSystem = (RSACryptoSystem)(new Object(this._n.length * 8));
               return (PublicKey)(new Object(rsaCryptoSystem, this._e, this._n));
            case 16:
            case 20:
               DHCryptoSystem dhCryptoSystem = (DHCryptoSystem)(new Object(this._p, this._g));
               return (PublicKey)(new Object(dhCryptoSystem, this._y));
            case 17:
               DSACryptoSystem dsaCryptoSystem = (DSACryptoSystem)(new Object(this._p, this._q, this._g));
               return (PublicKey)(new Object(dsaCryptoSystem, this._y));
            default:
               throw new Object(((StringBuffer)(new Object("Pub:"))).append(this._publicKeyAlgorithm).toString());
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

   private void calculateFingerprint() {
      switch (this._version) {
         case 2:
            throw new Object(((StringBuffer)(new Object("Ver:"))).append(this._version).toString());
         case 3:
         default:
            MD5Digest md5Digest = (MD5Digest)(new Object());
            md5Digest.update(this._n);
            md5Digest.update(this._e);
            this._fingerprint = md5Digest.getDigest();
            return;
         case 4:
            SHA1Digest sha1Digest = (SHA1Digest)(new Object());
            sha1Digest.update(153);
            int thisKeyLength = this.getPublicKeyLength();
            sha1Digest.update(thisKeyLength >> 8 & 0xFF);
            sha1Digest.update(thisKeyLength & 0xFF);
            sha1Digest.update(this.getEncoding(), 0, thisKeyLength);
            this._fingerprint = sha1Digest.getDigest();
      }
   }

   private void calculateKeyID() {
      switch (this._version) {
         case 2:
            throw new Object(((StringBuffer)(new Object("Ver:"))).append(this._version).toString());
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
