package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.i18n.CommonResource;

class ApplicationControlScreen$GroupMenuItem extends MenuItem {
   public ApplicationControlScreen$GroupMenuItem(boolean expanded) {
      super(CommonResource.getBundle(), expanded ? 12 : 11, 10000, 10);
   }

   @Override
   public void run() {
      ApplicationControlScreen$ItemGroup g = ((ApplicationControlScreen$ItemField)this.getTarget())._ig;
      if (g._expanded) {
         g.hideItems();
      } else {
         g.showItems();
      }
   }
}
