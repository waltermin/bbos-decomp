package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.vm.Array;

class CalendarViewListField extends Field {
   private CalendarViewListField$CalendarViewListFieldCallback _callback;
   private int _numEntries;
   private int _selectedIndex = -1;
   private int _anchorIndex = -1;
   private int[] _cumulativeHeights;
   private boolean _multiSelectAllowed;
   private static final int EMPTY_LIST_HEIGHT = 8;

   CalendarViewListField(CalendarViewListField$CalendarViewListFieldCallback callback, boolean multiSelectAllowed) {
      super(18014398509481984L);
      this._cumulativeHeights = new int[this._numEntries];
      this._multiSelectAllowed = true;
      this._callback = callback;
      this._multiSelectAllowed = multiSelectAllowed;
      this.setThemeAttributesSpecialClear(true);
   }

   public void setSize(int numEntries, int selectedIndex) {
      if (selectedIndex < 0) {
         selectedIndex = 0;
      } else if (selectedIndex >= numEntries) {
         selectedIndex = numEntries - 1;
      }

      this._numEntries = numEntries;
      this._selectedIndex = selectedIndex;
      this._anchorIndex = -1;
      Array.resize(this._cumulativeHeights, numEntries);
      this.updateLayout();
      this.invalidate();
      this.focusAdd(false);
   }

   public void setSelectedIndex(int selectedIndex) {
      if (selectedIndex < 0) {
         selectedIndex = 0;
      } else if (selectedIndex >= this._numEntries) {
         selectedIndex = this._numEntries - 1;
      }

      this._selectedIndex = this.scanForValidFocus(selectedIndex, true);
      if (this._selectedIndex < 0) {
         this._selectedIndex = this.scanForValidFocus(selectedIndex, false);
      }

      this._anchorIndex = -1;
      this.invalidate();
      this.focusAdd(false);
   }

   private int scanForValidFocus(int startIndex, boolean forward) {
      CalendarViewListField$CalendarViewListFieldCallback callback = this._callback;
      if (forward) {
         int endIndex = this._numEntries;

         for (int i = startIndex; i < endIndex; i++) {
            if (callback.allowFocusAt(this, i)) {
               return i;
            }
         }
      } else {
         int endIndex = 0;

         for (int i = startIndex; i >= endIndex; i--) {
            if (callback.allowFocusAt(this, i)) {
               return i;
            }
         }
      }

      return -1;
   }

   public void makeOffsetVisible(int offset) {
      this.makeOffsetVisible(offset, false);
   }

   public void makeOffsetVisible(int offset, boolean forceAtTop) {
      if (offset >= 0 && offset < this._cumulativeHeights.length) {
         int heightToPrevLine;
         int heightToCurrLine;
         if (offset < 0) {
            heightToPrevLine = 0;
            heightToCurrLine = this._callback != null ? this._callback.getRowHeight(this, -1) : 8;
         } else {
            if (offset > 0) {
               heightToPrevLine = this._cumulativeHeights[offset - 1];
            } else {
               heightToPrevLine = 0;
            }

            heightToCurrLine = this._cumulativeHeights[offset];
         }

         int width = this.getWidth();
         int height = heightToCurrLine - heightToPrevLine;
         int y = heightToPrevLine;
         int x = 0;
         if (forceAtTop) {
            height = Display.getHeight();
         }

         this.getScreen().ensureRegionVisible(this, x, y, width, height);
      }
   }

   public int getAnchorIndex() {
      return this._anchorIndex;
   }

   @Override
   public int getAccessibleRole() {
      return 28;
   }

   @Override
   public int getAccessibleChildCount() {
      return this._numEntries;
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      if (this._callback != null) {
         return (AccessibleContext)(this._callback.getAccessibleChildAt(this._selectedIndex) != null
            ? this._callback.getAccessibleChildAt(this._selectedIndex)
            : new Object("", 0, 4));
      } else {
         return null;
      }
   }

   private CalendarViewListField$CalendarViewListFieldCallback getCallback() {
      return this._callback;
   }

   public int getSelectedIndex() {
      return this._selectedIndex;
   }

   private int getSize() {
      return this._numEntries;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      return this._selectedIndex == index;
   }

   public boolean isMultiSelectInProgress() {
      return this._anchorIndex >= 0;
   }

   @Override
   public void invalidate() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public void invalidate(int x, int y, int width, int height) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected void drawFocus(Graphics g, boolean on) {
      if (this._callback != null) {
         this._callback.drawFocus(this, g, on);
      }
   }

   @Override
   public void getFocusRect(XYRect rect) {
      this.getHilightRect(rect, true);
   }

   public void getHilightRect(XYRect rect, boolean focus) {
      int heightToPrevLine = 0;
      int heightToCurrLine = 8;
      int selectedIndex = this._selectedIndex;
      if (this._callback != null) {
         heightToCurrLine = this._callback.getRowHeight(this, -1);
      }

      if (selectedIndex >= 0) {
         int start = selectedIndex - 1;
         int end = selectedIndex;
         if (!focus && this._anchorIndex >= 0) {
            start = Math.min(this._anchorIndex, selectedIndex) - 1;
            end = Math.max(this._anchorIndex, selectedIndex);
         }

         if (start < 0) {
            heightToPrevLine = 0;
         } else {
            heightToPrevLine = this._cumulativeHeights[start];
         }

         if (this._cumulativeHeights.length != 0) {
            heightToCurrLine = this._cumulativeHeights[end];
         }

         rect.x = 0;
         rect.y = heightToPrevLine;
         rect.width = this.getWidth();
         rect.height = heightToCurrLine - heightToPrevLine;
      } else {
         rect.x = 0;
         rect.y = 0;
         rect.width = 0;
         rect.height = 0;
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27 && this._anchorIndex >= 0) {
         this.focusRemove();
         XYRect hilightRect = (XYRect)(new Object());
         this.getHilightRect(hilightRect, false);
         this.invalidate(hilightRect.x, hilightRect.y, hilightRect.width, hilightRect.height);
         this._anchorIndex = -1;
         this.focusAdd(true);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public int getPreferredWidth() {
      return this._callback.getPreferredWidth(this);
   }

   @Override
   protected void layout(int width, int height) {
      int extentWidth = width;
      int extentHeight = 8;
      if (this._callback != null) {
         this._callback.layout(this, width, height);
         int heightToLast = 0;
         int numEntries = this._numEntries;
         if (numEntries == 0) {
            extentHeight = this._callback.getRowHeight(this, -1);
         } else {
            for (int i = 0; i < numEntries; i++) {
               heightToLast += this._callback.getRowHeight(this, i);
               this._cumulativeHeights[i] = heightToLast;
            }

            extentHeight = this._cumulativeHeights[this._numEntries - 1];
         }
      }

      this.setExtent(extentWidth, extentHeight);
   }

   void paintFocus(Graphics graphics, boolean on) {
      if (this._cumulativeHeights.length > 0) {
         XYRect hilightRect = (XYRect)(new Object());
         this.getHilightRect(hilightRect, true);
         int firstLine = 0;

         while (this._cumulativeHeights[firstLine] <= hilightRect.y) {
            firstLine++;
         }

         for (int redrawnHeight = 0; redrawnHeight < hilightRect.height && this._callback != null; firstLine++) {
            int rowHeight = this._callback.getRowHeight(this, firstLine);
            this._callback.drawListRow(this, graphics, firstLine, hilightRect.y + redrawnHeight, hilightRect.width, rowHeight);
            redrawnHeight += rowHeight;
         }
      }
   }

   @Override
   protected void paint(Graphics graphics) {
      CalendarViewListField$CalendarViewListFieldCallback callback = this._callback;
      if (callback != null) {
         if (this._numEntries <= 0) {
            String emptyString = callback.getEmptyString(this);
            if (emptyString != null) {
               graphics.drawText(emptyString, 0, 0, 4, Math.min(this.getWidth(), this.getManager().getVisibleWidth()));
               return;
            }
         }

         XYRect redrawRect = graphics.getClippingRect();
         int startPixel = redrawRect.y;
         int endPixel = startPixel + redrawRect.height - 1;
         int[] cumulativeHeights = this._cumulativeHeights;
         int startLine = -1;
         int numEntries = this._numEntries;

         for (int i = 0; i < numEntries; i++) {
            if (cumulativeHeights[i] > startPixel) {
               startLine = i;
               break;
            }
         }

         if (startLine >= 0) {
            int endLine = -1;

            for (int i = startLine; i < numEntries; i++) {
               if (cumulativeHeights[i] > endPixel) {
                  endLine = i;
                  break;
               }
            }

            if (endLine < 0) {
               endLine = numEntries - 1;
            }

            graphics.setBackgroundColor(callback.getBackgroundColor());
            graphics.clear();
            int width = this.getWidth();

            while (startLine <= endLine) {
               int y;
               if (startLine > 0) {
                  y = cumulativeHeights[startLine - 1];
               } else {
                  y = 0;
               }

               int height = cumulativeHeights[startLine] - y;
               callback.drawListRow(this, graphics, startLine, y, width, height);
               startLine++;
            }
         }
      }
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      boolean forward = amount >= 0;
      int tempIndex = this._selectedIndex;
      int unusedScroll = amount;
      XYRect hilightRect = Ui.getTmpXYRect();
      if (this.isMultiSelectInProgress()) {
         this.getHilightRect(hilightRect, false);
         this.invalidate(hilightRect.x, hilightRect.y, hilightRect.width, hilightRect.height);
      }

      if ((status & 65536) == 0) {
         if ((status & 2) != 0) {
            if (this._multiSelectAllowed && this._anchorIndex < 0) {
               this._anchorIndex = this._selectedIndex;
               this.getHilightRect(hilightRect, false);
               this.invalidate(hilightRect.x, hilightRect.y, hilightRect.width, hilightRect.height);
            }
         } else {
            this._anchorIndex = -1;
         }

         tempIndex += amount;
         unusedScroll = 0;
         if (tempIndex < 0) {
            unusedScroll = this._numEntries <= 0 ? amount : tempIndex;
            tempIndex = 0;
         } else if (tempIndex >= this._numEntries) {
            unusedScroll = this._numEntries <= 0 ? amount : tempIndex - this._numEntries + 1;
            tempIndex = this._numEntries - 1;
         }

         tempIndex = this.scanForValidFocus(tempIndex, forward);
         if (tempIndex < 0) {
            tempIndex = this.scanForValidFocus(tempIndex, !forward);
         }

         this._selectedIndex = tempIndex;
         if (this.isMultiSelectInProgress() && (amount >= 2 || amount <= -2)) {
            this.getHilightRect(hilightRect, false);
            this.invalidate(hilightRect.x, hilightRect.y, hilightRect.width, hilightRect.height);
         }

         if (this._callback != null) {
            this._callback.focusMoved(this, amount, status, time, this._selectedIndex);
         }
      }

      if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(6, new Object(1), new Object(2), this);
      }

      Ui.returnTmpXYRect(hilightRect);
      if ((status & 2) != 0) {
         unusedScroll = 0;
      }

      return unusedScroll;
   }

   @Override
   protected void moveFocus(int x, int y, int status, int time) {
      int numEntries = this._numEntries;
      int i = 0;

      while (i < numEntries && this._cumulativeHeights[i] < y) {
         i++;
      }

      this.setSelectedIndex(i);
      if (this._callback != null) {
         this._callback.focusMoved(this, 0, status, time, this._selectedIndex);
      }

      if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(6, new Object(1), new Object(2), this);
      }
   }

   @Override
   protected void onFocus(int direction) {
      if (direction > 0) {
         this._selectedIndex = this.scanForValidFocus(0, true);
      }
   }

   @Override
   protected void onUnfocus() {
      super.onUnfocus();
      this._selectedIndex = -1;
   }
}
