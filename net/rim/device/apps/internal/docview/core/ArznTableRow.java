package net.rim.device.apps.internal.docview.core;

final class ArznTableRow extends ArznObject {
   private int _crtRow = -1;

   @Override
   final void initialize(UCSParser docParser, ArznClassFactory classFactory, ArznObject parent) {
      super.initialize(docParser, classFactory, parent);
      this._crtRow = -1;
   }

   @Override
   protected final void beforeCellParsing(int row, int column, boolean isCompleteCell) {
      if (this._crtRow != -1) {
         super.beforeCellParsing(this._crtRow, column, isCompleteCell);
      } else {
         super.beforeCellParsing(row, column, isCompleteCell);
      }
   }

   @Override
   protected final void colorParsed(boolean foreground, int color) {
      if (!foreground) {
         this.setTableRowBgColor(this._crtRow, color, -1, -1);
      }
   }

   @Override
   protected final void setTableRowBgColor(int row, int color, int startColumn, int endColumn) {
      super.setTableRowBgColor(this._crtRow, color, startColumn, endColumn);
   }

   @Override
   protected final void setCellBgColor(int row, int column, int color) {
      if (this._crtRow != -1) {
         super.setCellBgColor(this._crtRow, column, color);
      } else {
         super.setCellBgColor(row, column, color);
      }
   }

   @Override
   protected final int parseContainerData(ArznParsingInfo parsingData) {
      int offset = 0;
      switch (super._ucsParser.getCurrentCommandCode()) {
         case 29:
            this._crtRow = super._ucsParser.readUnsignedShort();
            offset += 2;
         default:
            return offset;
      }
   }
}
