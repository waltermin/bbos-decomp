package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public class ObjectComboField$ListCallback implements ListFieldCallback {
   protected Object[] _choices;
   protected Object[] _subset;
   private final ObjectComboField this$0;

   public ObjectComboField$ListCallback(ObjectComboField _1, Object[] choices) {
      this.this$0 = _1;
      this._subset = new Object[0];
      this._choices = choices;
   }

   public void update(String prefix) {
      if (!this.this$0.doFilter()) {
         this._subset = this._choices;
      } else {
         Array.resize(this._subset, 0);
         int count = this._choices.length;

         for (int i = 0; i < count; i++) {
            String choice = this._choices[i].toString();
            if (this.this$0.matches(choice, prefix)) {
               Arrays.add(this._subset, this._choices[i]);
            }
         }
      }
   }

   public int length() {
      return this._subset.length;
   }

   @Override
   public void drawListRow(ListField listField, Graphics g, int index, int y, int width) {
      Object data = this.get(listField, index);
      if (data != null) {
         String dataString = data.toString();
         y = listField.getAdjustedY(g.getFont(), dataString, y);
         g.drawText(dataString, 0, Integer.MAX_VALUE, 0, y, 64, width);
      }
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return 0;
   }

   @Override
   public Object get(ListField listField, int index) {
      return this._subset[index];
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }
}
