package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class EditAddressCardScreen$2 extends Verb {
   private final EditAddressCardScreen this$0;

   EditAddressCardScreen$2(EditAddressCardScreen _1, int x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(600);
   }

   @Override
   public final Object invoke(Object context) {
      this.this$0._customTuneField.getManager().delete(this.this$0._customTuneField);
      this.this$0._customTuneField = null;
      this.this$0.setDirty(true);
      return null;
   }
}
