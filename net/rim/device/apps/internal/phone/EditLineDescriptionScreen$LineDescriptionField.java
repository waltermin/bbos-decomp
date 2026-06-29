package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class EditLineDescriptionScreen$LineDescriptionField extends EditField {
   EditLineDescriptionScreen$LineDescriptionField(String description) {
      super(((StringBuffer)(new Object())).append(PhoneResources.getString(6323)).append(": ").toString(), description, 20, 2201170739200L);
   }

   @Override
   protected final boolean isSymbolScreenAllowed() {
      return false;
   }
}
