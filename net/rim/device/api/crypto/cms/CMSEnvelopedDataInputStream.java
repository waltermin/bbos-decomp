package net.rim.device.api.crypto.cms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.BlockDecryptorEngine;
import net.rim.device.api.crypto.BlockUnformatterEngine;
import net.rim.device.api.crypto.CAST128Key;
import net.rim.device.api.crypto.DESKey;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.Key;
import net.rim.device.api.crypto.PrivateKeyDecryptorEngine;
import net.rim.device.api.crypto.PseudoRandomSource;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RC2Key;
import net.rim.device.api.crypto.RSAPrivateKey;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.TripleDESKey;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.SerialNumberIssuerKeyStoreIndex;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.certificate.x509.X509CertificateRevocationList;
import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.io.SharedInputStream;
import net.rim.device.api.util.Arrays;

public final class CMSEnvelopedDataInputStream extends CMSInputStream {
   private KeyStore _keyStore;
   private SharedInputStream _encryptedContentInfoStream;
   private ASN1InputStream _encryptedContentInfo;
   private byte[] _recipientInfosData;
   private ASN1InputStream _originatorCerts;
   private byte[] _originatorCrls;
   private SymmetricKey _sessionKey;
   private byte[] _sessionKeyData;
   private byte[] _iv;
   private OID _encryptionType;
   private int _encryptionConstant;
   private X509CertificateRevocationList _crl;
   private Certificate _recipientCertificate;
   private String _recipientPublicKeyAlgorithm;
   private int _recipientPublicKeyBitLength;
   private CMSEntityIdentifier[] _recipients;
   private Vector _attributes;
   private X509Certificate[] _pool;
   private CMSEntityIdentifier _recipient;
   private boolean _displayUI;
   private boolean _contentComplete;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(60462186577914032L, "net.rim.device.internal.resource.crypto.KeyStore");
   private static final int MAX_CMS_PASSWORD_LENGTH = 255;

   CMSEnvelopedDataInputStream(InputStream inputStream, KeyStore keyStore) {
      this(inputStream, keyStore, null, false, true);
   }

   CMSEnvelopedDataInputStream(InputStream inputStream, KeyStore keyStore, SymmetricKey sessionKey) {
      this(inputStream, keyStore, sessionKey, false, true);
   }

   CMSEnvelopedDataInputStream(InputStream param1, KeyStore param2, SymmetricKey param3, boolean param4, boolean param5) throws CMSParsingException {
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
      // 006: new java/lang/Object
      // 009: dup
      // 00a: invokespecial java/util/Vector.<init> ()V
      // 00d: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._attributes Ljava/util/Vector;
      // 010: aload 0
      // 011: aload 3
      // 012: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 015: aload 0
      // 016: aload 2
      // 017: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._keyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 01a: aload 0
      // 01b: iload 5
      // 01d: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._displayUI Z
      // 020: iload 4
      // 022: ifne 029
      // 025: aload 0
      // 026: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.addKeyStoreIndices ()V
      // 029: new java/lang/Object
      // 02c: dup
      // 02d: aload 0
      // 02e: getfield net/rim/device/api/crypto/cms/CMSInputStream._input Ljava/io/InputStream;
      // 031: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 034: astore 6
      // 036: aload 6
      // 038: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 03b: astore 7
      // 03d: aload 7
      // 03f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // 042: istore 8
      // 044: iload 8
      // 046: ifeq 066
      // 049: iload 8
      // 04b: bipush 2
      // 04d: if_icmpeq 066
      // 050: iload 8
      // 052: bipush 3
      // 054: if_icmpeq 066
      // 057: iload 8
      // 059: bipush 4
      // 05b: if_icmpeq 066
      // 05e: new net/rim/device/api/crypto/cms/CMSParsingException
      // 061: dup
      // 062: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 065: athrow
      // 066: aload 7
      // 068: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 06b: ifne 0a8
      // 06e: aload 7
      // 070: bipush 2
      // 072: bipush 0
      // 073: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 076: astore 9
      // 078: aload 9
      // 07a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 07d: ifne 08c
      // 080: aload 0
      // 081: aload 9
      // 083: bipush 2
      // 085: bipush 0
      // 086: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSet (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 089: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._originatorCerts Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 08c: aload 9
      // 08e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 091: bipush 1
      // 092: if_icmpne 0a8
      // 095: aload 7
      // 097: bipush 2
      // 099: bipush 1
      // 09a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSet (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 09d: astore 10
      // 09f: aload 0
      // 0a0: aload 10
      // 0a2: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 0a5: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._originatorCrls [B
      // 0a8: aload 0
      // 0a9: aload 7
      // 0ab: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 0ae: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientInfosData [B
      // 0b1: aload 0
      // 0b2: aload 7
      // 0b4: bipush 16
      // 0b6: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readStreamWithTag (I)Ljava/io/InputStream;
      // 0b9: invokestatic net/rim/device/api/io/SharedInputStream.getSharedInputStream (Ljava/io/InputStream;)Lnet/rim/device/api/io/SharedInputStream;
      // 0bc: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptedContentInfoStream Lnet/rim/device/api/io/SharedInputStream;
      // 0bf: aload 0
      // 0c0: new java/lang/Object
      // 0c3: dup
      // 0c4: aload 0
      // 0c5: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptedContentInfoStream Lnet/rim/device/api/io/SharedInputStream;
      // 0c8: invokevirtual net/rim/device/api/io/SharedInputStream.readInputStream ()Lnet/rim/device/api/io/SharedInputStream;
      // 0cb: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 0ce: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptedContentInfo Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 0d1: iload 4
      // 0d3: ifne 0da
      // 0d6: aload 0
      // 0d7: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.readRecipientInfos ()V
      // 0da: aload 7
      // 0dc: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 0df: bipush -1
      // 0e1: if_icmpeq 0e9
      // 0e4: aload 0
      // 0e5: bipush 1
      // 0e6: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._contentComplete Z
      // 0e9: aload 7
      // 0eb: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 0ee: bipush 1
      // 0ef: if_icmpne 12f
      // 0f2: aload 7
      // 0f4: bipush 2
      // 0f6: bipush 1
      // 0f7: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSet (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 0fa: astore 9
      // 0fc: aload 9
      // 0fe: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.endOfStream ()Z
      // 101: ifne 12f
      // 104: aload 9
      // 106: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 109: astore 10
      // 10b: aload 10
      // 10d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 110: astore 11
      // 112: aload 10
      // 114: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 117: astore 12
      // 119: aload 0
      // 11a: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._attributes Ljava/util/Vector;
      // 11d: new net/rim/device/api/crypto/cms/CMSAttribute
      // 120: dup
      // 121: aload 11
      // 123: aload 12
      // 125: bipush 0
      // 126: invokespecial net/rim/device/api/crypto/cms/CMSAttribute.<init> (Lnet/rim/device/api/crypto/oid/OID;[BZ)V
      // 129: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 12c: goto 0fc
      // 12f: aload 7
      // 131: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 134: bipush 30
      // 136: if_icmpne 185
      // 139: aload 7
      // 13b: bipush 2
      // 13d: bipush 30
      // 13f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 142: astore 9
      // 144: aload 9
      // 146: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 149: ifne 158
      // 14c: aload 0
      // 14d: aload 9
      // 14f: bipush 2
      // 151: bipush 0
      // 152: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSet (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 155: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._originatorCerts Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 158: aload 9
      // 15a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 15d: bipush 1
      // 15e: if_icmpne 185
      // 161: aload 9
      // 163: bipush 2
      // 165: bipush 1
      // 166: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSet (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 169: astore 10
      // 16b: aload 0
      // 16c: aload 10
      // 16e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 171: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._originatorCrls [B
      // 174: return
      // 175: astore 6
      // 177: new net/rim/device/api/crypto/cms/CMSParsingException
      // 17a: dup
      // 17b: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 17e: athrow
      // 17f: astore 6
      // 181: return
      // 182: astore 6
      // 184: return
      // 185: return
      // try (21 -> 169): 170 null
      // try (21 -> 169): 175 null
      // try (21 -> 169): 177 null
   }

   public final Certificate getCertificate(CMSEntityIdentifier recipient) {
      if (recipient == null) {
         return null;
      }

      Certificate certificate = null;
      if (this._pool == null) {
         this.getCertificates();
      }

      if (recipient.getSerialNumber() == null) {
         if (recipient.getSubjectKeyIdentifier() != null) {
            byte[] keyIdent = recipient.getSubjectKeyIdentifier();
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
         X509DistinguishedName issuer = recipient.getIssuer();
         byte[] serialNumber = recipient.getSerialNumber();
         if (this._keyStore != null) {
            Object alias = SerialNumberIssuerKeyStoreIndex.getAlias(serialNumber, issuer);
            Enumeration enumeration = this._keyStore.elements(-6470299966859493514L, alias);

            while (enumeration.hasMoreElements()) {
               KeyStoreData keyStoreData = (KeyStoreData)enumeration.nextElement();
               Certificate tempCertificate = keyStoreData.getCertificate();
               if (CMSUtilities.isCertificateAllowed(tempCertificate, 16, 4)) {
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

   public final Certificate[] getCertificateChain(CMSEntityIdentifier recipient) {
      Certificate cert = this.getCertificate(recipient);
      return cert == null ? null : CertificateUtilities.buildCertificateChain(cert, this._pool, this._keyStore);
   }

   public final int getContentCipherConstant() {
      return this._encryptionConstant;
   }

   public final String getRecipientPublicKeyAlgorithm() {
      return this._recipientPublicKeyAlgorithm;
   }

   public final int getRecipientPublicKeyBitLength() {
      return this._recipientPublicKeyBitLength;
   }

   final void continueInitialization(KeyStore keyStore, SymmetricKey sessionKey, boolean displayUI) throws CMSParsingException {
      this._keyStore = keyStore;
      if (this._sessionKey == null) {
         this._sessionKey = sessionKey;
      }

      this._displayUI = displayUI;
      this.addKeyStoreIndices();
      if (this._recipientInfosData != null && this._encryptedContentInfoStream != null) {
         this._encryptedContentInfo = (ASN1InputStream)(new Object(this._encryptedContentInfoStream.readInputStream()));
         this.readRecipientInfos();
      } else {
         throw new CMSParsingException();
      }
   }

   private final void addKeyStoreIndices() {
      KeyStoreIndex[] indexArray = new Object[]{new Object(), new Object()};
      if (this._keyStore != null) {
         this._keyStore.addIndices(indexArray);
      }
   }

   @Override
   public final boolean isSigned() {
      return !(super._data instanceof CMSInputStream) ? false : ((CMSInputStream)super._data).isSigned();
   }

   @Override
   public final boolean isEncrypted() {
      return true;
   }

   public final Enumeration getAttributes() {
      return this._attributes.elements();
   }

   public final CMSAttribute getAttribute(OID oid) {
      int size = this._attributes.size();

      for (int i = 0; i < size; i++) {
         CMSAttribute attribute = (CMSAttribute)this._attributes.elementAt(i);
         if (oid.equals(attribute.getOID())) {
            return attribute;
         }
      }

      return null;
   }

   @Override
   public final void setData(InputStream data) throws CMSDecryptionException {
      BlockDecryptor cryptoStream;
      if (this._encryptionType.equals(OIDs.getOID(-472306990))) {
         if (!(this._sessionKey instanceof Object)) {
            throw new CMSDecryptionException();
         }

         cryptoStream = (BlockDecryptor)(new Object(
            new CMSBlockUnformatterEngine(
               (BlockDecryptorEngine)(new Object(
                  (BlockDecryptorEngine)(new Object((TripleDESKey)this._sessionKey)), (InitializationVector)(new Object(this._iv))
               ))
            ),
            data
         ));
      } else if (this._encryptionType.equals(OIDs.getOID(774757737))) {
         if (!(this._sessionKey instanceof Object)) {
            throw new CMSDecryptionException();
         }

         cryptoStream = (BlockDecryptor)(new Object(
            new CMSBlockUnformatterEngine(
               (BlockDecryptorEngine)(new Object((BlockDecryptorEngine)(new Object((DESKey)this._sessionKey)), (InitializationVector)(new Object(this._iv))))
            ),
            data
         ));
      } else if (this._encryptionType.equals(OIDs.getOID(-472312110))) {
         if (!(this._sessionKey instanceof Object)) {
            throw new CMSDecryptionException();
         }

         cryptoStream = (BlockDecryptor)(new Object(
            new CMSBlockUnformatterEngine(
               (BlockDecryptorEngine)(new Object((BlockDecryptorEngine)(new Object((RC2Key)this._sessionKey)), (InitializationVector)(new Object(this._iv))))
            ),
            data
         ));
      } else if (!this._encryptionType.equals(OIDs.getOID(540861300))
         && !this._encryptionType.equals(OIDs.getOID(546104180))
         && !this._encryptionType.equals(OIDs.getOID(551347060))) {
         if (!this._encryptionType.equals(OIDs.getOID(552133493))) {
            throw new Object(this._encryptionType.toString());
         }

         if (!(this._sessionKey instanceof Object)) {
            throw new CMSDecryptionException();
         }

         cryptoStream = (BlockDecryptor)(new Object(
            new CMSBlockUnformatterEngine(
               (BlockDecryptorEngine)(new Object((BlockDecryptorEngine)(new Object((CAST128Key)this._sessionKey)), (InitializationVector)(new Object(this._iv))))
            ),
            data
         ));
      } else {
         if (!(this._sessionKey instanceof Object)) {
            throw new CMSDecryptionException();
         }

         cryptoStream = (BlockDecryptor)(new Object(
            new CMSBlockUnformatterEngine(
               (BlockDecryptorEngine)(new Object((BlockDecryptorEngine)(new Object((AESKey)this._sessionKey)), (InitializationVector)(new Object(this._iv))))
            ),
            data
         ));
      }

      if (super._contentType.equals(OIDs.getOID(542121532))) {
         super._data = new CMSSignedDataInputStream(cryptoStream, this._keyStore, false, this._displayUI);
      } else if (super._contentType.equals(OIDs.getOID(542383676))) {
         super._data = new CMSEnvelopedDataInputStream(cryptoStream, this._keyStore, null, false, this._displayUI);
      } else if (super._contentType.equals(OIDs.getOID(-1721352904))) {
         super._data = new CMSCompressedDataInputStream(cryptoStream, this._keyStore, false, this._displayUI);
      } else {
         super._data = cryptoStream;
      }
   }

   private final void setData(InputStream param1, ASN1InputStream param2) throws CMSParsingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 2
      // 002: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 005: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionType Lnet/rim/device/api/crypto/oid/OID;
      // 008: aload 0
      // 009: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionType Lnet/rim/device/api/crypto/oid/OID;
      // 00c: ldc_w -472306990
      // 00f: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 012: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 015: ifeq 051
      // 018: aload 0
      // 019: bipush 100
      // 01b: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionConstant I
      // 01e: aload 0
      // 01f: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 022: ifnonnull 034
      // 025: aload 0
      // 026: new java/lang/Object
      // 029: dup
      // 02a: aload 0
      // 02b: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKeyData [B
      // 02e: invokespecial net/rim/device/api/crypto/TripleDESKey.<init> ([B)V
      // 031: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 034: aload 0
      // 035: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 038: instanceof java/lang/Object
      // 03b: ifeq 049
      // 03e: aload 0
      // 03f: aload 2
      // 040: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 043: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._iv [B
      // 046: goto 269
      // 049: new net/rim/device/api/crypto/cms/CMSDecryptionException
      // 04c: dup
      // 04d: invokespecial net/rim/device/api/crypto/cms/CMSDecryptionException.<init> ()V
      // 050: athrow
      // 051: aload 0
      // 052: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionType Lnet/rim/device/api/crypto/oid/OID;
      // 055: ldc_w 774757737
      // 058: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 05b: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 05e: ifeq 09a
      // 061: aload 0
      // 062: bipush 109
      // 064: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionConstant I
      // 067: aload 0
      // 068: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 06b: ifnonnull 07d
      // 06e: aload 0
      // 06f: new java/lang/Object
      // 072: dup
      // 073: aload 0
      // 074: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKeyData [B
      // 077: invokespecial net/rim/device/api/crypto/DESKey.<init> ([B)V
      // 07a: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 07d: aload 0
      // 07e: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 081: instanceof java/lang/Object
      // 084: ifeq 092
      // 087: aload 0
      // 088: aload 2
      // 089: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 08c: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._iv [B
      // 08f: goto 269
      // 092: new net/rim/device/api/crypto/cms/CMSDecryptionException
      // 095: dup
      // 096: invokespecial net/rim/device/api/crypto/cms/CMSDecryptionException.<init> ()V
      // 099: athrow
      // 09a: aload 0
      // 09b: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionType Lnet/rim/device/api/crypto/oid/OID;
      // 09e: ldc_w -472312110
      // 0a1: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 0a4: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 0a7: ifne 0ad
      // 0aa: goto 142
      // 0ad: aload 2
      // 0ae: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 0b1: astore 4
      // 0b3: aload 4
      // 0b5: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // 0b8: istore 5
      // 0ba: iload 5
      // 0bc: invokestatic net/rim/device/api/crypto/RC2Key.decodeEffectiveBitLength (I)I
      // 0bf: istore 6
      // 0c1: iload 6
      // 0c3: bipush 40
      // 0c5: if_icmpne 0d1
      // 0c8: aload 0
      // 0c9: bipush 101
      // 0cb: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionConstant I
      // 0ce: goto 10c
      // 0d1: iload 6
      // 0d3: bipush 64
      // 0d5: if_icmpne 0e1
      // 0d8: aload 0
      // 0d9: bipush 102
      // 0db: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionConstant I
      // 0de: goto 10c
      // 0e1: iload 6
      // 0e3: sipush 128
      // 0e6: if_icmpne 0f2
      // 0e9: aload 0
      // 0ea: bipush 103
      // 0ec: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionConstant I
      // 0ef: goto 10c
      // 0f2: new java/lang/Object
      // 0f5: dup
      // 0f6: new java/lang/Object
      // 0f9: dup
      // 0fa: ldc_w "RC2 "
      // 0fd: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 100: iload 6
      // 102: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 105: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 108: invokespecial net/rim/device/api/crypto/UnsupportedCryptoSystemException.<init> (Ljava/lang/String;)V
      // 10b: athrow
      // 10c: aload 0
      // 10d: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 110: ifnonnull 124
      // 113: aload 0
      // 114: new java/lang/Object
      // 117: dup
      // 118: aload 0
      // 119: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKeyData [B
      // 11c: iload 6
      // 11e: invokespecial net/rim/device/api/crypto/RC2Key.<init> ([BI)V
      // 121: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 124: aload 0
      // 125: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 128: instanceof java/lang/Object
      // 12b: ifeq 13a
      // 12e: aload 0
      // 12f: aload 4
      // 131: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 134: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._iv [B
      // 137: goto 269
      // 13a: new net/rim/device/api/crypto/cms/CMSDecryptionException
      // 13d: dup
      // 13e: invokespecial net/rim/device/api/crypto/cms/CMSDecryptionException.<init> ()V
      // 141: athrow
      // 142: aload 0
      // 143: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionType Lnet/rim/device/api/crypto/oid/OID;
      // 146: ldc_w 552133493
      // 149: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 14c: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 14f: ifeq 1c1
      // 152: aload 0
      // 153: bipush 108
      // 155: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionConstant I
      // 158: aload 2
      // 159: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 15c: astore 4
      // 15e: aload 4
      // 160: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 163: bipush 4
      // 165: if_icmpne 174
      // 168: aload 0
      // 169: aload 4
      // 16b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 16e: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._iv [B
      // 171: goto 17c
      // 174: aload 0
      // 175: bipush 8
      // 177: newarray 8
      // 179: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._iv [B
      // 17c: aload 4
      // 17e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // 181: istore 5
      // 183: iload 5
      // 185: sipush 128
      // 188: if_icmpeq 1a5
      // 18b: new java/lang/Object
      // 18e: dup
      // 18f: new java/lang/Object
      // 192: dup
      // 193: ldc_w "CAST "
      // 196: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 199: iload 5
      // 19b: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 19e: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 1a1: invokespecial net/rim/device/api/crypto/UnsupportedCryptoSystemException.<init> (Ljava/lang/String;)V
      // 1a4: athrow
      // 1a5: aload 0
      // 1a6: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 1a9: ifnull 1af
      // 1ac: goto 269
      // 1af: aload 0
      // 1b0: new java/lang/Object
      // 1b3: dup
      // 1b4: aload 0
      // 1b5: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKeyData [B
      // 1b8: invokespecial net/rim/device/api/crypto/CAST128Key.<init> ([B)V
      // 1bb: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 1be: goto 269
      // 1c1: aload 0
      // 1c2: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionType Lnet/rim/device/api/crypto/oid/OID;
      // 1c5: ldc_w 540861300
      // 1c8: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 1cb: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 1ce: ifne 1f1
      // 1d1: aload 0
      // 1d2: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionType Lnet/rim/device/api/crypto/oid/OID;
      // 1d5: ldc_w 546104180
      // 1d8: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 1db: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 1de: ifne 1f1
      // 1e1: aload 0
      // 1e2: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionType Lnet/rim/device/api/crypto/oid/OID;
      // 1e5: ldc_w 551347060
      // 1e8: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 1eb: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 1ee: ifeq 25a
      // 1f1: aload 0
      // 1f2: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionType Lnet/rim/device/api/crypto/oid/OID;
      // 1f5: ldc_w 540861300
      // 1f8: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 1fb: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 1fe: ifeq 20a
      // 201: aload 0
      // 202: bipush 104
      // 204: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionConstant I
      // 207: goto 239
      // 20a: aload 0
      // 20b: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionType Lnet/rim/device/api/crypto/oid/OID;
      // 20e: ldc_w 546104180
      // 211: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 214: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 217: ifeq 223
      // 21a: aload 0
      // 21b: bipush 105
      // 21d: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionConstant I
      // 220: goto 239
      // 223: aload 0
      // 224: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionType Lnet/rim/device/api/crypto/oid/OID;
      // 227: ldc_w 551347060
      // 22a: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 22d: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 230: ifeq 239
      // 233: aload 0
      // 234: bipush 106
      // 236: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionConstant I
      // 239: aload 0
      // 23a: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 23d: ifnonnull 24f
      // 240: aload 0
      // 241: new java/lang/Object
      // 244: dup
      // 245: aload 0
      // 246: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKeyData [B
      // 249: invokespecial net/rim/device/api/crypto/AESKey.<init> ([B)V
      // 24c: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 24f: aload 0
      // 250: aload 2
      // 251: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 254: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._iv [B
      // 257: goto 269
      // 25a: new java/lang/Object
      // 25d: dup
      // 25e: aload 0
      // 25f: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptionType Lnet/rim/device/api/crypto/oid/OID;
      // 262: invokevirtual net/rim/device/api/crypto/oid/OID.toString ()Ljava/lang/String;
      // 265: invokespecial net/rim/device/api/crypto/UnsupportedCryptoSystemException.<init> (Ljava/lang/String;)V
      // 268: athrow
      // 269: aload 0
      // 26a: aload 1
      // 26b: invokevirtual net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setData (Ljava/io/InputStream;)V
      // 26e: return
      // 26f: astore 3
      // 270: new net/rim/device/api/crypto/cms/CMSParsingException
      // 273: dup
      // 274: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 277: athrow
      // 278: astore 3
      // 279: new net/rim/device/api/crypto/cms/CMSParsingException
      // 27c: dup
      // 27d: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 280: athrow
      // try (0 -> 266): 267 null
      // try (0 -> 266): 272 null
   }

   public final SymmetricKey getSessionKey() {
      return this._sessionKey;
   }

   public final X509CertificateRevocationList getCRL() {
      if (this._crl != null) {
         return this._crl;
      }

      if (this._originatorCrls == null) {
         return null;
      }

      if (this._keyStore == null) {
         throw new Object();
      }

      this._crl = (X509CertificateRevocationList)(new Object((InputStream)(new Object(this._originatorCrls)), this._keyStore));
      return this._crl;
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

   public final Certificate[] getCertificates() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 04: ifnonnull 7d
      // 07: aload 0
      // 08: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._originatorCerts Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 0b: ifnull 7d
      // 0e: aload 0
      // 0f: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._originatorCerts Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 12: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.endOfStream ()Z
      // 15: ifne 68
      // 18: aload 0
      // 19: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._originatorCerts Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 1c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 1f: bipush 16
      // 21: if_icmpne 56
      // 24: ldc_w "X509"
      // 27: aload 0
      // 28: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._originatorCerts Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 2b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 2e: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;[B)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 31: checkcast java/lang/Object
      // 34: astore 1
      // 35: aload 0
      // 36: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 39: ifnonnull 4b
      // 3c: aload 0
      // 3d: bipush 1
      // 3e: anewarray 2627
      // 41: dup
      // 42: bipush 0
      // 43: aload 1
      // 44: aastore
      // 45: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 48: goto 0e
      // 4b: aload 0
      // 4c: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 4f: aload 1
      // 50: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 53: goto 0e
      // 56: aload 0
      // 57: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._originatorCerts Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 5a: aload 0
      // 5b: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._originatorCerts Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 5e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 61: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readStreamWithTag (I)Ljava/io/InputStream;
      // 64: pop
      // 65: goto 0e
      // 68: aload 0
      // 69: aconst_null
      // 6a: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._originatorCerts Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 6d: goto 7d
      // 70: astore 1
      // 71: goto 7d
      // 74: astore 1
      // 75: goto 7d
      // 78: astore 1
      // 79: goto 7d
      // 7c: astore 1
      // 7d: aload 0
      // 7e: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 81: areturn
      // try (3 -> 50): 51 null
      // try (3 -> 50): 53 null
      // try (3 -> 50): 55 null
      // try (3 -> 50): 57 null
   }

   public final boolean areCertificatesPresent() {
      return this._pool != null;
   }

   public final CMSEntityIdentifier getRecipient() {
      return this._recipient;
   }

   public final CMSEntityIdentifier[] getRecipients() throws CMSParsingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipients [Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 04: ifnull 0c
      // 07: aload 0
      // 08: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipients [Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 0b: areturn
      // 0c: aload 0
      // 0d: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientInfosData [B
      // 10: ifnonnull 15
      // 13: aconst_null
      // 14: areturn
      // 15: new java/lang/Object
      // 18: dup
      // 19: invokespecial java/util/Vector.<init> ()V
      // 1c: astore 1
      // 1d: new java/lang/Object
      // 20: dup
      // 21: aload 0
      // 22: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientInfosData [B
      // 25: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 28: astore 2
      // 29: aload 2
      // 2a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSet ()V
      // 2d: aload 2
      // 2e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getEndPosition ()I
      // 31: istore 3
      // 32: aload 2
      // 33: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getStartPosition ()I
      // 36: iload 3
      // 37: if_icmpge 8f
      // 3a: aload 2
      // 3b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 3e: bipush 16
      // 40: if_icmpne 58
      // 43: aload 2
      // 44: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getFieldEndPosition ()I
      // 47: istore 4
      // 49: aload 0
      // 4a: aload 2
      // 4b: aload 1
      // 4c: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.readKeyTransportEntities (Lnet/rim/device/api/crypto/asn1/ASN1InputByteArray;Ljava/util/Vector;)V
      // 4f: aload 2
      // 50: iload 4
      // 52: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.setStartPosition (I)V
      // 55: goto 32
      // 58: aload 2
      // 59: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 5c: bipush 1
      // 5d: if_icmpne 88
      // 60: new java/lang/Object
      // 63: dup
      // 64: new java/lang/Object
      // 67: dup
      // 68: aload 2
      // 69: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 6c: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 6f: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 72: astore 4
      // 74: aload 4
      // 76: bipush 2
      // 78: bipush 1
      // 79: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 7c: astore 4
      // 7e: aload 0
      // 7f: aload 4
      // 81: aload 1
      // 82: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.readKeyAgreementEntities (Lnet/rim/device/api/crypto/asn1/ASN1InputStream;Ljava/util/Vector;)V
      // 85: goto 32
      // 88: aload 2
      // 89: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 8c: goto 32
      // 8f: aload 1
      // 90: invokevirtual java/util/Vector.size ()I
      // 93: istore 4
      // 95: iload 4
      // 97: ifle c4
      // 9a: aload 0
      // 9b: iload 4
      // 9d: anewarray 2852
      // a0: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipients [Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // a3: iload 4
      // a5: bipush 1
      // a6: isub
      // a7: istore 5
      // a9: iload 5
      // ab: iflt c4
      // ae: aload 0
      // af: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipients [Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // b2: iload 5
      // b4: aload 1
      // b5: iload 5
      // b7: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // ba: checkcast net/rim/device/api/crypto/cms/CMSEntityIdentifier
      // bd: aastore
      // be: iinc 5 -1
      // c1: goto a9
      // c4: aload 0
      // c5: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipients [Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // c8: areturn
      // c9: astore 1
      // ca: new net/rim/device/api/crypto/cms/CMSParsingException
      // cd: dup
      // ce: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // d1: athrow
      // d2: astore 1
      // d3: new net/rim/device/api/crypto/cms/CMSParsingException
      // d6: dup
      // d7: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // da: athrow
      // try (6 -> 10): 99 null
      // try (11 -> 98): 99 null
      // try (6 -> 10): 104 null
      // try (11 -> 98): 104 null
   }

   private final void readKeyAgreementEntities(ASN1InputStream keyAgreement, Vector recipients) throws CMSParsingException {
      int version = keyAgreement.readInteger();
      if (version != 3) {
         throw new CMSParsingException();
      }

      keyAgreement.skipField();
      if (keyAgreement.peekNextTag() == 1) {
         keyAgreement.skipField();
      }

      ASN1InputStream keyEncryptionAlgorithmIdentifier = keyAgreement.readSequence();
      OID keyAgreementScheme = keyEncryptionAlgorithmIdentifier.readOID();
      ASN1InputStream recipientEncryptedKeys = keyAgreement.readSequence();
      if (!keyAgreementScheme.equals(OIDs.getOID(-1721354952))
         && !keyAgreementScheme.equals(OIDs.getOID(-1721344712))
         && !keyAgreementScheme.equals(OIDs.getOID(549068105))
         && !keyAgreementScheme.equals(OIDs.getOID(545398089))
         && !keyAgreementScheme.equals(OIDs.getOID(545660233))) {
         throw new CMSParsingException();
      }

      while (!recipientEncryptedKeys.endOfStream()) {
         ASN1InputStream recipient = recipientEncryptedKeys.readSequence();
         if (recipient.peekNextTag() == 16) {
            ASN1InputStream recipientIssuerAndSerialNumber = recipient.readSequence();
            X509DistinguishedName recipientIssuer = (X509DistinguishedName)(new Object(recipientIssuerAndSerialNumber));
            byte[] recipientSerialNumber = recipientIssuerAndSerialNumber.readIntegerAsByteArray();
            recipients.addElement(new CMSEntityIdentifier(recipientSerialNumber, recipientIssuer));
         } else {
            if (recipient.peekNextTag() != 0) {
               throw new CMSParsingException();
            }

            ASN1InputStream rKeyId = recipient.readSequence(2, 0);
            byte[] rKeyIdSubjectKeyIdentifier = rKeyId.readOctetStringAsByteArray();
            recipients.addElement(new CMSEntityIdentifier(rKeyIdSubjectKeyIdentifier));
         }
      }
   }

   private final void readKeyTransportEntities(ASN1InputByteArray recipientInfos, Vector recipients) throws CMSParsingException {
      recipientInfos.readSequence();
      int version = recipientInfos.readInteger();
      if (recipientInfos.peekNextTag() == 0) {
         if (version != 2) {
            throw new CMSParsingException();
         }

         byte[] subjectKeyIdentifier = recipientInfos.readOctetString(2, 0);
         recipients.addElement(new CMSEntityIdentifier(subjectKeyIdentifier));
      } else {
         if (version != 0) {
            throw new CMSParsingException();
         }

         recipientInfos.readSequence();
         X509DistinguishedName issuer = (X509DistinguishedName)(new Object(recipientInfos.readFieldAsByteArray()));
         byte[] serialNumber = recipientInfos.readIntegerAsByteArray();
         recipients.addElement(new CMSEntityIdentifier(serialNumber, issuer));
      }
   }

   private final void readRecipientInfos() throws CMSParsingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 004: ifnonnull 00c
      // 007: aload 0
      // 008: invokevirtual net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.getCertificates ()[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 00b: pop
      // 00c: aload 0
      // 00d: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientInfosData [B
      // 010: ifnonnull 01b
      // 013: new net/rim/device/api/crypto/cms/CMSParsingException
      // 016: dup
      // 017: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 01a: athrow
      // 01b: new java/lang/Object
      // 01e: dup
      // 01f: aload 0
      // 020: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientInfosData [B
      // 023: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 026: astore 1
      // 027: aload 1
      // 028: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSet ()V
      // 02b: aload 1
      // 02c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getEndPosition ()I
      // 02f: istore 2
      // 030: aload 1
      // 031: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getStartPosition ()I
      // 034: iload 2
      // 035: if_icmplt 03b
      // 038: goto 0e0
      // 03b: aload 1
      // 03c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 03f: bipush 16
      // 041: if_icmpne 064
      // 044: aload 1
      // 045: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getFieldEndPosition ()I
      // 048: istore 3
      // 049: aload 0
      // 04a: aload 0
      // 04b: aload 1
      // 04c: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.readKeyTransport (Lnet/rim/device/api/crypto/asn1/ASN1InputByteArray;)Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 04f: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipient Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 052: aload 0
      // 053: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipient Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 056: ifnull 05c
      // 059: goto 0e0
      // 05c: aload 1
      // 05d: iload 3
      // 05e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.setStartPosition (I)V
      // 061: goto 030
      // 064: aload 1
      // 065: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 068: bipush 1
      // 069: if_icmpne 09a
      // 06c: new java/lang/Object
      // 06f: dup
      // 070: new java/lang/Object
      // 073: dup
      // 074: aload 1
      // 075: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 078: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 07b: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 07e: astore 3
      // 07f: aload 3
      // 080: bipush 2
      // 082: bipush 1
      // 083: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 086: astore 3
      // 087: aload 0
      // 088: aload 0
      // 089: aload 3
      // 08a: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.readKeyAgreement (Lnet/rim/device/api/crypto/asn1/ASN1InputStream;)Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 08d: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipient Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 090: aload 0
      // 091: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipient Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 094: ifnull 030
      // 097: goto 0e0
      // 09a: aload 1
      // 09b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 09e: bipush 2
      // 0a0: if_icmpne 0aa
      // 0a3: aload 1
      // 0a4: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 0a7: goto 030
      // 0aa: aload 1
      // 0ab: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 0ae: bipush 3
      // 0b0: if_icmpne 0d9
      // 0b3: aload 0
      // 0b4: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 0b7: ifnonnull 0d2
      // 0ba: aload 0
      // 0bb: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._displayUI Z
      // 0be: ifeq 0d2
      // 0c1: aload 0
      // 0c2: aload 1
      // 0c3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 0c6: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.readPasswordBasedEncryption ([B)Z
      // 0c9: ifne 0cf
      // 0cc: goto 030
      // 0cf: goto 0e0
      // 0d2: aload 1
      // 0d3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 0d6: goto 030
      // 0d9: aload 1
      // 0da: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 0dd: goto 030
      // 0e0: aload 0
      // 0e1: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 0e4: ifnonnull 0f6
      // 0e7: aload 0
      // 0e8: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKeyData [B
      // 0eb: ifnonnull 0f6
      // 0ee: new net/rim/device/api/crypto/cms/CMSNoPrivateKeyFoundException
      // 0f1: dup
      // 0f2: invokespecial net/rim/device/api/crypto/cms/CMSNoPrivateKeyFoundException.<init> ()V
      // 0f5: athrow
      // 0f6: aload 0
      // 0f7: aload 0
      // 0f8: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptedContentInfo Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 0fb: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 0fe: putfield net/rim/device/api/crypto/cms/CMSInputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 101: aload 0
      // 102: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptedContentInfo Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 105: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 108: astore 3
      // 109: aload 0
      // 10a: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptedContentInfo Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 10d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 110: ifne 121
      // 113: aload 0
      // 114: aload 0
      // 115: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._encryptedContentInfo Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 118: bipush 2
      // 11a: bipush 0
      // 11b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetString (II)Ljava/io/InputStream;
      // 11e: putfield net/rim/device/api/crypto/cms/CMSInputStream._data Ljava/io/InputStream;
      // 121: aload 0
      // 122: getfield net/rim/device/api/crypto/cms/CMSInputStream._data Ljava/io/InputStream;
      // 125: ifnonnull 130
      // 128: new net/rim/device/api/crypto/cms/CMSDecryptionException
      // 12b: dup
      // 12c: invokespecial net/rim/device/api/crypto/cms/CMSDecryptionException.<init> ()V
      // 12f: athrow
      // 130: aload 0
      // 131: aload 0
      // 132: getfield net/rim/device/api/crypto/cms/CMSInputStream._data Ljava/io/InputStream;
      // 135: aload 3
      // 136: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setData (Ljava/io/InputStream;Lnet/rim/device/api/crypto/asn1/ASN1InputStream;)V
      // 139: return
      // 13a: astore 1
      // 13b: new net/rim/device/api/crypto/cms/CMSParsingException
      // 13e: dup
      // 13f: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 142: athrow
      // 143: astore 1
      // 144: new net/rim/device/api/crypto/cms/CMSParsingException
      // 147: dup
      // 148: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 14b: athrow
      // try (0 -> 148): 149 null
      // try (0 -> 148): 154 null
   }

   private final boolean readPasswordBasedEncryption(byte[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 2
      // 002: iload 2
      // 003: bipush 5
      // 005: if_icmplt 00b
      // 008: goto 278
      // 00b: new java/lang/Object
      // 00e: dup
      // 00f: aload 1
      // 010: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 013: astore 3
      // 014: aload 3
      // 015: bipush 2
      // 017: bipush 3
      // 019: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence (II)V
      // 01c: aload 3
      // 01d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readInteger ()I
      // 020: istore 4
      // 022: iload 4
      // 024: ifeq 02f
      // 027: new net/rim/device/api/crypto/cms/CMSParsingException
      // 02a: dup
      // 02b: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 02e: athrow
      // 02f: aconst_null
      // 030: astore 5
      // 032: iload 2
      // 033: ifne 052
      // 036: new java/lang/Object
      // 039: dup
      // 03a: getstatic net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 03d: sipush 6046
      // 040: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 043: bipush 0
      // 044: sipush 255
      // 047: ldc_w 134217728
      // 04a: invokespecial net/rim/device/internal/ui/component/PasswordDialog.<init> (Ljava/lang/String;ZII)V
      // 04d: astore 5
      // 04f: goto 06b
      // 052: new java/lang/Object
      // 055: dup
      // 056: getstatic net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 059: sipush 6047
      // 05c: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 05f: bipush 0
      // 060: sipush 255
      // 063: ldc_w 134217728
      // 066: invokespecial net/rim/device/internal/ui/component/PasswordDialog.<init> (Ljava/lang/String;ZII)V
      // 069: astore 5
      // 06b: aload 5
      // 06d: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.show (Lnet/rim/device/internal/ui/component/PopupDialog;)V
      // 070: aload 5
      // 072: invokevirtual net/rim/device/internal/ui/component/PasswordDialog.getPassword ()[B
      // 075: astore 6
      // 077: aload 6
      // 079: ifnull 089
      // 07c: aload 5
      // 07e: invokevirtual net/rim/device/internal/ui/component/PopupDialog.getCloseReason ()I
      // 081: aload 5
      // 083: pop
      // 084: bipush -1
      // 086: if_icmpne 08b
      // 089: bipush 0
      // 08a: ireturn
      // 08b: aload 3
      // 08c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 08f: ifeq 094
      // 092: bipush 0
      // 093: ireturn
      // 094: aload 3
      // 095: bipush 2
      // 097: bipush 0
      // 098: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence (II)V
      // 09b: aload 3
      // 09c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getEndPosition ()I
      // 09f: istore 7
      // 0a1: aload 3
      // 0a2: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 0a5: astore 8
      // 0a7: aconst_null
      // 0a8: astore 9
      // 0aa: aload 3
      // 0ab: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 0ae: bipush 16
      // 0b0: if_icmpne 0f4
      // 0b3: aload 3
      // 0b4: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 0b7: aload 3
      // 0b8: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 0bb: bipush 4
      // 0bd: if_icmpeq 0c2
      // 0c0: bipush 0
      // 0c1: ireturn
      // 0c2: aload 3
      // 0c3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString ()[B
      // 0c6: astore 9
      // 0c8: aload 3
      // 0c9: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readInteger ()I
      // 0cc: istore 10
      // 0ce: aload 3
      // 0cf: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 0d2: bipush 2
      // 0d4: if_icmpne 0db
      // 0d7: aload 3
      // 0d8: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 0db: aload 3
      // 0dc: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 0df: bipush 16
      // 0e1: if_icmpne 0f6
      // 0e4: aload 3
      // 0e5: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getStartPosition ()I
      // 0e8: iload 7
      // 0ea: if_icmpge 0f6
      // 0ed: aload 3
      // 0ee: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 0f1: goto 0f6
      // 0f4: bipush 0
      // 0f5: ireturn
      // 0f6: aconst_null
      // 0f7: astore 11
      // 0f9: aload 8
      // 0fb: ldc_w 273417788
      // 0fe: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 101: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 104: ifeq 119
      // 107: new java/lang/Object
      // 10a: dup
      // 10b: aload 6
      // 10d: aload 9
      // 10f: iload 10
      // 111: invokespecial net/rim/device/api/crypto/PKCS5KDF2PseudoRandomSource.<init> ([B[BI)V
      // 114: astore 11
      // 116: goto 11b
      // 119: bipush 0
      // 11a: ireturn
      // 11b: aload 3
      // 11c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 11f: aload 3
      // 120: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 123: astore 12
      // 125: aload 12
      // 127: ldc_w -1721346760
      // 12a: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 12d: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 130: ifne 165
      // 133: aload 12
      // 135: ldc_w 541647732
      // 138: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 13b: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 13e: ifne 165
      // 141: aload 12
      // 143: ldc_w 546890612
      // 146: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 149: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 14c: ifne 165
      // 14f: aload 12
      // 151: ldc_w 552133492
      // 154: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 157: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 15a: ifne 165
      // 15d: new net/rim/device/api/crypto/cms/CMSParsingException
      // 160: dup
      // 161: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 164: athrow
      // 165: aload 3
      // 166: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 169: aload 3
      // 16a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 16d: astore 13
      // 16f: aconst_null
      // 170: astore 14
      // 172: aconst_null
      // 173: astore 15
      // 175: aload 13
      // 177: ldc_w -472306990
      // 17a: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 17d: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 180: ifeq 1a3
      // 183: new java/lang/Object
      // 186: dup
      // 187: aload 11
      // 189: bipush 24
      // 18b: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 18e: invokespecial net/rim/device/api/crypto/TripleDESKey.<init> ([B)V
      // 191: astore 15
      // 193: new java/lang/Object
      // 196: dup
      // 197: aload 3
      // 198: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString ()[B
      // 19b: invokespecial net/rim/device/api/crypto/InitializationVector.<init> ([B)V
      // 19e: astore 14
      // 1a0: goto 208
      // 1a3: aload 13
      // 1a5: ldc_w 540861300
      // 1a8: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 1ab: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 1ae: ifeq 1c4
      // 1b1: new java/lang/Object
      // 1b4: dup
      // 1b5: aload 11
      // 1b7: bipush 16
      // 1b9: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 1bc: invokespecial net/rim/device/api/crypto/AESKey.<init> ([B)V
      // 1bf: astore 15
      // 1c1: goto 208
      // 1c4: aload 13
      // 1c6: ldc_w 546104180
      // 1c9: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 1cc: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 1cf: ifeq 1e5
      // 1d2: new java/lang/Object
      // 1d5: dup
      // 1d6: aload 11
      // 1d8: bipush 24
      // 1da: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 1dd: invokespecial net/rim/device/api/crypto/AESKey.<init> ([B)V
      // 1e0: astore 15
      // 1e2: goto 208
      // 1e5: aload 13
      // 1e7: ldc_w 551347060
      // 1ea: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 1ed: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 1f0: ifeq 206
      // 1f3: new java/lang/Object
      // 1f6: dup
      // 1f7: aload 11
      // 1f9: bipush 32
      // 1fb: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 1fe: invokespecial net/rim/device/api/crypto/AESKey.<init> ([B)V
      // 201: astore 15
      // 203: goto 208
      // 206: bipush 0
      // 207: ireturn
      // 208: aload 3
      // 209: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString ()[B
      // 20c: astore 16
      // 20e: aload 0
      // 20f: aconst_null
      // 210: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKeyData [B
      // 213: aload 12
      // 215: ldc_w -1721346760
      // 218: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 21b: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 21e: ifeq 231
      // 221: aload 0
      // 222: aload 15
      // 224: aload 16
      // 226: aload 14
      // 228: invokestatic net/rim/device/api/crypto/cms/CMSKeyUnWrap.passwordBasedKeyUnWrap (Lnet/rim/device/api/crypto/SymmetricKey;[BLnet/rim/device/api/crypto/InitializationVector;)[B
      // 22b: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKeyData [B
      // 22e: goto 269
      // 231: aload 12
      // 233: ldc_w 541647732
      // 236: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 239: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 23c: ifne 25b
      // 23f: aload 12
      // 241: ldc_w 546890612
      // 244: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 247: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 24a: ifne 25b
      // 24d: aload 12
      // 24f: ldc_w 552133492
      // 252: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 255: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 258: ifeq 269
      // 25b: aload 0
      // 25c: aload 15
      // 25e: checkcast java/lang/Object
      // 261: aload 16
      // 263: invokestatic net/rim/device/api/crypto/cms/CMSKeyUnWrap.AESKeyUnWrap (Lnet/rim/device/api/crypto/AESKey;[B)[B
      // 266: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKeyData [B
      // 269: aload 0
      // 26a: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKeyData [B
      // 26d: ifnull 272
      // 270: bipush 1
      // 271: ireturn
      // 272: iinc 2 1
      // 275: goto 002
      // 278: bipush 0
      // 279: ireturn
      // 27a: astore 2
      // 27b: bipush 0
      // 27c: ireturn
      // 27d: astore 2
      // 27e: bipush 0
      // 27f: ireturn
      // try (0 -> 63): 281 null
      // try (64 -> 68): 281 null
      // try (69 -> 92): 281 null
      // try (93 -> 117): 281 null
      // try (118 -> 134): 281 null
      // try (135 -> 232): 281 null
      // try (233 -> 276): 281 null
      // try (277 -> 280): 281 null
      // try (0 -> 63): 284 null
      // try (64 -> 68): 284 null
      // try (69 -> 92): 284 null
      // try (93 -> 117): 284 null
      // try (118 -> 134): 284 null
      // try (135 -> 232): 284 null
      // try (233 -> 276): 284 null
      // try (277 -> 280): 284 null
   }

   private final CMSEntityIdentifier readKeyAgreement(ASN1InputStream param1) throws CMSParsingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 2
      // 002: aload 1
      // 003: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // 006: istore 3
      // 007: iload 3
      // 008: bipush 3
      // 00a: if_icmpeq 015
      // 00d: new net/rim/device/api/crypto/cms/CMSParsingException
      // 010: dup
      // 011: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 014: athrow
      // 015: new java/lang/Object
      // 018: dup
      // 019: aload 1
      // 01a: bipush 0
      // 01b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readStreamWithTag (I)Ljava/io/InputStream;
      // 01e: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 021: astore 4
      // 023: aconst_null
      // 024: astore 5
      // 026: aconst_null
      // 027: astore 6
      // 029: aconst_null
      // 02a: astore 7
      // 02c: aload 4
      // 02e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 031: bipush 16
      // 033: if_icmpne 040
      // 036: aload 4
      // 038: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 03b: astore 5
      // 03d: goto 068
      // 040: aload 4
      // 042: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 045: ifne 055
      // 048: aload 4
      // 04a: bipush 2
      // 04c: bipush 0
      // 04d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray (II)[B
      // 050: astore 6
      // 052: goto 068
      // 055: aload 4
      // 057: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 05a: bipush 1
      // 05b: if_icmpne 068
      // 05e: aload 4
      // 060: bipush 2
      // 062: bipush 1
      // 063: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 066: astore 7
      // 068: aconst_null
      // 069: astore 8
      // 06b: aload 1
      // 06c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 06f: bipush 1
      // 070: if_icmpne 07b
      // 073: aload 1
      // 074: bipush 1
      // 075: bipush 1
      // 076: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray (II)[B
      // 079: astore 8
      // 07b: aload 1
      // 07c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 07f: astore 9
      // 081: aload 9
      // 083: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 086: astore 10
      // 088: aload 1
      // 089: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 08c: astore 11
      // 08e: aload 10
      // 090: ldc_w -1721354952
      // 093: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 096: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 099: ifne 0ad
      // 09c: aload 10
      // 09e: ldc_w -1721344712
      // 0a1: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 0a4: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 0a7: ifne 0ad
      // 0aa: goto 44d
      // 0ad: aconst_null
      // 0ae: astore 12
      // 0b0: aconst_null
      // 0b1: astore 13
      // 0b3: aload 11
      // 0b5: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.endOfStream ()Z
      // 0b8: ifeq 0be
      // 0bb: goto 186
      // 0be: aload 11
      // 0c0: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 0c3: astore 14
      // 0c5: aload 14
      // 0c7: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 0ca: bipush 16
      // 0cc: if_icmpne 121
      // 0cf: aload 14
      // 0d1: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 0d4: astore 15
      // 0d6: new java/lang/Object
      // 0d9: dup
      // 0da: aload 15
      // 0dc: invokespecial net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.<init> (Lnet/rim/device/api/crypto/asn1/ASN1InputStream;)V
      // 0df: astore 16
      // 0e1: aload 15
      // 0e3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readIntegerAsByteArray ()[B
      // 0e6: astore 17
      // 0e8: aload 0
      // 0e9: aload 16
      // 0eb: aload 17
      // 0ed: bipush 0
      // 0ee: ldc_w "DH"
      // 0f1: bipush 16
      // 0f3: i2l
      // 0f4: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.findKeyWithIssuerAndSerialNumber (Lnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;[BZLjava/lang/String;J)Lnet/rim/device/api/crypto/Key;
      // 0f7: astore 18
      // 0f9: aload 18
      // 0fb: dup
      // 0fc: instanceof java/lang/Object
      // 0ff: ifne 106
      // 102: pop
      // 103: goto 10b
      // 106: checkcast java/lang/Object
      // 109: astore 12
      // 10b: new net/rim/device/api/crypto/cms/CMSEntityIdentifier
      // 10e: dup
      // 10f: aload 17
      // 111: aload 16
      // 113: invokespecial net/rim/device/api/crypto/cms/CMSEntityIdentifier.<init> ([BLnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;)V
      // 116: astore 2
      // 117: aload 18
      // 119: instanceof java/lang/Object
      // 11c: ifeq 177
      // 11f: aload 2
      // 120: areturn
      // 121: aload 14
      // 123: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 126: ifne 16f
      // 129: aload 14
      // 12b: bipush 2
      // 12d: bipush 0
      // 12e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 131: astore 15
      // 133: aload 15
      // 135: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 138: astore 16
      // 13a: aload 0
      // 13b: aload 16
      // 13d: bipush 0
      // 13e: ldc_w "DH"
      // 141: bipush 16
      // 143: i2l
      // 144: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.findKeyWithSubjectKeyIdentifier ([BZLjava/lang/String;J)Lnet/rim/device/api/crypto/Key;
      // 147: astore 17
      // 149: aload 17
      // 14b: dup
      // 14c: instanceof java/lang/Object
      // 14f: ifne 156
      // 152: pop
      // 153: goto 15b
      // 156: checkcast java/lang/Object
      // 159: astore 12
      // 15b: new net/rim/device/api/crypto/cms/CMSEntityIdentifier
      // 15e: dup
      // 15f: aload 16
      // 161: invokespecial net/rim/device/api/crypto/cms/CMSEntityIdentifier.<init> ([B)V
      // 164: astore 2
      // 165: aload 17
      // 167: instanceof java/lang/Object
      // 16a: ifeq 177
      // 16d: aload 2
      // 16e: areturn
      // 16f: new net/rim/device/api/crypto/cms/CMSParsingException
      // 172: dup
      // 173: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 176: athrow
      // 177: aload 12
      // 179: ifnonnull 17f
      // 17c: goto 0b3
      // 17f: aload 14
      // 181: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 184: astore 13
      // 186: aload 12
      // 188: ifnonnull 18d
      // 18b: aconst_null
      // 18c: areturn
      // 18d: aconst_null
      // 18e: astore 14
      // 190: aload 7
      // 192: ifnull 1eb
      // 195: aload 7
      // 197: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 19a: astore 15
      // 19c: aload 15
      // 19e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 1a1: astore 16
      // 1a3: aload 16
      // 1a5: ldc_w -1487623704
      // 1a8: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 1ab: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 1ae: ifne 1b9
      // 1b1: new net/rim/device/api/crypto/cms/CMSParsingException
      // 1b4: dup
      // 1b5: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 1b8: athrow
      // 1b9: new java/lang/Object
      // 1bc: dup
      // 1bd: new java/lang/Object
      // 1c0: dup
      // 1c1: aload 7
      // 1c3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readBitString ()Lnet/rim/device/api/crypto/asn1/ASN1BitSet;
      // 1c6: invokevirtual net/rim/device/api/crypto/asn1/ASN1BitSet.toByteArray ()[B
      // 1c9: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 1cc: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 1cf: astore 17
      // 1d1: aload 17
      // 1d3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readIntegerAsByteArray ()[B
      // 1d6: astore 18
      // 1d8: new java/lang/Object
      // 1db: dup
      // 1dc: aload 12
      // 1de: invokevirtual net/rim/device/api/crypto/DHPrivateKey.getDHCryptoSystem ()Lnet/rim/device/api/crypto/DHCryptoSystem;
      // 1e1: aload 18
      // 1e3: invokespecial net/rim/device/api/crypto/DHPublicKey.<init> (Lnet/rim/device/api/crypto/DHCryptoSystem;[B)V
      // 1e6: astore 14
      // 1e8: goto 313
      // 1eb: aload 5
      // 1ed: ifnonnull 1f3
      // 1f0: goto 295
      // 1f3: new java/lang/Object
      // 1f6: dup
      // 1f7: aload 5
      // 1f9: invokespecial net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.<init> (Lnet/rim/device/api/crypto/asn1/ASN1InputStream;)V
      // 1fc: astore 15
      // 1fe: aload 5
      // 200: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readIntegerAsByteArray ()[B
      // 203: astore 16
      // 205: aload 0
      // 206: aload 15
      // 208: aload 16
      // 20a: bipush 1
      // 20b: ldc_w "DH"
      // 20e: bipush 16
      // 210: i2l
      // 211: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.findKeyWithIssuerAndSerialNumber (Lnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;[BZLjava/lang/String;J)Lnet/rim/device/api/crypto/Key;
      // 214: astore 17
      // 216: aload 17
      // 218: dup
      // 219: instanceof java/lang/Object
      // 21c: ifne 223
      // 21f: pop
      // 220: goto 228
      // 223: checkcast java/lang/Object
      // 226: astore 14
      // 228: aload 14
      // 22a: ifnull 230
      // 22d: goto 313
      // 230: aload 0
      // 231: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 234: ifnonnull 239
      // 237: aconst_null
      // 238: areturn
      // 239: bipush 0
      // 23a: istore 18
      // 23c: iload 18
      // 23e: aload 0
      // 23f: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 242: arraylength
      // 243: if_icmpge 288
      // 246: aload 0
      // 247: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 24a: iload 18
      // 24c: aaload
      // 24d: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getSerialNumber ()[B
      // 250: aload 16
      // 252: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 255: ifeq 282
      // 258: aload 15
      // 25a: aload 0
      // 25b: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 25e: iload 18
      // 260: aaload
      // 261: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getIssuer ()Lnet/rim/device/api/crypto/certificate/DistinguishedName;
      // 264: invokevirtual net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.equals (Ljava/lang/Object;)Z
      // 267: ifeq 282
      // 26a: aload 0
      // 26b: aload 0
      // 26c: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 26f: iload 18
      // 271: aaload
      // 272: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.getPublicKeyFromPool (Lnet/rim/device/api/crypto/certificate/Certificate;)Lnet/rim/device/api/crypto/PublicKey;
      // 275: checkcast java/lang/Object
      // 278: astore 14
      // 27a: aload 14
      // 27c: ifnull 282
      // 27f: goto 288
      // 282: iinc 18 1
      // 285: goto 23c
      // 288: aload 14
      // 28a: ifnonnull 313
      // 28d: new net/rim/device/api/crypto/cms/CMSParsingException
      // 290: dup
      // 291: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 294: athrow
      // 295: aload 6
      // 297: ifnull 313
      // 29a: aload 0
      // 29b: aload 6
      // 29d: bipush 1
      // 29e: ldc_w "DH"
      // 2a1: bipush 16
      // 2a3: i2l
      // 2a4: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.findKeyWithSubjectKeyIdentifier ([BZLjava/lang/String;J)Lnet/rim/device/api/crypto/Key;
      // 2a7: astore 15
      // 2a9: aload 15
      // 2ab: dup
      // 2ac: instanceof java/lang/Object
      // 2af: ifne 2b6
      // 2b2: pop
      // 2b3: goto 2bb
      // 2b6: checkcast java/lang/Object
      // 2b9: astore 14
      // 2bb: aload 14
      // 2bd: ifnonnull 313
      // 2c0: aload 0
      // 2c1: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 2c4: ifnonnull 2c9
      // 2c7: aconst_null
      // 2c8: areturn
      // 2c9: bipush 0
      // 2ca: istore 16
      // 2cc: iload 16
      // 2ce: aload 0
      // 2cf: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 2d2: arraylength
      // 2d3: if_icmpge 306
      // 2d6: aload 0
      // 2d7: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 2da: iload 16
      // 2dc: aaload
      // 2dd: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getSubjectKeyIdentifier ()[B
      // 2e0: aload 6
      // 2e2: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 2e5: ifeq 300
      // 2e8: aload 0
      // 2e9: aload 0
      // 2ea: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 2ed: iload 16
      // 2ef: aaload
      // 2f0: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.getPublicKeyFromPool (Lnet/rim/device/api/crypto/certificate/Certificate;)Lnet/rim/device/api/crypto/PublicKey;
      // 2f3: checkcast java/lang/Object
      // 2f6: astore 14
      // 2f8: aload 14
      // 2fa: ifnull 300
      // 2fd: goto 306
      // 300: iinc 16 1
      // 303: goto 2cc
      // 306: aload 14
      // 308: ifnonnull 313
      // 30b: new net/rim/device/api/crypto/cms/CMSParsingException
      // 30e: dup
      // 30f: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 312: athrow
      // 313: aload 12
      // 315: aload 14
      // 317: bipush 0
      // 318: invokestatic net/rim/device/api/crypto/DHKeyAgreement.generateSharedSecret (Lnet/rim/device/api/crypto/DHPrivateKey;Lnet/rim/device/api/crypto/DHPublicKey;Z)[B
      // 31b: astore 15
      // 31d: aload 9
      // 31f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 322: astore 16
      // 324: aload 16
      // 326: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 329: astore 17
      // 32b: aload 17
      // 32d: ldc_w -1721352904
      // 330: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 333: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 336: ifeq 356
      // 339: new java/lang/Object
      // 33c: dup
      // 33d: aload 15
      // 33f: aload 17
      // 341: aload 8
      // 343: sipush 192
      // 346: invokespecial net/rim/device/api/crypto/RFC2631KDFPseudoRandomSource.<init> ([BLnet/rim/device/api/crypto/oid/OID;[BI)V
      // 349: astore 18
      // 34b: aload 0
      // 34c: bipush 1
      // 34d: aload 18
      // 34f: aload 13
      // 351: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // 354: aload 2
      // 355: areturn
      // 356: aload 17
      // 358: ldc_w -1721350856
      // 35b: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 35e: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 361: ifeq 38e
      // 364: aload 16
      // 366: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // 369: bipush 58
      // 36b: if_icmpeq 370
      // 36e: aload 2
      // 36f: areturn
      // 370: new java/lang/Object
      // 373: dup
      // 374: aload 15
      // 376: aload 17
      // 378: aload 8
      // 37a: sipush 128
      // 37d: invokespecial net/rim/device/api/crypto/RFC2631KDFPseudoRandomSource.<init> ([BLnet/rim/device/api/crypto/oid/OID;[BI)V
      // 380: astore 18
      // 382: aload 0
      // 383: bipush 2
      // 385: aload 18
      // 387: aload 13
      // 389: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // 38c: aload 2
      // 38d: areturn
      // 38e: aload 17
      // 390: ldc_w 552133494
      // 393: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 396: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 399: ifeq 3c7
      // 39c: aload 16
      // 39e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // 3a1: sipush 128
      // 3a4: if_icmpeq 3a9
      // 3a7: aload 2
      // 3a8: areturn
      // 3a9: new java/lang/Object
      // 3ac: dup
      // 3ad: aload 15
      // 3af: aload 17
      // 3b1: aload 8
      // 3b3: sipush 128
      // 3b6: invokespecial net/rim/device/api/crypto/RFC2631KDFPseudoRandomSource.<init> ([BLnet/rim/device/api/crypto/oid/OID;[BI)V
      // 3b9: astore 18
      // 3bb: aload 0
      // 3bc: bipush 3
      // 3be: aload 18
      // 3c0: aload 13
      // 3c2: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // 3c5: aload 2
      // 3c6: areturn
      // 3c7: aload 17
      // 3c9: ldc_w 541647732
      // 3cc: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 3cf: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 3d2: ifeq 3f3
      // 3d5: new java/lang/Object
      // 3d8: dup
      // 3d9: aload 15
      // 3db: aload 17
      // 3dd: aload 8
      // 3df: sipush 128
      // 3e2: invokespecial net/rim/device/api/crypto/RFC2631KDFPseudoRandomSource.<init> ([BLnet/rim/device/api/crypto/oid/OID;[BI)V
      // 3e5: astore 18
      // 3e7: aload 0
      // 3e8: bipush 4
      // 3ea: aload 18
      // 3ec: aload 13
      // 3ee: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // 3f1: aload 2
      // 3f2: areturn
      // 3f3: aload 17
      // 3f5: ldc_w 546890612
      // 3f8: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 3fb: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 3fe: ifeq 41f
      // 401: new java/lang/Object
      // 404: dup
      // 405: aload 15
      // 407: aload 17
      // 409: aload 8
      // 40b: sipush 192
      // 40e: invokespecial net/rim/device/api/crypto/RFC2631KDFPseudoRandomSource.<init> ([BLnet/rim/device/api/crypto/oid/OID;[BI)V
      // 411: astore 18
      // 413: aload 0
      // 414: bipush 5
      // 416: aload 18
      // 418: aload 13
      // 41a: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // 41d: aload 2
      // 41e: areturn
      // 41f: aload 17
      // 421: ldc_w 552133492
      // 424: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 427: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 42a: ifeq 44b
      // 42d: new java/lang/Object
      // 430: dup
      // 431: aload 15
      // 433: aload 17
      // 435: aload 8
      // 437: sipush 256
      // 43a: invokespecial net/rim/device/api/crypto/RFC2631KDFPseudoRandomSource.<init> ([BLnet/rim/device/api/crypto/oid/OID;[BI)V
      // 43d: astore 18
      // 43f: aload 0
      // 440: bipush 6
      // 442: aload 18
      // 444: aload 13
      // 446: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // 449: aload 2
      // 44a: areturn
      // 44b: aconst_null
      // 44c: areturn
      // 44d: aload 10
      // 44f: ldc_w 545398089
      // 452: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 455: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 458: ifne 46c
      // 45b: aload 10
      // 45d: ldc_w 545660233
      // 460: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 463: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 466: ifne 46c
      // 469: goto 7af
      // 46c: aconst_null
      // 46d: astore 12
      // 46f: aconst_null
      // 470: astore 13
      // 472: aload 11
      // 474: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.endOfStream ()Z
      // 477: ifeq 47d
      // 47a: goto 545
      // 47d: aload 11
      // 47f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 482: astore 14
      // 484: aload 14
      // 486: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 489: bipush 16
      // 48b: if_icmpne 4e0
      // 48e: aload 14
      // 490: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 493: astore 15
      // 495: new java/lang/Object
      // 498: dup
      // 499: aload 15
      // 49b: invokespecial net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.<init> (Lnet/rim/device/api/crypto/asn1/ASN1InputStream;)V
      // 49e: astore 16
      // 4a0: aload 15
      // 4a2: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readIntegerAsByteArray ()[B
      // 4a5: astore 17
      // 4a7: aload 0
      // 4a8: aload 16
      // 4aa: aload 17
      // 4ac: bipush 0
      // 4ad: ldc_w "EC"
      // 4b0: bipush 16
      // 4b2: i2l
      // 4b3: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.findKeyWithIssuerAndSerialNumber (Lnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;[BZLjava/lang/String;J)Lnet/rim/device/api/crypto/Key;
      // 4b6: astore 18
      // 4b8: aload 18
      // 4ba: dup
      // 4bb: instanceof java/lang/Object
      // 4be: ifne 4c5
      // 4c1: pop
      // 4c2: goto 4ca
      // 4c5: checkcast java/lang/Object
      // 4c8: astore 12
      // 4ca: new net/rim/device/api/crypto/cms/CMSEntityIdentifier
      // 4cd: dup
      // 4ce: aload 17
      // 4d0: aload 16
      // 4d2: invokespecial net/rim/device/api/crypto/cms/CMSEntityIdentifier.<init> ([BLnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;)V
      // 4d5: astore 2
      // 4d6: aload 18
      // 4d8: instanceof java/lang/Object
      // 4db: ifeq 536
      // 4de: aload 2
      // 4df: areturn
      // 4e0: aload 14
      // 4e2: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 4e5: ifne 52e
      // 4e8: aload 14
      // 4ea: bipush 2
      // 4ec: bipush 0
      // 4ed: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 4f0: astore 15
      // 4f2: aload 15
      // 4f4: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 4f7: astore 16
      // 4f9: aload 0
      // 4fa: aload 16
      // 4fc: bipush 0
      // 4fd: ldc_w "EC"
      // 500: bipush 16
      // 502: i2l
      // 503: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.findKeyWithSubjectKeyIdentifier ([BZLjava/lang/String;J)Lnet/rim/device/api/crypto/Key;
      // 506: astore 17
      // 508: aload 17
      // 50a: dup
      // 50b: instanceof java/lang/Object
      // 50e: ifne 515
      // 511: pop
      // 512: goto 51a
      // 515: checkcast java/lang/Object
      // 518: astore 12
      // 51a: new net/rim/device/api/crypto/cms/CMSEntityIdentifier
      // 51d: dup
      // 51e: aload 16
      // 520: invokespecial net/rim/device/api/crypto/cms/CMSEntityIdentifier.<init> ([B)V
      // 523: astore 2
      // 524: aload 17
      // 526: instanceof java/lang/Object
      // 529: ifeq 536
      // 52c: aload 2
      // 52d: areturn
      // 52e: new net/rim/device/api/crypto/cms/CMSParsingException
      // 531: dup
      // 532: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 535: athrow
      // 536: aload 12
      // 538: ifnonnull 53e
      // 53b: goto 472
      // 53e: aload 14
      // 540: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 543: astore 13
      // 545: aload 12
      // 547: ifnonnull 54c
      // 54a: aconst_null
      // 54b: areturn
      // 54c: aload 7
      // 54e: ifnonnull 559
      // 551: new net/rim/device/api/crypto/cms/CMSParsingException
      // 554: dup
      // 555: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 558: athrow
      // 559: aload 7
      // 55b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 55e: astore 14
      // 560: aload 14
      // 562: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 565: astore 15
      // 567: aload 15
      // 569: ldc_w -1487624216
      // 56c: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 56f: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 572: ifne 57d
      // 575: new net/rim/device/api/crypto/cms/CMSParsingException
      // 578: dup
      // 579: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 57c: athrow
      // 57d: aload 7
      // 57f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readBitString ()Lnet/rim/device/api/crypto/asn1/ASN1BitSet;
      // 582: invokevirtual net/rim/device/api/crypto/asn1/ASN1BitSet.toByteArray ()[B
      // 585: astore 16
      // 587: new java/lang/Object
      // 58a: dup
      // 58b: aload 12
      // 58d: invokevirtual net/rim/device/api/crypto/ECPrivateKey.getECCryptoSystem ()Lnet/rim/device/api/crypto/ECCryptoSystem;
      // 590: aload 16
      // 592: invokespecial net/rim/device/api/crypto/ECPublicKey.<init> (Lnet/rim/device/api/crypto/ECCryptoSystem;[B)V
      // 595: astore 17
      // 597: aload 10
      // 599: ldc_w 545660233
      // 59c: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 59f: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 5a2: istore 18
      // 5a4: aload 12
      // 5a6: aload 17
      // 5a8: iload 18
      // 5aa: invokestatic net/rim/device/api/crypto/ECDHKeyAgreement.generateSharedSecret (Lnet/rim/device/api/crypto/ECPrivateKey;Lnet/rim/device/api/crypto/ECPublicKey;Z)[B
      // 5ad: astore 19
      // 5af: aload 9
      // 5b1: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 5b4: astore 20
      // 5b6: aload 20
      // 5b8: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 5bb: astore 21
      // 5bd: new java/lang/Object
      // 5c0: dup
      // 5c1: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 5c4: astore 22
      // 5c6: new java/lang/Object
      // 5c9: dup
      // 5ca: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 5cd: astore 23
      // 5cf: aload 0
      // 5d0: aload 23
      // 5d2: aload 21
      // 5d4: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeKeyWrapInformation (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;Lnet/rim/device/api/crypto/oid/OID;)V
      // 5d7: aload 22
      // 5d9: aload 23
      // 5db: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 5de: aload 8
      // 5e0: ifnull 5ec
      // 5e3: aload 22
      // 5e5: aload 8
      // 5e7: bipush 1
      // 5e8: bipush 0
      // 5e9: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // 5ec: aload 21
      // 5ee: ldc_w -1721352904
      // 5f1: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 5f4: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 5f7: ifeq 632
      // 5fa: aload 22
      // 5fc: bipush 4
      // 5fd: newarray 8
      // 5ff: dup
      // 600: bipush 0
      // 601: bipush 0
      // 602: bastore
      // 603: dup
      // 604: bipush 1
      // 605: bipush 0
      // 606: bastore
      // 607: dup
      // 608: bipush 2
      // 609: bipush 0
      // 60a: bastore
      // 60b: dup
      // 60c: bipush 3
      // 60d: bipush -64
      // 60f: bastore
      // 610: bipush 1
      // 611: bipush 2
      // 613: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // 616: new java/lang/Object
      // 619: dup
      // 61a: aload 19
      // 61c: aload 0
      // 61d: aload 22
      // 61f: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)[B
      // 622: invokespecial net/rim/device/api/crypto/X963KDFPseudoRandomSource.<init> ([B[B)V
      // 625: astore 24
      // 627: aload 0
      // 628: bipush 1
      // 629: aload 24
      // 62b: aload 13
      // 62d: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // 630: aload 2
      // 631: areturn
      // 632: aload 21
      // 634: ldc_w -1721350856
      // 637: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 63a: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 63d: ifeq 685
      // 640: aload 22
      // 642: bipush 4
      // 643: newarray 8
      // 645: dup
      // 646: bipush 0
      // 647: bipush 0
      // 648: bastore
      // 649: dup
      // 64a: bipush 1
      // 64b: bipush 0
      // 64c: bastore
      // 64d: dup
      // 64e: bipush 2
      // 64f: bipush 0
      // 650: bastore
      // 651: dup
      // 652: bipush 3
      // 653: bipush -128
      // 655: bastore
      // 656: bipush 1
      // 657: bipush 2
      // 659: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // 65c: new java/lang/Object
      // 65f: dup
      // 660: aload 19
      // 662: aload 0
      // 663: aload 22
      // 665: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)[B
      // 668: invokespecial net/rim/device/api/crypto/X963KDFPseudoRandomSource.<init> ([B[B)V
      // 66b: astore 24
      // 66d: aload 20
      // 66f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // 672: bipush 58
      // 674: if_icmpeq 679
      // 677: aconst_null
      // 678: areturn
      // 679: aload 0
      // 67a: bipush 2
      // 67c: aload 24
      // 67e: aload 13
      // 680: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // 683: aload 2
      // 684: areturn
      // 685: aload 21
      // 687: ldc_w 552133494
      // 68a: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 68d: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 690: ifeq 6d9
      // 693: aload 22
      // 695: bipush 4
      // 696: newarray 8
      // 698: dup
      // 699: bipush 0
      // 69a: bipush 0
      // 69b: bastore
      // 69c: dup
      // 69d: bipush 1
      // 69e: bipush 0
      // 69f: bastore
      // 6a0: dup
      // 6a1: bipush 2
      // 6a2: bipush 0
      // 6a3: bastore
      // 6a4: dup
      // 6a5: bipush 3
      // 6a6: bipush -128
      // 6a8: bastore
      // 6a9: bipush 1
      // 6aa: bipush 2
      // 6ac: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // 6af: new java/lang/Object
      // 6b2: dup
      // 6b3: aload 19
      // 6b5: aload 0
      // 6b6: aload 22
      // 6b8: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)[B
      // 6bb: invokespecial net/rim/device/api/crypto/X963KDFPseudoRandomSource.<init> ([B[B)V
      // 6be: astore 24
      // 6c0: aload 20
      // 6c2: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // 6c5: sipush 128
      // 6c8: if_icmpeq 6cd
      // 6cb: aconst_null
      // 6cc: areturn
      // 6cd: aload 0
      // 6ce: bipush 3
      // 6d0: aload 24
      // 6d2: aload 13
      // 6d4: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // 6d7: aload 2
      // 6d8: areturn
      // 6d9: aload 21
      // 6db: ldc_w 541647732
      // 6de: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 6e1: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 6e4: ifeq 720
      // 6e7: aload 22
      // 6e9: bipush 4
      // 6ea: newarray 8
      // 6ec: dup
      // 6ed: bipush 0
      // 6ee: bipush 0
      // 6ef: bastore
      // 6f0: dup
      // 6f1: bipush 1
      // 6f2: bipush 0
      // 6f3: bastore
      // 6f4: dup
      // 6f5: bipush 2
      // 6f6: bipush 0
      // 6f7: bastore
      // 6f8: dup
      // 6f9: bipush 3
      // 6fa: bipush -128
      // 6fc: bastore
      // 6fd: bipush 1
      // 6fe: bipush 2
      // 700: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // 703: new java/lang/Object
      // 706: dup
      // 707: aload 19
      // 709: aload 0
      // 70a: aload 22
      // 70c: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)[B
      // 70f: invokespecial net/rim/device/api/crypto/X963KDFPseudoRandomSource.<init> ([B[B)V
      // 712: astore 24
      // 714: aload 0
      // 715: bipush 4
      // 717: aload 24
      // 719: aload 13
      // 71b: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // 71e: aload 2
      // 71f: areturn
      // 720: aload 21
      // 722: ldc_w 546890612
      // 725: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 728: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 72b: ifeq 767
      // 72e: aload 22
      // 730: bipush 4
      // 731: newarray 8
      // 733: dup
      // 734: bipush 0
      // 735: bipush 0
      // 736: bastore
      // 737: dup
      // 738: bipush 1
      // 739: bipush 0
      // 73a: bastore
      // 73b: dup
      // 73c: bipush 2
      // 73d: bipush 0
      // 73e: bastore
      // 73f: dup
      // 740: bipush 3
      // 741: bipush -64
      // 743: bastore
      // 744: bipush 1
      // 745: bipush 2
      // 747: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // 74a: new java/lang/Object
      // 74d: dup
      // 74e: aload 19
      // 750: aload 0
      // 751: aload 22
      // 753: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)[B
      // 756: invokespecial net/rim/device/api/crypto/X963KDFPseudoRandomSource.<init> ([B[B)V
      // 759: astore 24
      // 75b: aload 0
      // 75c: bipush 5
      // 75e: aload 24
      // 760: aload 13
      // 762: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // 765: aload 2
      // 766: areturn
      // 767: aload 21
      // 769: ldc_w 552133492
      // 76c: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 76f: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 772: ifeq 7ad
      // 775: aload 22
      // 777: bipush 4
      // 778: newarray 8
      // 77a: dup
      // 77b: bipush 0
      // 77c: bipush 0
      // 77d: bastore
      // 77e: dup
      // 77f: bipush 1
      // 780: bipush 0
      // 781: bastore
      // 782: dup
      // 783: bipush 2
      // 784: bipush 1
      // 785: bastore
      // 786: dup
      // 787: bipush 3
      // 788: bipush 0
      // 789: bastore
      // 78a: bipush 1
      // 78b: bipush 2
      // 78d: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // 790: new java/lang/Object
      // 793: dup
      // 794: aload 19
      // 796: aload 0
      // 797: aload 22
      // 799: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)[B
      // 79c: invokespecial net/rim/device/api/crypto/X963KDFPseudoRandomSource.<init> ([B[B)V
      // 79f: astore 24
      // 7a1: aload 0
      // 7a2: bipush 6
      // 7a4: aload 24
      // 7a6: aload 13
      // 7a8: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // 7ab: aload 2
      // 7ac: areturn
      // 7ad: aconst_null
      // 7ae: areturn
      // 7af: aload 10
      // 7b1: ldc_w 549068105
      // 7b4: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 7b7: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 7ba: ifne 7c0
      // 7bd: goto d12
      // 7c0: aconst_null
      // 7c1: astore 12
      // 7c3: aconst_null
      // 7c4: astore 13
      // 7c6: aconst_null
      // 7c7: astore 14
      // 7c9: aload 11
      // 7cb: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.endOfStream ()Z
      // 7ce: ifeq 7d4
      // 7d1: goto 906
      // 7d4: aload 11
      // 7d6: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 7d9: astore 15
      // 7db: aload 15
      // 7dd: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 7e0: bipush 16
      // 7e2: if_icmpeq 7e8
      // 7e5: goto 86f
      // 7e8: aload 15
      // 7ea: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 7ed: astore 16
      // 7ef: new java/lang/Object
      // 7f2: dup
      // 7f3: aload 16
      // 7f5: invokespecial net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.<init> (Lnet/rim/device/api/crypto/asn1/ASN1InputStream;)V
      // 7f8: astore 17
      // 7fa: aload 16
      // 7fc: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readIntegerAsByteArray ()[B
      // 7ff: astore 18
      // 801: aload 0
      // 802: aload 17
      // 804: aload 18
      // 806: bipush 0
      // 807: ldc_w "EC"
      // 80a: bipush 16
      // 80c: i2l
      // 80d: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.findKeyWithIssuerAndSerialNumber (Lnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;[BZLjava/lang/String;J)Lnet/rim/device/api/crypto/Key;
      // 810: astore 19
      // 812: aload 19
      // 814: dup
      // 815: instanceof java/lang/Object
      // 818: ifne 81f
      // 81b: pop
      // 81c: goto 824
      // 81f: checkcast java/lang/Object
      // 822: astore 12
      // 824: aload 12
      // 826: ifnull 856
      // 829: new java/lang/Object
      // 82c: dup
      // 82d: aload 0
      // 82e: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientCertificate Lnet/rim/device/api/crypto/certificate/Certificate;
      // 831: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 836: checkcast java/lang/Object
      // 839: aload 12
      // 83b: invokespecial net/rim/device/api/crypto/ECKeyPair.<init> (Lnet/rim/device/api/crypto/ECPublicKey;Lnet/rim/device/api/crypto/ECPrivateKey;)V
      // 83e: astore 13
      // 840: goto 856
      // 843: astore 20
      // 845: aconst_null
      // 846: astore 13
      // 848: aconst_null
      // 849: astore 12
      // 84b: goto 856
      // 84e: astore 20
      // 850: aconst_null
      // 851: astore 13
      // 853: aconst_null
      // 854: astore 12
      // 856: new net/rim/device/api/crypto/cms/CMSEntityIdentifier
      // 859: dup
      // 85a: aload 18
      // 85c: aload 17
      // 85e: invokespecial net/rim/device/api/crypto/cms/CMSEntityIdentifier.<init> ([BLnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;)V
      // 861: astore 2
      // 862: aload 19
      // 864: instanceof java/lang/Object
      // 867: ifne 86d
      // 86a: goto 8f7
      // 86d: aload 2
      // 86e: areturn
      // 86f: aload 15
      // 871: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // 874: ifne 8ef
      // 877: aload 15
      // 879: bipush 2
      // 87b: bipush 0
      // 87c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence (II)Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 87f: astore 16
      // 881: aload 16
      // 883: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 886: astore 17
      // 888: aload 0
      // 889: aload 17
      // 88b: bipush 0
      // 88c: ldc_w "EC"
      // 88f: bipush 16
      // 891: i2l
      // 892: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.findKeyWithSubjectKeyIdentifier ([BZLjava/lang/String;J)Lnet/rim/device/api/crypto/Key;
      // 895: astore 18
      // 897: aload 18
      // 899: dup
      // 89a: instanceof java/lang/Object
      // 89d: ifne 8a4
      // 8a0: pop
      // 8a1: goto 8a9
      // 8a4: checkcast java/lang/Object
      // 8a7: astore 12
      // 8a9: aload 12
      // 8ab: ifnull 8db
      // 8ae: new java/lang/Object
      // 8b1: dup
      // 8b2: aload 0
      // 8b3: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientCertificate Lnet/rim/device/api/crypto/certificate/Certificate;
      // 8b6: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 8bb: checkcast java/lang/Object
      // 8be: aload 12
      // 8c0: invokespecial net/rim/device/api/crypto/ECKeyPair.<init> (Lnet/rim/device/api/crypto/ECPublicKey;Lnet/rim/device/api/crypto/ECPrivateKey;)V
      // 8c3: astore 13
      // 8c5: goto 8db
      // 8c8: astore 19
      // 8ca: aconst_null
      // 8cb: astore 13
      // 8cd: aconst_null
      // 8ce: astore 12
      // 8d0: goto 8db
      // 8d3: astore 19
      // 8d5: aconst_null
      // 8d6: astore 13
      // 8d8: aconst_null
      // 8d9: astore 12
      // 8db: new net/rim/device/api/crypto/cms/CMSEntityIdentifier
      // 8de: dup
      // 8df: aload 17
      // 8e1: invokespecial net/rim/device/api/crypto/cms/CMSEntityIdentifier.<init> ([B)V
      // 8e4: astore 2
      // 8e5: aload 18
      // 8e7: instanceof java/lang/Object
      // 8ea: ifeq 8f7
      // 8ed: aload 2
      // 8ee: areturn
      // 8ef: new net/rim/device/api/crypto/cms/CMSParsingException
      // 8f2: dup
      // 8f3: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 8f6: athrow
      // 8f7: aload 12
      // 8f9: ifnonnull 8ff
      // 8fc: goto 7c9
      // 8ff: aload 15
      // 901: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray ()[B
      // 904: astore 14
      // 906: aload 12
      // 908: ifnonnull 90d
      // 90b: aconst_null
      // 90c: areturn
      // 90d: aconst_null
      // 90e: astore 15
      // 910: aload 5
      // 912: ifnonnull 918
      // 915: goto 9bd
      // 918: new java/lang/Object
      // 91b: dup
      // 91c: aload 5
      // 91e: invokespecial net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.<init> (Lnet/rim/device/api/crypto/asn1/ASN1InputStream;)V
      // 921: astore 16
      // 923: aload 5
      // 925: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readIntegerAsByteArray ()[B
      // 928: astore 17
      // 92a: aload 0
      // 92b: aload 16
      // 92d: aload 17
      // 92f: bipush 1
      // 930: ldc_w "EC"
      // 933: bipush 16
      // 935: i2l
      // 936: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.findKeyWithIssuerAndSerialNumber (Lnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;[BZLjava/lang/String;J)Lnet/rim/device/api/crypto/Key;
      // 939: astore 18
      // 93b: aload 18
      // 93d: dup
      // 93e: instanceof java/lang/Object
      // 941: ifne 948
      // 944: pop
      // 945: goto 94d
      // 948: checkcast java/lang/Object
      // 94b: astore 15
      // 94d: aload 15
      // 94f: ifnull 955
      // 952: goto a8c
      // 955: aload 0
      // 956: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 959: ifnonnull 95e
      // 95c: aconst_null
      // 95d: areturn
      // 95e: bipush 0
      // 95f: istore 19
      // 961: iload 19
      // 963: aload 0
      // 964: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 967: arraylength
      // 968: if_icmpge 9ad
      // 96b: aload 0
      // 96c: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 96f: iload 19
      // 971: aaload
      // 972: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getSerialNumber ()[B
      // 975: aload 17
      // 977: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 97a: ifeq 9a7
      // 97d: aload 16
      // 97f: aload 0
      // 980: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 983: iload 19
      // 985: aaload
      // 986: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getIssuer ()Lnet/rim/device/api/crypto/certificate/DistinguishedName;
      // 989: invokevirtual net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.equals (Ljava/lang/Object;)Z
      // 98c: ifeq 9a7
      // 98f: aload 0
      // 990: aload 0
      // 991: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 994: iload 19
      // 996: aaload
      // 997: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.getPublicKeyFromPool (Lnet/rim/device/api/crypto/certificate/Certificate;)Lnet/rim/device/api/crypto/PublicKey;
      // 99a: checkcast java/lang/Object
      // 99d: astore 15
      // 99f: aload 15
      // 9a1: ifnull 9a7
      // 9a4: goto 9ad
      // 9a7: iinc 19 1
      // 9aa: goto 961
      // 9ad: aload 15
      // 9af: ifnull 9b5
      // 9b2: goto a8c
      // 9b5: new net/rim/device/api/crypto/cms/CMSParsingException
      // 9b8: dup
      // 9b9: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // 9bc: athrow
      // 9bd: aload 6
      // 9bf: ifnull a3e
      // 9c2: aload 0
      // 9c3: aload 6
      // 9c5: bipush 1
      // 9c6: ldc_w "EC"
      // 9c9: bipush 16
      // 9cb: i2l
      // 9cc: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.findKeyWithSubjectKeyIdentifier ([BZLjava/lang/String;J)Lnet/rim/device/api/crypto/Key;
      // 9cf: astore 16
      // 9d1: aload 16
      // 9d3: dup
      // 9d4: instanceof java/lang/Object
      // 9d7: ifne 9de
      // 9da: pop
      // 9db: goto 9e3
      // 9de: checkcast java/lang/Object
      // 9e1: astore 15
      // 9e3: aload 15
      // 9e5: ifnull 9eb
      // 9e8: goto a8c
      // 9eb: aload 0
      // 9ec: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 9ef: ifnonnull 9f4
      // 9f2: aconst_null
      // 9f3: areturn
      // 9f4: bipush 0
      // 9f5: istore 17
      // 9f7: iload 17
      // 9f9: aload 0
      // 9fa: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 9fd: arraylength
      // 9fe: if_icmpge a31
      // a01: aload 0
      // a02: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // a05: iload 17
      // a07: aaload
      // a08: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getSubjectKeyIdentifier ()[B
      // a0b: aload 6
      // a0d: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // a10: ifeq a2b
      // a13: aload 0
      // a14: aload 0
      // a15: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._pool [Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // a18: iload 17
      // a1a: aaload
      // a1b: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.getPublicKeyFromPool (Lnet/rim/device/api/crypto/certificate/Certificate;)Lnet/rim/device/api/crypto/PublicKey;
      // a1e: checkcast java/lang/Object
      // a21: astore 15
      // a23: aload 15
      // a25: ifnull a2b
      // a28: goto a31
      // a2b: iinc 17 1
      // a2e: goto 9f7
      // a31: aload 15
      // a33: ifnonnull a8c
      // a36: new net/rim/device/api/crypto/cms/CMSParsingException
      // a39: dup
      // a3a: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // a3d: athrow
      // a3e: aload 7
      // a40: ifnull a84
      // a43: aload 7
      // a45: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // a48: astore 16
      // a4a: aload 16
      // a4c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // a4f: astore 17
      // a51: aload 17
      // a53: ldc_w -1487624216
      // a56: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // a59: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // a5c: ifne a67
      // a5f: new net/rim/device/api/crypto/cms/CMSParsingException
      // a62: dup
      // a63: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // a66: athrow
      // a67: aload 7
      // a69: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readBitString ()Lnet/rim/device/api/crypto/asn1/ASN1BitSet;
      // a6c: invokevirtual net/rim/device/api/crypto/asn1/ASN1BitSet.toByteArray ()[B
      // a6f: astore 18
      // a71: new java/lang/Object
      // a74: dup
      // a75: aload 12
      // a77: invokevirtual net/rim/device/api/crypto/ECPrivateKey.getECCryptoSystem ()Lnet/rim/device/api/crypto/ECCryptoSystem;
      // a7a: aload 18
      // a7c: invokespecial net/rim/device/api/crypto/ECPublicKey.<init> (Lnet/rim/device/api/crypto/ECCryptoSystem;[B)V
      // a7f: astore 15
      // a81: goto a8c
      // a84: new net/rim/device/api/crypto/cms/CMSParsingException
      // a87: dup
      // a88: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // a8b: athrow
      // a8c: aload 8
      // a8e: ifnonnull a99
      // a91: new net/rim/device/api/crypto/cms/CMSParsingException
      // a94: dup
      // a95: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // a98: athrow
      // a99: new java/lang/Object
      // a9c: dup
      // a9d: aload 8
      // a9f: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> ([B)V
      // aa2: astore 16
      // aa4: aload 16
      // aa6: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // aa9: astore 17
      // aab: aload 17
      // aad: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // ab0: astore 18
      // ab2: aload 18
      // ab4: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // ab7: astore 19
      // ab9: aload 19
      // abb: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // abe: astore 20
      // ac0: aload 20
      // ac2: ldc_w -1487624216
      // ac5: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // ac8: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // acb: ifne ad6
      // ace: new net/rim/device/api/crypto/cms/CMSParsingException
      // ad1: dup
      // ad2: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // ad5: athrow
      // ad6: aload 18
      // ad8: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readBitString ()Lnet/rim/device/api/crypto/asn1/ASN1BitSet;
      // adb: invokevirtual net/rim/device/api/crypto/asn1/ASN1BitSet.toByteArray ()[B
      // ade: astore 21
      // ae0: new java/lang/Object
      // ae3: dup
      // ae4: aload 12
      // ae6: invokevirtual net/rim/device/api/crypto/ECPrivateKey.getECCryptoSystem ()Lnet/rim/device/api/crypto/ECCryptoSystem;
      // ae9: aload 21
      // aeb: invokespecial net/rim/device/api/crypto/ECPublicKey.<init> (Lnet/rim/device/api/crypto/ECCryptoSystem;[B)V
      // aee: astore 22
      // af0: aconst_null
      // af1: astore 23
      // af3: aload 17
      // af5: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.peekNextTag ()I
      // af8: ifne b04
      // afb: aload 17
      // afd: bipush 1
      // afe: bipush 0
      // aff: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOctetStringAsByteArray (II)[B
      // b02: astore 23
      // b04: aload 12
      // b06: aload 13
      // b08: aload 15
      // b0a: aload 22
      // b0c: bipush 1
      // b0d: invokestatic net/rim/device/api/crypto/ECMQVKeyAgreement.generateSharedSecret (Lnet/rim/device/api/crypto/ECPrivateKey;Lnet/rim/device/api/crypto/ECKeyPair;Lnet/rim/device/api/crypto/ECPublicKey;Lnet/rim/device/api/crypto/ECPublicKey;Z)[B
      // b10: astore 24
      // b12: aload 9
      // b14: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // b17: astore 25
      // b19: aload 25
      // b1b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // b1e: astore 26
      // b20: new java/lang/Object
      // b23: dup
      // b24: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // b27: astore 27
      // b29: new java/lang/Object
      // b2c: dup
      // b2d: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // b30: astore 28
      // b32: aload 0
      // b33: aload 28
      // b35: aload 26
      // b37: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeKeyWrapInformation (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;Lnet/rim/device/api/crypto/oid/OID;)V
      // b3a: aload 27
      // b3c: aload 28
      // b3e: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // b41: aload 23
      // b43: ifnull b4f
      // b46: aload 27
      // b48: aload 23
      // b4a: bipush 1
      // b4b: bipush 0
      // b4c: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // b4f: aload 26
      // b51: ldc_w -1721352904
      // b54: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // b57: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // b5a: ifeq b95
      // b5d: aload 27
      // b5f: bipush 4
      // b60: newarray 8
      // b62: dup
      // b63: bipush 0
      // b64: bipush 0
      // b65: bastore
      // b66: dup
      // b67: bipush 1
      // b68: bipush 0
      // b69: bastore
      // b6a: dup
      // b6b: bipush 2
      // b6c: bipush 0
      // b6d: bastore
      // b6e: dup
      // b6f: bipush 3
      // b70: bipush -64
      // b72: bastore
      // b73: bipush 1
      // b74: bipush 2
      // b76: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // b79: new java/lang/Object
      // b7c: dup
      // b7d: aload 24
      // b7f: aload 0
      // b80: aload 27
      // b82: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)[B
      // b85: invokespecial net/rim/device/api/crypto/X963KDFPseudoRandomSource.<init> ([B[B)V
      // b88: astore 29
      // b8a: aload 0
      // b8b: bipush 1
      // b8c: aload 29
      // b8e: aload 14
      // b90: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // b93: aload 2
      // b94: areturn
      // b95: aload 26
      // b97: ldc_w -1721350856
      // b9a: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // b9d: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // ba0: ifeq be8
      // ba3: aload 27
      // ba5: bipush 4
      // ba6: newarray 8
      // ba8: dup
      // ba9: bipush 0
      // baa: bipush 0
      // bab: bastore
      // bac: dup
      // bad: bipush 1
      // bae: bipush 0
      // baf: bastore
      // bb0: dup
      // bb1: bipush 2
      // bb2: bipush 0
      // bb3: bastore
      // bb4: dup
      // bb5: bipush 3
      // bb6: bipush -128
      // bb8: bastore
      // bb9: bipush 1
      // bba: bipush 2
      // bbc: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // bbf: new java/lang/Object
      // bc2: dup
      // bc3: aload 24
      // bc5: aload 0
      // bc6: aload 27
      // bc8: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)[B
      // bcb: invokespecial net/rim/device/api/crypto/X963KDFPseudoRandomSource.<init> ([B[B)V
      // bce: astore 29
      // bd0: aload 25
      // bd2: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // bd5: bipush 58
      // bd7: if_icmpeq bdc
      // bda: aconst_null
      // bdb: areturn
      // bdc: aload 0
      // bdd: bipush 2
      // bdf: aload 29
      // be1: aload 14
      // be3: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // be6: aload 2
      // be7: areturn
      // be8: aload 26
      // bea: ldc_w 552133494
      // bed: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // bf0: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // bf3: ifeq c3c
      // bf6: aload 27
      // bf8: bipush 4
      // bf9: newarray 8
      // bfb: dup
      // bfc: bipush 0
      // bfd: bipush 0
      // bfe: bastore
      // bff: dup
      // c00: bipush 1
      // c01: bipush 0
      // c02: bastore
      // c03: dup
      // c04: bipush 2
      // c05: bipush 0
      // c06: bastore
      // c07: dup
      // c08: bipush 3
      // c09: bipush -128
      // c0b: bastore
      // c0c: bipush 1
      // c0d: bipush 2
      // c0f: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // c12: new java/lang/Object
      // c15: dup
      // c16: aload 24
      // c18: aload 0
      // c19: aload 27
      // c1b: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)[B
      // c1e: invokespecial net/rim/device/api/crypto/X963KDFPseudoRandomSource.<init> ([B[B)V
      // c21: astore 29
      // c23: aload 25
      // c25: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readInteger ()I
      // c28: sipush 128
      // c2b: if_icmpeq c30
      // c2e: aconst_null
      // c2f: areturn
      // c30: aload 0
      // c31: bipush 3
      // c33: aload 29
      // c35: aload 14
      // c37: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // c3a: aload 2
      // c3b: areturn
      // c3c: aload 26
      // c3e: ldc_w 541647732
      // c41: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // c44: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // c47: ifeq c83
      // c4a: aload 27
      // c4c: bipush 4
      // c4d: newarray 8
      // c4f: dup
      // c50: bipush 0
      // c51: bipush 0
      // c52: bastore
      // c53: dup
      // c54: bipush 1
      // c55: bipush 0
      // c56: bastore
      // c57: dup
      // c58: bipush 2
      // c59: bipush 0
      // c5a: bastore
      // c5b: dup
      // c5c: bipush 3
      // c5d: bipush -128
      // c5f: bastore
      // c60: bipush 1
      // c61: bipush 2
      // c63: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // c66: new java/lang/Object
      // c69: dup
      // c6a: aload 24
      // c6c: aload 0
      // c6d: aload 27
      // c6f: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)[B
      // c72: invokespecial net/rim/device/api/crypto/X963KDFPseudoRandomSource.<init> ([B[B)V
      // c75: astore 29
      // c77: aload 0
      // c78: bipush 4
      // c7a: aload 29
      // c7c: aload 14
      // c7e: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // c81: aload 2
      // c82: areturn
      // c83: aload 26
      // c85: ldc_w 546890612
      // c88: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // c8b: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // c8e: ifeq cca
      // c91: aload 27
      // c93: bipush 4
      // c94: newarray 8
      // c96: dup
      // c97: bipush 0
      // c98: bipush 0
      // c99: bastore
      // c9a: dup
      // c9b: bipush 1
      // c9c: bipush 0
      // c9d: bastore
      // c9e: dup
      // c9f: bipush 2
      // ca0: bipush 0
      // ca1: bastore
      // ca2: dup
      // ca3: bipush 3
      // ca4: bipush -64
      // ca6: bastore
      // ca7: bipush 1
      // ca8: bipush 2
      // caa: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // cad: new java/lang/Object
      // cb0: dup
      // cb1: aload 24
      // cb3: aload 0
      // cb4: aload 27
      // cb6: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)[B
      // cb9: invokespecial net/rim/device/api/crypto/X963KDFPseudoRandomSource.<init> ([B[B)V
      // cbc: astore 29
      // cbe: aload 0
      // cbf: bipush 5
      // cc1: aload 29
      // cc3: aload 14
      // cc5: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // cc8: aload 2
      // cc9: areturn
      // cca: aload 26
      // ccc: ldc_w 552133492
      // ccf: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // cd2: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // cd5: ifeq d10
      // cd8: aload 27
      // cda: bipush 4
      // cdb: newarray 8
      // cdd: dup
      // cde: bipush 0
      // cdf: bipush 0
      // ce0: bastore
      // ce1: dup
      // ce2: bipush 1
      // ce3: bipush 0
      // ce4: bastore
      // ce5: dup
      // ce6: bipush 2
      // ce7: bipush 1
      // ce8: bastore
      // ce9: dup
      // cea: bipush 3
      // ceb: bipush 0
      // cec: bastore
      // ced: bipush 1
      // cee: bipush 2
      // cf0: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // cf3: new java/lang/Object
      // cf6: dup
      // cf7: aload 24
      // cf9: aload 0
      // cfa: aload 27
      // cfc: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)[B
      // cff: invokespecial net/rim/device/api/crypto/X963KDFPseudoRandomSource.<init> ([B[B)V
      // d02: astore 29
      // d04: aload 0
      // d05: bipush 6
      // d07: aload 29
      // d09: aload 14
      // d0b: invokespecial net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream.setSessionKey (ILnet/rim/device/api/crypto/PseudoRandomSource;[B)V
      // d0e: aload 2
      // d0f: areturn
      // d10: aconst_null
      // d11: areturn
      // d12: new net/rim/device/api/crypto/cms/CMSParsingException
      // d15: dup
      // d16: invokespecial net/rim/device/api/crypto/cms/CMSParsingException.<init> ()V
      // d19: athrow
      // try (1015 -> 1024): 1025 null
      // try (1015 -> 1024): 1031 null
      // try (1077 -> 1086): 1087 null
      // try (1077 -> 1086): 1093 null
   }

   private final CMSEntityIdentifier readKeyTransport(ASN1InputByteArray recipientInfos) throws CMSParsingException {
      recipientInfos.readSequence();
      int version = recipientInfos.readInteger();
      RSAPrivateKey key = null;
      CMSEntityIdentifier identifier;
      if (recipientInfos.peekNextTag() == 0) {
         if (version != 2) {
            throw new CMSParsingException();
         }

         byte[] subjectKeyIdentifier = recipientInfos.readOctetString(2, 0);
         Key readKey = this.findKeyWithSubjectKeyIdentifier(subjectKeyIdentifier, false, "RSA", 4);
         if (readKey instanceof Object) {
            key = (RSAPrivateKey)readKey;
         }

         identifier = new CMSEntityIdentifier(subjectKeyIdentifier);
         if (readKey instanceof Object) {
            return identifier;
         }

         if (key == null) {
            return null;
         }
      } else {
         if (version != 0) {
            throw new CMSParsingException();
         }

         recipientInfos.readSequence();
         X509DistinguishedName issuer = (X509DistinguishedName)(new Object(recipientInfos.readFieldAsByteArray()));
         byte[] serialNumber = recipientInfos.readIntegerAsByteArray();
         Key readKey = this.findKeyWithIssuerAndSerialNumber(issuer, serialNumber, false, "RSA", 4);
         if (readKey instanceof Object) {
            key = (RSAPrivateKey)readKey;
         }

         identifier = new CMSEntityIdentifier(serialNumber, issuer);
         if (readKey instanceof Object) {
            return identifier;
         }

         if (key == null) {
            return null;
         }
      }

      recipientInfos.readSequence();
      OID keyAlgorithm = recipientInfos.readOID();
      byte[] params = recipientInfos.readFieldAsByteArray();
      byte[] encryptedKey = recipientInfos.readOctetString();
      ByteArrayInputStream in = (ByteArrayInputStream)(new Object(encryptedKey));
      BlockDecryptor cryptoStream;
      if (keyAlgorithm.equals(OIDs.getOID(543426108))) {
         ASN1InputByteArray parameters = (ASN1InputByteArray)(new Object(params));
         parameters.readSequence();
         Digest digest = null;
         if (parameters.peekNextTag() == 0) {
            parameters.readSequence(2, 0);
            OID digestOID = parameters.readOID();

            try {
               digest = DigestFactory.getInstance(OIDs.getAssociatedString(3134008036018563479L, digestOID));
            } finally {
               ;
            }
         } else {
            digest = (Digest)(new Object());
         }

         cryptoStream = (BlockDecryptor)(new Object((BlockUnformatterEngine)(new Object((PrivateKeyDecryptorEngine)(new Object(key)), digest)), in));
      } else {
         cryptoStream = (BlockDecryptor)(new Object((BlockUnformatterEngine)(new Object((PrivateKeyDecryptorEngine)(new Object(key)))), in));
      }

      byte[] tempdata = new byte[encryptedKey.length];
      int length = cryptoStream.read(tempdata, 0, tempdata.length);
      byte[] data = new byte[length];
      System.arraycopy(tempdata, 0, data, 0, length);
      this._sessionKeyData = data;
      return identifier;
   }

   private final Key findKeyWithIssuerAndSerialNumber(X509DistinguishedName issuer, byte[] serialNumber, boolean isPublicKey, String type, long keyUsage) {
      if (this._keyStore != null && issuer != null && serialNumber != null) {
         Object alias = SerialNumberIssuerKeyStoreIndex.getAlias(serialNumber, issuer);
         Enumeration enumeration = this._keyStore.elements(-6470299966859493514L, alias);
         return this.checkKey(enumeration, isPublicKey, type, keyUsage);
      } else {
         return null;
      }
   }

   private final Key findKeyWithSubjectKeyIdentifier(byte[] subjectKeyIdentifier, boolean isPublicKey, String type, long keyUsage) {
      if (this._keyStore != null && subjectKeyIdentifier != null) {
         Enumeration enumeration = this._keyStore.elements(1612863905495138626L, subjectKeyIdentifier);
         return this.checkKey(enumeration, isPublicKey, type, keyUsage);
      } else {
         return null;
      }
   }

   private final Key checkKey(Enumeration param1, boolean param2, String param3, long param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 006: ifne 00c
      // 009: goto 156
      // 00c: aload 1
      // 00d: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 012: checkcast java/lang/Object
      // 015: astore 6
      // 017: iload 2
      // 018: ifeq 050
      // 01b: aload 6
      // 01d: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 022: astore 7
      // 024: aload 7
      // 026: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 02b: astore 8
      // 02d: aload 8
      // 02f: invokeinterface net/rim/device/api/crypto/Key.getAlgorithm ()Ljava/lang/String; 1
      // 034: aload 3
      // 035: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 038: ifeq 000
      // 03b: aload 7
      // 03d: lload 4
      // 03f: invokeinterface net/rim/device/api/crypto/certificate/Certificate.queryKeyUsage (J)I 3
      // 044: ifeq 000
      // 047: aload 0
      // 048: aload 7
      // 04a: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientCertificate Lnet/rim/device/api/crypto/certificate/Certificate;
      // 04d: aload 8
      // 04f: areturn
      // 050: aload 6
      // 052: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isPrivateKeySet ()Z 1
      // 057: ifeq 000
      // 05a: aload 0
      // 05b: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 05e: ifnull 0a1
      // 061: aload 6
      // 063: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 068: astore 7
      // 06a: aload 7
      // 06c: ifnonnull 074
      // 06f: aload 0
      // 070: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 073: areturn
      // 074: aload 7
      // 076: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 07b: invokeinterface net/rim/device/api/crypto/Key.getAlgorithm ()Ljava/lang/String; 1
      // 080: aload 3
      // 081: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 084: ifeq 000
      // 087: aload 7
      // 089: lload 4
      // 08b: invokeinterface net/rim/device/api/crypto/certificate/Certificate.queryKeyUsage (J)I 3
      // 090: ifne 096
      // 093: goto 000
      // 096: aload 0
      // 097: aload 7
      // 099: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientCertificate Lnet/rim/device/api/crypto/certificate/Certificate;
      // 09c: aload 0
      // 09d: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._sessionKey Lnet/rim/device/api/crypto/SymmetricKey;
      // 0a0: areturn
      // 0a1: aload 0
      // 0a2: getfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._displayUI Z
      // 0a5: ifne 0ab
      // 0a8: goto 000
      // 0ab: aload 6
      // 0ad: getstatic net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0b0: sipush 6068
      // 0b3: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0b6: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getTicket (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket; 2
      // 0bb: astore 7
      // 0bd: aload 6
      // 0bf: aload 7
      // 0c1: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey; 2
      // 0c6: astore 8
      // 0c8: aload 8
      // 0ca: ifnonnull 0d0
      // 0cd: goto 000
      // 0d0: aload 8
      // 0d2: invokeinterface net/rim/device/api/crypto/PrivateKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 0d7: astore 9
      // 0d9: aload 9
      // 0db: invokeinterface net/rim/device/api/crypto/CryptoSystem.getAlgorithm ()Ljava/lang/String; 1
      // 0e0: astore 10
      // 0e2: aload 9
      // 0e4: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 0e9: istore 11
      // 0eb: aload 10
      // 0ed: aload 3
      // 0ee: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0f1: ifne 0f7
      // 0f4: goto 000
      // 0f7: aload 6
      // 0f9: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 0fe: astore 12
      // 100: aload 12
      // 102: ifnonnull 114
      // 105: aload 0
      // 106: aload 10
      // 108: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientPublicKeyAlgorithm Ljava/lang/String;
      // 10b: aload 0
      // 10c: iload 11
      // 10e: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientPublicKeyBitLength I
      // 111: aload 8
      // 113: areturn
      // 114: aload 12
      // 116: lload 4
      // 118: invokeinterface net/rim/device/api/crypto/certificate/Certificate.queryKeyUsage (J)I 3
      // 11d: ifne 123
      // 120: goto 000
      // 123: aload 0
      // 124: aload 12
      // 126: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientCertificate Lnet/rim/device/api/crypto/certificate/Certificate;
      // 129: aload 0
      // 12a: aload 10
      // 12c: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientPublicKeyAlgorithm Ljava/lang/String;
      // 12f: aload 0
      // 130: iload 11
      // 132: putfield net/rim/device/api/crypto/cms/CMSEnvelopedDataInputStream._recipientPublicKeyBitLength I
      // 135: aload 8
      // 137: areturn
      // 138: astore 6
      // 13a: goto 000
      // 13d: astore 6
      // 13f: goto 000
      // 142: astore 6
      // 144: goto 000
      // 147: astore 6
      // 149: goto 000
      // 14c: astore 6
      // 14e: goto 000
      // 151: astore 6
      // 153: goto 000
      // 156: aconst_null
      // 157: areturn
      // try (4 -> 29): 121 null
      // try (30 -> 43): 121 null
      // try (44 -> 60): 121 null
      // try (61 -> 104): 121 null
      // try (105 -> 120): 121 null
      // try (4 -> 29): 123 null
      // try (30 -> 43): 123 null
      // try (44 -> 60): 123 null
      // try (61 -> 104): 123 null
      // try (105 -> 120): 123 null
      // try (4 -> 29): 125 null
      // try (30 -> 43): 125 null
      // try (44 -> 60): 125 null
      // try (61 -> 104): 125 null
      // try (105 -> 120): 125 null
      // try (4 -> 29): 127 null
      // try (30 -> 43): 127 null
      // try (44 -> 60): 127 null
      // try (61 -> 104): 127 null
      // try (105 -> 120): 127 null
      // try (4 -> 29): 129 null
      // try (30 -> 43): 129 null
      // try (44 -> 60): 129 null
      // try (61 -> 104): 129 null
      // try (105 -> 120): 129 null
      // try (4 -> 29): 131 null
      // try (30 -> 43): 131 null
      // try (44 -> 60): 131 null
      // try (61 -> 104): 131 null
      // try (105 -> 120): 131 null
   }

   private final PublicKey getPublicKeyFromPool(Certificate certificate) {
      Certificate[] originatorChain = CertificateUtilities.buildCertificateChain(certificate, this._pool, null);
      if (certificate.queryKeyUsage(16) != 0) {
         try {
            return originatorChain[0].getPublicKey();
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   private final void setSessionKey(int algorithm, PseudoRandomSource source, byte[] wrappedData) {
      switch (algorithm) {
         case 0:
            return;
         case 1:
         default: {
            byte[] keyData = source.getBytes(24);
            this._sessionKeyData = CMSKeyUnWrap.TripleDESKeyUnWrap((TripleDESKey)(new Object(keyData)), wrappedData);
            return;
         }
         case 2:
            byte[] var8 = source.getBytes(16);
            this._sessionKeyData = CMSKeyUnWrap.RC2KeyUnWrap((RC2Key)(new Object(var8, 128)), wrappedData);
            return;
         case 3:
            byte[] var7 = source.getBytes(16);
            this._sessionKeyData = CMSKeyUnWrap.CASTKeyUnWrap((CAST128Key)(new Object(var7)), wrappedData);
            return;
         case 4:
            byte[] var6 = source.getBytes(16);
            this._sessionKeyData = CMSKeyUnWrap.AESKeyUnWrap((AESKey)(new Object(var6)), wrappedData);
            return;
         case 5:
            byte[] var5 = source.getBytes(24);
            this._sessionKeyData = CMSKeyUnWrap.AESKeyUnWrap((AESKey)(new Object(var5)), wrappedData);
            return;
         case 6: {
            byte[] keyData = source.getBytes(32);
            this._sessionKeyData = CMSKeyUnWrap.AESKeyUnWrap((AESKey)(new Object(keyData)), wrappedData);
         }
      }
   }

   private final void writeKeyWrapInformation(ASN1OutputStream tempAlgIdentifier, OID keyWrapAlgorithm) {
      tempAlgIdentifier.writeOID(keyWrapAlgorithm);
      if (keyWrapAlgorithm.equals(OIDs.getOID(-1721352904))) {
         tempAlgIdentifier.writeNull();
      } else if (keyWrapAlgorithm.equals(OIDs.getOID(-1721350856))) {
         tempAlgIdentifier.writeInteger(58);
      } else {
         if (keyWrapAlgorithm.equals(OIDs.getOID(552133494))) {
            tempAlgIdentifier.writeInteger(128);
         }
      }
   }

   private final byte[] writeSequence(ASN1OutputStream outputSequence) {
      ASN1OutputStream sharedInfoOut = (ASN1OutputStream)(new Object());
      sharedInfoOut.writeSequence(outputSequence);
      return sharedInfoOut.toByteArray();
   }

   @Override
   public final boolean isContentComplete() {
      return this._contentComplete;
   }
}
