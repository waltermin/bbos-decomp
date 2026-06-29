package net.rim.device.apps.internal.bluetooth;

import net.rim.device.internal.bluetooth.BluetoothAVRCP;
import net.rim.device.internal.bluetooth.BluetoothAVRCPListener;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.media.MediaRemoteControl;

final class BluetoothAVRCPManager extends BluetoothProfileManager implements BluetoothAVRCPListener {
   private int _avrcpHandle;
   private static final long LOG_GUID;
   private static final int LOG_CONNECT;
   private static final int LOG_DISCONNECT;
   private static final int LOG_CONNECT_RESPONSE;
   private static final int LOG_PANEL_PRESS;
   private static final int LOG_PANEL_HOLD;
   private static final int LOG_PANEL_RELEASE;

   BluetoothAVRCPManager(BluetoothDeviceManagerImpl btManager) {
      super(btManager, -451864196759457947L, "net.rim.bluetooth.avrcp");
   }

   @Override
   final int getID() {
      return 2;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   final boolean init() {
      if (!BluetoothAVRCP.isSupported()) {
         return false;
      }

      BluetoothAVRCP.addListener(super._btManager, this);

      try {
         this._avrcpHandle = BluetoothAVRCP.registerChannel(4364);
         return true;
      } catch (Throwable var3) {
         System.out.println(ex);
         return true;
      }
   }

   @Override
   final boolean canConnect(BluetoothDevice device) {
      return device.hasAVRCPController();
   }

   @Override
   final boolean isConnected(BluetoothDevice device) {
      return super._device == device;
   }

   @Override
   final synchronized int connect(BluetoothDevice device) {
      if (!BluetoothME.isPowerOn()) {
         return 4;
      }

      if (super._state != 0) {
         return 3;
      }

      if (device.hasAVRCPController()) {
         if (!BluetoothAVRCP.isEnabled()) {
            return 2;
         }

         int rc = BluetoothAVRCP.connect(this._avrcpHandle, device.getAddress());
         this.logResult(1129185280, rc);
         if (rc != 0 && rc != 2) {
            return 1;
         }

         this.updateState(1, device);
         return 0;
      } else {
         return 1;
      }
   }

   @Override
   final void disconnect(BluetoothDevice device) {
      if (super._device == device) {
         int rc = BluetoothAVRCP.disconnect(this._avrcpHandle);
         this.logResult(1145634816, rc);
         if (rc == 0 || rc == 2) {
            this.updateState(3);
         }
      }
   }

   @Override
   final void cleanup() {
      super.cleanup();
      if (super._device != null) {
         this.avrcpDisconnected(0);
      }
   }

   @Override
   final boolean sniffModeDesired() {
      return true;
   }

   @Override
   final String getName() {
      return BluetoothMainScreen.getString(80);
   }

   @Override
   public final void avrcpIncomingConnection(int handle, byte[] address) {
      if (!BluetoothAVRCP.isEnabled()) {
         BluetoothAVRCP.connectResponse(this._avrcpHandle, false);
      } else {
         BluetoothDevice device = super._btManager.getPairedDevice(address);
         if (device != null) {
            device.addSupportedProfile(32);
            int rc = BluetoothAVRCP.connectResponse(this._avrcpHandle, true);
            this.logResult(1129447424, rc);
            if (rc == 0 || rc == 2) {
               this.updateState(1, device);
            }
         }
      }
   }

   @Override
   public final void avrcpConnected(int handle, byte[] address) {
      this.updateState(2);
   }

   @Override
   public final void avrcpDisconnected(int handle) {
      this.updateState(0);
   }

   @Override
   public final void avrcpPanelPress(int handle, int operation) {
      this.logResult(1347420160, operation);
      MediaRemoteControl.postPanelEvent(operation, 0);
   }

   @Override
   public final void avrcpPanelHold(int handle, int operation) {
      this.logResult(1346895872, operation);
      MediaRemoteControl.postPanelEvent(operation, 1);
   }

   @Override
   public final void avrcpPanelRelease(int handle, int operation) {
      this.logResult(1347551232, operation);
      MediaRemoteControl.postPanelEvent(operation, 2);
   }

   @Override
   public final void avrcpPanelResponse(int handle, int operation, boolean press, int response) {
   }
}
