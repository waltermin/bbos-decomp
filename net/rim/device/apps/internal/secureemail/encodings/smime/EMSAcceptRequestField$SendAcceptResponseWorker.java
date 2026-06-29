package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

class EMSAcceptRequestField$SendAcceptResponseWorker extends PleaseWaitWorkerThread {
   private boolean _result;
   private boolean _accept;
   private final EMSAcceptRequestField this$0;

   protected EMSAcceptRequestField$SendAcceptResponseWorker(EMSAcceptRequestField _1, boolean accept) {
      this.this$0 = _1;
      this._accept = accept;
   }

   protected boolean getResult() {
      return this._result;
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
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField;
      // 004: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField._emailMessageModel Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 007: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailMessageUtilities.getServiceRecordForMessage (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;)Lnet/rim/device/api/servicebook/ServiceRecord;
      // 00a: astore 1
      // 00b: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEFactory.getInstance ()Lnet/rim/device/apps/internal/secureemail/encodings/smime/SMIMEFactory;
      // 00e: invokevirtual net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEFactory.createGlobalOptionsCopy ()Lnet/rim/device/apps/internal/secureemail/SecureEmailOptions;
      // 011: checkcast net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEOptions
      // 014: astore 2
      // 015: aload 2
      // 016: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailOptions.getSigningKeyStoreData ()Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 019: astore 3
      // 01a: aload 3
      // 01b: ifnull 021
      // 01e: goto 040
      // 021: sipush 2101
      // 024: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEResources.getString (I)Ljava/lang/String;
      // 027: invokestatic net/rim/device/internal/ui/component/SimpleChoiceDialog.askYesNoQuestionOnBackground (Ljava/lang/String;)Z
      // 02a: ifne 033
      // 02d: aload 0
      // 02e: bipush 0
      // 02f: putfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker._result Z
      // 032: return
      // 033: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEFactory.getInstance ()Lnet/rim/device/apps/internal/secureemail/encodings/smime/SMIMEFactory;
      // 036: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.createOptionsItem ()Lnet/rim/device/apps/internal/secureemail/SecureEmailOptionsItem;
      // 039: aconst_null
      // 03a: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailOptionsItem.doBlocking (Ljava/lang/Object;)V
      // 03d: goto 00b
      // 040: aload 3
      // 041: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 046: astore 4
      // 048: invokestatic net/rim/device/api/crypto/keystore/KeyStoreManager.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStoreManager;
      // 04b: aload 4
      // 04d: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManager.isSyncedWithBES (Lnet/rim/device/api/crypto/certificate/Certificate;)Z
      // 050: istore 5
      // 052: aload 3
      // 053: sipush 2099
      // 056: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEResources.getString (I)Ljava/lang/String;
      // 059: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getTicket (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket; 2
      // 05e: astore 7
      // 060: aload 3
      // 061: aload 7
      // 063: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey; 2
      // 068: astore 8
      // 06a: aload 8
      // 06c: invokeinterface net/rim/device/api/crypto/PrivateKey.verify ()V 1
      // 071: aload 8
      // 073: aconst_null
      // 074: invokestatic net/rim/device/api/crypto/SignatureSignerFactory.getInstance (Lnet/rim/device/api/crypto/PrivateKey;Ljava/lang/String;)Lnet/rim/device/api/crypto/SignatureSigner;
      // 077: astore 6
      // 079: goto 08c
      // 07c: astore 7
      // 07e: aload 0
      // 07f: bipush 0
      // 080: putfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker._result Z
      // 083: return
      // 084: astore 7
      // 086: aload 0
      // 087: bipush 0
      // 088: putfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker._result Z
      // 08b: return
      // 08c: new java/io/ByteArrayOutputStream
      // 08f: dup
      // 090: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 093: astore 7
      // 095: new net/rim/device/api/io/Base64OutputStream
      // 098: dup
      // 099: aload 7
      // 09b: invokespecial net/rim/device/api/io/Base64OutputStream.<init> (Ljava/io/OutputStream;)V
      // 09e: astore 8
      // 0a0: new net/rim/device/api/crypto/cms/CMSSignedDataOutputStream
      // 0a3: dup
      // 0a4: aload 8
      // 0a6: bipush 15
      // 0a8: bipush 1
      // 0a9: bipush 1
      // 0aa: bipush 1
      // 0ab: invokespecial net/rim/device/api/crypto/cms/CMSSignedDataOutputStream.<init> (Ljava/io/OutputStream;IZZZ)V
      // 0ae: astore 9
      // 0b0: new net/rim/device/api/crypto/cms/CMSSigner
      // 0b3: dup
      // 0b4: aload 6
      // 0b6: aload 4
      // 0b8: iload 5
      // 0ba: invokespecial net/rim/device/api/crypto/cms/CMSSigner.<init> (Lnet/rim/device/api/crypto/SignatureSigner;Lnet/rim/device/api/crypto/certificate/Certificate;Z)V
      // 0bd: astore 10
      // 0bf: aload 9
      // 0c1: aload 10
      // 0c3: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataOutputStream.addSigner (Lnet/rim/device/api/crypto/cms/CMSSigner;)V
      // 0c6: aload 0
      // 0c7: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker._accept Z
      // 0ca: ifeq 0de
      // 0cd: bipush 1
      // 0ce: anewarray 234
      // 0d1: dup
      // 0d2: bipush 0
      // 0d3: aload 0
      // 0d4: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField;
      // 0d7: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField._certificate Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 0da: aastore
      // 0db: goto 0df
      // 0de: aconst_null
      // 0df: astore 11
      // 0e1: new net/rim/device/api/crypto/cms/EMSAcceptResponseOutputStream
      // 0e4: dup
      // 0e5: aload 9
      // 0e7: aload 0
      // 0e8: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField;
      // 0eb: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField._emsAcceptRequestInputStream Lnet/rim/device/api/crypto/cms/EMSAcceptRequestInputStream;
      // 0ee: invokevirtual net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream.getNames ()[Ljava/lang/String;
      // 0f1: aload 11
      // 0f3: aload 0
      // 0f4: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField;
      // 0f7: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField._emsAcceptRequestInputStream Lnet/rim/device/api/crypto/cms/EMSAcceptRequestInputStream;
      // 0fa: invokevirtual net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream.getDisplayName ()Ljava/lang/String;
      // 0fd: aload 0
      // 0fe: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField;
      // 101: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField._emsAcceptRequestInputStream Lnet/rim/device/api/crypto/cms/EMSAcceptRequestInputStream;
      // 104: invokevirtual net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream.getFlags ()Lnet/rim/device/api/crypto/asn1/ASN1BitSet;
      // 107: aload 0
      // 108: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField;
      // 10b: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField._emsAcceptRequestInputStream Lnet/rim/device/api/crypto/cms/EMSAcceptRequestInputStream;
      // 10e: invokevirtual net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream.getVersion ()I
      // 111: aload 0
      // 112: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField;
      // 115: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField._emsAcceptRequestInputStream Lnet/rim/device/api/crypto/cms/EMSAcceptRequestInputStream;
      // 118: invokevirtual net/rim/device/api/crypto/cms/EMSAcceptRequestInputStream.getClientID ()Ljava/lang/String;
      // 11b: invokespecial net/rim/device/api/crypto/cms/EMSAcceptResponseOutputStream.<init> (Lnet/rim/device/api/crypto/cms/CMSSignedDataOutputStream;[Ljava/lang/String;[Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;Ljava/lang/String;Lnet/rim/device/api/crypto/asn1/ASN1BitSet;ILjava/lang/String;)V
      // 11e: astore 12
      // 120: aload 12
      // 122: invokevirtual net/rim/device/api/crypto/cms/EMSAcceptResponseOutputStream.close ()V
      // 125: new net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEBodyModel
      // 128: dup
      // 129: aload 7
      // 12b: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 12e: bipush 0
      // 12f: bipush 0
      // 130: bipush 1
      // 131: getstatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEConstants.SECURITY_ENCODING_SMIME_BYTES [B
      // 134: aconst_null
      // 135: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEBodyModel.<init> ([BZZZ[B[B)V
      // 138: astore 13
      // 13a: ldc2_w -6822293833372928884
      // 13d: aconst_null
      // 13e: invokestatic net/rim/device/api/util/FactoryUtil.createInstance (JLjava/lang/Object;)Ljava/lang/Object;
      // 141: checkcast net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel
      // 144: astore 14
      // 146: bipush 2
      // 148: anewarray 382
      // 14b: astore 15
      // 14d: aload 0
      // 14e: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField;
      // 151: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField._emailMessageModel Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 154: invokeinterface net/rim/device/api/collection/ReadableList.size ()I 1
      // 159: istore 16
      // 15b: bipush 0
      // 15c: istore 17
      // 15e: iload 17
      // 160: iload 16
      // 162: if_icmpge 1ad
      // 165: aload 0
      // 166: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField;
      // 169: getfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField._emailMessageModel Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 16c: iload 17
      // 16e: invokeinterface net/rim/device/api/collection/ReadableList.getAt (I)Ljava/lang/Object; 2
      // 173: astore 18
      // 175: aload 18
      // 177: dup
      // 178: instanceof net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel
      // 17b: ifne 182
      // 17e: pop
      // 17f: goto 1a7
      // 182: checkcast net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel
      // 185: astore 19
      // 187: aload 19
      // 189: invokevirtual net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel.getHeaderType ()I
      // 18c: bipush 3
      // 18e: if_icmpeq 19b
      // 191: aload 19
      // 193: invokevirtual net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel.getHeaderType ()I
      // 196: bipush 4
      // 198: if_icmpne 1a7
      // 19b: aload 19
      // 19d: aconst_null
      // 19e: aload 15
      // 1a0: invokevirtual net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel.convert (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 1a3: pop
      // 1a4: goto 1ad
      // 1a7: iinc 17 1
      // 1aa: goto 15e
      // 1ad: new net/rim/device/apps/api/framework/model/ContextObject
      // 1b0: dup
      // 1b1: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 1b4: astore 17
      // 1b6: aload 17
      // 1b8: sipush 251
      // 1bb: i2l
      // 1bc: aload 15
      // 1be: invokestatic net/rim/device/apps/api/framework/model/ContextObject.put (Ljava/lang/Object;JLjava/lang/Object;)Ljava/lang/Object;
      // 1c1: pop
      // 1c2: ldc2_w -2985347935260258684
      // 1c5: aload 17
      // 1c7: invokestatic net/rim/device/api/util/FactoryUtil.createInstance (JLjava/lang/Object;)Ljava/lang/Object;
      // 1ca: checkcast net/rim/device/apps/api/framework/model/RIMModel
      // 1cd: astore 18
      // 1cf: aload 14
      // 1d1: bipush 0
      // 1d2: aload 18
      // 1d4: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailBuilderApi.addRecipient (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;ILnet/rim/device/apps/api/framework/model/RIMModel;)V
      // 1d7: aload 14
      // 1d9: sipush 2100
      // 1dc: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEResources.getString (I)Ljava/lang/String;
      // 1df: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailBuilderApi.addSubjectLine (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;Ljava/lang/String;)V
      // 1e2: aload 14
      // 1e4: bipush 32
      // 1e6: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.setType (B)V 2
      // 1eb: aload 14
      // 1ed: aload 13
      // 1ef: invokeinterface net/rim/device/api/collection/WritableSet.add (Ljava/lang/Object;)V 2
      // 1f4: aload 14
      // 1f6: aload 1
      // 1f7: aconst_null
      // 1f8: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailSendUtility.sendMessage (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;Lnet/rim/device/api/servicebook/ServiceRecord;Ljava/lang/Object;)Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 1fb: astore 14
      // 1fd: aload 0
      // 1fe: bipush 1
      // 1ff: putfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker._result Z
      // 202: return
      // 203: astore 7
      // 205: goto 20a
      // 208: astore 7
      // 20a: aload 0
      // 20b: bipush 0
      // 20c: putfield net/rim/device/apps/internal/secureemail/encodings/smime/EMSAcceptRequestField$SendAcceptResponseWorker._result Z
      // 20f: return
      // try (35 -> 50): 51 null
      // try (35 -> 50): 56 null
      // try (61 -> 227): 228 null
      // try (61 -> 227): 230 null
   }
}
