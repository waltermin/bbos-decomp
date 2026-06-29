package net.rim.tid.im.ui;

import net.rim.device.api.ui.XYRect;

class EditModeLookup$EditModeLookupManager extends LookupManager {
   @Override
   public int actionPerformed(Object src, int action, Object parameter) {
      int index = super._currentVariant.getCurrentVariantIndex();
      if (action == 31 && !super._isPositionAboveComposedText || action == 32 && super._isPositionAboveComposedText) {
         if (index == -1) {
            super._currentVariant.setVariantIndex(0);
            return 0;
         }

         if (index < super._currentVariant.getVariantsCount() - 1) {
            super._currentVariant.nextVariant();
            return 0;
         }
      } else {
         if (index == 0) {
            super._currentVariant.setVariantIndex(-1);
            this.currentIndexChanged(-1);
            return 0;
         }

         if (index != -1) {
            super._currentVariant.previousVariant();
         }
      }

      return 0;
   }

   @Override
   public XYRect getBounds() {
      XYRect rect = super.getBounds();
      if (super._isPositionAboveComposedText) {
         rect.y -= 4;
      }

      return rect;
   }
}
