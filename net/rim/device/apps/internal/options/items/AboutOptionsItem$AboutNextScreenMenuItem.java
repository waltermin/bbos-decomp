package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class AboutOptionsItem$AboutNextScreenMenuItem extends MenuItem {
   private final AboutOptionsItem this$0;

   public AboutOptionsItem$AboutNextScreenMenuItem(AboutOptionsItem _1) {
      super(OptionsResources.getResourceBundle(), 1958, 196673, 0);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.nextScreen();
   }
}
