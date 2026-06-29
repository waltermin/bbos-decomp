package net.rim.device.apps.api.addressbook;

import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.search.SearchCriterion;

public final class AddressMatch {
   private static FriendlyNameAddressModel _matchAddressModel;

   private AddressMatch() {
   }

   public static final int match(AddressReference address, SearchCriterion criteria) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   public static final int match(AddressCardModel address, SearchCriterion criteria) {
      Object value = criteria.getValue();
      if (!(value instanceof Object[])) {
         return -1;
      }

      Object[] values = (Object[])value;
      long[] cardLUIDs = (long[])values[1];
      Object[] cards = (Object[])values[0];
      int length = cardLUIDs != null ? cardLUIDs.length : 0;
      if (length > 0 && address instanceof Object) {
         long luid = ((UniqueIDProvider)address).getLUID(null);

         for (int i = length - 1; i >= 0; i--) {
            if (luid == cardLUIDs[i]) {
               if (cards[i] != address) {
                  return verifyMatch(address, criteria);
               }

               return 1;
            }
         }
      }

      return verifyMatch(address, criteria);
   }

   private static final int verifyMatch(AddressCardModel address, SearchCriterion criteria) {
      PersonNameModel name = address.getName();
      return name instanceof Object && ((MatchProvider)name).match(criteria) == 1 ? 1 : 0;
   }
}
