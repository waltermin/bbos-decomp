package net.rim.wica.runtime.ui.internal;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;

public class PageScroller implements IPageScroller {
   private IScrollManager _scrollManager;
   private Vector _allFocusableFields = new Vector();
   private static PageScroller _instance;

   public Vector getAllFocusableFields() {
      this._allFocusableFields.removeAllElements();
      this.traverseTree(this._scrollManager.castToManager());
      return this._allFocusableFields;
   }

   public Field findBottomOrBelowFocus(Vector focusFields, int top, int bottom, XYRect rect) {
      int count = focusFields.size();
      Field bottomOrBelowFocus = null;
      int bottomY = Integer.MAX_VALUE;
      boolean isBottomVisible = false;

      for (int i = 0; i < count; i++) {
         Field f = (Field)focusFields.elementAt(i);
         this.getTransformedRect(f, rect);
         int rectY2 = this.getY2(rect);
         if (rectY2 > top) {
            if (isBottomVisible) {
               if (rectY2 >= bottomY && this.isFocusFullyVisible(f, rect, top, bottom)) {
                  if (rectY2 > bottom) {
                     return f;
                  }

                  bottomOrBelowFocus = f;
                  bottomY = rectY2;
               }
            } else if (rectY2 < bottomY) {
               isBottomVisible = this.isFocusFullyVisible(f, rect, top, bottom);
               if (isBottomVisible && rectY2 > bottom) {
                  return f;
               }

               if (isBottomVisible || rect.y > top) {
                  bottomOrBelowFocus = f;
                  bottomY = rectY2;
               }
            }
         }
      }

      return bottomOrBelowFocus;
   }

   public Field findTopOrAboveFocus(Vector focusFields, int top, int bottom, XYRect rect) {
      int count = focusFields.size();
      Field topOrAboveFocus = null;
      int topY = -1;
      boolean isTopVisible = false;

      for (int i = 0; i < count; i++) {
         Field f = (Field)focusFields.elementAt(i);
         this.getTransformedRect(f, rect);
         int rectY2 = this.getY2(rect);
         if (rect.y < bottom) {
            if (isTopVisible) {
               if (rectY2 < topY && this.isFocusFullyVisible(f, rect, top, bottom)) {
                  if (rect.y < top) {
                     return f;
                  }

                  topOrAboveFocus = f;
                  topY = rectY2;
               }
            } else if (rectY2 > topY) {
               isTopVisible = this.isFocusFullyVisible(f, rect, top, bottom);
               if (isTopVisible && rect.y < top) {
                  return f;
               }

               if (isTopVisible || rectY2 < bottom) {
                  topOrAboveFocus = f;
                  topY = rectY2;
               }
            }
         }
      }

      return topOrAboveFocus;
   }

   public int getFocusYPosition(Field f, XYRect rect, int top, int bottom, boolean isTopVisibleY) {
      XYRect frect = new XYRect();
      f.getFocusRect(frect);
      if (isTopVisibleY) {
         return rect.y >= top ? 0 : top - rect.y + frect.height - 1;
      }

      int rectY2 = this.getY2(rect);
      return rectY2 <= bottom ? rectY2 - rect.y : bottom - rect.y - frect.height + 1;
   }

   public boolean isFocusFullyVisible(Field f, XYRect rect, int top, int bottom) {
      int rectY2 = this.getY2(rect);
      if (rect.y >= top && rectY2 <= bottom) {
         return true;
      }

      if (f instanceof MultiFocusable && rectY2 >= top && rect.y <= bottom) {
         XYRect frect = new XYRect();
         f.getFocusRect(frect);
         int visibleHeight;
         if (rect.y < top && rectY2 > bottom) {
            visibleHeight = bottom - top + 1;
         } else if (rectY2 < bottom && rectY2 > top) {
            visibleHeight = rectY2 - top + 1;
         } else {
            visibleHeight = bottom - rect.y + 1;
         }

         return frect.height <= visibleHeight;
      } else {
         return false;
      }
   }

   public int getY2(XYRect rect) {
      return rect.height > 0 ? rect.y + rect.height - 1 : rect.y;
   }

   @Override
   public void scrollPage(int where) {
      XYRect rect = new XYRect();
      int verticalScroll = this._scrollManager.getVerticalScroll();
      int virtualHeight = this._scrollManager.getVirtualHeight();
      int contentHeight = this._scrollManager.getContentHeight();
      int fontHeight = this._scrollManager.getFont().getHeight();
      int top = 0;
      boolean isTopFocus = false;
      switch (where) {
         case 2:
            top = virtualHeight - contentHeight;
            break;
         case 256:
            top = Math.max(verticalScroll - contentHeight + fontHeight, 0);
         default:
            isTopFocus = true;
            break;
         case 512:
            top = Math.min(verticalScroll + contentHeight - fontHeight, virtualHeight - contentHeight);
      }

      this._scrollManager.setVerticalScroll(top);
      int bottom = top + contentHeight - 1;
      this.getAllFocusableFields();
      Field focus = isTopFocus
         ? this.findTopOrAboveFocus(this._allFocusableFields, top, bottom, rect)
         : this.findBottomOrBelowFocus(this._allFocusableFields, top, bottom, rect);
      if (focus == null) {
         focus = isTopFocus
            ? this.findBottomOrBelowFocus(this._allFocusableFields, top, bottom, rect)
            : this.findTopOrAboveFocus(this._allFocusableFields, top, bottom, rect);
      }

      this._allFocusableFields.removeAllElements();
      if (focus != null) {
         this.getTransformedRect(focus, rect);
         if (this.isFocusFullyVisible(focus, rect, top, bottom)) {
            this._scrollManager.disableFocusHolder();
            if (focus instanceof MultiFocusable) {
               ((MultiFocusable)focus).moveFocus(0, this.getFocusYPosition(focus, rect, top, bottom, isTopFocus), 0, 0);
            }

            focus.setFocus();
         } else {
            this._scrollManager.setLastFocus(focus);
            this._scrollManager.enableFocusHolder();
            this._scrollManager.setFocusHolder();
            if (focus instanceof MultiFocusable) {
               ((MultiFocusable)focus).moveFocus(0, rect.y > top ? 0 : focus.getContentHeight() - 1, 0, 0);
            }
         }
      }

      this._scrollManager = null;
   }

   public static IPageScroller getInstance(IScrollManager scrollManager) {
      if (_instance == null) {
         _instance = new PageScroller();
      }

      _instance._scrollManager = scrollManager;
      return _instance;
   }

   private void getTransformedRect(Field f, XYRect rect) {
      if (f instanceof MultiFocusable) {
         f.getContentRect(rect);
      } else {
         f.getFocusRect(rect);
         rect.translate(f.getContentLeft(), f.getContentTop());
      }

      this._scrollManager.transformToAppScreen(f, rect);
   }

   private void traverseTree(Manager m) {
      int count = m.getFieldCount();

      for (int i = 0; i < count; i++) {
         Field f = m.getField(i);
         if (f.isFocusable()) {
            if (f instanceof Manager) {
               this.traverseTree((Manager)f);
            } else {
               this._allFocusableFields.addElement(f);
            }
         }
      }
   }

   private PageScroller() {
   }
}
