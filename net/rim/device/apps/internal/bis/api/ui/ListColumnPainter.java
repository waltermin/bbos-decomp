package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.utility.columninfo.ColumnInformation;
import net.rim.device.internal.ui.RichText;

public final class ListColumnPainter {
   private ColumnInformation _columnInformation;
   private Graphics _graphics;
   private int _fontHeight;
   private int _linesPerEntry;
   private int _currentLineAttributes = 0;
   private int _numberOfColumns;
   private static final Tag TAG_HEADER = Tag.create("header");
   private static Tag TAG_LINE1 = Tag.create("messagelist-line2");
   private static Tag TAG_LINE1_LEVEL1 = Tag.create("messagelist-line1-level1");

   public ListColumnPainter(int numberOfColumns) {
      this._columnInformation = (ColumnInformation)(new Object(numberOfColumns));
      this._numberOfColumns = numberOfColumns;
      this.updateColumnInformation();
   }

   public final void updateColumnInformation() {
      this.updateColumnInformation(Font.getDefault());
   }

   public final void updateColumnInformation(Font font) {
      if (this._graphics == null) {
         UiApplication uiApp = UiApplication.getUiApplication();
         if (uiApp != null) {
            Screen activeScreen = uiApp.getActiveScreen();
            if (activeScreen != null) {
               this._graphics = activeScreen.getGraphics();
            }
         }
      }

      for (int i = 0; i < this._numberOfColumns; i++) {
         this._columnInformation.setColumnWidth(i, Display.getWidth() / this._numberOfColumns);
      }

      this._linesPerEntry = 1;
      this._fontHeight = font.getHeight();
   }

   public final void drawText(int columnId, String text, int height, boolean annex) {
      this._columnInformation.setColumnFilled(columnId);
      int columnWidth = this._columnInformation.getColumnWidth(columnId);
      if (columnWidth != 0) {
         if (text != null && text.length() > 100) {
            text = text.substring(0, 100);
         }

         int offset = this._columnInformation.getColumnOffset(columnId);
         int screenWidth = Display.getWidth() - 10;
         int y = height;
         Font originalFont = this._graphics.getFont();
         Font fontToUse = originalFont;
         int paragraphOrdering = RichText.getDefaultParagDirection();
         int drawFlags = 6;
         int rightmostExtent = offset + columnWidth;
         if (rightmostExtent > screenWidth) {
            columnWidth = screenWidth - offset;
            rightmostExtent = screenWidth;
         }

         if (!Graphics.SCREEN_HAS_BORDER && rightmostExtent == Display.getWidth()) {
            columnWidth--;
         }

         this._graphics.setFont(fontToUse);
         RichText.drawTextWithEllipses(this._graphics, text, offset, y, columnWidth, paragraphOrdering, drawFlags);
         this._graphics.setFont(originalFont);
      }
   }
}
