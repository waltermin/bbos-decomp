package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.util.Comparator;

public final class EmailFilterComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      if (o1 instanceof EmailFilterModelImpl && o2 instanceof EmailFilterModelImpl) {
         EmailFilterModelImpl m1 = (EmailFilterModelImpl)o1;
         EmailFilterModelImpl m2 = (EmailFilterModelImpl)o2;
         int order1 = m1.getOrder();
         int order2 = m2.getOrder();
         if (order1 < order2) {
            return -1;
         } else {
            return order1 == order2 ? 0 : 1;
         }
      } else {
         throw new Object();
      }
   }
}
