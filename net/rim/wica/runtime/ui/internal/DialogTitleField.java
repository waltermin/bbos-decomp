package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;

final class DialogTitleField extends Field {
   private String _title;
   private DialogTitleField$RedrawRunnable _redrawRunnable = new DialogTitleField$RedrawRunnable(this);
   private static final int SEPARATOR_HEIGHT = 1;

   final void setTitle(String title) {
      if (title != null) {
         title = title.trim();
         if (title.length() == 0) {
            title = null;
         }
      }

      this._title = title;
      this.redraw();
   }

   private final void redraw() {
      UiApplication app = UiApplication.getUiApplication();
      if (app.isForeground()) {
         synchronized (this._redrawRunnable) {
            if (this._redrawRunnable._invokeLaterPending) {
               return;
            }
         }

         app.invokeLater(this._redrawRunnable);
      }
   }

   @Override
   public final int getPreferredHeight() {
      return this.getFont().getHeight() + 2 + 1;
   }

   @Override
   protected final void layout(int width, int height) {
      int fieldHeight = this.getPreferredHeight();
      if (height >= fieldHeight) {
         this.setExtent(width, fieldHeight);
      } else {
         this.setExtent(width, 0);
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (this._title != null) {
         graphics.drawText(this._title, 0, 1, 70);
      }

      XYRect extent = this.getExtent();
      int yPos = extent.height - 1;
      graphics.drawLine(0, yPos, extent.width, yPos);
   }

   static final void access$000(DialogTitleField x0) {
      x0.invalidate();
   }
}
