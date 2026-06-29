package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.api.util.Comparator;

final class GranularPolicyElement$GranularPolicyComparator implements Comparator {
   private GranularPolicyElement$GranularPolicyComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      if (o1 instanceof GranularPolicyOrderProvider) {
         GranularPolicyOrderProvider orderProvider1 = (GranularPolicyOrderProvider)o1;
         if (o2 instanceof GranularPolicyOrderProvider) {
            GranularPolicyOrderProvider orderProvider2 = (GranularPolicyOrderProvider)o2;
            return orderProvider1.getOrder() - orderProvider2.getOrder();
         }
      }

      return 0;
   }

   GranularPolicyElement$GranularPolicyComparator(GranularPolicyElement$1 x0) {
      this();
   }
}
