package net.rim.device.api.crypto;

import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

class CryptoSmartCardUtilities$DoImportWork extends PleaseWaitWorkerThread {
   KeyStore _importKeyStore;
   KeyStore _trustedKeyStore;
   private boolean _certificatesAdded;

   private CryptoSmartCardUtilities$DoImportWork(KeyStore importKeyStore, KeyStore trustedKeyStore) {
      this._importKeyStore = importKeyStore;
      this._importKeyStore.addIndex((KeyStoreIndex)(new Object()));
      this._trustedKeyStore = trustedKeyStore;
   }

   public boolean wereCertificatesAdded() {
      return this._certificatesAdded;
   }

   @Override
   protected void doWork() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 1
      // 002: aconst_null
      // 003: astore 2
      // 004: aconst_null
      // 005: astore 3
      // 006: aload 0
      // 007: bipush 0
      // 008: putfield net/rim/device/api/crypto/CryptoSmartCardUtilities$DoImportWork._certificatesAdded Z
      // 00b: aconst_null
      // 00c: invokestatic net/rim/device/api/smartcard/SmartCardReaderFactory.getReaderSession (Lnet/rim/device/api/smartcard/SmartCard;)Lnet/rim/device/api/smartcard/SmartCardReaderSession;
      // 00f: astore 2
      // 010: aload 2
      // 011: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.getSmartCard ()Lnet/rim/device/api/smartcard/SmartCard;
      // 014: astore 4
      // 016: aload 4
      // 018: ifnonnull 052
      // 01b: aload 2
      // 01c: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 01f: invokestatic net/rim/device/api/smartcard/SmartCardFactory.getSmartCard ()Lnet/rim/device/api/smartcard/SmartCard;
      // 022: astore 4
      // 024: aconst_null
      // 025: invokestatic net/rim/device/api/smartcard/SmartCardReaderFactory.getReaderSession (Lnet/rim/device/api/smartcard/SmartCard;)Lnet/rim/device/api/smartcard/SmartCardReaderSession;
      // 028: astore 2
      // 029: aload 4
      // 02b: ifnonnull 052
      // 02e: getstatic net/rim/device/api/crypto/CryptoSmartCardUtilities._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 031: bipush 15
      // 033: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 036: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 039: aload 1
      // 03a: ifnull 041
      // 03d: aload 1
      // 03e: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 041: aload 2
      // 042: ifnull 049
      // 045: aload 2
      // 046: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 049: aload 3
      // 04a: ifnull 051
      // 04d: aload 3
      // 04e: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 051: return
      // 052: aload 4
      // 054: aload 2
      // 055: invokevirtual net/rim/device/api/smartcard/SmartCard.openSession (Lnet/rim/device/api/smartcard/SmartCardReaderSession;)Lnet/rim/device/api/smartcard/SmartCardSession;
      // 058: astore 1
      // 059: aload 1
      // 05a: dup
      // 05b: instanceof net/rim/device/api/crypto/CryptoSmartCardSession
      // 05e: ifne 065
      // 061: pop
      // 062: goto 33d
      // 065: checkcast net/rim/device/api/crypto/CryptoSmartCardSession
      // 068: astore 5
      // 06a: aload 5
      // 06c: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.getKeyStoreDataArray ()[Lnet/rim/device/api/crypto/CryptoSmartCardKeyStoreData;
      // 06f: astore 6
      // 071: aload 5
      // 073: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 076: aload 6
      // 078: ifnull 081
      // 07b: aload 6
      // 07d: arraylength
      // 07e: ifne 0a5
      // 081: getstatic net/rim/device/api/crypto/CryptoSmartCardUtilities._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 084: bipush 11
      // 086: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 089: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 08c: aload 1
      // 08d: ifnull 094
      // 090: aload 1
      // 091: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 094: aload 2
      // 095: ifnull 09c
      // 098: aload 2
      // 099: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 09c: aload 3
      // 09d: ifnull 0a4
      // 0a0: aload 3
      // 0a1: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 0a4: return
      // 0a5: aload 6
      // 0a7: arraylength
      // 0a8: istore 7
      // 0aa: iload 7
      // 0ac: anewarray 204
      // 0af: astore 8
      // 0b1: iload 7
      // 0b3: anewarray 206
      // 0b6: astore 9
      // 0b8: iload 7
      // 0ba: newarray 10
      // 0bc: astore 10
      // 0be: iload 7
      // 0c0: anewarray 208
      // 0c3: astore 11
      // 0c5: iload 7
      // 0c7: anewarray 210
      // 0ca: astore 12
      // 0cc: bipush 0
      // 0cd: istore 13
      // 0cf: bipush 0
      // 0d0: istore 14
      // 0d2: iload 14
      // 0d4: iload 7
      // 0d6: if_icmplt 0dc
      // 0d9: goto 193
      // 0dc: aload 6
      // 0de: iload 14
      // 0e0: aaload
      // 0e1: astore 15
      // 0e3: aload 15
      // 0e5: ifnonnull 0eb
      // 0e8: goto 18d
      // 0eb: aload 15
      // 0ed: invokevirtual net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate;
      // 0f0: astore 16
      // 0f2: aload 16
      // 0f4: ifnonnull 0fa
      // 0f7: goto 18d
      // 0fa: aload 0
      // 0fb: getfield net/rim/device/api/crypto/CryptoSmartCardUtilities$DoImportWork._importKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 0fe: ldc2_w -2038609988711824737
      // 101: aload 16
      // 103: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (JLjava/lang/Object;)Ljava/util/Enumeration; 4
      // 108: astore 17
      // 10a: aload 17
      // 10c: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 111: istore 18
      // 113: aconst_null
      // 114: astore 19
      // 116: iload 18
      // 118: ifeq 15f
      // 11b: aload 15
      // 11d: invokevirtual net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.isPrivateKeySet ()Z
      // 120: ifeq 15f
      // 123: bipush 0
      // 124: istore 18
      // 126: new java/lang/Object
      // 129: dup
      // 12a: invokespecial java/util/Vector.<init> ()V
      // 12d: astore 19
      // 12f: aload 17
      // 131: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 136: ifeq 15f
      // 139: aload 17
      // 13b: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 140: checkcast java/lang/Object
      // 143: astore 20
      // 145: aload 20
      // 147: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isPrivateKeySet ()Z 1
      // 14c: ifeq 155
      // 14f: bipush 1
      // 150: istore 18
      // 152: goto 15f
      // 155: aload 19
      // 157: aload 20
      // 159: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 15c: goto 12f
      // 15f: iload 18
      // 161: ifne 18d
      // 164: aload 11
      // 166: iload 13
      // 168: aload 15
      // 16a: aastore
      // 16b: aload 8
      // 16d: iload 13
      // 16f: aload 15
      // 171: invokevirtual net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.getLabel ()Ljava/lang/String;
      // 174: aastore
      // 175: aload 10
      // 177: iload 13
      // 179: iload 13
      // 17b: iastore
      // 17c: aload 9
      // 17e: iload 13
      // 180: aload 16
      // 182: aastore
      // 183: aload 12
      // 185: iload 13
      // 187: aload 19
      // 189: aastore
      // 18a: iinc 13 1
      // 18d: iinc 14 1
      // 190: goto 0d2
      // 193: iload 13
      // 195: ifne 1bc
      // 198: getstatic net/rim/device/api/crypto/CryptoSmartCardUtilities._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 19b: bipush 12
      // 19d: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 1a0: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 1a3: aload 1
      // 1a4: ifnull 1ab
      // 1a7: aload 1
      // 1a8: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 1ab: aload 2
      // 1ac: ifnull 1b3
      // 1af: aload 2
      // 1b0: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 1b3: aload 3
      // 1b4: ifnull 1bb
      // 1b7: aload 3
      // 1b8: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 1bb: return
      // 1bc: aload 8
      // 1be: iload 13
      // 1c0: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 1c3: aload 9
      // 1c5: iload 13
      // 1c7: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 1ca: aload 11
      // 1cc: iload 13
      // 1ce: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 1d1: aload 10
      // 1d3: iload 13
      // 1d5: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 1d8: new java/lang/Object
      // 1db: dup
      // 1dc: getstatic net/rim/device/api/crypto/CryptoSmartCardUtilities._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 1df: bipush 10
      // 1e1: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 1e4: invokespecial net/rim/device/api/ui/component/RichTextField.<init> (Ljava/lang/String;)V
      // 1e7: aload 8
      // 1e9: aload 9
      // 1eb: aload 10
      // 1ed: aload 0
      // 1ee: getfield net/rim/device/api/crypto/CryptoSmartCardUtilities$DoImportWork._importKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 1f1: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.selectCertificates (Lnet/rim/device/api/ui/component/RichTextField;[Ljava/lang/String;[Lnet/rim/device/api/crypto/certificate/Certificate;[ILnet/rim/device/api/crypto/keystore/KeyStore;)[I
      // 1f4: astore 14
      // 1f6: aload 14
      // 1f8: ifnonnull 214
      // 1fb: aload 1
      // 1fc: ifnull 203
      // 1ff: aload 1
      // 200: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 203: aload 2
      // 204: ifnull 20b
      // 207: aload 2
      // 208: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 20b: aload 3
      // 20c: ifnull 213
      // 20f: aload 3
      // 210: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 213: return
      // 214: aconst_null
      // 215: astore 15
      // 217: aconst_null
      // 218: astore 16
      // 21a: bipush 0
      // 21b: istore 17
      // 21d: iload 17
      // 21f: aload 14
      // 221: arraylength
      // 222: if_icmplt 228
      // 225: goto 33d
      // 228: aload 11
      // 22a: aload 14
      // 22c: iload 17
      // 22e: iaload
      // 22f: aaload
      // 230: astore 18
      // 232: aload 18
      // 234: invokevirtual net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate;
      // 237: astore 19
      // 239: aload 16
      // 23b: ifnonnull 249
      // 23e: aload 0
      // 23f: getfield net/rim/device/api/crypto/CryptoSmartCardUtilities$DoImportWork._importKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 242: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.getTicket ()Lnet/rim/device/api/crypto/keystore/KeyStoreTicket; 1
      // 247: astore 16
      // 249: aload 12
      // 24b: iload 17
      // 24d: aaload
      // 24e: astore 20
      // 250: aload 20
      // 252: ifnull 27e
      // 255: aload 20
      // 257: invokevirtual java/util/Vector.size ()I
      // 25a: bipush 1
      // 25b: isub
      // 25c: istore 21
      // 25e: iload 21
      // 260: iflt 27e
      // 263: aload 0
      // 264: getfield net/rim/device/api/crypto/CryptoSmartCardUtilities$DoImportWork._importKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 267: aload 20
      // 269: iload 21
      // 26b: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 26e: checkcast java/lang/Object
      // 271: aload 16
      // 273: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.removeKey (Lnet/rim/device/api/crypto/keystore/KeyStoreData;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)V 3
      // 278: iinc 21 -1
      // 27b: goto 25e
      // 27e: aload 0
      // 27f: bipush 1
      // 280: putfield net/rim/device/api/crypto/CryptoSmartCardUtilities$DoImportWork._certificatesAdded Z
      // 283: aload 0
      // 284: getfield net/rim/device/api/crypto/CryptoSmartCardUtilities$DoImportWork._importKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 287: aload 19
      // 289: invokestatic net/rim/device/api/crypto/CryptoSmartCardUtilities.getSmartCardAssociatedData (Lnet/rim/device/api/crypto/certificate/Certificate;)[Lnet/rim/device/api/crypto/keystore/AssociatedData;
      // 28c: aload 18
      // 28e: invokevirtual net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.getLabel ()Ljava/lang/String;
      // 291: aload 18
      // 293: aconst_null
      // 294: invokevirtual net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey;
      // 297: aconst_null
      // 298: bipush 1
      // 299: aload 19
      // 29b: aconst_null
      // 29c: aload 16
      // 29e: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.set ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/PrivateKey;Ljava/lang/String;ILnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData; 9
      // 2a3: pop
      // 2a4: aload 19
      // 2a6: ldc2_w -1188891808812199856
      // 2a9: new java/lang/Object
      // 2ac: dup
      // 2ad: aload 18
      // 2af: invokevirtual net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.isPrivateKeySet ()Z
      // 2b2: invokespecial java/lang/Boolean.<init> (Z)V
      // 2b5: getstatic java/lang/Boolean.FALSE Ljava/lang/Boolean;
      // 2b8: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getInformation (JLjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 5
      // 2bd: checkcast java/lang/Object
      // 2c0: astore 21
      // 2c2: aload 21
      // 2c4: invokevirtual java/lang/Boolean.booleanValue ()Z
      // 2c7: ifeq 337
      // 2ca: aload 0
      // 2cb: getfield net/rim/device/api/crypto/CryptoSmartCardUtilities$DoImportWork._trustedKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 2ce: aload 19
      // 2d0: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.isMember (Lnet/rim/device/api/crypto/certificate/Certificate;)Z 2
      // 2d5: ifne 337
      // 2d8: aload 0
      // 2d9: getfield net/rim/device/api/crypto/CryptoSmartCardUtilities$DoImportWork._trustedKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 2dc: dup
      // 2dd: instanceof java/lang/Object
      // 2e0: ifne 2e7
      // 2e3: pop
      // 2e4: goto 2f2
      // 2e7: checkcast java/lang/Object
      // 2ea: aload 19
      // 2ec: invokevirtual net/rim/device/api/crypto/keystore/TrustedKeyStore.isAllowed (Lnet/rim/device/api/crypto/certificate/Certificate;)Z
      // 2ef: ifeq 337
      // 2f2: sipush 6089
      // 2f5: invokestatic net/rim/device/api/crypto/keystore/KeyStoreResources.getString (I)Ljava/lang/String;
      // 2f8: aload 18
      // 2fa: invokevirtual net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.getLabel ()Ljava/lang/String;
      // 2fd: invokestatic net/rim/device/internal/ui/component/SimpleChoiceDialog.askYesNoQuestionOnBackground (Ljava/lang/String;Ljava/lang/String;)Z
      // 300: ifne 306
      // 303: goto 337
      // 306: aload 15
      // 308: ifnonnull 316
      // 30b: aload 0
      // 30c: getfield net/rim/device/api/crypto/CryptoSmartCardUtilities$DoImportWork._trustedKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 30f: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.getTicket ()Lnet/rim/device/api/crypto/keystore/KeyStoreTicket; 1
      // 314: astore 15
      // 316: aload 0
      // 317: bipush 1
      // 318: putfield net/rim/device/api/crypto/CryptoSmartCardUtilities$DoImportWork._certificatesAdded Z
      // 31b: aload 0
      // 31c: getfield net/rim/device/api/crypto/CryptoSmartCardUtilities$DoImportWork._trustedKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 31f: aload 19
      // 321: invokestatic net/rim/device/api/crypto/CryptoSmartCardUtilities.getSmartCardAssociatedData (Lnet/rim/device/api/crypto/certificate/Certificate;)[Lnet/rim/device/api/crypto/keystore/AssociatedData;
      // 324: aload 18
      // 326: invokevirtual net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.getLabel ()Ljava/lang/String;
      // 329: aconst_null
      // 32a: aconst_null
      // 32b: bipush 1
      // 32c: aload 19
      // 32e: aconst_null
      // 32f: aload 15
      // 331: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.set ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/PrivateKey;Ljava/lang/String;ILnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData; 9
      // 336: pop
      // 337: iinc 17 1
      // 33a: goto 21d
      // 33d: aload 1
      // 33e: ifnull 345
      // 341: aload 1
      // 342: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 345: aload 2
      // 346: ifnull 34d
      // 349: aload 2
      // 34a: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 34d: aload 3
      // 34e: ifnonnull 354
      // 351: goto 4a5
      // 354: aload 3
      // 355: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 358: return
      // 359: astore 4
      // 35b: getstatic net/rim/device/api/crypto/CryptoSmartCardUtilities._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 35e: bipush 13
      // 360: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 363: astore 3
      // 364: aload 1
      // 365: ifnull 36c
      // 368: aload 1
      // 369: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 36c: aload 2
      // 36d: ifnull 374
      // 370: aload 2
      // 371: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 374: aload 3
      // 375: ifnonnull 37b
      // 378: goto 4a5
      // 37b: aload 3
      // 37c: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 37f: return
      // 380: astore 4
      // 382: aload 1
      // 383: ifnull 38a
      // 386: aload 1
      // 387: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 38a: aload 2
      // 38b: ifnull 392
      // 38e: aload 2
      // 38f: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 392: aload 3
      // 393: ifnonnull 399
      // 396: goto 4a5
      // 399: aload 3
      // 39a: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 39d: return
      // 39e: astore 4
      // 3a0: getstatic net/rim/device/api/crypto/CryptoSmartCardUtilities._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 3a3: bipush 23
      // 3a5: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 3a8: astore 3
      // 3a9: aload 1
      // 3aa: ifnull 3b1
      // 3ad: aload 1
      // 3ae: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 3b1: aload 2
      // 3b2: ifnull 3b9
      // 3b5: aload 2
      // 3b6: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 3b9: aload 3
      // 3ba: ifnonnull 3c0
      // 3bd: goto 4a5
      // 3c0: aload 3
      // 3c1: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 3c4: return
      // 3c5: astore 4
      // 3c7: aload 1
      // 3c8: ifnull 3cf
      // 3cb: aload 1
      // 3cc: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 3cf: aload 2
      // 3d0: ifnull 3d7
      // 3d3: aload 2
      // 3d4: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 3d7: aload 3
      // 3d8: ifnonnull 3de
      // 3db: goto 4a5
      // 3de: aload 3
      // 3df: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 3e2: return
      // 3e3: astore 4
      // 3e5: aload 1
      // 3e6: ifnull 3ed
      // 3e9: aload 1
      // 3ea: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 3ed: aload 2
      // 3ee: ifnull 3f5
      // 3f1: aload 2
      // 3f2: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 3f5: aload 3
      // 3f6: ifnonnull 3fc
      // 3f9: goto 4a5
      // 3fc: aload 3
      // 3fd: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 400: return
      // 401: astore 4
      // 403: aload 4
      // 405: invokevirtual net/rim/device/api/crypto/CryptoException.toString ()Ljava/lang/String;
      // 408: astore 3
      // 409: aload 1
      // 40a: ifnull 411
      // 40d: aload 1
      // 40e: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 411: aload 2
      // 412: ifnull 419
      // 415: aload 2
      // 416: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 419: aload 3
      // 41a: ifnonnull 420
      // 41d: goto 4a5
      // 420: aload 3
      // 421: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 424: return
      // 425: astore 4
      // 427: aload 1
      // 428: ifnull 42f
      // 42b: aload 1
      // 42c: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 42f: aload 2
      // 430: ifnull 437
      // 433: aload 2
      // 434: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 437: aload 3
      // 438: ifnull 4a5
      // 43b: aload 3
      // 43c: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 43f: return
      // 440: astore 4
      // 442: getstatic net/rim/device/api/crypto/CryptoSmartCardUtilities._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 445: bipush 14
      // 447: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 44a: astore 3
      // 44b: aload 1
      // 44c: ifnull 453
      // 44f: aload 1
      // 450: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 453: aload 2
      // 454: ifnull 45b
      // 457: aload 2
      // 458: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 45b: aload 3
      // 45c: ifnull 4a5
      // 45f: aload 3
      // 460: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 463: return
      // 464: astore 4
      // 466: getstatic net/rim/device/api/crypto/CryptoSmartCardUtilities._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 469: bipush 21
      // 46b: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 46e: astore 3
      // 46f: aload 1
      // 470: ifnull 477
      // 473: aload 1
      // 474: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 477: aload 2
      // 478: ifnull 47f
      // 47b: aload 2
      // 47c: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 47f: aload 3
      // 480: ifnull 4a5
      // 483: aload 3
      // 484: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 487: return
      // 488: astore 22
      // 48a: aload 1
      // 48b: ifnull 492
      // 48e: aload 1
      // 48f: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 492: aload 2
      // 493: ifnull 49a
      // 496: aload 2
      // 497: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.close ()V
      // 49a: aload 3
      // 49b: ifnull 4a2
      // 49e: aload 3
      // 49f: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // 4a2: aload 22
      // 4a4: athrow
      // 4a5: return
      // try (9 -> 30): 389 null
      // try (43 -> 69): 389 null
      // try (82 -> 192): 389 null
      // try (205 -> 232): 389 null
      // try (245 -> 375): 389 null
      // try (9 -> 30): 408 null
      // try (43 -> 69): 408 null
      // try (82 -> 192): 408 null
      // try (205 -> 232): 408 null
      // try (245 -> 375): 408 null
      // try (9 -> 30): 423 null
      // try (43 -> 69): 423 null
      // try (82 -> 192): 423 null
      // try (205 -> 232): 423 null
      // try (245 -> 375): 423 null
      // try (9 -> 30): 442 null
      // try (43 -> 69): 442 null
      // try (82 -> 192): 442 null
      // try (205 -> 232): 442 null
      // try (245 -> 375): 442 null
      // try (9 -> 30): 457 null
      // try (43 -> 69): 457 null
      // try (82 -> 192): 457 null
      // try (205 -> 232): 457 null
      // try (245 -> 375): 457 null
      // try (9 -> 30): 472 null
      // try (43 -> 69): 472 null
      // try (82 -> 192): 472 null
      // try (205 -> 232): 472 null
      // try (245 -> 375): 472 null
      // try (9 -> 30): 490 null
      // try (43 -> 69): 490 null
      // try (82 -> 192): 490 null
      // try (205 -> 232): 490 null
      // try (245 -> 375): 490 null
      // try (9 -> 30): 504 null
      // try (43 -> 69): 504 null
      // try (82 -> 192): 504 null
      // try (205 -> 232): 504 null
      // try (245 -> 375): 504 null
      // try (9 -> 30): 522 null
      // try (43 -> 69): 522 null
      // try (82 -> 192): 522 null
      // try (205 -> 232): 522 null
      // try (245 -> 375): 522 null
      // try (9 -> 30): 540 null
      // try (43 -> 69): 540 null
      // try (82 -> 192): 540 null
      // try (205 -> 232): 540 null
      // try (245 -> 375): 540 null
      // try (389 -> 394): 540 null
      // try (408 -> 409): 540 null
      // try (423 -> 428): 540 null
      // try (442 -> 443): 540 null
      // try (457 -> 458): 540 null
      // try (472 -> 476): 540 null
      // try (490 -> 491): 540 null
      // try (504 -> 509): 540 null
      // try (522 -> 527): 540 null
      // try (540 -> 541): 540 null
   }

   CryptoSmartCardUtilities$DoImportWork(KeyStore x0, KeyStore x1, CryptoSmartCardUtilities$1 x2) {
      this(x0, x1);
   }
}
