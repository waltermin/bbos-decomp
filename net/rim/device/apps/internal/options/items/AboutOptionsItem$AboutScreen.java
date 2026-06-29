package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.util.MathUtilities;

public final class AboutOptionsItem$AboutScreen extends FullScreen {
   private String _screenCopyableText;
   private final AboutOptionsItem this$0;

   public AboutOptionsItem$AboutScreen(AboutOptionsItem _1, long style) {
      super(style);
      this.this$0 = _1;
   }

   public final void setScreenCopyableText(String s) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final String getScreenCopyableText() {
      return this._screenCopyableText;
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      return dy != 0 ? this.trackwheelRoll(dy, status, time) : false;
   }

   @Override
   public final boolean trackwheelRoll(int amount, int status, int time) {
      Manager m = this.getDelegate();
      int currentScroll = m.getVerticalScroll();
      if (amount > 0) {
         currentScroll += this.this$0._fontHeight;
      } else {
         currentScroll -= this.this$0._fontHeight;
      }

      m.setVerticalScroll(MathUtilities.clamp(0, currentScroll, Math.max(0, m.getVirtualHeight() - m.getHeight())));
      return true;
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               this.this$0.nextScreen();
               return true;
         }
      }

      return handled;
   }

   @Override
   public final Menu getMenu(int instance) {
      Menu m = super.getMenu(instance);
      m.add(this.this$0._copyContentsMenuItem);
      m.add((MenuItem)this.this$0._nextScreenMenuItem);
      return m;
   }
}
