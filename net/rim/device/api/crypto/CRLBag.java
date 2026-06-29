package net.rim.device.api.crypto;

import net.rim.device.api.crypto.certificate.CertificateRevocationList;

public class CRLBag extends SafeBag {
   private CertificateRevocationList _crl;

   public CRLBag(byte[] data, byte[] attributes, PKCS12ContentInfo parent) {
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
      // 08: new net/rim/device/api/crypto/asn1/ASN1InputByteArray
      // 0b: dup
      // 0c: aload 0
      // 0d: getfield net/rim/device/api/crypto/SafeBag._bagData [B
      // 10: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 13: astore 1
      // 14: aload 1
      // 15: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 18: istore 2
      // 19: aload 1
      // 1a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 1d: astore 3
      // 1e: aload 3
      // 1f: ldc_w -1684020687
      // 22: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 25: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 28: ifeq 48
      // 2b: aload 1
      // 2c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString ()[B
      // 2f: astore 4
      // 31: aload 0
      // 32: new net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList
      // 35: dup
      // 36: new net/rim/device/api/crypto/asn1/ASN1InputStream
      // 39: dup
      // 3a: aload 4
      // 3c: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> ([B)V
      // 3f: invokestatic net/rim/device/api/crypto/keystore/DeviceKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 42: invokespecial net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList.<init> (Lnet/rim/device/api/crypto/asn1/ASN1InputStream;Lnet/rim/device/api/crypto/keystore/KeyStore;)V
      // 45: putfield net/rim/device/api/crypto/CRLBag._crl Lnet/rim/device/api/crypto/certificate/CertificateRevocationList;
      // 48: aload 0
      // 49: getfield net/rim/device/api/crypto/SafeBag._bagAttributes [B
      // 4c: ifnull 9f
      // 4f: new net/rim/device/api/crypto/asn1/ASN1InputByteArray
      // 52: dup
      // 53: aload 0
      // 54: getfield net/rim/device/api/crypto/SafeBag._bagAttributes [B
      // 57: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 5a: astore 4
      // 5c: aload 4
      // 5e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 61: istore 2
      // 62: iload 2
      // 63: bipush -1
      // 65: if_icmpeq 9f
      // 68: iload 2
      // 69: bipush 16
      // 6b: if_icmpne 8e
      // 6e: aload 4
      // 70: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 73: aload 0
      // 74: getfield net/rim/device/api/crypto/SafeBag._attributes Ljava/util/Vector;
      // 77: new net/rim/device/api/crypto/PKCS12Attribute
      // 7a: dup
      // 7b: aload 4
      // 7d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 80: aload 4
      // 82: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 85: invokespecial net/rim/device/api/crypto/PKCS12Attribute.<init> (Lnet/rim/device/api/crypto/oid/OID;[B)V
      // 88: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 8b: goto 96
      // 8e: new net/rim/device/api/crypto/PKCS12ParsingException
      // 91: dup
      // 92: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> ()V
      // 95: athrow
      // 96: aload 4
      // 98: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 9b: istore 2
      // 9c: goto 62
      // 9f: aload 0
      // a0: bipush 1
      // a1: putfield net/rim/device/api/crypto/SafeBag._parsed Z
      // a4: return
      // a5: astore 1
      // a6: new net/rim/device/api/crypto/PKCS12ParsingException
      // a9: dup
      // aa: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> ()V
      // ad: athrow
      // ae: astore 1
      // af: return
      // b0: astore 1
      // b1: return
      // b2: astore 1
      // b3: return
      // b4: astore 1
      // b5: return
      // b6: astore 1
      // b7: return
      // try (4 -> 76): 77 null
      // try (4 -> 76): 82 null
      // try (4 -> 76): 84 null
      // try (4 -> 76): 86 null
      // try (4 -> 76): 88 null
      // try (4 -> 76): 90 null
   }

   @Override
   public PKCS12ContentInfo[] getChildrenContentInfos() {
      return null;
   }

   public CertificateRevocationList getCRL() {
      this.parse();
      return this._crl;
   }
}
