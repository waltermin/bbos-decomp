package net.rim.device.apps.internal.browser.plugin.media.menu;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.MediaUtilities;

public final class SetAsRingtoneMenuItem extends MenuItem {
   private String _tuneName;

   public SetAsRingtoneMenuItem(String tuneName) {
      super(BrowserResources.getResourceBundle(), 787, 565264, Integer.MAX_VALUE);
      this._tuneName = tuneName;
   }

   @Override
   public final void run() {
      if (MediaUtilities.setAsRingtone(this._tuneName)) {
         Dialog.alert(MessageFormat.format(BrowserResources.getString(829), new Object[]{this._tuneName}));
      }
   }
}
