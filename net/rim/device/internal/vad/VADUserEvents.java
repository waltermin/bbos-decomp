package net.rim.device.internal.vad;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.vm.Message;

public final class VADUserEvents {
   public static final void sendEvent(int event, int context) {
      Message msg = new Message(6, event, context);
      ApplicationManagerInternal appManager = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();
      appManager.postMessage(msg);
   }

   public static final void addListener(Application app, VADUserEventListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(6) == null) {
            dispatchManager.setDispatcher(6, new VADUserEventDispatcher());
         }
      }

      app.addListener(6, listener);
   }

   public static final void removeListener(Application app, VADUserEventListener listener) {
      app.removeListener(6, listener);
   }
}
