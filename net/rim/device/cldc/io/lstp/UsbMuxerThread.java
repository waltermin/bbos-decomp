package net.rim.device.cldc.io.lstp;

import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.internal.crypto.CryptoBlock;
import net.rim.device.resources.Resource;

final class UsbMuxerThread extends MuxerThread {
   private static final byte SC_MAJOR = 2;
   private static final byte SC_MINOR = 0;
   private static final byte OV_MAJOR = 3;
   private static final byte OV_MINOR = 0;
   private static final int VERSION = 131075;

   protected UsbMuxerThread(NativeLayer nativeLayer) {
      super(nativeLayer);
   }

   @Override
   protected final void expectLogin() {
      DatagramBase dgram = (DatagramBase)super._conn.newDatagram();
      super._conn.receive(dgram);
   }

   @Override
   protected final void sendChallenge() {
      if (!CryptoBlock.areMasterKeysAvailable()) {
         if (Alert.isBuzzerSupported()) {
            short[] tune = new short[]{440, 100, 880, 100, 1760, 100, 3, -12284, -5441, 157, 3, -12284};
            Alert.startBuzzer(tune, 100);
         } else if (Alert.isMIDISupported()) {
            Resource rsrc = Resource.getResourceClass();
            byte[] tune = rsrc.getResource("ContentProtectionNotification.mid");
            Alert.setVolume(100);
            Alert.startMIDI(tune, 2);
         }

         ApplicationManager.getApplicationManager().unlockSystem();
      }

      DatagramBase dgram = (DatagramBase)super._conn.newDatagram();
      dgram.writeInt(131075);
      super._conn.send(dgram);
   }
}
