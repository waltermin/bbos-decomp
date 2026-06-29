package net.rim.device.apps.internal.browser.pme;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;

final class PanVerb extends ZoomAndPanVerb {
   private ResourceBundle _resources = ResourceBundle.getBundle(1239635442466553906L, "net.rim.device.apps.internal.resource.PMEPluggin");
   private String _label = this._resources.getString(19);

   PanVerb(Field field, Object player) {
      super(field, player);
   }

   @Override
   public final String toString() {
      return this._label;
   }

   @Override
   public final void setStartingState() {
      ZoomAndPanVerb._zpScreen.setStartingState(0);
   }
}
