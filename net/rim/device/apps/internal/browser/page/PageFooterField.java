package net.rim.device.apps.internal.browser.page;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ribbon.RibbonNetworkInfo;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.device.apps.internal.browser.util.FontCache;
import net.rim.device.internal.browser.wap.WAPServiceRecord;

public final class PageFooterField extends Field {
   private long _lastPaintTime;
   private String _operator;
   private Verb[] _verbs;
   private XYRect[] _verbRects;
   private String[] _verbLabels;
   private int _focusIndex;
   private int _min;
   private int _max;
   private int _current;
   private String _label;
   private int _barStart;
   private int _barEnd;
   private int _barCurrent;
   private boolean _showingFooter;
   private boolean _fullScreenMode;
   private boolean _isWapBrowser;
   private int _themeGeneration;
   private Bitmap _footerBitmap;
   private Bitmap _progressBitmap;
   private Bitmap _focusLeftBitmap;
   private Bitmap _focusRightBitmap;
   private Application _app;
   private Runnable _repaintRunnable;
   private int _repaintRunnableId = -1;
   private static final Tag TAG = Tag.create("browser-footer");
   private static final int SEPARATOR_HEIGHT = 1;
   private static final int SPACE_PADDING_WIDTH = 5;
   private static final int MIN_PAINT_DELAY = 200;
   private static final String BROWSER_FOOTER_PROGRESS_BITMAP = "browser-footer~progress";
   private static final String BROWSER_FOOTER_FOCUS_LEFT_BITMAP = "browser-footer~focus-left";
   private static final String BROWSER_FOOTER_FOCUS_RIGHT_BITMAP = "browser-footer~focus-right";
   private static final String BROWSER_FOOTER_BITMAP = "browser-footer";

   PageFooterField() {
      this.setTag(TAG);
      this._focusIndex = -1;
      this.configChanged();
      this.setFont(FontCache.getInstance().getFont(null, 0, Ui.convertSize(6, 3, 0)));
      this.setupBitmaps();
      this._app = Application.getApplication();
      this._repaintRunnable = new PageFooterField$RepaintMe(this);
   }

   private static final Bitmap getBitmap(Theme theme, String imgStr) {
      EncodedImage img = theme.getImage(imgStr, true);
      return img != null ? img.getBitmap() : null;
   }

   private final void setupBitmaps() {
      int currentGeneration = ThemeManager.getGeneration();
      if (this._themeGeneration != currentGeneration) {
         this._themeGeneration = currentGeneration;
         Theme theme = ThemeManager.getActiveTheme();
         this._footerBitmap = getBitmap(theme, "browser-footer");
         this._progressBitmap = getBitmap(theme, "browser-footer~progress");
         this._focusLeftBitmap = getBitmap(theme, "browser-footer~focus-left");
         this._focusRightBitmap = getBitmap(theme, "browser-footer~focus-right");
      }
   }

   @Override
   public final int getPreferredWidth() {
      return Display.getWidth();
   }

   @Override
   public final int getPreferredHeight() {
      int preferredHeight = this.getFont().getHeight() + 1 + 1;
      if (!this._showingFooter && (this._fullScreenMode || !this._isWapBrowser && (this._verbs == null || this._verbs[0] == null && this._verbs[1] == null))) {
         preferredHeight = 0;
      }

      return preferredHeight;
   }

   protected final void showFooter() {
      if (!this._showingFooter) {
         this._showingFooter = true;
         this.updateLayout();
      }
   }

   protected final void hideFooter() {
      if (this._showingFooter) {
         this._showingFooter = false;
         this.updateLayout();
      }
   }

   protected final void setFullScreenMode(boolean fullScreenMode) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void configChanged() {
      boolean isWapBrowser = false;
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null) {
         int configType = session.getConfig().getPropertyAsInt(12);
         isWapBrowser = configType == 7
            || configType == 2
            || StringUtilities.strEqualIgnoreCase(WAPServiceRecord.SERVICE_CID, session.getConfig().getPropertyAsString(3), 1701707776);
      }

      this.setIsWapBrowser(isWapBrowser);
   }

   protected final void setIsWapBrowser(boolean isWapBrowser) {
      this._isWapBrowser = isWapBrowser;
      this.updateOperatorNameString();
   }

   public final void operatorNameStringChanged() {
      this.updateOperatorNameString();
      this._app.invokeLater(this._repaintRunnable);
   }

   private final void updateOperatorNameString() {
      if (this._isWapBrowser) {
         this._operator = RibbonNetworkInfo.getInstance().getOperatorName();
      } else {
         this._operator = CommonResources.getString(9101);
      }
   }

   private final void drawLabel(Graphics graphics, int color) {
      graphics.setColor(color);
      graphics.drawText(this._label, 0, this._label.length(), this._barStart, 1, 116, this._barEnd - this._barStart);
   }

   @Override
   protected final void paint(Graphics graphics) {
      this._lastPaintTime = System.currentTimeMillis();
      int fieldWidth = this.getWidth();
      int fieldHeight = this.getHeight();
      if (this._footerBitmap != null) {
         graphics.drawBitmap(0, 0, fieldWidth, fieldHeight, this._footerBitmap, 0, 0);
      }

      Font font = this.getFont();
      if (this._label != null) {
         int foregroundColor1 = ThemeAttributeSet.getColor(this, 1);
         int foregroundColor2 = ThemeAttributeSet.getColor(this, 5);
         if (this._progressBitmap != null) {
            graphics.drawBitmap(this._barStart, 0, this._barCurrent, fieldHeight, this._progressBitmap, 0, 0);
            if (foregroundColor2 == foregroundColor1) {
               this.drawLabel(graphics, foregroundColor1);
            } else {
               this.drawLabel(graphics, foregroundColor1);
               graphics.pushContext(this._barStart, 0, this._barCurrent, fieldHeight, 0, 0);
               this.drawLabel(graphics, foregroundColor2);
               graphics.popContext();
            }
         } else {
            this.drawLabel(graphics, foregroundColor1);
            graphics.invert(this._barStart, 1, this._barCurrent - this._barStart, fieldHeight - 1 - 1);
         }
      } else {
         int leftVerbWidth = this._verbs != null && this._verbs[0] != null ? font.getBounds(this._verbLabels[0]) + 10 : 0;
         int rightVerbWidth = this._verbs != null && this._verbs[1] != null ? font.getBounds(this._verbLabels[1]) + 10 : 0;
         int operatorWidth = this._operator != null ? font.getBounds(this._operator) + 10 : 0;
         int verbsWidthAvailable = this._isWapBrowser && this._operator != null ? fieldWidth - operatorWidth : fieldWidth;
         int widthAvailable = fieldWidth;
         if (leftVerbWidth > 0 && verbsWidthAvailable > 15) {
            leftVerbWidth = Math.min(verbsWidthAvailable, leftVerbWidth);
            boolean invert = false;
            if (graphics.isDrawingStyleSet(8) && this._focusIndex == 0) {
               graphics.setColor(ThemeAttributeSet.getColor(this, 3));
               if (this._focusLeftBitmap != null) {
                  graphics.drawBitmap(0, 0, leftVerbWidth, fieldHeight, this._focusLeftBitmap, 0, 0);
               } else {
                  invert = true;
               }
            } else {
               graphics.setColor(ThemeAttributeSet.getColor(this, 1));
            }

            this._verbRects[0].set(0, 1, leftVerbWidth, fieldHeight - 1);
            if (invert) {
               graphics.invert(this._verbRects[0]);
            }

            graphics.drawText(this._verbLabels[0], 5, 1, 116, leftVerbWidth - 10);
            widthAvailable -= leftVerbWidth;
            verbsWidthAvailable -= leftVerbWidth;
            graphics.drawLine(leftVerbWidth, 1, leftVerbWidth, fieldHeight - 1);
         }

         if (rightVerbWidth > 0 && verbsWidthAvailable > 15) {
            rightVerbWidth = Math.min(verbsWidthAvailable, rightVerbWidth);
            int offset = fieldWidth - rightVerbWidth;
            boolean invert = false;
            if (graphics.isDrawingStyleSet(8) && this._focusIndex == 1) {
               graphics.setColor(ThemeAttributeSet.getColor(this, 3));
               if (this._focusRightBitmap != null) {
                  graphics.drawBitmap(offset, 0, rightVerbWidth, fieldHeight, this._focusRightBitmap, 0, 0);
               } else {
                  invert = true;
               }
            } else {
               graphics.setColor(ThemeAttributeSet.getColor(this, 1));
            }

            this._verbRects[1].set(offset + 1, 1, rightVerbWidth, fieldHeight - 1);
            if (invert) {
               graphics.invert(this._verbRects[1]);
            }

            graphics.drawText(this._verbLabels[1], offset + 5, 1, 116, rightVerbWidth - 10);
            widthAvailable -= rightVerbWidth;
            graphics.drawLine(offset, 1, offset, fieldHeight - 1);
         }

         if (this._operator != null && (widthAvailable >= operatorWidth || this._isWapBrowser)) {
            graphics.drawText(this._operator, leftVerbWidth + 5, 1, 116, widthAvailable - 10);
         }
      }

      graphics.setColor(ThemeAttributeSet.getColor(this, 1));
      graphics.drawLine(0, 0, fieldWidth, 0);
   }

   @Override
   protected final void layout(int width, int height) {
      this.setupBitmaps();
      int widthToUse = Math.min(width, this.getPreferredWidth());
      int heightToUse = Math.min(height, this.getPreferredHeight());
      this.setExtent(widthToUse, heightToUse);
      this.progressLayout();
   }

   @Override
   public final boolean isFocusable() {
      return this._label == null && this._focusIndex != -1;
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      if (this._focusIndex >= 0 && this._focusIndex < 2) {
         rect.set(this._verbRects[this._focusIndex]);
      }
   }

   @Override
   protected final void onFocus(int direction) {
      this._focusIndex = this._verbs[0] != null ? 0 : -1;
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      if (amount == 0 || this._focusIndex == -1) {
         return amount;
      }

      if (amount > 0) {
         if (this._focusIndex == 0 && this._verbs[1] != null && this._verbRects[1] != null && this._verbRects[1].X2() != 0 && this._verbRects[1].Y2() != 0) {
            this._focusIndex = 1;
            amount--;
         }

         return amount;
      } else {
         if (this._focusIndex == 1) {
            this._focusIndex = 0;
            amount++;
         }

         return amount;
      }
   }

   final void addVerbs(Vector verbs) {
      if (this._verbs != null) {
         for (int index = this._verbs.length - 1; index >= 0; index--) {
            this._verbs[index] = null;
            this._verbLabels[index] = null;
         }

         this._focusIndex = -1;
      } else {
         this._verbs = new Object[2];
         this._verbRects = new Object[2];
         this._verbLabels = new Object[2];
      }

      int size = 0;
      if (verbs != null && (size = verbs.size()) != 0) {
         if (size == 1) {
            this._verbs[0] = (Verb)verbs.elementAt(0);
            this._verbRects[0] = (XYRect)(new Object());
            this._verbLabels[0] = this._verbs[0].toString();
            this._focusIndex = 0;
         } else {
            int ordering0 = -1;
            int ordering1 = -1;
            int newOrdering = -1;
            Verb verb = null;

            for (int index = 0; index < size; index++) {
               verb = (Verb)verbs.elementAt(index);
               newOrdering = verb.getOrdering();
               if (this._verbs[0] == null) {
                  this._verbs[0] = verb;
                  ordering0 = newOrdering;
               } else if (newOrdering < ordering0) {
                  this._verbs[1] = this._verbs[0];
                  ordering1 = ordering0;
                  this._verbs[0] = verb;
                  ordering0 = newOrdering;
               } else if (this._verbs[1] == null || newOrdering < ordering1) {
                  this._verbs[1] = verb;
                  ordering1 = newOrdering;
               }
            }

            this._verbRects[0] = (XYRect)(new Object());
            this._verbRects[1] = (XYRect)(new Object());
            this._verbLabels[0] = this._verbs[0].toString();
            this._verbLabels[1] = this._verbs[1].toString();
            this._focusIndex = 0;
         }
      }
   }

   public final void clearVerbs() {
      this._verbs = null;
      this._focusIndex = -1;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n' && this._focusIndex >= 0 && this._verbs[this._focusIndex] != null) {
         this._verbs[this._focusIndex].invoke(null);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public final boolean navigationClick(int status, int time) {
      if (this._focusIndex >= 0 && this._verbs[this._focusIndex] != null) {
         this._verbs[this._focusIndex].invoke(null);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      return (status & 1) == 0 ? this.navigationClick(status, time) : false;
   }

   final void reset(String label, int min, int max, int start, boolean immediate) {
      Asserts.productionArgumentAssert(min < max && start >= min && start <= max);
      boolean changed = false;
      if (this._label != label || this._min != min || this._max != max) {
         this._label = label;
         this._min = min;
         this._max = max;
         changed = true;
      }

      this._current = start;
      Manager manager = this.getManager();
      if (manager != null && manager.isValidLayout()) {
         int currentValue = this._barCurrent;
         this.progressLayout();
         if (changed || currentValue != this._barCurrent) {
            synchronized (this._app.getAppEventLock()) {
               if (!immediate && this._lastPaintTime + 200 > System.currentTimeMillis()) {
                  if (this._repaintRunnableId == -1) {
                     this._repaintRunnableId = this._app.invokeLater(this._repaintRunnable, 200, false);
                  }
               } else {
                  if (this._repaintRunnableId != -1) {
                     this._app.cancelInvokeLater(this._repaintRunnableId);
                     this._repaintRunnableId = -1;
                  }

                  this.invalidate();
               }

               return;
            }
         }
      }
   }

   private final void progressLayout() {
      this._barStart = 0;
      this._barEnd = this.getWidth();
      if (this._current == this._max && this._max > 0) {
         this._barCurrent = this._barEnd;
      } else if (this._current == this._min) {
         this._barCurrent = this._barStart;
      } else {
         long barWidth = this._barEnd - this._barStart;
         long rangeWidth = this._max - this._min;
         this._barCurrent = (int)((barWidth << 32) / rangeWidth * (this._current - this._min) >> 32) + this._barStart;
      }
   }

   static final void access$100(PageFooterField x0) {
      x0.invalidate();
   }
}
