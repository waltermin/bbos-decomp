package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;

class DisplayCategoriesVerb extends Verb {
   DisplayCategoriesVerb(ResourceBundleFamily rb, int rbKey) {
      super(479376, rb, rbKey);
   }

   boolean displayCategories(int[] selectedCategoryIds, byte mode) {
      CategoriesScreen categoriesScreen = new CategoriesScreen(selectedCategoryIds, mode);
      categoriesScreen.perform(6099736323056465049L, null);
      if (categoriesScreen.changesSaved()) {
         categoriesScreen.getSelectedCategoryIds(selectedCategoryIds);
         return true;
      } else {
         return false;
      }
   }
}
