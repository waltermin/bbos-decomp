package net.rim.device.api.crypto.cms;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.BlockEncryptor;
import net.rim.device.api.crypto.BlockEncryptorEngine;
import net.rim.device.api.crypto.BlockFormatterEngine;
import net.rim.device.api.crypto.CAST128Key;
import net.rim.device.api.crypto.DHKeyAgreement;
import net.rim.device.api.crypto.DHKeyPair;
import net.rim.device.api.crypto.DHPublicKey;
import net.rim.device.api.crypto.ECDHKeyAgreement;
import net.rim.device.api.crypto.ECKeyPair;
import net.rim.device.api.crypto.ECMQVKeyAgreement;
import net.rim.device.api.crypto.ECPrivateKey;
import net.rim.device.api.crypto.ECPublicKey;
import net.rim.device.api.crypto.EncryptorFactory;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.PKCS5KDF2PseudoRandomSource;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.PublicKeyEncryptorEngine;
import net.rim.device.api.crypto.RC2Key;
import net.rim.device.api.crypto.RFC2631KDFPseudoRandomSource;
import net.rim.device.api.crypto.RSAPublicKey;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.TripleDESKey;
import net.rim.device.api.crypto.X963KDFPseudoRandomSource;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.vm.Array;

public final class CMSEnvelopedDataOutputStream extends CMSOutputStream {
   private int _encryptionScheme;
   private SymmetricKey _sessionKey;
   private InitializationVector _iv;
   private Vector _recipients;
   private boolean _versionZero;
   private boolean _noMoreAdds;
   private ByteArrayOutputStream _outStream;
   private BlockEncryptor _cryptoStream;
   private Vector _attributes = (Vector)(new Object());

   public CMSEnvelopedDataOutputStream(OutputStream out, int contentType, boolean outer) {
      this(out, contentType, outer, 100);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public CMSEnvelopedDataOutputStream(OutputStream out, int contentType, boolean outer, int contentAlgorithm) {
      super(out, contentType, true, outer);
      this._recipients = (Vector)(new Object());
      this._versionZero = true;
      this._outStream = (ByteArrayOutputStream)(new Object());
      this._encryptionScheme = contentAlgorithm;

      try {
         if (contentAlgorithm == 100) {
            this._sessionKey = (SymmetricKey)(new Object());
            this._iv = (InitializationVector)(new Object(8));
         } else if (contentAlgorithm != 101 && contentAlgorithm != 102 && contentAlgorithm != 103) {
            if (contentAlgorithm == 108) {
               this._sessionKey = (SymmetricKey)(new Object());
               this._iv = (InitializationVector)(new Object(8));
            } else {
               if (contentAlgorithm != 104 && contentAlgorithm != 105 && contentAlgorithm != 106) {
                  throw new Object();
               }

               if (contentAlgorithm == 104) {
                  this._sessionKey = (SymmetricKey)(new Object(128));
               } else if (contentAlgorithm == 105) {
                  this._sessionKey = (SymmetricKey)(new Object(192));
               } else {
                  this._sessionKey = (SymmetricKey)(new Object(256));
               }

               this._iv = (InitializationVector)(new Object(16));
            }
         } else {
            if (contentAlgorithm == 101) {
               this._sessionKey = (SymmetricKey)(new Object(40));
            } else if (contentAlgorithm == 102) {
               this._sessionKey = (SymmetricKey)(new Object(64));
            } else {
               this._sessionKey = (SymmetricKey)(new Object(128));
            }

            this._iv = (InitializationVector)(new Object(8));
         }

         this._cryptoStream = (BlockEncryptor)(new Object(
            new CMSBlockFormatterEngine((BlockEncryptorEngine)(new Object(EncryptorFactory.getBlockEncryptorEngine(this._sessionKey), this._iv))),
            this._outStream
         ));
      } catch (Throwable var7) {
         if (!(e instanceof Object)) {
            throw new Object();
         } else {
            throw (Object)e;
         }
      }
   }

   public final void addAttribute(CMSAttribute attribute) {
      this._attributes.addElement(attribute);
   }

   public final void addRecipient(Certificate certificate, int algorithm, PrivateKey privateKey, Certificate originator) {
      this.addRecipientInternal(certificate, algorithm, privateKey, originator);
   }

   public final void addRecipient(Certificate certificate) {
      this.addRecipientInternal(certificate, -1, null, null);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void addRecipientInternal(Certificate certificate, int algorithm, PrivateKey privateKey, Certificate originator) {
      if (this._noMoreAdds) {
         throw new Object();
      }

      boolean var8 = false /* VF: Semaphore variable */;

      PublicKey key;
      try {
         var8 = true;
         key = certificate.getPublicKey();
         var8 = false;
      } finally {
         if (var8) {
            throw new Object();
         }
      }

      String keyAlgorithm = key.getAlgorithm();
      if (algorithm == -1) {
         if (keyAlgorithm.equals("RSA")) {
            algorithm = 0;
         } else if (keyAlgorithm.equals("DH")) {
            algorithm = 5;
         } else {
            algorithm = 2;
         }
      }

      switch (algorithm) {
         case -1:
            throw new Object();
         case 0:
         case 1:
         default:
            if (certificate.queryKeyUsage(4) == 0) {
               throw new Object();
            }

            if (keyAlgorithm.equals("RSA/OAEP")) {
               algorithm = 1;
            } else {
               algorithm = 0;
            }

            if (!keyAlgorithm.equals("RSA")) {
               throw new Object();
            }
            break;
         case 2:
         case 3:
         case 4:
            this._versionZero = false;
            if (certificate.queryKeyUsage(16) == 0) {
               throw new Object();
            }

            if (!keyAlgorithm.equals("EC")) {
               throw new Object();
            }

            if (privateKey != null && !privateKey.getAlgorithm().equals("EC")) {
               throw new Object();
            }
            break;
         case 5:
            this._versionZero = false;
            if (certificate.queryKeyUsage(16) == 0) {
               throw new Object();
            }

            if (!keyAlgorithm.equals("DH")) {
               throw new Object();
            }
      }

      this._recipients.addElement(new Recipient(certificate, algorithm, privateKey, originator, null));
   }

   public final void addRecipient(byte[] password) {
      this._recipients.addElement(new Recipient(null, 6, null, null, password));
   }

   public final SymmetricKey getSessionKey() {
      return this._sessionKey;
   }

   @Override
   public final void write(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         this._cryptoStream.write(data, offset, length);
      } else {
         throw new Object();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void close() {
      this._noMoreAdds = true;

      try {
         ASN1OutputStream envelopedDataOutput = (ASN1OutputStream)(new Object(super._out));
         ASN1OutputStream envelopedData = (ASN1OutputStream)(new Object());
         if (this._versionZero) {
            envelopedData.writeInteger(0);
         } else {
            envelopedData.writeInteger(2);
         }

         Enumeration e = this._recipients.elements();
         ASN1OutputStream recipientInfos;
         if (!e.hasMoreElements()) {
            recipientInfos = null;
         } else {
            recipientInfos = (ASN1OutputStream)(new Object());
         }

         while (e.hasMoreElements()) {
            Recipient recipient = (Recipient)e.nextElement();
            Certificate recipientCertificate = recipient.getCertificate();
            int transportType = recipient.getType();
            PrivateKey privateKey = recipient.getPrivateKey();
            Certificate originator = recipient.getOriginator();
            if (transportType == 0 || transportType == 1) {
               this.writeKeyTransportRSA(recipientCertificate, recipientInfos, transportType);
            } else if (transportType == 6) {
               ASN1OutputStream passwordRecipientInfo = (ASN1OutputStream)(new Object());
               this.writePasswordBasedEncryption(passwordRecipientInfo, recipient.getPassword());
               recipientInfos.writeSequence(passwordRecipientInfo, 2, 3);
            } else {
               ASN1OutputStream keyAgreeRecipientInfo = (ASN1OutputStream)(new Object());
               keyAgreeRecipientInfo.writeInteger(3);
               if (transportType == 5) {
                  this.writeKeyAgreementDH(recipientCertificate, keyAgreeRecipientInfo);
               } else if (transportType != 2 && transportType != 3) {
                  this.writeKeyAgreementECMQV(recipientCertificate, keyAgreeRecipientInfo, privateKey, originator);
               } else {
                  this.writeKeyAgreementECDH(recipientCertificate, keyAgreeRecipientInfo, transportType == 3);
               }

               recipientInfos.writeSequence(keyAgreeRecipientInfo, 2, 1);
            }
         }

         if (recipientInfos != null) {
            envelopedData.writeSet(recipientInfos);
         }

         ASN1OutputStream encryptedContentInfo = (ASN1OutputStream)(new Object());
         encryptedContentInfo.writeOID(super._contentType);
         ASN1OutputStream encryptedAlgorithmIdentifier = (ASN1OutputStream)(new Object());
         if (this._encryptionScheme == 100) {
            encryptedAlgorithmIdentifier.writeOID(OIDs.getOID(-472306990));
            encryptedAlgorithmIdentifier.writeOctetString(this._iv.getData());
         } else if (this._encryptionScheme == 101 || this._encryptionScheme == 102 || this._encryptionScheme == 103) {
            encryptedAlgorithmIdentifier.writeOID(OIDs.getOID(-472312110));
            ASN1OutputStream tempParams = (ASN1OutputStream)(new Object());
            if (this._encryptionScheme == 101) {
               tempParams.writeInteger(160);
            } else if (this._encryptionScheme == 102) {
               tempParams.writeInteger(120);
            } else if (this._encryptionScheme == 103) {
               tempParams.writeInteger(58);
            }

            tempParams.writeOctetString(this._iv.getData());
            encryptedAlgorithmIdentifier.writeSequence(tempParams);
         } else if (this._encryptionScheme == 108) {
            encryptedAlgorithmIdentifier.writeOID(OIDs.getOID(552133493));
            ASN1OutputStream tempParams = (ASN1OutputStream)(new Object());
            tempParams.writeOctetString(this._iv.getData());
            tempParams.writeInteger(128);
            encryptedAlgorithmIdentifier.writeSequence(tempParams);
         } else if (this._encryptionScheme == 104 || this._encryptionScheme == 105 || this._encryptionScheme == 106) {
            if (this._encryptionScheme == 104) {
               encryptedAlgorithmIdentifier.writeOID(OIDs.getOID(540861300));
            } else if (this._encryptionScheme == 105) {
               encryptedAlgorithmIdentifier.writeOID(OIDs.getOID(546104180));
            } else if (this._encryptionScheme == 106) {
               encryptedAlgorithmIdentifier.writeOID(OIDs.getOID(551347060));
            }

            encryptedAlgorithmIdentifier.writeOctetString(this._iv.getData());
         }

         encryptedContentInfo.writeSequence(encryptedAlgorithmIdentifier);
         this._cryptoStream.close();
         this._outStream.close();
         encryptedContentInfo.writeOctetString(this._outStream.toByteArray(), 2, 0);
         envelopedData.writeSequence(encryptedContentInfo);
         int size = this._attributes.size();
         if (size > 0) {
            ASN1OutputStream unProtectedAttributes = (ASN1OutputStream)(new Object());

            for (int i = 0; i < size; i++) {
               CMSAttribute attribute = (CMSAttribute)this._attributes.elementAt(i);
               ASN1OutputStream attributeSequence = (ASN1OutputStream)(new Object());
               attributeSequence.writeOID(attribute.getOID());
               attributeSequence.writeRawByteArray(attribute.getValue());
               unProtectedAttributes.writeSequence(attributeSequence);
            }

            envelopedData.writeSet(unProtectedAttributes, 2, 1);
         }

         if (super._outer) {
            ASN1OutputStream contentInfo = (ASN1OutputStream)(new Object());
            contentInfo.writeOID(OIDs.getOID(542383676));
            contentInfo.writeSequence(envelopedData, 1, 0);
            envelopedDataOutput.writeSequence(contentInfo);
         } else {
            envelopedDataOutput.writeSequence(envelopedData);
         }

         super._out.close();
      } catch (Throwable var13) {
         throw new Object(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void writeKeyTransportRSA(Certificate recipientCertificate, ASN1OutputStream recipientInfos, int transportType) {
      ASN1OutputStream keyTransRecipientInfo = (ASN1OutputStream)(new Object());
      keyTransRecipientInfo.writeInteger(0);
      this.writeIssuerAndSerialNumber(keyTransRecipientInfo, recipientCertificate, false);
      PublicKey certKey = null;

      try {
         certKey = recipientCertificate.getPublicKey();
      } catch (Throwable var11) {
         throw new Object(e);
      }

      if (transportType == 0) {
         ASN1OutputStream algorithmIdentifier = (ASN1OutputStream)(new Object());
         algorithmIdentifier.writeOID(OIDs.getOID(541853244));
         algorithmIdentifier.writeNull();
         keyTransRecipientInfo.writeSequence(algorithmIdentifier);
         NoCopyByteArrayOutputStream out = (NoCopyByteArrayOutputStream)(new Object());
         RSAPublicKey key = (RSAPublicKey)certKey;
         BlockEncryptor cryptoStream = (BlockEncryptor)(new Object((BlockFormatterEngine)(new Object((PublicKeyEncryptorEngine)(new Object(key)))), out));
         cryptoStream.write(this._sessionKey.getData(), 0, this._sessionKey.getLength());
         cryptoStream.close();
         keyTransRecipientInfo.writeOctetString(out.getByteArray());
      } else {
         ASN1OutputStream algorithmIdentifier = (ASN1OutputStream)(new Object());
         algorithmIdentifier.writeOID(OIDs.getOID(543426108));
         algorithmIdentifier.writeSequence((ASN1OutputStream)(new Object()));
         keyTransRecipientInfo.writeSequence(algorithmIdentifier);
         NoCopyByteArrayOutputStream out = (NoCopyByteArrayOutputStream)(new Object());
         RSAPublicKey key = (RSAPublicKey)certKey;
         BlockEncryptor cryptoStream = (BlockEncryptor)(new Object((BlockFormatterEngine)(new Object((PublicKeyEncryptorEngine)(new Object(key)))), out));
         cryptoStream.write(this._sessionKey.getData(), 0, this._sessionKey.getLength());
         cryptoStream.close();
         keyTransRecipientInfo.writeOctetString(out.getByteArray());
      }

      recipientInfos.writeSequence(keyTransRecipientInfo);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void writeKeyAgreementDH(Certificate recipientCertificate, ASN1OutputStream keyAgreeRecipientInfo) {
      PublicKey certStaticKey = null;

      try {
         certStaticKey = recipientCertificate.getPublicKey();
      } catch (Throwable var13) {
         throw new Object(e);
      }

      DHPublicKey staticPublicKey = (DHPublicKey)certStaticKey;
      DHKeyPair ephemeral = (DHKeyPair)(new Object(staticPublicKey.getDHCryptoSystem()));
      byte[] sharedSecret = DHKeyAgreement.generateSharedSecret(ephemeral.getDHPrivateKey(), staticPublicKey, false);
      byte[] keyData = null;
      if (this._encryptionScheme == 100) {
         RFC2631KDFPseudoRandomSource source = (RFC2631KDFPseudoRandomSource)(new Object(sharedSecret, OIDs.getOID(-1721352904), null, 192));
         keyData = source.getBytes(24);
      } else if (this._encryptionScheme == 101 || this._encryptionScheme == 102 || this._encryptionScheme == 103) {
         RFC2631KDFPseudoRandomSource source = (RFC2631KDFPseudoRandomSource)(new Object(sharedSecret, OIDs.getOID(-1721350856), null, 128));
         keyData = source.getBytes(16);
      } else if (this._encryptionScheme == 108) {
         RFC2631KDFPseudoRandomSource source = (RFC2631KDFPseudoRandomSource)(new Object(sharedSecret, OIDs.getOID(552133494), null, 128));
         keyData = source.getBytes(16);
      } else if (this._encryptionScheme == 104) {
         RFC2631KDFPseudoRandomSource source = (RFC2631KDFPseudoRandomSource)(new Object(sharedSecret, OIDs.getOID(541647732), null, 128));
         keyData = source.getBytes(16);
      } else if (this._encryptionScheme == 105) {
         RFC2631KDFPseudoRandomSource source = (RFC2631KDFPseudoRandomSource)(new Object(sharedSecret, OIDs.getOID(546890612), null, 192));
         keyData = source.getBytes(24);
      } else if (this._encryptionScheme == 106) {
         RFC2631KDFPseudoRandomSource source = (RFC2631KDFPseudoRandomSource)(new Object(sharedSecret, OIDs.getOID(552133492), null, 256));
         keyData = source.getBytes(32);
      }

      ASN1OutputStream originatorKey = (ASN1OutputStream)(new Object());
      ASN1OutputStream originatorAlgorithmIdentifier = (ASN1OutputStream)(new Object());
      originatorAlgorithmIdentifier.writeOID(OIDs.getOID(-1487623704));
      originatorKey.writeSequence(originatorAlgorithmIdentifier);
      ASN1OutputStream keyInteger = (ASN1OutputStream)(new Object());
      keyInteger.writeInteger(ephemeral.getDHPublicKey().getPublicKeyData());
      originatorKey.writeBitString(keyInteger.toByteArray());
      ASN1OutputStream midWay = (ASN1OutputStream)(new Object());
      midWay.writeSequence(originatorKey, 2, 1);
      keyAgreeRecipientInfo.writeStreamWithTag(midWay, 1, 0);
      this.writeEncryptedKeyAgreementKey(keyAgreeRecipientInfo, recipientCertificate, keyData, OIDs.getOID(-1721354952));
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void writeKeyAgreementECDH(Certificate recipientCertificate, ASN1OutputStream keyAgreeRecipientInfo, boolean useCofactor) {
      PublicKey certStaticKey = null;

      try {
         certStaticKey = recipientCertificate.getPublicKey();
      } catch (Throwable var12) {
         throw new Object(e);
      }

      ECPublicKey staticPublicKey = (ECPublicKey)certStaticKey;
      ECKeyPair ephemeral = (ECKeyPair)(new Object(staticPublicKey.getECCryptoSystem()));
      byte[] sharedSecret = ECDHKeyAgreement.generateSharedSecret(ephemeral.getECPrivateKey(), staticPublicKey, useCofactor);
      byte[] keyData = new byte[0];
      ASN1OutputStream originatorKey = this.writeECCKeyAgreement(sharedSecret, ephemeral, keyData, null, null, false);
      ASN1OutputStream midWay = (ASN1OutputStream)(new Object());
      midWay.writeSequence(originatorKey, 2, 1);
      keyAgreeRecipientInfo.writeStreamWithTag(midWay, 1, 0);
      if (useCofactor) {
         this.writeEncryptedKeyAgreementKey(keyAgreeRecipientInfo, recipientCertificate, keyData, OIDs.getOID(545660233));
      } else {
         this.writeEncryptedKeyAgreementKey(keyAgreeRecipientInfo, recipientCertificate, keyData, OIDs.getOID(545398089));
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void writeKeyAgreementECMQV(
      Certificate recipientCertificate, ASN1OutputStream keyAgreeRecipientInfo, PrivateKey privateKey, Certificate originator
   ) {
      if (privateKey instanceof Object) {
         ECPrivateKey localPrivateKey = (ECPrivateKey)privateKey;
         PublicKey certStaticKey = null;

         try {
            certStaticKey = recipientCertificate.getPublicKey();
         } catch (Throwable var15) {
            throw new Object(e);
         }

         ECPublicKey staticPublicKey = (ECPublicKey)certStaticKey;
         ECKeyPair ephemeral = (ECKeyPair)(new Object(localPrivateKey.getECCryptoSystem()));
         byte[] sharedSecret = ECMQVKeyAgreement.generateSharedSecret(localPrivateKey, ephemeral, staticPublicKey, staticPublicKey, true);
         ASN1OutputStream mqvUserKeyingMaterial = (ASN1OutputStream)(new Object());
         byte[] keyData = new byte[0];
         ASN1OutputStream ephemeralKey = this.writeECCKeyAgreement(sharedSecret, ephemeral, keyData, keyAgreeRecipientInfo, originator, true);
         mqvUserKeyingMaterial.writeSequence(ephemeralKey);
         ASN1OutputStream midWay = (ASN1OutputStream)(new Object());
         midWay.writeSequence(mqvUserKeyingMaterial);
         keyAgreeRecipientInfo.writeOctetString(midWay.toByteArray(), 1, 1);
         this.writeEncryptedKeyAgreementKey(keyAgreeRecipientInfo, recipientCertificate, keyData, OIDs.getOID(549068105));
      }
   }

   private final void writePasswordBasedEncryption(ASN1OutputStream passwordRecipientInfos, byte[] password) {
      int iterationCount = 50;
      passwordRecipientInfos.writeInteger(0);
      InitializationVector iv = (InitializationVector)(new Object(8));
      byte[] salt = new byte[20];
      RandomSource.getBytes(salt);
      SymmetricKey kekKey = null;
      if (this._sessionKey instanceof Object) {
         PKCS5KDF2PseudoRandomSource source = (PKCS5KDF2PseudoRandomSource)(new Object(password, salt, iterationCount));
         kekKey = (SymmetricKey)(new Object(source.getBytes(((AESKey)this._sessionKey).getLength())));
      } else {
         PKCS5KDF2PseudoRandomSource source = (PKCS5KDF2PseudoRandomSource)(new Object(password, salt, iterationCount));
         kekKey = (SymmetricKey)(new Object(source.getBytes(24)));
      }

      ASN1OutputStream keyDerivationAlgorithm = (ASN1OutputStream)(new Object());
      keyDerivationAlgorithm.writeOID(OIDs.getOID(273417788));
      ASN1OutputStream derivationParameters = (ASN1OutputStream)(new Object());
      derivationParameters.writeOctetString(salt);
      derivationParameters.writeInteger(iterationCount);
      keyDerivationAlgorithm.writeSequence(derivationParameters);
      passwordRecipientInfos.writeSequence(keyDerivationAlgorithm, 2, 0);
      if (!(this._sessionKey instanceof Object)) {
         ASN1OutputStream keyEncryptionAlgorithm = (ASN1OutputStream)(new Object());
         keyEncryptionAlgorithm.writeOID(OIDs.getOID(-1721346760));
         ASN1OutputStream keyAlgorithm = (ASN1OutputStream)(new Object());
         keyAlgorithm.writeOID(OIDs.getOID(-472306990));
         keyAlgorithm.writeOctetString(iv.getData());
         keyEncryptionAlgorithm.writeSequence(keyAlgorithm);
         passwordRecipientInfos.writeSequence(keyEncryptionAlgorithm);
         passwordRecipientInfos.writeOctetString(CMSKeyWrap.passwordBasedKeyWrap(kekKey, this._sessionKey, iv));
      } else {
         AESKey sessionKey = (AESKey)this._sessionKey;
         ASN1OutputStream keyEncryptionAlgorithm = (ASN1OutputStream)(new Object());
         int keyLength = sessionKey.getLength() << 3;
         if (keyLength == 128) {
            keyEncryptionAlgorithm.writeOID(OIDs.getOID(541647732));
            ASN1OutputStream keyAlgorithm = (ASN1OutputStream)(new Object());
            keyAlgorithm.writeOID(OIDs.getOID(540861300));
            keyEncryptionAlgorithm.writeSequence(keyAlgorithm);
         } else if (keyLength == 192) {
            keyEncryptionAlgorithm.writeOID(OIDs.getOID(546890612));
            ASN1OutputStream keyAlgorithm = (ASN1OutputStream)(new Object());
            keyAlgorithm.writeOID(OIDs.getOID(546104180));
            keyEncryptionAlgorithm.writeSequence(keyAlgorithm);
         } else {
            keyEncryptionAlgorithm.writeOID(OIDs.getOID(552133492));
            ASN1OutputStream keyAlgorithm = (ASN1OutputStream)(new Object());
            keyAlgorithm.writeOID(OIDs.getOID(551347060));
            keyEncryptionAlgorithm.writeSequence(keyAlgorithm);
         }

         passwordRecipientInfos.writeSequence(keyEncryptionAlgorithm);
         passwordRecipientInfos.writeOctetString(CMSKeyWrap.AESKeyWrap((AESKey)kekKey, (AESKey)this._sessionKey));
      }
   }

   private final void writeEncryptedKeyAgreementKey(ASN1OutputStream asn1Stream, Certificate recipientCertificate, byte[] keyData, OID oid) {
      ASN1OutputStream keyEncryptionAlgorithm = (ASN1OutputStream)(new Object());
      keyEncryptionAlgorithm.writeOID(oid);
      ASN1OutputStream keyWrapAlgorithm = (ASN1OutputStream)(new Object());
      byte[] wrappedKey = null;
      if (this._encryptionScheme == 100) {
         keyWrapAlgorithm.writeOID(OIDs.getOID(-1721352904));
         keyWrapAlgorithm.writeNull();
         wrappedKey = CMSKeyWrap.TripleDESKeyWrap((TripleDESKey)(new Object(keyData)), (TripleDESKey)this._sessionKey);
      } else if (this._encryptionScheme == 101 || this._encryptionScheme == 102 || this._encryptionScheme == 103) {
         keyWrapAlgorithm.writeOID(OIDs.getOID(-1721350856));
         keyWrapAlgorithm.writeInteger(58);
         wrappedKey = CMSKeyWrap.RC2KeyWrap((RC2Key)(new Object(keyData)), (RC2Key)this._sessionKey);
      } else if (this._encryptionScheme == 108) {
         keyWrapAlgorithm.writeOID(OIDs.getOID(552133494));
         keyWrapAlgorithm.writeInteger(128);
         wrappedKey = CMSKeyWrap.CASTKeyWrap((CAST128Key)(new Object(keyData)), (CAST128Key)this._sessionKey);
      } else if (this._encryptionScheme == 104) {
         keyWrapAlgorithm.writeOID(OIDs.getOID(541647732));
         wrappedKey = CMSKeyWrap.AESKeyWrap((AESKey)(new Object(keyData)), (AESKey)this._sessionKey);
      } else if (this._encryptionScheme == 105) {
         keyWrapAlgorithm.writeOID(OIDs.getOID(546890612));
         wrappedKey = CMSKeyWrap.AESKeyWrap((AESKey)(new Object(keyData)), (AESKey)this._sessionKey);
      } else if (this._encryptionScheme == 106) {
         keyWrapAlgorithm.writeOID(OIDs.getOID(552133492));
         wrappedKey = CMSKeyWrap.AESKeyWrap((AESKey)(new Object(keyData)), (AESKey)this._sessionKey);
      }

      keyEncryptionAlgorithm.writeSequence(keyWrapAlgorithm);
      asn1Stream.writeSequence(keyEncryptionAlgorithm);
      ASN1OutputStream recipientEncryptedKeys = (ASN1OutputStream)(new Object());
      ASN1OutputStream recipientEncryptedKey = (ASN1OutputStream)(new Object());
      this.writeIssuerAndSerialNumber(recipientEncryptedKey, recipientCertificate, false);
      recipientEncryptedKey.writeOctetString(wrappedKey);
      recipientEncryptedKeys.writeSequence(recipientEncryptedKey);
      asn1Stream.writeSequence(recipientEncryptedKeys);
   }

   private final void writeIssuerAndSerialNumber(ASN1OutputStream asn1Stream, Certificate certificate, boolean explicit) {
      ASN1OutputStream issuerAndSerialNumber = (ASN1OutputStream)(new Object());
      if (!(certificate instanceof Object)) {
         DistinguishedName issuerDN = certificate.getIssuer();
         Enumeration oids = issuerDN.getOIDs();
         Object issuer = new Object();

         while (oids.hasMoreElements()) {
            ASN1OutputStream relativeDistinguishedName = (ASN1OutputStream)(new Object());
            ASN1OutputStream attribute = (ASN1OutputStream)(new Object());
            OID oid = (OID)oids.nextElement();
            attribute.writeOID(oid);
            attribute.writeUTF8String(issuerDN.getString(oid));
            relativeDistinguishedName.writeSequence(attribute);
            ((ASN1OutputStream)issuer).writeSet(relativeDistinguishedName);
         }

         issuerAndSerialNumber.writeSequence((ASN1OutputStream)issuer);
         issuerAndSerialNumber.writeInteger(certificate.getSerialNumber());
      } else {
         X509Certificate x509Certificate = (X509Certificate)certificate;
         issuerAndSerialNumber.writeRawByteArray(x509Certificate.getIssuer().getEncoding());
         issuerAndSerialNumber.writeInteger(x509Certificate.getSignedSerialNumber());
      }

      if (explicit) {
         asn1Stream.writeSequence(issuerAndSerialNumber, 1, 0);
      } else {
         asn1Stream.writeSequence(issuerAndSerialNumber);
      }
   }

   private final ASN1OutputStream writeECCKeyAgreement(
      byte[] sharedSecret, ECKeyPair ephemeral, byte[] keyData, ASN1OutputStream keyAgreeRecipientInfo, Certificate originator, boolean ECMQV
   ) {
      ASN1OutputStream outputSequence = (ASN1OutputStream)(new Object());
      ASN1OutputStream tempAlgIdentifier = (ASN1OutputStream)(new Object());
      if (this._encryptionScheme == 100) {
         tempAlgIdentifier.writeOID(OIDs.getOID(-1721352904));
         tempAlgIdentifier.writeNull();
      } else if (this._encryptionScheme == 101 || this._encryptionScheme == 102 || this._encryptionScheme == 103) {
         tempAlgIdentifier.writeOID(OIDs.getOID(-1721350856));
         tempAlgIdentifier.writeInteger(58);
      } else if (this._encryptionScheme == 108) {
         tempAlgIdentifier.writeOID(OIDs.getOID(552133494));
         tempAlgIdentifier.writeInteger(128);
      } else if (this._encryptionScheme == 104) {
         tempAlgIdentifier.writeOID(OIDs.getOID(541647732));
      } else if (this._encryptionScheme == 105) {
         tempAlgIdentifier.writeOID(OIDs.getOID(546890612));
      } else if (this._encryptionScheme == 106) {
         tempAlgIdentifier.writeOID(OIDs.getOID(552133492));
      }

      outputSequence.writeSequence(tempAlgIdentifier);
      if (this._encryptionScheme == 100 || this._encryptionScheme == 105) {
         outputSequence.writeOctetString(new byte[]{0, 0, 0, -64}, 1, 2);
      } else if (this._encryptionScheme == 101
         || this._encryptionScheme == 102
         || this._encryptionScheme == 103
         || this._encryptionScheme == 108
         || this._encryptionScheme == 104) {
         outputSequence.writeOctetString(new byte[]{0, 0, 0, -128}, 1, 2);
      } else if (this._encryptionScheme == 106) {
         outputSequence.writeOctetString(new byte[]{0, 0, 1, 0}, 1, 2);
      }

      ASN1OutputStream sharedInfoOut = (ASN1OutputStream)(new Object());
      sharedInfoOut.writeSequence(outputSequence);
      X963KDFPseudoRandomSource source = (X963KDFPseudoRandomSource)(new Object(sharedSecret, sharedInfoOut.toByteArray()));
      int keyDataLength;
      if (this._encryptionScheme == 100 || this._encryptionScheme == 105) {
         keyDataLength = 24;
      } else if (this._encryptionScheme != 101
         && this._encryptionScheme != 102
         && this._encryptionScheme != 103
         && this._encryptionScheme != 108
         && this._encryptionScheme != 104) {
         if (this._encryptionScheme != 106) {
            throw new Object();
         }

         keyDataLength = 32;
      } else {
         keyDataLength = 16;
      }

      byte[] temp = source.getBytes(keyDataLength);
      Array.resize(keyData, keyDataLength);
      System.arraycopy(temp, 0, keyData, 0, keyDataLength);
      if (ECMQV) {
         this.writeIssuerAndSerialNumber(keyAgreeRecipientInfo, originator, true);
      }

      ASN1OutputStream originatorKey = (ASN1OutputStream)(new Object());
      ASN1OutputStream originatorAlgorithmIdentifier = (ASN1OutputStream)(new Object());
      originatorAlgorithmIdentifier.writeOID(OIDs.getOID(-1487624216));
      originatorAlgorithmIdentifier.writeNull();
      originatorKey.writeSequence(originatorAlgorithmIdentifier);
      originatorKey.writeBitString(ephemeral.getECPublicKey().getPublicKeyData());
      return originatorKey;
   }
}
