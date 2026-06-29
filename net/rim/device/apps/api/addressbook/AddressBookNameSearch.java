package net.rim.device.apps.api.addressbook;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.vm.Array;

public class AddressBookNameSearch implements VerbProvider, SearchCriterion, PersistableRIMModel {
   public Object establishValue(String value) {
      Object result = null;
      String name_value = value;
      if (name_value != null && (!(name_value instanceof Object) || name_value.length() != 0)) {
         Object[] cards = AddressBookServices.lookup(name_value, 5);
         if (cards != null && cards.length == 0) {
            cards = null;
         }

         long[] luids = null;
         if (cards != null) {
            luids = new long[cards.length];

            for (int i = cards.length - 1; i >= 0; i--) {
               Object card = cards[i];
               if (card instanceof Object) {
                  UniqueIDProvider uidProvider = (UniqueIDProvider)card;
                  luids[i] = uidProvider.getLUID(null);
               }
            }
         }

         String[] words = StringUtilities.stringToWords(name_value);
         Object[] values = new Object[]{cards, luids, words};
         result = values;
      }

      return result;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      } else {
         Object whichField = ContextObject.get(context, 9045827404276417370L);
         if (!(whichField instanceof Object)) {
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

   @Override
   public Object getValue() {
      throw null;
   }

   @Override
   public int getType() {
      throw null;
   }
}
