package net.rim.device.apps.internal.vad;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.util.MathUtilities;

final class VADApplication$ChoiceListField extends ObjectListField {
   private int _index;
   private boolean _override;
   private final VADApplication this$0;
   private static final int ATTRIBUTES_MASK = 71;

   VADApplication$ChoiceListField(VADApplication _1, Object[] choices) {
      this.this$0 = _1;
      this.set(choices);
      this.setSelectedIndex(this._index);
   }

   final void incrementSelectedIndex() {
      this.setSelectedIndex(++this._index);
   }

   final void overrideSelectedIndex(int index) {
      this._override = true;
      this.setSelectedIndex(index);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      String text = this.get(listField, index).toString();
      int style = (int)(this.getStyle() & 71);
      int fudge = listField.adjustRowHeight(graphics.getFont(), index, text);
      StringBuffer sb = (StringBuffer)(new Object());
      sb.append(index + 1);
      sb.append('.');
      graphics.drawText(sb, 0, Integer.MAX_VALUE, 0, y + fudge, style, width);
      int offset = graphics.getFont().getAdvance("2. ");
      graphics.drawText(text, 0, Integer.MAX_VALUE, offset, y + fudge, style, width);
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      amount = MathUtilities.clamp(-1, amount, 1);
      int ret = super.moveFocus(amount, status, time);
      if (!this._override && amount != 0) {
         int newIndex = this.getSelectedIndex();
         if (newIndex != this._index) {
            int diff = newIndex - this._index;
            int event = 13;
            if (diff < 0) {
               event = 12;
               diff = -diff;
            }

            for (int i = 0; i < diff; i++) {
               this.this$0._manager.sendEvent(event);
            }

            this._index = newIndex;
         }
      }

      return ret;
   }
}
