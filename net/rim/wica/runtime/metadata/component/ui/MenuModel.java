package net.rim.wica.runtime.metadata.component.ui;

public interface MenuModel extends UIComponent {
   MenuItemModel getMenuItem(String var1);

   MenuItemModel[] getItems();
}
