package net.rim.device.api.crypto.certificate.x509;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.CryptoUtilities;
import net.rim.device.api.crypto.InvalidCryptoSystemException;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SignatureVerifier;
import net.rim.device.api.crypto.asn1.ASN1BitSet;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.asn1.ASN1SignedByteArray;
import net.rim.device.api.crypto.certificate.CertificateDisplayField;
import net.rim.device.api.crypto.certificate.CertificateExtension;
import net.rim.device.api.crypto.certificate.CertificateParsingException;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.CertificateVerificationException;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.certificate.NoIssuerFoundException;
import net.rim.device.api.crypto.certificate.SubjectKeyStoreIndex;
import net.rim.device.api.crypto.encoder.DecodedSignature;
import net.rim.device.api.crypto.encoder.PublicKeyDecoder;
import net.rim.device.api.crypto.encoder.SignatureDecoder;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

final class Content implements Persistable {
   protected int _version;
   protected ASN1SignedByteArray _signedSerialNumber;
   protected X509DistinguishedName _issuer;
   protected long _notValidBefore;
   protected long _notValidAfter;
   protected X509DistinguishedName _subject;
   protected byte[] _issuerUniqueID;
   protected byte[] _subjectUniqueID;
   protected CertificateExtension[] _extensions;
   protected int _signatureOffset;
   protected int _signatureLength;
   protected int _encodedFormHashCode;
   protected int _tbsCertificateOffset;
   protected int _tbsCertificateLength;
   protected int _encodedKeyOffset;
   protected int _encodedKeyLength;
   protected ASN1BitSet _keyUsageBitSet;
   protected long[] _extendedKeyUsage;
   private CryptoSystem _issuerPublicKeyCryptoSystem;
   protected PublicKey _issuerPublicKey;
   protected PublicKey _publicKey;
   protected byte[] _encoding;

   public Content(byte[] param1) throws CertificateParsingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokespecial java/lang/Object.<init> ()V
      // 004: aload 0
      // 005: aload 1
      // 006: putfield net/rim/device/api/crypto/certificate/x509/Content._encoding [B
      // 009: new net/rim/device/api/crypto/asn1/ASN1InputByteArray
      // 00c: dup
      // 00d: aload 0
      // 00e: getfield net/rim/device/api/crypto/certificate/x509/Content._encoding [B
      // 011: bipush 0
      // 012: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([BI)V
      // 015: astore 2
      // 016: aload 2
      // 017: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getFieldEndPosition ()I
      // 01a: istore 3
      // 01b: aload 2
      // 01c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 01f: aload 0
      // 020: aload 2
      // 021: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getStartPosition ()I
      // 024: putfield net/rim/device/api/crypto/certificate/x509/Content._tbsCertificateOffset I
      // 027: aload 2
      // 028: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 02b: aload 0
      // 02c: aload 2
      // 02d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getEndPosition ()I
      // 030: aload 0
      // 031: getfield net/rim/device/api/crypto/certificate/x509/Content._tbsCertificateOffset I
      // 034: isub
      // 035: putfield net/rim/device/api/crypto/certificate/x509/Content._tbsCertificateLength I
      // 038: aload 0
      // 039: aload 0
      // 03a: getfield net/rim/device/api/crypto/certificate/x509/Content._tbsCertificateLength I
      // 03d: aload 0
      // 03e: getfield net/rim/device/api/crypto/certificate/x509/Content._tbsCertificateOffset I
      // 041: iadd
      // 042: putfield net/rim/device/api/crypto/certificate/x509/Content._signatureOffset I
      // 045: aload 2
      // 046: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 049: aload 2
      // 04a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 04d: aload 0
      // 04e: aload 2
      // 04f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getEndPosition ()I
      // 052: aload 0
      // 053: getfield net/rim/device/api/crypto/certificate/x509/Content._signatureOffset I
      // 056: isub
      // 057: putfield net/rim/device/api/crypto/certificate/x509/Content._signatureLength I
      // 05a: aload 0
      // 05b: getfield net/rim/device/api/crypto/certificate/x509/Content._signatureOffset I
      // 05e: aload 0
      // 05f: getfield net/rim/device/api/crypto/certificate/x509/Content._signatureLength I
      // 062: iadd
      // 063: iload 3
      // 064: if_icmple 06f
      // 067: new net/rim/device/api/crypto/certificate/CertificateParsingException
      // 06a: dup
      // 06b: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 06e: athrow
      // 06f: aload 2
      // 070: aload 0
      // 071: getfield net/rim/device/api/crypto/certificate/x509/Content._tbsCertificateOffset I
      // 074: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.setStartPosition (I)V
      // 077: aload 2
      // 078: aload 0
      // 079: getfield net/rim/device/api/crypto/certificate/x509/Content._tbsCertificateOffset I
      // 07c: aload 0
      // 07d: getfield net/rim/device/api/crypto/certificate/x509/Content._tbsCertificateLength I
      // 080: iadd
      // 081: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.setEndPosition (I)V
      // 084: aload 2
      // 085: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 088: aload 0
      // 089: aload 2
      // 08a: bipush 1
      // 08b: bipush 0
      // 08c: bipush 0
      // 08d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readInteger (III)I
      // 090: putfield net/rim/device/api/crypto/certificate/x509/Content._version I
      // 093: aload 0
      // 094: aload 2
      // 095: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readIntegerAsSignedByteArray ()Lnet/rim/device/api/crypto/asn1/ASN1SignedByteArray;
      // 098: putfield net/rim/device/api/crypto/certificate/x509/Content._signedSerialNumber Lnet/rim/device/api/crypto/asn1/ASN1SignedByteArray;
      // 09b: aload 2
      // 09c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 09f: aload 2
      // 0a0: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getStartPosition ()I
      // 0a3: istore 4
      // 0a5: aload 2
      // 0a6: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 0a9: aload 0
      // 0aa: new net/rim/device/api/crypto/certificate/x509/X509DistinguishedName
      // 0ad: dup
      // 0ae: aload 1
      // 0af: iload 4
      // 0b1: aload 2
      // 0b2: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getEndPosition ()I
      // 0b5: iload 4
      // 0b7: isub
      // 0b8: invokespecial net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.<init> ([BII)V
      // 0bb: putfield net/rim/device/api/crypto/certificate/x509/Content._issuer Lnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;
      // 0be: aload 2
      // 0bf: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 0c2: aload 2
      // 0c3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 0c6: bipush 23
      // 0c8: if_icmpne 0d6
      // 0cb: aload 0
      // 0cc: aload 2
      // 0cd: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readUTCTime ()J
      // 0d0: putfield net/rim/device/api/crypto/certificate/x509/Content._notValidBefore J
      // 0d3: goto 0f2
      // 0d6: aload 2
      // 0d7: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 0da: bipush 24
      // 0dc: if_icmpne 0ea
      // 0df: aload 0
      // 0e0: aload 2
      // 0e1: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readGeneralizedTime ()J
      // 0e4: putfield net/rim/device/api/crypto/certificate/x509/Content._notValidBefore J
      // 0e7: goto 0f2
      // 0ea: new net/rim/device/api/crypto/asn1/ASN1EncodingException
      // 0ed: dup
      // 0ee: invokespecial net/rim/device/api/crypto/asn1/ASN1EncodingException.<init> ()V
      // 0f1: athrow
      // 0f2: aload 2
      // 0f3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 0f6: bipush 23
      // 0f8: if_icmpne 106
      // 0fb: aload 0
      // 0fc: aload 2
      // 0fd: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readUTCTime ()J
      // 100: putfield net/rim/device/api/crypto/certificate/x509/Content._notValidAfter J
      // 103: goto 122
      // 106: aload 2
      // 107: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 10a: bipush 24
      // 10c: if_icmpne 11a
      // 10f: aload 0
      // 110: aload 2
      // 111: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readGeneralizedTime ()J
      // 114: putfield net/rim/device/api/crypto/certificate/x509/Content._notValidAfter J
      // 117: goto 122
      // 11a: new net/rim/device/api/crypto/asn1/ASN1EncodingException
      // 11d: dup
      // 11e: invokespecial net/rim/device/api/crypto/asn1/ASN1EncodingException.<init> ()V
      // 121: athrow
      // 122: aload 2
      // 123: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getStartPosition ()I
      // 126: istore 5
      // 128: aload 2
      // 129: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 12c: aload 0
      // 12d: new net/rim/device/api/crypto/certificate/x509/X509DistinguishedName
      // 130: dup
      // 131: aload 1
      // 132: iload 5
      // 134: aload 2
      // 135: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getEndPosition ()I
      // 138: iload 5
      // 13a: isub
      // 13b: invokespecial net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.<init> ([BII)V
      // 13e: putfield net/rim/device/api/crypto/certificate/x509/Content._subject Lnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;
      // 141: aload 0
      // 142: aload 2
      // 143: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getStartPosition ()I
      // 146: putfield net/rim/device/api/crypto/certificate/x509/Content._encodedKeyOffset I
      // 149: aload 2
      // 14a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 14d: aload 0
      // 14e: aload 2
      // 14f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getEndPosition ()I
      // 152: aload 0
      // 153: getfield net/rim/device/api/crypto/certificate/x509/Content._encodedKeyOffset I
      // 156: isub
      // 157: putfield net/rim/device/api/crypto/certificate/x509/Content._encodedKeyLength I
      // 15a: aload 0
      // 15b: new java/io/ByteArrayInputStream
      // 15e: dup
      // 15f: aload 0
      // 160: getfield net/rim/device/api/crypto/certificate/x509/Content._encoding [B
      // 163: aload 0
      // 164: getfield net/rim/device/api/crypto/certificate/x509/Content._encodedKeyOffset I
      // 167: aload 0
      // 168: getfield net/rim/device/api/crypto/certificate/x509/Content._encodedKeyLength I
      // 16b: invokespecial java/io/ByteArrayInputStream.<init> ([BII)V
      // 16e: ldc_w "X509"
      // 171: invokestatic net/rim/device/api/crypto/encoder/PublicKeyDecoder.decode (Ljava/io/InputStream;Ljava/lang/String;)Lnet/rim/device/api/crypto/PublicKey;
      // 174: putfield net/rim/device/api/crypto/certificate/x509/Content._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 177: goto 181
      // 17a: astore 6
      // 17c: goto 181
      // 17f: astore 6
      // 181: aload 0
      // 182: getfield net/rim/device/api/crypto/certificate/x509/Content._version I
      // 185: bipush 1
      // 186: if_icmpeq 192
      // 189: aload 0
      // 18a: getfield net/rim/device/api/crypto/certificate/x509/Content._version I
      // 18d: bipush 2
      // 18f: if_icmpne 1e2
      // 192: aload 2
      // 193: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 196: bipush 1
      // 197: if_icmpne 1ab
      // 19a: aload 0
      // 19b: aload 2
      // 19c: bipush 2
      // 19e: bipush 1
      // 19f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readBitString (II)Lnet/rim/device/api/crypto/asn1/ASN1BitSet;
      // 1a2: invokevirtual net/rim/device/api/crypto/asn1/ASN1BitSet.toByteArray ()[B
      // 1a5: putfield net/rim/device/api/crypto/certificate/x509/Content._issuerUniqueID [B
      // 1a8: goto 1b0
      // 1ab: aload 0
      // 1ac: aconst_null
      // 1ad: putfield net/rim/device/api/crypto/certificate/x509/Content._issuerUniqueID [B
      // 1b0: aload 2
      // 1b1: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 1b4: bipush 2
      // 1b6: if_icmpne 1cb
      // 1b9: aload 0
      // 1ba: aload 2
      // 1bb: bipush 2
      // 1bd: bipush 2
      // 1bf: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readBitString (II)Lnet/rim/device/api/crypto/asn1/ASN1BitSet;
      // 1c2: invokevirtual net/rim/device/api/crypto/asn1/ASN1BitSet.toByteArray ()[B
      // 1c5: putfield net/rim/device/api/crypto/certificate/x509/Content._subjectUniqueID [B
      // 1c8: goto 1d0
      // 1cb: aload 0
      // 1cc: aconst_null
      // 1cd: putfield net/rim/device/api/crypto/certificate/x509/Content._subjectUniqueID [B
      // 1d0: aload 0
      // 1d1: getfield net/rim/device/api/crypto/certificate/x509/Content._version I
      // 1d4: bipush 2
      // 1d6: if_icmpne 1e2
      // 1d9: aload 0
      // 1da: aload 0
      // 1db: aload 2
      // 1dc: invokespecial net/rim/device/api/crypto/certificate/x509/Content.readExtensions (Lnet/rim/device/api/crypto/asn1/ASN1InputByteArray;)[Lnet/rim/device/api/crypto/certificate/CertificateExtension;
      // 1df: putfield net/rim/device/api/crypto/certificate/x509/Content._extensions [Lnet/rim/device/api/crypto/certificate/CertificateExtension;
      // 1e2: aload 0
      // 1e3: invokespecial net/rim/device/api/crypto/certificate/x509/Content.setKeyUsage ()V
      // 1e6: aload 0
      // 1e7: aload 1
      // 1e8: invokestatic net/rim/device/api/crypto/HashCodeCalculator.getCRC32 ([B)I
      // 1eb: putfield net/rim/device/api/crypto/certificate/x509/Content._encodedFormHashCode I
      // 1ee: return
      // 1ef: astore 2
      // 1f0: new net/rim/device/api/crypto/certificate/CertificateParsingException
      // 1f3: dup
      // 1f4: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 1f7: athrow
      // try (176 -> 189): 190 null
      // try (176 -> 189): 192 null
      // try (5 -> 246): 247 net/rim/device/api/crypto/asn1/ASN1EncodingException
   }

   public final byte[] getEncoding() {
      return CryptoUtilities.getImmutableOrCopiedByteArray(this._encoding);
   }

   public final byte[] getEncoding(int field) {
      try {
         switch (field) {
            case 1:
               ASN1InputStream keyStream = new ASN1InputStream(new ByteArrayInputStream(this._encoding, this._encodedKeyOffset, this._encodedKeyLength));
               ASN1InputStream keySequence = keyStream.readSequence();
               keySequence.readSequence();
               return keySequence.readBitString().toByteArray();
         }
      } catch (ASN1EncodingException var6) {
         return null;
      } finally {
         return null;
      }

      return null;
   }

   public final String getType() {
      return "X509";
   }

   public final void verify(KeyStore keystore) {
      if (keystore == null) {
         throw new IllegalArgumentException();
      }

      try {
         if (!keystore.existsIndex(-1581141357654337861L)) {
            keystore.addIndex(new SubjectKeyStoreIndex());
         }

         Enumeration enumeration = keystore.elements(-1581141357654337861L, this._issuer);
         if (!enumeration.hasMoreElements()) {
            throw new NoIssuerFoundException();
         }

         PublicKey issuerKey = ((KeyStoreData)enumeration.nextElement()).getPublicKey();
         if (issuerKey == null) {
            throw new CertificateVerificationException();
         }

         this.verify(issuerKey);
      } finally {
         return;
      }
   }

   public final void verify(PublicKey issuerPublicKey) throws CertificateVerificationException {
      if (issuerPublicKey == null) {
         throw new IllegalArgumentException();
      }

      if (this._issuerPublicKey != null) {
         if (!this._issuerPublicKey.equals(issuerPublicKey)) {
            throw new CertificateVerificationException();
         }
      } else {
         try {
            DecodedSignature decoder = SignatureDecoder.decode(new ByteArrayInputStream(this._encoding, this._signatureOffset, this._signatureLength), "X509");
            SignatureVerifier verifier = decoder.getVerifier(issuerPublicKey);
            verifier.update(this._encoding, this._tbsCertificateOffset, this._tbsCertificateLength);
            if (!verifier.verify()) {
               throw new CertificateVerificationException();
            }

            if (this._publicKey == null && this._issuerPublicKeyCryptoSystem == null) {
               this._issuerPublicKeyCryptoSystem = issuerPublicKey.getCryptoSystem();
            }

            if (this._issuerPublicKey == null) {
               this._issuerPublicKey = issuerPublicKey;
            }
         } finally {
            throw new CertificateVerificationException();
         }
      }
   }

   public final void verify() throws CertificateVerificationException, NoIssuerFoundException {
      if (this.isRoot()) {
         try {
            this.verify(this.getPublicKey());
         } finally {
            throw new CertificateVerificationException();
         }
      } else {
         throw new NoIssuerFoundException();
      }
   }

   public final boolean isRoot() {
      return this._subject.equals(this._issuer);
   }

   public final boolean isCA() {
      return this.getBasicConstraints() >= 0;
   }

   public final byte[] getSubjectKeyIdentifier() {
      try {
         CertificateExtension subjectKeyID = this.getExtension(OIDs.getOID(-1252329557));
         if (subjectKeyID == null) {
            return null;
         } else {
            return subjectKeyID.getCritical() ? null : new ASN1InputByteArray(subjectKeyID.getValue()).readOctetString();
         }
      } catch (ASN1EncodingException e) {
         return null;
      }
   }

   public final String[] getAuthorityCertIssuer() {
      try {
         CertificateExtension authorityKeyID = this.getExtension(OIDs.getOID(-1250953301));
         if (authorityKeyID == null) {
            return null;
         }

         ASN1InputByteArray asn1Stream = new ASN1InputByteArray(authorityKeyID.getValue());
         asn1Stream.readSequence();
         if ((asn1Stream.peekNextTag() & 31) == 0) {
            asn1Stream.skipField();
         }

         if ((asn1Stream.peekNextTag() & 31) != 1) {
            return null;
         }

         asn1Stream.readSequence(2, 1);
         String[] array = new String[0];
         int endOffset = asn1Stream.getEndPosition();

         while (asn1Stream.getStartPosition() < endOffset) {
            int tag = asn1Stream.peekNextTag() & 31;
            if (tag != 1 && tag != 2 && tag != 6) {
               asn1Stream.skipField();
            } else {
               Arrays.add(array, asn1Stream.readIA5String(2, tag));
            }
         }

         return array.length > 0 ? array : null;
      } catch (ASN1EncodingException e) {
         return null;
      }
   }

   public final byte[] getAuthorityCertSerialNumber() {
      try {
         CertificateExtension authorityKeyID = this.getExtension(OIDs.getOID(-1250953301));
         if (authorityKeyID == null) {
            return null;
         }

         ASN1InputByteArray asn1Stream = new ASN1InputByteArray(authorityKeyID.getValue());
         asn1Stream.readSequence();
         if ((asn1Stream.peekNextTag() & 31) == 0) {
            asn1Stream.skipField();
         }

         if ((asn1Stream.peekNextTag() & 31) == 1) {
            asn1Stream.skipField();
         }

         return (asn1Stream.peekNextTag() & 31) != 2 ? null : asn1Stream.readIntegerAsByteArray(2, 2);
      } catch (ASN1EncodingException e) {
         return null;
      }
   }

   public final String[] getSubjectAltNameStrings(int type) {
      CertificateExtension subjectKeyID = this.getExtension(OIDs.getOID(-1252132949));
      return subjectKeyID == null ? null : this.getAltNameStrings(subjectKeyID.getValue(), type);
   }

   public final String[] getIssuerAltNameStrings(int type) {
      CertificateExtension issuerKeyID = this.getExtension(OIDs.getOID(-1252067413));
      return issuerKeyID == null ? null : this.getAltNameStrings(issuerKeyID.getValue(), type);
   }

   private final String[] getAltNameStrings(byte[] encoding, int type) {
      int tagToSearchFor;
      switch (type) {
         case 9:
            return null;
         case 10:
         default:
            tagToSearchFor = 1;
            break;
         case 11:
            tagToSearchFor = 2;
            break;
         case 12:
            tagToSearchFor = 6;
      }

      String[] array = new String[0];

      try {
         ASN1InputByteArray asn1Stream = new ASN1InputByteArray(encoding);
         asn1Stream.readSequence();
         int endOffset = asn1Stream.getEndPosition();

         while (asn1Stream.getStartPosition() < endOffset) {
            int nextTag = asn1Stream.peekNextTag();
            if ((nextTag & 31) == tagToSearchFor) {
               Arrays.add(array, asn1Stream.readIA5String(2, nextTag));
            } else {
               asn1Stream.skipField();
            }
         }
      } catch (ASN1EncodingException var8) {
      }

      return array.length > 0 ? array : null;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final PublicKey getPublicKey() throws InvalidCryptoSystemException {
      if (this._publicKey != null) {
         return this._publicKey;
      }

      try {
         this._publicKey = PublicKeyDecoder.decode(
            new ByteArrayInputStream(this._encoding, this._encodedKeyOffset, this._encodedKeyLength), "X509", this._issuerPublicKeyCryptoSystem, null
         );
         return this._publicKey;
      } catch (Throwable var3) {
         throw new InvalidCryptoSystemException(e.toString());
      }
   }

   public final String getPublicKeyAlgorithm() {
      try {
         ASN1InputByteArray input = new ASN1InputByteArray(this._encoding, this._encodedKeyOffset);
         input.readSequence();
         input.readSequence();
         return OIDs.getAssociatedString(-5979163936319872658L, input.readOID());
      } catch (ASN1EncodingException e) {
         return null;
      }
   }

   public final String getSignatureAlgorithm() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: new java/io/ByteArrayInputStream
      // 03: dup
      // 04: aload 0
      // 05: getfield net/rim/device/api/crypto/certificate/x509/Content._encoding [B
      // 08: aload 0
      // 09: getfield net/rim/device/api/crypto/certificate/x509/Content._signatureOffset I
      // 0c: aload 0
      // 0d: getfield net/rim/device/api/crypto/certificate/x509/Content._signatureLength I
      // 10: invokespecial java/io/ByteArrayInputStream.<init> ([BII)V
      // 13: ldc_w "X509"
      // 16: invokestatic net/rim/device/api/crypto/encoder/SignatureDecoder.decode (Ljava/io/InputStream;Ljava/lang/String;)Lnet/rim/device/api/crypto/encoder/DecodedSignature;
      // 19: astore 1
      // 1a: aload 1
      // 1b: invokevirtual net/rim/device/api/crypto/encoder/DecodedSignature.getAlgorithm ()Ljava/lang/String;
      // 1e: areturn
      // 1f: astore 1
      // 20: aconst_null
      // 21: areturn
      // 22: astore 1
      // 23: aconst_null
      // 24: areturn
      // try (0 -> 14): 15 null
      // try (0 -> 14): 18 null
   }

   public final boolean isValid() {
      return this.isValid(System.currentTimeMillis());
   }

   public final boolean isValid(long date) {
      return this._notValidBefore <= date && date <= this._notValidAfter;
   }

   public final int getVersion() {
      return this._version;
   }

   public final byte[] getSerialNumber() {
      return CryptoUtilities.getImmutableOrCopiedByteArray(this._signedSerialNumber.getValue());
   }

   public final ASN1SignedByteArray getSignedSerialNumber() {
      return this._signedSerialNumber.getImmutableOrCopy();
   }

   public final DistinguishedName getIssuer() {
      return this._issuer;
   }

   public final DistinguishedName getSubject() {
      return this._subject;
   }

   public final long getNotBefore() {
      return this._notValidBefore;
   }

   public final long getNotAfter() {
      return this._notValidAfter;
   }

   public final byte[] getIssuerUniqueIdentifier() {
      return CryptoUtilities.getImmutableOrCopiedByteArray(this._issuerUniqueID);
   }

   public final byte[] getSubjectUniqueIdentifier() {
      return CryptoUtilities.getImmutableOrCopiedByteArray(this._subjectUniqueID);
   }

   public final CertificateExtension getExtension(OID oid) {
      if (this._extensions == null) {
         return null;
      }

      for (int i = this._extensions.length - 1; i >= 0; i--) {
         if (oid.equals(this._extensions[i].getOID())) {
            return this._extensions[i];
         }
      }

      return null;
   }

   public final CertificateExtension[] getExtensions() {
      if (this._extensions == null) {
         return null;
      }

      CertificateExtension[] extensions = new CertificateExtension[this._extensions.length];
      System.arraycopy(this._extensions, 0, extensions, 0, extensions.length);
      return extensions;
   }

   public final CertificateExtension[] getExtensions(boolean criticalBit) {
      if (this._extensions == null) {
         return null;
      }

      CertificateExtension[] extensions = new CertificateExtension[0];
      int numExtensions = this._extensions.length;

      for (int i = 0; i < numExtensions; i++) {
         if (this._extensions[i].getCritical() == criticalBit) {
            Arrays.add(extensions, this._extensions[i]);
         }
      }

      return extensions.length > 0 ? extensions : null;
   }

   public final int getBasicConstraints() {
      CertificateExtension extension = this.getExtension(OIDs.getOID(-1252001877));
      if (extension != null) {
         byte[] basicConstraintsExtensionValue = extension.getValue();
         if (basicConstraintsExtensionValue != null) {
            try {
               ASN1InputByteArray basicConstraintsSequence = new ASN1InputByteArray(basicConstraintsExtensionValue);
               basicConstraintsSequence.readSequence();
               if (basicConstraintsSequence.peekNextTag() == 1) {
                  basicConstraintsSequence.readBoolean();
                  if (basicConstraintsSequence.peekNextTag() == 2) {
                     return basicConstraintsSequence.readInteger();
                  }

                  return Integer.MAX_VALUE;
               }
            } catch (ASN1EncodingException e) {
               return -1;
            }
         }
      } else {
         extension = this.getExtension(OIDs.getOID(-1252001878));
         if (extension != null) {
            byte[] basicConstraintsExtensionValue = extension.getValue();
            if (basicConstraintsExtensionValue != null) {
               try {
                  ASN1InputByteArray basicConstraintsSequence = new ASN1InputByteArray(basicConstraintsExtensionValue);
                  basicConstraintsSequence.readSequence();
                  ASN1BitSet bitSet = basicConstraintsSequence.readBitString();
                  if (bitSet.isSet(0)) {
                     return Integer.MAX_VALUE;
                  }
               } catch (ASN1EncodingException e) {
                  return -1;
               }
            }
         }
      }

      return -1;
   }

   public final boolean checkAgainstNameConstraints(X509Certificate cert) {
      boolean allowed = true;

      try {
         CertificateExtension extension = this.getExtension(OIDs.getOID(-1251280981));
         if (extension != null) {
            String[] email = cert.getSubjectAltNameStrings(10);
            String additionalEmail = cert.getSubject().getEmailAddress();
            if (additionalEmail != null) {
               if (email == null) {
                  email = new String[]{additionalEmail};
               } else {
                  Array.resize(email, email.length + 1);
                  email[email.length - 1] = additionalEmail;
               }
            }

            String[] dns = cert.getSubjectAltNameStrings(11);
            String[] url = cert.getSubjectAltNameStrings(12);
            byte[] nameConstraintsExtensionValue = extension.getValue();
            ASN1InputByteArray nameConstraintsSequence = new ASN1InputByteArray(nameConstraintsExtensionValue);
            nameConstraintsSequence.readSequence();
            if ((nameConstraintsSequence.peekNextTag() & 31) == 0) {
               allowed = this.containedInConstraint(nameConstraintsSequence.readFieldAsByteArray(), 0, email, dns, url);
            }

            if ((nameConstraintsSequence.peekNextTag() & 31) == 1) {
               return !this.containedInConstraint(nameConstraintsSequence.readFieldAsByteArray(), 1, email, dns, url);
            }
         }
      } catch (ASN1EncodingException var10) {
      }

      return allowed;
   }

   private final boolean containedInConstraint(byte[] encoding, int tag, String[] email, String[] dns, String[] url) {
      boolean found = false;
      ASN1InputByteArray constraints = new ASN1InputByteArray(encoding);
      constraints.readSequence(2, tag);
      int endOffset = constraints.getEndPosition();

      while (constraints.getStartPosition() < endOffset) {
         constraints.readSequence();
         switch (constraints.peekNextTag()) {
            case 1:
               String emailRestriction = constraints.readIA5String(2, 1);
               if (email == null) {
                  break;
               }

               boolean exactMatch = emailRestriction.indexOf(64) >= 0;
               boolean exactHost = emailRestriction.charAt(0) != '.';

               for (int i = 0; i < email.length; i++) {
                  int index = email[i].indexOf(64);
                  if (index != -1) {
                     String host = email[i].substring(index + 1);
                     if (exactMatch && email[i].equals(emailRestriction)
                        || exactHost && host.equals(emailRestriction)
                        || !exactHost && host.endsWith(emailRestriction)) {
                        found = true;
                        break;
                     }
                  }
               }
               break;
            case 2:
               String dnsRestriction = constraints.readIA5String(2, 2);
               if (dns != null) {
                  for (int i = 0; i < dns.length; i++) {
                     if (dns[i].endsWith(dnsRestriction)) {
                        found = true;
                        break;
                     }
                  }
               }
               break;
            case 6:
               String urlRestriction = constraints.readIA5String(2, 6);
               boolean exactURL = urlRestriction.charAt(0) != '.';
               if (url != null) {
                  for (int i = 0; i < url.length; i++) {
                     if (exactURL && url[i].equals(urlRestriction) || !exactURL && url[i].endsWith(urlRestriction)) {
                        found = true;
                        break;
                     }
                  }
               }
               break;
            default:
               constraints.skipField();
         }
      }

      return found;
   }

   @Override
   public final int hashCode() {
      return this._encodedFormHashCode;
   }

   @Override
   public final String toString() {
      return "X.509 Certificate for: '" + this.getSubject().toString() + '\'';
   }

   private final CertificateExtension[] readExtensions(ASN1InputByteArray inputStream) {
      if (inputStream.peekNextTag() != 3) {
         return null;
      }

      inputStream.readSequence(1, 3);
      Vector extensionsVector = new Vector();
      int endOffset = inputStream.getEndPosition();

      while (inputStream.getStartPosition() < endOffset) {
         inputStream.readSequence();
         OID extnID = inputStream.readOID();
         boolean critical = false;
         if (inputStream.peekNextTag() == 1) {
            critical = inputStream.readBoolean();
         }

         byte[] extnValue = inputStream.readOctetString();
         extensionsVector.addElement(new CertificateExtension(extnID, critical, extnValue));
      }

      CertificateExtension[] extensions = new CertificateExtension[extensionsVector.size()];

      for (int i = 0; i < extensions.length; i++) {
         extensions[i] = (CertificateExtension)extensionsVector.elementAt(i);
      }

      return extensions;
   }

   public final int queryKeyUsage(long purpose) {
      if (purpose == 1) {
         return this.checkKeyUsage(0);
      } else if (purpose == 2) {
         return this.checkKeyUsage(1);
      } else if (purpose == 4) {
         return this.checkKeyUsage(2);
      } else if (purpose == 8) {
         return this.checkKeyUsage(3);
      } else if (purpose == 16) {
         return this.checkKeyUsage(4);
      } else if (purpose == 32) {
         return this.checkKeyUsage(5);
      } else if (purpose == 64) {
         return this.checkKeyUsage(6);
      } else if (purpose == 128) {
         return this.checkKeyUsage(7);
      } else if (purpose == 256) {
         return this.checkKeyUsage(8);
      } else if (purpose == 512) {
         return this.checkExtendedKeyUsage(purpose);
      } else if (purpose == 1024) {
         return this.checkExtendedKeyUsage(purpose);
      } else if (purpose == 2048) {
         return this.checkExtendedKeyUsage(purpose);
      } else if (purpose == 4096) {
         return this.checkExtendedKeyUsage(purpose);
      } else if (purpose == 8192) {
         return this.checkExtendedKeyUsage(purpose);
      } else {
         return purpose == 16384 ? this.checkExtendedKeyUsage(purpose) : -1;
      }
   }

   private final int checkKeyUsage(int element) {
      if (this._keyUsageBitSet == null) {
         return 1;
      } else {
         return this._keyUsageBitSet.getLength() > element && this._keyUsageBitSet.isSet(element) ? 1 : 0;
      }
   }

   private final int checkExtendedKeyUsage(long purpose) {
      if (this._extendedKeyUsage == null) {
         return -1;
      }

      for (int i = 0; i < this._extendedKeyUsage.length; i++) {
         if (this._extendedKeyUsage[i] == purpose) {
            return 1;
         }
      }

      return 0;
   }

   private final void setKeyUsage() throws CertificateParsingException {
      CertificateExtension extension = this.getExtension(OIDs.getOID(-1252264021));
      if (extension != null) {
         byte[] keyUsageExtensionValue = extension.getValue();
         if (keyUsageExtensionValue == null) {
            throw new CertificateParsingException();
         }

         this._keyUsageBitSet = new ASN1InputByteArray(keyUsageExtensionValue).readBitString();
      }

      extension = this.getExtension(OIDs.getOID(-1250822229));
      if (extension != null) {
         byte[] keyUsageExtensionValue = extension.getValue();
         if (keyUsageExtensionValue == null) {
            throw new CertificateParsingException();
         }

         ASN1InputByteArray keyUsageOids = new ASN1InputByteArray(keyUsageExtensionValue);
         keyUsageOids.readSequence();
         this._extendedKeyUsage = new long[0];
         int endOffset = keyUsageOids.getEndPosition();

         while (keyUsageOids.getStartPosition() < endOffset) {
            OID oid = keyUsageOids.readOID();
            if (oid.equals(OIDs.getOID(-477712431))) {
               Arrays.add(this._extendedKeyUsage, 512);
            } else if (oid.equals(OIDs.getOID(-477711407))) {
               Arrays.add(this._extendedKeyUsage, 1024);
            } else if (oid.equals(OIDs.getOID(-477710383))) {
               Arrays.add(this._extendedKeyUsage, 2048);
            } else if (oid.equals(OIDs.getOID(-477709359))) {
               Arrays.add(this._extendedKeyUsage, 4096);
            } else if (oid.equals(OIDs.getOID(-477705263))) {
               Arrays.add(this._extendedKeyUsage, 8192);
            } else if (oid.equals(OIDs.getOID(-477712248))) {
               Arrays.add(this._extendedKeyUsage, 16384);
            }
         }
      }
   }

   public final String getSubjectFriendlyName(X509Certificate cert) {
      String name = CertificateUtilities.getSubjectFriendlyName(cert);
      if (name == null) {
         String[] emailAddresses = this.getSubjectAltNameStrings(10);
         if (emailAddresses != null && emailAddresses.length > 0) {
            return emailAddresses[0];
         }
      }

      return name;
   }

   public final CertificateDisplayField[] getCustomDisplayFields() {
      return null;
   }
}
