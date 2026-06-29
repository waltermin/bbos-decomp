package net.rim.device.api.ui.accessibility;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public class AccessibleRole {
   public static final int UNKNOWN = 0;
   public static final int SCREEN = 1;
   public static final int TEXT_FIELD = 2;
   public static final int MENU_ITEM = 3;
   public static final int MENU = 4;
   public static final int APP_ICON = 5;
   public static final int PUSH_BUTTON = 6;
   public static final int DIALOG = 7;
   public static final int CHECKBOX = 8;
   public static final int DATE = 9;
   public static final int PASSWORD = 10;
   public static final int RADIO_BUTTON = 11;
   public static final int COMBO = 12;
   public static final int CHOICE = 13;
   public static final int HYPERLINK = 14;
   public static final int ICON = 15;
   public static final int LABEL = 16;
   public static final int GAUGE = 17;
   public static final int HINT_POPUP = 18;
   public static final int CHART = 19;
   public static final int PIN = 20;
   public static final int SCROLLBAR = 21;
   public static final int SYMBOL = 22;
   public static final int BITMAP = 23;
   public static final int SEPARATOR = 24;
   public static final int LIST = 25;
   public static final int FILTERED_LIST = 26;
   public static final int TREE_FIELD = 27;
   public static final int CALENDAR_FIELD = 28;
   public static final int MONTH_FIELD = 29;
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(-5723050251981538727L, "net.rim.device.internal.resource.AccessibleRole");

   public static String toDisplayString(int role) {
      return _rb.getString(role);
   }
}
