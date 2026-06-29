package net.rim.device.cldc.impl.hrt;

import javax.microedition.io.DatagramConnection;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;

final class HRTUpdaterThread extends Thread {
   private DatagramConnectionBase _mdpConn;
   private HRTRequestThread _hrtThread;

   public HRTUpdaterThread(HRTRequestThread rT, DatagramConnection mdpCon, HostRoutingTable defHrt) {
      this._mdpConn = (DatagramConnectionBase)mdpCon;
      this._hrtThread = rT;
   }

   @Override
   public final void run() {
      while (true) {
         try {
            this._hrtThread.receivedRegResponse(this.getNextPacketData());
         } finally {
            continue;
         }
      }
   }

   private final DatagramBase getNextPacketData() {
      DatagramBase dg = (DatagramBase)this._mdpConn.newDatagram(0);
      this._mdpConn.receive(dg);
      this._mdpConn.datagramProcessed(dg.getDatagramId());
      return dg;
   }
}
