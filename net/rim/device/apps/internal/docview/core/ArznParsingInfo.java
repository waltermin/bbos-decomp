package net.rim.device.apps.internal.docview.core;

final class ArznParsingInfo {
   private ArznParseCallback _parseItf;
   private FormatObject _formatObj = new FormatObject();
   private int _parsingLevel;

   ArznParsingInfo(ArznParseCallback parseItf) {
      this._parseItf = parseItf;
   }

   final void reset() {
      this._formatObj.clear();
      this._parsingLevel = 0;
   }

   final void increaseParsingLevel() {
      this._parsingLevel++;
   }

   final void decreaseParsingLevel() {
      this._formatObj.discardCurrentInfo(this._parsingLevel);
      this._parsingLevel--;
   }

   final int getCurrentFormattingInfo(int tag) {
      return this._formatObj.getCurrentInfo(this._parsingLevel, tag);
   }

   final void setCurrentFormattingInfo(int tag, int value) {
      this._formatObj.setCurrentInfo(this._parsingLevel, tag, value);
   }

   final boolean isDefaultStyle() {
      return this._formatObj.isDefaultInfo(this._parsingLevel);
   }

   final void addTextContent(ArznRichTextInfo formatInfo, StringBuffer strText, boolean deletedByTrackChange, boolean isPartOfTOC) {
      if (this._parseItf != null) {
         this._parseItf.addTextContent(formatInfo, strText, deletedByTrackChange, isPartOfTOC);
      }
   }

   final void addInternalHyperlink(int bookmarkID, boolean isPartOfTOC) {
      if (this._parseItf != null) {
         this._parseItf.addInternalHyperlink(bookmarkID, isPartOfTOC);
      }
   }

   final void addExternalHyperlink(StringBuffer strTarget, boolean isPartOfTOC) {
      if (this._parseItf != null) {
         this._parseItf.addExternalHyperlink(strTarget, isPartOfTOC);
      }
   }

   final void addTrackChange(String authorName, String dateTime, boolean isPartOfTOC) {
      if (this._parseItf != null) {
         this._parseItf.addTrackChange(authorName, dateTime, isPartOfTOC);
      }
   }

   final void addBookmarkInfo(int bookmarkID) {
      if (this._parseItf != null) {
         this._parseItf.addBookmarkInfo(bookmarkID);
      }
   }

   final void addDocumentHeader(ArznDocumentHeader docHeader) {
      if (this._parseItf != null) {
         this._parseItf.addDocumentHeader(docHeader);
      }
   }

   final void setPageCount(int pageCount) {
      if (this._parseItf != null) {
         this._parseItf.setPageCount(pageCount);
      }
   }

   final byte getStopFlag() {
      return this._parseItf != null ? this._parseItf.getStopFlag() : 0;
   }

   final void addChunkHint(int chunkHint) {
      if (this._parseItf != null) {
         this._parseItf.addChunkHint(chunkHint);
      }
   }

   final void addDOMIDHint(String domIDHint) {
      if (this._parseItf != null) {
         this._parseItf.addDOMIDHint(domIDHint);
      }
   }

   final void addSummaryDOMID(String strDOMID) {
      if (this._parseItf != null) {
         this._parseItf.addSummaryDOMID(strDOMID);
      }
   }

   final void addDocInfoValue(int docInfoID, String value) {
      if (this._parseItf != null) {
         this._parseItf.addDocInfoValue(docInfoID, value);
      }
   }

   final void afterPageParsing(int pageIndex) {
      if (this._parseItf != null) {
         this._parseItf.afterPageParsing(pageIndex);
      }
   }

   final void beforePageParsing() {
      if (this._parseItf != null) {
         this._parseItf.beforePageParsing();
      }
   }

   final void markLastHyperlinkElementEndOffset(boolean isPartOfTOC) {
      if (this._parseItf != null) {
         this._parseItf.markLastHyperlinkElementEndOffset(isPartOfTOC);
      }
   }

   final void setLastHyperlinkElementChunkID(int chunkHint, boolean isPartOfTOC) {
      if (this._parseItf != null) {
         this._parseItf.setLastHyperlinkElementChunkID(chunkHint, isPartOfTOC);
      }
   }

   final ArznSheetData addSpreadsheet(int nRows, int nCols, String strSheetName, boolean isComplete) {
      return this._parseItf != null ? this._parseItf.addSpreadsheet(nRows, nCols, strSheetName, isComplete) : null;
   }

   final ArznImageData addImage() {
      return this._parseItf != null ? this._parseItf.addImage() : null;
   }

   final ArznAudioData addAudio() {
      return this._parseItf != null ? this._parseItf.addAudio() : null;
   }

   final EmbeddedHint addEmbeddedObjectHint(byte type) {
      return this._parseItf != null ? this._parseItf.addEmbeddedObjectHint(type) : null;
   }

   final void parsedEmbeddedObjectHint(EmbeddedHint hint) {
      if (this._parseItf != null) {
         this._parseItf.parsedEmbeddedObjectHint(hint);
      }
   }

   final void parsedEmbeddedObjectHint(ArznSheetData sheetData, EmbeddedHint hint) {
      if (this._parseItf != null) {
         this._parseItf.parsedEmbeddedObjectHint(sheetData, hint);
      }
   }

   final void beforeParagraphParsing(boolean deletedByTrackChange) {
      if (this._parseItf != null) {
         this._parseItf.beforeParagraphParsing(deletedByTrackChange);
      }
   }

   final void addParagraphInfo(byte paraType, int listLevel, int beforeParaSpacing, int afterParaSpacing, boolean isPartOfTOC) {
      if (this._parseItf != null) {
         this._parseItf.addParagraphInfo(paraType, listLevel, beforeParaSpacing, afterParaSpacing, isPartOfTOC);
      }
   }

   final void afterParagraphParsing(boolean incompleteParagraph, boolean isPartOfTOC, boolean isLastCommand) {
      if (this._parseItf != null) {
         this._parseItf.afterParagraphParsing(incompleteParagraph, isPartOfTOC, isLastCommand);
      }
   }

   final void addFontSizes(short[] fontSizes) {
      if (this._parseItf != null) {
         this._parseItf.addFontSizes(fontSizes);
      }
   }

   final void endCommandCodeParsing(byte atomicIdentifier) {
      if (this._parseItf != null) {
         this._parseItf.endCommandCodeParsing(atomicIdentifier);
      }
   }
}
