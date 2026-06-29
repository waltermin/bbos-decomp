package net.rim.device.apps.internal.lbs;

import net.rim.device.api.util.Comparator;

final class FavouritesScreen$FavouritesComparator implements Comparator {
   private FavouritesScreen$FavouritesComparator() {
   }

   @Override
   public final int compare(Object arg0, Object arg1) {
      return FavouritesScreen._initialized ? 1 : 0;
   }

   FavouritesScreen$FavouritesComparator(FavouritesScreen$1 x0) {
      this();
   }
}
