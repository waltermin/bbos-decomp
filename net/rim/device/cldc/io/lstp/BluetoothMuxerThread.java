package net.rim.device.cldc.io.lstp;

import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.system.LockEventLogger;

final class BluetoothMuxerThread extends SerialMuxerThread {
   protected BluetoothMuxerThread(NativeLayer nativeLayer) {
      super(nativeLayer);
   }

   @Override
   protected final void sendChallenge() {
      DatagramBase dgram = (DatagramBase)super._conn.newDatagram();
      dgram.ensureCapacity(118);
      dgram.writeInt(131075);
      dgram.writeByte(RadioInfo.getNetworkType());
      dgram.writeInt(DeviceInfo.getDeviceId());
      dgram.writeInt(115200);
      dgram.writeShort(SerialLayer.DATA_FRAGMENT_PAYLOAD);
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      switch (super._challengeType) {
         case 1:
         case 17:
            dgram.writeByte(0);
            break;
         case 16:
            LockEventLogger.logLockEvent(1281516392);
            appManager.lockSystem(false);
         case 0:
            appManager.unlockSystem();
      }

      dgram.trim(false);
      super._conn.send(dgram);
   }
}
