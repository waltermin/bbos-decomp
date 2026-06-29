package net.rim.device.api.crypto;

import net.rim.device.api.crypto.certificate.Certificate;

public class CertificateBag extends SafeBag {
   private Certificate _certificate;

   public CertificateBag(byte[] data, byte[] attributes, PKCS12ContentInfo parent) {
      super(data, attributes, parent);
   }

   @Override
   protected void parse() throws PKCS12ParsingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/SafeBag._parsed Z
      // 04: ifeq 08
      // 07: return
      // 08: new java/lang/Object
      // 0b: dup
      // 0c: aload 0
      // 0d: getfield net/rim/device/api/crypto/SafeBag._bagData [B
      // 10: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 13: astore 1
      // 14: aload 1
      // 15: bipush 1
      // 16: bipush 0
      // 17: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence (II)V
      // 1a: aload 1
      // 1b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 1e: astore 2
      // 1f: aload 2
      // 20: ldc_w -1684020703
      // 23: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 26: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 29: ifeq a2
      // 2c: aload 1
      // 2d: bipush 1
      // 2e: bipush 0
      // 2f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString (II)[B
      // 32: astore 3
      // 33: aload 0
      // 34: new java/lang/Object
      // 37: dup
      // 38: aload 3
      // 39: invokespecial net/rim/device/api/crypto/certificate/x509/X509Certificate.<init> ([B)V
      // 3c: putfield net/rim/device/api/crypto/CertificateBag._certificate Lnet/rim/device/api/crypto/certificate/Certificate;
      // 3f: aload 0
      // 40: getfield net/rim/device/api/crypto/SafeBag._bagAttributes [B
      // 43: ifnull 9a
      // 46: new java/lang/Object
      // 49: dup
      // 4a: aload 0
      // 4b: getfield net/rim/device/api/crypto/SafeBag._bagAttributes [B
      // 4e: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 51: astore 4
      // 53: aload 4
      // 55: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 58: istore 5
      // 5a: iload 5
      // 5c: bipush -1
      // 5e: if_icmpeq 9a
      // 61: iload 5
      // 63: bipush 16
      // 65: if_icmpne 88
      // 68: aload 4
      // 6a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 6d: aload 0
      // 6e: getfield net/rim/device/api/crypto/SafeBag._attributes Ljava/util/Vector;
      // 71: new net/rim/device/api/crypto/PKCS12Attribute
      // 74: dup
      // 75: aload 4
      // 77: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 7a: aload 4
      // 7c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 7f: invokespecial net/rim/device/api/crypto/PKCS12Attribute.<init> (Lnet/rim/device/api/crypto/oid/OID;[B)V
      // 82: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 85: goto 90
      // 88: new net/rim/device/api/crypto/PKCS12ParsingException
      // 8b: dup
      // 8c: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> ()V
      // 8f: athrow
      // 90: aload 4
      // 92: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 95: istore 5
      // 97: goto 5a
      // 9a: aload 0
      // 9b: bipush 1
      // 9c: putfield net/rim/device/api/crypto/SafeBag._parsed Z
      // 9f: goto c2
      // a2: aload 2
      // a3: ldc_w -1684020702
      // a6: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // a9: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // ac: ifeq c2
      // af: return
      // b0: astore 1
      // b1: new net/rim/device/api/crypto/PKCS12ParsingException
      // b4: dup
      // b5: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> ()V
      // b8: athrow
      // b9: astore 1
      // ba: new net/rim/device/api/crypto/PKCS12ParsingException
      // bd: dup
      // be: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> ()V
      // c1: athrow
      // c2: return
      // try (4 -> 81): 82 null
      // try (4 -> 81): 87 null
   }

   @Override
   public PKCS12ContentInfo[] getChildrenContentInfos() {
      return null;
   }

   public Certificate getCertificate() {
      this.parse();
      return this._certificate;
   }
}
