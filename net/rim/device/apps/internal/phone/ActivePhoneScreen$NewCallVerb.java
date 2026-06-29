package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class ActivePhoneScreen$NewCallVerb extends Verb {
   private final ActivePhoneScreen this$0;

   ActivePhoneScreen$NewCallVerb(ActivePhoneScreen _1) {
      super(131329, PhoneResources.getResourceBundle(), 420);
      this.this$0 = _1;
   }

   @Override
   public final int getOrdering() {
      return PhoneUtilities.cdmaTypeNetwork() ? 70928 : super.getOrdering();
   }

   @Override
   public final String toString() {
      return PhoneUtilities.cdmaTypeNetwork() ? PhoneResources.getString(465) : super.toString();
   }

   @Override
   public final Object invoke(Object context) {
      this.this$0._voiceApp.showNewCallScreen();
      return null;
   }
}
