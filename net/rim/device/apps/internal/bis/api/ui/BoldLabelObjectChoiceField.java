package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ObjectChoiceField;

public final class BoldLabelObjectChoiceField extends ObjectChoiceField {
   public BoldLabelObjectChoiceField() {
   }

   public BoldLabelObjectChoiceField(String label, Object[] choices) {
   }

   @Override
   protected final void applyFont() {
      super.applyFont();
      Font currentFont = this.getFont();
      Font newFont = currentFont.derive(currentFont.getStyle() | 1);
      this.setFont(newFont);
   }
}
