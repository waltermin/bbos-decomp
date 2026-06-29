package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.ribbon.RibbonNetworkInfo;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

final class ActiveScreenONSField extends LabelField implements SystemListener, GlobalEventListener, Runnable {
   private boolean _listening;
   private boolean _batteryLow;
   private Application _app = Application.getApplication();

   public ActiveScreenONSField() {
      super(null, 1152921504606846980L);
      this._batteryLow = (DeviceInfo.getBatteryStatus() & 268435456) != 0;
      if (PhoneUtilities.smallScreen()) {
         FontFamily systemFamily = null;

         label36:
         try {
            systemFamily = FontFamily.forName(FontFamily.FAMILY_SYSTEM);
         } finally {
            break label36;
         }

         if (systemFamily != null) {
            this.setFont(systemFamily.getFont(0, 8, 3));
         } else {
            this.setFont(Font.getDefault().derive(0, 8, 3));
         }
      } else {
         this.setFont(Font.getDefault().derive(0, 8, 3));
      }

      this.setText(this.getONS());
   }

   private final String getONS() {
      if (!DeviceInfo.isSimulator()) {
         RibbonNetworkInfo ni = RibbonNetworkInfo.getInstance();
         if (ni != null) {
            String newONS = ni.getOperatorName();
            Out.p(((StringBuffer)(new Object("PHONE-ons="))).append(newONS).toString());
            return newONS;
         }
      }

      return "BlackBerry";
   }

   private final void updateStatusString() {
      if (this._batteryLow) {
         this.setText(CommonResources.getString(9060));
      } else {
         this.setText(this.getONS());
      }
   }

   private final void updateOnCorrectThread() {
      this._app.invokeLater(this);
   }

   @Override
   public final void run() {
      Out.p("PHONE-updating ONS");
      this.updateStatusString();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7219683504990287771L) {
         Out.p("PHONE-got ONS glbl evt");
         this._app.invokeLater(this);
      }
   }

   @Override
   public final void batteryLow() {
      this._batteryLow = true;
      this.updateOnCorrectThread();
   }

   @Override
   public final void batteryGood() {
      this._batteryLow = false;
      this.updateOnCorrectThread();
   }

   @Override
   public final void batteryStatusChange(int status) {
      this._batteryLow = (status & 268435456) != 0;
      this.updateOnCorrectThread();
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         if (!this._listening) {
            this._app.addSystemListener(this);
            this._app.addGlobalEventListener(this);
            this._listening = true;
            return;
         }
      } else {
         this._app.removeSystemListener(this);
         this._app.removeGlobalEventListener(this);
         this._listening = false;
      }
   }
}
