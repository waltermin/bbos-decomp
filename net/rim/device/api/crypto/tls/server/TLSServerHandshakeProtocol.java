package net.rim.device.api.crypto.tls.server;

import java.util.Enumeration;
import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DHKeyPair;
import net.rim.device.api.crypto.DHPublicKey;
import net.rim.device.api.crypto.DSAPrivateKey;
import net.rim.device.api.crypto.DSASignatureSigner;
import net.rim.device.api.crypto.PKCS1SignatureSigner;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSAKeyPair;
import net.rim.device.api.crypto.RSAPrivateKey;
import net.rim.device.api.crypto.RSAPublicKey;
import net.rim.device.api.crypto.SignatureSigner;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.crypto.tls.AlertProtocolMethods;
import net.rim.device.api.crypto.tls.ConnectionState;
import net.rim.device.api.crypto.tls.RandomStructure;
import net.rim.device.api.crypto.tls.RecordProtocol;
import net.rim.device.api.crypto.tls.TLSUtilities;
import net.rim.device.api.crypto.tls.ssl30.SSLConnectionState;
import net.rim.device.api.crypto.tls.ssl30.SSLDigest;
import net.rim.device.api.crypto.tls.ssl30.SSLHandshakeUtilities;
import net.rim.device.api.crypto.tls.ssl30.SSLRecordProtocol;
import net.rim.device.api.crypto.tls.tls10.TLSAlertProtocol;
import net.rim.device.api.crypto.tls.tls10.TLSCipherSuites;
import net.rim.device.api.crypto.tls.tls10.TLSHandshakeProtocol;
import net.rim.device.api.crypto.tls.tls10.TLSRecordProtocol;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

public class TLSServerHandshakeProtocol extends TLSHandshakeProtocol {
   private int[] _cipherSuites;

   public TLSServerHandshakeProtocol(TLSRecordProtocol recordProtocol) {
      super(recordProtocol);
      super._recordProtocol = recordProtocol;
      super._alertProtocol = new TLSAlertProtocol((TLSRecordProtocol)super._recordProtocol);
      super._changeCipherSpecProtocol = ((SSLRecordProtocol)super._recordProtocol).getChangeCipherSpecProtocol();
   }

   public void helloRequest() {
      throw new Object();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void clientHello(DataBuffer buffer, boolean SSLv2) {
      try {
         if (!SSLv2) {
            int version1 = buffer.readUnsignedByte();
            int version2 = buffer.readUnsignedByte();
            super._remoteVersion = (version1 << 8) + version2;
            ((SSLRecordProtocol)super._recordProtocol).setRemoteVersion(super._remoteVersion);
            byte[] clientRandom = new byte[32];
            buffer.readFully(clientRandom);
            ((RecordProtocol)super._recordProtocol).setClientRandom(clientRandom);
            int sessionIDLength = buffer.readUnsignedByte();
            super._sessionID = null;
            if (sessionIDLength > 0) {
               super._sessionID = new byte[sessionIDLength];
               buffer.readFully(super._sessionID);
            }

            int cipherSuiteLength1 = buffer.readUnsignedByte();
            int cipherSuiteLength2 = buffer.readUnsignedByte();
            int cipherSuiteLength = (cipherSuiteLength1 << 8) + cipherSuiteLength2;
            byte[] cipherSuites = new byte[cipherSuiteLength];
            buffer.readFully(cipherSuites);
            this._cipherSuites = new int[cipherSuites.length >> 1];

            for (int i = 0; i < cipherSuites.length >> 1; i++) {
               this._cipherSuites[i] = (cipherSuites[i * 2] << 8) + cipherSuites[i * 2 + 1];
            }

            int compressionLength = buffer.readUnsignedByte();
            byte[] compression = new byte[compressionLength];
            buffer.readFully(compression);
         } else {
            int version1 = buffer.readUnsignedByte();
            int version2 = buffer.readUnsignedByte();
            super._remoteVersion = (version1 << 8) + version2;
            ((SSLRecordProtocol)super._recordProtocol).setRemoteVersion(super._remoteVersion);
            int cipherSpec1 = buffer.readUnsignedByte();
            int cipherSpec2 = buffer.readUnsignedByte();
            int cipherSpecLength = (cipherSpec1 << 8) + cipherSpec2;
            if (cipherSpecLength >= 0 && cipherSpecLength % 3 == 0) {
               int session1 = buffer.readUnsignedByte();
               int session2 = buffer.readUnsignedByte();
               int sessionLength = (session1 << 8) + session2;
               int challenge1 = buffer.readUnsignedByte();
               int challenge2 = buffer.readUnsignedByte();
               int challengeLength = (challenge1 << 8) + challenge2;
               int numCipherSuites = cipherSpecLength / 3;
               this._cipherSuites = new int[numCipherSuites];

               for (int i = 0; i < numCipherSuites; i++) {
                  int cipher1 = buffer.readUnsignedByte();
                  int cipher2 = buffer.readUnsignedByte();
                  int cipher3 = buffer.readUnsignedByte();
                  this._cipherSuites[i] = (cipher1 << 16) + (cipher2 << 8) + cipher3;
               }

               super._sessionID = null;
               if (sessionLength > 0) {
                  super._sessionID = new byte[sessionLength];
                  buffer.readFully(super._sessionID);
               }

               byte[] clientRandom = new byte[32];
               if (challengeLength > 32) {
                  int skipBytes = challengeLength - 32;
                  if (buffer.skipBytes(skipBytes) != skipBytes) {
                     throw new Object();
                  }

                  buffer.readFully(clientRandom);
               } else {
                  buffer.readFully(clientRandom, 32 - challengeLength, challengeLength);
               }

               ((RecordProtocol)super._recordProtocol).setClientRandom(clientRandom);
            } else {
               ((TLSAlertProtocol)super._alertProtocol).sendAlertMessage((byte)3, (byte)40);
               throw new Object((Exception)(new Object((byte)3, (byte)40)));
            }
         }
      } catch (Throwable var20) {
         throw new Object(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void serverHello() {
      try {
         DataBuffer buffer = (DataBuffer)(new Object());
         buffer.write(2);
         buffer.writeByte(0);
         buffer.writeByte(0);
         buffer.writeByte(0);
         buffer.writeShort(super._remoteVersion);
         RandomStructure random = this.getRandom(28);
         ((RecordProtocol)super._recordProtocol).setServerRandom(random.getRandomBytes());
         buffer.write(((RecordProtocol)super._recordProtocol).getServerRandom());
         buffer.write(0);
         boolean found = false;
         String algorithmToLookFor = null;
         Certificate clientCertificate = ((SSLRecordProtocol)super._recordProtocol).getLocalCertificate();
         if (clientCertificate != null) {
            algorithmToLookFor = clientCertificate.getPublicKeyAlgorithm();
         }

         int length = this._cipherSuites.length;

         for (int i = 0; i < length; i++) {
            if (!TLSCipherSuites.notSupported(this._cipherSuites[i])
               && StringUtilities.strEqual(TLSCipherSuites.getPublicKeyAlgorithm(this._cipherSuites[i]), algorithmToLookFor)) {
               super._cipherSuite = this._cipherSuites[i];
               found = true;
               break;
            }
         }

         if (!found && algorithmToLookFor != null) {
            for (int i = 0; i < length; i++) {
               if (!TLSCipherSuites.notSupported(this._cipherSuites[i]) && TLSCipherSuites.getPublicKeyAlgorithm(this._cipherSuites[i]) == null) {
                  super._cipherSuite = this._cipherSuites[i];
                  found = true;
                  break;
               }
            }
         }

         if (!found) {
            ((TLSAlertProtocol)super._alertProtocol).sendAlertMessage((byte)3, (byte)40);
            throw new Object((Exception)(new Object((byte)3, (byte)40)));
         }

         buffer.writeByte(super._cipherSuite >>> 8);
         buffer.writeByte(super._cipherSuite);
         buffer.writeByte(0);
         SSLConnectionState readState = super._factory.getConnectionState(super._cipherSuite);
         SSLConnectionState writeState = super._factory.getConnectionState(super._cipherSuite);
         ((SSLRecordProtocol)super._recordProtocol).setPendingRead(readState);
         ((SSLRecordProtocol)super._recordProtocol).setPendingWrite(writeState);
         this.write(buffer);
      } catch (Throwable var10) {
         throw new Object(e);
      }
   }

   public void serverCertificate() {
      super._types = new byte[]{1};
      KeyStore keyStore = TrustedKeyStore.getInstance();
      Enumeration enumeration = keyStore.elements();
      super._dn = new Object[0];

      while (enumeration.hasMoreElements()) {
         KeyStoreData data = (KeyStoreData)enumeration.nextElement();
         Certificate cert = data.getCertificate();
         if (cert != null && cert.getType().equals("X509")) {
            Array.resize(super._dn, super._dn.length + 1);
            super._dn[super._dn.length - 1] = cert.getSubject();
         }
      }

      try {
         DataBuffer buffer = (DataBuffer)(new Object());
         buffer.write(11);
         TLSUtilities.writeIntegerThreeBytes(buffer, 0);
         Certificate[] certChain = null;
         Certificate clientCertificate = ((SSLRecordProtocol)super._recordProtocol).getLocalCertificate();
         if (clientCertificate != null && SSLHandshakeUtilities.checkCertificateType(clientCertificate, super._types)) {
            super._privateKey = ((SSLRecordProtocol)super._recordProtocol).getLocalPrivateKey();
            if (super._privateKey != null) {
               certChain = CertificateUtilities.buildCertificateChain(clientCertificate, DeviceKeyStore.getInstance());
               SSLHandshakeUtilities.writeClientCertificate(buffer, certChain);
            }
         }

         this.write(buffer);
         super._clientCertificateSent = true;
      } finally {
         TLSUtilities.sendAlertAndThrowException((AlertProtocolMethods)super._alertProtocol, (byte)50);
         return;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void serverKeyExchange() {
      try {
         DataBuffer buffer = (DataBuffer)(new Object());
         buffer.write(12);
         TLSUtilities.writeIntegerThreeBytes(buffer, 0);
         ConnectionState connectionState = ((SSLRecordProtocol)super._recordProtocol).getPendingWrite();
         byte keyExchangeAlgorithm = connectionState.getKeyExchangeAlgorithm();
         switch (keyExchangeAlgorithm) {
            case 1:
            case 3:
            case 4:
            default:
               TLSUtilities.sendAlertAndThrowException((AlertProtocolMethods)super._alertProtocol, (byte)10);
               break;
            case 2:
               boolean var30 = false /* VF: Semaphore variable */;

               label177:
               try {
                  var30 = true;
                  if (!(super._privateKey instanceof Object)) {
                     TLSUtilities.sendAlertAndThrowException((AlertProtocolMethods)super._alertProtocol, (byte)47);
                  }

                  RSACryptoSystem system = (RSACryptoSystem)(new Object(1024));
                  RSAKeyPair keyPair = system.createRSAKeyPair();
                  RSAPublicKey pubKey = keyPair.getRSAPublicKey();
                  byte[] n = pubKey.getN();
                  int mLength1 = n.length >> 8;
                  int mLength2 = n.length & 0xFF;
                  buffer.writeShort(n.length);
                  buffer.write(n);
                  byte[] e = pubKey.getE();
                  int eLength1 = e.length >> 8;
                  int eLength2 = e.length & 0xFF;
                  buffer.writeShort(e.length);
                  buffer.write(e);
                  SSLDigest digest = new SSLDigest();
                  PKCS1SignatureSigner signer = (PKCS1SignatureSigner)(new Object((RSAPrivateKey)super._privateKey, digest, false));
                  signer.update(((RecordProtocol)super._recordProtocol).getClientRandom());
                  signer.update(((RecordProtocol)super._recordProtocol).getServerRandom());
                  signer.update(mLength1);
                  signer.update(mLength2);
                  signer.update(n);
                  signer.update(eLength1);
                  signer.update(eLength2);
                  signer.update(e);
                  byte[] signature = new byte[n.length];
                  signer.sign(signature, 0);
                  buffer.writeShort(signature.length);
                  buffer.write(signature);
                  super._privateKey = keyPair.getRSAPrivateKey();
                  super._publicKey = pubKey;
                  var30 = false;
                  break;
               } finally {
                  if (var30) {
                     TLSUtilities.sendAlertAndThrowException((AlertProtocolMethods)super._alertProtocol, (byte)51);
                     break label177;
                  }
               }
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 11:
               boolean var34 = false /* VF: Semaphore variable */;

               label174:
               try {
                  var34 = true;
                  DHCryptoSystem e = new Object();
                  DHKeyPair dhKeys = ((DHCryptoSystem)e).createDHKeyPair();
                  DHPublicKey dhPub = dhKeys.getDHPublicKey();
                  byte[] p = ((DHCryptoSystem)e).getP();
                  int pLength1 = p.length >> 8;
                  int pLength2 = p.length & 0xFF;
                  buffer.writeShort(p.length);
                  buffer.write(p);
                  byte[] g = ((DHCryptoSystem)e).getG();
                  int gLength1 = g.length >> 8;
                  int gLength2 = g.length & 0xFF;
                  buffer.writeShort(g.length);
                  buffer.write(g);
                  byte[] y = dhPub.getPublicKeyData();
                  int yLength1 = y.length >> 8;
                  int yLength2 = y.length & 0xFF;
                  buffer.writeShort(y.length);
                  buffer.write(y);
                  if (keyExchangeAlgorithm == 6 || keyExchangeAlgorithm == 7 || keyExchangeAlgorithm == 8 || keyExchangeAlgorithm == 9) {
                     PrivateKey priv = ((SSLRecordProtocol)super._recordProtocol).getLocalPrivateKey();
                     SignatureSigner signer;
                     if (keyExchangeAlgorithm != 6 && keyExchangeAlgorithm != 7) {
                        if (!(priv instanceof Object)) {
                           TLSUtilities.sendAlertAndThrowException((AlertProtocolMethods)super._alertProtocol, (byte)47);
                        }

                        signer = (SignatureSigner)(new Object((RSAPrivateKey)priv, new SSLDigest(), false));
                     } else {
                        if (!(priv instanceof Object)) {
                           TLSUtilities.sendAlertAndThrowException((AlertProtocolMethods)super._alertProtocol, (byte)47);
                        }

                        signer = (SignatureSigner)(new Object((DSAPrivateKey)priv));
                     }

                     signer.update(((RecordProtocol)super._recordProtocol).getClientRandom());
                     signer.update(((RecordProtocol)super._recordProtocol).getServerRandom());
                     signer.update(pLength1);
                     signer.update(pLength2);
                     signer.update(p);
                     signer.update(gLength1);
                     signer.update(gLength2);
                     signer.update(g);
                     signer.update(yLength1);
                     signer.update(yLength2);
                     signer.update(y);
                     byte[] formattedSignature;
                     if (keyExchangeAlgorithm != 6 && keyExchangeAlgorithm != 7) {
                        PKCS1SignatureSigner rsaSigner = (PKCS1SignatureSigner)signer;
                        formattedSignature = new byte[rsaSigner.getLength()];
                        rsaSigner.sign(formattedSignature, 0);
                     } else {
                        DSASignatureSigner dsaSigner = (DSASignatureSigner)signer;
                        byte[] r = new byte[dsaSigner.getRLength()];
                        byte[] s = new byte[dsaSigner.getSLength()];
                        dsaSigner.sign(r, 0, s, 0);
                        ASN1OutputStream sigOut = new ASN1OutputStream();
                        ASN1OutputStream sigSequence = new ASN1OutputStream();
                        sigSequence.writeInteger(r);
                        sigSequence.writeInteger(s);
                        sigOut.writeSequence(sigSequence);
                        formattedSignature = sigOut.toByteArray();
                     }

                     TLSUtilities.writeIntegerTwoBytes(buffer, formattedSignature.length);
                     buffer.write(formattedSignature);
                  }

                  super._publicKey = dhPub;
                  super._privateKey = dhKeys.getPrivateKey();
                  var34 = false;
                  break;
               } finally {
                  if (var34) {
                     TLSUtilities.sendAlertAndThrowException((AlertProtocolMethods)super._alertProtocol, (byte)51);
                     break label174;
                  }
               }
            case 0:
            case 10:
               TLSUtilities.sendAlertAndThrowException((AlertProtocolMethods)super._alertProtocol, (byte)47);
         }

         this.write(buffer);
         System.out.println("SSL:->CKE");
      } finally {
         TLSUtilities.sendAlertAndThrowException((AlertProtocolMethods)super._alertProtocol, (byte)50);
         return;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void serverCertificateRequest() {
      try {
         DataBuffer buffer = (DataBuffer)(new Object());
         buffer.write(13);
         buffer.writeByte(0);
         buffer.writeByte(0);
         buffer.writeByte(0);
         buffer.write(super._types.length);
         buffer.write(super._types);
         buffer.write(0);
         buffer.write(0);
         int dnLength = super._dn.length;
         byte[] tempDN = null;

         for (int i = 0; i < dnLength; i++) {
            tempDN = super._dn[i].getEncoding();
            buffer.writeByte(tempDN.length >> 8 & 0xFF);
            buffer.writeByte(tempDN.length & 0xFF);
            buffer.write(tempDN);
         }

         int length = buffer.getLength() - (7 + super._types.length);
         byte[] lengthBytes = new byte[]{(byte)(length >>> 8), (byte)length};
         buffer.rewind();
         buffer.setPosition(6);
         buffer.write(lengthBytes);
         this.write(buffer);
      } catch (Throwable var7) {
         throw new Object(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void serverHelloDone() {
      try {
         DataBuffer buffer = (DataBuffer)(new Object());
         buffer.write(14);
         buffer.writeByte(0);
         buffer.writeByte(0);
         buffer.writeByte(0);
         this.write(buffer);
      } catch (Throwable var3) {
         throw new Object(e);
      }
   }

   public void clientCertificate(DataBuffer buffer) {
      super.serverCertificate(buffer);
   }

   public void clientKeyExchange(DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 004: instanceof java/lang/Object
      // 007: ifne 00d
      // 00a: goto 0fe
      // 00d: bipush 0
      // 00e: istore 2
      // 00f: aload 0
      // 010: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._remoteVersion I
      // 013: sipush 769
      // 016: if_icmpne 02f
      // 019: aload 1
      // 01a: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 01d: istore 3
      // 01e: aload 1
      // 01f: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 022: istore 4
      // 024: iload 3
      // 025: bipush 8
      // 027: ishl
      // 028: iload 4
      // 02a: iadd
      // 02b: istore 2
      // 02c: goto 041
      // 02f: aload 0
      // 030: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 033: invokeinterface net/rim/device/api/crypto/PrivateKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 038: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 03d: bipush 8
      // 03f: idiv
      // 040: istore 2
      // 041: iload 2
      // 042: newarray 8
      // 044: astore 3
      // 045: aload 1
      // 046: aload 3
      // 047: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 04a: new java/lang/Object
      // 04d: dup
      // 04e: aload 0
      // 04f: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 052: checkcast java/lang/Object
      // 055: invokespecial net/rim/device/api/crypto/RSADecryptorEngine.<init> (Lnet/rim/device/api/crypto/RSAPrivateKey;)V
      // 058: astore 4
      // 05a: new java/lang/Object
      // 05d: dup
      // 05e: aload 4
      // 060: invokespecial net/rim/device/api/crypto/PKCS1UnformatterEngine.<init> (Lnet/rim/device/api/crypto/PrivateKeyDecryptorEngine;)V
      // 063: astore 5
      // 065: new java/lang/Object
      // 068: dup
      // 069: aload 3
      // 06a: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 06d: astore 6
      // 06f: new java/lang/Object
      // 072: dup
      // 073: aload 5
      // 075: aload 6
      // 077: invokespecial net/rim/device/api/crypto/BlockDecryptor.<init> (Lnet/rim/device/api/crypto/BlockUnformatterEngine;Ljava/io/InputStream;)V
      // 07a: astore 7
      // 07c: aload 0
      // 07d: bipush 0
      // 07e: newarray 8
      // 080: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 083: bipush 10
      // 085: newarray 8
      // 087: astore 8
      // 089: aload 7
      // 08b: aload 8
      // 08d: bipush 0
      // 08e: bipush 10
      // 090: invokevirtual net/rim/device/api/crypto/BlockDecryptor.read ([BII)I
      // 093: istore 9
      // 095: iload 9
      // 097: ifge 09d
      // 09a: goto 0c3
      // 09d: aload 0
      // 09e: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 0a1: iload 9
      // 0a3: aload 0
      // 0a4: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 0a7: arraylength
      // 0a8: iadd
      // 0a9: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 0ac: aload 8
      // 0ae: bipush 0
      // 0af: aload 0
      // 0b0: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 0b3: aload 0
      // 0b4: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 0b7: arraylength
      // 0b8: iload 9
      // 0ba: isub
      // 0bb: iload 9
      // 0bd: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 0c0: goto 089
      // 0c3: aload 0
      // 0c4: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 0c7: bipush 0
      // 0c8: baload
      // 0c9: bipush 3
      // 0cb: if_icmpne 0df
      // 0ce: aload 0
      // 0cf: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 0d2: bipush 1
      // 0d3: baload
      // 0d4: aload 0
      // 0d5: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._remoteVersion I
      // 0d8: sipush 255
      // 0db: iand
      // 0dc: if_icmpeq 0fd
      // 0df: aload 0
      // 0e0: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Ljava/lang/Object;
      // 0e3: bipush 3
      // 0e5: bipush 21
      // 0e7: invokevirtual net/rim/device/api/crypto/tls/tls10/TLSAlertProtocol.sendAlertMessage (BB)V
      // 0ea: new java/lang/Object
      // 0ed: dup
      // 0ee: new java/lang/Object
      // 0f1: dup
      // 0f2: bipush 3
      // 0f4: bipush 21
      // 0f6: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // 0f9: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 0fc: athrow
      // 0fd: return
      // 0fe: aload 0
      // 0ff: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 102: instanceof java/lang/Object
      // 105: ifeq 140
      // 108: aload 1
      // 109: invokevirtual net/rim/device/api/util/DataBuffer.readShort ()S
      // 10c: istore 2
      // 10d: iload 2
      // 10e: newarray 8
      // 110: astore 3
      // 111: aload 1
      // 112: aload 3
      // 113: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 116: new java/lang/Object
      // 119: dup
      // 11a: aload 0
      // 11b: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 11e: invokeinterface net/rim/device/api/crypto/PrivateKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 123: checkcast java/lang/Object
      // 126: aload 3
      // 127: invokespecial net/rim/device/api/crypto/DHPublicKey.<init> (Lnet/rim/device/api/crypto/DHCryptoSystem;[B)V
      // 12a: astore 4
      // 12c: aload 0
      // 12d: aload 0
      // 12e: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 131: checkcast java/lang/Object
      // 134: aload 4
      // 136: bipush 0
      // 137: invokestatic net/rim/device/api/crypto/DHKeyAgreement.generateSharedSecret (Lnet/rim/device/api/crypto/DHPrivateKey;Lnet/rim/device/api/crypto/DHPublicKey;Z)[B
      // 13a: putfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._premasterSecret [B
      // 13d: goto 170
      // 140: aload 0
      // 141: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 144: instanceof java/lang/Object
      // 147: ifeq 170
      // 14a: new java/lang/Object
      // 14d: dup
      // 14e: invokespecial java/lang/RuntimeException.<init> ()V
      // 151: athrow
      // 152: astore 2
      // 153: new java/lang/Object
      // 156: dup
      // 157: aload 2
      // 158: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 15b: athrow
      // 15c: astore 2
      // 15d: new java/lang/Object
      // 160: dup
      // 161: aload 2
      // 162: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 165: athrow
      // 166: astore 2
      // 167: new java/lang/Object
      // 16a: dup
      // 16b: aload 2
      // 16c: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 16f: athrow
      // 170: return
      // try (0 -> 125): 165 null
      // try (126 -> 165): 165 null
      // try (0 -> 125): 171 null
      // try (126 -> 165): 171 null
      // try (0 -> 125): 177 null
      // try (126 -> 165): 177 null
   }

   public void clientCertificateVerify(DataBuffer param1) {
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
      // 07: ifeq 77
      // 0a: aload 1
      // 0b: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 0e: istore 2
      // 0f: aload 1
      // 10: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 13: istore 3
      // 14: iload 2
      // 15: bipush 8
      // 17: ishl
      // 18: iload 3
      // 19: iadd
      // 1a: istore 4
      // 1c: iload 4
      // 1e: newarray 8
      // 20: astore 5
      // 22: aload 1
      // 23: aload 5
      // 25: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 28: new net/rim/device/api/crypto/tls/ssl30/SSLDigest
      // 2b: dup
      // 2c: aload 0
      // 2d: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifyMD5Hash Lnet/rim/device/api/crypto/MD5Digest;
      // 30: aload 0
      // 31: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifySHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 34: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLDigest.<init> (Lnet/rim/device/api/crypto/MD5Digest;Lnet/rim/device/api/crypto/SHA1Digest;)V
      // 37: astore 6
      // 39: new java/lang/Object
      // 3c: dup
      // 3d: aload 0
      // 3e: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 41: checkcast java/lang/Object
      // 44: aload 6
      // 46: aload 5
      // 48: bipush 0
      // 49: invokespecial net/rim/device/api/crypto/PKCS1SignatureVerifier.<init> (Lnet/rim/device/api/crypto/RSAPublicKey;Lnet/rim/device/api/crypto/Digest;[BI)V
      // 4c: astore 7
      // 4e: aload 7
      // 50: invokevirtual net/rim/device/api/crypto/PKCS1SignatureVerifier.verify ()Z
      // 53: ifeq 59
      // 56: goto e9
      // 59: aload 0
      // 5a: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Ljava/lang/Object;
      // 5d: bipush 3
      // 5f: bipush 40
      // 61: invokevirtual net/rim/device/api/crypto/tls/tls10/TLSAlertProtocol.sendAlertMessage (BB)V
      // 64: new java/lang/Object
      // 67: dup
      // 68: new java/lang/Object
      // 6b: dup
      // 6c: bipush 3
      // 6e: bipush 40
      // 70: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // 73: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // 76: athrow
      // 77: aload 0
      // 78: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 7b: instanceof java/lang/Object
      // 7e: ifeq 84
      // 81: goto e9
      // 84: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 87: ldc_w "Public key for certificate is of the wrong type."
      // 8a: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 8d: aload 0
      // 8e: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Ljava/lang/Object;
      // 91: bipush 3
      // 93: bipush 47
      // 95: invokevirtual net/rim/device/api/crypto/tls/tls10/TLSAlertProtocol.sendAlertMessage (BB)V
      // 98: new java/lang/Object
      // 9b: dup
      // 9c: new java/lang/Object
      // 9f: dup
      // a0: bipush 3
      // a2: bipush 47
      // a4: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // a7: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // aa: athrow
      // ab: astore 2
      // ac: aload 0
      // ad: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Ljava/lang/Object;
      // b0: bipush 3
      // b2: bipush 51
      // b4: invokevirtual net/rim/device/api/crypto/tls/tls10/TLSAlertProtocol.sendAlertMessage (BB)V
      // b7: new java/lang/Object
      // ba: dup
      // bb: new java/lang/Object
      // be: dup
      // bf: bipush 3
      // c1: bipush 51
      // c3: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // c6: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // c9: athrow
      // ca: astore 2
      // cb: aload 0
      // cc: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Ljava/lang/Object;
      // cf: bipush 3
      // d1: bipush 50
      // d3: invokevirtual net/rim/device/api/crypto/tls/tls10/TLSAlertProtocol.sendAlertMessage (BB)V
      // d6: new java/lang/Object
      // d9: dup
      // da: new java/lang/Object
      // dd: dup
      // de: bipush 3
      // e0: bipush 50
      // e2: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // e5: invokespecial net/rim/device/cldc/io/ssl/TLSException.<init> (Ljava/lang/Exception;)V
      // e8: athrow
      // e9: return
      // try (0 -> 80): 80 null
      // try (0 -> 80): 95 null
   }

   @Override
   public void changeCipherSpec(DataBuffer buffer) {
      try {
         if (buffer != null) {
            ((TLSAlertProtocol)super._alertProtocol).sendAlertMessage((byte)3, (byte)10);
            throw new Object((Exception)(new Object("Change cipher spec message should never be received at this layer.")));
         }

         System.out.println("SSL: Sent change cipher spec packet.");
         ((SSLRecordProtocol)super._recordProtocol).getChangeCipherSpecProtocol().sendChangeCipherSpecMessage();
         ((SSLRecordProtocol)super._recordProtocol).changeWriteCipherSpec();
      } finally {
         ((TLSAlertProtocol)super._alertProtocol).sendAlertMessage((byte)3, (byte)50);
         throw new Object((Exception)(new Object((byte)3, (byte)50)));
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void accept() {
      try {
         ((SSLRecordProtocol)super._recordProtocol).setClientMode(false);
         ((SSLRecordProtocol)super._recordProtocol).setState(2);
         DataBuffer[] buffer = null;
         buffer = new Object[1];
         int type = this.read(buffer);
         if (type != 1 && type != 24) {
            ((TLSAlertProtocol)super._alertProtocol).sendAlertMessage((byte)3, (byte)10);
            throw new Object((byte)3, (byte)10);
         }

         this.clientHello(buffer[0], type == 24);
         this.serverHello();
         boolean sendCert = false;
         boolean sendKeyExchange = false;
         Certificate cert = ((SSLRecordProtocol)super._recordProtocol).getLocalCertificate();
         ConnectionState connectionState = ((SSLRecordProtocol)super._recordProtocol).getPendingWrite();
         byte keyExchangeAlgorithm = connectionState.getKeyExchangeAlgorithm();
         switch (keyExchangeAlgorithm) {
            case 0:
            case 5:
               sendKeyExchange = true;
               break;
            case 1:
            case 3:
            case 4:
            default:
               sendCert = cert != null;
               sendKeyExchange = !sendCert;
               break;
            case 2:
               sendCert = cert != null;
               if (cert != null) {
                  boolean var13 = false /* VF: Semaphore variable */;

                  label135:
                  try {
                     var13 = true;
                     sendKeyExchange = cert.getPublicKey().getCryptoSystem().getBitLength() > 512;
                     var13 = false;
                  } finally {
                     if (var13) {
                        TLSUtilities.sendAlertAndThrowException((AlertProtocolMethods)super._alertProtocol, (byte)42);
                        break label135;
                     }
                  }
               }
               break;
            case 6:
            case 7:
            case 8:
            case 9:
               sendCert = cert != null;
               sendKeyExchange = true;
         }

         if (sendCert) {
            this.serverCertificate();
         }

         if (sendKeyExchange) {
            this.serverKeyExchange();
         }

         this.serverHelloDone();
         type = this.read(buffer);
         if (type != 16) {
            ((TLSAlertProtocol)super._alertProtocol).sendAlertMessage((byte)3, (byte)10);
            throw new Object((byte)3, (byte)10);
         }

         this.clientKeyExchange(buffer[0]);
         this.updateConnectionState();
         type = this.read(buffer);
         if (type != 20) {
            ((TLSAlertProtocol)super._alertProtocol).sendAlertMessage((byte)3, (byte)10);
            throw new Object((byte)3, (byte)10);
         }

         this.finished(buffer[0]);
         this.changeCipherSpec(null);
         this.finished(null);
         ((SSLRecordProtocol)super._recordProtocol).setState(1);
         System.out.println("SSL: Client connection has been accepted.  Handshake complete!");
      } finally {
         throw new Object((Exception)(new Object((byte)3, (byte)40)));
      }
   }

   @Override
   public int read(DataBuffer[] buffer) {
      int type = 0;
      if (super._dataBuffer.getPosition() == super._dataBuffer.getLength()) {
         type = ((SSLRecordProtocol)super._recordProtocol).read(super._dataBuffer);
         if (type != 22 && type != 24) {
            ((TLSAlertProtocol)super._alertProtocol).sendAlertMessage((byte)3, (byte)10);
            throw new Object((Exception)(new Object((byte)3, (byte)10)));
         }

         super._dataBuffer.rewind();
         int tempType = super._dataBuffer.readUnsignedByte();
         if (super._resumption) {
            if (tempType != 20) {
               super._dataBuffer.rewind();
               byte[] hashBytes = new byte[super._dataBuffer.getLength()];
               super._dataBuffer.readFully(hashBytes);
               this.updateHash(hashBytes);
            }
         } else {
            super._dataBuffer.rewind();
            byte[] hashBytes = new byte[super._dataBuffer.getLength()];
            super._dataBuffer.readFully(hashBytes);
            if (tempType == 20) {
               super._serverMD5Hash.update(hashBytes);
               super._serverSHAHash.update(hashBytes);
            } else if (tempType == 15) {
               super._serverMD5Hash.update(hashBytes);
               super._serverSHAHash.update(hashBytes);
               super._clientMD5Hash.update(hashBytes);
               super._clientSHAHash.update(hashBytes);
            } else {
               this.updateHash(hashBytes);
            }
         }

         super._dataBuffer.rewind();
      }

      if (type == 24) {
         int v2Type = super._dataBuffer.readUnsignedByte();
         if (v2Type != 1) {
            ((TLSAlertProtocol)super._alertProtocol).sendAlertMessage((byte)3, (byte)40);
            throw new Object((Exception)(new Object((byte)3, (byte)40)));
         } else {
            buffer[0] = (DataBuffer)(new Object(super._dataBuffer, super._dataBuffer.getLength() - super._dataBuffer.getPosition()));
            return type;
         }
      } else {
         type = super._dataBuffer.readUnsignedByte();
         int length1 = super._dataBuffer.readUnsignedByte();
         int length2 = super._dataBuffer.readUnsignedByte();
         int length3 = super._dataBuffer.readUnsignedByte();
         int length = (length1 << 16) + (length2 << 8) + length3;
         buffer[0] = (DataBuffer)(new Object(super._dataBuffer, length));
         return type;
      }
   }

   @Override
   public void write(DataBuffer buffer) {
      int length = buffer.getLength() - 4;
      byte[] lengthBytes = new byte[]{(byte)(length >>> 16), (byte)(length >>> 8), (byte)length};
      buffer.rewind();
      buffer.setPosition(1);
      buffer.write(lengthBytes);
      buffer.rewind();
      byte[] hashBytes = new byte[buffer.getLength()];
      buffer.readFully(hashBytes);
      this.updateHash(hashBytes);
      buffer.rewind();
      ((SSLRecordProtocol)super._recordProtocol).write(22, buffer);
   }
}
