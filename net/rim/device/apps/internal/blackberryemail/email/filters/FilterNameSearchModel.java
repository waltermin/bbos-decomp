package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.addressbook.AddressBookNameSearch;
import net.rim.device.apps.api.addressbook.SelectNameVerb;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.vm.Array;

public final class FilterNameSearchModel extends AddressBookNameSearch implements PersistableRIMModel {
   Object _value;
   String _nameValue;

   @Override
   public final int getType() {
      return 5;
   }

   @Override
   public final Object getValue() {
      return this._value;
   }

   protected final boolean setValue(String name) {
      if (name != null && name.length() != 0) {
         this._nameValue = name;
         return true;
      } else {
         this._nameValue = null;
         return false;
      }
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      } else {
         Object whichField = ContextObject.get(context, 9045827404276417370L);
         if (!(whichField instanceof EditField)) {
            Array.resize(verbs, 0);
            return null;
         } else {
            EditField nameField = (EditField)whichField;
            Array.resize(verbs, 1);
            verbs[0] = new SelectNameVerb(nameField);
            return nameField.isMuddy() && nameField.getTextLength() != 0 ? null : verbs[0];
         }
      }
   }
}
