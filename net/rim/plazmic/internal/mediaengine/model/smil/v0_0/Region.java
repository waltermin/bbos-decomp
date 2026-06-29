package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.util.MathUtilities;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui.ScalableBitmapField;

public class Region extends Manager {
   private int _left;
   private int _right;
   private int _width;
   private int _top;
   private int _bottom;
   private int _height;
   private boolean _leftIsPercent;
   private boolean _rightIsPercent;
   private boolean _widthIsPercent;
   private boolean _topIsPercent;
   private boolean _bottomIsPercent;
   private boolean _heightIsPercent;
   private int _backgroundColor;
   private int _fit;
   public static final int UNDEFINED = Integer.MAX_VALUE;
   public static final long DEFAULT_STYLE = 18014398509481984L;
   public static final long SCROLL_STYLE = 145610523889631232L;
   public static final int HIDDEN = 0;
   public static final int FIT = 1;
   public static final int SLICE = 2;
   public static final int MEET = 4;
   public static final int SCROLL = 8;

   public Region(long style) {
      super(style);
      this.setLeft(Integer.MAX_VALUE, false);
      this.setRight(Integer.MAX_VALUE, false);
      this.setWidth(Integer.MAX_VALUE, false);
      this.setTop(Integer.MAX_VALUE, false);
      this.setBottom(Integer.MAX_VALUE, false);
      this.setHeight(Integer.MAX_VALUE, false);
      this.setBackgroundColor(-1);
      this.setFit(0);
   }

   public void setLeft(int left, boolean isPercent) {
      this._left = left;
      this._leftIsPercent = isPercent;
   }

   public void setRight(int right, boolean isPercent) {
      this._right = right;
      this._rightIsPercent = isPercent;
   }

   public void setWidth(int width, boolean isPercent) {
      if (width < 0) {
         width = 0;
      }

      this._width = width;
      this._widthIsPercent = isPercent;
   }

   public void setTop(int top, boolean isPercent) {
      this._top = top;
      this._topIsPercent = isPercent;
   }

   public void setBottom(int bottom, boolean isPercent) {
      this._bottom = bottom;
      this._bottomIsPercent = isPercent;
   }

   public void setHeight(int height, boolean isPercent) {
      if (height < 0) {
         height = 0;
      }

      this._height = height;
      this._heightIsPercent = isPercent;
   }

   public void setBackgroundColor(int color) {
      if (color == -1 || color >= 0 && color <= 16777215) {
         this._backgroundColor = color;
      } else {
         throw new Object();
      }
   }

   public int getBackgroundColor() {
      int color = this._backgroundColor;
      Manager parentManager = this.getManager();
      if (color == -1 && parentManager instanceof Region) {
         color = ((Region)parentManager).getBackgroundColor();
      }

      return color;
   }

   @Override
   protected void paintBackground(Graphics graphics) {
      graphics.setBackgroundColor(this.getBackgroundColor());
      graphics.clear();
   }

   public void setFit(int fit) {
      switch (fit) {
         case 0:
         case 1:
         case 2:
         case 4:
         case 8:
            this._fit = fit;
            return;
         default:
            throw new Object(((StringBuffer)(new Object("Fit: "))).append(fit).append(" is not supported").toString());
      }
   }

   @Override
   public void sublayout(int availWidth, int availHeight) {
      int left = this.resolveDimension(this._left, this._leftIsPercent, availWidth);
      int right = this.resolveDimension(this._right, this._rightIsPercent, availWidth);
      int width = this.resolveDimension(this._width, this._widthIsPercent, availWidth);
      int top = this.resolveDimension(this._top, this._topIsPercent, availHeight);
      int bottom = this.resolveDimension(this._bottom, this._bottomIsPercent, availHeight);
      int height = this.resolveDimension(this._height, this._heightIsPercent, availHeight);
      int x = 0;
      int y = 0;
      int w = availWidth;
      int h = availHeight;
      if (left == Integer.MAX_VALUE) {
         if (width == Integer.MAX_VALUE) {
            if (right != Integer.MAX_VALUE) {
               w -= right;
            }
         } else {
            w = width;
            if (right != Integer.MAX_VALUE) {
               x = availWidth - right - width;
            }
         }
      } else {
         x = left;
         if (width == Integer.MAX_VALUE) {
            if (right != Integer.MAX_VALUE) {
               w = w - left - right;
            } else {
               w -= left;
            }
         } else {
            w = width;
         }
      }

      if (top == Integer.MAX_VALUE) {
         if (height == Integer.MAX_VALUE) {
            if (bottom != Integer.MAX_VALUE) {
               h = availHeight - bottom;
            }
         } else {
            h = height;
            if (bottom != Integer.MAX_VALUE) {
               y = availHeight - bottom - height;
            }
         }
      } else {
         y = top;
         if (height == Integer.MAX_VALUE) {
            if (bottom != Integer.MAX_VALUE) {
               h = h - top - bottom;
            } else {
               h -= top;
            }
         } else {
            h = height;
         }
      }

      this.setPosition(x, y);
      this.setVirtualExtent(w, h);
      this.setExtent(Math.min(availWidth, w), Math.min(availHeight, h));

      for (int i = 0; i < this.getFieldCount(); i++) {
         Field child = this.getField(i);
         if (child instanceof ScalableBitmapField) {
            ((ScalableBitmapField)child).setFillStyle(this._fit);
         }

         this.layoutChild(child, w, h);
      }
   }

   private int resolveDimension(int dimension, boolean isPercentage, int baseValue) {
      int result = Integer.MAX_VALUE;
      if (dimension != Integer.MAX_VALUE) {
         result = isPercentage ? Fixed32.mul(dimension, baseValue) : dimension;
      }

      return result;
   }

   private boolean isResponsibleForScrolling(Field childField) {
      return this._fit == 8 && !(childField instanceof Object);
   }

   @Override
   public boolean trackwheelRoll(int amount, int status, int time) {
      boolean handled = super.trackwheelRoll(amount, status, time);
      Field field = this.getFieldWithFocus();
      if (!handled && this.isResponsibleForScrolling(field)) {
         if ((status & 1) != 0) {
            int horizontalBound = field.getWidth() - this.getWidth();
            if (horizontalBound > 0) {
               int horizontalScroll = this.getHorizontalScroll();
               if (horizontalScroll != horizontalBound) {
                  int scroll = MathUtilities.clamp(0, horizontalScroll + (amount << 2), horizontalBound);
                  this.setHorizontalScroll(scroll);
                  handled = true;
               }
            }
         } else {
            int verticalBound = field.getHeight() - this.getHeight();
            if (verticalBound > 0) {
               int verticalScroll = this.getVerticalScroll();
               if (verticalScroll < verticalBound && amount > 0 || verticalScroll > 0 && amount < 0) {
                  int scroll = MathUtilities.clamp(0, verticalScroll + (amount << 2), verticalBound);
                  this.setVerticalScroll(scroll);
                  handled = true;
               }
            }
         }
      }

      return handled;
   }

   @Override
   protected boolean isScrollCopyable() {
      return false;
   }
}
