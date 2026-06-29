package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.Clipboard;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class StatusPIN$CopyPINVerb extends Verb {
   private final StatusPIN this$0;

   StatusPIN$CopyPINVerb(StatusPIN _1) {
      super(16986368, OptionsResources.getResourceBundle(), 1452);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object context) {
      Clipboard.getClipboard().put(this.this$0.getDisplayValue());
      return null;
   }
}
