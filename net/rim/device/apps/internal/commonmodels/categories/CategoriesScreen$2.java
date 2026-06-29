package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.ui.component.CheckboxField;

class CategoriesScreen$2 implements Runnable {
   private final CheckboxField val$finalCheckboxToRemove;
   private final CategoriesScreen this$0;

   CategoriesScreen$2(CategoriesScreen _1, CheckboxField _2) {
      this.this$0 = _1;
      this.val$finalCheckboxToRemove = _2;
   }

   @Override
   public void run() {
      CategoriesScreen.access$200(this.this$0).delete(this.val$finalCheckboxToRemove);
      if (this.this$0._checkboxes.length == 0) {
         CategoriesScreen.access$400(this.this$0).add(new CategoriesScreen$NoCategoriesLabelField());
      }
   }
}
