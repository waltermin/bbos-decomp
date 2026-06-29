package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.Action;
import net.rim.device.apps.internal.phone.data.MissedCallIndicator;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class PhoneEntry extends Action {
   private Bitmap _icon = Bitmap.getBitmapResource("Phone28.gif");
   String _state = MissedCallIndicator.getInstance().isEnabled() ? "new" : null;
   boolean _isForeground;
   Object _extraInfo;

   PhoneEntry() {
      super(new ApplicationDescriptor(ApplicationDescriptor.currentApplicationDescriptor(), new String[]{"show"}), "net_rim_bb_phone_app.Phone", 20);
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      return propID == 5 ? this._icon : super.get(propID, defaultReturned);
   }

   @Override
   public final Boolean get(long propID, Boolean defaultReturned) {
      return defaultReturned;
   }

   @Override
   public final String get(long propID, String defaultReturned) {
      return super.get(propID, defaultReturned);
   }

   @Override
   public final void set(long propID, Object valueToSet) {
      if (propID == 12) {
         this.setExtraInfo(valueToSet);
      } else {
         super.set(propID, valueToSet);
      }
   }

   @Override
   public final void set(long propID, String valueToSet) {
      if (propID == 2) {
         if (!this._isForeground) {
            this.setState(valueToSet);
            return;
         }
      } else {
         super.set(propID, valueToSet);
      }
   }

   @Override
   protected final Object getExtraInfo() {
      return this._extraInfo;
   }

   final void setExtraInfo(Object info) {
      if (this._extraInfo != null || info != null) {
         this._extraInfo = info;
         this.update();
      }
   }

   @Override
   protected final String getDescription() {
      String option = ThemeManager.getActiveTheme().getOption("ModifyPhoneApplicationTitleOnMissedCalls");
      return option != null && option.startsWith("t") && MissedCallIndicator.getInstance().isEnabled()
         ? PhoneResources.getString(6243)
         : PhoneResources.getString(0);
   }

   @Override
   protected final String getState() {
      return this._state;
   }

   final void setState(String state) {
      if (this._state != null || state != null) {
         if (this._state == null || !this._state.equals(state)) {
            this._state = state;
            this.update();
         }
      }
   }

   @Override
   public final void run() {
      VoiceServices.getVoiceApplication().requestForeground(null, null);
   }
}
