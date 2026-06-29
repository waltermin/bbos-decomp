package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.cldc.util.TimeService;
import net.rim.device.internal.deviceoptions.AutoOnOff;
import net.rim.device.internal.deviceoptions.DeviceOptions;
import net.rim.device.internal.deviceoptions.Owner;
import net.rim.device.internal.i18n.DateTimeFormatOptions;
import net.rim.device.internal.ui.UiSettings;

final class DeviceOptionsSyncItem extends SyncItem implements SyncCollectionStatusProvider, SyncEventListener {
   private boolean _inSerialSync = false;
   private boolean _deferredRemove = false;
   static final int DATA_VERSION_MIN = 1;
   static final int DATA_VERSION_MAX = 2;
   static final int DATA_SIZE = 290;
   private static final int USER_NAME_OFFSET = 117;

   final void getUserInfo(DataBuffer buffer) {
      fillZero(buffer, 117);
      this.writeString(buffer, Owner.getOwnerName(), 40);
      this.writeString(buffer, Owner.getOwnerInfo(), 128);
      fillZero(buffer, 5);
   }

   final void setData(DataBuffer buffer) {
      try {
         int position = buffer.getPosition();

         label100:
         try {
            UiSettings.setDisplayContrast(MathUtilities.clamp(0, buffer.readShort() * 100 / 30, 100));
         } finally {
            break label100;
         }

         int fontSize = buffer.readByte();
         if (fontSize == 0) {
            fontSize = 10;
         }

         if (fontSize == 1) {
            fontSize = 8;
         }

         label94:
         try {
            Font.setDefaultFontForSystem(FontFamily.FAMILY_SYSTEM, 0, fontSize, 3);
         } finally {
            break label94;
         }

         buffer.readByte();
         buffer.readShort();
         buffer.readShort();
         buffer.skipBytes(11);
         buffer.readByte();
         buffer.readByte();
         buffer.readByte();
         DateTimeFormatOptions.setTimeFormat(buffer.readShort());
         AutoOnOff.setWeekdayOnTime((int)DeviceOptions.readTime(buffer));
         AutoOnOff.setWeekdayOffTime((int)DeviceOptions.readTime(buffer));
         AutoOnOff.enableWeekdayAutoOnOff(buffer.readByte() != 0);
         buffer.readByte();
         AutoOnOff.setWeekendOnTime((int)DeviceOptions.readTime(buffer));
         AutoOnOff.setWeekendOffTime((int)DeviceOptions.readTime(buffer));
         AutoOnOff.enableWeekendAutoOnOff(buffer.readByte() != 0);
         buffer.readByte();
         buffer.skipBytes(36);
         buffer.skipBytes(13);
         Owner.setOwnerName(stripCR(this.readString(buffer, 40)));
         Owner.setOwnerInfo(stripCR(this.readString(buffer, 128)));
         buffer.skipBytes(1);
         buffer.readByte();
         buffer.skipBytes(1);
         TimeService ts = TimeService.getTimeService();
         ts.setDefaultTimeZone(ts.getTimeZoneIDFromSerialSyncID(buffer.readShort()));
         buffer.setPosition(position);
         DeviceOptions.getInstance().setLegacyDeviceOptions(buffer);
      } finally {
         return;
      }
   }

   final void setUserInfo(DataBuffer buffer) {
      try {
         buffer.skipBytes(117);
         Owner.setOwnerName(stripCR(this.readString(buffer, 40)));
         Owner.setOwnerInfo(stripCR(this.readString(buffer, 128)));
      } finally {
         return;
      }
   }

   @Override
   public final void syncEventOccurred(int eventId, Object obj) {
      switch (eventId) {
         case 1:
         default:
            this._inSerialSync = true;
            this._deferredRemove = false;
            return;
         case 2:
            this._inSerialSync = false;
            this._deferredRemove = false;
         case 0:
      }
   }

   @Override
   public final boolean isWritableForSerialSync() {
      return true;
   }

   @Override
   public final boolean isReadableForSerialSync() {
      return true;
   }

   @Override
   public final boolean isWritableForOTASL() {
      return false;
   }

   @Override
   public final int getOTASLControlMask() {
      return 0;
   }

   private static final void fillZero(DataBuffer buffer, int num) {
      while (num-- > 0) {
         buffer.writeByte(0);
      }
   }

   private final void writeString(DataBuffer buffer, String value, int length) {
      int len = length - 1;
      if (len > value.length()) {
         len = value.length();
      }

      int i;
      for (i = 0; i < len; i++) {
         buffer.writeByte(value.charAt(i));
      }

      fillZero(buffer, length - i);
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final String getSyncName() {
      return "Device Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncObjectCount() {
      return 0;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      return new Object[0];
   }

   @Override
   public final boolean removeAllSyncObjects() {
      if (this._inSerialSync) {
         this._deferredRemove = true;
         return true;
      } else {
         this.reallyRemoveAllSyncObjects();
         return true;
      }
   }

   private final boolean reallyRemoveAllSyncObjects() {
      AutoOnOff.resetToDefaults();
      Owner.resetToDefaults();
      this._deferredRemove = false;
      return true;
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      if (this._inSerialSync && this._deferredRemove) {
         this.reallyRemoveAllSyncObjects();
      }

      try {
         if (version >= 1 && version <= 2) {
            int size = buffer.readShort();
            if (size < 290) {
               return false;
            }

            buffer.readByte();
            this.setData(buffer);
            return true;
         } else {
            return false;
         }
      } finally {
         ;
      }
   }

   private final String readString(DataBuffer buffer, int maxLength) {
      if (maxLength > buffer.available()) {
         throw new Object();
      }

      byte[] data = buffer.getArray();
      int start = buffer.getArrayPosition();
      int len = 0;

      while (len < maxLength && data[start + len] != 0) {
         len++;
      }

      buffer.skipBytes(maxLength);
      return (String)(new Object(data, start, len));
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      buffer.writeShort(0);
      buffer.writeByte(0);
      return true;
   }

   DeviceOptionsSyncItem() {
      SyncManager.getInstance().addSyncEventListener(this);
   }

   private static final String stripCR(String str) {
      StringBuffer buffer = (StringBuffer)(new Object());
      int end = str.length();

      for (int lv = 0; lv < end; lv++) {
         char ch = str.charAt(lv);
         if (ch != '\r') {
            buffer.append(ch);
         }
      }

      return buffer.toString();
   }
}
