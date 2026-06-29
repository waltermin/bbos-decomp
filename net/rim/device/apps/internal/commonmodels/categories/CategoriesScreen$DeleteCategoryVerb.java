package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class CategoriesScreen$DeleteCategoryVerb extends Verb {
   private int _id;

   CategoriesScreen$DeleteCategoryVerb(int id) {
      super(627472);
      this._id = id;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(1000);
   }

   @Override
   public final Object invoke(Object parameter) {
      CategoryList categoryList = CategoryList.getInstance();
      int result = Dialog.ask(2, CommonResources.getString(9109), -1);
      if (result == 3) {
         categoryList.removeCategory(this._id);
      }

      return null;
   }
}
