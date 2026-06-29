package net.rim.device.apps.internal.browser.ui;

import java.util.Stack;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringMatch;
import net.rim.device.apps.internal.browser.html.HTMLTextAreaField;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.internal.ui.AnimationThread;
import net.rim.device.internal.ui.Border;
import net.rim.device.internal.ui.Cursor;
import net.rim.device.internal.ui.TextFlowRegion;

public class TextFlowManager extends Manager {
   private int _currentZoomValue;
   private int _maxZoomValue;
   private boolean _wideViewMode;
   private boolean _mobileViewCursor;
   private Cursor _currentCursor;
   private int _lastKeypadStatus;
   XYRect _borderPaint;
   TextFlowCell$Painter _painter;
   TextFlowCell$ExtentStack _leftObjects;
   TextFlowCell$ExtentStack _rightObjects;
   TextFlowCell$ExtentStack _leftObjects2;
   TextFlowCell$ExtentStack _rightObjects2;
   int _selectionCellBegin;
   int _selectionCellEnd;
   private int _lastLayoutHeight;
   int _lastLayoutStartY;
   int _lastLayoutEndY;
   TextFlowData _textFlowData;
   TextFlowLayout _textFlowLayout;
   TextFlowCell _rootCell;
   private TextFlowCell _currentCell;
   private Stack _cellStack;
   private TextFlowManager$FocusField _focusField;
   private int _visibleAmount;
   private boolean _layoutActive;
   private int _layoutGeneration;
   private int _layoutWidth;
   int _layoutHeight;
   private int _layoutFocusCount;
   private int _invalidLayoutStart;
   private int _invalidLayoutEnd;
   private Bitmap _scalingBitmap;
   private Graphics _scalingGraphics;
   private int[] _tempx;
   private int[] _tempy;
   private int _mode;
   private SelectionPosition _selectionPosition;
   private int _selectionStart;
   private int _selectionEnd;
   private MenuItem _positionItem;
   private MenuItem _cancelPositionItem;
   private MenuItem _wideViewItem;
   private MenuItem _narrowViewItem;
   private int _focusIndex;
   private int _pendingFocusIndex;
   private int _pendingFocusState;
   private int _focusState;
   private int _rollAmount;
   private XYRect _rect;
   private XYRect _visibleRect;
   private boolean _leaving;
   private int _lastStatus;
   private int _focusSearchDirection;
   private boolean _inFindNewFocus;
   private int _verticalNavigationMovementAmount;
   private int _setScroll;
   private int _currentScroll;
   private int _cursorXPos;
   private int _cursorYPos;
   private XYRect _cursorFocusRect;
   private Manager _scrollingParent;
   private int _scrollAmount;
   private Runnable _hoverRunnable;
   private int _hoverRunnableId;
   private boolean _sendMoveToFocus;
   private static final int[] ZOOM_VALUES = new int[]{65536, Fixed32.tenThouToFP(12500), Fixed32.tenThouToFP(43574)};
   private static final int HOTSPOT_ACTIVATION_THRESHOLD = Fixed32.toFP(2);
   private static final int WIDE_VIEW_WIDTH = 1024;
   private static final int POINTER_OVERLAY_INDEX = 4;
   static final long FIELD_HALIGN_MASK = 12884901888L;
   public static final int PADDING_LEFT_SHIFT = 0;
   public static final int PADDING_RIGHT_SHIFT = 8;
   public static final int PADDING_TOP_SHIFT = 16;
   public static final int PADDING_BOTTOM_SHIFT = 24;
   public static final int PADDING_MAX_VALUE = 255;
   public static final int PADDING_MIN_VALUE = 0;
   public static final int STATUS_CURSOR_MODE = 32768;
   public static final int INVALID_SCALAR_COOKIE = 0;
   public static final int ANIMATION_NONE = 0;
   public static final int ANIMATION_STOP = 1;
   public static final int ANIMATION_SCROLL = 2;
   public static final int ANIMATION_SLIDE = 4;
   public static final int ANIMATION_ALTERNATE = 8;
   public static final int ANIMATION_BLINK = 16;
   public static final int ANIMATION_LEFT = 32;
   public static final int ANIMATION_RIGHT = 64;
   static final int BACKGROUND_FIXED = 1;
   static final int BACKGROUND_REPEAT_X = 2;
   static final int BACKGROUND_REPEAT_Y = 4;
   static final int ALL_ON_SAME_Y_POS = 8;
   public static final int FIXED_WIDTH = 16;
   static final int BACKGROUND_IMAGE_SET = 32;
   static final int WIDTH_ADJUSTED = 64;
   public static final int NO_WRAP_PSEUDO = 128;
   public static final int NO_WRAP_REALLY = 256;
   public static final int VALIGN_TOP = 0;
   public static final int VALIGN_BASELINE = 512;
   public static final int VALIGN_BOTTOM = 1024;
   public static final int VALIGN_MIDDLE = 1536;
   public static final int VALIGN_MASK = 1536;
   static final int NO_WRAP_MASK = 384;
   public static final int HALIGN_LEFT = 2048;
   public static final int HALIGN_CENTER = 4096;
   public static final int HALIGN_RIGHT = 6144;
   public static final int HALIGN_MASK = 6144;
   public static final long NO_USE_ALL_HEIGHT = 1L;
   public static final long USE_SCREEN_HEIGHT = 2L;
   public static final long MINIMAL_MENU_MODE = 4L;
   public static final long START_ANCHOR = Long.MIN_VALUE;
   public static final long INVALID_ANCHOR = -9223372036854775807L;
   private static final int MODE_SCROLLING = 1;
   private static final int MODE_POSITIONING = 2;
   private static final int MODE_SELECTING = 3;
   private static final boolean DEBUG = false;
   private static final int MAX_CELL_STACK_DEPTH = 40;
   private static final int NO_SET_SCROLL = Integer.MAX_VALUE;
   public static boolean _oneDNavigationMode;

   public TextFlowManager() {
      this(0);
   }

   public TextFlowManager(long style) {
      super(validateStyle(style));
      this._currentZoomValue = ZOOM_VALUES[0];
      this._maxZoomValue = ZOOM_VALUES[ZOOM_VALUES.length - 1];
      this._borderPaint = new XYRect();
      this._painter = new TextFlowCell$Painter();
      this._leftObjects = new TextFlowCell$ExtentStack();
      this._rightObjects = new TextFlowCell$ExtentStack();
      this._leftObjects2 = new TextFlowCell$ExtentStack();
      this._rightObjects2 = new TextFlowCell$ExtentStack();
      this._selectionCellBegin = Integer.MAX_VALUE;
      this._selectionCellEnd = Integer.MAX_VALUE;
      this._cellStack = new Stack();
      this._focusField = new TextFlowManager$FocusField(this);
      this._visibleAmount = Font.getDefault().getHeight();
      this._layoutActive = true;
      this._layoutGeneration = -1;
      this._layoutWidth = -1;
      this._layoutHeight = -1;
      this._mode = 1;
      this._selectionPosition = new SelectionPosition();
      this._positionItem = new TextFlowManager$TextFlowMenuItem(this, 655, 30240, 500);
      this._cancelPositionItem = new TextFlowManager$TextFlowMenuItem(this, 656, 30240, 550);
      this._wideViewItem = new TextFlowManager$TextFlowMenuItem(this, 853, 139266, 560);
      this._narrowViewItem = new TextFlowManager$TextFlowMenuItem(this, 854, 139266, 560);
      this._pendingFocusIndex = -1;
      this._rollAmount = Integer.MAX_VALUE;
      this._rect = new XYRect();
      this._visibleRect = new XYRect();
      this._setScroll = Integer.MAX_VALUE;
      this._currentScroll = 0;
      this._cursorFocusRect = new XYRect();
      this._scrollAmount = Font.getDefault().getHeight() * (Trackball.isSupported() ? 1 : 2);
      this._hoverRunnable = new TextFlowManager$HoverRunnable(this);
      this._hoverRunnableId = -1;
      this._textFlowData = new TextFlowData();
      this._textFlowLayout = new TextFlowLayout(this._textFlowData);
      this._rootCell = new TextFlowCell(this);
      this._rootCell.setStartValues();
      this._currentCell = this._rootCell;
      this._cellStack.push(this._rootCell);
      super.add(this._focusField);
   }

   private boolean usingCursor() {
      return this._wideViewMode || this._mobileViewCursor;
   }

   public void setMobileViewCursor(boolean value) {
      if (this._mobileViewCursor != value) {
         this._mobileViewCursor = value;
         if (!this._wideViewMode && this.getScreen() != null) {
            if (value) {
               if (this.moveCursorTo(
                  this.getHorizontalScroll() + (this.getHorizontalVisibleScroll() >> 1), this.getVerticalScroll() + (this.getVerticalVisibleScroll() >> 1)
               )) {
                  this.handleCursorMove();
               }

               this.trackballSensitivityOn();
               return;
            }

            this.trackballSensitivityOff();
         }
      }
   }

   public boolean getWideViewMode() {
      return this._wideViewMode;
   }

   public void setWideViewMode(boolean value) {
      if (Trackball.isSupported()) {
         this._wideViewMode = value;
         this._currentZoomValue = ZOOM_VALUES[this._wideViewMode ? ZOOM_VALUES.length - 1 : 0];
      }
   }

   public boolean canZoomOut() {
      return this._wideViewMode && this.getZoomValue() < this._maxZoomValue;
   }

   private int getZoomValue() {
      return Math.min(this._currentZoomValue, this._maxZoomValue);
   }

   @Override
   protected boolean drawLeafFocus(boolean drawBackground, boolean drawFocus) {
      Field focus = this.getFieldWithFocus();
      int zoomValue = this.getZoomValue();
      if (zoomValue != 65536) {
         if (focus != this._focusField) {
            Graphics g = this.getGraphics0();
            g.setDrawingStyle(8, drawFocus);
            this.paintScaledField(focus, zoomValue, g);
            return true;
         } else {
            this._focusField.drawFocus(this.getGraphics0(), drawFocus);
            return true;
         }
      } else {
         return false;
      }
   }

   public void viewModeChanged() {
   }

   public void toggleViewMode() {
      if (Trackball.isSupported()) {
         this.trackballSensitivityOff();
         int zoomValue = this.getZoomValue();
         int unscaledCursorX;
         int unscaledCursorY;
         if (this.usingCursor()) {
            unscaledCursorX = RendererControl.fixed32MultToInt(this._cursorXPos, zoomValue);
            unscaledCursorY = RendererControl.fixed32MultToInt(this._cursorYPos, zoomValue);
         } else {
            unscaledCursorX = this.getHorizontalScroll() + (this.getHorizontalVisibleScroll() >> 1);
            unscaledCursorY = this.getVerticalScroll() + (this.getVerticalVisibleScroll() >> 1);
         }

         int textPosition = this._rootCell.getTextPositionFromXYOffset(-1, unscaledCursorX, unscaledCursorY, false);
         Screen screen = this.getScreen();
         TextFlowManager topmost = this.recursiveSetWideViewMode(screen, !this._wideViewMode);
         if (topmost != null) {
            topmost.viewModeChanged();
            topmost.updateLayout();
         }

         unscaledCursorY = this._rootCell.getYOffsetFromTextPosition(textPosition, true);
         this.scrollViewTo(0, RendererControl.fixed32DivToInt(unscaledCursorY, this.getZoomValue()) - (this.getVerticalVisibleScroll() >> 1), true);
         screen.invalidate();
         this.trackballSensitivityOn();
      }
   }

   private TextFlowManager recursiveSetWideViewMode(Manager mgr, boolean value) {
      if (mgr instanceof TextFlowManager) {
         TextFlowManager tfm = (TextFlowManager)mgr;
         tfm.setWideViewMode(value);
         tfm.reflowAll();
      }

      TextFlowManager topMost = null;

      for (int i = mgr.getFieldCount() - 1; i >= 0; i--) {
         Field f = mgr.getField(i);
         if (f instanceof Manager) {
            topMost = this.recursiveSetWideViewMode((Manager)f, value);
         }
      }

      return !(mgr instanceof TextFlowManager) ? topMost : (TextFlowManager)mgr;
   }

   public void scrollViewTo(int topLeftX, int topLeftY, boolean reCenterCursor) {
      int actualWidth = this.getVirtualWidth();
      int actualHeight = this.getVirtualHeight();
      int visibleWidth = this.getHorizontalVisibleScroll();
      int visibleHeight = this.getVerticalVisibleScroll();
      topLeftX = MathUtilities.clamp(0, topLeftX, Math.max(0, actualWidth - visibleWidth));
      topLeftY = MathUtilities.clamp(0, topLeftY, Math.max(0, actualHeight - visibleHeight));
      if (reCenterCursor) {
         this._cursorXPos = topLeftX + (visibleWidth >> 1);
         this._cursorYPos = topLeftY + (visibleHeight >> 1);
      } else {
         Cursor cursor = this._currentCursor != null ? this._currentCursor : this.getDefaultCursor();
         this._cursorXPos = MathUtilities.clamp(topLeftX + cursor.getOriginX(), this._cursorXPos, topLeftX + visibleWidth - cursor.getBitmap().getWidth());
         this._cursorYPos = MathUtilities.clamp(topLeftY + cursor.getOriginY(), this._cursorYPos, topLeftY + visibleHeight - cursor.getBitmap().getHeight());
      }

      if (this.isStyle(281474976710656L)) {
         super.setVerticalScroll(topLeftY);
      }

      if (this.isStyle(1125899906842624L)) {
         super.setHorizontalScroll(topLeftX);
      }

      this._currentScroll = topLeftY;
      this._setScroll = Integer.MAX_VALUE;
      this.handleCursorMove();
   }

   private boolean moveCursorTo(int x, int y) {
      Cursor cursor = this._currentCursor != null ? this._currentCursor : this.getDefaultCursor();
      x = MathUtilities.clamp(0 + cursor.getOriginX(), x, this.getVirtualWidth() - cursor.getBitmap().getWidth());
      y = MathUtilities.clamp(0 + cursor.getOriginY(), y, this.getVirtualHeight() - cursor.getBitmap().getHeight());
      boolean cursorMoved = x != this._cursorXPos || y != this._cursorYPos;
      this._cursorXPos = x;
      this._cursorYPos = y;
      return cursorMoved;
   }

   private boolean moveCursor(int dx, int dy) {
      return this.moveCursorTo(this._cursorXPos + dx, this._cursorYPos + dy);
   }

   private Cursor getDefaultCursor() {
      int zoomValue = this.getZoomValue();
      int index;
      if (zoomValue == 65536) {
         if (this._maxZoomValue != 65536 && (this._lastKeypadStatus & 17) != 0) {
            index = 4;
         } else {
            index = 0;
         }
      } else if ((this._lastKeypadStatus & 17) != 0) {
         if (zoomValue == this._maxZoomValue) {
            index = 0;
         } else {
            index = 4;
         }
      } else {
         index = 3;
      }

      return Cursor.getPredefinedCursor(index);
   }

   private void repaintCursor(Graphics g, boolean updateOverlay) {
      if (this.usingCursor() && !this._sendMoveToFocus) {
         if (g == null) {
            g = this.getGraphics0();
         }

         int drawXPos = this._cursorXPos;
         int drawYPos = this._cursorYPos;
         if (this._currentCursor != null) {
            drawXPos -= this._currentCursor.getOriginX();
            drawYPos -= this._currentCursor.getOriginY();
         }

         if (!updateOverlay) {
            updateOverlay = !g.setOverlayPosition(4, drawXPos, drawYPos);
         }

         if (updateOverlay) {
            Bitmap cursorBitmap = this._currentCursor == null ? null : this._currentCursor.getBitmap();
            g.setOverlay(4, cursorBitmap, drawXPos, drawYPos, true);
         }
      }
   }

   private void updateCursor(Cursor cursorToDisplay) {
      if (this._currentCursor != cursorToDisplay) {
         this._currentCursor = cursorToDisplay;
         this.repaintCursor(null, true);
      }
   }

   private boolean handleCursorMove() {
      if (!this.usingCursor()) {
         return false;
      }

      this.setSendMoveToFocus(false);
      boolean updateOverlay = false;
      int zoomValue = this.getZoomValue();
      int unscaledCursorX = RendererControl.fixed32MultToInt(this._cursorXPos, zoomValue);
      int unscaledCursorY = RendererControl.fixed32MultToInt(this._cursorYPos, zoomValue);
      int focusIndex = this._rootCell.getFocusIndexFromXYPosition(unscaledCursorX, unscaledCursorY);
      if (focusIndex != this._focusIndex && this._focusIndex != -1) {
         if (this._currentCursor != this.getDefaultCursor()) {
            this._currentCursor = this.getDefaultCursor();
            updateOverlay = true;
         }
      } else if (this._cursorFocusRect.contains(this._cursorXPos, this._cursorYPos)) {
         if (this._currentCursor == null) {
            return false;
         }
      } else if (this._currentCursor != this.getDefaultCursor()) {
         this._currentCursor = this.getDefaultCursor();
         updateOverlay = true;
      }

      this.repaintCursor(null, updateOverlay);
      Screen screen = this.getScreen();
      if (screen != null) {
         Application app = screen.getApplication();
         if (app != null) {
            if (this._hoverRunnableId != -1) {
               app.cancelInvokeLater(this._hoverRunnableId);
            }

            this._hoverRunnableId = app.invokeLater(this._hoverRunnable, 50, false);
         }
      }

      return true;
   }

   @Override
   public void setVerticalScroll(int y) {
      this.scrollViewTo(this.getHorizontalScroll(), y, false);
   }

   @Override
   public void setHorizontalScroll(int x) {
      this.scrollViewTo(x, this.getVerticalScroll(), false);
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      this._lastKeypadStatus = status;
      if (!this.usingCursor() && super.navigationMovement(dx, dy, status, time)) {
         return true;
      }

      if (this._mode != 1) {
         return false;
      }

      if (this._sendMoveToFocus) {
         return false;
      }

      if (this.usingCursor()) {
         if (this.moveCursor(dx, dy)) {
            Cursor cursor = this._currentCursor != null ? this._currentCursor : this.getDefaultCursor();
            XYRect cursRect = Ui.getTmpXYRect();
            cursRect.set(
               this._cursorXPos - cursor.getOriginX(), this._cursorYPos - cursor.getOriginY(), cursor.getBitmap().getWidth(), cursor.getBitmap().getHeight()
            );
            this.unscaleRect(cursRect);
            this.getScreen().ensureRegionVisible(this._focusField, cursRect.x, cursRect.y, cursRect.width, cursRect.height);
            Ui.returnTmpXYRect(cursRect);
            if (this.handleCursorMove()) {
               return true;
            }
         }

         return this.getFieldWithFocus() instanceof FrameManager;
      } else if ((status & 1) != 0) {
         if (dy != 0) {
            Screen screen = this.getScreen();
            if (screen != null) {
               screen.scroll(dy > 0 ? 512 : 256);
            }
         }

         return true;
      } else {
         if (_oneDNavigationMode && (status & 536870912) != 0 && dx != 0 && dy == 0) {
            Field f = this.getLeafFieldWithFocus();
            if (!(f instanceof TextField)) {
               Screen screen = this.getScreen();
               if (screen != null) {
                  screen.scroll(dx > 0 ? 512 : 256);
               }

               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean setFocus(int x, int y, int status) {
      this._cursorXPos = x;
      this._cursorYPos = y;
      return super.setFocus(x, y, status);
   }

   @Override
   public void setFont(Font f) {
      super.setFont(f);
      this._textFlowData.setDefaultFont(f);
   }

   private static long validateStyle(long style) {
      if ((style & 562949953421312L) == 0) {
         style |= 281474976710656L;
      }

      style |= 144115188075855872L;
      if ((style & 2251799813685248L) == 0) {
         style |= 1125899906842624L;
      }

      return style;
   }

   public long getAnchor() {
      return this._textFlowData.getTextLength();
   }

   public long getCurrentAnchor() {
      return this._focusIndex != -1 && this._focusState == 0
         ? this._textFlowData.getRegionStartOffset(this._textFlowData.getFocusRegion(this._focusIndex))
         : this._rootCell.getTextPositionFromYOffset(-1, Math.max(0, this.getCurrentScroll()), false);
   }

   public void jumpToAnchor(long anchor) {
      if (this.getScreen() != null) {
         int pos = this.getTextPositionFromAnchor(anchor);
         int y = this._rootCell.getYOffsetFromTextPosition(pos, true);
         y = RendererControl.fixed32DivToInt(y, this.getZoomValue());
         this.scrollViewTo(0, y, false);
         if (this.moveCursorTo(0, y)) {
            this.handleCursorMove();
         }

         switch (this._mode) {
            case 2:
            default:
               this._selectionEnd = pos;
            case 3:
               this._focusField.updateSelection(pos);
            case 1:
         }
      }
   }

   public void jumpToAnchor(Object anchor) {
      if (anchor != null) {
         this.jumpToAnchor(this._textFlowData.getAnchorFromRegionObject(anchor));
      }
   }

   public long find(StringMatch matcher, boolean findNext, long anchor) {
      int offset;
      if (anchor == Long.MIN_VALUE) {
         if (this._mode != 2 && this._mode != 3) {
            offset = 0;
         } else {
            offset = Math.max(this._selectionPosition.textPosition, 0);
         }
      } else {
         offset = this.getTextPositionFromAnchor(anchor);
         if (findNext) {
            offset++;
         }
      }

      StringBuffer buff = this._textFlowData.getText();
      if (buff.length() <= offset) {
         return -9223372036854775807L;
      }

      int match = matcher.indexOf(buff, offset);
      if (match == -1) {
         return -9223372036854775807L;
      }

      anchor = this.getAnchorFromTextPosition(match);
      if (this.getScreen() != null) {
         this.jumpToAnchor(anchor);
      }

      return anchor;
   }

   public void setLayoutActive(boolean on) {
      if (on) {
         if (!this._layoutActive) {
            this._layoutActive = true;
            this._focusField.delegateUpdateLayout();
            return;
         }
      } else {
         this._layoutActive = false;
      }
   }

   public void appendText(String text) {
      this.checkLayoutActive();
      this.appendText(text, 0, -1);
   }

   public void appendText(String text, int offset, int length) {
      int textLength = text.length();
      Asserts.productionArgumentAssert(offset >= 0 && offset < textLength && length >= -1);
      if (length == -1) {
         length = textLength - offset;
      }

      Asserts.productionArgumentAssert(offset + length <= textLength);
      this.checkLayoutActive();
      this._currentCell.appendText(text, offset, length);
   }

   public void setBackgroundImage(Object cell, EncodedImage img) {
      TextFlowCell tfc = (TextFlowCell)cell;
      tfc.setBackgroundImage(img);
   }

   public void regionChanged(TextFlowRegion region, boolean relayout) {
      this.assertHaveEventLock();
      this.checkLayoutActive();
      if (!relayout) {
         this.invalidate();
      } else {
         int regionIndex = this._textFlowData.findRegionIndex(region);
         if (regionIndex != -1) {
            this.invalidateRegion(regionIndex);
         }
      }
   }

   public void setBackgroundStyle(Object cell, EncodedImage img, boolean fixed, boolean repeatX, boolean repeatY, int posX, int posY) {
      TextFlowCell tfc = (TextFlowCell)cell;
      tfc.setBackgroundImage(img);
      tfc.setBackgroundImageFixed(fixed);
      tfc.setBackgroundImageRepeat(repeatX, repeatY);
      tfc.setBackgroundImagePos(posX, posY);
   }

   public boolean isBackgroundImageSet() {
      return this._currentCell.isBackgroundImageSet();
   }

   public Object getCurrentCell() {
      return this._currentCell;
   }

   public int getCurrentContentWidth() {
      return this._currentCell.getContentWidth();
   }

   public void setWidth(Object cell, int newWidth) {
      this.checkLayoutActive();
      ((TextFlowCell)cell).setWidth(newWidth);
   }

   private void invalidateRegion(int regionIndex) {
      this.setInvalidStartEnd(this._textFlowData.getRegionStartOffset(regionIndex), this._textFlowData.getRegionEndOffset(regionIndex));
   }

   void setInvalidStartEnd(int start, int end) {
      this._invalidLayoutStart = Math.min(this._invalidLayoutStart, start);
      this._invalidLayoutEnd = Math.max(this._invalidLayoutEnd, end);
   }

   public int getPreferredWidth(Object cell) {
      return ((TextFlowCell)cell).getPreferredWidth();
   }

   public int getPreferredWidthOfChildPublic(Field field) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public int getMinPreferredWidth(Object cell) {
      return ((TextFlowCell)cell).getMinPreferredWidth();
   }

   public boolean isFocusRegionOpen() {
      return this._textFlowData.isFocusRegionOpen();
   }

   public void startFocusRegion(Object objCookie, long scalarCookie) {
      this._textFlowData.startFocusRegion(objCookie, scalarCookie);
   }

   public void endFocusRegion() {
      this._textFlowData.endFocusRegion();
   }

   public void appendInlineField(Field field) {
      this.checkLayoutActive();
      long fieldStyle = field.getStyle() & 51539607552L;
      int alignFlags = 0;
      if (fieldStyle == 34359738368L) {
         alignFlags = 4096;
      } else if (fieldStyle == 17179869184L) {
         alignFlags = 8192;
      } else if (fieldStyle == 51539607552L) {
         alignFlags = 12288;
      }

      this._currentCell.appendInlineField(field, alignFlags);
      super.add(field);
   }

   public void appendOutOfLineField(Field field) {
      this.checkLayoutActive();
      this._currentCell.appendOutOfLineField(field);
      super.add(field);
   }

   public void pushRegion(TextFlowRegion region) {
      this.checkLayoutActive();
      this._textFlowData.pushRegion(region);
   }

   public void popRegion() {
      this.checkLayoutActive();
      this._textFlowData.popRegion();
   }

   public void pushCell(int width, int animationStyle, int scrollDelay, int scrollAmount, int loopCount, Border border) {
      this.checkLayoutActive();
      if (this._cellStack.size() < 40) {
         TextFlowCell child = new TextFlowCell(
            this,
            width,
            (TextFlowCell)this._cellStack.peek(),
            this._textFlowData.getNewCellId(),
            this._textFlowData.getOutOfLineObjectCount(),
            animationStyle,
            scrollDelay,
            scrollAmount,
            loopCount,
            border
         );
         this._currentCell.appendOutOfLineCell(child, 1, 1);
         this._cellStack.push(child);
         this._currentCell = child;
         AnimationThread.addAnimation(child);
      } else {
         this._cellStack.push(this._currentCell);
      }
   }

   public void pushCell(
      int width, int preferredWidth, boolean table, int cellFlags, Border border, int cellSpacing, int cellPadding, int colSpan, int rowSpan, int minHeight
   ) {
      this.checkLayoutActive();
      if (this._cellStack.size() < 40) {
         TextFlowCell child = new TextFlowCell(
            this,
            width,
            border,
            preferredWidth,
            cellSpacing,
            cellPadding,
            (TextFlowCell)this._cellStack.peek(),
            table,
            cellFlags,
            this._textFlowData.getNewCellId(),
            this._textFlowData.getOutOfLineObjectCount(),
            minHeight
         );
         this._currentCell.appendOutOfLineCell(child, colSpan, rowSpan);
         this._cellStack.push(child);
         this._currentCell = child;
      } else {
         this._cellStack.push(this._currentCell);
      }
   }

   public void popCell() {
      this.checkLayoutActive();
      if (this._cellStack.size() < 40) {
         this._currentCell.setEndValues();
         this._textFlowData.popOutOfLine();
         this._cellStack.pop();
         this._currentCell = (TextFlowCell)this._cellStack.peek();
      } else {
         this._cellStack.pop();
      }
   }

   public void finishRow() {
      this.checkLayoutActive();
      this._currentCell.finishRow();
   }

   public int appendOutOfLineFieldAlt(Field field, String text, int offset, int length) {
      this.checkLayoutActive();
      super.add(field);
      return this._currentCell.appendOutOfLineFieldAlt(field, text, offset, length);
   }

   public void toggleOutOfLineFieldAlt(int tag, boolean text) {
      this.checkLayoutActive();
      this._textFlowData.toggleOutOfLineObjectAlt(tag, text);
      int cellId = this._textFlowData.getRegionCellOOLIndex(this._textFlowData.getOutOfLineObjectRegion(tag));
      TextFlowCell cell;
      if (cellId == -1) {
         cell = this._rootCell;
      } else {
         cell = (TextFlowCell)this._textFlowData.getOutOfLineObject(cellId);
      }

      cell.fieldWidthAdjusted(-1, -1);
      int start = this._textFlowData.getOutOfLineObjectOffset(tag);
      this.setInvalidStartEnd(start, start + this._textFlowData.getOutOfLineObjectLength(tag));
   }

   public void forceNewline() {
      this.checkLayoutActive();
      this._currentCell.forceNewline();
   }

   public Object getFocusCookie() {
      return this._focusIndex != -1 && this._focusState == 0 ? this._textFlowData.getFocusRegionCookie(this._focusIndex) : null;
   }

   public long getFocusScalarCookie() {
      return this._focusIndex != -1 && this._focusState == 0 ? this._textFlowData.getFocusRegionScalarCookie(this._focusIndex) : 0;
   }

   private int getScrollingOffset() {
      int offset = 0;

      for (Manager mgr = this; mgr != this._scrollingParent; mgr = mgr.getManager()) {
         offset += mgr.getTop();
      }

      return offset;
   }

   private int getScrollingOffsetH() {
      int offset = 0;

      for (Manager mgr = this; mgr != this._scrollingParent; mgr = mgr.getManager()) {
         offset += mgr.getLeft();
      }

      return offset;
   }

   private int getVerticalVisibleScroll() {
      int height = Integer.MAX_VALUE;

      for (Manager mgr = this._scrollingParent; mgr != null; mgr = mgr.getManager()) {
         height = Math.min(height, mgr.getContentHeight());
      }

      if (this._scrollingParent != null) {
         height -= Math.max(0, this.getScrollingOffset() - this._scrollingParent.getVerticalScroll());
      }

      return height;
   }

   private int getHorizontalVisibleScroll() {
      int width = Integer.MAX_VALUE;

      for (Manager mgr = this; mgr != null; mgr = mgr.getManager()) {
         width = Math.min(width, mgr.getContentWidth());
      }

      if (this._scrollingParent != null) {
         width -= Math.max(0, this.getScrollingOffsetH() - this._scrollingParent.getHorizontalScroll());
      }

      return width;
   }

   private int getRegionIndexFromField(Field field) {
      int lookIndex = field.getIndex();
      int low = 0;
      int high = this._textFlowData.getRegionCount() - 1;

      while (low <= high) {
         int trueMid = low + high >> 1;
         int mid = this.findFirstNonNullField(low, trueMid, high);
         if (mid == -1) {
            Asserts.productionArgumentAssert(false);
         }

         Field midObject = (Field)this._textFlowData.getRegionObject(mid);
         int midIndex = midObject.getIndex();
         if (lookIndex < midIndex) {
            high = mid - 1;
         } else if (lookIndex > midIndex) {
            low = Math.max(mid + 1, trueMid + 1);
         } else {
            if (midObject == field) {
               return mid;
            }

            high = mid;
         }
      }

      Asserts.productionArgumentAssert(false);
      return -1;
   }

   private int findFirstNonNullField(int low, int mid, int high) {
      int offset = mid;
      if (this._textFlowData.getRegionObject(offset) instanceof Field) {
         return offset;
      }

      while (low < offset) {
         if (this._textFlowData.getRegionObject(--offset) instanceof Field) {
            return offset;
         }
      }

      offset = mid;

      while (offset < high) {
         if (this._textFlowData.getRegionObject(++offset) instanceof Field) {
            return offset;
         }
      }

      return -1;
   }

   @Override
   public void invalidate() {
      XYRect extent = this.getExtent();
      super.invalidate(this.getHorizontalScroll(), this.getVerticalScroll(), extent.width, extent.height);
   }

   @Override
   protected void invalidate(int x, int y, int width, int height) {
      int zoom = this.getZoomValue();
      if (zoom != 65536) {
         x = RendererControl.fixed32DivToInt(x, zoom);
         y = RendererControl.fixed32DivToInt(y, zoom);
         width = RendererControl.fixed32DivToInt(width, zoom);
         height = RendererControl.fixed32DivToInt(height, zoom);
      }

      super.invalidate(x, y, width, height);
   }

   private void invalidateField(Field field) {
      int regionIndex = this.getRegionIndexFromField(field);
      this.invalidateRegion(regionIndex);
      boolean wasDirty = false;
      if (this._textFlowData.isRegionInline(regionIndex)) {
         wasDirty = this._textFlowData.isRegionDirty(regionIndex);
         this._textFlowData.setRegionDirty(regionIndex, true);
      } else {
         int oolIndex = this._textFlowData.getOutoflineIndexFromRegionIndex(regionIndex);
         wasDirty = this._textFlowData.isRegionDirty(oolIndex);
         this._textFlowData.setOutOfLineDirty(oolIndex, true);
      }

      if (!wasDirty) {
         int cellOolIndex = this._textFlowData.getRegionCellOOLIndex(regionIndex);
         TextFlowCell cell = cellOolIndex < 0 ? this._rootCell : (TextFlowCell)this._textFlowData.getOutOfLineObject(cellOolIndex);
         cell.fieldWidthAdjusted(-1, field.getPreferredWidth());
      }
   }

   private void invalidateLayout(boolean forceCellReflows) {
      this.setInvalidStartEnd(0, this._textFlowData.getTextLength());

      for (int ool = this._textFlowData.getOutOfLineObjectCount() - 1; ool >= 0; ool--) {
         if (forceCellReflows) {
            Object obj = this._textFlowData.getOutOfLineObject(ool);
            if (obj instanceof TextFlowCell) {
               ((TextFlowCell)obj).forceReflow();
            }
         }

         this._textFlowData.setOutOfLineDirty(ool, true);
      }

      if (forceCellReflows) {
         this._rootCell.forceReflow();
      }

      for (int il = this._textFlowData.getRegionCount() - 1; il >= 0; il--) {
         this._textFlowData.setRegionDirty(il, true);
      }
   }

   private int getTextPositionFromAnchor(long anchor) {
      if (anchor == Long.MIN_VALUE) {
         anchor = 0;
      }

      Asserts.productionArgumentAssert(anchor >= 0 && anchor <= this._textFlowData.getTextLength());
      return (int)anchor;
   }

   private long getAnchorFromTextPosition(int pos) {
      return pos;
   }

   private final void checkLayoutActive() {
      Asserts.productionStateAssert(!this.isValidLayout() || !this._layoutActive);
   }

   @Override
   protected void makeFocusVisible(boolean immediate, XYRect region, boolean draw, boolean reset) {
      if (this._setScroll != Integer.MAX_VALUE) {
         region.set(this.getHorizontalScroll(), this._setScroll, this.getHorizontalVisibleScroll(), this.getVerticalVisibleScroll());
         this._currentScroll = this._setScroll;
         this._setScroll = Integer.MAX_VALUE;
         super.makeFocusVisible(immediate, region, draw, reset);
      } else if (this.getFieldWithFocus() != this._focusField) {
         this.scaleRect(region);
         if (this.getFieldWithFocus() instanceof FrameManager
            || region.width < this.getHorizontalVisibleScroll() >> 1 && region.height < this.getVerticalVisibleScroll() >> 1) {
            super.makeFocusVisible(immediate, region, draw, reset);
            this._currentScroll = (this._scrollingParent != null ? this._scrollingParent.getVerticalScroll() : this.getVerticalScroll())
               - this.getScrollingOffset();
         }
      } else {
         if (this.usingCursor() || this._mode != 1) {
            this.scaleRect(region);
            super.makeFocusVisible(immediate, region, draw, reset);
            this._currentScroll = (this._scrollingParent != null ? this._scrollingParent.getVerticalScroll() : this.getVerticalScroll())
               - this.getScrollingOffset();
         }
      }
   }

   public boolean isPositioningOrSelecting() {
      return this._mode == 2 || this._mode == 3;
   }

   public BrowserBitmapField getSelectedImage() {
      if (this._mode == 2 && this._selectionPosition.selectedRegion != -1) {
         Object selectedObject = this._textFlowData.getRegionObject(this._selectionPosition.selectedRegion);
         if (selectedObject instanceof BrowserBitmapField) {
            return (BrowserBitmapField)selectedObject;
         }
      }

      return null;
   }

   @Override
   public void setFieldWithFocus(Field child) {
      super.setFieldWithFocus(child);
      if (child != this._focusField) {
         for (int i = 0; i < this._layoutFocusCount; i++) {
            int regionIndex = this._textFlowData.getFocusRegion(i);
            int endRegion = regionIndex + this._textFlowData.getNumSubRegions(regionIndex) + 1;

            for (int j = regionIndex; j < endRegion; j++) {
               boolean matched = !this._textFlowData.isRegionText(j) && this._textFlowData.getFocusRegionCookie(i) == child
                  || this._textFlowData.getRegionObject(j) == child;
               if (matched) {
                  this._focusIndex = i;
                  this._focusState = 0;
                  return;
               }
            }
         }

         Asserts.productionAssert(false);
      }
   }

   private int getCurrentScroll() {
      return this._currentScroll;
   }

   int getCurrentTextPosition() {
      switch (this._mode) {
         case 0:
            Asserts.productionAssert(false);
            return 0;
         case 1:
         default:
            return this._rootCell.getTextPositionFromYOffset(-1, this._currentScroll, false);
         case 2:
            if (this._selectionPosition.textPosition >= 0) {
               return this._selectionPosition.textPosition;
            }

            return this._textFlowData.getRegionStartOffset(this._selectionPosition.selectedRegion);
         case 3:
            return this._selectionStart;
      }
   }

   private int calculateCurrentScroll() {
      if (this._setScroll != Integer.MAX_VALUE) {
         return this._setScroll;
      } else {
         return this._scrollingParent == null ? 0 : this._scrollingParent.getVerticalScroll() - this.getScrollingOffset();
      }
   }

   @Override
   protected int firstFocus(int direction) {
      int startIndex;
      if (direction > 0) {
         this._setScroll = 0;
         startIndex = 0;
      } else {
         if (direction == 0) {
            return -1;
         }

         this._setScroll = Math.max(0, this.getVirtualHeight() - this.getVerticalVisibleScroll());
         startIndex = this._layoutFocusCount - 1;
      }

      if (this._layoutFocusCount == 0) {
         this._focusIndex = -1;
         this._focusState = direction;
         return this._focusField.getIndex();
      }

      int index = this.getNextFocusIndex(startIndex, -direction, direction, false);
      if (index != -1) {
         this.getFocusRectFromIndex(index, this._rect);
         if (this.isRectVisible(this._setScroll, this._rect, this._visibleAmount)) {
            this._focusIndex = index;
            this._focusState = 0;
            return this.getFocusFieldFromIndex(index).getIndex();
         }
      }

      this._focusIndex = startIndex;
      this._focusState = -direction;
      return this._focusField.getIndex();
   }

   private int requiredScrollAdjust(XYRect rect, int scroll, int direction) {
      return direction < 0 ? Math.min(rect.y - scroll, 0) : Math.max(rect.y + rect.height - (scroll + this.getVerticalVisibleScroll()), 0);
   }

   @Override
   protected int nextFocus(int direction, int axis) {
      return this.nextFocus(direction, false);
   }

   @Override
   protected int nextFocus(int direction, boolean alt) {
      if (this._mode != 1 || !this.usingCursor()) {
         int amount = this._rollAmount;
         if (this._rollAmount != Integer.MAX_VALUE) {
            if (this._rollAmount == 0) {
               return this.getFieldWithFocusIndex();
            }

            this._rollAmount = 0;
         }

         int virtualHeight = this._scrollingParent != null ? this._scrollingParent.getVirtualHeight() : this.getVirtualHeight();
         int visibleScroll = this.getVerticalVisibleScroll();
         int scrollingOffset = this.getScrollingOffset();
         int scroll = 0;
         if (this._focusState == 0 && amount > 1) {
            TextFlowRegion region = this._textFlowData.getRegion(this._textFlowData.getFocusRegion(this._focusIndex));
            scroll = region.getOffsetYBottom();
            int currentScroll = this.getCurrentScroll();
            if (scroll < currentScroll) {
               scroll = currentScroll;
            }
         } else {
            scroll = this.getCurrentScroll();
         }

         int maxScroll = Math.max(virtualHeight - scrollingOffset - visibleScroll, 0);
         int nominalScroll = scroll + amount * this._scrollAmount;
         if (nominalScroll > maxScroll) {
            nominalScroll = maxScroll;
         }

         scrollingOffset *= -1;
         if (nominalScroll < scrollingOffset) {
            nominalScroll = scrollingOffset;
         }

         int nominalScrollAdjust = Math.abs(nominalScroll - scroll);
         if (this._inFindNewFocus) {
            this._focusState = -1;
            return this._focusField.getIndex();
         }

         if (direction < 0 && this._focusIndex <= 0 && scroll <= 0) {
            this._setScroll = 0;
            this._leaving = true;
            return -1;
         }

         if (direction > 0 && scroll >= this.getVirtualHeight() - visibleScroll) {
            if (this._focusIndex >= this._layoutFocusCount - 1) {
               this._setScroll = maxScroll;
               this._leaving = true;
               return -1;
            }

            amount = MathUtilities.clamp(-1, amount, 1);
         }

         int index = -1;
         if (Math.abs(amount) < 4) {
            if (this._verticalNavigationMovementAmount == 0) {
               index = this.getNextFocusIndex(this._focusIndex, this._focusState, direction, (this._lastStatus & 2) != 0);
            }

            if (this._verticalNavigationMovementAmount == 0) {
               if (index != -1) {
                  this.getFocusRectFromIndex(index, this._rect);
                  this._setScroll = scroll
                     + MathUtilities.clamp(-nominalScrollAdjust, this.requiredScrollAdjust(this._rect, scroll, direction), nominalScrollAdjust);
                  if (this.isRectVisible(this._setScroll, this._rect, this._visibleAmount)) {
                     this._focusIndex = index;
                     this._focusState = 0;
                     return this.getFocusFieldFromIndex(index).getIndex();
                  }
               }
            } else {
               index = this.getNextFocusIndexVertically(this._focusIndex, this._focusState, direction, nominalScrollAdjust);
               if (index != -1) {
                  this._focusIndex = index;
                  this._focusState = 0;
                  return this.getFocusFieldFromIndex(index).getIndex();
               }
            }

            this._setScroll = nominalScroll;
            if (this._focusState == 0) {
               this.getFocusRectFromIndex(this._focusIndex, this._rect);
               if (!this.isRectVisible(nominalScroll, this._rect, 1)) {
                  this._focusState = direction;
                  return this._focusField.getIndex();
               }
            }

            return -1;
         } else {
            this._focusSearchDirection = direction;
            this._setScroll = nominalScroll;
            return this.getFieldClosestToLocation(0, nominalScroll, this._lastStatus);
         }
      } else {
         return this._sendMoveToFocus ? this.getFieldWithFocusIndex() : this._focusField.getIndex();
      }
   }

   private int getNextFocusIndexVertically(int focusIndex, int focusState, int direction, int nominalScrollAdjust) {
      int index = this.getNextFocusIndex(focusIndex, focusState, direction, false);
      if (index < 0) {
         return index;
      }

      int currentScroll = this.getCurrentScroll();
      if (focusIndex >= 0 && focusIndex < this._layoutFocusCount) {
         this.getFocusRectFromIndex(focusIndex, this._rect, true, true);
         int startX1 = this._rect.x;
         int startX2 = this._rect.X2();
         int currentY1 = this._rect.y;
         int currentY2 = this._rect.Y2();
         int nextLineIndex = -1;
         int nextLineScroll = -1;
         boolean foundNextLine = false;

         do {
            this.getFocusRectFromIndex(index, this._rect, false, true);
            int requiredScroll = currentScroll
               + MathUtilities.clamp(-nominalScrollAdjust, this.requiredScrollAdjust(this._rect, currentScroll, direction), nominalScrollAdjust);
            if (this.isRectVisible(requiredScroll, this._rect, this._visibleAmount)) {
               if (direction > 0 && this._rect.y >= currentY2 || direction < 0 && this._rect.Y2() <= currentY1) {
                  if (foundNextLine) {
                     break;
                  }

                  foundNextLine = true;
                  nextLineIndex = index;
                  nextLineScroll = requiredScroll;
                  currentY1 = this._rect.y;
                  currentY2 = this._rect.Y2();
               }

               if (foundNextLine) {
                  this.getFocusRectFromIndex(index, this._rect, true, true);
                  if (direction > 0 && (this._rect.x >= startX1 || this._rect.X2() >= startX2)
                     || direction < 0 && (this._rect.X2() <= startX2 || this._rect.x <= startX1)) {
                     nextLineIndex = index;
                     nextLineScroll = requiredScroll;
                     break;
                  }
               }
            }

            index = this.getNextFocusIndex(index, 0, direction, false);
         } while (index >= 0);

         if (nextLineIndex >= 0) {
            this._setScroll = nextLineScroll;
            return nextLineIndex;
         } else {
            return -1;
         }
      } else {
         this.getFocusRectFromIndex(index, this._rect, false, true);
         int requiredScroll = currentScroll
            + MathUtilities.clamp(-nominalScrollAdjust, this.requiredScrollAdjust(this._rect, currentScroll, direction), nominalScrollAdjust);
         if (this.isRectVisible(requiredScroll, this._rect, this._visibleAmount)) {
            this._setScroll = requiredScroll;
            return index;
         } else {
            return -1;
         }
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      if (this._mode == 1 && this.usingCursor()) {
         return super.moveFocus(amount, status, time);
      }

      this._inFindNewFocus = (status & 32768) != 0;
      this._lastStatus = status;
      this._rollAmount = amount;
      if (!_oneDNavigationMode && (status & 131072) != 0) {
         this._verticalNavigationMovementAmount = amount;
      } else if ((status & 65536) != 0) {
         this._rollAmount = MathUtilities.clamp(-1, amount, 1);
      }

      int unused = super.moveFocus(amount, status, time);
      this._verticalNavigationMovementAmount = 0;
      this._rollAmount = Integer.MAX_VALUE;
      this._inFindNewFocus = false;
      this._lastStatus = 0;
      if (this._leaving) {
         this._leaving = false;
         return unused;
      } else {
         return 0;
      }
   }

   private Field getFocusFieldFromIndex(int focusIndex) {
      int regionIndex = this._textFlowData.getFocusRegion(focusIndex);
      if (this._textFlowData.isRegionText(regionIndex)) {
         return this._focusField;
      }

      Object regionObj = this._textFlowData.getRegionObject(regionIndex);
      if (regionObj == null && this._textFlowData.getNumSubRegions(regionIndex) == 1 && !this._textFlowData.isRegionText(regionIndex + 1)) {
         regionObj = this._textFlowData.getRegionObject(regionIndex + 1);
      }

      return regionObj != null && regionObj instanceof Field ? (Field)regionObj : this._focusField;
   }

   private void getFocusRectFromIndex(int focusIndex, XYRect rect) {
      this.getFocusRectFromIndex(focusIndex, rect, false, true);
   }

   private void getFocusRectFromIndex(int focusIndex, XYRect rect, boolean accurateXCoordsNeeded, boolean entireFieldRegion) {
      Field field = this.getFocusFieldFromIndex(focusIndex);
      if (field == this._focusField) {
         this._rootCell.getTextFocusRect(focusIndex, rect, accurateXCoordsNeeded);
      } else if (entireFieldRegion) {
         rect.set(field.getLeft(), field.getTop(), field.getWidth(), field.getHeight());
      } else {
         field.getFocusRect(rect);
         rect.translate(field.getLeft(), field.getTop());
      }

      this.scaleRect(rect);
   }

   private int getNextFocusIndex(int currentIndex, int focusState, int direction, boolean horizontal) {
      if (currentIndex >= -1 && currentIndex < this._layoutFocusCount) {
         Asserts.productionAssert(direction == -1 || direction == 1);
         if (focusState == direction) {
            focusState = 0;
         }

         currentIndex = this._rootCell.getNextFocusIndex(currentIndex, focusState, direction, horizontal);
         return currentIndex >= 0 && currentIndex < this._layoutFocusCount ? currentIndex : -1;
      } else {
         return -1;
      }
   }

   private boolean isRectVisible(int scroll, XYRect rect, int requiredPixels) {
      this._visibleRect.set(0, scroll, this.getWidth(), this.getVerticalVisibleScroll());
      this._visibleRect.intersect(rect);
      return rect.height == this._visibleRect.height ? true : this._visibleRect.height >= requiredPixels;
   }

   private boolean isTopmostTFM() {
      return !(this.getManager() instanceof FrameManager);
   }

   private TextFlowManager getTopmostTFM() {
      TextFlowManager tfm = this;

      for (Manager m = this.getManager(); m != null; m = m.getManager()) {
         if (m instanceof TextFlowManager) {
            tfm = (TextFlowManager)m;
         }
      }

      return tfm;
   }

   private boolean setSendMoveToFocus(boolean value) {
      if (this._sendMoveToFocus != value) {
         if (value) {
            this.updateCursor(null);
            this.trackballSensitivityOff();
            this._sendMoveToFocus = value;
            return true;
         } else {
            this._sendMoveToFocus = value;
            this.trackballSensitivityOn();
            this.updateCursor(this.getDefaultCursor());
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      this._lastKeypadStatus = status;
      if (this.setSendMoveToFocus(false)) {
         return true;
      }

      boolean usingCursor = this.usingCursor();
      if (usingCursor) {
         status |= 32768;
      }

      if (super.navigationClick(status, time)) {
         return true;
      }

      if (!this.isTopmostTFM()) {
         return false;
      }

      if (this._mode == 1 && usingCursor) {
         int zoomValue = this.getZoomValue();
         if (this._focusIndex != -1 && this._cursorFocusRect.contains(this._cursorXPos, this._cursorYPos) && zoomValue <= HOTSPOT_ACTIVATION_THRESHOLD) {
            Field field = this.getFocusFieldFromIndex(this._focusIndex);
            if (field != null) {
               if (this.getFieldWithFocus() != field) {
                  field.setFocus();
               }

               boolean ret = this.stylusTap(
                  RendererControl.fixed32MultToInt(this._cursorXPos, zoomValue) - this.getHorizontalScroll(),
                  RendererControl.fixed32MultToInt(this._cursorYPos, zoomValue) - this.getVerticalScroll(),
                  status,
                  time
               );
               if (field instanceof TextInputField || field instanceof HTMLTextAreaField) {
                  return this.setSendMoveToFocus(true);
               }

               if (ret) {
                  return true;
               }
            }

            return this._focusState != 0;
         } else {
            TextFlowManager$ViewModeRunnable.getInstance().kick(this);
            return true;
         }
      } else if ((status & 1073741824) == 0 && (status & 1) != 0 && this._mode == 1) {
         this._positionItem.run();
         return true;
      } else {
         return false;
      }
   }

   public void adjustZoom(int direction) {
      if (this._mode == 1) {
         int oldZoomValue = this.getZoomValue();
         direction = MathUtilities.clamp(-ZOOM_VALUES.length, direction, ZOOM_VALUES.length);
         int newZoomValue = oldZoomValue;
         int startSearchIndex = direction < 0 ? ZOOM_VALUES.length - 1 : 0;

         do {
            int nextSearchIndex = MathUtilities.clamp(0, startSearchIndex + direction, ZOOM_VALUES.length - 1);
            if (nextSearchIndex == startSearchIndex) {
               break;
            }

            startSearchIndex = nextSearchIndex;
            this._currentZoomValue = ZOOM_VALUES[nextSearchIndex];
            newZoomValue = this.getZoomValue();
         } while ((direction >= 0 || newZoomValue >= oldZoomValue) && (direction <= 0 || newZoomValue <= oldZoomValue));

         if (newZoomValue != oldZoomValue) {
            int cursorXOffset = this._cursorXPos - this.getHorizontalScroll();
            int cursorYOffset = this._cursorYPos - this.getVerticalScroll();
            this.updateLayout();
            this._rootCell.revalidateManagers();
            int zoomRatio = Fixed32.div(oldZoomValue, newZoomValue);
            int newCenterXPos = RendererControl.fixed32MultToInt(this._cursorXPos, zoomRatio);
            int newCenterYPos = RendererControl.fixed32MultToInt(this._cursorYPos, zoomRatio);
            this.scrollViewTo(newCenterXPos - (this.getHorizontalVisibleScroll() >> 1), newCenterYPos - (this.getVerticalVisibleScroll() >> 1), false);
            if (this.moveCursorTo(this.getHorizontalScroll() + cursorXOffset, this.getVerticalScroll() + cursorYOffset)) {
               this.handleCursorMove();
            }

            this.getScreen().invalidate();
         }
      }
   }

   @Override
   protected boolean navigationUnclick(int status, int time) {
      if (TextFlowManager$ViewModeRunnable.getInstance().cancel()) {
         boolean alt = (this._lastKeypadStatus & 1) != 0;
         this._lastKeypadStatus = status;
         if (alt) {
            this.adjustZoom(1);
            return true;
         } else {
            this.adjustZoom(-1);
            return true;
         }
      } else {
         this._lastKeypadStatus = status;
         return super.navigationUnclick(status, time) ? true : (status & 1) != 0;
      }
   }

   @Override
   protected boolean incrementalLayout(int index, int added, int deleted) {
      if (index != 0 && added == 1 && deleted == 1) {
         this.invalidateField(this.getField(index));
      }

      if (!this._layoutActive) {
         return true;
      }

      if ((this.getStyle() & 1) != 0) {
         return false;
      }

      this.layout0(this._layoutWidth, this._layoutHeight);
      int lastLayoutStartYscaled = RendererControl.fixed32DivToInt(this._lastLayoutStartY, this.getZoomValue());
      super.invalidate(0, lastLayoutStartYscaled, this._layoutWidth, this.getVirtualHeight() - lastLayoutStartYscaled + this._layoutHeight);
      return true;
   }

   private void layout0(int width, int height) {
      if (this._invalidLayoutEnd == -1) {
         this._invalidLayoutEnd = this._textFlowData.getTextLength();
      }

      if (this._rootCell.getLayoutEndPosition() < this._textFlowData.getTextLength()) {
         this._invalidLayoutEnd = this._textFlowData.getTextLength();
      }

      this._lastLayoutStartY = -1;
      int virtualWidth = this._wideViewMode ? 1024 : width;
      this._rootCell.setWidth(virtualWidth);
      this._rootCell.setSpecifiedWidth(virtualWidth);
      this._layoutWidth = width;
      int[] virtualDimensions = this._rootCell.layout(this._invalidLayoutStart, this._invalidLayoutEnd);
      int virtualHeight = virtualDimensions[1];
      if ((this.getStyle() & 1) != 0) {
         this._layoutHeight = virtualHeight;
      } else {
         this._layoutHeight = height;
      }

      virtualWidth = virtualDimensions[0];
      this._invalidLayoutStart = this._rootCell.getLayoutEndPosition();
      this._invalidLayoutEnd = -1;
      int setVirtualHeight = virtualHeight;
      if (setVirtualHeight < this._layoutHeight && (this.getStyle() & 2) != 0) {
         setVirtualHeight = this._layoutHeight;
      }

      if (virtualWidth > 0) {
         if (virtualWidth <= width) {
            this._maxZoomValue = 65536;
         } else {
            this._maxZoomValue = Fixed32.div(Fixed32.toFP(virtualWidth), Fixed32.toFP(width));
            this._maxZoomValue = Math.min(this._maxZoomValue, ZOOM_VALUES[ZOOM_VALUES.length - 1]);
         }
      }

      int zoomValue = this.getZoomValue();
      this.setVirtualExtent(
         RendererControl.fixed32DivToInt(virtualWidth, zoomValue),
         RendererControl.fixed32DivToInt(setVirtualHeight + this.getDefaultCursor().getBitmap().getHeight(), zoomValue)
      );
      this.setPositionChild(this._focusField, 0, 0);
      this.layoutChild(this._focusField, virtualWidth, virtualHeight);
      if (this.isTopmostTFM()) {
         int scroll = this.getCurrentScroll();
         int visibleHeight = this.getVerticalVisibleScroll();
         if (RendererControl.fixed32DivToInt(this._lastLayoutStartY, zoomValue) < scroll) {
            scroll += RendererControl.fixed32DivToInt(virtualHeight - this._lastLayoutHeight, zoomValue);
            if (scroll < 0) {
               scroll = 0;
            }
         }

         if (this._focusState == 0 && this.getFieldWithFocus() == this._focusField) {
            this.getFocusRectFromIndex(this._focusIndex, this._rect);
            if (!this.isRectVisible(scroll, this._rect, 1)) {
               scroll = this._rect.y + this._visibleAmount - visibleHeight;
            }
         }

         this.setVerticalScroll(scroll);
      }

      this._lastLayoutHeight = virtualHeight;
      this._layoutFocusCount = this._textFlowData.getFocusRegionCount();
      if (this._textFlowData.isFocusRegionOpen()) {
         this._layoutFocusCount--;
      }
   }

   @Override
   protected void sublayout(int width, int height) {
      Screen screen = this.getScreen();
      Application application = null;
      if (screen != null && screen.isDisplayed() && (application = screen.getApplication()) != null) {
         int layoutGeneration = ((UiApplication)application).getLayoutGeneration();
         if (layoutGeneration != this._layoutGeneration || this._layoutWidth != width) {
            this.invalidateLayout(false);
            this._layoutGeneration = layoutGeneration;
         }

         this.layout0(width, height);
         if ((this.getStyle() & 1) != 0) {
            height = Math.min(this.getVirtualHeight(), height);
         }

         this.setExtent(width, height);
         Manager mgr = this;

         while ((mgr.getStyle() & 281474976710656L) == 0) {
            mgr = mgr.getManager();
            if (mgr == null) {
               mgr = this;
               break;
            }
         }

         this._scrollingParent = mgr;
      }
   }

   @Override
   protected boolean moveFocus(int where) {
      int visibleHeight = this.getVerticalVisibleScroll();
      int scroll = this.getCurrentScroll();
      int cursorOffset = this._cursorYPos - scroll;
      switch (where & 771) {
         case 1:
            scroll = 0;
            this._focusSearchDirection = 1;
            break;
         case 2:
            scroll = Integer.MAX_VALUE;
            this._focusSearchDirection = -1;
            break;
         case 256:
            scroll -= visibleHeight - this._scrollAmount;
            this._focusSearchDirection = 1;
            break;
         case 512:
            scroll += visibleHeight - this._scrollAmount;
            this._focusSearchDirection = -1;
            break;
         default:
            Asserts.productionAssert(false);
      }

      int newFocusY = this._focusSearchDirection < 0 ? Integer.MAX_VALUE : 0;
      if (this._mode != 1) {
         this._focusSearchDirection = -this._focusSearchDirection;
      }

      scroll = MathUtilities.clamp(0, scroll, Math.max(0, this.getVirtualHeight() - visibleHeight));
      newFocusY = MathUtilities.clamp(scroll, newFocusY, Math.max(0, scroll + visibleHeight - 1));
      if (this._mode == 1) {
         this._setScroll = scroll;
      }

      Cursor cursor = this._currentCursor != null ? this._currentCursor : this.getDefaultCursor();
      this._cursorYPos = MathUtilities.clamp(
         0 + cursor.getOriginY(), scroll + cursorOffset, Math.max(0, this.getVirtualHeight() - cursor.getBitmap().getHeight())
      );
      this.moveFocus(0, newFocusY, 0, 0);
      return true;
   }

   @Override
   public int getFieldAtLocation(int x, int y) {
      return -1;
   }

   @Override
   public int getFieldClosestToLocation(int x, int y, int status) {
      switch (this._mode) {
         case 0:
            Asserts.productionAssert(false);
            return 0;
         case 1:
         default:
            return this.getFieldClosestToLocation_ModeScrolling(x, y, status);
         case 2:
         case 3:
            return this._focusField.getIndex();
      }
   }

   private int getFieldClosestToLocation_ModeScrolling(int x, int y, int status) {
      int rootCellHeight = this._rootCell.getHeight();
      if (rootCellHeight == 0) {
         y = 0;
      } else {
         y = MathUtilities.clamp(0, y, this._rootCell.getHeight() - 1);
      }

      int currentScroll = this.calculateCurrentScroll();

      for (int attempt = 0; attempt < 2; attempt++) {
         int focusIndex = this._textFlowData.getFocusRegionNearY(y, this._focusSearchDirection * (attempt == 0 ? 1 : -1));
         if (focusIndex >= 0 && focusIndex < this._layoutFocusCount) {
            this.getFocusRectFromIndex(focusIndex, this._rect);
            if (this.isRectVisible(currentScroll, this._rect, 1)) {
               this._focusIndex = focusIndex;
               this._focusSearchDirection = 0;
               this._focusState = 0;
               return this.getFocusFieldFromIndex(focusIndex).getIndex();
            }
         }

         if (attempt == 0 && this._focusState == 0 && this._focusIndex != focusIndex && this._focusIndex >= 0 && this._focusIndex < this._layoutFocusCount) {
            this.getFocusRectFromIndex(this._focusIndex, this._rect);
            if (this.isRectVisible(currentScroll, this._rect, 1)) {
               this._focusSearchDirection = 0;
               return this.getFocusFieldFromIndex(this._focusIndex).getIndex();
            }
         }
      }

      if (this._focusSearchDirection == 0) {
         this._focusState = 1;
      } else {
         this._focusState = this._focusSearchDirection;
      }

      this._focusSearchDirection = 0;
      return this._focusField.getIndex();
   }

   @Override
   protected boolean isFocusDrawn() {
      return false;
   }

   final void placeField(Field field, int x, int y) {
      this.setPositionChild(field, x, y);
   }

   final void paintField(Graphics graphics, Field field) {
      if (field instanceof BrowserBitmapField) {
         ((BrowserBitmapField)field).resumeAnimation();
      }

      this.paintChild(graphics, field);
   }

   final void layoutField(Field field, int width, int height) {
      this.layoutChild(field, width, height);
   }

   final void callbackDrawHighlightRegion(Graphics graphics, int style, boolean on, int x, int y, int width, int height) {
      this.drawHighlightRegion(graphics, style, on, x, y, width, height);
   }

   @Override
   protected boolean isScrollCopyable() {
      return true;
   }

   @Override
   protected void paintBackground(Graphics graphics) {
      graphics.setBackgroundColor(16777215);
      graphics.clear();
   }

   @Override
   protected void subpaint(Graphics graphics) {
      this._rootCell.paint(graphics, this.getFont(), this.getZoomValue());
      this.paintChild(graphics, this._focusField);
      if (this._mode == 1 && this.isTopmostTFM()) {
         this.repaintCursor(graphics, !graphics.isOverlaySet(4));
      }
   }

   public void paintThumbnail(Graphics graphics, int scale) {
      this._rootCell.paint(graphics, this.getFont(), scale);
   }

   @Override
   public void add(Field field) {
      Asserts.productionAssert(false);
   }

   @Override
   public void delete(Field field) {
      Asserts.productionAssert(false);
   }

   @Override
   public void deleteRange(int start, int count) {
      Asserts.productionAssert(false);
   }

   @Override
   public void insert(Field field, int index) {
      Asserts.productionAssert(false);
   }

   public void replaceField(Field oldField, Field newField) {
      this.checkLayoutActive();
      Asserts.productionArgumentAssert(oldField != null && newField != null);
      int oldIndex = oldField.getIndex();
      this._textFlowData.replaceField(this.getRegionIndexFromField(oldField), oldField, newField);
      super.delete(oldField);
      super.insert(newField, oldIndex);
      this.invalidateField(newField);
   }

   public boolean addMenuItemsToContextMenu() {
      return this.getLeafFieldWithFocus() instanceof TextFlowManager$FocusField;
   }

   @Override
   protected void onVisibilityChange(boolean visible) {
      super.onVisibilityChange(visible);
      this.toggleAnimations(visible);
   }

   @Override
   protected void onExposed() {
      super.onExposed();
      this.toggleAnimations(true);
   }

   @Override
   protected void onObscured() {
      super.onObscured();
      this.toggleAnimations(false);
   }

   @Override
   protected void onDisplay() {
      super.onDisplay();
      this.trackballSensitivityOn();
   }

   @Override
   protected void onUndisplay() {
      this.trackballSensitivityOff();
      if (this._hoverRunnableId != -1) {
         this.getScreen().getApplication().cancelInvokeLater(this._hoverRunnableId);
         this._hoverRunnableId = -1;
      }

      TextFlowManager$ViewModeRunnable.getInstance().cancel();
      super.onUndisplay();
      this.toggleAnimations(false);
   }

   @Override
   protected boolean keyChar(char character, int status, int time) {
      return character == 27 && this.setSendMoveToFocus(false) ? true : super.keyChar(character, status, time);
   }

   @Override
   protected boolean keyStatus(int keycode, int time) {
      Cursor oldDefaultCursor = this.getDefaultCursor();
      this._lastKeypadStatus = Keypad.status(keycode);
      Cursor newDefaultCursor = this.getDefaultCursor();
      if (this._currentCursor == oldDefaultCursor && newDefaultCursor != oldDefaultCursor) {
         this.updateCursor(newDefaultCursor);
      }

      return super.keyStatus(keycode, time);
   }

   private void trackballSensitivityOn() {
      if (this._mode == 1) {
         Screen screen = this.getScreen();
         if (this.usingCursor()) {
            screen.setTrackballSensitivityXOffset(100);
            screen.setTrackballSensitivityYOffset(100);
            screen.setTrackballFilter(6);
         }
      }
   }

   private void trackballSensitivityOff() {
      Screen screen = this.getScreen();
      screen.setTrackballSensitivityXOffset(Integer.MAX_VALUE);
      screen.setTrackballSensitivityYOffset(Integer.MAX_VALUE);
      screen.setTrackballFilter(-1);
   }

   private void toggleAnimations(boolean on) {
      int size = this._textFlowData.getOutOfLineObjectCount();

      for (int i = 0; i < size; i++) {
         Object obj = this._textFlowData.getOutOfLineObject(i);
         if (obj instanceof TextFlowCell) {
            TextFlowCell cell = (TextFlowCell)obj;
            if (cell._animationProperties != null && cell._animationProperties._animationStyle != 0) {
               if (on) {
                  AnimationThread.addAnimation(cell);
               } else {
                  AnimationThread.removeAnimation(cell);
               }
            }
         }
      }
   }

   private boolean isMinimalMenuMode() {
      return (this.getStyle() & 4) != 0;
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (!this.isMinimalMenuMode()) {
         switch (this._mode) {
            case 0:
               break;
            case 1:
            default:
               menu.add(this._positionItem);
               break;
            case 2:
               menu.add(this._cancelPositionItem);
         }

         if (Trackball.isSupported()) {
            menu.add(this._wideViewMode ? this._narrowViewItem : this._wideViewItem);
         }
      }
   }

   public void cancelCurrentSelection() {
      if (this._mode == 2) {
         this._focusField.position(false);
      } else {
         if (this._mode == 3) {
            this._focusField.select(false);
         }
      }
   }

   private void setSelection(int carat, int anchor) {
      if (carat < anchor) {
         this._selectionCellBegin = carat;
         this._selectionCellEnd = anchor;
      } else {
         this._selectionCellBegin = anchor;
         this._selectionCellEnd = carat;
      }
   }

   private void resetSelection() {
      this._selectionCellBegin = Integer.MAX_VALUE;
      this._selectionCellEnd = Integer.MAX_VALUE;
   }

   public void reflowAll() {
      this.invalidateLayout(true);
   }

   public void reflowCell(Object eventLock, Object tableObj) {
      TextFlowCell table = (TextFlowCell)tableObj;
      table.reflow(eventLock);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   void paintScaledField(Field field, int scale, Graphics graphics) {
      boolean painted = false;
      int left = RendererControl.fixed32DivToInt(field.getLeft(), scale);
      int top = RendererControl.fixed32DivToInt(field.getTop(), scale);
      int width = RendererControl.fixed32DivToInt(field.getWidth(), scale);
      int height = RendererControl.fixed32DivToInt(field.getHeight(), scale);

      label133:
      try {
         if (!(field instanceof BrowserBitmapField)) {
            if (field instanceof FrameManager) {
               Manager frameManager = (Manager)field;
               graphics.pushRegion(left, top, width, height, 0, 0);
               boolean var20 = false /* VF: Semaphore variable */;

               try {
                  var20 = true;
                  int fieldHeight = frameManager.getFieldCount();

                  for (int fieldTop = 0; fieldTop < fieldHeight; fieldTop++) {
                     Field frameField = frameManager.getField(fieldTop);
                     if (frameField instanceof TextFlowManager) {
                        TextFlowManager embeddedTFM = (TextFlowManager)frameField;
                        embeddedTFM.paintBackground(graphics);
                        embeddedTFM.paintThumbnail(graphics, scale);
                     }
                  }

                  painted = true;
                  var20 = false;
               } finally {
                  if (var20) {
                     graphics.popContext();
                  }
               }

               graphics.popContext();
            }
         } else {
            BrowserBitmapField bitmapField = (BrowserBitmapField)field;
            EncodedImage img = bitmapField.getImage();
            bitmapField.pauseAnimation();
            if (img != null) {
               int targetHeight = RendererControl.fixed32DivToInt(field.getContentHeight(), scale);
               int targetWidth = RendererControl.fixed32DivToInt(field.getContentWidth(), scale);
               if (targetHeight > 0 && targetWidth > 0) {
                  int imageWidth = img.getWidth();
                  int imageHeight = img.getHeight();
                  img.setScaleX32(Fixed32.div(Fixed32.toFP(imageWidth), Fixed32.toFP(targetWidth)));
                  img.setScaleY32(Fixed32.div(Fixed32.toFP(imageHeight), Fixed32.toFP(targetHeight)));
                  graphics.drawImage(left, top, width, height, img, 0, 0, 0);
                  img.setScaleX32(65536);
                  img.setScaleY32(65536);
                  painted = true;
               }
            }
         }
      } finally {
         break label133;
      }

      if (!painted) {
         int fieldWidth = field.getWidth();
         int fieldHeight = field.getHeight();
         int fieldTop = field.getTop();
         int fieldLeft = field.getLeft();
         if (this._scalingBitmap == null || this._scalingBitmap.getWidth() < fieldWidth || this._scalingBitmap.getHeight() < fieldHeight) {
            this._scalingBitmap = new Bitmap(Math.max(fieldWidth, Display.getWidth()), Math.max(fieldHeight, 40));
            this._scalingGraphics = new Graphics(this._scalingBitmap);
            this._tempx = new int[4];
            this._tempy = new int[4];
         }

         this._scalingGraphics.pushRegion(0, 0, fieldWidth, fieldHeight, -fieldLeft, -fieldTop);
         this._scalingGraphics.setBackgroundColor(16777215);
         this._scalingGraphics.clear();
         this.paintField(this._scalingGraphics, field);
         this._scalingGraphics.popContext();
         this._tempx[0] = this._tempx[3] = 0;
         this._tempx[1] = this._tempx[2] = width;
         this._tempy[0] = this._tempy[1] = 0;
         this._tempy[2] = this._tempy[3] = height;
         graphics.pushRegion(left, top, width, height, 0, 0);
         graphics.drawTexturedPath(this._tempx, this._tempy, null, null, 0, 0, scale, 0, 0, scale, this._scalingBitmap);
         graphics.popContext();
      }
   }

   private void scaleRect(XYRect rect) {
      int zoomValue = this.getZoomValue();
      if (zoomValue != 65536) {
         rect.x = RendererControl.fixed32DivToInt(rect.x, zoomValue);
         rect.y = RendererControl.fixed32DivToInt(rect.y, zoomValue);
         rect.width = RendererControl.fixed32DivToInt(rect.width, zoomValue);
         rect.height = RendererControl.fixed32DivToInt(rect.height, zoomValue);
      }
   }

   private void unscaleRect(XYRect rect) {
      int zoomValue = this.getZoomValue();
      if (zoomValue != 65536) {
         rect.x = RendererControl.fixed32MultToInt(rect.x, zoomValue);
         rect.y = RendererControl.fixed32MultToInt(rect.y, zoomValue);
         rect.width = RendererControl.fixed32MultToInt(rect.width, zoomValue);
         rect.height = RendererControl.fixed32MultToInt(rect.height, zoomValue);
      }
   }

   static boolean access$1700(TextFlowManager x0, int x1, int x2) {
      return x0.onCursorHover(x1, x2);
   }

   static Graphics access$3700(TextFlowManager x0) {
      return x0.getGraphics0();
   }

   static void access$3800(TextFlowManager x0, int x1, int x2, int x3, int x4) {
      x0.moveFocus(x1, x2, x3, x4);
   }

   static Graphics access$3900(TextFlowManager x0) {
      return x0.getGraphics0();
   }
}
