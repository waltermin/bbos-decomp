package net.rim.device.api.crypto.tls.tls10;

import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.tls.ConnectionState;
import net.rim.device.api.crypto.tls.KeyMaterial;
import net.rim.device.api.crypto.tls.TLSUtilities;
import net.rim.device.api.crypto.tls.ssl30.SSLHandshakeProtocol;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.vm.Array;

public class TLSHandshakeProtocol extends SSLHandshakeProtocol {
   protected TLSRecordProtocol _recordProtocol;
   protected TLSAlertProtocol _alertProtocol;

   public TLSHandshakeProtocol(TLSRecordProtocol recordProtocol) {
      super(recordProtocol);
      this._recordProtocol = recordProtocol;
      this._alertProtocol = new TLSAlertProtocol(this._recordProtocol);
      super._factory = new TLSCipherSuiteFactory(this._recordProtocol);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void clientKeyExchange() {
      if (super._remoteVersion != this.getLocalVersion()) {
         super.clientKeyExchange();
      } else {
         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            ConnectionState e = this._recordProtocol.getPendingWrite();
            byte keyExchangeAlgorithm = ((ConnectionState)e).getKeyExchangeAlgorithm();
            switch (keyExchangeAlgorithm) {
               case 1:
               case 2:
                  DataBuffer buffer = (DataBuffer)(new Object());
                  buffer.write(16);
                  TLSUtilities.writeIntegerThreeBytes(buffer, 0);
                  byte[] premasterSecret = this.generatePremasterSecret();
                  TLSUtilities.writeIntegerTwoBytes(buffer, premasterSecret.length);
                  buffer.write(premasterSecret);
                  this.write(buffer);
                  System.out.println("TLS:->CKE");
                  var6 = false;
                  return;
               case 12:
               case 13:
               case 16:
               case 17:
                  throw new Object();
               default:
                  super.clientKeyExchange();
            }
         } finally {
            if (var6) {
               TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)50);
               return;
            }
         }
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
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._remoteVersion I
      // 004: aload 0
      // 005: invokevirtual net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol.getLocalVersion ()I
      // 008: if_icmpeq 010
      // 00b: aload 0
      // 00c: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.clientCertificateVerify ()V
      // 00f: return
      // 010: new java/lang/Object
      // 013: dup
      // 014: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 017: astore 1
      // 018: aload 1
      // 019: bipush 15
      // 01b: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 01e: aload 1
      // 01f: bipush 0
      // 020: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerThreeBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 023: aload 0
      // 024: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 027: instanceof java/lang/Object
      // 02a: ifeq 070
      // 02d: new net/rim/device/api/crypto/tls/ssl30/SSLDigest
      // 030: dup
      // 031: aload 0
      // 032: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifyMD5Hash Lnet/rim/device/api/crypto/MD5Digest;
      // 035: aload 0
      // 036: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifySHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 039: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLDigest.<init> (Lnet/rim/device/api/crypto/MD5Digest;Lnet/rim/device/api/crypto/SHA1Digest;)V
      // 03c: astore 2
      // 03d: new java/lang/Object
      // 040: dup
      // 041: aload 0
      // 042: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 045: checkcast java/lang/Object
      // 048: aload 2
      // 049: bipush 0
      // 04a: invokespecial net/rim/device/api/crypto/PKCS1SignatureSigner.<init> (Lnet/rim/device/api/crypto/RSAPrivateKey;Lnet/rim/device/api/crypto/Digest;Z)V
      // 04d: astore 3
      // 04e: aload 3
      // 04f: invokevirtual net/rim/device/api/crypto/PKCS1SignatureSigner.getLength ()I
      // 052: istore 4
      // 054: iload 4
      // 056: newarray 8
      // 058: astore 5
      // 05a: aload 3
      // 05b: aload 5
      // 05d: bipush 0
      // 05e: invokevirtual net/rim/device/api/crypto/PKCS1SignatureSigner.sign ([BI)V
      // 061: aload 1
      // 062: iload 4
      // 064: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 067: aload 1
      // 068: aload 5
      // 06a: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 06d: goto 0fc
      // 070: aload 0
      // 071: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 074: instanceof java/lang/Object
      // 077: ifeq 0eb
      // 07a: new java/lang/Object
      // 07d: dup
      // 07e: aload 0
      // 07f: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 082: checkcast java/lang/Object
      // 085: aload 0
      // 086: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._verifySHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 089: invokespecial net/rim/device/api/crypto/DSASignatureSigner.<init> (Lnet/rim/device/api/crypto/DSAPrivateKey;Lnet/rim/device/api/crypto/Digest;)V
      // 08c: astore 2
      // 08d: aload 2
      // 08e: invokevirtual net/rim/device/api/crypto/DSASignatureSigner.getRLength ()I
      // 091: istore 3
      // 092: aload 2
      // 093: invokevirtual net/rim/device/api/crypto/DSASignatureSigner.getSLength ()I
      // 096: istore 4
      // 098: iload 3
      // 099: newarray 8
      // 09b: astore 5
      // 09d: iload 4
      // 09f: newarray 8
      // 0a1: astore 6
      // 0a3: aload 2
      // 0a4: aload 5
      // 0a6: bipush 0
      // 0a7: aload 6
      // 0a9: bipush 0
      // 0aa: invokevirtual net/rim/device/api/crypto/DSASignatureSigner.sign ([BI[BI)V
      // 0ad: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 0b0: dup
      // 0b1: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 0b4: astore 7
      // 0b6: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 0b9: dup
      // 0ba: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 0bd: astore 8
      // 0bf: aload 8
      // 0c1: aload 5
      // 0c3: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger ([B)V
      // 0c6: aload 8
      // 0c8: aload 6
      // 0ca: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger ([B)V
      // 0cd: aload 7
      // 0cf: aload 8
      // 0d1: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 0d4: aload 7
      // 0d6: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 0d9: astore 9
      // 0db: aload 1
      // 0dc: aload 9
      // 0de: arraylength
      // 0df: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerTwoBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 0e2: aload 1
      // 0e3: aload 9
      // 0e5: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 0e8: goto 0fc
      // 0eb: aload 0
      // 0ec: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._privateKey Lnet/rim/device/api/crypto/PrivateKey;
      // 0ef: ifnonnull 0f3
      // 0f2: return
      // 0f3: aload 0
      // 0f4: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/tls10/TLSAlertProtocol;
      // 0f7: bipush 47
      // 0f9: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 0fc: aload 0
      // 0fd: aload 1
      // 0fe: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.write (Lnet/rim/device/api/util/DataBuffer;)V
      // 101: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 104: ldc_w "TLS:->CV"
      // 107: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 10a: return
      // 10b: astore 1
      // 10c: aload 0
      // 10d: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/tls10/TLSAlertProtocol;
      // 110: bipush 51
      // 112: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 115: return
      // 116: astore 1
      // 117: aload 0
      // 118: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/tls10/TLSAlertProtocol;
      // 11b: bipush 51
      // 11d: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 120: return
      // try (8 -> 118): 130 null
      // try (119 -> 129): 130 null
      // try (8 -> 118): 136 null
      // try (119 -> 129): 136 null
   }

   @Override
   public void finished(DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._remoteVersion I
      // 004: aload 0
      // 005: invokevirtual net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol.getLocalVersion ()I
      // 008: if_icmpeq 011
      // 00b: aload 0
      // 00c: aload 1
      // 00d: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.finished (Lnet/rim/device/api/util/DataBuffer;)V
      // 010: return
      // 011: aload 0
      // 012: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._recordProtocol Lnet/rim/device/api/crypto/tls/tls10/TLSRecordProtocol;
      // 015: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLRecordProtocol.getClientMode ()Z
      // 018: ifeq 043
      // 01b: aload 1
      // 01c: ifnonnull 031
      // 01f: aload 0
      // 020: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._clientMD5Hash Lnet/rim/device/api/crypto/MD5Digest;
      // 023: astore 2
      // 024: aload 0
      // 025: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._clientSHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 028: astore 3
      // 029: ldc_w "client finished"
      // 02c: astore 4
      // 02e: goto 068
      // 031: aload 0
      // 032: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._serverMD5Hash Lnet/rim/device/api/crypto/MD5Digest;
      // 035: astore 2
      // 036: aload 0
      // 037: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._serverSHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 03a: astore 3
      // 03b: ldc_w "server finished"
      // 03e: astore 4
      // 040: goto 068
      // 043: aload 1
      // 044: ifnonnull 059
      // 047: aload 0
      // 048: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._serverMD5Hash Lnet/rim/device/api/crypto/MD5Digest;
      // 04b: astore 2
      // 04c: aload 0
      // 04d: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._serverSHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 050: astore 3
      // 051: ldc_w "server finished"
      // 054: astore 4
      // 056: goto 068
      // 059: aload 0
      // 05a: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._clientMD5Hash Lnet/rim/device/api/crypto/MD5Digest;
      // 05d: astore 2
      // 05e: aload 0
      // 05f: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._clientSHAHash Lnet/rim/device/api/crypto/SHA1Digest;
      // 062: astore 3
      // 063: ldc_w "client finished"
      // 066: astore 4
      // 068: aload 1
      // 069: ifnonnull 0c6
      // 06c: new java/lang/Object
      // 06f: dup
      // 070: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 073: astore 1
      // 074: aload 1
      // 075: bipush 20
      // 077: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 07a: aload 1
      // 07b: bipush 0
      // 07c: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.writeIntegerThreeBytes (Lnet/rim/device/api/util/DataBuffer;I)V
      // 07f: bipush 36
      // 081: newarray 8
      // 083: astore 5
      // 085: aload 2
      // 086: aload 5
      // 088: bipush 0
      // 089: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ([BI)I
      // 08c: pop
      // 08d: aload 3
      // 08e: aload 5
      // 090: bipush 16
      // 092: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ([BI)I
      // 095: pop
      // 096: new net/rim/device/api/crypto/tls/tls10/TLSPRF
      // 099: dup
      // 09a: aload 0
      // 09b: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._masterSecret [B
      // 09e: aload 4
      // 0a0: invokevirtual java/lang/String.getBytes ()[B
      // 0a3: aload 5
      // 0a5: invokespecial net/rim/device/api/crypto/tls/tls10/TLSPRF.<init> ([B[B[B)V
      // 0a8: astore 6
      // 0aa: aload 1
      // 0ab: aload 6
      // 0ad: bipush 12
      // 0af: invokevirtual net/rim/device/api/crypto/tls/tls10/TLSPRF.getBytes (I)[B
      // 0b2: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 0b5: aload 0
      // 0b6: aload 1
      // 0b7: invokevirtual net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.write (Lnet/rim/device/api/util/DataBuffer;)V
      // 0ba: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 0bd: ldc_w "TLS:->F"
      // 0c0: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 0c3: goto 130
      // 0c6: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 0c9: ldc_w "TLS:<-F"
      // 0cc: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 0cf: bipush 36
      // 0d1: newarray 8
      // 0d3: astore 5
      // 0d5: aload 2
      // 0d6: aload 5
      // 0d8: bipush 0
      // 0d9: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ([BI)I
      // 0dc: pop
      // 0dd: aload 3
      // 0de: aload 5
      // 0e0: bipush 16
      // 0e2: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ([BI)I
      // 0e5: pop
      // 0e6: new net/rim/device/api/crypto/tls/tls10/TLSPRF
      // 0e9: dup
      // 0ea: aload 0
      // 0eb: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._masterSecret [B
      // 0ee: aload 4
      // 0f0: invokevirtual java/lang/String.getBytes ()[B
      // 0f3: aload 5
      // 0f5: invokespecial net/rim/device/api/crypto/tls/tls10/TLSPRF.<init> ([B[B[B)V
      // 0f8: astore 6
      // 0fa: aload 6
      // 0fc: bipush 12
      // 0fe: invokevirtual net/rim/device/api/crypto/tls/tls10/TLSPRF.getBytes (I)[B
      // 101: astore 7
      // 103: bipush 12
      // 105: newarray 8
      // 107: astore 8
      // 109: aload 1
      // 10a: aload 8
      // 10c: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 10f: aload 8
      // 111: aload 7
      // 113: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 116: ifne 130
      // 119: aload 0
      // 11a: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/tls10/TLSAlertProtocol;
      // 11d: bipush 3
      // 11f: bipush 51
      // 121: invokevirtual net/rim/device/api/crypto/tls/tls10/TLSAlertProtocol.sendAlertMessage (BB)V
      // 124: new java/lang/Object
      // 127: dup
      // 128: bipush 3
      // 12a: bipush 51
      // 12c: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // 12f: athrow
      // 130: return
      // 131: astore 5
      // 133: aload 0
      // 134: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/tls10/TLSAlertProtocol;
      // 137: bipush 51
      // 139: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 13c: return
      // 13d: astore 5
      // 13f: aload 0
      // 140: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/tls10/TLSAlertProtocol;
      // 143: bipush 50
      // 145: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 148: return
      // 149: astore 5
      // 14b: aload 0
      // 14c: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/tls10/TLSAlertProtocol;
      // 14f: bipush 51
      // 151: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 154: return
      // try (52 -> 148): 149 null
      // try (52 -> 148): 155 null
      // try (52 -> 148): 161 null
   }

   @Override
   public KeyMaterial generateKeyMaterial(byte[] masterSecret, byte[] clientRandom, byte[] serverRandom) {
      if (super._remoteVersion != this.getLocalVersion()) {
         return super.generateKeyMaterial(masterSecret, clientRandom, serverRandom);
      }

      try {
         ConnectionState read = (ConnectionState)this._recordProtocol.getPendingRead();
         ConnectionState write = (ConnectionState)this._recordProtocol.getPendingWrite();
         byte[] random = new byte[serverRandom.length + clientRandom.length];
         System.arraycopy(serverRandom, 0, random, 0, serverRandom.length);
         System.arraycopy(clientRandom, 0, random, serverRandom.length, clientRandom.length);
         TLSPRF prf = new TLSPRF(masterSecret, "key expansion".getBytes(), random);
         HMACKey clientHMACKey = (HMACKey)(new Object(prf.getBytes(write.getHashSize())));
         HMACKey serverHMACKey = (HMACKey)(new Object(prf.getBytes(read.getHashSize())));
         byte[] clientWriteKeyData = new byte[write.getKeySize()];
         SymmetricKey clientWriteKey = TLSUtilities.getKey(write, prf, clientWriteKeyData, true);
         byte[] serverWriteKeyData = new byte[read.getKeySize()];
         SymmetricKey serverWriteKey = TLSUtilities.getKey(read, prf, serverWriteKeyData, true);
         InitializationVector clientWriteIV = null;
         if (!write.getIsExportable() && write.getCipherType() == 2) {
            clientWriteIV = (InitializationVector)(new Object(prf.getBytes(write.getIVSize())));
         }

         InitializationVector serverWriteIV = null;
         if (!read.getIsExportable() && read.getCipherType() == 2) {
            serverWriteIV = (InitializationVector)(new Object(prf.getBytes(read.getIVSize())));
         }

         TLSPRF exportIV = null;
         byte[] exportRandom = null;
         if (write.getIsExportable()) {
            exportRandom = new byte[clientRandom.length + serverRandom.length];
            System.arraycopy(clientRandom, 0, exportRandom, 0, clientRandom.length);
            System.arraycopy(serverRandom, 0, exportRandom, clientRandom.length, serverRandom.length);
            TLSPRF writeExportPRF = new TLSPRF(clientWriteKeyData, "client write key".getBytes(), exportRandom);
            Array.resize(clientWriteKeyData, write.getKeyMaterialLength());
            Arrays.fill(clientWriteKeyData, (byte)0);
            clientWriteKey = TLSUtilities.getKey(write, writeExportPRF, clientWriteKeyData, false);
            if (write.getCipherType() == 2) {
               exportIV = new TLSPRF(new byte[0], "IV block".getBytes(), exportRandom);
               clientWriteIV = (InitializationVector)(new Object(exportIV.getBytes(write.getIVSize())));
            }
         }

         if (read.getIsExportable()) {
            if (exportRandom == null) {
               exportRandom = new byte[clientRandom.length + serverRandom.length];
               System.arraycopy(clientRandom, 0, exportRandom, 0, clientRandom.length);
               System.arraycopy(serverRandom, 0, exportRandom, clientRandom.length, serverRandom.length);
            }

            TLSPRF readExportPRF = new TLSPRF(serverWriteKeyData, "server write key".getBytes(), exportRandom);
            Array.resize(serverWriteKeyData, read.getKeyMaterialLength());
            Arrays.fill(serverWriteKeyData, (byte)0);
            serverWriteKey = TLSUtilities.getKey(read, readExportPRF, serverWriteKeyData, false);
            if (read.getCipherType() == 2) {
               if (exportIV == null) {
                  exportIV = new TLSPRF(new byte[0], "IV block".getBytes(), exportRandom);
               }

               serverWriteIV = (InitializationVector)(new Object(exportIV.getBytes(read.getIVSize())));
            }
         }

         return (KeyMaterial)(new Object(clientWriteKey, serverWriteKey, clientHMACKey, serverHMACKey, clientWriteIV, serverWriteIV));
      } finally {
         TLSUtilities.sendAlertAndThrowException(this._alertProtocol, (byte)51);
         return null;
      }
   }

   @Override
   public byte[] generateMasterSecret(byte[] param1, byte[] param2, byte[] param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol._remoteVersion I
      // 04: aload 0
      // 05: invokevirtual net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol.getLocalVersion ()I
      // 08: if_icmpeq 13
      // 0b: aload 0
      // 0c: aload 1
      // 0d: aload 2
      // 0e: aload 3
      // 0f: invokespecial net/rim/device/api/crypto/tls/ssl30/SSLHandshakeProtocol.generateMasterSecret ([B[B[B)[B
      // 12: areturn
      // 13: aload 2
      // 14: arraylength
      // 15: aload 3
      // 16: arraylength
      // 17: iadd
      // 18: newarray 8
      // 1a: astore 4
      // 1c: aload 2
      // 1d: bipush 0
      // 1e: aload 4
      // 20: bipush 0
      // 21: aload 2
      // 22: arraylength
      // 23: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 26: aload 3
      // 27: bipush 0
      // 28: aload 4
      // 2a: aload 2
      // 2b: arraylength
      // 2c: aload 3
      // 2d: arraylength
      // 2e: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 31: new net/rim/device/api/crypto/tls/tls10/TLSPRF
      // 34: dup
      // 35: aload 1
      // 36: ldc_w "master secret"
      // 39: invokevirtual java/lang/String.getBytes ()[B
      // 3c: aload 4
      // 3e: invokespecial net/rim/device/api/crypto/tls/tls10/TLSPRF.<init> ([B[B[B)V
      // 41: bipush 48
      // 43: invokevirtual net/rim/device/api/crypto/tls/tls10/TLSPRF.getBytes (I)[B
      // 46: areturn
      // 47: astore 4
      // 49: aload 0
      // 4a: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/tls10/TLSAlertProtocol;
      // 4d: bipush 51
      // 4f: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 52: aconst_null
      // 53: areturn
      // 54: astore 4
      // 56: aload 0
      // 57: getfield net/rim/device/api/crypto/tls/tls10/TLSHandshakeProtocol._alertProtocol Lnet/rim/device/api/crypto/tls/tls10/TLSAlertProtocol;
      // 5a: bipush 51
      // 5c: invokestatic net/rim/device/api/crypto/tls/TLSUtilities.sendAlertAndThrowException (Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;B)V
      // 5f: aconst_null
      // 60: areturn
      // try (11 -> 42): 43 null
      // try (11 -> 42): 50 null
   }

   @Override
   public byte[] getCipherSuites() {
      int[] cipherSuites = TLSCipherSuites.getPriority();
      int length = cipherSuites.length * 2;
      byte[] output = new byte[length];
      int i = 0;

      for (int j = 0; i < length; j++) {
         output[i] = (byte)(cipherSuites[j] << 8);
         output[i + 1] = (byte)cipherSuites[j];
         i += 2;
      }

      return output;
   }

   @Override
   public int[] getRawCipherSuites() {
      return TLSCipherSuites.getPriority();
   }

   @Override
   public int getLocalVersion() {
      return 769;
   }
}
