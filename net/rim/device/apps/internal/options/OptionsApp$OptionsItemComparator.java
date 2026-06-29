package net.rim.device.apps.internal.options;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.KeyUtilities;
import net.rim.device.apps.internal.options.items.SecurityOptionsItem;
import net.rim.device.internal.i18n.CollatorImpl;

final class OptionsApp$OptionsItemComparator implements Comparator {
   CollatorImpl _collator = new CollatorImpl();

   public OptionsApp$OptionsItemComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      if (o1 instanceof SecurityOptionsItem) {
         return -1;
      }

      if (o2 instanceof SecurityOptionsItem) {
         return 1;
      }

      if (o1 instanceof KeyProvider) {
         KeyProvider kp1 = (KeyProvider)o1;
         if (o2 instanceof KeyProvider) {
            KeyProvider kp2 = (KeyProvider)o2;
            String s1 = KeyUtilities.getStringKey(kp1);
            String s2 = KeyUtilities.getStringKey(kp2);
            return this._collator.compare(s1, s2);
         }
      }

      return StringUtilities.compareObjectToStringIgnoreCase(o1, o2, 1701707776);
   }
}
