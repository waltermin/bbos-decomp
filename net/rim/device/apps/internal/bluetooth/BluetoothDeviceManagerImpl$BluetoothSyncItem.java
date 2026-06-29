package net.rim.device.apps.internal.bluetooth;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.internal.bluetooth.BluetoothME;

final class BluetoothDeviceManagerImpl$BluetoothSyncItem extends OTASyncCapableSyncItem implements SyncCollectionStatusProvider {
   private final BluetoothDeviceManagerImpl this$0;
   private static final int TYPE_FLAGS = 0;
   private static final int TYPE_LOCAL_NAME = 1;
   private static final int TYPE_PAIRED_DEVICE = 2;
   private static final int TYPE_MAC_ADDRESS = 4;
   private static final int TYPE_PIN = 5;
   private static final int TYPE_ALLOW_OUTGOING_CALLS = 6;
   private static final int TYPE_ADDRESS_BOOK_TRANSFER_MODE = 7;
   private static final int TYPE_ADDRESS_BOOK_CATEGORIES = 8;
   private static final int TYPE_SECURITY_MODE = 9;
   private static final int TYPE_LAST_CONNECTED_DEVICE = 10;
   private static final int FLAG_POWER_ON = 1;
   private static final int FLAG_DISCOVERABLE = 2;
   private static final int FLAG_LED_INDICATOR_DISABLED = 4;
   private static final int FLAG_CONNECT_ON_POWER_UP_DISABLED = 8;

   BluetoothDeviceManagerImpl$BluetoothSyncItem(BluetoothDeviceManagerImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public final String getSyncName() {
      return "Bluetooth Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      BluetoothOptionsData data = this.this$0._bluetoothOptionsData;
      int flags = 0;
      if (data._powerOn) {
         flags |= 1;
      }

      if (data._discoverable) {
         flags |= 2;
      }

      if (!data._ledIndicatorEnabled) {
         flags |= 4;
      }

      if (!data._connectOnPowerUpEnabled) {
         flags |= 8;
      }

      ConverterUtilities.writeInt(buffer, 0, flags);
      ConverterUtilities.writeString(buffer, 1, data._localName);
      Vector pairedDeviceData = data._pairedDeviceData;
      int length = pairedDeviceData.size();

      for (int i = 0; i < length; i++) {
         BluetoothDeviceData deviceData = (BluetoothDeviceData)pairedDeviceData.elementAt(i);
         byte[] b = deviceData.serialize(buffer.isBigEndian());
         ConverterUtilities.writeByteArray(buffer, 2, b);
      }

      ConverterUtilities.writeInt(buffer, 5, DeviceInfo.getDeviceId());
      ConverterUtilities.writeInt(buffer, 6, data._allowOutgoingCalls);
      ConverterUtilities.writeInt(buffer, 7, data._addressBookTransferMode);
      ConverterUtilities.writeString(buffer, 8, data._addressBookCategories.getCategoryNames(true));
      ConverterUtilities.writeInt(buffer, 9, data._securityMode);
      if (data._lastConnectedDevice != null) {
         ConverterUtilities.writeByteArray(buffer, 10, data._lastConnectedDevice);
      }

      return true;
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      BluetoothOptionsData data = this.this$0._bluetoothOptionsData;
      if (this.this$0._isConnected) {
         return false;
      }

      if (BluetoothME.isPowerOn()) {
         this.this$0.requestPowerOff();

         for (int i = 0; i < 5; i++) {
            label289:
            try {
               Thread.currentThread();
               Thread.sleep(1000);
            } finally {
               break label289;
            }

            if (!BluetoothME.isPowerOn()) {
               break;
            }
         }

         if (BluetoothME.isPowerOn()) {
            return false;
         }
      }

      synchronized (this.this$0._pairedDevices) {
         for (int i = this.this$0._pairedDevices.size() - 1; i >= 0; i--) {
            BluetoothDevice device = (BluetoothDevice)this.this$0._pairedDevices.elementAt(i);
            device.deleteLinkKey();
         }
      }

      data._pairedDeviceData = (Vector)(new Object());
      String oldLocalName = data._localName;
      int pin = 0;

      try {
         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(buffer);
            } finally {
               break;
            }

            switch (type) {
               case -1:
               case 3:
               case 4:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 0:
               default:
                  int flags = ConverterUtilities.readInt(buffer);
                  data._powerOn = (flags & 1) != 0;
                  data._discoverable = (flags & 2) != 0;
                  data._ledIndicatorEnabled = (flags & 4) == 0;
                  data._connectOnPowerUpEnabled = (flags & 8) == 0;
                  break;
               case 1:
                  data._localName = ConverterUtilities.readString(buffer);
                  break;
               case 2:
                  byte[] b = ConverterUtilities.readByteArray(buffer);
                  BluetoothDeviceData deviceData = BluetoothDeviceData.deserialize(b, buffer.isBigEndian());
                  if (deviceData != null) {
                     data._pairedDeviceData.addElement(deviceData);
                  }
                  break;
               case 5:
                  pin = ConverterUtilities.readInt(buffer);
                  break;
               case 6:
                  data._allowOutgoingCalls = ConverterUtilities.readInt(buffer);
                  break;
               case 7:
                  data._addressBookTransferMode = ConverterUtilities.readInt(buffer);
                  break;
               case 8:
                  data._addressBookCategories = (CategoriesModel)(new Object(ConverterUtilities.readString(buffer)));
                  break;
               case 9:
                  data._securityMode = ConverterUtilities.readInt(buffer);
                  break;
               case 10:
                  data._lastConnectedDevice = ConverterUtilities.readByteArray(buffer);
            }
         }
      } finally {
         ;
      }

      if (pin == 0 || pin != DeviceInfo.getDeviceId()) {
         data._pairedDeviceData.removeAllElements();
         data._localName = oldLocalName;
      }

      this.this$0._persistentObject.commit();
      this.this$0.init();
      if (data._powerOn) {
         this.this$0.requestPowerOn();
      }

      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      return true;
   }

   @Override
   public final boolean isWritableForSerialSync() {
      return !this.this$0._isConnected;
   }

   @Override
   public final boolean isReadableForSerialSync() {
      return true;
   }

   @Override
   public final boolean isWritableForOTASL() {
      return true;
   }

   @Override
   public final int getOTASLControlMask() {
      return 0;
   }
}
