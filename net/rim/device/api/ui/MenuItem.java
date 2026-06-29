package net.rim.device.api.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleText;
import net.rim.device.api.ui.accessibility.AccessibleValue;
import net.rim.device.api.ui.menu.MenuItemPrefab;
import net.rim.device.api.util.Comparator;
import net.rim.device.internal.ui.Image;

public class MenuItem implements Runnable, AccessibleContext {
   private ResourceBundle _bundle;
   private int _id;
   private int _ordinal;
   private int _priority;
   private Locale _locale;
   private String _text;
   private Image _icon;
   public static final byte SPELL_CHECK_ITEMS = 1;
   private static String SEPARATOR_STRING = "-";
   public static final int SEPARATOR_ID = -1;
   public static final int CHANGE_OPTION = 0;
   public static final int COPY = 1;
   public static final int CUT = 2;
   public static final int PASTE = 3;
   public static final int SELECT = 4;
   public static final int CANCEL_SELECT = 5;
   public static final int SPELL_RUN = 6;
   public static final int SPELL_CONTINUE = 7;
   public static final int SPELL_STOP = 8;
   public static final int CLOSE = 14;
   public static final int SAVE_CLOSE = 15;
   public static final int MODE_SELECT = 16;
   public static final int MODE_VIEW = 17;
   public static final int FULL_MENU = 18;
   public static final Comparator ORDINAL_COMPARATOR = new MenuItem$1();

   public MenuItem(ResourceBundle bundle, int id, int ordinal, int priority) {
      if (priority >= 0 && ordinal >= 0) {
         this._bundle = bundle;
         this._id = id;
         this._ordinal = ordinal;
         this._priority = priority;
         if (bundle != null) {
            this._locale = Locale.getDefault();
            this._text = this._bundle.getString(id);
         }

         this._icon = null;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public MenuItem(String text, int ordinal, int priority) {
      this(text, ordinal, priority, null);
   }

   public MenuItem(String text, int ordinal, int priority, Image icon) {
      if (priority >= 0 && ordinal >= 0) {
         this._text = text;
         this._ordinal = ordinal;
         this._priority = priority;
         this._icon = icon;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public ResourceBundle getBundle() {
      return this._bundle;
   }

   public Image getIcon() {
      return this._icon;
   }

   public Bitmap getIconBitmap() {
      return null;
   }

   public int getId() {
      return this._id;
   }

   public int getOrdinal() {
      return this._ordinal;
   }

   public static MenuItem getPrefab(int id) {
      return MenuItemPrefab.get(id);
   }

   public int getPriority() {
      return this._priority;
   }

   public Field getTarget() {
      return ContextMenu.getInstance().getTarget();
   }

   public boolean isSeparator() {
      return this._bundle == null && this._id == -1 || SEPARATOR_STRING.equals(this._text);
   }

   public static MenuItem separator(int ordinal) {
      return new MenuItem$2(SEPARATOR_STRING, ordinal, Integer.MAX_VALUE);
   }

   public void setIcon(Image icon) {
      this._icon = icon;
   }

   public void setOrdinal(int ordinal) {
      this._ordinal = ordinal;
   }

   public void setPriority(int priority) {
      this._priority = priority;
   }

   public void setText(String text) {
      this._text = text;
   }

   @Override
   public String toString() {
      if (this._bundle != null && this._locale != Locale.getDefault()) {
         this._locale = Locale.getDefault();
         this._text = this._bundle.getString(this._id);
      }

      return this._text;
   }

   public boolean isGroupItem(byte groupID) {
      return false;
   }

   @Override
   public int getAccessibleRole() {
      return 3;
   }

   @Override
   public String getAccessibleName() {
      return this.toString();
   }

   @Override
   public String getAccessibleDescription() {
      return null;
   }

   @Override
   public AccessibleContext getAccessibleParent() {
      return null;
   }

   @Override
   public int getAccessibleChildCount() {
      return 0;
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      return null;
   }

   @Override
   public int getAccessibleStateSet() {
      return 1;
   }

   @Override
   public boolean isAccessibleStateSet(int state) {
      return false;
   }

   @Override
   public String getAccessibleIconDescription() {
      return null;
   }

   @Override
   public AccessibleText getAccessibleText() {
      return null;
   }

   @Override
   public AccessibleValue getAccessibleValue() {
      return null;
   }

   @Override
   public int getAccessibleSelectionCount() {
      return 0;
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      return null;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      return false;
   }

   @Override
   public void run() {
      throw null;
   }
}
