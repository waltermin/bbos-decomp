package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class EditAddressCardScreen$1 extends Verb {
   private final EditAddressCardScreen this$0;

   EditAddressCardScreen$1(EditAddressCardScreen _1, int x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return AddressBookResources.getString(1733);
   }

   @Override
   public final Object invoke(Object context) {
      this.this$0._customTuneField = TuneManager.getTuneManager().getTuneChoiceField(AddressBookResources.getString(1734), null, null, true);
      this.this$0.add(this.this$0._customTuneField);
      this.this$0._customTuneField.setFocus();
      this.this$0.setDirty(true);
      return null;
   }
}
