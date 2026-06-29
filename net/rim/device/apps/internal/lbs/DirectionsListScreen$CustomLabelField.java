package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

public final class DirectionsListScreen$CustomLabelField extends LabelField {
   DirectionsListScreen$CustomLabelField(Object text, long style) {
      super(text, style);
   }

   @Override
   protected final void paint(Graphics graphics) {
      int height = this.getHeight();
      int width = Display.getWidth();
      int oldColour = graphics.getColor();
      graphics.setColor(16777164);
      graphics.fillRect(0, 0, width, height);
      graphics.setColor(oldColour);
      super.paint(graphics);
   }
}
