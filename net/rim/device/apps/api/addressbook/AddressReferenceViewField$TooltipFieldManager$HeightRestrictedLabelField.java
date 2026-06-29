package net.rim.device.apps.api.addressbook;

import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;

class AddressReferenceViewField$TooltipFieldManager$HeightRestrictedLabelField extends LabelField {
   public AddressReferenceViewField$TooltipFieldManager$HeightRestrictedLabelField() {
   }

   @Override
   public int getPreferredHeight() {
      return super.getPreferredHeight() * 3;
   }

   @Override
   protected void layout(int width, int height) {
      super.layout(width, height);
      XYRect extent = this.getExtent();
      int maxHeight = this.getPreferredHeight();
      if (extent.height > maxHeight) {
         this.setExtent(extent.width, maxHeight);
      }
   }
}
