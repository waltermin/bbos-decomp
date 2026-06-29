package net.rim.device.api.crypto.certificate.wtls;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import net.rim.device.api.crypto.CryptoUtilities;
import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateDisplayField;
import net.rim.device.api.crypto.certificate.CertificateExtension;
import net.rim.device.api.crypto.certificate.CertificateParsingException;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.CertificateVerificationException;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.certificate.NoIssuerFoundException;
import net.rim.device.api.crypto.certificate.SubjectKeyStoreIndex;
import net.rim.device.api.crypto.encoder.DecodedSignature;
import net.rim.device.api.crypto.encoder.SignatureDecoder;
import net.rim.device.api.crypto.keystore.CertificateStatusManager;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public final class WTLSCertificate implements Certificate, Persistable {
   private int _version;
   private int _signatureAlgorithm;
   private WTLSDistinguishedName _issuer;
   private long _notBefore;
   private long _notAfter;
   private WTLSDistinguishedName _subject;
   private PublicKey _publicKey;
   private byte[] _signature;
   private byte[] _serialNumber;
   private boolean _isCertificateAuthority;
   private int _hashCode;
   private byte[] _encoding;

   public WTLSCertificate(byte[] input) {
      this(input, 0, input.length);
   }

   public WTLSCertificate(byte[] input, int offset, int length) {
      this(new ByteArrayInputStream(input, offset, length));
   }

   public WTLSCertificate(InputStream param1) throws CertificateParsingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokespecial java/lang/Object.<init> ()V
      // 004: aload 1
      // 005: ifnonnull 010
      // 008: new java/lang/IllegalArgumentException
      // 00b: dup
      // 00c: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 00f: athrow
      // 010: new java/io/ByteArrayOutputStream
      // 013: dup
      // 014: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 017: astore 2
      // 018: new java/io/DataInputStream
      // 01b: dup
      // 01c: new net/rim/device/api/crypto/certificate/wtls/WTLSCertificate$PassThroughInputStream
      // 01f: dup
      // 020: aload 1
      // 021: aload 2
      // 022: invokespecial net/rim/device/api/crypto/certificate/wtls/WTLSCertificate$PassThroughInputStream.<init> (Ljava/io/InputStream;Ljava/io/OutputStream;)V
      // 025: invokespecial java/io/DataInputStream.<init> (Ljava/io/InputStream;)V
      // 028: astore 3
      // 029: aload 0
      // 02a: aload 3
      // 02b: invokevirtual java/io/DataInputStream.readUnsignedByte ()I
      // 02e: putfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._version I
      // 031: aload 0
      // 032: getfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._version I
      // 035: bipush 1
      // 036: if_icmpeq 041
      // 039: new net/rim/device/api/crypto/certificate/CertificateParsingException
      // 03c: dup
      // 03d: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 040: athrow
      // 041: aload 0
      // 042: aload 3
      // 043: invokevirtual java/io/DataInputStream.readUnsignedByte ()I
      // 046: putfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._signatureAlgorithm I
      // 049: aload 0
      // 04a: bipush 0
      // 04b: putfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._isCertificateAuthority Z
      // 04e: aload 0
      // 04f: new net/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName
      // 052: dup
      // 053: aload 3
      // 054: invokespecial net/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName.<init> (Ljava/io/InputStream;)V
      // 057: putfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._issuer Lnet/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName;
      // 05a: aload 0
      // 05b: aload 3
      // 05c: invokevirtual java/io/DataInputStream.readInt ()I
      // 05f: i2l
      // 060: ldc2_w 4294967295
      // 063: land
      // 064: sipush 1000
      // 067: i2l
      // 068: lmul
      // 069: putfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._notBefore J
      // 06c: aload 0
      // 06d: aload 3
      // 06e: invokevirtual java/io/DataInputStream.readInt ()I
      // 071: i2l
      // 072: ldc2_w 4294967295
      // 075: land
      // 076: sipush 1000
      // 079: i2l
      // 07a: lmul
      // 07b: putfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._notAfter J
      // 07e: aload 0
      // 07f: new net/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName
      // 082: dup
      // 083: aload 3
      // 084: invokespecial net/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName.<init> (Ljava/io/InputStream;)V
      // 087: putfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._subject Lnet/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName;
      // 08a: aload 0
      // 08b: getfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._subject Lnet/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName;
      // 08e: invokevirtual net/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName.isCertificateAuthority ()Z
      // 091: ifeq 099
      // 094: aload 0
      // 095: bipush 1
      // 096: putfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._isCertificateAuthority Z
      // 099: aload 0
      // 09a: aload 0
      // 09b: getfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._subject Lnet/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName;
      // 09e: invokevirtual net/rim/device/api/crypto/certificate/wtls/WTLSDistinguishedName.getSerialNumber ()[B
      // 0a1: putfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._serialNumber [B
      // 0a4: aload 0
      // 0a5: aload 3
      // 0a6: ldc_w "WTLS"
      // 0a9: invokestatic net/rim/device/api/crypto/encoder/PublicKeyDecoder.decode (Ljava/io/InputStream;Ljava/lang/String;)Lnet/rim/device/api/crypto/PublicKey;
      // 0ac: putfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._publicKey Lnet/rim/device/api/crypto/PublicKey;
      // 0af: aload 3
      // 0b0: invokevirtual java/io/DataInputStream.readShort ()S
      // 0b3: ldc_w 65535
      // 0b6: iand
      // 0b7: istore 4
      // 0b9: aload 0
      // 0ba: bipush 2
      // 0bc: iload 4
      // 0be: iadd
      // 0bf: newarray 8
      // 0c1: putfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._signature [B
      // 0c4: aload 0
      // 0c5: getfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._signature [B
      // 0c8: bipush 0
      // 0c9: iload 4
      // 0cb: bipush 8
      // 0cd: ishr
      // 0ce: i2b
      // 0cf: bastore
      // 0d0: aload 0
      // 0d1: getfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._signature [B
      // 0d4: bipush 1
      // 0d5: iload 4
      // 0d7: sipush 255
      // 0da: iand
      // 0db: i2b
      // 0dc: bastore
      // 0dd: aload 3
      // 0de: aload 0
      // 0df: getfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._signature [B
      // 0e2: bipush 2
      // 0e4: iload 4
      // 0e6: invokevirtual java/io/DataInputStream.read ([BII)I
      // 0e9: pop
      // 0ea: aload 0
      // 0eb: aload 2
      // 0ec: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 0ef: putfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._encoding [B
      // 0f2: aload 0
      // 0f3: invokespecial net/rim/device/api/crypto/certificate/wtls/WTLSCertificate.setHashCode ()V
      // 0f6: aload 0
      // 0f7: getfield net/rim/device/api/crypto/certificate/wtls/WTLSCertificate._encoding [B
      // 0fa: invokestatic net/rim/vm/Memory.createGroup (Ljava/lang/Object;)Z
      // 0fd: pop
      // 0fe: aload 0
      // 0ff: invokestatic net/rim/vm/Memory.createGroup (Ljava/lang/Object;)Z
      // 102: pop
      // 103: return
      // 104: astore 2
      // 105: new net/rim/device/api/crypto/certificate/CertificateParsingException
      // 108: dup
      // 109: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 10c: athrow
      // 10d: astore 2
      // 10e: new net/rim/device/api/crypto/certificate/CertificateParsingException
      // 111: dup
      // 112: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 115: athrow
      // 116: astore 2
      // 117: new net/rim/device/api/crypto/certificate/CertificateParsingException
      // 11a: dup
      // 11b: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 11e: athrow
      // 11f: astore 2
      // 120: new net/rim/device/api/crypto/certificate/CertificateParsingException
      // 123: dup
      // 124: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 127: athrow
      // 128: astore 2
      // 129: new net/rim/device/api/crypto/certificate/CertificateParsingException
      // 12c: dup
      // 12d: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 130: athrow
      // 131: astore 2
      // 132: new net/rim/device/api/crypto/certificate/CertificateParsingException
      // 135: dup
      // 136: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 139: athrow
      // 13a: astore 2
      // 13b: new net/rim/device/api/crypto/certificate/CertificateParsingException
      // 13e: dup
      // 13f: invokespecial net/rim/device/api/crypto/certificate/CertificateParsingException.<init> ()V
      // 142: athrow
      // try (8 -> 136): 137 net/rim/device/api/crypto/InvalidKeyEncodingException
      // try (8 -> 136): 142 null
      // try (8 -> 136): 147 net/rim/device/api/crypto/InvalidCryptoSystemException
      // try (8 -> 136): 152 net/rim/device/api/crypto/UnsupportedCryptoSystemException
      // try (8 -> 136): 157 null
      // try (8 -> 136): 162 null
      // try (8 -> 136): 167 null
   }

   @Override
   public final byte[] getEncoding() {
      return CryptoUtilities.getImmutableOrCopiedByteArray(this._encoding);
   }

   @Override
   public final byte[] getEncoding(int field) {
      return null;
   }

   @Override
   public final void verify(PublicKey issuerPublicKey) throws CertificateVerificationException {
      SHA1Digest digest = new SHA1Digest();
      digest.update(this._encoding, 0, this._encoding.length - this._signature.length);
      DecodedSignature decodedSignature = SignatureDecoder.decode(this._signature, 0, "WTLS", "" + this._signatureAlgorithm);
      decodedSignature.initialize(digest);
      if (!decodedSignature.getVerifier(issuerPublicKey).verify()) {
         throw new CertificateVerificationException();
      }
   }

   @Override
   public final void verify(KeyStore keystore) throws NoIssuerFoundException {
      if (keystore == null) {
         throw new IllegalArgumentException();
      }

      SubjectKeyStoreIndex index = new SubjectKeyStoreIndex();
      keystore.addIndex(index);
      Enumeration enumeration = keystore.elements(index.getID(), this._issuer);
      if (enumeration.hasMoreElements()) {
         KeyStoreData data = (KeyStoreData)enumeration.nextElement();
         PublicKey issuerKey = data.getPublicKey();
         this.verify(issuerKey);
      } else {
         throw new NoIssuerFoundException();
      }
   }

   @Override
   public final boolean isRoot() {
      return this._issuer.equals(this._subject);
   }

   @Override
   public final boolean isCA() {
      return this._isCertificateAuthority;
   }

   @Override
   public final void checkCertificateChain(int position, Certificate[] chain) {
   }

   @Override
   public final void verify() throws NoIssuerFoundException {
      if (this.isRoot()) {
         this.verify(this._publicKey);
      } else {
         throw new NoIssuerFoundException();
      }
   }

   @Override
   public final PublicKey getPublicKey() {
      return this._publicKey;
   }

   @Override
   public final String getPublicKeyAlgorithm() {
      return this._publicKey.getCryptoSystem().getAlgorithm();
   }

   @Override
   public final String getSignatureAlgorithm() {
      if (this._signatureAlgorithm == 1) {
         return "ECDSA";
      } else {
         return this._signatureAlgorithm == 2 ? "RSA_PKCS1" : null;
      }
   }

   @Override
   public final String getType() {
      return "WTLS";
   }

   @Override
   public final CertificateStatus getStatus() {
      CertificateStatusManager manager = CertificateStatusManager.getInstance();
      return manager.getStatus(this);
   }

   @Override
   public final void setStatus(CertificateStatus status) {
      CertificateStatusManager manager = CertificateStatusManager.getInstance();
      manager.setStatus(this, status, null);
   }

   @Override
   public final boolean isValid() {
      return this.isValid(System.currentTimeMillis());
   }

   @Override
   public final boolean isValid(long date) {
      return this._notBefore <= date && date <= this._notAfter;
   }

   @Override
   public final int getVersion() {
      return this._version;
   }

   @Override
   public final byte[] getSerialNumber() {
      return CryptoUtilities.getImmutableOrCopiedByteArray(this._serialNumber);
   }

   @Override
   public final DistinguishedName getIssuer() {
      return this._issuer;
   }

   @Override
   public final DistinguishedName getSubject() {
      return this._subject;
   }

   @Override
   public final long getNotBefore() {
      return this._notBefore;
   }

   @Override
   public final long getNotAfter() {
      return this._notAfter;
   }

   @Override
   public final CertificateExtension getExtension(OID oid) {
      return null;
   }

   @Override
   public final CertificateExtension[] getExtensions() {
      return null;
   }

   @Override
   public final CertificateExtension[] getExtensions(boolean criticalBit) {
      return null;
   }

   @Override
   public final String toString() {
      return CertificateUtilities.getSubjectFriendlyName(this);
   }

   private final void setHashCode() {
      this._hashCode = HashCodeCalculator.getCRC32(this._encoding);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof WTLSCertificate)) {
         if (!(other instanceof byte[])) {
            return false;
         }

         byte[] encoding = (byte[])other;
         return Arrays.equals(this._encoding, encoding);
      } else {
         WTLSCertificate cert = (WTLSCertificate)other;
         return Arrays.equals(this._encoding, cert.getEncoding());
      }
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   @Override
   public final int queryKeyUsage(long purpose) {
      return -1;
   }

   @Override
   public final String getSubjectFriendlyName() {
      return CertificateUtilities.getSubjectFriendlyName(this);
   }

   @Override
   public final CertificateDisplayField[] getCustomDisplayFields() {
      return null;
   }

   @Override
   public final Object getInformation(long id, Object param, Object defaultValue) {
      if (id == -1188891808812199856L) {
         return new Boolean(this.isRoot());
      } else if (id != -7341435958452683242L) {
         return id == -5753772986264564736L ? CertificateUtilities.getX509WTLSSummaryText(this) : defaultValue;
      } else {
         return new Boolean(!this.isRoot() && !this.isCA());
      }
   }
}
