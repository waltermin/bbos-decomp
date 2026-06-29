package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.api.util.Comparator;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

class AddressGuesser$2 implements Comparator {
   private final AddressGuesser this$0;

   AddressGuesser$2(AddressGuesser _1) {
      this.this$0 = _1;
   }

   @Override
   public int compare(Object o1, Object o2) {
      EmailMessageModel emm1 = (EmailMessageModel)o1;
      EmailMessageModel emm2 = (EmailMessageModel)o2;
      if (emm1.getTimestamp() < emm2.getTimestamp()) {
         return 1;
      } else {
         return emm1.getTimestamp() > emm2.getTimestamp() ? -1 : 0;
      }
   }

   @Override
   public boolean equals(Object comp2) {
      return this == comp2;
   }
}
