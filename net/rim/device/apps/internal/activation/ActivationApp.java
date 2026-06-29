package net.rim.device.apps.internal.activation;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.cldc.io.fastdormancy.FastDormancyManager;
import net.rim.device.internal.deviceagent.OutgoingDeviceAgentCollection;
import net.rim.device.internal.provisioning.ActivationService;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.InternalServices;

public final class ActivationApp
   extends UiApplication
   implements GlobalEventListener,
   DialogClosedListener,
   ActivationEventListenener,
   ActivationEventQueueCallback {
   ActivationScreen _screen;
   private int _currentState;
   private Dialog _currentGlobalScreenDialog = null;
   private ActivationEventQueue _eventQueue = new ActivationEventQueue();
   static final String ACTIVATION_APP_RIBBON_ID = "net.rim.ActivationHomeScreenApp";
   public static long ACTIVATION_APP_ID = 3829627575527376155L;
   public static long ACTIVATION_APP_SYNC_STATUS_CHANGED = 7566343571206858229L;
   public static final int READY = 0;
   public static final int PREPARING = 1;
   public static final int ACTIVATING = 2;
   public static final int KEYREGENERATING = 3;
   public static final int SLOWSYNCING = 4;
   public static final int CANCELLING = 5;
   static ResourceBundleFamily _resources = ResourceBundle.getBundle(6266320976869391490L, "net.rim.device.apps.internal.resource.Activation");

   final void exitApp() {
      this._eventQueue.stop();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.remove(ACTIVATION_APP_ID);
   }

   final void setCurrentState(int state) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final int getCurrentState() {
      return this._currentState;
   }

   public final int displayRadioToggleDialog() {
      synchronized (this.getAppEventLock()) {
         String[] choices = new Object[]{CommonResources.getString(9154), CommonResources.getString(9042)};
         int answer = Dialog.ask(CommonResources.getString(9153), choices, 0);
         if (answer == 0) {
            Radio.requestPowerOn();
         }

         return answer;
      }
   }

   final void setCurrentGlobalScreenDialog(Dialog dialog) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final Dialog getCurrentGlobalScreenDialog() {
      return this._currentGlobalScreenDialog;
   }

   final void refreshMainScreen(String email, String password, long sid) {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord record = sb.getRecordByCidAndSid("sync", sid);
      if (record == null) {
         record = sb.getRecordByCidAndSid("cical", sid);
      }

      ActivationScreen newScreen = this._screen;
      if (OTASyncProgressHandler.getInstance().isSyncInProgress() && sid != -1) {
         if (!(this._screen instanceof OTASyncProgressScreen)) {
            this.setCurrentState(4);
            String statusInfo = null;
            if (this._screen instanceof ActivationScreen) {
               ActivationScreen activationScreen = this._screen;
               activationScreen.clearActivationStatus();
               statusInfo = activationScreen.getStatusInfo();
            }

            if (statusInfo == null) {
               statusInfo = "";
            }

            String value = "";
            if (record != null) {
               value = record.getName();
            }

            if (statusInfo.length() > 0) {
               statusInfo = ((StringBuffer)(new Object())).append(statusInfo).append('\n').toString();
            }

            statusInfo = ((StringBuffer)(new Object()))
               .append(statusInfo)
               .append(MessageFormat.format(_resources.getString(153), new Object[]{value}))
               .toString();
            newScreen = new OTASyncProgressScreen(this, statusInfo, sid);
            RIMGlobalMessagePoster.postGlobalEvent(-4731267519193158412L, 3852, 0, null, null);
         }
      } else if (this._screen == null) {
         this.setCurrentState(0);
         newScreen = new ActivationScreen(this, null, email, password);
      }

      if (newScreen != this._screen) {
         if (this._screen != null) {
            this.popScreen(this._screen);
         }

         this._screen = newScreen;
         this._screen.setFocus();
         this.pushScreen(this._screen);
      }
   }

   final void pushConfigScreen() {
      this.pushScreen(new OTASyncConfigScreen(this));
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if ((guid == 8508406279413621091L || guid == -594020114676189989L) && ITPolicy.getBoolean(33, 6, false)) {
         guid = -4731267519193158412L;
         data0 = 1398166104;
      }

      if (guid != -4731267519193158412L) {
         if (guid == ACTIVATION_APP_SYNC_STATUS_CHANGED) {
            long sid = -1;
            if (object0 instanceof Object) {
               sid = object0;
            }

            this.refreshMainScreen(null, null, sid);
         }
      } else {
         int messageId = data1;
         switch (data0) {
            case 3840:
               messageId = data0;
               this._screen.displayStatus(messageId, object0);
               break;
            case 3841:
            case 3842:
            case 3843:
            case 3850:
            case 3851:
            case 1398166104:
               this.setCurrentState(0);
               messageId = data0;
            case 3844:
               this.setCurrentState(0);
            case 3845:
               this._screen.displayMessage(messageId, object0);
               break;
            case 3846:
            case 3847:
               this._screen.displayStatus(messageId, object0);
               break;
            case 3848:
               if (object0 instanceof Object[]) {
                  Object[] values = (Object[])object0;
                  long sid = -1;
                  String newTitle = "";
                  if (values.length > 0) {
                     Object var10000 = values[0];
                     if (values[0] instanceof Object) {
                        newTitle = (String)var10000;
                     }
                  }

                  if (values.length >= 1) {
                     Object var14 = values[1];
                     if (values[1] instanceof Object) {
                        sid = var14;
                     }
                  }

                  if (this._screen instanceof OTASyncProgressScreen) {
                     this._screen.setTitle(newTitle);
                  }

                  this.refreshMainScreen(null, null, sid);
               }
               break;
            case 3849:
               this._screen.displayMessage(messageId, object0);
         }

         this.checkQueue(data0);
      }
   }

   @Override
   public final void onEventFromActivationEventQueue(long guid, int data0, int data1, Object object0, Object object1) {
      synchronized (this.getAppEventLock()) {
         this.eventOccurred(guid, data0, data1, object0, object1);
      }
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      if (dialog == this._currentGlobalScreenDialog && !(dialog instanceof ActivationApp$RadioOffWarningDialog)) {
         this.setCurrentState(0);
         this._screen.exitApp();
      }

      this._currentGlobalScreenDialog = null;
   }

   public final void activationEventOccurred(ActivationEventQueueCallback callback, long eventID, int value1, int value2, Object obj1, Object obj2) {
      this._eventQueue.addEvent(callback, eventID, value1, value2, obj1, obj2);
   }

   static final String getApplicationTitle(boolean removeUnderscores) {
      String title = _resources.getString(0);
      if (removeUnderscores) {
         title = StringUtilities.removeChars(title, "̲");
      }

      return title;
   }

   private static final ApplicationDescriptor findActivationApp(boolean foreground) {
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      ApplicationDescriptor[] descriptors = ApplicationManager.getApplicationManager().getVisibleApplications();
      int activationModuleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_activation");
      if (descriptors != null) {
         for (int i = 0; i < descriptors.length; i++) {
            if (activationModuleHandle == descriptors[i].getModuleHandle()) {
               if (foreground) {
                  appManager.requestForeground(appManager.getProcessId(descriptors[i]));
               }

               return descriptors[i];
            }
         }
      }

      return null;
   }

   public static final boolean isRunning() {
      return isRunning(false);
   }

   public static final boolean isRunning(boolean foreground) {
      return findActivationApp(foreground) != null;
   }

   public static final void run(String[] args) {
      int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_activation");
      ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(moduleHandle);
      if (descriptors.length >= 1) {
         ApplicationDescriptor descriptor = descriptors[0];
         ApplicationManager appManager = ApplicationManager.getApplicationManager();
         if (descriptor != null) {
            try {
               if (args != null) {
                  descriptor = (ApplicationDescriptor)(new Object(descriptor, args));
               }

               appManager.runApplication(descriptor);
            } finally {
               return;
            }
         }
      }
   }

   public static final int getActivationProcessId() {
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      ApplicationDescriptor[] descriptors = appManager.getVisibleApplications();

      for (int i = 0; i < descriptors.length; i++) {
         if (descriptors[i].getModuleName().equals("net_rim_bb_activation")) {
            return appManager.getProcessId(descriptors[i]);
         }
      }

      return 0;
   }

   static final void setSlowSyncState(long serviceId, int state) {
      OTASyncProgressHandler.getInstance().setSyncStatus(serviceId, state);
      Proxy.getInstance().invokeLater(new ActivationApp$ToggleSyncStateRunnable(serviceId, state == 0 || state == 5));
   }

   public static final void main(String[] args) {
      String email = null;
      String password = null;
      if (args != null && args.length > 0) {
         if ("init".equals(args[0])) {
            Application.setAcceptEventsForProcess(false);
            EventLogger.register(-5915434835955743234L, "net.rim.activation", 2);
            EventLogger.register(1200380696048604626L, "net.rim.activation.data", 2);
            ActivationServiceImpl.register();
            OTASyncProgressHandler.register();
            ((ActivationServiceImpl)ActivationService.getInstance()).iconRefresh();
            OutgoingDeviceAgentCollection oac = (OutgoingDeviceAgentCollection)OutgoingDeviceAgentCollection.getInstance();
            if (oac != null) {
               String OSVersion = DeviceInfo.getPlatformVersion();
               String AppsVersion = ((StringBuffer)(new Object("v"))).append(DeviceInfo.getSoftwareVersion()).toString();
               String modelName = DeviceInfo.getDeviceName();
               String manufacturerName = DeviceInfo.getManufacturerName();
               int hardwareID = InternalServices.getHardwareID();
               int networkType = RadioInfo.getNetworkType();
               String brandingVersion = Integer.toString(Branding.getVersion());
               int brandingVendorId = Branding.getVendorId();
               oac.addDeviceCapabilities((byte)23, OSVersion.getBytes());
               oac.addDeviceCapabilities((byte)19, AppsVersion.getBytes());
               oac.addDeviceCapabilities((byte)21, modelName.getBytes());
               oac.addDeviceCapabilities((byte)20, manufacturerName.getBytes());
               byte[] intBuffer = new byte[4];
               DataBuffer buffer = (DataBuffer)(new Object(intBuffer, 0, intBuffer.length, true));
               buffer.reset();
               buffer.writeInt(hardwareID);
               buffer.trim();
               oac.addDeviceCapabilities((byte)22, buffer.toArray());
               buffer.reset();
               buffer.writeInt(networkType);
               buffer.trim();
               oac.addDeviceCapabilities((byte)29, buffer.toArray());
               buffer.reset();
               buffer.writeInt(brandingVendorId);
               buffer.trim();
               oac.addDeviceCapabilities((byte)26, buffer.toArray());
               oac.addDeviceCapabilities((byte)25, brandingVersion.getBytes());
            }

            EmailSetupWizard.register();
            return;
         }

         if (args.length == 2) {
            email = args[0];
            password = args[1];
         }
      }

      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      ApplicationDescriptor[] descriptors = appManager.getVisibleApplications();

      for (int i = 0; i < descriptors.length; i++) {
         if (descriptors[i].getModuleName().equals("net_rim_bb_activation")) {
            appManager.requestForeground(appManager.getProcessId(descriptors[i]));
            return;
         }
      }

      ActivationApp app = new ActivationApp(email, password);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.replace(ACTIVATION_APP_ID, app);
      app.addGlobalEventListener(app);
      app.enterEventDispatcher();
      FastDormancyManager.getInstance().setFastDormancy(true);
   }

   public static final ActivationApp getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (ActivationApp)ar.get(ACTIVATION_APP_ID);
   }

   private final void checkQueue(int event) {
      switch (event) {
         case 3842:
         case 3843:
         case 3850:
         case 3851:
            if (this._eventQueue.isAlive()) {
               this._eventQueue.stop();
            }
      }
   }

   private ActivationApp(String email, String password) {
      this.refreshMainScreen(email, password, -1);
   }
}
