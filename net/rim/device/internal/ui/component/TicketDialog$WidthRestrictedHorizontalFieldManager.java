package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.container.HorizontalFieldManager;

class TicketDialog$WidthRestrictedHorizontalFieldManager extends HorizontalFieldManager {
   private int _restrictedWidth;

   public TicketDialog$WidthRestrictedHorizontalFieldManager(int restrictedWidth) {
      this._restrictedWidth = restrictedWidth;
   }

   @Override
   protected void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(maxWidth - this._restrictedWidth, maxHeight);
   }
}
