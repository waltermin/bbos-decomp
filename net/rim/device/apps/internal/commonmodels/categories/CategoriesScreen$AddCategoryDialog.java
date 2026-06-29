package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.internal.ui.component.PopupDialog;

final class CategoriesScreen$AddCategoryDialog extends PopupDialog {
   private CategoryEditField _categoryEditField;

   CategoriesScreen$AddCategoryDialog(String prompt) {
      super((Manager)(new Object(1153202979583557632L)), 0);
      this.add((Field)(new Object(prompt, 36028797018963968L)));
      this._categoryEditField = new CategoryEditField(null);
      this.add(this._categoryEditField);
   }

   final String getCategoryName() {
      return this._categoryEditField.getText().trim();
   }

   private final boolean cancel() {
      this._categoryEditField.setText("");
      this.close(-1);
      return true;
   }

   private final boolean accept() {
      this.close(0);
      return true;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return super.trackwheelClick(status, time) ? true : this.accept();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            return this.accept();
         case '\u001b':
            return this.cancel();
         default:
            return super.keyChar(key, status, time);
      }
   }
}
