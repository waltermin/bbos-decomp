package net.rim.device.api.crypto;

import net.rim.device.api.i18n.ResourceBundle;

public class PKCS8ShroudedKeyBag extends KeyBag {
   private static final ResourceBundle _rb = ResourceBundle.getBundle(60462186577914032L, "net.rim.device.internal.resource.crypto.KeyStore");

   public PKCS8ShroudedKeyBag(byte[] data, byte[] attributes, PKCS12ContentInfo parent) {
      super(data, attributes, parent);
   }

   @Override
   protected void parse() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/crypto/SafeBag._parsed Z
      // 004: ifeq 008
      // 007: return
      // 008: new java/lang/Object
      // 00b: dup
      // 00c: aload 0
      // 00d: getfield net/rim/device/api/crypto/SafeBag._bagData [B
      // 010: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 013: astore 1
      // 014: aload 1
      // 015: bipush 1
      // 016: bipush 0
      // 017: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence (II)V
      // 01a: aload 1
      // 01b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 01e: aload 1
      // 01f: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 022: astore 2
      // 023: aload 1
      // 024: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 027: aload 1
      // 028: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString ()[B
      // 02b: astore 3
      // 02c: aload 1
      // 02d: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readInteger ()I
      // 030: istore 4
      // 032: aload 1
      // 033: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOctetString ()[B
      // 036: astore 5
      // 038: aload 5
      // 03a: arraylength
      // 03b: newarray 8
      // 03d: astore 6
      // 03f: aconst_null
      // 040: astore 9
      // 042: aconst_null
      // 043: astore 10
      // 045: aconst_null
      // 046: astore 11
      // 048: aload 0
      // 049: invokevirtual net/rim/device/api/crypto/PKCS8ShroudedKeyBag.getPasswords ()Ljava/util/Enumeration;
      // 04c: astore 12
      // 04e: bipush 0
      // 04f: istore 13
      // 051: iload 13
      // 053: bipush 7
      // 055: if_icmplt 05b
      // 058: goto 443
      // 05b: iload 13
      // 05d: ifne 069
      // 060: aconst_null
      // 061: astore 10
      // 063: iinc 13 1
      // 066: goto 135
      // 069: iload 13
      // 06b: bipush 1
      // 06c: if_icmpne 098
      // 06f: aload 12
      // 071: ifnull 08a
      // 074: aload 12
      // 076: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 07b: ifeq 08a
      // 07e: aload 12
      // 080: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 085: checkcast [B
      // 088: astore 10
      // 08a: aload 10
      // 08c: ifnull 092
      // 08f: goto 135
      // 092: iinc 13 1
      // 095: goto 051
      // 098: iload 13
      // 09a: bipush 2
      // 09c: if_icmpne 0ba
      // 09f: new java/lang/Object
      // 0a2: dup
      // 0a3: getstatic net/rim/device/api/crypto/PKCS8ShroudedKeyBag._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0a6: sipush 6046
      // 0a9: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0ac: bipush 0
      // 0ad: bipush 50
      // 0af: ldc_w 134217728
      // 0b2: invokespecial net/rim/device/internal/ui/component/PasswordDialog.<init> (Ljava/lang/String;ZII)V
      // 0b5: astore 9
      // 0b7: goto 0d2
      // 0ba: new java/lang/Object
      // 0bd: dup
      // 0be: getstatic net/rim/device/api/crypto/PKCS8ShroudedKeyBag._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0c1: sipush 6047
      // 0c4: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0c7: bipush 0
      // 0c8: bipush 50
      // 0ca: ldc_w 134217728
      // 0cd: invokespecial net/rim/device/internal/ui/component/PasswordDialog.<init> (Ljava/lang/String;ZII)V
      // 0d0: astore 9
      // 0d2: aload 9
      // 0d4: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.show (Lnet/rim/device/internal/ui/component/PopupDialog;)V
      // 0d7: aload 9
      // 0d9: invokevirtual net/rim/device/internal/ui/component/PasswordDialog.getPassword ()[B
      // 0dc: astore 11
      // 0de: aload 11
      // 0e0: ifnull 0f0
      // 0e3: aload 9
      // 0e5: invokevirtual net/rim/device/internal/ui/component/PopupDialog.getCloseReason ()I
      // 0e8: aload 9
      // 0ea: pop
      // 0eb: bipush -1
      // 0ed: if_icmpne 0fb
      // 0f0: new net/rim/device/api/crypto/PKCS12ParsingException
      // 0f3: dup
      // 0f4: ldc_w "No Key: Unable to decrypt the keybag"
      // 0f7: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> (Ljava/lang/String;)V
      // 0fa: athrow
      // 0fb: aload 11
      // 0fd: arraylength
      // 0fe: bipush 2
      // 100: imul
      // 101: bipush 2
      // 103: iadd
      // 104: newarray 8
      // 106: astore 10
      // 108: aload 10
      // 10a: bipush 0
      // 10b: bipush 0
      // 10c: aload 10
      // 10e: arraylength
      // 10f: invokestatic net/rim/device/api/util/Arrays.fill ([BBII)V
      // 112: bipush 0
      // 113: istore 14
      // 115: iload 14
      // 117: aload 11
      // 119: arraylength
      // 11a: if_icmpge 132
      // 11d: aload 10
      // 11f: iload 14
      // 121: bipush 2
      // 123: imul
      // 124: bipush 1
      // 125: iadd
      // 126: aload 11
      // 128: iload 14
      // 12a: baload
      // 12b: bastore
      // 12c: iinc 14 1
      // 12f: goto 115
      // 132: iinc 13 1
      // 135: aconst_null
      // 136: astore 15
      // 138: aconst_null
      // 139: astore 16
      // 13b: aload 2
      // 13c: ldc_w 314138129
      // 13f: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 142: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 145: ifeq 190
      // 148: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 14b: dup
      // 14c: aload 10
      // 14e: aload 3
      // 14f: iload 4
      // 151: bipush 1
      // 152: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 155: astore 7
      // 157: new java/lang/Object
      // 15a: dup
      // 15b: new java/lang/Object
      // 15e: dup
      // 15f: aload 7
      // 161: bipush 16
      // 163: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 166: invokespecial net/rim/device/api/crypto/ARC4Key.<init> ([B)V
      // 169: invokespecial net/rim/device/api/crypto/ARC4PseudoRandomSource.<init> (Lnet/rim/device/api/crypto/ARC4Key;)V
      // 16c: astore 17
      // 16e: new java/lang/Object
      // 171: dup
      // 172: aload 17
      // 174: new java/lang/Object
      // 177: dup
      // 178: aload 5
      // 17a: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 17d: invokespecial net/rim/device/api/crypto/PRNGDecryptor.<init> (Lnet/rim/device/api/crypto/PseudoRandomSource;Ljava/io/InputStream;)V
      // 180: astore 18
      // 182: aload 18
      // 184: aload 6
      // 186: bipush 0
      // 187: aload 6
      // 189: arraylength
      // 18a: invokevirtual net/rim/device/api/crypto/PRNGDecryptor.decrypt ([BII)V
      // 18d: goto 3a1
      // 190: aload 2
      // 191: ldc_w 314138130
      // 194: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 197: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 19a: ifeq 1e5
      // 19d: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 1a0: dup
      // 1a1: aload 10
      // 1a3: aload 3
      // 1a4: iload 4
      // 1a6: bipush 1
      // 1a7: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 1aa: astore 7
      // 1ac: new java/lang/Object
      // 1af: dup
      // 1b0: new java/lang/Object
      // 1b3: dup
      // 1b4: aload 7
      // 1b6: bipush 5
      // 1b8: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 1bb: invokespecial net/rim/device/api/crypto/ARC4Key.<init> ([B)V
      // 1be: invokespecial net/rim/device/api/crypto/ARC4PseudoRandomSource.<init> (Lnet/rim/device/api/crypto/ARC4Key;)V
      // 1c1: astore 17
      // 1c3: new java/lang/Object
      // 1c6: dup
      // 1c7: aload 17
      // 1c9: new java/lang/Object
      // 1cc: dup
      // 1cd: aload 5
      // 1cf: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 1d2: invokespecial net/rim/device/api/crypto/PRNGDecryptor.<init> (Lnet/rim/device/api/crypto/PseudoRandomSource;Ljava/io/InputStream;)V
      // 1d5: astore 18
      // 1d7: aload 18
      // 1d9: aload 6
      // 1db: bipush 0
      // 1dc: aload 6
      // 1de: arraylength
      // 1df: invokevirtual net/rim/device/api/crypto/PRNGDecryptor.decrypt ([BII)V
      // 1e2: goto 3a1
      // 1e5: aload 2
      // 1e6: ldc_w 314138131
      // 1e9: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 1ec: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 1ef: ifeq 24b
      // 1f2: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 1f5: dup
      // 1f6: aload 10
      // 1f8: aload 3
      // 1f9: iload 4
      // 1fb: bipush 1
      // 1fc: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 1ff: astore 7
      // 201: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 204: dup
      // 205: aload 10
      // 207: aload 3
      // 208: iload 4
      // 20a: bipush 2
      // 20c: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 20f: astore 8
      // 211: new java/lang/Object
      // 214: dup
      // 215: aload 7
      // 217: bipush 24
      // 219: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 21c: invokespecial net/rim/device/api/crypto/TripleDESKey.<init> ([B)V
      // 21f: astore 14
      // 221: new java/lang/Object
      // 224: dup
      // 225: aload 8
      // 227: bipush 8
      // 229: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 22c: invokespecial net/rim/device/api/crypto/InitializationVector.<init> ([B)V
      // 22f: astore 16
      // 231: new java/lang/Object
      // 234: dup
      // 235: new java/lang/Object
      // 238: dup
      // 239: aload 14
      // 23b: checkcast java/lang/Object
      // 23e: invokespecial net/rim/device/api/crypto/TripleDESDecryptorEngine.<init> (Lnet/rim/device/api/crypto/TripleDESKey;)V
      // 241: aload 16
      // 243: invokespecial net/rim/device/api/crypto/CBCDecryptorEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;Lnet/rim/device/api/crypto/InitializationVector;)V
      // 246: astore 15
      // 248: goto 37e
      // 24b: aload 2
      // 24c: ldc_w 314138132
      // 24f: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 252: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 255: ifeq 2b1
      // 258: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 25b: dup
      // 25c: aload 10
      // 25e: aload 3
      // 25f: iload 4
      // 261: bipush 1
      // 262: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 265: astore 7
      // 267: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 26a: dup
      // 26b: aload 10
      // 26d: aload 3
      // 26e: iload 4
      // 270: bipush 2
      // 272: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 275: astore 8
      // 277: new java/lang/Object
      // 27a: dup
      // 27b: aload 7
      // 27d: bipush 16
      // 27f: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 282: invokespecial net/rim/device/api/crypto/TripleDESKey.<init> ([B)V
      // 285: astore 14
      // 287: new java/lang/Object
      // 28a: dup
      // 28b: aload 8
      // 28d: bipush 8
      // 28f: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 292: invokespecial net/rim/device/api/crypto/InitializationVector.<init> ([B)V
      // 295: astore 16
      // 297: new java/lang/Object
      // 29a: dup
      // 29b: new java/lang/Object
      // 29e: dup
      // 29f: aload 14
      // 2a1: checkcast java/lang/Object
      // 2a4: invokespecial net/rim/device/api/crypto/TripleDESDecryptorEngine.<init> (Lnet/rim/device/api/crypto/TripleDESKey;)V
      // 2a7: aload 16
      // 2a9: invokespecial net/rim/device/api/crypto/CBCDecryptorEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;Lnet/rim/device/api/crypto/InitializationVector;)V
      // 2ac: astore 15
      // 2ae: goto 37e
      // 2b1: aload 2
      // 2b2: ldc_w 314138133
      // 2b5: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 2b8: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 2bb: ifeq 317
      // 2be: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 2c1: dup
      // 2c2: aload 10
      // 2c4: aload 3
      // 2c5: iload 4
      // 2c7: bipush 1
      // 2c8: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 2cb: astore 7
      // 2cd: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 2d0: dup
      // 2d1: aload 10
      // 2d3: aload 3
      // 2d4: iload 4
      // 2d6: bipush 2
      // 2d8: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 2db: astore 8
      // 2dd: new net/rim/device/api/crypto/RC2Key
      // 2e0: dup
      // 2e1: aload 7
      // 2e3: bipush 16
      // 2e5: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 2e8: invokespecial net/rim/device/api/crypto/RC2Key.<init> ([B)V
      // 2eb: astore 14
      // 2ed: new java/lang/Object
      // 2f0: dup
      // 2f1: aload 8
      // 2f3: bipush 8
      // 2f5: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 2f8: invokespecial net/rim/device/api/crypto/InitializationVector.<init> ([B)V
      // 2fb: astore 16
      // 2fd: new java/lang/Object
      // 300: dup
      // 301: new net/rim/device/api/crypto/RC2DecryptorEngine
      // 304: dup
      // 305: aload 14
      // 307: checkcast net/rim/device/api/crypto/RC2Key
      // 30a: invokespecial net/rim/device/api/crypto/RC2DecryptorEngine.<init> (Lnet/rim/device/api/crypto/RC2Key;)V
      // 30d: aload 16
      // 30f: invokespecial net/rim/device/api/crypto/CBCDecryptorEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;Lnet/rim/device/api/crypto/InitializationVector;)V
      // 312: astore 15
      // 314: goto 37e
      // 317: aload 2
      // 318: ldc_w 314138134
      // 31b: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 31e: invokevirtual net/rim/device/api/crypto/oid/OID.equals (Ljava/lang/Object;)Z
      // 321: ifeq 37d
      // 324: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 327: dup
      // 328: aload 10
      // 32a: aload 3
      // 32b: iload 4
      // 32d: bipush 1
      // 32e: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 331: astore 7
      // 333: new net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource
      // 336: dup
      // 337: aload 10
      // 339: aload 3
      // 33a: iload 4
      // 33c: bipush 2
      // 33e: invokespecial net/rim/device/api/crypto/PKCS12KDFPseudoRandomSource.<init> ([B[BIB)V
      // 341: astore 8
      // 343: new net/rim/device/api/crypto/RC2Key
      // 346: dup
      // 347: aload 7
      // 349: bipush 5
      // 34b: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 34e: invokespecial net/rim/device/api/crypto/RC2Key.<init> ([B)V
      // 351: astore 14
      // 353: new java/lang/Object
      // 356: dup
      // 357: aload 8
      // 359: bipush 8
      // 35b: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 35e: invokespecial net/rim/device/api/crypto/InitializationVector.<init> ([B)V
      // 361: astore 16
      // 363: new java/lang/Object
      // 366: dup
      // 367: new net/rim/device/api/crypto/RC2DecryptorEngine
      // 36a: dup
      // 36b: aload 14
      // 36d: checkcast net/rim/device/api/crypto/RC2Key
      // 370: invokespecial net/rim/device/api/crypto/RC2DecryptorEngine.<init> (Lnet/rim/device/api/crypto/RC2Key;)V
      // 373: aload 16
      // 375: invokespecial net/rim/device/api/crypto/CBCDecryptorEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;Lnet/rim/device/api/crypto/InitializationVector;)V
      // 378: astore 15
      // 37a: goto 37e
      // 37d: return
      // 37e: new java/lang/Object
      // 381: dup
      // 382: new java/lang/Object
      // 385: dup
      // 386: aload 15
      // 388: invokespecial net/rim/device/api/crypto/PKCS5UnformatterEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;)V
      // 38b: new java/lang/Object
      // 38e: dup
      // 38f: aload 5
      // 391: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 394: invokespecial net/rim/device/api/crypto/BlockDecryptor.<init> (Lnet/rim/device/api/crypto/BlockUnformatterEngine;Ljava/io/InputStream;)V
      // 397: astore 17
      // 399: aload 17
      // 39b: aload 6
      // 39d: invokevirtual net/rim/device/api/crypto/CryptoInputStream.read ([B)I
      // 3a0: pop
      // 3a1: aload 0
      // 3a2: aload 6
      // 3a4: ldc_w "PKCS8"
      // 3a7: invokestatic net/rim/device/api/crypto/encoder/PrivateKeyDecoder.decode ([BLjava/lang/String;)Lnet/rim/device/api/crypto/PrivateKey;
      // 3aa: putfield net/rim/device/api/crypto/KeyBag._key Lnet/rim/device/api/crypto/PrivateKey;
      // 3ad: aload 10
      // 3af: ifnull 3b8
      // 3b2: aload 0
      // 3b3: aload 10
      // 3b5: invokevirtual net/rim/device/api/crypto/PKCS8ShroudedKeyBag.addPassword ([B)V
      // 3b8: aload 0
      // 3b9: getfield net/rim/device/api/crypto/SafeBag._bagAttributes [B
      // 3bc: ifnull 413
      // 3bf: new java/lang/Object
      // 3c2: dup
      // 3c3: aload 0
      // 3c4: getfield net/rim/device/api/crypto/SafeBag._bagAttributes [B
      // 3c7: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 3ca: astore 17
      // 3cc: aload 17
      // 3ce: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 3d1: istore 18
      // 3d3: iload 18
      // 3d5: bipush -1
      // 3d7: if_icmpeq 413
      // 3da: iload 18
      // 3dc: bipush 16
      // 3de: if_icmpne 401
      // 3e1: aload 17
      // 3e3: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 3e6: aload 0
      // 3e7: getfield net/rim/device/api/crypto/SafeBag._attributes Ljava/util/Vector;
      // 3ea: new net/rim/device/api/crypto/PKCS12Attribute
      // 3ed: dup
      // 3ee: aload 17
      // 3f0: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readOID ()Lnet/rim/device/api/crypto/oid/OID;
      // 3f3: aload 17
      // 3f5: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 3f8: invokespecial net/rim/device/api/crypto/PKCS12Attribute.<init> (Lnet/rim/device/api/crypto/oid/OID;[B)V
      // 3fb: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 3fe: goto 409
      // 401: new net/rim/device/api/crypto/PKCS12ParsingException
      // 404: dup
      // 405: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> ()V
      // 408: athrow
      // 409: aload 17
      // 40b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 40e: istore 18
      // 410: goto 3d3
      // 413: aload 0
      // 414: bipush 1
      // 415: putfield net/rim/device/api/crypto/SafeBag._parsed Z
      // 418: goto 443
      // 41b: astore 14
      // 41d: goto 051
      // 420: astore 14
      // 422: goto 051
      // 425: astore 14
      // 427: goto 051
      // 42a: astore 14
      // 42c: goto 051
      // 42f: astore 14
      // 431: goto 051
      // 434: astore 14
      // 436: goto 051
      // 439: astore 14
      // 43b: goto 051
      // 43e: astore 14
      // 440: goto 051
      // 443: iload 13
      // 445: bipush 7
      // 447: if_icmpne 45e
      // 44a: new net/rim/device/api/crypto/PKCS12ParsingException
      // 44d: dup
      // 44e: ldc_w "Wrong Key: Unable to decrypt the keybag"
      // 451: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> (Ljava/lang/String;)V
      // 454: athrow
      // 455: astore 1
      // 456: new net/rim/device/api/crypto/PKCS12ParsingException
      // 459: dup
      // 45a: invokespecial net/rim/device/api/crypto/PKCS12ParsingException.<init> ()V
      // 45d: athrow
      // 45e: return
      // try (147 -> 413): 483 null
      // try (414 -> 482): 483 null
      // try (147 -> 413): 485 null
      // try (414 -> 482): 485 null
      // try (147 -> 413): 487 null
      // try (414 -> 482): 487 null
      // try (147 -> 413): 489 null
      // try (414 -> 482): 489 null
      // try (147 -> 413): 491 null
      // try (414 -> 482): 491 null
      // try (147 -> 413): 493 null
      // try (414 -> 482): 493 null
      // try (147 -> 413): 495 null
      // try (414 -> 482): 495 null
      // try (147 -> 413): 497 null
      // try (414 -> 482): 497 null
      // try (4 -> 413): 507 null
      // try (414 -> 507): 507 null
   }
}
