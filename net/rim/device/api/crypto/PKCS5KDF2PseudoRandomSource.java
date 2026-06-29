package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class PKCS5KDF2PseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private HMAC _hmac;
   private byte[] _salt;
   private int _digestLength;
   private int _iterationCount;
   private int _currentBlock;
   private byte[] _digestBuffer;
   private byte[] _outputBuffer;
   private byte[] _previousOutputBuffer;
   private byte[] _intToByteArray = new byte[4];
   private int _outputOffset;

   public PKCS5KDF2PseudoRandomSource(byte[] password, byte[] salt, int iterationCount) {
      this(password, 0, password == null ? 0 : password.length, salt, iterationCount, (Digest)(new Object()));
   }

   public PKCS5KDF2PseudoRandomSource(byte[] password, int offset, int length, byte[] salt, int iterationCount) {
      this(password, offset, length, salt, iterationCount, (Digest)(new Object()));
   }

   public PKCS5KDF2PseudoRandomSource(byte[] param1, int param2, int param3, byte[] param4, int param5, Digest param6) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial net/rim/device/api/crypto/AbstractPseudoRandomSource.<init> ()V
      // 04: aload 0
      // 05: bipush 4
      // 07: newarray 8
      // 09: putfield net/rim/device/api/crypto/PKCS5KDF2PseudoRandomSource._intToByteArray [B
      // 0c: aload 1
      // 0d: ifnull 20
      // 10: iload 2
      // 11: iflt 20
      // 14: iload 3
      // 15: iflt 20
      // 18: aload 1
      // 19: arraylength
      // 1a: iload 3
      // 1b: isub
      // 1c: iload 2
      // 1d: if_icmpge 28
      // 20: new java/lang/Object
      // 23: dup
      // 24: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 27: athrow
      // 28: aload 0
      // 29: new java/lang/Object
      // 2c: dup
      // 2d: aload 1
      // 2e: iload 2
      // 2f: iload 3
      // 30: invokespecial net/rim/device/api/crypto/HMACKey.<init> ([BII)V
      // 33: aload 4
      // 35: iload 5
      // 37: aload 6
      // 39: invokespecial net/rim/device/api/crypto/PKCS5KDF2PseudoRandomSource.initialize (Lnet/rim/device/api/crypto/HMACKey;[BILnet/rim/device/api/crypto/Digest;)V
      // 3c: return
      // 3d: astore 7
      // 3f: new java/lang/Object
      // 42: dup
      // 43: aload 7
      // 45: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 48: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 4b: athrow
      // 4c: astore 7
      // 4e: new java/lang/Object
      // 51: dup
      // 52: aload 7
      // 54: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 57: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 5a: athrow
      // try (22 -> 33): 34 null
      // try (22 -> 33): 41 null
   }

   public PKCS5KDF2PseudoRandomSource(HMACKey key, byte[] salt, int iterationCount, Digest digest) {
      this.initialize(key, salt, iterationCount, digest);
   }

   private final void initialize(HMACKey key, byte[] salt, int iterationCount, Digest digest) {
      if (key != null && salt != null && iterationCount >= 1 && digest != null) {
         this._hmac = (HMAC)(new Object(key, digest));
         this._digestLength = this._hmac.getLength();
         this._salt = Arrays.copy(salt);
         this._iterationCount = iterationCount;
         this._outputOffset = this._digestLength;
         this._currentBlock = 0;
         this._digestBuffer = new byte[this._digestLength];
         this._outputBuffer = new byte[this._digestLength];
         this._previousOutputBuffer = new byte[this._digestLength];
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "PKCS5KDF2";
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         try {
            while (length > 0) {
               if (this._outputOffset == this._digestLength) {
                  do {
                     this._hmac.update(this._salt);
                     this._hmac.update(intToBigEndianByteArray(++this._currentBlock, this._intToByteArray));
                     this._hmac.getMAC(this._digestBuffer, 0);
                     System.arraycopy(this._digestBuffer, 0, this._outputBuffer, 0, this._digestLength);
                     int counter = this._iterationCount;

                     while (--counter > 0) {
                        this._hmac.update(this._digestBuffer);
                        this._hmac.getMAC(this._digestBuffer, 0);

                        for (int j = 0; j < this._digestLength; j++) {
                           this._outputBuffer[j] = (byte)(this._outputBuffer[j] ^ this._digestBuffer[j]);
                        }
                     }

                     this._outputOffset = 0;
                  } while (Arrays.equals(this._outputBuffer, this._previousOutputBuffer));

                  System.arraycopy(this._outputBuffer, 0, this._previousOutputBuffer, 0, this._digestLength);
               }

               int blockLength = Math.min(this._digestLength - this._outputOffset, length);
               length -= blockLength;

               while (blockLength-- > 0) {
                  buffer[offset++] ^= this._outputBuffer[this._outputOffset++];
               }
            }
         } catch (Throwable var7) {
            throw new Object(e.toString());
         }
      } else {
         throw new Object();
      }
   }

   private static final byte[] intToBigEndianByteArray(int i, byte[] output) {
      output[0] = (byte)(i >> 24);
      output[1] = (byte)(i >> 16);
      output[2] = (byte)(i >> 8);
      output[3] = (byte)i;
      return output;
   }

   @Override
   public final int getAvailable() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final int getMaxAvailable() {
      return Integer.MAX_VALUE;
   }

   public static final void selfTest() {
      byte[] result = new byte[]{
         87, -97, -95, 126, 86, 119, -124, -91, -18, 62, -72, 61, 11, 39, 8, 73, 49, -77, 23, -9, 10, -28, -118, 3, -62, 61, 55, 87, -49, -93, -93, -12
      };

      try {
         byte[] key = Arrays.copy(SelfTestData.RANDOM_DATA);
         PKCS5KDF2PseudoRandomSource source = new PKCS5KDF2PseudoRandomSource(key, key, 1);
         byte[] data = source.getBytes(32);
         if (Arrays.equals(data, result)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      long ID_TEST_PRS_PKCS5KDF2 = -492745862243622127L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      Object instance = appRegistry.getOrWaitFor(ID_TEST_PRS_PKCS5KDF2);
      if (instance == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_PKCS5KDF2, appRegistry);
      }
   }
}
