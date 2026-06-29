package net.rim.tid.im.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.TextInputDialog;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.util.ByteVector;
import net.rim.device.api.util.IntVector;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLInputMethod;
import net.rim.tid.im.conv.ISLVariantsObserver;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.SLVariants;
import net.rim.tid.itie.EventHandler;

public class LookupImpl2 extends Lookup implements ISLVariantsObserver, TextInputDialog {
   private Font _auxilaryElementsFont;
   private XYRect _bounds = (XYRect)(new Object());
   private SLCurrentVariant _current = new SLCurrentVariant();
   protected SLVariants _currentVariant;
   private ByteVector _frameOffsets;
   private ByteVector _frameArrows;
   private ByteVector _indexesOfFramesWithOneVariant = (ByteVector)(new Object(1));
   private IntVector _variantsLengths = (IntVector)(new Object(10, 3));
   private String[] _numbers;
   private StringBuffer _infoString;
   private TextMetrics _textMetrics = (TextMetrics)(new Object());
   protected int _currentIndex;
   private int _firstItem = -1;
   private int _lastItem = -1;
   private String _leftLabel;
   private String _rightLabel;
   private byte _type;
   private byte _leftLabelWidth;
   private byte _rightLabelWidth;
   private byte _rightLabelHeight;
   private byte _typeWidth;
   private int _backgroundColor;
   private int _foregroundColor;
   private int _composedHeight;
   private int _composedY;
   public static final int INPUT_METHOD_LOOKUP_PRIORITY = 550;
   private static final Tag TAG = Tag.create("input-method-popup");
   private static final int MAX_VISIBLE = 10;
   private static String _leftArrow = "◀";
   private static String _rightArrow = "▶";

   public int getPrefferedHeight() {
      return this.getGraphics().getFont().getHeight() + 50;
   }

   protected Font transformFontForVariantIndex(Font origin, int variantIndex) {
      return origin;
   }

   @Override
   public void listOfVariantsChanged() {
      this._lastItem = this._firstItem = -1;
      this._frameOffsets.setSize(0);
      this._variantsLengths.setSize(0);
      this._indexesOfFramesWithOneVariant.setSize(0);
      if (this._frameArrows != null) {
         this._frameArrows.setSize(0);
      }
   }

   @Override
   public void currentIndexChanged(int anIndex) {
      this._currentIndex = anIndex == -1 ? 0 : anIndex;
      this.invalidate(0, 0, this.getWidth(), this.getHeight());
   }

   @Override
   public void removeVariant(Object aVariant) {
      if (this._currentVariant == aVariant) {
         this.reset();
      }
   }

   @Override
   public int getSelectedIndex() {
      return 0;
   }

   @Override
   public int getPreferredWidth() {
      return 100;
   }

   public LookupImpl2() {
      super(new LookupImpl2$LookupField());
      this.setTrackballSensitivityYOffset(-30);
      ((LookupImpl2$LookupField)this.getDelegate()).setOuter(this);
      this.setMargin(0, 0, 0, 0);
      this.setTag(TAG);
   }

   private int getVariantsWidth() {
      int adjustment = 0;
      if (this._leftLabelWidth != 0) {
         adjustment += this._leftLabelWidth + 4;
      }

      if (this._rightLabelWidth != 0) {
         adjustment += this._rightLabelWidth + 4;
      }

      switch (this._type & 3) {
         case -1:
            break;
         case 0:
            adjustment += 2 * (this._typeWidth + 2);
            break;
         case 1:
         default:
            adjustment += this._typeWidth + 2;
      }

      return this.getContentWidth() - adjustment;
   }

   private int getArrowsStartPosition() {
      int adjustment = 0;
      if (this._rightLabelWidth != 0) {
         adjustment += this._rightLabelWidth + 10;
      }

      switch (this._type & 3) {
         case -1:
            break;
         case 0:
         default:
            adjustment += 2 * (this._typeWidth + 2);
            break;
         case 1:
            adjustment += this._typeWidth;
      }

      return this.getContentWidth() - adjustment;
   }

   private void setAuxilaryElementsLengths() {
      if (ThemeAttributeSet.getFocusStyle(this) == 3) {
         this._backgroundColor = ThemeAttributeSet.getColor(this, 4);
         this._foregroundColor = ThemeAttributeSet.getColor(this, 5);
      } else {
         this._backgroundColor = ThemeAttributeSet.getColor(this, 2);
         this._foregroundColor = ThemeAttributeSet.getColor(this, 3);
      }

      if (this._leftLabel != null) {
         this._leftLabelWidth = (byte)this._auxilaryElementsFont.getBounds(this._leftLabel);
      }

      if (this._rightLabel != null) {
         this._auxilaryElementsFont.measureText(this._rightLabel, 0, this._rightLabel.length(), null, this._textMetrics);
         this._rightLabelWidth = (byte)(this._textMetrics.iBoundsBrX - this._textMetrics.iBoundsTlX);
         this._rightLabelHeight = (byte)(this._textMetrics.iBoundsBrY - this._textMetrics.iBoundsTlY);
      }

      switch (this._type & 3) {
         case 0:
         default:
            this._typeWidth = (byte)this._auxilaryElementsFont.getBounds(_leftArrow);
            return;
         case 1:
            this._typeWidth = (byte)this._auxilaryElementsFont.getBounds(this._currentVariant.getVariantsCount() > 9 ? "20/20" : "0/0");
         case -1:
      }
   }

   @Override
   public void setVariants(SLVariants aVariants) {
      if (aVariants != this._currentVariant) {
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
   public void reset() {
      this._currentIndex = 0;
      this._currentVariant = null;
      this._bounds.set(0, 0, 0, 0);
      this._firstItem = -1;
      this._lastItem = -1;
      this._indexesOfFramesWithOneVariant.setSize(0);
      this._frameOffsets.setSize(0);
      this._variantsLengths.setSize(0);
      if (this._frameArrows != null) {
         this._frameArrows.setSize(0);
      }
   }

   @Override
   public void init(SLInputMethod im, Locale locale, int aType) {
      this._frameOffsets = (ByteVector)(new Object(10, 2));
      this._type = (byte)aType;
      switch (this._type & 3) {
         case -1:
            break;
         case 0:
            this._frameArrows = (ByteVector)(new Object(10, 2));
            break;
         case 1:
         default:
            this._infoString = (StringBuffer)(new Object(5));
      }

      if ((this._type & 8) != 0) {
         this._numbers = new Object[10];

         for (int i = 0; i < 10; i++) {
            this._numbers[i] = String.valueOf(i + 1);
         }
      }
   }

   private int calcFrames(Font aFont) {
      int frameIndex = this.getFrameFor();
      if (frameIndex >= 0) {
         this._firstItem = this._frameOffsets.elementAt(frameIndex) & 255;
         this._lastItem = this._frameOffsets.elementAt(frameIndex + 1) & 255;
         return frameIndex >> 1;
      }

      this._auxilaryElementsFont = aFont.derive(aFont.getStyle(), aFont.getHeight() - 2);
      this.setAuxilaryElementsLengths();
      int width = this.getVariantsWidth();
      if (width <= 0) {
         return -1;
      }

      int size = this._frameOffsets.size();
      int last = size > 0 ? this._frameOffsets.elementAt(size - 1) & 0xFF : 0;

      do {
         last = this.calculateNextFrame(aFont, last, width, (this._type & 8) != 0);
      } while (last != -1);

      return this._frameOffsets.size() / 2 - 1;
   }

   private int calculateNextFrame(Font aFont, int aStartIndex, int aWidth, boolean aDrawNumbers) {
      if (aStartIndex > this._currentIndex) {
         return -1;
      }

      int variantsCount = this._currentVariant.getVariantsCount();
      int indexInFrame = 0;
      int flag = 0;
      int index = aStartIndex;

      do {
         if (aDrawNumbers) {
            aWidth -= aFont.getBounds(this._numbers[indexInFrame]);
         }

         this._currentVariant.getVariantAt(index, this._current);
         Font fontToMeasure = this.transformFontForVariantIndex(aFont, index);
         int current_element_length = fontToMeasure.getBounds(this._current._variants, this._current._offset, this._current._length);
         if (current_element_length + 5 > aWidth) {
            break;
         }

         flag++;
         this._variantsLengths.addElement(current_element_length);
         index++;
         indexInFrame++;
         aWidth -= current_element_length + 5;
      } while (index < variantsCount && indexInFrame < 10);

      if (flag == 0) {
         index++;
         indexInFrame++;
         this._variantsLengths.addElement(0);
         this._indexesOfFramesWithOneVariant.addElement((byte)aStartIndex);
      }

      this._frameOffsets.addElement((byte)aStartIndex);
      this._frameOffsets.addElement((byte)index);
      this._firstItem = aStartIndex;
      this._lastItem = index;
      if (this._frameArrows != null) {
         this._frameArrows.addElement((byte)(this._firstItem == 0 ? 0 : 1));
         this._frameArrows.addElement((byte)(this._lastItem == this._currentVariant.getVariantsCount() ? 0 : 1));
      }

      return index;
   }

   @Override
   public void setLabels(String leftLabel, String rightLabel) {
      if (this._leftLabel != null && !this._leftLabel.equals(leftLabel)) {
         this._leftLabel = leftLabel;
         this._leftLabelWidth = 0;
      }

      if (this._rightLabel != null && !this._rightLabel.equals(rightLabel)) {
         this._rightLabel = rightLabel;
         this._rightLabelWidth = 0;
         this._rightLabelHeight = 0;
      }
   }

   private int getFrameFor() {
      int len = this._frameOffsets.size();
      int res = -1;

      for (int i = 0; i < len; i += 2) {
         int start = this._frameOffsets.elementAt(i) & 255;
         int end = this._frameOffsets.elementAt(i + 1) & 255;
         if (this._currentIndex >= start && this._currentIndex < end) {
            return i;
         }
      }

      return res;
   }

   @Override
   public int actionPerformed(Object aSrc, int anAction, Object aParameter) {
      int ret = 0;
      switch (anAction) {
         case -114:
            if (aParameter instanceof int[]) {
               int[] res = (int[])aParameter;
               int var8 = res[0];
               if (var8 < this._lastItem - this._firstItem) {
                  res[0] = var8 - (this._currentIndex - this._firstItem);
                  if (this._currentVariant.isVariantsSeparated()) {
                     res[0]++;
                  }
               } else {
                  ret = 4;
               }
            }
            break;
         case 31:
            if (!this._currentVariant.isVariantsSeparated() && this._currentIndex == this._currentVariant.getVariantsCount() - 1) {
               ret = 4;
            } else {
               this._currentVariant.nextVariant();
            }
            break;
         case 32:
            if (!this._currentVariant.isVariantsSeparated() && this._currentIndex <= 0) {
               ret = 4;
            } else {
               this._currentVariant.previousVariant();
            }
            break;
         case 33:
            if (this._currentVariant.getVariantsCount() - 1 - this._lastItem >= 0) {
               this._currentVariant.setVariantIndex(this._lastItem);
            } else {
               ret = 4;
            }
            break;
         case 34:
            if (this._firstItem > 0) {
               this._currentVariant.setVariantIndex(this._firstItem - 1);
            } else {
               ret = 4;
            }
            break;
         case 35:
            if (aParameter != null && aParameter instanceof Object) {
               int temp = ((Integer)aParameter).byteValue();
               if (temp >= 1 && temp <= this._lastItem - this._firstItem) {
                  temp += this._firstItem - 1;
                  this._currentVariant.setVariantIndex(temp);
               } else {
                  ret = 4;
               }
            }
            break;
         default:
            ret = -1;
      }

      return ret;
   }

   @Override
   public void setBounds(int x, int y, int height, int width, int composedHeight) {
      if (this._bounds.x != x || this._bounds.y != y || this._bounds.height != height || this._bounds.width != width) {
         this._bounds.set(0, y, Display.getWidth(), height);
         this.invalidateLayout0();
         this.doLayout();
         this.invalidate(0, 0, Display.getWidth(), this.getHeight());
      }
   }

   @Override
   public void composedTextChanged(XYRect composedBounds) {
      this._composedHeight = composedBounds.height;
      this._composedY = composedBounds.y;
      this.invalidateLayout0();
      this.doLayout();
      this.invalidate(0, 0, this.getWidth(), this.getHeight());
   }

   @Override
   protected void onObscured() {
      if (Ui.getUiEngine().getActiveScreen() instanceof Object) {
         this.setVisible(false);
         this.setVisible(true);
      }
   }

   @Override
   public void setVisible(boolean isVisible) {
      UiEngine engine = Ui.getUiEngine();
      if (isVisible) {
         Screen screen = ((Field)InputContext.getInstance().getInputComponent()).getScreen();
         if (screen.isGlobal()) {
            engine.pushGlobalScreen(this, engine.getGlobalPriority(screen), 4);
         } else {
            engine.pushScreen(this);
         }
      } else {
         engine.popScreen(this);
         if (engine.getActiveScreen() != null) {
            engine.getActiveScreen().setGateInput(false);
         }
      }

      this.invalidate(0, 0, this.getWidth(), this.getHeight());
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
   protected void sublayout(int width, int height) {
      super.sublayout(width, height);
      height = this.getHeight();
      int y;
      if (this._composedY + this._composedHeight + height + 2 > Display.getHeight()) {
         y = this._composedY - height - 2;
      } else {
         y = this._composedY + this._composedHeight + 2;
      }

      this._bounds.set(0, y, Display.getWidth(), height);
      this.setPosition(this._bounds.x, this._bounds.y);
   }

   @Override
   public XYRect getBounds() {
      if (this._bounds.height == 0 || this._bounds.width == 0) {
         this._bounds.set(this.getExtent());
      }

      return this._bounds;
   }

   private String formLongVariant(Font aFont) {
      this._currentVariant.getVariantAt(this._firstItem, this._current);
      StringBuffer buffer = (StringBuffer)(new Object(" ..."));
      if ((this._type & 8) != 0) {
         buffer.insert(0, "1");
      }

      int width = this.getVariantsWidth();

      for (int i = 0; i < this._current._length; i++) {
         buffer.insert(i, this._current._variants[this._current._offset + i]);
         if (aFont.getBounds(buffer) > width) {
            buffer.deleteCharAt(i);
            break;
         }
      }

      return buffer.toString();
   }

   private int paintLeftLabel(Graphics graphics, int x, int y) {
      graphics.setFont(this._auxilaryElementsFont);
      graphics.drawText(this._leftLabel, 0, this._leftLabel.length(), x, y, 0, -1);
      return this._leftLabelWidth + 2;
   }

   private void paintRightLabel(Graphics graphics, int width, int height) {
      int fg = graphics.getColor();
      int bg = graphics.getBackgroundColor();
      int x = width - (this._rightLabelWidth + 4);
      graphics.setFont(this._auxilaryElementsFont);
      graphics.setColor(this._backgroundColor);
      graphics.fillRoundRect(x - 2, 0, this._rightLabelWidth + 6, height, height / 3, height / 3);
      graphics.setColor(this._foregroundColor);
      graphics.drawText(this._rightLabel, x, height - this._rightLabelHeight >> 1);
      graphics.setColor(fg);
      graphics.setBackgroundColor(bg);
   }
}
