package net.rim.device.apps.internal.options;

import net.rim.device.api.system.Branding;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.system.InternalServices;

final class OptionsApp$OptionsListScreen extends MainScreen {
   OptionsApp$OptionGroup _group;
   int _titleId;

   OptionsApp$OptionsListScreen(OptionsApp$OptionGroup group, int titleId) {
      this._group = group;
      this._titleId = titleId;
      this.updateTitle();
   }

   final void updateTitle() {
      String title = StringUtilities.removeChars(net.rim.device.apps.internal.options.resources.OptionsResources.getString(0), "̲");
      if (InternalServices.isReducedFormFactor() && Branding.getVendorId() == 100) {
         title = "Settings";
      }

      if (this._titleId != -1) {
         StringBuffer fullTitle = new StringBuffer(title);
         fullTitle.append(" - ");
         fullTitle.append(net.rim.device.apps.internal.options.resources.OptionsResources.getString(this._titleId));
         title = fullTitle.toString();
      }

      this.setTitle(title);
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this._group.openCurrentItem(OptionsApp._context);
            return true;
         default:
            return false;
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            this._group.openCurrentItem(OptionsApp._context);
            return true;
         case '\u001b':
            this._group.close(true);
            return true;
         default:
            return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean stylusTap(int x, int y, int status, int time) {
      return super.stylusTap(x, y, status, time) ? true : this._group.openCurrentItem(OptionsApp._context);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return this.invokeAction(1);
   }
}
