package javax.microedition.lcdui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ObjectChoiceField;

class PopupChoice$PopupChangeListener implements FieldChangeListener {
   private final PopupChoice this$0;

   PopupChoice$PopupChangeListener(PopupChoice _1) {
      this.this$0 = _1;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field instanceof ObjectChoiceField) {
         ObjectChoiceField popup = (ObjectChoiceField)field;
         int selectedIndex = popup.getSelectedIndex();
         if (selectedIndex >= 0 && selectedIndex != this.this$0._currentlySelectedIndex) {
            if (this.this$0._popupContainer.getFieldCount() > 1) {
               this.this$0._popupContainer.deleteRange(0, 1);
            }

            if (this.this$0._popupImages[selectedIndex] != null) {
               this.this$0._popupContainer.insert(this.this$0._popupImages[selectedIndex], 0);
            }

            this.this$0._currentlySelectedIndex = selectedIndex;
         }

         if (this.this$0._changeListener != null) {
            this.this$0._changeListener.fieldChanged(field, context);
         }
      }
   }
}
