package net.rim.device.apps.internal.browser.pme;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MediaField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.resource.PMEPlugginResource;

final class ScrollVerb extends Verb implements PMEPlugginResource {
   private MediaField _field;
   private static ResourceBundle _resources = ResourceBundle.getBundle(1239635442466553906L, "net.rim.device.apps.internal.resource.PMEPluggin");

   public ScrollVerb(MediaField field) {
      super(341278);
      this._field = field;
   }

   @Override
   public final String toString() {
      return this._field.isScrollMode() ? _resources.getString(15) : _resources.getString(14);
   }

   @Override
   public final Object invoke(Object context) {
      this._field.setScrollMode(!this._field.isScrollMode());
      return null;
   }
}
