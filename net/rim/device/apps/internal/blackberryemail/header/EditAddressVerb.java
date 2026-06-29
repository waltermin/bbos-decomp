package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.framework.model.CompoundRecognizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.implus.IMPlusComposeModel;
import net.rim.device.apps.api.messaging.implus.IMPlusServiceModel;
import net.rim.device.apps.internal.blackberryemail.address.UseOnceAddressVerb;
import net.rim.device.apps.internal.blackberryemail.email.EmailEditorScreen;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.Array;

final class EditAddressVerb extends Verb {
   private EmailHeaderModel _emailHeaderModel;

   EditAddressVerb(EmailHeaderModel headerModel) {
      super(16859728, EmailResources.getResourceBundle(), 2);
      this._emailHeaderModel = headerModel;
   }

   @Override
   public final Object invoke(Object context) {
      VerbRepository addressVerbs = VerbRepository.getVerbRepository(-1789952090272871921L);
      ContextObject contextObject = ContextObject.clone(context);
      boolean allowCrossServiceAddressSelection = false;
      if (contextObject.getFlag(13) && !contextObject.getFlag(115)) {
         allowCrossServiceAddressSelection = true;
      }

      allowCrossServiceAddressSelection &= !ITPolicy.getBoolean(24, 38, false);
      boolean isPin = ContextObject.getFlag(context, 94);
      Verb[] verbs = new Verb[0];
      if (isPin || allowCrossServiceAddressSelection) {
         Verb[] tempVerbs = addressVerbs.getVerbs(4246852237058296601L);
         Array.resize(verbs, verbs.length + tempVerbs.length);
         System.arraycopy(tempVerbs, 0, verbs, 0, tempVerbs.length);
      }

      if (!isPin || allowCrossServiceAddressSelection) {
         Verb[] tempVerbs = addressVerbs.getVerbs(-2985347935260258684L);
         Array.resize(verbs, verbs.length + tempVerbs.length);
         System.arraycopy(tempVerbs, 0, verbs, verbs.length - tempVerbs.length, tempVerbs.length);
      }

      PersistableRIMModel newModel = null;
      Recognizer pinRecognizer = RecognizerRepository.getRecognizers(4246852237058296601L);
      Recognizer emailRecognizer = RecognizerRepository.getRecognizers(-2985347935260258684L);
      Recognizer groupAddressRecognizer = RecognizerRepository.getRecognizers(-1326186686655625745L);
      if (verbs.length > 0) {
         String[] useEntryPrefixes = new String[0];
         Verb[] useOnceVerbs = new Verb[0];
         Recognizer[] recognizers = new Recognizer[0];
         if (!isPin || allowCrossServiceAddressSelection) {
            Array.resize(useOnceVerbs, useOnceVerbs.length + 1);
            useOnceVerbs[useOnceVerbs.length - 1] = UseOnceAddressVerb.newUseOnceEmailAddressVerb(false);
            Recognizer recognizerToUse = null;
            if (groupAddressRecognizer != null) {
               CompoundRecognizer compoundRecognizer = new CompoundRecognizer();
               compoundRecognizer.addRecognizer(groupAddressRecognizer);
               compoundRecognizer.addRecognizer(emailRecognizer);
               recognizerToUse = compoundRecognizer;
            } else {
               recognizerToUse = emailRecognizer;
            }

            Array.resize(recognizers, recognizers.length + 1);
            recognizers[recognizers.length - 1] = recognizerToUse;
            Array.resize(useEntryPrefixes, useEntryPrefixes.length + 1);
            useEntryPrefixes[useEntryPrefixes.length - 1] = EmailResources.getString(1100);
         }

         if (isPin || allowCrossServiceAddressSelection) {
            Array.resize(useOnceVerbs, useOnceVerbs.length + 1);
            useOnceVerbs[useOnceVerbs.length - 1] = UseOnceAddressVerb.newUseOncePINAddressVerb(null);
            Array.resize(recognizers, recognizers.length + 1);
            recognizers[recognizers.length - 1] = pinRecognizer;
            Array.resize(useEntryPrefixes, useEntryPrefixes.length + 1);
            useEntryPrefixes[useEntryPrefixes.length - 1] = EmailResources.getString(1101);
         }

         ContextObject selectionContextObject = new ContextObject();
         IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
         IMPlusComposeModel[] implusComposeModels = null;
         if (implusService != null && (ContextObject.getPrivateFlag(context, -3859986508589425865L, 1) || allowCrossServiceAddressSelection)) {
            implusComposeModels = implusService.getComposeModels();
            int numModels = implusComposeModels.length;

            for (int i = 0; i < numModels; i++) {
               Array.resize(useOnceVerbs, useOnceVerbs.length + 1);
               useOnceVerbs[useOnceVerbs.length - 1] = implusComposeModels[i].getUseOnceVerb();
               Array.resize(recognizers, recognizers.length + 1);
               recognizers[recognizers.length - 1] = implusComposeModels[i].getRecognizer();
               Array.resize(useEntryPrefixes, useEntryPrefixes.length + 1);
               useEntryPrefixes[useEntryPrefixes.length - 1] = implusComposeModels[i].getUseEntryPrefix();
               if (implusComposeModels[i].getObjectType() == 3797587162219887872L) {
                  selectionContextObject.setFlag(34);
                  selectionContextObject.setFlag(42);
               }
            }
         }

         AddressSelectionContext selectionContext = new AddressSelectionContext(null, null, null, recognizers, useOnceVerbs);
         int verbGroupId = 15556151;
         if (isPin) {
            verbGroupId = 13685231;
         }

         selectionContextObject.put(6609423255094033855L, new Integer(verbGroupId));
         selectionContext.setContext(selectionContextObject);
         selectionContext.setUseEntryPrefixes(useEntryPrefixes);
         selectionContext.setPreferredDefaultIndex(isPin ? 1 : 0);
         if (contextObject.getFlag(31)) {
            AddressBookServices.setLastSelectedAddress(contextObject.get(250));
         }

         newModel = (PersistableRIMModel)verbs[0].invoke(selectionContext);
         if (newModel != null) {
            if (ObjectGroup.isInGroup(newModel)) {
               ContextObject cloneContext = new ContextObject();
               cloneContext.put(254, newModel);
               long addressObjectType = -2985347935260258684L;
               if (groupAddressRecognizer != null && groupAddressRecognizer.recognize(newModel)) {
                  addressObjectType = -1326186686655625745L;
               } else if (allowCrossServiceAddressSelection) {
                  if (pinRecognizer.recognize(newModel)) {
                     addressObjectType = 4246852237058296601L;
                  }
               } else if (isPin) {
                  addressObjectType = 4246852237058296601L;
               }

               if (implusComposeModels != null) {
                  for (int i = implusComposeModels.length - 1; i >= 0; i--) {
                     if (implusComposeModels[i].getRecognizer().recognize(newModel)) {
                        addressObjectType = implusComposeModels[i].getObjectType();
                        break;
                     }
                  }
               }

               Factory factory = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(addressObjectType);
               newModel = (PersistableRIMModel)factory.createInstance(cloneContext);
            }

            EmailEditorScreen emailEditorScreen = null;

            for (Screen activeScreen = UiApplication.getUiApplication().getActiveScreen(); activeScreen != null; activeScreen = activeScreen.getScreenBelow()) {
               if (activeScreen instanceof EmailEditorScreen) {
                  emailEditorScreen = (EmailEditorScreen)activeScreen;
                  emailEditorScreen.handleRecipientRemoved(this._emailHeaderModel);
                  break;
               }
            }

            this._emailHeaderModel.setInsideModel(newModel, selectionContext.getSelectedSource());
            if (emailEditorScreen != null) {
               emailEditorScreen.handleRecipientAdded(this._emailHeaderModel);
            }
         }
      }

      return newModel;
   }
}
