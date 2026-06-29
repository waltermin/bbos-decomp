package net.rim.device.apps.internal.applicationcontrol;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.internal.ui.component.PopupDialog;

final class ApplicationPermissionsProxy$PermissionsRequestDialog extends PopupDialog implements FieldChangeListener {
   String _requestor;
   ButtonField _viewButton;

   ApplicationPermissionsProxy$PermissionsRequestDialog(String requestor) {
      super((Manager)(new Object()), 134217728);
      this._requestor = requestor;
      this.populateFields();
   }

   private final void populateFields() {
      ResourceBundle rb = ResourceBundle.getBundle(-1488627819050031640L, "net.rim.device.apps.internal.resource.Security");
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setIcon((BitmapField)(new Object(Bitmap.getPredefinedBitmap(2), 32)));
      String message = MessageFormat.format(rb.getString(749), new Object[]{this._requestor});
      RichTextField messageField = (RichTextField)(new Object(message, 45035996273704960L));
      dfm.addCustomField(messageField);
      this._viewButton = (ButtonField)(new Object(rb.getString(750), 12884901888L));
      this._viewButton.setChangeListener(this);
      dfm.addCustomField(this._viewButton);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._viewButton) {
         this.close(0);
      }
   }
}
