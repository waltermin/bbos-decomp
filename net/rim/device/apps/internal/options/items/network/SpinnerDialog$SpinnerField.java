package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

final class SpinnerDialog$SpinnerField extends Field {
   int _desiredRadius;
   int _curIndex = 0;
   int _xPos = 0;
   int _xDelta = 0;
   int _radius;
   int _diameter;

   public SpinnerDialog$SpinnerField(int radius) {
      this._desiredRadius = radius;
   }

   @Override
   public final int getPreferredWidth() {
      return (this._desiredRadius << 1) + 1;
   }

   @Override
   public final int getPreferredHeight() {
      return (this._desiredRadius << 1) + 1;
   }

   @Override
   protected final void layout(int width, int height) {
      this._radius = width - (1 - (width & 1)) >> 1;
      if (this._radius > this._desiredRadius) {
         this._radius = this._desiredRadius;
      }

      this._diameter = (this._radius << 1) + 1;
      this.setExtent(width, this._diameter);
      this._xPos = 0;
      this._xDelta = width > this._diameter ? 1 : 0;
   }

   public final void drawSpinner(Graphics g, int xPos) {
      g.drawArc(xPos, 0, this._diameter, this._diameter, 0, 360);
      g.drawLine(this._radius + xPos, 0, this._radius + xPos, this._diameter);
      g.drawLine(xPos, this._radius, xPos + this._diameter, this._radius);
      g.fillArc(xPos, 0, this._diameter, this._diameter, 90 * this._curIndex, 90);
      g.fillArc(xPos, 0, this._diameter, this._diameter, 90 * (this._curIndex + 2 & 3), 90);
   }

   @Override
   public final void paint(Graphics g) {
      this.drawSpinner(g, this._xPos);
      int off = this._xPos + this._diameter - this.getWidth();
      if (off > 0) {
         this.drawSpinner(g, -this._diameter + off);
      }
   }

   public final void advance(int amount) {
      this._curIndex = this._curIndex + amount & 3;
      if (this._xDelta != 0) {
         int width = this.getWidth();
         this._xPos = this._xPos + amount * this._radius * this._xDelta;
         if (this._xPos > width) {
            this._xPos %= width;
         }
      }

      this.invalidate();
   }
}
