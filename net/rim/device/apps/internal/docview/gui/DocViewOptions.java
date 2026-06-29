package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.options.OptionsBase;

final class DocViewOptions extends OptionsBase {
   private DocViewOptions$PersistedDocViewOptions _persistedDocViewOptions;
   private static final long DOCVIEW_OPTIONS_SYNC_ITEM = -3398605801590679464L;
   private static final long PERSISTED_DOCVIEW_OPTIONS = 4949335666682152184L;
   public static final long NEW_OPTIONS_SYNCED = -2473353045158446860L;
   public static final int[] CACHE_SIZES = new int[]{
      1024000, 2048000, 3072000, 4096000, 5120000, -804650999, 6, 7, 8, 9, 10, 11, 12, 13, 14, -804651000, 7, 8, 9, 10
   };
   private static DocViewOptions _options;

   private DocViewOptions() {
   }

   public static final DocViewOptions getOptions() {
      if (_options == null) {
         _options = new DocViewOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(4949335666682152184L);
      synchronized (persistentObject) {
         this._persistedDocViewOptions = (DocViewOptions$PersistedDocViewOptions)persistentObject.getContents();
         if (this._persistedDocViewOptions == null) {
            this._persistedDocViewOptions = new DocViewOptions$PersistedDocViewOptions();
            persistentObject.setContents(this._persistedDocViewOptions, 51, false);
            persistentObject.commit();
         } else if (this._persistedDocViewOptions.init()) {
            this.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(-3398605801590679464L);
         if (syncItem == null) {
            syncItem = new DocViewOptions$DocViewOptionsSyncItem(null);
            ar.put(-3398605801590679464L, syncItem);
         }

         return syncItem;
      }
   }

   public final boolean isOutlineCells() {
      return this._persistedDocViewOptions._outlineCells;
   }

   public final void setOutlineCells(boolean outlineCells) {
      this._persistedDocViewOptions._outlineCells = outlineCells;
   }

   public final boolean isHorizontalScroll() {
      return this._persistedDocViewOptions._horzScroll;
   }

   public final void setHorizontalScroll(boolean horzScroll) {
   }

   public final boolean isVerticalScroll() {
      return this._persistedDocViewOptions._vertScroll;
   }

   public final void setVerticalScroll(boolean vertScroll) {
   }

   public final boolean isShowLabels() {
      return this._persistedDocViewOptions._showLabels;
   }

   public final void setShowLabels(boolean showLabels) {
      this._persistedDocViewOptions._showLabels = showLabels;
   }

   public final int getMaxCacheSize() {
      return this._persistedDocViewOptions._attachmentDataCacheSize;
   }

   public final void setMaxCacheSize(int maxCacheSize) {
   }

   private static final int adjustCacheSize(int value) {
      int retValue = value;
      int index = Arrays.getIndex(CACHE_SIZES, value);
      if (index == -1) {
         retValue = CACHE_SIZES[CACHE_SIZES.length - 1];
      }

      return retValue;
   }

   public final byte getSheetLookAndFeel() {
      return this._persistedDocViewOptions._lookAndFeel;
   }

   public final void setSheetLookAndFeel(byte lookAndFeel) {
   }

   public final boolean getUseOriginalDocFont() {
      return this._persistedDocViewOptions._useOriginalDocFont;
   }

   public final boolean getUseOriginalSheetFont() {
      return this._persistedDocViewOptions._useOriginalSheetFont;
   }

   private final boolean isCaseSensitiveSearch() {
      return this._persistedDocViewOptions._caseSensitiveSearch;
   }

   private final void setCaseSensitiveSearch(boolean caseSensitiveSearch) {
      this._persistedDocViewOptions._caseSensitiveSearch = caseSensitiveSearch;
   }

   public final String getDocFontName() {
      return this._persistedDocViewOptions._docFontName;
   }

   public final String getSheetFontName() {
      return this._persistedDocViewOptions._sheetFontName;
   }

   public final boolean setDocFontName(String fontName, boolean checkName) {
      boolean correctName = true;
      if (checkName) {
         correctName = this.isCorrectFontName(fontName);
      }

      if (correctName) {
         this._persistedDocViewOptions._docFontName = fontName;
         return true;
      } else {
         return false;
      }
   }

   public final void setUseOriginalDocFont(boolean useOriginalDocFont) {
      this._persistedDocViewOptions._useOriginalDocFont = useOriginalDocFont;
   }

   public final void setUseOriginalSheetFont(boolean useOriginalSheetFont) {
      this._persistedDocViewOptions._useOriginalSheetFont = useOriginalSheetFont;
   }

   public final boolean setSheetFontName(String fontName, boolean checkName) {
      boolean correctName = true;
      if (checkName) {
         correctName = this.isCorrectFontName(fontName);
      }

      if (correctName) {
         this._persistedDocViewOptions._sheetFontName = fontName;
         return true;
      } else {
         return false;
      }
   }

   private final boolean isCorrectFontName(String fontName) {
      try {
         FontFamily ff = FontFamily.forName(fontName);
         return true;
      } finally {
         ;
      }
   }

   public final int getDocFontSize() {
      return this._persistedDocViewOptions._docFontSize;
   }

   public final int getSheetFontSize() {
      return this._persistedDocViewOptions._sheetFontSize;
   }

   public final void setDocFontStyle(int fontStyle) {
      this._persistedDocViewOptions._docFontStyle = fontStyle;
   }

   public final int getDocFontStyle() {
      return this._persistedDocViewOptions._docFontStyle;
   }

   public final void setSheetFontStyle(int fontStyle) {
      this._persistedDocViewOptions._sheetFontStyle = fontStyle;
   }

   public final int getSheetFontStyle() {
      return this._persistedDocViewOptions._sheetFontStyle;
   }

   private final int checkFontSize(String fontName, int fontSize) {
      try {
         FontFamily ff = FontFamily.forName(fontName);
         if (!ff.isHeightSupported(Ui.convertSize(fontSize, 4194307, 0))) {
            return Ui.convertSize(ff.getHeights()[0], 0, 4194307);
         }
      } finally {
         return fontSize;
      }

      return fontSize;
   }

   public final void setDocFontSize(int fontSize, boolean checkSize) {
      int candidateSize = fontSize;
      if (checkSize) {
         candidateSize = this.checkFontSize(this._persistedDocViewOptions._docFontName, fontSize);
      }

      this._persistedDocViewOptions._docFontSize = candidateSize;
   }

   public final void setSheetFontSize(int fontSize, boolean checkSize) {
      int candidateSize = fontSize;
      if (checkSize) {
         candidateSize = this.checkFontSize(this._persistedDocViewOptions._sheetFontName, fontSize);
      }

      this._persistedDocViewOptions._sheetFontSize = candidateSize;
   }

   public final byte getSheetColumnWidth() {
      return this._persistedDocViewOptions._sheetColWidth;
   }

   public final void setSheetColumnWidth(byte sheetColWidth) {
      switch (sheetColWidth) {
         case -1:
         default:
            sheetColWidth = 1;
         case 0:
         case 1:
         case 2:
         case 3:
            this._persistedDocViewOptions._sheetColWidth = sheetColWidth;
      }
   }

   @Override
   public final void commit() {
      super.commit();
      this.fireOptionsChanged(-1);
   }

   public static final void editOptions(byte optionsType) {
      DocViewOptionsScreen.showEditOptionsScreen(optionsType);
   }
}
