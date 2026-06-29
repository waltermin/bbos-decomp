package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.LabelField;

public final class BoldLabelField extends LabelField {
   public BoldLabelField() {
   }

   public BoldLabelField(Object text) {
   }

   @Override
   protected final void applyFont() {
      super.applyFont();
      Font currentFont = this.getFont();
      Font newFont = currentFont.derive(currentFont.getStyle() | 1);
      this.setFont(newFont);
   }
}
