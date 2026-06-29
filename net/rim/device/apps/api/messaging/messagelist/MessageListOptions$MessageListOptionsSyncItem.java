package net.rim.device.apps.api.messaging.messagelist;

import java.util.Enumeration;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

final class MessageListOptions$MessageListOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int MIN_DATA_LENGTH = 10;
   private static final int MIN_VERSION = 0;
   private static final int MAX_VERSION = 1;
   private static final int DB_VERSION = 1;
   private static final int END_OF_TAGGED_DATA = 0;
   private static final int SERVICE_RECORD_NAME_TAG = 1;
   private static final int SERVICE_RECORD_UID_TAG = 2;
   private static final int DELETE_ON_LOCATION_TAG = 3;
   private static final int SPELL_CHECK_BEFORE_SEND_TAG = 4;
   private static final int KEEP_MESSAGES_DURATION_TAG = 5;
   private static final int HIDE_SENT_TAG = 6;
   private static final int DISPLAY_MESSAGE_COUNT_TAG = 8;
   private static final int DISPLAY_NEW_MESSAGE_INDICTAOR_TAG = 9;
   private static final int DSN_SETTINGS_TAG = 10;
   private static final int MESSAGE_LIST_LINE_MODE = 11;
   private static final int SMS_EMAIL_INBOX_TAG = 12;
   private static final int AUTO_DOWNLOAD_ATTACHMENT_TAG = 13;
   private static final int AUTO_DOWNLOAD_ATTACHMENT_HIGH_SPEED_ONLY_TAG = 14;
   private static final int LIST_SEPARATOR_APPEARANCE = 15;
   private static final int CONFIRM_MARK_PRIOR_OPENED_TAG = 16;

   @Override
   public final String getSyncName() {
      return "Message List Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      MessageListOptions options = MessageListOptions.getOptions();
      if (version >= 0 && version <= 1) {
         short keepMessagesDuration = 0;
         boolean updateServiceView = false;

         label249:
         try {
            int length = buffer.readShort();
            buffer.readByte();
            if (length < 10) {
               return false;
            }

            options.setFlag(1, buffer.readBoolean());
            options.setFlag(2, buffer.readBoolean());
            buffer.skipBytes(1);
            options.setFlag(8, buffer.readByte() != 0);
            buffer.skipBytes(1);
            options.setFlag(256, buffer.readBoolean());
            buffer.skipBytes(1);
            options.setFlag(16, buffer.readBoolean());
            options.setFlag(4, buffer.readBoolean());
            buffer.skipBytes(1);
            if (length >= 11) {
               buffer.skipBytes(1);
               if (length >= 12) {
                  options.setFlag(32, buffer.readBoolean());
                  if (length >= 13) {
                     options.setFlag(64, buffer.readBoolean());
                     if (length >= 14) {
                        label240:
                        try {
                           String serviceName = null;
                           String serviceUid = null;

                           label237:
                           while (!buffer.eof()) {
                              switch (ConverterUtilities.getType(buffer, true)) {
                                 case -1:
                                 case 7:
                                    ConverterUtilities.skipField(buffer);
                                    break;
                                 case 0:
                                    break label237;
                                 case 1:
                                 default:
                                    serviceName = ConverterUtilities.readString(buffer);
                                    break;
                                 case 2:
                                    serviceUid = ConverterUtilities.readString(buffer);
                                    break;
                                 case 3:
                                    options.setDeleteOnLocation(serviceName, serviceUid, ConverterUtilities.readInt(buffer));
                                    break;
                                 case 4:
                                    options.setFlag(128, ConverterUtilities.readInt(buffer) != 0);
                                    break;
                                 case 5:
                                    keepMessagesDuration = ConverterUtilities.readShort(buffer);
                                    break;
                                 case 6:
                                    options.setFlag(512, ConverterUtilities.readInt(buffer) != 0);
                                    break;
                                 case 8:
                                    options.setDisplayMessageCount(ConverterUtilities.readShort(buffer));
                                    break;
                                 case 9:
                                    options.setFlag(1024, ConverterUtilities.readInt(buffer) != 0);
                                    break;
                                 case 10:
                                    options.setDSNSettings(serviceName, serviceUid, (byte)ConverterUtilities.readInt(buffer));
                                    break;
                                 case 11:
                                    options.setMessageListLineMode(ConverterUtilities.readShort(buffer));
                                    break;
                                 case 12:
                                    options.setSMSEmailInbox(ConverterUtilities.readShort(buffer));
                                    updateServiceView = true;
                                    break;
                                 case 13:
                                    short value = ConverterUtilities.readShort(buffer);
                                    if (value != 0 && value != 1 && value != 2) {
                                       options.setAutoDownloadAttachments((short)0);
                                    } else {
                                       options.setAutoDownloadAttachments(value);
                                    }
                                    break;
                                 case 14:
                                    int intValuex = ConverterUtilities.readInt(buffer);
                                    if (intValuex != 0 && intValuex != 1) {
                                       options.setHighSpeedNetworkOnlyForAutoDownloadAttachment(true);
                                    } else {
                                       options.setHighSpeedNetworkOnlyForAutoDownloadAttachment(intValuex != 0);
                                    }
                                    break;
                                 case 15:
                                    options.setListSeparatorAppearance(ConverterUtilities.readShort(buffer));
                                    break;
                                 case 16:
                                    int intValue = ConverterUtilities.readInt(buffer);
                                    if (intValue != 0 && intValue != 1) {
                                       options.setConfirmMarkPriorOpened(true);
                                    } else {
                                       options.setConfirmMarkPriorOpened(intValue != 0);
                                    }
                              }
                           }
                        } finally {
                           break label240;
                        }
                     }
                  }
               }
            }
         } finally {
            break label249;
         }

         if (keepMessagesDuration == 0) {
            keepMessagesDuration = MessageListOptions.KEEP_MESSAGES_DURATION_CHOICES[MessageListOptions.FOREVER_KEEP_MESSAGES_DURATION_INDEX];
         }

         options.setKeepMessagesDuration(keepMessagesDuration);
         switch (MessageListOptions._options.getDisplayMessageCount()) {
            case 0:
               if (!MessageListOptions._options.getFlag(1024)) {
                  MessageListOptions._options.setFlag(1024, true);
               }
            default:
               if (updateServiceView) {
                  ShowMessageApp.postEvent(-8639396151207124460L, 0, 0, new Long(-4696470826620059293L), null);
               }

               options.commit();
               return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      MessageListOptions options = MessageListOptions.getOptions();
      DataBuffer tmpBuffer = new DataBuffer();
      tmpBuffer.setBigEndian(false);
      tmpBuffer.writeBoolean(options.getFlag(1));
      tmpBuffer.writeBoolean(options.getFlag(2));
      tmpBuffer.writeBoolean(true);
      tmpBuffer.writeBoolean(options.getFlag(8));
      tmpBuffer.writeByte(0);
      tmpBuffer.writeBoolean(options.getFlag(256));
      tmpBuffer.writeBoolean(true);
      tmpBuffer.writeBoolean(options.getFlag(16));
      tmpBuffer.writeBoolean(options.getFlag(4));
      tmpBuffer.writeByte(0);
      tmpBuffer.writeByte(3);
      tmpBuffer.writeBoolean(options.getFlag(32));
      tmpBuffer.writeBoolean(options.getFlag(64));
      Enumeration e = options.getServiceRecordKeys();

      while (e.hasMoreElements()) {
         ServiceKey serviceKey = (ServiceKey)e.nextElement();
         int deleteOnSetting = options.getDeleteOnLocation(serviceKey.getName(), serviceKey.getUid(), -1);
         byte dsnSettings = options.getDSNSettings(serviceKey.getName(), serviceKey.getUid());
         ConverterUtilities.writeStringSmart(tmpBuffer, 1, serviceKey.getName());
         ConverterUtilities.writeStringSmart(tmpBuffer, 2, serviceKey.getUid());
         ConverterUtilities.convertInt(tmpBuffer, 3, deleteOnSetting, 1);
         ConverterUtilities.convertInt(tmpBuffer, 10, dsnSettings, 1);
      }

      ConverterUtilities.writeShort(tmpBuffer, 5, options.getKeepMessagesDuration());
      ConverterUtilities.convertInt(tmpBuffer, 6, options.getFlag(512) ? 1 : 0, 1);
      ConverterUtilities.writeShort(tmpBuffer, 8, options.getDisplayMessageCount());
      ConverterUtilities.convertInt(tmpBuffer, 9, options.getFlag(1024) ? 1 : 0, 1);
      ConverterUtilities.writeShort(tmpBuffer, 11, (short)options.getMessageListLineMode());
      ConverterUtilities.writeShort(tmpBuffer, 12, options.getSMSEmailInbox());
      ConverterUtilities.writeShort(tmpBuffer, 13, options.getAutoDownloadAttachments());
      ConverterUtilities.convertInt(tmpBuffer, 14, options.getHighSpeedNetworkOnlyForAutoDownloadAttachment() ? 1 : 0, 1);
      ConverterUtilities.convertInt(tmpBuffer, 16, options.getConfirmMarkPriorOpened() ? 1 : 0, 1);
      ConverterUtilities.writeShort(tmpBuffer, 15, options.getListSeparatorAppearance());
      tmpBuffer.writeShort(0);
      tmpBuffer.writeByte(0);
      byte[] data = tmpBuffer.getArray();
      int start = tmpBuffer.getArrayStart();
      int end = tmpBuffer.getArrayPosition();
      buffer.writeShort(end - start);
      buffer.writeByte(0);
      buffer.write(data, start, end - start);
      return true;
   }
}
