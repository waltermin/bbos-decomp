package net.rim.device.internal.timesync;

import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import net.rim.device.cldc.io.gme.GMEDatagram;

final class TimeSyncThread extends Thread implements TimeSyncEvent {
   @Override
   public final void run() {
      try {
         DatagramConnection conn = (DatagramConnection)Connector.open("gme:gme");
         GMEDatagram dgram = (GMEDatagram)conn.newDatagram(0);
         dgram.setCommandByte(10);
         conn.send(dgram);
         conn.close();
      } finally {
         return;
      }
   }
}
