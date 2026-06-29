package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Clipboard;
import net.rim.device.apps.api.framework.verb.Verb;

final class StatusListItem$CopyStatusListItemVerb extends Verb {
   private final StatusListItem this$0;

   StatusListItem$CopyStatusListItemVerb(StatusListItem _1) {
      super(16986368, ResourceBundle.getBundle(-8414468493733347764L, "net.rim.device.apps.internal.resource.Common"), 1800);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object context) {
      Clipboard.getClipboard().put(this.this$0.getDisplayValue());
      return null;
   }
}
