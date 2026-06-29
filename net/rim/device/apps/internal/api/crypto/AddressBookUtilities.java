package net.rim.device.apps.internal.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.CompoundRecognizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.address.UseOnceAddressVerb;
import net.rim.vm.Array;

public class AddressBookUtilities {
   private AddressBookUtilities() {
   }

   public static EmailAddressModel[] getEmailAddresses(EmailAddressModel itemToSelect, String usePrefix) {
      PersistableRIMModel model = getEmailPINAddress(false, itemToSelect, usePrefix);
      if (model instanceof Object) {
         return new Object[]{model};
      }

      if (!(model instanceof Object)) {
         return null;
      }

      GroupAddressCardModel groupModel = (GroupAddressCardModel)model;
      int groupModelSize = groupModel.size();
      EmailAddressModel[] newModels = new Object[groupModelSize];
      int total = 0;

      for (int j = 0; j < groupModelSize; j++) {
         if (groupModel.getAddressModelTypeAt(j) == 0) {
            RIMModel memberAddress = groupModel.getAddressModelAt(j);
            if (memberAddress != null && memberAddress instanceof Object) {
               newModels[j] = (EmailAddressModel)memberAddress;
               total++;
            }
         }
      }

      Array.resize(newModels, total);
      return newModels;
   }

   private static PersistableRIMModel getEmailPINAddress(boolean isPin, EmailAddressModel itemToSelect, String usePrefix) {
      VerbRepository addressVerbs = VerbRepository.getVerbRepository(-1789952090272871921L);
      PersistableRIMModel newModel = null;
      Recognizer pinRecognizer = RecognizerRepository.getRecognizers(4246852237058296601L);
      Recognizer groupAddressRecognizer = RecognizerRepository.getRecognizers(-1326186686655625745L);
      Verb[] verbs = addressVerbs.getVerbs(isPin ? 4246852237058296601L : -2985347935260258684L);
      if (verbs.length > 0) {
         Recognizer baseRecognizer = null;
         Verb[] useOnceVerbs;
         if (isPin) {
            useOnceVerbs = new Object[]{UseOnceAddressVerb.newUseOncePINAddressVerb(null)};
            baseRecognizer = pinRecognizer;
         } else {
            useOnceVerbs = new Object[]{UseOnceAddressVerb.newUseOnceEmailAddressVerb(false)};
            baseRecognizer = RecognizerRepository.getRecognizers(-2985347935260258684L);
         }

         Recognizer recognizerToUse;
         if (groupAddressRecognizer != null) {
            CompoundRecognizer compoundRecognizer = (CompoundRecognizer)(new Object());
            compoundRecognizer.addRecognizer(groupAddressRecognizer);
            compoundRecognizer.addRecognizer(baseRecognizer);
            recognizerToUse = compoundRecognizer;
         } else {
            recognizerToUse = baseRecognizer;
         }

         Recognizer[] recognizers = new Object[]{recognizerToUse};
         ContextObject selectionContextObject = (ContextObject)(new Object());
         AddressSelectionContext selectionContext = (AddressSelectionContext)(new Object(null, null, null, recognizers, useOnceVerbs));
         int verbGroupId = isPin ? 13685231 : 15556151;
         selectionContextObject.put(6609423255094033855L, new Object(verbGroupId));
         selectionContext.setContext(selectionContextObject);
         selectionContext.setUseEntryPrefixes(new Object[]{usePrefix});
         selectionContext.setPreferredDefaultIndex(isPin ? 1 : 0);
         if (itemToSelect != null) {
            AddressBookServices.setLastSelectedAddress(itemToSelect);
         }

         newModel = (PersistableRIMModel)verbs[0].invoke(selectionContext);
         if (newModel != null && ObjectGroup.isInGroup(newModel)) {
            ContextObject cloneContext = (ContextObject)(new Object());
            cloneContext.put(254, newModel);
            long addressObjectType = -2985347935260258684L;
            if (groupAddressRecognizer != null && groupAddressRecognizer.recognize(newModel)) {
               addressObjectType = -1326186686655625745L;
            } else if (pinRecognizer.recognize(newModel) || isPin) {
               addressObjectType = 4246852237058296601L;
            }

            Factory factory = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(addressObjectType);
            newModel = (PersistableRIMModel)factory.createInstance(cloneContext);
         }
      }

      return newModel;
   }
}
