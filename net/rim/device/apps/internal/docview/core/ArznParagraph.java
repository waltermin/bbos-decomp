package net.rim.device.apps.internal.docview.core;

final class ArznParagraph extends ArznObject {
   private byte _byFlags;
   private static final int DEFAULT_FONT_HEIGHT = 200;
   private static final byte TOC_FLAG = 1;
   private static final byte LIST_FLAG = 2;
   private static final byte BULLET_FLAG = 4;

   @Override
   final void initialize(UCSParser docParser, ArznClassFactory classFactory, ArznObject parent) {
      super.initialize(docParser, classFactory, parent);
      this._byFlags = 0;
   }

   @Override
   final void parse(ArznParsingInfo parsingData) {
      this.beforeParagraphParsing(parsingData, super._commandCode == 55);
      super.parse(parsingData);
      this.afterParagraphParsing(parsingData, super._incompleteObject, this.isPartOfTOC(), !super._ucsParser.isCommandDataRemaining());
   }

   @Override
   final boolean isPartOfTOC() {
      return (this._byFlags & 1) != 0;
   }

   @Override
   protected final void colorParsed(boolean foreground, int color) {
   }

   @Override
   protected final int parseContainerData(ArznParsingInfo parsingData) {
      int commandCode = super._ucsParser.getCurrentCommandCode();
      int commandSize = super._ucsParser.getCurrentCommandSize();
      int offset = 0;
      switch (commandCode) {
         case 7:
            parsingData.addSummaryDOMID(super._ucsParser.readString(false, commandSize).toString());
            offset += commandSize;
            break;
         case 20:
            this._byFlags = super._ucsParser.readByte();
            offset++;
            byte paraType = 0;
            int listLevel = 0;
            if ((this._byFlags & 2) != 0) {
               paraType = 1;
               listLevel = super._ucsParser.readUnsignedByte();
               offset++;
            } else if ((this._byFlags & 4) != 0) {
               paraType = 2;
            }

            int beforeParagraphSpacing = 0;
            int afterParagraphSpacing = 0;
            if (commandSize > offset + 8) {
               beforeParagraphSpacing = super._ucsParser.readInt() - 200;
               offset += 4;
               afterParagraphSpacing = super._ucsParser.readInt() - 200;
               offset += 4;
            }

            this.addParagraphInfo(parsingData, paraType, listLevel, beforeParagraphSpacing, afterParagraphSpacing, this.isPartOfTOC());
      }

      return offset;
   }
}
