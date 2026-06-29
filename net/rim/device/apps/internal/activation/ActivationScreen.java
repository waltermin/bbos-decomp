package net.rim.device.apps.internal.activation;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.internal.provisioning.ActivationService;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.ui.component.IPEditField;

class ActivationScreen extends AppsMainScreen {
   private int _transactionID;
   private String _emailAddress;
   private ActivationService _activationService;
   private ActivationApp _app;
   private Thread _connWaiterThread;
   protected long _currentSid;
   public EmailAddressEditField _emailEditField;
   private PasswordEditField _passwordEditField;
   public IPEditField _activationServerField;
   private RichTextField _statusField;
   private ObjectChoiceField _pinField;
   private VerticalFieldManager _activationInputFields;
   private VerticalFieldManager _statusFieldManager;
   private VerticalFieldManager _mainManager;
   private StringBuffer _statusInformation = null;
   private Verb _activationVerb = new ActivationScreen$ActivationVerb(this);
   private Verb _hideVerb = new ActivationScreen$HideVerb(this);
   private Verb _cancelVerb = new ActivationScreen$CancelVerb(this);
   private boolean _statusDisplayed;
   String _uid;

   public ActivationScreen(ActivationApp app, String statusInfo, String email, String password) {
      super(0);
      if (statusInfo != null) {
         this._statusInformation = new StringBuffer(statusInfo);
      } else {
         this._statusInformation = new StringBuffer(0);
      }

      this._app = app;
      this.setTitle(new LabelField(ActivationApp.getApplicationTitle(true)));
      this._activationService = ActivationService.getInstance();
      OTAKeyGenEvent lastEvent = ((ActivationServiceImpl)this._activationService).getLastOTAKeyGenEvent();
      if (lastEvent != null) {
         this._transactionID = lastEvent._transactionId;
         this._emailAddress = lastEvent._emailAddress;
         if (this._emailAddress == null) {
            this._uid = lastEvent._serviceUID;
         }
      }

      this._mainManager = new VerticalFieldManager(64424509440L);
      this._statusField = new RichTextField();
      this._statusFieldManager = new VerticalFieldManager(64424509440L);
      this.updateStatus();
      this._pinField = new ObjectChoiceField(ActivationApp._resources.getString(147), new String[]{this.getPin()}, 0, 36028797018963968L);
      this._pinField.setEditable(false);
      OTASyncProgressHandler progressHandler = OTASyncProgressHandler.getInstance();
      if (progressHandler.isSyncInProgress()) {
         this._app.setCurrentState(4);
      } else if (this._emailAddress != null) {
         this._app.setCurrentState(2);
      } else if (this._uid != null) {
         this._app.setCurrentState(3);
      }

      if (this._app.getCurrentState() == 0) {
         this._activationInputFields = new VerticalFieldManager(12884901888L);
         this._emailEditField = new EmailAddressEditField(ActivationApp._resources.getString(102), email);
         this._passwordEditField = new PasswordEditField(ActivationApp._resources.getString(103), password, 31, 12884901888L);
         if (WLAN.isSupported()) {
            this._activationServerField = new IPEditField(ActivationApp._resources.getString(175), "");
         }

         if (email == null) {
            this._emailEditField.setText(((ActivationServiceImpl)this._activationService)._lastEmailAddress);
         }

         this._activationInputFields.add(this._emailEditField);
         this._activationInputFields.add(this._passwordEditField);
         if (this._activationServerField != null) {
            this._activationInputFields.add(this._activationServerField);
            this._activationServerField.setText(((ActivationServiceImpl)this._activationService)._lastActivationServerAddress);
         }

         this._activationInputFields.add(new SeparatorField());
         this._activationInputFields.add(this._pinField);
         this._activationInputFields.add(new SeparatorField());
         this._mainManager.add(this._activationInputFields);
      }

      if (email != null && password != null) {
         this._app.invokeLater((Runnable)this._activationVerb);
      }

      this._mainManager.add(this._statusFieldManager);
      this.add(this._mainManager);
      if (statusInfo == null || statusInfo.length() == 0) {
         this.refreshActivationStatus();
         this.updateStatus();
      }
   }

   void refreshActivationStatus() {
      ActivationApp eaApp = ActivationApp.getInstance();
      int status = eaApp == null ? 0 : eaApp.getCurrentState();
      if (this._statusInformation.length() == 0 && status == 0) {
         if (ActivationService.hasThisDeviceBeenActivated()) {
            this.buildActivationCompletedString(this._statusInformation);
         } else {
            this._statusInformation.append(ActivationApp._resources.getString(177));
         }

         this._statusDisplayed = true;
      }
   }

   private void buildActivationCompletedString(StringBuffer buffer) {
      long[] completedActivations = ActivationService.getActivatedServices();
      String date = "";
      String doubleLine = "\n\n";
      String colon = ": ";
      ServiceBook sb = ServiceBook.getSB();

      for (int i = completedActivations.length - 1; i >= 0; i--) {
         ServiceRecord sr = sb.getRecordByCidAndSid("sync", completedActivations[i]);
         if (sr != null) {
            if (buffer.length() != 0) {
               buffer.append(doubleLine);
            }

            buffer.append(sr.getName());
            buffer.append(colon);
         }

         date = DateFormat.getInstance(48).formatLocal(ActivationService.getLastSuccessfulActivationDate(completedActivations[i]));
         buffer.append(MessageFormat.format(ActivationApp._resources.getString(176), new String[]{date}));
      }
   }

   void clearActivationStatus() {
      if (this._statusDisplayed) {
         this._statusInformation.setLength(0);
         this._statusDisplayed = false;
      }
   }

   private void resetActivationFields() {
      this._app.setCurrentState(0);
      this._mainManager.deleteAll();
      this._mainManager.add(this._activationInputFields);
      this._mainManager.add(this._statusFieldManager);
      this._statusInformation.setLength(0);
      this._statusDisplayed = false;
      this.refreshActivationStatus();
      this.updateStatus();
   }

   private String getPin() {
      int pin = DeviceInfo.getDeviceId();
      String str = Integer.toHexString(pin);
      return StringUtilities.toUpperCase(str, 1701707776);
   }

   public void displayStatus(int reason, Object data) {
      String msg = null;
      switch (reason) {
         case 3840:
            if (this._emailAddress == null) {
               this._emailAddress = ((ActivationServiceImpl)this._activationService)._lastEmailAddress;
            }

            msg = MessageFormat.format(ActivationApp._resources.getString(110), new String[]{this._emailAddress});
            break;
         case 1094931521:
         case 1380272961:
            msg = ActivationApp._resources.getString(116);
            break;
         case 1094931539:
            msg = MessageFormat.format(ActivationApp._resources.getString(114), new String[]{this._emailAddress});
            break;
         case 1145853003:
            msg = "DLTK: ";
            break;
         case 1146311755:
            msg = "DSTK: ";
            break;
         case 1212240712:
            msg = "Hash: ";
            break;
         case 1262836041:
            msg = "KID: ";
            break;
         case 1262836054:
            msg = ActivationApp._resources.getString(123);
            break;
         case 1380272979:
            msg = ActivationApp._resources.getString(115);
            break;
         case 1381257817:
            msg = ActivationApp._resources.getString(117);
            break;
         case 1397511243:
            msg = "SLTK: ";
            break;
         case 1397969995:
            msg = "SSTK: ";
            break;
         case 1398166104:
            msg = ActivationApp._resources.getString(119);
            break;
         case 1464226646:
            msg = ActivationApp._resources.getString(118);
            break;
         default:
            msg = null;
      }

      if (data instanceof byte[]) {
         byte[] temp = (byte[])data;

         for (int i = 0; i < temp.length; i++) {
            int value = temp[i];
            if (value < 0) {
               value -= -256;
            }

            msg = msg + this.hex(value / 16);
            msg = msg + this.hex(value % 16);
            msg = msg + ' ';
         }
      }

      if (msg != null) {
         if (this._statusInformation.length() > 0) {
            this._statusInformation.append("\n");
         }

         this._statusInformation.append(msg);
         this.updateStatus();
      }
   }

   private char hex(int value) {
      return value < 10 ? (char)(value + 48) : (char)(value + 87);
   }

   public void displayMessage(int reason, Object param1) {
      String msg = null;
      boolean exitApp = false;
      boolean dialog = false;
      boolean deregisterRibbonIcon = false;
      boolean globalStatus = false;
      switch (reason) {
         case 7:
         case 3843:
            dialog = true;
            exitApp = true;
            deregisterRibbonIcon = false;
            msg = ActivationApp._resources.getString(144);
            break;
         case 8:
            msg = ActivationApp._resources.getString(127);
            dialog = true;
            exitApp = true;
            break;
         case 11:
            msg = ActivationApp._resources.getString(154);
            dialog = true;
            globalStatus = true;
            exitApp = true;
            break;
         case 12:
            dialog = true;
            msg = ActivationApp._resources.getString(158);
            exitApp = true;
            break;
         case 3841:
            dialog = true;
            exitApp = true;
            String castString = !(param1 instanceof String) ? "" : (String)param1;
            msg = MessageFormat.format(ActivationApp._resources.getString(109), new String[]{castString});
            break;
         case 3842:
         case 3851:
            dialog = true;
            exitApp = true;
            deregisterRibbonIcon = true;
            globalStatus = true;
            if (reason != 3851) {
               msg = ActivationApp._resources.getString(132);
               System.out.println("Activation Complete ID: 0x721e61cd5bb87ea2L");
            } else {
               msg = ActivationApp._resources.getString(156);
               System.out.println("Activation Complete with Errors ID: 0x6e15f667a0048abaL");
               long sid = -1;
               if (param1 instanceof Long) {
                  sid = (Long)param1;
               }

               String[] failedDatabases = OTASyncProgressHandler.getInstance().getIncompleteDatabases(sid);
               if (failedDatabases != null) {
                  for (int i = 0; i < failedDatabases.length; i++) {
                     msg = msg + "\r\n" + failedDatabases[i];
                  }
               }
            }
            break;
         case 3850:
            dialog = true;
            exitApp = true;
            deregisterRibbonIcon = false;
            msg = ActivationApp._resources.getString(151);
            break;
         case 1094931529:
            dialog = true;
            msg = ActivationApp._resources.getString(111);
            break;
         case 1263686990:
            dialog = true;
            msg = ActivationApp._resources.getString(149);
            break;
         case 1346981421:
            dialog = true;
            msg = ActivationApp._resources.getString(145);
            exitApp = true;
            break;
         case 1380669769:
            dialog = true;
            msg = ActivationApp._resources.getString(112);
            break;
         case 1397638213:
            msg = ActivationApp._resources.getString(174);
            dialog = true;
            break;
         case 1398166104:
            msg = ActivationApp._resources.getString(157);
            globalStatus = true;
            dialog = true;
            exitApp = false;
            break;
         default:
            msg = ActivationApp._resources.getString(107);
            dialog = true;
            exitApp = true;
      }

      if (msg == null) {
         throw new RuntimeException("Activation:MSG:" + reason);
      }

      if (this._statusInformation.length() > 0) {
         this._statusInformation.append("\n");
      }

      this._statusInformation.append(msg);
      this.updateStatus();
      if (dialog) {
         this._app.invokeLater(new ActivationScreen$ActivationMessageDialog(this, msg, exitApp, deregisterRibbonIcon, globalStatus));
      }
   }

   private void updateStatus() {
      if (this._statusInformation.length() > 0 && this._statusFieldManager.getFieldCount() == 0) {
         this._statusFieldManager.add(this._statusField);
      }

      if (this._statusInformation.length() == 0) {
         this._statusField.setText("");
         if (this._statusField.getManager() == this._statusFieldManager) {
            this._statusFieldManager.delete(this._statusField);
            return;
         }
      } else {
         this._statusField.setText(this._statusInformation.toString());
      }
   }

   String getStatusInfo() {
      return this._statusInformation.toString();
   }

   @Override
   public boolean onClose() {
      this.exitApp();
      return true;
   }

   @Override
   public void close() {
      Screen toClose = null;

      while ((toClose = this.getScreenAbove()) != null || (toClose = this.getScreenBelow()) != null) {
         toClose.close();
      }

      this._app.exitApp();
      super.close();
   }

   @Override
   protected boolean keyChar(char keyChar, int statusInt, int timeInt) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public boolean onMenu(int i) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   void exitApp() {
      OTASyncProgressHandler syncHandler = OTASyncProgressHandler.getInstance();
      if (this._app.getCurrentState() == 4 && (!syncHandler.isSyncInProgress() || syncHandler.isSyncInProgress() && syncHandler.getPercentComplete() == 100)) {
         this._app.setCurrentState(0);
      }

      if (this._app.getCurrentState() != 0) {
         UiApplication.getUiApplication().requestBackground();
      } else {
         this._statusInformation.setLength(0);
         ((ActivationServiceImpl)this._activationService).iconRefresh();

         try {
            this.close();
         } finally {
            return;
         }
      }
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this._app.getCurrentState() == 0 && this._emailEditField != null) {
         menu.add(this._activationVerb);
         menu.setDefault(this._activationVerb);
      } else {
         menu.add(this._hideVerb);
         menu.setDefault(this._hideVerb);
         if (this._app.getCurrentState() != 4 || OTASyncProgressHandler.getInstance().getPercentComplete() != 100) {
            if (this._app.getCurrentState() != 5) {
               menu.add(this._cancelVerb);
            }
         }
      }
   }

   private boolean validate(String email, String password) {
      if (email == null || email.length() == 0 || !EmailMessageUtilities.isEmailAddressFullyQualified(email)) {
         Dialog dialog = new Dialog(0, ActivationApp._resources.getString(129), 0, Bitmap.getPredefinedBitmap(0), 33554432);
         dialog.doModal();
         return false;
      }

      if (password != null && password.length() != 0) {
         return true;
      }

      Dialog dialog = new Dialog(0, ActivationApp._resources.getString(130), 0, Bitmap.getPredefinedBitmap(0), 33554432);
      dialog.doModal();
      return false;
   }

   @Override
   public boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1330336580:
            ((ActivationServiceImpl)this._activationService).setDebugMode(true);
            Dialog.alert("Debug Mode");
            return true;
         case 1464751699:
            NvStore.deleteData(18);
            NvStore.deleteData(19);
            NvStore.deleteData(20);
            Dialog.alert("Long Term Keys cleared");
            return true;
         default:
            return super.openDevelopmentBackdoor(backdoorCode);
      }
   }

   @Override
   public boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1129203270:
            return super.openProductionBackdoor(backdoorCode);
         case 1129203271:
         default:
            ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("sync");
            if (records != null && records.length != 0) {
               this._app.pushConfigScreen();
               return true;
            } else {
               Dialog.alert(ActivationApp._resources.getString(128));
               return true;
            }
      }
   }

   private boolean checkEmailAddressActive(String emailAddress) {
      boolean matched = true;
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("CMIME");
      int size = records != null ? records.length : 0;

      for (int i = 0; i < size; i++) {
         matched = false;
         String address = CMIMEUtilities.getEmailAddress(records[i]);
         if (emailAddress != null && address != null && StringUtilities.compareToIgnoreCase(emailAddress, address) == 0) {
            return true;
         }
      }

      return matched;
   }

   private void wipeHandheld(String email, String password) {
      ResourceBundle srb = ResourceBundle.getBundle(-1488627819050031640L, "net.rim.device.apps.internal.resource.Security");
      Dialog dialog = new Dialog(srb.getString(704), null, null, 0, Bitmap.getPredefinedBitmap(2));
      UiApplication.getUiApplication().pushGlobalScreen(dialog, -1073741823, 2);
      ActivationServiceImpl activationService = (ActivationServiceImpl)ActivationService.getInstance();
      activationService.storeDataBeforeDeviceWipe(email, password);
      Application app = Application.getApplication();
      app.invokeLater(new ActivationScreen$1(this));
   }
}
