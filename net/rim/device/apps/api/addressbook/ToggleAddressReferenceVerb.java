package net.rim.device.apps.api.addressbook;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;

final class ToggleAddressReferenceVerb extends Verb {
   private AddressReferenceViewField _field;

   public ToggleAddressReferenceVerb(AddressReferenceViewField field, ResourceBundleFamily rb, int rbKey) {
      super(16859712, rb, rbKey);
      this._field = field;
   }

   @Override
   public final Object invoke(Object parameter) {
      this._field.toggleVisibleField();
      return null;
   }
}
