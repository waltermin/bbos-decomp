package javax.microedition.lcdui;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.DialogFieldManager;

class MIDPAlert extends MIDPScreen {
   private AlertType _alertType;
   private static final int SCALE_FACTOR = 9;
   private static final int INSIDE_SPACE = 2;

   MIDPAlert(Manager delegate, AlertType at) {
      super(delegate);
      this._alertType = at;
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         if (this._alertType != null) {
            this._alertType.playSound();
         }

         Manager cfm = ((DialogFieldManager)this.getDelegate()).getCustomManager();
         if (cfm.getVirtualHeight() <= cfm.getHeight()) {
         }
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      ((Alert)this.getDisplayable()).dismiss();
      return true;
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      return this.trackwheelClick(status, time);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean handled = false;
      if (key == '\n' || key == 27 || key == ' ' || key == '\b') {
         ((Alert)this.getDisplayable()).dismiss();
         handled = true;
      }

      if (!handled) {
         handled = super.keyChar(key, status, time);
      }

      return handled;
   }

   @Override
   protected void sublayout(int width, int height) {
      int realHeight = height;
      Manager dfm = this.getDelegate();
      if (super._ticker != null) {
         super._ticker.setStuff(this.getFont());
         height -= super._ticker.getHeight();
      }

      int subwidth = width / 10 * 9 - 4;
      int subheight = height / 10 * 9 - 4;
      this.layoutDelegate(subwidth, subheight);
      int newx = width - dfm.getWidth() >> 1;
      int newy = height - dfm.getHeight() >> 1;
      this.setPositionDelegate(newx, newy);
      this.setPosition(0, 0);
      this.setExtent(width, realHeight);
   }

   @Override
   protected void paint(net.rim.device.api.ui.Graphics graphics) {
      XYRect inside = this.getDelegate().getExtent();
      graphics.drawRect(inside.x - 2, inside.y - 2, inside.width + 4, inside.height + 4);
      super.paint(graphics);
   }
}
