package net.rim.device.apps.internal.docview.core;

final class ArznCell extends ArznObject {
   private int _nCrtCellRow = -1;
   private int _nCrtCellColumn = -1;

   @Override
   final void initialize(UCSParser docParser, ArznClassFactory classFactory, ArznObject parent) {
      super.initialize(docParser, classFactory, parent);
      this._nCrtCellRow = this._nCrtCellColumn = -1;
   }

   @Override
   final void parse(ArznParsingInfo parsingData) {
      super.parse(parsingData);
      this.afterCellParsing();
   }

   @Override
   protected final void beforeParagraphParsing(ArznParsingInfo parsingData, boolean deletedByTrackChange) {
      this.beforeParagraphParsing(deletedByTrackChange);
   }

   @Override
   protected final void addParagraphInfo(
      ArznParsingInfo parsingData, byte paraType, int listLevel, int beforeParaSpacing, int afterParaSpacing, boolean isPartOfTOC
   ) {
      this.addParagraphInfo(paraType, listLevel, beforeParaSpacing, afterParaSpacing);
   }

   @Override
   protected final void addText(ArznParsingInfo parsingData, StringBuffer strText, boolean isPartOfTOC, boolean deletedTrackChangeText) {
      this.addText(parsingData, strText, deletedTrackChangeText);
   }

   @Override
   protected final void addInternalHyperlink(ArznParsingInfo parsingData, int bookmarkID, boolean isPartOfTOC) {
      this.addInternalHyperlink(bookmarkID);
   }

   @Override
   protected final void addExternalHyperlink(ArznParsingInfo parsingData, StringBuffer strTarget, boolean isPartOfTOC) {
      this.addExternalHyperlink(strTarget);
   }

   @Override
   protected final void addBookmarkInfo(ArznParsingInfo parsingData, int bookmarkID) {
      this.addBookmarkInfo(bookmarkID);
   }

   @Override
   protected final void markLastHyperlinkElementEndOffset(ArznParsingInfo parsingData, boolean isPartOfTOC) {
      this.markLastHyperlinkElementEndOffset();
   }

   @Override
   protected final void addTrackChange(ArznParsingInfo parsingData, String authorName, String dateTime, boolean isPartofTOC) {
      this.addTrackChange(authorName, dateTime);
   }

   @Override
   protected final void parsedEmbeddedObjectHint(ArznParsingInfo parsingData, EmbeddedHint embHint) {
      this.parsedEmbeddedObjectHintEx(parsingData, embHint);
   }

   @Override
   protected final void afterParagraphParsing(ArznParsingInfo parsingData, boolean incompleteParagraph, boolean isPartOfTOC, boolean isLastCommand) {
      this.afterParagraphParsing(incompleteParagraph, this.getContainerParsingIndex() == super._commandSize);
   }

   @Override
   protected final void colorParsed(boolean foreground, int color) {
      if (!foreground) {
         this.setCellBgColor(this._nCrtCellRow, this._nCrtCellColumn, color);
      }
   }

   @Override
   protected final int parseContainerData(ArznParsingInfo parsingData) {
      int offset = 0;
      switch (super._ucsParser.getCurrentCommandCode()) {
         case 27:
            this._nCrtCellRow = super._ucsParser.readUnsignedShort();
            offset += 2;
         case 30:
            this._nCrtCellColumn = super._ucsParser.readUnsignedShort();
            offset += 2;
            this.beforeCellParsing(this._nCrtCellRow, this._nCrtCellColumn, !super._incompleteObject);
            return offset;
         default:
            return offset;
      }
   }
}
