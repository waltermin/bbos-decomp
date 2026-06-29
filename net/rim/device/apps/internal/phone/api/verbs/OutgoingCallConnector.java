package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GAN;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.phone.VoiceApplication;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.CallConnector;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.apps.internal.phone.api.PhoneLogger;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.iConferenceCall;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.ui.PhoneStatusDialog;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.options.SSRequestStatusDialog;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.ui.component.BackgroundDialog;

public class OutgoingCallConnector extends CallConnector implements RadioStatusListener {
   private Object _connectionContext;
   private boolean _emergencyCall;
   private boolean _gainingService;
   private boolean _waitForRadio;
   private PhoneStatusDialog _statusDialog;
   private int _communicationsState;
   static final int LOG_OUTGOING_CALL_REJECTED = 1380598612;
   private static final long DEFAULT_TIMEOUT = 4000L;
   private static final long WAIT_FOR_RADIO_TIMEOUT = 60000L;
   private static final int COMM_STATE_ON = 1;
   private static final int COMM_STATE_OFF = 2;
   private static final int COMM_STATE_LOW_BATTERY = 3;
   private static final int COMM_STATE_TURNING_ON = 4;
   private static final int COMM_STATE_OTHER = 5;

   public static boolean outgoingCallPermitted() {
      switch (RadioInfo.getNetworkType()) {
         case 2:
            return false;
         case 5:
         default:
            LiveCall currentCall = (LiveCall)VoiceServices.getVoiceApplication().getCurrentCall();
            if (currentCall instanceof iConferenceCall) {
               return false;
            } else if (currentCall != null && currentCall.getFlag(8192)) {
               return false;
            }
         case 3:
         case 4:
         case 6:
         case 7:
            byte state = VoiceServices.getPhoneState();
            return state == -1 ? false : VoiceServices.getOutgoingCallPermissionBits(state, (VoiceServices.getVoiceNetworkCapabilities() & 4) == 0);
      }
   }

   private static int getCommunicationsState() {
      int commState = 1;
      if ((RadioInfo.getNetworkService() & 2) != 0) {
         return 1;
      }

      int requiredWafs = Phone.getInstance().getWAFs(PhoneUtilities.getCurrentLineId());
      int activeWafs = RadioInfo.getActiveWAFs();
      if ((requiredWafs & 4) == 0) {
         switch (RadioInfo.getState()) {
            case -1:
            case 3:
            case 4:
               return 5;
            case 0:
            default:
               return 2;
            case 1:
               return 1;
            case 2:
               return 3;
            case 5:
               return 4;
         }
      } else {
         if ((requiredWafs & activeWafs) == 0) {
            commState = 2;
         }

         return commState;
      }
   }

   public static boolean startCall(Object connectionParameters) {
      String phoneNumber = (String)ContextObject.get(connectionParameters, 6486659828352467672L);
      if (phoneNumber != null) {
         int callId = VoiceServices.filterStartCall(phoneNumber, 0);
         switch (callId) {
            case -1:
               break;
            default:
               return false;
         }
      }

      if (!ITPolicy.getBoolean(1, true) && !PhoneUtilities.isEmergencyNumber(phoneNumber) && !PhoneUtilities.isCDMAServiceCall(phoneNumber)) {
         Status.show(PhoneResources.getString(6054), Bitmap.getPredefinedBitmap(2), 3000);
         return false;
      }

      if (PhoneUtilities.cdmaTypeNetwork()
         && PhoneUtilities.canExit911CallbackMode()
         && (RadioInfo.getNetworkService() & 256) != 0
         && !PhoneUtilities.isEmergencyNumber(phoneNumber)) {
         String prompt = PhoneResources.getString(6274);
         String[] choices = new Object[]{PhoneResources.getString(6276), CommonResources.getString(9042)};
         if (BackgroundDialog.getChoice(prompt, choices, 0) != 0) {
            return false;
         }

         VoiceServices.exitEmergencyCallbackMode();
      }

      boolean emergencyCall = PhoneUtilities.emergencyCall(connectionParameters);
      int commState = getCommunicationsState();
      int batteryStatus = DeviceInfo.getBatteryStatus();
      boolean lowBatt = (batteryStatus & 16384) != 0;
      if (commState != 2) {
         if (commState == 3 && !emergencyCall) {
            Status.show(CommonResources.getString(9099), Bitmap.getPredefinedBitmap(2), 2000);
            return false;
         }

         if (emergencyCall && (RadioInfo.getNetworkService() & 1) == 0) {
            setEmergencyGANPreference();
         }

         if (!emergencyCall || RadioInfo.getNetworkType() != 4 && RadioInfo.getNetworkType() != 6) {
            if (commState == 4) {
               Out.p("PHONE;radio turning on");
               new OutgoingCallConnector(connectionParameters, 60000, commState, true).connect();
               return true;
            }

            if (commState == 1) {
               if (emergencyCall) {
                  if (RadioInfo.getSignalLevel() != -256) {
                     new OutgoingCallConnector(connectionParameters).connect();
                     return true;
                  }

                  new OutgoingCallConnector(connectionParameters, 60000, commState, true).connect();
                  return true;
               }

               if (!outgoingCallPermitted()) {
                  Dialog.inform(PhoneResources.getString(6001));
                  return false;
               }

               new OutgoingCallConnector(connectionParameters).connect();
            }

            return true;
         } else {
            new OutgoingCallConnector(connectionParameters).connect();
            return true;
         }
      } else {
         Out.p("PHONE;radio is off");
         if (emergencyCall) {
            setEmergencyGANPreference();
         } else {
            if (lowBatt) {
               Status.show(CommonResources.getString(9099), Bitmap.getPredefinedBitmap(2), 2000);
               return false;
            }

            ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
            Object o = applicationRegistry.get(1185883946270450222L);
            String[] choices;
            if (o == null) {
               choices = new Object[]{CommonResources.getString(9154), CommonResources.getString(9042)};
            } else {
               choices = new Object[]{CommonResources.getString(9175), CommonResources.getString(9042)};
            }

            Application app = Application.getApplication();
            int answer;
            if (ApplicationManager.getApplicationManager().isSystemLocked() && app.hasEventThread()) {
               answer = Dialog.ask(CommonResources.getString(9153), choices, 0);
            } else {
               answer = BackgroundDialog.getChoice(CommonResources.getString(9153), choices, 0);
            }

            if (answer != 0) {
               ContextObject.setFlag(connectionParameters, 40);
               return false;
            }
         }

         new OutgoingCallConnector(connectionParameters, 60000, commState, true).connect();
         return true;
      }
   }

   private static void setEmergencyGANPreference() {
      if (GAN.isGANAllowed()) {
         int ganPref = RadioInternal.getGANPreference();
         if (ganPref == 2) {
            RadioInternal.setGANPreference(3);
            System.out.println("GAN Preference changed from GAN_PREFERENCE_GAN_ONLY to GAN_PREFERENCE_GAN_PREFERRED for emergency call");
            return;
         }

         if (ganPref == 0) {
            RadioInternal.setGANPreference(1);
            System.out.println("GAN Preference changed from GAN_PREFERENCE_CELL_ONLY to GAN_PREFERENCE_CELL_PREFERRED for emergency call");
         }
      }
   }

   public OutgoingCallConnector(Object context) {
      this(context, 4000, getCommunicationsState(), false);
   }

   public OutgoingCallConnector(Object context, long timeout, int communicationsState, boolean gainingService) {
      super(timeout);
      this._communicationsState = communicationsState;
      this._connectionContext = ContextObject.clone(context);
      this._emergencyCall = PhoneUtilities.emergencyCall(this._connectionContext);
      this._gainingService = gainingService;
      if (PhoneUtilities.getAllLineIds().length > 1) {
         this.setPreferredLine(ContextObject.getIntegerData(this._connectionContext, PhoneUtilities.getCurrentLineId()));
      }
   }

   @Override
   protected void startConnection() {
      super.startConnection();
      this.doStartConnection();
   }

   private void completeConnectionLater() {
      super._app.invokeLater(new OutgoingCallConnector$1(this), 2000, false);
   }

   @Override
   protected void completeConnection() {
      super.completeConnection();
      this.clearStatus();
      this.startCall();
   }

   @Override
   protected void startListening() {
      super.startListening();
      VoiceServices.addRadioStatusListener(this);
   }

   @Override
   protected void stopListening() {
      super.stopListening();
      VoiceServices.removeRadioStatusListener(this);
   }

   @Override
   protected boolean conditionsSatisfied(int lastEvent, int callId, Object context) {
      return !this._waitForRadio ? super.conditionsSatisfied(lastEvent, callId, context) : false;
   }

   @Override
   protected void onTimeout() {
      super.onTimeout();
      this.clearStatus();
      if (this._waitForRadio) {
         super._app.invokeLater(new OutgoingCallConnector$AlertMessage(PhoneResources.getString(6005)));
      }
   }

   private void doStartConnection() {
      if (this._communicationsState == 2 || this._communicationsState == 4 || this._gainingService) {
         Runnable runnable = new OutgoingCallConnector$2(this);
         VoiceServices.getVoiceApplication().requestForeground(runnable, new Object(96));
      }

      if (this._communicationsState == 1 && this._communicationsState != 4 && !this._gainingService) {
         if (VoiceServices.getPhoneState() == 2) {
            VoiceServices.holdCall();
            this.waitForEvent(1003);
         } else {
            this.completeConnection();
         }
      } else {
         switch (RadioInfo.getNetworkType()) {
            case 6:
               this.waitForEvent(4000);
               return;
            default:
               this.waitForEvent(4010);
         }
      }
   }

   private void startCall() {
      String phoneNumber = (String)ContextObject.get(this._connectionContext, 6486659828352467672L);
      if (phoneNumber != null && (!phoneNumber.equals("*") || !PhoneUtilities.gsmTypeNetwork())) {
         int clirFlags = PhoneOptions.getOptions().getCLIR();
         if (DeviceInfo.isInHolster() && !PhoneUtilities.getPrivateFlag(this._connectionContext, 81)) {
            Out.p(1128352844, 1380598612, phoneNumber);
         } else {
            VoiceApplication voiceApp = VoiceServices.getVoiceApplication();
            if (voiceApp.inForeground()) {
               this.doStartCall(phoneNumber, clirFlags);
            } else {
               int clirFlag = clirFlags;
               Runnable voiceAppActivationRunnable = new OutgoingCallConnector$3(this, phoneNumber, clirFlag);
               voiceApp.requestForeground(voiceAppActivationRunnable, this._connectionContext);
            }
         }
      } else {
         String[] buttons = CommonResource.getStringArray(10004);
         Dialog dlg = (Dialog)(new Object(PhoneResources.getString(3021), buttons, null, 0, null, 33554432));
         dlg.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
         dlg.show(-2147483645);
      }
   }

   private void doStartCall(String phoneNumber, int clirFlags) {
      int id = VoiceServices.startCall(phoneNumber, clirFlags);
      Out.p(1128352844, 1129072964, id);
      if (id == 32769) {
         PhoneUtilities.setLastNumberDialed("");
         SSRequestStatusDialog status = new OutgoingCallConnector$4(this, false, true);
         status.show();
         if (!PersistentContent.isEncryptionEnabled()) {
            PhoneLogger.log(((StringBuffer)(new Object("OCC SS_CALL "))).append(phoneNumber).toString());
            return;
         }
      } else if (id != 0) {
         if (PhoneUtilities.isEmergencyNumber(phoneNumber)) {
            PhoneUtilities.setLastNumberDialed("");
         }

         VoiceServices.broadcastEvent(1100, id, this._connectionContext);
         if (!PersistentContent.isEncryptionEnabled()) {
            PhoneLogger.log(((StringBuffer)(new Object("OCC calling "))).append(phoneNumber).toString());
         }
      }
   }

   private void showStatus(String statusText) {
      this._statusDialog = new PhoneStatusDialog(statusText);
      Font f = this._statusDialog.getFont();
      if (Ui.convertSize(f.getHeight(), 0, 2) > 9) {
         this._statusDialog.setFont(f.derive(f.getStyle(), Ui.convertSize(9, 2, 0)));
      }

      super._app.pushGlobalScreen(this._statusDialog, -2147483645, 2);
   }

   private void clearStatus() {
      if (this._statusDialog != null) {
         super._app.popScreen(this._statusDialog);
      }
   }

   private boolean testWaitCondition(int eventId, int networkService) {
      switch (eventId) {
         case 4000:
            if (RadioInfo.getNetworkType() == 6) {
               return true;
            }
         case 4010:
            if (this._emergencyCall) {
               if ((networkService & 1) != 0 || (networkService & 2) != 0 || (networkService & 4) != 0) {
                  String logString = "PHONE: OCC have service => starting emerg call";
                  Out.p(logString);
                  PhoneLogger.log(logString);
                  return true;
               }
            } else if (!isValidSIMCard()) {
               if ((networkService & 1) != 0) {
                  return true;
               }
            } else if ((networkService & 2) != 0) {
               PhoneLogger.log("OCC have voice srvc => calling");
               return true;
            }
            break;
         case 4040:
            int signalLevel = RadioInfo.getSignalLevel();
            if (this._emergencyCall && signalLevel != -256) {
               PhoneLogger.log("OCC have cvrg => emerg calling");
               return true;
            }
      }

      return false;
   }

   private static boolean isValidSIMCard() {
      if (!PhoneUtilities.cdmaTypeNetwork() && !PhoneUtilities.wifiTypeNetwork()) {
         try {
            return SIMCard.isValid();
         } finally {
            ;
         }
      } else {
         return true;
      }
   }

   @Override
   public void networkStarted(int networkId, int service) {
      this.radioEventReceived(4000, service);
   }

   @Override
   public void networkServiceChange(int networkId, int service) {
      this.radioEventReceived(4010, service);
   }

   @Override
   public void signalLevel(int level) {
      this.radioEventReceived(4040, RadioInfo.getNetworkService());
   }

   private void radioEventReceived(int eventID, int service) {
      if (this.testWaitCondition(eventID, service) && !super._conditionsSatisfied) {
         super._conditionsSatisfied = true;
         this.stopTimer();
         this.completeConnectionLater();
      }
   }

   @Override
   public void baseStationChange() {
   }

   @Override
   public void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public void radioTurnedOff() {
   }

   @Override
   public void networkStateChange(int state) {
   }

   @Override
   public void networkScanComplete(boolean success) {
   }

   static UiApplication access$100(OutgoingCallConnector x0) {
      return x0._app;
   }
}
