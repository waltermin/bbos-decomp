package net.rim.device.api.ui.accessibility;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public class AccessibleRole {
   public static final int UNKNOWN;
   public static final int SCREEN;
   public static final int TEXT_FIELD;
   public static final int MENU_ITEM;
   public static final int MENU;
   public static final int APP_ICON;
   public static final int PUSH_BUTTON;
   public static final int DIALOG;
   public static final int CHECKBOX;
   public static final int DATE;
   public static final int PASSWORD;
   public static final int RADIO_BUTTON;
   public static final int COMBO;
   public static final int CHOICE;
   public static final int HYPERLINK;
   public static final int ICON;
   public static final int LABEL;
   public static final int GAUGE;
   public static final int HINT_POPUP;
   public static final int CHART;
   public static final int PIN;
   public static final int SCROLLBAR;
   public static final int SYMBOL;
   public static final int BITMAP;
   public static final int SEPARATOR;
   public static final int LIST;
   public static final int FILTERED_LIST;
   public static final int TREE_FIELD;
   public static final int CALENDAR_FIELD;
   public static final int MONTH_FIELD;
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(-5723050251981538727L, "net.rim.device.internal.resource.AccessibleRole");

   public static String toDisplayString(int role) {
      return _rb.getString(role);
   }
}
