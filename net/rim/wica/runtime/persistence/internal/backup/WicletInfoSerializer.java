package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.wica.runtime.lifecycle.Alert;
import net.rim.wica.runtime.lifecycle.UninstallTaskInfo;
import net.rim.wica.runtime.lifecycle.UpgradeTaskInfo;
import net.rim.wica.runtime.lifecycle.WicletInfo;
import net.rim.wica.runtime.util.SerializerUtil;

final class WicletInfoSerializer extends AbstractSerializer {
   private static WicletInfoSerializer _instance;
   private static final byte NAME = 0;
   private static final byte URI = 1;
   private static final byte VERSION = 2;
   private static final byte VENDOR = 3;
   private static final byte DESCRIPTION = 4;
   private static final byte TARGET_FOLDER = 5;
   private static final byte SYSTEM_APPLICATION = 6;
   private static final byte ICON = 7;
   private static final byte RIBBON_VISIBLE = 8;
   private static final byte PROCESS_MSGS_IN_BACKGROUND = 9;
   private static final byte ENTRY_POINT = 10;
   private static final byte PERSISTENCE_MODE = 11;
   private static final byte MESSAGE_DELIVERY = 12;
   private static final byte ALERTS = 13;
   private static final byte ID = 14;
   private static final byte DEDICATED_SERVER_ID = 15;
   private static final byte INSTALL_DATE = 16;
   private static final byte STORE_ID = 17;
   private static final byte QUARANTINED = 19;
   private static final byte QUARANTINE_TASK = 21;
   private static final byte UNINSTALL_TASK = 22;
   private static final byte UPGRADE_TASK = 23;
   private static final byte HOVER_ICON = 24;
   private static final byte LANGUAGES = 25;
   private static final byte LANGUAGE_INDEX = 26;

   static final WicletInfoSerializer getInstance() {
      if (_instance == null) {
         _instance = new WicletInfoSerializer();
      }

      return _instance;
   }

   static final void nullInstance() {
      _instance = null;
   }

   @Override
   protected final void deserializeObjectField(DataBuffer buffer, Object obj, int type) {
      WicletInfo wicletInfo = (WicletInfo)obj;
      switch (type) {
         case -1:
         case 5:
         case 18:
         case 20:
         case 25:
            ConverterUtilities.skipField(buffer);
            return;
         case 0:
         default:
            wicletInfo.setName(ConverterUtilities.readString(buffer));
            return;
         case 1:
            wicletInfo.setUri(ConverterUtilities.readString(buffer));
            return;
         case 2:
            wicletInfo.setVersion(ConverterUtilities.readString(buffer));
            return;
         case 3:
            wicletInfo.setVendor(ConverterUtilities.readString(buffer));
            return;
         case 4:
            wicletInfo.setDescription(ConverterUtilities.readString(buffer));
            return;
         case 6:
            wicletInfo.setSystemApplication(SerializerUtil.readBoolean(buffer));
            return;
         case 7:
            wicletInfo.setIconUri(ConverterUtilities.readString(buffer));
            return;
         case 8:
            wicletInfo.setRibbonVisible(SerializerUtil.readBoolean(buffer));
            return;
         case 9:
            wicletInfo.setProcessMsgsInBackground(SerializerUtil.readBoolean(buffer));
            return;
         case 10:
            wicletInfo.setEntryPoint(ConverterUtilities.readInt(buffer));
            return;
         case 11:
            wicletInfo.setPersistenceMode(SerializerUtil.readBoolean(buffer));
            return;
         case 12:
            wicletInfo.setMessageDelivery(ConverterUtilities.readInt(buffer));
            return;
         case 13:
            wicletInfo.setAlerts((Alert[])AlertSerializer.getInstance().deserializeArray(buffer));
            return;
         case 14:
            wicletInfo.setId(ConverterUtilities.readLong(buffer));
            return;
         case 15:
            wicletInfo.setDedicatedServerId(ConverterUtilities.readLong(buffer));
            return;
         case 16:
            wicletInfo.setInstallDate(ConverterUtilities.readLong(buffer));
            return;
         case 17:
            wicletInfo.setStoreId(ConverterUtilities.readLong(buffer));
            return;
         case 19:
            SerializerUtil.readBoolean(buffer);
            return;
         case 21:
            QuarantineTaskInfoSerializer.getInstance().deserialize(buffer);
            return;
         case 22:
            wicletInfo.setUninstallTask((UninstallTaskInfo)UninstallTaskInfoSerializer.getInstance().deserialize(buffer));
            return;
         case 23:
            wicletInfo.setUpgradeTask((UpgradeTaskInfo)UpgradeTaskInfoSerializer.getInstance().deserialize(buffer));
            return;
         case 24:
            wicletInfo.setHoverIcon(ConverterUtilities.readString(buffer));
            return;
         case 26:
            wicletInfo.setLanguageIndex(ConverterUtilities.readInt(buffer));
      }
   }

   @Override
   protected final Object createObject() {
      return new WicletInfo();
   }

   private WicletInfoSerializer() {
   }
}
