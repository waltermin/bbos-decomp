package net.rim.device.api.crypto.encoder;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;

final class X509_RIM_PublicKeyDecoder3 extends X509_PublicKeyDecoder {
   @Override
   public final PublicKey decodeKey(ASN1InputByteArray param1, String param2, byte[] param3, CryptoSystem param4) throws InvalidKeyEncodingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 2
      // 001: ldc_w "EC"
      // 004: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 007: ifeq 07c
      // 00a: aconst_null
      // 00b: astore 5
      // 00d: aload 1
      // 00e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 011: istore 6
      // 013: iload 6
      // 015: bipush 6
      // 017: if_icmpne 045
      // 01a: aload 1
      // 01b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 01e: astore 7
      // 020: ldc2_w -3607261449824502613
      // 023: aload 7
      // 025: invokestatic net/rim/device/api/crypto/oid/OIDs.getAssociatedString (JLnet/rim/device/api/crypto/oid/OID;)Ljava/lang/String;
      // 028: astore 8
      // 02a: aload 8
      // 02c: ifnonnull 037
      // 02f: new net/rim/device/api/crypto/InvalidCryptoSystemException
      // 032: dup
      // 033: invokespecial net/rim/device/api/crypto/InvalidCryptoSystemException.<init> ()V
      // 036: athrow
      // 037: new net/rim/device/api/crypto/ECCryptoSystem
      // 03a: dup
      // 03b: aload 8
      // 03d: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Ljava/lang/String;)V
      // 040: astore 5
      // 042: goto 071
      // 045: iload 6
      // 047: bipush 5
      // 049: if_icmpne 069
      // 04c: aload 4
      // 04e: dup
      // 04f: instanceof net/rim/device/api/crypto/ECCryptoSystem
      // 052: ifne 059
      // 055: pop
      // 056: goto 061
      // 059: checkcast net/rim/device/api/crypto/ECCryptoSystem
      // 05c: astore 5
      // 05e: goto 071
      // 061: new net/rim/device/api/crypto/InvalidCryptoSystemException
      // 064: dup
      // 065: invokespecial net/rim/device/api/crypto/InvalidCryptoSystemException.<init> ()V
      // 068: athrow
      // 069: new net/rim/device/api/crypto/CryptoUnsupportedOperationException
      // 06c: dup
      // 06d: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> ()V
      // 070: athrow
      // 071: new net/rim/device/api/crypto/ECPublicKey
      // 074: dup
      // 075: aload 5
      // 077: aload 3
      // 078: invokespecial net/rim/device/api/crypto/ECPublicKey.<init> (Lnet/rim/device/api/crypto/ECCryptoSystem;[B)V
      // 07b: areturn
      // 07c: aload 2
      // 07d: ldc_w "KEA"
      // 080: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 083: ifne 089
      // 086: goto 141
      // 089: aload 4
      // 08b: dup
      // 08c: instanceof net/rim/device/api/crypto/KEACryptoSystem
      // 08f: ifne 096
      // 092: pop
      // 093: goto 09e
      // 096: checkcast net/rim/device/api/crypto/KEACryptoSystem
      // 099: astore 5
      // 09b: goto 0a6
      // 09e: new net/rim/device/api/crypto/InvalidCryptoSystemException
      // 0a1: dup
      // 0a2: invokespecial net/rim/device/api/crypto/InvalidCryptoSystemException.<init> ()V
      // 0a5: athrow
      // 0a6: aload 1
      // 0a7: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString ()[B
      // 0aa: astore 6
      // 0ac: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 0af: dup
      // 0b0: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 0b3: astore 7
      // 0b5: aload 7
      // 0b7: aload 5
      // 0b9: invokevirtual net/rim/device/api/crypto/KEACryptoSystem.getP ()[B
      // 0bc: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger ([B)V
      // 0bf: aload 7
      // 0c1: aload 5
      // 0c3: invokevirtual net/rim/device/api/crypto/KEACryptoSystem.getG ()[B
      // 0c6: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger ([B)V
      // 0c9: aload 7
      // 0cb: aload 5
      // 0cd: invokevirtual net/rim/device/api/crypto/KEACryptoSystem.getQ ()[B
      // 0d0: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger ([B)V
      // 0d3: aload 7
      // 0d5: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 0d8: astore 8
      // 0da: new net/rim/device/api/crypto/SHA1Digest
      // 0dd: dup
      // 0de: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 0e1: astore 9
      // 0e3: aload 9
      // 0e5: aload 8
      // 0e7: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 0ea: aload 9
      // 0ec: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ()[B
      // 0ef: astore 10
      // 0f1: aload 9
      // 0f3: invokevirtual net/rim/device/api/crypto/SHA1Digest.getDigestLength ()I
      // 0f6: bipush 1
      // 0f7: ishr
      // 0f8: istore 11
      // 0fa: bipush 0
      // 0fb: istore 12
      // 0fd: iload 12
      // 0ff: iload 11
      // 101: if_icmpge 12f
      // 104: aload 6
      // 106: iload 12
      // 108: baload
      // 109: aload 10
      // 10b: iload 12
      // 10d: baload
      // 10e: aload 10
      // 110: iload 12
      // 112: iload 11
      // 114: iadd
      // 115: baload
      // 116: ixor
      // 117: if_icmpeq 122
      // 11a: new net/rim/device/api/crypto/InvalidKeyEncodingException
      // 11d: dup
      // 11e: invokespecial net/rim/device/api/crypto/InvalidKeyEncodingException.<init> ()V
      // 121: athrow
      // 122: iinc 12 1
      // 125: goto 0fd
      // 128: astore 7
      // 12a: goto 12f
      // 12d: astore 7
      // 12f: new net/rim/device/api/crypto/KEAPublicKey
      // 132: dup
      // 133: aload 5
      // 135: aload 3
      // 136: invokespecial net/rim/device/api/crypto/KEAPublicKey.<init> (Lnet/rim/device/api/crypto/KEACryptoSystem;[B)V
      // 139: areturn
      // 13a: astore 5
      // 13c: goto 141
      // 13f: astore 5
      // 141: new net/rim/device/api/crypto/InvalidKeyEncodingException
      // 144: dup
      // 145: invokespecial net/rim/device/api/crypto/InvalidKeyEncodingException.<init> ()V
      // 148: athrow
      // try (78 -> 136): 136 null
      // try (78 -> 136): 138 null
      // try (0 -> 56): 145 null
      // try (57 -> 144): 145 null
      // try (0 -> 56): 147 null
      // try (57 -> 144): 147 null
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "X509";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"EC", "KEA"};
   }
}
