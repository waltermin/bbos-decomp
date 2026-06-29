package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.verb.WrapperVerb;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;

class DialVerbTypeComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      int t1 = this.getDialVerbType(o1);
      int t2 = this.getDialVerbType(o2);
      if (t1 < t2) {
         return -1;
      } else {
         return t1 > t2 ? 1 : 0;
      }
   }

   private int getDialVerbType(Object obj) {
      while (obj instanceof WrapperVerb) {
         obj = ((WrapperVerb)obj).getInnerVerb();
      }

      if (obj instanceof DialVerb) {
         DialVerb dialVerb = (DialVerb)obj;
         obj = dialVerb.getPhoneNumber();
         if (obj instanceof AbstractPhoneNumberModel) {
            AbstractPhoneNumberModel pnm = (AbstractPhoneNumberModel)obj;
            return pnm.getType();
         }
      }

      return 0;
   }
}
