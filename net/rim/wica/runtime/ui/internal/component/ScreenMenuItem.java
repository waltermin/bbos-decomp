package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.MenuItem;
import net.rim.wica.runtime.metadata.component.ui.MenuItemModel;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.ui.View;

final class ScreenMenuItem extends MenuItem implements View {
   private MenuItemModel _menuItemModel;
   private static final String SEPARATOR_STRING;

   ScreenMenuItem(MenuItemModel menuItemModel, int priority) {
      super(validateText(menuItemModel.getLabel()), 0, priority);
      this._menuItemModel = menuItemModel;
   }

   private static final String validateText(String text) {
      if (text == null || text.length() == 0) {
         return " ";
      } else {
         return "-".equals(text) ? ((StringBuffer)(new Object())).append(text).append(' ').toString() : text;
      }
   }

   @Override
   public final void run() {
      this._menuItemModel.onClick();
   }

   @Override
   public final UIComponent getModel() {
      return null;
   }

   @Override
   public final void setModel(UIComponent model) {
   }

   @Override
   public final void setVisibility(byte visibility) {
   }

   @Override
   public final void update(int row) {
   }
}
