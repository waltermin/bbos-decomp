package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;

public final class FilteredByCategoriesTitleField extends VerticalFieldManager {
   private Field _titleField;
   private LabelField _filterIndicatorField;
   private boolean _indicatorFieldAdded;
   private Tag TAG = Tag.create("category-filter");

   public FilteredByCategoriesTitleField(Field titleField) {
      super(1152921504606846976L);
      this._titleField = titleField;
      this._filterIndicatorField = new LabelField(null, 1152921504606847044L);
      this._filterIndicatorField.setTag(this.TAG);
      this.add(this._titleField);
   }

   public final void updateCategories(int[] categoryIds) {
      String categoryNames = null;
      if (categoryIds != null && categoryIds.length != 0) {
         CategoryList categoryList = CategoryList.getInstance();
         categoryNames = categoryList.getCategoryNames(categoryIds);
      }

      if (categoryNames != null) {
         if (!this._indicatorFieldAdded) {
            this.add(new SeparatorField());
            this.add(this._filterIndicatorField);
            this._indicatorFieldAdded = true;
         }
      } else if (this._indicatorFieldAdded) {
         this.deleteRange(1, 2);
         this._indicatorFieldAdded = false;
      }

      this._filterIndicatorField.setText(categoryNames);
   }
}
