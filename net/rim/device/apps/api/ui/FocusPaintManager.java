package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

public final class FocusPaintManager {
   private int _focusStyle = 0;
   public static final int FOCUS_STYLE_DEFAULT = 0;
   public static final int FOCUS_STYLE_SOLIDOUTLINE = 1;
   public static final int FOCUS_STYLE_DOTTEDOUTLINE = 2;
   public static final int FOCUS_STYLE_TRANSPARENT = 3;
   private static final int DOTTED_LINE_STIPPLE = -252645136;
   private static final int SOLID_LINE_STIPPLE = -1;

   public final boolean setFocusStyle(int newStyle) {
      if (newStyle != this._focusStyle) {
         switch (newStyle) {
            case -1:
               return false;
            case 0:
            case 1:
            case 2:
            case 3:
            default:
               this._focusStyle = newStyle;
               return true;
         }
      } else {
         return true;
      }
   }

   public final int getFocusStyle() {
      return this._focusStyle;
   }

   public final void drawFocus(Graphics graphics, XYRect focusRect, int outlineColor) {
      if (focusRect != null) {
         switch (this._focusStyle) {
            case -1:
               break;
            case 0:
            default:
               int originalAlpha = graphics.getGlobalAlpha();
               graphics.setGlobalAlpha(64);
               graphics.rop(-95, focusRect.x, focusRect.y, focusRect.width, focusRect.height, null, 0, 0);
               graphics.setGlobalAlpha(originalAlpha);
               return;
            case 1:
            case 2:
               int foreColor = graphics.getColor();
               graphics.setColor(outlineColor);
               if (this._focusStyle == 2) {
                  graphics.setStipple(-252645136);
               }

               graphics.drawRect(focusRect.x, focusRect.y, focusRect.width, focusRect.height);
               if (this._focusStyle == 2) {
                  graphics.setStipple(-1);
               }

               graphics.setColor(foreColor);
         }
      }
   }
}
