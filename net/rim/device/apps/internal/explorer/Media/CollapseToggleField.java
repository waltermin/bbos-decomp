package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;

final class CollapseToggleField extends Field {
   private boolean _collapsed;
   private Manager _manager;
   private Field _field;

   public CollapseToggleField(Manager manager, Field field) {
      super(18014398509481984L);
      this._manager = manager;
      this._field = field;
   }

   @Override
   public final int getPreferredHeight() {
      return this.getFont().getHeight();
   }

   @Override
   public final int getPreferredWidth() {
      return this.getFont().getAdvance(" [+] ");
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(Math.min(width, this.getPreferredWidth()), Math.min(height, this.getPreferredHeight()));
   }

   @Override
   protected final void paint(Graphics graphics) {
      int graphicsColor = graphics.getColor();
      int graphicsAlpha = graphics.getGlobalAlpha();
      int drawColor = 14277081;
      graphics.setColor(drawColor);
      graphics.setGlobalAlpha(65);
      graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
      graphics.setColor(graphicsColor);
      graphics.setGlobalAlpha(graphicsAlpha);
      if (this._collapsed) {
         graphics.drawText(" [+] ", 0, 0);
      } else {
         graphics.drawText(" [-] ", 0, 0);
      }
   }

   public final void toggleCollapse() {
      this._collapsed = !this._collapsed;
      if (this._collapsed) {
         this._manager.delete(this._field);
      } else {
         this._manager.insert(this._field, 1);
      }

      this.invalidate();
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (c == '\n') {
         this.toggleCollapse();
         return true;
      } else {
         return super.keyChar(c, status, time);
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      if (super.navigationClick(status, time)) {
         return true;
      }

      this.toggleCollapse();
      return true;
   }
}
