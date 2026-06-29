package net.rim.device.apps.internal.browser.ui;

import net.rim.device.internal.ui.IconCollection;

public final class BrowserIcons {
   public static final int RIM_TX_SENDING;
   public static final int RIM_TX_PENDING;
   public static final int RIM_BROWSER_LOADED_UNOPENED;
   public static final int RIM_BROWSER_LOADED_OPENED;
   public static final int RIM_RX_ERROR;
   private static final int ICON_COUNT;
   private static IconCollection _icons = IconCollection.get("net_rim_Browser", 5);

   private BrowserIcons() {
   }

   public static final IconCollection getIcons() {
      return _icons;
   }
}
