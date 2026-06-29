package net.rim.device.apps.internal.sms.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.messaging.MessageIcons;
import net.rim.device.apps.internal.sms.SMSModel;
import net.rim.device.internal.ui.component.ImageField;

public class StatusIconField extends ImageField {
   private SMSModel _model;
   private int _index;
   private int _statusIconID;

   public StatusIconField(SMSModel model) {
      this(model, -1);
   }

   public StatusIconField(SMSModel model, int index) {
      super(65536);
      this._model = model;
      this._index = index;
      if (this._index == -1) {
         this._statusIconID = this._model.getOverallStatusIcon();
      } else {
         this._statusIconID = this._model.getRecipientStatusIcon(this._index);
      }

      this.setImage(MessageIcons.getIcons().getImage(this._statusIconID));
   }

   @Override
   protected void paint(Graphics g) {
      int statusIconID;
      if (this._index == -1) {
         statusIconID = this._model.getOverallStatusIcon();
      } else {
         statusIconID = this._model.getRecipientStatusIcon(this._index);
      }

      if (statusIconID != this._statusIconID) {
         this._statusIconID = statusIconID;
         this.setImage(MessageIcons.getIcons().getImage(this._statusIconID));
      }

      super.paint(g);
   }
}
