package net.rim.device.apps.internal.standardcalculator;

import net.rim.device.api.ui.component.LabelField;

final class CalculatorUI$MemoryLabelField extends LabelField {
   CalculatorUI$MemoryLabelField(long style) {
      super("", style);
   }

   public final void setText(String text) {
      if (text.length() == 0) {
         super.setText(text);
      } else {
         super.setText("M=" + text);
      }
   }
}
