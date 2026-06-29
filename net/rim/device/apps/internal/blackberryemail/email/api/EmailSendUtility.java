package net.rim.device.apps.internal.blackberryemail.email.api;

import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FolderProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.ResolvedStatusProvider;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailSendListener;
import net.rim.device.apps.internal.blackberryemail.email.LargeAttachmentModel;
import net.rim.device.apps.internal.blackberryemail.email.LargeAttachmentModel$LargeCachedAttachmentModel;
import net.rim.device.apps.internal.blackberryemail.email.NativeAttachmentRequestProcessor$Helper;
import net.rim.device.apps.internal.blackberryemail.email.PersistentProxyModelStore;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModel;
import net.rim.device.apps.internal.blackberryemail.email.RemoveWhenSendingModel;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.folder.FolderUtil;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModelFactory;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;
import net.rim.device.internal.crypto.CryptoBlock;
import net.rim.device.internal.deviceoptions.Owner;
import net.rim.device.internal.ui.UiInternal;

public final class EmailSendUtility {
   public static final long ORIGINAL_EMAIL_MESSAGE_MODEL = 2691016377741031418L;
   private static final String CORPORATE_PIN_KEY_NAME = "SCRAMBLE_CURRENT";
   private static final long SENDLISTENERS_ID = 922368635746834671L;

   public static final boolean determineWhetherMessageAlreadyFiled(EmailMessageModel message, Object context) {
      int CMIMEReferenceIdentifier = message.getCMIMEReferenceIdentifier();
      return CMIMEReferenceIdentifier != 0;
   }

   public static final int assignCMIMEReferenceIdentifierToMessage(EmailMessageModel message, Object context) {
      int CMIMEReferenceIdentifier = message.getCMIMEReferenceIdentifier();
      if (CMIMEReferenceIdentifier == 0) {
         CMIMEReferenceIdentifier = CMIMEUtilities.newDeviceSideIdentifier();
         message.setCMIMEReferenceIdentifier(CMIMEReferenceIdentifier);
      }

      return CMIMEReferenceIdentifier;
   }

   public static final int assignGMEReferenceIdentifierToMessage(EmailMessageModel message, Object context) {
      int HACKYHACKYHACKHURRYUPRAFALGMEIdentifier = message.getCMIMEReferenceIdentifier();
      message.setGMEReferenceIdentifier(HACKYHACKYHACKHURRYUPRAFALGMEIdentifier);
      return HACKYHACKYHACKHURRYUPRAFALGMEIdentifier;
   }

   public static final void transportMessage(EmailMessageModel param0, Object param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokestatic net/rim/device/apps/api/framework/model/ContextObject.clone (Ljava/lang/Object;)Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 004: astore 2
      // 005: aload 0
      // 006: sipush 8192
      // 009: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.flagsSet (I)Z 2
      // 00e: istore 3
      // 00f: aload 2
      // 010: bipush 12
      // 012: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.clearFlag (I)V
      // 015: aload 2
      // 016: bipush 53
      // 018: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.clearFlag (I)V
      // 01b: aload 2
      // 01c: bipush 29
      // 01e: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.clearFlag (I)V
      // 021: aload 2
      // 022: bipush 30
      // 024: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.clearFlag (I)V
      // 027: aload 2
      // 028: bipush 13
      // 02a: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.clearFlag (I)V
      // 02d: aload 0
      // 02e: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getType ()B 1
      // 033: lookupswitch 84 5 1 49 2 56 4 63 8 70 16 77
      // 064: bipush 12
      // 066: istore 4
      // 068: goto 08b
      // 06b: bipush 53
      // 06d: istore 4
      // 06f: goto 08b
      // 072: bipush 29
      // 074: istore 4
      // 076: goto 08b
      // 079: bipush 30
      // 07b: istore 4
      // 07d: goto 08b
      // 080: bipush 13
      // 082: istore 4
      // 084: goto 08b
      // 087: bipush 31
      // 089: istore 4
      // 08b: aload 2
      // 08c: iload 4
      // 08e: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 091: ldc2_w 8399767144006445082
      // 094: invokestatic net/rim/device/apps/api/transmission/TransmissionServiceManager.get (J)Lnet/rim/device/apps/api/transmission/TransmissionService;
      // 097: astore 5
      // 099: aload 5
      // 09b: ifnonnull 0a1
      // 09e: goto 39c
      // 0a1: new java/lang/Object
      // 0a4: dup
      // 0a5: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingOutgoingMessage.<init> ()V
      // 0a8: astore 6
      // 0aa: aload 2
      // 0ab: ldc2_w -6095803566992128485
      // 0ae: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 0b1: checkcast java/lang/Object
      // 0b4: astore 7
      // 0b6: invokestatic net/rim/device/api/system/ApplicationRegistry.getApplicationRegistry ()Lnet/rim/device/api/system/ApplicationRegistry;
      // 0b9: ldc2_w -2205884509140292945
      // 0bc: invokevirtual net/rim/device/api/system/ApplicationRegistry.get (J)Ljava/lang/Object;
      // 0bf: checkcast java/lang/Object
      // 0c2: astore 8
      // 0c4: aload 8
      // 0c6: ifnull 0ec
      // 0c9: aload 8
      // 0cb: aload 0
      // 0cc: invokeinterface net/rim/device/apps/api/messaging/implus/IMPlusServiceModel.getAckRequestHeaderByte (Lnet/rim/device/apps/api/framework/model/RIMModel;)B 2
      // 0d1: istore 9
      // 0d3: iload 9
      // 0d5: ifeq 0ec
      // 0d8: aload 6
      // 0da: iload 9
      // 0dc: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setAcknowledgementRequestSettings (B)V
      // 0df: ldc2_w 2623838111545834320
      // 0e2: aload 0
      // 0e3: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getCMIMEReferenceIdentifier ()I 1
      // 0e8: aload 0
      // 0e9: invokestatic net/rim/device/apps/api/messaging/MessageLookups.put (JILjava/lang/Object;)V
      // 0ec: aload 6
      // 0ee: aload 0
      // 0ef: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getType ()B 1
      // 0f4: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingOutgoingMessage.setMessageType (B)V
      // 0f7: aload 0
      // 0f8: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getSensitivity ()B 1
      // 0fd: istore 9
      // 0ff: iload 9
      // 101: ifeq 10f
      // 104: aload 6
      // 106: aload 0
      // 107: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getSensitivity ()B 1
      // 10c: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setSensitivity (B)V
      // 10f: invokestatic net/rim/device/api/i18n/Locale.getDefaultForSystem ()Lnet/rim/device/api/i18n/Locale;
      // 112: astore 10
      // 114: aload 10
      // 116: ifnull 123
      // 119: aload 6
      // 11b: aload 10
      // 11d: invokevirtual net/rim/device/api/i18n/Locale.getCode ()I
      // 120: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setLocaleCode (I)V
      // 123: aload 2
      // 124: ldc2_w 2691016377741031418
      // 127: invokevirtual net/rim/device/api/util/LongHashtable.get (J)Ljava/lang/Object;
      // 12a: astore 11
      // 12c: aload 11
      // 12e: instanceof net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel
      // 131: ifeq 143
      // 134: aload 6
      // 136: aload 11
      // 138: checkcast net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel
      // 13b: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getCMIMEReferenceIdentifier ()I 1
      // 140: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setOriginalReferenceIdentifier (I)V
      // 143: aload 6
      // 145: aload 0
      // 146: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getPriority ()B 1
      // 14b: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setImportance (B)V
      // 14e: aload 0
      // 14f: checkcast java/lang/Object
      // 152: invokeinterface net/rim/device/apps/api/framework/model/FolderProvider.getFolderId ()J 1
      // 157: lstore 12
      // 159: lload 12
      // 15b: bipush 0
      // 15c: i2l
      // 15d: lcmp
      // 15e: ifeq 167
      // 161: aload 0
      // 162: lload 12
      // 164: invokestatic net/rim/device/apps/internal/blackberryemail/folder/EmailHierarchy.removeMessage (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;J)V
      // 167: aload 0
      // 168: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getPayload ()Lnet/rim/device/apps/internal/blackberryemail/email/EmailPayloadModel; 1
      // 16d: astore 14
      // 16f: aload 14
      // 171: invokestatic java/lang/System.currentTimeMillis ()J
      // 174: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailPayloadModel.setCreationDate (J)V 3
      // 179: iload 3
      // 17a: ifeq 195
      // 17d: aload 2
      // 17e: bipush 70
      // 180: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 183: aload 2
      // 184: ldc2_w -8804918554992595454
      // 187: new java/lang/Object
      // 18a: dup
      // 18b: ldc_w "CMIME"
      // 18e: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 191: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 194: pop
      // 195: aload 0
      // 196: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailSendUtility.expandGroupAddresses (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;)V
      // 199: iload 3
      // 19a: ifne 212
      // 19d: aconst_null
      // 19e: aload 7
      // 1a0: if_acmpne 1b8
      // 1a3: aload 0
      // 1a4: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailMessageUtilities.getServiceRecordForMessage (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;)Lnet/rim/device/api/servicebook/ServiceRecord;
      // 1a7: astore 7
      // 1a9: aload 7
      // 1ab: ifnull 1b8
      // 1ae: aload 2
      // 1af: ldc2_w -6095803566992128485
      // 1b2: aload 7
      // 1b4: invokestatic net/rim/device/apps/api/framework/model/ContextObject.put (Ljava/lang/Object;JLjava/lang/Object;)Ljava/lang/Object;
      // 1b7: pop
      // 1b8: aload 7
      // 1ba: ifnull 220
      // 1bd: aload 6
      // 1bf: aconst_null
      // 1c0: aconst_null
      // 1c1: aload 7
      // 1c3: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEUtilities.getTextContentType (Lnet/rim/device/api/servicebook/ServiceRecord;)Ljava/lang/String;
      // 1c6: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setText (Ljava/lang/Object;Lnet/rim/device/apps/api/transmission/Parameters;Ljava/lang/String;)V
      // 1c9: aload 0
      // 1ca: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getEncoding ()B 1
      // 1cf: bipush 112
      // 1d1: iand
      // 1d2: ifeq 1e0
      // 1d5: aload 6
      // 1d7: aload 0
      // 1d8: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getEncoding ()B 1
      // 1dd: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setEncodingHints (B)V
      // 1e0: aload 2
      // 1e1: sipush 129
      // 1e4: i2l
      // 1e5: invokevirtual net/rim/device/api/util/LongHashtable.get (J)Ljava/lang/Object;
      // 1e8: invokestatic net/rim/device/api/system/PersistentContent.decodeString (Ljava/lang/Object;)Ljava/lang/String;
      // 1eb: astore 15
      // 1ed: goto 1f5
      // 1f0: astore 16
      // 1f2: aconst_null
      // 1f3: astore 15
      // 1f5: aload 6
      // 1f7: aload 15
      // 1f9: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingOutgoingMessage.setNNEPassword (Ljava/lang/String;)V
      // 1fc: aload 7
      // 1fe: bipush -14
      // 200: bipush 4
      // 202: bipush 0
      // 203: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEUtilities.getServiceCapability (Lnet/rim/device/api/servicebook/ServiceRecord;BBZ)Z
      // 206: ifeq 220
      // 209: aload 2
      // 20a: bipush 70
      // 20c: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 20f: goto 220
      // 212: aload 6
      // 214: aconst_null
      // 215: aconst_null
      // 216: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEUtilities.getEncodings ()[B
      // 219: bipush 0
      // 21a: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEUtilities.getTextContentType ([BZ)Ljava/lang/String;
      // 21d: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setText (Ljava/lang/Object;Lnet/rim/device/apps/api/transmission/Parameters;Ljava/lang/String;)V
      // 220: aload 2
      // 221: ldc2_w -1943436819741481055
      // 224: new java/lang/Object
      // 227: dup
      // 228: invokespecial net/rim/device/apps/api/transmission/rim/ContentPartIDGenerator.<init> ()V
      // 22b: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 22e: pop
      // 22f: aload 0
      // 230: invokeinterface net/rim/device/api/collection/ReadableList.size ()I 1
      // 235: istore 15
      // 237: bipush 0
      // 238: istore 16
      // 23a: iload 16
      // 23c: iload 15
      // 23e: if_icmplt 244
      // 241: goto 2c3
      // 244: aload 0
      // 245: iload 16
      // 247: invokeinterface net/rim/device/api/collection/ReadableList.getAt (I)Ljava/lang/Object; 2
      // 24c: checkcast java/lang/Object
      // 24f: astore 17
      // 251: aload 17
      // 253: instanceof java/lang/Object
      // 256: ifeq 2bd
      // 259: iload 3
      // 25a: ifne 2ab
      // 25d: aload 17
      // 25f: dup
      // 260: instanceof net/rim/device/apps/internal/blackberryemail/email/EmailPayloadModel
      // 263: ifne 26a
      // 266: pop
      // 267: goto 2ab
      // 26a: checkcast net/rim/device/apps/internal/blackberryemail/email/EmailPayloadModel
      // 26d: astore 18
      // 26f: aload 18
      // 271: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailPayloadModel.getCMIMEReferenceIdentifier ()I 1
      // 276: istore 19
      // 278: ldc2_w -4420850319371185992
      // 27b: iload 19
      // 27d: invokestatic net/rim/device/apps/api/messaging/MessageLookups.get (JI)Ljava/lang/Object;
      // 280: checkcast net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel
      // 283: astore 20
      // 285: aload 20
      // 287: ifnull 2a5
      // 28a: aload 20
      // 28c: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getFolderId ()J 1
      // 291: invokestatic net/rim/device/apps/internal/blackberryemail/folder/EmailHierarchy.getEmailHierarchyForFolder (J)Lnet/rim/device/apps/internal/blackberryemail/folder/EmailHierarchy;
      // 294: lload 12
      // 296: invokestatic net/rim/device/apps/internal/blackberryemail/folder/EmailHierarchy.getEmailHierarchyForFolder (J)Lnet/rim/device/apps/internal/blackberryemail/folder/EmailHierarchy;
      // 299: if_acmpeq 2ab
      // 29c: aload 2
      // 29d: bipush 70
      // 29f: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 2a2: goto 2ab
      // 2a5: aload 2
      // 2a6: bipush 70
      // 2a8: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 2ab: aload 17
      // 2ad: checkcast java/lang/Object
      // 2b0: astore 18
      // 2b2: aload 18
      // 2b4: aload 2
      // 2b5: aload 6
      // 2b7: invokeinterface net/rim/device/apps/api/framework/model/ConversionProvider.convert (Ljava/lang/Object;Ljava/lang/Object;)Z 3
      // 2bc: pop
      // 2bd: iinc 16 1
      // 2c0: goto 23a
      // 2c3: aload 6
      // 2c5: aload 0
      // 2c6: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getCMIMEReferenceIdentifier ()I 1
      // 2cb: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setReferenceIdentifier (I)V
      // 2ce: iload 3
      // 2cf: ifeq 311
      // 2d2: aload 2
      // 2d3: ldc2_w -8804918554992595454
      // 2d6: invokevirtual net/rim/device/api/util/LongHashtable.get (J)Ljava/lang/Object;
      // 2d9: checkcast java/lang/Object
      // 2dc: astore 16
      // 2de: new java/lang/Object
      // 2e1: dup
      // 2e2: invokespecial net/rim/device/cldc/io/gme/GMEAddress.<init> ()V
      // 2e5: astore 17
      // 2e7: aload 17
      // 2e9: aload 16
      // 2eb: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2ee: invokevirtual net/rim/device/cldc/io/gme/GMEAddress.setAddress (Ljava/lang/String;)V
      // 2f1: aload 2
      // 2f2: ldc2_w -7981905408958106750
      // 2f5: aload 17
      // 2f7: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 2fa: pop
      // 2fb: aload 2
      // 2fc: ldc2_w -5971550291443523639
      // 2ff: aload 16
      // 301: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 304: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 307: pop
      // 308: aload 2
      // 309: bipush 75
      // 30b: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 30e: goto 317
      // 311: aload 2
      // 312: bipush 75
      // 314: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.clearFlag (I)V
      // 317: aload 6
      // 319: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.getFrom ()[[Ljava/lang/String;
      // 31c: ifnonnull 32a
      // 31f: aload 6
      // 321: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailSendUtility.getDevicePINString ()Ljava/lang/String;
      // 324: invokestatic net/rim/device/internal/deviceoptions/Owner.getOwnerName ()Ljava/lang/String;
      // 327: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.addFrom (Ljava/lang/String;Ljava/lang/String;)V
      // 32a: iload 3
      // 32b: ifeq 372
      // 32e: aload 6
      // 330: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.isEncoded ()Z
      // 333: ifeq 372
      // 336: aload 6
      // 338: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.getMessageParameters ()Lnet/rim/device/apps/api/transmission/Parameters;
      // 33b: astore 16
      // 33d: aload 16
      // 33f: ifnull 359
      // 342: aload 16
      // 344: bipush -120
      // 346: invokevirtual net/rim/device/apps/api/transmission/Parameters.has (B)Z
      // 349: ifeq 359
      // 34c: aload 6
      // 34e: ldc_w "Unsupported encoding"
      // 351: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setSubject (Ljava/lang/Object;)V
      // 354: goto 359
      // 357: astore 17
      // 359: aload 16
      // 35b: ifnull 372
      // 35e: aload 16
      // 360: bipush -127
      // 362: invokevirtual net/rim/device/apps/api/transmission/Parameters.has (B)Z
      // 365: ifeq 372
      // 368: aload 6
      // 36a: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailSendUtility.getDevicePINString ()Ljava/lang/String;
      // 36d: aconst_null
      // 36e: bipush 0
      // 36f: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.addFrom (Ljava/lang/String;Ljava/lang/String;Z)V
      // 372: aload 6
      // 374: invokestatic java/lang/System.currentTimeMillis ()J
      // 377: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setDate (J)V
      // 37a: aload 2
      // 37b: ldc2_w -6581931217101110672
      // 37e: invokestatic net/rim/device/apps/api/framework/model/ContextObject.remove (Ljava/lang/Object;J)Ljava/lang/Object;
      // 381: pop
      // 382: aload 5
      // 384: ldc_w "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE"
      // 387: aload 6
      // 389: aconst_null
      // 38a: aload 0
      // 38b: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getGMEReferenceIdentifier ()I 1
      // 390: aload 2
      // 391: invokeinterface net/rim/device/apps/api/transmission/TransmissionService.transmitObject (Ljava/lang/String;Ljava/lang/Object;Lnet/rim/device/apps/api/transmission/TransmissionStatusListener;ILjava/lang/Object;)V 6
      // 396: return
      // 397: astore 16
      // 399: return
      // 39a: astore 16
      // 39c: return
      // try (183 -> 189): 190 null
      // try (338 -> 341): 342 null
      // try (361 -> 369): 370 null
      // try (361 -> 369): 372 null
   }

   public static final void cancelTransportMessage(EmailMessageModel message, Object context) {
      TransmissionService transmissionService = TransmissionServiceManager.get(8399767144006445082L);
      if (transmissionService != null) {
         transmissionService.cancelTransmitObject(message.getGMEReferenceIdentifier(), context);
      }
   }

   public static final void assignGMEReferenceIdentifierToMessage(EmailMessageModel message, int newGMEReferenceIdentifier, Object context) {
      int oldGMEReferenceIdentifier = message.getGMEReferenceIdentifier();
      if (oldGMEReferenceIdentifier != newGMEReferenceIdentifier) {
         MessageLookups.remove(431630751329425149L, oldGMEReferenceIdentifier);
         message.setGMEReferenceIdentifier(newGMEReferenceIdentifier);
      }
   }

   public static final EmailHierarchy getActiveEmailHierarchy() {
      RIMMessagingService transmissionService = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
      if (transmissionService != null) {
         ServiceRecord sr = transmissionService.getOutgoingServiceRecord();
         if (sr != null) {
            Folder hierarchy = FolderUtil.getHierarchyByService(sr);
            if (hierarchy != null) {
               return (UnknownMimePartModel)hierarchy;
            }
         }
      }

      return (UnknownMimePartModel)FolderUtil.getActiveFolderHierarchy("CMIME");
   }

   public static final long assignFolderToMessage(EmailMessageModel message, Object context) {
      boolean isPin = message.flagsSet(8192);
      long outbox = message.getFolderId();
      if (outbox != 0) {
         EmailHierarchy hierarchyToUse = EmailHierarchy.getEmailHierarchyForFolder(outbox);
         ServiceRecord serviceRecord = (ServiceRecord)ContextObject.get(context, -6095803566992128485L);
         if (serviceRecord != null) {
            Folder newSendHierarchy = FolderUtil.getHierarchyByService(serviceRecord);
            if (newSendHierarchy instanceof UnknownMimePartModel && newSendHierarchy != hierarchyToUse) {
               long newOutbox = ((UnknownMimePartModel)newSendHierarchy).getSentFolder();
               if (outbox == hierarchyToUse.getOrphanedSavedFolder().getLUID()) {
                  newOutbox = ((UnknownMimePartModel)newSendHierarchy).getOrphanedSavedFolder().getLUID();
               }

               EmailHierarchy.removeMessage(message, outbox);
               outbox = newOutbox;
            }
         }
      } else {
         if (!isPin) {
            ServiceRecord serviceRecord = (ServiceRecord)ContextObject.get(context, -6095803566992128485L);
            if (serviceRecord != null) {
               Folder hierarchy = FolderUtil.getHierarchyByService(serviceRecord);
               if (hierarchy instanceof UnknownMimePartModel) {
                  outbox = ((UnknownMimePartModel)hierarchy).getSentFolder();
               }
            }
         }

         if (outbox == 0) {
            EmailHierarchy hierarchy = null;
            if (ContextObject.getFlag(context, 13)
               || ContextObject.getFlag(context, 12)
               || ContextObject.getFlag(context, 53)
               || ContextObject.getFlag(context, 29)
               || ContextObject.getFlag(context, 30)) {
               Object originalMessage = ContextObject.get(context, 245);
               if (originalMessage instanceof EmailMessageModelImpl) {
                  hierarchy = EmailHierarchy.getEmailHierarchyForFolder(((EmailMessageModelImpl)originalMessage).getFolderId());
               }
            }

            if (hierarchy == null) {
               hierarchy = EmailHierarchy.getAnonymousEmailHierarchy();
            }

            return hierarchy.getSentFolder();
         }
      }

      return outbox;
   }

   public static final void fileOutgoingMessageInFolder(EmailMessageModel message, long outbox, Object context) {
      EmailHierarchy.addMessage(message, outbox);
   }

   public static final boolean allAddressesResolved(RIMModel item) {
      if (!(item instanceof Object)) {
         return true;
      }

      ReadableList list = (ReadableList)item;
      int resolvedCount = 0;
      int unresolvedCount = 0;
      LargeAttachmentModel$LargeCachedAttachmentModel[] unresolved = new LargeAttachmentModel$LargeCachedAttachmentModel[list.size()];

      for (int cannotContinueText = list.size() - 1; cannotContinueText >= 0; cannotContinueText--) {
         RIMModel model = (RIMModel)list.getAt(cannotContinueText);
         if (model instanceof LargeAttachmentModel$LargeCachedAttachmentModel) {
            EmailHeaderModel ehm = (LargeAttachmentModel$LargeCachedAttachmentModel)model;
            RIMModel insideModel = ehm.getInsideModel();
            resolvedCount++;
            if (insideModel instanceof Object) {
               ResolvedStatusProvider rsp = (ResolvedStatusProvider)insideModel;
               if (!rsp.isResolved()) {
                  resolvedCount--;
                  unresolved[unresolvedCount++] = ehm;
               }
            }
         }
      }

      if (unresolvedCount == 0) {
         return true;
      }

      if (resolvedCount != 0 && item instanceof Object) {
         WritableSet wl = (WritableSet)item;
         String continueText = EmailResources.getString(1120);
         if (Dialog.ask(3, continueText, -1) == 4) {
            for (int i = 0; i < unresolvedCount; i++) {
               wl.remove(unresolved[i]);
            }

            return true;
         }
      } else {
         String cannotContinueText = EmailResources.getString(1121);
         Dialog.ask(0, cannotContinueText);
      }

      return false;
   }

   public static final ServiceRecord getSyncSR(String id) {
      ServiceRecord sr = null;

      label26:
      try {
         sr = ServiceBook.getSB().getRecordByCidAndUserId("SYNC", Integer.parseInt(id));
      } finally {
         break label26;
      }

      if (sr == null) {
         sr = ServiceBook.getSB().getRecordByCidAndDataSourceId("SYNC", id);
      }

      return sr;
   }

   public static final void sendDuressMessage() {
      new EmailSendUtility$DuressNotification().run();
   }

   private static final void appendServiceInfo(StringBuffer sb, ServiceRecord sr) {
      sb.append(" NAME=");
      sb.append(sr.getName());
      sb.append(" UID=");
      sb.append(sr.getUid());
      sb.append(" USERID=");
      sb.append(sr.getUserId());
   }

   public static final EmailMessageModel sendMessage(EmailMessageModel message, ServiceRecord serviceRecord, Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      contextObject.setFlag(43);
      boolean isPin = message.flagsSet(8192);
      if (isPin) {
         contextObject.setFlag(94);
      } else {
         contextObject.clearFlag(94);
      }

      if (!isPin) {
         ContextObject.remove(contextObject, -5971550291443523639L);
         ContextObject.put(contextObject, -6095803566992128485L, serviceRecord);
      }

      EmailPayloadModel oldPayload = EmailModifier.beginChanges(message, contextObject);
      assignCMIMEReferenceIdentifierToMessage(message, contextObject);
      assignGMEReferenceIdentifierToMessage(message, contextObject);
      synchronized (RIMPersistentStore.getSynchObject()) {
         synchronized (FolderHierarchies.getLockObject()) {
            MessageLookups.put(-4420850319371185992L, message.getCMIMEReferenceIdentifier(), message);
            MessageLookups.put(431630751329425149L, message.getGMEReferenceIdentifier(), message);
            long folderId = ((FolderProvider)message).getFolderId();
            if (folderId != 0) {
               adjustProxyRefCounts(message, 1);
            }

            message.changeStatus(0, 0, 134217727, 0, false, false, false, false, contextObject);
            long folderToPutMessageIn = assignFolderToMessage(message, contextObject);
            message.setFolderId(folderToPutMessageIn);
            if (serviceRecord != null && serviceRecord.isSecureService()) {
               message.setFlags(16777216);
            }

            if (serviceRecord != null && serviceRecord.isEncrypted()) {
               message.setFlags(32768);
            }

            if (isPin && CryptoBlock.isKeyPresent("SCRAMBLE_CURRENT")) {
               message.setFlags(32768);
            }

            boolean serviceSupportsNativeAttachments = NativeAttachmentRequestProcessor$Helper.validateServiceSupportsMessageForLargeAttachments(
                  serviceRecord, NativeAttachmentRequestProcessor$Helper.getNativeAttachments(message), 0
               )
               == 0;

            for (int i = message.size() - 1; i >= 0; i--) {
               RIMModel currentModel = (RIMModel)message.getAt(i);
               if (currentModel instanceof RemoveWhenSendingModel) {
                  RemoveWhenSendingModel rwsm = (RemoveWhenSendingModel)currentModel;
                  if (rwsm.removeBeforeSending()) {
                     message.remove(currentModel);
                     continue;
                  }
               }

               if (currentModel instanceof LargeAttachmentModel && !serviceSupportsNativeAttachments) {
                  message.remove(currentModel);
               }
            }

            if (!exclude(message)) {
               transportMessage(message, contextObject);
            }

            for (int i = message.size() - 1; i >= 0; i--) {
               RIMModel currentModel = (RIMModel)message.getAt(i);
               if (currentModel instanceof RemoveWhenSendingModel) {
                  RemoveWhenSendingModel rwsm = (RemoveWhenSendingModel)currentModel;
                  if (rwsm.removeAfterSending()) {
                     message.remove(currentModel);
                  }
               }
            }

            EmailModifier.endChanges(message, oldPayload, contextObject);
            fileOutgoingMessageInFolder(message, folderToPutMessageIn, contextObject);
            if (folderId != 0) {
               adjustProxyRefCounts(message, -1);
            }
         }

         return message;
      }
   }

   private static final void adjustProxyRefCounts(EmailMessageModel message, int adjustment) {
      for (int i = message.size() - 1; i >= 0; i--) {
         RIMModel model = (RIMModel)message.getAt(i);
         if (model instanceof ProxyModel) {
            ProxyModel proxyM = (ProxyModel)model;
            PersistentProxyModelStore proxyModelStore = PersistentProxyModelStore.getInstance(proxyM.getRootId(), true);
            if (proxyModelStore != null) {
               proxyModelStore.refCount(proxyM.getObjectHandle(), adjustment);
            }
         }
      }
   }

   public static final String getDevicePINString() {
      int pin = DeviceInfo.getDeviceId();
      return StringUtilities.toUpperCase(Integer.toHexString(pin), 1701707776);
   }

   private static final void expandGroupAddresses(EmailMessageModel message) {
      EmailHeaderModel[] groupAddresses = null;
      boolean isPin = message.flagsSet(8192);
      int size = message.size();

      for (int i = 0; i < size; i++) {
         RIMModel model = (RIMModel)message.getAt(i);
         if (model instanceof LargeAttachmentModel$LargeCachedAttachmentModel) {
            EmailHeaderModel header = (LargeAttachmentModel$LargeCachedAttachmentModel)model;
            RIMModel insideModel = header.getInsideModel();
            if (insideModel instanceof Object) {
               if (groupAddresses == null) {
                  groupAddresses = new LargeAttachmentModel$LargeCachedAttachmentModel[0];
               }

               Arrays.add(groupAddresses, header);
            }
         }
      }

      if (groupAddresses != null) {
         for (EmailHeaderModel header : groupAddresses) {
            GroupAddressCardModel gacm = (GroupAddressCardModel)header.getInsideModel();
            ContextObject emailHeaderContext = (ContextObject)(new Object());
            int gacmSize = gacm.size();

            for (int j = 0; j < gacmSize; j++) {
               if (gacm.getAddressModelTypeAt(j) == (isPin ? 1 : 0)) {
                  RIMModel rm = gacm.getAddressModelAt(j);
                  if (rm != null && rm instanceof Object && !isEmailAddressPresentInMessage(((FriendlyNameAddressModel)rm).getAddress(), message)) {
                     emailHeaderContext.put(254, rm);
                     message.add(EmailHeaderModelFactory.createInstance(header.getHeaderType(), emailHeaderContext));
                  }
               }
            }

            message.remove(header);
         }
      }
   }

   private static final boolean isEmailAddressPresentInMessage(String address, EmailMessageModel message) {
      if (address == null) {
         return true;
      }

      for (int i = message.size() - 1; i >= 0; i--) {
         RIMModel model = (RIMModel)message.getAt(i);
         if (model instanceof LargeAttachmentModel$LargeCachedAttachmentModel) {
            model = ((LargeAttachmentModel$LargeCachedAttachmentModel)model).getInsideModel();
         }

         if (model instanceof Object) {
            String address2 = ((FriendlyNameAddressModel)model).getAddress();
            if (address2 != null && StringUtilities.compareToIgnoreCase(address, address2, 1701707776) == 0) {
               return true;
            }
         }
      }

      return false;
   }

   public static final int sortSendMethods(String[] sendMethodDescriptions, SendMethod[] sendMethods, int defaultSendMethodIndex) {
      SendMethod defaultSendMethod = sendMethods[defaultSendMethodIndex];
      Arrays.sort(sendMethodDescriptions, 0, sendMethodDescriptions.length, sendMethods, new EmailSendUtility$StringComparator());

      for (int i = 0; i < sendMethods.length; i++) {
         if (defaultSendMethod == sendMethods[i]) {
            defaultSendMethodIndex = i;
         }
      }

      return defaultSendMethodIndex;
   }

   public static final EmailMessageModel send(String to, String subject, String body) {
      EmailMessageModel msg = (EmailMessageModel)FactoryUtil.createInstance(-6822293833372928884L, null);
      String[] names = new Object[2];
      names[0] = to;
      names[1] = to;
      ContextObject context = (ContextObject)(new Object());
      ContextObject.put(context, 251, names);
      Object recipient = FactoryUtil.createInstance(-2985347935260258684L, context);
      EmailBuilderApi.addRecipient(msg, 0, (RIMModel)recipient);
      EmailBuilderApi.addSubjectLine(msg, subject);
      EmailBuilderApi.addMessageBody(msg, body);
      msg.setType((byte)32);
      RIMMessagingService service = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
      if (service == null) {
         return null;
      }

      ServiceRecord sr = service.getOutgoingServiceRecord();
      if (sr == null) {
         return null;
      }

      context.reset();
      sendMessage(msg, sr, new Object());
      return msg;
   }

   private static final void sendDuressNotification() {
      String email = ITPolicy.getString(22, 7);
      if (email != null) {
         ContextObject context = (ContextObject)(new Object());
         String[] addr = new Object[2];
         addr[0] = email;
         addr[1] = EmailResources.getString(152);
         ContextObject.put(context, 251, addr);
         RIMModel eam = (RIMModel)FactoryUtil.createInstance(-2985347935260258684L, context);
         EmailMessageModel emm = (EmailMessageModel)FactoryUtil.createInstance(-6822293833372928884L, null);
         TransmissionService transmissionService = TransmissionServiceManager.get(8399767144006445082L);
         if (transmissionService instanceof Object) {
            ServiceRecord serviceRecord = ((RIMMessagingService)transmissionService).getOutgoingServiceRecord();
            if (serviceRecord != null) {
               EmailBuilderApi.addSubjectLine(emm, EmailResources.getString(154));
               EmailBuilderApi.addRecipient(emm, 0, eam);
               EmailBuilderApi.addMessageBody(emm, getDuressInfo(serviceRecord));
               if (serviceRecord.isSecureService()) {
                  emm.setFlags(16777216);
               }

               if (serviceRecord.isEncrypted()) {
                  emm.setFlags(32768);
               }
            }
         }

         emm.setType((byte)32);
         context.setFlag(43);
         assignCMIMEReferenceIdentifierToMessage(emm, context);
         assignGMEReferenceIdentifierToMessage(emm, context);
         UiInternal.setRadioIconsVisible(false);
         transportMessage(emm, context);
      }
   }

   private static final String getDuressInfo(ServiceRecord sr) {
      DateFormat date = DateFormat.getInstance(54);
      return ((StringBuffer)(new Object("Name: ")))
         .append(Owner.getOwnerName())
         .append('\n')
         .append("Email: ")
         .append(CMIMEUtilities.getEmailAddress(sr))
         .append('\n')
         .append("PIN: ")
         .append(StringUtilities.toUpperCase(Integer.toHexString(DeviceInfo.getDeviceId()), 1701707776))
         .append('\n')
         .append("Time: ")
         .append(date.formatLocal(System.currentTimeMillis()))
         .append('\n')
         .toString();
   }

   private static final Vector getSendListeners() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Vector sendListeners = (Vector)ar.getOrWaitFor(922368635746834671L);
      if (sendListeners == null) {
         sendListeners = (Vector)(new Object(1));
         ar.put(922368635746834671L, sendListeners);
      }

      return sendListeners;
   }

   public static final synchronized void addSendListeners(EmailSendListener l) {
      Vector sendListeners = getSendListeners();
      sendListeners.addElement(l);
   }

   public static final synchronized void removeSendListener(EmailSendListener l) {
      Vector sendListeners = getSendListeners();
      sendListeners.removeElement(l);
   }

   private static final synchronized boolean exclude(EmailMessageModel m) {
      Vector sendListeners = getSendListeners();
      synchronized (FolderHierarchies.getLockObject()) {
         for (int i = sendListeners.size() - 1; i >= 0; i--) {
            EmailSendListener l = (EmailSendListener)sendListeners.elementAt(i);
            if (!l.onSend(m)) {
               return true;
            }
         }

         return false;
      }
   }
}
