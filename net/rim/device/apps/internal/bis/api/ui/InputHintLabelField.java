package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.LabelField;

public final class InputHintLabelField extends LabelField {
   private static final int FONT_SIZE_PT;

   public InputHintLabelField() {
   }

   public InputHintLabelField(Object text) {
   }

   @Override
   protected final void applyFont() {
      super.applyFont();
      Font currentFont = this.getFont();
      Font newFont = currentFont.derive(currentFont.getStyle(), 5, 3);
      this.setFont(newFont);
   }
}
