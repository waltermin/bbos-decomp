package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class SwitchField$SwitchMenuItem extends MenuItem {
   private final SwitchField this$0;

   SwitchField$SwitchMenuItem(SwitchField _1) {
      super(null, 65, 332033, 0);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Screen scr = this.this$0.getScreen();
      if (scr != null) {
         Object scrCookie = scr.getCookie();
         if (scrCookie instanceof Object) {
            ContextObject obj = (ContextObject)(new Object());
            ContextObject.put(obj, 254, this.this$0.getCookie());
            ((ActionProvider)scrCookie).perform(NotificationMessageField.SWITCH, obj);
         }
      }
   }

   @Override
   public final String toString() {
      return QmResources.getString(65);
   }
}
