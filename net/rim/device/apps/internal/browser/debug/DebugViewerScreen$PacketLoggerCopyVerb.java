package net.rim.device.apps.internal.browser.debug;

import net.rim.device.api.system.Clipboard;
import net.rim.device.apps.api.framework.verb.Verb;

final class DebugViewerScreen$PacketLoggerCopyVerb extends Verb {
   private final DebugViewerScreen this$0;

   DebugViewerScreen$PacketLoggerCopyVerb(DebugViewerScreen _1) {
      super(40010);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return "Copy to Clipboard";
   }

   @Override
   public final Object invoke(Object context) {
      Clipboard.getClipboard().put(this.this$0._text);
      return null;
   }
}
