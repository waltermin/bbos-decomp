package net.rim.device.apps.internal.calendar.eventprovider;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ObjectChoiceField;

class EventViewer$CalendarChoiceField extends ObjectChoiceField {
   public EventViewer$CalendarChoiceField(String label, Object[] choices, int initialIndex) {
      super(label, choices, initialIndex, 0);
   }

   @Override
   protected void drawChoice(int index, Graphics graphics, int x, int y, int flags, int width) {
      int heightOfChoices = this.getHeightOfChoices();
      Object choice = this.getChoice(index);
      if (choice instanceof CalendarChoice) {
         int color = ((CalendarChoice)choice).getColor();
         if (color >= 0) {
            int oldColor = graphics.getColor();
            graphics.setColor(color);
            graphics.fillRect(x + 1, y + 1, heightOfChoices - 2, heightOfChoices - 2);
            graphics.setColor(oldColor);
            graphics.drawRect(x + 1, y + 1, heightOfChoices - 2, heightOfChoices - 2);
            width -= heightOfChoices;
            x += heightOfChoices;
         }
      }

      graphics.drawText(choice.toString(), x, y, flags | 64, width);
   }

   @Override
   protected int getWidthOfChoice(int index) {
      int width = super.getWidthOfChoice(index);
      Object choice = this.getChoice(index);
      if (choice instanceof CalendarChoice && ((CalendarChoice)choice).getColor() >= 0) {
         width += this.getHeightOfChoices();
      }

      return width;
   }

   @Override
   protected int getHeightOfChoices() {
      return this.getFont().getHeight();
   }
}
