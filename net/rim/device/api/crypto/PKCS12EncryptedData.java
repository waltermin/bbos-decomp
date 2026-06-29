package net.rim.device.api.crypto;

import net.rim.device.api.i18n.ResourceBundle;

public class PKCS12EncryptedData extends PKCS12ContentInfo {
   private PKCS12Data _decryptedData;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(60462186577914032L, "net.rim.device.internal.resource.crypto.KeyStore");

   public PKCS12EncryptedData(byte[] data, PKCS12ContentInfo parent) {
      super(data, parent);
   }

   @Override
   public void parse() throws PKCS12ParsingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/PKCS12ContentInfo._parsed Z
      // 004: ifeq 008
      // 007: return
      // 008: new net/rim/device/api/crypto/asn1/ASN1InputByteArray
      // 00b: dup
      // 00c: aload 0
      // 00d: getfield net/rim/device/api/crypto/PKCS12ContentInfo._buffer [B
      // 010: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 013: astore 1
      // 014: aload 1
      // 015: bipush 1
      // 016: bipush 0
      // 017: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence (II)V
      // 01a: aload 1
      // 01b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readInteger ()I
      // 01e: istore 2
      // 01f: iload 2
      // 020: ifeq 02b
      // 023: new net/rim/device/api/crypto/PKCS12UnsupportedOperationException
      // 026: dup
      // 027: invokespecial net/rim/device/api/crypto/PKCS12UnsupportedOperationException.<init> ()V
      // 02a: athrow
      // 02b: bipush 0
      // 02c: istore 3
      // 02d: aload 1
      // 02e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 031: aload 1
      // 032: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 035: astore 4
      // 037: aload 1
      // 038: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 03b: aload 1
      // 03c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 03f: astore 5
      // 041: aload 1
      // 042: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 045: aload 1
      // 046: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString ()[B
      // 049: astore 6
      // 04b: aload 1
      // 04c: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readInteger ()I
      // 04f: istore 7
      // 051: aload 1
      // 052: bipush 2
      // 054: bipush 0
      // 055: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString (II)[B
      // 058: astore 8
      // 05a: aload 8
      // 05c: arraylength
      // 05d: newarray 8
      // 05f: astore 9
      // 061: aconst_null
      // 062: astore 10
      // 064: aconst_null
      // 065: astore 11
      // 067: aconst_null
      // 068: astore 12
      // 06a: aload 0
      // 06b: invokevirtual net/rim/device/api/crypto/PKCS12EncryptedData.getPasswords ()Ljava/util/Enumeration;
      // 06e: astore 15
      // 070: bipush 0
      // 071: istore 16
      // 073: iload 16
      // 075: bipush 7
      // 077: if_icmplt 07d
      // 07a: goto 436
      // 07d: iload 16
      // 07f: ifne 08b
      // 082: aconst_null
      // 083: astore 11
      // 085: iinc 16 1
      // 088: goto 157
      // 08b: iload 16
      // 08d: bipush 1
      // 08e: if_icmpne 0ba
      // 091: aload 15
      // 093: ifnull 0ac
      // 096: aload 15
      // 098: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 09d: ifeq 0ac
      // 0a0: aload 15
      // 0a2: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0a7: checkcast [B
      // 0aa: astore 11
      // 0ac: aload 11
      // 0ae: ifnull 0b4
      // 0b1: goto 157
      // 0b4: iinc 16 1
      // 0b7: goto 073
      // 0ba: iload 16
      // 0bc: bipush 2
      // 0be: if_icmpne 0dc
      // 0c1: new net/rim/device/internal/ui/component/PasswordDialog
      // 0c4: dup
      // 0c5: getstatic net/rim/device/api/crypto/PKCS12EncryptedData._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0c8: sipush 6046
      // 0cb: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0ce: bipush 0
      // 0cf: bipush 50
      // 0d1: ldc_w 134217728
      // 0d4: invokespecial net/rim/device/internal/ui/component/PasswordDialog.<init> (Ljava/lang/String;ZII)V
      // 0d7: astore 10
      // 0d9: goto 0f4
      // 0dc: new net/rim/device/internal/ui/component/PasswordDialog
      // 0df: dup
      // 0e0: getstatic net/rim/device/api/crypto/PKCS12EncryptedData._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0e3: sipush 6047
      // 0e6: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0e9: bipush 0
      // 0ea: bipush 50
      // 0ec: ldc_w 134217728
      // 0ef: invokespecial net/rim/device/internal/ui/component/PasswordDialog.<init> (Ljava/lang/String;ZII)V
      // 0f2: astore 10
      // 0f4: aload 10
      // 0f6: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.show (Lnet/rim/device/internal/ui/component/PopupDialog;)V
      // 0f9: aload 10
      // 0fb: invokevirtual net/rim/device/internal/ui/component/PasswordDialog.getPassword ()[B
      // 0fe: astore 12
      // 100: aload 12
      // 102: ifnull 112
      // 105: aload 10
      // 107: invokevirtual net/rim/device/internal/ui/component/PopupDialog.getCloseReason ()I
      // 10a: aload 10
      // 10c: pop
      // 10d: bipush -1
      // 10f: if_icmpne 11d
      // 112: new net/rim/device/api/crypto/PKCS12ParsingException
      // 115: dup
      // 116: ldc_w "No Key: Unable to decrypt the data"
      // 119: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> (Ljava/lang/String;)V
      // 11c: athrow
      // 11d: aload 12
      // 11f: arraylength
      // 120: bipush 2
      // 122: imul
      // 123: bipush 2
      // 125: iadd
      // 126: newarray 8
      // 128: astore 11
      // 12a: aload 11
      // 12c: bipush 0
      // 12d: bipush 0
      // 12e: aload 11
      // 130: arraylength
      // 131: invokestatic net/rim/device/api/util/Arrays.fill ([BBII)V
      // 134: bipush 0
      // 135: istore 17
      // 137: iload 17
      // 139: aload 12
      // 13b: arraylength
      // 13c: if_icmpge 154
      // 13f: aload 11
      // 141: iload 17
      // 143: bipush 2
      // 145: imul
      // 146: bipush 1
      // 147: iadd
      // 148: aload 12
      // 14a: iload 17
      // 14c: baload
      // 14d: bastore
      // 14e: iinc 17 1
      // 151: goto 137
      // 154: iinc 16 1
      // 157: aconst_null
      // 158: astore 18
      // 15a: aconst_null
      // 15b: astore 19
      // 15d: aload 5
      // 15f: ldc_w 314138129
      // 162: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 165: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 168: ifeq 1b4
      // 16b: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 16e: dup
      // 16f: aload 11
      // 171: aload 6
      // 173: iload 7
      // 175: bipush 1
      // 176: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 179: astore 13
      // 17b: new net/rim/device/api/crypto/ARC4PseudoRandomSource
      // 17e: dup
      // 17f: new net/rim/device/api/crypto/ARC4Key
      // 182: dup
      // 183: aload 13
      // 185: bipush 16
      // 187: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 18a: invokespecial net/rim/device/api/crypto/ARC4Key.<init> ([B)V
      // 18d: invokespecial net/rim/device/api/crypto/ARC4PseudoRandomSource.<init> (Lnet/rim/device/api/crypto/ARC4Key;)V
      // 190: astore 20
      // 192: new net/rim/device/api/crypto/PRNGDecryptor
      // 195: dup
      // 196: aload 20
      // 198: new java/io/ByteArrayInputStream
      // 19b: dup
      // 19c: aload 8
      // 19e: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 1a1: invokespecial net/rim/device/api/crypto/PRNGDecryptor.<init> (Lnet/rim/device/api/crypto/PseudoRandomSource;Ljava/io/InputStream;)V
      // 1a4: astore 21
      // 1a6: aload 21
      // 1a8: aload 9
      // 1aa: bipush 0
      // 1ab: aload 9
      // 1ad: arraylength
      // 1ae: invokevirtual net/rim/device/api/crypto/PRNGDecryptor.decrypt ([BII)V
      // 1b1: goto 3d3
      // 1b4: aload 5
      // 1b6: ldc_w 314138130
      // 1b9: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 1bc: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 1bf: ifeq 20b
      // 1c2: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 1c5: dup
      // 1c6: aload 11
      // 1c8: aload 6
      // 1ca: iload 7
      // 1cc: bipush 1
      // 1cd: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 1d0: astore 13
      // 1d2: new net/rim/device/api/crypto/ARC4PseudoRandomSource
      // 1d5: dup
      // 1d6: new net/rim/device/api/crypto/ARC4Key
      // 1d9: dup
      // 1da: aload 13
      // 1dc: bipush 5
      // 1de: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 1e1: invokespecial net/rim/device/api/crypto/ARC4Key.<init> ([B)V
      // 1e4: invokespecial net/rim/device/api/crypto/ARC4PseudoRandomSource.<init> (Lnet/rim/device/api/crypto/ARC4Key;)V
      // 1e7: astore 20
      // 1e9: new net/rim/device/api/crypto/PRNGDecryptor
      // 1ec: dup
      // 1ed: aload 20
      // 1ef: new java/io/ByteArrayInputStream
      // 1f2: dup
      // 1f3: aload 8
      // 1f5: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 1f8: invokespecial net/rim/device/api/crypto/PRNGDecryptor.<init> (Lnet/rim/device/api/crypto/PseudoRandomSource;Ljava/io/InputStream;)V
      // 1fb: astore 21
      // 1fd: aload 21
      // 1ff: aload 9
      // 201: bipush 0
      // 202: aload 9
      // 204: arraylength
      // 205: invokevirtual net/rim/device/api/crypto/PRNGDecryptor.decrypt ([BII)V
      // 208: goto 3d3
      // 20b: aload 5
      // 20d: ldc_w 314138131
      // 210: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 213: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 216: ifeq 274
      // 219: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 21c: dup
      // 21d: aload 11
      // 21f: aload 6
      // 221: iload 7
      // 223: bipush 1
      // 224: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 227: astore 13
      // 229: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 22c: dup
      // 22d: aload 11
      // 22f: aload 6
      // 231: iload 7
      // 233: bipush 2
      // 235: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 238: astore 14
      // 23a: new net/rim/device/api/crypto/TripleDESKey
      // 23d: dup
      // 23e: aload 13
      // 240: bipush 24
      // 242: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 245: invokespecial net/rim/device/api/crypto/TripleDESKey.<init> ([B)V
      // 248: astore 17
      // 24a: new net/rim/device/api/crypto/InitializationVector
      // 24d: dup
      // 24e: aload 14
      // 250: bipush 8
      // 252: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 255: invokespecial net/rim/device/api/crypto/InitializationVector.<init> ([B)V
      // 258: astore 19
      // 25a: new net/rim/device/api/crypto/CBCDecryptorEngine
      // 25d: dup
      // 25e: new net/rim/device/api/crypto/TripleDESDecryptorEngine
      // 261: dup
      // 262: aload 17
      // 264: checkcast net/rim/device/api/crypto/TripleDESKey
      // 267: invokespecial net/rim/device/api/crypto/TripleDESDecryptorEngine.<init> (Lnet/rim/device/api/crypto/TripleDESKey;)V
      // 26a: aload 19
      // 26c: invokespecial net/rim/device/api/crypto/CBCDecryptorEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;Lnet/rim/device/api/crypto/InitializationVector;)V
      // 26f: astore 18
      // 271: goto 3b0
      // 274: aload 5
      // 276: ldc_w 314138132
      // 279: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 27c: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 27f: ifeq 2dd
      // 282: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 285: dup
      // 286: aload 11
      // 288: aload 6
      // 28a: iload 7
      // 28c: bipush 1
      // 28d: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 290: astore 13
      // 292: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 295: dup
      // 296: aload 11
      // 298: aload 6
      // 29a: iload 7
      // 29c: bipush 2
      // 29e: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 2a1: astore 14
      // 2a3: new net/rim/device/api/crypto/TripleDESKey
      // 2a6: dup
      // 2a7: aload 13
      // 2a9: bipush 16
      // 2ab: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 2ae: invokespecial net/rim/device/api/crypto/TripleDESKey.<init> ([B)V
      // 2b1: astore 17
      // 2b3: new net/rim/device/api/crypto/InitializationVector
      // 2b6: dup
      // 2b7: aload 14
      // 2b9: bipush 8
      // 2bb: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 2be: invokespecial net/rim/device/api/crypto/InitializationVector.<init> ([B)V
      // 2c1: astore 19
      // 2c3: new net/rim/device/api/crypto/CBCDecryptorEngine
      // 2c6: dup
      // 2c7: new net/rim/device/api/crypto/TripleDESDecryptorEngine
      // 2ca: dup
      // 2cb: aload 17
      // 2cd: checkcast net/rim/device/api/crypto/TripleDESKey
      // 2d0: invokespecial net/rim/device/api/crypto/TripleDESDecryptorEngine.<init> (Lnet/rim/device/api/crypto/TripleDESKey;)V
      // 2d3: aload 19
      // 2d5: invokespecial net/rim/device/api/crypto/CBCDecryptorEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;Lnet/rim/device/api/crypto/InitializationVector;)V
      // 2d8: astore 18
      // 2da: goto 3b0
      // 2dd: aload 5
      // 2df: ldc_w 314138133
      // 2e2: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 2e5: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 2e8: ifeq 346
      // 2eb: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 2ee: dup
      // 2ef: aload 11
      // 2f1: aload 6
      // 2f3: iload 7
      // 2f5: bipush 1
      // 2f6: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 2f9: astore 13
      // 2fb: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 2fe: dup
      // 2ff: aload 11
      // 301: aload 6
      // 303: iload 7
      // 305: bipush 2
      // 307: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 30a: astore 14
      // 30c: new net/rim/device/api/crypto/RC2Key
      // 30f: dup
      // 310: aload 13
      // 312: bipush 16
      // 314: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 317: invokespecial net/rim/device/api/crypto/RC2Key.<init> ([B)V
      // 31a: astore 17
      // 31c: new net/rim/device/api/crypto/InitializationVector
      // 31f: dup
      // 320: aload 14
      // 322: bipush 8
      // 324: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 327: invokespecial net/rim/device/api/crypto/InitializationVector.<init> ([B)V
      // 32a: astore 19
      // 32c: new net/rim/device/api/crypto/CBCDecryptorEngine
      // 32f: dup
      // 330: new net/rim/device/api/crypto/RC2DecryptorEngine
      // 333: dup
      // 334: aload 17
      // 336: checkcast net/rim/device/api/crypto/RC2Key
      // 339: invokespecial net/rim/device/api/crypto/RC2DecryptorEngine.<init> (Lnet/rim/device/api/crypto/RC2Key;)V
      // 33c: aload 19
      // 33e: invokespecial net/rim/device/api/crypto/CBCDecryptorEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;Lnet/rim/device/api/crypto/InitializationVector;)V
      // 341: astore 18
      // 343: goto 3b0
      // 346: aload 5
      // 348: ldc_w 314138134
      // 34b: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 34e: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 351: ifeq 3af
      // 354: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 357: dup
      // 358: aload 11
      // 35a: aload 6
      // 35c: iload 7
      // 35e: bipush 1
      // 35f: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 362: astore 13
      // 364: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 367: dup
      // 368: aload 11
      // 36a: aload 6
      // 36c: iload 7
      // 36e: bipush 2
      // 370: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 373: astore 14
      // 375: new net/rim/device/api/crypto/RC2Key
      // 378: dup
      // 379: aload 13
      // 37b: bipush 5
      // 37d: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 380: invokespecial net/rim/device/api/crypto/RC2Key.<init> ([B)V
      // 383: astore 17
      // 385: new net/rim/device/api/crypto/InitializationVector
      // 388: dup
      // 389: aload 14
      // 38b: bipush 8
      // 38d: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 390: invokespecial net/rim/device/api/crypto/InitializationVector.<init> ([B)V
      // 393: astore 19
      // 395: new net/rim/device/api/crypto/CBCDecryptorEngine
      // 398: dup
      // 399: new net/rim/device/api/crypto/RC2DecryptorEngine
      // 39c: dup
      // 39d: aload 17
      // 39f: checkcast net/rim/device/api/crypto/RC2Key
      // 3a2: invokespecial net/rim/device/api/crypto/RC2DecryptorEngine.<init> (Lnet/rim/device/api/crypto/RC2Key;)V
      // 3a5: aload 19
      // 3a7: invokespecial net/rim/device/api/crypto/CBCDecryptorEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;Lnet/rim/device/api/crypto/InitializationVector;)V
      // 3aa: astore 18
      // 3ac: goto 3b0
      // 3af: return
      // 3b0: new net/rim/device/api/crypto/BlockDecryptor
      // 3b3: dup
      // 3b4: new net/rim/device/api/crypto/PKCS5UnformatterEngine
      // 3b7: dup
      // 3b8: aload 18
      // 3ba: invokespecial net/rim/device/api/crypto/PKCS5UnformatterEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;)V
      // 3bd: new java/io/ByteArrayInputStream
      // 3c0: dup
      // 3c1: aload 8
      // 3c3: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 3c6: invokespecial net/rim/device/api/crypto/BlockDecryptor.<init> (Lnet/rim/device/api/crypto/BlockUnformatterEngine;Ljava/io/InputStream;)V
      // 3c9: astore 20
      // 3cb: aload 20
      // 3cd: aload 9
      // 3cf: invokevirtual net/rim/device/api/crypto/CryptoInputStream.read ([B)I
      // 3d2: pop
      // 3d3: new net/rim/device/api/crypto/asn1/ASN1InputByteArray
      // 3d6: dup
      // 3d7: aload 9
      // 3d9: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 3dc: astore 20
      // 3de: aload 20
      // 3e0: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 3e3: istore 3
      // 3e4: iload 3
      // 3e5: bipush 16
      // 3e7: if_icmpeq 3ed
      // 3ea: goto 073
      // 3ed: aload 4
      // 3ef: ldc_w 541859388
      // 3f2: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 3f5: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 3f8: ifeq 41f
      // 3fb: aload 0
      // 3fc: new net/rim/device/api/crypto/PKCS12Data
      // 3ff: dup
      // 400: aload 20
      // 402: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 405: aload 0
      // 406: invokespecial net/rim/device/api/crypto/PKCS12Data.<init> ([BLnet/rim/device/api/crypto/PKCS12ContentInfo;)V
      // 409: putfield net/rim/device/api/crypto/PKCS12EncryptedData._decryptedData Lnet/rim/device/api/crypto/PKCS12Data;
      // 40c: aload 0
      // 40d: bipush 1
      // 40e: putfield net/rim/device/api/crypto/PKCS12ContentInfo._parsed Z
      // 411: aload 11
      // 413: ifnull 436
      // 416: aload 0
      // 417: aload 11
      // 419: invokevirtual net/rim/device/api/crypto/PKCS12EncryptedData.addPassword ([B)V
      // 41c: goto 436
      // 41f: new net/rim/device/api/crypto/PKCS12ParsingException
      // 422: dup
      // 423: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> ()V
      // 426: athrow
      // 427: astore 17
      // 429: goto 073
      // 42c: astore 17
      // 42e: goto 073
      // 431: astore 17
      // 433: goto 073
      // 436: iload 16
      // 438: bipush 7
      // 43a: if_icmpne 448
      // 43d: new net/rim/device/api/crypto/PKCS12ParsingException
      // 440: dup
      // 441: ldc_w "Wrong Key: Unable to decrypt the data"
      // 444: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> (Ljava/lang/String;)V
      // 447: athrow
      // 448: iload 3
      // 449: bipush 16
      // 44b: if_icmpeq 458
      // 44e: return
      // 44f: astore 2
      // 450: new net/rim/device/api/crypto/PKCS12ParsingException
      // 453: dup
      // 454: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> ()V
      // 457: athrow
      // 458: return
      // try (165 -> 431): 486 null
      // try (432 -> 481): 486 null
      // try (482 -> 486): 486 null
      // try (165 -> 431): 488 null
      // try (432 -> 481): 488 null
      // try (482 -> 486): 488 null
      // try (165 -> 431): 490 null
      // try (432 -> 481): 490 null
      // try (482 -> 486): 490 null
      // try (10 -> 431): 504 null
      // try (432 -> 503): 504 null
   }

   @Override
   public PKCS12ContentInfo[] getChildrenContentInfos() {
      this.parse();
      return this._decryptedData != null ? this._decryptedData.getChildrenContentInfos() : null;
   }
}
