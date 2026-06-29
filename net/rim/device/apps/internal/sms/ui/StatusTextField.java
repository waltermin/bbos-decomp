package net.rim.device.apps.internal.sms.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.sms.message.SMSMessageModel;

public class StatusTextField extends LabelField {
   private SMSMessageModel _model;
   private int _index;
   private int _status;

   public StatusTextField(SMSMessageModel model, int index) {
      this._model = model;
      this._index = index;
      this._status = this._model.getStatus(this._index);
      this.refreshText();
   }

   @Override
   protected void paint(Graphics g) {
      int status = this._model.getStatus(this._index);
      if (status != this._status) {
         this._status = status;
         this.refreshText();
      }

      super.paint(g);
   }

   private void refreshText() {
      String statusString = this._model.getStatusString(this._index);
      if (statusString != null) {
         this.setText(" - " + statusString);
      } else {
         this.setText(null);
      }
   }
}
