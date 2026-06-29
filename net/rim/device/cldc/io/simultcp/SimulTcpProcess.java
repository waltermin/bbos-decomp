package net.rim.device.cldc.io.simultcp;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.vm.Process;

public final class SimulTcpProcess extends Application {
   private static final long GUID;

   public static final void libMain(String[] args) {
      getInstance().enterEventDispatcher();
   }

   private SimulTcpProcess() {
      Process process = Process.currentProcess();
      process.setThreadLimit(128);
      process.haltDeviceIfThisProcessDies(true);
   }

   public static final SimulTcpProcess getInstance() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      SimulTcpProcess tcpProcess = (SimulTcpProcess)applicationRegistry.getOrWaitFor(-8788489361183018457L);
      if (tcpProcess == null) {
         tcpProcess = new SimulTcpProcess();
         applicationRegistry.put(-8788489361183018457L, tcpProcess);
      }

      return tcpProcess;
   }

   public final void startThread(Thread thread) {
      this.invokeLater(new TcpProcessRunnable(thread));
   }
}
