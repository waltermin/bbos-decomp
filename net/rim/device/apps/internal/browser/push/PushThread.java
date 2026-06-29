package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.browser.util.RunnableThread;
import net.rim.device.apps.internal.browser.wappush.WAPPushModel;

public final class PushThread extends RunnableThread {
   @Override
   protected final void runItem(Object obj) {
      if (obj instanceof WAPPushModel) {
         Application.getApplication().invokeAndWait((WAPPushModel)obj);
      } else {
         if (!(obj instanceof BrowserPushModel)) {
            if (obj instanceof BasePushModel) {
               ((BasePushModel)obj).run();
            }
         } else {
            BrowserPushModel bpm = (BrowserPushModel)obj;
            Application.getApplication().invokeAndWait(bpm);
            if (bpm.performAction()) {
               bpm.doAction();
               return;
            }
         }
      }
   }
}
