package net.rim.device.apps.internal.lbs.render;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.internal.lbs.maplet.MapRect;
import net.rim.device.apps.internal.lbs.maplet.Maplet;
import net.rim.device.apps.internal.lbs.maplet.MarkerDictionary;
import net.rim.device.internal.ui.BorderBitmap;

final class PathMarker implements Comparator {
   Maplet _maplet;
   int _textIndex;
   XYRect _rect;
   public String _value;
   int _type;
   int _width;
   int _height;
   boolean _isNewVer;
   boolean _isDrawn;
   int[] _x;
   int[] _y;
   static Field _mapField;
   static BorderBitmap _hiwayBorder = (BorderBitmap)(new Object(3, 5, 3, 5, Bitmap.getBitmapResource("hiway.png")));
   static Bitmap _hwyBorder = Bitmap.getBitmapResource("hiway_can.png");
   static Bitmap _transCanBorder = Bitmap.getBitmapResource("hiway_tc.png");
   static Bitmap _interstate = Bitmap.getBitmapResource("interstate.png");
   static BorderBitmap _hwyEurope = (BorderBitmap)(new Object(3, 5, 3, 5, Bitmap.getBitmapResource("hiway_eu.png")));
   static Font _hiwayFont = FontRegistry.get("BBMillbankTall").getFont(1, 10, 0, 2, 0);

   protected final void render(Graphics g) {
      int[] matrix = g.getMatrix();
      g.setIdentity();
      g.setFont(_hiwayFont);
      int w = g.getFont().getAdvance(this._value);
      int h = g.getFont().getHeight();
      if (this._isNewVer) {
         if (this._type >= 0 && this._type < MarkerDictionary.MARKER_EUROPEAN_TYPE_ARRAY.length) {
            int fillColor = -1;
            int textColor = -1;
            int green = 6468422;
            int blue = 7768030;
            int red = 16731212;
            int yellow = 16776300;
            switch (this._type) {
               case 0:
                  break;
               case 1:
               default:
                  fillColor = green;
                  textColor = 16777215;
                  break;
               case 2:
               case 3:
                  fillColor = red;
                  textColor = 16777215;
                  break;
               case 4:
               case 5:
               case 8:
               case 9:
               case 10:
               case 12:
                  fillColor = blue;
                  textColor = 16777215;
                  break;
               case 6:
               case 7:
                  fillColor = yellow;
                  textColor = 0;
                  break;
               case 11:
                  fillColor = green;
                  textColor = 16777215;
            }

            if (fillColor != textColor && this._type != 0) {
               _hwyEurope.paint(g, this._rect);
               g.setColor(fillColor);
               g.fillRect(this._rect.x + 1, this._rect.y + 1, this._rect.width - 2, this._rect.height - 2);
               g.setColor(textColor);
               g.drawText(this._value, this._rect.x + (this._rect.width - w >> 1), this._rect.y + (this._rect.height - h >> 1));
            }
         }
      } else {
         if (this._type == 74 || this._type == 75 || this._type == 132) {
            for (int fontSize = 10; w > this._rect.width && fontSize > 1; w = g.getFont().getAdvance(this._value)) {
               g.setFont(FontRegistry.get("BBMillbankTall").getFont(1, --fontSize, 0, 2, 0));
            }

            if (w > this._rect.width) {
               g.setFont(FontRegistry.get("BBMillbankTall").getFont(1, 6, 0, 2, 0));
            }
         }

         switch (this._type) {
            case 73:
               g.setDrawingStyle(2, true);
               g.setColor(0);
               this.setPointerLocation(1);
               g.drawFilledPath(this._x, this._y, null, null);
               g.setColor(16777215);
               this.setPointerLocation(0);
               g.drawFilledPath(this._x, this._y, null, null);
               g.setColor(0);
               g.drawText(this._value, this._rect.x + (this._rect.width - w >> 1), this._rect.y + (this._rect.height - h >> 1) + 2);
               break;
            case 74:
            case 75:
               g.drawBitmap(this._rect.x, this._rect.y, this._rect.width, this._rect.height, _interstate, 0, 0);
               g.setColor(16777215);
               g.drawText(this._value, this._rect.x + (this._rect.width - w >> 1), this._rect.y + (this._rect.height - h >> 1) + 2);
               break;
            case 132:
               g.drawBitmap(this._rect.x, this._rect.y, this._rect.width, this._rect.height, _transCanBorder, 0, 0);
               g.setColor(1870945);
               g.drawText(this._value, this._rect.x + (this._rect.width - w >> 1), this._rect.y + (this._rect.height - h >> 1) + 2);
               break;
            default:
               _hiwayBorder.paint(g, this._rect);
               g.setColor(16777215);
               g.fillRect(this._rect.x + 5, this._rect.y + 3, this._rect.width - 10, 8);
               g.setColor(0);
               g.drawText(this._value, this._rect.x + (this._rect.width - w >> 1), this._rect.y + (this._rect.height - h >> 1) + 1);
         }
      }

      g.setMatrix(matrix);
   }

   final int compare(PathMarker other) {
      return this._value.compareTo(other._value);
   }

   final void setMarker(boolean isNewVer, int type, String value, Maplet maplet, MapRect rect, int zoom, int rotation) {
      this._value = value;
      this._type = type;
      this._rect = (XYRect)(new Object());
      this._isNewVer = isNewVer;
      this._isDrawn = false;
      if (!this._isNewVer) {
         switch (type) {
            case 73:
               this._rect.width = 4 + _hiwayFont.getAdvance(this._value);
               this._rect.height = 10;
               return;
            case 74:
            case 75:
               this._rect.width = 27;
               this._rect.height = 21;
               return;
            case 132:
               this._rect.width = 26;
               this._rect.height = 26;
               return;
            default:
               this._rect.width = 10 + _hiwayFont.getAdvance(this._value);
               this._rect.height = 14;
         }
      } else {
         this._rect.width = 8 + _hiwayFont.getAdvance(this._value);
         this._rect.height = 14;
      }
   }

   protected final void render(Graphics g, int x, int y) {
      this._rect.x = x - this._rect.width / 2;
      this._rect.y = y - this._rect.height / 2;
      this.render(g);
   }

   @Override
   public final int compare(Object o1, Object o2) {
      return ((PathMarker)o1).compare((PathMarker)o2);
   }

   private final void setPointerLocation(int offset) {
      this._x[0] = this._x[1] = this._x[5] = this._rect.x - offset;
      this._y[0] = this._y[4] = this._y[5] = this._rect.y - offset;
      this._y[1] = this._y[3] = this._rect.y + this._rect.height + offset;
      this._x[3] = this._x[4] = this._rect.x + this._rect.width + offset;
      this._x[2] = this._rect.x + this._rect.width / 2;
      this._y[2] = this._rect.y + this._rect.height * 4 / 3 + offset;
   }

   public PathMarker(int width, int height) {
      this._width = width;
      this._height = height;
      this._x = new int[6];
      this._y = new int[6];
   }
}
