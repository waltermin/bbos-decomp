package net.rim.device.api.crypto.cms;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SignatureVerifier;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.SerialNumberIssuerKeyStoreIndex;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.certificate.x509.X509CertificateRevocationList;
import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;
import net.rim.device.api.crypto.encoder.DecodedSignature;
import net.rim.device.api.crypto.encoder.SignatureDecoder;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class CMSSignedDataInputStream extends CMSInputStream {
   private KeyStore _keyStore;
   private int _version;
   private byte[] _dataBuffer;
   private byte[] _signerInfos;
   private ASN1InputStream _certificates;
   private ASN1InputStream _rimCertificates;
   private byte[] _crls;
   private X509CertificateRevocationList _crl;
   private CMSEntityIdentifier[] _signers;
   private X509Certificate[] _pool;
   private CMSReceiptData _receiptData;
   private byte[] _signedContentIdentifier;
   private String[] _receiptsFrom;
   private String[] _receiptsTo;
   private boolean _isVerificationPossible;
   private boolean _displayUI;
   private boolean _contentComplete;
   private static final byte[] CRLF = new byte[]{13, 10};

   CMSSignedDataInputStream(InputStream inputStream, KeyStore keyStore) {
      this(inputStream, keyStore, false, true);
   }

   CMSSignedDataInputStream(InputStream param1, KeyStore param2, boolean param3, boolean param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 1
      // 002: invokespecial net/rim/device/api/crypto/cms/CMSInputStream.<init> (Ljava/io/InputStream;)V
      // 005: aload 0
      // 006: aload 2
      // 007: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._keyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 00a: aload 0
      // 00b: iload 4
      // 00d: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._displayUI Z
      // 010: iload 3
      // 011: ifne 018
      // 014: aload 0
      // 015: invokespecial net/rim/device/api/crypto/cms/CMSSignedDataInputStream.addKeyStoreIndices ()V
      // 018: new java/lang/Object
      // 01b: dup
      // 01c: aload 0
      // 01d: getfield net/rim/device/api/crypto/cms/CMSInputStream._input Ljava/io/InputStream;
      // 020: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 023: astore 5
      // 025: aload 5
      // 027: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 02a: astore 6
      // 02c: aload 0
      // 02d: aload 6
      // 02f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // 032: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._version I
      // 035: aload 0
      // 036: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._version I
      // 039: bipush 1
      // 03a: if_icmpeq 057
      // 03d: aload 0
      // 03e: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._version I
      // 041: bipush 3
      // 043: if_icmpeq 057
      // 046: aload 0
      // 047: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._version I
      // 04a: bipush 4
      // 04c: if_icmpeq 057
      // 04f: new net/rim/device/api/crypto/cms/CMSParsingException
      // 052: dup
      // 053: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 056: athrow
      // 057: aload 6
      // 059: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.skipField ()V
      // 05c: aload 6
      // 05e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 061: astore 7
      // 063: aload 0
      // 064: aload 7
      // 066: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 069: putfield net/rim/device/api/crypto/cms/CMSInputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 06c: aload 7
      // 06e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 071: ifeq 081
      // 074: aload 0
      // 075: aconst_null
      // 076: putfield net/rim/device/api/crypto/cms/CMSInputStream._data Ljava/io/InputStream;
      // 079: aload 0
      // 07a: aconst_null
      // 07b: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._dataBuffer [B
      // 07e: goto 1a7
      // 081: new java/lang/Object
      // 084: dup
      // 085: aload 7
      // 087: bipush 0
      // 088: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readStreamWithTag (I)Ljava/io/InputStream;
      // 08b: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 08e: astore 8
      // 090: aload 8
      // 092: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 095: bipush 4
      // 097: if_icmpne 0a6
      // 09a: aload 0
      // 09b: aload 8
      // 09d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 0a0: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._dataBuffer [B
      // 0a3: goto 0af
      // 0a6: aload 0
      // 0a7: aload 8
      // 0a9: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 0ac: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._dataBuffer [B
      // 0af: aload 0
      // 0b0: getfield net/rim/device/api/crypto/cms/CMSInputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 0b3: ldc_w 542121532
      // 0b6: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 0b9: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 0bc: ifeq 0e1
      // 0bf: aload 0
      // 0c0: new net/rim/device/api/crypto/cms/CMSSignedDataInputStream
      // 0c3: dup
      // 0c4: new java/lang/Object
      // 0c7: dup
      // 0c8: aload 0
      // 0c9: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._dataBuffer [B
      // 0cc: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 0cf: aload 0
      // 0d0: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._keyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 0d3: iload 3
      // 0d4: aload 0
      // 0d5: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._displayUI Z
      // 0d8: invokespecial net/rim/device/api/crypto/cms/CMSSignedDataInputStream.<init> (Ljava/io/InputStream;Lnet/rim/device/api/crypto/keystore/KeyStore;ZZ)V
      // 0db: putfield net/rim/device/api/crypto/cms/CMSInputStream._data Ljava/io/InputStream;
      // 0de: goto 1a7
      // 0e1: aload 0
      // 0e2: getfield net/rim/device/api/crypto/cms/CMSInputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 0e5: ldc_w 542383676
      // 0e8: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 0eb: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 0ee: ifeq 114
      // 0f1: aload 0
      // 0f2: new net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream
      // 0f5: dup
      // 0f6: new java/lang/Object
      // 0f9: dup
      // 0fa: aload 0
      // 0fb: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._dataBuffer [B
      // 0fe: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 101: aload 0
      // 102: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._keyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 105: aconst_null
      // 106: iload 3
      // 107: aload 0
      // 108: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._displayUI Z
      // 10b: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.<init> (Ljava/io/InputStream;Lnet/rim/device/api/crypto/keystore/KeyStore;Lnet/rim/device/api/crypto/SymmetricKey;ZZ)V
      // 10e: putfield net/rim/device/api/crypto/cms/CMSInputStream._data Ljava/io/InputStream;
      // 111: goto 1a7
      // 114: aload 0
      // 115: getfield net/rim/device/api/crypto/cms/CMSInputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 118: ldc_w -1721352904
      // 11b: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 11e: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 121: ifeq 146
      // 124: aload 0
      // 125: new net/rim/device/api/crypto/cms/CMSCompressedDataInputStream
      // 128: dup
      // 129: new java/lang/Object
      // 12c: dup
      // 12d: aload 0
      // 12e: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._dataBuffer [B
      // 131: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 134: aload 0
      // 135: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._keyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 138: iload 3
      // 139: aload 0
      // 13a: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._displayUI Z
      // 13d: invokespecial net/rim/device/api/crypto/cms/CMSCompressedDataInputStream.<init> (Ljava/io/InputStream;Lnet/rim/device/api/crypto/keystore/KeyStore;ZZ)V
      // 140: putfield net/rim/device/api/crypto/cms/CMSInputStream._data Ljava/io/InputStream;
      // 143: goto 1a7
      // 146: aload 0
      // 147: getfield net/rim/device/api/crypto/cms/CMSInputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 14a: ldc_w -1721352925
      // 14d: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 150: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 153: ifeq 16f
      // 156: aload 0
      // 157: new net/rim/device/api/crypto/cms/CMSSignedReceiptInputStream
      // 15a: dup
      // 15b: new java/lang/Object
      // 15e: dup
      // 15f: aload 0
      // 160: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._dataBuffer [B
      // 163: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 166: invokespecial net/rim/device/api/crypto/cms/CMSSignedReceiptInputStream.<init> (Ljava/io/InputStream;)V
      // 169: putfield net/rim/device/api/crypto/cms/CMSInputStream._data Ljava/io/InputStream;
      // 16c: goto 1a7
      // 16f: aload 0
      // 170: getfield net/rim/device/api/crypto/cms/CMSInputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 173: ldc_w -477712249
      // 176: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 179: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 17c: ifeq 198
      // 17f: aload 0
      // 180: new net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream
      // 183: dup
      // 184: new java/lang/Object
      // 187: dup
      // 188: aload 0
      // 189: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._dataBuffer [B
      // 18c: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 18f: invokespecial net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream.<init> (Ljava/io/InputStream;)V
      // 192: putfield net/rim/device/api/crypto/cms/CMSInputStream._data Ljava/io/InputStream;
      // 195: goto 1a7
      // 198: aload 0
      // 199: new java/lang/Object
      // 19c: dup
      // 19d: aload 0
      // 19e: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._dataBuffer [B
      // 1a1: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 1a4: putfield net/rim/device/api/crypto/cms/CMSInputStream._data Ljava/io/InputStream;
      // 1a7: aload 0
      // 1a8: getfield net/rim/device/api/crypto/cms/CMSInputStream._data Ljava/io/InputStream;
      // 1ab: ifnull 1bd
      // 1ae: aload 6
      // 1b0: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 1b3: bipush -1
      // 1b5: if_icmpeq 1bd
      // 1b8: aload 0
      // 1b9: bipush 1
      // 1ba: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._contentComplete Z
      // 1bd: aload 6
      // 1bf: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 1c2: ifne 1d1
      // 1c5: aload 0
      // 1c6: aload 6
      // 1c8: bipush 2
      // 1ca: bipush 0
      // 1cb: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSet (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 1ce: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._certificates Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 1d1: aload 6
      // 1d3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 1d6: bipush 1
      // 1d7: if_icmpne 1ed
      // 1da: aload 6
      // 1dc: bipush 2
      // 1de: bipush 1
      // 1df: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSet (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 1e2: astore 8
      // 1e4: aload 0
      // 1e5: aload 8
      // 1e7: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 1ea: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._crls [B
      // 1ed: aload 0
      // 1ee: aload 6
      // 1f0: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 1f3: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._signerInfos [B
      // 1f6: aload 0
      // 1f7: bipush 1
      // 1f8: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._isVerificationPossible Z
      // 1fb: aload 6
      // 1fd: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 200: bipush 30
      // 202: if_icmpne 212
      // 205: aload 0
      // 206: aload 6
      // 208: bipush 2
      // 20a: bipush 30
      // 20c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSet (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 20f: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._rimCertificates Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 212: aload 6
      // 214: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 217: bipush 29
      // 219: if_icmpne 241
      // 21c: aload 6
      // 21e: bipush 2
      // 220: bipush 29
      // 222: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSet (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 225: astore 8
      // 227: aload 0
      // 228: aload 8
      // 22a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 22d: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._crls [B
      // 230: return
      // 231: astore 5
      // 233: new net/rim/device/api/crypto/cms/CMSParsingException
      // 236: dup
      // 237: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 23a: athrow
      // 23b: astore 5
      // 23d: return
      // 23e: astore 5
      // 240: return
      // 241: return
      // try (13 -> 251): 252 null
      // try (13 -> 251): 257 null
      // try (13 -> 251): 259 null
   }

   public final boolean isVerificationPossible() {
      return this._isVerificationPossible;
   }

   final void continueInitialization(KeyStore keyStore, boolean displayUI) {
      this._keyStore = keyStore;
      this.addKeyStoreIndices();
      if (!(super._data instanceof CMSSignedDataInputStream)) {
         if (!(super._data instanceof CMSEnvelopedDataInputStream)) {
            if (!(super._data instanceof CMSCompressedDataInputStream)) {
               if (!(super._data instanceof CMSSignedReceiptInputStream)) {
                  if (!(super._data instanceof EMSAcceptRequestInputStream)) {
                     if (super._data != null) {
                        super._data.reset();
                     }
                  }
               }
            } else {
               ((CMSCompressedDataInputStream)super._data).continueInitialization(keyStore, displayUI);
            }
         } else {
            ((CMSEnvelopedDataInputStream)super._data).continueInitialization(keyStore, null, displayUI);
         }
      } else {
         ((CMSSignedDataInputStream)super._data).continueInitialization(keyStore, displayUI);
      }
   }

   private final void addKeyStoreIndices() {
      KeyStoreIndex[] indexArray = new Object[]{new Object(), new Object(), new Object()};
      if (this._keyStore != null) {
         this._keyStore.addIndices(indexArray);
      }
   }

   public final boolean isSignerCertificatePresent(CMSEntityIdentifier signer) {
      if (signer == null) {
         throw new Object();
      } else if (signer.getCreator() != this) {
         throw new CMSNoSuchEntityException();
      } else {
         return this.getSignerCertificate(signer) != null;
      }
   }

   public final boolean isSignedReceipt() {
      return super._data instanceof CMSSignedReceiptInputStream;
   }

   private final String[] readGeneralNames(ASN1InputByteArray input) {
      String[] names = new Object[0];
      input.readSequence();
      int endOffset = input.getEndPosition();

      while (input.getStartPosition() < endOffset) {
         int tag = input.peekNextTag() & 31;
         if (tag != 1 && tag != 2 && tag != 6) {
            input.skipField();
         } else {
            int length = names.length;
            Array.resize(names, length + 1);
            names[length] = input.readIA5String(2, tag);
         }
      }

      return names.length == 0 ? null : names;
   }

   private final void populateSignedReceiptValues(CMSAttribute receipt) {
      if (this._signedContentIdentifier == null) {
         try {
            if (receipt != null) {
               ASN1InputByteArray input = (ASN1InputByteArray)(new Object(receipt.getValue()));
               input.readSet();
               input.readSequence();
               this._signedContentIdentifier = input.readOctetString();
               if (input.peekNextTag() == 0) {
                  input.readInteger(2, 0);
                  this._receiptsFrom = null;
               } else if (input.peekNextTag() == 1) {
                  input.readSequence(2, 1);
                  this._receiptsFrom = this.appendNewReceipts(input, this._receiptsFrom);
               } else {
                  input.skipField();
               }

               input.readSequence();
               this._receiptsTo = this.appendNewReceipts(input, this._receiptsTo);
               return;
            }
         } finally {
            return;
         }
      }
   }

   public final boolean isSignedReceiptRequested(CMSEntityIdentifier signer, String recipient) {
      if (recipient != null && signer != null) {
         if (!this.isSignedReceipt()) {
            CMSAttribute receipt = this.getSignerAttribute(OIDs.getOID(-1721363152), signer);
            this.populateSignedReceiptValues(receipt);
            if (receipt != null) {
               if (this._receiptsFrom == null) {
                  return true;
               }

               int length = this._receiptsFrom.length;

               for (int i = 0; i < length; i++) {
                  if (recipient.equals(this._receiptsFrom[i])) {
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         throw new Object();
      }
   }

   public final boolean isSignedReceiptRequested() {
      if (!this.isSignedReceipt()) {
         try {
            CMSEntityIdentifier[] signers = this.getSigners();
            if (signers == null) {
               return false;
            }

            int length = signers.length;

            for (int i = 0; i < length; i++) {
               CMSAttribute receipt = this.getSignerAttribute(OIDs.getOID(-1721363152), signers[i]);
               if (receipt != null) {
                  return true;
               }
            }
         } catch (CMSNoSuchEntityException var5) {
            return false;
         } catch (CMSParsingException var6) {
         }
      }

      return false;
   }

   public final CMSReceiptData[] getReceiptInformation() {
      try {
         CMSEntityIdentifier[] signers = this.getSigners();
         if (signers == null) {
            return null;
         }

         CMSReceiptData[] receipts = new CMSReceiptData[0];

         for (int i = 0; i < signers.length; i++) {
            CMSEntityIdentifier signer = signers[i];
            CMSAttribute receipt = this.getSignerAttribute(OIDs.getOID(-1721363152), signer);
            if (receipt != null) {
               this.populateSignedReceiptValues(receipt);
               if (this._signedContentIdentifier != null) {
                  byte[] signatureEncoding = signer.getSignatureEncoding();
                  ASN1InputByteArray asn1 = (ASN1InputByteArray)(new Object(signatureEncoding));
                  asn1.skipField();
                  byte[] signatureValue = asn1.readFieldAsByteArray();
                  ASN1InputByteArray digestAlgorithm = (ASN1InputByteArray)(new Object(signer.getDigestAlgorithm()));
                  digestAlgorithm.readSequence();
                  OID digestOID = digestAlgorithm.readOID();
                  Digest digest = null;
                  if (digestOID.equals(OIDs.getOID(774767465))) {
                     digest = (Digest)(new Object());
                  } else if (digestOID.equals(OIDs.getOID(-472309042))) {
                     digest = (Digest)(new Object());
                  } else if (digestOID.equals(OIDs.getOID(540600180))) {
                     digest = (Digest)(new Object());
                  } else if (digestOID.equals(OIDs.getOID(540862324))) {
                     digest = (Digest)(new Object());
                  } else {
                     if (!digestOID.equals(OIDs.getOID(541124468))) {
                        continue;
                     }

                     digest = (Digest)(new Object());
                  }

                  try {
                     ASN1OutputStream sequence = (ASN1OutputStream)(new Object());
                     sequence.writeInteger(1);
                     sequence.writeOID(super._contentType);
                     sequence.writeOctetString(this._signedContentIdentifier);
                     sequence.writeRawByteArray(signatureValue);
                     ASN1OutputStream stream = (ASN1OutputStream)(new Object());
                     stream.writeSequence(sequence);
                     byte[] derEncoding = stream.toByteArray();
                     digest.update(derEncoding);
                  } finally {
                     continue;
                  }

                  byte[] msgDigest = digest.getDigest();
                  digest.reset();
                  digest.update(signer.getSignedAttributeArray());
                  byte[] msgSigDigest = digest.getDigest();
                  Array.resize(receipts, receipts.length + 1);
                  receipts[receipts.length - 1] = new CMSReceiptData(this._signedContentIdentifier, signatureValue, super._contentType, msgDigest, msgSigDigest);
               }
            }
         }

         return receipts;
      } catch (CMSException var22) {
         return null;
      } finally {
         ;
      }
   }

   public final String[] getSignedReceiptRequestors(CMSEntityIdentifier signer) {
      if (signer == null) {
         throw new Object();
      } else if (!this.isSignedReceipt()) {
         CMSAttribute receipt = this.getSignerAttribute(OIDs.getOID(-1721363152), signer);
         this.populateSignedReceiptValues(receipt);
         return this._receiptsTo;
      } else {
         return null;
      }
   }

   public final CMSSignedReceiptOutputStream createSignedReceiptStream(CMSSignedDataOutputStream output, CMSEntityIdentifier signer) {
      if (this.isSignedReceipt()) {
         return null;
      }

      CMSAttribute receipt = this.getSignerAttribute(OIDs.getOID(-1721363152), signer);
      if (receipt == null) {
         return null;
      }

      this.populateSignedReceiptValues(receipt);

      try {
         if (this._signedContentIdentifier == null) {
            return null;
         }

         byte[] signatureEncoding = signer.getSignatureEncoding();
         ASN1InputByteArray asn1 = (ASN1InputByteArray)(new Object(signatureEncoding));
         asn1.skipField();
         byte[] signatureValue = asn1.readFieldAsByteArray();
         CMSSignedReceiptOutputStream stream = new CMSSignedReceiptOutputStream(output, super._contentType, this._signedContentIdentifier, signatureValue);
         ASN1InputByteArray digestAlgorithm = (ASN1InputByteArray)(new Object(signer.getDigestAlgorithm()));
         digestAlgorithm.readSequence();
         OID digestOID = digestAlgorithm.readOID();
         Digest digest;
         if (digestOID.equals(OIDs.getOID(774767465))) {
            digest = (Digest)(new Object());
         } else if (digestOID.equals(OIDs.getOID(-472309042))) {
            digest = (Digest)(new Object());
         } else if (digestOID.equals(OIDs.getOID(540600180))) {
            digest = (Digest)(new Object());
         } else if (digestOID.equals(OIDs.getOID(540862324))) {
            digest = (Digest)(new Object());
         } else {
            if (!digestOID.equals(OIDs.getOID(541124468))) {
               throw new Object();
            }

            digest = (Digest)(new Object());
         }

         digest.update(signer.getSignedAttributeArray());
         output.setMsgSigDigest(digest.getDigest());
         return stream;
      } finally {
         ;
      }
   }

   public final CMSReceiptData setReceiptData(CMSReceiptData[] receiptData) {
      if (receiptData == null) {
         throw new Object();
      }

      this._receiptData = this.getStreamReceiptData(receiptData);
      return this._receiptData;
   }

   private final CMSReceiptData getStreamReceiptData(CMSReceiptData[] receiptInformation) {
      if (this.isSignedReceipt()) {
         CMSReceiptData check = ((CMSSignedReceiptInputStream)super._data).getCMSReceiptData();
         if (check == null) {
            return null;
         }

         int receiptLength = receiptInformation.length;

         for (int i = 0; i < receiptLength; i++) {
            if (check.equals(receiptInformation[i])) {
               return receiptInformation[i];
            }
         }
      }

      return null;
   }

   @Override
   public final boolean isSigned() {
      return true;
   }

   @Override
   public final boolean isEncrypted() {
      return !(super._data instanceof CMSInputStream) ? false : ((CMSInputStream)super._data).isEncrypted();
   }

   @Override
   public final int read(byte[] buffer, int offset, int length) {
      if (buffer == null || offset < 0 || length < 0 || buffer.length - length < offset) {
         throw new Object();
      } else {
         return super._data == null ? -1 : super._data.read(buffer, offset, length);
      }
   }

   @Override
   public final int available() {
      return super._data == null ? 0 : super._data.available();
   }

   @Override
   public final long skip(long n) {
      return super._data == null ? 0 : super._data.skip(n);
   }

   public final boolean isDataPresent() {
      return super._data != null;
   }

   @Override
   public final void setData(InputStream data) {
      int offset = 0;
      this._dataBuffer = new byte[100];

      while (true) {
         int len = data.read(this._dataBuffer, offset, 100);
         if (len == -1) {
            break;
         }

         if (len < 100) {
            byte[] tem = new byte[offset + len];
            System.arraycopy(this._dataBuffer, 0, tem, 0, offset + len);
            Array.resize(this._dataBuffer, offset + len);
            System.arraycopy(tem, 0, this._dataBuffer, 0, offset + len);
            break;
         }

         Array.resize(this._dataBuffer, this._dataBuffer.length + 100);
         offset += 100;
      }

      if (super._contentType.equals(OIDs.getOID(542121532))) {
         super._data = new CMSSignedDataInputStream((InputStream)(new Object(this._dataBuffer)), this._keyStore, false, this._displayUI);
      } else if (super._contentType.equals(OIDs.getOID(542383676))) {
         super._data = new CMSEnvelopedDataInputStream((InputStream)(new Object(this._dataBuffer)), this._keyStore, null, false, this._displayUI);
      } else if (super._contentType.equals(OIDs.getOID(-1721352904))) {
         super._data = new CMSCompressedDataInputStream((InputStream)(new Object(this._dataBuffer)), this._keyStore, false, this._displayUI);
      } else if (super._contentType.equals(OIDs.getOID(-1721352925))) {
         super._data = new CMSSignedReceiptInputStream((InputStream)(new Object(this._dataBuffer)));
      } else if (super._contentType.equals(OIDs.getOID(-477712249))) {
         super._data = new EMSAcceptRequestInputStream((InputStream)(new Object(this._dataBuffer)));
      } else {
         super._data = (InputStream)(new Object(this._dataBuffer));
      }

      this._contentComplete = true;
   }

   public final Certificate getSignerCertificate(CMSEntityIdentifier signer) {
      if (signer == null) {
         throw new Object();
      }

      if (signer.getCreator() != this) {
         throw new CMSNoSuchEntityException();
      }

      Certificate certificate = null;
      if (this._pool == null) {
         this.getCertificates();
      }

      if (signer.getSerialNumber() == null) {
         if (signer.getSubjectKeyIdentifier() != null) {
            byte[] keyIdent = signer.getSubjectKeyIdentifier();
            if (this._keyStore != null) {
               Enumeration enumeration = this._keyStore.elements(1612863905495138626L, keyIdent);

               while (enumeration.hasMoreElements()) {
                  KeyStoreData keyStoreData = (KeyStoreData)enumeration.nextElement();
                  Certificate tempCertificate = keyStoreData.getCertificate();
                  if (CMSUtilities.isCertificateAllowed(tempCertificate, 1, 2)) {
                     certificate = tempCertificate;
                     break;
                  }
               }
            }

            if (certificate == null && this._pool != null) {
               for (int i = 0; i < this._pool.length; i++) {
                  if (Arrays.equals(this._pool[i].getSubjectKeyIdentifier(), keyIdent)) {
                     return this._pool[i];
                  }
               }
            }
         }
      } else {
         X509DistinguishedName issuer = signer.getIssuer();
         byte[] serialNumber = signer.getSerialNumber();
         if (this._keyStore != null) {
            Object alias = SerialNumberIssuerKeyStoreIndex.getAlias(serialNumber, issuer);
            Enumeration enumeration = this._keyStore.elements(-6470299966859493514L, alias);

            while (enumeration.hasMoreElements()) {
               KeyStoreData keyStoreData = (KeyStoreData)enumeration.nextElement();
               Certificate tempCertificate = keyStoreData.getCertificate();
               if (CMSUtilities.isCertificateAllowed(tempCertificate, 1, 2)) {
                  certificate = tempCertificate;
                  break;
               }
            }
         }

         if (certificate == null && this._pool != null) {
            for (int i = 0; i < this._pool.length; i++) {
               if (Arrays.equals(this._pool[i].getSerialNumber(), serialNumber) && issuer.equals(this._pool[i].getIssuer())) {
                  return this._pool[i];
               }
            }
         }
      }

      return certificate;
   }

   public final Certificate[] getSignerCertificateChain(CMSEntityIdentifier signer) {
      Certificate[][] chains = this.getSignerCertificateChains(signer);
      return chains != null ? chains[0] : null;
   }

   public final Certificate[][] getSignerCertificateChains(CMSEntityIdentifier signer) {
      if (signer == null) {
         throw new Object();
      }

      if (signer.getCreator() != this) {
         throw new CMSNoSuchEntityException();
      }

      Certificate cert = this.getSignerCertificate(signer);
      return cert == null ? (Object[][])null : CertificateUtilities.buildCertificateChains(cert, this._pool, this._keyStore);
   }

   public final X509CertificateRevocationList getCRL() {
      if (this._crl != null) {
         return this._crl;
      }

      if (this._crls == null) {
         return null;
      }

      if (this._keyStore == null) {
         throw new Object();
      }

      this._crl = (X509CertificateRevocationList)(new Object((InputStream)(new Object(this._crls)), this._keyStore));
      return this._crl;
   }

   public final CMSAttribute getSignerAttribute(OID oid, CMSEntityIdentifier signer) {
      if (signer == null) {
         throw new Object();
      }

      if (signer.getCreator() != this) {
         throw new CMSNoSuchEntityException();
      }

      Vector attributes = signer.getAttributes();
      int length = attributes.size();

      for (int i = 0; i < length; i++) {
         CMSAttribute attribute = (CMSAttribute)attributes.elementAt(i);
         if (oid.equals(attribute.getOID())) {
            return attribute;
         }
      }

      return null;
   }

   public final Enumeration getSignerAttributes(CMSEntityIdentifier signer) {
      if (signer == null) {
         throw new Object();
      } else if (signer.getCreator() != this) {
         throw new CMSNoSuchEntityException();
      } else {
         return signer.getAttributes().elements();
      }
   }

   public final Certificate[] getCertificates() {
      if (this._pool == null) {
         if (this._certificates != null) {
            this.readCertificates(this._certificates);
            this._certificates = null;
         }

         if (this._rimCertificates != null) {
            this.readCertificates(this._rimCertificates);
            this._rimCertificates = null;
         }
      }

      return this._pool;
   }

   private final void readCertificates(ASN1InputStream param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 004: ifnonnull 00b
      // 007: bipush 0
      // 008: goto 010
      // 00b: aload 0
      // 00c: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 00f: arraylength
      // 010: istore 2
      // 011: aload 1
      // 012: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.endOfStream ()Z
      // 015: ifeq 01b
      // 018: goto 191
      // 01b: aload 1
      // 01c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 01f: bipush 16
      // 021: if_icmpne 053
      // 024: ldc_w "X509"
      // 027: aload 1
      // 028: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 02b: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;[B)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 02e: checkcast java/lang/Object
      // 031: astore 3
      // 032: aload 0
      // 033: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 036: ifnonnull 048
      // 039: aload 0
      // 03a: bipush 1
      // 03b: anewarray 3173
      // 03e: dup
      // 03f: bipush 0
      // 040: aload 3
      // 041: aastore
      // 042: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 045: goto 011
      // 048: aload 0
      // 049: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 04c: aload 3
      // 04d: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 050: goto 011
      // 053: aload 1
      // 054: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 057: bipush 30
      // 059: if_icmpeq 05f
      // 05c: goto 183
      // 05f: aload 1
      // 060: bipush 2
      // 062: bipush 30
      // 064: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetString (II)Ljava/io/InputStream;
      // 067: astore 3
      // 068: bipush 4
      // 06a: newarray 8
      // 06c: astore 4
      // 06e: aload 3
      // 06f: aload 4
      // 071: invokevirtual java/io/InputStream.read ([B)I
      // 074: pop
      // 075: aload 0
      // 076: aload 4
      // 078: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataInputStream.getInt ([B)I
      // 07b: istore 5
      // 07d: iload 5
      // 07f: newarray 8
      // 081: astore 6
      // 083: aload 3
      // 084: aload 6
      // 086: invokevirtual java/io/InputStream.read ([B)I
      // 089: pop
      // 08a: aload 3
      // 08b: aload 4
      // 08d: invokevirtual java/io/InputStream.read ([B)I
      // 090: pop
      // 091: aload 0
      // 092: aload 4
      // 094: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataInputStream.getInt ([B)I
      // 097: istore 5
      // 099: iload 5
      // 09b: newarray 8
      // 09d: astore 7
      // 09f: aload 3
      // 0a0: aload 7
      // 0a2: invokevirtual java/io/InputStream.read ([B)I
      // 0a5: pop
      // 0a6: new java/lang/Object
      // 0a9: dup
      // 0aa: aload 7
      // 0ac: invokespecial net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.<init> ([B)V
      // 0af: astore 8
      // 0b1: aload 3
      // 0b2: aload 4
      // 0b4: invokevirtual java/io/InputStream.read ([B)I
      // 0b7: pop
      // 0b8: aload 0
      // 0b9: aload 4
      // 0bb: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataInputStream.getInt ([B)I
      // 0be: istore 5
      // 0c0: iload 5
      // 0c2: newarray 8
      // 0c4: astore 9
      // 0c6: aload 3
      // 0c7: aload 9
      // 0c9: invokevirtual java/io/InputStream.read ([B)I
      // 0cc: pop
      // 0cd: invokestatic net/rim/device/api/crypto/keystore/DeviceKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 0d0: astore 10
      // 0d2: aload 10
      // 0d4: ldc2_w 4966172969402917741
      // 0d7: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.existsIndex (J)Z 3
      // 0dc: ifne 0ee
      // 0df: aload 10
      // 0e1: new java/lang/Object
      // 0e4: dup
      // 0e5: invokespecial net/rim/device/api/crypto/certificate/CertificateHashKeyStoreIndex.<init> ()V
      // 0e8: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.addIndex (Lnet/rim/device/api/crypto/keystore/KeyStoreIndex;)Z 2
      // 0ed: pop
      // 0ee: aload 10
      // 0f0: ldc2_w 4966172969402917741
      // 0f3: aload 6
      // 0f5: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (JLjava/lang/Object;)Ljava/util/Enumeration; 4
      // 0fa: astore 11
      // 0fc: aload 11
      // 0fe: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 103: ifne 109
      // 106: goto 011
      // 109: aload 11
      // 10b: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 110: checkcast java/lang/Object
      // 113: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 118: astore 12
      // 11a: aload 12
      // 11c: instanceof java/lang/Object
      // 11f: ifne 125
      // 122: goto 0fc
      // 125: aload 12
      // 127: checkcast java/lang/Object
      // 12a: astore 13
      // 12c: aload 13
      // 12e: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getIssuer ()Lnet/rim/device/api/crypto/certificate/DistinguishedName;
      // 131: aload 8
      // 133: invokevirtual net/rim/device/api/crypto/certificate/DistinguishedName.equals (Ljava/lang/Object;)Z
      // 136: ifne 13c
      // 139: goto 0fc
      // 13c: aload 9
      // 13e: bipush 0
      // 13f: baload
      // 140: ifne 150
      // 143: aload 9
      // 145: bipush 1
      // 146: aload 9
      // 148: arraylength
      // 149: bipush 1
      // 14a: isub
      // 14b: invokestatic net/rim/device/api/util/Arrays.copy ([BII)[B
      // 14e: astore 9
      // 150: aload 13
      // 152: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getSerialNumber ()[B
      // 155: aload 9
      // 157: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 15a: ifne 160
      // 15d: goto 0fc
      // 160: aload 0
      // 161: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 164: ifnonnull 177
      // 167: aload 0
      // 168: bipush 1
      // 169: anewarray 3363
      // 16c: dup
      // 16d: bipush 0
      // 16e: aload 13
      // 170: aastore
      // 171: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 174: goto 0fc
      // 177: aload 0
      // 178: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 17b: aload 13
      // 17d: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 180: goto 0fc
      // 183: aload 1
      // 184: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.skipField ()V
      // 187: goto 011
      // 18a: astore 2
      // 18b: return
      // 18c: astore 2
      // 18d: return
      // 18e: astore 2
      // 18f: return
      // 190: astore 2
      // 191: return
      // try (0 -> 180): 180 null
      // try (0 -> 180): 182 null
      // try (0 -> 180): 184 null
      // try (0 -> 180): 186 null
   }

   final int getInt(byte[] bytes) {
      return (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | bytes[3] & 0xFF;
   }

   public final boolean isCertificatePresent() {
      return this._pool != null;
   }

   public final void verify(CMSEntityIdentifier signer) {
      if (signer == null) {
         throw new Object();
      }

      if (signer.getCreator() != this) {
         throw new CMSNoSuchEntityException();
      }

      if (!signer.getVerified()) {
         Exception lastException = signer.getLastException();
         if (!(lastException instanceof Object)) {
            if (!(lastException instanceof Object)) {
               if (this._signerInfos == null) {
                  throw new CMSParsingException();
               }

               Certificate certificate = this.getSignerCertificate(signer);
               if (certificate == null) {
                  CMSNoCertificateFoundException e = new CMSNoCertificateFoundException();
                  signer.setLastException(e);
                  throw e;
               }

               this.verify(signer, certificate);
            } else {
               throw (Object)lastException;
            }
         } else {
            throw (Object)lastException;
         }
      }
   }

   public final void verify(CMSEntityIdentifier param1, Certificate param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.getDigestAlgorithm ()[B
      // 004: astore 3
      // 005: aload 1
      // 006: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.getSignedAttributeArray ()[B
      // 009: astore 4
      // 00b: aconst_null
      // 00c: astore 5
      // 00e: aconst_null
      // 00f: astore 6
      // 011: aload 4
      // 013: ifnull 06e
      // 016: aload 0
      // 017: ldc_w 542647868
      // 01a: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 01d: aload 1
      // 01e: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataInputStream.getSignerAttribute (Lnet/rim/device/api/crypto/oid/OID;Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;)Lnet/rim/device/api/crypto/cms/CMSAttribute;
      // 021: astore 7
      // 023: aload 7
      // 025: ifnull 042
      // 028: new java/lang/Object
      // 02b: dup
      // 02c: aload 7
      // 02e: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getValue ()[B
      // 031: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 034: astore 8
      // 036: aload 8
      // 038: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSet ()V
      // 03b: aload 8
      // 03d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString ()[B
      // 040: astore 5
      // 042: aload 0
      // 043: ldc_w -1721354960
      // 046: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 049: aload 1
      // 04a: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataInputStream.getSignerAttribute (Lnet/rim/device/api/crypto/oid/OID;Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;)Lnet/rim/device/api/crypto/cms/CMSAttribute;
      // 04d: astore 7
      // 04f: aload 7
      // 051: ifnull 06e
      // 054: new java/lang/Object
      // 057: dup
      // 058: aload 7
      // 05a: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getValue ()[B
      // 05d: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 060: astore 8
      // 062: aload 8
      // 064: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSet ()V
      // 067: aload 8
      // 069: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString ()[B
      // 06c: astore 6
      // 06e: aload 3
      // 06f: invokestatic net/rim/device/api/crypto/cms/CMSUtilities.getSignatureDigest ([B)Lnet/rim/device/api/crypto/Digest;
      // 072: astore 7
      // 074: aload 1
      // 075: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.getSignatureEncoding ()[B
      // 078: astore 8
      // 07a: aconst_null
      // 07b: astore 9
      // 07d: aload 2
      // 07e: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 083: astore 9
      // 085: goto 09c
      // 088: astore 10
      // 08a: new net/rim/device/api/crypto/cms/CMSNoPublicKeyFoundException
      // 08d: dup
      // 08e: invokespecial net/rim/device/api/crypto/cms/CMSNoPublicKeyFoundException.<init> ()V
      // 091: astore 11
      // 093: aload 1
      // 094: aload 11
      // 096: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.setLastException (Ljava/lang/Exception;)V
      // 099: aload 11
      // 09b: athrow
      // 09c: aload 0
      // 09d: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._dataBuffer [B
      // 0a0: ifnonnull 0aa
      // 0a3: aload 0
      // 0a4: bipush 0
      // 0a5: newarray 8
      // 0a7: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._dataBuffer [B
      // 0aa: aload 0
      // 0ab: aload 1
      // 0ac: aload 4
      // 0ae: aload 5
      // 0b0: aload 6
      // 0b2: aload 8
      // 0b4: aload 9
      // 0b6: aload 7
      // 0b8: bipush 0
      // 0b9: invokespecial net/rim/device/api/crypto/cms/CMSSignedDataInputStream.verifySignature (Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;[B[B[B[BLnet/rim/device/api/crypto/PublicKey;Lnet/rim/device/api/crypto/Digest;Z)Z
      // 0bc: ifeq 0c5
      // 0bf: aload 1
      // 0c0: bipush 1
      // 0c1: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.setVerified (Z)V
      // 0c4: return
      // 0c5: aload 0
      // 0c6: aload 1
      // 0c7: aload 4
      // 0c9: aload 5
      // 0cb: aload 6
      // 0cd: aload 8
      // 0cf: aload 9
      // 0d1: aload 7
      // 0d3: bipush 1
      // 0d4: invokespecial net/rim/device/api/crypto/cms/CMSSignedDataInputStream.verifySignature (Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;[B[B[B[BLnet/rim/device/api/crypto/PublicKey;Lnet/rim/device/api/crypto/Digest;Z)Z
      // 0d7: ifeq 135
      // 0da: aload 1
      // 0db: bipush 1
      // 0dc: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.setVerified (Z)V
      // 0df: return
      // 0e0: astore 3
      // 0e1: new net/rim/device/api/crypto/cms/CMSParsingException
      // 0e4: dup
      // 0e5: aload 3
      // 0e6: invokevirtual net/rim/device/api/crypto/asn1/ASN1EncodingException.toString ()Ljava/lang/String;
      // 0e9: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> (Ljava/lang/String;)V
      // 0ec: astore 4
      // 0ee: aload 1
      // 0ef: aload 4
      // 0f1: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.setLastException (Ljava/lang/Exception;)V
      // 0f4: aload 4
      // 0f6: athrow
      // 0f7: astore 3
      // 0f8: aload 1
      // 0f9: aload 3
      // 0fa: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.setLastException (Ljava/lang/Exception;)V
      // 0fd: aload 3
      // 0fe: athrow
      // 0ff: astore 3
      // 100: aload 1
      // 101: aload 3
      // 102: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.setLastException (Ljava/lang/Exception;)V
      // 105: aload 3
      // 106: athrow
      // 107: astore 3
      // 108: new net/rim/device/api/crypto/cms/CMSParsingException
      // 10b: dup
      // 10c: aload 3
      // 10d: invokevirtual net/rim/device/api/crypto/InvalidSignatureEncodingException.toString ()Ljava/lang/String;
      // 110: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> (Ljava/lang/String;)V
      // 113: astore 4
      // 115: aload 1
      // 116: aload 4
      // 118: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.setLastException (Ljava/lang/Exception;)V
      // 11b: aload 4
      // 11d: athrow
      // 11e: astore 3
      // 11f: new net/rim/device/api/crypto/cms/CMSNoSuchAlgorithmException
      // 122: dup
      // 123: aload 3
      // 124: invokevirtual net/rim/device/api/crypto/NoSuchAlgorithmException.toString ()Ljava/lang/String;
      // 127: invokespecial net/rim/device/api/crypto/cms/CMSNoSuchAlgorithmException.<init> (Ljava/lang/String;)V
      // 12a: astore 4
      // 12c: aload 1
      // 12d: aload 4
      // 12f: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.setLastException (Ljava/lang/Exception;)V
      // 132: aload 4
      // 134: athrow
      // 135: new net/rim/device/api/crypto/cms/CMSVerificationException
      // 138: dup
      // 139: invokespecial net/rim/device/api/crypto/cms/CMSVerificationException.<init> ()V
      // 13c: astore 3
      // 13d: aload 1
      // 13e: aload 3
      // 13f: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.setLastException (Ljava/lang/Exception;)V
      // 142: aload 3
      // 143: athrow
      // try (58 -> 61): 62 null
      // try (0 -> 93): 109 null
      // try (94 -> 108): 109 null
      // try (0 -> 93): 121 null
      // try (94 -> 108): 121 null
      // try (0 -> 93): 127 null
      // try (94 -> 108): 127 null
      // try (0 -> 93): 133 null
      // try (94 -> 108): 133 null
      // try (0 -> 93): 145 null
      // try (94 -> 108): 145 null
   }

   public final String getSignatureDigestName(CMSEntityIdentifier signer) {
      return CMSUtilities.getSignatureDigestName(signer);
   }

   public final CMSEntityIdentifier[] getSigners() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: new java/lang/Object
      // 003: dup
      // 004: invokespecial java/util/Vector.<init> ()V
      // 007: astore 1
      // 008: aconst_null
      // 009: astore 2
      // 00a: aload 0
      // 00b: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._signerInfos [B
      // 00e: ifnonnull 019
      // 011: new net/rim/device/api/crypto/cms/CMSParsingException
      // 014: dup
      // 015: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 018: athrow
      // 019: new java/lang/Object
      // 01c: dup
      // 01d: aload 0
      // 01e: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._signerInfos [B
      // 021: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 024: astore 3
      // 025: aload 3
      // 026: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSet ()V
      // 029: aload 3
      // 02a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getEndPosition ()I
      // 02d: istore 4
      // 02f: aload 3
      // 030: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getStartPosition ()I
      // 033: iload 4
      // 035: if_icmplt 042
      // 038: aload 0
      // 039: aconst_null
      // 03a: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._signers [Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 03d: aload 0
      // 03e: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._signers [Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 041: areturn
      // 042: aload 3
      // 043: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getStartPosition ()I
      // 046: iload 4
      // 048: if_icmplt 04e
      // 04b: goto 20c
      // 04e: bipush -1
      // 050: istore 5
      // 052: aload 3
      // 053: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getFieldEndPosition ()I
      // 056: istore 5
      // 058: goto 061
      // 05b: astore 6
      // 05d: iload 4
      // 05f: istore 5
      // 061: aload 3
      // 062: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 065: aload 3
      // 066: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readInteger ()I
      // 069: istore 6
      // 06b: aload 3
      // 06c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 06f: bipush 16
      // 071: if_icmpne 0a4
      // 074: iload 6
      // 076: bipush 1
      // 077: if_icmpeq 082
      // 07a: new net/rim/device/api/crypto/cms/CMSParsingException
      // 07d: dup
      // 07e: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 081: athrow
      // 082: aload 3
      // 083: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 086: new java/lang/Object
      // 089: dup
      // 08a: aload 3
      // 08b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 08e: invokespecial net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.<init> ([B)V
      // 091: astore 7
      // 093: new net/rim/device/api/crypto/cms/CMSEntityIdentifier
      // 096: dup
      // 097: aload 3
      // 098: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readIntegerAsByteArray ()[B
      // 09b: aload 7
      // 09d: invokespecial net/rim/device/api/crypto/cms/CMSEntityIdentifier.<init> ([BLnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;)V
      // 0a0: astore 2
      // 0a1: goto 0c2
      // 0a4: iload 6
      // 0a6: bipush 3
      // 0a8: if_icmpeq 0b3
      // 0ab: new net/rim/device/api/crypto/cms/CMSParsingException
      // 0ae: dup
      // 0af: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 0b2: athrow
      // 0b3: new net/rim/device/api/crypto/cms/CMSEntityIdentifier
      // 0b6: dup
      // 0b7: aload 3
      // 0b8: bipush 2
      // 0ba: bipush 0
      // 0bb: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString (II)[B
      // 0be: invokespecial net/rim/device/api/crypto/cms/CMSEntityIdentifier.<init> ([B)V
      // 0c1: astore 2
      // 0c2: aload 3
      // 0c3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 0c6: astore 7
      // 0c8: aconst_null
      // 0c9: astore 8
      // 0cb: new java/lang/Object
      // 0ce: dup
      // 0cf: invokespecial java/util/Vector.<init> ()V
      // 0d2: astore 9
      // 0d4: aload 3
      // 0d5: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 0d8: ifeq 0de
      // 0db: goto 17f
      // 0de: aload 3
      // 0df: bipush 2
      // 0e1: bipush 0
      // 0e2: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSet (II)V
      // 0e5: aload 3
      // 0e6: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getEndPosition ()I
      // 0e9: istore 10
      // 0eb: aload 3
      // 0ec: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getStartPosition ()I
      // 0ef: iload 10
      // 0f1: if_icmpge 158
      // 0f4: aload 3
      // 0f5: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 0f8: astore 11
      // 0fa: new java/lang/Object
      // 0fd: dup
      // 0fe: aload 11
      // 100: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 103: astore 12
      // 105: aload 12
      // 107: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 10a: aload 12
      // 10c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 10f: astore 13
      // 111: aload 12
      // 113: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 116: astore 14
      // 118: aload 9
      // 11a: new net/rim/device/api/crypto/cms/CMSAttribute
      // 11d: dup
      // 11e: aload 13
      // 120: aload 14
      // 122: bipush 1
      // 123: invokespecial net/rim/device/api/crypto/cms/CMSAttribute.<init> (Lnet/rim/device/api/crypto/oid/OID;[BZ)V
      // 126: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 129: aload 8
      // 12b: ifnonnull 138
      // 12e: aload 11
      // 130: invokestatic net/rim/device/api/util/Arrays.copy ([B)[B
      // 133: astore 8
      // 135: goto 0eb
      // 138: aload 8
      // 13a: arraylength
      // 13b: istore 15
      // 13d: aload 8
      // 13f: iload 15
      // 141: aload 11
      // 143: arraylength
      // 144: iadd
      // 145: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 148: aload 11
      // 14a: bipush 0
      // 14b: aload 8
      // 14d: iload 15
      // 14f: aload 11
      // 151: arraylength
      // 152: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 155: goto 0eb
      // 158: new java/lang/Object
      // 15b: dup
      // 15c: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 15f: astore 11
      // 161: new java/lang/Object
      // 164: dup
      // 165: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 168: astore 12
      // 16a: aload 12
      // 16c: aload 8
      // 16e: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 171: aload 11
      // 173: aload 12
      // 175: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSet (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 178: aload 11
      // 17a: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 17d: astore 8
      // 17f: aload 3
      // 180: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getStartPosition ()I
      // 183: istore 10
      // 185: aload 3
      // 186: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 189: aload 3
      // 18a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 18d: aload 3
      // 18e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getEndPosition ()I
      // 191: iload 10
      // 193: isub
      // 194: istore 11
      // 196: iload 11
      // 198: newarray 8
      // 19a: astore 12
      // 19c: aload 0
      // 19d: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._signerInfos [B
      // 1a0: iload 10
      // 1a2: aload 12
      // 1a4: bipush 0
      // 1a5: iload 11
      // 1a7: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 1aa: aload 3
      // 1ab: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 1ae: bipush 1
      // 1af: if_icmpne 1e4
      // 1b2: aload 3
      // 1b3: bipush 2
      // 1b5: bipush 1
      // 1b6: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSet (II)V
      // 1b9: aload 3
      // 1ba: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getEndPosition ()I
      // 1bd: istore 13
      // 1bf: aload 3
      // 1c0: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getStartPosition ()I
      // 1c3: iload 13
      // 1c5: if_icmpge 1e4
      // 1c8: aload 3
      // 1c9: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 1cc: aload 9
      // 1ce: new net/rim/device/api/crypto/cms/CMSAttribute
      // 1d1: dup
      // 1d2: aload 3
      // 1d3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 1d6: aload 3
      // 1d7: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 1da: bipush 0
      // 1db: invokespecial net/rim/device/api/crypto/cms/CMSAttribute.<init> (Lnet/rim/device/api/crypto/oid/OID;[BZ)V
      // 1de: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 1e1: goto 1bf
      // 1e4: aload 2
      // 1e5: aload 9
      // 1e7: aload 0
      // 1e8: aload 7
      // 1ea: aload 8
      // 1ec: aload 12
      // 1ee: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.setSignerInfo (Ljava/util/Vector;Lnet/rim/device/api/crypto/cms/CMSInputStream;[B[B[B)V
      // 1f1: aload 1
      // 1f2: aload 2
      // 1f3: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 1f6: aload 3
      // 1f7: iload 5
      // 1f9: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.setStartPosition (I)V
      // 1fc: goto 042
      // 1ff: astore 2
      // 200: goto 20c
      // 203: astore 2
      // 204: new net/rim/device/api/crypto/cms/CMSParsingException
      // 207: dup
      // 208: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 20b: athrow
      // 20c: aload 1
      // 20d: invokevirtual java/util/Vector.size ()I
      // 210: istore 2
      // 211: iload 2
      // 212: ifle 239
      // 215: aload 0
      // 216: iload 2
      // 217: anewarray 4273
      // 21a: putfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._signers [Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 21d: iload 2
      // 21e: bipush 1
      // 21f: isub
      // 220: istore 3
      // 221: iload 3
      // 222: iflt 239
      // 225: aload 0
      // 226: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._signers [Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 229: iload 3
      // 22a: aload 1
      // 22b: iload 3
      // 22c: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 22f: checkcast net/rim/device/api/crypto/cms/CMSEntityIdentifier
      // 232: aastore
      // 233: iinc 3 -1
      // 236: goto 221
      // 239: aload 0
      // 23a: getfield net/rim/device/api/crypto/cms/CMSSignedDataInputStream._signers [Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 23d: areturn
      // try (41 -> 44): 45 null
      // try (4 -> 33): 247 null
      // try (34 -> 247): 247 null
      // try (4 -> 33): 249 null
      // try (34 -> 247): 249 null
   }

   private final String[] appendNewReceipts(ASN1InputByteArray input, String[] array) {
      int endOffset = input.getEndPosition();

      while (input.getStartPosition() < endOffset) {
         String[] temp = this.readGeneralNames(input);
         if (array == null) {
            array = temp;
         } else if (temp != null) {
            int oldLength = array.length;
            Array.resize(array, oldLength + temp.length);
            System.arraycopy(temp, 0, array, oldLength, temp.length);
         }
      }

      return array;
   }

   private final boolean verifySignature(
      CMSEntityIdentifier signer,
      byte[] signedAttribute,
      byte[] hash,
      byte[] msgSigDigest,
      byte[] encoding,
      PublicKey publicKey,
      Digest digest,
      boolean entrustBug
   ) {
      boolean hashMatch = false;
      DecodedSignature decoded = SignatureDecoder.decode(
         (InputStream)(new Object(encoding)), "CMS", ((StringBuffer)(new Object("/"))).append(digest.getAlgorithm()).toString()
      );
      SignatureVerifier verifier = decoded.getVerifier(publicKey);
      if (signedAttribute == null) {
         hashMatch = true;
         verifier.update(this._dataBuffer);
         if (entrustBug) {
            verifier.update(CRLF);
         }
      } else if (hash != null) {
         digest.reset();
         digest.update(this._dataBuffer);
         if (entrustBug) {
            digest.update(CRLF);
         }

         byte[] hash2 = digest.getDigest();
         if (this.isSignedReceipt()) {
            if (this._receiptData == null) {
               CMSNoReceiptDataFoundException e = new CMSNoReceiptDataFoundException();
               signer.setLastException(e);
               throw e;
            }

            byte[] receiptMsgSigDigest = this._receiptData.getMsgSigDigest();
            byte[] receiptMsgDigest = this._receiptData.getMessageDigest();
            hashMatch = true;
            hashMatch &= Arrays.equals(hash, hash2);
            hashMatch &= Arrays.equals(hash, receiptMsgDigest);
            hashMatch &= Arrays.equals(receiptMsgSigDigest, msgSigDigest);
         } else if (Arrays.equals(hash, hash2)) {
            hashMatch = true;
         }

         verifier.update(signedAttribute);
      }

      return hashMatch && verifier.verify();
   }

   @Override
   public final boolean isContentComplete() {
      return this._contentComplete;
   }
}
