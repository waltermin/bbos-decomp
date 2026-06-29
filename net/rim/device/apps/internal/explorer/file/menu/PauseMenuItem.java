package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.explorer.file.render.RenderScreen;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class PauseMenuItem extends MenuItem {
   RenderScreen _screenToPause;
   boolean _pause;

   public PauseMenuItem(RenderScreen screenToPause, boolean pause) {
      super(ExplorerResources.getResourceBundleFamily(), pause ? 85 : 84, 591105, 0);
      this._screenToPause = screenToPause;
      this._pause = pause;
   }

   @Override
   public final void run() {
      this._screenToPause.pause(this._pause);
   }
}
