package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

public final class PasswordKeeperComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      try {
         if (o1 instanceof PasswordKeeperElement && o2 instanceof PasswordKeeperElement) {
            PasswordKeeperElement element1 = (PasswordKeeperElement)o1;
            PasswordKeeperElement element2 = (PasswordKeeperElement)o2;
            return StringUtilities.compareToIgnoreCase(element1.getTitle(), element2.getTitle());
         }
      } catch (DecryptionException var5) {
         return 0;
      } catch (PasswordKeeperLockedException var6) {
      }

      return 0;
   }

   @Override
   public final boolean equals(Object obj) {
      return false;
   }
}
