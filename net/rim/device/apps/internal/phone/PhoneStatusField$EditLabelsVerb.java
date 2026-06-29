package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class PhoneStatusField$EditLabelsVerb extends Verb {
   public PhoneStatusField$EditLabelsVerb() {
      super(611152);
   }

   @Override
   public final String toString() {
      return PhoneResources.getString(6322);
   }

   @Override
   public final Object invoke(Object parameter) {
      UiApplication.getUiApplication().pushScreen(new EditLineDescriptionScreen());
      return null;
   }
}
