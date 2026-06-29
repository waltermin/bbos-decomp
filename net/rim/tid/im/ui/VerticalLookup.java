package net.rim.tid.im.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.TextInputDialog;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.tid.im.SLInputMethod;
import net.rim.tid.im.conv.ISLVariantsObserver;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.SLVariants;
import net.rim.tid.itie.EventHandler;

public class VerticalLookup extends Lookup implements ISLVariantsObserver, TextInputDialog {
   protected int _currentVariantIndex;
   protected SLVariants _currentVariant;
   protected XYRect _bounds = (XYRect)(new Object());
   protected boolean _needBoundsCalc;
   protected int _firstItem = -1;
   protected int _lastItem = -1;
   private SLCurrentVariant _current = new SLCurrentVariant();
   protected int _maxVisible = 1;
   protected boolean _isVisible;
   private int _longestVariantWidth;
   private boolean _showArrows;
   private int _widestNumWidth;
   private int _widestProgressNumWidth;
   private int _statusAreaWidth;
   private StringBuffer _widthCalculationBuffer = (StringBuffer)(new Object());
   private String _maxVariantsStringHolder = "";
   private boolean _recalcNumbering;
   private byte _scrollStyle;
   private byte _numberingStyle;
   private Bitmap _upArrowBitmap;
   private Bitmap _downArrowBitmap;
   private XYRect _composedBounds = (XYRect)(new Object());
   public static final byte SCROLL_VARIANTS;
   public static final byte NUMBER_VARIANTS;
   public static final byte VARIANT_PROGRESS_NUMBER;
   private static final int MAXIMUM_SIDE_ARROW_FONT;
   private static final int MINIMUM_SIDE_ARROW_FONT;
   private static int _minFont = Ui.convertSize(8, 3, 0);
   private static int _maxFont = Ui.convertSize(11, 3, 0);
   private static final int GAP;
   private static final Tag TAG = Tag.create("input-method-popup");
   private static final String _upArrow;
   private static final String _downArrow;
   private static final String[] NUMBERS = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
   private static Font _lookupFont;
   private static Font _lookupJapaneseFont = null;
   private static Font _defaultFont;

   public void setScrollStyle(byte scrollStyle) {
      this._scrollStyle = scrollStyle;
      if (this.anyNumbering()) {
         this._recalcNumbering = true;
      }
   }

   public void setNumberingStyle(byte numberingStyle) {
      this._numberingStyle = numberingStyle;
      if (this.anyNumbering()) {
         this._recalcNumbering = true;
      }
   }

   public int getPrefferedHeight() {
      this.setLookupFont(_minFont, _maxFont);
      return _lookupFont.getHeight() + 50;
   }

   protected int getLongestWidth(Font font) {
      int variant_no = this._currentVariant.getVariantsCount();
      int longestVariantWidth = 0;

      for (int i = 0; i < variant_no; i++) {
         this._currentVariant.getVariantAt(i, this._current);
         int len = font.getBounds(this._current._variants, this._current._offset, this._current._length);
         longestVariantWidth = Math.max(longestVariantWidth, len);
      }

      if (longestVariantWidth > Display.getWidth() * 2 / 3) {
         longestVariantWidth = Display.getWidth() * 2 / 3;
      }

      return longestVariantWidth;
   }

   protected void setVisibleItems(int index) {
      if (this._currentVariant != null) {
         if ((this._scrollStyle & 1) == 0) {
            this._firstItem = index / this._maxVisible * this._maxVisible;
            this._lastItem = Math.min(this.getTotalItems() - 1, this._firstItem + this._maxVisible - 1);
         } else if (this._firstItem > index) {
            this._firstItem = index;
            this._lastItem = Math.min(this._firstItem + this._maxVisible - 1, this.getTotalItems() - 1);
         } else if (this._lastItem < index) {
            this._lastItem = Math.max(index, Math.min(this.getTotalItems() - 1, this._maxVisible - 1));
            this._firstItem = Math.max(this._lastItem - this._maxVisible + 1, 0);
         } else if (this._lastItem == this.getTotalItems() - 1) {
            this._firstItem = Math.max(this._lastItem - this._maxVisible + 1, 0);
         } else if (this._firstItem == 0) {
            this._lastItem = Math.min(this._maxVisible - 1, this.getTotalItems() - 1);
         } else {
            this._lastItem = Math.min(this._firstItem + this._maxVisible - 1, this.getTotalItems() - 1);
            this._firstItem = Math.max(this._lastItem - this._maxVisible + 1, 0);
         }
      }
   }

   protected int getTotalItems() {
      return this._currentVariant.getVariantsCount();
   }

   @Override
   public void listOfVariantsChanged() {
      this.resetBoundsAndIndexes();
      this.invalidateLayout0();
      if (this.isVisible()) {
         this.doLayout();
      }
   }

   @Override
   public void currentIndexChanged(int anIndex) {
      if (anIndex == -1) {
         anIndex = 0;
      }

      this._currentVariantIndex = anIndex;
      this.setVisibleItems(this._currentVariantIndex);
      this.invalidate(0, 0, this.getWidth(), this.getHeight());
   }

   @Override
   public void removeVariant(Object aVariant) {
      if (this._currentVariant == aVariant) {
         this.reset();
      }
   }

   private boolean anyNumbering() {
      return this._numberingStyle != 0;
   }

   @Override
   public void reset() {
      this._currentVariant = null;
      this._bounds.x = 0;
      this._bounds.y = 0;
      this.resetBoundsAndIndexes();
   }

   private void resetBoundsAndIndexes() {
      this._currentVariantIndex = 0;
      this._bounds.height = 0;
      this._bounds.width = 0;
      this._needBoundsCalc = true;
      this._recalcNumbering = true;
      this._firstItem = -1;
      this._lastItem = -1;
      this._longestVariantWidth = 0;
   }

   @Override
   public int actionPerformed(Object aSrc, int anAction, Object aParameter) {
      int ret = 0;
      switch (anAction) {
         case 30:
            ret = -1;
            break;
         case 31:
         default:
            this._currentVariant.nextVariant();
            break;
         case 32:
            this._currentVariant.previousVariant();
            break;
         case 33:
            int temp = this._currentVariant.getVariantsCount() - 1 - this._lastItem;
            if (temp >= 0) {
               this._currentVariant.setVariantIndex(temp == 0 ? this._lastItem : this._lastItem + 1);
            }
            break;
         case 34:
            if (this._firstItem > 0) {
               this._currentVariant.setVariantIndex(this._firstItem - 1);
            }
            break;
         case 35:
            if (aParameter != null && aParameter instanceof Object) {
               int tempx = ((Integer)aParameter).byteValue();
               if (tempx < 1 || tempx > this._lastItem - this._firstItem + 1) {
                  return 1;
               }

               tempx += this._firstItem - 1;
               this._currentVariant.setVariantIndex(tempx);
            }
      }

      return ret;
   }

   @Override
   public int getSelectedIndex() {
      return 0;
   }

   @Override
   public int getPreferredWidth() {
      return 100;
   }

   public VerticalLookup() {
      this(new VerticalLookup$LookupField());
   }

   @Override
   public void setVariants(SLVariants aVariants) {
      if (aVariants != this._currentVariant) {
         this.reset();
         if (this._currentVariant != null) {
            this._currentVariant.removeObserver(this);
         }

         this._currentVariant = aVariants;
         if (aVariants != null) {
            aVariants.setObserver(this);
         }

         this.invalidate(0, 0, this.getWidth(), this.getHeight());
      }
   }

   @Override
   public void init(SLInputMethod im, Locale locale, int aType) {
   }

   @Override
   public void setLabels(String leftLabel, String rightLabel) {
   }

   private void setLookupFont(int minSize, int maxSize) {
      _defaultFont = this.getFont();
      _lookupFont = _defaultFont;
      int fontHeight = _defaultFont.getHeight();
      if ((Locale.getDefaultInputForSystem().getCode() & -65536) == 1784741888 && _lookupJapaneseFont != null) {
         _lookupFont = _lookupJapaneseFont;
      }

      _lookupFont = _lookupFont.derive(
         _defaultFont.getStyle(),
         fontHeight < minSize ? minSize : (fontHeight > maxSize ? maxSize : fontHeight),
         0,
         _defaultFont.getAntialiasMode(),
         _defaultFont.getEffects()
      );
   }

   @Override
   public void setFont(Font aFont) {
      super.setFont(aFont);
      if (this.anyNumbering()) {
         this._recalcNumbering = true;
      }
   }

   public VerticalLookup(VerticalLookup$LookupField field) {
      super(field);
      ((VerticalLookup$LookupField)this.getDelegate()).setParent(this);
      this.setMargin(0, 0, 0, 0);
      this.setTag(TAG);
      this._upArrowBitmap = Theme.getThemeBitmap(0);
      this._downArrowBitmap = Theme.getThemeBitmap(1);

      label20:
      try {
         FontFamily family = FontFamily.forName("BBJapanese");
         _lookupJapaneseFont = family.getFont(1, 10, 2, 4, 0);
      } finally {
         break label20;
      }

      this.setLookupFont(_minFont, _maxFont);
   }

   private void initNumbers() {
      if (this._recalcNumbering) {
         if (!this.anyNumbering()) {
            this._widestNumWidth = 0;
            this._widthCalculationBuffer.setLength(0);
            this._maxVariantsStringHolder = "";
         } else {
            this._widestNumWidth = 0;

            for (int i = 1; i < 10; i++) {
               int width = _lookupFont.getBounds(NUMBERS[i]);
               if (width > this._widestNumWidth) {
                  this._widestNumWidth = width;
               }
            }

            int variantsCount = this._currentVariant.getVariantsCount();
            this._widestProgressNumWidth = this._widestNumWidth * (variantsCount < 10 ? 1 : (variantsCount < 100 ? 2 : 3));
            this._widestNumWidth += 2;
            this._maxVariantsStringHolder = Integer.toString(variantsCount);
         }
      }
   }

   @Override
   public void setBounds(int x, int y, int height, int width, int composedHeight) {
   }

   @Override
   public void setVisible(boolean isVisible) {
      if (this._isVisible != isVisible) {
         this._isVisible = isVisible;
         if (isVisible) {
            Ui.getUiEngine().pushScreen(this);
         } else {
            Ui.getUiEngine().popScreen(this);
         }

         this.invalidate(0, 0, this.getWidth(), this.getHeight());
      }
   }

   @Override
   protected void sublayout(int width, int height) {
      if (this._needBoundsCalc) {
         this.setLookupFont(_minFont, _maxFont);
         int heightPerVariant = _lookupFont.getHeight() + 1;
         int displayHeight = Display.getHeight();
         this._maxVisible = (
                  displayHeight - heightPerVariant - 2 - this.getBorderTop() - this.getBorderBottom() - this.getPaddingTop() - this.getPaddingBottom()
               )
               / 2
               / heightPerVariant
            + 1;
         if (this._maxVisible > 9) {
            this._maxVisible = 9;
         }

         int above = this._composedBounds.y;
         int space = displayHeight - above - this._composedBounds.height;
         boolean aboveFlag = above > space;
         if (aboveFlag) {
            space = above - 2 - this.getBorderBottom() - this.getPaddingBottom();
         } else {
            space -= 2 + this.getBorderTop() + this.getPaddingTop();
         }

         this._maxVisible = Math.min(this._maxVisible, space / heightPerVariant);
         if (this._maxVisible < 1) {
            this._maxVisible = 1;
         }

         if (this._currentVariant != null && this.getTotalItems() > 0) {
            if (this._firstItem != -1 && this._lastItem != -1) {
               this.currentIndexChanged(this._currentVariantIndex);
            } else {
               this.currentIndexChanged(0);
            }
         }

         this.initNumbers();
         if ((this._numberingStyle & 2) != 0) {
            this._statusAreaWidth = Math.max(_lookupFont.getBounds("▲"), this._widestProgressNumWidth) + 2;
         } else {
            this._statusAreaWidth = 0;
         }

         this._longestVariantWidth = this.getLongestWidth(_lookupFont);
         this._bounds.width = this._longestVariantWidth;
         if ((this._numberingStyle & 1) != 0) {
            this._bounds.width = this._bounds.width + this._widestNumWidth + 4;
         }

         this._bounds.height = heightPerVariant * (this._lastItem - this._firstItem + 1);
         this._showArrows = this.getTotalItems() > this._maxVisible;
         if (this._showArrows) {
            if ((this._numberingStyle & 2) != 0) {
               this._bounds.width = this._bounds.width + this._statusAreaWidth;
            } else {
               this._bounds.width = this._bounds.width + this._downArrowBitmap.getWidth();
            }
         }

         int displayWidth = Display.getWidth();
         int widthTotal = this._bounds.width + this.getBorderLeft() + this.getBorderRight() + this.getPaddingLeft() + this.getPaddingRight();
         this._bounds.x = this._composedBounds.x;
         if (this._bounds.x + widthTotal > displayWidth) {
            this._bounds.x = displayWidth - widthTotal;
         }

         if (aboveFlag) {
            this._bounds.y = this._composedBounds.y
               - this._bounds.height
               - 2
               - this.getBorderTop()
               - this.getBorderBottom()
               - this.getPaddingTop()
               - this.getPaddingBottom();
         } else {
            this._bounds.y = this._composedBounds.y + this._composedBounds.height + 2;
         }

         this._needBoundsCalc = false;
      }

      if (this._bounds != null) {
         this.setPositionDelegate(0, 0);
         this.layoutDelegate(this._bounds.width, this._bounds.height);
         this.setExtent(this._bounds.width, this._bounds.height);
         this.setPosition(this._bounds.x, this._bounds.y);
      }
   }

   @Override
   public XYRect getBounds() {
      if (this._bounds.x == 0 && this._bounds.y == 0 && this._bounds.height == 0 && this._bounds.width == 0) {
         this._bounds.x = this.getExtent().x;
         this._bounds.y = this.getExtent().y;
         this._bounds.height = this.getExtent().height;
         this._bounds.width = this.getExtent().width;
      }

      return this._bounds;
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      switch (event) {
         case 513:
         case 514:
         case 520:
            if (Keypad.key(keycode) == 0) {
               keycode = 32768;
            }

            return EventHandler.getInstance().processKeyEvent(event, keycode, key, keycode, time, true);
         default:
            return 0;
      }
   }

   @Override
   public boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      return EventHandler.getInstance().processNavigationEvent(event, dx, dy, status, time);
   }

   @Override
   public void composedTextChanged(XYRect composedBounds) {
      this._composedBounds = composedBounds;
      this.invalidateLayout0();
      this.doLayout();
      this.invalidate(0, 0, this.getWidth(), this.getHeight());
   }
}
