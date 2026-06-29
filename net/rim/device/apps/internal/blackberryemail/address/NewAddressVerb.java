package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class NewAddressVerb extends Verb {
   private AddressModelFactory _factory;

   private NewAddressVerb(int menuOrdering, int resourceId, AddressModelFactory factory) {
      super(menuOrdering, EmailResources.getResourceBundle(), resourceId);
      this._factory = factory;
   }

   static final Verb newEmailAddressVerb(AddressModelFactory factory) {
      return new NewAddressVerb(16864256, 1, factory);
   }

   static final Verb newPINAddressVerb(AddressModelFactory factory) {
      return new NewAddressVerb(16864336, 950, factory);
   }

   @Override
   public final Object invoke(Object context) {
      return this._factory.createInstance(context);
   }
}
