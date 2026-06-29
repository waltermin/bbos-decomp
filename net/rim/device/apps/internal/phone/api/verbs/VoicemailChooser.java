package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.ui.component.PopupDialog;

class VoicemailChooser extends PopupDialog implements FieldChangeListener {
   private int _selectedIndex = -1;
   private int _firstButton = -1;

   public void setDefault(int index) {
      Field selectedField = this.getField(index + this._firstButton);
      if (selectedField != null) {
         selectedField.setFocus();
      }
   }

   protected void select() {
      Field field = this.getLeafFieldWithFocus();
      if (field != null) {
         this._selectedIndex = field.getIndex() - this._firstButton;
         this.close(0);
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field instanceof ButtonField) {
         this.select();
      }
   }

   VoicemailChooser() {
      super(new VerticalFieldManager());
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (!handled) {
         if (key == '\n') {
            this.select();
            return true;
         }

         if (key == 27) {
            this.close(-1);
         }
      }

      return handled;
   }

   @Override
   public void add(Field field) {
      if (this._firstButton == -1 && field instanceof ButtonField) {
         this._firstButton = this.getFieldCount();
      }

      super.add(field);
   }

   @Override
   public int getCloseReason() {
      return this._selectedIndex == -1 ? -1 : this._selectedIndex;
   }
}
