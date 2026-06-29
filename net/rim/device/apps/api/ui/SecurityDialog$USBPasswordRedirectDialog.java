package net.rim.device.apps.api.ui;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.USBPasswordRedirectManager;
import net.rim.device.internal.system.USBPortInternal$Internal;
import net.rim.device.internal.ui.component.SimplePasswordDialog;

public class SecurityDialog$USBPasswordRedirectDialog extends SimplePasswordDialog implements GlobalEventListener, SystemListener2 {
   private Application _app;
   private Vector _choices;
   private Vector _channels;
   private USBPasswordRedirectManager _redirectManager;
   public static final int DISMISS = 1;

   public SecurityDialog$USBPasswordRedirectDialog() {
      super(null, 1, 32, false, 0);
      this.setStatusPriority(-2147483644);
      this._choices = new Vector();
      this._redirectManager = USBPasswordRedirectManager.getInstance();
      this._app = Application.getApplication();
      this._app.addGlobalEventListener(this);
      this._app.addSystemListener(this);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void close(int closeReason) {
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         if (closeReason == -1) {
            USBPasswordRedirectManager.logEvent(1130458723);
            this._redirectManager.clearChannels(false);
         } else if (closeReason == 1) {
            USBPasswordRedirectManager.logEvent(1147761517);
         }

         this._app.removeGlobalEventListener(this);
         this._app.removeSystemListener(this);
         super.close(closeReason);
         var4 = false;
      } finally {
         if (var4) {
            this.setText(null);
         }
      }

      this.setText(null);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void setMessage(String deviceAttempts) {
      this._channels = this._redirectManager.getAllChannels();
      this.setPrompt(CommonResource.getString(10180));
      boolean var6 = false /* VF: Semaphore variable */;

      label43:
      try {
         var6 = true;

         for (int ioe = 0; ioe < this._channels.size(); ioe++) {
            String usbPeripheralName = USBPortInternal$Internal.getChannelName((Integer)this._channels.elementAt(ioe));
            if (usbPeripheralName.equals("RIM Bypass")) {
               usbPeripheralName = CommonResource.getString(10183);
            } else if (usbPeripheralName.equals("RIM Desktop")) {
               usbPeripheralName = CommonResource.getString(10181);
            }

            CheckboxField checkboxField = new CheckboxField(usbPeripheralName, true);
            this.add(checkboxField);
            this._choices.addElement(checkboxField);
         }

         var6 = false;
      } finally {
         if (var6) {
            USBPasswordRedirectManager.logEvent(1195593285);
            break label43;
         }
      }

      this.add(new RichTextField(CommonResources.getString(2012) + deviceAttempts, 36028797018963968L));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public int[] getSelectedChannels() {
      Vector selectedChannels = new Vector();

      for (int i = 0; i < this._channels.size(); i++) {
         if (((CheckboxField)this._choices.elementAt(i)).getChecked()) {
            selectedChannels.addElement(this._channels.elementAt(i));
         } else {
            this._redirectManager.allowChannel((Integer)this._channels.elementAt(i), false);
            boolean var5 = false /* VF: Semaphore variable */;

            try {
               var5 = true;
               this._redirectManager.addToDisallowedChannels(USBPortInternal$Internal.getChannelName((Integer)this._channels.elementAt(i)));
               var5 = false;
            } finally {
               if (var5) {
                  USBPasswordRedirectManager.logEvent(1195593285);
                  continue;
               }
            }
         }
      }

      int[] selectedChannelsInt = new int[selectedChannels.size()];

      for (int i = 0; i < selectedChannels.size(); i++) {
         selectedChannelsInt[i] = (Integer)selectedChannels.elementAt(i);
      }

      return selectedChannelsInt;
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      if (this.getLeafFieldWithFocus() instanceof CheckboxField) {
         if (((CheckboxField)this.getLeafFieldWithFocus()).getChecked()) {
            ((CheckboxField)this.getLeafFieldWithFocus()).setChecked(false);
            return true;
         } else {
            ((CheckboxField)this.getLeafFieldWithFocus()).setChecked(true);
            return true;
         }
      } else {
         return super.navigationClick(status, time);
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6345609069135580235L) {
         this.closeDialogLater();
      }
   }

   private void closeDialogLater() {
      this._app.invokeLater(new SecurityDialog$USBPasswordRedirectDialog$1(this));
   }

   @Override
   public void usbConnectionStateChange(int state) {
      if (state == 4) {
         this.closeDialogLater();
      }
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
   public void fastReset() {
   }

   @Override
   public void backlightStateChange(boolean on) {
   }
}
