package net.rim.device.apps.internal.docview.core;

final class ArznPage extends ArznObject {
   private int _pageIndex = -1;

   @Override
   final void parse(ArznParsingInfo parsingData) {
      parsingData.beforePageParsing();
      super.parse(parsingData);
      if (!super._incompleteObject) {
         parsingData.afterPageParsing(this._pageIndex);
      }
   }

   @Override
   protected final int parseContainerData(ArznParsingInfo parsingData) {
      int offset = 0;
      switch (super._ucsParser.getCurrentCommandCode()) {
         case 17:
            this._pageIndex = super._ucsParser.readUnsignedShort();
            offset += 2;
         default:
            return offset;
      }
   }
}
