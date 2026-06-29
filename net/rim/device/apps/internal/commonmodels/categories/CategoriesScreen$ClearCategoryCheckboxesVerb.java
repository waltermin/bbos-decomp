package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class CategoriesScreen$ClearCategoryCheckboxesVerb extends Verb {
   private final CategoriesScreen this$0;

   CategoriesScreen$ClearCategoryCheckboxesVerb(CategoriesScreen _1) {
      super(627488);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(9107);
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.clearCheckboxes(null);
      if (this.this$0._mode == 2) {
         this.this$0.save();
         CategoriesScreen.access$1000(this.this$0).invoke(null);
      }

      return null;
   }
}
