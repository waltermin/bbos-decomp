package net.rim.device.apps.internal.docview.gui;

import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;

final class DocViewMoreVerb extends EmailMoreVerb {
   private int _messageID;
   boolean _duplicateMore;
   private boolean _autoLoadInProgress;
   DocViewDisplayScreen _viewerScreen;
   byte _docViewMoreType;

   public DocViewMoreVerb(DocViewDisplayScreen viewerScreen, int messageID, MorePartModel partModel, byte[] contentType, boolean duplicateMore, byte moreType) {
      super(partModel, moreType, contentType);
      this._viewerScreen = viewerScreen;
      this._duplicateMore = duplicateMore;
      this._messageID = messageID;
      this._docViewMoreType = moreType;
   }

   @Override
   public final Object invoke(Object param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ifnonnull 006
      // 004: aconst_null
      // 005: areturn
      // 006: aconst_null
      // 007: astore 2
      // 008: aload 1
      // 009: ldc2_w -7432523643332070209
      // 00c: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 00f: checkcast net/rim/device/apps/internal/docview/gui/ClientRequest
      // 012: astore 2
      // 013: goto 017
      // 016: astore 3
      // 017: aload 2
      // 018: ifnonnull 01d
      // 01b: aconst_null
      // 01c: areturn
      // 01d: aload 1
      // 01e: ldc2_w 2945628545186852484
      // 021: bipush 0
      // 022: invokestatic net/rim/device/apps/api/framework/model/ContextObject.getPrivateFlag (Ljava/lang/Object;JI)Z
      // 025: istore 3
      // 026: aload 0
      // 027: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._viewerScreen Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayScreen;
      // 02a: ifnonnull 030
      // 02d: goto 280
      // 030: aload 2
      // 031: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._areaToEnlarge Lnet/rim/device/api/ui/XYRect;
      // 034: ifnull 03a
      // 037: goto 280
      // 03a: aload 2
      // 03b: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._blockIndex I
      // 03e: bipush -1
      // 040: if_icmpne 04e
      // 043: aload 2
      // 044: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._srcType B
      // 047: bipush 1
      // 048: if_icmpeq 04e
      // 04b: goto 280
      // 04e: aload 2
      // 04f: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._commandCode Ljava/lang/String;
      // 052: ldc_w "NEXT"
      // 055: invokevirtual java/lang/String.compareTo (Ljava/lang/String;)I
      // 058: ifeq 06b
      // 05b: aload 2
      // 05c: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._commandCode Ljava/lang/String;
      // 05f: ldc_w "AUDIO"
      // 062: invokevirtual java/lang/String.compareTo (Ljava/lang/String;)I
      // 065: ifeq 06b
      // 068: goto 280
      // 06b: aload 2
      // 06c: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._srcType B
      // 06f: bipush 1
      // 070: if_icmpne 07a
      // 073: aload 2
      // 074: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._domID Ljava/lang/String;
      // 077: goto 07b
      // 07a: aconst_null
      // 07b: astore 4
      // 07d: invokestatic net/rim/device/apps/internal/docview/gui/DocViewAttachmentPersist.getInstance ()Lnet/rim/device/apps/internal/docview/gui/DocViewAttachmentPersist;
      // 080: astore 5
      // 082: aload 5
      // 084: aload 0
      // 085: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._messageID I
      // 088: aload 0
      // 089: getfield net/rim/device/apps/internal/blackberryemail/email/api/EmailMoreVerb._morePartModel Lnet/rim/device/apps/internal/blackberryemail/email/MorePartModel;
      // 08c: invokeinterface net/rim/device/apps/internal/blackberryemail/email/MorePartModel.getMorePartID ()I 1
      // 091: aload 2
      // 092: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._archiveIndicator Ljava/lang/String;
      // 095: aload 4
      // 097: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewAttachmentPersist.getAttachmentBlockCount (IILjava/lang/String;Ljava/lang/String;)I
      // 09a: istore 6
      // 09c: iload 6
      // 09e: ifgt 0a4
      // 0a1: goto 280
      // 0a4: aconst_null
      // 0a5: astore 7
      // 0a7: bipush 0
      // 0a8: istore 8
      // 0aa: aload 2
      // 0ab: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._blockIndex I
      // 0ae: bipush -1
      // 0b0: if_icmpeq 0bc
      // 0b3: aload 2
      // 0b4: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._blockIndex I
      // 0b7: bipush 1
      // 0b8: iadd
      // 0b9: goto 0bd
      // 0bc: bipush 0
      // 0bd: istore 9
      // 0bf: aload 2
      // 0c0: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._imageRequest Z
      // 0c3: ifeq 12e
      // 0c6: aload 2
      // 0c7: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._blockIndex I
      // 0ca: bipush -1
      // 0cc: if_icmpne 12e
      // 0cf: bipush 1
      // 0d0: istore 8
      // 0d2: bipush 0
      // 0d3: istore 10
      // 0d5: iload 10
      // 0d7: iload 6
      // 0d9: if_icmpge 105
      // 0dc: aload 5
      // 0de: aload 0
      // 0df: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._messageID I
      // 0e2: aload 0
      // 0e3: getfield net/rim/device/apps/internal/blackberryemail/email/api/EmailMoreVerb._morePartModel Lnet/rim/device/apps/internal/blackberryemail/email/MorePartModel;
      // 0e6: invokeinterface net/rim/device/apps/internal/blackberryemail/email/MorePartModel.getMorePartID ()I 1
      // 0eb: aload 2
      // 0ec: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._archiveIndicator Ljava/lang/String;
      // 0ef: aload 4
      // 0f1: iload 10
      // 0f3: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewAttachmentPersist.isObjectChunkAvailable (IILjava/lang/String;Ljava/lang/String;I)Z
      // 0f6: ifne 0ff
      // 0f9: bipush 0
      // 0fa: istore 8
      // 0fc: goto 105
      // 0ff: iinc 10 1
      // 102: goto 0d5
      // 105: iload 8
      // 107: ifeq 189
      // 10a: iload 6
      // 10c: bipush 1
      // 10d: if_icmpne 189
      // 110: aload 5
      // 112: aload 0
      // 113: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._messageID I
      // 116: aload 0
      // 117: getfield net/rim/device/apps/internal/blackberryemail/email/api/EmailMoreVerb._morePartModel Lnet/rim/device/apps/internal/blackberryemail/email/MorePartModel;
      // 11a: invokeinterface net/rim/device/apps/internal/blackberryemail/email/MorePartModel.getMorePartID ()I 1
      // 11f: aload 2
      // 120: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._archiveIndicator Ljava/lang/String;
      // 123: aload 4
      // 125: bipush 0
      // 126: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewAttachmentPersist.getUCSData (IILjava/lang/String;Ljava/lang/String;I)[B
      // 129: astore 7
      // 12b: goto 189
      // 12e: aload 5
      // 130: aload 0
      // 131: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._messageID I
      // 134: aload 0
      // 135: getfield net/rim/device/apps/internal/blackberryemail/email/api/EmailMoreVerb._morePartModel Lnet/rim/device/apps/internal/blackberryemail/email/MorePartModel;
      // 138: invokeinterface net/rim/device/apps/internal/blackberryemail/email/MorePartModel.getMorePartID ()I 1
      // 13d: aload 2
      // 13e: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._archiveIndicator Ljava/lang/String;
      // 141: aload 4
      // 143: iload 9
      // 145: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewAttachmentPersist.getUCSData (IILjava/lang/String;Ljava/lang/String;I)[B
      // 148: astore 7
      // 14a: iload 3
      // 14b: ifeq 189
      // 14e: aload 7
      // 150: ifnonnull 189
      // 153: aload 1
      // 154: ldc2_w 2945628545186852484
      // 157: bipush 1
      // 158: invokestatic net/rim/device/apps/api/framework/model/ContextObject.getPrivateFlag (Ljava/lang/Object;JI)Z
      // 15b: ifne 189
      // 15e: iinc 9 1
      // 161: iload 9
      // 163: iload 6
      // 165: if_icmpge 189
      // 168: aload 5
      // 16a: aload 0
      // 16b: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._messageID I
      // 16e: aload 0
      // 16f: getfield net/rim/device/apps/internal/blackberryemail/email/api/EmailMoreVerb._morePartModel Lnet/rim/device/apps/internal/blackberryemail/email/MorePartModel;
      // 172: invokeinterface net/rim/device/apps/internal/blackberryemail/email/MorePartModel.getMorePartID ()I 1
      // 177: aload 2
      // 178: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._archiveIndicator Ljava/lang/String;
      // 17b: aload 4
      // 17d: iload 9
      // 17f: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewAttachmentPersist.getUCSData (IILjava/lang/String;Ljava/lang/String;I)[B
      // 182: astore 7
      // 184: aload 7
      // 186: ifnull 15e
      // 189: iload 8
      // 18b: ifne 196
      // 18e: aload 7
      // 190: ifnonnull 196
      // 193: goto 280
      // 196: bipush 0
      // 197: istore 10
      // 199: aload 0
      // 19a: aload 0
      // 19b: astore 11
      // 19d: monitorenter
      // 19e: aload 0
      // 19f: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._autoLoadInProgress Z
      // 1a2: ifeq 1aa
      // 1a5: aconst_null
      // 1a6: aload 11
      // 1a8: monitorexit
      // 1a9: areturn
      // 1aa: aload 0
      // 1ab: bipush 1
      // 1ac: putfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._autoLoadInProgress Z
      // 1af: aload 11
      // 1b1: monitorexit
      // 1b2: goto 1bd
      // 1b5: astore 12
      // 1b7: aload 11
      // 1b9: monitorexit
      // 1ba: aload 12
      // 1bc: athrow
      // 1bd: aload 2
      // 1be: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._blockIndex I
      // 1c1: bipush -1
      // 1c3: if_icmpne 20c
      // 1c6: aload 2
      // 1c7: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._srcType B
      // 1ca: bipush 1
      // 1cb: if_icmpne 20c
      // 1ce: aload 0
      // 1cf: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._viewerScreen Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayScreen;
      // 1d2: aload 7
      // 1d4: aload 5
      // 1d6: aload 0
      // 1d7: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._messageID I
      // 1da: aload 0
      // 1db: getfield net/rim/device/apps/internal/blackberryemail/email/api/EmailMoreVerb._morePartModel Lnet/rim/device/apps/internal/blackberryemail/email/MorePartModel;
      // 1de: invokeinterface net/rim/device/apps/internal/blackberryemail/email/MorePartModel.getMorePartID ()I 1
      // 1e3: aload 2
      // 1e4: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._archiveIndicator Ljava/lang/String;
      // 1e7: aload 4
      // 1e9: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewAttachmentPersist.getAttachmentBlockCount (IILjava/lang/String;Ljava/lang/String;)I
      // 1ec: aload 4
      // 1ee: aload 5
      // 1f0: aload 0
      // 1f1: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._messageID I
      // 1f4: aload 0
      // 1f5: getfield net/rim/device/apps/internal/blackberryemail/email/api/EmailMoreVerb._morePartModel Lnet/rim/device/apps/internal/blackberryemail/email/MorePartModel;
      // 1f8: invokeinterface net/rim/device/apps/internal/blackberryemail/email/MorePartModel.getMorePartID ()I 1
      // 1fd: aload 2
      // 1fe: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._archiveIndicator Ljava/lang/String;
      // 201: aload 4
      // 203: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewAttachmentPersist.getAttachmentRetrievedBlockCount (IILjava/lang/String;Ljava/lang/String;)I
      // 206: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen.processEmbeddedInitialChunk (Ljava/lang/Object;ILjava/lang/String;I)V
      // 209: goto 254
      // 20c: aload 4
      // 20e: aload 0
      // 20f: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._viewerScreen Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayScreen;
      // 212: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._isEmbScreen Z
      // 215: ifeq 222
      // 218: aload 0
      // 219: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._viewerScreen Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayScreen;
      // 21c: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._domID Ljava/lang/String;
      // 21f: goto 223
      // 222: aconst_null
      // 223: invokestatic net/rim/device/api/util/ObjectUtilities.objEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 226: ifeq 254
      // 229: aload 0
      // 22a: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._viewerScreen Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayScreen;
      // 22d: aload 7
      // 22f: iload 9
      // 231: aload 5
      // 233: aload 0
      // 234: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._messageID I
      // 237: aload 0
      // 238: getfield net/rim/device/apps/internal/blackberryemail/email/api/EmailMoreVerb._morePartModel Lnet/rim/device/apps/internal/blackberryemail/email/MorePartModel;
      // 23b: invokeinterface net/rim/device/apps/internal/blackberryemail/email/MorePartModel.getMorePartID ()I 1
      // 240: aload 2
      // 241: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._archiveIndicator Ljava/lang/String;
      // 244: aload 4
      // 246: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewAttachmentPersist.getAttachmentRetrievedBlockCount (IILjava/lang/String;Ljava/lang/String;)I
      // 249: iload 3
      // 24a: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen.moreDataProcessed ([BIIZ)B
      // 24d: istore 10
      // 24f: goto 254
      // 252: astore 11
      // 254: aload 0
      // 255: aload 0
      // 256: astore 11
      // 258: monitorenter
      // 259: aload 0
      // 25a: bipush 0
      // 25b: putfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._autoLoadInProgress Z
      // 25e: aload 11
      // 260: monitorexit
      // 261: goto 26c
      // 264: astore 13
      // 266: aload 11
      // 268: monitorexit
      // 269: aload 13
      // 26b: athrow
      // 26c: iload 3
      // 26d: ifeq 27e
      // 270: iload 10
      // 272: bipush 1
      // 273: if_icmpne 27e
      // 276: new net/rim/device/apps/internal/docview/gui/StopAutoLoadObject
      // 279: dup
      // 27a: invokespecial net/rim/device/apps/internal/docview/gui/StopAutoLoadObject.<init> ()V
      // 27d: areturn
      // 27e: aconst_null
      // 27f: areturn
      // 280: iload 3
      // 281: ifeq 28c
      // 284: new net/rim/device/apps/internal/docview/gui/StopAutoLoadObject
      // 287: dup
      // 288: invokespecial net/rim/device/apps/internal/docview/gui/StopAutoLoadObject.<init> ()V
      // 28b: areturn
      // 28c: aload 0
      // 28d: aload 0
      // 28e: astore 4
      // 290: monitorenter
      // 291: bipush 0
      // 292: istore 5
      // 294: aload 0
      // 295: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._duplicateMore Z
      // 298: ifne 2bd
      // 29b: bipush 1
      // 29c: istore 5
      // 29e: aload 0
      // 29f: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._viewerScreen Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayScreen;
      // 2a2: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen.getEmailMessageModel ()Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 2a5: aload 0
      // 2a6: getfield net/rim/device/apps/internal/blackberryemail/email/api/EmailMoreVerb._morePartModel Lnet/rim/device/apps/internal/blackberryemail/email/MorePartModel;
      // 2a9: invokeinterface net/rim/device/apps/internal/blackberryemail/email/MorePartModel.getMorePartID ()I 1
      // 2ae: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailMoreVerb.findMorePartByIdentifier (Lnet/rim/device/api/collection/ReadableList;I)Lnet/rim/device/apps/internal/blackberryemail/email/MorePartModel;
      // 2b1: invokeinterface net/rim/device/apps/internal/blackberryemail/email/MorePartModel.getMoreRequestSent ()Z 1
      // 2b6: istore 5
      // 2b8: goto 2bd
      // 2bb: astore 6
      // 2bd: aload 0
      // 2be: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._duplicateMore Z
      // 2c1: ifne 2c9
      // 2c4: iload 5
      // 2c6: ifne 34a
      // 2c9: aconst_null
      // 2ca: astore 6
      // 2cc: aload 1
      // 2cd: sipush 253
      // 2d0: i2l
      // 2d1: aload 2
      // 2d2: invokestatic net/rim/device/apps/internal/docview/gui/CommandHandler.createClientRequest (Lnet/rim/device/apps/internal/docview/gui/ClientRequest;)Ljava/lang/String;
      // 2d5: invokestatic net/rim/device/apps/api/framework/model/ContextObject.put (Ljava/lang/Object;JLjava/lang/Object;)Ljava/lang/Object;
      // 2d8: pop
      // 2d9: aconst_null
      // 2da: astore 7
      // 2dc: aload 2
      // 2dd: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._srcType B
      // 2e0: bipush 1
      // 2e1: if_icmpne 2ea
      // 2e4: aload 2
      // 2e5: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._domID Ljava/lang/String;
      // 2e8: astore 7
      // 2ea: aload 2
      // 2eb: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._commandCode Ljava/lang/String;
      // 2ee: ifnull 313
      // 2f1: aload 2
      // 2f2: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._commandCode Ljava/lang/String;
      // 2f5: ldc_w "RENDER"
      // 2f8: invokestatic net/rim/device/api/util/ObjectUtilities.objEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 2fb: ifeq 313
      // 2fe: aload 2
      // 2ff: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._arbDOMID Ljava/lang/String;
      // 302: ifnull 313
      // 305: aload 7
      // 307: aload 2
      // 308: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._arbDOMID Ljava/lang/String;
      // 30b: ldc_w "RenderDomID"
      // 30e: invokestatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory.constructCustomDomIDStringEx (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
      // 311: astore 7
      // 313: aload 0
      // 314: getfield net/rim/device/apps/internal/docview/gui/DocViewMoreVerb._viewerScreen Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayScreen;
      // 317: aload 2
      // 318: getfield net/rim/device/apps/internal/docview/gui/ClientRequest._partIndex I
      // 31b: aload 7
      // 31d: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen.saveLatestRequest (ILjava/lang/String;)V
      // 320: goto 325
      // 323: astore 7
      // 325: aload 0
      // 326: aload 1
      // 327: invokespecial net/rim/device/apps/internal/blackberryemail/email/api/EmailMoreVerb.invoke (Ljava/lang/Object;)Ljava/lang/Object;
      // 32a: astore 6
      // 32c: goto 33b
      // 32f: astore 7
      // 331: goto 33b
      // 334: astore 7
      // 336: goto 33b
      // 339: astore 7
      // 33b: aload 1
      // 33c: sipush 253
      // 33f: i2l
      // 340: invokestatic net/rim/device/apps/api/framework/model/ContextObject.remove (Ljava/lang/Object;J)Ljava/lang/Object;
      // 343: pop
      // 344: aload 6
      // 346: aload 4
      // 348: monitorexit
      // 349: areturn
      // 34a: aload 4
      // 34c: monitorexit
      // 34d: aconst_null
      // 34e: areturn
      // 34f: astore 14
      // 351: aload 4
      // 353: monitorexit
      // 354: aload 14
      // 356: athrow
      // try (6 -> 11): 12 null
      // try (189 -> 195): 202 null
      // try (196 -> 201): 202 null
      // try (202 -> 205): 202 null
      // try (207 -> 270): 271 null
      // try (276 -> 281): 282 null
      // try (282 -> 285): 282 null
      // try (315 -> 324): 325 null
      // try (340 -> 372): 373 null
      // try (333 -> 378): 379 null
      // try (333 -> 378): 381 null
      // try (333 -> 378): 383 null
      // try (308 -> 392): 397 null
      // try (393 -> 395): 397 null
      // try (397 -> 400): 397 null
   }
}
