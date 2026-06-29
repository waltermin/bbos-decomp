package net.rim.device.internal.media;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.vm.Message;

public class MediaRemoteControl {
   public static void addListener(Application app, MediaRemoteControlListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(28) == null) {
            dispatchManager.setDispatcher(28, new MediaRemoteControlDispatcher());
         }
      }

      app.addListener(28, listener);
   }

   public static void removeListener(Application app, MediaRemoteControlListener listener) {
      app.removeListener(28, listener);
   }

   public static void postPanelEvent(int operation, int action) {
      Message msg = new Message(28, operation, action);
      ApplicationManagerInternal appManager = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();
      appManager.postMessage(msg);
   }
}
