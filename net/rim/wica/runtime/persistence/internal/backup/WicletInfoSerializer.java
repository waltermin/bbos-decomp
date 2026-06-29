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
   private static final byte NAME;
   private static final byte URI;
   private static final byte VERSION;
   private static final byte VENDOR;
   private static final byte DESCRIPTION;
   private static final byte TARGET_FOLDER;
   private static final byte SYSTEM_APPLICATION;
   private static final byte ICON;
   private static final byte RIBBON_VISIBLE;
   private static final byte PROCESS_MSGS_IN_BACKGROUND;
   private static final byte ENTRY_POINT;
   private static final byte PERSISTENCE_MODE;
   private static final byte MESSAGE_DELIVERY;
   private static final byte ALERTS;
   private static final byte ID;
   private static final byte DEDICATED_SERVER_ID;
   private static final byte INSTALL_DATE;
   private static final byte STORE_ID;
   private static final byte QUARANTINED;
   private static final byte QUARANTINE_TASK;
   private static final byte UNINSTALL_TASK;
   private static final byte UPGRADE_TASK;
   private static final byte HOVER_ICON;
   private static final byte LANGUAGES;
   private static final byte LANGUAGE_INDEX;

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
