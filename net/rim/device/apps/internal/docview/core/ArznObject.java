package net.rim.device.apps.internal.docview.core;

class ArznObject {
   protected UCSParser _ucsParser;
   protected boolean _incompleteObject = true;
   protected int _commandCode = 65535;
   protected int _commandSize;
   private int _iObjectCreationID;
   private ArznClassFactory _factory;
   private ArznObject _parent;
   private int _parsingIndex = -1;

   void initialize(UCSParser docParser, ArznClassFactory classFactory, ArznObject parent) {
      this._ucsParser = docParser;
      this._factory = classFactory;
      this._parent = parent;
      this._commandCode = this._ucsParser.getCurrentCommandCode();
      this._commandSize = this._ucsParser.getCurrentCommandSize();
      this._incompleteObject = this._ucsParser.isIncomplete(this._commandCode);
      this._iObjectCreationID = this._ucsParser.readRawCommandCode(this._commandCode);
      this._parsingIndex = -1;
   }

   int getObjectType() {
      return this._iObjectCreationID;
   }

   protected void addText(ArznParsingInfo parsingData, StringBuffer strText, boolean isPartOfTOC, boolean deletedTrackChangeText) {
      if (this._parent != null) {
         this._parent.addText(parsingData, strText, isPartOfTOC, deletedTrackChangeText);
      } else {
         if (strText.length() > 0) {
            this.addTextToBuffer(parsingData, strText, deletedTrackChangeText, isPartOfTOC);
         }
      }
   }

   protected ArznImageData addImage(ArznParsingInfo parsingData) {
      return this._parent != null ? this._parent.addImage(parsingData) : parsingData.addImage();
   }

   protected ArznSheetData addSpreadsheet(ArznParsingInfo parsingData, int rows, int columns, String name, boolean isComplete) {
      return this._parent != null
         ? this._parent.addSpreadsheet(parsingData, rows, columns, name, isComplete)
         : parsingData.addSpreadsheet(rows, columns, name, isComplete);
   }

   protected ArznAudioData addAudio(ArznParsingInfo parsingData) {
      return this._parent != null ? this._parent.addAudio(parsingData) : parsingData.addAudio();
   }

   protected void addInternalHyperlink(int bookmarkID) {
      if (this._parent != null) {
         this._parent.addInternalHyperlink(bookmarkID);
      }
   }

   protected void addInternalHyperlink(ArznParsingInfo parsingData, int bookmarkID, boolean isPartOfTOC) {
      if (this._parent != null) {
         this._parent.addInternalHyperlink(parsingData, bookmarkID, isPartOfTOC);
      } else {
         parsingData.addInternalHyperlink(bookmarkID, isPartOfTOC);
      }
   }

   protected void addExternalHyperlink(StringBuffer strTarget) {
      if (this._parent != null) {
         this._parent.addExternalHyperlink(strTarget);
      }
   }

   protected void addExternalHyperlink(ArznParsingInfo parsingData, StringBuffer strTarget, boolean isPartOfTOC) {
      if (this._parent != null) {
         this._parent.addExternalHyperlink(parsingData, strTarget, isPartOfTOC);
      } else {
         parsingData.addExternalHyperlink(strTarget, isPartOfTOC);
      }
   }

   protected void addBookmarkInfo(int bookmarkID) {
      if (this._parent != null) {
         this._parent.addBookmarkInfo(bookmarkID);
      }
   }

   protected void addBookmarkInfo(ArznParsingInfo parsingData, int bookmarkID) {
      if (this._parent != null) {
         this._parent.addBookmarkInfo(parsingData, bookmarkID);
      } else {
         parsingData.addBookmarkInfo(bookmarkID);
      }
   }

   protected void markLastHyperlinkElementEndOffset() {
      if (this._parent != null) {
         this._parent.markLastHyperlinkElementEndOffset();
      }
   }

   protected void markLastHyperlinkElementEndOffset(ArznParsingInfo parsingData, boolean isPartOfTOC) {
      if (this._parent != null) {
         this._parent.markLastHyperlinkElementEndOffset(parsingData, isPartOfTOC);
      } else {
         parsingData.markLastHyperlinkElementEndOffset(isPartOfTOC);
      }
   }

   protected void addTrackChange(String authorName, String dateTime) {
      if (this._parent != null) {
         this._parent.addTrackChange(authorName, dateTime);
      }
   }

   protected void addTrackChange(ArznParsingInfo parsingData, String authorName, String dateTime, boolean isPartOfTOC) {
      if (this._parent != null) {
         this._parent.addTrackChange(parsingData, authorName, dateTime, isPartOfTOC);
      } else {
         parsingData.addTrackChange(authorName, dateTime, isPartOfTOC);
      }
   }

   protected void parsedEmbeddedObjectHint(ArznParsingInfo parsingData, EmbeddedHint embHint) {
      if (this._parent != null) {
         this._parent.parsedEmbeddedObjectHint(parsingData, embHint);
      } else {
         parsingData.parsedEmbeddedObjectHint(embHint);
      }
   }

   protected void parsedEmbeddedObjectHintEx(ArznParsingInfo parsingData, EmbeddedHint embHint) {
      if (this._parent != null) {
         this._parent.parsedEmbeddedObjectHintEx(parsingData, embHint);
      }
   }

   protected void beforeCellParsing(int row, int column, boolean isCompleteCell) {
      if (this._parent != null) {
         this._parent.beforeCellParsing(row, column, isCompleteCell);
      }
   }

   protected void afterCellParsing() {
      if (this._parent != null) {
         this._parent.afterCellParsing();
      }
   }

   protected void beforeParagraphParsing(ArznParsingInfo parsingData, boolean deletedByTrackChange) {
      if (this._parent != null) {
         this._parent.beforeParagraphParsing(parsingData, deletedByTrackChange);
      } else {
         parsingData.beforeParagraphParsing(deletedByTrackChange);
      }
   }

   protected void beforeParagraphParsing(boolean deletedByTrackChange) {
      if (this._parent != null) {
         this._parent.beforeParagraphParsing(deletedByTrackChange);
      }
   }

   protected void addParagraphInfo(ArznParsingInfo parsingData, byte paraType, int listLevel, int beforeParaSpacing, int afterParaSpacing, boolean isPartOfTOC) {
      if (this._parent != null) {
         this._parent.addParagraphInfo(parsingData, paraType, listLevel, beforeParaSpacing, afterParaSpacing, isPartOfTOC);
      } else {
         parsingData.addParagraphInfo(paraType, listLevel, beforeParaSpacing, afterParaSpacing, isPartOfTOC);
      }
   }

   protected void addParagraphInfo(byte paraType, int listLevel, int beforeParaSpacing, int afterParaSpacing) {
      if (this._parent != null) {
         this._parent.addParagraphInfo(paraType, listLevel, beforeParaSpacing, afterParaSpacing);
      }
   }

   protected void afterParagraphParsing(boolean incompleteParagraph, boolean lastCommandInCell) {
      if (this._parent != null) {
         this._parent.afterParagraphParsing(incompleteParagraph, lastCommandInCell);
      }
   }

   protected void afterParagraphParsing(ArznParsingInfo parsingData, boolean incompleteParagraph, boolean isPartOfTOC, boolean isLastCommand) {
      if (this._parent != null) {
         this._parent.afterParagraphParsing(parsingData, incompleteParagraph, isPartOfTOC, isLastCommand);
      } else {
         parsingData.afterParagraphParsing(incompleteParagraph, isPartOfTOC, isLastCommand);
      }
   }

   protected void addImageContents(byte[] imageContents) {
      if (this._parent != null) {
         this._parent.addImageContents(imageContents);
      }
   }

   protected void addAudioContents(byte[] audioContents) {
      if (this._parent != null) {
         this._parent.addAudioContents(audioContents);
      }
   }

   protected void addText(ArznParsingInfo parsingData, StringBuffer strText, boolean deletedTrackChangeText) {
      if (this._parent != null) {
         this._parent.addText(parsingData, strText, deletedTrackChangeText);
      }
   }

   private void addTextToBuffer(ArznParsingInfo parsingData, StringBuffer strText, boolean deletedTrackChangeText, boolean isPartOfTOC) {
      ArznRichTextInfo formatInfo = null;
      if (!parsingData.isDefaultStyle()) {
         int textFontStyle = parsingData.getCurrentFormattingInfo(0);
         int textForeColor = parsingData.getCurrentFormattingInfo(1);
         int textBgColor = parsingData.getCurrentFormattingInfo(2);
         int textFontSize = parsingData.getCurrentFormattingInfo(3);
         formatInfo = new ArznRichTextInfo(textFontStyle, textForeColor, textBgColor, textFontSize);
      }

      parsingData.addTextContent(formatInfo, strText, deletedTrackChangeText, isPartOfTOC);
   }

   protected void setCellBgColor(int row, int column, int color) {
      if (this._parent != null) {
         this._parent.setCellBgColor(row, column, color);
      }
   }

   protected void setTableRowBgColor(int row, int color, int startColumn, int endColumn) {
      if (this._parent != null) {
         this._parent.setTableRowBgColor(row, color, startColumn, endColumn);
      }
   }

   boolean isPartOfTOC() {
      return this._parent != null ? this._parent.isPartOfTOC() : false;
   }

   void parse(ArznParsingInfo parsingData) throws ArznStopParsingException {
      if (!this._ucsParser.isContainer(this._commandCode)) {
         int parsedBytes = this.parseData(parsingData);
         this._ucsParser.jumpCursor(this._commandSize - parsedBytes);
      } else {
         parsingData.increaseParsingLevel();
         int crtCommandSize = 0;
         this._parsingIndex = 0;
         int commandHeaderSize = this._ucsParser.getCommandHeaderSize();

         while (this._parsingIndex < this._commandSize) {
            switch (parsingData.getStopFlag()) {
               case 0:
                  break;
               case 1:
                  this._ucsParser.waitForNotify();
                  break;
               case 2:
               default:
                  throw new ArznStopParsingException();
            }

            this._ucsParser.readNextCommand();
            int crtCommandCode = this._ucsParser.getCurrentCommandCode();
            crtCommandSize = this._ucsParser.getCurrentCommandSize();
            this._parsingIndex += crtCommandSize + commandHeaderSize;
            int parsedBytes = this.parseContainerData(parsingData);
            if (parsedBytes == 0) {
               ArznObject newObject = this._factory.getObject(this._ucsParser, this);
               if (newObject != null) {
                  newObject.parse(parsingData);
                  this._factory.putObject(newObject, newObject.getObjectType());
               } else {
                  this._ucsParser.jumpCursor(crtCommandSize);
               }

               parsingData.endCommandCodeParsing(this._ucsParser.getAtomicIdentifer(crtCommandCode));
            } else {
               this._ucsParser.jumpCursor(crtCommandSize - parsedBytes);
            }
         }

         parsingData.decreaseParsingLevel();
      }
   }

   protected int parseData(ArznParsingInfo parsingData) {
      return 0;
   }

   protected void colorParsed(boolean foreground, int color) {
      if (this._parent != null) {
         this._parent.colorParsed(foreground, color);
      }
   }

   protected int parseContainerData(ArznParsingInfo parsingData) {
      return 0;
   }

   protected final int getContainerParsingIndex() {
      return this._parsingIndex;
   }
}
