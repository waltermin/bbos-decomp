package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.ui.AppsMainScreen;

final class InfoMainScreen extends AppsMainScreen {
   private int _scrollQuanta;

   InfoMainScreen(Font font, long style) {
      super(style);
      this._scrollQuanta = font.getHeight();
      this.add((Field)(new Object(18014398509481984L)));
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      Manager manager = this.getMainManager();
      int maxScroll = manager.getVirtualHeight() - manager.getHeight();
      if (maxScroll < 0) {
         return false;
      }

      int position = MathUtilities.clamp(0, manager.getVerticalScroll() + amount * this._scrollQuanta, maxScroll);
      manager.setVerticalScroll(position);
      return true;
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      return this.trackwheelRoll(dy, status, time);
   }
}
