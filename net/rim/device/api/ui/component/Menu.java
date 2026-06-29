package net.rim.device.api.ui.component;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.menu.MenuList;
import net.rim.device.api.ui.menu.MenuScreen;
import net.rim.device.api.ui.menu.MenuScreenFactory;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.vm.Array;

public class Menu {
   private MenuItem[] _items = new MenuItem[0];
   private long _style;
   private MenuItem[] _displayItems;
   private boolean[] _highlights;
   private MenuItem _emptyMenuItem;
   private MenuScreen _screen;
   private int _selectedPosition = -1;
   private int _default = -1;
   private boolean _contextMenuDefaultSet;
   private Menu$Listeners _listeners = new Menu$Listeners(this);
   private int _instance;
   private Menu _parentMenu;
   private static String SEPARATOR_STRING = "-";
   public static final int CANCELLED;
   public static final int UNDEFINED;
   public static final long SORTED;
   public static final long HORIZONTAL_ROLL_TO_CLOSE;
   public static final int INSTANCE_DEFAULT;
   public static final int INSTANCE_CONTEXT;
   public static final int INSTANCE_CONTEXT_SELECTION;
   public static final int INSTANCE_FROM_MENU_KEY;
   public static final int MENU_FULL;
   public static final int MENU_SHORT;
   private static Screen _targetScreen;

   public Menu() {
      this(0);
   }

   public Menu(long style) {
      this._style = style;
      this._screen = MenuScreenFactory.createScreenWithDefault();
      this._screen.setMenu(this);
      this._emptyMenuItem = new Menu$1(this, CommonResource.getBundle(), 10104, 0, Integer.MAX_VALUE);
   }

   public void add(ContextMenu contextMenu) {
      this.add(contextMenu, false);
   }

   public void add(ContextMenu contextMenu, boolean addSeparator) {
      contextMenu.sort();
      MenuItem[] items = contextMenu.getItems();
      int maxOrdinal = 0;

      for (MenuItem item : items) {
         if (item.getOrdinal() > maxOrdinal) {
            maxOrdinal = item.getOrdinal();
         }

         this.add(item);
      }

      if (contextMenu.isDefaultSet()) {
         this.setDefault(contextMenu.getDefault());
         this._contextMenuDefaultSet = true;
      } else {
         this._contextMenuDefaultSet = false;
      }

      if (addSeparator && !contextMenu.isEmpty()) {
         this.add(MenuItem.separator(maxOrdinal));
      }
   }

   public void add(MenuItem item) {
      if (item != null) {
         int index;
         if ((this._style & 65536) != 0) {
            index = Arrays.binarySearch(this._items, item, MenuItem.ORDINAL_COMPARATOR, 0, this._items.length);
            if (index < 0) {
               index = -index - 1;
            } else {
               int ordinal = item.getOrdinal();

               while (index < this._items.length && this._items[index].getOrdinal() == ordinal) {
                  index++;
               }
            }
         } else {
            index = this._items.length;
         }

         Arrays.insertAt(this._items, item, index);
         if (index <= this._default) {
            this._default++;
         }

         if (this._highlights != null) {
            int length = this._highlights.length;
            Array.resize(this._highlights, length + 1);
            System.arraycopy(this._highlights, index, this._highlights, index + 1, length - index);
            this._highlights[index] = false;
         }
      }
   }

   public void add(String text, Object cookie, int id) {
      if (text.equals(SEPARATOR_STRING)) {
         this.addSeparator();
      } else {
         this.add(new Menu$OldMenuItem(text, cookie, id));
      }
   }

   public int addItem(String text, Object cookie, int id) {
      this.add(text, cookie, id);
      return this._items.length - 1;
   }

   public int addSeparator() {
      int maxOrdinal = this._items.length > 0 ? this._items[this._items.length - 1].getOrdinal() : 0;
      this.add(MenuItem.separator(maxOrdinal));
      return this._items.length - 1;
   }

   public void close() {
      if (this._screen != null) {
         this._screen.close();
      }
   }

   public void deleteAll() {
      Array.resize(this._items, 0);
      this._default = -1;
   }

   public void deleteItem(int position) {
      if (position >= 0 && position < this.getSize()) {
         Arrays.removeAt(this._items, position);
         if (position < this._default) {
            this._default--;
         } else if (position == this._default) {
            this._default = -1;
         }

         if (this._highlights != null) {
            int index = position;
            this._highlights[index] = false;
            int newLength = this._highlights.length - 1;
            System.arraycopy(this._highlights, index + 1, this._highlights, index, newLength - index);
            Array.resize(this._highlights, newLength);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Object getCookie(MenuItem item) {
      Object cookie = null;
      if (!(item instanceof Menu$OldMenuItem)) {
         return item;
      }

      Menu$OldMenuItem olditem = (Menu$OldMenuItem)item;
      return olditem.getCookie();
   }

   public MenuItem getCurrentMenuItem() {
      return this._screen.getCurrentItem();
   }

   public MenuItem getDefault() {
      int defaultItem = this.getDefaultIndex();
      return defaultItem != -1 ? this._items[defaultItem] : this._emptyMenuItem;
   }

   public int getInstance() {
      return this._instance;
   }

   public boolean isDefaultSet() {
      return this.getDefaultIndex() != -1;
   }

   private int getDefaultIndex() {
      int defaultItem = this._default;
      if (defaultItem == -1) {
         int topPriority = Integer.MAX_VALUE;
         int end = this._items.length;

         for (int lv = 0; lv < end; lv++) {
            MenuItem litem = this._items[lv];
            if (litem.getPriority() < topPriority) {
               topPriority = litem.getPriority();
               defaultItem = lv;
            }
         }
      }

      if (this._items.length > 0) {
         defaultItem = MathUtilities.clamp(0, defaultItem, this._items.length - 1);
         int direction = 1;

         while (this._items[defaultItem].isSeparator()) {
            if (defaultItem >= this._items.length - 1) {
               direction = -1;
            }

            defaultItem += direction;
            if (defaultItem < 0) {
               return defaultItem;
            }
         }
      } else {
         defaultItem = -1;
      }

      return defaultItem;
   }

   public MenuItem[] getDisplayItems() {
      return this._displayItems;
   }

   public MenuItem getItem(int position) {
      if (position >= 0 && position < this.getSize()) {
         return this._items[position];
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Object getItemCookie(int position) {
      if (position >= 0 && position < this.getSize()) {
         MenuItem item = this._items[position];
         Object cookie = item;
         if (item instanceof Menu$OldMenuItem) {
            Menu$OldMenuItem oldItem = (Menu$OldMenuItem)item;
            cookie = oldItem.getCookie();
         }

         return cookie;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getItemId(int position) {
      if (position >= 0 && position < this.getSize()) {
         MenuItem item = this._items[position];
         int id = -1;
         if (item instanceof Menu$OldMenuItem) {
            Menu$OldMenuItem oldItem = (Menu$OldMenuItem)item;
            id = oldItem.getId();
         }

         return id;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getSelectedId() {
      int id = -1;
      if (this._selectedPosition >= 0) {
         MenuItem item = this._displayItems[this._selectedPosition];
         if (item instanceof Menu$OldMenuItem) {
            Menu$OldMenuItem olditem = (Menu$OldMenuItem)item;
            id = olditem.getId();
         }
      }

      return id;
   }

   public Object getSelectedCookie() {
      Object cookie = null;
      if (this._selectedPosition >= 0) {
         MenuItem item = this._displayItems[this._selectedPosition];
         if (item instanceof Menu$OldMenuItem) {
            Menu$OldMenuItem olditem = (Menu$OldMenuItem)item;
            return olditem.getCookie();
         }

         cookie = item;
      }

      return cookie;
   }

   public MenuItem getSelectedItem() {
      MenuItem item = null;
      if (this._selectedPosition >= 0) {
         item = this._displayItems[this._selectedPosition];
      }

      return item;
   }

   public int getSize() {
      return this._items.length;
   }

   public long getStyle() {
      return this._style;
   }

   public static Screen getTargetScreen() {
      return _targetScreen;
   }

   public void invokeDefaultItem() {
      int defaultItem = this.getDefaultIndex();
      MenuItem item = this._items[defaultItem];
      item.run();
   }

   public boolean isDisplayed() {
      return this._screen.isDisplayed();
   }

   public void notifySelected(MenuItem item) {
      this._selectedPosition = -1;

      for (int lv = this._displayItems.length - 1; lv >= 0; lv--) {
         if (this._displayItems[lv] == item) {
            this._selectedPosition = lv;
            if (this._parentMenu != null) {
               this._parentMenu.notifySubItemSelected(item);
               return;
            }
            break;
         }
      }
   }

   public void notifySubItemSelected(MenuItem item) {
      this._screen.close();
      if (this._parentMenu != null) {
         this._parentMenu.notifySubItemSelected(item);
      }
   }

   public void setAlignment(long hAlign, long vAlign) {
      this._screen.setAlignment(hAlign, vAlign);
   }

   public void setCurrentItem(MenuItem item) {
      this._screen.setCurrentItem(item);
   }

   public void setDefault(int position) {
      if (!this._contextMenuDefaultSet) {
         if (position >= 0 && position < this.getSize()) {
            this._default = position;
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public void setDefaultIgnoreContextMenuDefault(MenuItem item) {
      int end = this._items.length;

      for (int lv = 0; lv < end; lv++) {
         MenuItem litem = this._items[lv];
         if (litem == item) {
            this._default = lv;
            return;
         }
      }
   }

   public void setDefault(MenuItem item) {
      if (!this._contextMenuDefaultSet) {
         int end = this._items.length;

         for (int lv = 0; lv < end; lv++) {
            MenuItem litem = this._items[lv];
            if (litem == item) {
               this._default = lv;
               return;
            }
         }
      }
   }

   public void setInstance(int instance) {
      this._instance = instance;
   }

   public void setItemHighlight(int position, boolean highlight) {
      if (this._highlights == null) {
         this._highlights = new boolean[this._items.length];
      }

      this._highlights[position] = highlight;
   }

   public void setOrigin(int xOffset, int yOffset) {
      this._screen.setOrigin(xOffset, yOffset);
   }

   public void setParentMenu(Menu parentMenu) {
      this._parentMenu = parentMenu;
      if (_targetScreen != null) {
         _targetScreen.addScreenUiEngineAttachedListener(this._listeners);
      }
   }

   public void setTarget(Field field) {
   }

   public static void setTargetScreen(Screen targetScreen) {
      _targetScreen = targetScreen;
   }

   public void setTargetScreenVirtual(Screen targetScreen) {
      if (targetScreen != null) {
         targetScreen.addScreenUiEngineAttachedListener(this._listeners);
      }
   }

   public int show() {
      this._displayItems = new MenuItem[2 * this._items.length];
      int nItems = 0;
      boolean prevItemIsSeparator = true;
      int prevOrdinal = 0;
      int end = this._items.length;

      for (int lv = 0; lv < end; lv++) {
         MenuItem item = this._items[lv];
         if (!item.isSeparator() || !prevItemIsSeparator) {
            int ordinal = item.getOrdinal();
            if ((this._style & 65536) != 0 && !prevItemIsSeparator && !item.isSeparator() && (ordinal ^ prevOrdinal) >> 16 != 0) {
               this._displayItems[nItems++] = MenuItem.separator(prevOrdinal);
               prevItemIsSeparator = true;
            }

            prevOrdinal = ordinal;
            this._displayItems[nItems++] = item;
            prevItemIsSeparator = item.isSeparator();
         }
      }

      if (prevItemIsSeparator && nItems > 0) {
         nItems--;
      }

      Array.resize(this._displayItems, nItems);
      if (Ui.getMode() < 2 && nItems == 0) {
         Array.resize(this._displayItems, 1);
         this._displayItems[0] = this._emptyMenuItem;
         int var9 = true;
      }

      MenuList list = MenuScreenFactory.createListFieldWithDefault();
      list.setMenuItems(this._displayItems);
      list.setCurrentItem(this.getDefault());
      this._screen.setList(list);
      this._selectedPosition = -1;
      boolean global = _targetScreen != null ? _targetScreen.isGlobal() : false;
      if (!global) {
         Ui.getUiEngine().pushModalScreen((Screen)this._screen);
      } else {
         int priority = Ui.getUiEngine().getGlobalPriority(_targetScreen);
         Ui.getUiEngine().pushGlobalScreen((Screen)this._screen, priority, 5);
      }

      if (this._selectedPosition >= 0) {
         this._displayItems[this._selectedPosition].run();
      }

      ContextMenu.getInstance().setTarget(null);
      _targetScreen = null;
      MenuItem selectedMenuItem = this.getSelectedItem();
      if (selectedMenuItem != null) {
         int selectedMenuItemID = selectedMenuItem.getId();
         if (selectedMenuItemID != 6 && selectedMenuItemID != 3 && selectedMenuItemID != 4) {
            Clipboard.getClipboard().setNotYetPasted(false);
         }
      }

      return this._selectedPosition;
   }
}
