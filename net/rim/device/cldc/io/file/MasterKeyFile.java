package net.rim.device.cldc.io.file;

import net.rim.device.api.crypto.SHA256Digest;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEFieldController;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.crypto.EncryptionUtilities;
import net.rim.vm.Array;

public class MasterKeyFile implements TLEFieldController {
   byte[] _masterKey;
   byte[] _salt;
   byte[] _cipher;
   byte[] _deviceKeyHash;
   int _version;
   int _lockType = -1;
   int _pin;
   int _cipherAlgorithm;
   long _timestamp;
   public static final int DEVICE_LOCKED;
   public static final int DEVIVE_AND_PASSWORD_LOCKED;
   public static final int PASSWORD_LOCKED;
   public static final int KNOWN_PASSOWRD_LOCKED;
   public static final int CIPHER_AES_WITH_256_DIGEST;
   private static final int VERSION;
   private static final int LOCK_TYPE;
   private static final int CIPHER;
   private static final int SALT;
   private static final int KEY_HASH;
   private static final int PIN;
   private static final int CIPHER_ALGORITHM;

   public void writeFields(DataBuffer db) {
      TLEUtilities.writeField(db, 6, this);
      TLEUtilities.writeField(db, 1, this);
      TLEUtilities.writeField(db, 2, this);
      TLEUtilities.writeField(db, 3, this);
      TLEUtilities.writeField(db, 4, this);
      TLEUtilities.writeField(db, 7, this);
      TLEUtilities.writeField(db, 5, this);
   }

   public boolean checkUserKey() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 0: aload 0
      // 1: aconst_null
      // 2: invokevirtual net/rim/device/cldc/io/file/MasterKeyFile.decryptMasterKey ([B)V
      // 5: bipush 1
      // 6: ireturn
      // 7: astore 1
      // 8: bipush 0
      // 9: ireturn
      // a: astore 1
      // b: bipush 0
      // c: ireturn
      // try (0 -> 4): 5 null
      // try (0 -> 4): 8 null
   }

   public boolean isPasswordLocked() {
      return this._lockType == 2 || this._lockType == 3;
   }

   public boolean isDeviceLocked() {
      return this._lockType == 2 || this._lockType == 1;
   }

   public boolean keyFileMatchesDevice() {
      return !this.pinMatchesDevice() ? false : this._deviceKeyHash == null || Arrays.equals(this._deviceKeyHash, FileSystemEncryption.getDeviceKeyHash());
   }

   public boolean pinMatchesDevice() {
      return this._pin == DeviceInfo.getDeviceId();
   }

   public byte[] getSalt() {
      return this._salt;
   }

   public int getLockType() {
      return this._lockType;
   }

   public long getTimestamp() {
      return this._timestamp;
   }

   public byte[] getMasterKey() {
      return this._masterKey;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void decryptMasterKey(byte[] pwd) {
      if (this._cipherAlgorithm != 1) {
         throw new Object("Bad cipher algorithm");
      }

      byte[] userKey = FileSystemEncryption.getUserKey(pwd, this._salt, this._lockType);
      this._masterKey = new byte[this._cipher.length];

      try {
         int numBytes = EncryptionUtilities.decrypt(userKey, this._cipher, 0, this._cipher.length, this._masterKey, 0);
         Array.resize(this._masterKey, numBytes);
      } catch (Throwable var6) {
         throw new Object(((StringBuffer)(new Object("failed to decrypt "))).append(e.getMessage()).toString());
      }

      int keyLength = this._masterKey.length - 32;
      SHA256Digest digest = (SHA256Digest)(new Object());
      digest.update(this._masterKey, 0, keyLength);
      if (Arrays.equals(digest.getDigest(), 0, this._masterKey, keyLength, 32)) {
         Array.resize(this._masterKey, keyLength);
      } else {
         EventLogger.logEvent(4782370668738403183L, 1819764580, 0);
         throw new Object("Error trying to open file where device key doesn't match card");
      }
   }

   public boolean checkPassword(byte[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnonnull 06
      // 04: bipush 0
      // 05: ireturn
      // 06: aload 0
      // 07: aload 1
      // 08: invokevirtual net/rim/device/cldc/io/file/MasterKeyFile.decryptMasterKey ([B)V
      // 0b: bipush 1
      // 0c: ireturn
      // 0d: astore 2
      // 0e: bipush 0
      // 0f: ireturn
      // 10: astore 2
      // 11: bipush 0
      // 12: ireturn
      // try (4 -> 8): 9 null
      // try (4 -> 8): 12 null
   }

   @Override
   public void dumpField(int type, DataBuffer db) {
      switch (type) {
         case 0:
            break;
         case 1:
         default:
            db.writeCompressedInt(this._version);
            return;
         case 2:
            db.writeCompressedInt(this._lockType);
            return;
         case 3:
            if (this._cipher != null) {
               db.writeByteArray(this._cipher);
               return;
            }
            break;
         case 4:
            if (this._salt != null) {
               db.writeByteArray(this._salt);
               return;
            }
            break;
         case 5:
            db.writeByteArray(this._deviceKeyHash);
            break;
         case 6:
            db.writeCompressedInt(this._pin);
            return;
         case 7:
            db.writeCompressedInt(this._cipherAlgorithm);
            return;
      }
   }

   @Override
   public boolean processField(int type, int length, DataBuffer db) {
      try {
         switch (type) {
            case 0:
               return false;
            case 1:
            default:
               this._version = db.readCompressedInt();
               return true;
            case 2:
               this._lockType = db.readCompressedInt();
               return true;
            case 3:
               this._cipher = db.readByteArray();
               return true;
            case 4:
               this._salt = db.readByteArray();
               return true;
            case 5:
               this._deviceKeyHash = db.readByteArray();
               return true;
            case 6:
               this._pin = db.readCompressedInt();
               return true;
            case 7:
               this._cipherAlgorithm = db.readCompressedInt();
               return true;
         }
      } finally {
         throw new Object();
      }
   }

   public MasterKeyFile(long timestamp) {
      this._timestamp = timestamp;
   }
}
