package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.internal.bis.ApplicationResources;

public final class FilterLabelField extends LabelField {
   private NotificationMenuItemListener _listener;
   public static final int INSTANCE_FILTER_FIELD_SELECTED;

   public FilterLabelField() {
   }

   public FilterLabelField(Object text) {
   }

   @Override
   public final boolean isSelectable() {
      return true;
   }

   @Override
   public final boolean isFocusable() {
      return true;
   }

   public final void setMenuListener(NotificationMenuItemListener listener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.setInstance(65538);
      NotificationMenuItem deleteMenuItem = new NotificationMenuItem(ApplicationResources.getString(163), 20000, 0);
      menu.add(deleteMenuItem);
      menu.setDefault(deleteMenuItem);
      deleteMenuItem.setListener(this._listener);
   }
}
