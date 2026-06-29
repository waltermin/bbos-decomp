package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.MenuItem;
import net.rim.wica.runtime.resources.RuntimeResources;

final class ScreenContext$BackMenuItem extends MenuItem {
   private final ScreenContext this$0;

   ScreenContext$BackMenuItem(ScreenContext this$0) {
      super(RuntimeResources.getString(46), Integer.MAX_VALUE, Integer.MAX_VALUE);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0._uiService.requestScreenBack();
   }
}
