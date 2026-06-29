package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.vm.Array;

final class DocViewOptionsScreen extends SaveableMainScreenOptionsListItem implements FieldChangeListener {
   private BooleanChoiceField _outlineCells;
   private ObjectChoiceField _sheetColumnWidth;
   private BooleanChoiceField _showLabels;
   private ObjectChoiceField _fontFamilyField;
   private ObjectChoiceField _fontSizeField;
   private ObjectChoiceField _cacheSize;
   private BooleanChoiceField _useOriginalFont;
   private RichTextField _fontSampleField;
   private ObjectChoiceField _fontStyleField;
   private Integer[] _fontSizes;
   private int[] _fontSizes_cptd;
   private boolean _isScalable;
   private FontFamily[] _fontFamilies = new Object[0];
   private DocViewOptions _docViewOptions = DocViewOptions.getOptions();
   private byte _optionsType = -1;
   private static final byte STYLE_UPDATED;
   private static final byte SIZE_UPDATED;
   private static final byte FAMILY_UPDATED;
   private static ResourceBundleFamily _optionsResources = ResourceBundle.getBundle(5215163841290712012L, "net.rim.device.apps.internal.resource.Options");
   private static ResourceBundle _resources = ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView");
   private static final int[] FONT_SIZES;
   private static final int[] FONT_STYLES;

   private DocViewOptionsScreen(byte optionsType) {
      super(_resources.getString(18));
      this._optionsType = optionsType;
      FontFamily[] families = FontFamily.getFontFamilies();

      for (int i = 0; i < families.length; i++) {
         if (isValidFontFamily(families[i])) {
            Arrays.add(this._fontFamilies, families[i]);
         }
      }
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      switch (this._optionsType) {
         case -1:
            break;
         case 0:
         default:
            this._cacheSize = (ObjectChoiceField)(new Object(
               _resources.getString(56), _resources.getStringArray(57), Arrays.getIndex(DocViewOptions.CACHE_SIZES, this._docViewOptions.getMaxCacheSize())
            ));
            mainScreen.add(this._cacheSize);
            break;
         case 1:
            this.createFontControls(
               this._docViewOptions.getDocFontName(),
               this._docViewOptions.getDocFontSize(),
               this._docViewOptions.getDocFontStyle(),
               this._docViewOptions.getUseOriginalDocFont()
            );
            break;
         case 2:
            this._outlineCells = (BooleanChoiceField)(new Object(_resources.getString(24), 0, this._docViewOptions.isOutlineCells()));
            this._sheetColumnWidth = (ObjectChoiceField)(new Object(
               _resources.getString(20), _resources.getStringArray(25), this._docViewOptions.getSheetColumnWidth()
            ));
            this._showLabels = (BooleanChoiceField)(new Object(_resources.getString(21), 0, this._docViewOptions.isShowLabels()));
            this.createFontControls(
               this._docViewOptions.getSheetFontName(),
               this._docViewOptions.getSheetFontSize(),
               this._docViewOptions.getSheetFontStyle(),
               this._docViewOptions.getUseOriginalSheetFont()
            );
            mainScreen.add(this._outlineCells);
            mainScreen.add(this._sheetColumnWidth);
            mainScreen.add(this._showLabels);
            mainScreen.add((Field)(new Object()));
      }

      if (this._optionsType == 1 || this._optionsType == 2) {
         this._fontSampleField = (RichTextField)(new Object(36028797018963968L));
         this._fontSampleField.setText(_optionsResources.getString(1465));
         mainScreen.add(this._fontFamilyField);
         if (this._optionsType == 1) {
            mainScreen.add(this._useOriginalFont);
            if (!this._useOriginalFont.isAffirmative()) {
               mainScreen.add(this._fontSizeField);
               mainScreen.add(this._fontStyleField);
               mainScreen.add(this._fontSampleField);
               this.updateChoiceOptions(this._fontFamilyField.getSelectedIndex());
               this.updateFont((byte)2, this._fontFamilyField.getSelectedIndex());
               this.updateFont((byte)1, this._fontSizeField.getSelectedIndex());
               this.updateFont((byte)0, this._fontStyleField.getSelectedIndex());
               return;
            }
         } else {
            mainScreen.add(this._fontSizeField);
            mainScreen.add(this._useOriginalFont);
            if (!this._useOriginalFont.isAffirmative()) {
               mainScreen.add(this._fontStyleField);
               mainScreen.add(this._fontSampleField);
               this.updateFont((byte)0, this._fontStyleField.getSelectedIndex());
            } else {
               mainScreen.add(this._fontSampleField);
            }

            this.updateChoiceOptions(this._fontFamilyField.getSelectedIndex());
            this.updateFont((byte)2, this._fontFamilyField.getSelectedIndex());
            this.updateFont((byte)1, this._fontSizeField.getSelectedIndex());
         }
      }
   }

   @Override
   protected final boolean save() {
      switch (this._optionsType) {
         case -1:
            break;
         case 0:
         default:
            this._docViewOptions.setMaxCacheSize(DocViewOptions.CACHE_SIZES[this._cacheSize.getSelectedIndex()]);
            break;
         case 1:
            this._docViewOptions.setDocFontName(this._fontFamilies[this._fontFamilyField.getSelectedIndex()].getName(), false);
            boolean useOriginalDocFont = this._useOriginalFont.isAffirmative();
            this._docViewOptions.setUseOriginalDocFont(useOriginalDocFont);
            if (!useOriginalDocFont) {
               this._docViewOptions.setDocFontSize(this._fontSizes_cptd[this._fontSizeField.getSelectedIndex()], false);
            }

            this._docViewOptions.setDocFontStyle(FONT_STYLES[this._fontStyleField.getSelectedIndex()]);
            break;
         case 2:
            this._docViewOptions.setOutlineCells(this._outlineCells.isAffirmative());
            this._docViewOptions.setSheetColumnWidth((byte)this._sheetColumnWidth.getSelectedIndex());
            this._docViewOptions.setShowLabels(this._showLabels.isAffirmative());
            this._docViewOptions.setSheetFontName(this._fontFamilies[this._fontFamilyField.getSelectedIndex()].getName(), false);
            boolean useOriginalSheetFont = this._useOriginalFont.isAffirmative();
            this._docViewOptions.setUseOriginalSheetFont(useOriginalSheetFont);
            this._docViewOptions.setSheetFontSize(this._fontSizes_cptd[this._fontSizeField.getSelectedIndex()], false);
            this._docViewOptions.setSheetFontStyle(FONT_STYLES[this._fontStyleField.getSelectedIndex()]);
      }

      this._docViewOptions.commit();
      return super.save();
   }

   private static final boolean isValidFontFamily(FontFamily ff) {
      return ff.isStyleSupported(0) && ff.isStyleSupported(1) && ff.isStyleSupported(4);
   }

   private final void createFontControls(String initialFontName, int initialFontHeight, int initialStyle, boolean useOriginalFont) {
      int ffIndex = this.getFontFamilyIndex(initialFontName);
      if (ffIndex == -1) {
         ffIndex = 0;
      }

      this._fontFamilyField = (ObjectChoiceField)(new Object(_resources.getString(52), this._fontFamilies, ffIndex));
      this._fontFamilyField.setChangeListener(this);
      this._fontSizeField = (ObjectChoiceField)(new Object(_resources.getString(22), null));
      this._isScalable = false;
      this.populateFontSizes(ffIndex, initialFontHeight);
      this._fontStyleField = (ObjectChoiceField)(new Object(_optionsResources.getString(1424), _optionsResources.getStringArray(1425)));
      int index = 0;
      switch (initialStyle & 0xFF) {
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
      this._useOriginalFont = (BooleanChoiceField)(new Object(_resources.getString(81), 0, useOriginalFont));
      this._useOriginalFont.setChangeListener(this);
      this._fontStyleField.setChangeListener(this);
      this._fontSizeField.setChangeListener(this);
   }

   private final void populateFontSizes(int familyIndex, int initialFontHeight) {
      FontFamily fontFamily = this._fontFamilies[familyIndex];
      boolean isNewFontScalable = (fontFamily.getTypefaceType() & FontFamily.SCALABLE_FONT) != 0;
      if (!this._isScalable || !isNewFontScalable) {
         this._isScalable = isNewFontScalable;
         int[] fontSizes = isNewFontScalable ? FONT_SIZES : fontFamily.getHeights();
         int numFontSizes = fontSizes.length;
         int[] fontSizes_ptd = new int[numFontSizes];
         int[] fontSizes_cptd = new int[numFontSizes];
         if (this._isScalable) {
            for (int index = numFontSizes - 1; index >= 0; index--) {
               fontSizes_ptd[index] = fontSizes[index];
               fontSizes_cptd[index] = Ui.convertSize(fontSizes[index], 3, 4194307);
            }
         } else {
            int out = 0;

            for (int i = 0; i < numFontSizes; i++) {
               int size_ptd = Ui.convertSize(fontSizes[i], 0, 3);
               int size_cptd = Ui.convertSize(fontSizes[i], 0, 4194307);
               if (this.checkPointSize(size_ptd)) {
                  if (out > 0 && fontSizes_ptd[out - 1] == size_ptd) {
                     if (Math.abs(size_ptd * 100 - size_cptd) >= Math.abs(size_ptd * 100 - fontSizes_cptd[out - 1])) {
                        continue;
                     }

                     out--;
                  }

                  fontSizes_ptd[out] = size_ptd;
                  fontSizes_cptd[out++] = size_cptd;
               }
            }

            if (out == 0) {
               fontSizes_ptd[out] = 10;
               fontSizes_cptd[out++] = 1000;
            }

            Array.resize(fontSizes_ptd, out);
            Array.resize(fontSizes_cptd, out);
            numFontSizes = out;
         }

         int initialFontHeightIndex = 0;
         this._fontSizes = new Object[numFontSizes];

         for (int i = 0; i < numFontSizes; i++) {
            this._fontSizes[i] = (Integer)(new Object(fontSizes_ptd[i]));
            if (initialFontHeight == fontSizes_cptd[i]) {
               initialFontHeightIndex = i;
            }
         }

         this._fontSizes_cptd = fontSizes_cptd;
         this._fontSizeField.setChoices(this._fontSizes);
         this._fontSizeField.setSelectedIndex(initialFontHeightIndex);
      }
   }

   private final int getFontFamilyIndex(String fontName) {
      for (int i = 0; i < this._fontFamilies.length; i++) {
         if (this._fontFamilies[i].getName().equals(fontName)) {
            return i;
         }
      }

      return -1;
   }

   private final boolean checkPointSize(int aSize) {
      for (int index = 0; index < FONT_SIZES.length; index++) {
         if (FONT_SIZES[index] == aSize) {
            return true;
         }
      }

      return false;
   }

   static final void showEditOptionsScreen(byte optionsType) {
      DocViewOptionsScreen optionsScreen = new DocViewOptionsScreen(optionsType);
      optionsScreen.perform(6099736323056465049L, null);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      Field original = field.getOriginal();
      if (ObjectUtilities.objEqual(original, this._fontFamilyField)) {
         if (field instanceof Object) {
            this.populateFontSizes(((ChoiceField)field).getSelectedIndex(), this._fontSizes_cptd[this._fontSizeField.getSelectedIndex()]);
            this.updateChoiceOptions(((ChoiceField)field).getSelectedIndex());
            this.updateFont((byte)2, ((ChoiceField)field).getSelectedIndex());
            return;
         }
      } else if (ObjectUtilities.objEqual(original, this._fontSizeField)) {
         if (field instanceof Object) {
            this.updateFont((byte)1, ((ChoiceField)field).getSelectedIndex());
            return;
         }
      } else if (ObjectUtilities.objEqual(original, this._fontStyleField)) {
         if (field instanceof Object) {
            this.updateFont((byte)0, ((ChoiceField)field).getSelectedIndex());
            return;
         }
      } else if (ObjectUtilities.objEqual(original, this._useOriginalFont) && field instanceof Object) {
         boolean useOriginalFont = ((ChoiceField)field).getSelectedIndex() == 0;
         if (useOriginalFont) {
            if (this._optionsType == 1) {
               if (this._fontSizeField.getIndex() != -1) {
                  super._mainScreen.delete(this._fontSizeField);
               }

               if (this._fontSampleField.getIndex() != -1) {
                  super._mainScreen.delete(this._fontSampleField);
               }
            }

            if (this._fontStyleField.getIndex() != -1) {
               FieldChangeListener listener = this._fontStyleField.getChangeListener();
               this._fontStyleField.setChangeListener(null);
               this._fontStyleField.setSelectedIndex(0);
               this._fontStyleField.setChangeListener(listener);
               super._mainScreen.delete(this._fontStyleField);
            }
         } else {
            if (this._fontSizeField.getIndex() == -1) {
               super._mainScreen.add(this._fontSizeField);
            }

            if (this._fontStyleField.getIndex() == -1) {
               if (this._fontSampleField.getIndex() != -1) {
                  super._mainScreen.insert(this._fontStyleField, this._fontSampleField.getIndex());
               } else {
                  super._mainScreen.add(this._fontStyleField);
               }
            }

            if (this._fontSampleField.getIndex() == -1) {
               super._mainScreen.add(this._fontSampleField);
            }

            this.updateChoiceOptions(this._fontFamilyField.getSelectedIndex());
            this.updateFont((byte)2, this._fontFamilyField.getSelectedIndex());
            this.updateFont((byte)1, this._fontSizeField.getSelectedIndex());
         }

         if (this._fontSampleField.getIndex() != -1) {
            this.updateFont((byte)0, this._fontStyleField.getSelectedIndex());
         }
      }
   }

   private final void updateChoiceOptions(int index) {
      FontFamily fontFamily = this._fontFamilies[index];
      int type = fontFamily.getTypefaceType();
      boolean isBitmap = type == FontFamily.MONO_BITMAP_FONT || type == FontFamily.UNKNOWN_FONT;
      if (isBitmap) {
         if (this._fontStyleField.getIndex() != -1) {
            FieldChangeListener listener = this._fontStyleField.getChangeListener();
            this._fontStyleField.setChangeListener(null);
            this._fontStyleField.setSelectedIndex(0);
            this._fontStyleField.setChangeListener(listener);
            this._fontStyleField.getManager().delete(this._fontStyleField);
            return;
         }
      } else if (!this._useOriginalFont.isAffirmative() && this._fontStyleField.getIndex() == -1 && this._fontSampleField.getIndex() != -1) {
         this._fontSampleField.getManager().insert(this._fontStyleField, this._fontSampleField.getIndex());
      }
   }

   private final void updateFont(byte reason, int index) {
      Font current = this._fontSampleField.getFont();
      Font newFont = null;
      switch (reason) {
         case -1:
            break;
         case 0:
         default:
            newFont = current.derive(FONT_STYLES[index]);
            break;
         case 1:
            newFont = current.derive(FONT_STYLES[this._fontStyleField.getSelectedIndex()], this._fontSizes_cptd[index], 4194307);
            break;
         case 2:
            FontFamily family = this._fontFamilies[index];
            int type = family.getTypefaceType();
            boolean isBitmap = type == FontFamily.MONO_BITMAP_FONT || type == FontFamily.UNKNOWN_FONT;
            newFont = family.getFont(
               isBitmap ? 0 : FONT_STYLES[this._fontStyleField.getSelectedIndex()], this._fontSizes_cptd[this._fontSizeField.getSelectedIndex()], 4194307
            );
      }

      if (newFont != null) {
         this._fontSampleField.setFont(newFont);
      }
   }

   static {
      if (Display.getVerticalResolution() > 5000) {
         FONT_SIZES = new int[]{
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            51,
            207814912,
            1277135469,
            16218465,
            2781953,
            -1910540799,
            1979777154,
            1979777066,
            6646639,
            1802466817,
            1952661861,
            1979777052,
            1097165679,
            1633117294,
            1979777027,
            1281715055,
            16780049,
            1701539702,
            1634640466,
            16809570,
            67159477,
            13894656,
            2032147206,
            1634076160
         };
      } else {
         FONT_SIZES = new int[]{
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
            51,
            207814912,
            1277135469,
            16218465,
            2781953,
            -1910540799,
            1979777154,
            1979777066,
            6646639,
            1802466817,
            1952661861,
            1979777052,
            1097165679,
            1633117294,
            1979777027,
            1281715055,
            16780049,
            1701539702
         };
      }

      FONT_STYLES = new int[]{0, 1, 64, 3, 2, -804651003, 1024000, 2048000, 3072000, 4096000, 5120000, -804650999, 6, 7, 8, 9, 10, 11, 12, 13};
   }
}
