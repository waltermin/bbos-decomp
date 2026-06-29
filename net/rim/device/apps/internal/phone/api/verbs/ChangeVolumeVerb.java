package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class ChangeVolumeVerb extends Verb implements PhoneVerb {
   public ChangeVolumeVerb() {
      this(71680);
   }

   public ChangeVolumeVerb(int ordering) {
      super(ordering, PhoneResources.getResourceBundle(), 413);
   }

   @Override
   public final Object invoke(Object context) {
      UiApplication owner = UiApplication.getUiApplication();
      StandardVolumeGaugePopup volumeGaugePopup = new StandardVolumeGaugePopup();
      volumeGaugePopup.setOwner(owner);
      volumeGaugePopup.show();
      return null;
   }
}
