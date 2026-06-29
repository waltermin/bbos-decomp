package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

final class ConversationScreen$BottomUpManager extends Manager {
   private int[] _buffer;
   private final ConversationScreen this$0;

   ConversationScreen$BottomUpManager(ConversationScreen _1) {
      super(0);
      this.this$0 = _1;
      this._buffer = new int[1];
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (ConversationScreen.access$400(this.this$0) != null) {
         this.setExtent(width, height);
         int count = this.getFieldCount();
         this._buffer[0] = height;
         Field list = this.layoutFields(width, this._buffer, count);
         height = this._buffer[0];
         if (list != null) {
            this.layoutChild(list, width, height);
         }

         this.setFieldsPositions(height, list, count);
         if (list != null) {
            synchronized (ConversationScreen.access$400(this.this$0).getAppEventLock()) {
               this.scrolHistoryManager((MessageList)list);
               return;
            }
         }
      }
   }

   private final Field layoutFields(int width, int[] height, int count) {
      Field list = null;

      for (int i = 0; i < count; i++) {
         Field field = this.getField(i);
         if (field instanceof MessageList) {
            list = field;
         } else {
            this.layoutChild(field, width, height[0]);
            height[0] -= field.getHeight();
         }
      }

      return list;
   }

   private final void setFieldsPositions(int height, Field list, int count) {
      int y = 0;

      for (int i = 0; i < count; i++) {
         Field field = this.getField(i);
         this.setPositionChild(field, 0, y);
         y += field == list ? height : field.getHeight();
      }
   }

   private final void scrolHistoryManager(MessageList manager) {
      if (manager != null) {
         manager.scroll(142);
      }
   }
}
