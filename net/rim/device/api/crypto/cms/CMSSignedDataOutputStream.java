package net.rim.device.api.crypto.cms;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.vm.Array;

public final class CMSSignedDataOutputStream extends CMSOutputStream {
   private Vector _signers;
   private boolean _noMoreAdds;
   private ByteArrayOutputStream _bufferOut;
   private OutputStream _outFilter;
   private boolean _writeCerts;
   private Certificate[] _certs;
   private boolean[] _writeCertsShorthand;
   private CMSReceiptRequest _receiptRequest;
   private CMSReceiptData[] _receiptInformation = new CMSReceiptData[0];
   private byte[] _msgSigDigest;
   private boolean _usePKCS7Format;

   public CMSSignedDataOutputStream(OutputStream out, int contentType, boolean outer) {
      this(out, contentType, true, outer, false, false);
   }

   public CMSSignedDataOutputStream(OutputStream out, int contentType, boolean dataOut, boolean outer, boolean writeCerts) {
      this(out, contentType, dataOut, outer, writeCerts, false);
   }

   public CMSSignedDataOutputStream(OutputStream out, int contentType, boolean dataOut, boolean outer, boolean writeCerts, boolean usePKCS7Format) {
      super(out, contentType, dataOut, outer);
      this._writeCerts = writeCerts;
      this._signers = (Vector)(new Object());
      this._bufferOut = (ByteArrayOutputStream)(new Object());
      this._usePKCS7Format = usePKCS7Format;
   }

   final void setMsgSigDigest(byte[] msgSigDigest) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void addSigner(CMSSigner signer) {
      if (this._noMoreAdds) {
         throw new Object();
      }

      this._signers.addElement(signer);
      Digest digest = DigestFactory.getInstance(signer.getSigner().getDigestAlgorithm());
      signer.setDigest(digest);
      if (this._outFilter == null) {
         this._outFilter = (OutputStream)(new Object(digest, (OutputStream)(new Object(signer.getSigner(), this._bufferOut))));
      } else {
         this._outFilter = (OutputStream)(new Object(signer.getSigner(), this._outFilter));
         this._outFilter = (OutputStream)(new Object(digest, (OutputStream)(new Object(signer.getSigner(), this._outFilter))));
      }
   }

   public final void addCertificates(Certificate[] certificates) {
      this.addCertificates(certificates, false);
   }

   public final void addCertificates(Certificate[] certificates, boolean useShortForm) {
      if (certificates != null) {
         int certificatesLength = certificates.length;
         if (this._certs == null) {
            this._certs = new Object[certificates.length];
            this._writeCertsShorthand = new boolean[certificates.length];
            System.arraycopy(certificates, 0, this._certs, 0, certificates.length);

            for (int i = 0; i < certificatesLength; i++) {
               this._writeCertsShorthand[i] = useShortForm;
            }
         } else {
            int oldLength = this._certs.length;
            Array.resize(this._certs, oldLength + certificatesLength);
            Array.resize(this._writeCertsShorthand, oldLength + certificatesLength);
            System.arraycopy(certificates, 0, this._certs, oldLength, certificatesLength);

            for (int i = 0; i < certificatesLength; i++) {
               this._writeCertsShorthand[i + oldLength] = useShortForm;
            }
         }
      }
   }

   public final void addReceiptRequest(CMSReceiptRequest receiptRequest) {
      if (!super._contentType.equals(OIDs.getOID(-1721363152)) && receiptRequest != null) {
         this._receiptRequest = receiptRequest;
      } else {
         throw new Object();
      }
   }

   public final CMSReceiptData[] getReceiptInformation() {
      return this._receiptInformation;
   }

   @Override
   public final void write(byte[] data, int offset, int length) {
      if (data != null && offset >= 0 && length >= 0 && data.length - length >= offset) {
         this._noMoreAdds = true;
         if (this._outFilter == null) {
            this._bufferOut.write(data, offset, length);
         } else {
            this._outFilter.write(data, offset, length);
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final void close() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: bipush 1
      // 002: putfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._noMoreAdds Z
      // 005: new java/lang/Object
      // 008: dup
      // 009: aload 0
      // 00a: getfield net/rim/device/api/crypto/cms/CMSOutputStream._out Ljava/io/OutputStream;
      // 00d: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> (Ljava/io/OutputStream;)V
      // 010: astore 1
      // 011: new java/lang/Object
      // 014: dup
      // 015: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 018: astore 2
      // 019: aload 0
      // 01a: getfield net/rim/device/api/crypto/cms/CMSOutputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 01d: ldc_w 541859388
      // 020: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 023: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 026: ifne 030
      // 029: aload 0
      // 02a: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._writeCerts Z
      // 02d: ifne 038
      // 030: aload 2
      // 031: bipush 1
      // 032: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger (I)V
      // 035: goto 03e
      // 038: aload 2
      // 039: bipush 3
      // 03b: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger (I)V
      // 03e: aload 0
      // 03f: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._signers Ljava/util/Vector;
      // 042: invokevirtual java/util/Vector.elements ()Ljava/util/Enumeration;
      // 045: astore 3
      // 046: new java/lang/Object
      // 049: dup
      // 04a: invokespecial java/util/Hashtable.<init> ()V
      // 04d: astore 4
      // 04f: aload 3
      // 050: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 055: ifeq 09a
      // 058: aload 3
      // 059: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 05e: checkcast net/rim/device/api/crypto/cms/CMSSigner
      // 061: astore 5
      // 063: aload 0
      // 064: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._receiptRequest Lnet/rim/device/api/crypto/cms/CMSReceiptRequest;
      // 067: ifnull 077
      // 06a: aload 5
      // 06c: aload 0
      // 06d: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._receiptRequest Lnet/rim/device/api/crypto/cms/CMSReceiptRequest;
      // 070: invokevirtual net/rim/device/api/crypto/cms/CMSReceiptRequest.getReceiptRequestAttribute ()Lnet/rim/device/api/crypto/cms/CMSAttribute;
      // 073: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.addAttribute (Lnet/rim/device/api/crypto/cms/CMSAttribute;)Z
      // 076: pop
      // 077: aload 5
      // 079: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.getSigner ()Lnet/rim/device/api/crypto/SignatureSigner;
      // 07c: astore 6
      // 07e: aload 6
      // 080: ifnull 04f
      // 083: aload 4
      // 085: aload 6
      // 087: invokeinterface net/rim/device/api/crypto/SignatureSigner.getDigestAlgorithm ()Ljava/lang/String; 1
      // 08c: aload 6
      // 08e: invokeinterface net/rim/device/api/crypto/SignatureSigner.getDigestAlgorithm ()Ljava/lang/String; 1
      // 093: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 096: pop
      // 097: goto 04f
      // 09a: new java/lang/Object
      // 09d: dup
      // 09e: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 0a1: astore 5
      // 0a3: aload 4
      // 0a5: invokevirtual java/util/Hashtable.elements ()Ljava/util/Enumeration;
      // 0a8: astore 6
      // 0aa: aload 6
      // 0ac: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 0b1: ifeq 0e5
      // 0b4: aload 6
      // 0b6: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0bb: checkcast java/lang/Object
      // 0be: astore 7
      // 0c0: new java/lang/Object
      // 0c3: dup
      // 0c4: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 0c7: astore 8
      // 0c9: aload 8
      // 0cb: ldc2_w 3134008036018563479
      // 0ce: aload 7
      // 0d0: invokestatic net/rim/device/api/crypto/oid/OIDs.getAssociatedOID (JLjava/lang/Object;)Lnet/rim/device/api/crypto/oid/OID;
      // 0d3: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 0d6: aload 8
      // 0d8: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeNull ()V
      // 0db: aload 5
      // 0dd: aload 8
      // 0df: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 0e2: goto 0aa
      // 0e5: aload 2
      // 0e6: aload 5
      // 0e8: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSet (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 0eb: aload 0
      // 0ec: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._outFilter Ljava/io/OutputStream;
      // 0ef: ifnull 0f9
      // 0f2: aload 0
      // 0f3: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._outFilter Ljava/io/OutputStream;
      // 0f6: invokevirtual java/io/OutputStream.close ()V
      // 0f9: aload 0
      // 0fa: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._bufferOut Ljava/io/ByteArrayOutputStream;
      // 0fd: invokevirtual java/io/ByteArrayOutputStream.close ()V
      // 100: new java/lang/Object
      // 103: dup
      // 104: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 107: astore 7
      // 109: aload 7
      // 10b: aload 0
      // 10c: getfield net/rim/device/api/crypto/cms/CMSOutputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 10f: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 112: aload 0
      // 113: getfield net/rim/device/api/crypto/cms/CMSOutputStream._dataOut Z
      // 116: ifne 11c
      // 119: goto 1d4
      // 11c: aload 0
      // 11d: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._usePKCS7Format Z
      // 120: ifne 126
      // 123: goto 1c6
      // 126: aload 0
      // 127: getfield net/rim/device/api/crypto/cms/CMSOutputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 12a: ldc_w 541859388
      // 12d: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 130: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 133: ifeq 139
      // 136: goto 1c6
      // 139: new java/lang/Object
      // 13c: dup
      // 13d: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 140: astore 8
      // 142: aload 0
      // 143: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._bufferOut Ljava/io/ByteArrayOutputStream;
      // 146: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 149: astore 9
      // 14b: aload 9
      // 14d: bipush 0
      // 14e: baload
      // 14f: bipush 31
      // 151: iand
      // 152: bipush 4
      // 154: if_icmpeq 15b
      // 157: bipush 1
      // 158: goto 15c
      // 15b: bipush 0
      // 15c: istore 10
      // 15e: iload 10
      // 160: ifeq 1b3
      // 163: aload 0
      // 164: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._signers Ljava/util/Vector;
      // 167: invokevirtual java/util/Vector.elements ()Ljava/util/Enumeration;
      // 16a: astore 11
      // 16c: aload 11
      // 16e: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 173: ifeq 1b3
      // 176: aload 11
      // 178: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 17d: checkcast net/rim/device/api/crypto/cms/CMSSigner
      // 180: astore 12
      // 182: aload 12
      // 184: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.getSigner ()Lnet/rim/device/api/crypto/SignatureSigner;
      // 187: astore 13
      // 189: aload 13
      // 18b: invokeinterface net/rim/device/api/crypto/SignatureSigner.reset ()V 1
      // 190: aload 13
      // 192: aload 9
      // 194: invokeinterface net/rim/device/api/crypto/SignatureSigner.update ([B)V 2
      // 199: aload 12
      // 19b: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.getDigest ()Lnet/rim/device/api/crypto/Digest;
      // 19e: astore 14
      // 1a0: aload 14
      // 1a2: invokeinterface net/rim/device/api/crypto/Digest.reset ()V 1
      // 1a7: aload 14
      // 1a9: aload 9
      // 1ab: invokeinterface net/rim/device/api/crypto/Digest.update ([B)V 2
      // 1b0: goto 16c
      // 1b3: aload 8
      // 1b5: aload 9
      // 1b7: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 1ba: aload 7
      // 1bc: aload 8
      // 1be: bipush 1
      // 1bf: bipush 0
      // 1c0: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeStreamWithTag (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;II)V
      // 1c3: goto 1d4
      // 1c6: aload 7
      // 1c8: aload 0
      // 1c9: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._bufferOut Ljava/io/ByteArrayOutputStream;
      // 1cc: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 1cf: bipush 1
      // 1d0: bipush 0
      // 1d1: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // 1d4: aload 2
      // 1d5: aload 7
      // 1d7: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 1da: aload 0
      // 1db: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._writeCerts Z
      // 1de: ifne 1e4
      // 1e1: goto 459
      // 1e4: new java/lang/Object
      // 1e7: dup
      // 1e8: invokespecial java/util/Hashtable.<init> ()V
      // 1eb: astore 8
      // 1ed: new java/lang/Object
      // 1f0: dup
      // 1f1: invokespecial java/util/Hashtable.<init> ()V
      // 1f4: astore 9
      // 1f6: aload 0
      // 1f7: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._certs [Lnet/rim/device/api/crypto/certificate/Certificate;
      // 1fa: ifnull 249
      // 1fd: aload 0
      // 1fe: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._certs [Lnet/rim/device/api/crypto/certificate/Certificate;
      // 201: arraylength
      // 202: istore 10
      // 204: bipush 0
      // 205: istore 11
      // 207: iload 11
      // 209: iload 10
      // 20b: if_icmpge 249
      // 20e: aload 0
      // 20f: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._writeCertsShorthand [Z
      // 212: iload 11
      // 214: baload
      // 215: ifeq 22f
      // 218: aload 9
      // 21a: aload 0
      // 21b: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._certs [Lnet/rim/device/api/crypto/certificate/Certificate;
      // 21e: iload 11
      // 220: aaload
      // 221: aload 0
      // 222: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._certs [Lnet/rim/device/api/crypto/certificate/Certificate;
      // 225: iload 11
      // 227: aaload
      // 228: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 22b: pop
      // 22c: goto 243
      // 22f: aload 8
      // 231: aload 0
      // 232: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._certs [Lnet/rim/device/api/crypto/certificate/Certificate;
      // 235: iload 11
      // 237: aaload
      // 238: aload 0
      // 239: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._certs [Lnet/rim/device/api/crypto/certificate/Certificate;
      // 23c: iload 11
      // 23e: aaload
      // 23f: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 242: pop
      // 243: iinc 11 1
      // 246: goto 207
      // 249: aload 0
      // 24a: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._signers Ljava/util/Vector;
      // 24d: invokevirtual java/util/Vector.elements ()Ljava/util/Enumeration;
      // 250: astore 10
      // 252: aload 10
      // 254: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 259: ifeq 2af
      // 25c: aload 10
      // 25e: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 263: checkcast net/rim/device/api/crypto/cms/CMSSigner
      // 266: astore 11
      // 268: aload 11
      // 26a: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.getCertificateChain ()[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 26d: astore 12
      // 26f: aload 12
      // 271: arraylength
      // 272: istore 13
      // 274: bipush 0
      // 275: istore 14
      // 277: iload 14
      // 279: iload 13
      // 27b: if_icmpge 252
      // 27e: aload 11
      // 280: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.isWriteShortForm ()Z
      // 283: ifeq 299
      // 286: aload 9
      // 288: aload 12
      // 28a: iload 14
      // 28c: aaload
      // 28d: aload 12
      // 28f: iload 14
      // 291: aaload
      // 292: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 295: pop
      // 296: goto 2a9
      // 299: aload 8
      // 29b: aload 12
      // 29d: iload 14
      // 29f: aaload
      // 2a0: aload 12
      // 2a2: iload 14
      // 2a4: aaload
      // 2a5: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 2a8: pop
      // 2a9: iinc 14 1
      // 2ac: goto 277
      // 2af: new java/lang/Object
      // 2b2: dup
      // 2b3: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 2b6: astore 11
      // 2b8: aload 9
      // 2ba: invokevirtual java/util/Hashtable.elements ()Ljava/util/Enumeration;
      // 2bd: astore 12
      // 2bf: aload 12
      // 2c1: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 2c6: ifne 2cc
      // 2c9: goto 424
      // 2cc: aload 12
      // 2ce: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 2d3: checkcast java/lang/Object
      // 2d6: astore 13
      // 2d8: aload 8
      // 2da: aload 13
      // 2dc: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 2df: ifnull 2ea
      // 2e2: aload 8
      // 2e4: aload 13
      // 2e6: invokevirtual java/util/Hashtable.remove (Ljava/lang/Object;)Ljava/lang/Object;
      // 2e9: pop
      // 2ea: new java/lang/Object
      // 2ed: dup
      // 2ee: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 2f1: astore 14
      // 2f3: bipush 4
      // 2f5: newarray 8
      // 2f7: astore 15
      // 2f9: new java/lang/Object
      // 2fc: dup
      // 2fd: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 300: astore 16
      // 302: aload 16
      // 304: aload 13
      // 306: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getEncoding ()[B 1
      // 30b: invokevirtual net/rim/device/api/crypto/AbstractDigest.update ([B)V
      // 30e: aload 15
      // 310: bipush 0
      // 311: bipush 0
      // 312: bastore
      // 313: aload 15
      // 315: bipush 1
      // 316: bipush 0
      // 317: bastore
      // 318: aload 15
      // 31a: bipush 2
      // 31c: bipush 0
      // 31d: bastore
      // 31e: aload 15
      // 320: bipush 3
      // 322: bipush 20
      // 324: bastore
      // 325: aload 14
      // 327: aload 15
      // 329: invokevirtual java/io/OutputStream.write ([B)V
      // 32c: aload 14
      // 32e: aload 16
      // 330: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ()[B
      // 333: invokevirtual java/io/OutputStream.write ([B)V
      // 336: aload 13
      // 338: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getIssuer ()Lnet/rim/device/api/crypto/certificate/DistinguishedName; 1
      // 33d: invokeinterface net/rim/device/api/crypto/certificate/DistinguishedName.getEncoding ()[B 1
      // 342: astore 17
      // 344: aload 15
      // 346: bipush 0
      // 347: aload 17
      // 349: arraylength
      // 34a: bipush 24
      // 34c: ishr
      // 34d: i2b
      // 34e: bastore
      // 34f: aload 15
      // 351: bipush 1
      // 352: aload 17
      // 354: arraylength
      // 355: bipush 16
      // 357: ishr
      // 358: i2b
      // 359: bastore
      // 35a: aload 15
      // 35c: bipush 2
      // 35e: aload 17
      // 360: arraylength
      // 361: bipush 8
      // 363: ishr
      // 364: i2b
      // 365: bastore
      // 366: aload 15
      // 368: bipush 3
      // 36a: aload 17
      // 36c: arraylength
      // 36d: i2b
      // 36e: bastore
      // 36f: aload 14
      // 371: aload 15
      // 373: invokevirtual java/io/OutputStream.write ([B)V
      // 376: aload 14
      // 378: aload 17
      // 37a: invokevirtual java/io/OutputStream.write ([B)V
      // 37d: aload 13
      // 37f: dup
      // 380: instanceof java/lang/Object
      // 383: ifne 38a
      // 386: pop
      // 387: goto 3a7
      // 38a: checkcast java/lang/Object
      // 38d: astore 20
      // 38f: aload 20
      // 391: invokevirtual net/rim/device/api/crypto/certificate/x509/X509Certificate.getSignedSerialNumber ()Lnet/rim/device/api/crypto/asn1/ASN1SignedByteArray;
      // 394: astore 21
      // 396: aload 21
      // 398: invokevirtual net/rim/device/api/crypto/asn1/ASN1SignedByteArray.getValue ()[B
      // 39b: astore 18
      // 39d: aload 21
      // 39f: invokevirtual net/rim/device/api/crypto/asn1/ASN1SignedByteArray.isPositive ()Z
      // 3a2: istore 19
      // 3a4: goto 3b3
      // 3a7: aload 13
      // 3a9: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getSerialNumber ()[B 1
      // 3ae: astore 18
      // 3b0: bipush 1
      // 3b1: istore 19
      // 3b3: aload 18
      // 3b5: arraylength
      // 3b6: istore 20
      // 3b8: iload 19
      // 3ba: ifeq 3d0
      // 3bd: iload 20
      // 3bf: ifle 3d0
      // 3c2: aload 18
      // 3c4: bipush 0
      // 3c5: baload
      // 3c6: sipush 128
      // 3c9: iand
      // 3ca: ifeq 3d0
      // 3cd: iinc 20 1
      // 3d0: aload 15
      // 3d2: bipush 0
      // 3d3: iload 20
      // 3d5: bipush 24
      // 3d7: ishr
      // 3d8: i2b
      // 3d9: bastore
      // 3da: aload 15
      // 3dc: bipush 1
      // 3dd: iload 20
      // 3df: bipush 16
      // 3e1: ishr
      // 3e2: i2b
      // 3e3: bastore
      // 3e4: aload 15
      // 3e6: bipush 2
      // 3e8: iload 20
      // 3ea: bipush 8
      // 3ec: ishr
      // 3ed: i2b
      // 3ee: bastore
      // 3ef: aload 15
      // 3f1: bipush 3
      // 3f3: iload 20
      // 3f5: i2b
      // 3f6: bastore
      // 3f7: aload 14
      // 3f9: aload 15
      // 3fb: invokevirtual java/io/OutputStream.write ([B)V
      // 3fe: iload 20
      // 400: aload 18
      // 402: arraylength
      // 403: if_icmpeq 40c
      // 406: aload 14
      // 408: bipush 0
      // 409: invokevirtual java/io/ByteArrayOutputStream.write (I)V
      // 40c: aload 14
      // 40e: aload 18
      // 410: invokevirtual java/io/OutputStream.write ([B)V
      // 413: aload 11
      // 415: aload 14
      // 417: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 41a: bipush 2
      // 41c: bipush 30
      // 41e: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([BII)V
      // 421: goto 2bf
      // 424: aload 8
      // 426: invokevirtual java/util/Hashtable.elements ()Ljava/util/Enumeration;
      // 429: astore 13
      // 42b: aload 13
      // 42d: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 432: ifeq 450
      // 435: aload 13
      // 437: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 43c: checkcast java/lang/Object
      // 43f: astore 14
      // 441: aload 11
      // 443: aload 14
      // 445: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getEncoding ()[B 1
      // 44a: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 44d: goto 42b
      // 450: aload 2
      // 451: aload 11
      // 453: bipush 2
      // 455: bipush 0
      // 456: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSet (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;II)V
      // 459: aload 0
      // 45a: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._signers Ljava/util/Vector;
      // 45d: invokevirtual java/util/Vector.elements ()Ljava/util/Enumeration;
      // 460: astore 8
      // 462: new java/lang/Object
      // 465: dup
      // 466: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 469: astore 9
      // 46b: aload 8
      // 46d: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 472: ifne 478
      // 475: goto 861
      // 478: aload 8
      // 47a: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 47f: checkcast net/rim/device/api/crypto/cms/CMSSigner
      // 482: astore 10
      // 484: new java/lang/Object
      // 487: dup
      // 488: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 48b: astore 11
      // 48d: aload 11
      // 48f: bipush 1
      // 490: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger (I)V
      // 493: aload 0
      // 494: aload 11
      // 496: aload 10
      // 498: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate;
      // 49b: invokespecial net/rim/device/api/crypto/cms/CMSSignedDataOutputStream.writeIssuerAndSerialNumber (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 49e: new java/lang/Object
      // 4a1: dup
      // 4a2: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 4a5: astore 12
      // 4a7: aload 12
      // 4a9: ldc2_w 3134008036018563479
      // 4ac: aload 10
      // 4ae: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.getSigner ()Lnet/rim/device/api/crypto/SignatureSigner;
      // 4b1: invokeinterface net/rim/device/api/crypto/SignatureSigner.getDigestAlgorithm ()Ljava/lang/String; 1
      // 4b6: invokestatic net/rim/device/api/crypto/oid/OIDs.getAssociatedOID (JLjava/lang/Object;)Lnet/rim/device/api/crypto/oid/OID;
      // 4b9: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 4bc: aload 12
      // 4be: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeNull ()V
      // 4c1: aload 11
      // 4c3: aload 12
      // 4c5: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 4c8: bipush 0
      // 4c9: istore 13
      // 4cb: bipush 0
      // 4cc: istore 14
      // 4ce: bipush 0
      // 4cf: istore 15
      // 4d1: aload 10
      // 4d3: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.getSignedAttributes ()Ljava/util/Enumeration;
      // 4d6: astore 16
      // 4d8: aload 16
      // 4da: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 4df: ifeq 533
      // 4e2: aload 16
      // 4e4: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 4e9: checkcast net/rim/device/api/crypto/cms/CMSAttribute
      // 4ec: astore 17
      // 4ee: ldc_w 542647868
      // 4f1: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 4f4: aload 17
      // 4f6: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 4f9: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 4fc: ifeq 505
      // 4ff: bipush 1
      // 500: istore 13
      // 502: goto 4d8
      // 505: ldc_w 542385724
      // 508: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 50b: aload 17
      // 50d: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 510: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 513: ifeq 51c
      // 516: bipush 1
      // 517: istore 14
      // 519: goto 4d8
      // 51c: ldc_w -1721354960
      // 51f: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 522: aload 17
      // 524: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 527: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 52a: ifeq 4d8
      // 52d: bipush 1
      // 52e: istore 15
      // 530: goto 4d8
      // 533: iload 13
      // 535: ifne 561
      // 538: aload 0
      // 539: aload 10
      // 53b: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.getDigest ()Lnet/rim/device/api/crypto/Digest;
      // 53e: invokeinterface net/rim/device/api/crypto/Digest.getDigest ()[B 1
      // 543: invokespecial net/rim/device/api/crypto/cms/CMSSignedDataOutputStream.writeHashSequence ([B)Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;
      // 546: astore 17
      // 548: aload 10
      // 54a: new net/rim/device/api/crypto/cms/CMSAttribute
      // 54d: dup
      // 54e: ldc_w 542647868
      // 551: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 554: aload 17
      // 556: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 559: bipush 1
      // 55a: invokespecial net/rim/device/api/crypto/cms/CMSAttribute.<init> (Lnet/rim/device/api/crypto/oid/OID;[BZ)V
      // 55d: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.addAttribute (Lnet/rim/device/api/crypto/cms/CMSAttribute;)Z
      // 560: pop
      // 561: iload 14
      // 563: ifne 5a1
      // 566: new java/lang/Object
      // 569: dup
      // 56a: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 56d: astore 17
      // 56f: new java/lang/Object
      // 572: dup
      // 573: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 576: astore 18
      // 578: aload 18
      // 57a: aload 0
      // 57b: getfield net/rim/device/api/crypto/cms/CMSOutputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 57e: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 581: aload 17
      // 583: aload 18
      // 585: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSet (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 588: aload 10
      // 58a: new net/rim/device/api/crypto/cms/CMSAttribute
      // 58d: dup
      // 58e: ldc_w 542385724
      // 591: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 594: aload 17
      // 596: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 599: bipush 1
      // 59a: invokespecial net/rim/device/api/crypto/cms/CMSAttribute.<init> (Lnet/rim/device/api/crypto/oid/OID;[BZ)V
      // 59d: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.addAttribute (Lnet/rim/device/api/crypto/cms/CMSAttribute;)Z
      // 5a0: pop
      // 5a1: aload 0
      // 5a2: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._msgSigDigest [B
      // 5a5: ifnull 5e0
      // 5a8: iload 15
      // 5aa: ifne 5e0
      // 5ad: aload 0
      // 5ae: getfield net/rim/device/api/crypto/cms/CMSOutputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 5b1: ldc_w -1721352925
      // 5b4: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 5b7: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 5ba: ifeq 5e0
      // 5bd: aload 0
      // 5be: aload 0
      // 5bf: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._msgSigDigest [B
      // 5c2: invokespecial net/rim/device/api/crypto/cms/CMSSignedDataOutputStream.writeHashSequence ([B)Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;
      // 5c5: astore 17
      // 5c7: aload 10
      // 5c9: new net/rim/device/api/crypto/cms/CMSAttribute
      // 5cc: dup
      // 5cd: ldc_w -1721354960
      // 5d0: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 5d3: aload 17
      // 5d5: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 5d8: bipush 1
      // 5d9: invokespecial net/rim/device/api/crypto/cms/CMSAttribute.<init> (Lnet/rim/device/api/crypto/oid/OID;[BZ)V
      // 5dc: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.addAttribute (Lnet/rim/device/api/crypto/cms/CMSAttribute;)Z
      // 5df: pop
      // 5e0: aload 10
      // 5e2: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.getSignedAttributes ()Ljava/util/Enumeration;
      // 5e5: astore 17
      // 5e7: new java/lang/Object
      // 5ea: dup
      // 5eb: invokespecial java/util/Vector.<init> ()V
      // 5ee: astore 18
      // 5f0: aload 17
      // 5f2: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 5f7: ifeq 659
      // 5fa: aload 17
      // 5fc: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 601: checkcast net/rim/device/api/crypto/cms/CMSAttribute
      // 604: astore 19
      // 606: aload 19
      // 608: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getLength ()I
      // 60b: istore 20
      // 60d: aload 18
      // 60f: invokevirtual java/util/Vector.size ()I
      // 612: istore 21
      // 614: bipush 0
      // 615: istore 22
      // 617: bipush 0
      // 618: istore 23
      // 61a: iload 23
      // 61c: iload 21
      // 61e: if_icmpge 648
      // 621: aload 18
      // 623: iload 23
      // 625: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 628: checkcast net/rim/device/api/crypto/cms/CMSAttribute
      // 62b: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getLength ()I
      // 62e: iload 20
      // 630: if_icmple 642
      // 633: aload 18
      // 635: aload 19
      // 637: iload 23
      // 639: invokevirtual java/util/Vector.insertElementAt (Ljava/lang/Object;I)V
      // 63c: bipush 1
      // 63d: istore 22
      // 63f: goto 648
      // 642: iinc 23 1
      // 645: goto 61a
      // 648: iload 22
      // 64a: ifne 5f0
      // 64d: aload 18
      // 64f: aload 19
      // 651: iload 21
      // 653: invokevirtual java/util/Vector.insertElementAt (Ljava/lang/Object;I)V
      // 656: goto 5f0
      // 659: new java/lang/Object
      // 65c: dup
      // 65d: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 660: astore 19
      // 662: aload 18
      // 664: invokevirtual java/util/Vector.size ()I
      // 667: istore 20
      // 669: bipush 0
      // 66a: istore 21
      // 66c: iload 21
      // 66e: iload 20
      // 670: if_icmpge 6a9
      // 673: aload 18
      // 675: iload 21
      // 677: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 67a: checkcast net/rim/device/api/crypto/cms/CMSAttribute
      // 67d: astore 22
      // 67f: new java/lang/Object
      // 682: dup
      // 683: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 686: astore 23
      // 688: aload 23
      // 68a: aload 22
      // 68c: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 68f: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 692: aload 23
      // 694: aload 22
      // 696: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getValue ()[B
      // 699: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 69c: aload 19
      // 69e: aload 23
      // 6a0: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 6a3: iinc 21 1
      // 6a6: goto 66c
      // 6a9: aload 19
      // 6ab: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 6ae: astore 21
      // 6b0: aload 11
      // 6b2: aload 19
      // 6b4: bipush 2
      // 6b6: bipush 0
      // 6b7: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSet (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;II)V
      // 6ba: new java/lang/Object
      // 6bd: dup
      // 6be: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 6c1: astore 22
      // 6c3: new java/lang/Object
      // 6c6: dup
      // 6c7: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 6ca: astore 23
      // 6cc: aload 23
      // 6ce: aload 21
      // 6d0: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 6d3: aload 22
      // 6d5: aload 23
      // 6d7: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSet (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 6da: aload 22
      // 6dc: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 6df: astore 24
      // 6e1: aload 10
      // 6e3: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.getSigner ()Lnet/rim/device/api/crypto/SignatureSigner;
      // 6e6: astore 25
      // 6e8: aload 25
      // 6ea: invokeinterface net/rim/device/api/crypto/SignatureSigner.reset ()V 1
      // 6ef: aload 25
      // 6f1: aload 24
      // 6f3: invokeinterface net/rim/device/api/crypto/SignatureSigner.update ([B)V 2
      // 6f8: aload 25
      // 6fa: ldc_w "CMS"
      // 6fd: invokestatic net/rim/device/api/crypto/encoder/SignatureEncoder.encode (Lnet/rim/device/api/crypto/SignatureSigner;Ljava/lang/String;)Lnet/rim/device/api/crypto/encoder/EncodedSignature;
      // 700: astore 26
      // 702: aload 26
      // 704: invokevirtual net/rim/device/api/crypto/encoder/EncodedSignature.getEncodedSignature ()[B
      // 707: astore 27
      // 709: aload 11
      // 70b: aload 27
      // 70d: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 710: new java/lang/Object
      // 713: dup
      // 714: aload 27
      // 716: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 719: astore 28
      // 71b: aload 28
      // 71d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.skipField ()V
      // 720: aload 28
      // 722: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 725: astore 29
      // 727: aload 0
      // 728: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._receiptRequest Lnet/rim/device/api/crypto/cms/CMSReceiptRequest;
      // 72b: ifnonnull 731
      // 72e: goto 7f6
      // 731: new java/lang/Object
      // 734: dup
      // 735: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 738: astore 30
      // 73a: aload 30
      // 73c: bipush 1
      // 73d: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger (I)V
      // 740: aload 30
      // 742: aload 0
      // 743: getfield net/rim/device/api/crypto/cms/CMSOutputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 746: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 749: aload 30
      // 74b: aload 0
      // 74c: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._receiptRequest Lnet/rim/device/api/crypto/cms/CMSReceiptRequest;
      // 74f: invokevirtual net/rim/device/api/crypto/cms/CMSReceiptRequest.getSignedContentIdentifier ()[B
      // 752: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([B)V
      // 755: aload 30
      // 757: aload 29
      // 759: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 75c: new java/lang/Object
      // 75f: dup
      // 760: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 763: astore 31
      // 765: aload 31
      // 767: aload 30
      // 769: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 76c: aload 31
      // 76e: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 771: astore 32
      // 773: aload 25
      // 775: invokeinterface net/rim/device/api/crypto/SignatureSigner.getDigestAlgorithm ()Ljava/lang/String; 1
      // 77a: invokestatic net/rim/device/api/crypto/DigestFactory.getInstance (Ljava/lang/String;)Lnet/rim/device/api/crypto/Digest;
      // 77d: astore 33
      // 77f: aload 33
      // 781: aload 32
      // 783: invokeinterface net/rim/device/api/crypto/Digest.update ([B)V 2
      // 788: aload 33
      // 78a: invokeinterface net/rim/device/api/crypto/Digest.getDigest ()[B 1
      // 78f: astore 34
      // 791: aload 33
      // 793: invokeinterface net/rim/device/api/crypto/Digest.reset ()V 1
      // 798: aload 33
      // 79a: aload 24
      // 79c: invokeinterface net/rim/device/api/crypto/Digest.update ([B)V 2
      // 7a1: aload 33
      // 7a3: invokeinterface net/rim/device/api/crypto/Digest.getDigest ()[B 1
      // 7a8: astore 35
      // 7aa: aload 0
      // 7ab: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._receiptInformation [Lnet/rim/device/api/crypto/cms/CMSReceiptData;
      // 7ae: arraylength
      // 7af: istore 36
      // 7b1: aload 0
      // 7b2: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._receiptInformation [Lnet/rim/device/api/crypto/cms/CMSReceiptData;
      // 7b5: iload 36
      // 7b7: bipush 1
      // 7b8: iadd
      // 7b9: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 7bc: aload 0
      // 7bd: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._receiptInformation [Lnet/rim/device/api/crypto/cms/CMSReceiptData;
      // 7c0: iload 36
      // 7c2: new net/rim/device/api/crypto/cms/CMSReceiptData
      // 7c5: dup
      // 7c6: aload 0
      // 7c7: getfield net/rim/device/api/crypto/cms/CMSSignedDataOutputStream._receiptRequest Lnet/rim/device/api/crypto/cms/CMSReceiptRequest;
      // 7ca: invokevirtual net/rim/device/api/crypto/cms/CMSReceiptRequest.getSignedContentIdentifier ()[B
      // 7cd: aload 29
      // 7cf: aload 0
      // 7d0: getfield net/rim/device/api/crypto/cms/CMSOutputStream._contentType Lnet/rim/device/api/crypto/oid/OID;
      // 7d3: aload 34
      // 7d5: aload 35
      // 7d7: invokespecial net/rim/device/api/crypto/cms/CMSReceiptData.<init> ([B[BLnet/rim/device/api/crypto/oid/OID;[B[B)V
      // 7da: aastore
      // 7db: goto 7f6
      // 7de: astore 26
      // 7e0: new java/lang/Object
      // 7e3: dup
      // 7e4: aload 26
      // 7e6: invokespecial net/rim/device/api/crypto/CryptoIOException.<init> (Lnet/rim/device/api/crypto/CryptoException;)V
      // 7e9: athrow
      // 7ea: astore 26
      // 7ec: new java/lang/Object
      // 7ef: dup
      // 7f0: aload 26
      // 7f2: invokespecial net/rim/device/api/crypto/CryptoIOException.<init> (Lnet/rim/device/api/crypto/CryptoException;)V
      // 7f5: athrow
      // 7f6: aload 10
      // 7f8: invokevirtual net/rim/device/api/crypto/cms/CMSSigner.getUnsignedAttributes ()Ljava/util/Enumeration;
      // 7fb: astore 26
      // 7fd: aload 26
      // 7ff: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 804: ifeq 857
      // 807: new java/lang/Object
      // 80a: dup
      // 80b: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 80e: astore 27
      // 810: aload 26
      // 812: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 817: ifeq 84d
      // 81a: aload 26
      // 81c: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 821: checkcast net/rim/device/api/crypto/cms/CMSAttribute
      // 824: astore 28
      // 826: new java/lang/Object
      // 829: dup
      // 82a: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 82d: astore 29
      // 82f: aload 29
      // 831: aload 28
      // 833: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 836: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 839: aload 29
      // 83b: aload 28
      // 83d: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getValue ()[B
      // 840: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 843: aload 27
      // 845: aload 29
      // 847: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 84a: goto 810
      // 84d: aload 11
      // 84f: aload 27
      // 851: bipush 2
      // 853: bipush 1
      // 854: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSet (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;II)V
      // 857: aload 9
      // 859: aload 11
      // 85b: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 85e: goto 46b
      // 861: aload 2
      // 862: aload 9
      // 864: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSet (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 867: aload 0
      // 868: getfield net/rim/device/api/crypto/cms/CMSOutputStream._outer Z
      // 86b: ifeq 893
      // 86e: new java/lang/Object
      // 871: dup
      // 872: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 875: astore 10
      // 877: aload 10
      // 879: ldc_w 542121532
      // 87c: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 87f: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 882: aload 10
      // 884: aload 2
      // 885: bipush 1
      // 886: bipush 0
      // 887: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;II)V
      // 88a: aload 1
      // 88b: aload 10
      // 88d: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 890: goto 898
      // 893: aload 1
      // 894: aload 2
      // 895: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 898: aload 0
      // 899: getfield net/rim/device/api/crypto/cms/CMSOutputStream._out Ljava/io/OutputStream;
      // 89c: invokevirtual java/io/OutputStream.close ()V
      // 89f: return
      // 8a0: astore 1
      // 8a1: new java/lang/Object
      // 8a4: dup
      // 8a5: aload 1
      // 8a6: invokespecial net/rim/device/api/crypto/CryptoIOException.<init> (Lnet/rim/device/api/crypto/CryptoException;)V
      // 8a9: athrow
      // try (780 -> 876): 877 null
      // try (780 -> 876): 883 null
      // try (0 -> 960): 961 null
   }

   private final void writeIssuerAndSerialNumber(ASN1OutputStream asn1Stream, Certificate certificate) {
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

      asn1Stream.writeSequence(issuerAndSerialNumber);
   }

   private final ASN1OutputStream writeHashSequence(byte[] information) {
      ASN1OutputStream hashSequence = (ASN1OutputStream)(new Object());
      ASN1OutputStream hashValue = (ASN1OutputStream)(new Object());
      hashValue.writeOctetString(information);
      hashSequence.writeSet(hashValue);
      return hashSequence;
   }
}
