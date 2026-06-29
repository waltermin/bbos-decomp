package net.rim.device.api.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.ui.Cursor;
import net.rim.device.internal.ui.RichText;
import net.rim.device.internal.ui.component.Scrollbar;
import net.rim.vm.Array;

public class Manager extends Field {
   private int _verticalQuanta = 1;
   private int _horizontalQuanta = 1;
   private int _virtualWidth;
   private int _virtualHeight;
   private int _horizontalScroll;
   private int _verticalScroll;
   private Field[] _fields = new Field[5];
   private int _fieldsCount;
   private Field _fieldWithFocus;
   private int _fieldWithFocusIndex = -1;
   private boolean _validLayout;
   private int _indicatorX;
   private int _indicatorY;
   private int _indicatorWidth;
   private int _indicatorHeight;
   private int _scrollBarX;
   private int _scrollBarY;
   private int _scrollBarWidth;
   private int _scrollBarHeight;
   private int _stylusDownY;
   private int _stylusDownVerticalScroll;
   private boolean _stylusDraggingIndicator;
   private boolean _nonfocusableArrowOverride;
   private ScrollChangeListener _scrollListener;
   private Scrollbar _verticalScrollbar;
   private Scrollbar _horizontalScrollbar;
   private static final boolean haveStylus = Ui.isStylusSupported();
   private static Tag TAG = Tag.create("manager");
   private static Tag THEME_SCROLLBAR_TAG = Tag.create("themed-scrollbar");
   protected static final long VERTICAL_SCROLL_MASK = 844424930131968L;
   public static final long VERTICAL_SCROLL = 281474976710656L;
   public static final long NO_VERTICAL_SCROLL = 562949953421312L;
   protected static final long HORIZONTAL_SCROLL_MASK = 3377699720527872L;
   public static final long HORIZONTAL_SCROLL = 1125899906842624L;
   public static final long NO_HORIZONTAL_SCROLL = 2251799813685248L;
   protected static final long VERTICAL_SCROLLBAR_MASK = 52776558133248L;
   public static final long VERTICAL_SCROLLBAR = 17592186044416L;
   public static final long NO_VERTICAL_SCROLLBAR = 35184372088832L;
   protected static final long HORIZONTAL_SCROLLBAR_MASK = 211106232532992L;
   public static final long HORIZONTAL_SCROLLBAR = 70368744177664L;
   public static final long NO_HORIZONTAL_SCROLLBAR = 140737488355328L;
   public static final long LEAVE_BLANK_SPACE = 288230376151711744L;
   public static final long INCREMENTAL_LAYOUT = 576460752303423488L;
   public static final long NO_SCROLL_RESET = 144115188075855872L;
   public static final long SCROLLBAR_ELEVATOR = 8796093022208L;
   public static final long USE_ALL_SCREEN_HEIGHT = 4611686018427387904L;
   private static final long MANAGER_STYLES = 5624987138492727296L;
   public static final int UPWARD = 256;
   public static final int DOWNWARD = 512;
   public static final int LEFTWARD = 1024;
   public static final int RIGHTWARD = 2048;
   public static final int TOPMOST = 1;
   public static final int BOTTOMMOST = 2;
   public static final int LEFTMOST = 4;
   public static final int RIGHTMOST = 8;
   public static final int QUANTA_FONT = -1;
   private static final int INDICATOR_WIDTH = haveStylus ? 6 : 3;
   private static final int SCROLLBAR_WIDTH = INDICATOR_WIDTH;
   private static final int SCROLLBAR_GAP = 2;
   private static final int SCROLLBAR_TOTAL_WIDTH = Math.max(INDICATOR_WIDTH, SCROLLBAR_WIDTH) + 2;
   private static final int SCROLLBAR_TOTAL_HEIGHT = SCROLLBAR_TOTAL_WIDTH;
   private static final int MIN_INDICATOR_HEIGHT = 6;
   private static final int OUTSIDE_SCROLLBAR = 0;
   private static final int ABOVE_INDICATOR = 1;
   private static final int ON_INDICATOR = 2;
   private static final int BELOW_INDICATOR = 3;

   protected Manager(long style) {
      super(validateStyle(style));
      if (!this.validateFieldStyle(style)) {
         throw new IllegalArgumentException();
      }

      this.setTag(TAG);
   }

   @Override
   public boolean acceptVisitor(FieldVisitor visitor) {
      if (!visitor.visit(this, 1)) {
         return false;
      }

      int total = this.getFieldCount();

      for (int i = 0; i < total; i++) {
         if (!this.getField(i).acceptVisitor(visitor)) {
            return false;
         }
      }

      return visitor.visit(this, 2);
   }

   public void add(Field field) {
      this.insertInternal(field, this._fieldsCount);
   }

   @Override
   protected void applyFont() {
      super.applyFont();

      for (int lv = this._fieldsCount - 1; lv >= 0; lv--) {
         this._fields[lv].applyFont();
      }
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();

      for (int lv = this._fieldsCount - 1; lv >= 0; lv--) {
         this._fields[lv].applyTheme();
      }
   }

   protected int calculateHorizontalScrollAmount(XYRect region) {
      if (!this.isStyle(1125899906842624L)) {
         return 0;
      }

      int width = this.getContentWidth();
      int xAmount = 0;
      int delta = region.x + region.width - (this.getHorizontalScroll() + width);
      if (delta > 0) {
         xAmount += delta;
      }

      delta = region.x - (this.getHorizontalScroll() + xAmount);
      if (delta < 0) {
         int deltaDelta = this.getHorizontalScroll() + xAmount + delta;
         if (deltaDelta < 0) {
            delta -= deltaDelta;
         }

         xAmount += delta;
      }

      return xAmount;
   }

   protected int calculateVerticalScrollAmount(XYRect region) {
      if (!this.isStyle(281474976710656L)) {
         return 0;
      }

      int height = this.getContentHeight();
      int yAmount = 0;
      int delta = region.y + region.height - (this.getVerticalScroll() + height);
      if (delta > 0) {
         yAmount += delta;
      }

      delta = region.y - (this.getVerticalScroll() + yAmount);
      if (delta < 0) {
         int deltaDelta = this.getVerticalScroll() + yAmount + delta;
         if (deltaDelta < 0) {
            delta -= deltaDelta;
         }

         yAmount += delta;
      }

      return yAmount;
   }

   @Override
   void callOnDisplayOrUndisplay(boolean attached) {
      super.callOnDisplayOrUndisplay(attached);

      for (int i = 0; i < this._fieldsCount; i++) {
         this._fields[i].callOnDisplayOrUndisplay(attached);
      }
   }

   public void delete(Field field) {
      int index = field.getIndex();
      if (field.getManager() != this) {
         throw new IllegalArgumentException("Attempt to delete a field that doesn't belong to the manager.");
      }

      this.assertHaveEventLock();
      this.notifyVisibilityChange(index, 1, false);
      if (this.isValidLayout() && this._fieldWithFocus == field) {
         this._fieldWithFocus.onUnfocus();
         Field dummy = new NullField();
         this._fieldWithFocus = dummy;
         this._fields[index] = dummy;
         dummy.setManager(this, index);
         field.setManager(null, -1);
         this.getScreen().findNewFocus(true);
         this.removeField(index, dummy);
      } else {
         if (this._fieldWithFocus == field) {
            field.onUnfocus();
            Field dummy = new NullField();
            this._fieldWithFocus = dummy;
            this._fields[index] = dummy;
            dummy.setManager(this, index);
            field.setManager(null, -1);
            field = dummy;
            this.getScreen().findNewFocus(true);
         }

         this.removeFieldHelper(index, field);
      }

      this.runLayoutUpdate(index, 0, 1);
   }

   public void deleteAll() {
      this.deleteRange(0, this.getFieldCount());
   }

   public void deleteRange(int start, int count) {
      this.assertHaveEventLock();
      Field dummy = null;
      int originalStart = start;
      int originalCount = count;
      if (count != 0) {
         if (start >= 0 && count >= 0 && start + count <= this._fieldsCount) {
            this.notifyVisibilityChange(start, count, false);
            if (this._fieldWithFocusIndex >= start && this._fieldWithFocusIndex < start + count) {
               this._fieldWithFocus.onUnfocus();
               dummy = new NullField();
               dummy.setManager(this, start);
               this._fields[start].setManager(null, -1);
               this._fieldWithFocus = dummy;
               this._fieldWithFocusIndex = start;
               this._fields[start] = dummy;
               start++;
               count--;
            }

            for (int i = start; i < start + count; i++) {
               this._fields[i].setManager(null, -1);
            }

            for (int i = start + count; i < this._fieldsCount; i++) {
               this._fields[i].setIndex(i - count);
            }

            System.arraycopy(this._fields, start + count, this._fields, start, this._fieldsCount - start - count);

            for (int i = this._fieldsCount - count; i < this._fieldsCount; i++) {
               this._fields[i] = null;
            }

            if (this._fieldWithFocusIndex > start) {
               this._fieldWithFocusIndex -= count;
            }

            this._fieldsCount -= count;
            if (dummy != null) {
               this.getScreen().findNewFocus(true);
               this.removeField(start - 1, dummy);
            }

            this.runLayoutUpdate(originalStart, 0, originalCount);
         } else {
            throw new IndexOutOfBoundsException("Invalid start and count.");
         }
      }
   }

   @Override
   void doVisibilityWalk(boolean visible) {
      this.onVisibilityChange(visible);

      for (int i = 0; i < this._fieldsCount; i++) {
         this._fields[i].doVisibilityWalk(visible);
      }
   }

   private void ensureFieldCapacity() {
      int capacity = this._fields.length;
      if (this._fieldsCount == capacity) {
         Array.resize(this._fields, capacity * 2);
      }
   }

   @Override
   void getDebugTreeHelper(int treeStyle, StringBuffer buffer, int indent) {
      super.getDebugTreeHelper(treeStyle, buffer, indent);
      if (this instanceof Screen) {
         Screen screen = (Screen)this;
         screen.getDelegate().getDebugTreeHelper(treeStyle, buffer, indent);
      } else {
         indent++;

         for (int index = 0; index < this.getFieldCount(); index++) {
            this.getField(index).getDebugTreeHelper(treeStyle, buffer, indent);
         }
      }
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      return this.getField(index);
   }

   @Override
   public int getAccessibleChildCount() {
      return this.getFieldWithFocus().getAccessibleSelectionAt(0) != null ? this.getFieldWithFocus().getAccessibleChildCount() : this.getFieldCount();
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      return this.getFieldWithFocus().getAccessibleSelectionAt(index) != null
         ? this.getFieldWithFocus().getAccessibleSelectionAt(index)
         : this.getFieldWithFocus();
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      return this.getFieldWithFocus().getAccessibleSelectionAt(0) != null
         ? this.getFieldWithFocus().isAccessibleChildSelected(index)
         : this.getFieldWithFocusIndex() == index;
   }

   public Field getField(int index) {
      if (index >= this._fieldsCount) {
         throw new IndexOutOfBoundsException();
      } else {
         return this._fields[index];
      }
   }

   public int getFieldAtLocation(int x, int y) {
      for (int lv = this._fieldsCount - 1; lv >= 0; lv--) {
         Field field = this._fields[lv];
         XYRect rect = field.getExtent();
         if (rect.y <= y && rect.x <= x && y <= rect.y + rect.height && x <= rect.x + rect.width) {
            return lv;
         }
      }

      return -1;
   }

   public int getFieldClosestToLocation(int x, int y, int status) {
      int start = 0;
      int end = this._fieldsCount;
      int increment = 1;
      int search = 0;
      if ((status & 256) != 0 || (status & 1024) != 0) {
         start = this._fieldsCount - 1;
         end = -1;
         increment = -1;
         search = -1;
      } else if ((status & 512) != 0 || (status & 2048) != 0) {
         search = 1;
      }

      int index = -1;
      int deltaY = Integer.MAX_VALUE;
      int deltaX = Integer.MAX_VALUE;

      for (int lv = start; lv != end; lv += increment) {
         Field field = this._fields[lv];
         XYRect rect = field.getExtent();
         if (rect.y <= y && y <= rect.y + rect.height) {
            deltaY = 0;
            if (rect.x <= x && x <= rect.x + rect.width) {
               return lv;
            }

            int deltaXNew = Math.abs(rect.x - x);
            if (deltaXNew < deltaX) {
               deltaX = deltaXNew;
               index = lv;
            }
         } else {
            int deltaYNew;
            if (search == -1) {
               deltaYNew = y - rect.y;
            } else if (search == 1) {
               deltaYNew = rect.y - y;
            } else {
               deltaYNew = Math.abs(rect.y - y);
            }

            if (deltaYNew >= 0 && deltaYNew < deltaY) {
               deltaY = deltaYNew;
               deltaX = Math.abs(rect.x - x);
               index = lv;
            } else if (deltaYNew == deltaY) {
               int deltaXNew = Math.abs(rect.x - x);
               if (deltaXNew < deltaX) {
                  deltaX = deltaXNew;
                  index = lv;
               }
            }
         }
      }

      return index;
   }

   public int getFieldCount() {
      return this._fieldsCount;
   }

   public Field getFieldWithFocus() {
      return this._fieldWithFocus;
   }

   public int getFieldWithFocusIndex() {
      return this._fieldWithFocusIndex;
   }

   @Override
   public void getFocusRect(XYRect rect) {
      if (this._fieldWithFocus != null) {
         this._fieldWithFocus.getFocusRect(rect);
         rect.translate(this._fieldWithFocus.getContentLeft() - this.getHorizontalScroll(), this._fieldWithFocus.getContentTop() - this.getVerticalScroll());
      } else {
         rect.set(this.getHorizontalScroll(), this.getVerticalScroll(), this.getContentWidth(), this.getContentHeight());
      }
   }

   @Override
   public void getFocusRectPhantom(XYRect rect) {
      if (this._fieldWithFocus != null) {
         this._fieldWithFocus.getFocusRectPhantom(rect);
         rect.translate(this._fieldWithFocus.getContentLeft() - this.getHorizontalScroll(), this._fieldWithFocus.getContentTop() - this.getVerticalScroll());
      } else {
         rect.set(this.getHorizontalScroll(), this.getVerticalScroll(), this.getContentWidth(), this.getContentHeight());
      }
   }

   @Override
   Graphics getGraphics0(XYRect clip, boolean drawBackground) {
      Graphics graphics = this.getManager().getGraphics0(clip, drawBackground && this.isFieldTransparent());
      graphics.pushRegion(this.getContentRect(), -this.getHorizontalScroll(), -this.getVerticalScroll());
      this.applyTheme(graphics, drawBackground);
      return graphics;
   }

   public int getHorizontalQuantization() {
      return this._horizontalQuanta;
   }

   public final int getHorizontalScroll() {
      return this._horizontalScroll;
   }

   @Override
   public Field getLeafFieldWithFocus() {
      return this._fieldWithFocus == null ? null : this._fieldWithFocus.getLeafFieldWithFocus();
   }

   public int getNavigationAxis(int status) {
      if ((status & 536870912) != 0) {
         return (status & 65536) != 0 ? 1 : 2;
      } else {
         return (status & 1) == 0 ? 0 : 2;
      }
   }

   protected final int getPreferredHeightOfChild(Field field) {
      return field.getPreferredHeight() + field.getPaddingTop() + field.getPaddingBottom() + field.getBorderTop() + field.getBorderBottom();
   }

   protected final int getPreferredWidthOfChild(Field field) {
      int preferredWidth = field.getPreferredWidth() + field.getPaddingRight() + field.getPaddingLeft() + field.getBorderRight() + field.getBorderLeft();
      if (this._verticalScrollbar != null) {
         preferredWidth -= SCROLLBAR_TOTAL_WIDTH;
      }

      return preferredWidth;
   }

   public int getVerticalQuantization() {
      return this._verticalQuanta;
   }

   public final int getVerticalScroll() {
      return this._verticalScroll;
   }

   public final int getVirtualHeight() {
      return this._virtualHeight;
   }

   public final int getVirtualWidth() {
      return this._virtualWidth;
   }

   public int getVisibleHeight() {
      return this.getVisibleHeight(Integer.MAX_VALUE, 0);
   }

   int getVisibleHeight(int min, int y) {
      if (0 > y) {
         min += y;
         y = 0;
      }

      if (this.getContentHeight() < min + y) {
         min = this.getContentHeight() - y;
      }

      return this.getManager().getVisibleHeight(min, y + this.getContentTop());
   }

   public int getVisibleWidth() {
      return this.getVisibleWidth(Integer.MAX_VALUE, 0);
   }

   int getVisibleWidth(int min, int x) {
      if (0 > x) {
         min += x;
         x = 0;
      }

      if (this.getContentWidth() < min + x) {
         min = this.getContentWidth() - x;
      }

      return this.getManager().getVisibleWidth(min, x + this.getContentLeft());
   }

   private int getXForScrollArrowLeft() {
      return this.getHorizontalScroll() - this.getPaddingLeft();
   }

   private int getXForScrollArrowRight(int width) {
      int xPosition = this.getHorizontalScroll() + this.getContentWidth() + this.getPaddingRight();
      return xPosition - width;
   }

   protected boolean incrementalLayout(int index, int added, int deleted) {
      return false;
   }

   public void insert(Field field, int index) {
      this.insertInternal(field, index);
   }

   private void insertInternal(Field field, int index) {
      if (index < 0 || index > this._fieldsCount) {
         throw new IndexOutOfBoundsException("Manager.insert called with an invalid index.");
      }

      if (this == field) {
         throw new IllegalArgumentException();
      }

      field.setManager(this, index);
      this.ensureFieldCapacity();

      for (int i = index; i < this._fieldsCount; i++) {
         this._fields[i].setIndex(i + 1);
      }

      System.arraycopy(this._fields, index, this._fields, index + 1, this._fieldsCount - index);
      this._fields[index] = field;
      this._fieldsCount++;
      if (index <= this._fieldWithFocusIndex) {
         this._fieldWithFocusIndex++;
      }

      if (this.isValidLayout() && this.getScreen() != null) {
         field.applyTheme();
         field.applyFont();
      }

      this.runLayoutUpdate(index, 1, 0);
      this.notifyVisibilityChange(index, 1, true);
   }

   @Override
   public void invalidate() {
      int widthAdjust = 0;
      int heightAdjust = 0;
      if (this._verticalScrollbar != null) {
         widthAdjust = SCROLLBAR_TOTAL_WIDTH;
      }

      if (this._horizontalScrollbar != null) {
         heightAdjust = SCROLLBAR_TOTAL_HEIGHT;
      }

      this.invalidate(this.getHorizontalScroll(), this.getVerticalScroll(), this.getWidth() + widthAdjust, this.getHeight() + heightAdjust);
   }

   @Override
   protected void invalidate(int x, int y, int width, int height) {
      super.invalidate(x - this.getHorizontalScroll(), y - this.getVerticalScroll(), width, height);
   }

   protected void invalidateFieldRange(int lower, int upper) {
      lower = lower < 0 ? 0 : lower;
      upper = upper < this._fieldsCount ? upper + 1 : this._fieldsCount;

      for (int i = lower; i < upper; i++) {
         this._fields[i].invalidate();
      }
   }

   @Override
   void invalidateLayout0() {
      this.setValidLayout(false);

      for (int i = 0; i < this._fieldsCount; i++) {
         this._fields[i].invalidateLayout0();
      }
   }

   @Override
   protected boolean invokeAction(int action) {
      return this._fieldWithFocus != null ? this._fieldWithFocus.invokeAction(action) : false;
   }

   @Override
   public boolean isDataValid() {
      for (int i = 0; i < this._fieldsCount; i++) {
         if (!this._fields[i].isDataValid()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean isDirty() {
      for (int i = 0; i < this._fieldsCount; i++) {
         if (this._fields[i].isDirty()) {
            return true;
         }
      }

      return false;
   }

   protected boolean isDownArrowShown() {
      return this.getVerticalScroll() + this.getContentHeight() < this._virtualHeight;
   }

   @Override
   public boolean isFocusable() {
      for (int i = this._fieldsCount - 1; i >= 0; i--) {
         if (this._fields[i].isFocusable()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isMuddy() {
      for (int i = 0; i < this._fieldsCount; i++) {
         if (this._fields[i].isMuddy()) {
            return true;
         }
      }

      return false;
   }

   protected boolean isScrollCopyable() {
      return !this.isFieldTransparent();
   }

   @Override
   public boolean isSelecting() {
      return this._fieldWithFocus != null ? this._fieldWithFocus.isSelecting() : false;
   }

   protected boolean isUpArrowShown() {
      return this.getVerticalScroll() > 0;
   }

   public final boolean isValidLayout() {
      return this._validLayout;
   }

   @Override
   protected boolean keyChar(char ch, int status, int time) {
      boolean handled = super.keyChar(ch, status, time);
      if (this._fieldWithFocus != null && !handled) {
         handled = this._fieldWithFocus.keyChar(ch, status, time);
      }

      return handled;
   }

   @Override
   protected boolean keyControl(char ch, int status, int time) {
      boolean handled = super.keyControl(ch, status, time);
      if (this._fieldWithFocus != null && !handled) {
         handled = this._fieldWithFocus.keyControl(ch, status, time);
      }

      return handled;
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      boolean handled = super.keyDown(keycode, time);
      if (this._fieldWithFocus != null && !handled) {
         handled = this._fieldWithFocus.keyDown(keycode, time);
      }

      return handled;
   }

   @Override
   protected boolean keyRepeat(int keycode, int time) {
      boolean handled = super.keyRepeat(keycode, time);
      if (this._fieldWithFocus != null && !handled) {
         handled = this._fieldWithFocus.keyRepeat(keycode, time);
      }

      return handled;
   }

   @Override
   protected boolean keyStatus(int keycode, int time) {
      boolean handled = super.keyStatus(keycode, time);
      if (this._fieldWithFocus != null && !handled) {
         handled = this._fieldWithFocus.keyStatus(keycode, time);
      }

      return handled;
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      boolean handled = super.keyUp(keycode, time);
      if (this._fieldWithFocus != null && !handled) {
         handled = this._fieldWithFocus.keyUp(keycode, time);
      }

      return handled;
   }

   @Override
   protected final void layout(int width, int height) {
      this._virtualHeight = -1;
      int widthAdjust = 0;
      int heightAdjust = 0;
      long temp = this.getStyle();
      Theme theme = ThemeManager.getActiveTheme();
      if (theme.getAttributeSet(THEME_SCROLLBAR_TAG) != null) {
         if (this.isStyle(17592186044416L) && (temp & 281474976710656L) > 0) {
            widthAdjust = SCROLLBAR_TOTAL_WIDTH;
            this._verticalScrollbar = new Scrollbar(true);
            this._verticalScrollbar.setManager(this, 0);
            if (this._scrollListener == null) {
               this._verticalScrollbar.setClient(this);
            }
         }

         if (this.isStyle(70368744177664L) && (temp & 1125899906842624L) > 0) {
            heightAdjust = SCROLLBAR_TOTAL_HEIGHT;
            this._horizontalScrollbar = new Scrollbar(true, true);
            this._horizontalScrollbar.setManager(this, 0);
            if (this._scrollListener == null) {
               this._horizontalScrollbar.setClient(this);
            }
         }
      }

      this.sublayout(width - widthAdjust, height - heightAdjust);
      if (this._verticalScrollbar != null) {
         XYRect tmp = this.getExtent();
         this.setExtent(tmp.width + widthAdjust, tmp.height);
      }

      if (this._horizontalScrollbar != null) {
         XYRect tmp = this.getExtent();
         this.setExtent(tmp.width, tmp.height + heightAdjust);
      }

      if (this._verticalQuanta != 1) {
         int denominator = this._verticalQuanta == -1 ? this.getFont().getHeight() : this._verticalQuanta;
         int remainder = this.getContentHeight() % denominator;
         this.setExtent(this.getContentWidth(), this.getContentHeight() - remainder);
      }

      if (this._horizontalQuanta != 1) {
         int denominator = this._horizontalQuanta == -1 ? this.getFont().getHeight() : this._horizontalQuanta;
         int remainder = this.getContentWidth() % denominator;
         this.setExtent(this.getContentWidth() - remainder, this.getContentHeight());
      }

      if (this._virtualHeight == -1) {
         this.setVirtualExtent(this.getContentWidth(), this.getContentHeight());
      }

      this.removeBlankSpace();
      this.setValidLayout(true);
   }

   protected final void layoutChild(Field field, int width, int height) {
      if (field.getManager() != this) {
         throw new IllegalArgumentException("Field is not a child of this manager.");
      }

      width -= field.getBorderLeft() + field.getBorderRight() + field.getPaddingLeft() + field.getPaddingRight();
      height -= field.getBorderTop() + field.getBorderBottom() + field.getPaddingTop() + field.getPaddingBottom();
      if (height >= 0) {
         field.layout(width, height);
      } else {
         System.out.println("WARNING: Cannot layout field, insufficient height");
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      boolean notify = false;
      int axis = this.getNavigationAxis(status);

      label74:
      while (amount != 0) {
         if (this._fieldWithFocus == null) {
            return amount;
         }

         int remaining = this._fieldWithFocus.moveFocus(amount, status, time);
         if ((status & 8) != 0) {
            remaining = 0;
         }

         if (remaining != amount) {
            if (!(this._fieldWithFocus instanceof Manager)) {
               this._fieldWithFocus.focusChangeNotify(2);
            }

            if (remaining == 0) {
               amount = 0;
               break;
            }
         }

         amount = remaining;
         int oldIndex = this._fieldWithFocusIndex;
         Field oldFocus = this._fieldWithFocus;
         int amountSign = MathUtilities.clamp(-1, amount, 1);

         do {
            this._fieldWithFocusIndex = this.nextFocus(amountSign, axis);
            if (this._fieldWithFocusIndex == -1 || this._fieldWithFocusIndex == oldIndex) {
               this._fieldWithFocusIndex = oldIndex;
               this._fieldWithFocus = oldFocus;
               break label74;
            }

            this._fieldWithFocus = this._fields[this._fieldWithFocusIndex];
         } while (!this._fieldWithFocus.isFocusable());

         oldFocus.onUnfocus();
         oldFocus.focusChangeNotify(3);
         this._fieldWithFocus.onFocus(amountSign);
         this._fieldWithFocus.focusChangeNotify(1);
         amount -= amountSign;
         notify = true;
      }

      if (notify) {
         this.focusChangeNotify(2);
      }

      if (amount != 0 && !this.isStyle(144115188075855872L)) {
         if (amount < 0) {
            if (this.getVerticalScroll() > 0 && axis != 1) {
               XYRect rect = Ui.getTmpXYRect();
               rect.set(this.getHorizontalScroll(), 0, 0, 0);
               this.makeRegionVisible(true, rect, true);
               Ui.returnTmpXYRect(rect);
               return amount;
            }

            if (this.getHorizontalScroll() > 0 && axis == 1) {
               XYRect rect = Ui.getTmpXYRect();
               rect.set(0, this.getVerticalScroll(), 0, 0);
               this.makeRegionVisible(true, rect, true);
               Ui.returnTmpXYRect(rect);
               return amount;
            }
         } else if (this.getVerticalScroll() + this.getContentHeight() < this._virtualHeight && axis != 1) {
            XYRect rect = Ui.getTmpXYRect();
            rect.set(this.getHorizontalScroll(), this._virtualHeight, 0, 0);
            this.makeRegionVisible(true, rect, true);
            Ui.returnTmpXYRect(rect);
         }
      }

      return amount;
   }

   private void notifyVisibilityChange(int start, int count, boolean visible) {
      if (this.isValidLayout()) {
         Screen scr = this.getScreen();
         if (scr != null) {
            if (scr.isDisplayed()) {
               if (visible) {
                  for (int i = start; i < start + count; i++) {
                     this._fields[i].callOnDisplayOrUndisplay(true);
                  }
               } else {
                  for (int i = start; i < start + count; i++) {
                     this._fields[i].callOnDisplayOrUndisplay(false);
                  }
               }
            }

            if (scr.isVisible()) {
               for (int i = start; i < start + count; i++) {
                  this._fields[i].doVisibilityWalk(visible);
               }
            }
         }
      }
   }

   @Override
   protected void paint(Graphics graphics) {
      if (this.isOkayForPainting()) {
         this.subpaint(graphics);
         if (this.isOkayForPainting()) {
            this.paintScrollbars(graphics);
         }
      }
   }

   private boolean isOkayForPainting() {
      UiEngineImpl engine = UiEngineImpl.getUiEngine();
      return engine == null || engine.getGlobalScreen() != null || engine.getApplication().isForeground();
   }

   protected final void paintChild(Graphics graphics, Field field) {
      field.paintSelf(graphics, true, 0, 0);
   }

   @Override
   void paintSelf(Graphics graphics, boolean addExtent, int xContentAdjust, int yContentAdjust) {
      super.paintSelf(graphics, addExtent, -this.getHorizontalScroll(), -this.getVerticalScroll());
   }

   protected final void clearCustomScrollArrows() {
      Graphics.resetOverlays();
   }

   protected boolean drawLeafFocus(boolean drawBackground, boolean drawFocus) {
      return !(this._fieldWithFocus instanceof Manager) ? false : ((Manager)this._fieldWithFocus).drawLeafFocus(drawBackground, drawFocus);
   }

   public void setNonfocusableOverride(boolean override) {
      this._nonfocusableArrowOverride = override;
   }

   private void paintScrollbars(Graphics graphics) {
      this._indicatorHeight = 0;
      int overlayOffset = this._nonfocusableArrowOverride ? 2 : 0;
      if (!(!this.isStyle(17592186044416L) & !this.isStyle(70368744177664L))) {
         XYRect content = this.getContentRect();
         boolean rightScrollArrows = RichText.getDefaultParagDirection() == 0;
         long style = this.getStyle();
         if ((style & 549755813888L) == 549755813888L) {
            rightScrollArrows = true;
         } else if ((style & 274877906944L) == 274877906944L) {
            rightScrollArrows = false;
         }

         if (!(this._verticalScrollbar != null | this._horizontalScrollbar != null)) {
            if (!this.isStyle(8796093022208L)) {
               Bitmap up = ThemeAttributeSet.getScrollArrow(this, 0);
               if (this.isUpArrowShown() && up != null) {
                  int x;
                  if (rightScrollArrows) {
                     x = this.getXForScrollArrowRight(up.getWidth());
                  } else {
                     x = this.getXForScrollArrowLeft();
                  }

                  graphics.setOverlay(overlayOffset + 0, up, x, this.getVerticalScroll());
               } else {
                  graphics.setOverlay(overlayOffset + 0, null, 0, 0);
               }

               Bitmap down = ThemeAttributeSet.getScrollArrow(this, 1);
               if (this.isDownArrowShown() && down != null) {
                  int x;
                  if (rightScrollArrows) {
                     x = this.getXForScrollArrowRight(down.getWidth());
                  } else {
                     x = this.getXForScrollArrowLeft();
                  }

                  graphics.setOverlay(overlayOffset + 1, down, x, this.getVisibleHeight() + this.getVerticalScroll() - down.getHeight());
                  return;
               }

               graphics.setOverlay(overlayOffset + 1, null, 0, 0);
               return;
            }

            if (this._virtualHeight <= content.height) {
               return;
            }

            this._indicatorWidth = INDICATOR_WIDTH;
            this._indicatorHeight = Math.max(content.height * content.height / this._virtualHeight, 6);
            this._indicatorY = this.getVerticalScroll() * content.height / this._virtualHeight;
            this._scrollBarWidth = SCROLLBAR_WIDTH;
            this._scrollBarHeight = content.height;
            this._scrollBarX = content.X2() + this.getHorizontalScroll() - this._scrollBarWidth;
            this._scrollBarY = this.getVerticalScroll();
            if (haveStylus) {
               graphics.drawRoundRect(
                  this._scrollBarX, this._scrollBarY, this._scrollBarWidth, this._scrollBarHeight, this._scrollBarWidth, this._scrollBarWidth
               );
            } else {
               Bitmap bitmap = Theme.getThemeBitmap(2);
               graphics.tileRop(-99, this._scrollBarX, this._scrollBarY, this._scrollBarWidth, this._scrollBarHeight, bitmap, 0, 0);
            }

            this._indicatorX = this._scrollBarX + (this._scrollBarWidth - this._indicatorWidth >> 1);
            this._indicatorY = this._indicatorY + this._scrollBarY;
            if (haveStylus) {
               int y = this._indicatorY + this._scrollBarWidth;
               int height = this._indicatorHeight - 2 * this._scrollBarWidth;
               if (height <= 6) {
                  y = this._indicatorY;
                  height = this._indicatorHeight;
               }

               Bitmap bitmap = Theme.getThemeBitmap(3);
               graphics.tileRop(-99, this._indicatorX, y, this._indicatorWidth, height, bitmap, 0, 0);
               return;
            }

            graphics.fillRect(this._indicatorX, this._indicatorY, this._indicatorWidth, this._indicatorHeight);
         } else {
            int cornerWidthAdjust = 0;
            int cornerHeightAdjust = 0;
            if (this._verticalScrollbar != null && this._horizontalScrollbar != null) {
               cornerHeightAdjust = SCROLLBAR_TOTAL_HEIGHT;
               cornerWidthAdjust = SCROLLBAR_TOTAL_WIDTH;
               Theme theme = ThemeManager.getActiveTheme();
               Bitmap bitmap = theme.getImage("themed-scrollbar-corner", true).getBitmap();
               graphics.drawBitmap(this.getVirtualWidth(), this.getVirtualHeight(), cornerWidthAdjust, cornerHeightAdjust, bitmap, 0, 0);
            }

            if (this._verticalScrollbar != null) {
               this._verticalScrollbar.setExtent(SCROLLBAR_TOTAL_WIDTH, this.getVisibleHeight() - cornerHeightAdjust);
               this._verticalScrollbar.setPosition(this.getVirtualWidth(), this._verticalScroll);
               this._verticalScrollbar.paintSelf(graphics, true, 0, 0);
            }

            if (this._horizontalScrollbar != null) {
               this._horizontalScrollbar.setExtent(this.getVisibleWidth() - cornerWidthAdjust, SCROLLBAR_TOTAL_HEIGHT);
               this._horizontalScrollbar.setPosition(this._horizontalScroll, this.getVirtualHeight());
               this._horizontalScrollbar.paintSelf(graphics, true, 0, 0);
               return;
            }
         }
      }
   }

   public void replace(Field oldField, Field newField) {
      if (oldField == null) {
         throw new IllegalArgumentException("Manager.replace: oldField is null");
      }

      if (newField == null) {
         throw new IllegalArgumentException("Manager.replace: newField is null");
      }

      if (oldField.getManager() != this) {
         throw new IllegalArgumentException("Manager.replace: oldField is not a child");
      }

      if (newField.getManager() != null) {
         throw new IllegalArgumentException("Manager.replace: newField already has a manager");
      }

      int index = oldField.getIndex();
      if (index == -1) {
         throw new IllegalStateException("Manager.replace: oldField's index is -1");
      }

      boolean hadFocus = oldField.isFocus();
      this.delete(oldField);
      this.insert(newField, index);
      if (hadFocus && newField.isFocusable()) {
         newField.setFocus();
      }
   }

   protected final void removeBlankSpace() {
      if (!this.isStyle(288230376151711744L)) {
         if (this.getVerticalScroll() + this.getContentHeight() > this._virtualHeight) {
            this._verticalScroll = this._virtualHeight - this.getContentHeight();
            if (this.getVerticalScroll() < 0) {
               this._verticalScroll = 0;
            }

            this.invalidate();
         }

         if (this.getHorizontalScroll() + this.getContentWidth() > this._virtualWidth) {
            this._horizontalScroll = this._virtualWidth - this.getContentWidth();
            if (this.getHorizontalScroll() < 0) {
               this._horizontalScroll = 0;
            }

            this.invalidate();
         }
      }
   }

   private void removeField(int index, Field field) {
      if (field == this._fieldWithFocus) {
         throw new IllegalStateException("tried to remove FieldWithFocus");
      }

      this.removeFieldHelper(index, field);
   }

   private void removeFieldHelper(int index, Field field) {
      for (int i = index + 1; i < this._fieldsCount; i++) {
         this._fields[i].setIndex(i - 1);
      }

      System.arraycopy(this._fields, index + 1, this._fields, index, this._fieldsCount - index - 1);
      this._fieldsCount--;
      this._fields[this._fieldsCount] = null;
      if (index < this._fieldWithFocusIndex) {
         this._fieldWithFocusIndex--;
      }

      field.setManager(null, -1);
   }

   final void runLayoutUpdate(int index, int added, int deleted) {
      if (this.isValidLayout()) {
         Screen screen = this.getScreen();
         if (screen != null) {
            this.runLayoutUpdate0(index, added, deleted);
            if (added > 0 && screen.getFieldWithFocus() == null) {
               boolean newfocus = false;

               for (int i = 0; i < added; i++) {
                  if (this._fields[index + i].isFocusable()) {
                     newfocus = true;
                     break;
                  }
               }

               if (newfocus) {
                  screen.setFocus();
               }
            }
         }
      }
   }

   void runLayoutUpdate0(int index, int added, int deleted) {
      if (!this.incrementalLayout(index, added, deleted)) {
         this.getManager().runLayoutUpdate0(this.getIndex(), 1, 1);
      } else {
         this.getScreen().ensureFocusVisible();
      }
   }

   public void setFieldWithFocus(Field child) {
      this._fieldWithFocusIndex = child.getIndex();
      this._fieldWithFocus = child;
   }

   final void setValidLayout(boolean validLayout) {
      this._validLayout = validLayout;
   }

   protected void sublayout(int _1, int _2) {
      throw null;
   }

   protected void subpaint(Graphics graphics) {
      int numFields = this.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         Field field = this.getField(i);
         this.paintChild(graphics, field);
      }
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      this._fieldWithFocusIndex = this.firstFocus(direction);
      if (this._fieldWithFocusIndex != -1) {
         this._fieldWithFocus = this._fields[this._fieldWithFocusIndex];
         this._fieldWithFocus.onFocus(direction);
         this._fieldWithFocus.focusChangeNotify(1);
      }
   }

   protected int firstFocus(int direction) {
      switch (direction) {
         case -1:
            for (int i = this._fieldsCount - 1; i >= 0; i--) {
               if (this._fields[i].isFocusable()) {
                  return i;
               }
            }
            break;
         case 1:
            for (int i = 0; i < this._fieldsCount; i++) {
               if (this._fields[i].isFocusable()) {
                  return i;
               }
            }
      }

      return -1;
   }

   @Override
   protected void onUnfocus() {
      if (this._fieldWithFocus != null) {
         this._fieldWithFocus.onUnfocus();
         this._fieldWithFocus.focusChangeNotify(3);
         this._fieldWithFocus = null;
         this._fieldWithFocusIndex = -1;
      }

      super.onUnfocus();
   }

   protected int nextFocus(int direction, boolean alt) {
      return this.nextFocus(direction, alt ? 0 : 2);
   }

   protected int nextFocus(int direction, int axis) {
      if (this._fieldWithFocusIndex <= -1) {
         return -1;
      }

      switch (direction) {
         case -1:
            return this._fieldWithFocusIndex - 1;
         case 1:
            if (this._fieldWithFocusIndex + 1 == this.getFieldCount()) {
               return -1;
            }

            return this._fieldWithFocusIndex + 1;
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   protected void moveFocus(int x, int y, int status, int time) {
      this.moveFocusToPoint(x, y, status, time, 0, false);
   }

   @Override
   boolean moveFocusToPoint(int x, int y, int status, int time) {
      int direction = status & 3855;
      return this.moveFocusToPoint(x, y, status, time, direction, true);
   }

   private boolean moveFocusToPoint(int x, int y, int status, int time, int direction, boolean isInManagersCoordinates) {
      if (!this.isFocusable()) {
         return false;
      }

      if (haveStylus && isInManagersCoordinates) {
         x += this.getHorizontalScroll();
         y += this.getVerticalScroll();
      }

      int startIndex = this.getFieldAtLocation(x, y);
      if (startIndex < 0) {
         startIndex = this.getFieldClosestToLocation(x, y, status);
      }

      int fieldIndex;
      label62:
      while (true) {
         fieldIndex = startIndex;
         switch (direction & 771) {
            case 1:
            case 256:
               while (fieldIndex < this._fieldsCount) {
                  if (this.getField(fieldIndex).isFocusable()) {
                     break label62;
                  }

                  fieldIndex++;
               }

               direction = 512;
               break;
            case 2:
            case 512:
               while (fieldIndex >= 0) {
                  if (this.getField(fieldIndex).isFocusable()) {
                     break label62;
                  }

                  fieldIndex--;
               }

               int var12 = false;
               direction = 256;
               break;
            default:
               if (!this.getField(fieldIndex).isFocusable()) {
                  return false;
               }
               break label62;
         }
      }

      if (this._fieldWithFocusIndex != fieldIndex) {
         if (this._fieldWithFocus != null) {
            this._fieldWithFocus.onUnfocus();
            this._fieldWithFocus.focusChangeNotify(3);
         }

         Field field = this.getField(fieldIndex);
         this._fieldWithFocusIndex = fieldIndex;
         this._fieldWithFocus = field;
         if (this._fieldWithFocus instanceof Manager) {
            this._fieldWithFocus.onFocus(1);
         } else {
            this._fieldWithFocus.onFocus(0);
         }

         this._fieldWithFocus.focusChangeNotify(1);
      } else {
         this.focusChangeNotify(2);
      }

      XYRect extent = this._fieldWithFocus.getExtent();
      x -= extent.x;
      y -= extent.y;
      if (!(this._fieldWithFocus instanceof Manager)) {
         this._fieldWithFocus.moveFocus(x, y, status, time);
         return true;
      } else {
         return ((Manager)this._fieldWithFocus).moveFocusToPoint(x, y, status, time, direction, isInManagersCoordinates);
      }
   }

   protected boolean moveFocus(int where) {
      Field field = this._fieldWithFocus;
      boolean result = false;
      if (!this.isStyle(281474976710656L) && field instanceof Manager) {
         result = ((Manager)field).moveFocus(where);
      }

      if (!result && (this.isStyle(281474976710656L) || this.getScreen().getDelegate() == this)) {
         int fontHeight = this.getFont().getHeight();
         XYRect cursor = Ui.getTmpXYRect();
         this.getFocusRect(cursor);
         int x = this.getHorizontalScroll() + cursor.x;
         int y = this.getVerticalScroll() + cursor.y;
         Ui.returnTmpXYRect(cursor);
         int status = 0;
         if ((where & 256) != 0) {
            y -= this.getContentHeight() - fontHeight;
            status |= 512;
         } else if ((where & 512) != 0) {
            y += this.getContentHeight() - fontHeight;
            status |= 256;
         } else if ((where & 1) != 0) {
            y = 0;
            status |= 512;
         } else if ((where & 2) != 0) {
            y = this.getVirtualHeight();
            status |= 256;
         }

         if ((where & 1024) != 0) {
            x -= this.getContentWidth() - fontHeight;
            status |= 2048;
         } else if ((where & 2048) != 0) {
            x += this.getContentWidth() - fontHeight;
            status |= 1024;
         } else if ((where & 4) != 0) {
            x = 0;
            status |= 2048;
         } else if ((where & 8) != 0) {
            x = this.getVirtualWidth();
            status |= 1024;
         }

         x = MathUtilities.clamp(0, x, this.getVirtualWidth() - 1);
         y = MathUtilities.clamp(0, y, this.getVirtualHeight() - fontHeight);
         XYRect rect = Ui.getTmpXYRect();
         rect.set(x, y, 0, fontHeight);
         this.makeRegionVisible(true, rect, true);
         Ui.returnTmpXYRect(rect);
         result = this.moveFocusToPoint(x, y, status, 0, where, false);
      }

      return result;
   }

   public boolean setFocus(int x, int y, int status) {
      return this.getScreen().setFocus(this, x, y, status, 0);
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      return this._fieldWithFocus != null ? this._fieldWithFocus.navigationClick(status, time) : super.navigationClick(status, time);
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      return this._fieldWithFocus != null ? this._fieldWithFocus.navigationMovement(dx, dy, status, time) : super.navigationMovement(dx, dy, status, time);
   }

   @Override
   protected boolean navigationUnclick(int status, int time) {
      return this._fieldWithFocus != null ? this._fieldWithFocus.navigationUnclick(status, time) : super.navigationUnclick(status, time);
   }

   @Override
   protected boolean trackwheelRoll(int amount, int status, int time) {
      return this._fieldWithFocus != null ? this._fieldWithFocus.trackwheelRoll(amount, status, time) : super.trackwheelRoll(amount, status, time);
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      return this._fieldWithFocus != null ? this._fieldWithFocus.processKeyEvent(event, key, keycode, time) : super.processKeyEvent(event, key, keycode, time);
   }

   @Override
   public boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      return this._fieldWithFocus != null
         ? this._fieldWithFocus.processNavigationEvent(event, dx, dy, status, time)
         : super.processNavigationEvent(event, dx, dy, status, time);
   }

   protected void makeFocusVisible(boolean immediate, XYRect region, boolean draw, boolean reset) {
      if (this._horizontalScrollbar != null) {
         region.height = region.height + SCROLLBAR_TOTAL_HEIGHT;
      }

      if (this._verticalScrollbar != null) {
         region.width = region.width + SCROLLBAR_TOTAL_WIDTH;
      }

      this.makeRegionVisible(immediate, region, draw);
   }

   private final void makeRegionVisible(boolean immediate, XYRect region, boolean draw) {
      int yAmount = this.calculateVerticalScrollAmount(region);
      int xAmount = this.calculateHorizontalScrollAmount(region);
      this.scroll(immediate, xAmount, yAmount, draw);
   }

   protected final void scroll(boolean immediate, int xAmount, int yAmount, boolean draw) {
      Ui.IN_MAKE_REGION_VISIBLE = true;
      int width = this.getContentWidth();
      int height = this.getContentHeight();
      int oldHScroll = this.getHorizontalScroll();
      int oldVScroll = this.getVerticalScroll();
      immediate = immediate && !this.getScreen().isTransparent();
      boolean scrollCopy = this.isScrollCopyable();
      if (yAmount != 0) {
         this._verticalScroll += yAmount;
         if (draw && immediate) {
            XYRect clip = Ui.getTmpXYRect();
            clip.set(0, 0, Math.max(this.getContentWidth(), this.getVirtualWidth()), Math.max(this.getContentHeight(), this.getVirtualHeight()));
            Graphics graphics = super.getGraphics0(clip, false);
            Ui.returnTmpXYRect(clip);
            XYRect var19 = null;
            if (scrollCopy && graphics.copyArea(this.getHorizontalScroll(), this.getVerticalScroll(), width, height, 0, -yAmount)) {
               XYRect invalid = this.getScreen().getInvalid();
               if (invalid.height > 0 && invalid.width > 0) {
                  XYRect myContent = Ui.getTmpXYRect();
                  myContent.set(this.getContentRect());
                  Manager manager = this.getManager();
                  if (manager != null) {
                     manager.transformToScreen(myContent);
                  }

                  if (myContent.contains(invalid)) {
                     invalid.y -= yAmount;
                  } else {
                     myContent.intersect(invalid);
                     if (myContent.height > 0 && myContent.width > 0) {
                        immediate = false;
                     }
                  }

                  Ui.returnTmpXYRect(myContent);
               }

               if (immediate) {
                  if (!scrollCopy) {
                     graphics.pushContext(this.getHorizontalScroll(), this.getVerticalScroll(), width, height, 0, 0);
                  } else if (yAmount < 0) {
                     graphics.pushContext(this.getHorizontalScroll(), this.getVerticalScroll(), width, -yAmount, 0, 0);
                  } else if (this._verticalScrollbar != null) {
                     graphics.pushContext(this.getHorizontalScroll(), this.getVerticalScroll() + height - yAmount, width + SCROLLBAR_TOTAL_WIDTH, yAmount, 0, 0);
                  } else {
                     graphics.pushContext(this.getHorizontalScroll(), this.getVerticalScroll() + height - yAmount, width, yAmount, 0, 0);
                  }

                  this.applyTheme(graphics, true);
                  int depth = graphics.getContextStackSize();
                  if (!this.getScreen().isCatchPaintExceptions()) {
                     this.paint(graphics);
                  } else {
                     try {
                        this.paint(graphics);
                     } catch (Throwable var17) {
                     }
                  }

                  if (graphics.getContextStackSize() != depth) {
                     throw new IllegalStackSizeException(
                        "Unpaired Graphics.push/pop(mrv_v) in class "
                           + this.getClass().getName()
                           + "depth "
                           + graphics.getContextStackSize()
                           + "!="
                           + depth
                           + "expected"
                     );
                  }

                  graphics.popContext();
               }
            } else {
               immediate = false;
            }
         }
      }

      if (xAmount != 0) {
         this._horizontalScroll += xAmount;
         if (draw && immediate) {
            XYRect clip = Ui.getTmpXYRect();
            clip.set(0, 0, Math.max(this.getContentWidth(), this.getVirtualWidth()), Math.max(this.getContentHeight(), this.getVirtualHeight()));
            Graphics graphics = super.getGraphics0(clip, false);
            Ui.returnTmpXYRect(clip);
            XYRect var21 = null;
            if (scrollCopy && graphics.copyArea(this.getHorizontalScroll(), this.getVerticalScroll(), width, height, -xAmount, 0)) {
               XYRect invalid = this.getScreen().getInvalid();
               if (invalid.height > 0 && invalid.width > 0) {
                  XYRect myContent = Ui.getTmpXYRect();
                  myContent.set(this.getContentRect());
                  Manager manager = this.getManager();
                  if (manager != null) {
                     manager.transformToScreen(myContent);
                  }

                  if (myContent.contains(invalid)) {
                     invalid.x -= xAmount;
                  } else {
                     myContent.intersect(invalid);
                     if (myContent.height > 0 && myContent.width > 0) {
                        immediate = false;
                     }
                  }

                  Ui.returnTmpXYRect(myContent);
               }

               if (immediate) {
                  if (!scrollCopy) {
                     graphics.pushContext(this.getHorizontalScroll(), this.getVerticalScroll(), width, height, 0, 0);
                  } else if (xAmount < 0) {
                     graphics.pushContext(this.getHorizontalScroll(), this.getVerticalScroll(), -xAmount, height, 0, 0);
                  } else {
                     graphics.pushContext(this.getHorizontalScroll() + width - xAmount, this.getVerticalScroll(), xAmount, height, 0, 0);
                  }

                  this.applyTheme(graphics, true);
                  int depth = graphics.getContextStackSize();
                  if (!this.getScreen().isCatchPaintExceptions()) {
                     this.paint(graphics);
                  } else {
                     try {
                        this.paint(graphics);
                     } catch (Throwable var16) {
                     }
                  }

                  if (graphics.getContextStackSize() != depth) {
                     throw new IllegalStackSizeException(
                        "Unpaired Graphics.push/pop(mrv_v) in class "
                           + this.getClass().getName()
                           + "depth "
                           + graphics.getContextStackSize()
                           + "!="
                           + depth
                           + "expected"
                     );
                  }

                  graphics.popContext();
               }
            } else {
               immediate = false;
            }
         }
      }

      if (draw && !immediate && (xAmount != 0 || yAmount != 0)) {
         this.invalidate();
      }

      if (this._scrollListener != null && (oldHScroll != this.getHorizontalScroll() || oldVScroll != this.getVerticalScroll())) {
         try {
            this._scrollListener.scrollChanged(this, this.getHorizontalScroll(), this.getVerticalScroll());
         } catch (Throwable var15) {
         }
      }

      if (this.isStyle(8796093022208L)) {
         XYRect extent = this.getExtent();
         this.invalidate(extent.X2() + this.getHorizontalScroll() - SCROLLBAR_TOTAL_WIDTH, this.getVerticalScroll(), SCROLLBAR_TOTAL_WIDTH, extent.height);
      }

      Ui.IN_MAKE_REGION_VISIBLE = false;
   }

   @Override
   public void setDirty(boolean dirty) {
      for (int i = 0; i < this._fieldsCount; i++) {
         this._fields[i].setDirty(dirty);
      }
   }

   public void setHorizontalQuantization(int horizontalQuanta) {
      if (horizontalQuanta != -1 && horizontalQuanta <= 0) {
         throw new IllegalArgumentException();
      }

      this._horizontalQuanta = horizontalQuanta;
   }

   public void setHorizontalScroll(int position) {
      if (!this.isStyle(1125899906842624L)) {
         throw new IllegalStateException("Attempting to scroll a non scollable manager.");
      }

      if (position < 0) {
         throw new IllegalArgumentException();
      }

      if (this.isValidLayout()) {
         XYRect rect = Ui.getTmpXYRect();
         rect.set(position, this.getVerticalScroll(), this.getContentWidth(), 0);
         this.makeRegionVisible(this.getScreen().isValid(), rect, true);
         Ui.returnTmpXYRect(rect);
      } else {
         this._horizontalScroll = position;
      }
   }

   protected final void setPositionChild(Field field, int x, int y) {
      if (field.getManager() != this) {
         throw new IllegalArgumentException("Field is not a child of this manager.");
      }

      field.setPosition(x, y);
   }

   public void setScrollListener(ScrollChangeListener listener) {
      if (listener != null && this._scrollListener != null) {
         throw new IllegalStateException("Multiple scroll listeners not allowed.");
      }

      this._scrollListener = listener;
   }

   public void setVerticalQuantization(int verticalQuanta) {
      if (verticalQuanta != -1 && verticalQuanta <= 0) {
         throw new IllegalArgumentException();
      }

      this._verticalQuanta = verticalQuanta;
   }

   public void setVerticalScroll(int position) {
      if (!this.isStyle(281474976710656L)) {
         throw new IllegalStateException("Attempting to scroll a non scrollable manager.");
      }

      if (position < 0) {
         throw new IllegalArgumentException();
      }

      int verticalQuantaPixels = this._verticalQuanta;
      if (verticalQuantaPixels < 0) {
         verticalQuantaPixels = -verticalQuantaPixels * this.getFont().getHeight();
      }

      if (verticalQuantaPixels > 1) {
         int numQuanta = position / verticalQuantaPixels;
         int floor = numQuanta * verticalQuantaPixels;
         int remainder = position - floor;
         if (remainder * 2 <= verticalQuantaPixels) {
            position = floor;
         } else {
            position = floor + verticalQuantaPixels;
         }
      }

      if (this.isValidLayout()) {
         XYRect rect = Ui.getTmpXYRect();
         rect.set(this.getHorizontalScroll(), position, 0, this.getContentHeight());
         this.makeRegionVisible(this.getScreen().isValid(), rect, true);
         Ui.returnTmpXYRect(rect);
      } else {
         this._verticalScroll = position;
      }
   }

   protected final void setVirtualExtent(int virtualWidth, int virtualHeight) {
      this._virtualWidth = virtualWidth;
      this._virtualHeight = virtualHeight;
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      return this._fieldWithFocus != null ? this._fieldWithFocus.trackwheelClick(status, time) : false;
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
   }

   private int pageSize() {
      int pageSize = this.getContentHeight();
      int fontHeight = this.getFont().getHeight();
      return pageSize < 2 * fontHeight ? pageSize : pageSize - fontHeight;
   }

   private int getStylusPos(int x, int y) {
      if (this._indicatorHeight == 0) {
         return 0;
      } else {
         int barX = Math.min(this._scrollBarX, this._indicatorX) - 2;
         if (x < barX) {
            return 0;
         } else if (x > this._scrollBarX + this._scrollBarWidth) {
            return 0;
         } else if (y < this._scrollBarY) {
            return 0;
         } else if (y > this._scrollBarY + this._scrollBarHeight) {
            return 0;
         } else if (y < this._indicatorY) {
            return 1;
         } else {
            return y > this._indicatorY + this._indicatorHeight ? 3 : 2;
         }
      }
   }

   private int mapStylusX(int x) {
      return x - this._fieldWithFocus.getExtent().x;
   }

   private int mapStylusY(int y) {
      return y - this._fieldWithFocus.getExtent().y;
   }

   @Override
   protected boolean stylusDown(int x, int y, int status, int time) {
      x += this.getHorizontalScroll();
      y += this.getVerticalScroll();
      switch (this.getStylusPos(x, y)) {
         case 0:
            if (this._fieldWithFocus != null) {
               return this._fieldWithFocus.stylusDown(this.mapStylusX(x), this.mapStylusY(y), status, time);
            }

            return false;
         case 2:
            this._stylusDownY = y - this.getVerticalScroll();
            this._stylusDownVerticalScroll = this.getVerticalScroll();
            this._stylusDraggingIndicator = true;
            return true;
         default:
            return true;
      }
   }

   @Override
   protected boolean stylusDrag(int x, int y, int status, int time) {
      if (this._stylusDraggingIndicator) {
         int delta = this._virtualHeight * (y - this._stylusDownY) / this._scrollBarHeight;
         this.setVerticalScroll(MathUtilities.clamp(0, this._stylusDownVerticalScroll + delta, this._virtualHeight - this.getExtent().height));
         return true;
      }

      x += this.getHorizontalScroll();
      y += this.getVerticalScroll();
      switch (this.getStylusPos(x, y)) {
         case 0:
            if (this._fieldWithFocus != null) {
               return this._fieldWithFocus.stylusDrag(this.mapStylusX(x), this.mapStylusY(y), status, time);
            }

            return false;
         default:
            return true;
      }
   }

   @Override
   protected boolean stylusUp(int x, int y, int status, int time) {
      if (this._stylusDraggingIndicator) {
         this._stylusDraggingIndicator = false;
         return true;
      }

      x += this.getHorizontalScroll();
      y += this.getVerticalScroll();
      switch (this.getStylusPos(x, y)) {
         case 0:
            if (this._fieldWithFocus != null) {
               return this._fieldWithFocus.stylusUp(this.mapStylusX(x), this.mapStylusY(y), status, time);
            }

            return false;
         default:
            return true;
      }
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      x += this.getHorizontalScroll();
      y += this.getVerticalScroll();
      switch (this.getStylusPos(x, y)) {
         case -1:
            return false;
         case 0:
         default:
            if (this._fieldWithFocus != null) {
               return this._fieldWithFocus.stylusTap(this.mapStylusX(x), this.mapStylusY(y), status, time);
            }

            return false;
         case 1:
            this.setVerticalScroll(Math.max(this.getVerticalScroll() - this.pageSize(), 0));
            return true;
         case 2:
            return true;
         case 3:
            this.setVerticalScroll(Math.min(this.getVerticalScroll() + this.pageSize(), this._virtualHeight - this.getExtent().height));
            return true;
      }
   }

   @Override
   protected boolean stylusDoubleTap(int x, int y, int status, int time) {
      x += this.getHorizontalScroll();
      y += this.getVerticalScroll();
      switch (this.getStylusPos(x, y)) {
         case 0:
            if (this._fieldWithFocus != null) {
               return this._fieldWithFocus.stylusDoubleTap(this.mapStylusX(x), this.mapStylusY(y), status, time);
            }

            return false;
         default:
            return true;
      }
   }

   @Override
   protected boolean stylusTapHold(int x, int y, int status, int time) {
      x += this.getHorizontalScroll();
      y += this.getVerticalScroll();
      switch (this.getStylusPos(x, y)) {
         case 0:
            if (this._fieldWithFocus != null) {
               return this._fieldWithFocus.stylusTapHold(this.mapStylusX(x), this.mapStylusY(y), status, time);
            }

            return false;
         default:
            return true;
      }
   }

   @Override
   protected boolean onCursorHover(int x, int y) {
      return this._fieldWithFocus != null ? this._fieldWithFocus.onCursorHover(this.mapStylusX(x), this.mapStylusY(y)) : false;
   }

   @Override
   public Cursor getFocusCursor() {
      return this._fieldWithFocus != null ? this._fieldWithFocus.getFocusCursor() : Cursor.getPredefinedCursor(0);
   }

   @Override
   protected boolean trackwheelUnclick(int status, int time) {
      return this._fieldWithFocus != null ? this._fieldWithFocus.trackwheelUnclick(status, time) : false;
   }

   @Override
   boolean validateFieldStyle(long style) {
      return super.validateFieldStyle(style & -5624987138492727297L);
   }

   private static long validateStyle(long style) {
      if ((style & 844424930131968L) == 0) {
         style |= 562949953421312L;
      }

      if ((style & 844424930131968L) == 562949953421312L) {
         style &= -52776558133249L;
         style |= 35184372088832L;
      }

      if ((style & 3377699720527872L) == 0) {
         style |= 2251799813685248L;
      }

      if ((style & 3377699720527872L) == 2251799813685248L) {
         style &= -211106232532993L;
         style |= 140737488355328L;
      }

      style &= -67553994410557441L;
      if (haveStylus && (style & 1407374883553280L) != 0) {
         style |= 8796093022208L;
      }

      return style;
   }

   @Override
   protected void onUndisplay() {
      super.onUndisplay();
      if (this._nonfocusableArrowOverride) {
         this.clearCustomScrollArrows();
      }
   }

   @Override
   void callOnExposed() {
      this.onExposed();

      for (int i = 0; i < this._fieldsCount; i++) {
         this._fields[i].callOnExposed();
      }
   }

   @Override
   void callOnObscured() {
      this.onObscured();

      for (int i = 0; i < this._fieldsCount; i++) {
         this._fields[i].callOnObscured();
      }
   }

   int getHorizontalScrollHeight() {
      return 0;
   }
}
