package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.explorer.file.options.ExplorerOptionsListItem;
import net.rim.device.internal.i18n.CommonResource;

public final class OptionsMenuItem extends MenuItem {
   public OptionsMenuItem() {
      super(CommonResource.getBundle(), 20, 16986368, 0);
   }

   @Override
   public final void run() {
      ExplorerOptionsListItem optionsListItem = new ExplorerOptionsListItem();
      optionsListItem.perform(6099736323056465049L, null);
   }
}
