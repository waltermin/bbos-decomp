package simulationservicebook;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;

class InsertInternetServiceBook$1 implements Runnable {
   private final String val$uri;
   private final byte[] val$data;
   private final long val$guid;
   private final InsertInternetServiceBook$Helper val$h;

   InsertInternetServiceBook$1(String _1, byte[] _2, long _3, InsertInternetServiceBook$Helper _5) {
      this.val$uri = _1;
      this.val$data = _2;
      this.val$guid = _3;
      this.val$h = _5;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      label34:
      try {
         DatagramConnection dc = (DatagramConnection)Connector.open(this.val$uri);
         Datagram d = dc.newDatagram(this.val$data, this.val$data.length);
         dc.send(d);
         InsertInternetServiceBook$SimulatorReceiveRunnable run = new InsertInternetServiceBook$SimulatorReceiveRunnable(dc);
         ProtocolDaemon.getInstance().submitRunnable(run);
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         InsertInternetServiceBook$SimulatorReceiveRunnable regRun = (InsertInternetServiceBook$SimulatorReceiveRunnable)ar.getOrWaitFor(this.val$guid);
         if (regRun != null) {
            regRun.closeConnection();
            InsertInternetServiceBook$SimulatorReceiveRunnable var12 = null;
         }

         ar.replace(this.val$guid, run);
      } catch (Throwable var10) {
         System.err.println(e.getMessage());
         e.printStackTrace();
         break label34;
      }

      synchronized (this.val$h._lock) {
         this.val$h._hasrun = true;
         this.val$h._lock.notify();
      }
   }
}
