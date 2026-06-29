package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.VariableHeightListField;

class MessageListVariableHeightListField extends VariableHeightListField {
   private MessageListUI _messageListUI;

   MessageListVariableHeightListField(MessageListUI ui, int numRows, long style) {
      super(numRows, style);
      this._messageListUI = ui;
      ui = this._messageListUI;
   }

   @Override
   protected void scrollRegionVertically(Graphics graphics, int y, int height, int dy, int oldTopRow) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected void setTopRow(int topRow, int cursor) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected boolean isOnTopRow(int index) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }
}
