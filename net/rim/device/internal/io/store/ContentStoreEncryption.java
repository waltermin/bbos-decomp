package net.rim.device.internal.io.store;

import java.io.InputStream;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.crypto.EncryptionUtilities;
import net.rim.device.internal.system.DRMServices;
import net.rim.vm.Array;

public final class ContentStoreEncryption {
   private static final byte[] EMPTY = new byte[0];
   private static final int DRM_KEY_BLOCK_TYPE = 1;
   private static final int DRM_KEY_ALG_TYPE = 2;
   private static final int DRM_KEY_ALG_ENCRYPTIONUTILTIES = 1;
   private static final int DRM_CRC_TYPE = 5;
   private static final int DRM_CRC_LENGTH = 4;
   private static final int DRM_SIM_KEY_TYPE = 3;
   private static final int DRM_DEVICE_KEY_TYPE = 4;
   private static final int DRM_DATA_BLOCK_TYPE = 6;
   private static final int DRM_KEY_SIZE = 16;
   private static String DRM_HEADER = "DRM";
   private static final byte DRM_VERSION = 1;

   public static final byte[] encrypt(InputStream input) {
      DataBuffer db = (DataBuffer)createEncryptionResult(input, false, -1);
      return db != null ? db.toArray() : EMPTY;
   }

   private static final Object createEncryptionResult(InputStream input, boolean calculateLength, int length) {
      byte[] deviceKey = DRMServices.getDeviceKey();
      byte[] simKey = DRMServices.getSubscriberKey();
      byte[] sk = RandomSource.getBytes(16);
      DataBuffer db = (DataBuffer)(new Object());

      try {
         db.write(DRM_HEADER.getBytes());
         db.writeByte(1);
         db.writeByte(1);
         int crc = CRC32.update(-1, sk);
         DataBuffer temp = (DataBuffer)(new Object());
         temp.writeByte(2);
         temp.writeByte(1);
         temp.writeByte(5);
         temp.writeCompressedInt(4);
         temp.writeInt(crc);
         byte[] cipher = new byte[EncryptionUtilities.getCiphertextLength(sk.length)];
         if (simKey != null) {
            int cipherlength = EncryptionUtilities.encrypt(simKey, sk, 0, sk.length, cipher, 0);
            Array.resize(cipher, cipherlength);
            temp.writeByte(3);
            temp.writeCompressedInt(cipher.length);
            temp.write(cipher);
         }

         EncryptionUtilities.encrypt(deviceKey, sk, 0, sk.length, cipher, 0);
         temp.writeByte(4);
         temp.writeCompressedInt(cipher.length);
         temp.write(cipher);
         byte[] data = temp.toArray();
         db.writeCompressedInt(data.length);
         db.write(data);
         db.write(6);
         temp.reset();
         if (calculateLength) {
            length = EncryptionUtilities.getCiphertextLength(length);
            db.writeCompressedInt(length);
            return new Object(db.getLength() + length);
         }

         data = new byte[256];
         int len = 0;

         while (-1 != (len = input.read(data))) {
            temp.write(data, 0, len);
         }

         data = temp.toArray();
         cipher = new byte[EncryptionUtilities.getCiphertextLength(data.length)];
         EncryptionUtilities.encrypt(sk, data, 0, data.length, cipher, 0);
         db.writeCompressedInt(cipher.length);
         db.write(cipher);
         return db;
      } finally {
         ;
      }
   }

   static final int getEncryptedLength(int length) {
      Integer intValue = (Integer)createEncryptionResult(null, true, length);
      return intValue != null ? intValue : 0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final byte[] decrypt(byte[] drmbuffer) {
      DataBuffer db = (DataBuffer)(new Object(drmbuffer, 0, drmbuffer.length, true));
      db.rewind();
      byte[] temp = new byte[256];
      int headerLength = DRM_HEADER.length();
      if (headerLength == db.read(temp, 0, headerLength) && Arrays.equals(temp, 0, DRM_HEADER.getBytes(), 0, headerLength)) {
         int crc = 0;
         byte[] simcipher = null;
         byte[] devicecipher = null;
         byte[] datacipher = null;
         boolean var15 = false /* VF: Semaphore variable */;

         try {
            var15 = true;
            if (db.readByte() != 1) {
               throw new Object("ContentStore: DRM version mismatch, expecting 1");
            }

            while (db.available() > 0) {
               switch (db.readByte()) {
                  case 0:
                     throw new Object("ContentStore: error parsing DRM buffer");
                  case 1:
                  default:
                     db.readCompressedInt();
                     break;
                  case 2:
                     if (db.readByte() != 1) {
                        throw new Object("ContentStore: DRM unsupported algorithm");
                     }
                     break;
                  case 3: {
                     int len = db.readCompressedInt();
                     simcipher = new byte[len];
                     db.read(simcipher);
                     break;
                  }
                  case 4: {
                     int len = db.readCompressedInt();
                     devicecipher = new byte[len];
                     db.read(devicecipher);
                     break;
                  }
                  case 5:
                     db.readCompressedInt();
                     crc = db.readInt();
                     break;
                  case 6: {
                     int len = db.readCompressedInt();
                     datacipher = new byte[len];
                     db.read(datacipher);
                  }
               }
            }

            var15 = false;
         } finally {
            if (var15) {
               throw new Object("ContentStore: error parsing DRM buffer");
            }
         }

         byte[] sk = decryptSessionKey(DRMServices.getSubscriberKey(), simcipher, crc);
         if (sk == null) {
            sk = decryptSessionKey(DRMServices.getDeviceKey(), devicecipher, crc);
            if (sk == null) {
               throw new Object("ContentStore: unable to decrypt data! no matching key found");
            }
         }

         byte[] plaintext = new byte[datacipher.length];

         try {
            int plaintextlength = EncryptionUtilities.decrypt(sk, datacipher, 0, datacipher.length, plaintext, 0);
            Array.resize(plaintext, plaintextlength);
            return plaintext;
         } finally {
            throw new Object("ContentStore: failed to decrypt data!");
         }
      } else {
         return drmbuffer;
      }
   }

   private static final byte[] decryptSessionKey(byte[] key, byte[] cipher, int crc) {
      if (key != null && cipher != null) {
         byte[] plaintext = new byte[cipher.length];

         try {
            int plaintextlength = EncryptionUtilities.decrypt(key, cipher, 0, cipher.length, plaintext, 0);
            Array.resize(plaintext, plaintextlength);
         } finally {
            ;
         }

         return crc != CRC32.update(-1, plaintext) ? null : plaintext;
      } else {
         return null;
      }
   }
}
