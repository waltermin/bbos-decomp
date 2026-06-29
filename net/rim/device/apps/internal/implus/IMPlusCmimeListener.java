package net.rim.device.apps.internal.implus;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbCombinerRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.messaging.implus.IMPlusComposeModel;
import net.rim.device.apps.api.messaging.implus.IMPlusServiceModel;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailModifier;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;
import net.rim.vm.Array;

final class IMPlusCmimeListener implements IMPlusServiceModel, GlobalEventListener {
   private IMPlusCmimeListener$ReceiptCapableService[] _serviceArray;
   public Verb _composeIMPlusVerb = null;
   public IMPlusAddressModelFactory _interactiveHHFactory;
   private Verb _interactiveHHUseOnceVerb;
   public IMPlusAddressModelFactory _oneWayPageFactory;
   private Verb _oneWayPageUseOnceVerb;
   public RIMModelFactory _phoneFactory;
   private Verb _phoneUseOnceVerb;
   private IMPlusPhoneComposeVerb _phoneComposeVerb;
   private IMPlusPhoneComposeVerbCombiner _phoneCombiner;
   public RIMModelFactory _faxFactory;
   private Verb _faxUseOnceVerb;
   private static final long IMPLUSCMIMELISTENER_ID;
   private static final String CMIME_CID;
   private static final int DSN_MESSAGE_FAILED;
   private static final byte RECOGNIZED_ADDRESS_TYPES_TAG;
   private static final byte DEVICE_CAPABILITIES_TAG;
   private static final byte SERVICE_CAPABILITIES_TAG;
   private static final byte SEND_VERBOSE_DSN_RESPONSE;
   private static final byte ENABLE_CONFIRM_DELIVERY_UI;
   private static final byte DEFAULT_CONFIRM_DELIVERY;
   private static final byte ENABLE_CONFIRM_READ_UI;
   private static final byte DEFAULT_CONFIRM_READ;
   private static final byte ALLOW_READ_CONFIRM_UI;
   private static final byte DEFAULT_ALLOW_READ_CONFIRM;

   public final IMPlusCmimeListener$ReceiptCapableService findReceiptCapableService(int serviceRecId) {
      for (int i = this._serviceArray.length - 1; i >= 0; i--) {
         if (serviceRecId == this._serviceArray[i]._serviceRecId) {
            return this._serviceArray[i];
         }
      }

      return null;
   }

   public final void init() {
      ServiceRecord[] cmimeServiceRecs = ServiceBook.getSB().findRecordsByCid("CMIME");

      for (int i = cmimeServiceRecs.length - 1; i >= 0; i--) {
         this.processAppData(cmimeServiceRecs[i]);
      }
   }

   public final IMPlusCmimeListener$ReceiptCapableService getIMPlusReceiptCapableService() {
      for (int i = 0; i < this._serviceArray.length; i++) {
         if (this._serviceArray[i]._twoWayPageEnabled
            || this._serviceArray[i]._oneWayPageEnabled
            || this._serviceArray[i]._phoneEnabled
            || this._serviceArray[i]._faxEnabled) {
            return this._serviceArray[i];
         }
      }

      return null;
   }

   @Override
   public final Object getIMPlusServiceRecord() {
      IMPlusCmimeListener$ReceiptCapableService receiptCapableService = this.getIMPlusReceiptCapableService();
      return receiptCapableService != null ? ServiceBook.getSB().getRecordById(receiptCapableService._serviceRecId) : null;
   }

   @Override
   public final boolean getMethodEnabled(int serviceRecId, byte method) {
      IMPlusCmimeListener$ReceiptCapableService receiptCapableService = this.findReceiptCapableService(serviceRecId);
      if (receiptCapableService == null) {
         return false;
      }

      boolean returnVal = false;
      switch (method) {
         case -1:
            break;
         case 0:
         default:
            returnVal = receiptCapableService._phoneEnabled;
            break;
         case 1:
            returnVal = receiptCapableService._faxEnabled;
            break;
         case 2:
            returnVal = receiptCapableService._oneWayPageEnabled;
            break;
         case 3:
            returnVal = receiptCapableService._twoWayPageEnabled;
      }

      return returnVal;
   }

   @Override
   public final int[] getReceiptCapableServiceRecIds() {
      int[] resultArray = new int[this._serviceArray.length];

      for (int i = 0; i < this._serviceArray.length; i++) {
         resultArray[i] = this._serviceArray[i]._serviceRecId;
      }

      return resultArray;
   }

   @Override
   public final String getServiceName(int serviceRecId) {
      ServiceRecord serviceRec = ServiceBook.getSB().getRecordById(serviceRecId);
      return serviceRec != null ? serviceRec.getName() : null;
   }

   @Override
   public final byte getAckRequestHeaderByte(RIMModel emailMessage) {
      byte dsnRequestByte = 0;
      if (!(emailMessage instanceof Object)) {
         return dsnRequestByte;
      }

      EmailMessageModel emailMsgModel = (EmailMessageModel)emailMessage;
      ServiceRecord cmimeService = emailMsgModel.getServiceRecordForMessage();
      if (cmimeService == null) {
         return dsnRequestByte;
      }

      IMPlusCmimeListener$ReceiptCapableService receiptCapableService = this.findReceiptCapableService(cmimeService.getId());
      if (receiptCapableService == null) {
         return dsnRequestByte;
      }

      boolean useDefault = !emailMsgModel.flagsSet(4194304);
      byte savedDSNSettings = MessageListOptions.getOptions().getDSNSettings(cmimeService.getName(), cmimeService.getUid());
      boolean confirmDeliveryFlag = false;
      if (useDefault) {
         confirmDeliveryFlag = savedDSNSettings != 0 && receiptCapableService._enableConfirmDeliveryUI
            ? (byte)(2 & savedDSNSettings) != 0
            : receiptCapableService._defaultConfirmDelivery;
      } else if (emailMsgModel.flagsSet(2097152)) {
         confirmDeliveryFlag = true;
      }

      if (confirmDeliveryFlag) {
         dsnRequestByte = (byte)(dsnRequestByte | 1);
      }

      boolean confirmReadFlag = false;
      if (useDefault) {
         confirmReadFlag = savedDSNSettings != 0 && receiptCapableService._enableConfirmReadUI
            ? (byte)(4 & savedDSNSettings) != 0
            : receiptCapableService._defaultConfirmRead;
      } else if (emailMsgModel.flagsSet(64)) {
         confirmReadFlag = true;
      }

      if (confirmReadFlag) {
         dsnRequestByte = (byte)(dsnRequestByte | 2);
      }

      return dsnRequestByte;
   }

   @Override
   public final void sendReceipt(RIMModel param1, boolean param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: instanceof java/lang/Object
      // 004: ifne 008
      // 007: return
      // 008: aload 1
      // 009: checkcast java/lang/Object
      // 00c: astore 3
      // 00d: aload 3
      // 00e: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getCMIMEReferenceIdentifier ()I 1
      // 013: istore 4
      // 015: new java/lang/Object
      // 018: dup
      // 019: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingOutgoingReceipt.<init> ()V
      // 01c: astore 5
      // 01e: aload 3
      // 01f: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getServiceRecordForMessage ()Lnet/rim/device/api/servicebook/ServiceRecord; 1
      // 024: astore 6
      // 026: aconst_null
      // 027: astore 7
      // 029: aload 6
      // 02b: ifnull 03d
      // 02e: aload 0
      // 02f: aload 6
      // 031: invokevirtual net/rim/device/api/servicebook/ServiceRecord.getId ()I
      // 034: invokevirtual net/rim/device/apps/internal/implus/IMPlusCmimeListener.findReceiptCapableService (I)Lnet/rim/device/apps/internal/implus/IMPlusCmimeListener$ReceiptCapableService;
      // 037: dup
      // 038: astore 7
      // 03a: ifnonnull 03e
      // 03d: return
      // 03e: aload 3
      // 03f: sipush 8192
      // 042: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.flagsSet (I)Z 2
      // 047: istore 8
      // 049: iload 8
      // 04b: ifeq 04f
      // 04e: return
      // 04f: iload 2
      // 050: ifne 056
      // 053: goto 13c
      // 056: invokestatic net/rim/device/api/system/ApplicationRegistry.getApplicationRegistry ()Lnet/rim/device/api/system/ApplicationRegistry;
      // 059: ldc2_w 2620647646956286337
      // 05c: invokevirtual net/rim/device/api/system/ApplicationRegistry.get (J)Ljava/lang/Object;
      // 05f: checkcast java/lang/Object
      // 062: astore 9
      // 064: new java/lang/Object
      // 067: dup
      // 068: bipush 73
      // 06a: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> (I)V
      // 06d: astore 10
      // 06f: aload 10
      // 071: aload 7
      // 073: getfield net/rim/device/apps/internal/implus/IMPlusCmimeListener$ReceiptCapableService._serviceRecId I
      // 076: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.putIntegerData (I)V
      // 079: aload 9
      // 07b: aload 10
      // 07d: invokeinterface net/rim/device/api/util/Factory.createInstance (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 082: checkcast net/rim/device/apps/internal/implus/IMPlusRequestOptionModelImpl
      // 085: astore 11
      // 087: aload 11
      // 089: invokevirtual net/rim/device/apps/internal/implus/IMPlusRequestOptionModelImpl.getPreference ()I
      // 08c: istore 12
      // 08e: aload 7
      // 090: getfield net/rim/device/apps/internal/implus/IMPlusCmimeListener$ReceiptCapableService._allowReadConfirmUI Z
      // 093: ifne 099
      // 096: goto 13c
      // 099: iload 12
      // 09b: ifne 0b0
      // 09e: ldc2_w 4530015158237739359
      // 0a1: iload 4
      // 0a3: invokestatic net/rim/device/apps/api/messaging/MessageLookups.remove (JI)Ljava/lang/Object;
      // 0a6: pop
      // 0a7: aload 3
      // 0a8: bipush 64
      // 0aa: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.clearFlags (I)V 2
      // 0af: return
      // 0b0: iload 12
      // 0b2: bipush 2
      // 0b4: if_icmpeq 0ba
      // 0b7: goto 13c
      // 0ba: new net/rim/device/apps/internal/implus/IMPlusCmimeListener$PromptDialog
      // 0bd: dup
      // 0be: invokespecial net/rim/device/apps/internal/implus/IMPlusCmimeListener$PromptDialog.<init> ()V
      // 0c1: astore 13
      // 0c3: aload 13
      // 0c5: invokevirtual net/rim/device/api/ui/component/Dialog.doModal ()I
      // 0c8: istore 14
      // 0ca: aload 13
      // 0cc: getfield net/rim/device/apps/internal/implus/IMPlusCmimeListener$PromptDialog._savePreference Lnet/rim/device/api/ui/component/CheckboxField;
      // 0cf: invokevirtual net/rim/device/api/ui/component/CheckboxField.getChecked ()Z
      // 0d2: ifeq 123
      // 0d5: invokestatic net/rim/device/apps/api/messaging/messagelist/MessageListOptions.getOptions ()Lnet/rim/device/apps/api/messaging/messagelist/MessageListOptions;
      // 0d8: astore 15
      // 0da: aload 15
      // 0dc: aload 6
      // 0de: invokevirtual net/rim/device/api/servicebook/ServiceRecord.getName ()Ljava/lang/String;
      // 0e1: aload 6
      // 0e3: invokevirtual net/rim/device/api/servicebook/ServiceRecord.getUid ()Ljava/lang/String;
      // 0e6: invokevirtual net/rim/device/apps/api/messaging/messagelist/MessageListOptions.getDSNSettings (Ljava/lang/String;Ljava/lang/String;)B
      // 0e9: istore 16
      // 0eb: iload 16
      // 0ed: bipush -17
      // 0ef: iand
      // 0f0: i2b
      // 0f1: istore 16
      // 0f3: iload 14
      // 0f5: bipush 4
      // 0f7: if_icmpne 105
      // 0fa: iload 16
      // 0fc: bipush 8
      // 0fe: ior
      // 0ff: i2b
      // 100: istore 16
      // 102: goto 10d
      // 105: iload 16
      // 107: bipush 8
      // 109: iand
      // 10a: i2b
      // 10b: istore 16
      // 10d: aload 15
      // 10f: aload 6
      // 111: invokevirtual net/rim/device/api/servicebook/ServiceRecord.getName ()Ljava/lang/String;
      // 114: aload 6
      // 116: invokevirtual net/rim/device/api/servicebook/ServiceRecord.getUid ()Ljava/lang/String;
      // 119: iload 16
      // 11b: invokevirtual net/rim/device/apps/api/messaging/messagelist/MessageListOptions.setDSNSettings (Ljava/lang/String;Ljava/lang/String;B)V
      // 11e: aload 15
      // 120: invokevirtual net/rim/device/apps/api/messaging/messagelist/MessageListOptions.commit ()V
      // 123: iload 14
      // 125: bipush -1
      // 127: if_icmpne 13c
      // 12a: ldc2_w 4530015158237739359
      // 12d: iload 4
      // 12f: invokestatic net/rim/device/apps/api/messaging/MessageLookups.remove (JI)Ljava/lang/Object;
      // 132: pop
      // 133: aload 3
      // 134: bipush 64
      // 136: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.clearFlags (I)V 2
      // 13b: return
      // 13c: ldc2_w 8399767144006445082
      // 13f: invokestatic net/rim/device/apps/api/transmission/TransmissionServiceManager.get (J)Lnet/rim/device/apps/api/transmission/TransmissionService;
      // 142: astore 9
      // 144: aload 9
      // 146: ifnonnull 14c
      // 149: goto 44e
      // 14c: iload 8
      // 14e: ifeq 159
      // 151: invokestatic net/rim/device/apps/internal/blackberryemail/email/api/EmailSendUtility.getDevicePINString ()Ljava/lang/String;
      // 154: astore 10
      // 156: goto 1e3
      // 159: aconst_null
      // 15a: astore 11
      // 15c: bipush 0
      // 15d: istore 13
      // 15f: iload 13
      // 161: aload 3
      // 162: invokeinterface net/rim/device/api/collection/ReadableList.size ()I 1
      // 167: if_icmpge 1d0
      // 16a: aload 3
      // 16b: iload 13
      // 16d: invokeinterface net/rim/device/api/collection/ReadableList.getAt (I)Ljava/lang/Object; 2
      // 172: checkcast java/lang/Object
      // 175: astore 14
      // 177: aload 14
      // 179: dup
      // 17a: instanceof java/lang/Object
      // 17d: ifne 184
      // 180: pop
      // 181: goto 1ca
      // 184: checkcast java/lang/Object
      // 187: astore 15
      // 189: aload 15
      // 18b: invokevirtual net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel.getHeaderType ()I
      // 18e: istore 16
      // 190: iload 16
      // 192: ifeq 1a2
      // 195: iload 16
      // 197: bipush 1
      // 198: if_icmpeq 1a2
      // 19b: iload 16
      // 19d: bipush 2
      // 19f: if_icmpne 1ac
      // 1a2: aload 15
      // 1a4: bipush 8
      // 1a6: invokevirtual net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel.flagsSet (I)Z
      // 1a9: ifne 1b3
      // 1ac: iload 16
      // 1ae: bipush 6
      // 1b0: if_icmpne 1ca
      // 1b3: bipush 2
      // 1b5: anewarray 696
      // 1b8: astore 17
      // 1ba: aload 15
      // 1bc: aload 17
      // 1be: invokevirtual net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel.extractNames ([Ljava/lang/String;)V
      // 1c1: aload 17
      // 1c3: bipush 0
      // 1c4: aaload
      // 1c5: astore 11
      // 1c7: goto 1d0
      // 1ca: iinc 13 1
      // 1cd: goto 15f
      // 1d0: aload 11
      // 1d2: ifnull 1dc
      // 1d5: aload 11
      // 1d7: astore 10
      // 1d9: goto 1e3
      // 1dc: aload 6
      // 1de: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEUtilities.getEmailAddress (Lnet/rim/device/api/servicebook/ServiceRecord;)Ljava/lang/String;
      // 1e1: astore 10
      // 1e3: aload 6
      // 1e5: aload 0
      // 1e6: invokevirtual net/rim/device/apps/internal/implus/IMPlusCmimeListener.getIMPlusServiceRecord ()Ljava/lang/Object;
      // 1e9: if_acmpeq 1f9
      // 1ec: aload 10
      // 1ee: ifnull 1f9
      // 1f1: aload 5
      // 1f3: aload 10
      // 1f5: aconst_null
      // 1f6: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.addFrom (Ljava/lang/String;Ljava/lang/String;)V
      // 1f9: aconst_null
      // 1fa: astore 11
      // 1fc: aload 3
      // 1fd: invokeinterface net/rim/device/api/collection/ReadableList.size ()I 1
      // 202: bipush 1
      // 203: isub
      // 204: istore 12
      // 206: iload 12
      // 208: iflt 24d
      // 20b: aload 3
      // 20c: iload 12
      // 20e: invokeinterface net/rim/device/api/collection/ReadableList.getAt (I)Ljava/lang/Object; 2
      // 213: checkcast java/lang/Object
      // 216: astore 13
      // 218: aload 13
      // 21a: dup
      // 21b: instanceof java/lang/Object
      // 21e: ifne 225
      // 221: pop
      // 222: goto 247
      // 225: checkcast java/lang/Object
      // 228: invokevirtual net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel.getHeaderType ()I
      // 22b: istore 14
      // 22d: iload 14
      // 22f: bipush 4
      // 231: if_icmpeq 240
      // 234: iload 14
      // 236: bipush 3
      // 238: if_icmpne 247
      // 23b: aload 11
      // 23d: ifnonnull 247
      // 240: aload 13
      // 242: checkcast java/lang/Object
      // 245: astore 11
      // 247: iinc 12 -1
      // 24a: goto 206
      // 24d: aload 11
      // 24f: ifnonnull 253
      // 252: return
      // 253: bipush 2
      // 255: anewarray 746
      // 258: astore 12
      // 25a: aload 11
      // 25c: aload 12
      // 25e: invokevirtual net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel.extractNames ([Ljava/lang/String;)V
      // 261: aload 5
      // 263: aload 12
      // 265: bipush 0
      // 266: aaload
      // 267: aload 12
      // 269: bipush 1
      // 26a: aaload
      // 26b: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.addTo (Ljava/lang/String;Ljava/lang/String;)V
      // 26e: aload 7
      // 270: getfield net/rim/device/apps/internal/implus/IMPlusCmimeListener$ReceiptCapableService._sendVerboseDSN Z
      // 273: ifeq 2c0
      // 276: aload 5
      // 278: invokestatic java/lang/System.currentTimeMillis ()J
      // 27b: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setDate (J)V
      // 27e: iload 2
      // 27f: ifeq 28a
      // 282: bipush 9
      // 284: invokestatic net/rim/device/apps/internal/implus/IMPlusResources.getString (I)Ljava/lang/String;
      // 287: goto 28f
      // 28a: bipush 10
      // 28c: invokestatic net/rim/device/apps/internal/implus/IMPlusResources.getString (I)Ljava/lang/String;
      // 28f: astore 13
      // 291: aload 3
      // 292: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getSubject ()Ljava/lang/String; 1
      // 297: astore 14
      // 299: aload 14
      // 29b: ifnull 2b4
      // 29e: new java/lang/Object
      // 2a1: dup
      // 2a2: invokespecial java/lang/StringBuffer.<init> ()V
      // 2a5: aload 13
      // 2a7: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2aa: aload 14
      // 2ac: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2af: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2b2: astore 13
      // 2b4: aload 5
      // 2b6: aload 13
      // 2b8: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setSubject (Ljava/lang/Object;)V
      // 2bb: goto 2c0
      // 2be: astore 15
      // 2c0: aload 7
      // 2c2: getfield net/rim/device/apps/internal/implus/IMPlusCmimeListener$ReceiptCapableService._sendVerboseDSN Z
      // 2c5: ifeq 2e6
      // 2c8: iload 2
      // 2c9: ifeq 2d4
      // 2cc: bipush 11
      // 2ce: invokestatic net/rim/device/apps/internal/implus/IMPlusResources.getString (I)Ljava/lang/String;
      // 2d1: goto 2d9
      // 2d4: bipush 12
      // 2d6: invokestatic net/rim/device/apps/internal/implus/IMPlusResources.getString (I)Ljava/lang/String;
      // 2d9: astore 13
      // 2db: aload 5
      // 2dd: aload 13
      // 2df: aconst_null
      // 2e0: ldc_w "text/plain"
      // 2e3: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setText (Ljava/lang/Object;Lnet/rim/device/apps/api/transmission/Parameters;Ljava/lang/String;)V
      // 2e6: new java/lang/Object
      // 2e9: dup
      // 2ea: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 2ed: astore 13
      // 2ef: aload 13
      // 2f1: bipush -15
      // 2f3: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 2f6: aload 13
      // 2f8: bipush 4
      // 2fa: invokevirtual net/rim/device/api/util/DataBuffer.writeCompressedInt (I)V
      // 2fd: iload 4
      // 2ff: istore 14
      // 301: ldc2_w 4530015158237739359
      // 304: iload 4
      // 306: invokestatic net/rim/device/apps/api/messaging/MessageLookups.get (JI)Ljava/lang/Object;
      // 309: checkcast java/lang/Object
      // 30c: astore 15
      // 30e: aload 15
      // 310: ifnull 327
      // 313: aload 15
      // 315: invokevirtual java/lang/Integer.intValue ()I
      // 318: istore 14
      // 31a: iload 2
      // 31b: ifeq 327
      // 31e: ldc2_w 4530015158237739359
      // 321: iload 4
      // 323: invokestatic net/rim/device/apps/api/messaging/MessageLookups.remove (JI)Ljava/lang/Object;
      // 326: pop
      // 327: aload 13
      // 329: iload 14
      // 32b: invokevirtual net/rim/device/api/util/DataBuffer.writeInt (I)V
      // 32e: aload 13
      // 330: bipush -7
      // 332: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 335: iload 8
      // 337: ifne 343
      // 33a: aload 6
      // 33c: aload 0
      // 33d: invokevirtual net/rim/device/apps/internal/implus/IMPlusCmimeListener.getIMPlusServiceRecord ()Ljava/lang/Object;
      // 340: if_acmpeq 348
      // 343: aload 10
      // 345: ifnonnull 357
      // 348: aload 13
      // 34a: bipush 1
      // 34b: invokevirtual net/rim/device/api/util/DataBuffer.writeCompressedInt (I)V
      // 34e: aload 13
      // 350: bipush 0
      // 351: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 354: goto 361
      // 357: aload 13
      // 359: aload 10
      // 35b: invokevirtual java/lang/String.getBytes ()[B
      // 35e: invokevirtual net/rim/device/api/util/DataBuffer.writeByteArray ([B)V
      // 361: aload 13
      // 363: bipush -6
      // 365: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 368: aload 13
      // 36a: bipush 1
      // 36b: invokevirtual net/rim/device/api/util/DataBuffer.writeCompressedInt (I)V
      // 36e: iload 2
      // 36f: ifeq 37c
      // 372: aload 13
      // 374: bipush 16
      // 376: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 379: goto 383
      // 37c: aload 13
      // 37e: bipush 32
      // 380: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 383: aload 13
      // 385: bipush -5
      // 387: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 38a: aload 13
      // 38c: bipush 1
      // 38d: invokevirtual net/rim/device/api/util/DataBuffer.writeCompressedInt (I)V
      // 390: aload 13
      // 392: bipush 0
      // 393: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 396: aload 13
      // 398: bipush 0
      // 399: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 39c: aload 13
      // 39e: invokevirtual net/rim/device/api/util/DataBuffer.trim ()V
      // 3a1: aload 5
      // 3a3: aload 13
      // 3a5: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 3a8: aconst_null
      // 3a9: ldc_w "message/delivery-status"
      // 3ac: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.addAttachment (Ljava/lang/Object;Lnet/rim/device/apps/api/transmission/Parameters;Ljava/lang/String;)V
      // 3af: aload 5
      // 3b1: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEUtilities.newDeviceSideIdentifier ()I
      // 3b4: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.setReferenceIdentifier (I)V
      // 3b7: new java/lang/Object
      // 3ba: dup
      // 3bb: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 3be: astore 16
      // 3c0: iload 8
      // 3c2: ifeq 40f
      // 3c5: aload 16
      // 3c7: bipush 94
      // 3c9: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 3cc: new java/lang/Object
      // 3cf: dup
      // 3d0: invokespecial net/rim/device/cldc/io/gme/GMEAddress.<init> ()V
      // 3d3: astore 17
      // 3d5: new java/lang/Object
      // 3d8: dup
      // 3d9: ldc_w "CMIME:"
      // 3dc: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 3df: astore 18
      // 3e1: aload 18
      // 3e3: aload 10
      // 3e5: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 3e8: pop
      // 3e9: aload 17
      // 3eb: aload 18
      // 3ed: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 3f0: invokevirtual net/rim/device/cldc/io/gme/GMEAddress.setAddress (Ljava/lang/String;)V
      // 3f3: aload 16
      // 3f5: ldc2_w -7981905408958106750
      // 3f8: aload 17
      // 3fa: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 3fd: pop
      // 3fe: aload 16
      // 400: ldc2_w -5971550291443523639
      // 403: aload 18
      // 405: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 408: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 40b: pop
      // 40c: goto 41a
      // 40f: aload 16
      // 411: ldc2_w -6095803566992128485
      // 414: aload 6
      // 416: invokestatic net/rim/device/apps/api/framework/model/ContextObject.put (Ljava/lang/Object;JLjava/lang/Object;)Ljava/lang/Object;
      // 419: pop
      // 41a: aload 9
      // 41c: ldc_w "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE"
      // 41f: aload 5
      // 421: aconst_null
      // 422: aload 5
      // 424: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingMessage.getReferenceIdentifier ()I
      // 427: aload 16
      // 429: invokeinterface net/rim/device/apps/api/transmission/TransmissionService.transmitObject (Ljava/lang/String;Ljava/lang/Object;Lnet/rim/device/apps/api/transmission/TransmissionStatusListener;ILjava/lang/Object;)V 6
      // 42e: goto 438
      // 431: astore 17
      // 433: goto 438
      // 436: astore 17
      // 438: iload 2
      // 439: ifeq 445
      // 43c: aload 3
      // 43d: bipush 64
      // 43f: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.clearFlags (I)V 2
      // 444: return
      // 445: aload 3
      // 446: ldc_w 2097152
      // 449: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.clearFlags (I)V 2
      // 44e: return
      // try (300 -> 303): 304 null
      // try (455 -> 463): 464 null
      // try (455 -> 463): 466 null
   }

   @Override
   public final void receiveReceipt(Object deliveryStatusNotice) {
      if (deliveryStatusNotice instanceof Object) {
         UnknownMimePartModel deliveryStatusPart = (UnknownMimePartModel)deliveryStatusNotice;
         byte[] data = deliveryStatusPart.getData();
         if (data.length > 0) {
            DataBuffer dnsBuffer = (DataBuffer)(new Object(data, 0, data.length, true));
            dnsBuffer.rewind();

            try {
               int originalMessageReferenceId = 0;
               String confirmingAddress = null;
               byte actionCode = 0;

               while (!dnsBuffer.eof()) {
                  byte tag = dnsBuffer.readByte();
                  if (tag == 0) {
                     break;
                  }

                  int length = dnsBuffer.readCompressedInt();
                  if (length != 0) {
                     switch (tag) {
                        case -15:
                           originalMessageReferenceId = dnsBuffer.readInt();
                           break;
                        case -7:
                           byte[] rawAddress = new byte[length];
                           dnsBuffer.read(rawAddress);
                           if (rawAddress[length - 1] == 0) {
                              Array.resize(rawAddress, rawAddress.length - 1);
                           }

                           confirmingAddress = (String)(new Object(rawAddress));
                           break;
                        case -6:
                           actionCode = dnsBuffer.readByte();
                           break;
                        case -5:
                           dnsBuffer.readByte();
                     }
                  }
               }

               if (confirmingAddress != null) {
                  if ((
                        confirmingAddress.startsWith("IAP:")
                           || confirmingAddress.startsWith("TAP:")
                           || confirmingAddress.startsWith("PHN:")
                           || confirmingAddress.startsWith("FAX:")
                     )
                     && confirmingAddress.length() > 4) {
                     confirmingAddress = confirmingAddress.substring(4);
                  }

                  EmailMessageModel message = (EmailMessageModel)MessageLookups.get(2623838111545834320L, originalMessageReferenceId);
                  if (message != null) {
                     ContextObject contextObject = (ContextObject)(new Object());
                     EmailPayloadModel oldPayload = EmailModifier.beginChanges(message, contextObject);
                     int numEmailHeaderModels = 0;
                     int numReadReceiptsReceived = 0;
                     int numDeliveryReceiptsReceived = 0;
                     int messageSize = message.size();

                     for (int i = 0; i < messageSize; i++) {
                        RIMModel model = (RIMModel)message.getAt(i);
                        if (model instanceof Object) {
                           EmailHeaderModel headerModel = (EmailHeaderModel)model;
                           int headerType = headerModel.getHeaderType();
                           if (headerType == 0 || headerType == 1 || headerType == 2 || headerType == 5) {
                              String[] nameStrings = new Object[2];
                              headerModel.extractNames(nameStrings);
                              numEmailHeaderModels++;
                              if (StringUtilities.compareToIgnoreCase(confirmingAddress, nameStrings[0], 1701707776) == 0) {
                                 switch (actionCode) {
                                    case 16:
                                       headerModel.clearFlags((byte)4);
                                       headerModel.setFlags((byte)2);
                                       break;
                                    case 32:
                                       headerModel.clearFlags((byte)4);
                                       headerModel.setFlags((byte)1);
                                       break;
                                    case 64:
                                       headerModel.setFlags((byte)4);
                                       message.setStatus(8191, 63);
                                 }
                              }

                              if (headerModel.flagsSet(2)) {
                                 numReadReceiptsReceived++;
                              }

                              if (headerModel.flagsSet(1)) {
                                 numDeliveryReceiptsReceived++;
                              }
                           }
                        }
                     }

                     if (numEmailHeaderModels == numReadReceiptsReceived) {
                        message.setStatus(2097151, 0);
                        MessageLookups.remove(2623838111545834320L, originalMessageReferenceId);
                     } else if (numEmailHeaderModels == numDeliveryReceiptsReceived) {
                        message.setStatus(4194303, 0);
                     }

                     EmailModifier.endChanges(message, oldPayload, contextObject);
                     return;
                  }
               }
            } finally {
               return;
            }
         }
      }
   }

   @Override
   public final String stripIMPlusPrefix(String address, RIMModel addressModel) {
      String strippedAddress = address;
      if (address != null) {
         if (!address.startsWith("IAP:") && !address.startsWith("TAP:")) {
            if ((address.startsWith("PHN:") || address.startsWith("FAX:")) && address.length() > 4) {
               strippedAddress = address.substring(4);
            }
         } else if (address.length() > 4) {
            strippedAddress = address.substring(4);
         }

         if (addressModel instanceof Object) {
            StringBuffer phoneNumBuffer = (StringBuffer)(new Object());

            for (int i = 0; i < strippedAddress.length(); i++) {
               char numChar = strippedAddress.charAt(i);
               if (numChar >= '0' && numChar <= '9') {
                  phoneNumBuffer.append(numChar);
               }
            }

            strippedAddress = phoneNumBuffer.toString();
         }
      }

      return strippedAddress;
   }

   @Override
   public final String appendIMPlusPrefix(RIMModel model, String address) {
      StringBuffer addressBuffer = (StringBuffer)(new Object());
      if (this._interactiveHHFactory.recognize(model)) {
         addressBuffer.append("IAP:");
      } else if (this._oneWayPageFactory.recognize(model)) {
         addressBuffer.append("TAP:");
      } else if (this._faxFactory.recognize(model)) {
         addressBuffer.append("FAX:");
      } else if (this._phoneFactory.recognize(model)) {
         addressBuffer.append("PHN:");
      }

      addressBuffer.append(address);
      return addressBuffer.toString();
   }

   @Override
   public final IMPlusComposeModel[] getComposeModels() {
      IMPlusComposeModel[] composeModels = new Object[0];
      IMPlusCmimeListener$ReceiptCapableService receiptCapableService = this.getIMPlusReceiptCapableService();
      if (receiptCapableService != null) {
         if (receiptCapableService._twoWayPageEnabled) {
            IMPlusComposeModelImpl newModel = new IMPlusComposeModelImpl(
               IMPlusUseOnceAddressVerb.newUseOnceAddressVerb(4439968724864684903L, false),
               this._interactiveHHFactory,
               IMPlusResources.getString(0),
               4439968724864684903L
            );
            Array.resize(composeModels, 1);
            composeModels[0] = newModel;
         }

         if (receiptCapableService._oneWayPageEnabled) {
            IMPlusComposeModelImpl newModel = new IMPlusComposeModelImpl(
               IMPlusUseOnceAddressVerb.newUseOnceAddressVerb(-7875293227724358566L, false),
               this._oneWayPageFactory,
               IMPlusResources.getString(16),
               -7875293227724358566L
            );
            Array.resize(composeModels, composeModels.length + 1);
            composeModels[composeModels.length - 1] = newModel;
         }

         if (receiptCapableService._phoneEnabled) {
            IMPlusComposeModelImpl newModel = new IMPlusComposeModelImpl(
               IMPlusUseOnceAddressVerb.newUseOnceAddressVerb(3797587162219887872L, false),
               this._phoneFactory,
               IMPlusResources.getString(17),
               3797587162219887872L
            );
            Array.resize(composeModels, composeModels.length + 1);
            composeModels[composeModels.length - 1] = newModel;
         }

         if (receiptCapableService._faxEnabled) {
            IMPlusComposeModelImpl newModel = new IMPlusComposeModelImpl(
               IMPlusUseOnceAddressVerb.newUseOnceAddressVerb(2862138288634470671L, false),
               this._faxFactory,
               IMPlusResources.getString(21),
               2862138288634470671L
            );
            Array.resize(composeModels, composeModels.length + 1);
            composeModels[composeModels.length - 1] = newModel;
         }
      }

      return composeModels;
   }

   @Override
   public final boolean isIMPlusCompose(Object context) {
      boolean isIMPlusMessageCompose = ContextObject.getPrivateFlag(context, -3859986508589425865L, 1);
      Object parentModel = ContextObject.get(context, 246);
      if (parentModel instanceof Object) {
         isIMPlusMessageCompose |= ((EmailMessageModel)parentModel).flagsSet(8388608);
      }

      return isIMPlusMessageCompose;
   }

   @Override
   public final int getOptionDefault(int serviceRecId, byte option) {
      IMPlusCmimeListener$ReceiptCapableService receiptCapableService = this.findReceiptCapableService(serviceRecId);
      if (receiptCapableService == null) {
         return 0;
      }

      int returnVal = 0;
      switch (option) {
         case -1:
            break;
         case 0:
         default:
            returnVal = receiptCapableService._defaultConfirmDelivery ? 1 : 0;
            break;
         case 1:
            returnVal = receiptCapableService._defaultConfirmRead ? 1 : 0;
            break;
         case 2:
            returnVal = receiptCapableService._defaultAllowReadConfirm;
      }

      return returnVal;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      ServiceBook sb = ServiceBook.getSB();
      if (guid == -4220058463650496006L || guid == 8288627527798139133L) {
         ServiceRecord targetSR = sb.getRecordById(data0);
         if (targetSR == null) {
            return;
         }

         if (StringUtilities.compareToIgnoreCase(targetSR.getCid(), "CMIME", 1701707776) == 0) {
            if (targetSR.getType() == 0) {
               this.processAppData(targetSR);
               return;
            }

            IMPlusCmimeListener$ReceiptCapableService receiptCapableService = this.findReceiptCapableService(targetSR.getId());
            if (receiptCapableService != null) {
               this.DeactivateSR(receiptCapableService._serviceRecId);
               return;
            }
         }
      } else if (guid == 2522898683889177438L && this.findReceiptCapableService(data0) != null) {
         this.DeactivateSR(data0);
      }
   }

   @Override
   public final boolean getOptionEnabled(int serviceRecId, byte option) {
      IMPlusCmimeListener$ReceiptCapableService receiptCapableService = this.findReceiptCapableService(serviceRecId);
      if (receiptCapableService == null) {
         return false;
      }

      boolean returnVal = false;
      switch (option) {
         case -1:
            break;
         case 0:
         default:
            returnVal = receiptCapableService._enableConfirmDeliveryUI;
            break;
         case 1:
            returnVal = receiptCapableService._enableConfirmReadUI;
            break;
         case 2:
            returnVal = receiptCapableService._allowReadConfirmUI;
      }

      return returnVal;
   }

   public static final IMPlusCmimeListener getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         IMPlusCmimeListener listener = (IMPlusCmimeListener)ar.get(1708259843772261205L);
         if (listener == null) {
            listener = new IMPlusCmimeListener();
            ar.put(1708259843772261205L, listener);
         }

         return listener;
      }
   }

   public IMPlusCmimeListener() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      this._interactiveHHFactory = new IMPlusAddressModelFactory(5);
      ar.put(4439968724864684903L, this._interactiveHHFactory);
      this._interactiveHHUseOnceVerb = IMPlusUseOnceAddressVerb.newUseOnceAddressVerb(4439968724864684903L, true);
      this._oneWayPageFactory = new IMPlusAddressModelFactory(4);
      ar.put(-7875293227724358566L, this._oneWayPageFactory);
      this._oneWayPageUseOnceVerb = IMPlusUseOnceAddressVerb.newUseOnceAddressVerb(-7875293227724358566L, true);
      this._phoneFactory = (RIMModelFactory)ar.waitFor(3797587162219887872L);
      this._phoneUseOnceVerb = IMPlusUseOnceAddressVerb.newUseOnceAddressVerb(3797587162219887872L, true);
      this._phoneComposeVerb = new IMPlusPhoneComposeVerb(false);
      this._phoneCombiner = new IMPlusPhoneComposeVerbCombiner();
      this._faxFactory = (RIMModelFactory)ar.waitFor(2862138288634470671L);
      this._faxUseOnceVerb = IMPlusUseOnceAddressVerb.newUseOnceAddressVerb(2862138288634470671L, true);
      this._serviceArray = new IMPlusCmimeListener$ReceiptCapableService[0];
      ApplicationRegistry.getApplicationRegistry().put(-2205884509140292945L, this);
   }

   private final boolean isSendMethodSupportedBySomeService(long sendMethod) {
      for (int i = this._serviceArray.length - 1; i >= 0; i--) {
         if (sendMethod == 4439968724864684903L && this._serviceArray[i]._twoWayPageEnabled) {
            return true;
         }

         if (sendMethod == -7875293227724358566L && this._serviceArray[i]._oneWayPageEnabled) {
            return true;
         }

         if (sendMethod == 4846413703361859244L && this._serviceArray[i]._phoneEnabled) {
            return true;
         }

         if (sendMethod == 2862138288634470671L && this._serviceArray[i]._faxEnabled) {
            return true;
         }
      }

      return false;
   }

   private final void processAppData(ServiceRecord targetSR) {
      byte[] applicationDataByteArray = targetSR.getApplicationData();
      if (applicationDataByteArray != null && applicationDataByteArray.length > 0) {
         try {
            IMPlusCmimeListener$ReceiptCapableService receiptCapableService = null;
            DataBuffer buffer = (DataBuffer)(new Object());
            buffer.setData(applicationDataByteArray, 0, applicationDataByteArray.length);
            buffer.readUnsignedByte();
            Parameters parameters = (Parameters)(new Object(12, 4));
            parameters.read(buffer, (byte)0);
            byte[] recTypes = parameters.getFirst((byte)48);
            if (recTypes != null) {
               int typesMask = (recTypes[0] & 255) << 24 | (recTypes[1] & 255) << 16 | (recTypes[2] & 255) << 8 | recTypes[3] & 255;
               boolean phoneEnabled = (typesMask & 4) != 0;
               boolean faxEnabled = (typesMask & 8) != 0;
               boolean oneWayPageEnabled = (typesMask & 16) != 0;
               boolean twoWayPageEnabled = (typesMask & 32) != 0;
               VerbRepository verbRepository = VerbRepository.getVerbRepository(-7881764549058890736L);
               if (!phoneEnabled && !faxEnabled && !oneWayPageEnabled && !twoWayPageEnabled) {
                  receiptCapableService = this.findReceiptCapableService(targetSR.getId());
                  if (receiptCapableService != null
                     && (
                        receiptCapableService._twoWayPageEnabled
                           || receiptCapableService._oneWayPageEnabled
                           || receiptCapableService._phoneEnabled
                           || receiptCapableService._faxEnabled
                     )) {
                     this.DeactivateSR(targetSR.getId());
                  }
               } else {
                  if (this._composeIMPlusVerb == null) {
                     this._composeIMPlusVerb = new IMPlusComposeVerb();
                     verbRepository.register(this._composeIMPlusVerb, 4439968724864684903L);
                  }

                  receiptCapableService = this.findReceiptCapableService(targetSR.getId());
                  int serviceRecId = targetSR.getId();
                  boolean addNewService = false;
                  if (receiptCapableService == null) {
                     receiptCapableService = new IMPlusCmimeListener$ReceiptCapableService();
                     receiptCapableService._serviceRecId = serviceRecId;
                     addNewService = true;
                  }

                  VerbRepository useOnceRepository = VerbRepository.getVerbRepository(8016149483483360697L);
                  RIMModelFactoryRepository.removeFactory(-5785746452676094833L, this._interactiveHHFactory);
                  useOnceRepository.deregister(this._interactiveHHUseOnceVerb, 4439968724864684903L);
                  if (twoWayPageEnabled || this.isSendMethodSupportedBySomeService(4439968724864684903L)) {
                     RIMModelFactoryRepository.addFactory(-5785746452676094833L, this._interactiveHHFactory);
                     useOnceRepository.register(this._interactiveHHUseOnceVerb, 4439968724864684903L);
                  }

                  receiptCapableService._twoWayPageEnabled = twoWayPageEnabled;
                  RIMModelFactoryRepository.removeFactory(-5785746452676094833L, this._oneWayPageFactory);
                  useOnceRepository.deregister(this._oneWayPageUseOnceVerb, -7875293227724358566L);
                  if (oneWayPageEnabled || this.isSendMethodSupportedBySomeService(-7875293227724358566L)) {
                     RIMModelFactoryRepository.addFactory(-5785746452676094833L, this._oneWayPageFactory);
                     useOnceRepository.register(this._oneWayPageUseOnceVerb, -7875293227724358566L);
                  }

                  receiptCapableService._oneWayPageEnabled = oneWayPageEnabled;
                  VerbRepository phoneComposeRepository = VerbRepository.getVerbRepository(-6761056765378641298L);
                  RIMModelFactoryRepository.removeFactory(-5785746452676094833L, this._phoneFactory);
                  useOnceRepository.deregister(this._phoneUseOnceVerb, 3797587162219887872L);
                  phoneComposeRepository.deregister(this._phoneComposeVerb, 3797587162219887872L);
                  VerbCombinerRepository.removeCombiner(282149773611355264L, this._phoneCombiner);
                  if (phoneEnabled || this.isSendMethodSupportedBySomeService(3797587162219887872L)) {
                     RIMModelFactoryRepository.addFactory(-5785746452676094833L, this._phoneFactory);
                     useOnceRepository.register(this._phoneUseOnceVerb, 3797587162219887872L);
                     phoneComposeRepository.register(this._phoneComposeVerb, 3797587162219887872L);
                     VerbCombinerRepository.addCombiner(282149773611355264L, this._phoneCombiner);
                  }

                  receiptCapableService._phoneEnabled = phoneEnabled;
                  RIMModelFactoryRepository.removeFactory(-5785746452676094833L, this._faxFactory);
                  useOnceRepository.deregister(this._faxUseOnceVerb, 2862138288634470671L);
                  if (faxEnabled || this.isSendMethodSupportedBySomeService(2862138288634470671L)) {
                     RIMModelFactoryRepository.addFactory(-5785746452676094833L, this._faxFactory);
                     useOnceRepository.register(this._faxUseOnceVerb, 2862138288634470671L);
                  }

                  receiptCapableService._faxEnabled = faxEnabled;
                  if (addNewService) {
                     Arrays.add(this._serviceArray, receiptCapableService);
                  }
               }
            }

            receiptCapableService = this.findReceiptCapableService(targetSR.getId());
            if (receiptCapableService == null) {
               receiptCapableService = new IMPlusCmimeListener$ReceiptCapableService();
               receiptCapableService._serviceRecId = targetSR.getId();
            } else {
               receiptCapableService._defaultAllowReadConfirm = 0;
               receiptCapableService._enableConfirmDeliveryUI = receiptCapableService._defaultConfirmDelivery = receiptCapableService._enableConfirmReadUI = receiptCapableService._defaultConfirmRead = receiptCapableService._allowReadConfirmUI = receiptCapableService._sendVerboseDSN = false;
            }

            byte[] serviceCapabilities = parameters.getFirst((byte)-15);
            if (serviceCapabilities != null) {
               if ((serviceCapabilities[0] & 1) != 0) {
                  receiptCapableService._enableConfirmDeliveryUI = true;
               }

               if ((serviceCapabilities[0] & 2) != 0) {
                  receiptCapableService._defaultConfirmDelivery = true;
               }

               if ((serviceCapabilities[0] & 4) != 0) {
                  receiptCapableService._enableConfirmReadUI = true;
               }

               if ((serviceCapabilities[0] & 8) != 0) {
                  receiptCapableService._defaultConfirmRead = true;
               }

               if ((serviceCapabilities[0] & 16) != 0) {
                  receiptCapableService._allowReadConfirmUI = true;
               }

               if ((serviceCapabilities[0] & 32) != 0) {
                  receiptCapableService._defaultAllowReadConfirm = 2;
               }
            }

            byte[] deviceCapabilities = parameters.getFirst((byte)-16);
            if (deviceCapabilities != null) {
               receiptCapableService._sendVerboseDSN = (deviceCapabilities[0] & 16) != 0;
            }

            if ((
                  receiptCapableService._defaultConfirmRead
                     || receiptCapableService._defaultConfirmDelivery
                     || receiptCapableService._defaultAllowReadConfirm != 0
                     || receiptCapableService._enableConfirmReadUI
                     || receiptCapableService._enableConfirmDeliveryUI
                     || receiptCapableService._allowReadConfirmUI
               )
               && this.findReceiptCapableService(receiptCapableService._serviceRecId) == null) {
               Arrays.add(this._serviceArray, receiptCapableService);
               return;
            }
         } finally {
            return;
         }
      }
   }

   private final void DeactivateSR(int serviceRecId) {
      boolean twoWayPageEnabled = false;
      boolean oneWayPageEnabled = false;
      boolean phoneEnabled = false;
      boolean faxEnabled = false;

      for (int i = 0; i < this._serviceArray.length; i++) {
         if (this._serviceArray[i]._serviceRecId == serviceRecId) {
            Arrays.removeAt(this._serviceArray, i);
            i--;
         } else {
            twoWayPageEnabled |= this._serviceArray[i]._twoWayPageEnabled;
            oneWayPageEnabled |= this._serviceArray[i]._oneWayPageEnabled;
            phoneEnabled |= this._serviceArray[i]._phoneEnabled;
            faxEnabled |= this._serviceArray[i]._faxEnabled;
         }
      }

      if (!twoWayPageEnabled && !oneWayPageEnabled && !phoneEnabled && !faxEnabled) {
         VerbRepository composeFactoryRepository = VerbRepository.getVerbRepository(-7881764549058890736L);
         composeFactoryRepository.deregister(this._composeIMPlusVerb, 4439968724864684903L);
         this._composeIMPlusVerb = null;
      }

      VerbRepository useOnceRepository = VerbRepository.getVerbRepository(8016149483483360697L);
      if (!twoWayPageEnabled) {
         RIMModelFactoryRepository.removeFactory(-5785746452676094833L, this._interactiveHHFactory);
         useOnceRepository.deregister(this._interactiveHHUseOnceVerb, 4439968724864684903L);
      }

      if (!oneWayPageEnabled) {
         RIMModelFactoryRepository.removeFactory(-5785746452676094833L, this._oneWayPageFactory);
         useOnceRepository.deregister(this._oneWayPageUseOnceVerb, -7875293227724358566L);
      }

      if (!phoneEnabled) {
         RIMModelFactoryRepository.removeFactory(-5785746452676094833L, this._phoneFactory);
         useOnceRepository.deregister(this._phoneUseOnceVerb, 3797587162219887872L);
         VerbRepository.getVerbRepository(-6761056765378641298L).deregister(this._phoneComposeVerb, 3797587162219887872L);
         VerbCombinerRepository.removeCombiner(282149773611355264L, this._phoneCombiner);
      }

      if (!faxEnabled) {
         RIMModelFactoryRepository.removeFactory(-5785746452676094833L, this._faxFactory);
         useOnceRepository.deregister(this._faxUseOnceVerb, 2862138288634470671L);
      }
   }
}
