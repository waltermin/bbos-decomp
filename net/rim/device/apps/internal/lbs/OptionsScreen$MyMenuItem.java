package net.rim.device.apps.internal.lbs;

import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.internal.lbs.maplet.MapletCache;

final class OptionsScreen$MyMenuItem extends MenuItem {
   private int _item;

   OptionsScreen$MyMenuItem(int item) {
      super(OptionsScreen._resources, OptionsScreen.getMenuId(item), item, 10000);
      this._item = item;
   }

   @Override
   public final void run() {
      switch (this._item) {
         case 65550:
            return;
         case 65551:
         default:
            String text = OptionsScreen._resources.getString(10);
            if (Dialog.ask(2, text) == 3) {
               MapletCache.getInstance().clear();
               if (OptionsScreen._currentCacheSize != null) {
                  OptionsScreen._currentCacheSize.setText("Cache Size: " + MapletCache.getSyncCollectionSize() / 1024 + " kB");
               }
            }

            return;
         case 65552:
            LBSOptions._dataCount = 0;
            LBSOptions.setInt(8640332184073563572L, LBSOptions._dataCount);
            OptionsScreen screen = (OptionsScreen)UiApplication.getUiApplication().getActiveScreen();
            screen.setDownloadData();
            screen.invalidate();
            return;
         case 65553:
            try {
               OptionsScreen.access$102(GPSProvider.getInstance().getDeviceInUse());
               RibbonLauncher.getInstance().launch("net_rim_bb_options_app.BluetoothConfig");
            } finally {
               return;
            }
      }
   }
}
