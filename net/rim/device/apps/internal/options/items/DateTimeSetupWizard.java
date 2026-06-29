package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.BasicWizardPage;
import net.rim.device.apps.api.setupwizard.SetupWizardOrdering;
import net.rim.device.apps.api.setupwizard.WizardDialog;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.timesync.TimeSync;

public final class DateTimeSetupWizard extends BasicWizardPage implements RadioStatusListener, RealtimeClockListener, GlobalEventListener {
   private DateTimeController _controller;
   private RichTextField _radioStatusLabel;
   private WizardDialog _connectingDialog;
   private int _networkUpdateState;
   private DateTimeSetupWizard$ConnectionTimeOutThread _connectionTimeOutThread;
   private boolean _secondConnectionAttempt;
   public static long WIZARD_WAIT_THREAD_UPDATE = 5663798581947223657L;
   private static final int UPDATE_IDLE;
   private static final int CONNECTING;
   private static final int UPDATING_TIME;
   private static final int TIME_UPDATED;
   private static final int CONNECT_FAILED;

   public DateTimeSetupWizard() {
      super(OptionsResources.getResourceBundle(), 2104, 200, SetupWizardOrdering.DATE_AND_TIME_CATEGORY);
   }

   @Override
   protected final void initialize() {
      this._controller = new DateTimeController(true);
      this._controller.initialize();
      UiApplication.getUiApplication().addGlobalEventListener(this);
   }

   @Override
   protected final void populateContent(AppsMainScreen screen, Manager content) {
      content.add((Field)(new Object(18014398509481984L)));
      VerticalFieldManager header = (VerticalFieldManager)(new Object());
      header.setFont(this.getHeaderFont());
      int headingResourceId;
      if (Trackball.isSupported()) {
         headingResourceId = 1976;
      } else {
         headingResourceId = 2116;
      }

      header.add((Field)(new Object(OptionsResources.getString(headingResourceId))));
      header.add((Field)(new Object()));
      content.add(header);
      this._controller.populateMainScreen(screen, content);
      this._radioStatusLabel = (RichTextField)(new Object("", 36028797019226112L));
      this._radioStatusLabel.setBorder(10, 5, 0, 5);
      content.add(this._radioStatusLabel);
      this._networkUpdateState = 0;
      Application.getApplication().invokeLater(new DateTimeSetupWizard$1(this));
      this.updateNetworkStatus();
      Application.getApplication().addRadioListener(this);
      UiApplication.getUiApplication().addRealtimeClockListener(this);
   }

   protected final boolean isSimCardMissing() {
      try {
         return SIMCard.isSupported() && !SIMCard.isValid();
      } finally {
         ;
      }
   }

   private final void startConnectionTimeOutThread() {
      if (this._connectionTimeOutThread != null && this._connectionTimeOutThread.isAlive()) {
         this._connectionTimeOutThread.stop();
         this._connectionTimeOutThread.interrupt();
      }

      this._connectionTimeOutThread = new DateTimeSetupWizard$ConnectionTimeOutThread(this.getUIAppProcessId(), 60000);
      this._connectionTimeOutThread.start();
   }

   private final void checkRadio() {
      this._networkUpdateState = 0;
      if (!this.isSimCardMissing() && RadioInfo.getState() != 1) {
         int answer = WizardDialog.ask(3, OptionsResources.getString(2018), 4, super._warnOnCloseOrHotKey);
         if (answer == 4) {
            RadioInternal.activateRadios(RadioInternal.getPrimaryWAF());
            TimeSync timeSync = TimeSync.getInstance();
            if (InternalServices.isNetworkTimeValid()) {
               timeSync.setSource(3);
               timeSync.synchronize(true);
            }

            this._networkUpdateState = 1;
            this.startConnectionTimeOutThread();
            this._connectingDialog = WizardDialog.showWaitingDialog(OptionsResources.getString(1977), super._warnOnCloseOrHotKey);
            this.updateNetworkStatus();
            return;
         }
      } else if (!this.isSimCardMissing() && RadioInfo.getState() == 1) {
         this._controller.bestGuessTimeZone();
      }
   }

   @Override
   protected final boolean saveWizard(Verb sender) {
      return this._controller.save();
   }

   @Override
   protected final void discardWizard() {
      this._controller.discard();
   }

   @Override
   protected final void beforeOpen() {
      this.reloadTitle();
      this._controller.beforeOpen();
   }

   @Override
   protected final void afterClose() {
      this._controller.beforeClose();
      Application.getApplication().removeRadioListener(this);
      UiApplication.getUiApplication().removeRealtimeClockListener(this);
   }

   protected final void setRadioStatusLabel(String text) {
      if (this._radioStatusLabel != null) {
         this._radioStatusLabel.setText(text);
      }
   }

   protected final void onConnect() {
      TimeSync timeSync = TimeSync.getInstance();
      if (InternalServices.isNetworkTimeValid()) {
         timeSync.setSource(3);
         timeSync.synchronize(true);
         this._controller.bestGuessTimeZone();
      }

      if (this._connectionTimeOutThread != null) {
         this._connectionTimeOutThread.stop();
      }

      if (this._connectingDialog != null && this._connectingDialog.dismissWaitingDialog()) {
         this._connectingDialog = null;
      }

      Status.show(PhoneResources.getString(300));
   }

   protected final void updateNetworkStatus() {
      switch (this._networkUpdateState) {
         case 0:
            break;
         case 1:
         default:
            if (RadioInfo.getState() == 1) {
               if (RadioInfo.getNetworkService() == 0) {
                  this.setRadioStatusLabel(OptionsResources.getString(1977));
                  return;
               }

               this.onConnect();
               TimeSync timeSync = TimeSync.getInstance();
               timeSync.setSource(2);
               timeSync.synchronize(true);
               this._networkUpdateState = 2;
               this.updateNetworkStatus();
               return;
            }
            break;
         case 2:
            this.setRadioStatusLabel(OptionsResources.getString(1978));
            return;
         case 3:
            this.setRadioStatusLabel(OptionsResources.getString(1979));
            if (this._connectingDialog != null && this._connectingDialog.dismissWaitingDialog()) {
               this._connectingDialog = null;
            }

            Status.show(OptionsResources.getString(1979));
            this._networkUpdateState = 0;
            break;
         case 4:
            this.setRadioStatusLabel(OptionsResources.getString(1999));
            return;
      }
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
      this.updateNetworkStatus();
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
      this.updateNetworkStatus();
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.updateNetworkStatus();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == WIZARD_WAIT_THREAD_UPDATE) {
         this.networkConnectionTimedOut();
      }
   }

   @Override
   public final void clockUpdated() {
      if (this._networkUpdateState == 2) {
         this._networkUpdateState = 3;
         this.updateNetworkStatus();
      }
   }

   private final int getUIAppProcessId() {
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      ApplicationDescriptor[] descriptors = appManager.getVisibleApplications();

      for (int i = 0; i < descriptors.length; i++) {
         if (descriptors[i].getModuleName().equals("net_rim_bb_setupwizard_app")) {
            return appManager.getProcessId(descriptors[i]);
         }
      }

      return 0;
   }

   private final void networkConnectionTimedOut() {
      if (this._connectingDialog != null && this._connectingDialog.dismissWaitingDialog()) {
         this._connectingDialog = null;
      }

      if (!this._secondConnectionAttempt) {
         this._connectionTimeOutThread.stop();
         int answer = WizardDialog.ask(3, OptionsResources.getString(2000), 4, super._warnOnCloseOrHotKey);
         this._secondConnectionAttempt = true;
         if (answer == 4) {
            this._connectingDialog = WizardDialog.showWaitingDialog(OptionsResources.getString(1977), super._warnOnCloseOrHotKey);
            this.startConnectionTimeOutThread();
         } else {
            this.networkConnectionTimedOut();
         }
      } else {
         this._networkUpdateState = 4;
         this.updateNetworkStatus();
         this._connectionTimeOutThread.stop();
         Status.show(OptionsResources.getString(1999));
      }
   }
}
