package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.file.FileSelector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.profiles.Overrides;

public final class AddCustomTuneVerb extends Verb implements VerbFactory {
   public AddCustomTuneVerb() {
      super(16864384);
   }

   @Override
   public final String toString() {
      return AddressBookResources.getString(1733);
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      Object selectedElement = ContextObject.get(context, 252);
      return selectedElement instanceof Object ? new Object[]{this} : null;
   }

   @Override
   public final Object invoke(Object context) {
      AddressCardModel addressCard = (AddressCardModel)ContextObject.get(context, 3696141428889703675L);
      FileSelector fileSelector = (FileSelector)(new Object(null, 2));
      String selectedTune = fileSelector.selectFile(null);
      if (selectedTune != null) {
         int result = 4;
         if (Overrides.getInstance().getCustomTune(addressCard.getUID()) != null) {
            result = Dialog.ask(3, AddressBookResources.getString(1743));
         }

         if (result == 4) {
            Overrides.getInstance().setCustomTune(addressCard, selectedTune);
            Dialog.alert(AddressBookResources.getString(1742));
         }
      }

      return null;
   }
}
