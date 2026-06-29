package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class CBCMAC extends AbstractMAC implements MAC {
   private BlockEncryptorEngine _encryptorEngine;
   private BlockDecryptorEngine _decryptorEngine;
   private byte[] _cbcBuffer;
   private int _cbcOffset;
   private int _blockLength;
   private static final long ID_TEST_CBCMAC;

   public CBCMAC(BlockEncryptorEngine encryptorEngine) {
      this(encryptorEngine, null);
   }

   public CBCMAC(BlockEncryptorEngine encryptorEngine, BlockDecryptorEngine decryptorEngine) {
      if (encryptorEngine != null && (decryptorEngine == null || encryptorEngine.getBlockLength() == decryptorEngine.getBlockLength())) {
         this._blockLength = encryptorEngine.getBlockLength();
         this._encryptorEngine = encryptorEngine;
         this._decryptorEngine = decryptorEngine;
         this._cbcBuffer = new byte[this._blockLength];
         this.reset();
      } else {
         throw new Object();
      }
   }

   public CBCMAC(SymmetricKey encryptorKey) {
      this(encryptorKey, null);
   }

   public CBCMAC(SymmetricKey encryptorKey, SymmetricKey decryptorKey) {
      this(EncryptorFactory.getBlockEncryptorEngine(encryptorKey), decryptorKey == null ? null : DecryptorFactory.getBlockDecryptorEngine(decryptorKey));
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object("CBCMAC/"))).append(this._encryptorEngine.getAlgorithm()).toString();
   }

   @Override
   public final void reset() {
      this._cbcOffset = 0;
      Arrays.fill(this._cbcBuffer, (byte)0);
   }

   @Override
   public final int getLength() {
      return this._blockLength;
   }

   @Override
   public final void update(byte[] data, int offset, int length) {
      if (data != null && data.length - length >= offset && offset >= 0 && length >= 0) {
         while (--length >= 0) {
            this._cbcBuffer[this._cbcOffset++] ^= data[offset++];
            if (this._cbcOffset == this._blockLength) {
               this._encryptorEngine.encrypt(this._cbcBuffer, 0, this._cbcBuffer, 0);
               this._cbcOffset = 0;
            }
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final int getMAC(byte[] buffer, int offset, boolean reset) {
      if (buffer != null && offset >= 0 && buffer.length - this._blockLength >= offset) {
         int cbcOffset = this._cbcOffset;
         byte[] cbcBuffer;
         if (reset) {
            cbcBuffer = this._cbcBuffer;
         } else {
            cbcBuffer = Arrays.copy(this._cbcBuffer);
         }

         if (cbcOffset > 0) {
            this._encryptorEngine.encrypt(cbcBuffer, 0, cbcBuffer, 0);
         }

         if (this._decryptorEngine != null) {
            this._decryptorEngine.decrypt(cbcBuffer, 0, cbcBuffer, 0);
            this._encryptorEngine.encrypt(cbcBuffer, 0, cbcBuffer, 0);
         }

         System.arraycopy(cbcBuffer, 0, buffer, offset, this._blockLength);
         if (reset) {
            this.reset();
         }

         return this._blockLength;
      } else {
         throw new Object();
      }
   }

   public static final void selfTest() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 8
      // 002: newarray 8
      // 004: dup
      // 005: bipush 0
      // 006: bipush 1
      // 007: bastore
      // 008: dup
      // 009: bipush 1
      // 00a: bipush 35
      // 00c: bastore
      // 00d: dup
      // 00e: bipush 2
      // 00f: bipush 69
      // 011: bastore
      // 012: dup
      // 013: bipush 3
      // 014: bipush 103
      // 016: bastore
      // 017: dup
      // 018: bipush 4
      // 019: bipush -119
      // 01b: bastore
      // 01c: dup
      // 01d: bipush 5
      // 01e: bipush -85
      // 020: bastore
      // 021: dup
      // 022: bipush 6
      // 024: bipush -51
      // 026: bastore
      // 027: dup
      // 028: bipush 7
      // 02a: bipush -17
      // 02c: bastore
      // 02d: astore 0
      // 02e: bipush 8
      // 030: newarray 8
      // 032: dup
      // 033: bipush 0
      // 034: bipush -2
      // 036: bastore
      // 037: dup
      // 038: bipush 1
      // 039: bipush -36
      // 03b: bastore
      // 03c: dup
      // 03d: bipush 2
      // 03e: bipush -70
      // 040: bastore
      // 041: dup
      // 042: bipush 3
      // 043: bipush -104
      // 045: bastore
      // 046: dup
      // 047: bipush 4
      // 048: bipush 118
      // 04a: bastore
      // 04b: dup
      // 04c: bipush 5
      // 04d: bipush 84
      // 04f: bastore
      // 050: dup
      // 051: bipush 6
      // 053: bipush 50
      // 055: bastore
      // 056: dup
      // 057: bipush 7
      // 059: bipush 16
      // 05b: bastore
      // 05c: astore 1
      // 05d: ldc_w "7654321 Now is the time for "
      // 060: invokevirtual java/lang/String.getBytes ()[B
      // 063: astore 2
      // 064: bipush 8
      // 066: newarray 8
      // 068: dup
      // 069: bipush 0
      // 06a: bipush 68
      // 06c: bastore
      // 06d: dup
      // 06e: bipush 1
      // 06f: bipush -26
      // 071: bastore
      // 072: dup
      // 073: bipush 2
      // 074: bipush 105
      // 076: bastore
      // 077: dup
      // 078: bipush 3
      // 079: bipush 88
      // 07b: bastore
      // 07c: dup
      // 07d: bipush 4
      // 07e: bipush -98
      // 080: bastore
      // 081: dup
      // 082: bipush 5
      // 083: bipush -87
      // 085: bastore
      // 086: dup
      // 087: bipush 6
      // 089: bipush 119
      // 08b: bastore
      // 08c: dup
      // 08d: bipush 7
      // 08f: bipush 21
      // 091: bastore
      // 092: astore 3
      // 093: bipush 8
      // 095: newarray 8
      // 097: dup
      // 098: bipush 0
      // 099: bipush 18
      // 09b: bastore
      // 09c: dup
      // 09d: bipush 1
      // 09e: bipush 52
      // 0a0: bastore
      // 0a1: dup
      // 0a2: bipush 2
      // 0a3: bipush 86
      // 0a5: bastore
      // 0a6: dup
      // 0a7: bipush 3
      // 0a8: bipush 120
      // 0aa: bastore
      // 0ab: dup
      // 0ac: bipush 4
      // 0ad: bipush -112
      // 0af: bastore
      // 0b0: dup
      // 0b1: bipush 5
      // 0b2: bipush -85
      // 0b4: bastore
      // 0b5: dup
      // 0b6: bipush 6
      // 0b8: bipush -51
      // 0ba: bastore
      // 0bb: dup
      // 0bc: bipush 7
      // 0be: bipush -17
      // 0c0: bastore
      // 0c1: astore 4
      // 0c3: new java/lang/Object
      // 0c6: dup
      // 0c7: aload 0
      // 0c8: invokespecial net/rim/device/api/crypto/DESKey.<init> ([B)V
      // 0cb: astore 5
      // 0cd: new java/lang/Object
      // 0d0: dup
      // 0d1: aload 5
      // 0d3: invokespecial net/rim/device/api/crypto/DESEncryptorEngine.<init> (Lnet/rim/device/api/crypto/DESKey;)V
      // 0d6: astore 6
      // 0d8: new java/lang/Object
      // 0db: dup
      // 0dc: aload 1
      // 0dd: invokespecial net/rim/device/api/crypto/DESKey.<init> ([B)V
      // 0e0: astore 7
      // 0e2: new java/lang/Object
      // 0e5: dup
      // 0e6: aload 7
      // 0e8: invokespecial net/rim/device/api/crypto/DESDecryptorEngine.<init> (Lnet/rim/device/api/crypto/DESKey;)V
      // 0eb: astore 8
      // 0ed: new net/rim/device/api/crypto/CBCMAC
      // 0f0: dup
      // 0f1: aload 6
      // 0f3: aload 8
      // 0f5: invokespecial net/rim/device/api/crypto/CBCMAC.<init> (Lnet/rim/device/api/crypto/BlockEncryptorEngine;Lnet/rim/device/api/crypto/BlockDecryptorEngine;)V
      // 0f8: astore 9
      // 0fa: bipush 0
      // 0fb: istore 10
      // 0fd: iload 10
      // 0ff: aload 4
      // 101: arraylength
      // 102: if_icmpge 118
      // 105: aload 2
      // 106: iload 10
      // 108: dup2
      // 109: baload
      // 10a: aload 4
      // 10c: iload 10
      // 10e: baload
      // 10f: ixor
      // 110: i2b
      // 111: bastore
      // 112: iinc 10 1
      // 115: goto 0fd
      // 118: aload 9
      // 11a: aload 2
      // 11b: invokevirtual net/rim/device/api/crypto/AbstractMAC.update ([B)V
      // 11e: aload 9
      // 120: aload 3
      // 121: invokevirtual net/rim/device/api/crypto/AbstractMAC.checkMAC ([B)Z
      // 124: ifeq 12f
      // 127: return
      // 128: astore 5
      // 12a: goto 12f
      // 12d: astore 5
      // 12f: new java/lang/Object
      // 132: dup
      // 133: invokespecial net/rim/device/api/crypto/CryptoSelfTestError.<init> ()V
      // 136: athrow
      // try (143 -> 194): 195 null
      // try (143 -> 194): 197 null
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(7820450527937782517L) == null) {
         selfTest();
         appRegistry.put(7820450527937782517L, appRegistry);
      }
   }
}
