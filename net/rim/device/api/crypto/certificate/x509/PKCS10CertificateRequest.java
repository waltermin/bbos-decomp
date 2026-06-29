package net.rim.device.api.crypto.certificate.x509;

import java.io.ByteArrayOutputStream;
import java.util.Vector;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SignatureSigner;
import net.rim.device.api.crypto.certificate.CertificateExtension;
import net.rim.device.api.io.Base64OutputStream;

public class PKCS10CertificateRequest {
   private PublicKey _publicKey;
   private X509DistinguishedName _name;
   private SignatureSigner _signer;
   private Vector _extensions;
   private byte[] _encoding;
   private boolean _changed = true;
   private static final String _header;
   private static final String _tail;

   public PKCS10CertificateRequest(PublicKey publicKey, X509DistinguishedName name, SignatureSigner signer) {
      if (publicKey != null && name != null && signer != null) {
         this._publicKey = publicKey;
         this._name = name;
         this._signer = signer;
         this._extensions = (Vector)(new Object());
      } else {
         throw new Object();
      }
   }

   public void addExtension(CertificateExtension extension) {
      if (extension != null) {
         this._extensions.addElement(extension);
         this._changed = true;
      }
   }

   public byte[] getEncoded() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._encoding [B
      // 004: ifnull 013
      // 007: aload 0
      // 008: getfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._changed Z
      // 00b: ifne 013
      // 00e: aload 0
      // 00f: getfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._encoding [B
      // 012: areturn
      // 013: new java/lang/Object
      // 016: dup
      // 017: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 01a: astore 1
      // 01b: aload 1
      // 01c: bipush 0
      // 01d: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger (I)V
      // 020: aload 1
      // 021: aload 0
      // 022: getfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._name Lnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;
      // 025: invokevirtual net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.getEncoding ()[B
      // 028: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 02b: aload 0
      // 02c: getfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 02f: ldc_w "X509"
      // 032: invokestatic net/rim/device/api/crypto/encoder/PublicKeyEncoder.encode (Lnet/rim/device/api/crypto/PublicKey;Ljava/lang/String;)Lnet/rim/device/api/crypto/encoder/EncodedKey;
      // 035: astore 2
      // 036: aload 1
      // 037: aload 2
      // 038: invokevirtual net/rim/device/api/crypto/encoder/EncodedKey.getEncodedKey ()[B
      // 03b: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 03e: new java/lang/Object
      // 041: dup
      // 042: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 045: astore 3
      // 046: aload 0
      // 047: getfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._extensions Ljava/util/Vector;
      // 04a: invokevirtual java/util/Vector.isEmpty ()Z
      // 04d: ifne 0ba
      // 050: aload 3
      // 051: ldc_w 545269308
      // 054: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 057: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 05a: new java/lang/Object
      // 05d: dup
      // 05e: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 061: astore 4
      // 063: bipush 0
      // 064: istore 5
      // 066: iload 5
      // 068: aload 0
      // 069: getfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._extensions Ljava/util/Vector;
      // 06c: invokevirtual java/util/Vector.size ()I
      // 06f: if_icmpge 0b4
      // 072: aload 0
      // 073: getfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._extensions Ljava/util/Vector;
      // 076: iload 5
      // 078: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 07b: checkcast java/lang/Object
      // 07e: astore 6
      // 080: new java/lang/Object
      // 083: dup
      // 084: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 087: astore 7
      // 089: aload 7
      // 08b: aload 6
      // 08d: invokevirtual net/rim/device/api/crypto/certificate/CertificateExtension.getOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 090: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 093: aload 7
      // 095: aload 6
      // 097: invokevirtual net/rim/device/api/crypto/certificate/CertificateExtension.getCritical ()Z
      // 09a: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeBoolean (Z)V
      // 09d: aload 7
      // 09f: aload 6
      // 0a1: invokevirtual net/rim/device/api/crypto/certificate/CertificateExtension.getValue ()[B
      // 0a4: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([B)V
      // 0a7: aload 4
      // 0a9: aload 7
      // 0ab: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 0ae: iinc 5 1
      // 0b1: goto 066
      // 0b4: aload 3
      // 0b5: aload 4
      // 0b7: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 0ba: aload 1
      // 0bb: aload 3
      // 0bc: bipush 2
      // 0be: bipush 0
      // 0bf: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSet (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;II)V
      // 0c2: new java/lang/Object
      // 0c5: dup
      // 0c6: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 0c9: astore 4
      // 0cb: aload 4
      // 0cd: aload 1
      // 0ce: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 0d1: aload 4
      // 0d3: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 0d6: astore 5
      // 0d8: aload 0
      // 0d9: getfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._signer Lnet/rim/device/api/crypto/SignatureSigner;
      // 0dc: ifnonnull 0e7
      // 0df: new java/lang/Object
      // 0e2: dup
      // 0e3: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0e6: athrow
      // 0e7: aload 0
      // 0e8: getfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._signer Lnet/rim/device/api/crypto/SignatureSigner;
      // 0eb: aload 5
      // 0ed: invokeinterface net/rim/device/api/crypto/SignatureSigner.update ([B)V 2
      // 0f2: aload 0
      // 0f3: getfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._signer Lnet/rim/device/api/crypto/SignatureSigner;
      // 0f6: ldc_w "X509"
      // 0f9: invokestatic net/rim/device/api/crypto/encoder/SignatureEncoder.encode (Lnet/rim/device/api/crypto/SignatureSigner;Ljava/lang/String;)Lnet/rim/device/api/crypto/encoder/EncodedSignature;
      // 0fc: astore 6
      // 0fe: aload 4
      // 100: aload 6
      // 102: invokevirtual net/rim/device/api/crypto/encoder/EncodedSignature.getEncodedSignature ()[B
      // 105: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 108: new java/lang/Object
      // 10b: dup
      // 10c: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 10f: astore 7
      // 111: aload 7
      // 113: aload 4
      // 115: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 118: aload 0
      // 119: aload 7
      // 11b: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 11e: putfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._encoding [B
      // 121: aload 0
      // 122: bipush 0
      // 123: putfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._changed Z
      // 126: aload 0
      // 127: getfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._encoding [B
      // 12a: areturn
      // 12b: astore 1
      // 12c: goto 130
      // 12f: astore 1
      // 130: aload 0
      // 131: aconst_null
      // 132: putfield net/rim/device/api/crypto/certificate/x509/PKCS10CertificateRequest._encoding [B
      // 135: aconst_null
      // 136: areturn
      // try (9 -> 134): 135 null
      // try (9 -> 134): 137 null
   }

   public String getCSRFormat() {
      try {
         if (this._encoding == null || this._changed) {
            this.getEncoded();
         }

         if (this._encoding == null) {
            return null;
         }

         ByteArrayOutputStream output = (ByteArrayOutputStream)(new Object());
         Base64OutputStream base = (Base64OutputStream)(new Object(output, true, true));
         base.write(this._encoding);
         base.close();
         return ((StringBuffer)(new Object("-----BEGIN NEW CERTIFICATE REQUEST-----\r\n")))
            .append((String)(new Object(output.toByteArray())))
            .append("-----END NEW CERTIFICATE REQUEST-----\r\n")
            .toString();
      } finally {
         ;
      }
   }
}
