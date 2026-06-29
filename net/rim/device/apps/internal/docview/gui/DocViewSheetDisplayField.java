package net.rim.device.apps.internal.docview.gui;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.api.util.StringPatternRepository$Internal;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class DocViewSheetDisplayField extends DocViewDisplayField {
   private Hashtable _listFieldHash = new Hashtable(1);
   private StringBuffer _selectedCellDisplayValue = new StringBuffer();
   private final StringPatternContainer _patterns = StringPatternRepository$Internal.getStringPatterns();
   private StringPattern$Match _match;
   private ContextObject _invokeContext;
   private boolean _singleCellOnlyWorksheet;
   private CellTextDescription _cellDescription = new CellTextDescription();

   DocViewSheetDisplayField(
      DocViewDataProvider dataProvider,
      DocViewGUIProvider guiProvider,
      DocViewNotify notifyObject,
      DocViewParser docData,
      String currentItemDomID,
      int themeBgColor,
      int themeForeColor
   ) {
      super(dataProvider, guiProvider, notifyObject, docData, currentItemDomID, null, null, themeBgColor, themeForeColor, true);
   }

   @Override
   protected final void onDisplay() {
      super.onDisplay();
      this._singleCellOnlyWorksheet = false;
      if (!this.hasMultipleBlocks()) {
         DocViewSheetData[] sheets = super._parsingData.getSpreadsheets();
         if (sheets != null && sheets.length == 1) {
            DocViewSheetData sheetData = sheets[0];
            if (sheetData != null
               && sheetData.getNumberOfRows() == 1
               && sheetData.getNumberOfCols() == 1
               && sheetData.getNumHiddenRows() == 0
               && sheetData.getNumHiddenColumns() == 0) {
               label42:
               try {
                  this.getCurrentDisplayField(false).setFocusedCell(0, 0);
               } finally {
                  break label42;
               }

               this._singleCellOnlyWorksheet = true;
               if (this.cellInfo(0, 0, sheetData, false)) {
                  this.closeMainScreen();
                  return;
               }

               this._singleCellOnlyWorksheet = false;
            }
         }
      }
   }

   @Override
   protected final boolean init() {
      if (!super.init()) {
         return false;
      }

      super._application.invokeLater(new DocViewSheetDisplayField$1(this), 300, false);
      return true;
   }

   @Override
   public final void perform(int menuCode, Object cookie) {
      switch (menuCode) {
         case 4:
            super.perform(menuCode, cookie);
            return;
         case 5:
         default:
            this.cellInfo();
            return;
         case 6:
            this.goToCell();
            return;
         case 7:
            this.copyCell();
            return;
         case 8:
            this.showHideHiddenItems();
      }
   }

   @Override
   protected final boolean isCurrentDisplayItemComplete() {
      DocViewSheetDisplayField$ArznScreenField field = this.getCurrentDisplayField(false);
      return field == null || field._sheetData.isSpreadsheetComplete();
   }

   private final void dataIn() {
      if (this._listFieldHash.isEmpty()) {
         if (super._parsingData.getSpreadsheets().length > 0) {
            if (super._currentItemDomID == null) {
               super._currentItemDomID = DocViewDisplayField.getFirstArbitraryDomID(super._parsingData);
            }

            synchronized (super._application.getAppEventLock()) {
               DocViewSheetDisplayField$ArznScreenField listFld = this.getDisplayField(super._currentItemDomID, true);
               if (listFld != null) {
                  this.add(listFld);
               }

               if (this.getIndex() != -1) {
                  this.setFocus();
               }

               return;
            }
         }
      } else {
         DocViewSheetDisplayField$ArznScreenField field = this.getCurrentDisplayField(false);
         if (super._fullDocState) {
            synchronized (super._application.getAppEventLock()) {
               field.displayTitleString();
            }
         }

         Enumeration e = this._listFieldHash.elements();

         while (e.hasMoreElements()) {
            DocViewSheetDisplayField$ArznScreenField screenFld = (DocViewSheetDisplayField$ArznScreenField)e.nextElement();
            screenFld.notifyMoreDataIn(ObjectUtilities.objEqual(screenFld, field));
         }
      }
   }

   @Override
   protected final void processNewData(int currentBlockIndex) {
      super.processNewData(currentBlockIndex);
      this.dataIn();
   }

   @Override
   protected final boolean doProcessMoreData() {
      DocViewSheetDisplayField$ArznScreenField field = this.getCurrentDisplayField(false);
      return field != null && field._sheetData.isSpreadsheetComplete() && this.getPrevNextItem(super._currentItemDomID, true) != null
         ? false
         : super.doProcessMoreData();
   }

   @Override
   protected final boolean toggleDisplayMode() {
      boolean retValue = super.toggleDisplayMode();
      if (super._fullDocState) {
         try {
            this.getCurrentDisplayField(false).displayTitleString();
            return retValue;
         } finally {
            return retValue;
         }
      } else {
         return retValue;
      }
   }

   private final void applyOptions(DocViewSheetDisplayField$ArznScreenField listField) {
      DocViewOptions options = DocViewOptions.getOptions();
      short newStyle = 0;
      if (options.isOutlineCells()) {
         newStyle = (short)(newStyle | 4096);
      }

      if (options.isHorizontalScroll()) {
         newStyle = (short)(newStyle | 1);
      }

      if (options.isVerticalScroll()) {
         newStyle = (short)(newStyle | 16);
      }

      if (options.isShowLabels()) {
         newStyle = (short)(newStyle | 256);
      }

      if (newStyle != listField.getTableStyle()) {
         listField.setTableStyle(newStyle);
      }

      boolean fontNameChanged = !options.getSheetFontName().equals(listField.getFont().getFontFamily().getName());
      int newFontSize = options.getSheetFontSize();
      boolean fontSizeChanged = newFontSize != listField.getFont().getHeight(4194307);
      boolean useOriginalFontChanged = listField.getUseOriginalFont() != options.getUseOriginalSheetFont();
      boolean fontStyleChanged = listField.getFontStyle() != options.getSheetFontStyle();
      boolean isFontChange = false;
      synchronized (super._application.getAppEventLock()) {
         if (fontNameChanged || fontSizeChanged || useOriginalFontChanged || !options.getUseOriginalSheetFont() && fontStyleChanged) {
            isFontChange = listField.setNewFont(
               options.getSheetFontName(), newFontSize, fontNameChanged, fontSizeChanged, options.getUseOriginalSheetFont(), options.getSheetFontStyle()
            );
         }

         listField.setSheetColumnWidth(options.getSheetColumnWidth(), true);
         listField.initFocus();
         if (isFontChange) {
            listField.setFocusedCell(true);
         }
      }
   }

   @Override
   protected final void addCustomMenuVerbs(Menu menu, int instance) {
      super.addCustomMenuVerbs(menu, instance);
      if (super._fullDocState) {
         DocViewSheetDisplayField$ArznScreenField grid = this.getCurrentDisplayField(false);
         if (grid != null) {
            if (this.isMoreAvailable() && (!grid._sheetData.isSpreadsheetComplete() || this.getPrevNextItem(super._currentItemDomID, true) == null)) {
               menu.add(new VerbMenuItem(new DocViewGuiVerb(9, 344064, EmailResources.getResourceBundle(), 80, this), 0));
            }

            if (grid.getRowCount() > 0 && grid.getColCount() > 0) {
               menu.add(new VerbMenuItem(new DocViewGuiVerb(6, 131088, DocViewDisplayField._resources, 29, this), 0));
            } else {
               this.removeFindRelatedMenuItems(menu);
            }

            if (grid.selectedCellHasData(false)) {
               VerbMenuItem viewCellVerb = new VerbMenuItem(new DocViewGuiVerb(5, 131088, DocViewDisplayField._resources, 27, this), 0);
               menu.add(viewCellVerb);
               menu.setDefault(viewCellVerb);
            }

            if (grid.selectedCellHasData(true)) {
               menu.add(new VerbMenuItem(new DocViewGuiVerb(7, 131088, DocViewDisplayField._resources, 28, this), 0));
            }

            if (grid.sheetHasAnyHiddenRowCol()) {
               menu.add(new VerbMenuItem(new DocViewGuiVerb(8, 131088, DocViewDisplayField._resources, grid.isShowHiddenRowCol() ? 54 : 53, this), 0));
            }
         }
      }
   }

   private final void goToCell() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: bipush 0
      // 02: invokespecial net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField.getCurrentDisplayField (Z)Lnet/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField$ArznScreenField;
      // 05: astore 1
      // 06: aload 1
      // 07: ifnonnull 0d
      // 0a: goto 9b
      // 0d: aload 1
      // 0e: invokespecial net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField$ArznScreenField.getRowCount ()I
      // 11: ifle 1f
      // 14: aload 1
      // 15: invokespecial net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField$ArznScreenField.getColCount ()I
      // 18: ifle 1f
      // 1b: bipush 1
      // 1c: goto 20
      // 1f: bipush 0
      // 20: istore 2
      // 21: iload 2
      // 22: bipush 1
      // 23: if_icmpne 9b
      // 26: new net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField$GoToCellDlg
      // 29: dup
      // 2a: getstatic net/rim/device/apps/internal/docview/gui/DocViewDisplayField._resources Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 2d: bipush 41
      // 2f: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 32: invokespecial net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField$GoToCellDlg.<init> (Ljava/lang/String;)V
      // 35: astore 3
      // 36: aload 3
      // 37: invokevirtual net/rim/device/internal/ui/component/PopupDialog.show ()V
      // 3a: aload 3
      // 3b: invokevirtual net/rim/device/internal/ui/component/PopupDialog.getCloseReason ()I
      // 3e: ifne 9b
      // 41: aload 3
      // 42: invokevirtual net/rim/device/internal/ui/component/SimpleInputDialog.getText ()Ljava/lang/String;
      // 45: ldc_w 1701707776
      // 48: invokestatic net/rim/device/api/util/StringUtilities.toLowerCase (Ljava/lang/String;I)Ljava/lang/String;
      // 4b: astore 4
      // 4d: aload 4
      // 4f: invokevirtual java/lang/String.length ()I
      // 52: ifle 9b
      // 55: new net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField$CellIndex
      // 58: dup
      // 59: aconst_null
      // 5a: invokespecial net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField$CellIndex.<init> (Lnet/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField$1;)V
      // 5d: astore 5
      // 5f: aload 4
      // 61: aload 5
      // 63: invokestatic net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField.decodeGoToCellString (Ljava/lang/String;Lnet/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField$CellIndex;)V
      // 66: aload 0
      // 67: bipush 0
      // 68: invokespecial net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField.getCurrentDisplayField (Z)Lnet/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField$ArznScreenField;
      // 6b: aload 5
      // 6d: getfield net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField$CellIndex.row S
      // 70: aload 5
      // 72: getfield net/rim/device/apps/internal/docview/gui/DocViewSheetDisplayField$CellIndex.column S
      // 75: invokevirtual net/rim/device/apps/internal/docview/gui/CustomListField.setFocusedCell (II)V
      // 78: goto 98
      // 7b: astore 6
      // 7d: getstatic net/rim/device/apps/internal/docview/gui/DocViewDisplayField._resources Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 80: bipush 42
      // 82: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 85: invokestatic net/rim/device/api/ui/component/Status.show (Ljava/lang/String;)V
      // 88: goto 98
      // 8b: astore 6
      // 8d: getstatic net/rim/device/apps/internal/docview/gui/DocViewDisplayField._resources Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 90: bipush 55
      // 92: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 95: invokestatic net/rim/device/api/ui/component/Status.show (Ljava/lang/String;)V
      // 98: aconst_null
      // 99: astore 5
      // 9b: return
      // try (45 -> 56): 57 null
      // try (45 -> 56): 63 null
   }

   private final void copyCell() {
      try {
         String strCellValue = this.getCurrentDisplayField(false).getSelectedCellValue();
         if (strCellValue.length() > 0) {
            Clipboard.getClipboard().put(strCellValue);
            return;
         }
      } finally {
         return;
      }
   }

   private final void showHideHiddenItems() {
      try {
         this.getCurrentDisplayField(false).toggleRowColsVisibility();
      } finally {
         return;
      }
   }

   @Override
   protected final boolean docViewHandleCharRegular(char ch, int altStatus, int time) {
      if (super._fullDocState) {
         switch (ch) {
            case ' ':
               this.cellInfo();
               return true;
            case 'G':
            case 'g':
               this.goToCell();
               return true;
         }
      }

      return super.docViewHandleCharRegular(ch, altStatus, time);
   }

   @Override
   protected final boolean docViewHandleCharReducedKeyboard(char ch, int altStatus, int time) {
      if (super._fullDocState && altStatus == 0) {
         Screen activeScreen = null;

         label58:
         try {
            activeScreen = Ui.getUiEngine().getActiveScreen();
         } finally {
            break label58;
         }

         DocViewSheetDisplayField$ArznScreenField scrField = this.getCurrentDisplayField(false);
         switch (ch) {
            case ' ':
            case 'G':
            case 'g':
               this.cellInfo();
               return true;
            case 'A':
            case 'a':
               this.goToCell();
               return true;
            case 'B':
            case 'b':
               if (activeScreen != null) {
                  return activeScreen.dispatchTrackwheelEvent(519, 1, 0, time);
               }
               break;
            case 'D':
            case 'd':
               if (activeScreen != null) {
                  return activeScreen.dispatchTrackwheelEvent(519, -1, 1, time);
               }
               break;
            case 'J':
            case 'j':
               if (activeScreen != null) {
                  return activeScreen.dispatchTrackwheelEvent(519, 1, 1, time);
               }
               break;
            case 'M':
            case 'm':
               if (scrField != null) {
                  scrField.screenSizeMove(false, true, time);
                  return true;
               }
               break;
            case 'T':
            case 't':
               if (activeScreen != null) {
                  return activeScreen.dispatchTrackwheelEvent(519, -1, 0, time);
               }
               break;
            case 'U':
            case 'u':
               if (scrField != null) {
                  scrField.screenSizeMove(false, false, time);
                  return true;
               }
         }
      }

      return super.docViewHandleCharReducedKeyboard(ch, altStatus, time);
   }

   @Override
   protected final int getCustomStringID(int menuStringID) {
      switch (menuStringID) {
         case 2:
            return super.getCustomStringID(menuStringID);
         case 3:
         default:
            return 30;
         case 4:
            return 31;
      }
   }

   @Override
   protected final void setExtremePosition(boolean top) {
      if (super._fullDocState) {
         try {
            DocViewSheetDisplayField$ArznScreenField crtSheet = this.getCurrentDisplayField(false);
            crtSheet.setFocusedCell(top);
            if (!top
               && !this.isMoreRequestSent()
               && !crtSheet._sheetData.isSpreadsheetComplete()
               && AttachmentViewerFactory.isAutoMoreEnabled()
               && crtSheet.getRowCount() > crtSheet._maxRowIndexWithData + 1) {
               this.executeMore(null, true, false);
               return;
            }
         } finally {
            return;
         }
      }
   }

   @Override
   protected final boolean findString(boolean bSearchNext, boolean bSameString, boolean bCaseSensitive) {
      boolean bReturn = false;

      label35:
      try {
         DocViewSheetDisplayField$ArznScreenField grid = this.getCurrentDisplayField(false);
         if (grid.getRowCount() > 0 && grid.getColCount() > 0) {
            bReturn = grid.findString(bSearchNext, bSameString, bCaseSensitive);
            if (!bReturn) {
               Status.show(DocViewDisplayField._resources.getString(43));
            }
         }
      } finally {
         break label35;
      }

      if (!bReturn) {
         super._strFindString = "";
      }

      return bReturn;
   }

   @Override
   protected final boolean searchString(boolean bDisplayDlg, boolean bSearchNext) {
      try {
         DocViewSheetDisplayField$ArznScreenField grid = this.getCurrentDisplayField(false);
         if (grid.getRowCount() > 0 && grid.getColCount() > 0) {
            return super.searchString(bDisplayDlg, bSearchNext);
         }
      } finally {
         return false;
      }

      return false;
   }

   private final void cellInfo() {
      DocViewSheetDisplayField$ArznScreenField field = this.getCurrentDisplayField(false);
      if (field != null) {
         int crtRow = field.getSelectedRow();
         int crtColumn = field.getSelectedColumn();
         if ((field._nStyle & 256) != 0) {
            crtRow--;
            crtColumn--;
         }

         this.cellInfo(crtRow, crtColumn, field._sheetData, true);
      }
   }

   private final boolean cellInfo(int crtRow, int crtColumn, DocViewSheetData sheetData, boolean displayInDialog) {
      DocViewTextContentHandler cellTextHandler = null;
      if (super._parsingData.getTrackChangesOnStatus()) {
         cellTextHandler = sheetData.getCellFormatterExtern(crtRow, crtColumn, true);
      }

      if (cellTextHandler == null) {
         cellTextHandler = sheetData.getCellFormatterExtern(crtRow, crtColumn, false);
      }

      if (cellTextHandler != null && !AttachmentViewerFactory.isTextDocumentEmpty(cellTextHandler)) {
         int cellBgColor = 16777215;
         int cellForeColor = 0;
         if (Graphics.isColor()) {
            DocViewCellInfo cellInfo = null;

            label100:
            try {
               cellInfo = sheetData.getCellValue(crtRow, crtColumn);
            } finally {
               break label100;
            }

            if (cellInfo != null) {
               cellBgColor = cellInfo.getCellBgColor();
               if (cellBgColor == -1 && sheetData.sheetRowHasDefinedBgColor(crtRow)) {
                  cellBgColor = sheetData.getSheetRowBgColor(crtRow);
               }

               if (cellBgColor == -1) {
                  cellBgColor = 16777215;
               }
            }

            cellForeColor = DocViewDisplayField.adjustForeColorDefault(cellForeColor, cellBgColor);
         }

         DocViewParser cellTextParser = super._parsingData.cloneAsText(cellTextHandler);
         String title = this._selectedCellDisplayValue.toString().substring(0, this._cellDescription._prefixSize);
         if (this.displayEmbeddedLikeObject(
            cellTextParser, title, cellForeColor, cellBgColor, (byte)0, true, this._singleCellOnlyWorksheet && this.allowDocInfo()
         )) {
            return true;
         }

         if (displayInDialog) {
            DocViewDisplayScreen scr = AttachmentViewerFactory.getDisplayScreen(
               cellTextParser, title, null, 0, 1, 0, true, cellTextParser.getParsingData().getDocumentType(), cellBgColor, cellForeColor, 0, 0, (byte)0, true
            );
            if (scr != null) {
               scr.moreDataProcessed(null, 0, 1, true);
               DocViewDisplayField fld = scr._displayField;
               scr.releaseRefs();
               DocViewDisplayScreen var17 = null;
               if (fld != null && fld.init()) {
                  fld.setDataProvider(null);
                  fld.setGUIProvider(null);
                  VerticalFieldManager vfm = new VerticalFieldManager(281474976710656L);
                  vfm.add(new LabelField(title, 64));
                  vfm.add(fld);
                  PopupScreen cellInfoDlg = new PopupScreen(vfm, 196608);
                  super._application.pushModalScreen(cellInfoDlg);
                  PopupScreen var19 = null;
                  VerticalFieldManager var18 = null;
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Override
   protected final boolean selectItem(String itemDomID) {
      DocViewSheetDisplayField$ArznScreenField listFld = this.getDisplayField(itemDomID, true);
      if (listFld != null) {
         label21:
         try {
            this.deleteRange(0, 1);
         } finally {
            break label21;
         }

         this.add(listFld);
         listFld.initFocus();
         listFld.setFocusedCell(true);
         return true;
      } else {
         return false;
      }
   }

   private final DocViewSheetDisplayField$ArznScreenField getCurrentDisplayField(boolean applyOptions) {
      return this.getDisplayField(super._currentItemDomID, applyOptions);
   }

   private final DocViewSheetDisplayField$ArznScreenField getDisplayField(String domID, boolean applyOptions) {
      DocViewSheetDisplayField$ArznScreenField fld = null;
      boolean initFocus = false;
      if (this._listFieldHash.containsKey(domID)) {
         fld = (DocViewSheetDisplayField$ArznScreenField)this._listFieldHash.get(domID);
      } else {
         Object obj = super._parsingData.getObjectWithDOMID(domID);
         if (obj instanceof DocViewSheetData) {
            DocViewSheetData sheetData = (DocViewSheetData)obj;
            fld = new DocViewSheetDisplayField$ArznScreenField(this, sheetData, (short)0, DocViewOptions.getOptions().getSheetLookAndFeel(), 18014398509481984L);
            if (fld != null) {
               this._listFieldHash.put(domID, fld);
               initFocus = true;
            }
         }
      }

      if (fld != null) {
         if (applyOptions) {
            this.applyOptions(fld);
         }

         if (initFocus) {
            fld.initFocus();
         }
      }

      return fld;
   }

   private static final void decodeGoToCellString(String cellIndex, DocViewSheetDisplayField$CellIndex cellIdx) {
      boolean bSucceed = false;
      int nLength = cellIndex.length();
      if (nLength >= 2) {
         int i = 0;

         while (i < nLength && cellIndex.charAt(i) >= 'a' && cellIndex.charAt(i) <= 'z') {
            i++;
         }

         if (i < nLength && i > 0) {
            short nColumn = 0;

            for (int j = 0; j < i; j++) {
               char chValue = cellIndex.charAt(j);
               if (j < i - 1) {
                  nColumn = (short)(nColumn + 26 * (chValue - 'a' + 1));
               } else {
                  nColumn = (short)(nColumn + chValue - 'a' + 1);
               }
            }

            short row = Short.parseShort(cellIndex.substring(i));
            short column = nColumn;
            if (row > 0 && column > 0) {
               cellIdx.row = (short)(row - 1);
               cellIdx.column = (short)(column - 1);
               bSucceed = true;
            }
         }
      }

      if (!bSucceed) {
         throw new NumberFormatException();
      }
   }

   @Override
   protected final void optionsChanged() {
      this.applyOptions(this.getCurrentDisplayField(false));
   }
}
