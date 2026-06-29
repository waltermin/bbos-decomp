package net.rim.device.apps.internal.browser.ui;

import net.rim.device.internal.ui.IconCollection;

public final class BrowserIcons {
   public static final int RIM_TX_SENDING = 0;
   public static final int RIM_TX_PENDING = 1;
   public static final int RIM_BROWSER_LOADED_UNOPENED = 2;
   public static final int RIM_BROWSER_LOADED_OPENED = 3;
   public static final int RIM_RX_ERROR = 4;
   private static final int ICON_COUNT = 5;
   private static IconCollection _icons = IconCollection.get("net_rim_Browser", 5);

   private BrowserIcons() {
   }

   public static final IconCollection getIcons() {
      return _icons;
   }
}
