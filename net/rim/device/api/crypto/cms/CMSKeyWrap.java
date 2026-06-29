package net.rim.device.api.crypto.cms;

import java.io.ByteArrayOutputStream;
import net.rim.device.api.crypto.AESEncryptorEngine;
import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.BlockEncryptor;
import net.rim.device.api.crypto.CAST128Key;
import net.rim.device.api.crypto.CBCEncryptorEngine;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.EncryptorFactory;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.RC2Key;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.TripleDESEncryptorEngine;
import net.rim.device.api.crypto.TripleDESKey;

final class CMSKeyWrap {
   private static final byte[] WRAP_IV = new byte[]{74, -35, -94, 44, 121, -24, 33, 5};
   private static final byte[] AES_IV = new byte[]{-90, -90, -90, -90, -90, -90, -90, -90};

   private CMSKeyWrap() {
   }

   static final byte[] TripleDESKeyWrap(TripleDESKey kekKey, TripleDESKey contentKey) {
      try {
         int WRAPPEDLENGTH = 40;
         int IVLENGTH = 8;
         int TEMP1LENGTH = 32;
         int CEKLENGTH = 24;
         int ICVLENGTH = 8;
         byte[] CEK = contentKey.getData();
         SHA1Digest digest = new SHA1Digest();
         digest.update(CEK);
         byte[] ICV = digest.getDigest();
         byte[] CEKICV = new byte[CEKLENGTH + ICVLENGTH];
         System.arraycopy(CEK, 0, CEKICV, 0, CEKLENGTH);
         System.arraycopy(ICV, 0, CEKICV, CEKLENGTH, ICVLENGTH);
         InitializationVector iv = new InitializationVector(IVLENGTH);
         byte[] IV = iv.getData();
         ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
         BlockEncryptor encryptor = new BlockEncryptor(new CBCEncryptorEngine(new TripleDESEncryptorEngine(kekKey), iv), stream1);
         encryptor.write(CEKICV);
         encryptor.close();
         byte[] TEMP1 = stream1.toByteArray();
         byte[] TEMP2 = new byte[WRAPPEDLENGTH];
         System.arraycopy(IV, 0, TEMP2, 0, IVLENGTH);
         System.arraycopy(TEMP1, 0, TEMP2, IVLENGTH, TEMP1LENGTH);
         byte[] TEMP3 = new byte[WRAPPEDLENGTH];

         for (int i = 0; i < WRAPPEDLENGTH; i++) {
            TEMP3[i] = TEMP2[WRAPPEDLENGTH - 1 - i];
         }

         ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
         BlockEncryptor encryptor2 = new BlockEncryptor(
            new CBCEncryptorEngine(new TripleDESEncryptorEngine(kekKey), new InitializationVector(WRAP_IV)), stream2
         );
         encryptor2.write(TEMP3);
         return stream2.toByteArray();
      } finally {
         throw new RuntimeException();
      }
   }

   static final byte[] RC2KeyWrap(RC2Key param0, RC2Key param1) throws CryptoTokenException, CryptoUnsupportedOperationException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 1
      // 02: invokestatic net/rim/device/api/crypto/cms/CMSKeyWrap.GenericKeyWrap (Lnet/rim/device/api/crypto/SymmetricKey;Lnet/rim/device/api/crypto/SymmetricKey;)[B
      // 05: areturn
      // 06: astore 2
      // 07: aload 2
      // 08: athrow
      // 09: astore 2
      // 0a: aload 2
      // 0b: athrow
      // 0c: astore 2
      // 0d: new net/rim/device/api/crypto/CryptoUnsupportedOperationException
      // 10: dup
      // 11: aload 2
      // 12: invokevirtual net/rim/device/api/crypto/CryptoException.toString ()Ljava/lang/String;
      // 15: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> (Ljava/lang/String;)V
      // 18: athrow
      // try (0 -> 3): 4 null
      // try (0 -> 3): 7 null
      // try (0 -> 3): 10 null
   }

   static final byte[] CASTKeyWrap(CAST128Key param0, CAST128Key param1) throws CryptoTokenException, CryptoUnsupportedOperationException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 1
      // 02: invokestatic net/rim/device/api/crypto/cms/CMSKeyWrap.GenericKeyWrap (Lnet/rim/device/api/crypto/SymmetricKey;Lnet/rim/device/api/crypto/SymmetricKey;)[B
      // 05: areturn
      // 06: astore 2
      // 07: aload 2
      // 08: athrow
      // 09: astore 2
      // 0a: aload 2
      // 0b: athrow
      // 0c: astore 2
      // 0d: new net/rim/device/api/crypto/CryptoUnsupportedOperationException
      // 10: dup
      // 11: aload 2
      // 12: invokevirtual net/rim/device/api/crypto/CryptoException.toString ()Ljava/lang/String;
      // 15: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> (Ljava/lang/String;)V
      // 18: athrow
      // try (0 -> 3): 4 null
      // try (0 -> 3): 7 null
      // try (0 -> 3): 10 null
   }

   static final byte[] GenericKeyWrap(SymmetricKey kekKey, SymmetricKey contentKey) {
      try {
         int ICVLENGTH = 8;
         int IVLENGTH = 8;
         byte[] CEK = contentKey.getData();
         byte[] LCEK = new byte[CEK.length + 1];
         LCEK[0] = (byte)CEK.length;
         System.arraycopy(CEK, 0, LCEK, 1, CEK.length);
         int padlength = 8 - LCEK.length % 8;
         if (padlength == 8) {
            padlength = 0;
         }

         byte[] LCEKPAD = new byte[LCEK.length + padlength];
         System.arraycopy(LCEK, 0, LCEKPAD, 0, LCEK.length);
         SHA1Digest digest = new SHA1Digest();
         digest.update(LCEKPAD);
         byte[] ICV = digest.getDigest();
         byte[] LCEKPADICV = new byte[LCEKPAD.length + ICVLENGTH];
         System.arraycopy(LCEKPAD, 0, LCEKPADICV, 0, LCEKPAD.length);
         System.arraycopy(ICV, 0, LCEKPADICV, LCEKPAD.length, ICVLENGTH);
         InitializationVector iv = new InitializationVector(IVLENGTH);
         byte[] IV = iv.getData();
         ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
         BlockEncryptor encryptor = new BlockEncryptor(new CBCEncryptorEngine(EncryptorFactory.getBlockEncryptorEngine(kekKey), iv), stream1);
         encryptor.write(LCEKPADICV);
         encryptor.close();
         byte[] TEMP1 = stream1.toByteArray();
         byte[] TEMP2 = new byte[IVLENGTH + TEMP1.length];
         System.arraycopy(IV, 0, TEMP2, 0, IVLENGTH);
         System.arraycopy(TEMP1, 0, TEMP2, IVLENGTH, TEMP1.length);
         int len = TEMP2.length;
         byte[] TEMP3 = new byte[len];

         for (int i = 0; i < len; i++) {
            TEMP3[i] = TEMP2[len - 1 - i];
         }

         ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
         BlockEncryptor encryptor2 = new BlockEncryptor(
            new CBCEncryptorEngine(EncryptorFactory.getBlockEncryptorEngine(kekKey), new InitializationVector(WRAP_IV)), stream2
         );
         encryptor2.write(TEMP3);
         return stream2.toByteArray();
      } finally {
         throw new RuntimeException();
      }
   }

   static final byte[] passwordBasedKeyWrap(SymmetricKey kekKey, SymmetricKey contentKey, InitializationVector iv) {
      int cekLength = contentKey.getLength();
      byte[] checkValue = new byte[3];
      System.arraycopy(contentKey.getData(), 0, checkValue, 0, checkValue.length);

      for (int i = 0; i < checkValue.length; i++) {
         checkValue[i] = (byte)(~checkValue[i]);
      }

      CBCEncryptorEngine engine = new CBCEncryptorEngine(EncryptorFactory.getBlockEncryptorEngine(kekKey), iv);
      int blockLength = engine.getBlockLength();
      int paddingLength = 0;
      if (4 + cekLength < 2 * blockLength) {
         paddingLength = 2 * blockLength - 4 - cekLength;
      } else if ((4 + cekLength) % blockLength != 0) {
         paddingLength = blockLength - (4 + cekLength) % blockLength;
      }

      byte[] formattedKey = new byte[1 + checkValue.length + cekLength + paddingLength];
      formattedKey[0] = (byte)cekLength;
      int offset = 1;
      System.arraycopy(checkValue, 0, formattedKey, offset, checkValue.length);
      offset += checkValue.length;
      System.arraycopy(contentKey.getData(), 0, formattedKey, offset, cekLength);
      offset += cekLength;
      System.arraycopy(RandomSource.getBytes(paddingLength), 0, formattedKey, offset, paddingLength);
      byte[] wrappedData = new byte[formattedKey.length];

      for (int i = 0; i < wrappedData.length; i += blockLength) {
         engine.encrypt(formattedKey, i, wrappedData, i);
      }

      byte[] wrappedKey = new byte[wrappedData.length];

      for (int i = 0; i < wrappedData.length; i += blockLength) {
         engine.encrypt(wrappedData, i, wrappedKey, i);
      }

      return wrappedKey;
   }

   static final byte[] AESKeyWrap(AESKey kekKey, AESKey contentKey) {
      AESEncryptorEngine engine = new AESEncryptorEngine(kekKey);
      byte[] data = contentKey.getData();
      byte[] rValues = new byte[data.length];
      System.arraycopy(data, 0, rValues, 0, data.length);
      byte[] B = new byte[16];
      byte[] A = new byte[16];
      System.arraycopy(AES_IV, 0, A, 0, 8);
      int n = data.length >> 3;

      for (int j = 0; j < 6; j++) {
         for (int i = 0; i < n; i++) {
            int t = n * j + i + 1;
            System.arraycopy(rValues, i * 8, A, 8, 8);
            engine.encrypt(A, 0, B, 0);
            System.arraycopy(B, 0, A, 0, 8);
            A[7] ^= (byte)t;
            System.arraycopy(B, 8, rValues, i * 8, 8);
         }
      }

      byte[] C = new byte[rValues.length + 8];
      System.arraycopy(A, 0, C, 0, 8);
      System.arraycopy(rValues, 0, C, 8, rValues.length);
      return C;
   }
}
