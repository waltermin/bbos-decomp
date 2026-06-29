package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.vm.Persistable;

final class DocViewOptions$PersistedDocViewOptions implements Persistable {
   private boolean _outlineCells = true;
   private boolean _horzScroll;
   private boolean _vertScroll;
   private boolean _showLabels = true;
   private byte _lookAndFeel;
   private boolean _caseSensitiveSearch;
   private String _docFontName;
   private int _docFontSize;
   private byte _sheetColWidth;
   private int _attachmentDataCacheSize;
   private String _sheetFontName;
   private int _sheetFontSize;
   private boolean _useOriginalDocFont = true;
   private int _docFontStyle;
   private int _sheetFontStyle;
   private boolean _useOriginalSheetFont = true;

   DocViewOptions$PersistedDocViewOptions() {
      this.initLookAndFeel();
      this._sheetColWidth = 3;
      this._attachmentDataCacheSize = DocViewOptions.CACHE_SIZES[DocViewOptions.CACHE_SIZES.length - 1];
      Font defaultFont = Font.getDefault();
      this._docFontSize = this._sheetFontSize = defaultFont.getHeight(4194307);
      this._docFontName = this._sheetFontName = defaultFont.getFontFamily().toString();
      this._docFontStyle = this._sheetFontStyle = 0;
   }

   final boolean init() {
      boolean retValue = false;
      byte crtLookAndFeel = this._lookAndFeel;
      this.initLookAndFeel();
      retValue = crtLookAndFeel != this._lookAndFeel;
      int crtCacheSize = this._attachmentDataCacheSize;
      this._attachmentDataCacheSize = DocViewOptions.CACHE_SIZES[DocViewOptions.CACHE_SIZES.length - 1];
      if (!retValue) {
         retValue = crtCacheSize != this._attachmentDataCacheSize;
      }

      return retValue;
   }

   private final void initLookAndFeel() {
      if (Graphics.isColor()) {
         this._lookAndFeel = 1;
      } else {
         this._lookAndFeel = 2;
      }
   }
}
