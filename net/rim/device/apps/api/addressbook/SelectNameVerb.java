package net.rim.device.apps.api.addressbook;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.framework.model.CompoundRecognizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

public class SelectNameVerb extends Verb {
   private EditField _editField;
   private static AddressSelectionContext _selectionContext;
   private static Verb _invokeAddressBookVerb;

   public SelectNameVerb(EditField editField) {
      super(16865360);
      this._editField = editField;
      if (_selectionContext == null) {
         VerbRepository addressVerbs = VerbRepository.getVerbRepository(-1789952090272871921L);
         Verb[] verbs = addressVerbs.getVerbs(null);
         RIMModelFactory[] abEntryFactories = RIMModelFactoryRepository.getModelFactories(-7921492803965144520L);
         if (verbs.length > 0 && abEntryFactories.length > 0) {
            String titleString = CommonResources.getString(9091) + ": ";
            _selectionContext = new AddressSelectionContext(titleString, null, null, new CompoundRecognizer(abEntryFactories), null);
            _selectionContext.setFindLabel(titleString);
            _invokeAddressBookVerb = verbs[0];
         }
      }
   }

   @Override
   public String toString() {
      return CommonResources.getString(9091);
   }

   @Override
   public Object invoke(Object context) {
      _selectionContext.setInitialSearchPattern(null);
      RIMModel addressBookEntry = (RIMModel)_invokeAddressBookVerb.invoke(_selectionContext);
      if (addressBookEntry instanceof FieldProvider) {
         Field field = ((FieldProvider)addressBookEntry).getField(new ContextObject(16, 0));
         if (field instanceof EditField) {
            EditField entryEditField = (EditField)field;
            if (this._editField.getText().length() > 0) {
               this._editField.insert(";");
            }

            this._editField.insert(entryEditField.getText());
            this._editField.setFocus();
            this._editField.setDirty(true);
            this._editField.setMuddy(true);
         }
      }

      return null;
   }
}
