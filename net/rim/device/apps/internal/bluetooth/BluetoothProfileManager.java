package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.NumberUtilities;

class BluetoothProfileManager {
   protected BluetoothDeviceManagerImpl _btManager;
   protected BluetoothDevice _device;
   protected int _state;
   private long _eventLogGUID;
   private String _eventLogTitle;
   static final int PROFILE_MANAGER_ID_PHONE = 0;
   static final int PROFILE_MANAGER_ID_A2DP = 1;
   static final int PROFILE_MANAGER_ID_AVRCP = 2;
   static final int STATE_DISCONNECTED = 0;
   static final int STATE_CONNECTING = 1;
   static final int STATE_CONNECTED = 2;
   static final int STATE_DISCONNECTING = 3;
   static final int CONNECT_IN_PROGRESS = 0;
   static final int CONNECT_FAILED = 1;
   static final int CONNECT_FAILED_DISABLED_BY_IT_POLICY = 2;
   static final int CONNECT_FAILED_ALREADY_CONNECTED = 3;
   static final int CONNECT_FAILED_POWER_OFF = 4;
   private static final int LOG_STATE_CHANGE = 1396899840;
   private static final int CLEANING_UP = 1129076048;

   BluetoothProfileManager(BluetoothDeviceManagerImpl btManager, long eventLogGUID, String eventLogTitle) {
      this._btManager = btManager;
      this._eventLogGUID = eventLogGUID;
      this._eventLogTitle = eventLogTitle;
      EventLogger.register(this._eventLogGUID, this._eventLogTitle, 2);
   }

   synchronized BluetoothDevice getDevice() {
      return this._device;
   }

   int getID() {
      throw null;
   }

   boolean init() {
      throw null;
   }

   boolean canConnect(BluetoothDevice _1) {
      throw null;
   }

   boolean isConnected(BluetoothDevice _1) {
      throw null;
   }

   int connect(BluetoothDevice _1) {
      throw null;
   }

   void disconnect(BluetoothDevice _1) {
      throw null;
   }

   void cleanup() {
      this.log(1129076048);
   }

   boolean sniffModeDesired() {
      throw null;
   }

   String getName() {
      throw null;
   }

   protected void updateState(int state) {
      this.updateState(state, null);
   }

   protected void updateState(int state, BluetoothDevice device) {
      boolean connectFailed = this._state == 1 && state == 0;
      this._state = state;
      if (state == 1) {
         this._device = device;
      }

      if (this._device != null) {
         this._device.connectionUpdate(this, state, connectFailed);
      }

      if (state == 0) {
         this._device = null;
      }

      this.logResult(1396899840, state);
   }

   synchronized int getState(BluetoothDevice device) {
      if (this._device == null) {
         return 0;
      } else {
         return this._device == device ? this._state : 0;
      }
   }

   ConnectionStateField getStateField(BluetoothDevice device) {
      return new ConnectionStateField(this, device);
   }

   void devicePropertiesUpdated(BluetoothDevice device) {
   }

   void updateBatteryIndicators(int status) {
   }

   void log(int id) {
      EventLogger.logEvent(this._eventLogGUID, id, 0);
      System.out
         .println(
            ((StringBuffer)(new Object()))
               .append(this._eventLogTitle)
               .append(": ")
               .append((char)(id >> 24 & 0xFF))
               .append((char)(id >> 16 & 0xFF))
               .append((char)(id >> 8 & 0xFF))
               .append((char)(id & 0xFF))
               .toString()
         );
   }

   void logResult(int event, int result) {
      char high = NumberUtilities.intToHexDigit(result >> 4);
      char low = NumberUtilities.intToHexDigit(result);
      this.log(event | high << '\b' | low);
   }
}
