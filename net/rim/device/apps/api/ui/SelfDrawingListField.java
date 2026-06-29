package net.rim.device.apps.api.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

public class SelfDrawingListField extends ListField implements ListFieldCallback {
   public SelfDrawingListField(int numEntries, long style) {
      super(numEntries, style);
      this.setCallback(this);
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public Object get(ListField listField, int index) {
      return "";
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }
}
