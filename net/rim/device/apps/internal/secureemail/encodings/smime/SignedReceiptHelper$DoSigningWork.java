package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.cms.CMSEntityIdentifier;
import net.rim.device.api.crypto.cms.CMSSignedDataInputStream;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

class SignedReceiptHelper$DoSigningWork extends PleaseWaitWorkerThread {
   private boolean _result;
   private CMSSignedDataInputStream _signedStream;
   private EmailMessageModel _receivedModel;
   private boolean _isPIN;
   private int _numSignersRequestingReceipts;
   private CMSEntityIdentifier[] _signersRequestingReceipts;
   private ServiceRecord _serviceRecord;

   private SignedReceiptHelper$DoSigningWork(
      CMSSignedDataInputStream signedStream,
      EmailMessageModel receivedModel,
      boolean isPIN,
      int numSignersRequestingReceipts,
      CMSEntityIdentifier[] signersRequestingReceipts,
      ServiceRecord serviceRecord
   ) {
      this._signedStream = signedStream;
      this._receivedModel = receivedModel;
      this._isPIN = isPIN;
      this._numSignersRequestingReceipts = numSignersRequestingReceipts;
      this._signersRequestingReceipts = signersRequestingReceipts;
      this._serviceRecord = serviceRecord;
   }

   public boolean getResult() {
      return this._result;
   }

   @Override
   protected void doWork() {
      this._result = this.processSignedReceiptRequestStageTwo(
         this._signedStream, this._receivedModel, this._isPIN, this._numSignersRequestingReceipts, this._signersRequestingReceipts, this._serviceRecord
      );
   }

   private boolean processSignedReceiptRequestStageTwo(
      CMSSignedDataInputStream param1, EmailMessageModel param2, boolean param3, int param4, CMSEntityIdentifier[] param5, ServiceRecord param6
   ) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: invokestatic net/rim/device/api/crypto/keystore/KeyStoreManager.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStoreManager;
      // 003: astore 7
      // 005: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEFactory.getInstance ()Lnet/rim/device/apps/internal/secureemail/encodings/smime/SMIMEFactory;
      // 008: invokevirtual net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEFactory.createGlobalOptionsCopy ()Lnet/rim/device/apps/internal/secureemail/SecureEmailOptions;
      // 00b: checkcast net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEOptions
      // 00e: astore 8
      // 010: aload 8
      // 012: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailOptions.getSigningKeyStoreData ()Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 015: astore 9
      // 017: aload 9
      // 019: ifnonnull 037
      // 01c: sipush 2071
      // 01f: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEResources.getString (I)Ljava/lang/String;
      // 022: invokestatic net/rim/device/internal/ui/component/SimpleChoiceDialog.askYesNoQuestionOnBackground (Ljava/lang/String;)Z
      // 025: ifne 02a
      // 028: bipush 0
      // 029: ireturn
      // 02a: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEFactory.getInstance ()Lnet/rim/device/apps/internal/secureemail/encodings/smime/SMIMEFactory;
      // 02d: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.createOptionsItem ()Lnet/rim/device/apps/internal/secureemail/SecureEmailOptionsItem;
      // 030: aconst_null
      // 031: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailOptionsItem.doBlocking (Ljava/lang/Object;)V
      // 034: goto 005
      // 037: aload 9
      // 039: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 03e: astore 10
      // 040: iload 3
      // 041: ifne 052
      // 044: aload 7
      // 046: aload 10
      // 048: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManager.isSyncedWithBES (Lnet/rim/device/api/crypto/certificate/Certificate;)Z
      // 04b: ifeq 052
      // 04e: bipush 1
      // 04f: goto 053
      // 052: bipush 0
      // 053: istore 11
      // 055: aconst_null
      // 056: astore 12
      // 058: bipush 0
      // 059: istore 13
      // 05b: iload 3
      // 05c: ifeq 063
      // 05f: bipush 0
      // 060: goto 064
      // 063: bipush 1
      // 064: istore 14
      // 066: iload 14
      // 068: ifeq 0aa
      // 06b: aload 8
      // 06d: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailOptions.getEncryptionKeyStoreData ()Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 070: astore 15
      // 072: aload 15
      // 074: ifnull 0aa
      // 077: aload 15
      // 079: aload 9
      // 07b: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreData.equals (Ljava/lang/Object;)Z
      // 07e: ifne 0aa
      // 081: aload 15
      // 083: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 088: astore 16
      // 08a: bipush 1
      // 08b: anewarray 237
      // 08e: dup
      // 08f: bipush 0
      // 090: aload 16
      // 092: aastore
      // 093: astore 12
      // 095: iload 3
      // 096: ifne 0a7
      // 099: aload 7
      // 09b: aload 16
      // 09d: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManager.isSyncedWithBES (Lnet/rim/device/api/crypto/certificate/Certificate;)Z
      // 0a0: ifeq 0a7
      // 0a3: bipush 1
      // 0a4: goto 0a8
      // 0a7: bipush 0
      // 0a8: istore 13
      // 0aa: aload 9
      // 0ac: sipush 2053
      // 0af: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEResources.getString (I)Ljava/lang/String;
      // 0b2: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getTicket (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket; 2
      // 0b7: astore 16
      // 0b9: aload 9
      // 0bb: aload 16
      // 0bd: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey; 2
      // 0c2: astore 17
      // 0c4: aload 17
      // 0c6: invokeinterface net/rim/device/api/crypto/PrivateKey.verify ()V 1
      // 0cb: aload 17
      // 0cd: aconst_null
      // 0ce: invokestatic net/rim/device/api/crypto/SignatureSignerFactory.getInstance (Lnet/rim/device/api/crypto/PrivateKey;Ljava/lang/String;)Lnet/rim/device/api/crypto/SignatureSigner;
      // 0d1: astore 15
      // 0d3: goto 0de
      // 0d6: astore 16
      // 0d8: bipush 1
      // 0d9: ireturn
      // 0da: astore 16
      // 0dc: bipush 0
      // 0dd: ireturn
      // 0de: invokestatic net/rim/device/api/system/Application.getEventLock ()Ljava/lang/Object;
      // 0e1: dup
      // 0e2: astore 16
      // 0e4: monitorenter
      // 0e5: aload 0
      // 0e6: getfield net/rim/device/internal/ui/component/PleaseWaitWorkerThread._pleaseWaitDialog Lnet/rim/device/internal/ui/component/PleaseWaitDialog;
      // 0e9: sipush 2079
      // 0ec: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEResources.getString (I)Ljava/lang/String;
      // 0ef: invokevirtual net/rim/device/internal/ui/component/PleaseWaitDialog.setMessage (Ljava/lang/String;)V
      // 0f2: aload 16
      // 0f4: monitorexit
      // 0f5: goto 100
      // 0f8: astore 18
      // 0fa: aload 16
      // 0fc: monitorexit
      // 0fd: aload 18
      // 0ff: athrow
      // 100: bipush 0
      // 101: istore 16
      // 103: iload 16
      // 105: iload 4
      // 107: if_icmplt 10d
      // 10a: goto 28f
      // 10d: new java/lang/Object
      // 110: dup
      // 111: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 114: astore 17
      // 116: new java/lang/Object
      // 119: dup
      // 11a: aload 17
      // 11c: invokespecial net/rim/device/api/io/Base64OutputStream.<init> (Ljava/io/OutputStream;)V
      // 11f: astore 18
      // 121: new java/lang/Object
      // 124: dup
      // 125: aload 18
      // 127: bipush 14
      // 129: bipush 1
      // 12a: bipush 1
      // 12b: iload 14
      // 12d: invokespecial net/rim/device/api/crypto/cms/CMSSignedDataOutputStream.<init> (Ljava/io/OutputStream;IZZZ)V
      // 130: astore 19
      // 132: new java/lang/Object
      // 135: dup
      // 136: aload 15
      // 138: aload 10
      // 13a: iload 11
      // 13c: invokespecial net/rim/device/api/crypto/cms/CMSSigner.<init> (Lnet/rim/device/api/crypto/SignatureSigner;Lnet/rim/device/api/crypto/certificate/Certificate;Z)V
      // 13f: astore 20
      // 141: aload 19
      // 143: aload 20
      // 145: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataOutputStream.addSigner (Lnet/rim/device/api/crypto/cms/CMSSigner;)V
      // 148: aload 12
      // 14a: ifnull 156
      // 14d: aload 19
      // 14f: aload 12
      // 151: iload 13
      // 153: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataOutputStream.addCertificates ([Lnet/rim/device/api/crypto/certificate/Certificate;Z)V
      // 156: aload 1
      // 157: aload 19
      // 159: aload 5
      // 15b: iload 16
      // 15d: aaload
      // 15e: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataInputStream.createSignedReceiptStream (Lnet/rim/device/api/crypto/cms/CMSSignedDataOutputStream;Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;)Lnet/rim/device/api/crypto/cms/CMSSignedReceiptOutputStream;
      // 161: astore 21
      // 163: aload 21
      // 165: ifnull 16d
      // 168: aload 21
      // 16a: invokevirtual net/rim/device/api/crypto/cms/CMSSignedReceiptOutputStream.close ()V
      // 16d: new net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEBodyModel
      // 170: dup
      // 171: aload 17
      // 173: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 176: bipush 0
      // 177: bipush 1
      // 178: bipush 1
      // 179: getstatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEConstants.SECURITY_ENCODING_SMIME_BYTES [B
      // 17c: aconst_null
      // 17d: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEBodyModel.<init> ([BZZZ[B[B)V
      // 180: astore 22
      // 182: ldc2_w -6822293833372928884
      // 185: aconst_null
      // 186: invokestatic net/rim/device/api/util/FactoryUtil.createInstance (JLjava/lang/Object;)Ljava/lang/Object;
      // 189: checkcast java/lang/Object
      // 18c: astore 23
      // 18e: iload 3
      // 18f: ifeq 19c
      // 192: aload 23
      // 194: sipush 8192
      // 197: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.setFlags (I)V 2
      // 19c: bipush 2
      // 19e: anewarray 391
      // 1a1: astore 24
      // 1a3: new java/lang/Object
      // 1a6: dup
      // 1a7: sipush 2073
      // 1aa: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEResources.getString (I)Ljava/lang/String;
      // 1ad: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 1b0: astore 25
      // 1b2: aload 2
      // 1b3: invokeinterface net/rim/device/api/collection/ReadableList.size ()I 1
      // 1b8: istore 26
      // 1ba: bipush 0
      // 1bb: istore 27
      // 1bd: iload 27
      // 1bf: iload 26
      // 1c1: if_icmpge 219
      // 1c4: aload 2
      // 1c5: iload 27
      // 1c7: invokeinterface net/rim/device/api/collection/ReadableList.getAt (I)Ljava/lang/Object; 2
      // 1cc: astore 28
      // 1ce: aload 28
      // 1d0: dup
      // 1d1: instanceof java/lang/Object
      // 1d4: ifne 1db
      // 1d7: pop
      // 1d8: goto 200
      // 1db: checkcast java/lang/Object
      // 1de: astore 29
      // 1e0: aload 29
      // 1e2: invokevirtual net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel.getHeaderType ()I
      // 1e5: bipush 3
      // 1e7: if_icmpeq 1f4
      // 1ea: aload 29
      // 1ec: invokevirtual net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel.getHeaderType ()I
      // 1ef: bipush 4
      // 1f1: if_icmpne 213
      // 1f4: aload 29
      // 1f6: aconst_null
      // 1f7: aload 24
      // 1f9: invokevirtual net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel.convert (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 1fc: pop
      // 1fd: goto 213
      // 200: aload 28
      // 202: instanceof java/lang/Object
      // 205: ifeq 213
      // 208: aload 25
      // 20a: aload 28
      // 20c: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 20f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 212: pop
      // 213: iinc 27 1
      // 216: goto 1bd
      // 219: new java/lang/Object
      // 21c: dup
      // 21d: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 220: astore 27
      // 222: aload 27
      // 224: sipush 251
      // 227: i2l
      // 228: aload 24
      // 22a: invokestatic net/rim/device/apps/api/framework/model/ContextObject.put (Ljava/lang/Object;JLjava/lang/Object;)Ljava/lang/Object;
      // 22d: pop
      // 22e: ldc2_w -2985347935260258684
      // 231: aload 27
      // 233: invokestatic net/rim/device/api/util/FactoryUtil.createInstance (JLjava/lang/Object;)Ljava/lang/Object;
      // 236: checkcast java/lang/Object
      // 239: astore 28
      // 23b: aload 23
      // 23d: aload 25
      // 23f: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 242: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailBuilderApi.addSubjectLine (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;Ljava/lang/String;)V
      // 245: aload 23
      // 247: bipush 0
      // 248: aload 28
      // 24a: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailBuilderApi.addRecipient (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;ILnet/rim/device/apps/api/framework/model/RIMModel;)V
      // 24d: aload 23
      // 24f: bipush 32
      // 251: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.setType (B)V 2
      // 256: aload 23
      // 258: aload 22
      // 25a: invokeinterface net/rim/device/api/collection/WritableSet.add (Ljava/lang/Object;)V 2
      // 25f: aload 23
      // 261: aload 6
      // 263: aconst_null
      // 264: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailSendUtility.sendMessage (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;Lnet/rim/device/api/servicebook/ServiceRecord;Ljava/lang/Object;)Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 267: astore 23
      // 269: aload 2
      // 26a: sipush 16384
      // 26d: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.setFlags (I)V 2
      // 272: aload 2
      // 273: invokestatic net/rim/device/api/system/PersistentObject.commit (Ljava/lang/Object;)V
      // 276: aload 1
      // 277: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataInputStream.getReceiptInformation ()[Lnet/rim/device/api/crypto/cms/CMSReceiptData;
      // 27a: aload 2
      // 27b: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SignedReceiptHelper.addReceiptDataToCache ([Lnet/rim/device/api/crypto/cms/CMSReceiptData;Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;)V
      // 27e: goto 289
      // 281: astore 17
      // 283: bipush 0
      // 284: ireturn
      // 285: astore 17
      // 287: bipush 0
      // 288: ireturn
      // 289: iinc 16 1
      // 28c: goto 103
      // 28f: bipush 1
      // 290: ireturn
      // try (76 -> 91): 92 null
      // try (76 -> 91): 95 null
      // try (102 -> 109): 110 null
      // try (110 -> 113): 110 null
      // try (121 -> 283): 284 null
      // try (121 -> 283): 287 null
   }

   SignedReceiptHelper$DoSigningWork(
      CMSSignedDataInputStream x0, EmailMessageModel x1, boolean x2, int x3, CMSEntityIdentifier[] x4, ServiceRecord x5, SignedReceiptHelper$1 x6
   ) {
      this(x0, x1, x2, x3, x4, x5);
   }
}
