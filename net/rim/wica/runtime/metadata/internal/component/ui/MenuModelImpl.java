package net.rim.wica.runtime.metadata.internal.component.ui;

import net.rim.device.api.util.IntIntHashtable;
import net.rim.wica.runtime.metadata.component.ui.MenuItemModel;
import net.rim.wica.runtime.metadata.component.ui.MenuModel;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.internal.util.SingleValueHelper;

public class MenuModelImpl extends UIComponentImpl implements MenuModel {
   private MenuItemModelImpl[] _items;
   private Object[] _inValues;
   private IntIntHashtable _menuMap;
   private int _showEventId;

   protected MenuModelImpl(int id, int type, UIContainer parent, int showEventId) {
      super(id, type, parent, -1, 0, 0, 0, -1);
      this._showEventId = showEventId;
   }

   protected void setMenuItemCount(int count) {
      this._items = new MenuItemModelImpl[count];
      this._inValues = new Object[count];
      this._menuMap = (IntIntHashtable)(new Object(count + (count >> 2)));
   }

   protected void setMenuItem(int menuItem, int id, Object inValue, int clickId, boolean visibility) {
      this._inValues[menuItem] = inValue;
      this._items[menuItem] = new MenuItemModelImpl(this, null, visibility, clickId);
      this._menuMap.put(id, menuItem);
   }

   @Override
   public void updateUI() {
      int menuCount = this._inValues.length;

      for (int i = 0; i < menuCount; i++) {
         Object value = SingleValueHelper.resolveInValue(this, this._inValues[i], 3);
         this._items[i].setLabel(value == null ? null : value.toString());
      }

      if (this._showEventId != -1) {
         ((ScreenModelImpl)this.getParent()).handleEvent(2147483646, this._showEventId);
      }
   }

   @Override
   public MenuItemModel getMenuItem(String id) {
      return this._items[this._menuMap.get(((ScreenModelImpl)this.getScreen()).getCodeByName(id))];
   }

   @Override
   public MenuItemModel[] getItems() {
      return this._items;
   }
}
