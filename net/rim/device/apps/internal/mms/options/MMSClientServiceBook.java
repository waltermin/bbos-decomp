package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.system.Branding;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.internal.phone.pattern.SmartDialingOptions;

public final class MMSClientServiceBook {
   private static final int MMSC_VERSION = 0;
   private static final int MAX_MESSAGE_SIZE = 1;
   private static final int AUTO_RETRIEVAL_MODE = 2;
   private static final int RESTRICTED_SEND_MODE = 4;
   private static final int MAX_IMAGE_WIDTH = 5;
   private static final int MAX_IMAGE_HEIGHT = 6;
   private static final int MAX_RECIPIENT_COUNT = 7;
   private static final int MAX_RECIPIENT_BYTE_SIZE = 8;
   private static final int SEND_TEXT_AS_SIMPLE_CONTENT = 9;
   private static final int CONNECTION_TIMEOUT = 10;
   private static final int ALLOW_HOME_ONLY = 11;
   private static final int MAX_TRANSPORT_THREADS = 12;
   private static final int RESTRICTED_SIZE_MODE = 13;
   private static final int DEFAULT_RECEPTION_MODE = 14;
   private static final int DEFAULT_AUTO_RETRIEVAL_MODE = 15;
   private static final int DEFAULT_OPTION_FLAGS = 16;
   private static final int ALLOW_SEND_IMAGE_REDUCTION = 17;
   private static final int MAX_VOICE_NOTES_RECORD_TIME = 18;
   private static final int MAX_VOICE_NOTES_RECORD_SIZE = 19;
   private static final int MAX_COMPOSE_TEXT_SIZE = 20;
   private static final int RETRIEVAL_URL_SCHEME = 21;
   private static final int INFER_ACK_URL = 22;
   private static final int COMPOSE_FROM_SCHEME = 23;
   private static final int LOCKED_OPTIONS_FLAGS = 24;
   private static final int ONE_VIDEO_PER_MMS = 25;
   private static final int ADDRESSING_OPTIONS_FLAGS = 26;
   private static final int UAPROF_URL = 32;
   private static final int UAPROF_USERAGENTNAME = 33;
   public static final int MAX_IMAGE_REDUCTION_BEFORE_SEND = 4;
   public static final int FROM_FIELD_NDD_REQUIRED = 1;
   public static final int TO_FIELD_NDD_REQUIRED = 2;

   public static final int getAutoRetrievalMode() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int mode = sbr.getIntegerField(2);
         if (mode != -1) {
            return mode;
         }
      }

      return MMSTransportServiceBook.getAutoRetrievalMode();
   }

   public static final void setAutoRetrievalMode(int mode) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(2, mode);
      sbr.save();
   }

   public static final int getMMSCVersion() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int version = sbr.getIntegerField(0);
         if (version == -1) {
            version = MMSTransportServiceBook.getMMSCVersion();
         }

         if (version < 16 || version > 19) {
            version = 16;
         }

         return version;
      } else {
         return 16;
      }
   }

   public static final void setMMSCVersion(int version) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(0, version);
      sbr.save();
   }

   public static final String getUAProfUrl() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         String url = sbr.getStringField(32);
         if (url == null) {
            url = "";
         }

         return url;
      } else {
         return "";
      }
   }

   public static final void setUAProfUrl(String url) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      if (url != null && url.length() == 0) {
         url = null;
      }

      sbr.setField(32, url);
      sbr.save();
   }

   public static final String getUserAgentName() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      return sbr != null ? sbr.getStringField(33) : null;
   }

   public static final void setUserAgentName(String name) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      if (name != null && name.length() == 0) {
         name = null;
      }

      sbr.setField(33, name);
      sbr.save();
   }

   public static final int getRestrictedSendMode() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int mode = sbr.getIntegerField(4);
         if (mode != -1) {
            return mode;
         }
      }

      return 0;
   }

   public static final void setRestrictedSendMode(int mode) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(4, mode);
      sbr.save();
   }

   public static final int getRestrictedSizeMode() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int mode = sbr.getIntegerField(13);
         if (mode != -1) {
            return mode;
         }
      }

      return 1;
   }

   public static final void setRestrictedSizeMode(int mode) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(13, mode);
      sbr.save();
   }

   public static final int getMaxMessageSize() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int size = sbr.getIntegerField(1);
         if (size != -1) {
            return size;
         }
      }

      return 307200;
   }

   public static final void setMaxMessageSize(int size) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(1, size);
      sbr.save();
   }

   public static final int getMaxComposeTextLength() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int size = sbr.getIntegerField(20);
         if (size != -1) {
            return size;
         }
      }

      return 64000;
   }

   public static final void setMaxComposeTextLength(int size) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(20, size);
      sbr.save();
   }

   public static final int getMaxImageWidth() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int width = sbr.getIntegerField(5);
         if (width != -1) {
            return width;
         }
      }

      return 640;
   }

   public static final void setMaxImageWidth(int width) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(5, width);
      sbr.save();
   }

   public static final int getMaxImageHeight() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int height = sbr.getIntegerField(6);
         if (height != -1) {
            return height;
         }
      }

      return 480;
   }

   public static final void setMaxImageHeight(int height) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(6, height);
      sbr.save();
   }

   public static final int getMaxRecipientCount() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int count = sbr.getIntegerField(7);
         if (count != -1) {
            return count;
         }
      }

      return 100;
   }

   public static final void setMaxRecipientCount(int count) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(7, count);
      sbr.save();
   }

   public static final int getMaxRecipientByteSize() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int size = sbr.getIntegerField(8);
         if (size != -1) {
            return size;
         }
      }

      return getMaxMessageSize();
   }

   public static final void setMaxRecipientByteSize(int size) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(8, size);
      sbr.save();
   }

   public static final int getMaxTransportThreads() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int count = sbr.getIntegerField(12);
         if (count != -1) {
            return count;
         }
      }

      return 1;
   }

   public static final void setMaxTransportThreads(int count) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(12, count);
      sbr.save();
   }

   public static final boolean sendTextAsSimpleContent() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      return sbr != null ? sbr.getBooleanField(9) : false;
   }

   public static final void enableSendTextAsSimpleContent(boolean enable) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(9, enable);
      sbr.save();
   }

   public static final int getRetrievalUrlScheme() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int scheme = sbr.getIntegerField(21);
         if (scheme != -1) {
            return scheme;
         }
      }

      return isDeviceVerizonBranded() ? 1 : 0;
   }

   public static final void setRetrievalUrlScheme(int scheme) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(21, scheme);
      sbr.save();
   }

   public static final int getComposeFromScheme() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int scheme = sbr.getIntegerField(23);
         if (scheme != -1) {
            return scheme;
         }
      }

      return 0;
   }

   public static final void setComposeFromScheme(int scheme) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(23, scheme);
      sbr.save();
   }

   public static final boolean inferMessageAcknowledgementUrl() {
      boolean defaultValue = isDeviceVerizonBranded();
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      return sbr != null ? sbr.getBooleanField(22, defaultValue) : defaultValue;
   }

   public static final void enableInferMessageAcknowledgementUrl(boolean enable) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(22, enable);
      sbr.save();
   }

   public static final boolean allowImageReductionBeforeSend() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      return sbr != null ? sbr.getBooleanField(17, true) : true;
   }

   public static final void enableImageReductionBeforeSend(boolean enable) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(17, enable);
      sbr.save();
   }

   public static final boolean allowHomeOnly() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      return sbr != null ? sbr.getBooleanField(11, RadioInfo.getNetworkType() != 5) : RadioInfo.getNetworkType() != 5;
   }

   public static final void enableAllowHomeOnly(boolean enable) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(11, enable);
      sbr.save();
   }

   public static final int getConnectionTimeout() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int seconds = sbr.getIntegerField(10);
         if (seconds != -1) {
            return seconds;
         }
      }

      return 30;
   }

   public static final void setConnectionTimeout(int seconds) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(10, seconds);
      sbr.save();
   }

   public static final int getDefaultReceptionMode() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int mode = sbr.getIntegerField(14);
         if (mode != -1) {
            return mode;
         }
      }

      return 0;
   }

   public static final void setDefaultReceptionMode(int mode) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(14, mode);
      sbr.save();
   }

   public static final int getDefaultAutomaticRetrievalMode() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int mode = sbr.getIntegerField(15);
         if (mode != -1) {
            return mode;
         }
      }

      return allowHomeOnly() ? 2 : 0;
   }

   public static final void setDefaultAutomaticRetrievalMode(int mode) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(15, mode);
      sbr.save();
   }

   public static final boolean oneVideoPerMMS() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      return sbr != null ? sbr.getBooleanField(25) : false;
   }

   public static final void setOneVideoPerMMS(boolean enable) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(25, enable);
      sbr.save();
   }

   public static final int getLockedOptionsFlags() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int flags = sbr.getIntegerField(24);
         if (flags != -1) {
            return flags;
         }
      }

      return 0;
   }

   public static final void setLockedOptionsFlags(int flags) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(24, flags);
      sbr.save();
   }

   public static final boolean isLockedOption(int flag) {
      return (getLockedOptionsFlags() & flag) != 0;
   }

   public static final int getDefaultOptionFlags() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int flags = sbr.getIntegerField(16);
         if (flags != -1) {
            return flags;
         }
      }

      switch (Branding.getVendorId()) {
         case 100:
            return 15;
         case 101:
         case 102:
         default:
            return 0;
      }
   }

   public static final void setDefaultOptionFlags(int flags) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(16, flags);
      sbr.save();
   }

   public static final int getMaxVoiceNoteRecordTime() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int time = sbr.getIntegerField(18);
         if (time > 0) {
            return time;
         }
      }

      return 60;
   }

   public static final void setMaxVoiceNoteRecordTime(int time) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(18, time);
      sbr.save();
   }

   public static final int getMaxVoiceNoteRecordSize() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int size = sbr.getIntegerField(19);
         if (size > 0) {
            return size;
         }
      }

      return 100000;
   }

   public static final void setMaxVoiceNoteRecordSize(int size) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(19, size);
      sbr.save();
   }

   public static final int getAddressingOptionsFlags() {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstanceNoCreate();
      if (sbr != null) {
         int flags = sbr.getIntegerField(26);
         if (flags != -1) {
            return flags;
         }
      }

      return SmartDialingOptions.getOptions().autoAppendNDDForSMS() ? 2 : 0;
   }

   public static final void setAddressingOptionsFlags(int flags) {
      MMSClientServiceBookRecord sbr = MMSClientServiceBookRecord.getInstance();
      sbr.setField(26, flags);
      sbr.save();
   }

   private static final boolean isDeviceVerizonBranded() {
      switch (Branding.getVendorId()) {
         case 105:
         case 226:
            return true;
         default:
            return false;
      }
   }
}
