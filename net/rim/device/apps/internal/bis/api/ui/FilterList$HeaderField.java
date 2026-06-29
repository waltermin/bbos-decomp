package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.theme.ThemeManager;

final class FilterList$HeaderField extends RichTextField {
   private String _column1;
   private String _column2;
   private final FilterList this$0;

   public FilterList$HeaderField(FilterList _1, String text) {
      super(text, 1188950301625810944L);
      this.this$0 = _1;
   }

   public FilterList$HeaderField(FilterList _1, String column1, String column2) {
      super(column1 + "    " + column2, 1188950301625810944L);
      this.this$0 = _1;
      this._column1 = column1;
      this._column2 = column2;
   }

   @Override
   public final void paint(Graphics graphics) {
      int width = this.getScreen().getWidth();
      int themeGeneration = ThemeManager.getGeneration();
      if (themeGeneration != this.this$0._themeGeneration) {
         this.this$0._themeGeneration = themeGeneration;
         this.this$0._themeAttributesHeader = ThemeManager.getActiveTheme().getAttributeSet(FilterList.TAG_HEADER);
      }

      this.setThemeAttributesSpecialClear(true);
      graphics.pushRegion(0, 0, width, this.getHeight(), 0, 0);
      this.setThemeAttributesSpecial(this.this$0._themeAttributesHeader, graphics);
      graphics.setFont(this.getFont());
      String text = this.getText();
      int columnWidth = this.getScreen().getWidth() - 5;
      if (this._column1 != null && this._column2 != null) {
         graphics.drawText(this._column1, 0, this._column1.length(), 0, 0, 64, columnWidth);
         graphics.drawText(this._column2, 0, this._column2.length(), width / 2, 0, 64, columnWidth);
      } else {
         graphics.drawText(text, 0, text.length(), 0, 0, 64, width);
      }

      this.setThemeAttributesSpecial(null, null);
      graphics.popContext();
   }
}
