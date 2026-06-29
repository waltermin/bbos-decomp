package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class AddToAddressBookVerb extends Verb {
   AddToAddressBookVerb() {
      super(610816);
   }

   @Override
   public final String toString() {
      return AddressBookResources.getString(1300);
   }

   @Override
   public final Object invoke(Object parameter) {
      AddressCardModel newEntry = new AddressCardModelImpl(null);
      StoreAction storeAction = new StoreAction();
      boolean dirty = false;
      if (parameter != null) {
         if (parameter instanceof CompressedAddressCardModel) {
            newEntry = ((AddressCardModelFactory)ApplicationRegistry.getApplicationRegistry().waitFor(-3124646573404667739L))
               .uncompressCard((CompressedAddressCardModel)parameter);
         } else if (!(parameter instanceof Object)) {
            Object editScreen = null;
            ContextObject context;
            if (!(parameter instanceof Object)) {
               context = (ContextObject)(new Object());
               if (parameter instanceof Object) {
                  ContextObject.put(context, 254, parameter);
                  editScreen = parameter;
               } else if (parameter instanceof Object) {
                  ContextObject.put(context, -4886909117188079897L, parameter);
               }
            } else {
               context = ContextObject.clone(parameter);
               editScreen = ContextObject.get(context, 254);
            }

            if (editScreen != null) {
               newEntry.add(editScreen);
               dirty = true;
            }

            ContextObject.setFlag(context, 10);
            RIMModel friendlyName = (RIMModel)FactoryUtil.createInstance(5149066071290992769L, context);
            if (friendlyName != null) {
               newEntry.add(friendlyName);
               dirty = true;
            }
         } else {
            newEntry = AddressCardUtilities.expandGroup((AddressCardModel)parameter);
         }
      }

      if (newEntry.getName() == null) {
         newEntry.add(new PersonNameModelImpl());
      }

      if (ContextObject.getFlag(parameter, 85)) {
         storeAction.invoke(newEntry);
      } else {
         String title = AddressBookResources.getString(1301);
         EditAddressCardScreen editScreen = new EditAddressCardScreen(storeAction, title);
         editScreen.setAddressCardModel(newEntry, null, false);
         if (dirty) {
            editScreen.setDirty(dirty);
         }

         editScreen.go();
      }

      return storeAction.getStoredObject();
   }
}
