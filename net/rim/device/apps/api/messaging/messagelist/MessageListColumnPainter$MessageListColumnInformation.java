package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.ui.Font;
import net.rim.device.apps.api.utility.columninfo.ColumnInformation;

final class MessageListColumnPainter$MessageListColumnInformation extends ColumnInformation {
   private Font _currentFont;
   private int _priorityColumnWidth;
   private final MessageListColumnPainter this$0;

   MessageListColumnPainter$MessageListColumnInformation(MessageListColumnPainter _1) {
      super(7);
      this.this$0 = _1;
   }

   private final int getPriorityColumnWidth() {
      Font font = this.this$0._graphics.getFont();
      if (this._currentFont != font) {
         this._currentFont = font;
         this._priorityColumnWidth = MessageListColumnPainter.getIconWidth(font);
      }

      return this._priorityColumnWidth;
   }

   @Override
   public final int getColumnWidth(int index) {
      return this.this$0._graphics != null && index == 0 ? this.getPriorityColumnWidth() : super.getColumnWidth(index);
   }

   @Override
   public final int getColumnOffset(int index) {
      int priorityOffset = super.getColumnOffset(0);
      if (index == 0) {
         return priorityOffset;
      }

      int offset = super.getColumnOffset(index);
      if (offset > priorityOffset) {
         offset -= super.getColumnWidth(0);
         offset += this.getColumnWidth(0);
      }

      return offset;
   }
}
