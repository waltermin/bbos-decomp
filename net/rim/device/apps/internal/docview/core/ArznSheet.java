package net.rim.device.apps.internal.docview.core;

final class ArznSheet extends ArznObject {
   private ArznSheetData _sheetData;

   @Override
   final void initialize(UCSParser docParser, ArznClassFactory classFactory, ArznObject parent) {
      super.initialize(docParser, classFactory, parent);
      this._sheetData = null;
   }

   @Override
   protected final void setCellBgColor(int row, int column, int color) {
      if (this._sheetData != null && row != -1 && column != -1) {
         this._sheetData.setCellBgColor(row, column, color);
      }
   }

   @Override
   protected final void setTableRowBgColor(int row, int color, int startColumn, int endColumn) {
      if (this._sheetData != null && row != -1) {
         if (startColumn == -1 && endColumn == -1) {
            this._sheetData.setTableRowBgColor(row, color);
            return;
         }

         if (startColumn >= 0 && endColumn >= 0 && startColumn <= endColumn) {
            for (int i = startColumn; i <= endColumn; i++) {
               this._sheetData.setCellBgColor(row, i, color);
            }
         }
      }
   }

   @Override
   protected final void beforeCellParsing(int row, int column, boolean isCompleteCell) {
      if (this._sheetData != null) {
         this._sheetData.beforeCellParsing(row, column, isCompleteCell);
      }
   }

   @Override
   protected final void afterCellParsing() {
      if (this._sheetData != null) {
         this._sheetData.afterCellParsing();
      }
   }

   @Override
   protected final void beforeParagraphParsing(boolean deletedByTrackChange) {
      if (this._sheetData != null) {
         this._sheetData.beforeParagraphParsing(deletedByTrackChange);
      }
   }

   @Override
   protected final void addParagraphInfo(byte paraType, int listLevel, int beforeParaSpacing, int afterParaSpacing) {
      if (this._sheetData != null) {
         this._sheetData.addParagraphInfo(paraType, listLevel, beforeParaSpacing, afterParaSpacing);
      }
   }

   @Override
   protected final void addText(ArznParsingInfo parsingData, StringBuffer strText, boolean deletedTrackChangeText) {
      if (this._sheetData != null) {
         ArznRichTextInfo formatInfo = null;
         if (!parsingData.isDefaultStyle()) {
            int textFontStyle = parsingData.getCurrentFormattingInfo(0);
            int textForeColor = parsingData.getCurrentFormattingInfo(1);
            int textBgColor = parsingData.getCurrentFormattingInfo(2);
            int textFontSize = parsingData.getCurrentFormattingInfo(3);
            formatInfo = new ArznRichTextInfo(textFontStyle, textForeColor, textBgColor, textFontSize);
         }

         this._sheetData.addTextContent(strText, formatInfo, deletedTrackChangeText);
      }
   }

   @Override
   protected final void addInternalHyperlink(int bookmarkID) {
      if (this._sheetData != null) {
         this._sheetData.addInternalHyperlink(bookmarkID);
      }
   }

   @Override
   protected final void addExternalHyperlink(StringBuffer strTarget) {
      if (this._sheetData != null) {
         this._sheetData.addExternalHyperlink(strTarget);
      }
   }

   @Override
   protected final void addBookmarkInfo(int bookmarkID) {
      if (this._sheetData != null) {
         this._sheetData.addBookmarkInfo(bookmarkID);
      }
   }

   @Override
   protected final void markLastHyperlinkElementEndOffset() {
      if (this._sheetData != null) {
         this._sheetData.markLastHyperlinkElementEndOffset();
      }
   }

   @Override
   protected final void addTrackChange(String authorName, String dateTime) {
      if (this._sheetData != null) {
         this._sheetData.addTrackChange(authorName, dateTime);
      }
   }

   @Override
   protected final void parsedEmbeddedObjectHintEx(ArznParsingInfo parsingData, EmbeddedHint embHint) {
      if (this._sheetData != null) {
         parsingData.parsedEmbeddedObjectHint(this._sheetData, embHint);
      }
   }

   @Override
   protected final void afterParagraphParsing(boolean incompleteParagraph, boolean lastCommandInCell) {
      if (this._sheetData != null) {
         this._sheetData.afterParagraphParsing(incompleteParagraph, lastCommandInCell);
      }
   }

   @Override
   protected final void beforeParagraphParsing(ArznParsingInfo parsingData, boolean deletedByTrackChange) {
   }

   @Override
   protected final void addParagraphInfo(
      ArznParsingInfo parsingData, byte paraType, int listLevel, int beforeParaSpacing, int afterParaSpacing, boolean isPartOfTOC
   ) {
   }

   @Override
   protected final void addText(ArznParsingInfo parsingData, StringBuffer strText, boolean isPartOfTOC, boolean deletedTrackChangeText) {
   }

   @Override
   protected final void addInternalHyperlink(ArznParsingInfo parsingData, int bookmarkID, boolean isPartOfTOC) {
   }

   @Override
   protected final void addExternalHyperlink(ArznParsingInfo parsingData, StringBuffer strTarget, boolean isPartOfTOC) {
   }

   @Override
   protected final void addBookmarkInfo(ArznParsingInfo parsingData, int bookmarkID) {
   }

   @Override
   protected final void markLastHyperlinkElementEndOffset(ArznParsingInfo parsingData, boolean isPartOfTOC) {
   }

   @Override
   protected final void addTrackChange(ArznParsingInfo parsingData, String authorName, String dateTime, boolean isPartofTOC) {
   }

   @Override
   protected final void parsedEmbeddedObjectHint(ArznParsingInfo parsingData, EmbeddedHint embHint) {
   }

   @Override
   protected final void afterParagraphParsing(ArznParsingInfo parsingData, boolean incompleteParagraph, boolean isPartOfTOC, boolean isLastCommand) {
   }

   @Override
   protected final int parseContainerData(ArznParsingInfo parsingData) {
      int offset = 0;
      int commandCode = super._ucsParser.getCurrentCommandCode();
      switch (commandCode) {
         case 25:
            int nSheetRows = super._ucsParser.readUnsignedShort();
            offset += 2;
            int nSheetCols = super._ucsParser.readUnsignedShort();
            offset += 2;
            int nSheetNameLength = super._ucsParser.readUnsignedShort();
            offset += 2;
            String strSheetName = null;
            if (nSheetNameLength > 0) {
               strSheetName = (String)(new Object(super._ucsParser.readString(false, nSheetNameLength)));
               offset += nSheetNameLength;
            }

            if (super._ucsParser.getCurrentCommandSize() > offset + 2) {
               int nUnicodeSheetNameLength = super._ucsParser.readUnsignedShort();
               offset += 2;
               if (nUnicodeSheetNameLength > 0) {
                  strSheetName = (String)(new Object(super._ucsParser.readString(true, nUnicodeSheetNameLength)));
                  offset += nUnicodeSheetNameLength;
               }
            }

            this._sheetData = this.addSpreadsheet(parsingData, nSheetRows, nSheetCols, strSheetName, !super._incompleteObject);
            return offset;
         case 32:
            int color = super._ucsParser.readInt();
            offset += 4;
            int param1 = super._ucsParser.readUnsignedShort();
            offset += 2;
            this._sheetData.setTableColBgColor(param1, color);
            return offset;
         case 33:
            int index = super._ucsParser.readUnsignedByte();
            offset++;
            int param2 = super._ucsParser.readUnsignedShort();
            offset += 2;

            try {
               this._sheetData.setTableColBgColor(param2, ArznColorTable._colorTable[index]);
               return offset;
            } finally {
               ;
            }
         case 34:
         case 35:
         case 36:
         case 37:
            int shorts = super._ucsParser.getCurrentCommandSize() / 2;
            if (commandCode == 35 || commandCode == 37) {
               shorts -= shorts % 2;
            }

            if (shorts > 0) {
               int[] values = new int[shorts];

               for (int i = 0; i < shorts; i++) {
                  values[i] = super._ucsParser.readUnsignedShort();
                  offset += 2;
               }

               this.processHiddenRowColsInformation(commandCode, values);
               Object var25 = null;
            }
         default:
            return offset;
      }
   }

   private final void processHiddenRowColsInformation(int commandCode, int[] params) {
      if (this._sheetData != null) {
         int intCount = params.length;
         if (intCount > 0) {
            switch (commandCode) {
               case 33:
                  break;
               case 34:
               default:
                  for (int i = 0; i < intCount; i++) {
                     this._sheetData.setRowHidden(params[i]);
                  }
                  break;
               case 35:
                  for (int i = 0; i < intCount; i += 2) {
                     for (int j = params[i]; j <= params[i + 1]; j++) {
                        this._sheetData.setRowHidden(j);
                     }
                  }
                  break;
               case 36:
                  for (int i = 0; i < intCount; i++) {
                     this._sheetData.setColumnHidden(params[i]);
                  }
                  break;
               case 37:
                  for (int i = 0; i < intCount; i += 2) {
                     for (int j = params[i]; j <= params[i + 1]; j++) {
                        this._sheetData.setColumnHidden(j);
                     }
                  }
            }
         }
      }
   }
}
