package net.rim.device.apps.internal.addressbook.userfields;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class EditLabelVerb extends Verb {
   private int _fieldId;
   private EditField _editField;

   EditLabelVerb(int id, EditField editField) {
      super(16865616);
      this._fieldId = id;
      this._editField = editField;
   }

   @Override
   public final Object invoke(Object parameter) {
      AddressBookOptions options = AddressBookServices.getAddressBookOptions();
      if (options.editUserDefinedFieldLabel(this._fieldId)) {
         this._editField.setLabel(options.getUserDefinedFieldLabel(this._fieldId) + ": ");
      }

      return null;
   }

   @Override
   public final String toString() {
      return AddressBookResources.getString(1201);
   }
}
