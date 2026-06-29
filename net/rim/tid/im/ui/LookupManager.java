package net.rim.tid.im.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.tid.im.SLInputMethod;
import net.rim.tid.im.conv.ISLVariantsObserver;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.SLVariants;

public class LookupManager extends Manager implements ISLVariantsObserver {
   protected byte _type;
   private Font _auxilaryElementsFont;
   private Font _predictiveElementsFont;
   private XYRect _bounds = (XYRect)(new Object());
   private SLCurrentVariant _current = new SLCurrentVariant();
   protected SLVariants _currentVariant;
   protected int _currentIndex;
   private String[] _numbers;
   private TextMetrics _textMetrics = (TextMetrics)(new Object());
   private int _backgroundColor;
   private int _foregroundColor;
   private LookupManager$LookupLineGroup _firstLineGroup = new LookupManager$HorizontalLookupLineGroup(this, false);
   private boolean _useAdditionalVariantsAsSecondLine = false;
   private LookupManager$LookupLineGroup _activeLineGroup = this._firstLineGroup;
   protected boolean _isPositionAboveComposedText;
   private int _composedWidth;
   private int _composedHeight;
   private int _composedX;
   private int _composedY;
   private int _lastLookupX;
   private int _lastLookupY;
   private int _lastLookupWidth;
   private int _lastLookupHeight;
   private int _lastComposedY;
   private Field _delegator;
   private boolean _alignWithComposedTextX = true;
   private boolean _allowRevertedVariants;
   private boolean _drawHorizontalSeparators;
   private boolean _avoidJumpinness = true;
   private boolean _listOfVariantsChanged;
   private int _lastAllowedHeight;
   private boolean _stickyLookupY;
   private DrawTextParam _drawTextParam = (DrawTextParam)(new Object());
   public static final byte LOOKUP_DEFAULT;
   public static final byte LOOKUP_WITH_AMOUNT;
   public static final byte LOOKUP_WITH_NUMBERS;
   public static final byte INSIDE_SPACE;
   public static final byte INSIDE_SPACE_DOUBLE;
   public static final byte INSIDE_SPACE_WITH_LINE;
   public static final byte SPACE;
   private static final int MAX_VISIBLE;
   private static String _leftArrow = "◀";
   private static String _rightArrow = "▶";
   private static String _upArrow = "▲";
   private static String _downArrow = "▼";
   private static final int LINE_WIDTH_BETWEEN_GROUPS;
   private static final int SPACE_BETWEEN_GROUPS;
   private static final int HEIGHT_SENSIBILITY;
   private static final int WIDTH_SENSIBILITY;

   public void setDelegator(Field delegator) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setAlignWithComposedTextX(boolean val) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setAllowRevertedVariants(boolean val) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setDrawHorizontalSeparators(boolean val) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setAvoidJumpinness(boolean val) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setLabels(String leftLabel, String rightLabel) {
      for (LookupManager$LookupLineGroup lineGroup = this._firstLineGroup; lineGroup != null; lineGroup = lineGroup._next) {
         lineGroup.setLabels(leftLabel, rightLabel);
      }
   }

   public void init(SLInputMethod im, Locale locale, int aType) {
      this._type = (byte)aType;

      for (LookupManager$LookupLineGroup lineGroup = this._firstLineGroup; lineGroup != null; lineGroup = lineGroup._next) {
         lineGroup.init();
      }

      if ((this._type & 8) != 0) {
         this._numbers = new Object[10];

         for (int i = 0; i < 10; i++) {
            this._numbers[i] = String.valueOf(i + 1);
         }
      }

      this._activeLineGroup = this._firstLineGroup;
   }

   public void reset() {
      for (LookupManager$LookupLineGroup lineGroup = this._firstLineGroup; lineGroup != null; lineGroup = lineGroup._next) {
         lineGroup.reset();
      }

      this._currentVariant = null;
      this._bounds.set(0, 0, 0, 0);
      this._activeLineGroup = this._firstLineGroup;
      this._stickyLookupY = false;
      this._listOfVariantsChanged = true;
   }

   public int actionPerformed(Object aSrc, int anAction, Object aParameter) {
      int ret = 0;
      switch (anAction) {
         case -114:
            if (aParameter instanceof int[]) {
               ret = this._activeLineGroup.selectElementGetShift((int[])aParameter);
            }
            break;
         case -110:
         case -109:
         case 31:
         case 32:
            if (!this._activeLineGroup.handleElementTraverse(anAction)) {
               LookupManager$LookupLineGroup group = this._activeLineGroup._next == null ? this._firstLineGroup : this._activeLineGroup._next;
               if (group._startIndex < group._endIndex) {
                  this._activeLineGroup = group;
                  this._currentVariant.setVariantIndex(this._activeLineGroup._startIndex);
               }
            }
            break;
         case 33:
            ret = this._activeLineGroup.nextPage();
            break;
         case 34:
            ret = this._activeLineGroup.prevPage();
            break;
         case 35:
            if (aParameter != null && aParameter instanceof Object) {
               ret = this._activeLineGroup.selectElement(((Integer)aParameter).byteValue());
            }
            break;
         default:
            ret = -1;
      }

      return ret;
   }

   public void setVariants(SLVariants aVariants) {
      if (aVariants != this._currentVariant) {
         if (this._currentVariant != null) {
            this._currentVariant.removeObserver(this);
         }

         this._currentVariant = aVariants;
         if (aVariants != null) {
            aVariants.setObserver(this);
         }
      }
   }

   protected int getWidth(Font font) {
      int width = 0;

      for (LookupManager$LookupLineGroup lineGroup = this._firstLineGroup; lineGroup != null; lineGroup = lineGroup._next) {
         width = Math.max(width, lineGroup.getWidth(font));
      }

      return width;
   }

   protected int getHeight(Font font) {
      int height = 0;

      for (LookupManager$LookupLineGroup lineGroup = this._firstLineGroup; lineGroup != null; lineGroup = lineGroup._next) {
         height += lineGroup.getHeight(font);
         if (lineGroup != this._firstLineGroup && lineGroup._startIndex != lineGroup._endIndex) {
            height += 6;
         }
      }

      return height;
   }

   protected Font transformFontForVariantIndex(Font origin, int variantIndex) {
      return origin;
   }

   protected int getLineCount() {
      return this._useAdditionalVariantsAsSecondLine && this._currentVariant.getAdditionalVariantsCount() > 0 ? 2 : 1;
   }

   public void composedTextChanged(XYRect composedBounds) {
      this._composedHeight = composedBounds.height;
      this._composedY = composedBounds.y;
      this._composedX = composedBounds.x;
      this._composedWidth = composedBounds.width;
   }

   protected void setUseAdditionalVariantsAsSecondLine() {
      this._useAdditionalVariantsAsSecondLine = true;
      LookupManager$LookupLineGroup secondLine = new LookupManager$VerticalLookupLineGroup(this, true);
      this._firstLineGroup._next = secondLine;
      this._allowRevertedVariants = true;
   }

   public void setStyle(byte style) {
      boolean isVertical = (style & 1) != 0;
      if (isVertical != (this._firstLineGroup instanceof LookupManager$VerticalLookupLineGroup)) {
         if (isVertical) {
            this._activeLineGroup = this._firstLineGroup = new LookupManager$VerticalLookupLineGroup(this, false);
         } else {
            this._activeLineGroup = this._firstLineGroup = new LookupManager$HorizontalLookupLineGroup(this, false);
         }
      }

      if (!isVertical) {
         this._alignWithComposedTextX = (style & 2) != 0;
      }

      this._firstLineGroup.setStyle(style);
   }

   @Override
   public void removeVariant(Object aVariant) {
      if (this._currentVariant == aVariant) {
         this.reset();
      }
   }

   @Override
   public void currentIndexChanged(int anIndex) {
      int lastIndex = this._currentIndex;
      this._currentIndex = anIndex == -1 ? 0 : anIndex;
      if (lastIndex != this._currentIndex) {
         this.invalidate();
      }
   }

   @Override
   public void listOfVariantsChanged() {
      LookupManager$LookupLineGroup lineGroup = this._firstLineGroup;
      this._listOfVariantsChanged = true;

      for (this._stickyLookupY = false; lineGroup != null; lineGroup = lineGroup._next) {
         lineGroup.listOfVariantsChanged();
      }

      this._activeLineGroup = this._firstLineGroup;
   }

   private void layout(Font aFont) {
      boolean firstInit = this._auxilaryElementsFont == null;
      if (firstInit) {
         int smallerSize = aFont.getHeight() - 2;
         this._auxilaryElementsFont = aFont.derive(aFont.getStyle(), smallerSize);
         int size = Math.max(8, smallerSize);
         this._predictiveElementsFont = aFont.derive(aFont.getStyle(), smallerSize);
         this._backgroundColor = ThemeAttributeSet.getColor(this, 2);
         this._foregroundColor = ThemeAttributeSet.getColor(this, 3);
      }

      int totalPadding = this._delegator.getBorderTop()
         + this._delegator.getBorderBottom()
         + this._delegator.getPaddingTop()
         + this._delegator.getPaddingBottom();
      int below = Display.getHeight() - (this._composedHeight + this._composedY);
      boolean avoidPositionFlip = this._avoidJumpinness && !this._listOfVariantsChanged;
      boolean lastPositionAbove = this._isPositionAboveComposedText;
      if (!avoidPositionFlip || !lastPositionAbove) {
         this._isPositionAboveComposedText = this._composedY > below;
      }

      int tryCount = 1;
      if (!this._useAdditionalVariantsAsSecondLine && this._isPositionAboveComposedText && (!avoidPositionFlip || !lastPositionAbove)) {
         this._isPositionAboveComposedText = false;
         tryCount = 2;
      }

      int lastAllowedHeight = this._lastAllowedHeight;

      for (int count = tryCount; count > 0; count--) {
         int allowedHeight = this._isPositionAboveComposedText
            ? this._composedY - totalPadding
            : Display.getHeight() - this._composedY - this._composedHeight - totalPadding;
         if (!this._listOfVariantsChanged) {
            allowedHeight = Math.min(allowedHeight, lastAllowedHeight);
         }

         this._lastAllowedHeight = allowedHeight;

         for (LookupManager$LookupLineGroup lineGroup = this._firstLineGroup; lineGroup != null; lineGroup = lineGroup._next) {
            lineGroup.layout(aFont, firstInit, allowedHeight);
            allowedHeight -= lineGroup.getHeight(aFont);
            allowedHeight -= 6;
         }

         if (count == 2) {
            if (allowedHeight > 0 && this._firstLineGroup.getVerticalElementCount() <= this._firstLineGroup.getMaxVerticalElementsPerFrame()) {
               return;
            }

            this._isPositionAboveComposedText = true;
         }
      }
   }

   @Override
   public XYRect getBounds() {
      int totalPadding = this._delegator.getBorderLeft()
         + this._delegator.getBorderRight()
         + this._delegator.getPaddingLeft()
         + this._delegator.getPaddingRight();
      int lookupWidth = this.getWidth(this.getFont()) + totalPadding;
      totalPadding = this._delegator.getBorderTop() + this._delegator.getBorderBottom() + this._delegator.getPaddingTop() + this._delegator.getPaddingBottom();
      int lookupHeight = this.getHeight(this.getFont()) + totalPadding;
      int x = 0;
      int y;
      if (!this._alignWithComposedTextX) {
         y = this._isPositionAboveComposedText ? this._composedY - lookupHeight : this._composedY + this._composedHeight;
      } else {
         boolean composedBoundsSet = this._composedWidth != 0;
         if (composedBoundsSet
            && (
               this._stickyLookupY
                  || this._avoidJumpinness
                     && !this._listOfVariantsChanged
                     && this._lastLookupX + lookupWidth < Display.getWidth()
                     && (this._lastLookupX + lookupWidth > this._composedX || this._lastComposedY != this._composedY)
            )) {
            x = this._lastLookupX;
            if (lookupHeight < this._lastLookupHeight && lookupHeight > this._lastLookupHeight - 10) {
               lookupHeight = this._lastLookupHeight;
            }

            if (lookupWidth < this._lastLookupWidth && lookupWidth > this._lastLookupWidth - 10) {
               lookupWidth = this._lastLookupWidth;
            }

            y = this._isPositionAboveComposedText
               ? Math.min(this._lastLookupY, this._composedY - lookupHeight)
               : Math.max(this._lastLookupY, this._composedY + this._composedHeight);
            if (!this._stickyLookupY) {
               this._stickyLookupY = this._lastComposedY != this._composedY;
            }
         } else {
            int centerX = this._composedX + this._composedWidth / 2;
            x = Math.max(0, centerX - lookupWidth / 2);
            if (x + lookupWidth > Display.getWidth()) {
               x = Math.max(0, Display.getWidth() - lookupWidth);
            }

            y = this._isPositionAboveComposedText ? this._composedY - lookupHeight : this._composedY + this._composedHeight;
         }
      }

      this._bounds.set(x, y, lookupWidth, lookupHeight);
      this._lastLookupX = x;
      this._lastLookupY = y;
      this._lastLookupWidth = lookupWidth;
      this._lastLookupHeight = lookupHeight;
      this._lastComposedY = this._composedY;
      return this._bounds;
   }

   @Override
   protected void sublayout(int width, int height) {
      if (this._currentVariant != null && this._composedWidth != 0) {
         this.layout(this.getFont());
         XYRect rect = this.getBounds();
         int totalHPadding = this._delegator.getBorderLeft()
            + this._delegator.getBorderRight()
            + this._delegator.getPaddingLeft()
            + this._delegator.getPaddingRight();
         int totalVPadding = this._delegator.getBorderTop()
            + this._delegator.getBorderBottom()
            + this._delegator.getPaddingTop()
            + this._delegator.getPaddingBottom();
         this.setExtent(rect.width - totalHPadding, rect.height - totalVPadding);
         this._listOfVariantsChanged = false;
      } else {
         this.setExtent(0, 0);
      }
   }

   private void nextElement() {
      if (this._currentVariant.isVariantsSeparated()) {
         this._currentVariant.nextVariant();
      } else {
         if (this._currentIndex != this._activeLineGroup._endIndex - 1) {
            this._currentVariant.nextVariant();
         }
      }
   }

   private void prevElement() {
      if (this._currentVariant.isVariantsSeparated()) {
         this._currentVariant.previousVariant();
      } else {
         if (this._currentIndex > this._activeLineGroup._startIndex) {
            this._currentVariant.previousVariant();
         }
      }
   }

   private void paintSeparator(Graphics graphics, int y) {
      graphics.fillRect(0, y + 2, this.getWidth(), 2);
   }

   public LookupManager() {
      super(0);
   }

   @Override
   protected void paint(Graphics graphics) {
      Font fm = this.getFont();
      if (this._currentVariant != null && this._currentIndex != -1 && this._currentVariant.getVariantsCount() != 0) {
         LookupManager$LookupLineGroup lineGroup = this._firstLineGroup;
         int y = 0;
         if (this._isPositionAboveComposedText) {
            if (lineGroup._next != null) {
               Font f = lineGroup._next.getFont(fm);
               graphics.setFont(f);
               lineGroup._next.paint(graphics, f, this._drawTextParam, y);
               int h = lineGroup._next.getHeight(fm);
               y += h;
               if (h > 0) {
                  this.paintSeparator(graphics, y);
                  y += 6;
               }
            }

            Font f = lineGroup.getFont(fm);
            graphics.setFont(f);
            lineGroup.paint(graphics, f, this._drawTextParam, y);
         } else {
            while (lineGroup != null) {
               Font f = lineGroup.getFont(fm);
               graphics.setFont(f);
               lineGroup.paint(graphics, f, this._drawTextParam, y);
               y += lineGroup.getHeight(fm);
               lineGroup = lineGroup._next;
               if (lineGroup != null && lineGroup._endIndex != lineGroup._startIndex) {
                  this.paintSeparator(graphics, y);
                  y += 6;
               }
            }
         }
      }
   }
}
