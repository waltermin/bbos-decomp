package net.rim.device.api.system;

import java.io.EOFException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.crypto.Crypto10Initialization;
import net.rim.device.api.crypto.CryptoByteArrayArithmetic;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.ECPointAtInfinityException;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.SHA256Digest;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.internal.compress.CompressUtilities;
import net.rim.device.internal.crypto.EncryptionUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.vm.Array;
import net.rim.vm.EventLog;
import net.rim.vm.Process;
import net.rim.vm.WeakReference;

public final class PersistentContent {
   private boolean _compress = true;
   private boolean _encrypt;
   private int _encryptStrength;
   private boolean _pendingCompress;
   private boolean _pendingEncrypt;
   private int _modeGeneration;
   private int _lockGeneration;
   private int _numRecodes;
   private int _numPKRecodes;
   private boolean _secureGCNeeded;
   private boolean _secure;
   private final int _symmetricKeyLength = 32;
   private byte[] _deviceSymmetricKey;
   private final int _encryptionOverhead = EncryptionUtilities.getCiphertextLength(0);
   private int[] _eccCurves = new int[]{0, 3, 4, -805044203, 135006216, 33817351, 117835017, 84476421, 100862985, 13063, 1128485376, 1229148672};
   private final int _publicKeyLength0 = EncryptionUtilities.getPublicKeyLength(0);
   private final int _publicKeyLength1 = EncryptionUtilities.getPublicKeyLength(3);
   private final int _publicKeyLength2 = EncryptionUtilities.getPublicKeyLength(4);
   private int[] _publicKeyLengths = new int[]{this._publicKeyLength0, this._publicKeyLength1, this._publicKeyLength2};
   private byte[][][] _devicePublicKeys = new byte[][][]{
      (byte[][])(new byte[this._publicKeyLength0]), (byte[][])(new byte[this._publicKeyLength1]), (byte[][])(new byte[this._publicKeyLength2])
   };
   private final int _privateKeyLength0 = EncryptionUtilities.getPrivateKeyLength(0);
   private final int _privateKeyLength1 = EncryptionUtilities.getPrivateKeyLength(3);
   private final int _privateKeyLength2 = EncryptionUtilities.getPrivateKeyLength(4);
   private int[] _privateKeyLengths = new int[]{this._privateKeyLength0, this._privateKeyLength1, this._privateKeyLength2};
   private byte[][][] _devicePrivateKeys;
   private byte[] _ephemeralPublicKey;
   private Object _ticket;
   private WeakReference _ticketWR = new WeakReference(null);
   private PersistentContent$ObjectCache _objectCache = new PersistentContent$ObjectCache();
   private PersistentContent$SymmetricKeyCache _symmetricKeyCache = new PersistentContent$SymmetricKeyCache();
   private PersistentContent$Listeners _listeners;
   private WeakReference _ceBufferWR = new WeakReference(null);
   private String _emptyString = "";
   private byte[] _emptyByteArray = new byte[0];
   private final int _expectedNvStoreDataLength;
   private Thread _checkSecureThread;
   private int _checkSecureDelay = 1000;
   private Hashtable _listenerClassNames;
   private PersistentObject _listenerClassNamesPersistentObject;
   private long _rTimestamp;
   private byte[] _r;
   private byte[] _passwordKey;
   private byte[] _digestOfrD;
   private static final int COMPRESS_MIN_BYTE_LENGTH;
   private static final int MASTER_HEADER_BYTE_LENGTH;
   private static final int MASTER_COMPRESS_FLAG;
   private static final int MASTER_ENCRYPT_FLAG;
   private static final int MASTER_STRING_FLAG;
   private static final int MASTER_BYTES_FLAG;
   private static final int NUM_MASTER_FLAGS;
   private static final int MASTER_FLAGS_MASK;
   private static final int MASTER_BYTE_LENGTH_SHIFT;
   private static final int MASTER_BYTE_LENGTH_BITS;
   private static final int MAX_MASTER_BYTE_LENGTH;
   private static final int BLOCK_HEADER_BYTE_LENGTH;
   private static final int BLOCK_COMPRESS_FLAG;
   private static final int BLOCK_PUBLIC_KEY_FLAG;
   private static final int BLOCK_DEVICE_KEY_FLAG;
   private static final int BLOCK_BYTES_FLAG;
   private static final int NUM_BLOCK_FLAGS;
   private static final int BLOCK_BYTE_LENGTH_SHIFT;
   private static final int BLOCK_BYTE_LENGTH_BITS;
   private static final int MAX_BLOCK_BYTE_LENGTH;
   private static final long ID;
   private static final long EVENT_LOGGER_ID;
   private static PersistentContent _instance;
   private static boolean _fileHook = Array.hasFileSupport();
   private static final int ECC_CURVE_0;
   private static final int ECC_CURVE_1;
   private static final int ECC_CURVE_2;
   private static final int ONE_SECOND;
   private static final int ONE_MINUTE;
   private static final int ONE_HOUR;
   private static final int INITIAL_CHECK_SECURE_DELAY;
   private static final byte NV_VERSION;
   private static final int NV_COMPRESS_FLAG;
   private static final int NV_ENCRYPT_FLAG;
   private static final int NV_STRENGTH_MASK;
   private static final int NV_STRENGTH_SHIFT;
   private static final long R_LIFETIME;

   private PersistentContent() {
      net.rim.vm.Memory.createGroup(this._emptyString);
      net.rim.vm.Memory.createGroup(this._emptyByteArray);
      this._expectedNvStoreDataLength = 11
         + this._publicKeyLength0
         + this._publicKeyLength1
         + this._publicKeyLength2
         + EncryptionUtilities.getCiphertextLength(32 + this._privateKeyLength0 + this._privateKeyLength1 + this._privateKeyLength2 + 4);

      try {
         this.loadSettings(null);
      } catch (PersistentContent$MissingPasswordException var2) {
      }

      this._pendingCompress = this._compress;
      this._pendingEncrypt = this._encrypt;
      this._listeners = new PersistentContent$Listeners(this._encrypt ? 3 : 1, this._modeGeneration);
      if (this._encrypt) {
         Proxy.getInstance().invokeLater(new PersistentContent$CheckSecureThreadLauncher(null));
         this._modeGeneration++;
         this.checkSecure();
      }

      this._listenerClassNamesPersistentObject = PersistentStore.getPersistentObject(-8690596288514859193L);
      this._listenerClassNames = (Hashtable)this._listenerClassNamesPersistentObject.getContents();
      if (this._listenerClassNames == null) {
         this._listenerClassNames = new Hashtable();
         String className = "net.rim.device.api.util.ContentProtectedHashtable";
         this._listenerClassNames.put(className, className);
         className = "net.rim.device.api.util.ContentProtectedVector";
         this._listenerClassNames.put(className, className);
         this._listenerClassNamesPersistentObject.setContents(this._listenerClassNames, 51);
         this._listenerClassNamesPersistentObject.commit();
      }
   }

   static final PersistentContent getInstance() {
      return _instance;
   }

   private final byte[] calculateStorageKey(String password, byte[] salt, int offset, int length) {
      byte[] passwordBytes;
      if (password != null) {
         passwordBytes = password.getBytes();
      } else {
         passwordBytes = new byte[]{8, 8, 12, 8, 7, 3, 4, 2, 9, 5, 6, 7, 5, 2, 9, 5, 9, 12, 3, 6, 7};
      }

      Digest digest = new SHA256Digest();
      digest.update(salt, offset, length);
      digest.update(passwordBytes, 0, passwordBytes.length);
      digest.update(salt, offset, length);
      int iterationCount = 19;
      byte[] key = net.rim.vm.Memory.allocRAMOnlyBytes(digest.getDigestLength());
      digest.getDigest(key, 0);

      for (int i = 1; i < iterationCount; i++) {
         digest.update(key);
         digest.getDigest(key, 0);
      }

      net.rim.vm.Memory.setPlaintext(passwordBytes);
      net.rim.vm.Memory.setPlaintext(key);
      return key;
   }

   private final byte[] getNvStoreData() {
      byte[] data = NvStore.readData(10);
      if (data == null) {
         return null;
      }

      if ((data.length == 2 || data.length == this._expectedNvStoreDataLength) && data[0] == 9) {
         return data;
      }

      NvStore.deleteData(10);
      return null;
   }

   private final void parseNvStoreData(String password) {
      if (password != null) {
         net.rim.vm.Memory.setPlaintext(password);
      }

      byte[] data = this.getNvStoreData();
      if (data != null) {
         byte flags = data[1];
         this._compress = (flags & 1) != 0;
         if (data.length != 2) {
            this._encrypt = (flags & 2) != 0;
            this._encryptStrength = flags >> 2 & 3;
            Crypto10Initialization.setCurrentECMMode(this._encrypt);
            InternalServices.allowOSLogging(!this._encrypt || !InternalServices.isDeviceSecure());
            MemoryCleanerManager.getInstance().setPersistentContentSecureOldObjects(this._encrypt);
            NvStore.setFlag(8, this._encrypt);
            if (this._encrypt && !Security.getInstance().isPasswordEnabled()) {
               Security.getInstance().deviceUnderAttack();
            }

            int offset = 11;
            System.arraycopy(data, offset, this._devicePublicKeys[0], 0, this._publicKeyLength0);
            offset += this._publicKeyLength0;
            System.arraycopy(data, offset, this._devicePublicKeys[1], 0, this._publicKeyLength1);
            offset += this._publicKeyLength1;
            System.arraycopy(data, offset, this._devicePublicKeys[2], 0, this._publicKeyLength2);
            offset += this._publicKeyLength2;
            if (this._encrypt) {
               if (password == null) {
                  throw new PersistentContent$MissingPasswordException(null);
               }
            } else {
               password = null;
            }

            byte[] storageKey = this.calculateStorageKey(password, data, 3, 8);
            int ciphertextOffset = offset;
            int ciphertextLength = data.length - ciphertextOffset;
            byte[] plaintext = net.rim.vm.Memory.allocRAMOnlyBytes(ciphertextLength);
            net.rim.vm.Memory.setPlaintext(plaintext);
            int plaintextLength = EncryptionUtilities.decrypt(storageKey, data, ciphertextOffset, ciphertextLength, plaintext, 0, false);
            int dataCRC = CRC32.update(-1, plaintext, 0, plaintextLength - 4);
            int storedCRC = (plaintext[plaintextLength - 4] & 255) << 24
               | (plaintext[plaintextLength - 3] & 255) << 16
               | (plaintext[plaintextLength - 2] & 255) << 8
               | plaintext[plaintextLength - 1] & 255;
            if (dataCRC != storedCRC) {
               throw new RuntimeException("Bad Password");
            }

            this.encryptPassword(password, false);
            this._devicePrivateKeys = new byte[3][][];
            int var15 = 0;
            this._deviceSymmetricKey = net.rim.vm.Memory.copyToRAMOnlyBytes(plaintext, var15, 32);
            var15 += 32;
            this._devicePrivateKeys[0] = (byte[][])net.rim.vm.Memory.copyToRAMOnlyBytes(plaintext, var15, this._privateKeyLength0);
            var15 += this._privateKeyLength0;
            this._devicePrivateKeys[1] = (byte[][])net.rim.vm.Memory.copyToRAMOnlyBytes(plaintext, var15, this._privateKeyLength1);
            var15 += this._privateKeyLength1;
            this._devicePrivateKeys[2] = (byte[][])net.rim.vm.Memory.copyToRAMOnlyBytes(plaintext, var15, this._privateKeyLength2);
            net.rim.vm.Memory.setPlaintext(this._deviceSymmetricKey);
            net.rim.vm.Memory.setPlaintext(this._devicePrivateKeys[0]);
            net.rim.vm.Memory.setPlaintext(this._devicePrivateKeys[1]);
            net.rim.vm.Memory.setPlaintext(this._devicePrivateKeys[2]);
         }
      }
   }

   private final void loadSettings(String password) {
      this.parseNvStoreData(password);
      this._ticket = this._ticketWR.get();
      if (this._ticket == null) {
         this._ticket = new Object();
         net.rim.vm.Memory.setPlaintext(this._ticket);
         this._ticketWR.set(this._ticket);
      }
   }

   private final void saveContentCompressionSettings() {
      byte[] data = this.getNvStoreData();
      if (data == null) {
         data = new byte[]{9, 0};
      }

      if (this._pendingCompress) {
         data[1] = (byte)(data[1] | 1);
      } else {
         data[1] = (byte)(data[1] & -2);
      }

      if (!NvStore.writeData(10, data)) {
         throw new RuntimeException("Unable to store Persistent Content Settings");
      }

      RIMGlobalMessagePoster.postGlobalEvent(9206737719270818227L, 0, 0);
   }

   private final void saveContentProtectionSettings(String password) {
      if (password != null) {
         net.rim.vm.Memory.setPlaintext(password);
      }

      if (!this._pendingEncrypt) {
         password = null;
      }

      if (this._devicePrivateKeys != null) {
         int plaintextLength = 32 + this._privateKeyLength0 + this._privateKeyLength1 + this._privateKeyLength2 + 4;
         byte[] plaintext = net.rim.vm.Memory.allocRAMOnlyBytes(plaintextLength);
         net.rim.vm.Memory.setPlaintext(plaintext);
         int offset = 0;
         System.arraycopy(this._deviceSymmetricKey, 0, plaintext, offset, 32);
         offset += 32;
         System.arraycopy(this._devicePrivateKeys[0], 0, plaintext, offset, this._privateKeyLength0);
         offset += this._privateKeyLength0;
         System.arraycopy(this._devicePrivateKeys[1], 0, plaintext, offset, this._privateKeyLength1);
         offset += this._privateKeyLength1;
         System.arraycopy(this._devicePrivateKeys[2], 0, plaintext, offset, this._privateKeyLength2);
         int crc = CRC32.update(-1, plaintext, 0, plaintext.length - 4);
         plaintext[plaintextLength - 4] = (byte)(crc >>> 24 & 0xFF);
         plaintext[plaintextLength - 3] = (byte)(crc >>> 16 & 0xFF);
         plaintext[plaintextLength - 2] = (byte)(crc >>> 8 & 0xFF);
         plaintext[plaintextLength - 1] = (byte)(crc & 0xFF);
         byte[] data = new byte[this._expectedNvStoreDataLength];
         data[0] = 9;
         data[1] = (byte)((this._pendingCompress ? 1 : 0) | (this._pendingEncrypt ? 2 : 0) | (this._encryptStrength & 3) << 2);
         data[2] = 0;
         RandomSource.getBytes(data, 3, 8);
         int var11 = 11;
         System.arraycopy(this._devicePublicKeys[0], 0, data, var11, this._publicKeyLength0);
         var11 += this._publicKeyLength0;
         System.arraycopy(this._devicePublicKeys[1], 0, data, var11, this._publicKeyLength1);
         var11 += this._publicKeyLength1;
         System.arraycopy(this._devicePublicKeys[2], 0, data, var11, this._publicKeyLength2);
         var11 += this._publicKeyLength2;
         byte[] storageKey = this.calculateStorageKey(password, data, 3, 8);
         EncryptionUtilities.encrypt(storageKey, plaintext, 0, plaintextLength, data, var11);
         if (!NvStore.writeData(10, data)) {
            throw new RuntimeException("Unable to store Persistent Content Settings");
         }

         this.encryptPassword(password, true);
         RIMGlobalMessagePoster.postGlobalEvent(9206737719270818227L, 0, 0);
      }
   }

   final synchronized void registerPersistentContentIndicator(PersistentContentListener listener) {
      this._listeners.addIndicator(listener);
   }

   final synchronized void lock() {
      if (this._numRecodes > 0 || this._numPKRecodes > 2053 || this._secureGCNeeded) {
         this._listeners.modeChanged(++this._modeGeneration);
      }

      this._lockGeneration++;
      if (this._pendingEncrypt) {
         if (this._checkSecureThread == null) {
            Proxy.getInstance().invokeLater(new PersistentContent$CheckSecureThreadLauncher(null));
         }

         if (this._ticket != null) {
            this._listeners.stateChanged(2, this._lockGeneration);
         }
      }

      this._listeners.lockChanged(this._lockGeneration);
   }

   final synchronized void lock2(int generation) {
      if (generation == this._lockGeneration && this._encrypt && this._ticket != null) {
         this._ticket = null;
         this._listeners.stateChanged(4, this._lockGeneration);
         this.checkSecure();
      }
   }

   public static final boolean isSecure() {
      synchronized (_instance) {
         return _instance._secure;
      }
   }

   public static final boolean isTicketInUse() {
      synchronized (_instance) {
         return _instance._ticketWR.get() != null;
      }
   }

   private final synchronized void checkSecure() {
      if (this._checkSecureThread != null && this._checkSecureDelay != 1000) {
         this._checkSecureDelay = 1000;
         synchronized (this._checkSecureThread) {
            this._checkSecureThread.notify();
         }
      }
   }

   private final void checkSecureLoop() {
      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();

      while (applicationManager.inStartup()) {
         try {
            Thread.sleep(10000);
         } catch (InterruptedException var21) {
         }
      }

      synchronized (_instance) {
         if (this._encrypt) {
            this._listeners.modeChanged(this._modeGeneration);
         }
      }

      MemoryCleanerManager memoryCleanerManager = MemoryCleanerManager.getInstance();
      int maxCheckSecureDelay = 60000;
      boolean ticketAvailable = true;
      int numPlaintext = 0;
      int numPlaintextSpecial = 0;

      while (true) {
         int checkSecureDelay;
         synchronized (this) {
            this._listeners.updateStateIndicator();
            this._checkSecureDelay = Math.min(this._checkSecureDelay * 2, maxCheckSecureDelay);
            checkSecureDelay = this._checkSecureDelay;
         }

         synchronized (this._checkSecureThread) {
            try {
               this._checkSecureThread.wait(checkSecureDelay);
            } catch (InterruptedException var16) {
            }

            for (int loop = 0; loop < 120 && DeviceInfo.getIdleTime() < 3; loop++) {
               try {
                  this._checkSecureThread.wait(1000);
               } catch (InterruptedException var15) {
               }
            }

            int loop = 0;

            for (int lastIdleCounter = Process.getLastIdleCounter(); loop < 120 && lastIdleCounter == Process.getLastIdleCounter(); loop++) {
               try {
                  this._checkSecureThread.wait(1000);
               } catch (InterruptedException var20) {
               }
            }
         }

         synchronized (this) {
            if (this._r != null && System.currentTimeMillis() - this._rTimestamp > 600000) {
               this._r = null;
               this._passwordKey = null;
            }

            if (this._checkSecureDelay == 1000) {
               continue;
            }

            ticketAvailable = this._ticket != null;
            if (!ticketAvailable) {
               System.out.println("PC: checking...");
               maxCheckSecureDelay = 3000;
               if (Phone.isSupported() && Phone.getInstance().isActive()) {
                  System.out.println("PC: phone is active");
                  continue;
               }

               if (this._secureGCNeeded) {
                  System.out.println("PC: starting secureGC");
                  net.rim.vm.Memory.secureThoroughGC();
                  System.out.println("PC: finished secureGC");
                  this._secureGCNeeded = false;
               }

               maxCheckSecureDelay = 60000;
               ticketAvailable = this._ticketWR.get() != null;
               if (ticketAvailable) {
                  net.rim.vm.Memory.fullGC();
                  ticketAvailable = this._ticketWR.get() != null;
                  if (ticketAvailable) {
                     System.out.println("PC: locked, ticket in use");
                     continue;
                  }
               }

               if (!this._listeners.isUpdateComplete()) {
                  System.out.println("PC: locked, updating PC listeners");
                  continue;
               }

               if (this._devicePrivateKeys != null) {
                  System.out.println("PC: locked, erasing key");
                  this._deviceSymmetricKey = null;
                  this._devicePrivateKeys = (byte[][][])((byte[][])null);
                  this._symmetricKeyCache = new PersistentContent$SymmetricKeyCache();
                  memoryCleanerManager.cleanPersistentContent();
                  this._checkSecureDelay = 1000;
               }

               if (!memoryCleanerManager.isUpdateComplete()) {
                  System.out.println("PC: locked, updating mem listeners");
                  continue;
               }

               System.out.println("PC: locked, as secure as we're gonna be");
               this._secure = true;
               this._listeners.stateChanged(3, this._lockGeneration);
               maxCheckSecureDelay = 3600000;
               numPlaintext = net.rim.vm.Memory.numPlaintext();
               if (numPlaintext > 0) {
                  numPlaintextSpecial = net.rim.vm.Memory.numPlaintextSpecial();
                  if (numPlaintext - numPlaintextSpecial > 0) {
                     System.out.println("PC: locked, plaintext found");
                     memoryCleanerManager.cleanPersistentContent();
                     continue;
                  }
               }
            }
         }

         synchronized (this._checkSecureThread) {
            try {
               System.out.println("PC: waiting");
               this._checkSecureThread.wait();
            } catch (InterruptedException var18) {
            }
         }
      }
   }

   final void encryptPassword(String password, boolean force) {
      if (password != null) {
         byte[] plaintext = password.getBytes();
         int curve = 2;
         int publicKeyLength = EncryptionUtilities.getPublicKeyLength(curve);
         int privateKeyLength = EncryptionUtilities.getPrivateKeyLength(curve);
         byte[] B = this.getBFromITPolicy(publicKeyLength);
         if (B == null) {
            PersistentContent$ResetPasswordData.clearInstance();
         } else {
            PersistentContent$ResetPasswordData data = PersistentContent$ResetPasswordData.getInstance();
            if (force || data == null || !data.isSameB(B)) {
               byte[] d = net.rim.vm.Memory.allocRAMOnlyBytes(privateKeyLength);
               byte[] D = new byte[publicKeyLength];
               EncryptionUtilities.createKeyPair(curve, D, d);
               byte[] sharedPoint = net.rim.vm.Memory.allocRAMOnlyBytes(privateKeyLength);
               EncryptionUtilities.calculateKey(curve, B, d, sharedPoint);
               Digest digest = new SHA256Digest();
               byte[] key = net.rim.vm.Memory.allocRAMOnlyBytes(digest.getDigestLength());
               digest.update(sharedPoint);
               digest.getDigest(key, 0);
               byte[] ciphertext = new byte[EncryptionUtilities.getCiphertextLength(plaintext.length)];
               EncryptionUtilities.encrypt(key, plaintext, 0, plaintext.length, ciphertext, 0);
               PersistentContent$ResetPasswordData.createInstance(B, D, ciphertext);
            }
         }
      }
   }

   final byte[] getBFromITPolicy(int expectedKeyLength) {
      byte[] result = null;
      byte[] policyDataBytes = ITPolicy.getByteArray(248);
      if (policyDataBytes != null && policyDataBytes.length >= expectedKeyLength + 2) {
         DataBuffer policyDataBuffer = new DataBuffer(policyDataBytes, 0, policyDataBytes.length, true);

         try {
            byte itemID = policyDataBuffer.readByte();
            result = policyDataBuffer.readByteArray();
            if (result == null || result.length != expectedKeyLength) {
               return null;
            }
         } catch (EOFException e) {
            return null;
         } catch (IOException e) {
            result = null;
         }
      }

      return result;
   }

   final byte[] getD() {
      int curve = 2;
      int publicKeyLength = EncryptionUtilities.getPublicKeyLength(curve);
      int privateKeyLength = EncryptionUtilities.getPrivateKeyLength(curve);
      PersistentContent$ResetPasswordData data = PersistentContent$ResetPasswordData.getInstance();
      if (data == null) {
         return new byte[0];
      }

      byte[] modulus = new byte[privateKeyLength];
      EncryptionUtilities.getGroupOrder(curve, modulus);
      long now = System.currentTimeMillis();
      if (this._r == null || now - this._rTimestamp < 300000) {
         this._rTimestamp = now;
         this._r = net.rim.vm.Memory.allocRAMOnlyBytes(modulus.length);

         do {
            CryptoByteArrayArithmetic.mod(RandomSource.getBytes(modulus.length), modulus, this._r);
         } while (CryptoByteArrayArithmetic.isZero(this._r));
      }

      byte[] rD = new byte[publicKeyLength];

      try {
         EncryptionUtilities.scalarMultiply(curve, this._r, data.getD(), rD);
      } catch (ECPointAtInfinityException e) {
         throw new RuntimeException(e.toString());
      }

      Digest digest = new SHA1Digest();
      digest.update(rD);
      this._digestOfrD = digest.getDigest();
      byte[] result = new byte[rD.length];
      System.arraycopy(rD, 0, result, 0, rD.length);
      return result;
   }

   final byte[] getBChecksum() {
      byte[] result = null;
      PersistentContent$ResetPasswordData data = PersistentContent$ResetPasswordData.getInstance();
      return data == null ? new byte[0] : data.getDigestOfB();
   }

   final boolean setK(byte[] data, byte[] checksum) {
      int curve = 2;
      int publicKeyLength = EncryptionUtilities.getPublicKeyLength(curve);
      int privateKeyLength = EncryptionUtilities.getPrivateKeyLength(curve);
      PersistentContent$ResetPasswordData resetPasswordData = PersistentContent$ResetPasswordData.getInstance();
      if (data != null
         && checksum != null
         && resetPasswordData != null
         && data.length == privateKeyLength
         && checksum.length == 20
         && Arrays.equals(checksum, 0, this._digestOfrD, 0, this._digestOfrD.length)) {
         byte[] K = new byte[publicKeyLength];
         K[0] = 2;
         System.arraycopy(data, 0, K, 1, data.length);
         byte[] modulus = new byte[privateKeyLength];
         EncryptionUtilities.getGroupOrder(curve, modulus);
         byte[] rInverse = new byte[modulus.length];
         CryptoByteArrayArithmetic.invert(this._r, modulus, rInverse);
         byte[] sharedPoint = net.rim.vm.Memory.allocRAMOnlyBytes(privateKeyLength);
         EncryptionUtilities.calculateKey(curve, K, rInverse, sharedPoint);
         Digest digest = new SHA256Digest();
         this._passwordKey = net.rim.vm.Memory.allocRAMOnlyBytes(digest.getDigestLength());
         digest.update(sharedPoint);
         digest.getDigest(this._passwordKey, 0);
         return true;
      } else {
         return false;
      }
   }

   private final String decryptPassword() {
      PersistentContent$ResetPasswordData resetPasswordData = PersistentContent$ResetPasswordData.getInstance();
      if (resetPasswordData == null) {
         return null;
      }

      byte[] ciphertext = resetPasswordData.getPasswordCiphertext();
      byte[] plaintext = net.rim.vm.Memory.allocRAMOnlyBytes(ciphertext.length);
      int plaintextLength = EncryptionUtilities.decrypt(this._passwordKey, ciphertext, 0, ciphertext.length, plaintext, 0);
      String password = new String(plaintext, 0, plaintextLength);
      net.rim.vm.Memory.setPlaintext(password);
      return password;
   }

   final void clearK() {
      this._r = null;
      this._passwordKey = null;
   }

   final void unlock(String password) {
      MemoryCleanerManager memoryCleanerManager = MemoryCleanerManager.getInstance();

      while (!memoryCleanerManager.isUpdateComplete()) {
         try {
            Thread.sleep(100);
         } catch (InterruptedException var6) {
         }
      }

      synchronized (this) {
         this.loadSettings(password);
         if (this._secureGCNeeded) {
            this._modeGeneration++;
         }

         this._secure = false;
         this._listeners.stateChanged(1, ++this._lockGeneration);
         this.notifyAll();
      }
   }

   public static final boolean isContentProtectionSupported() {
      return EncryptionUtilities.isSupported(0);
   }

   final synchronized void setContentProtection(String password, boolean encrypt, int strength) {
      if (this._ticket == null) {
         throw new IllegalStateException();
      }

      if (password != null && !Security.getInstance().isPasswordEnabled()) {
         throw new RuntimeException("Passwords out of sync");
      }

      if (this._pendingEncrypt != encrypt) {
         this._numRecodes++;
      }

      this._pendingEncrypt = encrypt;
      this._secureGCNeeded |= encrypt;
      this._encryptStrength = strength;
      Crypto10Initialization.setCurrentECMMode(encrypt);
      InternalServices.allowOSLogging(!encrypt || !InternalServices.isDeviceSecure());
      MemoryCleanerManager.getInstance().setPersistentContentSecureOldObjects(encrypt);
      NvStore.setFlag(8, encrypt);
      if (encrypt) {
         EventLog.clear();
      } else {
         net.rim.vm.Memory.resetPlaintext();
      }

      if (this._pendingEncrypt && this._devicePrivateKeys == null) {
         if (getEncryptionStrength() > 0) {
            int id;
            if (InternalServices.getFormFactor() == 13) {
               id = 10126;
            } else {
               id = 10016;
            }

            PersistentContent$RandomKeyPressDialog dialog = new PersistentContent$RandomKeyPressDialog(CommonResource.getString(id), 256, 134217728);
            dialog.setStatusPriority(-2147483644);
            BackgroundDialog.show(dialog);
         }

         this._deviceSymmetricKey = net.rim.vm.Memory.allocRAMOnlyBytes(32);
         RandomSource.getBytes(this._deviceSymmetricKey);
         this._devicePrivateKeys = new byte[][][]{
            (byte[][])net.rim.vm.Memory.allocRAMOnlyBytes(this._privateKeyLength0),
            (byte[][])net.rim.vm.Memory.allocRAMOnlyBytes(this._privateKeyLength1),
            (byte[][])net.rim.vm.Memory.allocRAMOnlyBytes(this._privateKeyLength2)
         };
         EncryptionUtilities.createKeyPair(0, (byte[])this._devicePublicKeys[0], (byte[])this._devicePrivateKeys[0]);
         EncryptionUtilities.createKeyPair(3, (byte[])this._devicePublicKeys[1], (byte[])this._devicePrivateKeys[1]);
         EncryptionUtilities.createKeyPair(4, (byte[])this._devicePublicKeys[2], (byte[])this._devicePrivateKeys[2]);
         net.rim.vm.Memory.setPlaintext(this._devicePrivateKeys[0]);
         net.rim.vm.Memory.setPlaintext(this._devicePrivateKeys[1]);
         net.rim.vm.Memory.setPlaintext(this._devicePrivateKeys[2]);
      }

      this.saveContentProtectionSettings(password);
   }

   final synchronized void changePassword(String oldPassword, String newPassword) {
      if (newPassword != null && !Security.getInstance().isPasswordEnabled()) {
         throw new RuntimeException("Passwords out of sync");
      }

      if (this._encrypt && this._devicePrivateKeys == null && oldPassword == null) {
         oldPassword = this.decryptPassword();
         if (oldPassword == null) {
            throw new IllegalStateException("Missing old password");
         }
      }

      if (this._devicePrivateKeys == null) {
         this.parseNvStoreData(oldPassword);
      }

      this.saveContentProtectionSettings(newPassword);
      if (this._ticket == null) {
         this._listeners.stateChanged(4, this._lockGeneration);
         this.checkSecure();
      }
   }

   final synchronized void setContentCompression(boolean compress) {
      if (this._pendingCompress != compress) {
         this._numRecodes++;
      }

      this._pendingCompress = compress;
      this.saveContentCompressionSettings();
   }

   private final synchronized void setMode(int modeGeneration) {
      if (modeGeneration == this._modeGeneration) {
         this._secureGCNeeded = this._secureGCNeeded | (!this._encrypt && this._pendingEncrypt);
         this._compress = this._pendingCompress;
         this._encrypt = this._pendingEncrypt;
      }
   }

   private final synchronized void setModeComplete(int modeGeneration) {
      if (modeGeneration == this._modeGeneration) {
         this._numRecodes = 0;
         this._numPKRecodes = 0;
      }
   }

   final boolean doesEncryptionKeyExist() {
      if (this._devicePrivateKeys != null) {
         return true;
      }

      byte[] data = this.getNvStoreData();
      return data != null && data.length > 2;
   }

   public static final boolean isEncryptionEnabled() {
      synchronized (_instance) {
         return _instance._pendingEncrypt;
      }
   }

   public static final int getEncryptionStrength() {
      return MathUtilities.clamp(0, Math.max(_instance._encryptStrength, ITPolicy.getInteger(24, 18, 0)), _instance._publicKeyLengths.length - 1);
   }

   public static final boolean isCompressionEnabled() {
      synchronized (_instance) {
         return _instance._pendingCompress;
      }
   }

   public static final int getModeGeneration() {
      synchronized (_instance) {
         return _instance._modeGeneration;
      }
   }

   public static final int getLockGeneration() {
      synchronized (_instance) {
         return _instance._lockGeneration;
      }
   }

   public static final int getState() {
      synchronized (_instance) {
         return _instance._listeners.getState();
      }
   }

   public static final void addListener(PersistentContentListener listener) {
      addListener(listener, false);
   }

   public static final void addWeakListener(PersistentContentListener listener) {
      addListener(listener, true);
   }

   private static final void addListener(PersistentContentListener listener, boolean weakListener) {
      synchronized (_instance) {
         _instance._listeners.add(listener, weakListener);
         _instance.addListenerClassName(listener);
      }
   }

   public static final void removeListener(PersistentContentListener listener) {
      synchronized (_instance) {
         _instance._listeners.remove(listener);
      }
   }

   private final void addListenerClassName(PersistentContentListener listener) {
      if (listener instanceof Persistable) {
         String className = listener.getClass().getName();
         this._listenerClassNames.put(className, className);
         this._listenerClassNamesPersistentObject.commit();
      }
   }

   private final synchronized void findListeners() {
      Enumeration e = this._listenerClassNames.elements();

      while (e.hasMoreElements()) {
         String className = (String)e.nextElement();

         try {
            Object[] listeners = net.rim.vm.Memory.getAllInstances(Class.forName(className));

            for (int i = listeners.length - 1; i >= 0; i--) {
               PersistentContentListener listener = (PersistentContentListener)listeners[i];
               if (!this._listeners.isListener(listener)) {
                  this._listeners.add(listener, true);
               }
            }
         } catch (ClassNotFoundException ex) {
            this._listenerClassNames.remove(className);
            this._listenerClassNamesPersistentObject.commit();
         }
      }
   }

   public static final void requestReEncode() {
      synchronized (_instance) {
         _instance._numRecodes++;
         _instance._secureGCNeeded = _instance._secureGCNeeded | (_instance._encrypt || _instance._pendingEncrypt);
      }
   }

   public static final void markAsPlaintext(Object object) {
      if (object != null) {
         _instance.setPlaintext(object);
      }
   }

   private final synchronized void setPlaintext(Object object) {
      if (this._encrypt || this._pendingEncrypt) {
         net.rim.vm.Memory.setPlaintext(object);
         this.checkSecure();
      }
   }

   public static final Object copyEncoding(Object encoding) {
      Object copy = copy(encoding);
      if (net.rim.vm.Memory.isPlaintext(encoding)) {
         markAsPlaintext(copy);
      }

      return copy;
   }

   private static final Object copy(Object encoding) {
      if (encoding instanceof String) {
         return (String)encoding;
      }

      if (!(encoding instanceof byte[])) {
         if (encoding instanceof char[]) {
            return !_fileHook ? Arrays.copy((char[])encoding) : encoding;
         }

         if (!(encoding instanceof PersistentContent$CharArrayWrapper)) {
            if (encoding == null) {
               return encoding;
            } else {
               throw new IllegalArgumentException();
            }
         } else {
            return ((PersistentContent$CharArrayWrapper)encoding).copy();
         }
      } else {
         return Arrays.copy((byte[])encoding);
      }
   }

   public static final Object getTicket() {
      synchronized (_instance) {
         return _instance._ticket;
      }
   }

   public static final Object waitForTicket() {
      while (true) {
         Object ticket = getTicket();
         if (ticket != null) {
            return ticket;
         }

         synchronized (_instance) {
            try {
               _instance.wait();
            } catch (InterruptedException var4) {
            }
         }
      }
   }

   public static final Object encodeObject(Object obj) {
      return encodeObject(obj, true, true);
   }

   public static final Object encodeObject(Object obj, boolean compress, boolean encrypt) {
      if (!(obj instanceof String)) {
         if (!(obj instanceof byte[])) {
            return obj instanceof char[] ? new PersistentContent$CharArrayWrapper((char[])obj) : obj;
         } else {
            return encode((byte[])obj, compress, encrypt);
         }
      } else {
         return encode((String)obj, compress, encrypt);
      }
   }

   public static final Object encode(String string) {
      return encodeAndAppend(string, 0, -1, true, true, null);
   }

   public static final Object encode(String string, boolean compress, boolean encrypt) {
      return encodeAndAppend(string, 0, -1, compress, encrypt, null);
   }

   public static final Object encode(String string, int index, int length, boolean compress, boolean encrypt) {
      return encodeAndAppend(string, index, length, compress, encrypt, null);
   }

   public static final Object encodeAndAppend(String string, Object content) {
      return encodeAndAppend(string, 0, -1, true, true, content);
   }

   public static final Object encodeAndAppend(String string, boolean compress, boolean encrypt, Object content) {
      return encodeAndAppend(string, 0, -1, compress, encrypt, content);
   }

   public static final Object encodeAndAppend(String string, int index, int length, boolean compress, boolean encrypt, Object content) {
      if (string == null) {
         return content;
      }

      if (index < 0) {
         throw new IllegalArgumentException();
      }

      int maxLength = string.length();
      if (length < 0) {
         length = maxLength - index;
      }

      if (index + length > maxLength) {
         throw new IllegalArgumentException();
      }

      if (length == 0) {
         return content != null ? content : _instance._emptyString;
      }

      if (content != null) {
         if (content instanceof String) {
            string = content + string.substring(index, index + length);
            index = 0;
            length = string.length();
            maxLength = length;
            content = null;
         } else if (!(content instanceof char[])) {
            throw new IllegalArgumentException();
         }
      }

      int charSize = StringUtilities.getCharacterSize(string);
      int stringLength = length;
      int byteLength = stringLength * charSize;
      int byteOffset = index * charSize;
      return _instance.encode(string, byteOffset, byteLength, stringLength, maxLength, true, compress, encrypt, (char[])content);
   }

   public static final Object encode(byte[] data) {
      return encodeAndAppend(data, 0, -1, true, true, null);
   }

   public static final Object encode(byte[] data, boolean compress, boolean encrypt) {
      return encodeAndAppend(data, 0, -1, compress, encrypt, null);
   }

   public static final Object encode(byte[] data, int offset, int length, boolean compress, boolean encrypt) {
      return encodeAndAppend(data, offset, length, compress, encrypt, null);
   }

   public static final Object encodeAndAppend(byte[] data, Object content) {
      return encodeAndAppend(data, 0, -1, true, true, content);
   }

   public static final Object encodeAndAppend(byte[] data, boolean compress, boolean encrypt, Object content) {
      return encodeAndAppend(data, 0, -1, compress, encrypt, content);
   }

   public static final Object encodeAndAppend(byte[] data, int offset, int length, boolean compress, boolean encrypt, Object content) {
      if (data == null) {
         return content;
      }

      if (offset < 0) {
         throw new IllegalArgumentException();
      }

      int maxLength = data.length;
      if (length < 0) {
         length = maxLength - offset;
      }

      if (offset + length > maxLength) {
         throw new IllegalArgumentException();
      }

      if (length == 0) {
         return content != null ? content : _instance._emptyByteArray;
      }

      if (content != null) {
         if (!(content instanceof byte[])) {
            if (!(content instanceof char[])) {
               throw new IllegalArgumentException();
            }
         } else {
            byte[] contentData = (byte[])content;
            int contentDataLength = contentData.length;
            int newDataLength = contentDataLength + length;
            byte[] newData = new byte[newDataLength];
            System.arraycopy(content, 0, newData, 0, contentDataLength);
            System.arraycopy(data, offset, newData, contentDataLength, length);
            data = newData;
            offset = 0;
            length = newDataLength;
            content = null;
         }
      }

      return _instance.encode(data, offset, length, length, maxLength, false, compress, encrypt, (char[])content);
   }

   private static final char[] read(char[] o) {
      return !_fileHook ? o : Array.readCharArrayFromFile(o);
   }

   private static final char[] write(char[] o) {
      return !_fileHook ? o : (char[])Array.writeToFile(o);
   }

   private final synchronized Object encode(
      Object input,
      int inputByteOffset,
      int inputByteLength,
      int inputElementLength,
      int inputMaxElementLength,
      boolean string,
      boolean compress,
      boolean encrypt,
      char[] output
   ) {
      boolean bytes = inputElementLength == inputByteLength;
      RandomSource.add(input);
      int header;
      int outputByteOffset;
      if (output == null) {
         compress &= this._compress && inputByteLength >= 32;
         encrypt &= this._encrypt;
         if (!encrypt && !compress && !_fileHook) {
            if (inputByteOffset == 0 && inputElementLength == inputMaxElementLength) {
               return input;
            }

            if (!(input instanceof String)) {
               byte[] array = (byte[])input;
               return Arrays.copy(array, inputByteOffset, inputByteLength);
            }

            String s = (String)input;
            int startIndex = inputByteOffset;
            if (!bytes) {
               startIndex >>= 1;
            }

            return s.substring(startIndex, startIndex + inputElementLength);
         }

         header = createMasterHeader(this._compress, encrypt, string, bytes, inputByteLength);
         outputByteOffset = 0;
      } else {
         output = read(output);
         header = getMasterHeader(output);
         if (getFlag(header, 4) != string) {
            throw new IllegalArgumentException();
         }

         compress = getFlag(header, 1) && inputByteLength >= 32;
         encrypt = getFlag(header, 2);
         if (bytes) {
            if (!getFlag(header, 8)) {
               header += inputByteLength << 4;
            }
         } else if (getFlag(header, 8)) {
            header &= -9;
            header += header & -16;
         }

         header += inputByteLength << 4;
         outputByteOffset = getOutputByteOffset(output);
      }

      int encryptionOverhead;
      int eccCurveId;
      byte[] symmetricKey;
      int publicKeyLength;
      if (encrypt) {
         this.setPlaintext(input);
         encryptionOverhead = this._encryptionOverhead;
         if (this._devicePrivateKeys != null) {
            symmetricKey = this._deviceSymmetricKey;
            eccCurveId = 0;
            publicKeyLength = 0;
         } else {
            symmetricKey = null;
            eccCurveId = getEncryptionStrength();
            publicKeyLength = this._publicKeyLengths[eccCurveId];
            this._listeners.stateChanged(4, this._lockGeneration);
         }
      } else {
         encryptionOverhead = 0;
         symmetricKey = null;
         eccCurveId = 0;
         publicKeyLength = 0;
      }

      int numBlocks = inputByteLength + 4096 - 1 >> 12;
      output = this.grow(output, outputByteOffset + inputByteLength + numBlocks * (2 + encryptionOverhead) + 1 + publicKeyLength);
      outputByteOffset = this.encodeBlocks(
         input, inputByteOffset, inputByteLength, compress, encrypt, symmetricKey, eccCurveId, bytes, output, outputByteOffset
      );
      setBytes(output, outputByteOffset, header, 3);
      outputByteOffset += 3;
      int outputCharOffset = outputByteOffset + 1 >> 1;
      Array.resize(output, outputCharOffset);
      RandomSource.add(output);
      return write(output);
   }

   private final char[] grow(char[] output, int maxByteOffset) {
      int maxCharOffset = maxByteOffset + 3 + 1 >> 1;
      if (output == null) {
         return new char[maxCharOffset];
      }

      Array.resize(output, maxCharOffset);
      return output;
   }

   private final synchronized int encodeBlocks(
      Object input,
      int inputByteOffset,
      int inputByteLength,
      boolean compress,
      boolean encrypt,
      byte[] symmetricKey,
      int eccCurveId,
      boolean bytes,
      char[] output,
      int outputByteOffset
   ) {
      int initialHeader = bytes ? 8 : 0;
      byte[] publicKey = null;
      if (encrypt) {
         if (symmetricKey != null) {
            initialHeader |= 4;
         } else {
            int publicKeyLength = this._publicKeyLengths[eccCurveId];
            publicKey = new byte[publicKeyLength];
            byte[] privateKey = new byte[this._privateKeyLengths[eccCurveId]];
            int eccCurve = this._eccCurves[eccCurveId];
            EncryptionUtilities.createKeyPair(eccCurve, publicKey, privateKey);
            symmetricKey = EncryptionUtilities.calculateKey(eccCurve, (byte[])this._devicePublicKeys[eccCurveId], privateKey);
            net.rim.vm.Memory.setPlaintext(privateKey);
            net.rim.vm.Memory.setPlaintext(symmetricKey);
         }
      }

      do {
         int blockByteLength = inputByteLength > 4096 ? 4096 : inputByteLength;
         int header = initialHeader;
         int headerByteOffset = outputByteOffset;
         outputByteOffset += 2;
         int headerByteLength;
         int encodedByteLength;
         if (!encrypt) {
            if (compress) {
               int compressedByteLength = CompressUtilities.compressBlock(input, inputByteOffset, blockByteLength, output, outputByteOffset, false);
               header |= compressedByteLength < blockByteLength ? 1 : 0;
               encodedByteLength = compressedByteLength;
               headerByteLength = blockByteLength;
            } else {
               copyBytes(input, inputByteOffset, output, outputByteOffset, blockByteLength, false);
               encodedByteLength = blockByteLength;
               headerByteLength = blockByteLength;
            }
         } else {
            if (publicKey != null) {
               setBytes(output, outputByteOffset, eccCurveId, 1);
               int publicKeyLength = this._publicKeyLengths[eccCurveId];
               copyBytes(publicKey, 0, output, ++outputByteOffset, publicKeyLength, false);
               publicKey = null;
               outputByteOffset += publicKeyLength;
               header |= 2;
            }

            if (compress) {
               char[] ceBuffer = WeakReferenceUtilities.getCharArray(this._ceBufferWR, 2048);
               net.rim.vm.Memory.setPlaintext(ceBuffer);
               int compressedByteLength = CompressUtilities.compressBlock(input, inputByteOffset, blockByteLength, ceBuffer, 0, true);
               RandomSource.add(ceBuffer);
               header |= compressedByteLength < blockByteLength ? 1 : 0;
               encodedByteLength = EncryptionUtilities.encrypt(symmetricKey, ceBuffer, 0, compressedByteLength, output, outputByteOffset);
               headerByteLength = compressedByteLength;
            } else {
               encodedByteLength = EncryptionUtilities.encrypt(symmetricKey, input, inputByteOffset, blockByteLength, output, outputByteOffset);
               headerByteLength = blockByteLength;
            }
         }

         header |= headerByteLength << 4;
         setBytes(output, headerByteOffset, header, 2);
         inputByteOffset += blockByteLength;
         inputByteLength -= blockByteLength;
         outputByteOffset += encodedByteLength;
      } while (inputByteLength > 0);

      return outputByteOffset;
   }

   public static final Object decode(Object content) {
      return decode(content, false);
   }

   public static final String decodeString(Object content) {
      return (String)decode(content, false);
   }

   public static final String decodeString(Object content, boolean firstBlockOnly) {
      return (String)decode(content, firstBlockOnly);
   }

   public static final byte[] decodeByteArray(Object content) {
      return (byte[])decode(content, false);
   }

   public static final byte[] decodeByteArray(Object content, boolean firstBlockOnly) {
      return (byte[])decode(content, firstBlockOnly);
   }

   public static final Object decode(Object content, boolean firstBlockOnly) {
      if (content instanceof char[]) {
         return _instance.decode((char[])content, firstBlockOnly, false);
      } else {
         return !(content instanceof PersistentContent$CharArrayWrapper) ? content : ((PersistentContent$CharArrayWrapper)content).getArray();
      }
   }

   final synchronized Object decode(char[] input, boolean firstBlockOnly, boolean keepPlaintextInRAM) {
      Object output = this._objectCache.get(input, firstBlockOnly);
      if (output != null) {
         return output;
      }

      char[] originalInput = input;
      input = read(input);
      int header = getMasterHeader(input);
      boolean compress = getFlag(header, 1);
      boolean encrypt = getFlag(header, 2);
      boolean string = getFlag(header, 4);
      boolean bytes = getFlag(header, 8);
      if (encrypt && this._devicePrivateKeys == null) {
         throw new IllegalStateException();
      }

      int byteLength = header >> 4;
      if (firstBlockOnly) {
         int blockHeader = getBytes(input, 0, 2);
         bytes = getFlag(blockHeader, 8);
         if (!compress || !encrypt) {
            byteLength = blockHeader >> 4;
            if (byteLength == 0) {
               byteLength = 4096;
            }
         } else if (byteLength > 4096) {
            byteLength = 4096;
         }
      }

      if (bytes) {
         if (keepPlaintextInRAM) {
            output = net.rim.vm.Memory.allocRAMOnlyBytes(byteLength);
            if (output == null) {
               throw new OutOfMemoryError();
            }
         } else {
            output = new byte[byteLength];
         }
      } else {
         output = new char[byteLength >> 1];
      }

      if (encrypt != this._encrypt || compress != this._compress) {
         this._numRecodes++;
      }

      this.decodeBlocks(input, compress, encrypt, !bytes, output, byteLength, firstBlockOnly);
      if (string) {
         CompressUtilities.convertToString(output);
      }

      if (encrypt && this._encrypt) {
         this.setPlaintext(output);
      }

      if (!keepPlaintextInRAM) {
         this._objectCache.put(originalInput, firstBlockOnly, output);
      }

      return output;
   }

   private final synchronized void decodeBlocks(
      char[] input, boolean compress, boolean encrypt, boolean masterConvertBytesToChars, Object output, int outputByteLength, boolean firstBlockOnly
   ) {
      int inputByteOffset = 0;
      int outputByteOffset = 0;
      byte[] symmetricKey = null;

      do {
         int blockHeader = getBytes(input, inputByteOffset, 2);
         inputByteOffset += 2;
         boolean convertBytesToChars = masterConvertBytesToChars && getFlag(blockHeader, 8);
         int headerByteLength = blockHeader >> 4;
         if (headerByteLength == 0) {
            headerByteLength = 4096;
         }

         int ciphertextByteLength;
         int decompressedByteLength;
         if (!encrypt) {
            if (compress && getFlag(blockHeader, 1)) {
               decompressedByteLength = headerByteLength;
               ciphertextByteLength = CompressUtilities.decompressBlock(
                  input, inputByteOffset, output, outputByteOffset, decompressedByteLength, convertBytesToChars
               );
               if (convertBytesToChars) {
                  decompressedByteLength <<= 1;
               }
            } else {
               ciphertextByteLength = headerByteLength;
               decompressedByteLength = headerByteLength;
               copyBytes(input, inputByteOffset, output, outputByteOffset, ciphertextByteLength, convertBytesToChars);
               if (convertBytesToChars) {
                  decompressedByteLength <<= 1;
               }
            }
         } else {
            if (getFlag(blockHeader, 2)) {
               int hashOffset = inputByteOffset;
               symmetricKey = this._symmetricKeyCache.get(input, hashOffset);
               if (symmetricKey == null) {
                  this._numPKRecodes++;
                  int eccCurveId = getBytes(input, inputByteOffset++, 1);
                  int publicKeyLength = this._publicKeyLengths[eccCurveId];
                  byte[] publicKey = new byte[publicKeyLength];
                  copyBytes(input, inputByteOffset, publicKey, 0, publicKeyLength, false);
                  inputByteOffset += publicKeyLength;
                  symmetricKey = EncryptionUtilities.calculateKey(this._eccCurves[eccCurveId], publicKey, (byte[])this._devicePrivateKeys[eccCurveId]);
                  net.rim.vm.Memory.setPlaintext(symmetricKey);
                  this._symmetricKeyCache.put(input, hashOffset, symmetricKey);
               }
            } else if (getFlag(blockHeader, 4)) {
               symmetricKey = this._deviceSymmetricKey;
            }

            int plaintextByteLength = headerByteLength;
            ciphertextByteLength = EncryptionUtilities.getCiphertextLength(plaintextByteLength);
            if (compress && getFlag(blockHeader, 1)) {
               char[] ceBuffer = WeakReferenceUtilities.getCharArray(this._ceBufferWR, 2048);
               net.rim.vm.Memory.setPlaintext(ceBuffer);
               EncryptionUtilities.decrypt(symmetricKey, input, inputByteOffset, ciphertextByteLength, ceBuffer, 0, false);
               decompressedByteLength = CompressUtilities.decompressBlock(ceBuffer, 0, output, outputByteOffset, -1, convertBytesToChars);
            } else {
               decompressedByteLength = EncryptionUtilities.decrypt(
                  symmetricKey, input, inputByteOffset, ciphertextByteLength, output, outputByteOffset, convertBytesToChars
               );
            }
         }

         outputByteOffset += decompressedByteLength;
         inputByteOffset += ciphertextByteLength;
         if (firstBlockOnly) {
            Array.resize(output, masterConvertBytesToChars ? decompressedByteLength >> 1 : decompressedByteLength);
            return;
         }
      } while (outputByteOffset < outputByteLength);
   }

   private static final int createMasterHeader(boolean compress, boolean encrypt, boolean string, boolean bytes, int inputByteLength) {
      int header = inputByteLength << 4;
      if (compress) {
         header |= 1;
      }

      if (encrypt) {
         header |= 2;
      }

      if (string) {
         header |= 4;
      }

      if (bytes) {
         header |= 8;
      }

      return header;
   }

   private static final int getMasterHeader(char[] output) {
      int outputByteOffset = output.length << 1;
      int header = getBytes(output, outputByteOffset - 4, 4);
      int var3;
      return (header & 0xFF) == 0 ? (var3 = header >>> 8) : header & 16777215;
   }

   private static final int getOutputByteOffset(char[] output) {
      int outputByteOffset = output.length << 1;
      if (getBytes(output, outputByteOffset - 1, 1) == 0) {
         outputByteOffset--;
      }

      return outputByteOffset - 3;
   }

   private static final boolean getFlag(int header, int flag) {
      return (header & flag) != 0;
   }

   static final native void copyBytes(Object var0, int var1, Object var2, int var3, int var4, boolean var5);

   static final native void setBytes(Object var0, int var1, int var2, int var3);

   static final native int getBytes(Object var0, int var1, int var2);

   public static final boolean isCompressed(Object encoding) {
      if (!(encoding instanceof char[])) {
         return false;
      }

      int masterHeader = getMasterHeader((char[])encoding);
      return getFlag(masterHeader, 1);
   }

   public static final boolean isEncrypted(Object encoding) {
      if (!(encoding instanceof char[])) {
         return false;
      }

      int masterHeader = getMasterHeader((char[])encoding);
      return getFlag(masterHeader, 2);
   }

   public static final boolean isString(Object encoding) {
      if (encoding instanceof String) {
         return true;
      }

      if (!(encoding instanceof char[])) {
         return false;
      }

      encoding = read((char[])encoding);
      int header = getMasterHeader((char[])encoding);
      return getFlag(header, 4);
   }

   public static final boolean isByteArray(Object encoding) {
      if (encoding instanceof byte[]) {
         return true;
      }

      if (!(encoding instanceof char[])) {
         return false;
      }

      encoding = read((char[])encoding);
      int header = getMasterHeader((char[])encoding);
      boolean string = getFlag(header, 4);
      return !string;
   }

   public static final int getLength(Object encoding) {
      if (!(encoding instanceof String)) {
         if (!(encoding instanceof byte[])) {
            if (!(encoding instanceof char[])) {
               throw new IllegalArgumentException();
            }

            Object var4 = read((char[])encoding);
            int header = getMasterHeader(var4);
            boolean bytes = getFlag(header, 8);
            int byteLength = header >> 4;
            if (!bytes) {
               byteLength >>= 1;
            }

            return byteLength;
         } else {
            return ((byte[])encoding).length;
         }
      } else {
         return ((String)encoding).length();
      }
   }

   public static final boolean checkEncoding(Object encoding) {
      return checkEncoding(encoding, true, true);
   }

   public static final boolean checkEncoding(Object encoding, boolean compress, boolean encrypt) {
      return _instance.checkEncoding2(encoding, compress, encrypt);
   }

   private final synchronized boolean checkEncoding2(Object encoding, boolean compress, boolean encrypt) {
      compress &= _instance._compress;
      encrypt &= _instance._encrypt;
      if (!(encoding instanceof String)) {
         if (!(encoding instanceof byte[])) {
            if (!(encoding instanceof char[])) {
               return true;
            }

            Object var12 = read((char[])encoding);
            int masterHeader = getMasterHeader(var12);
            boolean encodingEncrypt = getFlag(masterHeader, 2);
            boolean encodingCompress = getFlag(masterHeader, 1);
            if (encrypt == encodingEncrypt && this._compress == encodingCompress) {
               if (encrypt && this._devicePrivateKeys != null) {
                  char[] input = var12;
                  int inputByteOffset = 0;
                  int inputByteLength = (input.length << 1) - 4 - 2;

                  do {
                     int blockHeader = getBytes(input, inputByteOffset, 2);
                     inputByteOffset += 2;
                     if (getFlag(blockHeader, 2)) {
                        return false;
                     }

                     int plaintextByteLength = blockHeader >> 4;
                     if (plaintextByteLength == 0) {
                        plaintextByteLength = 4096;
                     }

                     inputByteOffset += EncryptionUtilities.getCiphertextLength(plaintextByteLength);
                  } while (inputByteOffset < inputByteLength);
               }

               return true;
            } else {
               if (encrypt && !encodingEncrypt) {
                  this.setPlaintext(var12);
               }

               return encodingEncrypt && this._devicePrivateKeys == null;
            }
         } else {
            int length = ((byte[])encoding).length;
            if (length == 0) {
               return true;
            }

            if (encrypt) {
               this.setPlaintext(encoding);
            }

            return !encrypt && (!compress || length < 32);
         }
      } else {
         int length = ((String)encoding).length();
         if (length == 0) {
            return true;
         }

         if (encrypt) {
            this.setPlaintext(encoding);
         }

         return !encrypt && (!compress || length < 32);
      }
   }

   public static final Object reEncode(Object encoding) {
      return reEncode(encoding, true, true);
   }

   public static final Object reEncode(Object encoding, boolean compress, boolean encrypt) {
      int firstBlockByteLength = 0;
      Object originalEncoding = encoding;
      if (encoding instanceof char[]) {
         Object var10 = read((char[])encoding);
         int blockHeader = getBytes(var10, 0, 2);
         firstBlockByteLength = blockHeader >> 4;
         boolean decrypting = !encrypt && getFlag(getMasterHeader(var10), 2);

         try {
            encoding = decode(originalEncoding, false);
            if (decrypting) {
               net.rim.vm.Memory.clearPlaintext(encoding);
            }
         } catch (IllegalStateException e) {
            return originalEncoding;
         }
      }

      Object newEncoding = null;
      if (!(encoding instanceof String)) {
         if (!(encoding instanceof byte[])) {
            newEncoding = encoding;
         } else {
            byte[] bytes = (byte[])encoding;
            int secondBlockByteLength = bytes.length - firstBlockByteLength;
            newEncoding = encode(bytes, 0, firstBlockByteLength, compress, encrypt);
            if (secondBlockByteLength > 0) {
               newEncoding = encodeAndAppend(bytes, firstBlockByteLength, secondBlockByteLength, compress, encrypt, newEncoding);
            }
         }
      } else {
         String string = (String)encoding;
         int firstBlockCharLength = firstBlockByteLength / StringUtilities.getCharacterSize(string);
         int secondBlockCharLength = string.length() - firstBlockCharLength;
         newEncoding = encode(string, 0, firstBlockCharLength, compress, encrypt);
         if (secondBlockCharLength > 0) {
            newEncoding = encodeAndAppend(string, firstBlockCharLength, secondBlockCharLength, compress, encrypt, newEncoding);
         }
      }

      return newEncoding;
   }

   public static final byte[] convertEncodingToByteArray(Object encoding) {
      byte type;
      int byteLength;
      if (encoding instanceof byte[]) {
         type = 0;
         byteLength = ((byte[])encoding).length;
      } else if (!(encoding instanceof String)) {
         if (!(encoding instanceof char[])) {
            if (encoding == null) {
               return _instance._emptyByteArray;
            }

            throw new IllegalArgumentException();
         }

         encoding = read((char[])encoding);
         type = 3;
         byteLength = ((char[])encoding).length * 2;
      } else {
         String string = (String)encoding;
         type = (byte)StringUtilities.getCharacterSize(string);
         byteLength = string.length() * type;
      }

      byte[] array = new byte[1 + byteLength];
      array[0] = type;
      copyBytes(encoding, 0, array, 1, byteLength, false);
      return array;
   }

   public static final Object convertByteArrayToEncoding(byte[] array) {
      if (array != null && array.length != 0) {
         int byteLength = array.length - 1;
         int type = array[0];
         switch (type) {
            case -1:
               throw new IllegalArgumentException();
            case 0:
            case 1:
            default:
               byte[] encoding = new byte[byteLength];
               copyBytes(array, 1, encoding, 0, byteLength, false);
               if (type == 1) {
                  CompressUtilities.convertToString(encoding);
               }

               return encoding;
            case 2:
            case 3:
               if ((byteLength & 1) == 1) {
                  throw new IllegalArgumentException();
               } else {
                  char[] encoding = new char[byteLength / 2];
                  copyBytes(array, 1, encoding, 0, byteLength, false);
                  if (type == 2) {
                     CompressUtilities.convertToString(encoding);
                  }

                  return encoding;
               }
         }
      } else {
         return null;
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (PersistentContent)ar.getOrWaitFor(-8690596288514859193L);
      if (_instance == null) {
         _instance = new PersistentContent();
         ar.put(-8690596288514859193L, _instance);
         EventLogger.register(-2564487553114485073L, "PersistentContent", 2);
         EventLogger.logEvent(-2564487553114485073L, ("PersistentContent: Encryption is " + (isEncryptionEnabled() ? "enabled" : "disabled")).getBytes(), 0);
         Object listener = ar.get(-3768474813176659123L);
         if (listener instanceof PersistentContentListener) {
            PersistentContentListener pcListener = (PersistentContentListener)listener;
            addListener(pcListener, false);
            pcListener.persistentContentModeChanged(0);
         }

         addListener(new PersistentContent$NvStorePersistentContentListener(null), false);
         listener = ar.get(4119503239558518103L);
         if (listener instanceof PersistentContentListener) {
            PersistentContentListener pcListener = (PersistentContentListener)listener;
            addListener(pcListener, false);
         }

         _instance.findListeners();
      }
   }
}
