package net.rim.device.apps.internal.browser.debug;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.cldc.io.utility.PacketLogger;

final class DebugViewerScreen$PacketLoggerSwitchViewVerb extends Verb {
   private final DebugViewerScreen this$0;

   DebugViewerScreen$PacketLoggerSwitchViewVerb(DebugViewerScreen _1) {
      super(40020);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      switch (this.this$0._nextMode) {
         case 0:
            return null;
         case 1:
         default:
            return "View Summary";
         case 2:
            return "View Details";
      }
   }

   @Override
   public final Object invoke(Object context) {
      int mode = 1;
      if (this.this$0._fullDetails) {
         mode |= 2;
      }

      this.this$0.setText(PacketLogger.getInstance().getText(this.this$0._nextMode, mode));
      this.this$0._nextMode = 3 - this.this$0._nextMode;
      return null;
   }
}
