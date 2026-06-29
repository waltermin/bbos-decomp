package net.rim.device.apps.internal.mms;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.MIMEContentVerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.model.MMSAttachmentImpl;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.options.MMSTransportServiceBook;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;
import net.rim.device.apps.internal.mms.verbs.MMSSendVCalVerb;
import net.rim.device.apps.internal.phone.model.PhoneNumberConverter;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.internal.io.store.ContentStoreEncryption;
import net.rim.device.internal.system.DataServices;
import net.rim.device.internal.util.StringUtilitiesInternal;

public final class MMSUtilities {
   public static final String MMS_SERVICE_CID;
   private static final String TEMP_FILE_PATH;
   private static final String EMAIL_SPECIAL_CHARS;
   private static IntHashtable _mimeTypes;

   public static final int parseInt(String str, int defaultValue) {
      if (str != null) {
         try {
            return Integer.parseInt(str.trim());
         } finally {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   public static final long parseLong(String str, long defaultValue) {
      if (str != null) {
         try {
            return Long.parseLong(str);
         } finally {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   public static final boolean canReply(MMSMessageModel message) {
      MMSPayloadModel payload = message.getPayload();
      return payload.getSender() != null;
   }

   public static final boolean canReplyToAll(MMSMessageModel message) {
      MMSPayloadModel payload = message.getPayload();
      if (payload.getSender() != null) {
         return true;
      }

      Vector v = payload.getRecipients();
      if (v != null && v.size() > 0) {
         return true;
      }

      v = payload.getCcRecipients();
      return v != null && v.size() > 0;
   }

   public static final boolean canForward(MMSMessageModel message) {
      AttachmentDataProvider attachmentProvider = message.getAttachmentDataProvider();
      return !attachmentProvider.hasAttachment("net_rim_ProtocolDataUnit") ? false : !message.isForwardLocked();
   }

   public static final boolean canRequestContent(MMSMessageModel message) {
      if (RadioInfo.getState() != 1) {
         return false;
      } else if (!message.isInbound()) {
         return false;
      } else if (message.getStatus() == 1023) {
         return false;
      } else {
         return message.getAttachmentDataProvider().hasAttachments() && message.getStatus() != 1
            ? false
            : message.getPayload().getAttribute("x-mms-content-location") != null;
      }
   }

   public static final boolean canSend() {
      return !MMSTransportServiceBook.hasMMSServiceRecord() ? false : RadioInfo.getState() == 1;
   }

   public static final boolean hasDataCoverage() {
      return RadioInfo.isDataServiceOperational() && DataServices.getInstance().isDataServicesEnabled();
   }

   public static final boolean isITPolicyEnabled() {
      return !ITPolicy.getBoolean(21, 7, false);
   }

   public static final boolean isPermanentFailure(MMSMessageModel message) {
      return isPermanentFailure(
         message.getStatus(),
         message.getHttpErrorCode(),
         message.getMMSResponseCode(),
         message.getWAPIOExceptionError(),
         message.getWAPIOExceptionAdditionalData()
      );
   }

   public static final boolean isPermanentFailure(int status, int httpErrorCode, int mmsResponseCode, int wapIOExceptionError, int wapIOExceptionAdditionalData) {
      if (status != 16383 && status != 8191 && status != 1) {
         return false;
      }

      if (httpErrorCode != 0 && httpErrorCode != 200 && httpErrorCode != 204) {
         switch (httpErrorCode) {
            case 408:
            case 504:
               return false;
            default:
               System.out.println(((StringBuffer)(new Object("MMS Http Error = "))).append(httpErrorCode).toString());
               return true;
         }
      } else {
         if (wapIOExceptionError != 0) {
            System.out
               .println(
                  ((StringBuffer)(new Object("MMS WapIOException = "))).append(wapIOExceptionError).append(' ').append(wapIOExceptionAdditionalData).toString()
               );
            return true;
         }

         if (mmsResponseCode != 0 && mmsResponseCode != 128) {
            switch (mmsResponseCode) {
               case 191:
                  System.out.println(((StringBuffer)(new Object("MMS error response code = "))).append(mmsResponseCode).toString());
                  return true;
               case 192:
               case 193:
               case 194:
               case 195:
               default:
                  return false;
            }
         } else {
            System.out.println("MMS permanent failure");
            return true;
         }
      }
   }

   public static final boolean isPhoneNumber(RIMModel address) {
      if (address instanceof Object) {
         PhoneNumberModel pnm = (PhoneNumberModel)address;
         if (pnm.getValue().length() > 0) {
            return true;
         }
      }

      return false;
   }

   public static final MMSAttachment reduceImage(MMSAttachment big, int targetSize, int maxWidth, int maxHeight) {
      byte[] data = big.getData();
      EncodedImage image = EncodedImage.createEncodedImage(data, 0, data.length);
      image.setDecodeMode(image.getDecodeMode() | 8);
      int scaleFactor = 1;
      if (big.getDataSize() / targetSize > 4) {
         scaleFactor = 3;
      } else if (big.getDataSize() > targetSize) {
         scaleFactor = 2;
      }

      if (image.getWidth() > maxWidth) {
         int wScale = (image.getWidth() + maxWidth - 1) / maxWidth;
         scaleFactor = Math.max(scaleFactor, wScale);
      }

      if (image.getHeight() > maxHeight) {
         int hScale = (image.getHeight() + maxHeight - 1) / maxHeight;
         scaleFactor = Math.max(scaleFactor, hScale);
      }

      if (scaleFactor == 1 && big.getDataSize() <= targetSize) {
         return big;
      }

      System.out.println(((StringBuffer)(new Object("MMS reduce "))).append(big.getName()).toString());
      System.out.println(((StringBuffer)(new Object(" original length="))).append(big.getDataSize()).toString());
      System.out.println(((StringBuffer)(new Object(" scale factor="))).append(scaleFactor).toString());
      byte[] smallData = reduceImage(image, scaleFactor, targetSize);
      String name = big.getName();
      int idx = name.lastIndexOf(46);
      if (idx > 0) {
         name = name.substring(0, idx);
      }

      name = ((StringBuffer)(new Object())).append(name).append(".jpg").toString();
      MMSAttachment small = new MMSAttachmentImpl(name, 30, smallData, null);
      System.out.println(((StringBuffer)(new Object(" final length="))).append(small.getDataSize()).toString());
      return small;
   }

   private static final byte[] reduceImage(EncodedImage image, int scaleFactor, int targetSize) {
      while (true) {
         image.setScale(scaleFactor);
         Bitmap bitmap = image.getBitmap();
         int qualityFactor = 50;
         EncodedImage smallImage = JPEGEncodedImage.encode(bitmap, qualityFactor);
         byte[] smallData = smallImage.getData();
         if (smallData.length <= targetSize) {
            return smallData;
         }

         scaleFactor++;
      }
   }

   public static final int getMIMEType(String mimeType) {
      if (mimeType == null) {
         return -1;
      }

      int type = parseInt(mimeType, -1);
      if (type != -1) {
         return type;
      }

      String normalizedMimeType = MIMETypeAssociations.getNormalizedType(mimeType);
      IntHashtable hashtable = getMIMETypeTable();
      IntEnumeration keys = hashtable.keys();

      while (keys.hasMoreElements()) {
         int key = keys.nextElement();
         if (normalizedMimeType.equals(hashtable.get(key))) {
            return key;
         }
      }

      System.out.println(((StringBuffer)(new Object("MMS unmapped MIME type: "))).append(mimeType).toString());
      return -1;
   }

   public static final String getMIMETypeString(int attachmentType) {
      return (String)getMIMETypeTable().get(attachmentType);
   }

   private static final IntHashtable getMIMETypeTable() {
      if (_mimeTypes == null) {
         _mimeTypes = (IntHashtable)(new Object());
         _mimeTypes.put(1, "text/*");
         _mimeTypes.put(2, "text/html");
         _mimeTypes.put(3, "text/plain");
         _mimeTypes.put(6, "text/x-vcalendar");
         _mimeTypes.put(773, "text/calendar");
         _mimeTypes.put(7, "text/x-vcard");
         _mimeTypes.put(8, "text/vnd.wap.wml");
         _mimeTypes.put(20, "application/vnd.wap.wmlc");
         _mimeTypes.put(28, "image/*");
         _mimeTypes.put(29, "image/gif");
         _mimeTypes.put(65545, "image/jpg");
         _mimeTypes.put(30, "image/jpeg");
         _mimeTypes.put(65546, "image/pjpeg");
         _mimeTypes.put(31, "image/tiff");
         _mimeTypes.put(32, "image/png");
         _mimeTypes.put(65548, "image/vnd.rim.png");
         _mimeTypes.put(65547, "image/bmp");
         _mimeTypes.put(33, "image/vnd.wap.wbmp");
         _mimeTypes.put(15, "multipart/alternative");
         _mimeTypes.put(35, "application/vnd.wap.multipart.mixed");
         _mimeTypes.put(38, "application/vnd.wap.multipart.alternative");
         _mimeTypes.put(51, "application/vnd.wap.multipart.related");
         _mimeTypes.put(59, "application/xhtml+xml");
         _mimeTypes.put(62, "application/vnd.wap.mms-message");
         _mimeTypes.put(69, "application/vnd.wap.xhtml+xml");
         _mimeTypes.put(71, "application/vnd.oma.dd+xml");
         _mimeTypes.put(72, "application/vnd.oma.drm.message");
         _mimeTypes.put(73, "application/vnd.oma.drm.content");
         _mimeTypes.put(74, "application/vnd.oma.drm.rights+xml");
         _mimeTypes.put(65537, "application/smil");
         _mimeTypes.put(65551, "audio/mpeg");
         _mimeTypes.put(65555, "audio/mp3");
         _mimeTypes.put(65554, "audio/x-mpeg");
         _mimeTypes.put(65552, "audio/x-wav");
         _mimeTypes.put(65553, "audio/wav");
         _mimeTypes.put(65543, "audio/mid");
         _mimeTypes.put(65538, "audio/midi");
         _mimeTypes.put(65544, "audio/x-midi");
         _mimeTypes.put(65550, "audio/x-mid");
         _mimeTypes.put(65542, "audio/sp-midi");
         _mimeTypes.put(65539, "audio/amr");
         _mimeTypes.put(65540, "application/x-vnd.rim.pme");
         _mimeTypes.put(65541, "application/x-vnd.rim.pme.b");
         _mimeTypes.put(65549, "application/octet-stream");
         _mimeTypes.put(80, "video/*");
         _mimeTypes.put(65556, "video/3gpp");
         _mimeTypes.put(65569, "video/3gpp2");
         _mimeTypes.put(65557, "video/mp4");
         _mimeTypes.put(65558, "video/x-ms-wmv");
         _mimeTypes.put(65559, "video/quicktime");
         _mimeTypes.put(65560, "video/x-msvideo");
         _mimeTypes.put(65561, "audio/mp4");
         _mimeTypes.put(65562, "audio/aac");
         _mimeTypes.put(65563, "audio/3gpp");
         _mimeTypes.put(65568, "audio/3gpp2");
         _mimeTypes.put(65564, "audio/x-gsm");
         _mimeTypes.put(65565, "audio/basic");
         _mimeTypes.put(65566, "audio/m4a");
         _mimeTypes.put(65567, "audio/x-ms-wma");
         _mimeTypes.put(65570, "audio/qcelp");
         ObjectGroup.createGroupIgnoreTooBig(_mimeTypes);
      }

      return _mimeTypes;
   }

   public static final boolean isSupportedType(int attachmentType, boolean allowAllKnownTypes) {
      if (isMultipartType(attachmentType)) {
         return true;
      }

      if (isImageType(attachmentType, allowAllKnownTypes)) {
         return true;
      }

      if (isTextType(attachmentType)) {
         return true;
      }

      if (isVideoType(attachmentType, allowAllKnownTypes)) {
         return true;
      }

      if (isAudioType(attachmentType, allowAllKnownTypes)) {
         return true;
      }

      switch (attachmentType) {
         case 6:
         case 7:
         case 15:
         case 38:
         case 65537:
            return true;
         default:
            return false;
      }
   }

   public static final boolean isMultipartType(int attachmentType) {
      switch (attachmentType) {
         case 35:
         case 51:
            return true;
         default:
            return false;
      }
   }

   public static final boolean isVideoType(int attachmentType) {
      return isVideoType(attachmentType, true);
   }

   public static final boolean isVideoType(int attachmentType, boolean allowAllKnownTypes) {
      switch (attachmentType) {
         case 80:
         case 65557:
         case 65558:
         case 65559:
         case 65560:
         case 65569:
            return allowAllKnownTypes;
         case 65556:
            return true;
         default:
            return false;
      }
   }

   public static final boolean isImageType(int attachmentType) {
      return isImageType(attachmentType, true);
   }

   public static final boolean isImageType(int attachmentType, boolean allowAllKnownTypes) {
      switch (attachmentType) {
         case 28:
         case 31:
         case 32:
         case 65547:
         case 65548:
            return allowAllKnownTypes;
         case 29:
         case 30:
         case 33:
         case 65545:
         case 65546:
            return true;
         default:
            return false;
      }
   }

   public static final boolean isTextType(int attachmentType) {
      switch (attachmentType) {
         case 1:
         case 3:
            return true;
         default:
            return false;
      }
   }

   public static final boolean isAudioType(int attachmentType) {
      return isAudioType(attachmentType, true);
   }

   public static final boolean isAudioType(int attachmentType, boolean allowAllKnownTypes) {
      switch (attachmentType) {
         case 65537:
         case 65540:
         case 65541:
         case 65545:
         case 65546:
         case 65547:
         case 65548:
         case 65549:
         case 65556:
         case 65557:
         case 65558:
         case 65559:
         case 65560:
         case 65569:
            return false;
         case 65538:
         case 65539:
         case 65542:
         case 65543:
         case 65544:
         case 65550:
            return true;
         case 65551:
         case 65552:
         case 65553:
         case 65554:
         case 65555:
         case 65561:
         case 65562:
         case 65563:
         case 65564:
         case 65565:
         case 65566:
         case 65567:
         case 65568:
         case 65570:
         default:
            return allowAllKnownTypes;
      }
   }

   public static final boolean isVoiceNoteAudioType(int attachmentType) {
      switch (attachmentType) {
         case 65538:
            return false;
         case 65539:
         default:
            return true;
      }
   }

   public static final boolean isRingtoneAudioType(int attachmentType) {
      switch (attachmentType) {
         case 65537:
         case 65539:
         case 65540:
         case 65541:
         case 65545:
         case 65546:
         case 65547:
         case 65548:
         case 65549:
         case 65556:
         case 65557:
         case 65558:
         case 65559:
         case 65560:
         case 65569:
            return false;
         case 65538:
         case 65542:
         case 65543:
         case 65544:
         case 65550:
         case 65551:
         case 65552:
         case 65553:
         case 65554:
         case 65555:
         case 65561:
         case 65562:
         case 65563:
         case 65564:
         case 65565:
         case 65566:
         case 65567:
         case 65568:
         case 65570:
         default:
            return true;
      }
   }

   public static final boolean isCalendarType(int attachmentType) {
      switch (attachmentType) {
         case 6:
         case 773:
            return true;
         default:
            return false;
      }
   }

   public static final boolean isAppointmentType(int attachmentType) {
      switch (attachmentType) {
         case 7:
            return true;
         default:
            return false;
      }
   }

   public static final String getDefaultAttachmentName(int mimetype) {
      switch (mimetype) {
         case 6:
            return "calendar.vcs";
         case 7:
            return "address.vcf";
         case 28:
            return "image";
         case 29:
            return "image.gif";
         case 30:
         case 65545:
         case 65546:
            return "image.jpg";
         case 31:
            return "image.tif";
         case 32:
         case 65548:
            return "image.png";
         case 33:
         case 65547:
            return "image.bmp";
         case 80:
            return "video";
         case 65538:
         case 65543:
         case 65544:
         case 65550:
            return "audio.mid";
         case 65539:
            return "voicenote.amr";
         case 65551:
         case 65554:
         case 65555:
            return "audio.mp3";
         case 65552:
         case 65553:
            return "audio.wav";
         case 65556:
            return "video.3gpp";
         case 65557:
            return "video.mp4";
         case 65558:
            return "video.wmv";
         case 65559:
            return "video.mov";
         case 65560:
            return "video.avi";
         case 65569:
            return "video.3gpp2";
         case 65570:
            return "audio.qcelp";
         default:
            return "object";
      }
   }

   public static final int getImpliedAttachmentType(String name) {
      if (name == null) {
         return -1;
      } else {
         name = StringUtilities.toLowerCase(name, 1701707776);
         if (name.endsWith(".amr")) {
            return 65539;
         } else if (name.endsWith(".gif")) {
            return 29;
         } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return 30;
         } else if (name.endsWith(".png")) {
            return 32;
         } else if (name.endsWith(".tif") || name.endsWith(".tiff")) {
            return 31;
         } else if (name.endsWith(".bmp")) {
            return 65547;
         } else if (name.endsWith(".wbmp")) {
            return 33;
         } else if (name.endsWith(".mid") || name.endsWith(".midi")) {
            return 65543;
         } else if (name.endsWith(".vcs")) {
            return 6;
         } else if (name.endsWith(".vcf")) {
            return 7;
         } else if (name.endsWith(".3gpp")) {
            return 65556;
         } else if (name.endsWith(".3g2") || name.endsWith(".3gpp2")) {
            return 65569;
         } else if (name.endsWith(".avi")) {
            return 65560;
         } else if (name.endsWith(".mov")) {
            return 65559;
         } else if (name.endsWith(".wmv")) {
            return 65558;
         } else if (name.endsWith(".mp4")) {
            return 65557;
         } else {
            return name.endsWith(".qcp") ? 65570 : -1;
         }
      }
   }

   public static final String addressToString(Object address, Object context) {
      if (!(address instanceof Object)) {
         if (address instanceof Object) {
            FriendlyNameAddressModel fnam = (FriendlyNameAddressModel)address;
            String str = fnam.getFriendlyName();
            if (str != null) {
               return ((StringBuffer)(new Object()))
                  .append(StringUtilities.removeChars(str, "()<>[]:;@,.\\\"").trim())
                  .append(" <")
                  .append(fnam.getAddress())
                  .append('>')
                  .toString();
            }
         }

         return address.toString();
      } else {
         PhoneNumberModel pnm = (PhoneNumberModel)address;
         boolean prependNDD = (MMSClientServiceBook.getAddressingOptionsFlags() & 2) != 0;
         return convertForTransmission(pnm.toString().toCharArray(), prependNDD);
      }
   }

   public static final String byteArrayToString(byte[] data, String encoding) {
      if (encoding != null) {
         try {
            return (String)(new Object(data, encoding));
         } finally {
            return StringUtilities.decodeBOM(data, 0, data.length);
         }
      } else {
         return StringUtilities.decodeBOM(data, 0, data.length);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final String convertForTransmission(char[] number, boolean prependNDD) {
      StringBuffer buffer = StringUtilitiesInternal.getScratchBuffer();
      StringBuffer dtmf = (StringBuffer)(new Object());
      synchronized (buffer) {
         boolean var10 = false /* VF: Semaphore variable */;

         String var5;
         try {
            var10 = true;
            buffer.setLength(0);
            PhoneNumberConverter.convertForTransmission(buffer, dtmf, number, true, false, false, true, prependNDD, false, false);
            buffer.append("/TYPE=PLMN");
            var5 = buffer.toString();
            var10 = false;
         } finally {
            if (var10) {
               buffer.setLength(0);
            }
         }

         buffer.setLength(0);
         return var5;
      }
   }

   public static final byte[] encrypt(byte[] data) {
      return ContentStoreEncryption.encrypt((InputStream)(new Object(data)));
   }

   public static final byte[] decrypt(byte[] param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokestatic net/rim/device/internal/io/store/ContentStoreEncryption.decrypt ([B)[B
      // 04: areturn
      // 05: astore 1
      // 06: goto 0a
      // 09: astore 1
      // 0a: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 0d: ldc_w "MMS decrypt failed."
      // 10: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 13: aconst_null
      // 14: areturn
      // try (0 -> 2): 3 null
      // try (0 -> 2): 5 null
   }

   public static final MMSSendVCalVerb getSendAsVCalVerb() {
      String mimeType = "text/x-vcalendar";
      Verb[] vcalVerbs = MIMEContentVerbRepository.getVerbs(mimeType);

      for (int i = 0; i < vcalVerbs.length; i++) {
         Verb var10000 = vcalVerbs[i];
         if (vcalVerbs[i] instanceof MMSSendVCalVerb) {
            return (MMSSendVCalVerb)var10000;
         }
      }

      return null;
   }

   static final boolean addToMessageList() {
      return getUnreadCountId() == 1;
   }

   static final int getUnreadCountId() {
      return MessagingUtil.getUnreadCountId("MMSFolder");
   }

   public static final void getMessageBodyText(StringBuffer buf, AttachmentDataProvider attachmentProvider) {
      Enumeration names = attachmentProvider.attachmentNames();
      if (names != null) {
         while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            int type = attachmentProvider.getAttachmentType(name);
            if (type == 62) {
               byte[] data = attachmentProvider.getAttachment(name).getData();
               MMSProtocolDataUnit pdu = new MMSProtocolDataUnit(data);
               getMessageBodyText(buf, pdu);
            } else if (isTextType(type)) {
               MMSAttachment attachment = attachmentProvider.getAttachment(name);
               byte[] data = decrypt(attachment.getData());
               if (data != null) {
                  buf.append(byteArrayToString(data, attachment.getCharset()));
               }
            }
         }
      }
   }

   public static final boolean modelIsAGroupWithAllInvalidAddresses(Object obj) {
      if (!(obj instanceof Object)) {
         return false;
      }

      GroupAddressCardModel group = (GroupAddressCardModel)obj;
      int size = group.size();
      boolean badAddressFound = false;
      boolean goodAddressFound = false;

      for (int i = 0; i < size; i++) {
         byte type = group.getAddressModelTypeAt(i);
         if ((type == 0 || type == 2) && group.getAddressModelAt(i) != null) {
            goodAddressFound = true;
         } else {
            badAddressFound = true;
         }
      }

      if (goodAddressFound) {
         if (badAddressFound) {
            group.warnUserSomeAddressesCannotReceive(MMSResources.getString(9));
         }

         return false;
      } else {
         return true;
      }
   }
}
