package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;

final class ThemeOptionsScreen$ThemePreviewField extends Field {
   private Bitmap _defaultBitmap;
   private String _defaultText;
   private boolean _default;
   private Bitmap _previewBitmap;
   private short FONT_HEIGHT = 16;

   ThemeOptionsScreen$ThemePreviewField(String defaultText, Bitmap defaultBitmap) {
      super(12884901888L);
      this._defaultBitmap = defaultBitmap;
      this._defaultText = defaultText;
      this._default = false;
   }

   public final void displayBitmap(Bitmap previewBitmap) {
      if (previewBitmap != null) {
         this._previewBitmap = previewBitmap;
         this._default = false;
      } else {
         this._previewBitmap = this._defaultBitmap;
         this._default = true;
      }

      this.invalidate();
   }

   @Override
   protected final void layout(int x, int y) {
      this.setExtent(Display.getWidth() - 10, (int)(Display.getHeight() * 4600877379321698714L));
   }

   @Override
   protected final void paint(Graphics graphics) {
      new XYPoint();
      graphics.setGlobalAlpha(255);
      XYPoint pt = this.getCenterAnchor(this._previewBitmap.getWidth(), this._previewBitmap.getHeight());
      graphics.drawBitmap(pt.x, pt.y, this.getWidth(), this.getHeight(), this._previewBitmap, 0, 0);
      if (this._default) {
         graphics.setColor(0);
         Font textFont = Font.getDefault().derive(1, this.FONT_HEIGHT);
         pt = this.getCenterAnchor(textFont.getBounds(this._defaultText), this.FONT_HEIGHT);
         graphics.setFont(textFont);
         graphics.drawText(this._defaultText, pt.x, pt.y, 4);
      }
   }

   private final XYPoint getCenterAnchor(int objectWidth, int objectHeight) {
      XYPoint point = new XYPoint();
      point.x = (this.getWidth() >> 1) - (objectWidth >> 1);
      point.y = (this.getHeight() >> 1) - (objectHeight >> 1);
      return point;
   }
}
