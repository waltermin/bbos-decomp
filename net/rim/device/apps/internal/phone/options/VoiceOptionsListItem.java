package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.ui.ScreenPopper;

class VoiceOptionsListItem extends SaveableMainScreenOptionsListItem {
   protected PhoneOptions _phoneOptions = PhoneOptions.getOptions();
   private ScreenPopper _screenPopper;

   public VoiceOptionsListItem(String title, Object context) {
      super(title);
      this._screenPopper = (ScreenPopper)ContextObject.get(context, -116504832846522962L);
   }

   public VoiceOptionsListItem(ResourceBundleFamily rb, int key, Object context) {
      super(rb, key);
      this._screenPopper = (ScreenPopper)ContextObject.get(context, -116504832846522962L);
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      boolean removeObjects = false;
      if (super._context instanceof Object && this._screenPopper != null) {
         ContextObject.put(super._context, -116504832846522962L, this._screenPopper);
         ContextObject.put(super._context, -1540107978048774676L, this);
         removeObjects = true;
      }

      boolean result = super.trackwheelClick(status, time);
      if (removeObjects) {
         ContextObject.remove(super._context, -116504832846522962L);
         ContextObject.remove(super._context, -1540107978048774676L);
      }

      return result;
   }

   protected MainScreen createNewMainScreen() {
      return new PhoneOptionsListItemScreen(this);
   }

   @Override
   protected MainScreen createMainScreen() {
      MainScreen mainScreen = this.createNewMainScreen();
      mainScreen.setTitle(this.getTitleField());
      this.populateMainScreen(mainScreen);
      mainScreen.addStylusListener(this);
      mainScreen.addTrackwheelListener(this);
      mainScreen.addKeyListener(this);
      return mainScreen;
   }

   void optionsUpdated() {
      this.populateMainScreen(super._mainScreen);
   }

   @Override
   protected void populateMainScreen(MainScreen mainScreen) {
      if (PhoneUtilities.getAllLineIds().length > 1) {
         mainScreen.add(
            (Field)(new Object(
               ((StringBuffer)(new Object()))
                  .append(PhoneUtilities.getLineDescription())
                  .append(" (")
                  .append(PhoneUtilities.getDevicePhoneNumber())
                  .append(")")
                  .toString()
            ))
         );
         mainScreen.add((Field)(new Object()));
      }
   }
}
