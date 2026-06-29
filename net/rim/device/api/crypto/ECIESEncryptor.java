package net.rim.device.api.crypto;

import java.io.OutputStream;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.device.api.system.ApplicationRegistry;

public final class ECIESEncryptor extends StreamEncryptor {
   private OutputStream _outputStream;
   private X963KDFPseudoRandomSource _source;
   private byte[] _additionalMACInfo;
   private String _macAlgorithm;
   private int _macKeyLength;
   private int _macLength;

   public ECIESEncryptor(OutputStream out, ECPublicKey recipientPublicKey) {
      this(out, recipientPublicKey, "HMAC/SHA1", -1, null, -1, null, null, true);
   }

   public ECIESEncryptor(
      OutputStream out,
      ECPublicKey recipientPublicKey,
      String macAlgorithm,
      int macKeyLength,
      byte[] additionalMACInfo,
      int macLength,
      byte[] additionalKDFInfo,
      Digest kdfDigest,
      boolean useCofactor
   ) {
      super(new NoCopyByteArrayOutputStream());
      this._outputStream = out;
      if (out != null && recipientPublicKey != null) {
         if (macAlgorithm == null) {
            macAlgorithm = "HMAC/SHA1";
         }

         this._macAlgorithm = macAlgorithm;
         this._macKeyLength = macKeyLength;
         this._additionalMACInfo = additionalMACInfo;
         this._macLength = macLength;
         if (this._macKeyLength == -1) {
            if (macAlgorithm.startsWith("HMAC")) {
               String digestName = RIMFactoryUtilities.stripLeftMostSubAlgorithm(macAlgorithm);
               if (digestName == null) {
                  digestName = "SHA1";
               }

               Digest digest = DigestFactory.getInstance(digestName);
               this._macKeyLength = digest.getDigestLength();
            } else {
               SymmetricKey key = SymmetricKeyFactory.getInstance(this._macAlgorithm, new byte[100], 0);
               this._macKeyLength = key.getLength();
            }
         }

         SymmetricKey key = SymmetricKeyFactory.getInstance(this._macAlgorithm, new byte[this._macKeyLength], 0, this._macKeyLength);
         MAC mac = MACFactory.getInstance(this._macAlgorithm, key);
         if (this._macLength == -1) {
            this._macLength = mac.getLength();
         }

         ECKeyPair ephemeral = new ECKeyPair(recipientPublicKey.getECCryptoSystem());
         byte[] sharedSecret = ECDHKeyAgreement.generateSharedSecret(ephemeral.getECPrivateKey(), recipientPublicKey, useCofactor);
         if (kdfDigest == null) {
            kdfDigest = new SHA1Digest();
         }

         this._source = new X963KDFPseudoRandomSource(sharedSecret, additionalKDFInfo, kdfDigest);
         this._outputStream.write(ephemeral.getECPublicKey().getPublicKeyData());
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "ECIES/" + this._macAlgorithm;
   }

   @Override
   protected final void encrypt(byte[] plaintext, int plaintextOffset, int plaintextLength, byte[] ciphertext) {
      this._source.xorBytes(plaintext, plaintextOffset, plaintextLength, ciphertext, 0);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void close() throws CryptoIOException {
      try {
         NoCopyByteArrayOutputStream out = (NoCopyByteArrayOutputStream)super._out;
         byte[] data = out.getByteArray();
         int dataLength = out.size();
         this._outputStream.write(data, 0, dataLength);
         byte[] macKey = this._source.getBytes(this._macKeyLength);
         SymmetricKey key = SymmetricKeyFactory.getInstance(this._macAlgorithm, macKey, 0, this._macKeyLength);
         MAC mac = MACFactory.getInstance(this._macAlgorithm, key);
         mac.update(data, 0, dataLength);
         if (this._additionalMACInfo != null) {
            mac.update(this._additionalMACInfo);
         }

         this._outputStream.write(mac.getMAC(), 0, this._macLength);
         this._outputStream.close();
         super.close();
         super._out = this._outputStream;
      } catch (Throwable var8) {
         throw new CryptoIOException(e);
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
      // 000: ldc_w "abcdefghijklmnopqrst"
      // 003: invokevirtual java/lang/String.getBytes ()[B
      // 006: astore 0
      // 007: bipush 20
      // 009: newarray 8
      // 00b: dup
      // 00c: bipush 0
      // 00d: bipush 69
      // 00f: bastore
      // 010: dup
      // 011: bipush 1
      // 012: bipush -5
      // 014: bastore
      // 015: dup
      // 016: bipush 2
      // 017: bipush 88
      // 019: bastore
      // 01a: dup
      // 01b: bipush 3
      // 01c: bipush -87
      // 01e: bastore
      // 01f: dup
      // 020: bipush 4
      // 021: bipush 42
      // 023: bastore
      // 024: dup
      // 025: bipush 5
      // 026: bipush 23
      // 028: bastore
      // 029: dup
      // 02a: bipush 6
      // 02c: bipush -83
      // 02e: bastore
      // 02f: dup
      // 030: bipush 7
      // 032: bipush 75
      // 034: bastore
      // 035: dup
      // 036: bipush 8
      // 038: bipush 21
      // 03a: bastore
      // 03b: dup
      // 03c: bipush 9
      // 03e: bipush 16
      // 040: bastore
      // 041: dup
      // 042: bipush 10
      // 044: bipush 28
      // 046: bastore
      // 047: dup
      // 048: bipush 11
      // 04a: bipush 102
      // 04c: bastore
      // 04d: dup
      // 04e: bipush 12
      // 050: bipush -25
      // 052: bastore
      // 053: dup
      // 054: bipush 13
      // 056: bipush 79
      // 058: bastore
      // 059: dup
      // 05a: bipush 14
      // 05c: bipush 39
      // 05e: bastore
      // 05f: dup
      // 060: bipush 15
      // 062: bipush 126
      // 064: bastore
      // 065: dup
      // 066: bipush 16
      // 068: bipush 43
      // 06a: bastore
      // 06b: dup
      // 06c: bipush 17
      // 06e: bipush 70
      // 070: bastore
      // 071: dup
      // 072: bipush 18
      // 074: bipush 8
      // 076: bastore
      // 077: dup
      // 078: bipush 19
      // 07a: bipush 102
      // 07c: bastore
      // 07d: astore 1
      // 07e: new net/rim/device/api/crypto/ECPrivateKey
      // 081: dup
      // 082: new net/rim/device/api/crypto/ECCryptoSystem
      // 085: dup
      // 086: ldc_w "EC160R1"
      // 089: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Ljava/lang/String;)V
      // 08c: aload 1
      // 08d: invokespecial net/rim/device/api/crypto/ECPrivateKey.<init> (Lnet/rim/device/api/crypto/ECCryptoSystem;[B)V
      // 090: astore 2
      // 091: new net/rim/device/api/crypto/ECPublicKey
      // 094: dup
      // 095: new net/rim/device/api/crypto/ECCryptoSystem
      // 098: dup
      // 099: ldc_w "EC160R1"
      // 09c: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Ljava/lang/String;)V
      // 09f: aload 2
      // 0a0: invokevirtual net/rim/device/api/crypto/ECPrivateKey.getPublicKeyData ()[B
      // 0a3: invokespecial net/rim/device/api/crypto/ECPublicKey.<init> (Lnet/rim/device/api/crypto/ECCryptoSystem;[B)V
      // 0a6: astore 3
      // 0a7: bipush 5
      // 0a8: newarray 8
      // 0aa: dup
      // 0ab: bipush 0
      // 0ac: bipush 17
      // 0ae: bastore
      // 0af: dup
      // 0b0: bipush 1
      // 0b1: bipush 34
      // 0b3: bastore
      // 0b4: dup
      // 0b5: bipush 2
      // 0b6: bipush 51
      // 0b8: bastore
      // 0b9: dup
      // 0ba: bipush 3
      // 0bb: bipush 68
      // 0bd: bastore
      // 0be: dup
      // 0bf: bipush 4
      // 0c0: bipush 85
      // 0c2: bastore
      // 0c3: astore 4
      // 0c5: bipush 5
      // 0c6: newarray 8
      // 0c8: dup
      // 0c9: bipush 0
      // 0ca: bipush 81
      // 0cc: bastore
      // 0cd: dup
      // 0ce: bipush 1
      // 0cf: bipush 66
      // 0d1: bastore
      // 0d2: dup
      // 0d3: bipush 2
      // 0d4: bipush 51
      // 0d6: bastore
      // 0d7: dup
      // 0d8: bipush 3
      // 0d9: bipush 36
      // 0db: bastore
      // 0dc: dup
      // 0dd: bipush 4
      // 0de: bipush 21
      // 0e0: bastore
      // 0e1: astore 5
      // 0e3: new java/io/ByteArrayOutputStream
      // 0e6: dup
      // 0e7: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 0ea: astore 6
      // 0ec: new net/rim/device/api/crypto/ECIESEncryptor
      // 0ef: dup
      // 0f0: aload 6
      // 0f2: aload 3
      // 0f3: ldc_w "HMAC/SHA1"
      // 0f6: bipush 20
      // 0f8: aload 4
      // 0fa: bipush 20
      // 0fc: aload 5
      // 0fe: aconst_null
      // 0ff: bipush 1
      // 100: invokespecial net/rim/device/api/crypto/ECIESEncryptor.<init> (Ljava/io/OutputStream;Lnet/rim/device/api/crypto/ECPublicKey;Ljava/lang/String;I[BI[BLnet/rim/device/api/crypto/Digest;Z)V
      // 103: astore 7
      // 105: aload 7
      // 107: aload 0
      // 108: invokevirtual net/rim/device/api/crypto/CryptoOutputStream.write ([B)V
      // 10b: aload 7
      // 10d: invokevirtual net/rim/device/api/crypto/ECIESEncryptor.close ()V
      // 110: aload 6
      // 112: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 115: astore 8
      // 117: new java/io/ByteArrayInputStream
      // 11a: dup
      // 11b: aload 8
      // 11d: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 120: astore 9
      // 122: new net/rim/device/api/crypto/ECIESDecryptor
      // 125: dup
      // 126: aload 9
      // 128: aload 2
      // 129: ldc_w "HMAC/SHA1"
      // 12c: bipush 20
      // 12e: aload 4
      // 130: bipush 20
      // 132: aload 5
      // 134: aconst_null
      // 135: bipush 1
      // 136: invokespecial net/rim/device/api/crypto/ECIESDecryptor.<init> (Ljava/io/InputStream;Lnet/rim/device/api/crypto/ECPrivateKey;Ljava/lang/String;I[BI[BLnet/rim/device/api/crypto/Digest;Z)V
      // 139: astore 10
      // 13b: bipush 32
      // 13d: newarray 8
      // 13f: astore 11
      // 141: aload 10
      // 143: aload 11
      // 145: invokevirtual net/rim/device/api/crypto/CryptoInputStream.read ([B)I
      // 148: istore 12
      // 14a: aload 10
      // 14c: invokevirtual net/rim/device/api/crypto/ECIESDecryptor.verify ()Z
      // 14f: ifeq 175
      // 152: aload 11
      // 154: bipush 0
      // 155: aload 0
      // 156: bipush 0
      // 157: iload 12
      // 159: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 15c: ifeq 175
      // 15f: return
      // 160: astore 1
      // 161: goto 175
      // 164: astore 1
      // 165: goto 175
      // 168: astore 1
      // 169: goto 175
      // 16c: astore 1
      // 16d: goto 175
      // 170: astore 1
      // 171: goto 175
      // 174: astore 1
      // 175: new net/rim/device/api/crypto/CryptoSelfTestError
      // 178: dup
      // 179: invokespecial net/rim/device/api/crypto/CryptoSelfTestError.<init> ()V
      // 17c: athrow
      // try (3 -> 211): 212 null
      // try (3 -> 211): 214 null
      // try (3 -> 211): 216 null
      // try (3 -> 211): 218 null
      // try (3 -> 211): 220 null
      // try (3 -> 211): 222 null
   }

   static {
      long ID_TEST_ECIESENCRYPTOR = 3771001068229482235L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_ECIESENCRYPTOR) == null) {
         selfTest();
         appRegistry.put(ID_TEST_ECIESENCRYPTOR, appRegistry);
      }
   }
}
