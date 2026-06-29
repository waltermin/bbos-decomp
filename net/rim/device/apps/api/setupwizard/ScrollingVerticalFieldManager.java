package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.ui.container.VerticalFieldManager;

public class ScrollingVerticalFieldManager extends VerticalFieldManager {
   public ScrollingVerticalFieldManager(long style) {
      super(281474976710656L | style);
      this.add(new ScrollingNullField());
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      int newAmount = 0;
      if (amount != 0) {
         int currentScroll = this.getVerticalScroll() + this.getFont().getHeight() * amount;
         if (amount < 0 && currentScroll < 0) {
            currentScroll = 0;
            newAmount = -1;
         } else if (amount > 0 && currentScroll > this.getVirtualHeight() - this.getHeight()) {
            currentScroll = this.getVirtualHeight() - this.getHeight();
            newAmount = 1;
         }

         this.setVerticalScroll(currentScroll);
      }

      return newAmount;
   }
}
