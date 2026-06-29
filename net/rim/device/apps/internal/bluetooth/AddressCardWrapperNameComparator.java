package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.addressbook.AddressCardModel;

class AddressCardWrapperNameComparator implements Comparator {
   private Comparator _comparator;

   AddressCardWrapperNameComparator(Comparator comparator) {
      this._comparator = comparator;
   }

   @Override
   public int compare(Object o1, Object o2) {
      AddressCardModel card1 = ((AddressCardWrapper)o1)._card;
      AddressCardModel card2 = ((AddressCardWrapper)o2)._card;
      return this._comparator.compare(card1, card2);
   }

   @Override
   public boolean equals(Object obj) {
      return this._comparator.equals(obj);
   }
}
