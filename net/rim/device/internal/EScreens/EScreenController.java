package net.rim.device.internal.EScreens;

import net.rim.device.api.system.DeviceInternal;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.system.EngineeringDataListener;
import net.rim.vm.Array;

public class EScreenController implements EngineeringDataListener, Runnable, SystemListener2, EScreenRoot {
   private EScreenModel _model;
   private EScreenUI _view;
   private UiApplication _app = UiApplication.getUiApplication();
   private EScreenController$PingSelfAction _selfPinger = new EScreenController$PingSelfAction(this._app);
   private int[] _screenIdStack = new int[0];
   private int[] _screenIdCookieStack = new int[0];
   private int[] _screenItemIndexStack = new int[0];
   private int _invokeLaterId = -1;
   private boolean _processEngUpdates = true;
   private int _msgsPerQuanta;
   private EScreenSessionManager _sessionManager;
   private static final boolean DEBUG_QUANTA = false;
   private static final int QUANTA_LENGTH = 1500;
   private static final int QUANTA_MSG_HWM = 5;

   public void doAction(int actionId, int idCookie, byte[] userData) {
      if ((actionId & -65536) == 0) {
         switch (actionId) {
            case 0:
               Dialog.alert(((StringBuffer)(new Object("Invalid actionId="))).append(actionId).toString());
               return;
            case 1:
            default:
               if (RadioInfo.getState() == 0) {
                  Radio.requestPowerOn();
                  Dialog.alert("Radio Activated");
                  return;
               }

               Radio.requestPowerOff();
               Dialog.alert("Radio Deactivated");
               return;
            case 2:
               if (!DeviceInternal.requestPowerOff(true)) {
                  Dialog.alert("Unable to power off");
                  return;
               }
               break;
            case 3:
               this._selfPinger.go(0, 5000, 4);
               return;
         }
      } else {
         EScreen.doAction(actionId, idCookie, userData);
      }
   }

   public void refresh(boolean automatic) {
      if (this._app.getActiveScreen() == this._view && (!automatic || (this._model.getScreenFlags() & 1) == 0)) {
         this._model.refresh();
         this._view.refresh();
      }
   }

   public void pushScreen(int screenId, int idCookie) {
      int index = this._view.getItemIndex();
      if ((screenId & -65536) == 0) {
         switch (screenId) {
            case 0:
               Dialog.alert(((StringBuffer)(new Object("Invalid screenId="))).append(screenId).toString());
               return;
            case 1:
               break;
            case 2:
            default:
               this._app.pushScreen(this._sessionManager);
               return;
            case 3:
               EventLogger.startEventLogViewer();
               return;
            case 4:
               this._app.pushScreen(new EScreenVMStats(this._view.getFont()));
               return;
            case 5:
               this._app.pushScreen(new EScreenGraphicsInfo(this._view.getFont()));
               return;
         }
      }

      if (this._screenItemIndexStack.length != 0) {
         this._screenItemIndexStack[this._screenItemIndexStack.length - 1] = index;
      }

      Arrays.add(this._screenIdStack, screenId);
      Arrays.add(this._screenIdCookieStack, idCookie);
      Arrays.add(this._screenItemIndexStack, 0);
      if (this._app.getActiveScreen() != this._view) {
         this._app.pushScreen(this._view);
      }

      try {
         this._model.setScreen(screenId, idCookie);
         this._view.reset();
      } catch (EScreenException e) {
         Dialog.alert(
            ((StringBuffer)(new Object("Unable to set screen, code=")))
               .append(e.getCode())
               .append(".\nscreenId=")
               .append(screenId)
               .append(" idCookie=")
               .append(idCookie)
               .toString()
         );
         this.popScreen();
      }
   }

   public void popScreen() {
      Screen scr = this._app.getActiveScreen();
      if (scr != this._view) {
         this._app.popScreen(scr);
      } else {
         while (true) {
            int length = this._screenIdStack.length - 1;
            Array.resize(this._screenIdStack, length);
            Array.resize(this._screenIdCookieStack, length);
            Array.resize(this._screenItemIndexStack, length);
            if (length == 0) {
               this._app.popScreen(scr);
               break;
            }

            int screenId = this._screenIdStack[length - 1];
            int idCookie = this._screenIdCookieStack[length - 1];
            int itemIndex = this._screenItemIndexStack[length - 1];

            try {
               this._model.setScreen(screenId, idCookie);
               this._view.reset();
               this._view.setItemIndex(itemIndex);
               break;
            } catch (EScreenException e) {
               Dialog.alert(
                  ((StringBuffer)(new Object("Unable to popScreen, code=")))
                     .append(e.getCode())
                     .append(".\n screenId=")
                     .append(screenId)
                     .append(" idCookie=")
                     .append(idCookie)
                     .toString()
               );
            }
         }
      }

      if (this._app.getScreenCount() == 0) {
         this.shutdown();
      }
   }

   public void shutdown() {
      try {
         EScreen.setActive(false);
      } catch (EScreenException var2) {
      }

      if (this._invokeLaterId != -1) {
         this._app.cancelInvokeLater(this._invokeLaterId);
         this._invokeLaterId = -1;
      }

      System.exit(0);
   }

   @Override
   public void startupEScreens() {
      this._invokeLaterId = this._app.invokeLater(this, 1500, true);
      this._app.addRadioListener(this);
      this._app.addSystemListener(this);
      this.pushScreen(1, 0);
      EScreen.setActive(true);
   }

   @Override
   public void initEScreens(int accessLevel) {
      this._model = new EScreenModel(accessLevel);
      this._view = new EScreenUI(this._model, this);
      this._sessionManager = new EScreenSessionManager(this._view.getFont());
   }

   @Override
   public void run() {
      if (this._msgsPerQuanta == 0 || !this._processEngUpdates) {
         this.refresh(true);
      }

      this._processEngUpdates = this._msgsPerQuanta < 5;
      this._msgsPerQuanta = 0;
   }

   @Override
   public void engResponseMasterReset(int type) {
   }

   @Override
   public void engServiceProgramEvent(int type) {
   }

   @Override
   public void engDataInitialized() {
   }

   @Override
   public void engDataChanged() {
      this._msgsPerQuanta++;
      if (this._processEngUpdates) {
         this.refresh(true);
         this._processEngUpdates = this._msgsPerQuanta < 5;
      }
   }

   @Override
   public void engDataLogworthy(int type) {
   }

   @Override
   public void engOTASPResponse(byte[] response) {
   }

   @Override
   public void powerOff() {
   }

   @Override
   public void powerUp() {
   }

   @Override
   public void batteryLow() {
   }

   @Override
   public void batteryGood() {
   }

   @Override
   public void batteryStatusChange(int status) {
   }

   @Override
   public void powerOffRequested(int reason) {
   }

   @Override
   public void cradleMismatch(boolean mismatch) {
   }

   @Override
   public void backlightStateChange(boolean on) {
   }

   @Override
   public void usbConnectionStateChange(int state) {
   }

   @Override
   public void fastReset() {
      EScreen.setActive(true);
      this.refresh(false);
   }
}
