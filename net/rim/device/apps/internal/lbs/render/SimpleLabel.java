package net.rim.device.apps.internal.lbs.render;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.apps.internal.lbs.Utilities;
import net.rim.device.apps.internal.lbs.maplet.Maplet;

final class SimpleLabel {
   Maplet _maplet;
   byte[] _labelEntry;
   int _facType;
   int _x;
   int _y;
   String _text;
   int _priority;
   int _zoom;
   int _paddingTown;
   boolean _isBitmapMarked;
   static CollisionArray _collisionArray = new CollisionArray();
   static Font _labelFont = FontRegistry.get("labelfont").getFont(0, 25);
   static Font _labelFontSmall = FontRegistry.get("LabelFontSmall").getFont(0, 11);
   static Font _labelFontMedium = FontRegistry.get("LabelFontMedium").getFont(0, 14);
   static Bitmap _hospital = Bitmap.getBitmapResource("hospital.png");
   static Bitmap _parking = Bitmap.getBitmapResource("parking.png");
   static Bitmap _school = Bitmap.getBitmapResource("school.png");
   static Bitmap _shopping = Bitmap.getBitmapResource("shopping.png");
   static Bitmap _restArea = Bitmap.getBitmapResource("restArea.png");
   static Bitmap _cityLarge = Bitmap.getBitmapResource("cityLarge.gif");
   static Bitmap _cityMedium = Bitmap.getBitmapResource("cityMedium.gif");
   static Bitmap _citySmall = Bitmap.getBitmapResource("citySmall.gif");

   SimpleLabel(int facType, String text, int x, int y, int priority, int zoom, int paddingTown) {
      this._facType = facType;
      this._x = x;
      this._y = y;
      this._text = text;
      this._priority = priority;
      this._zoom = zoom;
      this._paddingTown = paddingTown;
      this._isBitmapMarked = false;
   }

   protected final void render(Graphics graphics, int drawType) {
      boolean label = true;
      boolean icon = false;
      Font font = null;
      Bitmap bitmap = null;
      int bitmapWidth = 0;
      int bitmapHeight = 0;
      int bitmapX = 0;
      int bitmapY = 0;
      int paddingTown = 0;
      switch (this._facType) {
         case 1:
         case 9:
            label = false;
            icon = true;
            break;
         case 4:
         case 5:
         case 6:
            font = _labelFontSmall;
            if (this._zoom < 3 && !this._text.equals("")) {
               label = true;
               icon = true;
            } else if (this._zoom < 7) {
               label = false;
               icon = true;
            } else if (this._zoom > 6) {
               label = false;
               icon = false;
            }
            break;
         case 63:
            if (this._zoom >= 5) {
               label = true;
               icon = true;
            }
      }

      if (icon) {
         switch (this._facType) {
            case 1:
               bitmap = _parking;
               break;
            case 4:
               bitmap = _hospital;
               break;
            case 5:
               bitmap = _school;
               break;
            case 6:
               bitmap = _shopping;
               break;
            case 9:
               bitmap = _restArea;
               break;
            case 63:
               bitmap = _cityLarge;
         }

         bitmapWidth = bitmap.getWidth();
         bitmapHeight = bitmap.getHeight();
         bitmapX = this._x - bitmapWidth / 2;
         bitmapY = this._y - bitmapHeight / 2;
      }

      if (label) {
         if (font == null) {
            if (this._zoom < 5) {
               if (this._priority < 3) {
                  font = _labelFont;
               } else {
                  font = _labelFontSmall;
               }
            } else if (this._priority < 3) {
               font = _labelFont;
            } else if (this._priority < 6) {
               font = _labelFontMedium;
            } else {
               font = _labelFontSmall;
               paddingTown = this._paddingTown;
            }
         }

         graphics.setFont(font);
      }

      if (bitmap != null && drawType == 1) {
         if (_collisionArray.testArea(bitmapX, bitmapY, bitmapWidth, bitmapHeight)) {
            this._isBitmapMarked = false;
         } else {
            _collisionArray.markArea(bitmapX, bitmapY, bitmapWidth, bitmapHeight);
            this._isBitmapMarked = true;
         }
      } else {
         if (label
            && (
               drawType == 0 && font != _labelFontSmall && this._facType == 63 && icon
                  || drawType == 2 && font == _labelFontSmall && this._isBitmapMarked && icon
                  || drawType == 3 && font == _labelFontSmall && !this._isBitmapMarked && icon
                  || drawType == 0 && !icon
            )) {
            XYRect rect = Utilities.drawMultilineText(
               graphics, font, this._text, this._x, this._y, -1, -1, 1, 0, 75, Display.getWidth(), Display.getHeight(), false
            );
            int labelY = 0;
            int labelX = 0;
            int w = rect.width;
            int h = rect.height;
            int offsetY = 0;
            int offsetX = 0;
            boolean stop = false;
            int type = 0;
            if (icon) {
               if (drawType == 2) {
                  _collisionArray.unmarkArea(bitmapX, bitmapY, bitmapWidth, bitmapHeight);
                  this._isBitmapMarked = false;
               }

               if ((drawType == 3 || drawType == 0) && _collisionArray.testArea(bitmapX, bitmapY, bitmapWidth, bitmapHeight)) {
                  return;
               }

               if (this._facType == 63) {
                  if (font == _labelFontMedium) {
                     bitmap = _cityMedium;
                  } else if (font == _labelFontSmall) {
                     bitmap = _citySmall;
                  }
               }

               offsetY = h / 2;
               offsetX = w / 2;

               while (!stop) {
                  switch (type) {
                     case -1:
                        stop = true;
                        break;
                     case 0:
                     default:
                        labelY = this._y - (bitmap.getHeight() / 2 + h) - 2;
                        labelX = this._x - w / 2;
                        stop = !_collisionArray.testArea(labelX, labelY, w, h, paddingTown);
                        break;
                     case 1:
                        labelY = this._y - (bitmap.getHeight() / 2 + h) - 2;
                        labelX = this._x + bitmap.getWidth() / 2 - 2;
                        stop = !_collisionArray.testArea(labelX, labelY, w, h, paddingTown);
                        break;
                     case 2:
                        labelY = this._y - bitmap.getHeight() / 2 - 2;
                        labelX = this._x + bitmap.getWidth() / 2;
                        stop = !_collisionArray.testArea(labelX, labelY, w, h, paddingTown);
                        break;
                     case 3:
                        labelY = this._y - bitmap.getHeight() / 2 - 2;
                        labelX = this._x - bitmap.getWidth() / 2 - w - 2;
                        stop = !_collisionArray.testArea(labelX, labelY, w, h, paddingTown);
                        break;
                     case 4:
                        labelY = this._y + bitmap.getHeight() / 2 + 2;
                        labelX = this._x - w / 2;
                        stop = !_collisionArray.testArea(labelX, labelY, w, h, paddingTown);
                        break;
                     case 5:
                        labelY = this._y + bitmap.getHeight() / 2 - 2;
                        labelX = this._x + bitmap.getWidth() / 2 - 2;
                        stop = !_collisionArray.testArea(labelX, labelY, w, h, paddingTown);
                        break;
                     case 6:
                        labelY = this._y - (bitmap.getHeight() / 2 + h) - 2;
                        labelX = this._x - bitmap.getWidth() / 2 - w;
                        stop = !_collisionArray.testArea(labelX, labelY, w, h, paddingTown);
                        break;
                     case 7:
                        labelY = this._y + bitmap.getHeight() / 2 - 2;
                        labelX = this._x - bitmap.getWidth() / 2 - w;
                        stop = !_collisionArray.testArea(labelX, labelY, w, h, paddingTown);
                  }

                  type++;
               }

               if (type <= 8) {
                  Utilities.drawMultilineText(
                     graphics, font, this._text, labelX + offsetX, labelY + offsetY, w, h, 1, 0, 75, Display.getWidth(), Display.getHeight(), true
                  );
                  _collisionArray.markArea(labelX, labelY, w, h);
                  graphics.drawBitmap(bitmapX, bitmapY, bitmapWidth, bitmapHeight, bitmap, 0, 0);
                  _collisionArray.markArea(bitmapX, bitmapY, bitmapWidth, bitmapHeight);
                  this._isBitmapMarked = true;
                  return;
               }

               _collisionArray.unmarkArea(bitmapX, bitmapY, bitmapWidth, bitmapHeight);
               this._isBitmapMarked = false;
               return;
            }

            if (!_collisionArray.testArea(rect.x, rect.y, w, h)) {
               Utilities.drawMultilineText(graphics, font, this._text, this._x, this._y, w, h, 1, 0, 75, Display.getWidth(), Display.getHeight(), true);
               _collisionArray.markArea(rect.x, rect.y, w, h);
               return;
            }
         } else if (icon && !label && !_collisionArray.testArea(bitmapX, bitmapY, bitmapWidth, bitmapHeight)) {
            graphics.drawBitmap(bitmapX, bitmapY, bitmapWidth, bitmapHeight, bitmap, 0, 0);
         }
      }
   }
}
