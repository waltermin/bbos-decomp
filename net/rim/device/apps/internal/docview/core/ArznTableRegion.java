package net.rim.device.apps.internal.docview.core;

final class ArznTableRegion extends ArznObject {
   private int _firstColumn = -1;
   private int _lastColumn = -1;

   @Override
   final void initialize(UCSParser docParser, ArznClassFactory classFactory, ArznObject parent) {
      super.initialize(docParser, classFactory, parent);
      this._firstColumn = this._lastColumn = -1;
   }

   @Override
   protected final void colorParsed(boolean foreground, int color) {
      if (!foreground) {
         this.setTableRowBgColor(-1, color, this._firstColumn, this._lastColumn);
      }
   }

   @Override
   protected final int parseContainerData(ArznParsingInfo parsingData) {
      int offset = 0;
      switch (super._ucsParser.getCurrentCommandCode()) {
         case 81:
            this._firstColumn = super._ucsParser.readShort();
            offset += 2;
            this._lastColumn = super._ucsParser.readShort();
            offset += 2;
         default:
            return offset;
      }
   }
}
