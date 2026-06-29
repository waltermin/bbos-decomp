package net.rim.device.api.crypto.cms;

import java.io.InputStream;
import net.rim.device.api.crypto.AESDecryptorEngine;
import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.BlockDecryptorEngine;
import net.rim.device.api.crypto.CAST128Key;
import net.rim.device.api.crypto.CBCDecryptorEngine;
import net.rim.device.api.crypto.DecryptorFactory;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.RC2Key;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.TripleDESKey;
import net.rim.device.api.util.Arrays;

final class CMSKeyUnWrap {
   private static final byte[] WRAP_IV = new byte[]{74, -35, -94, 44, 121, -24, 33, 5};
   private static final byte[] AES_IV = new byte[]{-90, -90, -90, -90, -90, -90, -90, -90};

   private CMSKeyUnWrap() {
   }

   static final byte[] TripleDESKeyUnWrap(TripleDESKey kekKey, byte[] wrappedKey) {
      try {
         int WRAPPEDLENGTH = 40;
         int IVLENGTH = 8;
         int TEMP1LENGTH = 32;
         int CEKLENGTH = 24;
         int ICVLENGTH = 8;
         if (wrappedKey.length != WRAPPEDLENGTH) {
            return null;
         }

         BlockDecryptor decryptor = (BlockDecryptor)(new Object(
            (BlockDecryptorEngine)(new Object((BlockDecryptorEngine)(new Object(kekKey)), (InitializationVector)(new Object(WRAP_IV)))),
            (InputStream)(new Object(wrappedKey))
         ));
         byte[] TEMP3 = new byte[WRAPPEDLENGTH];
         decryptor.read(TEMP3);
         byte[] TEMP2 = new byte[WRAPPEDLENGTH];

         for (int i = 0; i < WRAPPEDLENGTH; i++) {
            TEMP2[i] = TEMP3[WRAPPEDLENGTH - 1 - i];
         }

         byte[] IV = new byte[IVLENGTH];
         byte[] TEMP1 = new byte[TEMP1LENGTH];
         System.arraycopy(TEMP2, 0, IV, 0, IVLENGTH);
         System.arraycopy(TEMP2, IVLENGTH, TEMP1, 0, TEMP1LENGTH);
         BlockDecryptor decryptor2 = (BlockDecryptor)(new Object(
            (BlockDecryptorEngine)(new Object((BlockDecryptorEngine)(new Object(kekKey)), (InitializationVector)(new Object(IV)))),
            (InputStream)(new Object(TEMP1))
         ));
         byte[] CEKICV = new byte[CEKLENGTH + ICVLENGTH];
         decryptor2.read(CEKICV);
         byte[] CEK = new byte[CEKLENGTH];
         byte[] ICV = new byte[ICVLENGTH];
         System.arraycopy(CEKICV, 0, CEK, 0, CEKLENGTH);
         System.arraycopy(CEKICV, CEKLENGTH, ICV, 0, ICVLENGTH);
         SHA1Digest digest = (SHA1Digest)(new Object());
         digest.update(CEK);
         byte[] checkSum = digest.getDigest();
         if (!Arrays.equals(ICV, 0, checkSum, 0, ICVLENGTH)) {
            return null;
         }

         TripleDESKey contentKey = (TripleDESKey)(new Object(CEK));
         return !Arrays.equals(CEK, 0, contentKey.getData(), 0, CEKLENGTH) ? null : CEK;
      } finally {
         throw new Object();
      }
   }

   static final byte[] RC2KeyUnWrap(RC2Key param0, byte[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 1
      // 02: invokestatic net/rim/device/api/crypto/cms/CMSKeyUnWrap.GenericKeyUnWrap (Lnet/rim/device/api/crypto/SymmetricKey;[B)[B
      // 05: areturn
      // 06: astore 2
      // 07: aload 2
      // 08: athrow
      // 09: astore 2
      // 0a: aload 2
      // 0b: athrow
      // 0c: astore 2
      // 0d: new java/lang/Object
      // 10: dup
      // 11: aload 2
      // 12: invokevirtual net/rim/device/api/crypto/CryptoException.toString ()Ljava/lang/String;
      // 15: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> (Ljava/lang/String;)V
      // 18: athrow
      // try (0 -> 3): 4 null
      // try (0 -> 3): 7 null
      // try (0 -> 3): 10 null
   }

   static final byte[] CASTKeyUnWrap(CAST128Key param0, byte[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 1
      // 02: invokestatic net/rim/device/api/crypto/cms/CMSKeyUnWrap.GenericKeyUnWrap (Lnet/rim/device/api/crypto/SymmetricKey;[B)[B
      // 05: areturn
      // 06: astore 2
      // 07: aload 2
      // 08: athrow
      // 09: astore 2
      // 0a: aload 2
      // 0b: athrow
      // 0c: astore 2
      // 0d: new java/lang/Object
      // 10: dup
      // 11: aload 2
      // 12: invokevirtual net/rim/device/api/crypto/CryptoException.toString ()Ljava/lang/String;
      // 15: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> (Ljava/lang/String;)V
      // 18: athrow
      // try (0 -> 3): 4 null
      // try (0 -> 3): 7 null
      // try (0 -> 3): 10 null
   }

   static final byte[] GenericKeyUnWrap(SymmetricKey kekKey, byte[] wrappedKey) {
      try {
         int IVLENGTH = 8;
         int ICVLENGTH = 8;
         if (wrappedKey.length % 8 != 0) {
            return null;
         }

         int WRAPPEDLENGTH = wrappedKey.length;
         int TEMP1LENGTH = WRAPPEDLENGTH - IVLENGTH;
         BlockDecryptor decryptor = (BlockDecryptor)(new Object(
            (BlockDecryptorEngine)(new Object(DecryptorFactory.getBlockDecryptorEngine(kekKey), (InitializationVector)(new Object(WRAP_IV)))),
            (InputStream)(new Object(wrappedKey))
         ));
         byte[] TEMP3 = new byte[WRAPPEDLENGTH];
         decryptor.read(TEMP3);
         byte[] TEMP2 = new byte[WRAPPEDLENGTH];

         for (int i = 0; i < WRAPPEDLENGTH; i++) {
            TEMP2[i] = TEMP3[WRAPPEDLENGTH - 1 - i];
         }

         byte[] IV = new byte[IVLENGTH];
         byte[] TEMP1 = new byte[TEMP1LENGTH];
         System.arraycopy(TEMP2, 0, IV, 0, IVLENGTH);
         System.arraycopy(TEMP2, IVLENGTH, TEMP1, 0, TEMP1LENGTH);
         BlockDecryptor decryptor2 = (BlockDecryptor)(new Object(
            (BlockDecryptorEngine)(new Object(DecryptorFactory.getBlockDecryptorEngine(kekKey), (InitializationVector)(new Object(IV)))),
            (InputStream)(new Object(TEMP1))
         ));
         int LCEKPADLENGTH = TEMP1.length - ICVLENGTH;
         byte[] LCEKPADICV = new byte[LCEKPADLENGTH + ICVLENGTH];
         decryptor2.read(LCEKPADICV);
         byte[] LCEKPAD = new byte[LCEKPADLENGTH];
         byte[] ICV = new byte[ICVLENGTH];
         System.arraycopy(LCEKPADICV, 0, LCEKPAD, 0, LCEKPADLENGTH);
         System.arraycopy(LCEKPADICV, LCEKPADLENGTH, ICV, 0, ICVLENGTH);
         SHA1Digest digest = (SHA1Digest)(new Object());
         digest.update(LCEKPAD);
         byte[] checkSum = digest.getDigest();
         if (!Arrays.equals(ICV, 0, checkSum, 0, ICVLENGTH)) {
            return null;
         }

         int LENGTH = LCEKPAD[0];
         byte[] CEK = new byte[LENGTH];

         for (int i = 0; i < LENGTH; i++) {
            CEK[i] = LCEKPAD[i + 1];
         }

         return CEK;
      } finally {
         throw new Object();
      }
   }

   static final byte[] passwordBasedKeyUnWrap(SymmetricKey kekKey, byte[] wrappedData, InitializationVector iv) {
      BlockDecryptorEngine blockEngine = DecryptorFactory.getBlockDecryptorEngine(kekKey);
      int blockLength = blockEngine.getBlockLength();
      if (wrappedData.length % blockLength != 0) {
         return null;
      }

      byte[] almostClearData = new byte[wrappedData.length];
      int numBlocks = wrappedData.length / blockLength;
      CBCDecryptorEngine firstEngine = (CBCDecryptorEngine)(new Object(
         blockEngine, (InitializationVector)(new Object(wrappedData, (numBlocks - 2) * blockLength, blockLength))
      ));
      firstEngine.decrypt(wrappedData, (numBlocks - 1) * blockLength, almostClearData, (numBlocks - 1) * blockLength);
      CBCDecryptorEngine secondEngine = (CBCDecryptorEngine)(new Object(
         blockEngine, (InitializationVector)(new Object(almostClearData, (numBlocks - 1) * blockLength, blockLength))
      ));

      for (int i = 0; i < almostClearData.length - blockLength; i += blockLength) {
         secondEngine.decrypt(wrappedData, i, almostClearData, i);
      }

      CBCDecryptorEngine finalEngine = (CBCDecryptorEngine)(new Object(blockEngine, iv));
      byte[] clearData = new byte[almostClearData.length];

      for (int i = 0; i < clearData.length; i += blockLength) {
         finalEngine.decrypt(almostClearData, i, clearData, i);
      }

      int cekLength = clearData[0];
      if (cekLength > 0 && cekLength < wrappedData.length) {
         byte[] checkValue = new byte[3];
         System.arraycopy(clearData, 1, checkValue, 0, checkValue.length);

         for (int i = 0; i < checkValue.length; i++) {
            checkValue[i] = (byte)(~checkValue[i]);
         }

         return !Arrays.equals(checkValue, 0, clearData, 1 + checkValue.length, checkValue.length)
            ? null
            : Arrays.copy(clearData, 1 + checkValue.length, cekLength);
      } else {
         return null;
      }
   }

   static final byte[] AESKeyUnWrap(AESKey kekKey, byte[] wrappedKey) {
      AESDecryptorEngine engine = (AESDecryptorEngine)(new Object(kekKey));
      byte[] rValues = new byte[wrappedKey.length - 8];
      System.arraycopy(wrappedKey, 8, rValues, 0, wrappedKey.length - 8);
      byte[] B = new byte[16];
      byte[] A = new byte[16];
      System.arraycopy(wrappedKey, 0, A, 0, 8);
      int n = (wrappedKey.length >> 3) - 1;

      for (int j = 5; j >= 0; j--) {
         for (int i = n - 1; i >= 0; i--) {
            int t = n * j + i + 1;
            A[7] ^= (byte)t;
            System.arraycopy(rValues, i * 8, A, 8, 8);
            engine.decrypt(A, 0, B, 0);
            System.arraycopy(B, 0, A, 0, 8);
            System.arraycopy(B, 8, rValues, i * 8, 8);
         }
      }

      return !Arrays.equals(A, 0, AES_IV, 0, AES_IV.length) ? null : rValues;
   }
}
