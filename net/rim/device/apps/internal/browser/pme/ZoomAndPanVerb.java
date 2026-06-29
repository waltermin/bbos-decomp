package net.rim.device.apps.internal.browser.pme;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.resource.PMEPlugginResource;
import net.rim.plazmic.mediaengine.MediaManager;

class ZoomAndPanVerb extends Verb implements PMEPlugginResource {
   protected Field _field;
   protected Object _player;
   private static ResourceBundle _resources = ResourceBundle.getBundle(1239635442466553906L, "net.rim.device.apps.internal.resource.PMEPluggin");
   protected static ZoomPanScreen _zpScreen;

   protected void setStartingState() {
      throw null;
   }

   public ZoomAndPanVerb(Field field, Object player) {
      super(341288);
      if (this._field == null) {
         this._field = field;
         this._player = player;
      }
   }

   @Override
   public String toString() {
      throw null;
   }

   @Override
   public Object invoke(Object context) {
      if (_zpScreen == null) {
         MediaManager manager = new MediaManager();
         Object overlaySkin = null;

         try {
            overlaySkin = manager.createMedia(_resources.getString(16), "application/x-vnd.rim.pme.b");
         } finally {
            ;
         }

         _zpScreen = new ZoomPanScreen(3458764513821065216L, this._field, overlaySkin, this._player);
      } else {
         _zpScreen.setField(this._field, this._player);
      }

      if (_zpScreen != null) {
         this.setStartingState();
         Application.getApplication().invokeLater(new ZoomAndPanVerb$1(this));
      }

      return null;
   }
}
