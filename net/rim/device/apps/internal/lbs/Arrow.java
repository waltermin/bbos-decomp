package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

class Arrow {
   protected int[] _tx;
   protected int[] _ty;
   protected int[] _endpoints;
   int[] _x;
   int[] _y;
   protected int _txLabel;
   protected int _tyLabel;
   protected int _xLabel;
   protected int _yLabel;
   protected int _color = 16711680;
   protected int _focusColor = 15728655;
   protected int _focusBookmarkColor = 9462000;
   int[] _arrowToMarkerX;
   int[] _arrowToMarkerY;
   int[] _arrowToCaptionX;
   int[] _arrowToCaptionY;
   Bitmap _marker = null;

   void init() {
      int numpoints = this._tx.length;
      this._x = new int[numpoints];
      this._y = new int[numpoints];
      this._arrowToMarkerX = new int[3];
      this._arrowToMarkerY = new int[3];
      this._arrowToCaptionX = new int[3];
      this._arrowToCaptionY = new int[3];
      this._marker = Bitmap.getBitmapResource("marker.png");
   }

   void calculatePointerLocation(int x, int y, int rotation) {
      if (rotation > 0) {
         rotation = 360 - rotation;
      }

      int cos = Utilities.cos(rotation);
      int sin = Utilities.sin(rotation);

      for (int i = this._tx.length - 1; i >= 0; i--) {
         int tx = this._tx[i];
         int ty = this._ty[i];
         this._x[i] = (cos * tx - sin * ty + 32768 >> 16) + x;
         this._y[i] = (sin * tx + cos * ty + 32768 >> 16) + y;
      }

      this._xLabel = x;
      this._yLabel = y;
   }

   void paint(Graphics graphics, int x, int y, int rotation, int colourFill) {
      this.calculatePointerLocation(x, y, rotation);
      int alpha = graphics.getGlobalAlpha();
      graphics.setDrawingStyle(2, true);
      graphics.setMatrix(65536, 0, 131072, 0, 65536, 131072, 0, 0, 65536);
      graphics.setColor(0);
      graphics.setGlobalAlpha(alpha / 4);
      graphics.drawFilledPath(this._x, this._y, null, this._endpoints);
      graphics.setMatrix(65536, 0, 0, 0, 65536, 0, 0, 0, 65536);
      graphics.setColor(colourFill);
      graphics.setGlobalAlpha(alpha);
      graphics.drawFilledPath(this._x, this._y, null, this._endpoints);
   }

   public boolean drawMarker(Graphics graphics, int x, int y, int colourStroke, int colourFill, Bitmap logo) {
      graphics.setGlobalAlpha(255);
      graphics.drawBitmap(x - 13, y - 38, 53, 42, this._marker, 0, 0);
      return false;
   }

   void drawCaption(
      Graphics graphics,
      Transform transform,
      Font font,
      int x,
      int y,
      String label,
      Bitmap logo,
      String distance,
      boolean drawArrow,
      boolean markerFlipped,
      String signText,
      String routeName,
      int action
   ) {
      this.drawCaption(graphics, transform, font, x, y, label, logo, distance, drawArrow, markerFlipped, -1, -1, -1, signText, routeName, action);
   }

   void drawCaption(
      Graphics graphics,
      Transform transform,
      Font font,
      int x,
      int y,
      String label,
      Bitmap logo,
      String distance,
      boolean drawArrow,
      boolean markerFlipped,
      int captionX,
      int captionY,
      int captionWidth,
      String signText,
      String routeName,
      int action
   ) {
      if (label != null && label.length() != 0) {
         if (font == null) {
            font = Font.getDefault();
         }

         graphics.setFont(font);
         XYRect rect = (XYRect)(new Object());
         if (distance != null && distance.length() > 0) {
            distance = ((StringBuffer)(new Object("("))).append(distance).append(")").toString();
            label = ((StringBuffer)(new Object())).append(label).append(" ").append(distance).toString();
         }

         int screenWidth = transform._screenView.width();
         int screenHeight = -transform._screenView.height();
         int logoWidth = 0;
         int logoHeight = 0;
         int y2 = 0;
         int y1 = 0;
         int x2 = 0;
         int x1 = 0;
         if (logo != null) {
            logoWidth = logo.getWidth();
            logoHeight = logo.getHeight();
            rect.width = logoWidth;
            rect.height = logoWidth;
            if (distance != null && distance.length() > 0) {
               rect.width = rect.width + Math.max(0, font.getAdvance(distance) - rect.width);
               rect.height = rect.height + font.getHeight() + 1;
            }
         }

         rect = Utilities.drawMultilineText(graphics, font, label, x, y, captionWidth, -1, 0, 1, 100, screenWidth, screenHeight, false);
         if (captionX != -1 && captionY != -1) {
            rect.x = captionX - rect.width / 2;
            rect.y = captionY - rect.height;
         } else {
            int xOffset = 0;
            if (y <= screenHeight / 2) {
               rect.y = y + 16;
            } else {
               rect.y = y - rect.height - 16;
               xOffset = 20;
            }

            rect.y = Math.min(rect.y, screenHeight - 1 - rect.height);
            rect.y = Math.max(rect.y, 0);
            if (markerFlipped) {
               xOffset = 20;
            }

            if (x <= screenWidth / 2) {
               if (logo != null) {
                  rect.x = 2;
               } else {
                  rect.x = x + xOffset;
               }
            } else if (logo != null) {
               rect.x = screenWidth - 3 - rect.width - logoWidth;
            } else {
               rect.x = x - rect.width - xOffset - logoWidth;
            }

            rect.x = Math.min(rect.x, screenWidth - 1 - rect.width - logoWidth);
            rect.x = Math.max(rect.x, 0);
            int arrowWidth = Math.min(20, Math.max(2, rect.width - 6));
            int arrowOffset = Math.min(20, (rect.width - arrowWidth) / 2);
            if (Math.abs(x - rect.x) <= Math.abs(x - rect.x - rect.width)) {
               x1 = rect.x + arrowOffset;
               x2 = x1 + arrowWidth;
            } else {
               x1 = rect.x + rect.width - arrowOffset;
               x2 = x1 - arrowWidth;
            }

            if (Math.abs(y - rect.y) <= Math.abs(y - rect.y - rect.height)) {
               y1 = rect.y + 16;
               y2 = y1 + 16;
            } else {
               y1 = rect.y + rect.height - 16;
               y2 = y1 - 16;
            }

            if (rect.y >= y) {
               y1 = y2 = rect.y - 1;
            } else if (rect.y + rect.height < y) {
               y1 = y2 = rect.y + rect.height + 1;
            } else if (rect.x >= x) {
               x1 = x2 = rect.x - 1;
            } else if (rect.x + rect.width <= x) {
               x1 = x2 = rect.x + rect.width + 1;
            } else {
               drawArrow = false;
            }
         }

         int oldColour = graphics.getColor();
         int oldAlpha = graphics.getGlobalAlpha();
         int adjustWidth = logo == null ? 0 : 2 + logo.getWidth();
         graphics.setColor(0);
         graphics.setGlobalAlpha(32);
         graphics.fillRoundRect(rect.x + 1, rect.y - 5, rect.width + 5 + adjustWidth, rect.height + 2, 20, 20);
         if (drawArrow) {
            this.createArrowToCaption(x1 + 4 + adjustWidth, y1 - 4, x, y, x2 + 4, y2 - 4);
            graphics.drawFilledPath(this._arrowToCaptionX, this._arrowToCaptionY, null, null);
         }

         graphics.setColor(16777215);
         graphics.setGlobalAlpha(192);
         graphics.fillRoundRect(rect.x - 3, rect.y - 1, rect.width + 5 + adjustWidth, rect.height + 2, 20, 20);
         if (drawArrow) {
            this.createArrowToCaption(x1 + adjustWidth, y1, x, y, x2, y2);
            graphics.drawFilledPath(this._arrowToCaptionX, this._arrowToCaptionY, null, null);
         }

         int logoOffsetY = 0;
         if (logo != null) {
            logoOffsetY = (rect.height + 2) / 2 - logo.getHeight() / 2;
            graphics.drawBitmap(rect.x, rect.y + logoOffsetY, logo.getWidth(), logo.getHeight(), logo, 0, 0);
            if (signText != null) {
               int adjust = signText.equals(LBSResources.getString(472)) ? 10 : 15;
               int adjustI = 0;
               int oldColor = graphics.getColor();
               Font oldFont = graphics.getFont();
               Font glowFont = FontRegistry.get("BBCondensed").getFont(1, 4, 2);
               glowFont = glowFont.derive(1, glowFont.getHeight(), 0, 1, 2560, 65536, 0, 0, 65536, 0, 0, 0, 16777215);
               String name = routeName.toUpperCase();
               if (action == 10) {
                  graphics.setFont(glowFont);
                  graphics.setColor(16777215);
               } else if (name.indexOf("HWY ") != -1 && signText.indexOf(LBSResources.getString(472)) == -1) {
                  graphics.setFont(FontRegistry.get("BBCondensed").getFont(1, 6, 2));
                  graphics.setColor(32768);
                  adjust = 20;
               } else if ((name.indexOf("AUT ") != -1 || name.indexOf("AUTOROUTE ") != -1 || name.indexOf("AUTO ROUTE ") != -1)
                  && signText.indexOf(LBSResources.getString(472)) == -1) {
                  graphics.setFont(font);
                  graphics.setColor(16777215);
                  adjust = 20;
               } else if ((name.indexOf("I-") != -1 || name.indexOf("I ") != -1) && signText.indexOf(LBSResources.getString(472)) == -1) {
                  graphics.setFont(font);
                  graphics.setColor(16777215);
                  adjustI = 5;
                  adjust = 20;
               } else if ((name.indexOf("US-") != -1 || name.indexOf("US ") != -1) && signText.indexOf(LBSResources.getString(472)) == -1) {
                  graphics.setFont(FontRegistry.get("BBCondensed").getFont(1, 4, 2));
                  graphics.setColor(0);
                  adjustI = 5;
                  adjust = 20;
               } else {
                  graphics.setFont(glowFont);
                  graphics.setColor(16777215);
               }

               int idx = signText.indexOf(" ");
               graphics.setGlobalAlpha(255);
               if (idx == -1) {
                  graphics.drawText(signText, rect.x, rect.y + adjust + logoOffsetY, 36, logo.getWidth());
               } else {
                  String number = signText.substring(0, idx);
                  String direction = signText.substring(idx + 1);
                  direction.toLowerCase();
                  int color = graphics.getColor();
                  Font f = graphics.getFont();
                  boolean changedColor = false;
                  if (color == 32768) {
                     color = 16777215;
                     f = f.derive(1, glowFont.getHeight(), 0, 1, 2560, 65536, 0, 0, 65536, 0, 0, 0, 16777215);
                     changedColor = true;
                  }

                  graphics.setColor(color);
                  graphics.setFont(f);
                  graphics.drawText(direction, rect.x, rect.y + 5 + adjustI + logoOffsetY, 36, logo.getWidth());
                  if (changedColor) {
                     color = 32768;
                     f = FontRegistry.get("BBCondensed").getFont(1, 6, 2);
                  }

                  graphics.setColor(color);
                  graphics.setFont(f);
                  graphics.drawText(number, rect.x, rect.y + adjust + logoOffsetY, 36, logo.getWidth());
               }

               graphics.setFont(oldFont);
               graphics.setColor(oldColor);
               graphics.setGlobalAlpha(192);
            }

            rect.x = rect.x + logo.getWidth() + 2;
         }

         graphics.setColor(0);
         Utilities.drawMultilineText(graphics, font, label, rect.x, rect.y, captionWidth, -1, 0, 1, 100, screenWidth, screenHeight, true);
         graphics.setColor(oldColour);
         graphics.setGlobalAlpha(oldAlpha);
      }
   }

   private void createArrowToCaption(int x0, int y0, int x1, int y1, int x2, int y2) {
      this._arrowToCaptionX[0] = x0;
      this._arrowToCaptionX[1] = x1;
      this._arrowToCaptionX[2] = x2;
      this._arrowToCaptionY[0] = y0;
      this._arrowToCaptionY[1] = y1;
      this._arrowToCaptionY[2] = y2;
   }
}
