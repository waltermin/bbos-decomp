package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;

class MessageStatusField$1 extends Verb {
   private final MessageStatusField this$0;

   MessageStatusField$1(MessageStatusField _1, int x0, ResourceBundleFamily x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object param) {
      Dialog.inform(this.this$0._details);
      return null;
   }
}
