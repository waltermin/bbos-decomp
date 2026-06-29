package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.editor.UseOnceEditorScreen;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.mms.resources.MMSResources;

final class MMSUseOnceVerb extends Verb {
   private Verb _composeVerb;

   public MMSUseOnceVerb() {
      this(null);
   }

   public MMSUseOnceVerb(Verb composeVerb) {
      super(328000);
      this._composeVerb = composeVerb;
   }

   @Override
   public final Object invoke(Object param) {
      String phoneNumber = (String)ContextObject.get(param, 247);
      ContextObject context = (ContextObject)(new Object(74));
      Object result;
      if (phoneNumber != null) {
         ContextObject contextObject = ContextObject.clone(param);
         contextObject.put(253, phoneNumber);
         Factory factory = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(3797587162219887872L);
         result = factory.createInstance(contextObject);
      } else {
         String initialValue = (String)ContextObject.get(param, 253);
         String[] choices = new Object[]{MMSResources.getString(69), MMSResources.getString(68)};
         int choice = Dialog.ask(AddressBookResources.getString(301), choices, 0);
         if (choice == 0) {
            result = UseOnceEditorScreen.showUseOnceScreen(MMSResources.getString(13), 3797587162219887872L, initialValue, null);
         } else {
            if (choice != 1) {
               return null;
            }

            result = UseOnceEditorScreen.showUseOnceScreen(MMSResources.getString(13), -2985347935260258684L, initialValue, context);
         }
      }

      if (result != null && this._composeVerb != null) {
         ContextObject contextObject = ContextObject.clone(param);
         contextObject.remove(253);
         contextObject.put(254, result);
         result = this._composeVerb.invoke(contextObject);
      }

      return result;
   }

   @Override
   public final String toString() {
      return MMSResources.getString(12);
   }
}
