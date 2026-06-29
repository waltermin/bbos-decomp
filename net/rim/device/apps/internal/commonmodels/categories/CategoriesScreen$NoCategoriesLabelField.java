package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.ui.CommonResources;

final class CategoriesScreen$NoCategoriesLabelField extends LabelField {
   CategoriesScreen$NoCategoriesLabelField() {
      super(CommonResources.getString(9105), 18014398509481988L);
   }

   @Override
   public final boolean isSelectionCopyable() {
      return false;
   }

   @Override
   public final int getPreferredWidth() {
      return Display.getWidth();
   }
}
