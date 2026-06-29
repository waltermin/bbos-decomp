package net.rim.device.apps.internal.phone;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;

final class PhoneVerbManager$1 extends Verb {
   private final PhoneVerbManager this$0;

   PhoneVerbManager$1(PhoneVerbManager _1, int x0, ResourceBundleFamily x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      VoiceServices.getUiApplication().requestForeground();
      return null;
   }
}
