package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.apps.api.ui.CommonResources;

public final class DisplayCategoriesForFieldVerb extends DisplayCategoriesVerb {
   private Field _field;

   public DisplayCategoriesForFieldVerb(Field field) {
      super(CommonResources.getResourceBundle(), 9103);
      if (field == null) {
         throw new Object();
      }

      if (!(field instanceof CategoriesField) && !(field instanceof Object)) {
         throw new Object();
      }

      this._field = field;
   }

   @Override
   public final Object invoke(Object parameter) {
      CategoryList categoryList = CategoryList.getInstance();
      int[] selectedCategoryIds = new int[0];
      String categoryNames;
      if (!(this._field instanceof CategoriesField)) {
         categoryNames = ((BasicEditField)this._field).getText();
      } else {
         categoryNames = ((CategoriesField)this._field).getCategoryNames();
      }

      categoryList.getCategoryIds(categoryNames, selectedCategoryIds, false);
      if (this.displayCategories(selectedCategoryIds, (byte)(this._field.isEditable() ? 1 : 0))) {
         categoryNames = categoryList.getCategoryNames(selectedCategoryIds);
         if (!(this._field instanceof CategoriesField)) {
            ((BasicEditField)this._field).setText(categoryNames);
         } else {
            ((CategoriesField)this._field).setCategoryNames(categoryNames);
         }

         this._field.setDirty(true);
      }

      return null;
   }
}
