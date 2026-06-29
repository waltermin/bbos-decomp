package net.rim.device.apps.api.quickcontact;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.ui.Image;

public final class QuickContactScreen extends AppsMainScreen implements ListFieldCallback, QuickContactList$Listener {
   private VerticalFieldManager _vfm;
   private QuickContactScreen$QuickContactListField _listField;
   private LabelField _titleField;
   private QuickContactList _quickContactList = QuickContactList.getInstance();
   private QuickContactItem[] _items = this._quickContactList.getItems();
   private boolean _addMode;
   private QuickContactItem _newItem;
   private QuickContactItem _itemBeingMoved = null;
   private char _initialAddModeKey = 0;
   private boolean _ignoreKeyUp = true;
   private int _initFlags;
   private boolean _smallScreen = Display.getHeight() < 160;
   private UiApplication _app = UiApplication.getUiApplication();
   private Image _upDownIcon = QuickContactIcons.MOVE_UP_DOWN_ICONS.getImage(0);
   private static final long MANAGER_FLAGS;
   private static final int MENU_NEW_QUICK_CONTACT;
   private static final int MENU_INVOKE_QUICK_CONTACT;
   private static final int MENU_VIEW_QUICK_CONTACT;
   private static final int MENU_EDIT_QUICK_CONTACT;
   private static final int MENU_MOVE_QUICK_CONTACT;
   private static final int MENU_DELETE_QUICK_CONTACT;
   public static final int ADD_MODE;
   public static final int MOVE_MODE;
   public static final int MOVE_ONLY;
   public static final int SHOW_MODAL;
   private static final int SCREEN_WIDTH = Display.getWidth();
   private static final int INTER_COLUMN_SPACE;
   private static final int KEY_OFFSET;
   private static final int ADDRESS_WIDTH = SCREEN_WIDTH * 45 / 100;
   private static ContextObject _paintingContext = (ContextObject)(new Object());

   QuickContactScreen(QuickContactItem item, int flags) {
      super(562949953486848L);
      this._addMode = (flags & 1) != 0;
      this._newItem = item;
      this._initFlags = flags;
      char key = item.getKey();
      if (key == 0) {
         String name = item.getFriendlyNameString();
         if (name != null && name.length() > 0) {
            key = CharacterUtilities.toUpperCase(name.charAt(0), 1701707776);
         }
      }

      this.constructScreen(key);
      this.setSupportClickAndHoldKeyEvents(true);
   }

   QuickContactScreen(char initialSelection) {
      super(562949953486848L);
      this.constructScreen(initialSelection);
      this.setSupportClickAndHoldKeyEvents(true);
   }

   private final void updateTitleText() {
      String title = CommonResources.getString(9);
      this._titleField.setText(title);
   }

   private final void addTitle() {
      this._titleField = (LabelField)(new Object());
      this.updateTitleText();
      this.setTitle(this._titleField);
   }

   private final void constructScreen(char initialSelection) {
      this.addTitle();
      this._listField = new QuickContactScreen$QuickContactListField();
      this._listField.setCallback(this);
      this._listField.setSize(this._items.length);
      Font currentFont = this._listField.getFont();
      if (this._smallScreen && currentFont.getHeight() < 10) {
         this.setFont(currentFont.derive(currentFont.getStyle(), 10));
      }

      this._vfm = (VerticalFieldManager)(new Object(3459063580983296000L));
      this._vfm.setVerticalQuantization(-1);
      this._vfm.add(this._listField);
      this.add(this._vfm);
      int initialIndex = QuickContactList.getKeyIndex(initialSelection);
      if (initialIndex != -1 && (this._initFlags & 2) != 0 && this._items[initialIndex] != null) {
         this._listField.setSelectedIndex(initialIndex);
      } else if (initialIndex == -1 || this._addMode && this._items[initialIndex] != null) {
         for (int i = 0; i < this._items.length; i++) {
            QuickContactItem item = this._items[i];
            if (this._addMode) {
               if (item == null) {
                  this._listField.setSelectedIndex(i);
                  break;
               }
            } else if (item != null) {
               this._listField.setSelectedIndex(i);
               break;
            }
         }
      } else {
         this._listField.setSelectedIndex(initialIndex);
      }

      if (this._addMode) {
         this._initialAddModeKey = QuickContactList.getIndexKey(this._listField.getSelectedIndex());
      }

      if (this._addMode || (this._initFlags & 2) != 0) {
         this.enableMoveMode(true);
      }

      this.setHelp("phone");
   }

   @Override
   public final void onQuickContactListEvent(int eventId, int index, QuickContactItem item) {
      int idx = index;
      int event = eventId;
      this._app.invokeLater(new QuickContactScreen$1(this, event, idx));
   }

   private final boolean confirmAndReplaceItem(QuickContactItem oldItem, QuickContactItem newItem, boolean move) {
      if (Dialog.ask(3, CommonResources.getString(10)) == 4) {
         if (move) {
            this._quickContactList.changeQuickContactKey(newItem.getKey(), oldItem.getKey());
            return true;
         } else {
            this._quickContactList.delete(oldItem);
            this._quickContactList.add(newItem);
            return true;
         }
      } else {
         return false;
      }
   }

   private final void completeAddItem() {
      char key = QuickContactList.getIndexKey(this._listField.getSelectedIndex());
      if (key != 0) {
         this._newItem.setKey(key);
         QuickContactItem currItem = this.getCurrentItem();
         if (currItem == null) {
            this._quickContactList.add(this._newItem);
         } else if (!this.confirmAndReplaceItem(currItem, this._newItem, false)) {
            return;
         }
      }

      this._addMode = false;
      this._newItem = null;
      this.enableMoveMode(false);
      this.updateTitleText();
   }

   private final void completeMoveItem() {
      QuickContactItem currItem = this.getCurrentItem();
      if (currItem == null) {
         char oldKey = this._itemBeingMoved.getKey();
         char newKey = this.getCurrentKey();
         this._quickContactList.changeQuickContactKey(oldKey, newKey);
      } else if (this.getCurrentKey() != this._itemBeingMoved.getKey() && !this.confirmAndReplaceItem(currItem, this._itemBeingMoved, true)) {
         return;
      }

      this.enableMoveMode(false);
   }

   private final char getCurrentKey() {
      return QuickContactList.getIndexKey(this._listField.getSelectedIndex());
   }

   private final QuickContactItem getCurrentItem() {
      return this.getItem(this._listField.getSelectedIndex());
   }

   private final QuickContactItem getItem(int index) {
      return index >= 0 && index < this._items.length ? this._items[index] : null;
   }

   @Override
   protected final boolean handleSendKey() {
      QuickContactItem qci = this.getCurrentItem();
      if (qci != null) {
         qci.invoke();
         this.close();
         return true;
      } else {
         this.close();
         return true;
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return this.invokeAction(1);
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      return false;
   }

   @Override
   protected final boolean invokeAction(int action) {
      if (action != 1 || !this._listField._moveItemMode) {
         return false;
      } else if (this._addMode && this._newItem != null) {
         this.completeAddItem();
         return true;
      } else {
         this.completeMoveItem();
         return true;
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if ((this._initFlags & 4) == 0) {
         int index = this._listField.getSelectedIndex();
         QuickContactItem item = this._items[index];
         if (item != null) {
            Verb invokeVerb = new QuickContactScreen$QuickContactItemVerb(this, item, 2, 131088);
            menu.add(invokeVerb);
            menu.add(new QuickContactScreen$QuickContactItemVerb(this, item, 0, 131120));
            menu.add(new QuickContactScreen$QuickContactItemVerb(this, item, 4, 131136));
            menu.add(new QuickContactScreen$QuickContactItemVerb(this, item, 1, 131152));
            menu.setDefault(invokeVerb);
         } else {
            QuickContactList.getInstance();
            char key = QuickContactList.getIndexKey(index);
            Verb newQCVerb = new QuickContactScreen$NewQuickContactItemVerb(this, key);
            if (newQCVerb != null) {
               menu.add(newQCVerb);
               menu.setDefault(newQCVerb);
            }
         }
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      if ((status & 1) != 0 && !this._listField._moveItemMode) {
         QuickContactItem item = this._itemBeingMoved = this._items[this._listField.getSelectedIndex()];
         if (item != null) {
            this._itemBeingMoved = item;
            this._listField._moveItemMode = true;
         }
      }

      if (this._itemBeingMoved != null) {
         int selectedIndex = this._listField.getSelectedIndex();
         this._listField.setSelectedIndex(selectedIndex + (dy > 0 ? 1 : -1));
         return true;
      } else {
         return super.navigationMovement(dx, dy, status, time);
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         this._quickContactList.addListener(this);
      } else {
         this._quickContactList.removeListener(this);
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   protected final void onObscured() {
      this._quickContactList.removeListener(this);
      super.onObscured();
   }

   @Override
   protected final void onExposed() {
      this._quickContactList.addListener(this);
      super.onExposed();
   }

   public static final void show() {
      show('\u0000');
   }

   public static final void show(char initialSelection) {
      UiApplication.getUiApplication().pushScreen(new QuickContactScreen(initialSelection));
   }

   public static final void show(QuickContactItem item, int flags) {
      if ((flags & 8) != 0) {
         UiApplication.getUiApplication().pushModalScreen(new QuickContactScreen(item, flags));
      } else {
         UiApplication.getUiApplication().pushScreen(new QuickContactScreen(item, flags));
      }
   }

   private final QuickContactItem getSelectedItem() {
      int index = this._listField.getSelectedIndex();
      return index != -1 ? this._quickContactList.get(index) : null;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      this._ignoreKeyUp = false;
      return super.keyDown(keycode, time);
   }

   @Override
   protected final boolean keyRepeat(int keycode, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      if (Keypad.key(keycode) == 257 && this._listField._moveItemMode && this._itemBeingMoved != null) {
         this.completeMoveItem();
         return true;
      }

      if (this._ignoreKeyUp) {
         return true;
      }

      char key = Keypad.map(keycode);
      QuickContactItem item = null;
      if (Keypad.getAltedChar(key) == 127) {
         key = 127;
      }

      switch (key) {
         case '\n':
            if (this._addMode && this._newItem != null) {
               this.completeAddItem();
            } else if (this._listField._moveItemMode) {
               this.completeMoveItem();
            } else {
               item = this.getSelectedItem();
               if (item != null) {
                  item.invoke();
               }
            }
            break;
         case '\u007f':
            item = this.getSelectedItem();
            if (item != null) {
               new QuickContactScreen$QuickContactItemVerb(this, item, 1, 131152).invoke(null);
            }
            break;
         default:
            int index = QuickContactList.getKeyIndex(key);
            if (index != -1) {
               this._listField.setSelectedIndex(index);
            }
      }

      return super.keyUp(keycode, time);
   }

   private final void enableMoveMode(boolean enable) {
      this._listField.setMoveItemMode(enable);
      if (enable) {
         if (this._addMode && this._newItem != null) {
            this._itemBeingMoved = this._newItem;
         } else {
            this._itemBeingMoved = this.getCurrentItem();
         }
      } else {
         this._itemBeingMoved = null;
      }

      this.updateTitleText();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if ((this._initFlags & 4) != 0) {
         return super.keyChar(key, status, time);
      }

      if (Keypad.getAltedChar(key) == 127) {
         key = 127;
      }

      if (key == 27) {
         if (this._addMode && this._newItem != null) {
            if (this._initialAddModeKey == QuickContactList.getIndexKey(this._listField.getSelectedIndex())) {
               this.close();
               return true;
            }

            this._listField.setSelectedIndex(QuickContactList.getKeyIndex(this._initialAddModeKey));
            return true;
         }

         if (this._listField._moveItemMode) {
            int currIndex = this._listField.getSelectedIndex();
            int oldIndex = QuickContactList.getKeyIndex(this._itemBeingMoved.getKey());
            if (currIndex == oldIndex) {
               this.enableMoveMode(false);
               return true;
            }

            this._listField.setSelectedIndex(oldIndex);
            return true;
         }
      }

      return key == '\n' ? true : super.keyChar(key, status, time);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics g, int index, int y, int width) {
      char key = QuickContactUtil.getValidKeys().charAt(index);
      _paintingContext.put(9045827404276417370L, listField);
      int x = QuickContactUtil.paintHotkey(key, g, 0, y, width, listField.getRowHeight(), 0, _paintingContext);
      QuickContactItem item;
      if (this._addMode) {
         if (this._newItem != null && index == listField.getSelectedIndex()) {
            item = this._newItem;
         } else {
            item = this._items[index];
         }
      } else if (!this._listField._moveItemMode || this._itemBeingMoved == null) {
         item = this._items[index];
      } else if (index == listField.getSelectedIndex()) {
         item = this._itemBeingMoved;
         width -= this._upDownIcon.getWidth(this.getFont().getHeight(), this.getFont().getHeight()) + 4;
      } else if (index == QuickContactList.getKeyIndex(this._itemBeingMoved.getKey())) {
         item = null;
      } else {
         item = this._items[index];
      }

      if (item != null) {
         x += 4;

         try {
            String friendlyName = item.getFriendlyNameString();
            if (friendlyName != null) {
               x += g.drawText(friendlyName, x, y, 64, ADDRESS_WIDTH);
               x += 4;
            }

            g.drawText(item.getRawAddressString(), x, y, 64, width - x);
         } finally {
            this._quickContactList.delete(item);
            return;
         }
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      String outItem = null;
      QuickContactItem item;
      if (this._addMode) {
         if (this._newItem != null && index == listField.getSelectedIndex()) {
            item = this._newItem;
         } else {
            item = this._items[index];
         }
      } else if (!this._listField._moveItemMode || this._itemBeingMoved == null) {
         item = this._items[index];
      } else if (index == listField.getSelectedIndex()) {
         item = this._itemBeingMoved;
      } else if (index == QuickContactList.getKeyIndex(this._itemBeingMoved.getKey())) {
         item = null;
      } else {
         item = this._items[index];
      }

      if (item != null) {
         outItem = item.getFriendlyNameString();
         if (outItem != null) {
            outItem = ((StringBuffer)(new Object())).append(outItem).append(" ").toString();
         }

         outItem = ((StringBuffer)(new Object())).append(outItem).append(item.getRawAddressString()).toString();
      }

      return outItem;
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return 10;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }
}
