package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ObjectChoiceField;

final class SecurityOptionsItem$SecurityServiceColourChoiceField extends ObjectChoiceField {
   SecurityOptionsItem$SecurityServiceColourChoiceField(String label) {
      super(label, null, 0, 268435456);
   }

   @Override
   protected final void drawChoice(int index, Graphics graphics, int x, int y, int flags, int width) {
      int colour = ((SecurityOptionsItem$SecurityServiceColour)this.getChoice(index)).getColour();
      if (colour >= 0) {
         int oldColour = graphics.getColor();
         graphics.setColor(colour);
         int heightOfChoices = this.getHeightOfChoices();
         graphics.fillRect(x + width - heightOfChoices + 2, y + 1, heightOfChoices - 2, heightOfChoices - 2);
         graphics.setColor(oldColour);
         graphics.drawRect(x + width - heightOfChoices + 2, y + 1, heightOfChoices - 2, heightOfChoices - 2);
         width -= heightOfChoices;
      }

      super.drawChoice(index, graphics, x, y, flags, width);
   }

   @Override
   protected final int getWidthOfChoice(int index) {
      int width = super.getWidthOfChoice(index);
      if (((SecurityOptionsItem$SecurityServiceColour)this.getChoice(index)).getColour() >= 0) {
         width += this.getHeightOfChoices();
      }

      return width;
   }
}
