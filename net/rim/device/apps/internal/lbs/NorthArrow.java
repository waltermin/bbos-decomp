package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class NorthArrow {
   NorthArrowRed _redArrow = new NorthArrowRed();
   int _xPosition = Display.getWidth() - 30;
   int _yPosition = Display.getHeight() - 40;

   final void paint(Graphics graphics, int rotation) {
      rotation = 360 - rotation;
      this._redArrow.paint(graphics, this._xPosition, this._yPosition, rotation, 16711680);
      if (rotation > 0) {
         rotation = 360 - rotation;
      }

      int cos = Utilities.cos(rotation);
      int sin = Utilities.sin(rotation);
      int tx = this._redArrow._tx[0];
      int ty = this._redArrow._ty[1];
      int x = (cos * tx - sin * ty + 32768 >> 16) + this._xPosition;
      int y = (sin * tx + cos * ty + 32768 >> 16) + this._yPosition;
      int r = 8;
      graphics.setColor(0);
      graphics.setGlobalAlpha(64);
      graphics.fillArc(x - r - 1, y - r - 1, 2 * r, 2 * r, 0, 360);
      graphics.setColor(16777215);
      graphics.setGlobalAlpha(255);
      graphics.fillArc(x - r + 1, y - r + 1, 2 * r, 2 * r, 0, 360);
      graphics.setColor(0);
      graphics.setFont(MapField._labelFont);
      char north = LBSResources.getString(186).charAt(0);
      int dx = MapField._labelFont.getAdvance(north) / 2;
      int dy = MapField._labelFont.getHeight() / 2 - 2;
      graphics.drawText(LBSResources.getString(186), x - dx, y - dy);
   }

   final void setFieldSize(int w, int h) {
      this._xPosition = w - 30;
      this._yPosition = h - 40;
   }
}
