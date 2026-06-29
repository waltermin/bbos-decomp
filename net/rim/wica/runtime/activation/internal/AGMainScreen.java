package net.rim.wica.runtime.activation.internal;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.management.RuntimeInfo;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.util.LongVector;

final class AGMainScreen extends MainScreen {
   private ActivationServiceImpl _activationService;
   private UiApplication _app;
   private RuntimeInfo _runtimeInfo;
   private LabelField _statusLabel;
   private BasicEditField _urlEditField;
   private Manager _statusManager;
   private ServiceRecord[] _records;
   private ObjectChoiceField _transportChoiceField;
   private static final String DEFAULT_URL_SCHEME = "http://";
   private static final String DEFAULT_URL_SCHEME_SECURE = "https://";
   private static final String DEFAULT_URL_PORT = "7080";
   private static final String DEFAULT_URL_PATH = "/mds";

   AGMainScreen(ActivationServiceImpl activationService) {
      super(196608);
      this.setTitle(RuntimeResources.getString(34));
      this._activationService = activationService;
      this._runtimeInfo = this._activationService.getRuntimeInfo();
      this._app = UiApplication.getUiApplication();
      this.updateUI();
      this._activationService.registerUI(this);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      int state = this._activationService.getState();
      if (state == 1 || state == 4 || !this._activationService.disallowUserInitiatedActivation() && state == 0) {
         menu.add(new LongVector(this));
      }
   }

   @Override
   public final boolean onClose() {
      this._activationService.unregisterUI(this);
      this.close();
      return true;
   }

   final void activationStatusEvent() {
      this._app.invokeLater(new AGMainScreen$1(this));
   }

   final void activationProgressEvent(String statusText, int progress) {
      this._app.invokeLater(new AGMainScreen$2(this, statusText, progress));
   }

   final void activationStateEvent() {
      this._app.invokeLater(new AGMainScreen$3(this));
   }

   final void activationProgressIncrementEvent() {
      this._app.invokeLater(new AGMainScreen$4(this));
   }

   final void exit() {
      this._app.invokeLater(new AGMainScreen$5(this));
   }

   private final void updateUI() {
      int state = this._activationService.getState();
      this.deleteAll();
      this.addStatusFields();
      this.updateStatusFields();
      if (state != 8) {
         if (state != 0) {
            this.addCurrentServerFields();
         }

         if (!this._activationService.disallowUserInitiatedActivation() && state != 2) {
            this.addEditingFields();
         }
      }

      if (state == 2) {
         this.addProgress();
         this.updateProgress(this._activationService.getActivationProgressString(), this._activationService.getActivationProgressStage());
      }
   }

   private final void addStatusFields() {
      this._statusLabel = (LabelField)(new Object("", 9007207844675584L));
      this.add((Field)(new Object((Field)(new Object(RuntimeResources.getString(43))), this._statusLabel)));
   }

   private final void updateStatusFields() {
      String status = "";
      switch (this._activationService.getState()) {
         case -1:
         case 3:
            status = RuntimeResources.getString(131);
            break;
         case 0:
            status = RuntimeResources.getString(50);
            break;
         case 1:
         default:
            status = RuntimeResources.getString(35);
            break;
         case 2:
            status = RuntimeResources.getString(36);
            break;
         case 4:
            status = RuntimeResources.getString(38);
      }

      this._statusLabel.setText(status);
   }

   private final void addCurrentServerFields() {
      AGInfo serverInfo = this.getActiveServerInfo();
      this.add((Field)(new Object()));
      this.add((Field)(new Object(RuntimeResources.getString(129))));
      String transportName = "";
      ServiceRecord transportRecord = serverInfo == null ? null : this._activationService.getTransportRecord(serverInfo.getIPPP_UID());
      if (transportRecord != null) {
         transportName = transportRecord.getName();
      }

      this.add((Field)(new Object((Field)(new Object(RuntimeResources.getString(45))), (Field)(new Object(transportName, 9007207844675584L)))));
      this.add((Field)(new Object(RuntimeResources.getString(149))));
      this.add((Field)(new Object(serverInfo == null ? "" : serverInfo.getAgRegURL())));
   }

   private final void addEditingFields() {
      this.add((Field)(new Object()));
      this._transportChoiceField = this.createTransportChoiceField();
      this.add(this._transportChoiceField);
      String url = null;
      AGInfo serverInfo = this.getActiveServerInfo();
      if (serverInfo != null) {
         url = serverInfo.getAgRegURL();
      }

      this.add((Field)(new Object(RuntimeResources.getString(37))));
      this._urlEditField = (BasicEditField)(new Object("", url, 400, 11928600576L));
      this.add(this._urlEditField);
      this._urlEditField.setFocus();
   }

   private final ObjectChoiceField createTransportChoiceField() {
      this._records = this._activationService.getTransportRecords();
      ServiceRecord defaultTransport = this.getDefaultTransport();
      String[] names = new Object[this._records.length];
      int defaultSelection = 0;

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord record = this._records[i];
         if (record == defaultTransport) {
            defaultSelection = i;
         }

         names[i] = record.getName();
      }

      return (ObjectChoiceField)(new Object(RuntimeResources.getString(45), names, defaultSelection));
   }

   private final AGInfo getActiveServerInfo() {
      AGInfo result = this._runtimeInfo.getDefaultAGInfo();
      if (result == null) {
         result = this._runtimeInfo.getNewAGInfo();
      }

      return result;
   }

   private final ServiceRecord getDefaultTransport() {
      return this._activationService.getDefaultTransportRecord();
   }

   private final String getActivationUrl() {
      String url = this._urlEditField.getText().trim().toLowerCase();
      if (!url.startsWith("http://") && !url.startsWith("https://")) {
         StringBuffer result = (StringBuffer)(new Object("http://"));
         String host = this._urlEditField.getText();
         result.append(host);
         result.append(':');
         result.append("7080");
         result.append("/mds");
         url = result.toString();
      }

      return url;
   }

   private final void addProgress() {
      this._statusManager = (Manager)(new Object());
      this._statusManager.add((Field)(new Object(RuntimeResources.getString(34), 36028797018963968L)));
      this._statusManager.add((Field)(new Object(null, 0, 6, 0, 4)));
      this.add(this._statusManager);
   }

   private final void updateProgress(String status, int value) {
      if (this._statusManager == null) {
         this.addProgress();
      }

      RichTextField statusText = (RichTextField)this._statusManager.getField(0);
      GaugeField statusBar = (GaugeField)this._statusManager.getField(1);
      statusText.setText(status);

      try {
         statusBar.setValue(value);
      } finally {
         Logger.log(((StringBuffer)(new Object("MDS Activation error: invalid progress value - "))).append(String.valueOf(value)).toString(), 2);
         statusBar.setValue(0);
         return;
      }
   }

   private final void completeProgress() {
      RichTextField statusField = (RichTextField)this._statusManager.getField(0);
      GaugeField statusBar = (GaugeField)this._statusManager.getField(1);
      statusField.setText(RuntimeResources.getString(27));
      statusBar.setValue(statusBar.getValueMax());
   }
}
