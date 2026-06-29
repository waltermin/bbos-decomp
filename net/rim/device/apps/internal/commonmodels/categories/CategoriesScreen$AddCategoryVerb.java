package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class CategoriesScreen$AddCategoryVerb extends Verb {
   CategoriesScreen$AddCategoryVerb() {
      super(627456);
   }

   @Override
   public final String toString() {
      return CommonResources.getString(9106);
   }

   @Override
   public final Object invoke(Object parameter) {
      CategoriesScreen$AddCategoryDialog dialog = new CategoriesScreen$AddCategoryDialog(CommonResources.getString(9111));
      dialog.show();
      String categoryName = dialog.getCategoryName();
      if (categoryName != null && categoryName.length() > 0) {
         CategoryList categoryList = CategoryList.getInstance();
         if (categoryList.getCategoryId(categoryName) != -1) {
            Status.show(CommonResources.getString(9112));
            return null;
         }

         categoryList.addCategoryIfNecessary(categoryName);
      }

      return null;
   }
}
