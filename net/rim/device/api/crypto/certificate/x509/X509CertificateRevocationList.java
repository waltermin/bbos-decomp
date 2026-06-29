package net.rim.device.api.crypto.certificate.x509;

import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateExtension;
import net.rim.device.api.crypto.certificate.CertificateRevocationList;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public class X509CertificateRevocationList implements CertificateRevocationList {
   private Hashtable _revocationList;
   private KeyStore _keyStore;
   private DistinguishedName _issuer;
   private long _thisUpdate;
   private long _nextUpdate;
   private byte[] _signature;
   private CertificateExtension[] _extensions;

   public X509CertificateRevocationList(InputStream in, KeyStore keyStore) {
      this(new ASN1InputStream(in), keyStore);
   }

   public X509CertificateRevocationList(ASN1InputStream param1, KeyStore param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial java/lang/Object.<init> ()V
      // 04: aload 1
      // 05: ifnull 0c
      // 08: aload 2
      // 09: ifnonnull 14
      // 0c: new java/lang/Object
      // 0f: dup
      // 10: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 13: athrow
      // 14: aload 0
      // 15: new java/lang/Object
      // 18: dup
      // 19: invokespecial java/util/Hashtable.<init> ()V
      // 1c: putfield net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList._revocationList Ljava/util/Hashtable;
      // 1f: aload 0
      // 20: aload 2
      // 21: putfield net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList._keyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 24: aload 0
      // 25: bipush 0
      // 26: i2l
      // 27: putfield net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList._thisUpdate J
      // 2a: aload 0
      // 2b: ldc2_w 9223372036854775807
      // 2e: putfield net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList._nextUpdate J
      // 31: aload 1
      // 32: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 35: astore 4
      // 37: aload 4
      // 39: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 3c: astore 3
      // 3d: aload 0
      // 3e: aload 3
      // 3f: invokespecial net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList.parseCertificateList ([B)V
      // 42: aload 4
      // 44: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 47: astore 5
      // 49: aload 4
      // 4b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 4e: astore 6
      // 50: aload 0
      // 51: aload 5
      // 53: arraylength
      // 54: aload 6
      // 56: arraylength
      // 57: iadd
      // 58: newarray 8
      // 5a: putfield net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList._signature [B
      // 5d: aload 5
      // 5f: bipush 0
      // 60: aload 0
      // 61: getfield net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList._signature [B
      // 64: bipush 0
      // 65: aload 5
      // 67: arraylength
      // 68: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 6b: aload 6
      // 6d: bipush 0
      // 6e: aload 0
      // 6f: getfield net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList._signature [B
      // 72: aload 5
      // 74: arraylength
      // 75: aload 6
      // 77: arraylength
      // 78: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 7b: goto 88
      // 7e: astore 4
      // 80: new java/lang/Object
      // 83: dup
      // 84: invokespecial net/rim/device/api/crypto/certificate/CRLEncodingException.<init> ()V
      // 87: athrow
      // 88: new java/lang/Object
      // 8b: dup
      // 8c: aload 0
      // 8d: getfield net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList._signature [B
      // 90: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 93: ldc_w "X509"
      // 96: invokestatic net/rim/device/api/crypto/encoder/SignatureDecoder.decode (Ljava/io/InputStream;Ljava/lang/String;)Lnet/rim/device/api/crypto/encoder/DecodedSignature;
      // 99: astore 4
      // 9b: aload 0
      // 9c: aload 0
      // 9d: getfield net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList._keyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // a0: aload 0
      // a1: getfield net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList._issuer Lnet/rim/device/api/crypto/certificate/DistinguishedName;
      // a4: invokespecial net/rim/device/api/crypto/certificate/x509/X509CertificateRevocationList.getCertByName (Lnet/rim/device/api/crypto/keystore/KeyStore;Lnet/rim/device/api/crypto/certificate/DistinguishedName;)Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // a7: astore 5
      // a9: aload 5
      // ab: ifnonnull b6
      // ae: new java/lang/Object
      // b1: dup
      // b2: invokespecial net/rim/device/api/crypto/certificate/CRLVerificationException.<init> ()V
      // b5: athrow
      // b6: aload 4
      // b8: aload 5
      // ba: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // bd: invokevirtual net/rim/device/api/crypto/encoder/DecodedSignature.getVerifier (Lnet/rim/device/api/crypto/PublicKey;)Lnet/rim/device/api/crypto/SignatureVerifier;
      // c0: astore 6
      // c2: aload 6
      // c4: aload 3
      // c5: invokeinterface net/rim/device/api/crypto/SignatureVerifier.update ([B)V 2
      // ca: aload 6
      // cc: invokeinterface net/rim/device/api/crypto/SignatureVerifier.verify ()Z 1
      // d1: ifne fa
      // d4: new java/lang/Object
      // d7: dup
      // d8: invokespecial net/rim/device/api/crypto/certificate/CRLVerificationException.<init> ()V
      // db: athrow
      // dc: astore 4
      // de: new java/lang/Object
      // e1: dup
      // e2: invokespecial net/rim/device/api/crypto/certificate/CRLVerificationException.<init> ()V
      // e5: athrow
      // e6: astore 4
      // e8: new java/lang/Object
      // eb: dup
      // ec: invokespecial net/rim/device/api/crypto/certificate/CRLEncodingException.<init> ()V
      // ef: athrow
      // f0: astore 4
      // f2: new java/lang/Object
      // f5: dup
      // f6: invokespecial net/rim/device/api/crypto/certificate/CRLVerificationException.<init> ()V
      // f9: athrow
      // fa: return
      // try (25 -> 65): 66 net/rim/device/api/crypto/asn1/ASN1EncodingException
      // try (71 -> 107): 107 null
      // try (71 -> 107): 112 null
      // try (71 -> 107): 117 null
   }

   private X509Certificate getCertByName(KeyStore keyStore, DistinguishedName name) {
      keyStore.addIndex((KeyStoreIndex)(new Object()));
      Enumeration elements = keyStore.elements(-1581141357654337861L, name);

      while (elements.hasMoreElements()) {
         Certificate certificate = ((KeyStoreData)elements.nextElement()).getCertificate();
         if (certificate instanceof X509Certificate) {
            return (X509Certificate)certificate;
         }
      }

      return null;
   }

   private void parseRevokedCertificates(ASN1InputStream revokedCertificates) {
      if (revokedCertificates.peekNextTag() == 16) {
         ASN1InputStream revokedCertificate = revokedCertificates.readSequence();

         while (!revokedCertificate.endOfStream()) {
            ASN1InputStream revokedCert = revokedCertificate.readSequence();
            X509CertificateRevocationList$RevokedInfo revokedInfo = new X509CertificateRevocationList$RevokedInfo();
            byte[] certificateSerial = revokedCert.readIntegerAsByteArray();
            if (revokedCert.peekNextTag() == 24) {
               revokedInfo.revocationDate = (Date)(new Object(revokedCert.readGeneralizedTime()));
            } else {
               if (revokedCert.peekNextTag() != 23) {
                  throw new ASN1EncodingException();
               }

               revokedInfo.revocationDate = (Date)(new Object(revokedCert.readUTCTime()));
            }

            if (!revokedCert.endOfStream()) {
               revokedInfo.extensions = this.readExtensions(new ASN1InputByteArray(revokedCert.readFieldAsByteArray()));
               if (revokedInfo.extensions != null) {
                  for (int i = 0; i < revokedInfo.extensions.length; i++) {
                     CertificateExtension extension = revokedInfo.extensions[i];
                     if (extension.getOID().equals(OIDs.getOID(-1251870805))) {
                        ASN1InputStream reason = new ASN1InputStream((InputStream)(new Object(extension.getValue())));
                        revokedInfo.revocationReason = reason.readEnumerated();
                     }
                  }
               }
            }

            this._revocationList.put(new Object(certificateSerial), revokedInfo);
         }

         if (revokedCertificates.peekNextTag() == 0) {
            this._extensions = this.readExtensions(new ASN1InputByteArray(revokedCertificates.readFieldAsByteArray()));
         }
      }
   }

   private void parseCertificateList(byte[] tbsCertList) {
      ASN1InputStream certificateList = new ASN1InputStream((InputStream)(new Object(tbsCertList))).readSequence();
      if (certificateList.peekNextTag() == 2) {
         int version = certificateList.readInteger();
         if (version != 0 && version != 1) {
            throw new Object();
         }
      }

      certificateList.readSequence();
      this._issuer = new X509DistinguishedName(certificateList);
      if (certificateList.peekNextTag() == 24) {
         this._thisUpdate = certificateList.readGeneralizedTime();
      } else {
         if (certificateList.peekNextTag() != 23) {
            throw new ASN1EncodingException();
         }

         this._thisUpdate = certificateList.readUTCTime();
      }

      if (certificateList.peekNextTag() == 24) {
         this._nextUpdate = certificateList.readGeneralizedTime();
      } else if (certificateList.peekNextTag() == 23) {
         this._nextUpdate = certificateList.readUTCTime();
      }

      this.parseRevokedCertificates(certificateList);
   }

   @Override
   public CertificateStatus getCertificateStatus(Certificate certificate) {
      return this.getCertificateStatus(certificate, System.currentTimeMillis());
   }

   private X509CertificateRevocationList$RevokedInfo getRevokedInfo(Certificate certificate) {
      if (certificate == null) {
         throw new Object();
      } else {
         return certificate.getIssuer().equals(this._issuer)
            ? (X509CertificateRevocationList$RevokedInfo)this._revocationList.get(new Object(certificate.getSerialNumber()))
            : null;
      }
   }

   @Override
   public CertificateStatus getCertificateStatus(Certificate certificate, long time) {
      if (!(certificate instanceof X509Certificate)) {
         throw new Object();
      }

      if (time >= this._thisUpdate && time <= this._nextUpdate) {
         X509CertificateRevocationList$RevokedInfo revokedInfo = this.getRevokedInfo(certificate);
         if (revokedInfo != null) {
            CertificateStatus status = (CertificateStatus)(new Object(
               1,
               this._thisUpdate,
               this._thisUpdate,
               this._nextUpdate,
               revokedInfo.revocationDate == null ? 0 : revokedInfo.revocationDate.getTime(),
               revokedInfo.revocationReason
            ));
            return status;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public long getThisUpdate() {
      return this._thisUpdate;
   }

   @Override
   public long getNextUpdate() {
      return this._nextUpdate;
   }

   @Override
   public DistinguishedName getIssuer() {
      return this._issuer;
   }

   @Override
   public CertificateExtension getCRLEntryExtension(Certificate certificate, OID oid) {
      if (certificate != null && oid != null) {
         X509CertificateRevocationList$RevokedInfo revokedInfo = this.getRevokedInfo(certificate);
         if (revokedInfo != null && revokedInfo.extensions != null) {
            int numExtensions = revokedInfo.extensions.length;

            for (int i = 0; i < numExtensions; i++) {
               if (oid.equals(revokedInfo.extensions[i].getOID())) {
                  return revokedInfo.extensions[i];
               }
            }
         }

         return null;
      } else {
         throw new Object();
      }
   }

   @Override
   public CertificateExtension[] getCRLEntryExtensions(Certificate certificate) {
      if (certificate == null) {
         throw new Object();
      } else {
         X509CertificateRevocationList$RevokedInfo revokedInfo = this.getRevokedInfo(certificate);
         if (revokedInfo != null && revokedInfo.extensions != null) {
            CertificateExtension[] extensions = new Object[revokedInfo.extensions.length];
            System.arraycopy(revokedInfo.extensions, 0, extensions, 0, extensions.length);
            return extensions;
         } else {
            return null;
         }
      }
   }

   @Override
   public CertificateExtension[] getCRLEntryExtensions(Certificate certificate, boolean criticalBit) {
      if (certificate == null) {
         throw new Object();
      }

      X509CertificateRevocationList$RevokedInfo revokedInfo = this.getRevokedInfo(certificate);
      if (revokedInfo != null && revokedInfo.extensions != null) {
         CertificateExtension[] extensions = new Object[this._extensions.length];
         int count = 0;

         for (int i = 0; i < revokedInfo.extensions.length; i++) {
            if (revokedInfo.extensions[i].getCritical() == criticalBit) {
               extensions[count++] = revokedInfo.extensions[i];
            }
         }

         if (count > 0) {
            Array.resize(extensions, count);
            return extensions;
         }
      }

      return null;
   }

   @Override
   public CertificateExtension getExtension(OID oid) {
      if (oid == null) {
         throw new Object();
      }

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

   @Override
   public CertificateExtension[] getExtensions() {
      if (this._extensions == null) {
         return null;
      }

      CertificateExtension[] extensions = new Object[this._extensions.length];
      System.arraycopy(this._extensions, 0, extensions, 0, this._extensions.length);
      return extensions;
   }

   @Override
   public CertificateExtension[] getExtensions(boolean criticalBit) {
      if (this._extensions == null) {
         return null;
      }

      CertificateExtension[] extensions = new Object[this._extensions.length];
      int count = 0;

      for (int i = this._extensions.length - 1; i >= 0; i--) {
         if (this._extensions[i].getCritical() == criticalBit) {
            extensions[count++] = this._extensions[i];
         }
      }

      if (count == 0) {
         return null;
      }

      Array.resize(extensions, count);
      return extensions;
   }

   @Override
   public boolean equals(Object obj2) {
      if (this == obj2) {
         return true;
      }

      if (!(obj2 instanceof X509CertificateRevocationList)) {
         return false;
      }

      X509CertificateRevocationList otherCRL = (X509CertificateRevocationList)obj2;
      return Arrays.equals(this._signature, otherCRL._signature);
   }

   private CertificateExtension[] readExtensions(ASN1InputByteArray inputStream) {
      if (inputStream.peekNextTag() != 0 && inputStream.peekNextTag() != 16) {
         return null;
      }

      if (inputStream.peekNextTag() == 0) {
         inputStream.readSequence(1, 0);
      } else {
         inputStream.readSequence();
      }

      Vector extensionsVector = (Vector)(new Object());
      int endOffset = inputStream.getEndPosition();

      while (inputStream.getStartPosition() < endOffset) {
         inputStream.readSequence();
         OID extnID = inputStream.readOID();
         boolean critical = false;
         if (inputStream.peekNextTag() == 1) {
            critical = inputStream.readBoolean();
         }

         byte[] extnValue = inputStream.readOctetString();
         extensionsVector.addElement(new Object(extnID, critical, extnValue));
      }

      CertificateExtension[] extensions = new Object[extensionsVector.size()];

      for (int i = 0; i < extensions.length; i++) {
         extensions[i] = (CertificateExtension)extensionsVector.elementAt(i);
      }

      return extensions;
   }
}
