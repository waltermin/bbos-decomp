package net.rim.device.apps.internal.docview.core;

final class ArznTrackChangeContainer extends ArznObject {
   private String _author;
   private String _dateTime;

   @Override
   final void initialize(UCSParser docParser, ArznClassFactory classFactory, ArznObject parent) {
      super.initialize(docParser, classFactory, parent);
      this._author = this._dateTime = null;
   }

   @Override
   protected final int parseContainerData(ArznParsingInfo parsingData) {
      int commandCode = super._ucsParser.getCurrentCommandCode();
      int commandSize = super._ucsParser.getCurrentCommandSize();
      boolean unicode = false;
      int offset = 0;
      switch (commandCode) {
         case 42:
            unicode = true;
         case 41:
         case 46:
            if (commandCode == 46) {
               this._dateTime = super._ucsParser.readString(unicode, commandSize).toString();
            } else {
               this._author = super._ucsParser.readString(unicode, commandSize).toString();
            }

            offset += commandSize;
         default:
            return offset;
      }
   }

   @Override
   final void parse(ArznParsingInfo parsingData) {
      super.parse(parsingData);
      this.addTrackChange(parsingData, this._author, this._dateTime, this.isPartOfTOC());
   }
}
