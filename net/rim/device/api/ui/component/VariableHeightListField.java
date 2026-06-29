package net.rim.device.api.ui.component;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextFactory;
import net.rim.device.api.ui.accessibility.AccessibleContextProxy;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.i18n.CommonResource;

public class VariableHeightListField extends Field {
   private int _size;
   private int _topRow;
   private int _cursor;
   private boolean _upScrollArrowVisible;
   private boolean _downScrollArrowVisible;
   private boolean _layoutOnSizeChange;
   private XYRect _focusRect = new XYRect();
   private int _selectionRange;
   private VariableHeightListFieldCallback _callback;
   private String _emptyString;
   private int _emptyStyle = 4;
   private long _rbId;
   private int _cachedLocaleCode;
   private int _rbKey;
   private String _rbName;
   private Bitmap _upScrollArrow;
   private Bitmap _downScrollArrow;
   private ThemeAttributeSet _tasRowEven;
   private ThemeAttributeSet _tasRowOdd;
   private static Tag TAG = Tag.create("list");
   private static final String TAG_ROW_EVEN_SUFFIX;
   private static final String TAG_ROW_ODD_SUFFIX;
   public static final int MULTI_SELECT;
   public static final int NO_ALTED_PAGE_UP_DOWN;

   public VariableHeightListField() {
      this(0, 0);
   }

   public VariableHeightListField(int numRows) {
      this(numRows, 0);
   }

   public VariableHeightListField(int numRows, long style) {
      super(validateStyle(style));
      this.setTag(TAG);
      this._size = 0;
      this._topRow = 0;
      this._cursor = -1;
      this._selectionRange = 0;
      this.setSize(numRows, 0);
      if (this instanceof VariableHeightListFieldCallback) {
         this._callback = (VariableHeightListFieldCallback)this;
      }
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      this._upScrollArrow = ThemeAttributeSet.getScrollArrow(this, 0);
      this._downScrollArrow = ThemeAttributeSet.getScrollArrow(this, 1);
      Theme theme = ThemeManager.getActiveTheme();
      this._tasRowEven = theme.getAttributeSet(Tag.create(this.getTag().toString() + "-row-even"));
      this._tasRowOdd = theme.getAttributeSet(Tag.create(this.getTag().toString() + "-row-odd"));
   }

   private void calcFocusRect(boolean move) {
      if (move) {
         this.focusRemove();
      }

      if (this._cursor >= 0) {
         this._focusRect.y = this.getYForRow(this._cursor);
         this._focusRect.height = this.getRowHeight(this._cursor);
      }

      if (move) {
         this.focusAdd(true);
      }
   }

   private void calcSelectedIndex(int index) {
      this.calcSelectedIndex(index, false, false);
   }

   private void calcSelectedIndex(int index, boolean scrollToTop) {
      this.calcSelectedIndex(index, scrollToTop, false);
   }

   private void calcSelectedIndex(int index, boolean scrollToTop, boolean moveFocus) {
      if (this._size == 0) {
         this._cursor = -1;
         this._topRow = 0;
         this._selectionRange = 0;
         this.updateScrollbar();
      } else {
         if (moveFocus) {
            this.focusRemove();
         }

         this._cursor = MathUtilities.clamp(0, index, this._size - 1);
         this._selectionRange = 0;
         if (scrollToTop || this._cursor < this._topRow) {
            this.setTopRow(this._cursor, this._cursor);
         }

         this.makeFocusVisible(moveFocus);
      }
   }

   private void checkLocale() {
      if (this._rbId != 0) {
         int currentCode = Locale.getDefault().getCode();
         if (this._cachedLocaleCode != currentCode) {
            this._cachedLocaleCode = currentCode;
            ResourceBundleFamily family = ResourceBundle.getBundle(this._rbId, this._rbName);
            this._emptyString = family.getString(this._rbKey);
         }
      }
   }

   private int computeFieldHeight() {
      int height = 0;

      for (int i = this._size - 1; i >= 0; i--) {
         try {
            height += this.getRowHeight(i);
         } catch (Throwable var4) {
         }
      }

      return height;
   }

   public void delete(int index) {
      boolean focusMoved = false;
      if (index >= 0 && index <= this._size) {
         this._selectionRange = 0;
         this._size--;
         if (index < this._topRow) {
            this._topRow--;
         }

         if (index < this._cursor) {
            this._cursor--;
         }

         if (index == this._cursor && index == this._size) {
            if (index == 0) {
               this._cursor = -1;
            } else {
               this._cursor--;
               focusMoved = true;
               index--;
            }
         }

         this.calcFocusRect(false);
         this.fieldChangeNotify(Integer.MIN_VALUE);
         Manager manager = this.getManager();
         if (manager != null) {
            this.updateLayout();
            if (focusMoved) {
               this.focusAdd(false);
               this.focusChangeNotify(2);
            }

            this.invalidate(0, this.getYForRow(index), this.getWidth(), 1073741823);
         }

         this.updateScrollbar();
      } else {
         throw new ArrayIndexOutOfBoundsException(index);
      }
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (!on) {
         this.invalidate(this._focusRect.x, this._focusRect.y, this._focusRect.width, this._focusRect.height);
      } else {
         super.drawFocus(graphics, on);
      }
   }

   @Override
   public String getAccessibleName() {
      return this.getAccessibleChildAt(0) != null ? this.getAccessibleChildAt(0).getAccessibleName() : null;
   }

   @Override
   public int getAccessibleStateSet() {
      return super.getAccessibleStateSet();
   }

   @Override
   public int getAccessibleRole() {
      return 25;
   }

   @Override
   public AccessibleContext getAccessibleParent() {
      return this.getScreen();
   }

   @Override
   public int getAccessibleChildCount() {
      return this._size;
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      Object temp = this._callback.get(this, index);
      if (temp != null) {
         return !(temp instanceof AccessibleContext) ? new AccessibleContextFactory(temp.toString()) : (AccessibleContext)temp;
      } else {
         return null;
      }
   }

   @Override
   public int getAccessibleSelectionCount() {
      return this.getSelection().length;
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      int[] selectionRange = this.getSelection();
      if (this._callback != null && index <= selectionRange.length) {
         Object temp = this._callback.get(this, this.getSelectedIndex() + index);
         if (temp == null) {
            return new AccessibleContextFactory(this.getEmptyString(), 25, 4);
         } else if (temp instanceof AccessibleContext) {
            return (AccessibleContext)temp;
         } else {
            return !(temp instanceof AccessibleContextProxy)
               ? new AccessibleContextFactory(temp.toString(), 25, 4)
               : ((AccessibleContextProxy)temp).getAccessibleContext();
         }
      } else {
         return null;
      }
   }

   public VariableHeightListFieldCallback getCallback() {
      return this._callback;
   }

   public String getEmptyString() {
      this.checkLocale();
      return this._emptyString != null ? this._emptyString : CommonResource.getString(1012);
   }

   public int getEmptyStringStyle() {
      return this._emptyStyle;
   }

   public int getFirstVisibleRow() {
      return this._topRow;
   }

   @Override
   public void getFocusRect(XYRect rect) {
      rect.set(this._focusRect);
   }

   public int getLastVisibleRow() {
      int lastVisibleRow = this._topRow + this.getVisibleLinesPageDown(this._topRow) - 1;
      if (lastVisibleRow >= this._size) {
         lastVisibleRow = this._size - 1;
      }

      return lastVisibleRow;
   }

   @Override
   public int getPreferredWidth() {
      return this._callback.getPreferredWidth(this);
   }

   protected int getRowHeight(int row) {
      return this._callback != null ? this._callback.getRowHeight(this, row) : 0;
   }

   protected int getRowHeight(int topRow, int row) {
      int oldTopRow = this._topRow;
      this._topRow = topRow;
      int height = this.getRowHeight(row);
      this._topRow = oldTopRow;
      return height;
   }

   public int getSelectedIndex() {
      return this.getSelectionAnchor();
   }

   public int[] getSelection() {
      int size = Math.abs(this._selectionRange) + 1;
      int[] selection = new int[size];
      int last = this._cursor + (this._selectionRange > 0 ? this._selectionRange : 0);
      int index = last;

      while (size > 0) {
         selection[--size] = index--;
      }

      return selection;
   }

   private int getSelectionAnchor() {
      return this._cursor;
   }

   public int getSize() {
      return this._size;
   }

   public int getTopRow() {
      return this._topRow;
   }

   private int getVisibleLinesPageDown(int topVisibleLine) {
      Manager manager = this.getManager();
      int height = this.getHeight();
      if (manager != null && manager.getVisibleHeight() < height) {
         height = manager.getVisibleHeight();
      }

      for (int row = topVisibleLine; row < this._size; row++) {
         int rowHeight = this.getRowHeight(row);
         if (rowHeight > height) {
            return row - topVisibleLine;
         }

         height -= rowHeight;
      }

      return this._size - topVisibleLine;
   }

   private int getVisibleLinesPageUp(int bottomVisibleLine) {
      int height = Math.min(this.getHeight(), this.getManager().getVisibleHeight());

      for (int row = bottomVisibleLine; row >= 0; row--) {
         int rowHeight = this.getRowHeight(row);
         if (rowHeight > height) {
            return bottomVisibleLine - row;
         }

         height -= rowHeight;
      }

      return bottomVisibleLine;
   }

   protected int getYForRow(int row) {
      if (row < this._topRow) {
         throw new IllegalArgumentException();
      }

      int offset = 0;

      for (int i = this._topRow; i < row; i++) {
         offset += this.getRowHeight(i);
      }

      return offset;
   }

   public void insert(int index) {
      boolean focusMoved = false;
      if (index >= 0 && index <= this._size) {
         this._selectionRange = 0;
         if (index <= this._topRow) {
            this._topRow++;
            this.updateScrollbar();
         }

         if (this._size == 0) {
            this._cursor = 0;
            this._topRow = 0;
         } else if (index <= this._cursor) {
            this._cursor++;
            focusMoved = true;
         }

         this._size++;
         this.calcFocusRect(false);
         Manager manager = this.getManager();
         if (manager != null) {
            this.updateLayout();
            if (focusMoved) {
               this.focusAdd(false);
            }

            this.invalidate(0, this.getYForRow(index), this.getWidth(), 1073741823);
         }

         this.fieldChangeNotify(Integer.MIN_VALUE);
         this.updateScrollbar();
      } else {
         throw new ArrayIndexOutOfBoundsException(index);
      }
   }

   @Override
   public void invalidate() {
      this.makeFocusVisible(false);
      this.updateScrollbar();
      super.invalidate();
   }

   public void invalidate(int index) {
      if (index >= this._topRow) {
         this.invalidate(0, this.getYForRow(index), this.getWidth(), this.getRowHeight(index));
      }
   }

   public void invalidateRange(int start, int end) {
      if (end < start) {
         throw new IllegalArgumentException();
      }

      if (start < this._topRow) {
         start = this._topRow;
      }

      if (end >= this._size) {
         end = this._size - 1;
      }

      int y = this.getYForRow(start);
      end = MathUtilities.clamp(start, end, this._topRow + this.getVisibleLinesPageDown(this._topRow) + 1);
      this.invalidate(0, y, this.getContentWidth(), this.getYForRow(end + 1) - y);
   }

   @Override
   public boolean isAccessibleStateSet(int state) {
      return (super.getAccessibleStateSet() & state) != 0;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      int[] selectionRange = this.getSelection();

      for (int i = 0; i < selectionRange.length; i++) {
         if (selectionRange[i] == index) {
            return true;
         }
      }

      return false;
   }

   public boolean isEmpty() {
      return this._size == 0;
   }

   protected boolean isOnTopRow(int index) {
      return index == this._topRow;
   }

   @Override
   protected void layout(int width, int height) {
      this._focusRect.x = 0;
      this._focusRect.width = width;
      this.calcFocusRect(false);
      if (height > 100000) {
         this._layoutOnSizeChange = true;
         height = this.computeFieldHeight();
      }

      this.setExtent(width, height);
      this.updateScrollbar();
   }

   private void makeFocusVisible(boolean moveFocus) {
      if (this._cursor < this._topRow) {
         if (this._cursor >= 0) {
            this.setTopRow(this._cursor, this._cursor);
         }
      } else {
         int y = this.getYForRow(this._cursor);
         int rowHeight = this.getRowHeight(this._cursor);
         int contentHeight = this.getContentHeight();
         if (contentHeight > 0 && y + rowHeight > contentHeight) {
            int height = contentHeight;

            int row;
            for (row = this._cursor; row >= 0; row--) {
               rowHeight = this.getRowHeight(row);
               if (height - rowHeight < 0) {
                  break;
               }

               height -= rowHeight;
            }

            this.setTopRow(row + 1, this._cursor);
         } else {
            this.setTopRow(this._topRow, this._cursor);
         }
      }

      this.updateScrollbar();
      this.calcFocusRect(moveFocus);
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      if (this._size != 0 && (status & 65536) == 0) {
         this.setMuddy(false);
         if ((status & 1) == 0 || this.isStyle(8)) {
            int desiredAnchor = this._cursor + amount;
            int oldCursor = this._cursor;
            this._cursor = MathUtilities.clamp(0, desiredAnchor, this._size - 1);
            amount = desiredAnchor - this._cursor;
            if ((status & 2) != 0) {
               if (this.isStyle(2)) {
                  boolean wasMultiple = this._selectionRange != 0;
                  this._selectionRange = this._selectionRange + (oldCursor - this._cursor);
                  if (this._selectionRange != 0 || wasMultiple) {
                     int start = Math.min(oldCursor, this._cursor + 1);
                     int end = Math.max(oldCursor, this._cursor - 1);
                     this.invalidateRange(start, end);
                  }
               } else {
                  this._selectionRange = 0;
               }

               amount = 0;
            } else {
               if (this._selectionRange != 0) {
                  int start = Math.min(oldCursor + this._selectionRange, oldCursor);
                  int end = Math.max(oldCursor + this._selectionRange, oldCursor);
                  this.invalidateRange(start, end);
               }

               this._selectionRange = 0;
            }
         } else if ((status & 1) != 0) {
            if (this._selectionRange != 0) {
               int start = Math.min(this._cursor + this._selectionRange, this._cursor);
               int end = Math.max(this._cursor + this._selectionRange, this._cursor);
               this.invalidateRange(start, end);
               this._selectionRange = 0;
            }

            int firstVisibleLine = this._topRow;
            int visibleLines = this.getVisibleLinesPageDown(firstVisibleLine);
            if (amount < 0) {
               if (this.isOnTopRow(this._cursor)) {
                  this._cursor = this._cursor - this.getVisibleLinesPageUp(this._cursor);
                  this._cursor = Math.max(0, this._cursor);
               } else {
                  this._cursor = firstVisibleLine;
               }
            } else {
               if (this._cursor == firstVisibleLine + visibleLines - 1) {
                  this._cursor = this._cursor + this.getVisibleLinesPageDown(this._cursor);
               } else {
                  this._cursor = firstVisibleLine + visibleLines - 1;
               }

               this._cursor = Math.min(this._cursor, this._size - 1);
            }

            amount = 0;
         }

         this.makeFocusVisible(false);
         return amount;
      } else {
         return amount;
      }
   }

   @Override
   protected void onFocus(int direction) {
      if (this._cursor < 0) {
         if (direction > 0) {
            this._cursor = Math.min(0, this._size - 1);
            this._topRow = this._cursor;
            this.updateScrollbar();
         } else if (direction != 0) {
            this._cursor = this._size - 1;
            this._topRow = this._cursor - this.getVisibleLinesPageUp(this._cursor);
            this.updateScrollbar();
         }
      }

      this.calcFocusRect(false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void paint(Graphics graphics) {
      int width = this.getContentWidth();
      if (this._size == 0) {
         int availableWidth = Math.min(width, this.getManager().getVisibleWidth()) - 1;
         graphics.drawText(this.getEmptyString(), 0, 0, this._emptyStyle, availableWidth);
      } else if (this._callback != null) {
         XYRect redrawRect = graphics.getClippingRect();
         if (redrawRect.y < 0) {
            throw new IllegalStateException("Clipping rectangle is wrong.");
         }

         int selectionStart = Math.min(this._cursor, this._cursor + this._selectionRange);
         int selectionEnd = Math.max(this._cursor, this._cursor + this._selectionRange);
         int y = 0;
         int endY = redrawRect.y + redrawRect.height;
         this.setThemeAttributesSpecial(null, null);

         for (int line = this._topRow; line < this._size && y < endY; line++) {
            int rowHeight = this.getRowHeight(line);
            if (y + rowHeight <= redrawRect.y) {
               y += rowHeight;
            } else {
               boolean var14 = false /* VF: Semaphore variable */;

               try {
                  var14 = true;
                  graphics.pushContext(0, y, width, rowHeight, 0, 0);
                  if (!graphics.isDrawingStyleSet(8)) {
                     this.setThemeAttributesSpecial((line & 1) == 0 ? this._tasRowEven : this._tasRowOdd, graphics);
                  }

                  boolean select = selectionStart <= line && line <= selectionEnd && line != this._cursor;
                  if (select) {
                     graphics.setDrawingStyle(16, true);
                     graphics.setColor(ThemeAttributeSet.getColor(this, 5));
                     graphics.setBackgroundColor(ThemeAttributeSet.getColor(this, 4));
                  } else {
                     graphics.setDrawingStyle(16, false);
                  }

                  if (select || this.getThemeAttributeSetSpecial() != null) {
                     int fg = graphics.getColor();
                     graphics.setColor(graphics.getBackgroundColor());
                     graphics.fillRect(0, y, width, rowHeight);
                     graphics.setColor(fg);
                  }

                  this._callback.drawListRow(this, graphics, line, y, width);
                  var14 = false;
               } finally {
                  if (var14) {
                     this.setThemeAttributesSpecial(null, null);
                     graphics.popContext();
                     y += rowHeight;
                  }
               }

               this.setThemeAttributesSpecial(null, null);
               graphics.popContext();
               y += rowHeight;
            }
         }

         this.paintVerticalScrollbar(graphics);
      }
   }

   private void paintVerticalScrollbar(Graphics graphics) {
      if (this._upScrollArrowVisible) {
         if (this._upScrollArrow != null) {
            Manager manager = this.getManager();
            int width = manager.getWidth();
            int x = width - this._upScrollArrow.getWidth();
            graphics.setOverlay(0, this._upScrollArrow, x, 0);
         }
      } else {
         graphics.setOverlay(0, null, 0, 0);
      }

      if (this._downScrollArrowVisible) {
         if (this._downScrollArrow != null) {
            Manager manager = this.getManager();
            int width = manager.getWidth();
            int x = width - this._downScrollArrow.getWidth();
            int y = manager.getHeight() - this._downScrollArrow.getHeight();
            graphics.setOverlay(1, this._downScrollArrow, x, y);
            return;
         }
      } else {
         graphics.setOverlay(1, null, 0, 0);
      }
   }

   protected void scrollRegionVertically(Graphics graphics, int y, int height, int dy, int oldTopRow) {
      graphics.copyArea(0, y, this.getContentWidth(), height, 0, dy);
   }

   public void setCallback(VariableHeightListFieldCallback callback) {
      this._callback = callback;
   }

   public void setEmptyString(ResourceBundleFamily family, int id, int style) {
      if (family != null) {
         family.getString(id);
         this.setEmptyStringFamily(family, id);
      } else {
         this.setEmptyStringFamily(CommonResource.getBundle(), 1012);
      }

      this._emptyStyle = style;
   }

   public void setEmptyString(String emptyString, int style) {
      this._rbId = 0;
      this._rbName = null;
      this._emptyString = emptyString;
      this._emptyStyle = style;
   }

   private void setEmptyStringFamily(ResourceBundleFamily rb, int key) {
      this._rbId = rb.getId();
      this._rbName = rb.getName();
      this._rbKey = key;
      this._cachedLocaleCode = 0;
   }

   public void setSelectedIndex(int index) {
      this.setSelectedIndex(index, false);
   }

   public void setSelectedIndex(int index, boolean scrollToTop) {
      if (this._cursor >= 0 && this._selectionRange != 0) {
         int topIndex = this._selectionRange > 0 ? this._cursor : this._cursor + this._selectionRange;
         int bottomIndex = this._selectionRange > 0 ? this._cursor + this._selectionRange : this._cursor;
         this.invalidateRange(topIndex, bottomIndex);
      }

      this.calcSelectedIndex(index, scrollToTop, true);
      this.updateScrollbar();
      this.makeFocusVisible(true);
   }

   public void setSize(int size) {
      this.setSize(size, 0);
   }

   public void setSize(int size, int focusRow) {
      boolean sizeChange = this._size != size;
      this._size = size;
      this.calcSelectedIndex(focusRow);
      this.calcFocusRect(false);
      this.fieldChangeNotify(Integer.MIN_VALUE);
      Manager manager = this.getManager();
      if (manager != null && manager.isValidLayout()) {
         if (sizeChange && this._layoutOnSizeChange) {
            this.updateLayout();
         }

         this.invalidate();
         this.focusAdd(false);
      }

      this.updateScrollbar();
   }

   protected void setTopRow(int topRow, int cursor) {
      if (topRow != this._topRow) {
         Graphics graphics = this.getGraphics0();
         int oldTopRow = this._topRow;
         int oldBottomVisible = this.getLastVisibleRow();
         this._topRow = topRow;
         int newBottomVisible = this.getLastVisibleRow();
         this._topRow = oldTopRow;
         if (topRow < oldTopRow && newBottomVisible >= oldTopRow) {
            int height = this.getYForRow(newBottomVisible + 1) + this.getRowHeight(newBottomVisible + 1);
            this._topRow = topRow;
            int newY = this.getYForRow(oldTopRow);
            this.scrollRegionVertically(graphics, 0, height, newY, oldTopRow);
            this.invalidate(0, 0, this.getContentWidth(), newY);
            int selectionStart = Math.min(this._cursor, this._cursor + this._selectionRange);
            int selectionEnd = Math.max(this._cursor, this._cursor + this._selectionRange);
            if (selectionStart < oldTopRow && selectionEnd >= oldTopRow || selectionStart <= newBottomVisible && selectionEnd > newBottomVisible) {
               this.invalidate();
            }
         } else if (newBottomVisible > oldBottomVisible && topRow < oldBottomVisible) {
            int oldY = this.getYForRow(topRow);
            int contentHeight = this.getContentHeight();
            int height = contentHeight - oldY;
            this._topRow = topRow;
            this.scrollRegionVertically(graphics, oldY, height, -oldY, oldTopRow);
            int actualHeight = this.getManager().getHeight();
            this.invalidate(0, actualHeight - oldY, this.getContentWidth(), contentHeight);
            int selectionStart = Math.min(this._cursor, this._cursor + this._selectionRange);
            int selectionEnd = Math.max(this._cursor, this._cursor + this._selectionRange);
            if (selectionStart < topRow && selectionEnd >= topRow || selectionStart <= oldBottomVisible && selectionEnd > oldBottomVisible) {
               this.invalidate();
            }
         } else {
            this._topRow = topRow;
            this.invalidate();
         }

         if (newBottomVisible >= this._size - 1) {
            int lastRow = this._size - 1;
            int lastY = this.getYForRow(lastRow);
            int lastHeight = this.getRowHeight(lastRow);
            int contentHeight = this.getContentHeight();
            if (lastY + lastHeight < contentHeight) {
               this.invalidate(0, lastY, this.getContentWidth(), contentHeight);
            }
         }
      }
   }

   private void updateScrollbar() {
      if (this.getManager() != null) {
         this._upScrollArrowVisible = this._topRow != 0;
         this._downScrollArrowVisible = this._topRow + this.getVisibleLinesPageDown(this._topRow) != this._size;
      }
   }

   private static long validateStyle(long style) {
      if ((style & 54043195528445952L) == 0) {
         style |= 18014398509481984L;
      }

      return style;
   }
}
