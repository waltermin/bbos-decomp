package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.qm.peer.common.FixedHeightManager;

final class NewContactRequestDialog extends PopupScreen implements FieldChangeListener {
   private PeerRequest _request;
   private ButtonField _acceptButton;
   private ButtonField _declineButton;
   private ButtonField _removeButton;
   private ButtonField _cancelButton;
   private boolean _result;

   NewContactRequestDialog(PeerRequest request) {
      super((Manager)(new Object(562949953421312L)));
      this._request = request;
      this.createDialog();
   }

   private final void createDialog() {
      LabelField label = (LabelField)(new Object(PeerResources.getString(1000)));
      RichTextField from = (RichTextField)(new Object(
         ((StringBuffer)(new Object())).append(PeerResources.getString(889)).append(" ").append(this._request.getName()).toString(), 9007199254740992L
      ));
      RichTextField rtf = (RichTextField)(new Object(this._request.getBody(), 9007199254740992L));
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object(299067162755072L));
      vfm.add(from);
      vfm.add((Field)(new Object()));
      vfm.add(rtf);
      FixedHeightManager fhm = new FixedHeightManager(vfm, 4);
      FlowFieldManager ffm = (FlowFieldManager)(new Object(12884901888L));
      this._cancelButton = (ButtonField)(new Object(CommonResources.getString(9042)));
      this._cancelButton.setChangeListener(this);
      this._removeButton = (ButtonField)(new Object(PeerResources.getString(905)));
      this._removeButton.setChangeListener(this);
      this._declineButton = (ButtonField)(new Object(PeerResources.getString(903)));
      this._declineButton.setChangeListener(this);
      this._acceptButton = (ButtonField)(new Object(PeerResources.getString(894)));
      this._acceptButton.setChangeListener(this);
      ffm.add(this._acceptButton);
      ffm.add(this._declineButton);
      ffm.add(this._removeButton);
      ffm.add(this._cancelButton);
      this.add(label);
      this.add((Field)(new Object()));
      this.add(fhm);
      this.add(ffm);
      this._acceptButton.setFocus();
   }

   final boolean doModal() {
      Ui.getUiEngine().pushModalScreen(this);
      return this._result;
   }

   private final void close(boolean result) {
      this._result = result;
      this.close();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._acceptButton) {
         this._request.accept();
         this.close(true);
      } else if (field == this._declineButton) {
         this._request.decline();
         this.close(true);
      } else if (field == this._removeButton) {
         this.close(true);
      } else {
         if (field == this._cancelButton) {
            this.close(false);
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.close(false);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }
}
