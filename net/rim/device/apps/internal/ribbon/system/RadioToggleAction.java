package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.apps.api.ribbon.Action;

final class RadioToggleAction extends Action {
   private Bitmap _onIcon = Bitmap.getBitmapResource("radioon.gif");
   private Bitmap _offIcon = Bitmap.getBitmapResource("radiooff.gif");
   private ApplicationDescriptor _radioOnApplicationDescriptor;
   private ApplicationDescriptor _radioOffApplicationDescriptor;
   private boolean _currentActionIsTurnRadioOn;
   private ResourceBundleFamily _rbf;
   private static String RADIO_TOGGLE_ENTRY_NAME = "net_rim_Radio";
   private static String STATE_OFF = "off";
   private static String STATE_ON = "on";
   private static final String RADIO_ON_BITMAP_NAME = "radioon.gif";
   private static final String RADIO_OFF_BITMAP_NAME = "radiooff.gif";

   RadioToggleAction(ApplicationDescriptor radioOnApplicationDescriptor, ApplicationDescriptor radioOffApplicationDescriptor, boolean userCanTurnRadioOn) {
      super(null, RADIO_TOGGLE_ENTRY_NAME, 240);
      this._radioOffApplicationDescriptor = radioOffApplicationDescriptor;
      this._radioOnApplicationDescriptor = radioOnApplicationDescriptor;
      this._rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
      this.updateActions(userCanTurnRadioOn);
   }

   final synchronized boolean updateActions(boolean userCanTurnRadioOn) {
      if (this._currentActionIsTurnRadioOn != userCanTurnRadioOn) {
         this._currentActionIsTurnRadioOn = userCanTurnRadioOn;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      if (propID == 5) {
         return this._currentActionIsTurnRadioOn ? this._onIcon : this._offIcon;
      } else {
         return defaultReturned;
      }
   }

   @Override
   public final String get(long propID, String defaultReturned) {
      return super.get(propID, defaultReturned);
   }

   @Override
   public final Boolean get(long propID, Boolean defaultReturned) {
      return propID == 7 ? Boolean.FALSE : defaultReturned;
   }

   @Override
   protected final String getDescription() {
      return this._rbf.getString(this._currentActionIsTurnRadioOn ? 61 : 2);
   }

   @Override
   protected final String getState() {
      return this._currentActionIsTurnRadioOn ? STATE_OFF : STATE_ON;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      try {
         ApplicationManager manager = ApplicationManager.getApplicationManager();
         if (this._currentActionIsTurnRadioOn) {
            if ((DeviceInfo.getBatteryStatus() & 16384) != 0) {
               SystemOnOffManager.showNoRadioMessage();
            } else {
               manager.runApplication(this._radioOnApplicationDescriptor);
            }
         } else {
            manager.runApplication(this._radioOffApplicationDescriptor);
         }
      } catch (Throwable var3) {
         throw new Object(e.getMessage());
      }
   }
}
