package net.rim.wica.runtime.diagnostics.impl;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;

final class DebugAgent extends Application {
   private DebugAgent() {
      boolean started = false;
      if (DeviceInfo.isSimulator()) {
         SimulatorDebugServer server = new SimulatorDebugServer(new DebugHandler());
         started = server.start();
      }

      if (started) {
         this.enterEventDispatcher();
      }
   }

   public static final void main(String[] args) {
      new DebugAgent();
   }
}
