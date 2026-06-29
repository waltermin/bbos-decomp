package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;

public final class ChangeDeviceScreen extends UserSettingsScreen {
   private BasicEditField _pinEdit;
   private BasicEditField _imeiEdit;
   private static final String PARAM_PIN = "pin";
   private static final String PARAM_IMEI = "imei";

   public ChangeDeviceScreen() {
      super(29);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(4));
      this.addContentField(new BoldLabelField(ApplicationResources.getString(26)));
      String currentPIN = ClientSessionState.getInstance().getUserInfo().getPIN();
      this._pinEdit = (BasicEditField)(new Object(null, currentPIN));
      this.addContentField(this._pinEdit, true);
      String hardwareIDType = null;
      if (RadioInfo.getNetworkType() == 4) {
         hardwareIDType = ApplicationResources.getString(202);
      } else {
         hardwareIDType = ApplicationResources.getString(201);
      }

      String imeiEsnLabel = MessageFormat.format(ApplicationResources.getString(27), new Object[]{hardwareIDType});
      this.addContentField(new BoldLabelField(imeiEsnLabel));
      this._imeiEdit = (BasicEditField)(new Object(null, null));
      this.addContentField(this._imeiEdit, true);
      Button cancelButton = new Button(ApplicationResources.getString(28));
      Button saveButton = new Button(ApplicationResources.getString(29));
      this.addButtonBarButtons(new Button[]{cancelButton, saveButton}, false, 1);
      this.attachEventToField(cancelButton, new BackEvent(28));
      CommandEvent saveEvent = new CommandEvent(29, 17, new String[]{"pin", "imei"});
      this.attachEventToField(saveButton, saveEvent);
      this.setDefaultEvent(saveEvent);
      this.setHelp("225639.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      String pin = this._pinEdit.getText();
      String imei = this._imeiEdit.getText();
      if (!InputValidationUtils.isValidPIN(pin)) {
         this.setError(ApplicationResources.getString(199));
         return false;
      }

      if (imei != null && imei.length() != 0) {
         inputMap.put("pin", pin);
         inputMap.put("imei", imei);
         return true;
      }

      String hardwareIDType = null;
      if (RadioInfo.getNetworkType() == 4) {
         hardwareIDType = ApplicationResources.getString(202);
      } else {
         hardwareIDType = ApplicationResources.getString(201);
      }

      String imeiEsnMissingMessage = MessageFormat.format(ApplicationResources.getString(200), new Object[]{hardwareIDType});
      this.setError(imeiEsnMissingMessage);
      return false;
   }
}
