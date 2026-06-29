package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.ScrollChangeListener;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.MathUtilities;

public final class Scrollbar extends Field implements ScrollChangeListener {
   private Manager _client;
   private boolean _enabled;
   private boolean _horizontalScroll;
   private int[] _tempDraw;
   private Bitmap _trackBitmap;
   private Bitmap _topOrLeftArrowBitmap;
   private Bitmap _bottomOrRightArrowBitmap;
   private Bitmap _sliderBitmap;
   private int _lastCalculatedSize;
   private int _lastCalculatedOffset;
   private static final Tag TAG = Tag.create("browser-scrollbar");
   private static final int MINIMUM_SCROLLBAR_HEIGHT;
   private static final int PREFERRED_SCROLLBAR_WIDTH;
   private static final int ARROW_HEIGHT;
   private static final int[] ARROW_X_POINTS = new int[]{
      0,
      5,
      5,
      4,
      4,
      3,
      3,
      2,
      2,
      1,
      1,
      0,
      -804650998,
      3,
      5,
      7,
      10,
      15,
      3,
      5,
      7,
      10,
      15,
      -804650996,
      6,
      6,
      4,
      4,
      2,
      2,
      0,
      0,
      2,
      2,
      4,
      4,
      -804650994,
      10,
      27,
      8,
      127,
      9,
      131,
      132,
      129,
      130,
      32,
      133
   };
   private static final int[] TOP_ARROW_Y_POINTS = new int[]{
      6,
      6,
      4,
      4,
      2,
      2,
      0,
      0,
      2,
      2,
      4,
      4,
      -804650994,
      10,
      27,
      8,
      127,
      9,
      131,
      132,
      129,
      130,
      32,
      133,
      134,
      137,
      128,
      -804650987,
      12,
      14,
      16,
      18,
      20,
      22,
      24,
      26,
      28,
      30,
      32,
      34,
      37,
      39,
      41,
      48,
      51,
      54,
      57,
      60
   };
   private static final String SCROLLBAR_TRACK_VERT_BITMAP;
   private static final String SCROLLBAR_TRACK_HORZ_BITMAP;
   private static final String SCROLLBAR_TOP_ARROW_BITMAP;
   private static final String SCROLLBAR_BOTTOM_ARROW_BITMAP;
   private static final String SCROLLBAR_LEFT_ARROW_BITMAP;
   private static final String SCROLLBAR_RIGHT_ARROW_BITMAP;
   private static final String SCROLLBAR_SLIDER_VERT_BITMAP;
   private static final String SCROLLBAR_SLIDER_HORZ_BITMAP;

   public Scrollbar() {
      this(true);
   }

   public Scrollbar(boolean enabled) {
      this(enabled, false);
   }

   public Scrollbar(boolean enabled, boolean horizontalScroll) {
      super(0);
      this.setTag(TAG);
      this._enabled = enabled;
      this._horizontalScroll = horizontalScroll;
      this.setupBitmaps();
   }

   @Override
   public final int getPreferredWidth() {
      if (!this._horizontalScroll) {
         if (!this._enabled) {
            return 0;
         } else {
            return this._trackBitmap != null ? this._trackBitmap.getWidth() : 5;
         }
      } else {
         return super.getPreferredWidth();
      }
   }

   @Override
   public final int getPreferredHeight() {
      if (this._horizontalScroll) {
         if (!this._enabled) {
            return 0;
         } else {
            return this._trackBitmap != null ? this._trackBitmap.getHeight() : 5;
         }
      } else {
         return super.getPreferredHeight();
      }
   }

   @Override
   protected final void layout(int width, int height) {
      if (this._horizontalScroll) {
         this.setExtent(width, this.getPreferredHeight());
      } else {
         this.setExtent(this.getPreferredWidth(), height);
      }
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      Theme theme = ThemeManager.getActiveTheme();
      ThemeAttributeSet tas = theme.getAttributeSet(this);
      if (tas != null) {
         String scrollbarName = tas.getScrollbarName();
         if (scrollbarName != null) {
            Bitmap[] bitmaps = theme.getScrollbar(scrollbarName);
            if (bitmaps != null) {
               if (this._horizontalScroll) {
                  this._trackBitmap = bitmaps[3];
                  this._topOrLeftArrowBitmap = bitmaps[6];
                  this._bottomOrRightArrowBitmap = bitmaps[7];
                  this._sliderBitmap = bitmaps[1];
               } else {
                  this._trackBitmap = bitmaps[2];
                  this._topOrLeftArrowBitmap = bitmaps[4];
                  this._bottomOrRightArrowBitmap = bitmaps[5];
                  this._sliderBitmap = bitmaps[0];
               }

               if (this._trackBitmap != null && this._topOrLeftArrowBitmap != null && this._bottomOrRightArrowBitmap != null && this._sliderBitmap != null) {
                  return;
               }

               this._trackBitmap = this._topOrLeftArrowBitmap = this._bottomOrRightArrowBitmap = this._sliderBitmap = null;
            }
         }
      }

      this.setupBitmaps();
   }

   private static final Bitmap getBitmap(Theme theme, String imgStr) {
      EncodedImage img = theme.getImage(imgStr, true);
      return img != null ? img.getBitmap() : null;
   }

   private final void setupBitmaps() {
      Theme theme = ThemeManager.getActiveTheme();
      if (this._horizontalScroll) {
         this._trackBitmap = getBitmap(theme, "browser-scrollbar~track-horz");
         this._topOrLeftArrowBitmap = getBitmap(theme, "browser-scrollbar~left-arrow");
         this._bottomOrRightArrowBitmap = getBitmap(theme, "browser-scrollbar~right-arrow");
         this._sliderBitmap = getBitmap(theme, "browser-scrollbar~slider-horz");
      } else {
         this._trackBitmap = getBitmap(theme, "browser-scrollbar~track-vert");
         this._topOrLeftArrowBitmap = getBitmap(theme, "browser-scrollbar~top-arrow");
         this._bottomOrRightArrowBitmap = getBitmap(theme, "browser-scrollbar~bottom-arrow");
         this._sliderBitmap = getBitmap(theme, "browser-scrollbar~slider-vert");
      }

      if (this._trackBitmap == null || this._topOrLeftArrowBitmap == null || this._bottomOrRightArrowBitmap == null || this._sliderBitmap == null) {
         this._trackBitmap = this._topOrLeftArrowBitmap = this._bottomOrRightArrowBitmap = this._sliderBitmap = null;
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      XYRect extent = this.getExtent();
      if (this._client != null && this._enabled) {
         if (this._trackBitmap == null) {
            if (this._tempDraw == null) {
               this._tempDraw = new int[12];
            }

            if (!this._horizontalScroll) {
               int virtualHeight = this._client.getVirtualHeight();
               if (virtualHeight > extent.height) {
                  int heightAvailable = extent.height - 7 - 7;
                  if (heightAvailable > 0) {
                     int y1 = 7;
                     int y2 = extent.height - 7;
                     int scrollbarHeight = extent.height * extent.height / virtualHeight;
                     this._lastCalculatedSize = scrollbarHeight;
                     scrollbarHeight = MathUtilities.clamp(7, scrollbarHeight, heightAvailable);
                     int scrollbarY = this._client.getVerticalScroll() * extent.height / virtualHeight % extent.height;
                     this._lastCalculatedOffset = scrollbarY;
                     scrollbarY = MathUtilities.clamp(y1, scrollbarY, y2 - scrollbarHeight);
                     graphics.clear(0, 0, extent.width, extent.height);
                     y2--;
                     graphics.setStipple(1431655765);
                     graphics.drawLine(1, y1, 1, y2);
                     graphics.drawLine(3, y1, 3, y2);
                     graphics.setStipple(-1431655766);
                     graphics.drawLine(2, y1, 2, y2);
                     graphics.setStipple(-1);
                     graphics.clear(0, scrollbarY - 1, extent.width, scrollbarHeight);
                     graphics.fillRect(1, scrollbarY, extent.width - 2, scrollbarHeight);
                     graphics.drawFilledPath(ARROW_X_POINTS, TOP_ARROW_Y_POINTS, null, null);
                     int h = extent.height;
                     int h6 = h - 6;
                     int h4 = h - 4;
                     int h2 = h - 2;
                     this._tempDraw[0] = h6;
                     this._tempDraw[1] = h6;
                     this._tempDraw[2] = h4;
                     this._tempDraw[3] = h4;
                     this._tempDraw[4] = h2;
                     this._tempDraw[5] = h2;
                     this._tempDraw[6] = h;
                     this._tempDraw[7] = h;
                     this._tempDraw[8] = h2;
                     this._tempDraw[9] = h2;
                     this._tempDraw[10] = h4;
                     this._tempDraw[11] = h4;
                     graphics.drawFilledPath(ARROW_X_POINTS, this._tempDraw, null, null);
                  }
               }
            } else {
               int virtualWidth = this._client.getVirtualWidth();
               if (virtualWidth > extent.width) {
                  int widthAvailable = extent.width - 7 - 7;
                  if (widthAvailable > 0) {
                     int x1 = 7;
                     int x2 = extent.width - 7;
                     int scrollbarWidth = extent.width * extent.width / virtualWidth;
                     this._lastCalculatedSize = scrollbarWidth;
                     scrollbarWidth = MathUtilities.clamp(7, scrollbarWidth, widthAvailable);
                     int scrollbarX = this._client.getHorizontalScroll() * extent.width / virtualWidth % extent.width;
                     this._lastCalculatedOffset = scrollbarX;
                     scrollbarX = MathUtilities.clamp(x1, scrollbarX, x2 - scrollbarWidth);
                     graphics.clear(0, 0, extent.width, extent.height);
                     x2--;
                     graphics.setStipple(1431655765);
                     graphics.drawLine(x1, 1, x2, 1);
                     graphics.drawLine(x1, 3, x2, 3);
                     graphics.setStipple(-1431655766);
                     graphics.drawLine(x1, 2, x2, 2);
                     graphics.setStipple(-1);
                     graphics.clear(scrollbarX - 1, 0, scrollbarWidth, extent.height);
                     graphics.fillRect(scrollbarX, 1, scrollbarWidth, extent.height - 2);
                     graphics.drawFilledPath(TOP_ARROW_Y_POINTS, ARROW_X_POINTS, null, null);
                     int h = extent.width;
                     int h6 = h - 6;
                     int h4 = h - 4;
                     int h2 = h - 2;
                     this._tempDraw[0] = h6;
                     this._tempDraw[1] = h6;
                     this._tempDraw[2] = h4;
                     this._tempDraw[3] = h4;
                     this._tempDraw[4] = h2;
                     this._tempDraw[5] = h2;
                     this._tempDraw[6] = h;
                     this._tempDraw[7] = h;
                     this._tempDraw[8] = h2;
                     this._tempDraw[9] = h2;
                     this._tempDraw[10] = h4;
                     this._tempDraw[11] = h4;
                     graphics.drawFilledPath(this._tempDraw, ARROW_X_POINTS, null, null);
                  }
               }
            }
         } else if (this._horizontalScroll) {
            int virtualWidth = this._client.getVirtualWidth();
            if (virtualWidth <= extent.width) {
               virtualWidth = this._client.getWidth();
            }

            if (virtualWidth == 0) {
               virtualWidth = extent.width;
            }

            int leftArrowWidth = this._topOrLeftArrowBitmap.getWidth();
            int rightArrowWidth = this._bottomOrRightArrowBitmap.getWidth();
            int widthAvailable = extent.width - leftArrowWidth - rightArrowWidth;
            if (widthAvailable > 0) {
               int x1 = leftArrowWidth;
               int x2 = extent.width - rightArrowWidth;
               int scrollbarWidth = extent.width * extent.width / virtualWidth;
               this._lastCalculatedSize = scrollbarWidth;
               scrollbarWidth = MathUtilities.clamp(7, scrollbarWidth, widthAvailable);
               int scrollbarX = this._client.getHorizontalScroll() * extent.width / virtualWidth % extent.width;
               this._lastCalculatedOffset = scrollbarX;
               scrollbarX = MathUtilities.clamp(x1, scrollbarX, x2 - scrollbarWidth);
               graphics.drawBitmap(0, 0, extent.width, extent.height, this._trackBitmap, 0, 0);
               graphics.drawBitmap(0, 0, this._topOrLeftArrowBitmap.getWidth(), this._topOrLeftArrowBitmap.getHeight(), this._topOrLeftArrowBitmap, 0, 0);
               graphics.drawBitmap(
                  x2, 0, this._bottomOrRightArrowBitmap.getWidth(), this._bottomOrRightArrowBitmap.getHeight(), this._bottomOrRightArrowBitmap, 0, 0
               );
               int sliderWidth = this._sliderBitmap.getWidth();
               int sliderHeight = this._sliderBitmap.getHeight();
               int halfScrollbarWidth = scrollbarWidth >> 1;
               graphics.drawBitmap(scrollbarX, 0, halfScrollbarWidth, sliderHeight, this._sliderBitmap, 0, 0);
               graphics.drawBitmap(
                  scrollbarX + halfScrollbarWidth, 0, halfScrollbarWidth, sliderHeight, this._sliderBitmap, Math.max(0, sliderWidth - halfScrollbarWidth), 0
               );
            }
         } else {
            int virtualHeight = this._client.getVirtualHeight();
            if (virtualHeight <= extent.height) {
               if (this._client.isStyle(288230376151711744L) && this._client.getVerticalScroll() > 0) {
                  virtualHeight = extent.height + this._client.getVerticalScroll();
               } else {
                  virtualHeight = this._client.getHeight();
               }
            }

            if (virtualHeight == 0) {
               virtualHeight = extent.height;
            }

            int topArrowHeight = this._topOrLeftArrowBitmap.getHeight();
            int bottomArrowHeight = this._bottomOrRightArrowBitmap.getHeight();
            int heightAvailable = extent.height - topArrowHeight - bottomArrowHeight;
            if (heightAvailable > 0) {
               int y1 = topArrowHeight;
               int y2 = extent.height - bottomArrowHeight;
               int scrollbarHeight = extent.height * extent.height / virtualHeight;
               this._lastCalculatedSize = scrollbarHeight;
               scrollbarHeight = MathUtilities.clamp(7, scrollbarHeight, heightAvailable);
               int scrollbarY = this._client.getVerticalScroll() * extent.height / virtualHeight % extent.height;
               this._lastCalculatedOffset = scrollbarY;
               scrollbarY = MathUtilities.clamp(y1, scrollbarY, y2 - scrollbarHeight);
               graphics.drawBitmap(0, 0, extent.width, extent.height, this._trackBitmap, 0, 0);
               graphics.drawBitmap(0, 0, this._topOrLeftArrowBitmap.getWidth(), this._topOrLeftArrowBitmap.getHeight(), this._topOrLeftArrowBitmap, 0, 0);
               graphics.drawBitmap(
                  0, y2, this._bottomOrRightArrowBitmap.getWidth(), this._bottomOrRightArrowBitmap.getHeight(), this._bottomOrRightArrowBitmap, 0, 0
               );
               int sliderWidth = this._sliderBitmap.getWidth();
               int sliderHeight = this._sliderBitmap.getHeight();
               int halfScrollbarHeight = scrollbarHeight >> 1;
               graphics.drawBitmap(0, scrollbarY, sliderWidth, halfScrollbarHeight, this._sliderBitmap, 0, 0);
               graphics.drawBitmap(
                  0, scrollbarY + halfScrollbarHeight, sliderWidth, halfScrollbarHeight, this._sliderBitmap, 0, Math.max(0, sliderHeight - halfScrollbarHeight)
               );
            }
         }
      }
   }

   public final void enable() {
      this._enabled = true;
   }

   public final void disable() {
      this._enabled = false;
   }

   public final boolean isEnabled() {
      return this._enabled;
   }

   public final void setClient(Manager manager) {
      this.setClient(manager, true);
   }

   public final void setClient(Manager manager, boolean registerScrollListener) {
      this._client = manager;
      if (registerScrollListener && this._client != null) {
         this._client.setScrollListener(this);
      }
   }

   @Override
   public final void scrollChanged(Manager manager, int newHorizontalScroll, int newVerticalScroll) {
      if (this._client != null && this._enabled) {
         if (this._horizontalScroll) {
            int width = this.getWidth();
            int virtualWidth = this._client.getVirtualWidth();
            if (virtualWidth == 0) {
               return;
            }

            int scrollbarWidth = width * width / virtualWidth;
            int scrollbarX = this._client.getHorizontalScroll() * width / virtualWidth % width;
            if (this._lastCalculatedSize != scrollbarWidth || this._lastCalculatedOffset != scrollbarX) {
               this.invalidate();
               return;
            }
         } else {
            int height = this.getHeight();
            int virtualHeight = this._client.getVirtualHeight();
            if (virtualHeight == 0) {
               return;
            }

            int scrollbarHeight = height * height / virtualHeight;
            int scrollbarY = this._client.getVerticalScroll() * height / virtualHeight % height;
            if (this._lastCalculatedSize != scrollbarHeight || this._lastCalculatedOffset != scrollbarY) {
               this.invalidate();
            }
         }
      }
   }
}
