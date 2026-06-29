package net.rim.device.apps.internal.docview.core;

final class ArznRefContainer extends ArznObject {
   private EmbeddedHint _embHint;
   private byte _type = -1;

   final void initialize(UCSParser docParser, ArznClassFactory classFactory, ArznObject parent, int refContainerType) {
      this.initialize(docParser, classFactory, parent);
      this._embHint = null;
      this._type = -1;
      switch (refContainerType) {
         case 31:
         case 52:
            this._type = 1;
            return;
         case 38:
            this._type = 2;
         default:
            return;
         case 69:
            this._type = 4;
            return;
         case 70:
            this._type = 3;
            return;
         case 71:
            this._type = 0;
      }
   }

   @Override
   protected final int parseContainerData(ArznParsingInfo parsingData) {
      int commandCode = super._ucsParser.getCurrentCommandCode();
      int commandSize = super._ucsParser.getCurrentCommandSize();
      int offset = 0;
      switch (commandCode) {
         case 6:
            if (this._embHint != null) {
               this._embHint._index = super._ucsParser.readShort();
               offset += 2;
            }
            break;
         case 8:
            super._ucsParser.readString(false, commandSize);
            offset += commandSize;
            break;
         case 49:
         case 50:
            if (this._embHint != null) {
               this._embHint._owner = super._ucsParser.readString(commandCode == 50, commandSize).toString();
               offset += commandSize;
            }
            break;
         case 88:
            if (this._embHint != null) {
               this._embHint._flipImage = super._ucsParser.readUnsignedInt();
               offset += 4;
            }
      }

      return offset;
   }

   @Override
   protected final ArznImageData addImage(ArznParsingInfo parsingData) {
      ArznImageData imageData = null;
      if (this._embHint != null) {
         if (this._embHint._previewData != null && this._embHint._previewData instanceof ArznImageData) {
            return (ArznImageData)this._embHint._previewData;
         }

         imageData = parsingData.addImage();
         this._embHint._previewData = imageData;
      }

      return imageData;
   }

   @Override
   protected final ArznSheetData addSpreadsheet(ArznParsingInfo parsingData, int rows, int columns, String name, boolean isComplete) {
      ArznSheetData sheetData = null;
      if (this._embHint != null) {
         if (this._embHint._previewData != null && this._embHint._previewData instanceof ArznSheetData) {
            return (ArznSheetData)this._embHint._previewData;
         }

         sheetData = parsingData.addSpreadsheet(rows, columns, name, isComplete);
         this._embHint._previewData = sheetData;
      }

      return sheetData;
   }

   @Override
   final void parse(ArznParsingInfo parsingData) {
      this._embHint = parsingData.addEmbeddedObjectHint(this._type);
      super.parse(parsingData);
      if (this._embHint != null) {
         if (!this._embHint._complete && !super._incompleteObject) {
            this._embHint._complete = true;
         }

         this.parsedEmbeddedObjectHint(parsingData, this._embHint);
         this._embHint = null;
      }
   }

   @Override
   protected final void addText(ArznParsingInfo parsingData, StringBuffer strText, boolean isPartOfTOC, boolean deletedTrackChangeText) {
      if (this._embHint != null) {
         this._embHint._name = strText.toString();
      }
   }
}
