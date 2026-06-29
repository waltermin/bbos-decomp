package net.rim.device.apps.internal.phone;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Branding;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.data.PhoneCallModelImpl;
import net.rim.device.apps.internal.phone.data.PhoneFolders;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.component.SimpleInputDialog;

final class PhoneBackdoorHelpScreen extends MainScreen {
   private UiApplication _app;
   private static final String[] _productionBackdoorStrings = new String[]{"SHFS - show phone state"};
   private static final String[] _developmentBackdoorStrings = new String[]{
      "AUTO - auto answer (7 secs)",
      "DOCO - docomo dialing rules",
      "USSD - ussd message",
      "CRSH - make the voice app go boom",
      "CSTP - spoof a call setup message",
      "CNAP - caller name ss response",
      "DSMD - disable smart dialing",
      "DVMP - new voicemail player",
      "ECHK - 01911 hack",
      "HSBC - headset button click",
      "HSBL - headset button lean",
      "LSSN - SS query screen",
      "OTAF - OTA activation failed",
      "QPNF - Determine phone number input font",
      "QLED - Query rapid blink LED setting",
      "SCAL - dc call alert",
      "SCNL - dc call disconnection",
      "SCTL - dc talkgroup call",
      "SDCL - dc call connection",
      "SDLT - SDLeft",
      "SDRN - set redirected number",
      "SPNF - Change phone number input font",
      "SSHN - SS testing hack on/off",
      "VBSC - verify before starting call",
      "NCFD - suppress call fwding dialog",
      "PCLV - populate cal log view"
   };
   private static String[] _backdoorStrings;

   public PhoneBackdoorHelpScreen(PhoneAppScreen phoneAppScreen) {
      super(2306142076376449024L);
      this.setTitle(new LabelField("Press ESCAPE to return"));
      boolean insecureDevice = !InternalServices.isDeviceSecure() || Branding.getData(16) != null;
      if (insecureDevice) {
         _backdoorStrings = _developmentBackdoorStrings;
      } else {
         _backdoorStrings = null;
      }

      this.add(new PhoneBackdoorHelpScreen$BackDoorManager());
      this._app = UiApplication.getUiApplication();
   }

   @Override
   protected final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1096111183:
            PhoneUtilities.toggleBooleanDebugFlag(-2489431779144400366L, "auto answer after 7 seconds = ");
            return true;
         case 1129202000:
            Application.getApplication().invokeLater(new PhoneBackdoorHelpScreen$2(this));
            this.exit();
            return true;
         case 1129468744:
            this.exit();
            Runnable boom = new PhoneBackdoorHelpScreen$5(this);
            Application.getApplication().invokeLater(boom, 2000, false);
            return true;
         case 1129534544:
            this.exit();
            Runnable setup = new PhoneBackdoorHelpScreen$7(this);
            Application.getApplication().invokeLater(setup, 50, false);
            return true;
         case 1146045263:
            PhoneUtilities.toggleBooleanDebugFlag(-6324595103497951497L, "docomo dialing rules = ");
            return true;
         case 1146309956:
            PhoneUtilities.toggleBooleanDebugFlag(-1032892149017511731L, "smart dialing = ");
            return true;
         case 1146506576:
            PhoneUtilities.toggleBooleanDebugFlag(-3015186877634035962L, "new vmail player");
            return true;
         case 1162037323:
            PhoneUtilities.toggleBooleanDebugFlag(8128272366344448397L, "E-911 testing hack ");
            return true;
         case 1162564429:
            if (Keypad.hasSendEndKeys()) {
               PhoneUtilities.toggleBooleanDebugFlag(-3318255709650210549L, "END key => Messages = ");
               return true;
            }
            break;
         case 1163285321:
            this.exit();
            Application.getApplication().invokeLater(new PhoneBackdoorHelpScreen$3(this), 2000, false);
            return true;
         case 1213416003:
            VoiceServices.broadcastEvent(100400, 100, null);
            VoiceServices.broadcastEvent(100401, 110, null);
            this.exit();
            return true;
         case 1213416012:
            VoiceServices.broadcastEvent(100400, 100, null);
            VoiceServices.broadcastEvent(100401, 2110, null);
            this.exit();
            return true;
         case 1280529230:
            this._app.pushScreen(new SSQueryScreen(this._app));
            return true;
         case 1313031748:
            PhoneUtilities.toggleBooleanDebugFlag(-8473416206083547985L, "show CF dlg = ");
            return true;
         case 1330921798:
            Runnable otaf = new PhoneBackdoorHelpScreen$6(this);
            Application.getApplication().invokeLater(otaf, 1000, false);
            this.exit();
            return true;
         case 1346587734:
            for (int i = 0; i < 20; i++) {
               CallerIDInfo callerIDInfo = PhoneUtilities.createCallerIDInfo("" + i);
               PhoneCallInitialData data = new PhoneCallInitialData(0, (byte)1, 0, callerIDInfo, null);
               PhoneCallModelImpl callLog = (PhoneCallModelImpl)PhoneUtilities.createPhoneCallModel(data);
               if (callLog != null) {
                  PhoneFolders.addItem(callLog);
               }
            }
            break;
         case 1347703875:
            PhoneUtilities.toggleBooleanDebugFlag(1835237846143718848L, "ptt call = ");
            return true;
         case 1363952964:
            boolean ledPolicy = ITPolicy.getBoolean(54, false);
            Dialog.inform("Force LED Blink = " + ledPolicy);
            return true;
         case 1396916556:
            Runnable callAlertRunner = new PhoneBackdoorHelpScreen$8(this);
            Application.getApplication().invokeLater(callAlertRunner, 10000, false);
            this.exit();
            return true;
         case 1396919884:
            VoiceServices.broadcastEvent(201010, 1, null);
            this.exit();
            return true;
         case 1396921420:
            int[] sctlParams = new int[]{3, 1, 12, -804651004, 4, 5, 6, 9, -804650997, 17, 18, 33};
            VoiceServices.broadcastEvent(201000, 1, sctlParams);
            return true;
         case 1396982604:
            Runnable dcCallConnectRunner = new PhoneBackdoorHelpScreen$9(this);
            Application.getApplication().invokeLater(dcCallConnectRunner, 5000, false);
            this.exit();
            return true;
         case 1396984916:
            PhoneUtilities.toggleBooleanDebugFlag(-4404035883206732463L, "SDleft");
            return true;
         case 1396986446:
            PhoneUtilities.toggleBooleanDebugFlag(705071609734938371L, "set redirected num = ");
            return true;
         case 1397903686:
            this.exit();
            SimpleInputDialog dlg = new SimpleInputDialog(3, "enter SS error code");
            dlg.setModal(true);
            dlg.show();
            String error = dlg.getText();
            dlg.setPrompt("enter reason code");
            dlg.setText("0");
            dlg.show();
            String reason = dlg.getText();
            int errorCode = Integer.valueOf(error);
            int reasonCode = Integer.valueOf(reason);
            Application.getApplication().invokeLater(new PhoneBackdoorHelpScreen$4(this, errorCode, reasonCode));
            return true;
         case 1397966926:
            PhoneUtilities.toggleBooleanDebugFlag(5709883646465653063L, "SS testing hack = ");
            return true;
         case 1431524164:
            this.exit();
            Application.getApplication().invokeLater(new PhoneBackdoorHelpScreen$1(this), 5000, false);
            return true;
         case 1447187267:
            PhoneUtilities.toggleBooleanDebugFlag(2687054694005903237L, "verify before starting call = ");
            return true;
         default:
            return super.openDevelopmentBackdoor(backdoorCode);
      }

      return true;
   }

   @Override
   protected final boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1397245522:
            return super.openProductionBackdoor(backdoorCode);
         case 1397245523:
         default:
            PhoneUtilities.toggleBooleanDebugFlag(-1379267073835150941L, "show phone state = ");
            return true;
      }
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (c == 27) {
         this.exit();
      }

      return true;
   }

   private final void exit() {
      this._app.popScreen(this);
   }
}
