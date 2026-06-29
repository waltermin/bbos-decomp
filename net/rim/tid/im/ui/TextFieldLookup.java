package net.rim.tid.im.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontMetrics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.TextInputDialog;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.tid.im.SLInputMethod;
import net.rim.tid.im.conv.ISLVariantsObserver;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.SLVariants;
import net.rim.tid.itie.EventHandler;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.AttributedString$Iterator;
import net.rim.vm.Array;

public class TextFieldLookup extends Lookup implements ISLVariantsObserver, TextInputDialog {
   protected int _currentVariantIndex;
   protected SLVariants _currentVariant;
   protected XYRect _bounds = (XYRect)(new Object());
   protected boolean _needBoundsCalc;
   private XYRect _composedBounds = (XYRect)(new Object());
   private SLCurrentVariant _current = new SLCurrentVariant();
   private StringBuffer _candidatesBuffer = (StringBuffer)(new Object());
   private int[] _variantPositions;
   private int _variantCount;
   protected boolean _isVisible;
   private NullField _nullField = (NullField)(new Object(18014398509481984L));
   private TextFieldLookup$LookupTextField _candidates = new TextFieldLookup$LookupTextField(9007199254740992L);
   private AttributedString _candidatesAttributedString = (AttributedString)(new Object());
   private AttributedString$Iterator _candidatesAttributedStringIterator = this._candidatesAttributedString.getIterator();
   private int _maxLookupWidth;
   private FontMetrics _fm = (FontMetrics)(new Object());
   private boolean _modelChanged;
   private VerticalFieldManager _vfm;
   private TextFieldLookup$Invoker _viewUpdateInvoker = new TextFieldLookup$Invoker(this);
   private boolean _delay;
   public static final int MAX_LINES_VISIBLE = 3;
   public static final int TEXT_LOOKUP_GAP = 2;
   private static final Tag TAG = Tag.create("input-method-popup");
   private static final int SCROLL_INDICATOR_WIDTH = 5;
   private static int _minFont = Ui.convertSize(8, 3, 0);
   private static int _maxFont = Ui.convertSize(11, 3, 0);
   private static final String JAPANESE_FONT = "BBJapanese";
   private static FontFamily _japaneseFontFamily = null;

   public int getPrefferedHeight() {
      Font f = this.getFont();
      if (f.getHeight() < _minFont) {
         f = f.derive(f.getStyle(), _minFont);
      }

      if (f.getHeight() > _maxFont) {
         f = f.derive(f.getStyle(), _maxFont);
      }

      f.getMetricsForLocale(this._fm, Locale.getDefaultInputForSystem().getCode());
      return (this._fm.iHeight + this._fm.iLeadingBelow) * 3;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void moveFocus(int x, int y) {
      if (this._currentVariantIndex != this._variantCount - 1 || x <= 0 && y <= 0) {
         if (this._currentVariantIndex != 0 || x >= 0 && y >= 0) {
            int newIndex = 0;
            if (y == 0) {
               this._currentVariantIndex += x > 0 ? 1 : -1;
               this.setIndexAndCaretPosition(this._currentVariantIndex);
            } else {
               while (true) {
                  boolean var6 = false /* VF: Semaphore variable */;

                  try {
                     var6 = true;
                     if ((newIndex = this.getVariantIndex(this._candidates.getCaretPosition())) != this._currentVariantIndex) {
                        var6 = false;
                        break;
                     }

                     this.trackwheelRoll(y > 0 ? 1 : -1, 0, 0);
                  } finally {
                     if (var6) {
                        this.setIndexAndCaretPosition(0);
                        return;
                     }
                  }
               }

               this.setIndexAndCaretPosition(newIndex);
            }
         } else {
            this.setIndexAndCaretPosition(this._variantCount - 1);
         }
      } else {
         this.setIndexAndCaretPosition(0);
      }
   }

   public boolean isLookupDisplayed() {
      return this._isVisible;
   }

   protected int getTotalItems() {
      return this._currentVariant.getVariantsCount();
   }

   public boolean isLookupVisible() {
      return this._isVisible;
   }

   @Override
   public void removeVariant(Object aVariant) {
      if (this._currentVariant == aVariant) {
         this.reset();
      }
   }

   @Override
   public void listOfVariantsChanged() {
      this.resetBoundsAndIndexes();
      this._modelChanged = true;
      this.scheduleViewUpdater();
   }

   @Override
   public void currentIndexChanged(int anIndex) {
      if (!this._modelChanged) {
         this.setIndexAndCaretPosition(anIndex);
      }
   }

   @Override
   public int getSelectedIndex() {
      return this._currentVariantIndex;
   }

   @Override
   public int getPreferredWidth() {
      return this._maxLookupWidth;
   }

   public TextFieldLookup(int totalVariants) {
      this(totalVariants, true);
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

   private void scheduleViewUpdater() {
      this._viewUpdateInvoker.cancel();
      if (this._delay) {
         this._viewUpdateInvoker.start();
      } else {
         this._viewUpdateInvoker.run();
      }
   }

   @Override
   public void composedTextChanged(XYRect composedBounds) {
      this._composedBounds = composedBounds;
      this.listOfVariantsChanged();
   }

   private void layoutText() {
      if (this._modelChanged) {
         this._candidatesBuffer.setLength(0);

         for (int i = 0; i < this._variantCount; i++) {
            this._variantPositions[i] = this._candidatesBuffer.length();
            this._currentVariant.getVariantAt(i, this._current);
            this._candidatesBuffer.append(this._current._variants, this._current._offset, this._current._length);
            if (i != this._variantCount - 1) {
               this._candidatesBuffer.append(' ');
            }
         }

         this._variantPositions[this._variantCount] = this._candidatesBuffer.length();
         this._candidatesAttributedString.replace(0, this._candidatesAttributedString.length(), this._candidatesBuffer);
         this._candidatesAttributedStringIterator.set(0, this._candidatesAttributedString.length());
         this._candidates.replace(0, this._candidates.getTextLength(), this._candidatesAttributedStringIterator, 0, 0);
         this.setIndexAndCaretPosition(-2);
         this._needBoundsCalc = true;
      }
   }

   private int getVariantIndex(int caret) {
      if (this._variantCount == 0) {
         return -1;
      }

      if (caret > this._candidates.getTextLength() - 1) {
         caret = this._candidates.getTextLength() - 1;
      }

      int previousLength = this._currentVariantIndex >= this._variantCount - 1
         ? 0
         : this._variantPositions[this._currentVariantIndex + 1] - this._variantPositions[this._currentVariantIndex] - 1;
      int i = 0;

      while (i < this._variantCount && (this._variantPositions[i] > caret || caret >= this._variantPositions[i + 1])) {
         i++;
      }

      if (i < this._variantCount) {
         if (i < this._variantCount - 2
            && caret << 1 >= this._variantPositions[i + 1] + this._variantPositions[i] - 1
            && this._variantPositions[i + 1] - caret << 1 <= previousLength + 1) {
            i++;
         }

         return i;
      } else {
         return -1;
      }
   }

   private void setIndexAndCaretPosition(int index) {
      if (this._currentVariant != null) {
         if (index < -1) {
            index = this._currentVariant.getCurrentVariantIndex();
            this._currentVariantIndex = index;
         } else {
            if (index >= this._variantCount) {
               System.err.println(((StringBuffer)(new Object("Lookup index "))).append(index).append(" >= count ").append(this._variantCount).toString());
               return;
            }

            this._currentVariant.setVariantIndex(index);
            this._currentVariantIndex = index;
         }

         boolean highlight = this._currentVariant.isCurrentVariantHiglighted();
         if (index < 0) {
            index = 0;
            highlight = false;
         }

         int begin = this._variantPositions[index];
         int end = this._variantPositions[index + 1] - 1;
         if (index + 1 < this._variantCount) {
            end--;
         }

         this._candidates.setCaretPosition(begin);
         if (highlight) {
            this._vfm.setFieldWithFocus(this._candidates);
            this._candidates.setCaretPosition(end);
            this._candidates.setSelection(begin, true, end + 1);
         } else {
            this._vfm.setFieldWithFocus(this._nullField);
         }
      }
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
            this.moveFocus(1, 0);
            break;
         case 32:
            this.moveFocus(-1, 0);
            break;
         case 33:
            this.moveFocus(0, 1);
            break;
         case 34:
            this.moveFocus(0, -1);
         case 35:
      }

      return ret;
   }

   private void resetBoundsAndIndexes() {
      this._currentVariantIndex = 0;
      this._bounds.height = 0;
      this._bounds.width = 0;
      this._needBoundsCalc = true;
   }

   private void updateViewFromModel() {
      int selectionIndex = -1;
      if (this._currentVariant != null) {
         this._variantCount = this._currentVariant.getVariantsCount();
         if (this._variantPositions.length < this._variantCount + 1) {
            Array.resize(this._variantPositions, this._variantCount + 1);
         }

         this.layoutText();
         selectionIndex = this._currentVariant.getCurrentVariantIndex();
      }

      this.invalidateLayout0();
      this.doLayout();
      this.setIndexAndCaretPosition(selectionIndex);
      this._modelChanged = false;
      if (this.isDisplayed() != this._isVisible) {
         if (this._isVisible) {
            Ui.getUiEngine().pushScreen(this);
         } else {
            Ui.getUiEngine().popScreen(this);
         }

         this.invalidate(0, 0, this.getWidth(), this.getHeight());
      }
   }

   @Override
   public void init(SLInputMethod im, Locale locale, int aType) {
   }

   @Override
   public void reset() {
      this._currentVariant = null;
      this._variantCount = 0;
      this._bounds.x = 0;
      this._bounds.y = 0;
      this.resetBoundsAndIndexes();
   }

   @Override
   public void setVisible(boolean isVisible) {
      if (this._isVisible != isVisible) {
         this._isVisible = isVisible;
         if (!this._isVisible && this.isDisplayed()) {
            Ui.getUiEngine().popScreen(this);
         }
      }
   }

   @Override
   public void setBounds(int x, int y, int height, int width, int composedHeight) {
      System.err.println("Error: TextEditLookup.setBounds() is called");
   }

   @Override
   public void setLabels(String leftLabel, String rightLabel) {
   }

   @Override
   protected void sublayout(int width, int height) {
      if (this._needBoundsCalc) {
         this._bounds.width = this._maxLookupWidth;
         this.layoutDelegate(this._bounds.width - 5, height);
         int lines = this._candidates.getDisplayLineCount();
         if (lines > 3) {
            lines = 3;
         }

         this._bounds.height = this._candidates.getLineTop(lines);
         this._needBoundsCalc = false;
      }

      if (this._bounds != null) {
         this.setPositionDelegate(0, 0);
         this.layoutDelegate(this._bounds.width - 5, this._bounds.height);
         this.setExtent(this._bounds.width, this._bounds.height);
         int heightScreen = Display.getHeight();
         int widthScreen = Display.getWidth();
         int heightLookup = this.getBorderLeft() + this.getPaddingLeft() + this._bounds.height + this.getPaddingRight() + this.getBorderRight();
         int widthLookup = this.getBorderTop() + this.getPaddingTop() + this._bounds.width + this.getPaddingBottom() + this.getBorderBottom();
         int textHeight = this._composedBounds.height;
         int y = this._composedBounds.y;
         if (y > heightScreen - y - textHeight) {
            y -= heightLookup + 2;
         } else {
            y += textHeight + 2;
         }

         int x = this._composedBounds.x;
         if (x + widthLookup > widthScreen) {
            x = widthScreen - widthLookup - 2;
         }

         this.setPosition(x, y);
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

   public TextFieldLookup(int totalVariants, boolean delay) {
      super(new TextFieldLookup$LookupVericalFieldManager(307863255777280L));
      this._delay = delay;
      this._vfm = (VerticalFieldManager)this.getDelegate();
      this._vfm.add(this._nullField);
      this._vfm.add(this._candidates);
      this._vfm.setFieldWithFocus(this._candidates);
      this.setMargin(0, 0, 0, 0);
      this.setTag(TAG);
      this._maxLookupWidth = this.getFont().getBounds("へへ ") * 4 + 5;
      if (this._maxLookupWidth > Display.getWidth()) {
         this._maxLookupWidth = Display.getWidth() * 4 / 5;
      }

      this._variantPositions = new int[totalVariants];

      try {
         _japaneseFontFamily = FontFamily.forName("BBJapanese");
      } finally {
         return;
      }
   }
}
