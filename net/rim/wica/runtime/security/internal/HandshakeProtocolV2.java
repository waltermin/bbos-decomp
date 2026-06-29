package net.rim.wica.runtime.security.internal;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;
import net.rim.device.cldc.io.utility.URL;
import net.rim.wica.runtime.comm.CommunicationService;
import net.rim.wica.runtime.comm.OutgoingRequest;
import net.rim.wica.runtime.comm.Response;
import net.rim.wica.runtime.comm.ResponseListener;
import net.rim.wica.runtime.security.HandshakeException;
import net.rim.wica.runtime.security.HandshakeInfo;
import net.rim.wica.runtime.security.SecurityService;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.util.internal.RuntimeUtilities;
import net.rim.wica.transport.handshake.ClientHello;
import net.rim.wica.transport.handshake.ClientHelloV1;
import net.rim.wica.transport.handshake.EncodedCertificate;
import net.rim.wica.transport.handshake.FailureV1;
import net.rim.wica.transport.handshake.HandshakeMessageException;
import net.rim.wica.transport.handshake.HandshakeMessageFactory;
import net.rim.wica.transport.handshake.HandshakeMessageHandler;
import net.rim.wica.transport.handshake.HandshakeMessageHandlerException;
import net.rim.wica.transport.handshake.OkV1;
import net.rim.wica.transport.handshake.RegisterV1;
import net.rim.wica.transport.handshake.ReplayProtectedHandshakeMessage;
import net.rim.wica.transport.handshake.ServerHello;
import net.rim.wica.transport.handshake.ServerHelloV1;
import net.rim.wica.transport.handshake.SignedHandshakeMessage;
import net.rim.wica.transport.handshake.UnregisterV1;
import net.rim.wica.transport.handshake.UnregisterV1_1;
import net.rim.wica.transport.handshake.VersionNotSupportedException;
import net.rim.wica.transport.security.Key;
import net.rim.wica.transport.security.KeyType;
import net.rim.wica.transport.security.SecurityProvider;

final class HandshakeProtocolV2 implements HandshakeProtocol, ResponseListener, HandshakeMessageHandler {
   private byte _state = 0;
   private byte[] _nonce;
   private Key _registrationKey;
   private Key _resetKey;
   private Key _agPublicKey;
   private HandshakeMessageFactory _mFactory;
   private SecurityProvider _securityProvider;
   private KeyProviderImpl _keyProvider;
   private int _securityVersion;
   private CommunicationService _comServ;
   private HandshakeHandler _handshakeHandler;
   private HandshakeInfo _handshakeInfo;
   private boolean _register;
   private int _retryRegisterCount = 2;
   private long _agId;
   private boolean _usingRecoveryKeys;
   private static final byte STATE_NOT_NEGOTIATED;
   private static final byte STATE_HELLO_WAIT;
   private static final byte STATE_REGISTER_WAIT;
   private static final byte STATE_NEGOTIATED;
   private static final int MAX_RETRY_COUNT;
   static Class class$net$rim$wica$runtime$comm$CommunicationService;
   static Class class$net$rim$wica$runtime$security$SecurityService;

   protected final void send(byte[] data, ResponseListener l) {
      OutgoingRequest outReq = this._comServ.createOutgoingRequestInstance(this._handshakeInfo.getAGURL());
      outReq.setRequestMethod("POST");
      outReq.setData(data);
      outReq.setResponseListener(l);
      this._comServ.sendRequest(outReq);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void unregister() {
      this._register = false;

      try {
         this._registrationKey = this.getAnyRegistrationKey(this._handshakeInfo.getAGId());
         this._nonce = generateNonce();
         UnregisterV1_1 unregister = new UnregisterV1_1(this._securityProvider);
         unregister.setPIN(Integer.toHexString(this._handshakeInfo.getDevicePIN()));
         unregister.setNonce(this._nonce);
         unregister.sign(this._registrationKey);
         this.send(unregister.serialize(), this);
      } catch (Throwable var3) {
         throw new HandshakeException("Exception during unregister operation", e, this._handshakeInfo);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void processResponse(Response response, OutgoingRequest request) {
      HandshakeMessageHandlerException e;
      try {
         try {
            int responseCode = response.getResponseCode();
            if (200 == responseCode) {
               this._mFactory.handleMessage(response.getData(), this);
            } else {
               if (this._state != 2 || this._retryRegisterCount <= 0 || responseCode != 503 && responseCode != 604 && responseCode != 603) {
                  if (this._usingRecoveryKeys && this._state == 2 && responseCode == 400) {
                     this._usingRecoveryKeys = false;
                     this._retryRegisterCount = 2;
                     this._keyProvider.setRecoveryKeys(this._agId, new Key[]{null, null});
                     this._registrationKey = this._securityProvider.generateKey(KeyType.AES);
                     this._resetKey = this._securityProvider.generateKey(KeyType.AES);
                     Key signKey = this.getAnyRegistrationKey(this._agId);
                     if (signKey == null) {
                        this._handshakeInfo.setResetState(true);
                        signKey = this.getAnyResetKey(this._agId);
                     }

                     this._keyProvider.setRecoveryKeys(this._agId, new Key[]{this._registrationKey, this._resetKey});
                     this.register(this._securityVersion, this._registrationKey, this._resetKey, signKey);
                     return;
                  }

                  String hint = this._state != 1 || responseCode != 500 && responseCode != 400 ? null : "Hello";
                  this._state = 0;
                  throw new HandshakeException(
                     hint, (Throwable)(new Object(((StringBuffer)(new Object("HTTP Error"))).append(responseCode).toString())), this._handshakeInfo
                  );
               }

               this._state = 0;
               this._retryRegisterCount--;
               this.register(this._securityVersion, this._registrationKey, this._resetKey, this._registrationKey);
            }

            return;
         } catch (HandshakeMessageHandlerException var7) {
            e = var7;
         }
      } catch (Throwable var8) {
         HandshakeException he = !(e instanceof HandshakeException) ? new HandshakeException(e, this._handshakeInfo) : (HandshakeException)e;
         this.failure(he);
         return;
      }

      HandshakeException he = e.getCause() != null ? new HandshakeException(e.getCause(), this._handshakeInfo) : new HandshakeException(e, this._handshakeInfo);
      this.failure(he);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void handleMessage(ServerHelloV1 m) {
      this._state = 0;
      int version = m.getServerVersion();
      if (version < this._handshakeHandler.getMinSecurityVersion()) {
         throw new HandshakeMessageHandlerException(new VersionNotSupportedException());
      }

      this._securityVersion = Math.min(this._handshakeInfo.getSecurityVersion(), version);

      try {
         this._agPublicKey = this.verifyCertificate(m.getServerCertificateChain());
         Key signKey = null;
         this._agId = m.getServerId();
         Key pendingRegKey = this._keyProvider.getPendingRegistrationKey(this._agId);
         if (pendingRegKey != null) {
            Key regKey = this.getAnyRegistrationKey(this._agId);
            if (regKey != null && pendingRegKey.getNativeKey().equals(regKey.getNativeKey())) {
               this._keyProvider.setRecoveryKeys(this._agId, new Key[]{null, null});
            } else {
               this._usingRecoveryKeys = true;
               this._registrationKey = pendingRegKey;
               this._resetKey = this._keyProvider.getPendingResetKey(this._agId);
               signKey = pendingRegKey;
            }
         }

         if (signKey == null) {
            signKey = this.getAnyRegistrationKey(this._agId);
            if (signKey != null) {
               this._resetKey = this._securityProvider.generateKey(KeyType.AES);
            } else {
               this._handshakeInfo.setResetState(true);
               signKey = this.getAnyResetKey(this._agId);
               Key pendingResetKey = this._keyProvider.getPendingResetKey(this._agId);
               if (pendingResetKey == null || signKey != null && pendingResetKey.getNativeKey().equals(signKey.getNativeKey())) {
                  this._resetKey = this._securityProvider.generateKey(KeyType.AES);
               } else {
                  this._usingRecoveryKeys = true;
                  signKey = pendingResetKey;
                  this._resetKey = pendingResetKey;
               }
            }

            this._registrationKey = this._securityProvider.generateKey(KeyType.AES);
            this._keyProvider.setRecoveryKeys(this._agId, new Key[]{this._registrationKey, this._resetKey});
         }

         this.register(version, this._registrationKey, this._resetKey, signKey);
      } catch (Throwable var7) {
         throw new HandshakeMessageHandlerException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void handleMessage(OkV1 m) {
      try {
         this.verifyResponse(m);
         this._state = 3;
         this._handshakeInfo.setAGId(m.getServerId());
         this._handshakeInfo.setDeviceId(m.getDeviceId());
         this._handshakeInfo.setServerVersions(m.getServerVersions());
         if (this._register) {
            this._handshakeInfo.setSecurityVersion(this._securityVersion);
            this._handshakeInfo.setResetState(m.firstHandshake());
         }

         this.success(this._handshakeInfo);
      } catch (Throwable var4) {
         throw new HandshakeMessageHandlerException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void handleMessage(FailureV1 m) {
      try {
         this.verifyResponse(m);
         this._state = 0;
         String reason = m.getReason();
         this.failure(new HandshakeException(reason, this._handshakeInfo));
      } catch (Throwable var4) {
         throw new HandshakeMessageHandlerException(e);
      }
   }

   @Override
   public final void handleMessage(RegisterV1 m) {
      throw new Object("RegisterV1 should not be recieved by client");
   }

   @Override
   public final void handleMessage(ClientHelloV1 m) {
      throw new Object("ClientHelloV1 should not be recieved by client");
   }

   @Override
   public final void handleMessage(ClientHello m) {
      throw new Object("ClientHello should not be recieved by client");
   }

   @Override
   public final void handleMessage(UnregisterV1 m) {
      throw new Object("UnregisterV1 should not be recieved by client");
   }

   @Override
   public final void handleMessage(ServerHello m) {
      throw new Object("ServerHello should not be recieved by client");
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void register() {
      this._register = true;

      try {
         ClientHelloV1 clientHello = this._mFactory.createClientHelloV1(this._handshakeInfo.getSecurityVersion());
         clientHello.setDeviceVersion(this._handshakeInfo.getSecurityVersion());
         this._state = 1;
         this.send(clientHello.serialize(), this);
      } catch (Throwable var3) {
         throw new HandshakeException(e, this._handshakeInfo);
      }
   }

   private final void verifyDomain(X509Certificate certificate) {
      URL agURL = (URL)(new Object(this._handshakeInfo.getAGURL()));
      String domain = agURL.getHost();
      String commonName = certificate.getSubject().getString(OIDs.getOID(-1253056853));
      boolean found = false;
      if (commonName != null && domain.indexOf(commonName) >= 0) {
         found = true;
      } else {
         String[] domainNames = certificate.getSubjectAltNameStrings(12);
         if (domainNames != null) {
            int domainLength = domainNames.length;

            for (int i = 0; i < domainLength; i++) {
               if (domain.equals(domainNames[i])) {
                  found = true;
                  break;
               }
            }
         }
      }

      if (!found) {
         throw new Object();
      }
   }

   private final Key verifyCertificate(EncodedCertificate[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: ldc_w "X509"
      // 03: aload 1
      // 04: bipush 0
      // 05: aaload
      // 06: invokevirtual net/rim/wica/transport/handshake/EncodedCertificate.getCertificate ()[B
      // 09: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;[B)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 0c: checkcast java/lang/Object
      // 0f: astore 2
      // 10: bipush 44
      // 12: bipush 3
      // 14: bipush 0
      // 15: invokestatic net/rim/device/api/itpolicy/ITPolicy.getBoolean (IIZ)Z
      // 18: ifne 1e
      // 1b: goto be
      // 1e: aload 0
      // 1f: aload 2
      // 20: invokespecial net/rim/wica/runtime/security/internal/HandshakeProtocolV2.verifyDomain (Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;)V
      // 23: aload 1
      // 24: arraylength
      // 25: istore 3
      // 26: iload 3
      // 27: bipush 1
      // 28: isub
      // 29: anewarray 1632
      // 2c: astore 4
      // 2e: bipush 1
      // 2f: istore 5
      // 31: iload 5
      // 33: iload 3
      // 34: if_icmpge 51
      // 37: aload 4
      // 39: iload 5
      // 3b: bipush 1
      // 3c: isub
      // 3d: ldc_w "X509"
      // 40: aload 1
      // 41: iload 5
      // 43: aaload
      // 44: invokevirtual net/rim/wica/transport/handshake/EncodedCertificate.getCertificate ()[B
      // 47: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;[B)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 4a: aastore
      // 4b: iinc 5 1
      // 4e: goto 31
      // 51: aload 2
      // 52: aload 4
      // 54: invokestatic net/rim/device/api/crypto/keystore/DeviceKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 57: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.buildCertificateChains (Lnet/rim/device/api/crypto/certificate/Certificate;[Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;)[[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 5a: astore 5
      // 5c: aload 5
      // 5e: invokestatic net/rim/device/api/crypto/keystore/TrustedKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 61: invokestatic java/lang/System.currentTimeMillis ()J
      // 64: invokestatic net/rim/device/api/crypto/certificate/CertificateChainProperties.getCertificateChainProperties ([[Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;J)[J
      // 67: astore 6
      // 69: aload 6
      // 6b: invokestatic net/rim/device/api/crypto/certificate/CertificateChainProperties.selectBestCertificateChain ([J)I
      // 6e: istore 7
      // 70: aload 6
      // 72: iload 7
      // 74: laload
      // 75: lstore 8
      // 77: lload 8
      // 79: sipush 1024
      // 7c: i2l
      // 7d: land
      // 7e: bipush 0
      // 7f: i2l
      // 80: lcmp
      // 81: ifeq 8f
      // 84: new java/lang/Object
      // 87: dup
      // 88: ldc_w "Certificate revoked"
      // 8b: invokespecial net/rim/device/api/crypto/certificate/CertificateVerificationException.<init> (Ljava/lang/String;)V
      // 8e: athrow
      // 8f: lload 8
      // 91: sipush 256
      // 94: i2l
      // 95: land
      // 96: bipush 0
      // 97: i2l
      // 98: lcmp
      // 99: ifeq a7
      // 9c: new java/lang/Object
      // 9f: dup
      // a0: ldc_w "Bad certificate"
      // a3: invokespecial net/rim/device/api/crypto/certificate/CertificateVerificationException.<init> (Ljava/lang/String;)V
      // a6: athrow
      // a7: lload 8
      // a9: bipush 30
      // ab: i2l
      // ac: land
      // ad: bipush 0
      // ae: i2l
      // af: lcmp
      // b0: ifeq be
      // b3: new java/lang/Object
      // b6: dup
      // b7: ldc_w "Certificate unknown"
      // ba: invokespecial net/rim/device/api/crypto/certificate/CertificateVerificationException.<init> (Ljava/lang/String;)V
      // bd: athrow
      // be: new net/rim/wica/transport/security/Key
      // c1: dup
      // c2: aload 2
      // c3: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // c6: getstatic net/rim/wica/transport/security/KeyType.RSA Lnet/rim/wica/transport/security/KeyType;
      // c9: invokespecial net/rim/wica/transport/security/Key.<init> (Ljava/lang/Object;Lnet/rim/wica/transport/security/KeyType;)V
      // cc: areturn
      // cd: astore 2
      // ce: aload 2
      // cf: athrow
      // d0: astore 2
      // d1: new java/lang/Object
      // d4: dup
      // d5: aload 2
      // d6: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // d9: invokespecial net/rim/device/api/crypto/certificate/CertificateVerificationException.<init> (Ljava/lang/String;)V
      // dc: athrow
      // try (0 -> 105): 106 null
      // try (0 -> 105): 109 null
   }

   private final void register(int version, Key registrationKey, Key resetKey, Key signKey) {
      this._nonce = generateNonce();
      RegisterV1 register = this._mFactory.createRegisterV1(version);
      register.setDeviceVersion(this._handshakeInfo.getSecurityVersion());
      register.setPIN(Integer.toHexString(this._handshakeInfo.getDevicePIN()));
      register.setDeviceVersions(this._handshakeInfo.getDeviceVersions());
      register.setResetState(this._handshakeInfo.getResetState());
      register.setReVersion(RuntimeUtilities.getRuntimeVersion(true));
      register.setNonce(this._nonce);
      register.setRK(this._securityProvider.encodeKey(registrationKey));
      register.setResetKey(this._securityProvider.encodeKey(resetKey));
      if (this._agPublicKey != null) {
         register.secure(this._agPublicKey);
      }

      if (signKey != null) {
         register.sign(signKey);
      }

      this._state = 2;
      this.send(register.serialize(), this);
   }

   private final void failure(HandshakeException e) {
      if (this._register) {
         this._handshakeHandler.registrationFailed(e);
      } else {
         this._handshakeHandler.unregistrationFailed(e);
      }
   }

   private final void success(HandshakeInfo info) {
      if (this._register) {
         this._handshakeHandler.registrationCompleted(info, new Key[]{this._registrationKey, this._resetKey});
      } else {
         this._handshakeHandler.unregistrationCompleted(info);
      }
   }

   static final byte[] generateNonce() {
      byte[] nonce = new byte[8];
      RandomSource.getBytes(nonce, 0, 8);
      return nonce;
   }

   private final void verifyResponse(Object message) {
      if (message instanceof SignedHandshakeMessage) {
         ((SignedHandshakeMessage)message).verifySignature(this._registrationKey);
      }

      if (message instanceof ReplayProtectedHandshakeMessage) {
         byte[] nonce = ((ReplayProtectedHandshakeMessage)message).getNonce();
         if (!Arrays.equals(nonce, this._nonce)) {
            throw new HandshakeMessageException();
         }
      }
   }

   HandshakeProtocolV2(HandshakeHandler h, HandshakeInfo info, ServiceProvider sp) {
      this._handshakeHandler = h;
      this._handshakeInfo = info;
      this._comServ = (CommunicationService)sp.getService(
         class$net$rim$wica$runtime$comm$CommunicationService == null
            ? (class$net$rim$wica$runtime$comm$CommunicationService = class$("net.rim.wica.runtime.comm.CommunicationService"))
            : class$net$rim$wica$runtime$comm$CommunicationService
      );
      SecurityService security = (SecurityService)sp.getService(
         class$net$rim$wica$runtime$security$SecurityService == null
            ? (class$net$rim$wica$runtime$security$SecurityService = class$("net.rim.wica.runtime.security.SecurityService"))
            : class$net$rim$wica$runtime$security$SecurityService
      );
      this._securityProvider = security.getSecurityProvider();
      this._keyProvider = (KeyProviderImpl)security.getKeyProvider();
      this._mFactory = new HandshakeMessageFactory(this._securityProvider, h.getMinSecurityVersion(), h.getMaxSecurityVersion());
   }

   private final Key getAnyRegistrationKey(long id) {
      Key key = this._keyProvider.getPrimaryRegistrationKey(id);
      return key != null ? key : this._keyProvider.getSecondaryRegistrationKey(id);
   }

   private final Key getAnyResetKey(long id) {
      Key key = this._keyProvider.getResetKey(id);
      return key != null ? key : this._keyProvider.getRecoveryResetKey(id);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
