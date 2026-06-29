package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.internal.i18n.CollatorImpl;

public final class AddressCardComparator {
   private static AddressCardKeys _card1Keys = new AddressCardKeys();
   private static AddressCardKeys _card2Keys = new AddressCardKeys();
   private static CollatorImpl _collator = (CollatorImpl)(new Object());

   private AddressCardComparator() {
   }

   private static final void getKeys(AddressCardModel card, AddressCardKeys keys, long key) {
      PersonNameModel pnm = card.getName();
      CompanyInfoModel cim = card.getCompanyInfo();
      int keyCount;
      if (key != 1232448844688687736L && key != 4922084531409364683L) {
         if (key != -227891759293611117L && key != 8199160529614935340L) {
            if (key == -4388042602796535003L) {
               keys._key1 = cim != null ? cim.getCompanyName() : null;
               if (pnm != null) {
                  keys._key2 = pnm.getLastName();
                  keys._key3 = pnm.getFirstName();
               }

               keyCount = pnm == null ? 1 : 3;
            } else {
               keyCount = 0;
            }
         } else if (pnm != null) {
            keys._key1 = pnm.getLastName();
            keys._key2 = pnm.getFirstName();
            keyCount = 2;
         } else {
            keys._key1 = cim != null ? cim.getCompanyName() : null;
            keyCount = 1;
         }
      } else if (pnm != null) {
         keys._key1 = pnm.getFirstName();
         keys._key2 = pnm.getLastName();
         keyCount = 2;
      } else {
         keys._key1 = cim != null ? cim.getCompanyName() : null;
         keyCount = 1;
      }

      switch (keyCount) {
         case 3:
         default:
            if (keys._key3 == null) {
               keyCount--;
            }
         case 2:
            if (keys._key2 == null) {
               keys._key2 = keys._key3;
               keyCount--;
            }
         case 1:
            if (keys._key1 == null) {
               keys._key1 = keys._key2;
               keys._key2 = keys._key3;
               keyCount--;
            }
         case 0:
            keys._count = keyCount;
      }
   }

   public static final int compare(Object card1, Object card2, long key) {
      getKeys((AddressCardModel)card1, _card1Keys, key);
      getKeys((AddressCardModel)card2, _card2Keys, key);
      int minKeyCount = _card1Keys._count < _card2Keys._count ? _card1Keys._count : _card2Keys._count;
      int result = 0;
      if (minKeyCount > 0) {
         result = _collator.compare(_card1Keys._key1, _card2Keys._key1);
         if (result == 0 && minKeyCount > 1) {
            result = _collator.compare(_card1Keys._key2, _card2Keys._key2);
            if (result == 0 && minKeyCount > 2) {
               result = _collator.compare(_card1Keys._key3, _card2Keys._key3);
            }
         }
      }

      if (result == 0) {
         result = _card1Keys._count - _card2Keys._count;
      }

      _card1Keys._key1 = null;
      _card1Keys._key2 = null;
      _card1Keys._key3 = null;
      _card2Keys._key1 = null;
      _card2Keys._key2 = null;
      _card2Keys._key3 = null;
      return result;
   }
}
