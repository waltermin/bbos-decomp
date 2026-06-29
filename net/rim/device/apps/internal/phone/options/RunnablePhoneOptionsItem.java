package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.framework.model.PaintProvider;

final class RunnablePhoneOptionsItem implements PhoneOptionsItem, PaintProvider {
   private Runnable _runnable;

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      return g.drawText(this._runnable.toString(), x, y, 64, width);
   }

   public final boolean isAvailable(boolean notifyUser) {
      return true;
   }

   @Override
   public final void onOpen() {
      this._runnable.run();
   }

   RunnablePhoneOptionsItem(Runnable runnable) {
      this._runnable = runnable;
   }
}
