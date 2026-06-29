package net.rim.wica.runtime.diagnostics.impl;

import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.system.DeviceInfo;
import net.rim.vm.DebugSupport;

final class SimulatorDebugServer$StartupRunnable implements Runnable {
   @Override
   public final void run() {
      try {
         String activationUrl = DebugSupport.getenv("MdsActivationUrl");
         if (activationUrl != null) {
            new ActivationHelper().activate(activationUrl);
         }

         StreamConnection connection = (StreamConnection)Connector.open("socket://localhost:45654;deviceside=true");
         OutputStream stream = connection.openOutputStream();
         stream.write(DeviceInfo.getDeviceId());
         stream.flush();
         connection.close();
      } finally {
         return;
      }
   }
}
