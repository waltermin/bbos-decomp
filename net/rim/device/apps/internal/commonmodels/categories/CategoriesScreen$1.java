package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.ui.component.CheckboxField;

class CategoriesScreen$1 implements Runnable {
   private final boolean val$wasEmpty;
   private final CheckboxField val$checkbox;
   private final int val$categoryIndex;
   private final CategoriesScreen this$0;

   CategoriesScreen$1(CategoriesScreen _1, boolean _2, CheckboxField _3, int _4) {
      this.this$0 = _1;
      this.val$wasEmpty = _2;
      this.val$checkbox = _3;
      this.val$categoryIndex = _4;
   }

   @Override
   public void run() {
      if (this.val$wasEmpty) {
         CategoriesScreen.access$000(this.this$0).deleteRange(0, 1);
      }

      CategoriesScreen.access$100(this.this$0).insert(this.val$checkbox, this.val$categoryIndex);
      this.val$checkbox.setFocus();
   }
}
