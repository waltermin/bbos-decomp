package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.internal.ui.component.PopupDialog;

class BaseVolumeGaugePopup extends PopupDialog implements PhoneEventListener, Runnable {
   private int _originalLevel;
   private AudioRouter _router;

   public BaseVolumeGaugePopup(Manager mgr) {
      super(mgr);
      this.setId("phonevolume");
      this.setModal(false);
      this._router = AudioRouter.getInstance();
   }

   @Override
   public void show() {
      this._originalLevel = this._router.getMasterVolume();
      VoiceServices.addPhoneEventListener(this);
      UiApplication.getUiApplication().pushGlobalScreen(this, -2147483645, 2);
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      if (!DeviceInfo.isInHolster()) {
         this._router.incrementMasterVolume(-10 * dy);
         return true;
      } else {
         return super.navigationMovement(dx, dy, status, time);
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean handled = false;
      switch (key) {
         case '\u001b':
            this._router.setMasterVolume(this._originalLevel);
         case '\n':
            this.closeOnOwnerThread();
            handled = true;
         default:
            if (!handled) {
               super.keyChar(key, status, time);
            }

            return handled;
      }
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      this.closeOnOwnerThread();
      return true;
   }

   @Override
   public void phoneEventNotify(int eventId, int param1, Object param2) {
      switch (eventId) {
         case 1002:
         case 100300:
         case 201010:
            this.closeOnOwnerThread();
      }
   }

   @Override
   protected void close(int closeReason) {
      UiApplication.getUiApplication().popScreen(this);
   }

   private void closeOnOwnerThread() {
      VoiceServices.removePhoneEventListener(this);
      ((Application)this.getOwner()).invokeLater(this);
   }

   @Override
   public void run() {
      if (Application.getApplication() != this.getOwner()) {
         throw new Object("Trying to close volume screen on the wrong application thread!");
      }

      this.close(0);
   }
}
