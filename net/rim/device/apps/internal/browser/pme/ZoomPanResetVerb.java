package net.rim.device.apps.internal.browser.pme;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.resource.PMEPlugginResource;

final class ZoomPanResetVerb extends Verb implements PMEPlugginResource {
   private ResourceBundle _resources = ResourceBundle.getBundle(1239635442466553906L, "net.rim.device.apps.internal.resource.PMEPluggin");
   private String _label = this._resources.getString(21);
   private Field _field;
   private Object _player;

   public ZoomPanResetVerb(Field field, Object player) {
      super(341288);
      this._field = field;
      this._player = player;
   }

   @Override
   public final String toString() {
      return this._label;
   }

   @Override
   public final Object invoke(Object context) {
      if (ZoomAndPanVerb._zpScreen != null) {
         ZoomAndPanVerb._zpScreen.setField(this._field, this._player);
         ZoomAndPanVerb._zpScreen.resetZoomPan();
      }

      return null;
   }
}
