package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.ui.Keypad;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;

public class TodaySkinManager extends SkinManager {
   private ContentInteractorManager _contentInteractorManager;
   private FocusInteractor _focusInteractor;
   private static final String LAUNCH_INTERNAL;

   @Override
   public void setServices(MediaServices services) {
      if (services != null) {
         this._focusInteractor = (FocusInteractor)services.getService("FocusInteractor");
      }
   }

   @Override
   public void setMedia(Object media) {
      this._contentInteractorManager = ContentInteractorFactory.getFactory().createInstance(this._focusInteractor, (ModelInteractorImpl)media);
   }

   @Override
   public boolean load(String url) {
      if (url.toLowerCase().startsWith("launchinternal?")) {
         this._contentInteractorManager.launchInternal(url.substring(15));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      return Keypad.key(keycode) == 17 ? this._contentInteractorManager.makePhoneCall() : false;
   }
}
