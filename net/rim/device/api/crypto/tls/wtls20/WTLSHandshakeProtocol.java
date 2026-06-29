package net.rim.device.api.crypto.tls.wtls20;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import net.rim.device.api.crypto.InvalidCryptoSystemException;
import net.rim.device.api.crypto.KeyPair;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSAPublicKey;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateParsingException;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.certificate.wtls.WTLSCertificate;
import net.rim.device.api.crypto.certificate.wtls.WTLSDistinguishedName;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.PrivateKeysKeyStoreIndex;
import net.rim.device.api.crypto.tls.HandshakeProtocol;
import net.rim.device.api.crypto.tls.KeyMaterial;
import net.rim.device.api.crypto.tls.SessionResumption;
import net.rim.device.api.crypto.tls.TLSAlertException;
import net.rim.device.api.crypto.tls.TLSUtilities;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.vm.Array;

final class WTLSHandshakeProtocol extends HandshakeProtocol {
   private byte[] _premasterSecret;
   private byte[] _masterSecret;
   protected SHA1Digest _clientVerifySHAHash = (SHA1Digest)(new Object());
   protected MD5Digest _clientVerifyMD5Hash = (MD5Digest)(new Object());
   protected SHA1Digest _clientSHAHash = (SHA1Digest)(new Object());
   protected MD5Digest _clientMD5Hash = (MD5Digest)(new Object());
   protected SHA1Digest _serverSHAHash = (SHA1Digest)(new Object());
   protected MD5Digest _serverMD5Hash = (MD5Digest)(new Object());
   private boolean _resumption;
   private byte[] _sessionID;
   private byte[][][] _clientKeyIds;
   private boolean _useClientIdInfo;
   private boolean _deleteSessionInfo = true;
   private int _cipherSuite;
   private int _ipv4Address;
   private boolean _wap20Conformance;
   private int _clientIdType;
   private String _clientIdValue;
   protected PublicKey _publicKey;
   protected DataBuffer _dataBuffer = (DataBuffer)(new Object());
   protected KeyPair _keyPair;
   protected Certificate[] _certificates;
   protected KeyStoreData _keyStoreData;
   private WTLSRecordProtocol _recordProtocol;
   private WTLSAlertProtocol _alertProtocol;
   private byte[] _cipherSuites;
   private String _apn;
   private static final boolean DEBUG;
   private static final int MAX_RETRIES;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(3628098008949486223L, "net.rim.device.internal.resource.crypto.WTLS");

   public WTLSHandshakeProtocol(WTLSRecordProtocol recordProtocol, String apn, int flags, int clientIdType, String clientIdValue) {
      this._recordProtocol = recordProtocol;
      this._apn = apn;
      this._useClientIdInfo = (flags & 1) != 0;
      this._deleteSessionInfo = (flags & 2) != 0;
      this._wap20Conformance = (flags & 4) != 0;
      this._clientIdType = clientIdType;
      this._clientIdValue = clientIdValue;
      this._alertProtocol = (WTLSAlertProtocol)recordProtocol.getAlertProtocol();
   }

   @Override
   public final void helloRequest(DataBuffer param1) {
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
      // 0a: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 0d: bipush 10
      // 0f: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 12: aload 1
      // 13: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.readIntegerThreeBytes (Lnet/rim/device/api/util/DataBuffer;)I
      // 16: ifeq 22
      // 19: aload 0
      // 1a: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 1d: bipush 50
      // 1f: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 22: aload 1
      // 23: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 26: aload 1
      // 27: invokevirtual net/rim/device/api/util/DataBuffer.getLength ()I
      // 2a: if_icmpeq 36
      // 2d: aload 0
      // 2e: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 31: bipush 50
      // 33: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 36: aload 0
      // 37: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.connect ()V
      // 3a: return
      // 3b: astore 2
      // 3c: new java/lang/Object
      // 3f: dup
      // 40: aload 2
      // 41: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 44: athrow
      // 45: astore 2
      // 46: new java/lang/Object
      // 49: dup
      // 4a: aload 2
      // 4b: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 4e: athrow
      // try (0 -> 27): 28 null
      // try (0 -> 27): 34 null
   }

   @Override
   public final void clientHello() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 004: bipush 1
      // 005: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.updateStatus (I)V
      // 008: new java/lang/Object
      // 00b: dup
      // 00c: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 00f: astore 1
      // 010: aload 1
      // 011: bipush 1
      // 012: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 015: aload 1
      // 016: bipush 0
      // 017: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 01a: aload 0
      // 01b: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.getLocalVersion ()I
      // 01e: istore 2
      // 01f: aload 1
      // 020: iload 2
      // 021: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 024: aload 0
      // 025: bipush 12
      // 027: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.getRandom (I)Lnet/rim/device/api/crypto/tls/RandomStructure;
      // 02a: astore 3
      // 02b: aload 1
      // 02c: aload 3
      // 02d: invokevirtual net/rim/device/api/crypto/tls/RandomStructure.getRandomBytes ()[B
      // 030: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 033: aload 0
      // 034: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 037: aload 3
      // 038: invokevirtual net/rim/device/api/crypto/tls/RandomStructure.getRandomBytes ()[B
      // 03b: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setClientRandom ([B)V
      // 03e: aload 0
      // 03f: aload 0
      // 040: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.getCipherSuites ()[B
      // 043: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuites [B
      // 046: invokestatic net/rim/device/api/crypto/tls/wtls20/WTLSOptionStore.getOptions ()Lnet/rim/device/api/crypto/tls/wtls20/WTLSOptionStore;
      // 049: astore 4
      // 04b: aload 4
      // 04d: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSOptionStore.getSessionResumption ()Z
      // 050: ifne 056
      // 053: goto 182
      // 056: new net/rim/device/api/crypto/tls/SessionResumption
      // 059: dup
      // 05a: invokespecial net/rim/device/api/crypto/tls/SessionResumption.<init> ()V
      // 05d: astore 5
      // 05f: aload 0
      // 060: bipush 8
      // 062: newarray 8
      // 064: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._sessionID [B
      // 067: aload 0
      // 068: bipush 20
      // 06a: newarray 8
      // 06c: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._masterSecret [B
      // 06f: aload 0
      // 070: aload 5
      // 072: aload 0
      // 073: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 076: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.getDomainName ()Ljava/lang/String;
      // 079: aload 0
      // 07a: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 07d: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.getProtocol ()Ljava/lang/String;
      // 080: aload 0
      // 081: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._sessionID [B
      // 084: aload 0
      // 085: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._masterSecret [B
      // 088: invokevirtual net/rim/device/api/crypto/tls/SessionResumption.getSession (Ljava/lang/String;Ljava/lang/String;[B[B)I
      // 08b: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 08e: aload 0
      // 08f: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 092: iflt 0fa
      // 095: aload 0
      // 096: aload 0
      // 097: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 09a: aload 0
      // 09b: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuites [B
      // 09e: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.checkCipherSuites (I[B)V
      // 0a1: aload 0
      // 0a2: bipush 1
      // 0a3: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._resumption Z
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 0aa: aload 0
      // 0ab: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._masterSecret [B
      // 0ae: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setMasterSecret ([B)V
      // 0b1: aload 0
      // 0b2: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 0b5: aload 0
      // 0b6: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 0b9: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setCipherSuite (I)V
      // 0bc: aload 5
      // 0be: aload 0
      // 0bf: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 0c2: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.getDomainName ()Ljava/lang/String;
      // 0c5: aload 0
      // 0c6: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 0c9: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.getProtocol ()Ljava/lang/String;
      // 0cc: invokevirtual net/rim/device/api/crypto/tls/SessionResumption.getSessionCertificate (Ljava/lang/String;Ljava/lang/String;)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 0cf: astore 6
      // 0d1: aload 0
      // 0d2: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 0d5: aload 6
      // 0d7: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setCertificate (Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 0da: aload 6
      // 0dc: ifnull 182
      // 0df: aload 0
      // 0e0: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 0e3: aload 6
      // 0e5: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 0ea: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 0ef: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 0f4: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setKeyAgreementSize (I)V
      // 0f7: goto 182
      // 0fa: bipush 2
      // 0fc: istore 6
      // 0fe: invokestatic net/rim/device/api/system/DeviceInfo.isSimulator ()Z
      // 101: ifne 110
      // 104: invokestatic net/rim/device/api/system/RadioInfo.getNetworkType ()I
      // 107: bipush 5
      // 109: if_icmpne 110
      // 10c: bipush 8
      // 10e: istore 6
      // 110: aload 0
      // 111: aload 5
      // 113: iload 6
      // 115: aload 0
      // 116: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 119: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.getDomainName ()Ljava/lang/String;
      // 11c: aload 0
      // 11d: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 120: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.getIPAddress ()I
      // 123: aload 0
      // 124: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 127: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.getPort ()I
      // 12a: aload 0
      // 12b: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._sessionID [B
      // 12e: aload 0
      // 12f: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._masterSecret [B
      // 132: invokevirtual net/rim/device/api/crypto/tls/SessionResumption.getSessionFromPermanentStore (ILjava/lang/String;II[B[B)I
      // 135: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 138: goto 147
      // 13b: astore 7
      // 13d: new java/lang/Object
      // 140: dup
      // 141: aload 7
      // 143: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 146: athrow
      // 147: aload 0
      // 148: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 14b: iflt 178
      // 14e: aload 0
      // 14f: aload 0
      // 150: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 153: aload 0
      // 154: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuites [B
      // 157: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.checkCipherSuites (I[B)V
      // 15a: aload 0
      // 15b: bipush 1
      // 15c: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._resumption Z
      // 15f: aload 0
      // 160: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 163: aload 0
      // 164: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._masterSecret [B
      // 167: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setMasterSecret ([B)V
      // 16a: aload 0
      // 16b: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 16e: aload 0
      // 16f: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 172: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setCipherSuite (I)V
      // 175: goto 182
      // 178: aload 0
      // 179: aconst_null
      // 17a: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._sessionID [B
      // 17d: aload 0
      // 17e: aconst_null
      // 17f: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._masterSecret [B
      // 182: aload 0
      // 183: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._sessionID [B
      // 186: ifnonnull 191
      // 189: aload 1
      // 18a: bipush 0
      // 18b: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 18e: goto 1a2
      // 191: aload 1
      // 192: aload 0
      // 193: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._sessionID [B
      // 196: arraylength
      // 197: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 19a: aload 1
      // 19b: aload 0
      // 19c: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._sessionID [B
      // 19f: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 1a2: aload 0
      // 1a3: invokestatic net/rim/device/api/crypto/tls/wtls20/WTLSCipherSuites.getKeyExchangePriority ()[[[B
      // 1a6: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._clientKeyIds [[[B
      // 1a9: aload 0
      // 1aa: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.getClientIds ()[[B
      // 1ad: astore 5
      // 1af: bipush 0
      // 1b0: istore 6
      // 1b2: bipush 0
      // 1b3: istore 7
      // 1b5: iload 7
      // 1b7: aload 5
      // 1b9: arraylength
      // 1ba: if_icmpge 1ce
      // 1bd: iload 6
      // 1bf: aload 5
      // 1c1: iload 7
      // 1c3: aaload
      // 1c4: arraylength
      // 1c5: iadd
      // 1c6: istore 6
      // 1c8: iinc 7 1
      // 1cb: goto 1b5
      // 1ce: iload 6
      // 1d0: bipush 2
      // 1d2: aload 5
      // 1d4: arraylength
      // 1d5: imul
      // 1d6: iadd
      // 1d7: aload 0
      // 1d8: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._clientKeyIds [[[B
      // 1db: arraylength
      // 1dc: imul
      // 1dd: istore 7
      // 1df: aload 1
      // 1e0: iload 7
      // 1e2: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 1e5: bipush 0
      // 1e6: istore 8
      // 1e8: iload 8
      // 1ea: aload 0
      // 1eb: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._clientKeyIds [[[B
      // 1ee: arraylength
      // 1ef: if_icmpge 21d
      // 1f2: bipush 0
      // 1f3: istore 9
      // 1f5: iload 9
      // 1f7: aload 5
      // 1f9: arraylength
      // 1fa: if_icmpge 217
      // 1fd: aload 1
      // 1fe: aload 0
      // 1ff: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._clientKeyIds [[[B
      // 202: iload 8
      // 204: aaload
      // 205: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 208: aload 1
      // 209: aload 5
      // 20b: iload 9
      // 20d: aaload
      // 20e: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 211: iinc 9 1
      // 214: goto 1f5
      // 217: iinc 8 1
      // 21a: goto 1e8
      // 21d: aload 0
      // 21e: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.getTrustedKeyIds ()[B
      // 221: astore 8
      // 223: aload 1
      // 224: aload 8
      // 226: arraylength
      // 227: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 22a: aload 1
      // 22b: aload 8
      // 22d: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 230: aload 1
      // 231: aload 0
      // 232: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuites [B
      // 235: arraylength
      // 236: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 239: aload 1
      // 23a: aload 0
      // 23b: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuites [B
      // 23e: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 241: aload 0
      // 242: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.getCompressionAlgorithms ()[B
      // 245: astore 9
      // 247: aload 1
      // 248: aload 9
      // 24a: arraylength
      // 24b: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 24e: aload 1
      // 24f: aload 9
      // 251: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 254: aload 1
      // 255: bipush 2
      // 257: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 25a: aload 1
      // 25b: aload 0
      // 25c: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.getKeyRefresh ()B
      // 25f: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 262: aload 0
      // 263: aload 1
      // 264: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.write (Lnet/rim/device/api/util/DataBuffer;)V
      // 267: return
      // 268: astore 1
      // 269: aload 0
      // 26a: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 26d: bipush 50
      // 26f: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 272: return
      // 273: astore 1
      // 274: aload 0
      // 275: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 278: bipush 51
      // 27a: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 27d: return
      // try (123 -> 141): 142 null
      // try (0 -> 297): 298 null
      // try (0 -> 297): 304 null
   }

   @Override
   public final void serverHello(DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 004: bipush 2
      // 006: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.updateStatus (I)V
      // 009: aload 1
      // 00a: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 00d: istore 2
      // 00e: iload 2
      // 00f: aload 0
      // 010: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.getLocalVersion ()I
      // 013: if_icmpeq 01f
      // 016: aload 0
      // 017: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 01a: bipush 70
      // 01c: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 01f: bipush 16
      // 021: newarray 8
      // 023: astore 3
      // 024: aload 1
      // 025: aload 3
      // 026: bipush 0
      // 027: bipush 16
      // 029: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([BII)V
      // 02c: aload 0
      // 02d: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 030: aload 3
      // 031: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setServerRandom ([B)V
      // 034: aload 1
      // 035: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 038: istore 4
      // 03a: iload 4
      // 03c: newarray 8
      // 03e: astore 5
      // 040: aload 1
      // 041: aload 5
      // 043: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 046: aload 0
      // 047: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._resumption Z
      // 04a: ifeq 05e
      // 04d: aload 5
      // 04f: aload 0
      // 050: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._sessionID [B
      // 053: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 056: ifne 05e
      // 059: aload 0
      // 05a: bipush 0
      // 05b: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._resumption Z
      // 05e: aload 0
      // 05f: aload 5
      // 061: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._sessionID [B
      // 064: aload 1
      // 065: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 068: istore 6
      // 06a: aload 0
      // 06b: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._resumption Z
      // 06e: ifne 089
      // 071: iload 6
      // 073: ifeq 080
      // 076: iload 6
      // 078: aload 0
      // 079: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._clientKeyIds [[[B
      // 07c: arraylength
      // 07d: if_icmple 089
      // 080: aload 0
      // 081: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 084: bipush 47
      // 086: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 089: aload 0
      // 08a: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._resumption Z
      // 08d: ifne 0bd
      // 090: aload 0
      // 091: aload 1
      // 092: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedShort ()I
      // 095: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 098: aload 0
      // 099: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuites [B
      // 09c: aload 0
      // 09d: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 0a0: invokestatic net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.checkCipherSuite ([BI)Z
      // 0a3: ifne 0af
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 0aa: bipush 71
      // 0ac: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 0af: aload 0
      // 0b0: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 0b3: aload 0
      // 0b4: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 0b7: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setCipherSuite (I)V
      // 0ba: goto 0c5
      // 0bd: aload 0
      // 0be: aload 1
      // 0bf: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedShort ()I
      // 0c2: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 0c5: invokestatic net/rim/device/api/crypto/tls/wtls20/WTLSOptionStore.getOptions ()Lnet/rim/device/api/crypto/tls/wtls20/WTLSOptionStore;
      // 0c8: astore 7
      // 0ca: aload 0
      // 0cb: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 0ce: bipush 8
      // 0d0: ishr
      // 0d1: i2b
      // 0d2: invokestatic net/rim/device/api/crypto/tls/wtls20/WTLSCipherSuites.export (B)Z
      // 0d5: ifeq 115
      // 0d8: aload 7
      // 0da: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSOptionStore.allowExportCipherSuites ()Z
      // 0dd: ifne 115
      // 0e0: getstatic net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0e3: bipush 9
      // 0e5: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0e8: getstatic net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0eb: bipush 14
      // 0ed: invokevirtual net/rim/device/api/i18n/ResourceBundle.getStringArray (I)[Ljava/lang/String;
      // 0f0: bipush 0
      // 0f1: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.getChoice (Ljava/lang/String;[Ljava/lang/Object;I)I
      // 0f4: istore 8
      // 0f6: iload 8
      // 0f8: bipush 1
      // 0f9: if_icmpne 108
      // 0fc: aload 0
      // 0fd: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 100: bipush 71
      // 102: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 105: goto 115
      // 108: iload 8
      // 10a: bipush 2
      // 10c: if_icmpne 115
      // 10f: aload 7
      // 111: bipush 1
      // 112: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSOptionStore.setAllowExportCipherSuites (Z)V
      // 115: aload 1
      // 116: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 119: istore 8
      // 11b: aload 0
      // 11c: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 11f: invokestatic net/rim/device/api/crypto/tls/wtls20/WTLSCipherSuiteFactory.getConnectionState (I)Lnet/rim/device/api/crypto/tls/wtls20/WTLSConnectionState;
      // 122: astore 9
      // 124: aload 9
      // 126: iload 8
      // 128: i2b
      // 129: invokestatic net/rim/device/api/crypto/tls/wtls20/WTLSCipherSuiteFactory.getCompressionAlgorithm (B)Ljava/lang/String;
      // 12c: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSConnectionState.setCompressionAlgorithm (Ljava/lang/String;)V
      // 12f: aload 0
      // 130: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._resumption Z
      // 133: ifne 158
      // 136: iload 6
      // 138: bipush 1
      // 139: isub
      // 13a: istore 10
      // 13c: aload 9
      // 13e: aload 0
      // 13f: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._clientKeyIds [[[B
      // 142: iload 10
      // 144: aaload
      // 145: bipush 0
      // 146: baload
      // 147: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSConnectionState.setKeyExchangeAlgorithm (B)V
      // 14a: aload 9
      // 14c: aload 0
      // 14d: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._clientKeyIds [[[B
      // 150: iload 10
      // 152: aaload
      // 153: bipush 1
      // 154: baload
      // 155: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSConnectionState.setKeyExchangeParameters (B)V
      // 158: aload 0
      // 159: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 15c: aload 9
      // 15e: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setPendingState (Lnet/rim/device/api/crypto/tls/wtls20/WTLSConnectionState;)V
      // 161: aload 1
      // 162: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 165: istore 10
      // 167: iload 10
      // 169: bipush 2
      // 16b: if_icmpeq 177
      // 16e: aload 0
      // 16f: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 172: bipush 47
      // 174: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 177: aload 1
      // 178: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 17b: istore 11
      // 17d: aload 0
      // 17e: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 181: bipush 1
      // 182: iload 11
      // 184: ishl
      // 185: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setKeyRefresh (I)V
      // 188: return
      // 189: astore 2
      // 18a: goto 18e
      // 18d: astore 2
      // 18e: aload 0
      // 18f: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 192: bipush 50
      // 194: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 197: return
      // try (0 -> 187): 188 null
      // try (0 -> 187): 190 null
   }

   @Override
   public final void serverCertificate(DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 004: bipush 3
      // 006: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.updateStatus (I)V
      // 009: new java/lang/Object
      // 00c: dup
      // 00d: aload 1
      // 00e: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 011: aload 1
      // 012: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 015: aload 1
      // 016: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 019: invokespecial java/io/ByteArrayInputStream.<init> ([BII)V
      // 01c: astore 2
      // 01d: aload 2
      // 01e: bipush 2
      // 020: i2l
      // 021: invokevirtual java/io/ByteArrayInputStream.skip (J)J
      // 024: pop2
      // 025: bipush 1
      // 026: istore 3
      // 027: aconst_null
      // 028: astore 4
      // 02a: bipush 0
      // 02b: anewarray 1530
      // 02e: astore 5
      // 030: aload 2
      // 031: invokevirtual java/io/ByteArrayInputStream.available ()I
      // 034: ifle 0a1
      // 037: aload 2
      // 038: invokevirtual java/io/ByteArrayInputStream.read ()I
      // 03b: sipush 255
      // 03e: iand
      // 03f: istore 6
      // 041: iload 6
      // 043: lookupswitch 82 1 1 17
      // 054: new net/rim/device/api/crypto/certificate/wtls/WTLSCertificate
      // 057: dup
      // 058: aload 2
      // 059: invokespecial net/rim/device/api/crypto/certificate/wtls/WTLSCertificate.<init> (Ljava/io/InputStream;)V
      // 05c: astore 7
      // 05e: iload 3
      // 05f: ifeq 07e
      // 062: aload 0
      // 063: aload 7
      // 065: invokevirtual net/rim/device/api/crypto/certificate/wtls/WTLSCertificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey;
      // 068: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 06b: goto 075
      // 06e: astore 8
      // 070: aload 0
      // 071: aconst_null
      // 072: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 075: aload 7
      // 077: astore 4
      // 079: bipush 0
      // 07a: istore 3
      // 07b: goto 030
      // 07e: aload 5
      // 080: aload 5
      // 082: arraylength
      // 083: bipush 1
      // 084: iadd
      // 085: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 088: aload 5
      // 08a: aload 5
      // 08c: arraylength
      // 08d: bipush 1
      // 08e: isub
      // 08f: aload 7
      // 091: aastore
      // 092: goto 030
      // 095: aload 0
      // 096: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 099: bipush 43
      // 09b: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 09e: goto 030
      // 0a1: aload 4
      // 0a3: ifnonnull 0af
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 0aa: bipush 41
      // 0ac: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 0af: invokestatic net/rim/device/api/crypto/keystore/TrustedKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 0b2: astore 6
      // 0b4: aload 4
      // 0b6: aload 5
      // 0b8: aload 6
      // 0ba: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.buildCertificateChain (Lnet/rim/device/api/crypto/certificate/Certificate;[Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;)[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 0bd: astore 7
      // 0bf: invokestatic net/rim/device/api/crypto/tls/wtls20/WTLSOptionStore.getOptions ()Lnet/rim/device/api/crypto/tls/wtls20/WTLSOptionStore;
      // 0c2: astore 8
      // 0c4: aload 7
      // 0c6: aload 6
      // 0c8: invokestatic java/lang/System.currentTimeMillis ()J
      // 0cb: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.verifyCertificateChain ([Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;J)Z
      // 0ce: ifeq 0d4
      // 0d1: goto 169
      // 0d4: aload 8
      // 0d6: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSOptionStore.getDisplayServerCertificateWarnings ()Z
      // 0d9: bipush 1
      // 0da: if_icmpne 0e2
      // 0dd: bipush 2
      // 0df: goto 0e3
      // 0e2: bipush 1
      // 0e3: istore 9
      // 0e5: bipush 29
      // 0e7: bipush 2
      // 0e9: iload 9
      // 0eb: invokestatic net/rim/device/api/itpolicy/ITPolicy.getInteger (III)I
      // 0ee: istore 10
      // 0f0: iload 10
      // 0f2: ifne 113
      // 0f5: aload 0
      // 0f6: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 0f9: bipush 3
      // 0fb: bipush 46
      // 0fd: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol.sendAlertMessage (BB)V
      // 100: new java/lang/Object
      // 103: dup
      // 104: new net/rim/device/api/crypto/tls/TLSAlertException
      // 107: dup
      // 108: bipush 3
      // 10a: bipush 46
      // 10c: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // 10f: invokespecial net/rim/device/cldc/io/ssl/TLSCancelException.<init> (Ljava/lang/Exception;)V
      // 112: athrow
      // 113: bipush 0
      // 114: istore 11
      // 116: iload 10
      // 118: bipush 2
      // 11a: if_icmpne 169
      // 11d: getstatic net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 120: bipush 2
      // 122: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 125: getstatic net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 128: bipush 3
      // 12a: invokevirtual net/rim/device/api/i18n/ResourceBundle.getStringArray (I)[Ljava/lang/String;
      // 12d: bipush 1
      // 12e: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.getChoice (Ljava/lang/String;[Ljava/lang/Object;I)I
      // 131: istore 11
      // 133: iload 11
      // 135: bipush 2
      // 137: if_icmpeq 13d
      // 13a: goto 145
      // 13d: aload 4
      // 13f: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.displayCertificateDetails (Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 142: goto 11d
      // 145: iload 11
      // 147: bipush 1
      // 148: if_icmpne 169
      // 14b: aload 0
      // 14c: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 14f: bipush 3
      // 151: bipush 46
      // 153: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol.sendAlertMessage (BB)V
      // 156: new java/lang/Object
      // 159: dup
      // 15a: new net/rim/device/api/crypto/tls/TLSAlertException
      // 15d: dup
      // 15e: bipush 3
      // 160: bipush 90
      // 162: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // 165: invokespecial net/rim/device/cldc/io/ssl/TLSCancelException.<init> (Ljava/lang/Exception;)V
      // 168: athrow
      // 169: aload 0
      // 16a: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._wap20Conformance Z
      // 16d: ifne 173
      // 170: goto 314
      // 173: aload 4
      // 175: invokeinterface net/rim/device/api/crypto/certificate/Certificate.isRoot ()Z 1
      // 17a: ifeq 180
      // 17d: goto 314
      // 180: aload 4
      // 182: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getSubject ()Lnet/rim/device/api/crypto/certificate/DistinguishedName; 1
      // 187: astore 9
      // 189: aload 9
      // 18b: dup
      // 18c: instanceof net/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName
      // 18f: ifne 196
      // 192: pop
      // 193: goto 314
      // 196: checkcast net/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName
      // 199: invokevirtual net/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName.isCertificateAuthority ()Z
      // 19c: ifeq 1a2
      // 19f: goto 314
      // 1a2: aload 0
      // 1a3: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 1a6: bipush 42
      // 1a8: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 1ab: goto 314
      // 1ae: astore 9
      // 1b0: bipush 0
      // 1b1: istore 10
      // 1b3: getstatic net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 1b6: bipush 5
      // 1b8: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 1bb: getstatic net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 1be: bipush 3
      // 1c0: invokevirtual net/rim/device/api/i18n/ResourceBundle.getStringArray (I)[Ljava/lang/String;
      // 1c3: bipush 1
      // 1c4: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.getChoice (Ljava/lang/String;[Ljava/lang/Object;I)I
      // 1c7: istore 10
      // 1c9: iload 10
      // 1cb: bipush 2
      // 1cd: if_icmpeq 1d3
      // 1d0: goto 1db
      // 1d3: aload 4
      // 1d5: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.displayCertificateDetails (Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 1d8: goto 1b3
      // 1db: iload 10
      // 1dd: bipush 1
      // 1de: if_icmpeq 1e4
      // 1e1: goto 314
      // 1e4: aload 0
      // 1e5: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 1e8: bipush 3
      // 1ea: bipush 46
      // 1ec: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol.sendAlertMessage (BB)V
      // 1ef: new java/lang/Object
      // 1f2: dup
      // 1f3: new net/rim/device/api/crypto/tls/TLSAlertException
      // 1f6: dup
      // 1f7: bipush 3
      // 1f9: bipush 90
      // 1fb: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // 1fe: invokespecial net/rim/device/cldc/io/ssl/TLSCancelException.<init> (Ljava/lang/Exception;)V
      // 201: athrow
      // 202: astore 9
      // 204: bipush 29
      // 206: bipush 6
      // 208: bipush 2
      // 20a: invokestatic net/rim/device/api/itpolicy/ITPolicy.getInteger (III)I
      // 20d: istore 10
      // 20f: iload 10
      // 211: ifne 232
      // 214: aload 0
      // 215: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 218: bipush 3
      // 21a: bipush 46
      // 21c: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol.sendAlertMessage (BB)V
      // 21f: new java/lang/Object
      // 222: dup
      // 223: new net/rim/device/api/crypto/tls/TLSAlertException
      // 226: dup
      // 227: bipush 3
      // 229: bipush 46
      // 22b: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // 22e: invokespecial net/rim/device/cldc/io/ssl/TLSCancelException.<init> (Ljava/lang/Exception;)V
      // 231: athrow
      // 232: iload 10
      // 234: bipush 2
      // 236: if_icmpeq 23c
      // 239: goto 314
      // 23c: bipush 0
      // 23d: istore 11
      // 23f: getstatic net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 242: bipush 6
      // 244: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 247: getstatic net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 24a: bipush 3
      // 24c: invokevirtual net/rim/device/api/i18n/ResourceBundle.getStringArray (I)[Ljava/lang/String;
      // 24f: bipush 1
      // 250: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.getChoice (Ljava/lang/String;[Ljava/lang/Object;I)I
      // 253: istore 11
      // 255: iload 11
      // 257: bipush 2
      // 259: if_icmpeq 25f
      // 25c: goto 267
      // 25f: aload 4
      // 261: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.displayCertificateDetails (Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 264: goto 23f
      // 267: iload 11
      // 269: bipush 1
      // 26a: if_icmpeq 270
      // 26d: goto 314
      // 270: aload 0
      // 271: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 274: bipush 3
      // 276: bipush 44
      // 278: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol.sendAlertMessage (BB)V
      // 27b: new java/lang/Object
      // 27e: dup
      // 27f: new net/rim/device/api/crypto/tls/TLSAlertException
      // 282: dup
      // 283: bipush 3
      // 285: bipush 90
      // 287: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // 28a: invokespecial net/rim/device/cldc/io/ssl/TLSCancelException.<init> (Ljava/lang/Exception;)V
      // 28d: athrow
      // 28e: astore 9
      // 290: bipush 29
      // 292: bipush 6
      // 294: bipush 2
      // 296: invokestatic net/rim/device/api/itpolicy/ITPolicy.getInteger (III)I
      // 299: istore 10
      // 29b: iload 10
      // 29d: ifne 2be
      // 2a0: aload 0
      // 2a1: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 2a4: bipush 3
      // 2a6: bipush 45
      // 2a8: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol.sendAlertMessage (BB)V
      // 2ab: new java/lang/Object
      // 2ae: dup
      // 2af: new net/rim/device/api/crypto/tls/TLSAlertException
      // 2b2: dup
      // 2b3: bipush 3
      // 2b5: bipush 45
      // 2b7: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // 2ba: invokespecial net/rim/device/cldc/io/ssl/TLSCancelException.<init> (Ljava/lang/Exception;)V
      // 2bd: athrow
      // 2be: iload 10
      // 2c0: bipush 2
      // 2c2: if_icmpne 314
      // 2c5: bipush 0
      // 2c6: istore 11
      // 2c8: getstatic net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 2cb: bipush 7
      // 2cd: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 2d0: getstatic net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 2d3: bipush 3
      // 2d5: invokevirtual net/rim/device/api/i18n/ResourceBundle.getStringArray (I)[Ljava/lang/String;
      // 2d8: bipush 1
      // 2d9: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.getChoice (Ljava/lang/String;[Ljava/lang/Object;I)I
      // 2dc: istore 11
      // 2de: iload 11
      // 2e0: bipush 2
      // 2e2: if_icmpeq 2e8
      // 2e5: goto 2f0
      // 2e8: aload 4
      // 2ea: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.displayCertificateDetails (Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 2ed: goto 2c8
      // 2f0: iload 11
      // 2f2: bipush 1
      // 2f3: if_icmpne 314
      // 2f6: aload 0
      // 2f7: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 2fa: bipush 3
      // 2fc: bipush 45
      // 2fe: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol.sendAlertMessage (BB)V
      // 301: new java/lang/Object
      // 304: dup
      // 305: new net/rim/device/api/crypto/tls/TLSAlertException
      // 308: dup
      // 309: bipush 3
      // 30b: bipush 90
      // 30d: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // 310: invokespecial net/rim/device/cldc/io/ssl/TLSCancelException.<init> (Ljava/lang/Exception;)V
      // 313: athrow
      // 314: aload 0
      // 315: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 318: aload 4
      // 31a: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setCertificate (Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 31d: aload 0
      // 31e: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 321: aload 0
      // 322: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 325: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 32a: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 32f: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setKeyAgreementSize (I)V
      // 332: return
      // 333: astore 2
      // 334: aload 0
      // 335: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 338: bipush 80
      // 33a: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 33d: return
      // 33e: astore 2
      // 33f: aload 0
      // 340: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 343: bipush 50
      // 345: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 348: return
      // try (43 -> 47): 48 net/rim/device/api/crypto/InvalidCryptoSystemException
      // try (91 -> 189): 190 net/rim/device/api/crypto/certificate/CertificateVerificationException
      // try (91 -> 189): 227 net/rim/device/api/crypto/certificate/CertificateRevokedException
      // try (91 -> 189): 289 net/rim/device/api/crypto/certificate/CertificateInvalidException
      // try (0 -> 360): 361 null
      // try (0 -> 360): 367 null
   }

   @Override
   public final void serverKeyExchange(DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 004: istore 2
      // 005: iload 2
      // 006: sipush 255
      // 009: if_icmpne 011
      // 00c: aload 1
      // 00d: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedShort ()I
      // 010: istore 3
      // 011: aload 0
      // 012: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 015: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.getPendingState ()Lnet/rim/device/api/crypto/tls/wtls20/WTLSConnectionState;
      // 018: astore 3
      // 019: aload 3
      // 01a: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSConnectionState.getKeyExchangeAlgorithm ()B
      // 01d: istore 4
      // 01f: iload 4
      // 021: tableswitch 43 1 7 215 102 102 102 43 43 43
      // 04c: aload 1
      // 04d: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedShort ()I
      // 050: newarray 8
      // 052: astore 5
      // 054: aload 1
      // 055: aload 5
      // 057: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 05a: aload 1
      // 05b: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedShort ()I
      // 05e: newarray 8
      // 060: astore 6
      // 062: aload 1
      // 063: aload 6
      // 065: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 068: aload 0
      // 069: new net/rim/device/api/crypto/RSAPublicKey
      // 06c: dup
      // 06d: new net/rim/device/api/crypto/RSACryptoSystem
      // 070: dup
      // 071: aload 6
      // 073: arraylength
      // 074: bipush 8
      // 076: imul
      // 077: invokespecial net/rim/device/api/crypto/RSACryptoSystem.<init> (I)V
      // 07a: aload 5
      // 07c: aload 6
      // 07e: invokespecial net/rim/device/api/crypto/RSAPublicKey.<init> (Lnet/rim/device/api/crypto/RSACryptoSystem;[B[B)V
      // 081: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 084: goto 101
      // 087: aload 1
      // 088: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedShort ()I
      // 08b: newarray 8
      // 08d: astore 5
      // 08f: aload 1
      // 090: aload 5
      // 092: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 095: aload 3
      // 096: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSConnectionState.getKeyExchangeParameters ()B
      // 099: sipush 255
      // 09c: iand
      // 09d: tableswitch 27 0 2 79 27 53
      // 0b8: aload 0
      // 0b9: new net/rim/device/api/crypto/DHPublicKey
      // 0bc: dup
      // 0bd: new net/rim/device/api/crypto/DHCryptoSystem
      // 0c0: dup
      // 0c1: ldc_w "WTLS1"
      // 0c4: invokespecial net/rim/device/api/crypto/DHCryptoSystem.<init> (Ljava/lang/String;)V
      // 0c7: aload 5
      // 0c9: invokespecial net/rim/device/api/crypto/DHPublicKey.<init> (Lnet/rim/device/api/crypto/DHCryptoSystem;[B)V
      // 0cc: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 0cf: goto 101
      // 0d2: aload 0
      // 0d3: new net/rim/device/api/crypto/DHPublicKey
      // 0d6: dup
      // 0d7: new net/rim/device/api/crypto/DHCryptoSystem
      // 0da: dup
      // 0db: ldc_w "WTLS2"
      // 0de: invokespecial net/rim/device/api/crypto/DHCryptoSystem.<init> (Ljava/lang/String;)V
      // 0e1: aload 5
      // 0e3: invokespecial net/rim/device/api/crypto/DHPublicKey.<init> (Lnet/rim/device/api/crypto/DHCryptoSystem;[B)V
      // 0e6: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 0e9: goto 101
      // 0ec: aload 0
      // 0ed: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 0f0: bipush 47
      // 0f2: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 0f5: goto 101
      // 0f8: aload 0
      // 0f9: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 0fc: bipush 80
      // 0fe: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 101: aload 0
      // 102: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 105: aload 0
      // 106: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 109: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 10e: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 113: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.setKeyAgreementSize (I)V
      // 116: return
      // 117: astore 2
      // 118: aload 0
      // 119: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 11c: bipush 50
      // 11e: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 121: return
      // 122: astore 2
      // 123: aload 0
      // 124: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 127: bipush 51
      // 129: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 12c: return
      // try (0 -> 97): 98 null
      // try (0 -> 97): 104 null
   }

   @Override
   public final void serverCertificateRequest(DataBuffer buffer) {
      KeyStore keyStore = DeviceKeyStore.getInstance();
      keyStore.addIndex(new PrivateKeysKeyStoreIndex());
      int[] keyExchangeSuites = new int[0];
      DistinguishedName[] dns = new DistinguishedName[0];

      label129:
      try {
         buffer.readUnsignedShort();

         while (buffer.available() > 0) {
            byte keyExchangeSuite = buffer.readByte();
            byte paramSpecifier = buffer.readByte();
            byte identifier = buffer.readByte();
            Array.resize(dns, dns.length + 1);
            Array.resize(keyExchangeSuites, keyExchangeSuites.length + 1);
            keyExchangeSuites[keyExchangeSuites.length - 1] = keyExchangeSuite;
            switch (identifier) {
               case -1:
                  TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)47);
               case 0:
                  break;
               case 1:
               default:
                  try {
                     int original = buffer.getArrayLength() - buffer.getArrayPosition();
                     ByteArrayInputStream in = (ByteArrayInputStream)(new Object(buffer.getArray(), buffer.getArrayPosition() - 1, buffer.getArrayLength()));
                     dns[dns.length - 1] = new WTLSDistinguishedName(in);
                     buffer.skipBytes(original - in.available());
                  } catch (CertificateParsingException var16) {
                  }
            }
         }
      } finally {
         break label129;
      }

      this._certificates = new Certificate[0];
      Enumeration keyDatas = keyStore.elements(-8376547269562148933L);

      while (keyDatas.hasMoreElements()) {
         KeyStoreData keyData = (KeyStoreData)keyDatas.nextElement();
         if (keyData.isPrivateKeySet()) {
            Certificate cert = keyData.getCertificate();
            if (cert != null) {
               Certificate[] certs = CertificateUtilities.buildCertificateChain(cert, null, null);

               try {
                  PublicKey pubKey = cert.getPublicKey();
                  DistinguishedName issuerDn = certs[0].getIssuer();
                  int dnLength = dns.length;

                  for (int i = 0; i < dnLength; i++) {
                     if ((keyExchangeSuites[i] == 8 || keyExchangeSuites[i] == 9 || keyExchangeSuites[i] == 10)
                        && pubKey instanceof RSAPublicKey
                        && (dns[i] == null || issuerDn.equals(dns[i]))) {
                        this._keyStoreData = keyData;
                        this._certificates = certs;
                        return;
                     }
                  }
               } catch (InvalidCryptoSystemException var17) {
               }
            }
         }
      }
   }

   @Override
   public final void serverHelloDone(DataBuffer buffer) {
      if (!buffer.eof()) {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)47);
      }
   }

   @Override
   public final void clientCertificate() {
      try {
         DataBuffer buffer = (DataBuffer)(new Object());
         buffer.write(11);
         TLSUtilities.writeIntegerTwoBytes(buffer, 0);
         TLSUtilities.writeIntegerTwoBytes(buffer, 0);
         int numItems = this._certificates.length;

         for (int i = 0; i < numItems; i++) {
            Certificate cert = this._certificates[i];
            if (cert instanceof WTLSCertificate) {
               buffer.writeByte(1);
               byte[] encoding = cert.getEncoding();
               buffer.write(encoding);
            }
         }

         int length = buffer.getLength() - 5;
         buffer.rewind();
         buffer.setPosition(3);
         TLSUtilities.writeIntegerTwoBytes(buffer, length);
         this.write(buffer);
      } finally {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)50);
         return;
      }
   }

   @Override
   public final void clientKeyExchange() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 004: bipush 4
      // 006: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.updateStatus (I)V
      // 009: new java/lang/Object
      // 00c: dup
      // 00d: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 010: astore 1
      // 011: aload 1
      // 012: bipush 16
      // 014: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 017: aload 1
      // 018: bipush 0
      // 019: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 01c: aload 0
      // 01d: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 020: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.getPendingState ()Lnet/rim/device/api/crypto/tls/wtls20/WTLSConnectionState;
      // 023: astore 2
      // 024: aload 2
      // 025: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSConnectionState.getKeyExchangeAlgorithm ()B
      // 028: istore 3
      // 029: iload 3
      // 02a: tableswitch 54 1 10 207 76 76 76 54 54 54 54 54 54
      // 060: aload 0
      // 061: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.generatePremasterSecret ()[B
      // 064: astore 4
      // 066: aload 1
      // 067: aload 4
      // 069: arraylength
      // 06a: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 06d: aload 1
      // 06e: aload 4
      // 070: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 073: goto 102
      // 076: aload 2
      // 077: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSConnectionState.getKeyExchangeParameters ()B
      // 07a: sipush 255
      // 07d: iand
      // 07e: tableswitch 26 0 2 74 26 50
      // 098: aload 0
      // 099: new net/rim/device/api/crypto/DHKeyPair
      // 09c: dup
      // 09d: new net/rim/device/api/crypto/DHCryptoSystem
      // 0a0: dup
      // 0a1: ldc_w "WTLS1"
      // 0a4: invokespecial net/rim/device/api/crypto/DHCryptoSystem.<init> (Ljava/lang/String;)V
      // 0a7: invokespecial net/rim/device/api/crypto/DHKeyPair.<init> (Lnet/rim/device/api/crypto/DHCryptoSystem;)V
      // 0aa: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._keyPair Lnet/rim/device/api/crypto/KeyPair;
      // 0ad: goto 0d1
      // 0b0: aload 0
      // 0b1: new net/rim/device/api/crypto/DHKeyPair
      // 0b4: dup
      // 0b5: new net/rim/device/api/crypto/DHCryptoSystem
      // 0b8: dup
      // 0b9: ldc_w "WTLS2"
      // 0bc: invokespecial net/rim/device/api/crypto/DHCryptoSystem.<init> (Ljava/lang/String;)V
      // 0bf: invokespecial net/rim/device/api/crypto/DHKeyPair.<init> (Lnet/rim/device/api/crypto/DHCryptoSystem;)V
      // 0c2: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._keyPair Lnet/rim/device/api/crypto/KeyPair;
      // 0c5: goto 0d1
      // 0c8: aload 0
      // 0c9: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 0cc: bipush 47
      // 0ce: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 0d1: aload 0
      // 0d2: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._keyPair Lnet/rim/device/api/crypto/KeyPair;
      // 0d5: checkcast net/rim/device/api/crypto/DHKeyPair
      // 0d8: invokevirtual net/rim/device/api/crypto/DHKeyPair.getDHPublicKey ()Lnet/rim/device/api/crypto/DHPublicKey;
      // 0db: astore 4
      // 0dd: aload 4
      // 0df: invokevirtual net/rim/device/api/crypto/DHPublicKey.getPublicKeyData ()[B
      // 0e2: astore 5
      // 0e4: aload 1
      // 0e5: aload 5
      // 0e7: arraylength
      // 0e8: invokevirtual net/rim/device/api/util/DataBuffer.writeShort (I)V
      // 0eb: aload 1
      // 0ec: aload 5
      // 0ee: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 0f1: aload 0
      // 0f2: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.generatePremasterSecret ()[B
      // 0f5: pop
      // 0f6: goto 102
      // 0f9: aload 0
      // 0fa: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 0fd: bipush 80
      // 0ff: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 102: aload 0
      // 103: aload 1
      // 104: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.write (Lnet/rim/device/api/util/DataBuffer;)V
      // 107: return
      // 108: astore 1
      // 109: aload 0
      // 10a: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 10d: bipush 50
      // 10f: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 112: return
      // 113: astore 1
      // 114: aload 0
      // 115: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 118: bipush 51
      // 11a: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 11d: return
      // try (0 -> 89): 90 null
      // try (0 -> 89): 96 null
   }

   @Override
   public final void clientCertificateVerify() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._keyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 04: ifnonnull 0a
      // 07: goto ee
      // 0a: aload 0
      // 0b: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 0e: bipush 5
      // 10: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.updateStatus (I)V
      // 13: new java/lang/Object
      // 16: dup
      // 17: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 1a: astore 1
      // 1b: aload 1
      // 1c: bipush 15
      // 1e: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 21: aload 1
      // 22: bipush 0
      // 23: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 26: aload 0
      // 27: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._keyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 2a: aconst_null
      // 2b: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey; 2
      // 30: astore 2
      // 31: aload 0
      // 32: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 35: sipush 255
      // 38: iand
      // 39: tableswitch 47 0 7 63 47 47 47 63 55 55 55
      // 68: aload 0
      // 69: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._clientVerifySHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 6c: astore 3
      // 6d: goto 83
      // 70: aload 0
      // 71: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._clientVerifyMD5Hash Lnet/rim/device/api/crypto/MD5Digest;
      // 74: astore 3
      // 75: goto 83
      // 78: aload 0
      // 79: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 7c: bipush 47
      // 7e: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 81: aconst_null
      // 82: astore 3
      // 83: aload 2
      // 84: ifnonnull 93
      // 87: aload 0
      // 88: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 8b: bipush 47
      // 8d: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 90: goto d3
      // 93: aload 2
      // 94: instanceof net/rim/device/api/crypto/RSAPrivateKey
      // 97: ifeq ca
      // 9a: new net/rim/device/api/crypto/PKCS1SignatureSigner
      // 9d: dup
      // 9e: aload 2
      // 9f: checkcast net/rim/device/api/crypto/RSAPrivateKey
      // a2: aload 3
      // a3: bipush 0
      // a4: invokespecial net/rim/device/api/crypto/PKCS1SignatureSigner.<init> (Lnet/rim/device/api/crypto/RSAPrivateKey;Lnet/rim/device/api/crypto/Digest;Z)V
      // a7: astore 4
      // a9: aload 4
      // ab: invokevirtual net/rim/device/api/crypto/PKCS1SignatureSigner.getLength ()I
      // ae: newarray 8
      // b0: astore 5
      // b2: aload 4
      // b4: aload 5
      // b6: bipush 0
      // b7: invokevirtual net/rim/device/api/crypto/PKCS1SignatureSigner.sign ([BI)V
      // ba: aload 1
      // bb: aload 5
      // bd: arraylength
      // be: invokevirtual net/rim/device/api/util/DataBuffer.writeShort (I)V
      // c1: aload 1
      // c2: aload 5
      // c4: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // c7: goto d3
      // ca: aload 0
      // cb: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // ce: bipush 47
      // d0: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // d3: aload 0
      // d4: aload 1
      // d5: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.write (Lnet/rim/device/api/util/DataBuffer;)V
      // d8: return
      // d9: astore 1
      // da: aload 0
      // db: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // de: bipush 50
      // e0: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // e3: return
      // e4: astore 1
      // e5: aload 0
      // e6: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // e9: bipush 51
      // eb: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // ee: return
      // try (4 -> 83): 84 null
      // try (4 -> 83): 90 null
   }

   @Override
   public final void changeCipherSpec(DataBuffer buffer) {
      try {
         if (buffer != null) {
            TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)10);
         }

         this._recordProtocol.getChangeCipherSpecProtocol().sendChangeCipherSpecMessage();
         this._recordProtocol.changeWriteCipherSpec();
      } finally {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)50);
         return;
      }
   }

   @Override
   public final void finished(DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ifnull 007
      // 004: goto 0b4
      // 007: aload 0
      // 008: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 00b: bipush 6
      // 00d: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.updateStatus (I)V
      // 010: new java/lang/Object
      // 013: dup
      // 014: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 017: astore 1
      // 018: aload 1
      // 019: bipush 20
      // 01b: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 01e: aload 1
      // 01f: bipush 0
      // 020: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 023: aload 0
      // 024: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 027: sipush 255
      // 02a: iand
      // 02b: tableswitch 45 0 7 81 45 45 45 81 63 63 63
      // 058: bipush 20
      // 05a: newarray 8
      // 05c: astore 2
      // 05d: aload 0
      // 05e: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._clientSHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 061: aload 2
      // 062: bipush 0
      // 063: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ([BI)I
      // 066: pop
      // 067: goto 087
      // 06a: bipush 16
      // 06c: newarray 8
      // 06e: astore 2
      // 06f: aload 0
      // 070: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._clientMD5Hash Lnet/rim/device/api/crypto/MD5Digest;
      // 073: aload 2
      // 074: bipush 0
      // 075: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ([BI)I
      // 078: pop
      // 079: goto 087
      // 07c: aload 0
      // 07d: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 080: bipush 47
      // 082: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 085: aconst_null
      // 086: astore 2
      // 087: new net/rim/device/api/crypto/tls/wtls20/WTLSPRF
      // 08a: dup
      // 08b: aload 0
      // 08c: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 08f: sipush 255
      // 092: iand
      // 093: aload 0
      // 094: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._masterSecret [B
      // 097: ldc_w "client finished"
      // 09a: invokevirtual java/lang/String.getBytes ()[B
      // 09d: aload 2
      // 09e: invokespecial net/rim/device/api/crypto/tls/wtls20/WTLSPRF.<init> (I[B[B[B)V
      // 0a1: astore 3
      // 0a2: aload 1
      // 0a3: aload 3
      // 0a4: bipush 12
      // 0a6: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 0a9: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 0ac: aload 0
      // 0ad: aload 1
      // 0ae: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.write (Lnet/rim/device/api/util/DataBuffer;)V
      // 0b1: goto 17b
      // 0b4: aload 0
      // 0b5: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol;
      // 0b8: bipush 7
      // 0ba: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSRecordProtocol.updateStatus (I)V
      // 0bd: aload 0
      // 0be: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 0c1: sipush 255
      // 0c4: iand
      // 0c5: tableswitch 47 0 7 83 47 47 47 83 65 65 65
      // 0f4: bipush 20
      // 0f6: newarray 8
      // 0f8: astore 2
      // 0f9: aload 0
      // 0fa: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._serverSHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 0fd: aload 2
      // 0fe: bipush 0
      // 0ff: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ([BI)I
      // 102: pop
      // 103: goto 123
      // 106: bipush 16
      // 108: newarray 8
      // 10a: astore 2
      // 10b: aload 0
      // 10c: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._serverMD5Hash Lnet/rim/device/api/crypto/MD5Digest;
      // 10f: aload 2
      // 110: bipush 0
      // 111: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ([BI)I
      // 114: pop
      // 115: goto 123
      // 118: aload 0
      // 119: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 11c: bipush 47
      // 11e: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 121: aconst_null
      // 122: astore 2
      // 123: new net/rim/device/api/crypto/tls/wtls20/WTLSPRF
      // 126: dup
      // 127: aload 0
      // 128: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._cipherSuite I
      // 12b: sipush 255
      // 12e: iand
      // 12f: aload 0
      // 130: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._masterSecret [B
      // 133: ldc_w "server finished"
      // 136: invokevirtual java/lang/String.getBytes ()[B
      // 139: aload 2
      // 13a: invokespecial net/rim/device/api/crypto/tls/wtls20/WTLSPRF.<init> (I[B[B[B)V
      // 13d: astore 3
      // 13e: aload 3
      // 13f: bipush 12
      // 141: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 144: astore 4
      // 146: bipush 12
      // 148: newarray 8
      // 14a: astore 5
      // 14c: aload 1
      // 14d: aload 5
      // 14f: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 152: aload 5
      // 154: aload 4
      // 156: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 159: ifne 17b
      // 15c: aload 0
      // 15d: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 160: bipush 51
      // 162: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 165: return
      // 166: astore 2
      // 167: aload 0
      // 168: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 16b: bipush 51
      // 16d: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 170: return
      // 171: astore 2
      // 172: aload 0
      // 173: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 176: bipush 50
      // 178: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 17b: return
      // try (0 -> 136): 137 null
      // try (0 -> 136): 143 null
   }

   @Override
   public final KeyMaterial generateKeyMaterial(byte[] masterSecret, byte[] clientRandom, byte[] serverRandom) {
      TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)47);
      return null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void connect() {
      boolean certificateRequest = false;
      this.clientHello();
      DataBuffer[] buffer = null;
      boolean serverHelloDone = false;
      int retryCount = 0;

      while (true) {
         int type;
         while (true) {
            buffer = new Object[1];
            type = 0;
            boolean var13 = false /* VF: Semaphore variable */;

            try {
               var13 = true;
               type = this.read(buffer);
               var13 = false;
               break;
            } finally {
               if (var13) {
                  if (retryCount >= 5) {
                     TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)40);
                     break;
                  }

                  this._recordProtocol.retransmit();
                  retryCount++;
                  continue;
               }
            }
         }

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
               throw new TLSAlertException((byte)3, (byte)10);
         }

         if (serverHelloDone || this._resumption) {
            this._recordProtocol.setAutoflush(false);
            if (certificateRequest) {
               this.clientCertificate();
            }

            if (!this._resumption) {
               this.clientKeyExchange();
            }

            if (certificateRequest) {
               this.clientCertificateVerify();
            }

            this.updateConnectionState();
            if (!this._resumption) {
               this.changeCipherSpec(null);
               this.finished(null);
            }

            this._recordProtocol.setAutoflush(true);
            buffer = new Object[1];
            type = 0;
            retryCount = 0;

            while (true) {
               boolean var10 = false /* VF: Semaphore variable */;

               try {
                  var10 = true;
                  type = this.read(buffer);
                  var10 = false;
                  break;
               } finally {
                  if (var10) {
                     if (retryCount < 5) {
                        this._recordProtocol.retransmit();
                        retryCount++;
                     } else {
                        TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)40);
                     }
                     continue;
                  }
               }
            }

            if (type == 20) {
               this.finished(buffer[0]);
            } else {
               this._alertProtocol.sendAlertMessage((byte)3, (byte)40);
            }

            if (this._resumption) {
               this._recordProtocol.setPiggybackMode(true);
               this.changeCipherSpec(null);
               this.finished(null);
               this._recordProtocol.setPiggybackMode(false);
            }

            if (!this._resumption) {
               int persistType = 1;
               if (!this._deleteSessionInfo) {
                  persistType |= 4;
               }

               if (!DeviceInfo.isSimulator() && RadioInfo.getNetworkType() == 5) {
                  persistType = 12;
               } else {
                  persistType |= 2;
               }

               SessionResumption resumption = new SessionResumption();
               resumption.addWTLSSession(
                  this._recordProtocol.getDomainName(),
                  this._recordProtocol.getProtocol(),
                  this._recordProtocol.getIPAddress(),
                  this._recordProtocol.getPort(),
                  this._ipv4Address,
                  this._sessionID,
                  this._masterSecret,
                  this._recordProtocol.getLastOpenwaveSignature(),
                  (short)this._cipherSuite,
                  this._recordProtocol.getRIMServerCertificate(),
                  persistType
               );
            }

            this._recordProtocol.updateStatus(8);
            return;
         }
      }
   }

   @Override
   public final int getLocalVersion() {
      return 1;
   }

   @Override
   public final byte[] getCipherSuites() {
      byte[] encryption = WTLSCipherSuites.getEncryptionPriority();
      byte[] mac = WTLSCipherSuites.getMACPriority();
      int length = 2 * encryption.length * mac.length;
      byte[] output = new byte[length];
      int offset = 0;
      int encryptionLength = encryption.length;

      for (int i = 0; i < encryptionLength; i++) {
         for (int j = 0; j < mac.length; j++) {
            output[offset++] = encryption[i];
            output[offset++] = mac[j];
         }
      }

      return output;
   }

   private final int getIPV4Address(String str) {
      int index = 0;
      int nextIndex = str.indexOf(46);
      int val = 0;
      int shift = 24;

      while (shift > 0) {
         if (nextIndex != -1 && index < str.length()) {
            int i = Integer.parseInt(str.substring(index, nextIndex), 10);
            if (i >= 0 && i <= 255) {
               val |= (i & 0xFF) << shift;
               index = nextIndex + 1;
               nextIndex = str.indexOf(46, index);
               shift -= 8;
               continue;
            }

            throw new Object();
         }

         throw new Object();
      }

      int i = Integer.parseInt(str.substring(index), 10);
      return (int)(val | (long)(i & 0xFF) << shift);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final byte[][][] getClientIds() {
      if (!this._useClientIdInfo) {
         return new byte[1][1][];
      }

      if (this._clientIdType < 0 || this._clientIdType > 7) {
         int networkType = RadioInfo.getNetworkType();
         switch (RadioInfo.getNetworkType()) {
            case 3:
            case 7:
               this._clientIdType = 2;
               break;
            case 4:
               this._clientIdType = 7;
               break;
            default:
               this._clientIdType = 0;
         }
      }

      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         String var8 = this._clientIdValue;
         switch (this._clientIdType) {
            case -1:
               var6 = false;
               break;
            case 1:
            default:
               if (var8 != null && var8.length() != 0) {
                  byte[][][] resultx = new byte[][][]{(byte[][])(new byte[13])};
                  resultx[0][0] = (byte[])2;
                  resultx[0][1] = (byte[])11;
                  resultx[0][6] = (byte[])4;
                  int ipV4 = this.getIPV4Address(var8);
                  resultx[0][7] = (byte[])((byte)(ipV4 >> 24));
                  resultx[0][8] = (byte[])((byte)(ipV4 >> 16));
                  resultx[0][9] = (byte[])((byte)(ipV4 >> 8));
                  resultx[0][10] = (byte[])((byte)ipV4);
                  this._ipv4Address = ipV4;
                  return resultx;
               }
            case 0:
               byte[][][] result = new byte[][][]{(byte[][])(new byte[13])};
               result[0][0] = (byte[])2;
               result[0][1] = (byte[])11;
               result[0][6] = (byte[])4;
               int apnId = RadioInfo.getAccessPointNumber(this._apn);
               byte[] ip = RadioInfo.getIPAddress(apnId);
               System.arraycopy(ip, 0, result[0], 7, 4);
               this._ipv4Address = (ip[0] & 255) << 24 | (ip[1] & 255) << 16 | (ip[2] & 255) << 8 | ip[3] & 255;
               return result;
            case 5:
            case 7:
               byte[] imsi = CDMAInfo.getIMSI();
               if (imsi != null) {
                  if (this._clientIdType == 7 && imsi.length > 10) {
                     imsi = Arrays.copy(imsi, imsi.length - 10, 10);
                  }

                  byte[][][] resultx = new byte[1][][];
                  int len = Math.min(imsi.length, 15);
                  resultx[0] = (byte[][])(new byte[len + 12]);
                  resultx[0][0] = (byte[])2;
                  resultx[0][1] = (byte[])((byte)(len + 10));
                  resultx[0][6] = (byte[])5;
                  resultx[0][7] = (byte[])2;
                  resultx[0][8] = (byte[])false;
                  resultx[0][9] = (byte[])false;
                  System.arraycopy(imsi, 0, resultx[0], 10, len);
                  return resultx;
               }
            case 4:
               var8 = Phone.getInstance().getNumber(0);
            case 6:
               if (var8 != null && var8.length() != 0) {
                  byte[][][] resultx = new byte[1][][];
                  int len = Math.min(var8.length(), 15);
                  resultx[0] = (byte[][])(new byte[len + 12]);
                  resultx[0][0] = (byte[])2;
                  resultx[0][1] = (byte[])((byte)(len + 10));
                  resultx[0][6] = (byte[])5;
                  resultx[0][7] = (byte[])2;
                  resultx[0][8] = (byte[])false;
                  resultx[0][9] = (byte[])false;

                  for (int i = 0; i < len; i++) {
                     resultx[0][i + 10] = (byte[])((byte)Integer.parseInt(var8.substring(i, i + 1)));
                  }

                  return resultx;
               }
            case 2:
               var8 = Phone.getInstance().getNumber(0);
            case 3:
               if (var8 == null) {
                  var6 = false;
               } else {
                  if (var8.length() != 0) {
                     byte[][][] resultx = new byte[1][][];
                     int len = Math.min(var8.length(), 18);
                     resultx[0] = (byte[][])(new byte[len + 12]);
                     resultx[0][0] = (byte[])2;
                     resultx[0][1] = (byte[])((byte)(len + 10));
                     resultx[0][6] = (byte[])2;
                     resultx[0][7] = (byte[])false;
                     resultx[0][8] = (byte[])true;
                     resultx[0][9] = (byte[])true;

                     for (int i = 0; i < len; i++) {
                        resultx[0][i + 10] = (byte[])((byte)Integer.parseInt(var8.substring(i, i + 1)));
                     }

                     return resultx;
                  }

                  var6 = false;
               }
         }
      } finally {
         if (var6) {
            EventLogger.logEvent(-7509200465648525729L, "WTLS: error reading client id key".getBytes());
            return new byte[1][1][];
         }
      }

      EventLogger.logEvent(-7509200465648525729L, "WTLS: no valid key found".getBytes());
      return new byte[1][1][];
   }

   public final byte[] getTrustedKeyIds() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 07: astore 1
      // 08: invokestatic net/rim/device/api/crypto/keystore/TrustedKeyStore.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 0b: astore 2
      // 0c: aload 2
      // 0d: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements ()Ljava/util/Enumeration; 1
      // 12: astore 3
      // 13: bipush 0
      // 14: istore 4
      // 16: aload 3
      // 17: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 1c: ifne 22
      // 1f: goto b9
      // 22: aload 3
      // 23: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 28: checkcast net/rim/device/api/crypto/keystore/KeyStoreData
      // 2b: astore 5
      // 2d: aload 5
      // 2f: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 34: astore 6
      // 36: aload 6
      // 38: ifnonnull 3e
      // 3b: goto 16
      // 3e: iinc 4 1
      // 41: iload 4
      // 43: bipush 40
      // 45: if_icmplt 4b
      // 48: goto b9
      // 4b: bipush 0
      // 4c: istore 7
      // 4e: bipush 0
      // 4f: istore 8
      // 51: aload 6
      // 53: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 58: astore 9
      // 5a: goto 62
      // 5d: astore 10
      // 5f: aconst_null
      // 60: astore 9
      // 62: aload 9
      // 64: ifnonnull 6a
      // 67: goto 16
      // 6a: new java/lang/Object
      // 6d: dup
      // 6e: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 71: astore 10
      // 73: aload 9
      // 75: instanceof net/rim/device/api/crypto/RSAPublicKey
      // 78: ifeq 16
      // 7b: bipush 8
      // 7d: istore 7
      // 7f: aload 9
      // 81: checkcast net/rim/device/api/crypto/RSAPublicKey
      // 84: astore 11
      // 86: aload 10
      // 88: aload 11
      // 8a: invokevirtual net/rim/device/api/crypto/RSAPublicKey.getN ()[B
      // 8d: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 90: aload 1
      // 91: iload 7
      // 93: invokevirtual java/io/ByteArrayOutputStream.write (I)V
      // 96: aload 1
      // 97: iload 8
      // 99: invokevirtual java/io/ByteArrayOutputStream.write (I)V
      // 9c: aload 1
      // 9d: sipush 254
      // a0: invokevirtual java/io/ByteArrayOutputStream.write (I)V
      // a3: aload 1
      // a4: aload 10
      // a6: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ()[B
      // a9: invokevirtual java/io/OutputStream.write ([B)V
      // ac: goto 16
      // af: astore 7
      // b1: goto 16
      // b4: astore 7
      // b6: goto 16
      // b9: aload 1
      // ba: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // bd: areturn
      // be: astore 1
      // bf: bipush 0
      // c0: newarray 8
      // c2: areturn
      // try (34 -> 37): 38 net/rim/device/api/crypto/InvalidCryptoSystemException
      // try (30 -> 43): 74 null
      // try (44 -> 73): 74 null
      // try (30 -> 43): 76 null
      // try (44 -> 73): 76 null
      // try (0 -> 80): 81 null
   }

   final byte getKeyRefresh() {
      return 5;
   }

   public final int read(DataBuffer[] buffer) {
      if (this._dataBuffer.getPosition() == this._dataBuffer.getLength()) {
         int type = this._recordProtocol.read(this._dataBuffer, true);
         if (type != 3) {
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
      int length = this._dataBuffer.readUnsignedShort();
      buffer[0] = (DataBuffer)(new Object(this._dataBuffer, length));
      return type;
   }

   public final byte[] generatePremasterSecret() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 004: ifnonnull 00f
      // 007: new java/lang/Object
      // 00a: dup
      // 00b: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 00e: athrow
      // 00f: aload 0
      // 010: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 013: dup
      // 014: instanceof net/rim/device/api/crypto/RSAPublicKey
      // 017: ifne 01e
      // 01a: pop
      // 01b: goto 0fe
      // 01e: checkcast net/rim/device/api/crypto/RSAPublicKey
      // 021: astore 1
      // 022: new net/rim/device/api/crypto/RSAEncryptorEngine
      // 025: dup
      // 026: aload 1
      // 027: invokespecial net/rim/device/api/crypto/RSAEncryptorEngine.<init> (Lnet/rim/device/api/crypto/RSAPublicKey;)V
      // 02a: astore 2
      // 02b: new net/rim/device/api/crypto/PKCS1FormatterEngine
      // 02e: dup
      // 02f: aload 2
      // 030: invokespecial net/rim/device/api/crypto/PKCS1FormatterEngine.<init> (Lnet/rim/device/api/crypto/PublicKeyEncryptorEngine;)V
      // 033: astore 3
      // 034: new java/lang/Object
      // 037: dup
      // 038: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 03b: astore 4
      // 03d: new net/rim/device/api/crypto/BlockEncryptor
      // 040: dup
      // 041: aload 3
      // 042: aload 4
      // 044: invokespecial net/rim/device/api/crypto/BlockEncryptor.<init> (Lnet/rim/device/api/crypto/BlockFormatterEngine;Ljava/io/OutputStream;)V
      // 047: astore 5
      // 049: aload 1
      // 04a: invokevirtual net/rim/device/api/crypto/RSAPublicKey.getE ()[B
      // 04d: astore 6
      // 04f: aload 1
      // 050: invokevirtual net/rim/device/api/crypto/RSAPublicKey.getN ()[B
      // 053: astore 7
      // 055: aload 6
      // 057: arraylength
      // 058: istore 8
      // 05a: aload 7
      // 05c: arraylength
      // 05d: istore 9
      // 05f: aload 0
      // 060: bipush 24
      // 062: iload 8
      // 064: iadd
      // 065: iload 9
      // 067: iadd
      // 068: newarray 8
      // 06a: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._premasterSecret [B
      // 06d: aload 0
      // 06e: invokevirtual net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol.getLocalVersion ()I
      // 071: istore 10
      // 073: aload 0
      // 074: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._premasterSecret [B
      // 077: bipush 0
      // 078: iload 10
      // 07a: i2b
      // 07b: bastore
      // 07c: aload 0
      // 07d: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._premasterSecret [B
      // 080: bipush 1
      // 081: bipush 19
      // 083: invokestatic net/rim/device/api/crypto/RandomSource.getBytes ([BII)V
      // 086: bipush 20
      // 088: istore 11
      // 08a: aload 0
      // 08b: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._premasterSecret [B
      // 08e: iload 11
      // 090: iinc 11 1
      // 093: iload 8
      // 095: bipush 8
      // 097: ishr
      // 098: i2b
      // 099: bastore
      // 09a: aload 0
      // 09b: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._premasterSecret [B
      // 09e: iload 11
      // 0a0: iinc 11 1
      // 0a3: iload 8
      // 0a5: i2b
      // 0a6: bastore
      // 0a7: aload 6
      // 0a9: bipush 0
      // 0aa: aload 0
      // 0ab: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._premasterSecret [B
      // 0ae: iload 11
      // 0b0: iload 8
      // 0b2: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 0b5: iload 11
      // 0b7: iload 8
      // 0b9: iadd
      // 0ba: istore 11
      // 0bc: aload 0
      // 0bd: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._premasterSecret [B
      // 0c0: iload 11
      // 0c2: iinc 11 1
      // 0c5: iload 9
      // 0c7: bipush 8
      // 0c9: ishr
      // 0ca: i2b
      // 0cb: bastore
      // 0cc: aload 0
      // 0cd: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._premasterSecret [B
      // 0d0: iload 11
      // 0d2: iinc 11 1
      // 0d5: iload 9
      // 0d7: i2b
      // 0d8: bastore
      // 0d9: aload 7
      // 0db: bipush 0
      // 0dc: aload 0
      // 0dd: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._premasterSecret [B
      // 0e0: iload 11
      // 0e2: iload 9
      // 0e4: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 0e7: aload 5
      // 0e9: aload 0
      // 0ea: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._premasterSecret [B
      // 0ed: bipush 0
      // 0ee: bipush 20
      // 0f0: invokevirtual net/rim/device/api/crypto/BlockEncryptor.write ([BII)V
      // 0f3: aload 5
      // 0f5: invokevirtual net/rim/device/api/crypto/BlockEncryptor.close ()V
      // 0f8: aload 4
      // 0fa: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 0fd: areturn
      // 0fe: aload 0
      // 0ff: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 102: instanceof net/rim/device/api/crypto/DHPublicKey
      // 105: ifeq 126
      // 108: aload 0
      // 109: aload 0
      // 10a: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._keyPair Lnet/rim/device/api/crypto/KeyPair;
      // 10d: checkcast net/rim/device/api/crypto/DHKeyPair
      // 110: invokevirtual net/rim/device/api/crypto/DHKeyPair.getDHPrivateKey ()Lnet/rim/device/api/crypto/DHPrivateKey;
      // 113: aload 0
      // 114: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 117: checkcast net/rim/device/api/crypto/DHPublicKey
      // 11a: bipush 0
      // 11b: invokestatic net/rim/device/api/crypto/DHKeyAgreement.generateSharedSecret (Lnet/rim/device/api/crypto/DHPrivateKey;Lnet/rim/device/api/crypto/DHPublicKey;Z)[B
      // 11e: putfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._premasterSecret [B
      // 121: aload 0
      // 122: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._premasterSecret [B
      // 125: areturn
      // 126: aload 0
      // 127: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 12a: bipush 47
      // 12c: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 12f: aconst_null
      // 130: areturn
      // 131: astore 1
      // 132: aload 0
      // 133: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 136: bipush 51
      // 138: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 13b: aconst_null
      // 13c: areturn
      // 13d: astore 1
      // 13e: aload 0
      // 13f: getfield net/rim/device/api/crypto/tls/wtls20/WTLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSAlertProtocol;
      // 142: bipush 50
      // 144: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 147: aconst_null
      // 148: areturn
      // try (0 -> 132): 157 null
      // try (133 -> 150): 157 null
      // try (151 -> 155): 157 null
      // try (0 -> 132): 164 null
      // try (133 -> 150): 164 null
      // try (151 -> 155): 164 null
   }

   public final void write(DataBuffer buffer) {
      int length = buffer.getLength() - 3;
      buffer.rewind();
      buffer.setPosition(1);
      TLSUtilities.writeIntegerTwoBytes(buffer, length);
      buffer.rewind();
      byte[] hashBytes = new byte[buffer.getLength()];
      buffer.readFully(hashBytes);
      this.updateHash(hashBytes);
      buffer.rewind();
      this._recordProtocol.write(3, buffer);
   }

   public final void updateConnectionState() {
      byte[] clientRandom = this._recordProtocol.getClientRandom();
      byte[] serverRandom = this._recordProtocol.getServerRandom();
      if (!this._resumption) {
         this._masterSecret = this.generateMasterSecret(this._premasterSecret, clientRandom, serverRandom);
      }

      this._recordProtocol.setMasterSecret(this._masterSecret);
      if (this._premasterSecret != null) {
         Arrays.fill(this._premasterSecret, (byte)0);
         this._premasterSecret = null;
      }
   }

   public final void updateHash(byte[] data) {
      this._clientSHAHash.update(data);
      this._clientMD5Hash.update(data);
      this._serverSHAHash.update(data);
      this._serverMD5Hash.update(data);
      this._clientVerifySHAHash.update(data);
      this._clientVerifyMD5Hash.update(data);
   }

   public final byte[] generateMasterSecret(byte[] premasterSecret, byte[] clientRandom, byte[] serverRandom) {
      try {
         byte[] random = new byte[clientRandom.length + serverRandom.length];
         System.arraycopy(clientRandom, 0, random, 0, clientRandom.length);
         System.arraycopy(serverRandom, 0, random, clientRandom.length, serverRandom.length);
         WTLSPRF prf = new WTLSPRF(this._cipherSuite & 0xFF, premasterSecret, "master secret".getBytes(), random);
         return prf.getBytes(20);
      } finally {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)51);
         return null;
      }
   }

   public final void checkCipherSuites(int cipherSuite, byte[] cipherSuites) {
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

   private static final boolean checkCipherSuite(byte[] cipherSuites, int cipherSuite) {
      int len = cipherSuites.length;
      byte mac = (byte)(cipherSuite & 0xFF);
      byte encrypt = (byte)(cipherSuite >> 8);

      for (int i = 0; i < len; i += 2) {
         if (cipherSuites[i] == encrypt && cipherSuites[i + 1] == mac) {
            return true;
         }
      }

      return false;
   }
}
