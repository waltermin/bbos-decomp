package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.PopupDialog;

public class PhoneNumberConverter$GetAreaCodeDialog extends PopupDialog {
   private EditField _areaCodeField;
   private int _closeReason = -1;

   public PhoneNumberConverter$GetAreaCodeDialog(String areaCode) {
      super((Manager)(new Object(1153202979583557632L)));
      this.setModal(true);
      this._areaCodeField = (EditField)(new Object(((StringBuffer)(new Object())).append(PhoneResources.getString(6315)).append(" ").toString(), "", 3, 0));
      this._areaCodeField.setText(areaCode);
      this._areaCodeField.setFilter((TextFilter)(new Object()));
      this.add((Field)(new Object(PhoneResources.getString(6316), 36028797018963968L)));
      this.add((Field)(new Object(4)));
      this.add(this._areaCodeField);
      this.add((Field)(new Object(4)));
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      return this.invokeAction(1);
   }

   @Override
   protected boolean invokeAction(int action) {
      if (action == 1) {
         this._closeReason = 0;
         Ui.getUiEngine().popScreen(this);
         return true;
      } else {
         return false;
      }
   }

   public int doModal() {
      Ui.getUiEngine().pushGlobalScreen(this, Ui.getUiEngine().getTopmostGlobalPriority() - 1, 1);
      return this._closeReason;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this._closeReason = -1;
         Ui.getUiEngine().popScreen(this);
      } else if (key == '\n') {
         int txtLen = this._areaCodeField.getTextLength();
         if (txtLen == 0 || txtLen == 3) {
            this._closeReason = 0;
            Ui.getUiEngine().popScreen(this);
         }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   public int getCloseReason() {
      return this._closeReason;
   }

   public String getAreaCode() {
      this._areaCodeField.setCaretPosition(this._areaCodeField.getLabelLength() + this._areaCodeField.getText().length());
      this.doModal();
      if (this.getCloseReason() == 0) {
         String areaCode = this._areaCodeField.getText();
         if (areaCode != null) {
            return areaCode.trim();
         }
      }

      return null;
   }
}
