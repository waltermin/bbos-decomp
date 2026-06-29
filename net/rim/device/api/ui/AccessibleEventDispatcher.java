package net.rim.device.api.ui;

import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleEventListener;

public class AccessibleEventDispatcher {
   public static boolean dispatchAccessibleEvent(int event, Object oldValue, Object newValue, AccessibleContext context) {
      AccessibleEventListener listener = GlobalScreenManager.getAccessibleEventListener();
      if (listener != null) {
         listener.accessibleEventOccurred(event, oldValue, newValue, context);
         return true;
      } else {
         return false;
      }
   }
}
