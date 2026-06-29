package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.Keypad;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.internal.phone.api.verbs.SpeedDialVerb;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;

final class SpeedDialVerbFactory implements VerbFactory {
   @Override
   public final Verb[] getVerbs(Object context) {
      if (ContextObject.getFlag(context, 11)) {
         Object focusedModel = ContextObject.get(context, 250);
         if (focusedModel instanceof PhoneNumberModel) {
            PhoneNumberModel phoneNumber = (PhoneNumberModel)focusedModel;
            Object addressCard = ContextObject.get(context, 252);
            char keyChar = QuickContactList.getInstance().getQuickContactKey(phoneNumber);
            if (keyChar == 0) {
               CallerIDInfo cidi = new CallerIDInfo(phoneNumber, (PersistableRIMModel)addressCard, false, false);
               int keycode = 0;
               Integer keycodeInt = (Integer)ContextObject.get(context, 8193350148640761582L);
               if (keycodeInt != null) {
                  keycode = keycodeInt;
                  keyChar = Keypad.map(keycode);
               }

               return new Verb[]{new SpeedDialVerb(cidi, 6072, 16777232, keyChar, keycode)};
            }

            return new Verb[]{new SpeedDialVerb(null, 6073, 16777248, keyChar)};
         }
      }

      return null;
   }
}
