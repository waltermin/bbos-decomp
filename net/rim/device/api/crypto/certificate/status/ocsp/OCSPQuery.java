package net.rim.device.api.crypto.certificate.status.ocsp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.crypto.certificate.CertificateServers;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.certificate.status.ProviderRequestData;
import net.rim.device.api.crypto.certificate.status.ProviderResponseData;
import net.rim.device.api.crypto.certificate.status.ProviderUiContext;
import net.rim.device.api.crypto.certificate.status.StatusProviderException;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;
import net.rim.device.api.crypto.encoder.DecodedSignature;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MultiMap;
import net.rim.device.internal.i18n.CommonResource;

final class OCSPQuery {
   private Certificate[] _certChain;
   private boolean _checkEntireChain;
   private Hashtable _contextInfoList;
   private Vector _responderUrls;
   private ProviderUiContext _uiContext;
   private ProviderResponseData _response;
   private byte[] _nonce;
   private static MultiMap _responderCerts;
   private static final byte TAG_USER_RESPONDERS = 0;
   private static final byte TAG_RESPONDER_CERT_HASHES = 1;
   private static final byte TAG_NONCE_VALUE = 2;
   private static final byte TAG_CERT_ISSUER_NAME_HASH = 0;
   private static final byte TAG_CERT_ISSUER_KEY_HASH = 1;
   private static final byte TAG_CERT_SUBJECT_SN = 2;
   private static final byte TAG_CERT_ISSUER_NAME = 4;
   private static final byte TAG_CERT_CDP = 5;
   private static final byte TAG_CERT_RESPONSE_BYTES = 0;
   private static final byte TAG_CERT_SIG_ALGORITHM = 1;
   private static final byte TAG_CERT_SIGNATURE = 2;
   private static final byte TAG_RESPONDER_CERTS = 1;
   private static final int ASN1_TAG_ADDITIONAL_CERTS = 0;
   private static final int ASN1_TAG_URI = 6;
   private static final ResourceBundle _rbCrypto = ResourceBundle.getBundle(-7644390350925054654L, "net.rim.device.internal.resource.crypto.StatusProviders");
   private static final String[] POPUP_BUTTON_TEXT_1 = new Object[]{CommonResource.getString(10015), _rbCrypto.getString(34), CommonResource.getString(10005)};
   private static final String[] POPUP_BUTTON_TEXT_2 = new Object[]{CommonResource.getStringArray(10012)[1], CommonResource.getStringArray(10012)[0]};
   private static final String[] POPUP_BUTTON_TEXT_3 = new Object[]{
      CommonResource.getString(10015), CommonResource.getStringArray(10012)[1], CommonResource.getStringArray(10012)[0]
   };
   private static final int BUTTON_VIEW = 0;
   private static final int BUTTON_TRUST = 1;
   private static final int BUTTON_CANCEL = 2;
   private static final int BUTTON_NO = 3;
   private static final int BUTTON_YES = 4;
   private static final int BUTTON_OK = 5;
   private static final int[] POPUP_BUTTON_CODES_1 = new int[]{0, 1, 2, -804651005, 0, 3, 4, -804651006, 3, 4, -805044216, -33606652};
   private static final int[] POPUP_BUTTON_CODES_2 = new int[]{3, 4, -805044216, -33606652, 232019217, -805044216, -992149756, 1607022049};
   private static final int[] POPUP_BUTTON_CODES_3 = new int[]{0, 3, 4, -804651006, 3, 4, -805044216, -33606652, 232019217, -805044216, -992149756, 1607022049};
   private static final long RESPONDER_CERTS_GUID = -8840675705458263554L;

   public OCSPQuery(Certificate[] certChain, boolean checkEntireChain, KeyStore keyStore, ProviderUiContext uiContext) {
      this._uiContext = uiContext;
      this._certChain = this.getCompleteChain(certChain, checkEntireChain, keyStore);
      this._checkEntireChain = checkEntireChain;
      this._contextInfoList = (Hashtable)(new Object());
      this._responderUrls = (Vector)(new Object());
      this._response = null;
   }

   public static final boolean isChainX509(Certificate[] certChain, boolean checkEntireChain) {
      int length = checkEntireChain ? certChain.length : Math.min(certChain.length, 2);

      for (int i = 0; i < length; i++) {
         if (!(certChain[i] instanceof Object)) {
            return false;
         }
      }

      return true;
   }

   private final boolean isChainComplete(Certificate[] certChain, boolean checkEntireChain) {
      int length = certChain.length;
      if (length == 1) {
         if (!certChain[0].getIssuer().equals(certChain[0].getSubject())) {
            return false;
         }
      } else {
         int end = checkEntireChain ? length - 1 : 1;

         for (int i = 0; i < end; i++) {
            if (!certChain[i].getIssuer().equals(certChain[i + 1].getSubject())) {
               return false;
            }
         }
      }

      return true;
   }

   private final Certificate[] getCompleteChain(Certificate[] certChain, boolean checkEntireChain, KeyStore keyStore) {
      if (!this.isChainComplete(certChain, checkEntireChain)) {
         certChain = CertificateUtilities.buildCertificateChain(certChain[0], keyStore);
         if (!this.isChainComplete(certChain, checkEntireChain)) {
            this._uiContext.setErrorMessage(_rbCrypto.getString(37));
            throw new StatusProviderException();
         }
      }

      if (!isChainX509(certChain, checkEntireChain)) {
         this._uiContext.setErrorMessage(_rbCrypto.getString(39));
         throw new StatusProviderException();
      } else {
         long properties = CertificateChainProperties.getCertificateChainProperties(certChain, TrustedKeyStore.getInstance(), System.currentTimeMillis());
         if ((properties & 16) != 0 || (properties & 4) != 0 || (properties & 2) != 0) {
            this._uiContext.setErrorMessage(_rbCrypto.getString(47));
            throw new StatusProviderException();
         } else if ((properties & 256) != 0) {
            this._uiContext.setErrorMessage(_rbCrypto.getString(61));
            throw new StatusProviderException();
         } else if ((properties & 1024) != 0 && (properties & 4194304) == 0) {
            this._uiContext.setErrorMessage(_rbCrypto.getString(62));
            throw new StatusProviderException();
         } else {
            return certChain;
         }
      }
   }

   private final void encodeSingleRequest(X509Certificate param1, X509Certificate param2, ProviderRequestData param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: new java/lang/Object
      // 003: dup
      // 004: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 007: astore 4
      // 009: aload 4
      // 00b: aload 2
      // 00c: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getSubject ()Lnet/rim/device/api/crypto/certificate/DistinguishedName;
      // 00f: invokeinterface net/rim/device/api/crypto/certificate/DistinguishedName.getEncoding ()[B 1
      // 014: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 017: aload 4
      // 019: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ()[B
      // 01c: astore 5
      // 01e: aload 4
      // 020: aload 2
      // 021: bipush 1
      // 022: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getEncoding (I)[B
      // 025: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 028: aload 4
      // 02a: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ()[B
      // 02d: astore 6
      // 02f: aload 1
      // 030: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getSerialNumber ()[B
      // 033: astore 7
      // 035: aload 3
      // 036: aload 1
      // 037: bipush 0
      // 038: aload 5
      // 03a: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderRequestData.addCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I[B)V 4
      // 03f: aload 3
      // 040: aload 1
      // 041: bipush 1
      // 042: aload 6
      // 044: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderRequestData.addCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I[B)V 4
      // 049: aload 3
      // 04a: aload 1
      // 04b: bipush 2
      // 04d: aload 7
      // 04f: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderRequestData.addCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I[B)V 4
      // 054: aload 3
      // 055: aload 1
      // 056: bipush 4
      // 058: aload 1
      // 059: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getIssuer ()Lnet/rim/device/api/crypto/certificate/DistinguishedName;
      // 05c: invokeinterface net/rim/device/api/crypto/certificate/DistinguishedName.getEncoding ()[B 1
      // 061: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderRequestData.addCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I[B)V 4
      // 066: aload 1
      // 067: ldc_w -477712439
      // 06a: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 06d: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getExtension (Lnet/rim/device/api/crypto/oid/OID;)Lnet/rim/device/api/crypto/certificate/CertificateExtension;
      // 070: astore 8
      // 072: aload 8
      // 074: ifnonnull 07a
      // 077: goto 0f5
      // 07a: new java/lang/Object
      // 07d: dup
      // 07e: new java/lang/Object
      // 081: dup
      // 082: aload 8
      // 084: invokevirtual net/rim/device/api/crypto/certificate/CertificateExtension.getValue ()[B
      // 087: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 08a: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 08d: astore 9
      // 08f: aload 9
      // 091: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 094: astore 9
      // 096: ldc_w -477712251
      // 099: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 09c: astore 10
      // 09e: aload 9
      // 0a0: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.endOfStream ()Z
      // 0a3: ifne 0f5
      // 0a6: aload 9
      // 0a8: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 0ab: astore 11
      // 0ad: aload 11
      // 0af: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 0b2: aload 10
      // 0b4: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 0b7: ifeq 09e
      // 0ba: aload 11
      // 0bc: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readFieldAsByteArray ()[B
      // 0bf: astore 12
      // 0c1: aload 3
      // 0c2: aload 1
      // 0c3: bipush 5
      // 0c5: aload 12
      // 0c7: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderRequestData.addCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I[B)V 4
      // 0cc: new java/lang/Object
      // 0cf: dup
      // 0d0: aload 12
      // 0d2: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> ([B)V
      // 0d5: astore 13
      // 0d7: aload 13
      // 0d9: bipush 2
      // 0db: bipush 6
      // 0dd: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readIA5String (II)Ljava/lang/String;
      // 0e0: astore 14
      // 0e2: aload 0
      // 0e3: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._responderUrls Ljava/util/Vector;
      // 0e6: aload 14
      // 0e8: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 0eb: goto 0f5
      // 0ee: astore 9
      // 0f0: goto 0f5
      // 0f3: astore 9
      // 0f5: new net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery$OCSPContextInfo
      // 0f8: dup
      // 0f9: invokespecial net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery$OCSPContextInfo.<init> ()V
      // 0fc: astore 9
      // 0fe: aload 9
      // 100: aload 5
      // 102: putfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery$OCSPContextInfo.issuerNameHash [B
      // 105: aload 9
      // 107: aload 6
      // 109: putfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery$OCSPContextInfo.issuerKeyHash [B
      // 10c: aload 9
      // 10e: aload 7
      // 110: putfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery$OCSPContextInfo.serialNo [B
      // 113: aload 0
      // 114: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._contextInfoList Ljava/util/Hashtable;
      // 117: aload 1
      // 118: aload 9
      // 11a: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 11d: pop
      // 11e: return
      // try (53 -> 102): 102 null
      // try (53 -> 102): 104 null
   }

   private final void encodeUserOCSPResponders(ProviderRequestData request) {
      try {
         ByteArrayOutputStream responders = (ByteArrayOutputStream)(new Object());
         DataOutputStream out = (DataOutputStream)(new Object(responders));
         CertificateServers certOptions = CertificateServers.getInstance();
         int count = certOptions.getServerSize(2);
         out.writeInt(count);

         for (int i = 0; i < count; i++) {
            CertificateServerInfo server = certOptions.elementAt(2, i);
            String serverUrl = server.getServer();
            out.writeUTF(serverUrl);
            this._responderUrls.addElement(serverUrl);
         }

         request.addGlobalField(0, responders.toByteArray());
      } finally {
         return;
      }
   }

   private final void encodeCachedCertHashes(ProviderRequestData request) {
      try {
         ByteArrayOutputStream responderCertHashes = (ByteArrayOutputStream)(new Object());
         int length = this._responderUrls.size();

         for (int i = 0; i < length; i++) {
            String url = (String)this._responderUrls.elementAt(i);
            Enumeration responderEnum = this.getStoredResponderCerts(url);

            while (responderEnum.hasMoreElements()) {
               OCSPQuery$ResponderCertContainer container = (OCSPQuery$ResponderCertContainer)responderEnum.nextElement();
               responderCertHashes.write(container.getHash());
            }
         }

         Enumeration responderEnum = this.getStoredResponderCerts("");

         while (responderEnum.hasMoreElements()) {
            OCSPQuery$ResponderCertContainer container = (OCSPQuery$ResponderCertContainer)responderEnum.nextElement();
            responderCertHashes.write(container.getHash());
         }

         request.addGlobalField(1, responderCertHashes.toByteArray());
      } finally {
         return;
      }
   }

   private final void addNonceValue(ProviderRequestData request) {
      int nonceValue = RandomSource.getInt();
      this._nonce = new byte[4];
      this._nonce[0] = (byte)(nonceValue >> 24);
      this._nonce[1] = (byte)(nonceValue >> 16);
      this._nonce[2] = (byte)(nonceValue >> 8);
      this._nonce[3] = (byte)nonceValue;
      request.addGlobalField(2, this._nonce);
   }

   public final void encodeRequest(ProviderRequestData request) {
      int end = this._checkEntireChain ? this._certChain.length - 1 : Math.min(this._certChain.length - 1, 1);

      int i;
      for (i = 0; i < end; i++) {
         this.encodeSingleRequest((X509Certificate)this._certChain[i], (X509Certificate)this._certChain[i + 1], request);
      }

      if ((i == 0 || this._checkEntireChain) && this._certChain[i].getIssuer().equals(this._certChain[i].getSubject())) {
         this.encodeSingleRequest((X509Certificate)this._certChain[i], (X509Certificate)this._certChain[i], request);
      }

      this.encodeUserOCSPResponders(request);
      this.encodeCachedCertHashes(request);
      this.addNonceValue(request);
   }

   private final boolean verifySignature(DecodedSignature param1, byte[] param2, X509Certificate param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: aload 3
      // 02: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 05: invokevirtual net/rim/device/api/crypto/encoder/DecodedSignature.getVerifier (Lnet/rim/device/api/crypto/PublicKey;)Lnet/rim/device/api/crypto/SignatureVerifier;
      // 08: astore 4
      // 0a: aload 4
      // 0c: aload 2
      // 0d: invokeinterface net/rim/device/api/crypto/SignatureVerifier.update ([B)V 2
      // 12: aload 4
      // 14: invokeinterface net/rim/device/api/crypto/SignatureVerifier.verify ()Z 1
      // 19: ireturn
      // 1a: astore 4
      // 1c: bipush 0
      // 1d: ireturn
      // 1e: astore 4
      // 20: bipush 0
      // 21: ireturn
      // 22: astore 4
      // 24: bipush 0
      // 25: ireturn
      // 26: astore 4
      // 28: bipush 0
      // 29: ireturn
      // try (0 -> 10): 11 null
      // try (0 -> 10): 14 null
      // try (0 -> 10): 17 null
      // try (0 -> 10): 20 null
   }

   private final X509Certificate getResponderCertificate(OCSPResponse response, DecodedSignature signature, X509Certificate issuer) {
      int responderIdType = response.getResponderIDType();
      byte[] responderId = response.getResponderID();
      byte[] responseBytes = response.getResponseBytes();
      KeyStore keyStore = DeviceKeyStore.getInstance();

      label67:
      try {
         X509DistinguishedName responderSubjectDN = null;
         Enumeration enumeration;
         if (responderIdType == 1) {
            responderSubjectDN = (X509DistinguishedName)(new Object(responderId));
            keyStore.addIndex((KeyStoreIndex)(new Object()));
            enumeration = keyStore.elements(-1581141357654337861L, responderSubjectDN);
         } else {
            keyStore.addIndex((KeyStoreIndex)(new Object((Digest)(new Object()))));
            enumeration = keyStore.elements(751537200683994917L, responderId);
         }

         while (enumeration.hasMoreElements()) {
            KeyStoreData data = (KeyStoreData)enumeration.nextElement();
            if (data.getCertificate() instanceof Object) {
               X509Certificate cert = (X509Certificate)data.getCertificate();
               if (this.verifySignature(signature, responseBytes, cert)) {
                  return cert;
               }
            }
         }

         if (responderIdType == 1) {
            enumeration = this.getStoredResponderCerts(responderSubjectDN);
         } else {
            enumeration = this.getStoredResponderCerts(responderId);
         }

         while (enumeration.hasMoreElements()) {
            X509Certificate cert = (X509Certificate)enumeration.nextElement();
            if (this.verifySignature(signature, responseBytes, cert)) {
               return cert;
            }
         }
      } finally {
         break label67;
      }

      this._uiContext.setErrorMessage(_rbCrypto.getString(36));
      return null;
   }

   private final boolean trustResponderCertificate(X509Certificate param1, X509Certificate param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: invokestatic net/rim/device/api/crypto/keystore/DeviceKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 003: astore 3
      // 004: invokestatic net/rim/device/api/crypto/keystore/TrustedKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 007: astore 4
      // 009: aload 1
      // 00a: bipush 1
      // 00b: anewarray 1461
      // 00e: dup
      // 00f: bipush 0
      // 010: aload 2
      // 011: aastore
      // 012: aload 3
      // 013: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.buildCertificateChains (Lnet/rim/device/api/crypto/certificate/Certificate;[Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;)[[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 016: astore 5
      // 018: aload 5
      // 01a: aload 4
      // 01c: invokestatic java/lang/System.currentTimeMillis ()J
      // 01f: invokestatic net/rim/device/api/crypto/certificate/CertificateChainProperties.getCertificateChainProperties ([[Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;J)[J
      // 022: astore 6
      // 024: aload 6
      // 026: invokestatic net/rim/device/api/crypto/certificate/CertificateChainProperties.selectBestCertificateChainProperties ([J)J
      // 029: lstore 7
      // 02b: lload 7
      // 02d: bipush 16
      // 02f: i2l
      // 030: land
      // 031: bipush 0
      // 032: i2l
      // 033: lcmp
      // 034: ifne 04f
      // 037: lload 7
      // 039: bipush 4
      // 03b: i2l
      // 03c: land
      // 03d: bipush 0
      // 03e: i2l
      // 03f: lcmp
      // 040: ifne 04f
      // 043: lload 7
      // 045: bipush 2
      // 047: i2l
      // 048: land
      // 049: bipush 0
      // 04a: i2l
      // 04b: lcmp
      // 04c: ifeq 062
      // 04f: aload 0
      // 050: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._uiContext Lnet/rim/device/api/crypto/certificate/status/ProviderUiContext;
      // 053: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 056: bipush 46
      // 058: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 05b: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderUiContext.setErrorMessage (Ljava/lang/String;)V 2
      // 060: bipush 0
      // 061: ireturn
      // 062: lload 7
      // 064: sipush 256
      // 067: i2l
      // 068: land
      // 069: bipush 0
      // 06a: i2l
      // 06b: lcmp
      // 06c: ifeq 082
      // 06f: aload 0
      // 070: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._uiContext Lnet/rim/device/api/crypto/certificate/status/ProviderUiContext;
      // 073: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 076: bipush 63
      // 078: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 07b: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderUiContext.setErrorMessage (Ljava/lang/String;)V 2
      // 080: bipush 0
      // 081: ireturn
      // 082: lload 7
      // 084: sipush 1024
      // 087: i2l
      // 088: land
      // 089: bipush 0
      // 08a: i2l
      // 08b: lcmp
      // 08c: ifeq 0a2
      // 08f: aload 0
      // 090: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._uiContext Lnet/rim/device/api/crypto/certificate/status/ProviderUiContext;
      // 093: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 096: bipush 64
      // 098: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 09b: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderUiContext.setErrorMessage (Ljava/lang/String;)V 2
      // 0a0: bipush 0
      // 0a1: ireturn
      // 0a2: lload 7
      // 0a4: bipush 32
      // 0a6: i2l
      // 0a7: land
      // 0a8: bipush 0
      // 0a9: i2l
      // 0aa: lcmp
      // 0ab: ifeq 0d7
      // 0ae: aload 0
      // 0af: aload 1
      // 0b0: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 0b3: bipush 45
      // 0b5: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0b8: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.POPUP_BUTTON_TEXT_3 [Ljava/lang/String;
      // 0bb: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.POPUP_BUTTON_CODES_3 [I
      // 0be: invokespecial net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.promptUserWithCertificate (Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;Ljava/lang/String;[Ljava/lang/String;[I)Z
      // 0c1: ifne 0d7
      // 0c4: aload 0
      // 0c5: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._uiContext Lnet/rim/device/api/crypto/certificate/status/ProviderUiContext;
      // 0c8: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 0cb: bipush 53
      // 0cd: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0d0: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderUiContext.setErrorMessage (Ljava/lang/String;)V 2
      // 0d5: bipush 0
      // 0d6: ireturn
      // 0d7: aload 3
      // 0d8: aload 1
      // 0d9: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.isMember (Lnet/rim/device/api/crypto/certificate/Certificate;)Z 2
      // 0de: ifne 11a
      // 0e1: invokestatic java/lang/System.currentTimeMillis ()J
      // 0e4: lstore 9
      // 0e6: new java/lang/Object
      // 0e9: dup
      // 0ea: bipush 0
      // 0eb: lload 9
      // 0ed: lload 9
      // 0ef: lload 9
      // 0f1: bipush -1
      // 0f3: i2l
      // 0f4: invokespecial net/rim/device/api/crypto/certificate/CertificateStatus.<init> (IJJJJ)V
      // 0f7: astore 11
      // 0f9: aload 3
      // 0fa: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 0fd: bipush 40
      // 0ff: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 102: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.getTicket (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreTicket; 2
      // 107: astore 12
      // 109: aload 3
      // 10a: aconst_null
      // 10b: aload 1
      // 10c: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getSubjectFriendlyName ()Ljava/lang/String;
      // 10f: aload 1
      // 110: aload 11
      // 112: aload 12
      // 114: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.set ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData; 6
      // 119: pop
      // 11a: lload 7
      // 11c: bipush 8
      // 11e: i2l
      // 11f: land
      // 120: bipush 0
      // 121: i2l
      // 122: lcmp
      // 123: ifne 129
      // 126: goto 1c8
      // 129: bipush 31
      // 12b: istore 9
      // 12d: aload 5
      // 12f: arraylength
      // 130: istore 10
      // 132: bipush 0
      // 133: istore 11
      // 135: iload 11
      // 137: iload 10
      // 139: if_icmpge 155
      // 13c: aload 5
      // 13e: iload 11
      // 140: aaload
      // 141: aload 2
      // 142: invokestatic net/rim/device/api/util/Arrays.contains ([Ljava/lang/Object;Ljava/lang/Object;)Z
      // 145: ifeq 14f
      // 148: bipush 44
      // 14a: istore 9
      // 14c: goto 155
      // 14f: iinc 11 1
      // 152: goto 135
      // 155: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 158: iload 9
      // 15a: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 15d: astore 11
      // 15f: aload 0
      // 160: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._uiContext Lnet/rim/device/api/crypto/certificate/status/ProviderUiContext;
      // 163: aload 11
      // 165: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderUiContext.setErrorMessage (Ljava/lang/String;)V 2
      // 16a: aload 0
      // 16b: aload 1
      // 16c: aload 11
      // 16e: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.POPUP_BUTTON_TEXT_1 [Ljava/lang/String;
      // 171: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.POPUP_BUTTON_CODES_1 [I
      // 174: invokespecial net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.promptUserWithCertificate (Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;Ljava/lang/String;[Ljava/lang/String;[I)Z
      // 177: ifne 18d
      // 17a: aload 0
      // 17b: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._uiContext Lnet/rim/device/api/crypto/certificate/status/ProviderUiContext;
      // 17e: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 181: bipush 53
      // 183: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 186: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderUiContext.setErrorMessage (Ljava/lang/String;)V 2
      // 18b: bipush 0
      // 18c: ireturn
      // 18d: invokestatic java/lang/System.currentTimeMillis ()J
      // 190: lstore 12
      // 192: new java/lang/Object
      // 195: dup
      // 196: bipush 0
      // 197: lload 12
      // 199: lload 12
      // 19b: lload 12
      // 19d: bipush -1
      // 19f: i2l
      // 1a0: invokespecial net/rim/device/api/crypto/certificate/CertificateStatus.<init> (IJJJJ)V
      // 1a3: astore 14
      // 1a5: aload 4
      // 1a7: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 1aa: bipush 40
      // 1ac: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 1af: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.getTicket (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreTicket; 2
      // 1b4: astore 15
      // 1b6: aload 4
      // 1b8: aconst_null
      // 1b9: aload 1
      // 1ba: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getSubjectFriendlyName ()Ljava/lang/String;
      // 1bd: aload 1
      // 1be: aload 14
      // 1c0: aload 15
      // 1c2: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.set ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData; 6
      // 1c7: pop
      // 1c8: bipush 1
      // 1c9: ireturn
      // 1ca: astore 3
      // 1cb: bipush 0
      // 1cc: ireturn
      // 1cd: astore 3
      // 1ce: bipush 0
      // 1cf: ireturn
      // 1d0: astore 3
      // 1d1: bipush 0
      // 1d2: ireturn
      // 1d3: astore 3
      // 1d4: bipush 0
      // 1d5: ireturn
      // 1d6: astore 3
      // 1d7: bipush 0
      // 1d8: ireturn
      // 1d9: astore 3
      // 1da: bipush 0
      // 1db: ireturn
      // try (0 -> 53): 224 null
      // try (54 -> 69): 224 null
      // try (70 -> 85): 224 null
      // try (86 -> 110): 224 null
      // try (111 -> 194): 224 null
      // try (195 -> 223): 224 null
      // try (0 -> 53): 227 null
      // try (54 -> 69): 227 null
      // try (70 -> 85): 227 null
      // try (86 -> 110): 227 null
      // try (111 -> 194): 227 null
      // try (195 -> 223): 227 null
      // try (0 -> 53): 230 null
      // try (54 -> 69): 230 null
      // try (70 -> 85): 230 null
      // try (86 -> 110): 230 null
      // try (111 -> 194): 230 null
      // try (195 -> 223): 230 null
      // try (0 -> 53): 233 null
      // try (54 -> 69): 233 null
      // try (70 -> 85): 233 null
      // try (86 -> 110): 233 null
      // try (111 -> 194): 233 null
      // try (195 -> 223): 233 null
      // try (0 -> 53): 236 null
      // try (54 -> 69): 236 null
      // try (70 -> 85): 236 null
      // try (86 -> 110): 236 null
      // try (111 -> 194): 236 null
      // try (195 -> 223): 236 null
      // try (0 -> 53): 239 null
      // try (54 -> 69): 239 null
      // try (70 -> 85): 239 null
      // try (86 -> 110): 239 null
      // try (111 -> 194): 239 null
      // try (195 -> 223): 239 null
   }

   private final boolean checkOCSPExtension(X509Certificate param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ldc_w -1250822229
      // 04: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 07: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getExtension (Lnet/rim/device/api/crypto/oid/OID;)Lnet/rim/device/api/crypto/certificate/CertificateExtension;
      // 0a: astore 2
      // 0b: aload 2
      // 0c: ifnull 43
      // 0f: ldc_w 1688405111
      // 12: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 15: astore 3
      // 16: new java/lang/Object
      // 19: dup
      // 1a: new java/lang/Object
      // 1d: dup
      // 1e: aload 2
      // 1f: invokevirtual net/rim/device/api/crypto/certificate/CertificateExtension.getValue ()[B
      // 22: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 25: invokespecial net/rim/device/api/crypto/asn1/ASN1InputStream.<init> (Ljava/io/InputStream;)V
      // 28: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readSequence ()Lnet/rim/device/api/crypto/asn1/ASN1InputStream;
      // 2b: astore 4
      // 2d: aload 4
      // 2f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.endOfStream ()Z
      // 32: ifne 43
      // 35: aload 3
      // 36: aload 4
      // 38: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputStream.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 3b: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 3e: ifeq 2d
      // 41: bipush 1
      // 42: ireturn
      // 43: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 46: bipush 32
      // 48: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 4b: astore 3
      // 4c: aload 0
      // 4d: aload 1
      // 4e: aload 3
      // 4f: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.POPUP_BUTTON_TEXT_3 [Ljava/lang/String;
      // 52: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.POPUP_BUTTON_CODES_3 [I
      // 55: invokespecial net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.promptUserWithCertificate (Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;Ljava/lang/String;[Ljava/lang/String;[I)Z
      // 58: ifne 6e
      // 5b: aload 0
      // 5c: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._uiContext Lnet/rim/device/api/crypto/certificate/status/ProviderUiContext;
      // 5f: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 62: bipush 53
      // 64: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 67: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderUiContext.setErrorMessage (Ljava/lang/String;)V 2
      // 6c: bipush 0
      // 6d: ireturn
      // 6e: bipush 1
      // 6f: ireturn
      // 70: astore 2
      // 71: bipush 0
      // 72: ireturn
      // 73: astore 2
      // 74: bipush 0
      // 75: ireturn
      // try (0 -> 29): 51 null
      // try (30 -> 48): 51 null
      // try (49 -> 50): 51 null
      // try (0 -> 29): 54 null
      // try (30 -> 48): 54 null
      // try (49 -> 50): 54 null
   }

   private final boolean promptUserWithCertificate(X509Certificate certificate, String message, String[] buttonText, int[] buttonCodes) {
      while (true) {
         int selection = this._uiContext.promptUser(_rbCrypto.getString(30), message, buttonText, buttonCodes);
         switch (selection) {
            case -2:
               break;
            case -1:
            case 2:
            case 3:
               return false;
            case 0:
            default:
               CertificateUtilities.displayCertificateDetails(certificate, null, false, null);
               break;
            case 1:
            case 4:
               return true;
         }
      }
   }

   private final void validateResponse(OCSPResponse param1, X509Certificate param2, X509Certificate param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokevirtual net/rim/device/api/crypto/certificate/status/ocsp/OCSPResponse.getNonceBytes ()[B
      // 004: astore 4
      // 006: aload 4
      // 008: ifnull 00e
      // 00b: goto 08d
      // 00e: new java/lang/Object
      // 011: dup
      // 012: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 015: astore 5
      // 017: aload 5
      // 019: aload 3
      // 01a: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getSubject ()Lnet/rim/device/api/crypto/certificate/DistinguishedName;
      // 01d: invokeinterface net/rim/device/api/crypto/certificate/DistinguishedName.getEncoding ()[B 1
      // 022: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 025: aload 5
      // 027: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ()[B
      // 02a: astore 6
      // 02c: aload 5
      // 02e: aload 3
      // 02f: bipush 1
      // 030: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getEncoding (I)[B
      // 033: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 036: aload 5
      // 038: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ()[B
      // 03b: astore 7
      // 03d: aload 2
      // 03e: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getSerialNumber ()[B
      // 041: astore 8
      // 043: aload 1
      // 044: aload 6
      // 046: aload 7
      // 048: aload 8
      // 04a: invokevirtual net/rim/device/api/crypto/certificate/status/ocsp/OCSPResponse.getCertificateStatus ([B[B[B)Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 04d: astore 9
      // 04f: aload 9
      // 051: ifnonnull 05c
      // 054: new net/rim/device/api/crypto/certificate/status/StatusProviderException
      // 057: dup
      // 058: invokespecial net/rim/device/api/crypto/certificate/status/StatusProviderException.<init> ()V
      // 05b: athrow
      // 05c: aload 9
      // 05e: invokevirtual net/rim/device/api/crypto/certificate/CertificateStatus.getNextUpdateTime ()J
      // 061: lstore 10
      // 063: lload 10
      // 065: bipush 0
      // 066: i2l
      // 067: lcmp
      // 068: ifle 074
      // 06b: lload 10
      // 06d: invokestatic java/lang/System.currentTimeMillis ()J
      // 070: lcmp
      // 071: ifge 0e1
      // 074: aload 0
      // 075: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._uiContext Lnet/rim/device/api/crypto/certificate/status/ProviderUiContext;
      // 078: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 07b: bipush 53
      // 07d: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 080: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderUiContext.setErrorMessage (Ljava/lang/String;)V 2
      // 085: new net/rim/device/api/crypto/certificate/status/StatusProviderException
      // 088: dup
      // 089: invokespecial net/rim/device/api/crypto/certificate/status/StatusProviderException.<init> ()V
      // 08c: athrow
      // 08d: aload 4
      // 08f: aload 0
      // 090: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._nonce [B
      // 093: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 096: ifne 0e1
      // 099: aload 0
      // 09a: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._uiContext Lnet/rim/device/api/crypto/certificate/status/ProviderUiContext;
      // 09d: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 0a0: bipush 30
      // 0a2: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0a5: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 0a8: bipush 50
      // 0aa: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0ad: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.POPUP_BUTTON_TEXT_2 [Ljava/lang/String;
      // 0b0: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.POPUP_BUTTON_CODES_2 [I
      // 0b3: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderUiContext.promptUser (Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;[I)I 5
      // 0b8: istore 5
      // 0ba: iload 5
      // 0bc: bipush -1
      // 0be: if_icmpeq 0c8
      // 0c1: iload 5
      // 0c3: bipush 3
      // 0c5: if_icmpne 0e1
      // 0c8: aload 0
      // 0c9: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._uiContext Lnet/rim/device/api/crypto/certificate/status/ProviderUiContext;
      // 0cc: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 0cf: bipush 53
      // 0d1: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0d4: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderUiContext.setErrorMessage (Ljava/lang/String;)V 2
      // 0d9: new net/rim/device/api/crypto/certificate/status/StatusProviderException
      // 0dc: dup
      // 0dd: invokespecial net/rim/device/api/crypto/certificate/status/StatusProviderException.<init> ()V
      // 0e0: athrow
      // 0e1: aload 0
      // 0e2: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._response Lnet/rim/device/api/crypto/certificate/status/ProviderResponseData;
      // 0e5: aload 2
      // 0e6: bipush 1
      // 0e7: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderResponseData.getCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I)[B 3
      // 0ec: astore 5
      // 0ee: aload 0
      // 0ef: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._response Lnet/rim/device/api/crypto/certificate/status/ProviderResponseData;
      // 0f2: aload 2
      // 0f3: bipush 2
      // 0f5: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderResponseData.getCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I)[B 3
      // 0fa: astore 6
      // 0fc: aload 5
      // 0fe: arraylength
      // 0ff: aload 6
      // 101: arraylength
      // 102: iadd
      // 103: newarray 8
      // 105: astore 7
      // 107: aload 5
      // 109: bipush 0
      // 10a: aload 7
      // 10c: bipush 0
      // 10d: aload 5
      // 10f: arraylength
      // 110: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 113: aload 6
      // 115: bipush 0
      // 116: aload 7
      // 118: aload 5
      // 11a: arraylength
      // 11b: aload 6
      // 11d: arraylength
      // 11e: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 121: aload 7
      // 123: bipush 0
      // 124: ldc_w "X509"
      // 127: invokestatic net/rim/device/api/crypto/encoder/SignatureDecoder.decode ([BILjava/lang/String;)Lnet/rim/device/api/crypto/encoder/DecodedSignature;
      // 12a: astore 8
      // 12c: aload 0
      // 12d: aload 1
      // 12e: aload 8
      // 130: aload 3
      // 131: invokespecial net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.getResponderCertificate (Lnet/rim/device/api/crypto/certificate/status/ocsp/OCSPResponse;Lnet/rim/device/api/crypto/encoder/DecodedSignature;Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;)Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 134: astore 9
      // 136: aload 9
      // 138: ifnull 14e
      // 13b: aload 0
      // 13c: aload 9
      // 13e: invokespecial net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.checkOCSPExtension (Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;)Z
      // 141: ifeq 14e
      // 144: aload 0
      // 145: aload 9
      // 147: aload 3
      // 148: invokespecial net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.trustResponderCertificate (Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;)Z
      // 14b: ifne 156
      // 14e: new net/rim/device/api/crypto/certificate/status/StatusProviderException
      // 151: dup
      // 152: invokespecial net/rim/device/api/crypto/certificate/status/StatusProviderException.<init> ()V
      // 155: athrow
      // 156: return
      // 157: astore 4
      // 159: goto 15e
      // 15c: astore 4
      // 15e: new net/rim/device/api/crypto/certificate/status/StatusProviderException
      // 161: dup
      // 162: invokespecial net/rim/device/api/crypto/certificate/status/StatusProviderException.<init> ()V
      // 165: athrow
      // try (0 -> 156): 157 null
      // try (0 -> 156): 159 null
   }

   private final void decodeSingleResponse(X509Certificate param1, X509Certificate param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._response Lnet/rim/device/api/crypto/certificate/status/ProviderResponseData;
      // 04: aload 1
      // 05: bipush 0
      // 06: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderResponseData.getCertField (Lnet/rim/device/api/crypto/certificate/Certificate;I)[B 3
      // 0b: astore 3
      // 0c: new net/rim/device/api/crypto/certificate/status/ocsp/OCSPResponse
      // 0f: dup
      // 10: aload 3
      // 11: invokespecial net/rim/device/api/crypto/certificate/status/ocsp/OCSPResponse.<init> ([B)V
      // 14: astore 4
      // 16: aload 0
      // 17: aload 4
      // 19: aload 1
      // 1a: aload 2
      // 1b: invokespecial net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery.validateResponse (Lnet/rim/device/api/crypto/certificate/status/ocsp/OCSPResponse;Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;)V
      // 1e: aload 0
      // 1f: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._contextInfoList Ljava/util/Hashtable;
      // 22: aload 1
      // 23: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 26: checkcast net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery$OCSPContextInfo
      // 29: astore 5
      // 2b: aload 4
      // 2d: aload 5
      // 2f: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery$OCSPContextInfo.issuerNameHash [B
      // 32: aload 5
      // 34: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery$OCSPContextInfo.issuerKeyHash [B
      // 37: aload 5
      // 39: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery$OCSPContextInfo.serialNo [B
      // 3c: invokevirtual net/rim/device/api/crypto/certificate/status/ocsp/OCSPResponse.getCertificateStatus ([B[B[B)Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 3f: astore 6
      // 41: aload 0
      // 42: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._response Lnet/rim/device/api/crypto/certificate/status/ProviderResponseData;
      // 45: aload 1
      // 46: aload 6
      // 48: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderResponseData.setCertificateStatus (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;)V 3
      // 4d: return
      // 4e: astore 3
      // 4f: goto 57
      // 52: astore 3
      // 53: goto 57
      // 56: astore 3
      // 57: aload 0
      // 58: getfield net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._uiContext Lnet/rim/device/api/crypto/certificate/status/ProviderUiContext;
      // 5b: getstatic net/rim/device/api/crypto/certificate/status/ocsp/OCSPQuery._rbCrypto Lnet/rim/device/api/i18n/ResourceBundle;
      // 5e: bipush 49
      // 60: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 63: invokeinterface net/rim/device/api/crypto/certificate/status/ProviderUiContext.setErrorMessage (Ljava/lang/String;)V 2
      // 68: new net/rim/device/api/crypto/certificate/status/StatusProviderException
      // 6b: dup
      // 6c: invokespecial net/rim/device/api/crypto/certificate/status/StatusProviderException.<init> ()V
      // 6f: athrow
      // try (0 -> 36): 37 null
      // try (0 -> 36): 39 null
      // try (0 -> 36): 41 net/rim/device/api/crypto/certificate/status/ResponseParsingException
   }

   private final void decodeResponderCerts() {
      byte[] additionalCerts = this._response.getGlobalField(1);

      try {
         if (additionalCerts != null) {
            DataInputStream in = (DataInputStream)(new Object((InputStream)(new Object(additionalCerts))));
            int count = in.readInt();

            while (count-- > 0) {
               String url = in.readUTF();
               int length = in.readInt();
               byte[] certBytes = new byte[length];
               in.read(certBytes);

               try {
                  this.addStoredResponderCert(url, (X509Certificate)(new Object(certBytes)));
               } finally {
                  continue;
               }
            }
         }
      } finally {
         return;
      }
   }

   private final Certificate getIssuer(Certificate cert) {
      DistinguishedName issuer = cert.getIssuer();
      int length = this._certChain.length;

      for (int i = 0; i < length; i++) {
         if (this._certChain[i].getSubject().equals(issuer)) {
            return this._certChain[i];
         }
      }

      return null;
   }

   public final void decodeResponse(ProviderResponseData response, ProviderUiContext uiContext) {
      this._uiContext = uiContext;
      this._response = response;
      this.decodeResponderCerts();
      Enumeration enumeration = response.getCertificates();

      while (enumeration.hasMoreElements()) {
         Certificate cert = (Certificate)enumeration.nextElement();
         this.decodeSingleResponse((X509Certificate)cert, (X509Certificate)this.getIssuer(cert));
      }
   }

   private final void addStoredResponderCert(String url, X509Certificate cert) {
      synchronized (_responderCerts) {
         _responderCerts.add(url, new OCSPQuery$ResponderCertContainer(cert));
      }
   }

   private final Enumeration getStoredResponderCerts(String url) {
      synchronized (_responderCerts) {
         return _responderCerts.elements(url);
      }
   }

   private final Enumeration getStoredResponderCerts(X509DistinguishedName name) {
      synchronized (_responderCerts) {
         Enumeration enumeration = _responderCerts.elements();
         Vector matched = (Vector)(new Object());

         while (enumeration.hasMoreElements()) {
            OCSPQuery$ResponderCertContainer container = (OCSPQuery$ResponderCertContainer)enumeration.nextElement();
            if (name.equals(container.getCertificate().getSubject())) {
               matched.addElement(container.getCertificate());
            }
         }

         return matched.elements();
      }
   }

   private final Enumeration getStoredResponderCerts(byte[] keyHash) {
      synchronized (_responderCerts) {
         Enumeration enumeration = _responderCerts.elements();
         Vector matched = (Vector)(new Object());

         while (enumeration.hasMoreElements()) {
            OCSPQuery$ResponderCertContainer container = (OCSPQuery$ResponderCertContainer)enumeration.nextElement();
            if (Arrays.equals(container.getKeyHash(), keyHash)) {
               matched.addElement(container.getCertificate());
            }
         }

         return matched.elements();
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _responderCerts = (MultiMap)ar.getOrWaitFor(-8840675705458263554L);
      if (_responderCerts == null) {
         _responderCerts = (MultiMap)(new Object());
         ar.put(-8840675705458263554L, _responderCerts);
      }
   }
}
