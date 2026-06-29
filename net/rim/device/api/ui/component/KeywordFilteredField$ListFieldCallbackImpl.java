package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Graphics;

class KeywordFilteredField$ListFieldCallbackImpl implements ListFieldCallback {
   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      CollectionListField collectionListField = (CollectionListField)listField;
      Object element = collectionListField.getElementAt(index);
      if (element != null) {
         graphics.drawText(element.toString(), 0, y);
      }
   }

   @Override
   public Object get(ListField listField, int index) {
      return null;
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return 0;
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }
}
