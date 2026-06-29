package net.rim.device.apps.internal.phone.callcontrol;

import net.rim.device.api.system.Application;
import net.rim.device.internal.callcontrol.CallControlSystem;

class CallControlApplication extends Application {
   public static void libMain(String[] args) {
      CallControlApplication app = new CallControlApplication();
      CallControlSystem.getInstance().startListening(app);
      app.enterEventDispatcher();
   }
}
