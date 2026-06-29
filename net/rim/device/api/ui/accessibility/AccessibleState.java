package net.rim.device.api.ui.accessibility;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public class AccessibleState {
   public static final int UNSET = 1;
   public static final int FOCUSED = 2;
   public static final int SELECTED = 4;
   public static final int ACTIVE = 8;
   public static final int PUSHED = 16;
   public static final int BUSY = 32;
   public static final int CHECKED = 64;
   public static final int EDITABLE = 128;
   public static final int EXPANDABLE = 256;
   public static final int EXPANDED = 512;
   public static final int COLLAPSED = 1024;
   public static final int FOCUSABLE = 2048;
   public static final int MODAL = 4096;
   public static final int OPAQUE = 8192;
   public static final int MULTI_SELECTABLE = 16384;
   public static final int SELECTABLE = 32768;
   public static final int VISIBLE = 65536;
   public static final int VERTICAL = 131072;
   public static final int HORIZONTAL = 262144;
   public static final int SINGLE_LINE = 524288;
   public static final int MULTI_LINE = 1048576;
   public static final int TRUNCATED = 2097152;
   public static final int AVAILABLE = 4194304;
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(-5104550746822732920L, "net.rim.device.internal.resource.AccessibleState");

   public static String toDisplayString(int state) {
      int count;
      for (count = 0; state >= 1; count++) {
         state >>= 1;
      }

      return _rb.getString(count);
   }
}
