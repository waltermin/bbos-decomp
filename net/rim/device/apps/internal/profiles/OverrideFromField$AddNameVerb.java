package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.framework.model.CompoundRecognizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class OverrideFromField$AddNameVerb extends Verb {
   private AddressSelectionContext _selectionContext;
   private Verb _invokeAddressBookVerb;
   private boolean _changeFrom;
   private final OverrideFromField this$0;

   private OverrideFromField$AddNameVerb(OverrideFromField _1, boolean changeFrom) {
      super(607792);
      this.this$0 = _1;
      this._changeFrom = changeFrom;
      if (this._selectionContext == null) {
         RIMModelFactory[] abEntryFactories = RIMModelFactoryRepository.getModelFactories(-7921492803965144520L);
         CompoundRecognizer compoundRecognizer = new OverrideFromField$AddNameVerb$1(this, abEntryFactories, _1);
         String titleString = CommonResources.getString(9091) + ": ";
         this._selectionContext = new AddressSelectionContext(titleString, null, null, compoundRecognizer, null);
         this._selectionContext.setFindLabel(titleString);
         this._selectionContext.setContext(new ContextObject(108));
         this._invokeAddressBookVerb = AddressBookServices.getAddressSelectionVerb(4738722199580714034L);
      }
   }

   @Override
   public final String toString() {
      ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      return this._changeFrom ? resources.getString(244) : resources.getString(233);
   }

   @Override
   public final Object invoke(Object context) {
      this._selectionContext.setInitialSearchPattern(null);
      RIMModel addressBookEntry = (RIMModel)this._invokeAddressBookVerb.invoke(this._selectionContext);
      if (addressBookEntry instanceof AddressCardModel) {
         if (this._changeFrom) {
            this.this$0.changeName((AddressCardModel)addressBookEntry);
            return null;
         }

         this.this$0.addName((AddressCardModel)addressBookEntry);
      }

      return null;
   }

   OverrideFromField$AddNameVerb(OverrideFromField x0, boolean x1, OverrideFromField$1 x2) {
      this(x0, x1);
   }
}
