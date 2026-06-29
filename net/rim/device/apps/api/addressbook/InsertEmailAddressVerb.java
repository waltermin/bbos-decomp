package net.rim.device.apps.api.addressbook;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;

public final class InsertEmailAddressVerb extends Verb {
   private EditField _editField;
   private String _label;
   private static AddressSelectionContext _selectionContext;
   private static Verb _invokeAddressBookVerb;

   public InsertEmailAddressVerb(int menuOrder, String label, EditField editField) {
      super(menuOrder);
      if (editField == null) {
         throw new IllegalArgumentException("Invalid EditField");
      }

      this._label = label;
      this._editField = editField;
      if (_selectionContext == null) {
         Verb addressSelectionVerb = AddressBookServices.getAddressSelectionVerb(-2985347935260258684L);
         if (addressSelectionVerb != null) {
            _selectionContext = new AddressSelectionContext(null, null, null, RecognizerRepository.getRecognizers(-2985347935260258684L), null);
            _invokeAddressBookVerb = addressSelectionVerb;
         }
      }
   }

   @Override
   public final String toString() {
      return this._label;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (_invokeAddressBookVerb == null) {
         return null;
      }

      Object addressBookEntry = _invokeAddressBookVerb.invoke(_selectionContext);
      String selectedAddress = null;
      if (!(addressBookEntry instanceof EmailAddressModel)) {
         if (addressBookEntry != null) {
            selectedAddress = addressBookEntry.toString();
         }
      } else {
         selectedAddress = ((EmailAddressModel)addressBookEntry).getAddress();
      }

      if (selectedAddress != null) {
         this._editField.insert(selectedAddress, Integer.MIN_VALUE, false, true);
      }

      return null;
   }
}
