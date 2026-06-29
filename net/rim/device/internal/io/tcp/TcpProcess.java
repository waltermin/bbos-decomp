package net.rim.device.internal.io.tcp;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.vm.Process;

public final class TcpProcess extends Application {
   private static final long GUID = -5773373294466765091L;

   public static final void libMain(String[] args) {
      getInstance().enterEventDispatcher();
   }

   private TcpProcess() {
      Process process = Process.currentProcess();
      process.setThreadLimit(64);
      process.haltDeviceIfThisProcessDies(true);
   }

   public static final TcpProcess getInstance() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      TcpProcess tcpProcess = (TcpProcess)applicationRegistry.getOrWaitFor(-5773373294466765091L);
      if (tcpProcess == null) {
         tcpProcess = new TcpProcess();
         applicationRegistry.put(-5773373294466765091L, tcpProcess);
      }

      return tcpProcess;
   }

   public final void startThread(Thread thread) {
      this.invokeLater(new TcpProcessRunnable(thread));
   }
}
