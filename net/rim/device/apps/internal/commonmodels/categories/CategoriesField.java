package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.api.framework.verb.Verb;

final class CategoriesField extends HorizontalFieldManager {
   private LabelField _namesField;
   private Verb _displayCategoriesVerb;
   private boolean _visible = true;
   private static final String EMPTY_CATEGORIES;

   public CategoriesField(String label, String initialCategoryNames) {
      if (label != null) {
         this.add((Field)(new Object(label)));
      }

      this._namesField = (LabelField)(new Object(null, 18014398509482048L));
      this.setCategoryNames(initialCategoryNames);
      this.add(this._namesField);
      this._displayCategoriesVerb = new DisplayCategoriesForFieldVerb(this);
   }

   public final void setCategoryNames(String categoryNames) {
      if (categoryNames == null || categoryNames.length() == 0) {
         categoryNames = " ";
      }

      this._namesField.setText(categoryNames);
   }

   public final String getCategoryNames() {
      return this._namesField.getText().trim();
   }

   public final void setVisible(boolean visible) {
      if (this._visible != visible) {
         this._visible = visible;
         this.updateLayout();
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == ' ' && this.isEditable()) {
         this._displayCategoriesVerb.invoke(null);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final void sublayout(int maxWidth, int maxHeight) {
      if (!this._visible) {
         maxHeight = 0;
         maxWidth = 0;
      }

      super.sublayout(maxWidth, maxHeight);
   }

   @Override
   public final boolean isFocusable() {
      return !this._visible ? false : super.isFocusable();
   }
}
