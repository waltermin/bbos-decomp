package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

final class CategoryList$CategoryComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      String category1;
      if (!(o1 instanceof String)) {
         category1 = ((CategoryModel)o1).getName();
      } else {
         category1 = (String)o1;
      }

      String category2;
      if (!(o2 instanceof String)) {
         category2 = ((CategoryModel)o2).getName();
      } else {
         category2 = (String)o2;
      }

      if (category1 == null && category2 == null) {
         return 0;
      } else if (category1 == null && category2 != null) {
         return -1;
      } else {
         return category1 != null && category2 == null ? 1 : StringUtilities.compareToIgnoreCase(category1, category2, Locale.getDefaultForSystem().getCode());
      }
   }
}
