package net.rim.device.api.crypto.tls.ssl30;

import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DHKeyPair;
import net.rim.device.api.crypto.DHPublicKey;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.KeyPair;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.SymmetricKeyFactory;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.certificate.CertificateImporterFactory;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.crypto.tls.AlertProtocolMethods;
import net.rim.device.api.crypto.tls.ChangeCipherSpecProtocol;
import net.rim.device.api.crypto.tls.ConnectionState;
import net.rim.device.api.crypto.tls.HandshakeProtocol;
import net.rim.device.api.crypto.tls.KeyMaterial;
import net.rim.device.api.crypto.tls.SessionResumption;
import net.rim.device.api.crypto.tls.TLSUtilities;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.ssl.SSLConnectionOptions;
import net.rim.device.cldc.io.ssl.TLSOptionStore;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.vm.Array;
import net.rim.vm.TraceBack;

public class SSLHandshakeProtocol extends HandshakeProtocol implements SSLRecordProtocolConstants {
   protected SHA1Digest _clientSHAHash = (SHA1Digest)(new Object());
   protected MD5Digest _clientMD5Hash = (MD5Digest)(new Object());
   protected SHA1Digest _serverSHAHash = (SHA1Digest)(new Object());
   protected MD5Digest _serverMD5Hash = (MD5Digest)(new Object());
   protected SHA1Digest _verifySHAHash = (SHA1Digest)(new Object());
   protected MD5Digest _verifyMD5Hash = (MD5Digest)(new Object());
   protected DataBuffer _dataBuffer = (DataBuffer)(new Object());
   protected PublicKey _publicKey;
   protected PrivateKey _privateKey;
   protected KeyPair _keyPair;
   protected byte[] _premasterSecret;
   protected byte[] _masterSecret;
   protected SSLCipherSuiteFactory _factory;
   protected boolean _clientCertificateSent;
   protected byte[] _types;
   protected DistinguishedName[] _dn;
   protected int _remoteVersion;
   protected boolean _resumption;
   protected byte[] _sessionID;
   protected int _cipherSuite;
   protected SSLRecordProtocol _recordProtocol;
   protected AlertProtocolMethods _alertProtocol;
   protected ChangeCipherSpecProtocol _changeCipherSpecProtocol;
   private SSLConnectionOptions _overrideConnectionOptions;
   protected static final boolean DEBUG = false;
   public static final int EXPORT_LENGTH = 1024;
   public static final int ECC_EXPORT_LENGTH = 163;
   public static final byte[] SENDER_CLIENT = new byte[]{67, 76, 78, 84};
   public static final byte[] SENDER_SERVER = new byte[]{83, 82, 86, 82};
   protected static final ResourceBundle _rb = ResourceBundle.getBundle(5710659227867441061L, "net.rim.device.internal.resource.crypto.SSL");

   public SSLHandshakeProtocol(SSLRecordProtocol recordProtocol) {
      this._recordProtocol = recordProtocol;
      this._alertProtocol = recordProtocol.getAlertProtocol();
      this._changeCipherSpecProtocol = this._recordProtocol.getChangeCipherSpecProtocol();
      this._factory = new SSLCipherSuiteFactory(recordProtocol);
   }

   @Override
   public void helloRequest(DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 04: istore 2
      // 05: iload 2
      // 06: ifeq 12
      // 09: aload 0
      // 0a: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 0d: bipush 10
      // 0f: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 12: aload 1
      // 13: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.readIntegerThreeBytes (Lnet/rim/device/api/util/DataBuffer;)I
      // 16: ifne 24
      // 19: aload 1
      // 1a: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 1d: aload 1
      // 1e: invokevirtual net/rim/device/api/util/DataBuffer.getLength ()I
      // 21: if_icmpeq 2d
      // 24: aload 0
      // 25: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 28: bipush 50
      // 2a: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 2d: aload 0
      // 2e: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.connect ()V
      // 31: return
      // 32: astore 2
      // 33: new java/lang/Object
      // 36: dup
      // 37: aload 2
      // 38: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 3b: athrow
      // 3c: astore 2
      // 3d: new java/lang/Object
      // 40: dup
      // 41: aload 2
      // 42: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 45: athrow
      // try (0 -> 23): 24 null
      // try (0 -> 23): 30 null
   }

   @Override
   public void clientHello() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: new java/lang/Object
      // 003: dup
      // 004: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 007: astore 1
      // 008: aload 1
      // 009: bipush 1
      // 00a: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 00d: aload 1
      // 00e: bipush 0
      // 00f: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerThreeBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 012: aload 1
      // 013: aload 0
      // 014: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.getLocalVersion ()I
      // 017: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 01a: aload 0
      // 01b: bipush 28
      // 01d: invokevirtual net/rim/device/api/crypto/tls/HandshakeProtocol.getRandom (I)Lnet/rim/device/api/crypto/tls/RandomStructure;
      // 020: astore 2
      // 021: aload 1
      // 022: aload 2
      // 023: invokevirtual net/rim/device/api/crypto/tls/RandomStructure.getRandomBytes ()[B
      // 026: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 029: aload 0
      // 02a: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 02d: aload 2
      // 02e: invokevirtual net/rim/device/api/crypto/tls/RandomStructure.getRandomBytes ()[B
      // 031: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.setClientRandom ([B)V
      // 034: aload 0
      // 035: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.getCipherSuites ()[B
      // 038: astore 3
      // 039: invokestatic net/rim/device/cldc/io/ssl/TLSOptionStore.getOptions ()Lnet/rim/device/cldc/io/ssl/TLSOptionStore;
      // 03c: astore 4
      // 03e: aload 4
      // 040: invokevirtual net/rim/device/cldc/io/ssl/TLSOptionStore.getSessionResumption ()Z
      // 043: ifne 049
      // 046: goto 0e8
      // 049: new java/lang/Object
      // 04c: dup
      // 04d: invokespecial net/rim/device/api/crypto/tls/SessionResumption.<init> ()V
      // 050: astore 5
      // 052: aload 0
      // 053: bipush 32
      // 055: newarray 8
      // 057: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._sessionID [B
      // 05a: aload 0
      // 05b: bipush 48
      // 05d: newarray 8
      // 05f: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._masterSecret [B
      // 062: aload 0
      // 063: aload 5
      // 065: aload 0
      // 066: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 069: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getDomainName ()Ljava/lang/String;
      // 06c: aload 0
      // 06d: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 070: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getProtocol ()Ljava/lang/String;
      // 073: aload 0
      // 074: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._sessionID [B
      // 077: aload 0
      // 078: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._masterSecret [B
      // 07b: invokevirtual net/rim/device/api/crypto/tls/SessionResumption.getSession (Ljava/lang/String;Ljava/lang/String;[B[B)I
      // 07e: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._cipherSuite I
      // 081: aload 0
      // 082: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._cipherSuite I
      // 085: iflt 0de
      // 088: aload 5
      // 08a: aload 0
      // 08b: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 08e: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getDomainName ()Ljava/lang/String;
      // 091: aload 0
      // 092: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 095: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getProtocol ()Ljava/lang/String;
      // 098: invokevirtual net/rim/device/api/crypto/tls/SessionResumption.getSessionCertificate (Ljava/lang/String;Ljava/lang/String;)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 09b: astore 6
      // 09d: aload 5
      // 09f: aload 0
      // 0a0: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0a3: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getDomainName ()Ljava/lang/String;
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0aa: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getProtocol ()Ljava/lang/String;
      // 0ad: invokevirtual net/rim/device/api/crypto/tls/SessionResumption.getSessionCertificatePool (Ljava/lang/String;Ljava/lang/String;)[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 0b0: astore 7
      // 0b2: aload 0
      // 0b3: aload 6
      // 0b5: aload 7
      // 0b7: bipush 0
      // 0b8: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.verifyCertificate (Lnet/rim/device/api/crypto/certificate/Certificate;[Lnet/rim/device/api/crypto/certificate/Certificate;Z)V
      // 0bb: aload 0
      // 0bc: aload 0
      // 0bd: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._cipherSuite I
      // 0c0: aload 3
      // 0c1: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.checkCipherSuites (I[B)V
      // 0c4: aload 0
      // 0c5: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0c8: aload 6
      // 0ca: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.saveCertificate (Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 0cd: aload 0
      // 0ce: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0d1: aload 7
      // 0d3: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.saveCertificatePool ([Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 0d6: aload 0
      // 0d7: bipush 1
      // 0d8: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._resumption Z
      // 0db: goto 0e8
      // 0de: aload 0
      // 0df: aconst_null
      // 0e0: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._sessionID [B
      // 0e3: aload 0
      // 0e4: aconst_null
      // 0e5: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._masterSecret [B
      // 0e8: aload 0
      // 0e9: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._sessionID [B
      // 0ec: ifnonnull 0f7
      // 0ef: aload 1
      // 0f0: bipush 0
      // 0f1: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 0f4: goto 108
      // 0f7: aload 1
      // 0f8: aload 0
      // 0f9: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._sessionID [B
      // 0fc: arraylength
      // 0fd: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 100: aload 1
      // 101: aload 0
      // 102: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._sessionID [B
      // 105: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 108: aload 1
      // 109: aload 3
      // 10a: arraylength
      // 10b: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 10e: aload 1
      // 10f: aload 3
      // 110: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 113: aload 0
      // 114: invokevirtual net/rim/device/api/crypto/tls/HandshakeProtocol.getCompressionAlgorithms ()[B
      // 117: astore 5
      // 119: aload 1
      // 11a: aload 5
      // 11c: arraylength
      // 11d: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 120: aload 1
      // 121: aload 5
      // 123: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 126: aload 0
      // 127: aload 1
      // 128: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.write (Lnet/rim/device/api/util/DataBuffer;)V
      // 12b: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 12e: ldc_w "SSL:->CH"
      // 131: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 134: return
      // 135: astore 1
      // 136: aload 0
      // 137: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 13a: bipush 3
      // 13c: bipush 50
      // 13e: invokeinterface net/rim/device/api/crypto/tls/AlertProtocolMethods.sendAlertMessage (BB)V 3
      // 143: new java/lang/Object
      // 146: dup
      // 147: aload 1
      // 148: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 14b: athrow
      // 14c: astore 1
      // 14d: aload 0
      // 14e: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 151: bipush 3
      // 153: bipush 50
      // 155: invokeinterface net/rim/device/api/crypto/tls/AlertProtocolMethods.sendAlertMessage (BB)V 3
      // 15a: new java/lang/Object
      // 15d: dup
      // 15e: aload 1
      // 15f: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 162: athrow
      // try (0 -> 150): 151 null
      // try (0 -> 150): 162 null
   }

   @Override
   public void serverHello(DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 003: ldc_w "SSL:<-SH"
      // 006: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 009: aload 0
      // 00a: aload 1
      // 00b: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.readIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;)I
      // 00e: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._remoteVersion I
      // 011: aload 0
      // 012: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._remoteVersion I
      // 015: aload 0
      // 016: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.getLocalVersion ()I
      // 019: if_icmpeq 058
      // 01c: invokestatic net/rim/device/cldc/io/ssl/TLSOptionStore.getOptions ()Lnet/rim/device/cldc/io/ssl/TLSOptionStore;
      // 01f: astore 2
      // 020: aload 2
      // 021: invokevirtual net/rim/device/cldc/io/ssl/TLSOptionStore.useSSL ()Z
      // 024: ifeq 046
      // 027: aload 0
      // 028: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._remoteVersion I
      // 02b: sipush 768
      // 02e: if_icmpeq 058
      // 031: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 034: ldc_w "SSL: Versions"
      // 037: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 03a: aload 0
      // 03b: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 03e: bipush 70
      // 040: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 043: goto 058
      // 046: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 049: ldc_w "SSL: SSL Restricted"
      // 04c: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 04f: aload 0
      // 050: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 053: bipush 70
      // 055: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 058: aload 0
      // 059: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 05c: aload 0
      // 05d: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._remoteVersion I
      // 060: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.setRemoteVersion (I)V
      // 063: bipush 32
      // 065: newarray 8
      // 067: astore 2
      // 068: aload 1
      // 069: aload 2
      // 06a: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 06d: aload 0
      // 06e: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 071: aload 2
      // 072: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.setServerRandom ([B)V
      // 075: aload 1
      // 076: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 079: istore 3
      // 07a: iload 3
      // 07b: newarray 8
      // 07d: astore 4
      // 07f: aload 1
      // 080: aload 4
      // 082: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 085: aload 0
      // 086: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._resumption Z
      // 089: ifeq 09d
      // 08c: aload 4
      // 08e: aload 0
      // 08f: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._sessionID [B
      // 092: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 095: ifne 09d
      // 098: aload 0
      // 099: bipush 0
      // 09a: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._resumption Z
      // 09d: aload 0
      // 09e: aload 4
      // 0a0: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._sessionID [B
      // 0a3: aload 0
      // 0a4: aload 1
      // 0a5: invokevirtual net/rim/device/api/util/DataBuffer.readShort ()S
      // 0a8: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._cipherSuite I
      // 0ab: aload 0
      // 0ac: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 0af: aload 0
      // 0b0: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._cipherSuite I
      // 0b3: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.setCipherSuite (I)V
      // 0b6: invokestatic net/rim/device/cldc/io/ssl/TLSOptionStore.getOptions ()Lnet/rim/device/cldc/io/ssl/TLSOptionStore;
      // 0b9: astore 5
      // 0bb: aload 0
      // 0bc: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.getRawCipherSuites ()[I
      // 0bf: aload 0
      // 0c0: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._cipherSuite I
      // 0c3: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.checkCipherSuite ([II)Z
      // 0c6: ifne 0db
      // 0c9: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 0cc: ldc_w "SSL: NO CS"
      // 0cf: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 0d2: aload 0
      // 0d3: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 0d6: bipush 71
      // 0d8: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 0db: aload 0
      // 0dc: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._cipherSuite I
      // 0df: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLCipherSuites.export (I)Z
      // 0e2: ifeq 122
      // 0e5: aload 5
      // 0e7: invokevirtual net/rim/device/cldc/io/ssl/TLSOptionStore.allowExportCipherSuites ()Z
      // 0ea: ifne 122
      // 0ed: getstatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0f0: bipush 14
      // 0f2: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0f5: getstatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0f8: bipush 16
      // 0fa: invokevirtual net/rim/device/api/i18n/ResourceBundle.getStringArray (I)[Ljava/lang/String;
      // 0fd: bipush 0
      // 0fe: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.getChoice (Ljava/lang/String;[Ljava/lang/Object;I)I
      // 101: istore 6
      // 103: iload 6
      // 105: bipush 1
      // 106: if_icmpne 115
      // 109: aload 0
      // 10a: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 10d: bipush 71
      // 10f: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 112: goto 122
      // 115: iload 6
      // 117: bipush 2
      // 119: if_icmpne 122
      // 11c: aload 5
      // 11e: bipush 1
      // 11f: invokevirtual net/rim/device/cldc/io/ssl/TLSOptionStore.setAllowExportCipherSuites (Z)V
      // 122: aload 0
      // 123: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._factory Lnet/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory;
      // 126: aload 0
      // 127: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._cipherSuite I
      // 12a: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory.getConnectionState (I)Ljava/lang/Object;
      // 12d: astore 6
      // 12f: aload 0
      // 130: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._factory Lnet/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory;
      // 133: aload 0
      // 134: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._cipherSuite I
      // 137: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory.getConnectionState (I)Ljava/lang/Object;
      // 13a: astore 7
      // 13c: aload 1
      // 13d: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 140: istore 8
      // 142: aload 6
      // 144: aload 0
      // 145: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._factory Lnet/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory;
      // 148: iload 8
      // 14a: i2b
      // 14b: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory.getCompressionAlgorithm (B)Ljava/lang/String;
      // 14e: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setCompressionAlgorithm (Ljava/lang/String;)V
      // 151: aload 7
      // 153: aload 0
      // 154: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._factory Lnet/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory;
      // 157: iload 8
      // 159: i2b
      // 15a: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLCipherSuiteFactory.getCompressionAlgorithm (B)Ljava/lang/String;
      // 15d: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.setCompressionAlgorithm (Ljava/lang/String;)V
      // 160: aload 0
      // 161: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 164: aload 6
      // 166: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.setPendingRead (Ljava/lang/Object;)V
      // 169: aload 0
      // 16a: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 16d: aload 7
      // 16f: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.setPendingWrite (Ljava/lang/Object;)V
      // 172: return
      // 173: astore 2
      // 174: goto 178
      // 177: astore 2
      // 178: aload 0
      // 179: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 17c: bipush 50
      // 17e: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 181: return
      // try (0 -> 165): 166 null
      // try (0 -> 165): 168 null
   }

   @Override
   public void serverCertificate(DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 03: ldc_w "SSL:<-SC"
      // 06: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 09: invokestatic net/rim/device/cldc/io/ssl/TLSOptionStore.getOptions ()Lnet/rim/device/cldc/io/ssl/TLSOptionStore;
      // 0c: astore 2
      // 0d: aload 0
      // 0e: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._overrideConnectionOptions Lnet/rim/device/cldc/io/ssl/SSLConnectionOptions;
      // 11: ifnull 1b
      // 14: aload 0
      // 15: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._overrideConnectionOptions Lnet/rim/device/cldc/io/ssl/SSLConnectionOptions;
      // 18: goto 1f
      // 1b: aload 2
      // 1c: invokevirtual net/rim/device/cldc/io/ssl/TLSOptionStore.getDefaultConnectionOptions ()Lnet/rim/device/cldc/io/ssl/SSLConnectionOptions;
      // 1f: astore 3
      // 20: aload 1
      // 21: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.readIntegerThreeBytes (Lnet/rim/device/api/util/DataBuffer;)I
      // 24: ifgt 30
      // 27: aload 0
      // 28: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 2b: bipush 42
      // 2d: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 30: aload 1
      // 31: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.readIntegerThreeBytes (Lnet/rim/device/api/util/DataBuffer;)I
      // 34: newarray 8
      // 36: astore 4
      // 38: aload 1
      // 39: aload 4
      // 3b: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 3e: ldc_w "X509"
      // 41: aload 4
      // 43: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;[B)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 46: checkcast net/rim/device/api/crypto/certificate/x509/X509Certificate
      // 49: astore 5
      // 4b: aload 5
      // 4d: aload 3
      // 4e: aload 0
      // 4f: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 52: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.verifyCertificateCapabilities (Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;Lnet/rim/device/cldc/io/ssl/SSLConnectionOptions;Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;)V
      // 55: bipush 0
      // 56: anewarray 1320
      // 59: astore 6
      // 5b: aload 1
      // 5c: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 5f: ifne 80
      // 62: aload 1
      // 63: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.readIntegerThreeBytes (Lnet/rim/device/api/util/DataBuffer;)I
      // 66: newarray 8
      // 68: astore 7
      // 6a: aload 1
      // 6b: aload 7
      // 6d: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 70: aload 6
      // 72: ldc_w "X509"
      // 75: aload 7
      // 77: invokestatic net/rim/device/api/crypto/certificate/CertificateFactory.getInstance (Ljava/lang/String;[B)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 7a: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 7d: goto 5b
      // 80: aload 0
      // 81: aload 5
      // 83: aload 6
      // 85: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.verifyCertificateDomainName (Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;[Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;)V
      // 88: aload 0
      // 89: aload 5
      // 8b: aload 6
      // 8d: bipush 1
      // 8e: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.verifyCertificate (Lnet/rim/device/api/crypto/certificate/Certificate;[Lnet/rim/device/api/crypto/certificate/Certificate;Z)V
      // 91: aload 0
      // 92: aload 5
      // 94: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 97: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 9a: goto a4
      // 9d: astore 7
      // 9f: aload 0
      // a0: aconst_null
      // a1: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // a4: aload 0
      // a5: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // a8: aload 5
      // aa: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.saveCertificate (Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // ad: aload 0
      // ae: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // b1: aload 6
      // b3: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.saveCertificatePool ([Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // b6: aload 0
      // b7: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // ba: aload 0
      // bb: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // be: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // c3: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // c8: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.setKeyExchangeSize (I)V
      // cb: return
      // cc: astore 2
      // cd: aload 0
      // ce: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // d1: bipush 50
      // d3: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // d6: return
      // d7: astore 2
      // d8: aload 0
      // d9: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // dc: bipush 21
      // de: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // e1: return
      // try (66 -> 70): 71 null
      // try (0 -> 90): 91 null
      // try (0 -> 90): 97 null
   }

   private void verifyCertificateDomainName(X509Certificate certificate, X509Certificate[] certificatePool) {
      TLSOptionStore globalTLSOptions = TLSOptionStore.getOptions();
      SSLConnectionOptions connectionOptions = this._overrideConnectionOptions != null
         ? this._overrideConnectionOptions
         : globalTLSOptions.getDefaultConnectionOptions();
      SSLCryptoSystemProperties sslCryptoSystemProperties = new SSLCryptoSystemProperties(
         ((ConnectionState)this._recordProtocol.getPendingRead()).getKeyExchangeAlgorithm(), connectionOptions
      );
      int disallowUnmatchedDomainName = 1;
      disallowUnmatchedDomainName = this.roundUpSecurity(disallowUnmatchedDomainName, connectionOptions.disallowUnmatchedDomainName());
      if (disallowUnmatchedDomainName != 1) {
         String recordProtocolRemoteHostName = this._recordProtocol.getRemoteHostName();
         if (recordProtocolRemoteHostName != null && recordProtocolRemoteHostName.length() != 0) {
            String commonName = certificate.getSubject().getCommonName();
            String[] certificateDomainNames;
            if (commonName == null) {
               certificateDomainNames = certificate.getSubjectAltNameStrings(12);
            } else {
               certificateDomainNames = new Object[]{commonName};
            }

            if (certificateDomainNames != null && certificateDomainNames.length != 0) {
               boolean domainNameMatches = false;
               int numCertificateDomainNames = certificateDomainNames.length;
               String currentCertificateDomainName = null;

               for (int i = 0; i < numCertificateDomainNames && !domainNameMatches; i++) {
                  currentCertificateDomainName = certificateDomainNames[i];
                  if (TLSUtilities.domainNameMatches(recordProtocolRemoteHostName, currentCertificateDomainName)) {
                     domainNameMatches = true;
                     break;
                  }

                  String[] acceptableAlternateDomainNames = connectionOptions.getAcceptableDomainNames();
                  if (acceptableAlternateDomainNames != null) {
                     for (int j = 0; j < acceptableAlternateDomainNames.length; j++) {
                        if (TLSUtilities.domainNameMatches(acceptableAlternateDomainNames[j], currentCertificateDomainName)) {
                           domainNameMatches = true;
                           break;
                        }
                     }
                  }
               }

               if (!domainNameMatches) {
                  if (disallowUnmatchedDomainName == 0) {
                     TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)42);
                  }

                  StringBuffer wrongDomainMessageBuffer = (StringBuffer)(new Object(
                     MessageFormat.format(_rb.getString(5), new Object[]{recordProtocolRemoteHostName})
                  ));

                  for (int i = 0; i < numCertificateDomainNames; i++) {
                     wrongDomainMessageBuffer.append(_rb.getString(57));
                     wrongDomainMessageBuffer.append(certificateDomainNames[i]);
                  }

                  while (true) {
                     int choice = BackgroundDialog.getChoice(wrongDomainMessageBuffer.toString(), _rb.getStringArray(17), 1);
                     switch (choice) {
                        case -1:
                        case 1:
                        default:
                           TLSUtilities.sendAlertAndCancel(this._alertProtocol, (byte)46);
                           break;
                        case 0:
                           return;
                        case 2:
                           break;
                        case 3:
                           globalTLSOptions.setPromptForDomainName(false);
                           return;
                     }

                     CertificateUtilities.displayCertificateDetails(
                        certificate, certificatePool, DeviceKeyStore.getInstance(), sslCryptoSystemProperties, false, null
                     );
                  }
               }
            } else {
               if (disallowUnmatchedDomainName == 0) {
                  TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)42);
               }

               while (true) {
                  String noDomainMessage = MessageFormat.format(_rb.getString(1), new Object[]{recordProtocolRemoteHostName});
                  int noDomainChoice = BackgroundDialog.getChoice(noDomainMessage, _rb.getStringArray(17), 1);
                  switch (noDomainChoice) {
                     case -1:
                     case 1:
                     default:
                        TLSUtilities.sendAlertAndCancel(this._alertProtocol, (byte)46);
                        break;
                     case 0:
                        return;
                     case 2:
                        break;
                     case 3:
                        globalTLSOptions.setPromptForDomainName(false);
                        return;
                  }

                  CertificateUtilities.displayCertificateDetails(
                     certificate, certificatePool, DeviceKeyStore.getInstance(), sslCryptoSystemProperties, false, null
                  );
               }
            }
         }
      }
   }

   private void verifyCertificate(Certificate certificate, Certificate[] certificatePool, boolean checkIsStrong) {
      TLSOptionStore globalTLSOptions = TLSOptionStore.getOptions();
      SSLConnectionOptions connectionOptions = this._overrideConnectionOptions != null
         ? this._overrideConnectionOptions
         : globalTLSOptions.getDefaultConnectionOptions();
      SSLCryptoSystemProperties sslCryptoSystemProperties = new SSLCryptoSystemProperties(
         ((ConnectionState)this._recordProtocol.getPendingRead()).getKeyExchangeAlgorithm(), connectionOptions
      );
      Certificate[][][] certificateChains = CertificateUtilities.buildCertificateChains(certificate, certificatePool, DeviceKeyStore.getInstance());
      long[] certificateChainProperties = CertificateChainProperties.getCertificateChainProperties(
         certificateChains, TrustedKeyStore.getInstance(), System.currentTimeMillis(), sslCryptoSystemProperties
      );
      int bestCertificateChainIndex = CertificateChainProperties.selectBestCertificateChain(certificateChainProperties);
      long bestCertificateChainProperties = certificateChainProperties[bestCertificateChainIndex];
      if ((bestCertificateChainProperties & 1024) != 0) {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)44);
      }

      if ((bestCertificateChainProperties & 256) != 0) {
         int disallowExpiredCertificate = ITPolicy.getInteger(28, 6, 2);
         disallowExpiredCertificate = this.roundUpSecurity(disallowExpiredCertificate, connectionOptions.disallowExpiredCertificate());
         if (disallowExpiredCertificate == 0) {
            TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)42);
         } else if (disallowExpiredCertificate == 2) {
            label109:
            while (true) {
               String message = _rb.getString(6);
               int choice = BackgroundDialog.getChoice(message, _rb.getStringArray(3), 1);
               switch (choice) {
                  case 0:
                     break label109;
                  case 2:
                     break;
                  default:
                     TLSUtilities.sendAlertAndCancel(this._alertProtocol, (byte)45);
               }

               CertificateUtilities.displayCertificateDetails(
                  certificate, certificatePool, DeviceKeyStore.getInstance(), sslCryptoSystemProperties, false, null
               );
            }
         }
      }

      if ((bestCertificateChainProperties & 30) != 0) {
         int disallowUntrustedCertificate = ITPolicy.getInteger(28, 2, 1);
         disallowUntrustedCertificate = this.roundUpSecurity(disallowUntrustedCertificate, connectionOptions.disallowUntrustedCertificate());
         if (disallowUntrustedCertificate == 0) {
            TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)46);
         } else if (disallowUntrustedCertificate == 2) {
            label96:
            while (true) {
               String message = _rb.getString(2);
               TrustedKeyStore trustedKeyStore = (TrustedKeyStore)TrustedKeyStore.getInstance();
               int choice;
               if (trustedKeyStore.isAllowed(certificate)) {
                  choice = BackgroundDialog.getChoice(message, _rb.getStringArray(56), 1);
               } else {
                  choice = BackgroundDialog.getChoice(message, _rb.getStringArray(3), 1);
               }

               switch (choice) {
                  case -1:
                  case 1:
                  default:
                     TLSUtilities.sendAlertAndCancel(this._alertProtocol, (byte)46);
                  case 2:
                     CertificateUtilities.displayCertificateDetails(
                        certificate, certificatePool, DeviceKeyStore.getInstance(), sslCryptoSystemProperties, false, null
                     );
                     break;
                  case 0:
                     break label96;
                  case 3:
                     KeyStore keyStore = DeviceKeyStore.getInstance();

                     try {
                        CertificateImporterFactory.importAndTrustCertificate(
                           certificate, null, certificate.getSubjectFriendlyName(), keyStore, keyStore.getTicket()
                        );
                        break label96;
                     } finally {
                        break label96;
                     }
               }
            }
         }
      }

      if (checkIsStrong && (bestCertificateChainProperties & 32) != 0) {
         boolean allowDontAskAgain = (bestCertificateChainProperties & 64) == 0;
         this.promptForWeakness(certificate, certificatePool, certificateChains[bestCertificateChainIndex], sslCryptoSystemProperties, allowDontAskAgain);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void promptForWeakness(
      Certificate certificate,
      Certificate[] certificatePool,
      Certificate[] bestCertificateChain,
      SSLCryptoSystemProperties sslCryptoSystemProperties,
      boolean showDontAskAgain
   ) {
      if (certificate != null && certificatePool != null && bestCertificateChain != null && sslCryptoSystemProperties != null) {
         TLSOptionStore options = TLSOptionStore.getOptions();

         while (true) {
            SimpleChoiceDialog dialog;
            if (showDontAskAgain) {
               dialog = (SimpleChoiceDialog)(new Object(_rb.getString(15), _rb.getStringArray(23), 0, null, 134217728));
            } else {
               dialog = (SimpleChoiceDialog)(new Object(_rb.getString(15), _rb.getStringArray(20), 0, null, 134217728));
            }

            BackgroundDialog.show(dialog);
            int result = dialog.getSelectedIndex();
            switch (result) {
               case -1:
                  break;
               case 0:
               default:
                  return;
               case 1:
                  TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)40);
               case 2:
                  CertificateUtilities.displayCertificateDetails(
                     certificate, certificatePool, DeviceKeyStore.getInstance(), sslCryptoSystemProperties, false, null
                  );
                  break;
               case 3:
                  boolean var13 = false /* VF: Semaphore variable */;

                  try {
                     var13 = true;

                     for (int e = 0; e < bestCertificateChain.length; e++) {
                        PublicKey publicKey = bestCertificateChain[e].getPublicKey();
                        int keySize = publicKey.getCryptoSystem().getBitLength();
                        if (publicKey instanceof Object) {
                           if (keySize < options.getMinimumStrongRSAKeySize()) {
                              options.setMinimumStrongRSAKeySize(keySize);
                           }
                        } else if (publicKey instanceof Object) {
                           if (keySize < options.getMinimumStrongDHKeySize()) {
                              options.setMinimumStrongDHKeySize(keySize);
                           }
                        } else {
                           if (!(publicKey instanceof Object)) {
                              throw new Object();
                           }

                           if (keySize < options.getMinimumStrongDSAKeySize()) {
                              options.setMinimumStrongDSAKeySize(keySize);
                           }
                        }
                     }

                     var13 = false;
                  } finally {
                     if (var13) {
                        TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)40);
                        return;
                     }
                  }

                  return;
            }
         }
      } else {
         throw new Object();
      }
   }

   private int roundUpSecurity(int previousValue, int valueToRoundTo) {
      switch (previousValue) {
         case 0:
         default:
            return previousValue;
         case 1:
            if (valueToRoundTo == 0) {
               return 0;
            } else {
               if (valueToRoundTo == 2) {
                  return 2;
               }

               return 1;
            }
         case 2:
            return valueToRoundTo == 0 ? 0 : 2;
      }
   }

   @Override
   public void serverKeyExchange(DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 003: ldc_w "SSL:<-SKE"
      // 006: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 009: aload 0
      // 00a: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 00d: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getPendingRead ()Ljava/lang/Object;
      // 010: astore 2
      // 011: aload 2
      // 012: invokevirtual net/rim/device/api/crypto/tls/ConnectionState.getKeyExchangeAlgorithm ()B
      // 015: istore 3
      // 016: iload 3
      // 017: tableswitch 61 0 11 1075 61 73 61 61 402 402 402 402 402 1075 402
      // 054: aload 0
      // 055: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 058: bipush 10
      // 05a: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 05d: goto 453
      // 060: aload 0
      // 061: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 064: instanceof java/lang/Object
      // 067: ifne 073
      // 06a: aload 0
      // 06b: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 06e: bipush 47
      // 070: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 073: aload 0
      // 074: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 077: checkcast java/lang/Object
      // 07a: astore 4
      // 07c: aload 4
      // 07e: invokevirtual net/rim/device/api/crypto/RSAPublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem;
      // 081: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 086: sipush 1024
      // 089: if_icmpge 095
      // 08c: aload 0
      // 08d: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 090: bipush 60
      // 092: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 095: aload 1
      // 096: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 099: istore 5
      // 09b: aload 1
      // 09c: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 09f: istore 6
      // 0a1: iload 5
      // 0a3: bipush 8
      // 0a5: ishl
      // 0a6: iload 6
      // 0a8: iadd
      // 0a9: newarray 8
      // 0ab: astore 7
      // 0ad: aload 1
      // 0ae: aload 7
      // 0b0: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 0b3: aload 1
      // 0b4: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 0b7: istore 8
      // 0b9: aload 1
      // 0ba: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 0bd: istore 9
      // 0bf: iload 8
      // 0c1: bipush 8
      // 0c3: ishl
      // 0c4: iload 9
      // 0c6: iadd
      // 0c7: newarray 8
      // 0c9: astore 10
      // 0cb: aload 1
      // 0cc: aload 10
      // 0ce: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 0d1: aload 1
      // 0d2: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedShort ()I
      // 0d5: istore 11
      // 0d7: iload 11
      // 0d9: newarray 8
      // 0db: astore 12
      // 0dd: aload 1
      // 0de: aload 12
      // 0e0: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 0e3: new net/rim/device/api/crypto/tls/ssl30/SSLDigest
      // 0e6: dup
      // 0e7: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLDigest.<init> ()V
      // 0ea: astore 13
      // 0ec: new java/lang/Object
      // 0ef: dup
      // 0f0: aload 4
      // 0f2: aload 13
      // 0f4: aload 12
      // 0f6: bipush 0
      // 0f7: invokespecial net/rim/device/api/crypto/PKCS1SignatureVerifier.<init> (Lnet/rim/device/api/crypto/RSAPublicKey;Lnet/rim/device/api/crypto/Digest;[BI)V
      // 0fa: astore 14
      // 0fc: aload 14
      // 0fe: aload 0
      // 0ff: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 102: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.getClientRandom ()[B
      // 105: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update ([B)V
      // 108: aload 14
      // 10a: aload 0
      // 10b: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 10e: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.getServerRandom ()[B
      // 111: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update ([B)V
      // 114: aload 14
      // 116: iload 5
      // 118: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update (I)V
      // 11b: aload 14
      // 11d: iload 6
      // 11f: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update (I)V
      // 122: aload 14
      // 124: aload 7
      // 126: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update ([B)V
      // 129: aload 14
      // 12b: iload 8
      // 12d: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update (I)V
      // 130: aload 14
      // 132: iload 9
      // 134: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update (I)V
      // 137: aload 14
      // 139: aload 10
      // 13b: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update ([B)V
      // 13e: aload 14
      // 140: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.verify ()Z
      // 143: ifne 14f
      // 146: aload 0
      // 147: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 14a: bipush 51
      // 14c: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 14f: aload 7
      // 151: bipush 0
      // 152: baload
      // 153: ifne 171
      // 156: aload 7
      // 158: arraylength
      // 159: bipush 1
      // 15a: isub
      // 15b: newarray 8
      // 15d: astore 15
      // 15f: aload 7
      // 161: bipush 1
      // 162: aload 15
      // 164: bipush 0
      // 165: aload 7
      // 167: arraylength
      // 168: bipush 1
      // 169: isub
      // 16a: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 16d: aload 15
      // 16f: astore 7
      // 171: aload 0
      // 172: new java/lang/Object
      // 175: dup
      // 176: new java/lang/Object
      // 179: dup
      // 17a: aload 7
      // 17c: arraylength
      // 17d: bipush 8
      // 17f: imul
      // 180: invokespecial net/rim/device/api/crypto/RSACryptoSystem.<init> (I)V
      // 183: aload 10
      // 185: aload 7
      // 187: invokespecial net/rim/device/api/crypto/RSAPublicKey.<init> (Lnet/rim/device/api/crypto/RSACryptoSystem;[B[B)V
      // 18a: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 18d: goto 453
      // 190: astore 4
      // 192: aload 0
      // 193: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 196: bipush 51
      // 198: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 19b: goto 1a9
      // 19e: astore 4
      // 1a0: aload 0
      // 1a1: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 1a4: bipush 50
      // 1a6: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 1a9: aload 1
      // 1aa: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 1ad: istore 4
      // 1af: aload 1
      // 1b0: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 1b3: istore 5
      // 1b5: iload 4
      // 1b7: bipush 8
      // 1b9: ishl
      // 1ba: iload 5
      // 1bc: iadd
      // 1bd: newarray 8
      // 1bf: astore 6
      // 1c1: aload 1
      // 1c2: aload 6
      // 1c4: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 1c7: aload 1
      // 1c8: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 1cb: istore 7
      // 1cd: aload 1
      // 1ce: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 1d1: istore 8
      // 1d3: iload 7
      // 1d5: bipush 8
      // 1d7: ishl
      // 1d8: iload 8
      // 1da: iadd
      // 1db: newarray 8
      // 1dd: astore 9
      // 1df: aload 1
      // 1e0: aload 9
      // 1e2: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 1e5: aload 1
      // 1e6: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 1e9: istore 10
      // 1eb: aload 1
      // 1ec: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 1ef: istore 11
      // 1f1: iload 10
      // 1f3: bipush 8
      // 1f5: ishl
      // 1f6: iload 11
      // 1f8: iadd
      // 1f9: newarray 8
      // 1fb: astore 12
      // 1fd: aload 1
      // 1fe: aload 12
      // 200: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 203: iload 3
      // 204: bipush 6
      // 206: if_icmpeq 212
      // 209: iload 3
      // 20a: bipush 7
      // 20c: if_icmpeq 212
      // 20f: goto 335
      // 212: aconst_null
      // 213: astore 13
      // 215: iload 3
      // 216: bipush 6
      // 218: if_icmpeq 221
      // 21b: iload 3
      // 21c: bipush 7
      // 21e: if_icmpne 23d
      // 221: aload 0
      // 222: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 225: instanceof java/lang/Object
      // 228: ifne 234
      // 22b: aload 0
      // 22c: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 22f: bipush 47
      // 231: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 234: aload 0
      // 235: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 238: checkcast java/lang/Object
      // 23b: astore 13
      // 23d: aload 1
      // 23e: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 241: istore 14
      // 243: aconst_null
      // 244: astore 15
      // 246: iload 14
      // 248: bipush 4
      // 24a: ishr
      // 24b: bipush 17
      // 24d: iand
      // 24e: istore 16
      // 250: iload 16
      // 252: bipush 1
      // 253: if_icmpeq 267
      // 256: iload 14
      // 258: bipush 8
      // 25a: ishl
      // 25b: aload 1
      // 25c: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 25f: iadd
      // 260: newarray 8
      // 262: astore 15
      // 264: goto 27e
      // 267: aload 1
      // 268: aload 1
      // 269: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 26c: bipush 1
      // 26d: isub
      // 26e: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 271: aload 1
      // 272: invokevirtual net/rim/device/api/util/DataBuffer.getLength ()I
      // 275: aload 1
      // 276: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 279: isub
      // 27a: newarray 8
      // 27c: astore 15
      // 27e: aload 1
      // 27f: aload 15
      // 281: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 284: new net/rim/device/api/crypto/asn1/ASN1InputByteArray
      // 287: dup
      // 288: aload 15
      // 28a: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 28d: astore 17
      // 28f: aload 17
      // 291: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.getFieldEndPosition ()I
      // 294: aload 15
      // 296: arraylength
      // 297: if_icmpeq 2a3
      // 29a: aload 0
      // 29b: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 29e: bipush 50
      // 2a0: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 2a3: aload 17
      // 2a5: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 2a8: aload 17
      // 2aa: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readIntegerAsByteArray ()[B
      // 2ad: astore 18
      // 2af: aload 17
      // 2b1: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readIntegerAsByteArray ()[B
      // 2b4: astore 19
      // 2b6: new java/lang/Object
      // 2b9: dup
      // 2ba: aload 13
      // 2bc: aload 18
      // 2be: bipush 0
      // 2bf: aload 19
      // 2c1: bipush 0
      // 2c2: invokespecial net/rim/device/api/crypto/DSASignatureVerifier.<init> (Lnet/rim/device/api/crypto/DSAPublicKey;[BI[BI)V
      // 2c5: astore 20
      // 2c7: aload 20
      // 2c9: aload 0
      // 2ca: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 2cd: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.getClientRandom ()[B
      // 2d0: invokevirtual net/rim/device/api/crypto/DSASignatureVerifier.update ([B)V
      // 2d3: aload 20
      // 2d5: aload 0
      // 2d6: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 2d9: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.getServerRandom ()[B
      // 2dc: invokevirtual net/rim/device/api/crypto/DSASignatureVerifier.update ([B)V
      // 2df: aload 20
      // 2e1: iload 4
      // 2e3: invokevirtual net/rim/device/api/crypto/DSASignatureVerifier.update (I)V
      // 2e6: aload 20
      // 2e8: iload 5
      // 2ea: invokevirtual net/rim/device/api/crypto/DSASignatureVerifier.update (I)V
      // 2ed: aload 20
      // 2ef: aload 6
      // 2f1: invokevirtual net/rim/device/api/crypto/DSASignatureVerifier.update ([B)V
      // 2f4: aload 20
      // 2f6: iload 7
      // 2f8: invokevirtual net/rim/device/api/crypto/DSASignatureVerifier.update (I)V
      // 2fb: aload 20
      // 2fd: iload 8
      // 2ff: invokevirtual net/rim/device/api/crypto/DSASignatureVerifier.update (I)V
      // 302: aload 20
      // 304: aload 9
      // 306: invokevirtual net/rim/device/api/crypto/DSASignatureVerifier.update ([B)V
      // 309: aload 20
      // 30b: iload 10
      // 30d: invokevirtual net/rim/device/api/crypto/DSASignatureVerifier.update (I)V
      // 310: aload 20
      // 312: iload 11
      // 314: invokevirtual net/rim/device/api/crypto/DSASignatureVerifier.update (I)V
      // 317: aload 20
      // 319: aload 12
      // 31b: invokevirtual net/rim/device/api/crypto/DSASignatureVerifier.update ([B)V
      // 31e: aload 20
      // 320: invokevirtual net/rim/device/api/crypto/DSASignatureVerifier.verify ()Z
      // 323: ifeq 329
      // 326: goto 3f3
      // 329: aload 0
      // 32a: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 32d: bipush 51
      // 32f: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 332: goto 3f3
      // 335: iload 3
      // 336: bipush 8
      // 338: if_icmpeq 344
      // 33b: iload 3
      // 33c: bipush 9
      // 33e: if_icmpeq 344
      // 341: goto 3f3
      // 344: aload 0
      // 345: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 348: instanceof java/lang/Object
      // 34b: ifne 357
      // 34e: aload 0
      // 34f: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 352: bipush 47
      // 354: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 357: aload 0
      // 358: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 35b: checkcast java/lang/Object
      // 35e: astore 13
      // 360: aload 1
      // 361: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedShort ()I
      // 364: istore 14
      // 366: iload 14
      // 368: newarray 8
      // 36a: astore 15
      // 36c: aload 1
      // 36d: aload 15
      // 36f: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 372: new net/rim/device/api/crypto/tls/ssl30/SSLDigest
      // 375: dup
      // 376: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLDigest.<init> ()V
      // 379: astore 16
      // 37b: new java/lang/Object
      // 37e: dup
      // 37f: aload 13
      // 381: aload 16
      // 383: aload 15
      // 385: bipush 0
      // 386: invokespecial net/rim/device/api/crypto/PKCS1SignatureVerifier.<init> (Lnet/rim/device/api/crypto/RSAPublicKey;Lnet/rim/device/api/crypto/Digest;[BI)V
      // 389: astore 17
      // 38b: aload 17
      // 38d: aload 0
      // 38e: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 391: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.getClientRandom ()[B
      // 394: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update ([B)V
      // 397: aload 17
      // 399: aload 0
      // 39a: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 39d: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.getServerRandom ()[B
      // 3a0: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update ([B)V
      // 3a3: aload 17
      // 3a5: iload 4
      // 3a7: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update (I)V
      // 3aa: aload 17
      // 3ac: iload 5
      // 3ae: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update (I)V
      // 3b1: aload 17
      // 3b3: aload 6
      // 3b5: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update ([B)V
      // 3b8: aload 17
      // 3ba: iload 7
      // 3bc: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update (I)V
      // 3bf: aload 17
      // 3c1: iload 8
      // 3c3: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update (I)V
      // 3c6: aload 17
      // 3c8: aload 9
      // 3ca: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update ([B)V
      // 3cd: aload 17
      // 3cf: iload 10
      // 3d1: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update (I)V
      // 3d4: aload 17
      // 3d6: iload 11
      // 3d8: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update (I)V
      // 3db: aload 17
      // 3dd: aload 12
      // 3df: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.update ([B)V
      // 3e2: aload 17
      // 3e4: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.verify ()Z
      // 3e7: ifne 3f3
      // 3ea: aload 0
      // 3eb: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 3ee: bipush 51
      // 3f0: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 3f3: aload 0
      // 3f4: new java/lang/Object
      // 3f7: dup
      // 3f8: new java/lang/Object
      // 3fb: dup
      // 3fc: aload 6
      // 3fe: aload 9
      // 400: invokespecial net/rim/device/api/crypto/DHCryptoSystem.<init> ([B[B)V
      // 403: aload 12
      // 405: invokespecial net/rim/device/api/crypto/DHPublicKey.<init> (Lnet/rim/device/api/crypto/DHCryptoSystem;[B)V
      // 408: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 40b: iload 3
      // 40c: bipush 11
      // 40e: if_icmpne 453
      // 411: aload 0
      // 412: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 415: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 41a: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 41f: sipush 1024
      // 422: if_icmple 453
      // 425: aload 0
      // 426: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 429: bipush 60
      // 42b: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 42e: goto 453
      // 431: astore 4
      // 433: aload 0
      // 434: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 437: bipush 51
      // 439: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 43c: goto 44a
      // 43f: astore 4
      // 441: aload 0
      // 442: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 445: bipush 50
      // 447: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 44a: aload 0
      // 44b: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 44e: bipush 47
      // 450: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 453: aload 0
      // 454: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 457: aload 0
      // 458: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 45b: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 460: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 465: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.setKeyExchangeSize (I)V
      // 468: return
      // 469: astore 4
      // 46b: aload 0
      // 46c: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 46f: bipush 80
      // 471: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 474: return
      // try (17 -> 161): 162 null
      // try (17 -> 161): 168 null
      // try (173 -> 472): 473 null
      // try (173 -> 472): 479 null
      // try (488 -> 495): 496 null
   }

   @Override
   public void serverCertificateRequest(DataBuffer buffer) {
      label38:
      try {
         try {
            System.out.println("SSL:->SCR");
            this._types = new byte[buffer.readUnsignedByte()];
            buffer.readFully(this._types);
            int dnLength = buffer.readUnsignedShort();
            this._dn = new Object[0];

            while (dnLength > 0) {
               int length = buffer.readUnsignedShort();
               byte[] temp = new byte[length];
               buffer.readFully(temp);
               int arrayLength = this._dn.length;
               Array.resize(this._dn, arrayLength + 1);
               this._dn[arrayLength] = new X509DistinguishedName(temp);
               dnLength -= length + 2;
            }

            return;
         } catch (ASN1EncodingException var8) {
         }
      } finally {
         break label38;
      }

      TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)50);
   }

   @Override
   public void serverHelloDone(DataBuffer buffer) {
      System.out.println("SSL:<-SHD");
      if (!buffer.eof()) {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)50);
      }
   }

   @Override
   public void clientCertificate() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: invokestatic net/rim/device/cldc/io/ssl/TLSOptionStore.getOptions ()Lnet/rim/device/cldc/io/ssl/TLSOptionStore;
      // 003: astore 1
      // 004: new java/lang/Object
      // 007: dup
      // 008: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 00b: astore 2
      // 00c: aload 2
      // 00d: bipush 11
      // 00f: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 012: aload 2
      // 013: bipush 0
      // 014: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerThreeBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 017: aconst_null
      // 018: astore 3
      // 019: aload 0
      // 01a: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 01d: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getLocalCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate;
      // 020: astore 4
      // 022: aload 4
      // 024: ifnull 059
      // 027: aload 4
      // 029: aload 0
      // 02a: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._types [B
      // 02d: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.checkCertificateType (Lnet/rim/device/api/crypto/certificate/Certificate;[B)Z
      // 030: ifeq 059
      // 033: aload 0
      // 034: aload 0
      // 035: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 038: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getLocalPrivateKey ()Lnet/rim/device/api/crypto/PrivateKey;
      // 03b: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 03e: aload 0
      // 03f: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 042: ifnonnull 048
      // 045: goto 3fc
      // 048: aload 4
      // 04a: invokestatic net/rim/device/api/crypto/keystore/DeviceKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 04d: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.buildCertificateChain (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;)[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 050: astore 3
      // 051: aload 2
      // 052: aload 3
      // 053: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.writeClientCertificate (Lnet/rim/device/api/util/DataBuffer;[Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 056: goto 3fc
      // 059: invokestatic net/rim/device/api/crypto/tls/TLSDeviceOptionStore.getOptions ()Lnet/rim/device/api/crypto/tls/TLSDeviceOptionStore;
      // 05c: astore 5
      // 05e: aconst_null
      // 05f: astore 6
      // 061: new java/lang/Object
      // 064: dup
      // 065: aload 0
      // 066: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 069: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getDomainName ()Ljava/lang/String;
      // 06c: invokespecial net/rim/device/cldc/io/utility/URL.<init> (Ljava/lang/String;)V
      // 06f: astore 7
      // 071: aload 7
      // 073: invokevirtual net/rim/device/cldc/io/utility/URL.getHost ()Ljava/lang/String;
      // 076: astore 6
      // 078: goto 07d
      // 07b: astore 7
      // 07d: invokestatic net/rim/device/api/crypto/keystore/DeviceKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 080: astore 7
      // 082: aload 7
      // 084: new net/rim/device/api/crypto/certificate/SerialNumberIssuerKeyStoreIndex
      // 087: dup
      // 088: invokespecial net/rim/device/api/crypto/certificate/SerialNumberIssuerKeyStoreIndex.<init> ()V
      // 08b: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.addIndex (Lnet/rim/device/api/crypto/keystore/KeyStoreIndex;)Z 2
      // 090: pop
      // 091: bipush 0
      // 092: istore 8
      // 094: bipush 0
      // 095: istore 9
      // 097: bipush 0
      // 098: istore 10
      // 09a: bipush 0
      // 09b: tableswitch 29 -1 2 865 29 130 224
      // 0b8: aload 6
      // 0ba: ifnull 11d
      // 0bd: aload 5
      // 0bf: aload 6
      // 0c1: invokevirtual net/rim/device/api/crypto/tls/TLSDeviceOptionStore.getClientCert (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 0c4: astore 11
      // 0c6: aload 11
      // 0c8: ifnull 11d
      // 0cb: bipush 1
      // 0cc: istore 10
      // 0ce: aload 11
      // 0d0: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 0d5: astore 12
      // 0d7: aload 12
      // 0d9: aload 0
      // 0da: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._types [B
      // 0dd: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.checkCertificateType (Lnet/rim/device/api/crypto/certificate/Certificate;[B)Z
      // 0e0: ifeq 11d
      // 0e3: aload 12
      // 0e5: aload 7
      // 0e7: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.buildCertificateChain (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;)[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 0ea: astore 3
      // 0eb: aload 3
      // 0ec: aload 0
      // 0ed: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._dn [Lnet/rim/device/api/crypto/certificate/DistinguishedName;
      // 0f0: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.serverAcceptsCertChain ([Lnet/rim/device/api/crypto/certificate/Certificate;[Lnet/rim/device/api/crypto/certificate/DistinguishedName;)Z
      // 0f3: ifeq 11d
      // 0f6: aload 12
      // 0f8: aload 0
      // 0f9: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 0fc: aload 0
      // 0fd: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 100: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getPendingWrite ()Ljava/lang/Object;
      // 103: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.checkForDH (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/PublicKey;Lnet/rim/device/api/crypto/tls/ssl30/SSLConnectionState;)Z
      // 106: ifeq 11d
      // 109: aload 0
      // 10a: aload 11
      // 10c: aconst_null
      // 10d: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey; 2
      // 112: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 115: aload 2
      // 116: aload 3
      // 117: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.writeClientCertificate (Lnet/rim/device/api/util/DataBuffer;[Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 11a: goto 3fc
      // 11d: aload 5
      // 11f: invokevirtual net/rim/device/api/crypto/tls/TLSDeviceOptionStore.getDefaultClientCert ()Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 122: astore 11
      // 124: aload 11
      // 126: ifnull 17b
      // 129: bipush 1
      // 12a: istore 9
      // 12c: aload 11
      // 12e: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 133: astore 12
      // 135: aload 12
      // 137: aload 0
      // 138: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._types [B
      // 13b: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.checkCertificateType (Lnet/rim/device/api/crypto/certificate/Certificate;[B)Z
      // 13e: ifeq 17b
      // 141: aload 12
      // 143: aload 7
      // 145: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.buildCertificateChain (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;)[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 148: astore 3
      // 149: aload 3
      // 14a: aload 0
      // 14b: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._dn [Lnet/rim/device/api/crypto/certificate/DistinguishedName;
      // 14e: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.serverAcceptsCertChain ([Lnet/rim/device/api/crypto/certificate/Certificate;[Lnet/rim/device/api/crypto/certificate/DistinguishedName;)Z
      // 151: ifeq 17b
      // 154: aload 12
      // 156: aload 0
      // 157: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 15a: aload 0
      // 15b: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 15e: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getPendingWrite ()Ljava/lang/Object;
      // 161: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.checkForDH (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/PublicKey;Lnet/rim/device/api/crypto/tls/ssl30/SSLConnectionState;)Z
      // 164: ifeq 17b
      // 167: aload 0
      // 168: aload 11
      // 16a: aconst_null
      // 16b: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey; 2
      // 170: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 173: aload 2
      // 174: aload 3
      // 175: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.writeClientCertificate (Lnet/rim/device/api/util/DataBuffer;[Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 178: goto 3fc
      // 17b: bipush 1
      // 17c: istore 8
      // 17e: aload 7
      // 180: new java/lang/Object
      // 183: dup
      // 184: invokespecial net/rim/device/api/crypto/keystore/PrivateKeysKeyStoreIndex.<init> ()V
      // 187: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.addIndex (Lnet/rim/device/api/crypto/keystore/KeyStoreIndex;)Z 2
      // 18c: pop
      // 18d: aload 7
      // 18f: ldc2_w -8376547269562148933
      // 192: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (J)Ljava/util/Enumeration; 3
      // 197: astore 12
      // 199: bipush 0
      // 19a: anewarray 3942
      // 19d: astore 13
      // 19f: bipush 0
      // 1a0: anewarray 3944
      // 1a3: astore 14
      // 1a5: bipush 0
      // 1a6: anewarray 3946
      // 1a9: astore 15
      // 1ab: bipush 0
      // 1ac: multianewarray 3948 1
      // 1b0: astore 16
      // 1b2: bipush 0
      // 1b3: istore 17
      // 1b5: aload 12
      // 1b7: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 1bc: ifne 1c2
      // 1bf: goto 262
      // 1c2: aload 12
      // 1c4: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 1c9: checkcast java/lang/Object
      // 1cc: astore 11
      // 1ce: aload 11
      // 1d0: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isPrivateKeySet ()Z 1
      // 1d5: ifeq 1b5
      // 1d8: aload 11
      // 1da: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 1df: astore 18
      // 1e1: aload 18
      // 1e3: ifnull 1b5
      // 1e6: aload 18
      // 1e8: aload 0
      // 1e9: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._types [B
      // 1ec: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.checkCertificateType (Lnet/rim/device/api/crypto/certificate/Certificate;[B)Z
      // 1ef: ifeq 1b5
      // 1f2: aload 18
      // 1f4: aload 7
      // 1f6: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.buildCertificateChain (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;)[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 1f9: astore 3
      // 1fa: aload 3
      // 1fb: aload 0
      // 1fc: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._dn [Lnet/rim/device/api/crypto/certificate/DistinguishedName;
      // 1ff: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.serverAcceptsCertChain ([Lnet/rim/device/api/crypto/certificate/Certificate;[Lnet/rim/device/api/crypto/certificate/DistinguishedName;)Z
      // 202: ifeq 1b5
      // 205: aload 18
      // 207: aload 0
      // 208: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 20b: aload 0
      // 20c: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 20f: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getPendingWrite ()Ljava/lang/Object;
      // 212: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.checkForDH (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/PublicKey;Lnet/rim/device/api/crypto/tls/ssl30/SSLConnectionState;)Z
      // 215: ifeq 1b5
      // 218: aload 16
      // 21a: iload 17
      // 21c: bipush 1
      // 21d: iadd
      // 21e: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 221: aload 14
      // 223: iload 17
      // 225: bipush 1
      // 226: iadd
      // 227: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 22a: aload 15
      // 22c: iload 17
      // 22e: bipush 1
      // 22f: iadd
      // 230: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 233: aload 13
      // 235: iload 17
      // 237: bipush 1
      // 238: iadd
      // 239: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 23c: aload 16
      // 23e: iload 17
      // 240: aload 3
      // 241: aastore
      // 242: aload 13
      // 244: iload 17
      // 246: aload 11
      // 248: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getLabel ()Ljava/lang/String; 1
      // 24d: aastore
      // 24e: aload 14
      // 250: iload 17
      // 252: aload 11
      // 254: aastore
      // 255: aload 15
      // 257: iload 17
      // 259: aload 18
      // 25b: aastore
      // 25c: iinc 17 1
      // 25f: goto 1b5
      // 262: bipush 0
      // 263: istore 18
      // 265: aload 16
      // 267: arraylength
      // 268: ifne 2c7
      // 26b: aload 0
      // 26c: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._remoteVersion I
      // 26f: sipush 768
      // 272: if_icmpne 281
      // 275: aload 0
      // 276: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 279: bipush 1
      // 27a: bipush 41
      // 27c: invokeinterface net/rim/device/api/crypto/tls/AlertProtocolMethods.sendAlertMessage (BB)V 3
      // 281: aload 1
      // 282: invokevirtual net/rim/device/cldc/io/ssl/TLSOptionStore.getPromptForNoClientCertificate ()Z
      // 285: ifne 28b
      // 288: goto 3cd
      // 28b: getstatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 28e: bipush 16
      // 290: invokevirtual net/rim/device/api/i18n/ResourceBundle.getStringArray (I)[Ljava/lang/String;
      // 293: astore 19
      // 295: getstatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 298: bipush 27
      // 29a: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 29d: aload 19
      // 29f: bipush 0
      // 2a0: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.getChoice (Ljava/lang/String;[Ljava/lang/Object;I)I
      // 2a3: istore 20
      // 2a5: iload 20
      // 2a7: ifne 2ad
      // 2aa: goto 3cd
      // 2ad: iload 20
      // 2af: bipush 1
      // 2b0: if_icmpne 2bf
      // 2b3: aload 0
      // 2b4: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 2b7: bipush 90
      // 2b9: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 2bc: goto 3cd
      // 2bf: aload 1
      // 2c0: bipush 0
      // 2c1: invokevirtual net/rim/device/cldc/io/ssl/TLSOptionStore.setPromptForNoClientCertificate (Z)V
      // 2c4: goto 3cd
      // 2c7: aload 16
      // 2c9: arraylength
      // 2ca: bipush 1
      // 2cb: if_icmpeq 2d1
      // 2ce: goto 354
      // 2d1: iload 8
      // 2d3: ifeq 2eb
      // 2d6: iload 9
      // 2d8: ifne 2eb
      // 2db: aload 14
      // 2dd: bipush 0
      // 2de: aaload
      // 2df: aload 16
      // 2e1: bipush 0
      // 2e2: aaload
      // 2e3: bipush 0
      // 2e4: aaload
      // 2e5: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.promptForDefaultCertificate (Lnet/rim/device/api/crypto/keystore/KeyStoreData;Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 2e8: goto 3cd
      // 2eb: iload 8
      // 2ed: ifeq 307
      // 2f0: iload 10
      // 2f2: ifne 307
      // 2f5: aload 14
      // 2f7: bipush 0
      // 2f8: aaload
      // 2f9: aload 16
      // 2fb: bipush 0
      // 2fc: aaload
      // 2fd: bipush 0
      // 2fe: aaload
      // 2ff: aload 6
      // 301: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.promptForMapping (Lnet/rim/device/api/crypto/keystore/KeyStoreData;Lnet/rim/device/api/crypto/certificate/Certificate;Ljava/lang/String;)V
      // 304: goto 3cd
      // 307: invokestatic net/rim/device/cldc/io/ssl/TLSOptionStore.getOptions ()Lnet/rim/device/cldc/io/ssl/TLSOptionStore;
      // 30a: invokevirtual net/rim/device/cldc/io/ssl/TLSOptionStore.getPromptForCertificateOne ()Z
      // 30d: ifne 313
      // 310: goto 3cd
      // 313: getstatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 316: bipush 22
      // 318: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 31b: bipush 1
      // 31c: anewarray 4168
      // 31f: dup
      // 320: bipush 0
      // 321: aload 0
      // 322: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 325: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getProtocol ()Ljava/lang/String;
      // 328: aastore
      // 329: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 32c: astore 19
      // 32e: aload 19
      // 330: invokestatic net/rim/device/internal/ui/RichTextFieldUtilities.getBoldFormattedRichTextField (Ljava/lang/String;)Lnet/rim/device/api/ui/component/RichTextField;
      // 333: aload 13
      // 335: aload 15
      // 337: aload 7
      // 339: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.selectCertificate (Lnet/rim/device/api/ui/component/RichTextField;[Ljava/lang/String;[Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;)I
      // 33c: istore 18
      // 33e: iload 18
      // 340: bipush -1
      // 342: if_icmpeq 348
      // 345: goto 3cd
      // 348: aload 0
      // 349: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 34c: bipush 90
      // 34e: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 351: goto 3cd
      // 354: aload 1
      // 355: invokevirtual net/rim/device/cldc/io/ssl/TLSOptionStore.getPromptForCertificate ()Z
      // 358: ifeq 386
      // 35b: getstatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 35e: bipush 22
      // 360: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 363: bipush 1
      // 364: anewarray 4230
      // 367: dup
      // 368: bipush 0
      // 369: aload 0
      // 36a: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol;
      // 36d: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getProtocol ()Ljava/lang/String;
      // 370: aastore
      // 371: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 374: astore 19
      // 376: aload 19
      // 378: invokestatic net/rim/device/internal/ui/RichTextFieldUtilities.getBoldFormattedRichTextField (Ljava/lang/String;)Lnet/rim/device/api/ui/component/RichTextField;
      // 37b: aload 13
      // 37d: aload 15
      // 37f: aload 7
      // 381: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.selectCertificate (Lnet/rim/device/api/ui/component/RichTextField;[Ljava/lang/String;[Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;)I
      // 384: istore 18
      // 386: iload 18
      // 388: bipush -1
      // 38a: if_icmpne 396
      // 38d: aload 0
      // 38e: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 391: bipush 90
      // 393: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 396: iload 8
      // 398: ifeq 3b2
      // 39b: iload 9
      // 39d: ifne 3b2
      // 3a0: aload 14
      // 3a2: iload 18
      // 3a4: aaload
      // 3a5: aload 16
      // 3a7: iload 18
      // 3a9: aaload
      // 3aa: bipush 0
      // 3ab: aaload
      // 3ac: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.promptForDefaultCertificate (Lnet/rim/device/api/crypto/keystore/KeyStoreData;Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 3af: goto 3cd
      // 3b2: iload 8
      // 3b4: ifeq 3cd
      // 3b7: iload 10
      // 3b9: ifne 3cd
      // 3bc: aload 14
      // 3be: iload 18
      // 3c0: aaload
      // 3c1: aload 16
      // 3c3: iload 18
      // 3c5: aaload
      // 3c6: bipush 0
      // 3c7: aaload
      // 3c8: aload 6
      // 3ca: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.promptForMapping (Lnet/rim/device/api/crypto/keystore/KeyStoreData;Lnet/rim/device/api/crypto/certificate/Certificate;Ljava/lang/String;)V
      // 3cd: iload 18
      // 3cf: bipush -1
      // 3d1: if_icmpeq 3f7
      // 3d4: aload 14
      // 3d6: arraylength
      // 3d7: iload 18
      // 3d9: if_icmple 3f7
      // 3dc: aload 0
      // 3dd: aload 14
      // 3df: iload 18
      // 3e1: aaload
      // 3e2: aconst_null
      // 3e3: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey; 2
      // 3e8: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 3eb: aload 2
      // 3ec: aload 16
      // 3ee: iload 18
      // 3f0: aaload
      // 3f1: invokestatic net/rim/device/api/crypto/tls/ssl30/SSLHandshakeUtilities.writeClientCertificate (Lnet/rim/device/api/util/DataBuffer;[Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 3f4: goto 3fc
      // 3f7: aload 2
      // 3f8: bipush 0
      // 3f9: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerThreeBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 3fc: aload 0
      // 3fd: aload 2
      // 3fe: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.write (Lnet/rim/device/api/util/DataBuffer;)V
      // 401: aload 0
      // 402: bipush 1
      // 403: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._clientCertificateSent Z
      // 406: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 409: ldc_w "SSL:->CC"
      // 40c: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 40f: return
      // 410: astore 1
      // 411: goto 415
      // 414: astore 1
      // 415: aload 0
      // 416: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 419: bipush 50
      // 41b: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 41e: return
      // try (46 -> 56): 57 null
      // try (0 -> 463): 464 null
      // try (0 -> 463): 466 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void clientKeyExchange() {
      try {
         DataBuffer buffer = (DataBuffer)(new Object());
         buffer.write(16);
         TLSUtilities.writeIntegerThreeBytes(buffer, 0);
         ConnectionState connectionState = (ConnectionState)this._recordProtocol.getPendingWrite();
         byte keyExchangeAlgorithm = connectionState.getKeyExchangeAlgorithm();
         switch (keyExchangeAlgorithm) {
            case 1:
            case 2:
            default:
               byte[] premasterSecret = this.generatePremasterSecret();
               buffer.write(premasterSecret);
               break;
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 11:
               boolean var14 = false /* VF: Semaphore variable */;

               try {
                  var14 = true;
                  if (keyExchangeAlgorithm == 3) {
                     if (this._clientCertificateSent) {
                        var14 = false;
                        break;
                     }

                     if (!(this._publicKey instanceof Object)) {
                        TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)47);
                     }

                     DHKeyPair keyPair = SSLHandshakeUtilities.findDHKeyPair((DHPublicKey)this._publicKey);
                     if (keyPair == null) {
                        TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)80);
                     }

                     byte[] publicKeyData = ((DHPublicKey)keyPair.getPublicKey()).getPublicKeyData();
                     TLSUtilities.writeIntegerTwoBytes(buffer, publicKeyData.length);
                     buffer.write(publicKeyData);
                     this._keyPair = keyPair;
                  } else {
                     if (!(this._publicKey instanceof Object)) {
                        TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)47);
                     }

                     DHPublicKey publicKey = (DHPublicKey)this._publicKey;
                     DHCryptoSystem system = (DHCryptoSystem)publicKey.getCryptoSystem();
                     DHCryptoSystem newSystem = (DHCryptoSystem)(new Object(system.getP(), system.getG()));
                     DHKeyPair keyPair = (DHKeyPair)(new Object(newSystem));
                     byte[] publicKeyData = ((DHPublicKey)keyPair.getPublicKey()).getPublicKeyData();
                     TLSUtilities.writeIntegerTwoBytes(buffer, publicKeyData.length);
                     buffer.write(publicKeyData);
                     this._keyPair = keyPair;
                  }

                  this._premasterSecret = this.generatePremasterSecret();
                  var14 = false;
                  break;
               } finally {
                  if (var14) {
                     TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)51);
                     break;
                  }
               }
            case 4:
               TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)47);
            case 10:
               TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)47);
            case 0:
               TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)47);
         }

         this.write(buffer);
         System.out.println("SSL:->CKE");
      } finally {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)50);
         return;
      }
   }

   @Override
   public void clientCertificateVerify() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: new java/lang/Object
      // 003: dup
      // 004: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 007: astore 1
      // 008: aload 1
      // 009: bipush 15
      // 00b: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 00e: aload 1
      // 00f: bipush 0
      // 010: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerThreeBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 013: aload 0
      // 014: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 017: instanceof java/lang/Object
      // 01a: ifne 020
      // 01d: goto 0fe
      // 020: bipush 48
      // 022: newarray 8
      // 024: astore 2
      // 025: aload 2
      // 026: bipush 54
      // 028: invokestatic net/rim/device/api/util/Arrays.fill ([BB)V
      // 02b: bipush 48
      // 02d: newarray 8
      // 02f: astore 3
      // 030: aload 3
      // 031: bipush 92
      // 033: invokestatic net/rim/device/api/util/Arrays.fill ([BB)V
      // 036: aload 0
      // 037: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifyMD5Hash Lnet/rim/device/api/crypto/MD5Digest;
      // 03a: aload 0
      // 03b: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._masterSecret [B
      // 03e: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 041: aload 0
      // 042: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifyMD5Hash Lnet/rim/device/api/crypto/MD5Digest;
      // 045: aload 2
      // 046: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 049: new java/lang/Object
      // 04c: dup
      // 04d: invokespecial net/rim/device/api/crypto/MD5Digest.<init> ()V
      // 050: astore 4
      // 052: aload 4
      // 054: aload 0
      // 055: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._masterSecret [B
      // 058: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 05b: aload 4
      // 05d: aload 3
      // 05e: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 061: aload 4
      // 063: aload 0
      // 064: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifyMD5Hash Lnet/rim/device/api/crypto/MD5Digest;
      // 067: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ()[B
      // 06a: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 06d: bipush 40
      // 06f: newarray 8
      // 071: astore 2
      // 072: aload 2
      // 073: bipush 54
      // 075: invokestatic net/rim/device/api/util/Arrays.fill ([BB)V
      // 078: bipush 40
      // 07a: newarray 8
      // 07c: astore 3
      // 07d: aload 3
      // 07e: bipush 92
      // 080: invokestatic net/rim/device/api/util/Arrays.fill ([BB)V
      // 083: aload 0
      // 084: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifySHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 087: aload 0
      // 088: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._masterSecret [B
      // 08b: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 08e: aload 0
      // 08f: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifySHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 092: aload 2
      // 093: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 096: new java/lang/Object
      // 099: dup
      // 09a: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 09d: astore 5
      // 09f: aload 5
      // 0a1: aload 0
      // 0a2: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._masterSecret [B
      // 0a5: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 0a8: aload 5
      // 0aa: aload 3
      // 0ab: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 0ae: aload 5
      // 0b0: aload 0
      // 0b1: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifySHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 0b4: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ()[B
      // 0b7: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 0ba: new net/rim/device/api/crypto/tls/ssl30/SSLDigest
      // 0bd: dup
      // 0be: aload 4
      // 0c0: aload 5
      // 0c2: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLDigest.<init> (Lnet/rim/device/api/crypto/MD5Digest;Lnet/rim/device/api/crypto/SHA1Digest;)V
      // 0c5: astore 6
      // 0c7: new java/lang/Object
      // 0ca: dup
      // 0cb: aload 0
      // 0cc: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 0cf: checkcast java/lang/Object
      // 0d2: aload 6
      // 0d4: bipush 0
      // 0d5: invokespecial net/rim/device/api/crypto/PKCS1SignatureSigner.<init> (Lnet/rim/device/api/crypto/RSAPrivateKey;Lnet/rim/device/api/crypto/Digest;Z)V
      // 0d8: astore 7
      // 0da: aload 7
      // 0dc: invokevirtual net/rim/device/api/crypto/PKCS1SignatureSigner.getLength ()I
      // 0df: istore 8
      // 0e1: iload 8
      // 0e3: newarray 8
      // 0e5: astore 9
      // 0e7: aload 7
      // 0e9: aload 9
      // 0eb: bipush 0
      // 0ec: invokevirtual net/rim/device/api/crypto/PKCS1SignatureSigner.sign ([BI)V
      // 0ef: aload 1
      // 0f0: iload 8
      // 0f2: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 0f5: aload 1
      // 0f6: aload 9
      // 0f8: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 0fb: goto 1d6
      // 0fe: aload 0
      // 0ff: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 102: instanceof java/lang/Object
      // 105: ifne 10b
      // 108: goto 1cd
      // 10b: bipush 40
      // 10d: newarray 8
      // 10f: astore 2
      // 110: aload 2
      // 111: bipush 54
      // 113: invokestatic net/rim/device/api/util/Arrays.fill ([BB)V
      // 116: bipush 40
      // 118: newarray 8
      // 11a: astore 3
      // 11b: aload 3
      // 11c: bipush 92
      // 11e: invokestatic net/rim/device/api/util/Arrays.fill ([BB)V
      // 121: aload 0
      // 122: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifySHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 125: aload 0
      // 126: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._masterSecret [B
      // 129: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 12c: aload 0
      // 12d: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifySHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 130: aload 2
      // 131: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 134: new java/lang/Object
      // 137: dup
      // 138: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 13b: astore 4
      // 13d: aload 4
      // 13f: aload 0
      // 140: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._masterSecret [B
      // 143: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 146: aload 4
      // 148: aload 3
      // 149: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 14c: aload 4
      // 14e: aload 0
      // 14f: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifySHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 152: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ()[B
      // 155: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 158: new java/lang/Object
      // 15b: dup
      // 15c: aload 0
      // 15d: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 160: checkcast java/lang/Object
      // 163: aload 4
      // 165: invokespecial net/rim/device/api/crypto/DSASignatureSigner.<init> (Lnet/rim/device/api/crypto/DSAPrivateKey;Lnet/rim/device/api/crypto/Digest;)V
      // 168: astore 5
      // 16a: aload 5
      // 16c: invokevirtual net/rim/device/api/crypto/DSASignatureSigner.getRLength ()I
      // 16f: istore 6
      // 171: aload 5
      // 173: invokevirtual net/rim/device/api/crypto/DSASignatureSigner.getSLength ()I
      // 176: istore 7
      // 178: iload 6
      // 17a: newarray 8
      // 17c: astore 8
      // 17e: iload 7
      // 180: newarray 8
      // 182: astore 9
      // 184: aload 5
      // 186: aload 8
      // 188: bipush 0
      // 189: aload 9
      // 18b: bipush 0
      // 18c: invokevirtual net/rim/device/api/crypto/DSASignatureSigner.sign ([BI[BI)V
      // 18f: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 192: dup
      // 193: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 196: astore 10
      // 198: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 19b: dup
      // 19c: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 19f: astore 11
      // 1a1: aload 11
      // 1a3: aload 8
      // 1a5: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger ([B)V
      // 1a8: aload 11
      // 1aa: aload 9
      // 1ac: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger ([B)V
      // 1af: aload 10
      // 1b1: aload 11
      // 1b3: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 1b6: aload 10
      // 1b8: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 1bb: astore 12
      // 1bd: aload 1
      // 1be: aload 12
      // 1c0: arraylength
      // 1c1: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 1c4: aload 1
      // 1c5: aload 12
      // 1c7: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 1ca: goto 1d6
      // 1cd: aload 0
      // 1ce: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 1d1: bipush 47
      // 1d3: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 1d6: aload 0
      // 1d7: aload 1
      // 1d8: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.write (Lnet/rim/device/api/util/DataBuffer;)V
      // 1db: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 1de: ldc_w "SSL:->CV"
      // 1e1: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 1e4: return
      // 1e5: astore 1
      // 1e6: aload 0
      // 1e7: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 1ea: bipush 51
      // 1ec: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 1ef: return
      // 1f0: astore 1
      // 1f1: aload 0
      // 1f2: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 1f5: bipush 50
      // 1f7: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 1fa: return
      // try (0 -> 227): 228 null
      // try (0 -> 227): 234 null
   }

   @Override
   public void changeCipherSpec(DataBuffer buffer) {
      try {
         if (buffer != null) {
            TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)10);
         }

         System.out.println("SSL:->CCS");
         this._recordProtocol.getChangeCipherSpecProtocol().sendChangeCipherSpecMessage();
         this._recordProtocol.changeWriteCipherSpec();
      } finally {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)50);
         return;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void finished(DataBuffer buffer) {
      MD5Digest md5;
      SHA1Digest sha1;
      byte[] message;
      if (this._recordProtocol.getClientMode()) {
         if (buffer == null) {
            md5 = this._clientMD5Hash;
            sha1 = this._clientSHAHash;
            message = SENDER_CLIENT;
         } else {
            md5 = this._serverMD5Hash;
            sha1 = this._serverSHAHash;
            message = SENDER_SERVER;
         }
      } else if (buffer == null) {
         md5 = this._serverMD5Hash;
         sha1 = this._serverSHAHash;
         message = SENDER_SERVER;
      } else {
         md5 = this._clientMD5Hash;
         sha1 = this._clientSHAHash;
         message = SENDER_CLIENT;
      }

      boolean var14 = false /* VF: Semaphore variable */;

      try {
         var14 = true;
         if (buffer != null) {
            System.out.println("SSL:<-F");
            MD5Digest outerMD5 = (MD5Digest)(new Object());
            byte[] pad1 = new byte[48];
            byte[] pad2 = new byte[48];
            Arrays.fill(pad1, (byte)54);
            Arrays.fill(pad2, (byte)92);
            outerMD5.update(this._masterSecret);
            outerMD5.update(pad2);
            md5.update(message);
            md5.update(this._masterSecret);
            md5.update(pad1);
            outerMD5.update(md5.getDigest());
            byte[] ourMD5 = outerMD5.getDigest();
            SHA1Digest outerSHA = (SHA1Digest)(new Object());
            pad1 = new byte[40];
            pad2 = new byte[40];
            Arrays.fill(pad1, (byte)54);
            Arrays.fill(pad2, (byte)92);
            outerSHA.update(this._masterSecret);
            outerSHA.update(pad2);
            sha1.update(message);
            sha1.update(this._masterSecret);
            sha1.update(pad1);
            outerSHA.update(sha1.getDigest());
            byte[] ourSHA = outerSHA.getDigest();
            byte[] serverMD5 = new byte[16];
            buffer.readFully(serverMD5);
            byte[] serverSHA = new byte[20];
            buffer.readFully(serverSHA);
            if (!Arrays.equals(serverMD5, ourMD5) || !Arrays.equals(serverSHA, ourSHA)) {
               TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)51);
               return;
            }

            var14 = false;
         } else {
            buffer = (DataBuffer)(new Object());
            buffer.write(20);
            TLSUtilities.writeIntegerThreeBytes(buffer, 0);
            MD5Digest outerMD5 = (MD5Digest)(new Object());
            byte[] pad1 = new byte[48];
            byte[] pad2 = new byte[48];
            Arrays.fill(pad1, (byte)54);
            Arrays.fill(pad2, (byte)92);
            outerMD5.update(this._masterSecret);
            outerMD5.update(pad2);
            md5.update(message);
            md5.update(this._masterSecret);
            md5.update(pad1);
            outerMD5.update(md5.getDigest());
            byte[] outputBytes = outerMD5.getDigest();
            buffer.write(outputBytes);
            SHA1Digest outerSHA = (SHA1Digest)(new Object());
            pad1 = new byte[40];
            pad2 = new byte[40];
            Arrays.fill(pad1, (byte)54);
            Arrays.fill(pad2, (byte)92);
            outerSHA.update(this._masterSecret);
            outerSHA.update(pad2);
            sha1.update(message);
            sha1.update(this._masterSecret);
            sha1.update(pad1);
            outerSHA.update(sha1.getDigest());
            outputBytes = outerSHA.getDigest();
            buffer.write(outputBytes);
            this.write(buffer);
            System.out.println("SSL:->F");
            var14 = false;
         }
      } finally {
         if (var14) {
            TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)50);
            return;
         }
      }
   }

   @Override
   public KeyMaterial generateKeyMaterial(byte[] masterSecret, byte[] clientRandom, byte[] serverRandom) {
      try {
         ConnectionState read = (ConnectionState)this._recordProtocol.getPendingRead();
         ConnectionState write = (ConnectionState)this._recordProtocol.getPendingWrite();
         SSLPRF prf = new SSLPRF(masterSecret, serverRandom, clientRandom);
         HMACKey writeHMACKey = (HMACKey)(new Object(prf.getBytes(write.getHashSize())));
         HMACKey readHMACKey = (HMACKey)(new Object(prf.getBytes(read.getHashSize())));
         byte[] writeKeyData = new byte[write.getKeySize()];
         SymmetricKey writeKey = TLSUtilities.getKey(write, prf, writeKeyData, true);
         byte[] readKeyData = new byte[read.getKeySize()];
         SymmetricKey readKey = TLSUtilities.getKey(read, prf, readKeyData, true);
         InitializationVector writeIV = null;
         if (write.getCipherType() == 2) {
            writeIV = (InitializationVector)(new Object(prf.getBytes(write.getIVSize())));
         }

         InitializationVector readIV = null;
         if (read.getCipherType() == 2) {
            readIV = (InitializationVector)(new Object(prf.getBytes(read.getIVSize())));
         }

         if (write.getIsExportable()) {
            MD5Digest exportClientWriteKey = (MD5Digest)(new Object());
            exportClientWriteKey.update(writeKeyData);
            exportClientWriteKey.update(clientRandom);
            exportClientWriteKey.update(serverRandom);
            byte[] temp = new byte[16];
            exportClientWriteKey.getDigest(temp, 0);
            int keyLength = write.getKeyMaterialLength();
            writeKey = SymmetricKeyFactory.getInstance(
               ((StringBuffer)(new Object())).append(write.getBulkCipherAlgorithm()).append('_').append(keyLength << 3).toString(), temp, 0, keyLength
            );
            if (write.getCipherType() == 2) {
               MD5Digest exportClientIV = (MD5Digest)(new Object());
               exportClientIV.update(clientRandom);
               exportClientIV.update(serverRandom);
               exportClientIV.getDigest(temp, 0);
               writeIV = (InitializationVector)(new Object(temp, 0, write.getIVSize()));
            }
         }

         if (read.getIsExportable()) {
            MD5Digest exportServerWriteKey = (MD5Digest)(new Object());
            exportServerWriteKey.update(readKeyData);
            exportServerWriteKey.update(serverRandom);
            exportServerWriteKey.update(clientRandom);
            byte[] temp = new byte[16];
            exportServerWriteKey.getDigest(temp, 0);
            int keyLength = read.getKeyMaterialLength();
            readKey = SymmetricKeyFactory.getInstance(
               ((StringBuffer)(new Object())).append(read.getBulkCipherAlgorithm()).append('_').append(keyLength << 3).toString(), temp, 0, keyLength
            );
            if (read.getCipherType() == 2) {
               MD5Digest exportServerIV = (MD5Digest)(new Object());
               exportServerIV.update(serverRandom);
               exportServerIV.update(clientRandom);
               exportServerIV.getDigest(temp, 0);
               readIV = (InitializationVector)(new Object(temp, 0, read.getIVSize()));
            }
         }

         return (KeyMaterial)(new Object(writeKey, readKey, writeHMACKey, readHMACKey, writeIV, readIV));
      } finally {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)51);
         return null;
      }
   }

   public byte[] generatePremasterSecret() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 04: instanceof java/lang/Object
      // 07: ifeq 8c
      // 0a: new java/lang/Object
      // 0d: dup
      // 0e: aload 0
      // 0f: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 12: checkcast java/lang/Object
      // 15: invokespecial net/rim/device/api/crypto/RSAEncryptorEngine.<init> (Lnet/rim/device/api/crypto/RSAPublicKey;)V
      // 18: astore 1
      // 19: new java/lang/Object
      // 1c: dup
      // 1d: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 20: astore 2
      // 21: new java/lang/Object
      // 24: dup
      // 25: new java/lang/Object
      // 28: dup
      // 29: aload 1
      // 2a: invokespecial net/rim/device/api/crypto/PKCS1FormatterEngine.<init> (Lnet/rim/device/api/crypto/PublicKeyEncryptorEngine;)V
      // 2d: aload 2
      // 2e: invokespecial net/rim/device/api/crypto/BlockEncryptor.<init> (Lnet/rim/device/api/crypto/BlockFormatterEngine;Ljava/io/OutputStream;)V
      // 31: astore 3
      // 32: aload 0
      // 33: bipush 48
      // 35: newarray 8
      // 37: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 3a: aload 0
      // 3b: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.getLocalVersion ()I
      // 3e: istore 4
      // 40: aload 0
      // 41: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 44: bipush 0
      // 45: iload 4
      // 47: bipush 8
      // 49: ishr
      // 4a: i2b
      // 4b: bastore
      // 4c: aload 0
      // 4d: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 50: bipush 1
      // 51: iload 4
      // 53: i2b
      // 54: bastore
      // 55: aload 0
      // 56: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 59: bipush 2
      // 5b: bipush 46
      // 5d: invokestatic net/rim/device/api/crypto/RandomSource.getBytes ([BII)V
      // 60: aload 0
      // 61: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 64: arraylength
      // 65: aload 1
      // 66: invokevirtual net/rim/device/api/crypto/RSAEncryptorEngine.getBlockLength ()I
      // 69: if_icmple 75
      // 6c: aload 0
      // 6d: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 70: bipush 40
      // 72: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 75: aload 3
      // 76: aload 0
      // 77: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 7a: bipush 0
      // 7b: aload 0
      // 7c: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 7f: arraylength
      // 80: invokevirtual net/rim/device/api/crypto/BlockEncryptor.write ([BII)V
      // 83: aload 3
      // 84: invokevirtual net/rim/device/api/crypto/BlockEncryptor.close ()V
      // 87: aload 2
      // 88: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 8b: areturn
      // 8c: aload 0
      // 8d: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 90: instanceof java/lang/Object
      // 93: ifeq bf
      // 96: aload 0
      // 97: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._keyPair Lnet/rim/device/api/crypto/KeyPair;
      // 9a: instanceof java/lang/Object
      // 9d: ifne a9
      // a0: aload 0
      // a1: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // a4: bipush 47
      // a6: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // a9: aload 0
      // aa: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._keyPair Lnet/rim/device/api/crypto/KeyPair;
      // ad: invokevirtual net/rim/device/api/crypto/KeyPair.getPrivateKey ()Lnet/rim/device/api/crypto/PrivateKey;
      // b0: checkcast java/lang/Object
      // b3: aload 0
      // b4: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // b7: checkcast java/lang/Object
      // ba: bipush 0
      // bb: invokestatic net/rim/device/api/crypto/DHKeyAgreement.generateSharedSecret (Lnet/rim/device/api/crypto/DHPrivateKey;Lnet/rim/device/api/crypto/DHPublicKey;Z)[B
      // be: areturn
      // bf: aload 0
      // c0: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // c3: bipush 43
      // c5: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // c8: aconst_null
      // c9: areturn
      // ca: astore 1
      // cb: aload 0
      // cc: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // cf: bipush 51
      // d1: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // d4: aconst_null
      // d5: areturn
      // d6: astore 1
      // d7: aload 0
      // d8: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // db: bipush 50
      // dd: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // e0: aconst_null
      // e1: areturn
      // try (0 -> 72): 101 null
      // try (73 -> 94): 101 null
      // try (95 -> 99): 101 null
      // try (0 -> 72): 108 null
      // try (73 -> 94): 108 null
      // try (95 -> 99): 108 null
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public byte[] generateMasterSecret(byte[] preMasterSecret, byte[] clientRandom, byte[] serverRandom) {
      try {
         return new SSLPRF(preMasterSecret, clientRandom, serverRandom).getBytes(48);
      } catch (Throwable var6) {
         throw new Object(e.toString());
      }
   }

   public void updateConnectionState() {
      byte[] clientRandom = this._recordProtocol.getClientRandom();
      byte[] serverRandom = this._recordProtocol.getServerRandom();
      if (!this._resumption) {
         this._masterSecret = this.generateMasterSecret(this._premasterSecret, clientRandom, serverRandom);
      }

      KeyMaterial keyMaterial = this.generateKeyMaterial(this._masterSecret, clientRandom, serverRandom);
      this._premasterSecret = null;
      SSLConnectionState writeState = (SSLConnectionState)this._recordProtocol.getPendingWrite();
      SSLConnectionState readState = (SSLConnectionState)this._recordProtocol.getPendingRead();
      this._factory.updateReadConnectionState(readState, keyMaterial, this._recordProtocol.getUnderlyingInputStream());
      this._factory.updateWriteConnectionState(writeState, keyMaterial, this._recordProtocol.getUnderlyingOutputStream());
      this._recordProtocol.setPendingWrite(writeState);
      this._recordProtocol.setPendingRead(readState);
   }

   @Override
   public byte[] getCipherSuites() {
      int[] cipherSuites = SSLCipherSuites.getPriority();
      int length = cipherSuites.length * 2;
      byte[] output = new byte[length];
      int i = 0;

      for (int j = 0; i < length; j++) {
         output[i] = (byte)(cipherSuites[j] >> 8);
         output[i + 1] = (byte)cipherSuites[j];
         i += 2;
      }

      return output;
   }

   public int[] getRawCipherSuites() {
      return SSLCipherSuites.getPriority();
   }

   @Override
   public void connect() {
      boolean certificateRequest = false;
      boolean serverHelloDone = false;
      this.clientHello();

      do {
         DataBuffer[] buffer = new Object[1];
         int type = this.read(buffer);
         switch (type) {
            case 2:
               this.serverHello(buffer[0]);
               break;
            case 11:
               this.serverCertificate(buffer[0]);
               break;
            case 12:
               this.serverKeyExchange(buffer[0]);
               break;
            case 13:
               this.serverCertificateRequest(buffer[0]);
               certificateRequest = true;
               break;
            case 14:
               this.serverHelloDone(buffer[0]);
               serverHelloDone = true;
               break;
            default:
               TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)10);
         }
      } while (!serverHelloDone && !this._resumption);

      if (certificateRequest) {
         this.clientCertificate();
      }

      if (!this._resumption) {
         this.clientKeyExchange();
      }

      this.updateConnectionState();
      if (certificateRequest) {
         this.clientCertificateVerify();
      }

      if (!this._resumption) {
         this.changeCipherSpec(null);
         this.finished(null);
      }

      DataBuffer[] var6 = new Object[1];
      int type = this.read(var6);
      if (type == 20) {
         this.finished((DataBuffer)var6[0]);
      } else {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)10);
      }

      if (this._resumption) {
         this.changeCipherSpec(null);
         this.finished(null);
      }

      if (!this._resumption) {
         SessionResumption resumption = (SessionResumption)(new Object());
         resumption.addSession(
            this._recordProtocol.getDomainName(),
            this._recordProtocol.getProtocol(),
            this._sessionID,
            this._masterSecret,
            this._cipherSuite,
            this._recordProtocol.getRIMServerCertificate(),
            this._recordProtocol.getCertificatePool()
         );
      }

      this._recordProtocol.setState(1);
      this._clientSHAHash.reset();
      this._clientMD5Hash.reset();
      this._serverSHAHash.reset();
      this._serverMD5Hash.reset();
      this._verifySHAHash.reset();
      this._verifyMD5Hash.reset();
      this._masterSecret = null;
      System.out.println("SSL: Ok");
   }

   @Override
   public int getLocalVersion() {
      return 768;
   }

   public void checkCipherSuites(int cipherSuite, byte[] cipherSuites) {
      int length = cipherSuites.length;
      byte cipherSuite1 = (byte)(cipherSuite >> 8);
      byte cipherSuite2 = (byte)cipherSuite;

      for (int i = 0; i < length; i++) {
         if (cipherSuites[i] == cipherSuite1 && cipherSuites[i + 1] == cipherSuite2) {
            return;
         }
      }

      byte[] newCipherSuites = new byte[length + 2];
      newCipherSuites[0] = cipherSuite1;
      newCipherSuites[1] = cipherSuite2;
      System.arraycopy(cipherSuites, 0, newCipherSuites, 2, length);
   }

   public void updateHash(byte[] data) {
      this._clientSHAHash.update(data);
      this._clientMD5Hash.update(data);
      this._serverSHAHash.update(data);
      this._serverMD5Hash.update(data);
      this._verifySHAHash.update(data);
      this._verifyMD5Hash.update(data);
   }

   public int read(DataBuffer[] buffer) {
      if (this._dataBuffer.getPosition() == this._dataBuffer.getLength()) {
         int type = this._recordProtocol.read(this._dataBuffer);
         if (type != 22) {
            TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)10);
         }

         this._dataBuffer.rewind();
         int tempType = this._dataBuffer.readUnsignedByte();
         if (!this._resumption) {
            if (tempType != 20) {
               this._dataBuffer.rewind();
               byte[] hashBytes = new byte[this._dataBuffer.getLength()];
               this._dataBuffer.readFully(hashBytes);
               this.updateHash(hashBytes);
            }
         } else {
            this._dataBuffer.rewind();
            byte[] hashBytes = new byte[this._dataBuffer.getLength()];
            this._dataBuffer.readFully(hashBytes);
            if (tempType == 20) {
               this._clientMD5Hash.update(hashBytes);
               this._clientSHAHash.update(hashBytes);
            } else {
               this.updateHash(hashBytes);
            }
         }

         this._dataBuffer.rewind();
      }

      int type = this._dataBuffer.readUnsignedByte();
      buffer[0] = (DataBuffer)(new Object(this._dataBuffer, TLSUtilities.readIntegerThreeBytes(this._dataBuffer)));
      return type;
   }

   public void write(DataBuffer buffer) {
      int length = buffer.getLength() - 4;
      buffer.rewind();
      buffer.setPosition(1);
      TLSUtilities.writeIntegerThreeBytes(buffer, length);
      buffer.rewind();
      byte[] hashBytes = new byte[buffer.getLength()];
      buffer.readFully(hashBytes);
      this.updateHash(hashBytes);
      buffer.rewind();
      this._recordProtocol.write(22, buffer);
   }

   void setOverrideConnectionOptions(SSLConnectionOptions connectionOptions) {
      TLSOptionStore defaultOptions = TLSOptionStore.getOptions();
      if (defaultOptions.getPromptForDomainName() && connectionOptions.disallowUnmatchedDomainName() == 1
         || defaultOptions.getPromptForCertificateTrust() && connectionOptions.disallowUntrustedCertificate() == 1
         || connectionOptions.disallowExpiredCertificate() == 1
         || defaultOptions.getMinimumStrongRSAKeySize() > connectionOptions.getMinimumStrongRSAKeySize()
         || defaultOptions.getMinimumStrongDSAKeySize() > connectionOptions.getMinimumStrongDSAKeySize()
         || defaultOptions.getMinimumStrongDHKeySize() > connectionOptions.getMinimumStrongDHKeySize()
         || defaultOptions.getMinimumStrongECKeySize() > connectionOptions.getMinimumStrongECKeySize()) {
         ControlledAccess.assertRRISignatures(true);
         Class[] callingClasses = TraceBack.getCallingClasses();
         if (callingClasses == null
            || callingClasses.length < 4
            || !StringUtilities.strEqual(callingClasses[0].getName(), "net.rim.device.api.crypto.tls.ssl30.SSLHandshakeProtocol")
            || !StringUtilities.strEqual(callingClasses[1].getName(), "net.rim.device.api.crypto.tls.ssl30.SSLRecordProtocol")
            || !StringUtilities.strEqual(callingClasses[2].getName(), "net.rim.device.api.crypto.tls.ssl30.SSL30Connection")
               && !StringUtilities.strEqual(callingClasses[2].getName(), "net.rim.device.api.crypto.tls.tls10.TLS10Connection")
            || !StringUtilities.strEqual(callingClasses[3].getName(), "net.rim.device.apps.internal.options.items.GetSecurityKeyThread")) {
            throw new Object();
         }
      }

      if (connectionOptions.getAcceptableDomainNames() != null) {
         ControlledAccess.assertRRISignatures(true);
         Class[] callingClasses = TraceBack.getCallingClasses();
         if (callingClasses == null
            || callingClasses.length < 8
            || !StringUtilities.strEqual(callingClasses[0].getName(), "net.rim.device.api.crypto.tls.ssl30.SSLHandshakeProtocol")
            || !StringUtilities.strEqual(callingClasses[1].getName(), "net.rim.device.api.crypto.tls.ssl30.SSLRecordProtocol")
            || !StringUtilities.strEqual(callingClasses[2].getName(), "net.rim.device.api.crypto.tls.ssl30.SSL30Connection")
               && !StringUtilities.strEqual(callingClasses[2].getName(), "net.rim.device.api.crypto.tls.tls10.TLS10Connection")
            || !StringUtilities.strEqual(callingClasses[3].getName(), "net.rim.device.cldc.io.srp.SrpBridgeConnection")
            || !StringUtilities.strEqual(callingClasses[4].getName(), "net.rim.device.cldc.io.srp.SrpBridgeConnection")
            || !StringUtilities.strEqual(callingClasses[5].getName(), "net.rim.device.cldc.io.srp.SrpConfiguration")
            || !StringUtilities.strEqual(callingClasses[6].getName(), "net.rim.device.cldc.io.srp.SrpSession")
            || !StringUtilities.strEqual(callingClasses[7].getName(), "net.rim.device.cldc.io.srp.SrpSession")) {
            throw new Object();
         }
      }

      this._overrideConnectionOptions = connectionOptions;
   }
}
