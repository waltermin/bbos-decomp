package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.quickcontact.QuickContactUtil;

final class SpeedDialKeyField extends Field {
   private char _key;

   SpeedDialKeyField(char key) {
      this._key = key;
   }

   @Override
   protected final void paint(Graphics g) {
      int height = g.getFont().getHeight();
      int width = height;
      int x = Display.getWidth() - width;
      ContextObject context = new ContextObject();
      context.put(9045827404276417370L, this);
      QuickContactUtil.paintHotkey(this._key, g, x, 0, width, height, 5, context);
   }

   @Override
   public final int getPreferredWidth() {
      return this.getFont().getHeight();
   }

   @Override
   public final int getPreferredHeight() {
      return this.getFont().getHeight();
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(this.getPreferredWidth(), this.getPreferredHeight());
   }
}
