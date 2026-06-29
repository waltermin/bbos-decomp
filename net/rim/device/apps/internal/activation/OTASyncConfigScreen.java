package net.rim.device.apps.internal.activation;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceIdentifier;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.provisioning.ActivationService;
import net.rim.device.internal.synchronization.ota.service.Configuration;
import net.rim.device.internal.synchronization.ota.service.ServicesConfigurationManager;
import net.rim.device.internal.ui.component.PropertyField;

final class OTASyncConfigScreen extends AppsMainScreen implements FieldChangeListener {
   Configuration _syncConfiguration = null;
   int _syncSourceIndex = -1;
   private VerticalFieldManager _mainManager;
   private ObjectChoiceField _syncableServices = null;
   private ObjectChoiceField _wirelessSync = new ObjectChoiceField("Wireless Sync:", new String[]{"Yes", "No"});
   private PropertyField _sessionTimeOut = null;
   private PropertyField _batchSyncTime = null;
   private PropertyField _userState = null;
   private PropertyField _numberOfRetries = null;
   private PropertyField _ignoreSessionTimeOut = null;
   private PropertyField _dataSourceIDField = null;
   private long[] _serviceIds = null;
   ActivationApp _app;

   OTASyncConfigScreen(ActivationApp app) {
      super(0);
      this._app = app;
      this.setTitle(new LabelField(ActivationApp.getApplicationTitle(true)));
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] records = sb.findRecordsByCid("sync");
      if (records != null && records.length > 0) {
         this._syncSourceIndex = 0;
         String[] values = new String[records.length];
         this._serviceIds = new long[records.length];

         for (int i = 0; i < values.length; i++) {
            values[i] = records[i].getName();
            this._serviceIds[i] = ServiceIdentifier.createSid(records[i]);
         }

         this._syncableServices = new ObjectChoiceField(ActivationApp._resources.getString(135), values);
         this._syncSourceIndex = 0;
         if (values.length == 1) {
            this._syncableServices.setEditable(false);
         }

         this._syncableServices.setChangeListener(this);
         this.add(this._syncableServices);
         this.add(new SeparatorField());
         this._mainManager = new VerticalFieldManager(64424509440L);
         this.add(this._mainManager);
         this.serviceChanged();
      }
   }

   private final void serviceChanged() {
      this._syncSourceIndex = this._syncableServices.getSelectedIndex();
      this._mainManager.deleteAll();
      long serviceId = this._serviceIds[this._syncSourceIndex];
      ServiceRecord aSyncServiceRecord = ServiceBook.getSB().getRecordByCidAndSid("sync", serviceId);
      if (aSyncServiceRecord != null) {
         this._syncConfiguration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(serviceId);
         this._wirelessSync.setSelectedIndex(this._syncConfiguration.isUserPreferenceToSyncSet() ? 0 : 1);
         this.addFields();
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      this.serviceChanged();
   }

   private final void addFields() {
      int sessionTimeout = (int)(this._syncConfiguration.getSessionTimeout() / 60000);
      int batchSymcTimeout = (int)(this._syncConfiguration.getBatchTimeout() / 60000);
      boolean userEnabled = this._syncConfiguration.isUserEnabled();
      long numberOfRetries = this._syncConfiguration.getNumberOfRetries();
      int ignoredSessionTimeout = (int)(this._syncConfiguration.getIgnoredSessionTimeout() / 60000);
      String label = ActivationApp._resources.getString(152);
      ServiceRecord sr = ServiceBook.getSB().getRecordByCidAndSid("sync", this._serviceIds[this._syncSourceIndex]);
      String dsid = "";
      if (sr != null) {
         dsid = sr.getDataSourceId();
      }

      this._dataSourceIDField = new PropertyField(label, dsid);
      label = ActivationApp._resources.getString(133);
      String value = MessageFormat.format(ActivationApp._resources.getString(139), new String[]{Integer.toString(sessionTimeout)});
      this._sessionTimeOut = new PropertyField(label, value);
      label = ActivationApp._resources.getString(134);
      value = MessageFormat.format(ActivationApp._resources.getString(139), new String[]{Integer.toString(batchSymcTimeout)});
      this._batchSyncTime = new PropertyField(label, value);
      label = ActivationApp._resources.getString(138);
      value = ActivationApp._resources.getString(userEnabled ? 140 : 141);
      this._userState = new PropertyField(label, value);
      label = ActivationApp._resources.getString(136);
      value = Long.toString(numberOfRetries);
      this._numberOfRetries = new PropertyField(label, value);
      label = ActivationApp._resources.getString(137);
      value = MessageFormat.format(ActivationApp._resources.getString(139), new String[]{Integer.toString(ignoredSessionTimeout)});
      this._ignoreSessionTimeOut = new PropertyField(label, value);
      this._mainManager.add(this._dataSourceIDField);
      this._mainManager.add(new SeparatorField());
      this._mainManager.add(this._wirelessSync);
      this._mainManager.add(new SeparatorField());
      this._mainManager.add(this._sessionTimeOut);
      this._mainManager.add(this._batchSyncTime);
      this._mainManager.add(this._userState);
      this._mainManager.add(this._numberOfRetries);
      this._mainManager.add(this._ignoreSessionTimeOut);
      this._mainManager.add(new SeparatorField());
      ObjectListField sidList = new ObjectListField(70);
      sidList.setEmptyString("No services Activated", 68);
      String[] sids = ((ActivationServiceImpl)ActivationService.getInstance()).getActivatedSids();
      sidList.set(sids);
      this._mainManager.add(new LabelField("Activated SIDs:"));
      this._mainManager.add(sidList);
      this._mainManager.add(new SeparatorField());
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this.isDirty()) {
         menu.add(new OTASyncConfigScreen$SaveVerb(this));
      }
   }

   @Override
   protected final boolean onSave() {
      long serviceId = this._serviceIds[this._syncSourceIndex];
      int status;
      int state;
      if (this._wirelessSync.getSelectedIndex() == 0) {
         status = 5;
         state = 0;
      } else {
         ((ActivationServiceImpl)ActivationService.getInstance()).clearActivationRecord(serviceId);
         status = 2;
         state = 0;
      }

      if (serviceId != -1) {
         this._app.setCurrentState(state);
         ActivationApp.setSlowSyncState(serviceId, status);
      }

      return true;
   }
}
