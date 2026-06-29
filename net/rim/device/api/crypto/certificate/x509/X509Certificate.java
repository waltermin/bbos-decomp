package net.rim.device.api.crypto.certificate.x509;

import java.io.InputStream;
import net.rim.device.api.crypto.KeyPair;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.asn1.ASN1SignedByteArray;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateDisplayField;
import net.rim.device.api.crypto.certificate.CertificateExtension;
import net.rim.device.api.crypto.certificate.CertificateResources;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.keystore.CertificateStatusManager;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;
import net.rim.vm.Memory;

public final class X509Certificate implements Certificate, Persistable {
   private Mid _mid;
   public static final int CERTIFICATE_V1 = 0;
   public static final int CERTIFICATE_V2 = 1;
   public static final int CERTIFICATE_V3 = 2;
   public static final int ALT_NAME_EMAIL = 10;
   public static final int ALT_NAME_DNS = 11;
   public static final int ALT_NAME_URL = 12;

   public final int getBasicConstraints() {
      return this._mid._content.getBasicConstraints();
   }

   public final byte[] getSubjectUniqueIdentifier() {
      return this._mid._content.getSubjectUniqueIdentifier();
   }

   public final byte[] getIssuerUniqueIdentifier() {
      return this._mid._content.getIssuerUniqueIdentifier();
   }

   public final ASN1SignedByteArray getSignedSerialNumber() {
      return this._mid._content.getSignedSerialNumber();
   }

   public final boolean checkAgainstNameConstraints(X509Certificate cert) {
      if (cert == null) {
         throw new Object();
      } else {
         return this._mid._content.checkAgainstNameConstraints(cert);
      }
   }

   public final String[] getIssuerAltNameStrings(int type) {
      return this._mid._content.getIssuerAltNameStrings(type);
   }

   public final String[] getSubjectAltNameStrings(int type) {
      return this._mid._content.getSubjectAltNameStrings(type);
   }

   public final byte[] getAuthorityCertSerialNumber() {
      return this._mid._content.getAuthorityCertSerialNumber();
   }

   public final String[] getAuthorityCertIssuer() {
      return this._mid._content.getAuthorityCertIssuer();
   }

   public final byte[] getSubjectKeyIdentifier() {
      return this._mid._content.getSubjectKeyIdentifier();
   }

   @Override
   public final boolean isCA() {
      return this._mid._content.isCA();
   }

   @Override
   public final boolean isRoot() {
      if (this._mid._content.isRoot()) {
         try {
            this.verify(this.getPublicKey());
            return true;
         } finally {
            return false;
         }
      } else {
         return false;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void verify() {
      boolean unGrouped = this.ungroupIfRequired(true);
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         this._mid._content.verify();
         var4 = false;
      } finally {
         if (var4) {
            if (unGrouped) {
               Memory.createGroup(this._mid._content);
            }
         }
      }

      if (unGrouped) {
         Memory.createGroup(this._mid._content);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void verify(KeyStore keystore) {
      boolean unGrouped = this.ungroupIfRequired(true);
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         this._mid._content.verify(keystore);
         var5 = false;
      } finally {
         if (var5) {
            if (unGrouped) {
               Memory.createGroup(this._mid._content);
            }
         }
      }

      if (unGrouped) {
         Memory.createGroup(this._mid._content);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void verify(PublicKey issuerPublicKey) {
      boolean unGrouped = this.ungroupIfRequired(true);
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         this._mid._content.verify(issuerPublicKey);
         var5 = false;
      } finally {
         if (var5) {
            if (unGrouped) {
               Memory.createGroup(this._mid._content);
            }
         }
      }

      if (unGrouped) {
         Memory.createGroup(this._mid._content);
      }
   }

   @Override
   public final PublicKey getPublicKey() {
      boolean unGrouped = this.ungroupIfRequired(false);
      PublicKey publicKey = this._mid._content.getPublicKey();
      if (unGrouped) {
         Memory.createGroup(this._mid._content);
      }

      return publicKey;
   }

   @Override
   public final String getPublicKeyAlgorithm() {
      return this._mid._content.getPublicKeyAlgorithm();
   }

   @Override
   public final String getSignatureAlgorithm() {
      return this._mid._content.getSignatureAlgorithm();
   }

   @Override
   public final CertificateStatus getStatus() {
      return CertificateStatusManager.getInstance().getStatus(this);
   }

   @Override
   public final void setStatus(CertificateStatus status) {
      CertificateStatusManager.getInstance().setStatus(this, status, null);
   }

   @Override
   public final boolean isValid() {
      return this._mid._content.isValid();
   }

   @Override
   public final boolean isValid(long date) {
      return this._mid._content.isValid(date);
   }

   @Override
   public final int getVersion() {
      return this._mid._content.getVersion();
   }

   @Override
   public final byte[] getSerialNumber() {
      return this._mid._content.getSerialNumber();
   }

   @Override
   public final void checkCertificateChain(int position, Certificate[] chain) {
      if (chain != null && position >= 0 && position < chain.length) {
         int len = this.getBasicConstraints();
         if (len != Integer.MAX_VALUE && len != -1 && position - 1 > len) {
            throw new Object();
         }

         if (this._mid._content.getVersion() >= 2 && position > 0 && this._mid._content.getExtensions() != null) {
            int result = this.queryKeyUsage(32);
            if (result == 0) {
               throw new Object();
            }

            if (result == 1 && !this.isCA()) {
               throw new Object();
            }
         }

         Certificate var10000 = chain[position];
         if (chain[position] instanceof X509Certificate) {
            X509Certificate cert = (X509Certificate)var10000;

            for (int i = position + 1; i < chain.length; i++) {
               var10000 = chain[i];
               if (chain[i] instanceof X509Certificate) {
                  X509Certificate current = (X509Certificate)var10000;
                  if (!current.checkAgainstNameConstraints(cert)) {
                     throw new Object();
                  }
               }
            }
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final DistinguishedName getIssuer() {
      return this._mid._content.getIssuer();
   }

   @Override
   public final DistinguishedName getSubject() {
      return this._mid._content.getSubject();
   }

   @Override
   public final long getNotBefore() {
      return this._mid._content.getNotBefore();
   }

   @Override
   public final long getNotAfter() {
      return this._mid._content.getNotAfter();
   }

   @Override
   public final String getType() {
      return "X509";
   }

   @Override
   public final byte[] getEncoding(int field) {
      return this._mid._content.getEncoding(field);
   }

   @Override
   public final CertificateExtension getExtension(OID oid) {
      return this._mid._content.getExtension(oid);
   }

   @Override
   public final CertificateExtension[] getExtensions() {
      return this._mid._content.getExtensions();
   }

   @Override
   public final CertificateExtension[] getExtensions(boolean criticalBit) {
      return this._mid._content.getExtensions(criticalBit);
   }

   @Override
   public final byte[] getEncoding() {
      return this._mid._content.getEncoding();
   }

   @Override
   public final int queryKeyUsage(long purpose) {
      return this._mid._content.queryKeyUsage(purpose);
   }

   @Override
   public final String getSubjectFriendlyName() {
      return this._mid._content.getSubjectFriendlyName(this);
   }

   @Override
   public final CertificateDisplayField[] getCustomDisplayFields() {
      String[] emails = this.getSubjectAltNameStrings(10);
      if (emails == null) {
         return null;
      }

      int numEmails = emails.length;
      CertificateDisplayField[] displayFields = new Object[numEmails];

      for (int i = 0; i < numEmails; i++) {
         displayFields[i] = (CertificateDisplayField)(new Object(CertificateResources.getString(216), emails[i]));
      }

      return displayFields;
   }

   @Override
   public final Object getInformation(long id, Object param, Object defaultValue) {
      if (id == -7850001002262082664L) {
         return this.getEmailAddresses();
      }

      if (id == -3174973482910568002L) {
         try {
            ASN1InputByteArray input = new ASN1InputByteArray(this._mid._content._encoding, this._mid._content._encodedKeyOffset);
            input.readSequence();
            input.readSequence();
            int endPosition = input.getEndPosition();
            input.skipField();
            if (input.getStartPosition() < endPosition) {
               return input.peekNextTag() == 5 ? null : input.readFieldAsByteArray();
            } else {
               return null;
            }
         } catch (ASN1EncodingException e) {
            return null;
         }
      } else if (id == -1188891808812199856L) {
         return new Object(this.isRoot());
      } else if (id != -7341435958452683242L) {
         return id == -5753772986264564736L ? CertificateUtilities.getX509WTLSSummaryText(this) : defaultValue;
      } else {
         return new Object(!this.isRoot() && !this.isCA());
      }
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      byte[] otherEncoding;
      if (!(other instanceof X509Certificate)) {
         if (!(other instanceof byte[])) {
            return false;
         }

         otherEncoding = (byte[])other;
      } else {
         otherEncoding = ((X509Certificate)other)._mid._content._encoding;
      }

      return Arrays.equals(this._mid._content._encoding, otherEncoding);
   }

   @Override
   public final int hashCode() {
      return this._mid._content.hashCode();
   }

   @Override
   public final String toString() {
      return this.getSubjectFriendlyName();
   }

   private final boolean ungroupIfRequired(boolean verification) {
      if (this._mid._content._publicKey != null && (!verification || this._mid._content._issuerPublicKey != null)) {
         return false;
      }

      this._mid._content = (Content)Memory.expandGroup(this._mid._content);
      return true;
   }

   public X509Certificate(InputStream input) {
      this(new ASN1InputStream(input));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public X509Certificate(ASN1InputStream asn1Input) {
      if (asn1Input == null) {
         throw new Object();
      }

      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         byte[] e = asn1Input.readFieldAsByteArray();
         this._mid = new Mid(e);
         Memory.createGroup(this._mid._content);
         return;
      } catch (ASN1EncodingException var6) {
         var5 = false;
      } finally {
         if (var5) {
            throw new Object();
         }
      }

      throw new Object();
   }

   public X509Certificate(byte[] encoding) {
      this._mid = new Mid(encoding);
      Memory.createGroup(this._mid._content);
   }

   private final String[] getEmailAddresses() {
      String[] emailAddresses = this.getSubjectAltNameStrings(10);
      X509DistinguishedName dn = (X509DistinguishedName)this.getSubject();
      String dnEmailAddress = dn.getEmailAddress();
      if (dnEmailAddress != null) {
         if (emailAddresses == null) {
            return new Object[]{dnEmailAddress};
         }

         int numEmailAddresses = emailAddresses.length;

         for (int i = 0; i < numEmailAddresses; i++) {
            if (dnEmailAddress.equals(emailAddresses[i])) {
               return emailAddresses;
            }
         }

         Array.resize(emailAddresses, numEmailAddresses + 1);
         emailAddresses[numEmailAddresses] = dnEmailAddress;
      }

      return emailAddresses;
   }

   public static final X509Certificate createX509Certificate(
      KeyPair subjectKeyPair,
      X509DistinguishedName subject,
      long keyUsage,
      byte[] serialNumber,
      CertificateExtension[] extensionList,
      long validNotBeforeDate,
      long validNotAfterDate
   ) {
      if (subjectKeyPair == null) {
         throw new Object();
      }

      subjectKeyPair.verify();
      return createX509CertificateImpl(
         subjectKeyPair.getPublicKey(),
         subject,
         keyUsage,
         serialNumber,
         extensionList,
         subject,
         subjectKeyPair.getPrivateKey(),
         validNotBeforeDate,
         validNotAfterDate
      );
   }

   public static final X509Certificate createX509Certificate(
      PublicKey subjectKey,
      X509DistinguishedName subject,
      long keyUsage,
      byte[] serialNumber,
      CertificateExtension[] extensionList,
      X509DistinguishedName issuer,
      PrivateKey issuerKey,
      long validNotBeforeDate,
      long validNotAfterDate
   ) {
      if (subjectKey != null && issuerKey != null) {
         subjectKey.verify();
         issuerKey.verify();
         return createX509CertificateImpl(subjectKey, subject, keyUsage, serialNumber, extensionList, issuer, issuerKey, validNotBeforeDate, validNotAfterDate);
      } else {
         throw new Object();
      }
   }

   private static final X509Certificate createX509CertificateImpl(
      PublicKey param0,
      X509DistinguishedName param1,
      long param2,
      byte[] param4,
      CertificateExtension[] param5,
      X509DistinguishedName param6,
      PrivateKey param7,
      long param8,
      long param10
   ) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ifnull 009
      // 004: aload 6
      // 006: ifnonnull 011
      // 009: new java/lang/Object
      // 00c: dup
      // 00d: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 010: athrow
      // 011: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 014: dup
      // 015: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 018: astore 12
      // 01a: aload 12
      // 01c: bipush 2
      // 01e: bipush 1
      // 01f: bipush 0
      // 020: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger (III)V
      // 023: aload 4
      // 025: ifnull 032
      // 028: aload 12
      // 02a: aload 4
      // 02c: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger ([B)V
      // 02f: goto 03a
      // 032: aload 12
      // 034: invokestatic net/rim/device/api/crypto/RandomSource.getInt ()I
      // 037: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeInteger (I)V
      // 03a: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 03d: dup
      // 03e: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 041: astore 13
      // 043: aload 7
      // 045: invokeinterface net/rim/device/api/crypto/PrivateKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 04a: invokeinterface net/rim/device/api/crypto/CryptoSystem.getAlgorithm ()Ljava/lang/String; 1
      // 04f: astore 14
      // 051: aload 14
      // 053: ifnonnull 05e
      // 056: new java/lang/Object
      // 059: dup
      // 05a: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 05d: athrow
      // 05e: aload 14
      // 060: ldc_w "RSA"
      // 063: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 066: ifeq 077
      // 069: aload 13
      // 06b: ldc_w 542901820
      // 06e: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 071: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 074: goto 0b1
      // 077: aload 14
      // 079: ldc_w "EC"
      // 07c: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 07f: ifeq 090
      // 082: aload 13
      // 084: ldc_w -1487362072
      // 087: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 08a: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 08d: goto 0b1
      // 090: aload 14
      // 092: ldc_w "DSA"
      // 095: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 098: ifeq 0a9
      // 09b: aload 13
      // 09d: ldc_w -1487364632
      // 0a0: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 0a3: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 0a6: goto 0b1
      // 0a9: new java/lang/Object
      // 0ac: dup
      // 0ad: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0b0: athrow
      // 0b1: aload 12
      // 0b3: aload 13
      // 0b5: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 0b8: aload 12
      // 0ba: aload 6
      // 0bc: invokevirtual net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.getEncoding ()[B
      // 0bf: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 0c2: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 0c5: dup
      // 0c6: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 0c9: astore 15
      // 0cb: lload 8
      // 0cd: bipush 0
      // 0ce: i2l
      // 0cf: lcmp
      // 0d0: ifne 0d8
      // 0d3: invokestatic java/lang/System.currentTimeMillis ()J
      // 0d6: lstore 8
      // 0d8: aload 15
      // 0da: lload 8
      // 0dc: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeTime (J)V
      // 0df: lload 10
      // 0e1: bipush 0
      // 0e2: i2l
      // 0e3: lcmp
      // 0e4: ifne 0f0
      // 0e7: lload 8
      // 0e9: ldc_w 889032704
      // 0ec: i2l
      // 0ed: ladd
      // 0ee: lstore 10
      // 0f0: aload 15
      // 0f2: lload 10
      // 0f4: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeTime (J)V
      // 0f7: aload 12
      // 0f9: aload 15
      // 0fb: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 0fe: aload 12
      // 100: aload 1
      // 101: invokevirtual net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.getEncoding ()[B
      // 104: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 107: aload 12
      // 109: aload 0
      // 10a: ldc_w "X509"
      // 10d: invokestatic net/rim/device/api/crypto/encoder/PublicKeyEncoder.encode (Lnet/rim/device/api/crypto/PublicKey;Ljava/lang/String;)Lnet/rim/device/api/crypto/encoder/EncodedKey;
      // 110: invokevirtual net/rim/device/api/crypto/encoder/EncodedKey.getEncodedKey ()[B
      // 113: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 116: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 119: dup
      // 11a: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 11d: astore 16
      // 11f: lload 2
      // 120: bipush 0
      // 121: i2l
      // 122: lcmp
      // 123: ifgt 129
      // 126: goto 324
      // 129: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 12c: dup
      // 12d: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 130: astore 17
      // 132: aload 17
      // 134: ldc_w -1252264021
      // 137: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 13a: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 13d: aload 17
      // 13f: bipush 1
      // 140: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeBoolean (Z)V
      // 143: bipush 2
      // 145: newarray 8
      // 147: astore 18
      // 149: bipush 9
      // 14b: istore 19
      // 14d: lload 2
      // 14e: bipush 1
      // 14f: i2l
      // 150: land
      // 151: bipush 0
      // 152: i2l
      // 153: lcmp
      // 154: ifeq 161
      // 157: aload 18
      // 159: bipush 0
      // 15a: dup2
      // 15b: baload
      // 15c: bipush -128
      // 15e: ior
      // 15f: i2b
      // 160: bastore
      // 161: lload 2
      // 162: bipush 2
      // 164: i2l
      // 165: land
      // 166: bipush 0
      // 167: i2l
      // 168: lcmp
      // 169: ifeq 176
      // 16c: aload 18
      // 16e: bipush 0
      // 16f: dup2
      // 170: baload
      // 171: bipush 64
      // 173: ior
      // 174: i2b
      // 175: bastore
      // 176: lload 2
      // 177: bipush 4
      // 179: i2l
      // 17a: land
      // 17b: bipush 0
      // 17c: i2l
      // 17d: lcmp
      // 17e: ifeq 18b
      // 181: aload 18
      // 183: bipush 0
      // 184: dup2
      // 185: baload
      // 186: bipush 32
      // 188: ior
      // 189: i2b
      // 18a: bastore
      // 18b: lload 2
      // 18c: bipush 8
      // 18e: i2l
      // 18f: land
      // 190: bipush 0
      // 191: i2l
      // 192: lcmp
      // 193: ifeq 1a0
      // 196: aload 18
      // 198: bipush 0
      // 199: dup2
      // 19a: baload
      // 19b: bipush 16
      // 19d: ior
      // 19e: i2b
      // 19f: bastore
      // 1a0: lload 2
      // 1a1: bipush 16
      // 1a3: i2l
      // 1a4: land
      // 1a5: bipush 0
      // 1a6: i2l
      // 1a7: lcmp
      // 1a8: ifeq 1b5
      // 1ab: aload 18
      // 1ad: bipush 0
      // 1ae: dup2
      // 1af: baload
      // 1b0: bipush 8
      // 1b2: ior
      // 1b3: i2b
      // 1b4: bastore
      // 1b5: lload 2
      // 1b6: bipush 32
      // 1b8: i2l
      // 1b9: land
      // 1ba: bipush 0
      // 1bb: i2l
      // 1bc: lcmp
      // 1bd: ifeq 1ca
      // 1c0: aload 18
      // 1c2: bipush 0
      // 1c3: dup2
      // 1c4: baload
      // 1c5: bipush 4
      // 1c7: ior
      // 1c8: i2b
      // 1c9: bastore
      // 1ca: lload 2
      // 1cb: bipush 64
      // 1cd: i2l
      // 1ce: land
      // 1cf: bipush 0
      // 1d0: i2l
      // 1d1: lcmp
      // 1d2: ifeq 1df
      // 1d5: aload 18
      // 1d7: bipush 0
      // 1d8: dup2
      // 1d9: baload
      // 1da: bipush 2
      // 1dc: ior
      // 1dd: i2b
      // 1de: bastore
      // 1df: lload 2
      // 1e0: sipush 128
      // 1e3: i2l
      // 1e4: land
      // 1e5: bipush 0
      // 1e6: i2l
      // 1e7: lcmp
      // 1e8: ifeq 1f4
      // 1eb: aload 18
      // 1ed: bipush 0
      // 1ee: dup2
      // 1ef: baload
      // 1f0: bipush 1
      // 1f1: ior
      // 1f2: i2b
      // 1f3: bastore
      // 1f4: lload 2
      // 1f5: sipush 256
      // 1f8: i2l
      // 1f9: land
      // 1fa: bipush 0
      // 1fb: i2l
      // 1fc: lcmp
      // 1fd: ifeq 20a
      // 200: aload 18
      // 202: bipush 1
      // 203: dup2
      // 204: baload
      // 205: bipush -128
      // 207: ior
      // 208: i2b
      // 209: bastore
      // 20a: new net/rim/device/api/crypto/asn1/ASN1BitSet
      // 20d: dup
      // 20e: aload 18
      // 210: iload 19
      // 212: invokespecial net/rim/device/api/crypto/asn1/ASN1BitSet.<init> ([BI)V
      // 215: astore 20
      // 217: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 21a: dup
      // 21b: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 21e: astore 21
      // 220: aload 21
      // 222: aload 20
      // 224: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeBitString (Lnet/rim/device/api/crypto/asn1/ASN1BitSet;)V
      // 227: aload 17
      // 229: aload 21
      // 22b: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 22e: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([B)V
      // 231: aload 16
      // 233: aload 17
      // 235: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 238: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 23b: dup
      // 23c: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 23f: astore 22
      // 241: aload 22
      // 243: ldc_w -1250822229
      // 246: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 249: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 24c: aload 22
      // 24e: bipush 1
      // 24f: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeBoolean (Z)V
      // 252: bipush 0
      // 253: istore 23
      // 255: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 258: dup
      // 259: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 25c: astore 24
      // 25e: lload 2
      // 25f: sipush 512
      // 262: i2l
      // 263: land
      // 264: bipush 0
      // 265: i2l
      // 266: lcmp
      // 267: ifeq 278
      // 26a: aload 24
      // 26c: ldc_w -477712431
      // 26f: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 272: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 275: bipush 1
      // 276: istore 23
      // 278: lload 2
      // 279: sipush 1024
      // 27c: i2l
      // 27d: land
      // 27e: bipush 0
      // 27f: i2l
      // 280: lcmp
      // 281: ifeq 292
      // 284: aload 24
      // 286: ldc_w -477711407
      // 289: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 28c: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 28f: bipush 1
      // 290: istore 23
      // 292: lload 2
      // 293: sipush 2048
      // 296: i2l
      // 297: land
      // 298: bipush 0
      // 299: i2l
      // 29a: lcmp
      // 29b: ifeq 2ac
      // 29e: aload 24
      // 2a0: ldc_w -477710383
      // 2a3: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 2a6: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 2a9: bipush 1
      // 2aa: istore 23
      // 2ac: lload 2
      // 2ad: sipush 4096
      // 2b0: i2l
      // 2b1: land
      // 2b2: bipush 0
      // 2b3: i2l
      // 2b4: lcmp
      // 2b5: ifeq 2c6
      // 2b8: aload 24
      // 2ba: ldc_w -477709359
      // 2bd: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 2c0: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 2c3: bipush 1
      // 2c4: istore 23
      // 2c6: lload 2
      // 2c7: sipush 8192
      // 2ca: i2l
      // 2cb: land
      // 2cc: bipush 0
      // 2cd: i2l
      // 2ce: lcmp
      // 2cf: ifeq 2e0
      // 2d2: aload 24
      // 2d4: ldc_w -477705263
      // 2d7: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 2da: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 2dd: bipush 1
      // 2de: istore 23
      // 2e0: lload 2
      // 2e1: sipush 16384
      // 2e4: i2l
      // 2e5: land
      // 2e6: bipush 0
      // 2e7: i2l
      // 2e8: lcmp
      // 2e9: ifeq 2fa
      // 2ec: aload 24
      // 2ee: ldc_w -477712248
      // 2f1: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 2f4: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 2f7: bipush 1
      // 2f8: istore 23
      // 2fa: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 2fd: dup
      // 2fe: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 301: astore 25
      // 303: aload 25
      // 305: aload 24
      // 307: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 30a: aload 25
      // 30c: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 30f: astore 26
      // 311: aload 22
      // 313: aload 26
      // 315: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([B)V
      // 318: iload 23
      // 31a: ifeq 324
      // 31d: aload 16
      // 31f: aload 22
      // 321: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 324: bipush 0
      // 325: istore 17
      // 327: ldc_w -1252001877
      // 32a: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 32d: astore 18
      // 32f: aload 5
      // 331: ifnull 38f
      // 334: bipush 0
      // 335: istore 19
      // 337: iload 19
      // 339: aload 5
      // 33b: arraylength
      // 33c: if_icmpge 38f
      // 33f: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 342: dup
      // 343: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 346: astore 20
      // 348: aload 5
      // 34a: iload 19
      // 34c: aaload
      // 34d: invokevirtual net/rim/device/api/crypto/certificate/CertificateExtension.getOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 350: aload 18
      // 352: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 355: ifeq 35b
      // 358: bipush 1
      // 359: istore 17
      // 35b: aload 20
      // 35d: aload 5
      // 35f: iload 19
      // 361: aaload
      // 362: invokevirtual net/rim/device/api/crypto/certificate/CertificateExtension.getOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 365: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 368: aload 20
      // 36a: aload 5
      // 36c: iload 19
      // 36e: aaload
      // 36f: invokevirtual net/rim/device/api/crypto/certificate/CertificateExtension.getCritical ()Z
      // 372: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeBoolean (Z)V
      // 375: aload 20
      // 377: aload 5
      // 379: iload 19
      // 37b: aaload
      // 37c: invokevirtual net/rim/device/api/crypto/certificate/CertificateExtension.getValue ()[B
      // 37f: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([B)V
      // 382: aload 16
      // 384: aload 20
      // 386: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 389: iinc 19 1
      // 38c: goto 337
      // 38f: iload 17
      // 391: ifne 3e3
      // 394: aload 1
      // 395: aload 6
      // 397: invokevirtual net/rim/device/api/crypto/certificate/x509/X509DistinguishedName.equals (Ljava/lang/Object;)Z
      // 39a: ifeq 3e3
      // 39d: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 3a0: dup
      // 3a1: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 3a4: astore 19
      // 3a6: aload 19
      // 3a8: aload 18
      // 3aa: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 3ad: aload 19
      // 3af: bipush 1
      // 3b0: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeBoolean (Z)V
      // 3b3: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 3b6: dup
      // 3b7: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 3ba: astore 20
      // 3bc: aload 20
      // 3be: bipush 1
      // 3bf: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeBoolean (Z)V
      // 3c2: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 3c5: dup
      // 3c6: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 3c9: astore 21
      // 3cb: aload 21
      // 3cd: aload 20
      // 3cf: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 3d2: aload 19
      // 3d4: aload 21
      // 3d6: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 3d9: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOctetString ([B)V
      // 3dc: aload 16
      // 3de: aload 19
      // 3e0: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 3e3: aload 12
      // 3e5: aload 16
      // 3e7: bipush 1
      // 3e8: bipush 3
      // 3ea: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;II)V
      // 3ed: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 3f0: dup
      // 3f1: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 3f4: astore 19
      // 3f6: aload 19
      // 3f8: aload 12
      // 3fa: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 3fd: aload 19
      // 3ff: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 402: astore 20
      // 404: aload 7
      // 406: aconst_null
      // 407: invokestatic net/rim/device/api/crypto/SignatureSignerFactory.getInstance (Lnet/rim/device/api/crypto/PrivateKey;Ljava/lang/String;)Lnet/rim/device/api/crypto/SignatureSigner;
      // 40a: astore 21
      // 40c: aload 21
      // 40e: aload 20
      // 410: invokeinterface net/rim/device/api/crypto/SignatureSigner.update ([B)V 2
      // 415: aload 21
      // 417: ldc_w "X509"
      // 41a: invokestatic net/rim/device/api/crypto/encoder/SignatureEncoder.encode (Lnet/rim/device/api/crypto/SignatureSigner;Ljava/lang/String;)Lnet/rim/device/api/crypto/encoder/EncodedSignature;
      // 41d: astore 22
      // 41f: aload 19
      // 421: aload 22
      // 423: invokevirtual net/rim/device/api/crypto/encoder/EncodedSignature.getEncodedSignature ()[B
      // 426: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 429: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 42c: dup
      // 42d: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 430: astore 23
      // 432: aload 23
      // 434: aload 19
      // 436: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 439: new net/rim/device/api/crypto/certificate/x509/X509Certificate
      // 43c: dup
      // 43d: aload 23
      // 43f: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 442: invokespecial net/rim/device/api/crypto/certificate/x509/X509Certificate.<init> ([B)V
      // 445: areturn
      // 446: astore 12
      // 448: goto 452
      // 44b: astore 12
      // 44d: goto 452
      // 450: astore 12
      // 452: new java/lang/Object
      // 455: dup
      // 456: invokespecial java/lang/RuntimeException.<init> ()V
      // 459: athrow
      // try (8 -> 555): 556 null
      // try (8 -> 555): 558 null
      // try (8 -> 555): 560 null
   }
}
