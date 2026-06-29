package net.rim.tid.im.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

final class ChooseVariantManager$LookupListField extends ListField implements ListFieldCallback {
   private final ChooseVariantManager this$0;

   ChooseVariantManager$LookupListField(ChooseVariantManager _1) {
      this.this$0 = _1;
   }

   public final void reinit() {
      if (this.this$0._lineCount != 0) {
         super.setSize(this.this$0._listData.size());
      }
   }

   @Override
   public final void setSize(int size) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public final void sort() {
      this.invalidate();
   }

   @Override
   public final boolean isFocusable() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      return super.moveFocus(amount, status & -2, time);
   }

   @Override
   protected final boolean invokeAction(int action) {
      return false;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return this.this$0._preferredWidth;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics g, int index, int y, int width) {
      String txt = this.this$0._listData.elementAt(index).toString();
      int showEllipsis = this.this$0._itemsTruncated ? 64 : 0;
      int adj = listField.adjustRowHeight(listField.getFont(), index, txt);
      g.drawText(txt, 0, y + adj, 6 | showEllipsis, width);
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this.this$0._listData.elementAt(index).toString();
   }
}
