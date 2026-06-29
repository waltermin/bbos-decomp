package net.rim.device.cldc.io.utility;

import net.rim.device.api.system.Application;
import net.rim.device.internal.ui.MIDletApplication;

public final class EventThreadCheck {
   private static boolean _isMidlet = Application.getApplication() instanceof MIDletApplication;

   private EventThreadCheck() {
   }

   public static final void throwException() {
      if (Application.isEventDispatchThread() && !_isMidlet) {
         throw new RuntimeException("blocking operation not permitted on event dispatch thread");
      }
   }
}
