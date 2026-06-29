package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.text.TextRect;
import net.rim.device.internal.ui.SystemIcon;

class MultiLineRadioButtonField extends RadioButtonField {
   public MultiLineRadioButtonField() {
      this(null, null, false, 0);
   }

   public MultiLineRadioButtonField(String label) {
      this(label, null, false, 0);
   }

   public MultiLineRadioButtonField(String label, RadioButtonGroup group, boolean selected) {
      this(label, group, selected, 0);
   }

   public MultiLineRadioButtonField(String label, RadioButtonGroup group, boolean selected, long style) {
      super(label, group, selected, style);
   }

   @Override
   protected void layout(int width, int height) {
      super.layout(width, height);
      String text = this.getLabel();
      if ((this.getStyle() & 2147483648L) != 0 && text != null && text.indexOf(10) >= 0) {
         TextRect textRect = new TextRect(this);
         textRect.setText(text);
         textRect.layout(width, height);
         Font font = this.getFont();
         int fontHeight = font.getHeight();
         int iconWidth = SystemIcon.COLLECTION.getWidth(fontHeight, fontHeight);
         int x_pos = iconWidth;
         width = x_pos + 2;
         width += textRect.getWidth();
         height = Math.max(textRect.getHeight(), fontHeight);
         this.setExtent(width, height);
      }
   }
}
