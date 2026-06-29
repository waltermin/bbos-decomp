package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.data.Filter;
import net.rim.device.apps.internal.bis.event.CommandEvent;

public final class FilterList$FilterListField extends ListField {
   Filter _filter;
   private final FilterList this$0;

   public FilterList$FilterListField(FilterList _1, int size, int flags) {
      super(size, flags);
      this.this$0 = _1;
   }

   @Override
   public final boolean isFocusable() {
      return true;
   }

   @Override
   public final boolean isSelectionCopyable() {
      return false;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.setInstance(65538);
      Filter selectedFilter = this._filter;
      String[] commandParams = new String[]{"filterid", "filtername"};
      NotificationMenuItem editMenuItem = new NotificationMenuItem(ApplicationResources.getString(305), 20000, 1);
      menu.add(editMenuItem);
      editMenuItem.setListener(this.this$0._menuItemListener);
      editMenuItem.setEvent(new CommandEvent(305, 28, commandParams));
      menu.setDefault(editMenuItem);
      NotificationMenuItem deleteMenuItem = new NotificationMenuItem(ApplicationResources.getString(307), 20000, 1);
      menu.add(deleteMenuItem);
      deleteMenuItem.setListener(this.this$0._menuItemListener);
      deleteMenuItem.setEvent(new CommandEvent(307, 26, commandParams));
   }
}
