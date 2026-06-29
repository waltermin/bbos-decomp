package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

public class UrlEntryField$UrlEntryCallback implements ListFieldCallback, FocusChangeListener {
   private final UrlEntryField this$0;

   public UrlEntryField$UrlEntryCallback(UrlEntryField _1) {
      this.this$0 = _1;
   }

   @Override
   public void drawListRow(ListField listField, Graphics g, int index, int y, int width) {
      String url = (String)this.get(listField, index);
      g.drawText(url, 0, y, 70, width);
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return 0;
   }

   @Override
   public Object get(ListField listField, int index) {
      if (index == 0) {
         return this.this$0._firstEntryText;
      }

      index--;

      for (int i = 0; i < this.this$0._indices.length; i += 2) {
         if (index + this.this$0._indices[i] < this.this$0._indices[i + 1]) {
            return this.this$0._urlStore.getElementAt(this.this$0._indices[i] + index);
         }

         index -= this.this$0._indices[i + 1] - this.this$0._indices[i];
      }

      return null;
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public void focusChanged(Field field, int eventType) {
      if (eventType == 2) {
         ListField list = this.this$0.getList();
         this.this$0.setText((String)this.get(list, list.getSelectedIndex()));
      }
   }
}
