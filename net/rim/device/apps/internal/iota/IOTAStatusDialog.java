package net.rim.device.apps.internal.iota;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.ui.component.PopupDialog;

final class IOTAStatusDialog extends PopupDialog implements FieldChangeListener {
   private LabelField _statusField;
   private ButtonField _cancelButton;
   private ProvisioningServiceAgent _psa;

   public final void setStatus(String msg) {
      synchronized (Application.getEventLock()) {
         this._statusField.setText(msg);
      }
   }

   @Override
   public final void fieldChanged(Field f, int mode) {
      if (f == this._cancelButton) {
         this._psa.cancelIOTASession();
      }
   }

   @Override
   public final void close() {
      this.close(-1);
   }

   public IOTAStatusDialog(String title, ProvisioningServiceAgent psa) {
      super(new VerticalFieldManager(12884901888L), 1152921569064910848L);
      this._psa = psa;
      this.add(new LabelField(title));
      this.add(new SeparatorField(32));
      VerticalFieldManager vfm = new VerticalFieldManager(12884901888L);
      this._statusField = new LabelField();
      ResourceBundle rb = ResourceBundle.getBundle(1200788073156220994L, "net.rim.device.apps.internal.resource.IOTA");
      this._cancelButton = new ButtonField(rb.getString(7));
      this._cancelButton.setChangeListener(this);
      this.setCancelAllowed(true);
      this.add(this._statusField);
      vfm.add(this._cancelButton);
      this.add(vfm);
   }
}
