package net.rim.device.apps.internal.ribbon.system;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.component.PopupDialogClosedListener;

final class AutoOffDialog extends PopupDialog implements Runnable, PopupDialogClosedListener, SystemListener, HolsterListener {
   private boolean _allowAutoOn;
   private boolean _forceAutoOn;
   private Field _pressAnyKeyField;
   private boolean _dontShutDown;
   private ButtonField _autoOff;
   private long _onTimeLong;
   private int _type;
   private UiApplication _app;
   static final int BATTERY_DRAINED_DIALOG;
   static final int AUTO_OFF_DIALOG_WITH_CANCEL;
   static final int POWER_OFF_DIALOG_WITH_OPTIONS;
   static final int POWER_OFF_DIALOG_WITH_CANCEL;
   static final int NO_AUTO_ON_TIME_DIALOG;
   private static final long USER_REQUESTED_OFF_PROMPT_TIME;
   private static final long AUTO_OFF_PROMPT_TIME;
   private static final long BATTERY_DEAD_OFF_PROMPT_TIME;

   AutoOffDialog(UiApplication app, int type) {
      super((Manager)(new Object()), 33554432);
      RadioOffWarningManagerImpl.fire(1);
      this._forceAutoOn = Security.getInstance().isAutoOnRequired();
      Font font = Font.getDefault();
      if (Font.getDefaultHeight(2) > 11) {
         this.setFont(font.derive(font.getStyle(), 11, 2));
      } else {
         this.setFont(font);
      }

      this.setPopupDialogClosedListener(this);
      this.setStatusPriority(-2147483647);
      this._onTimeLong = ApplicationManager.getApplicationManager().getNextAlarm(1);
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setIcon((BitmapField)(new Object(Bitmap.getPredefinedBitmap(2), 51539607552L)));
      this.setDialogType(type);
      this._app = app;
      this._app.addSystemListener(this);
      this._app.addHolsterListener(this);
   }

   private final void setDialogType(int type) {
      long time = 0;
      boolean cancel = false;
      ResourceBundleFamily resources = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
      this._type = type;
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      Manager customManager = dfm.getCustomManager();
      customManager.deleteAll();
      if ((this._type == 2 || this._type == 1 || this._type == 3) && this._onTimeLong == Long.MAX_VALUE) {
         this._type = 4;
      }

      if (this._type == 2 && this._forceAutoOn) {
         this._type = 3;
      }

      String msg;
      switch (this._type) {
         case -1:
            this._allowAutoOn = false;
            cancel = true;
            msg = resources.getString(40);
            time = 2000;
            break;
         case 0:
            this._allowAutoOn = false;
            cancel = false;
            msg = resources.getString(52);
            time = 5000;
            break;
         case 1:
         case 3: {
            Calendar calOn = this.getOnTimeCalendar();
            dfm.addCustomField((Field)(new Object(resources.getString(41), 12884901888L)));
            dfm.addCustomField((Field)(new Object(DateFormat.getInstance(48).format(calOn), 12884901888L)));
            dfm.addCustomField((Field)(new Object(DateFormat.getInstance(6).format(calOn), 12884901888L)));
            this._allowAutoOn = true;
            cancel = true;
            msg = resources.getString(40);
            time = this._type == 1 ? 5000 : 2000;
            break;
         }
         case 2:
         default: {
            this._allowAutoOn = false;
            cancel = false;
            Calendar calOn = this.getOnTimeCalendar();
            msg = ((StringBuffer)(new Object()))
               .append(resources.getString(68))
               .append(DateFormat.getInstance(48).format(calOn))
               .append(" ")
               .append(DateFormat.getInstance(6).format(calOn))
               .toString();
            dfm.addCustomField(this._autoOff = (ButtonField)(new Object(resources.getString(77), 12884901888L)));
            dfm.addCustomField((Field)(new Object(resources.getString(75), 12884901888L)));
         }
      }

      dfm.setMessage((RichTextField)(new Object(msg, 36028797018963968L)));
      if (cancel) {
         dfm.addCustomField((Field)(new Object("")));
         this._pressAnyKeyField = (Field)(new Object(resources.getString(42), 36028797019226112L));
         dfm.addCustomField(this._pressAnyKeyField);
      }

      if (time != 0) {
         Application.getApplication().invokeLater(this, time, false);
      }
   }

   private final Calendar getOnTimeCalendar() {
      Calendar calOn = Calendar.getInstance();
      ((CalendarExtensions)calOn).setTimeLong(this._onTimeLong);
      calOn.setTimeZone(TimeZone.getDefault());
      return calOn;
   }

   private final void displaySplashScreen() {
      SystemOnOffManager systemOnOffManager = SystemOnOffManager.getInstance();
      if (!systemOnOffManager.getIsPoweringOff()) {
         systemOnOffManager.setIsPoweringOff(true);
         Runnable runWhenDone = new AutoOffDialog$1(this);
         if (SystemOnOffManager.showSplashScreen(runWhenDone, true, true, false)) {
            this.close(0);
            this._app.removeSystemListener(this);
         }
      }
   }

   @Override
   public final void run() {
      if (!this._dontShutDown) {
         DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
         if (this._pressAnyKeyField != null) {
            dfm.getCustomManager().delete(this._pressAnyKeyField);
            this._pressAnyKeyField = null;
         }

         this.displaySplashScreen();
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (this._pressAnyKeyField != null) {
         this.close(-1);
         return true;
      }

      if (this._type != 0) {
         if (Keypad.key(keycode) == 10) {
            if (this.getLeafFieldWithFocus() == this._autoOff) {
               this.setDialogType(3);
               return true;
            }

            this.setDialogType(4);
            return true;
         }

         if (Keypad.key(keycode) == 27) {
            this.close(-1);
         }
      }

      return true;
   }

   @Override
   protected final boolean keyStatus(int keycode, int time) {
      if (this._pressAnyKeyField != null) {
         this.close(-1);
      }

      return true;
   }

   @Override
   protected final boolean keyControl(char c, int status, int time) {
      if (this._pressAnyKeyField == null || c != 150 && c != 151) {
         return super.keyControl(c, status, time);
      }

      this.close(-1);
      return true;
   }

   private final void handleInvokeAction() {
      if (this._pressAnyKeyField != null) {
         this.close(-1);
      } else {
         if (this._type != 0) {
            if (this.getLeafFieldWithFocus() == this._autoOff) {
               this.setDialogType(3);
               return;
            }

            this.setDialogType(4);
         }
      }
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      this.handleInvokeAction();
      return true;
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled && action == 1) {
         this.handleInvokeAction();
         return true;
      } else {
         return handled;
      }
   }

   @Override
   public final void dialogClosed(PopupDialog dialog, int closeReason) {
      if (closeReason == -1) {
         RadioOffWarningManagerImpl.fire(2);
         this._dontShutDown = true;
      }

      this._app.removeSystemListener(this);
      this._app.removeHolsterListener(this);
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerOff() {
      try {
         this.close(0);
      } finally {
         return;
      }
   }

   @Override
   public final void powerUp() {
      try {
         this._app.removeSystemListener(this);
         this._app.removeHolsterListener(this);
         this.close(0);
      } finally {
         return;
      }
   }

   @Override
   public final void inHolster() {
      if (this._pressAnyKeyField != null) {
         this.close(-1);
      }
   }

   @Override
   public final void outOfHolster() {
   }
}
