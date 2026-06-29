package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.setupwizard.WizardDialog;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.vm.Array;

final class ScreenKeyboardController implements FieldChangeListener {
   private VerticalFieldManager _fontSettings;
   private ChoiceField _fontFamilyField;
   private ObjectChoiceField _fontSizeField;
   private ChoiceField _fontStyleField;
   private ChoiceField _fontAntialiasField;
   private RichTextField _fontSampleField;
   private int _filterStyle;
   private int _filterAntialiasing;
   private int _filterSize;
   private FontFamily[] _fontFamilies;
   private int[] _cptFontSizes;
   private boolean _isScalable;
   private boolean _isWizard;
   private MainScreen _mainScreen;
   private boolean _isJapanese;
   private boolean _isFontSizeChanged = false;
   private static final boolean SUB_PIXEL_RENDERING_DISABLED = true;
   private static final byte STYLE_UPDATED = 0;
   private static final byte SIZE_UPDATED = 1;
   private static final byte FAMILY_UPDATED = 2;
   private static final byte ANTIALIAS_UPDATED = 3;
   private static final int[] FONT_STYLES = new int[]{0, 1, 64, 3, 2, -804651006, 1, 2, -804651005, 1, 4, 3, -804650999, 6, 7, 8, 9, 10, 11, 12};
   private static final int[] FONT_SIZES = Display.getVerticalResolution() > 5000
      ? new int[]{
         7,
         8,
         9,
         10,
         11,
         12,
         13,
         14,
         0,
         -804519929,
         10000,
         0,
         20000,
         0,
         30000,
         0,
         45000,
         0,
         60000,
         0,
         90000,
         0,
         120000,
         0,
         51,
         -804519925,
         60000,
         0,
         120000,
         0,
         300000,
         0
      }
      : new int[]{
         6,
         7,
         8,
         9,
         10,
         11,
         12,
         13,
         14,
         -804651000,
         7,
         8,
         9,
         10,
         11,
         12,
         13,
         14,
         0,
         -804519929,
         10000,
         0,
         20000,
         0,
         30000,
         0,
         45000,
         0,
         60000,
         0,
         90000,
         0,
         120000,
         0,
         51,
         -804519925
      };
   private static final int[] ANTIALIAS_VALUES = new int[]{1, 4, 3, -804650999, 6, 7, 8, 9, 10, 11, 12, 13};
   private static final int JA_SMALL_FONT_SIZE = 8;
   private static final int JA_MIN_FONT_SIZE = 10;
   private static final int JA_MAX_FONT_SIZE = 13;

   public ScreenKeyboardController() {
      this(false);
   }

   public ScreenKeyboardController(boolean isWizard) {
      this._isWizard = isWizard;
   }

   protected final void populateMainScreen(MainScreen mainScreen, Manager content) {
      this._mainScreen = mainScreen;
      this._isJapanese = (Locale.getDefaultForSystem().getCode() & -65536) == 1784741888
         || (Locale.getDefaultInputForSystem().getCode() & -65536) == 1784741888;
      Font initialFont = Font.getDefault();
      this._filterStyle = initialFont.getStyle();
      this._filterAntialiasing = initialFont.getAntialiasMode();
      if (this._filterAntialiasing == 3) {
         this._filterAntialiasing = 4;
      }

      this._filterSize = initialFont.getHeight(4194307);
      this._fontFamilyField = new ObjectChoiceField(OptionsResources.getString(1102), this._fontFamilies, initialFont.getFontFamily());
      this._fontFamilyField.setFont(mainScreen.getFontIfSet());
      this._fontSizeField = new ObjectChoiceField(OptionsResources.getString(1103), null, 0, 134217728);
      this._fontSizeField.setFont(mainScreen.getFontIfSet());
      this._isScalable = false;
      this.populateFontSizes();
      this._fontStyleField = new ObjectChoiceField(OptionsResources.getString(1424), OptionsResources.getStringArray(1425));
      this._fontStyleField.setFont(mainScreen.getFontIfSet());
      int index = 0;
      switch (initialFont.getStyle() & 0xFF) {
         case 1:
            index = 1;
            break;
         case 2:
            index = 4;
            break;
         case 3:
            index = 3;
            break;
         case 64:
            index = 2;
      }

      this._fontStyleField.setSelectedIndex(index);
      Object[] tAA = OptionsResources.getStringArray(1459);
      int currAntialias = initialFont.getAntialiasMode();
      Object[] tmpA = new Object[ANTIALIAS_VALUES.length - 1];
      System.arraycopy(tAA, 0, tmpA, 0, tmpA.length);
      tAA = tmpA;
      switch (currAntialias) {
         case 0:
         case 2:
            break;
         case 1:
         default:
            currAntialias = 0;
            break;
         case 3:
            currAntialias = 1;
            break;
         case 4:
            currAntialias = 1;
      }

      this._fontAntialiasField = new ObjectChoiceField(OptionsResources.getString(1458), tAA);
      this._fontAntialiasField.setFont(mainScreen.getFontIfSet());
      this._fontAntialiasField.setSelectedIndex(currAntialias);
      this._fontSampleField = new RichTextField(36028797018963968L);
      this._fontSampleField.setText(OptionsResources.getString(1465));
      this._fontSampleField.setFont(initialFont);
      content.add(this._fontFamilyField);
      content.add(this._fontSizeField);
      VerticalFieldManager fontSettings = new VerticalFieldManager();
      fontSettings.add(this._fontStyleField);
      fontSettings.add(this._fontAntialiasField);
      fontSettings.setBorder(0, 0, 5, 0);
      content.add(fontSettings);
      this._fontSettings = fontSettings;
      if (this._isWizard) {
         content.add(new LabelField(OptionsResources.getString(2114)));
         this._fontSampleField.setBorder(0, 10, 0, 10);
      }

      content.add(this._fontSampleField);
      this.updateChoiceOptions();
      this._fontFamilyField.setChangeListener(this);
      this._fontSizeField.setChangeListener(this);
      this._fontStyleField.setChangeListener(this);
      this._fontAntialiasField.setChangeListener(this);
   }

   protected final void initialize() {
      if (this._fontFamilies == null) {
         this._fontFamilies = FontFamily.getFontFamilies();
      }
   }

   protected final boolean save() {
      if (this._isJapanese) {
         int recommendedSize = -1;
         boolean wrongSizeChosen = false;
         boolean wrongAntialiasingChosen = true;
         boolean wrongStyleChosen = true;
         int size = 0;
         int index = this._fontSizeField.getSelectedIndex();
         if (index >= 0) {
            size = (Integer)this._fontSizeField.getChoice(index);
         }

         if (size < 8) {
            recommendedSize = 8;
         } else if (8 < size && size < 10) {
            recommendedSize = 10;
         } else if (size > 13) {
            recommendedSize = 13;
         }

         if (recommendedSize <= 0) {
            recommendedSize = size;
         } else {
            index = 0;

            while (index < this._fontSizeField.getSize() && this._fontSizeField.getChoice(index) != recommendedSize) {
               index++;
            }

            if (index < this._fontSizeField.getSize()) {
               wrongSizeChosen = true;
            }
         }

         String dialogString;
         int style;
         int antialiasMode;
         if (recommendedSize > 8) {
            style = 1;
            antialiasMode = 1;
            dialogString = OptionsResources.getString(2043);
         } else {
            style = 0;
            antialiasMode = 0;
            dialogString = OptionsResources.getString(2042);
         }

         wrongAntialiasingChosen = this._fontAntialiasField.getSelectedIndex() != antialiasMode;
         wrongStyleChosen = this._fontStyleField.getSelectedIndex() != style;
         if (wrongSizeChosen) {
            dialogString = dialogString + OptionsResources.getString(2067) + recommendedSize + OptionsResources.getString(2068);
         } else {
            dialogString = dialogString + OptionsResources.getString(2069);
         }

         if ((wrongSizeChosen || wrongAntialiasingChosen || wrongStyleChosen) && WizardDialog.ask(3, dialogString, false) == 4) {
            if (wrongSizeChosen) {
               FieldChangeListener oldListener = this._fontSizeField.getChangeListener();
               this._fontSizeField.setChangeListener(null);
               this._fontSizeField.setSelectedIndex(index);
               this._fontSizeField.setChangeListener(oldListener);
               this.updateFont((byte)1, this._fontSizeField);
            }

            if (wrongAntialiasingChosen && antialiasMode < this._fontAntialiasField.getSize()) {
               FieldChangeListener oldListener = this._fontAntialiasField.getChangeListener();
               this._fontAntialiasField.setChangeListener(null);
               this._fontAntialiasField.setSelectedIndex(antialiasMode);
               this._fontAntialiasField.setChangeListener(oldListener);
               this.updateFont((byte)3, this._fontAntialiasField);
            }

            if (wrongStyleChosen && style < this._fontStyleField.getSize()) {
               FieldChangeListener oldListener = this._fontStyleField.getChangeListener();
               this._fontStyleField.setChangeListener(null);
               this._fontStyleField.setSelectedIndex(style);
               this._fontStyleField.setChangeListener(oldListener);
               this.updateFont((byte)0, this._fontStyleField);
            }
         }
      }

      if (this._mainScreen != null && this._mainScreen.isDirty()) {
         Font font = this._fontSampleField.getFont();
         if (font != null) {
            Font.setDefaultFontForSystem(font);
         }
      }

      return true;
   }

   protected final boolean discard() {
      return true;
   }

   private final boolean isPointSizeValid(int size) {
      for (int index = 0; index < FONT_SIZES.length; index++) {
         if (FONT_SIZES[index] == size) {
            return true;
         }
      }

      return false;
   }

   private final void populateFontSizes() {
      FontFamily fontFamily = this._fontFamilies[this._fontFamilyField.getSelectedIndex()];
      if (!this._isScalable || (fontFamily.getTypefaceType() & FontFamily.SCALABLE_FONT) == 0) {
         this._isScalable = (fontFamily.getTypefaceType() & FontFamily.SCALABLE_FONT) != 0;
         int out = 0;
         int currentSize = Font.getDefault().getHeight();
         currentSize = Ui.convertSize(currentSize, 0, 3);
         Integer[] ptFontSizes;
         int numFontSizes;
         if (this._isScalable) {
            int[] fontSizes = FONT_SIZES;
            numFontSizes = fontSizes.length;
            ptFontSizes = new Integer[numFontSizes];
            this._cptFontSizes = new int[numFontSizes];

            for (int i = 0; i < numFontSizes; i++) {
               int ptSize = fontSizes[i];
               if (!this._isJapanese || ptSize == currentSize || ptSize == 8 || 10 <= ptSize && ptSize <= 13) {
                  ptFontSizes[out] = new Integer(ptSize);
                  this._cptFontSizes[out++] = Ui.convertSize(ptSize, 3, 4194307);
               }
            }
         } else {
            int[] fontSizes = fontFamily.getHeights();
            numFontSizes = fontSizes.length;
            ptFontSizes = new Integer[numFontSizes];
            this._cptFontSizes = new int[numFontSizes];
            out = 0;

            for (int i = 0; i < numFontSizes; i++) {
               int ptSize = Ui.convertSize(fontSizes[i], 0, 3);
               int cptSize = Ui.convertSize(fontSizes[i], 0, 4194307);
               if (this.isPointSizeValid(ptSize) && (!this._isJapanese || ptSize == currentSize || ptSize == 8 || 10 <= ptSize && ptSize <= 13)) {
                  if (out > 0 && ptFontSizes[out - 1] == ptSize) {
                     if (Math.abs(ptSize * 100 - cptSize) >= Math.abs(ptSize * 100 - this._cptFontSizes[out - 1])) {
                        continue;
                     }

                     out--;
                  }

                  ptFontSizes[out] = new Integer(ptSize);
                  this._cptFontSizes[out++] = cptSize;
               }
            }
         }

         if (out == 0) {
            ptFontSizes[out] = new Integer(10);
            this._cptFontSizes[out++] = 1000;
         }

         if (out != numFontSizes) {
            Array.resize(ptFontSizes, out);
            Array.resize(this._cptFontSizes, out);
         }

         int currentIndex = this._fontSizeField.getSelectedIndex();
         int initialFontHeight_cpt;
         if (currentIndex != -1) {
            initialFontHeight_cpt = this._filterSize;
         } else {
            int initialFontHeightPx = Font.getDefault().getHeight(0);
            initialFontHeight_cpt = this._filterSize;
         }

         int initialFontHeightIndex_cpt = 0;

         for (int var17 = 0; var17 < this._cptFontSizes.length; var17++) {
            if (initialFontHeight_cpt >= this._cptFontSizes[var17]) {
               initialFontHeightIndex_cpt = var17;
            }
         }

         if (initialFontHeightIndex_cpt < this._cptFontSizes.length - 1
            && Ui.convertSize(initialFontHeight_cpt, 4194307, 3) > Ui.convertSize(this._cptFontSizes[initialFontHeightIndex_cpt], 4194307, 3)) {
            initialFontHeightIndex_cpt++;
         }

         FieldChangeListener oldListener = this._fontSizeField.getChangeListener();
         this._fontSizeField.setChangeListener(null);
         this._fontSizeField.setChoices(ptFontSizes);
         this._fontSizeField.setSelectedIndex(initialFontHeightIndex_cpt);
         this._fontSizeField.setChangeListener(oldListener);
      }
   }

   private final void updateChoiceOptions() {
      FontFamily fontFamily = this._fontFamilies[this._fontFamilyField.getSelectedIndex()];
      int type = fontFamily.getTypefaceType();
      boolean isBitmap = type == FontFamily.MONO_BITMAP_FONT || type == FontFamily.UNKNOWN_FONT;
      ChoiceField fontAntialiasField = this._fontAntialiasField;
      VerticalFieldManager fontSettings = this._fontSettings;
      ChoiceField fontStyleField = this._fontStyleField;
      if (isBitmap) {
         if (fontStyleField.getManager() != null) {
            fontSettings.delete(fontStyleField);
            FieldChangeListener oldListener = fontStyleField.getChangeListener();
            fontStyleField.setChangeListener(null);
            fontStyleField.setSelectedIndex(0);
            fontStyleField.setChangeListener(oldListener);
         }

         if (fontAntialiasField.getManager() != null) {
            fontSettings.delete(fontAntialiasField);
            return;
         }
      } else {
         if (fontStyleField.getManager() == null) {
            fontSettings.add(fontStyleField);
            FieldChangeListener oldListener = fontStyleField.getChangeListener();
            fontStyleField.setChangeListener(null);
            int index = 0;

            while (index < FONT_STYLES.length && FONT_STYLES[index] != this._filterStyle) {
               index++;
            }

            fontStyleField.setSelectedIndex(index);
            fontStyleField.setChangeListener(oldListener);
         }

         int size = this._cptFontSizes[this._fontSizeField.getSelectedIndex()];
         if (this._isJapanese) {
            if (this._isFontSizeChanged) {
               size = Ui.convertSize(size, 4194307, 3);
               FieldChangeListener oldListener = fontAntialiasField.getChangeListener();
               FieldChangeListener oldStyleListener = fontStyleField.getChangeListener();
               fontAntialiasField.setChangeListener(null);
               fontStyleField.setChangeListener(null);
               if (size == 8) {
                  fontAntialiasField.setSelectedIndex(0);
                  fontAntialiasField.setChangeListener(oldListener);
                  fontStyleField.setSelectedIndex(0);
                  fontStyleField.setChangeListener(oldStyleListener);
               } else {
                  if (fontAntialiasField.getManager() == null) {
                     fontSettings.add(fontAntialiasField);
                  }

                  if (fontStyleField.getManager() == null) {
                     fontSettings.add(fontStyleField);
                  }

                  fontAntialiasField.setSelectedIndex(1);
                  fontAntialiasField.setChangeListener(oldListener);
                  fontStyleField.setSelectedIndex(1);
                  fontStyleField.setChangeListener(oldStyleListener);
               }

               this.updateFont((byte)3, fontAntialiasField);
               this.updateFont((byte)0, fontStyleField);
               this._isFontSizeChanged = false;
            }
         } else if (Ui.getMode() != 2 && Ui.convertSize(size, 4194307, 0) < 14) {
            FieldChangeListener oldListener = fontAntialiasField.getChangeListener();
            fontAntialiasField.setChangeListener(null);
            fontAntialiasField.setSelectedIndex(0);
            fontAntialiasField.setChangeListener(oldListener);
            if (fontAntialiasField.getManager() != null) {
               fontSettings.delete(fontAntialiasField);
               return;
            }
         } else if (fontAntialiasField.getManager() == null) {
            fontSettings.add(fontAntialiasField);
            FieldChangeListener oldListener = fontAntialiasField.getChangeListener();
            fontAntialiasField.setChangeListener(null);
            int currAntialias = 0;
            switch (this._filterAntialiasing) {
               case 0:
               case 2:
                  break;
               case 1:
               default:
                  currAntialias = 0;
                  break;
               case 3:
                  currAntialias = 1;
                  break;
               case 4:
                  currAntialias = 1;
            }

            fontAntialiasField.setSelectedIndex(currAntialias);
            fontAntialiasField.setChangeListener(oldListener);
            return;
         }
      }
   }

   private final void updateFont(byte reason, ChoiceField field) {
      Font current = this._fontSampleField.getFont();
      Font newFont = null;
      switch (reason) {
         case -1:
            break;
         case 0:
         default:
            this._filterStyle = FONT_STYLES[field.getSelectedIndex()];
            newFont = current.derive(this._filterStyle);
            break;
         case 1:
            this._filterSize = this._cptFontSizes[field.getSelectedIndex()];
            newFont = current.derive(
               FONT_STYLES[this._fontStyleField.getSelectedIndex()],
               this._filterSize,
               4194307,
               ANTIALIAS_VALUES[this._fontAntialiasField.getSelectedIndex()],
               0
            );
            break;
         case 2:
            FontFamily family = this._fontFamilies[field.getSelectedIndex()];
            int type = family.getTypefaceType();
            boolean isBitmap = type == FontFamily.MONO_BITMAP_FONT || type == FontFamily.UNKNOWN_FONT;
            newFont = family.getFont(
               isBitmap ? 0 : FONT_STYLES[this._fontStyleField.getSelectedIndex()],
               this._filterSize,
               4194307,
               isBitmap ? 1 : ANTIALIAS_VALUES[this._fontAntialiasField.getSelectedIndex()],
               0
            );
            break;
         case 3:
            this._filterAntialiasing = ANTIALIAS_VALUES[field.getSelectedIndex()];
            newFont = current.derive(current.getStyle(), current.getHeight(), 0, this._filterAntialiasing, 0);
      }

      if (newFont != null) {
         this._fontSampleField.setFont(newFont);
         this._mainScreen.doPaint();
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      Field original = field.getOriginal();
      if (original == this._fontFamilyField) {
         this.populateFontSizes();
         this.updateChoiceOptions();
         this.updateFont((byte)2, this._fontFamilyField);
      } else if (original == this._fontSizeField) {
         this._isFontSizeChanged = true;
         this.updateChoiceOptions();
         this.updateFont((byte)1, this._fontSizeField);
      } else if (original == this._fontStyleField) {
         this.updateFont((byte)0, this._fontStyleField);
      } else {
         if (original == this._fontAntialiasField) {
            this.updateFont((byte)3, this._fontAntialiasField);
         }
      }
   }
}
