package net.rim.device.apps.internal.mms.ui;

import javax.microedition.media.control.VolumeControl;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.ui.menu.MenuScreen;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.api.utility.framework.ModelScreen$NotificationRunnable;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.vm.PersistentInteger;

public class MMSModelScreen extends ModelScreen implements HolsterListener, SystemListener {
   private MMSMessageModel _message;
   private VolumeControl _volumeControl;
   private static final long PREVIOUS_VOLUME_GUID = -5967616738637215101L;
   private static int _previousVolumeId = PersistentInteger.getId(-5967616738637215101L, 50);

   public MMSModelScreen(Object context) {
      super(0, null, ContextObject.clone(context));
      if (super._context instanceof ContextObject) {
         ContextObject contextObject = (ContextObject)super._context;
         contextObject.setFlag(15);
         contextObject.put(244, new Integer(26274));
      }
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached) {
         super.onUiEngineAttached(attached);
         ModelViewListenerRegistry.notifyModelOpened(this, super._context);
         super._application.addHolsterListener(this);
         super._application.addSystemListener(this);
      } else {
         ModelViewListenerRegistry.notifyModelClosed(this, super._context);
         super._model = null;
         super._application.removeHolsterListener(this);
         super._application.removeSystemListener(this);
         super.onUiEngineAttached(attached);
      }
   }

   @Override
   protected void onVisibilityChange(boolean visible) {
      if (visible && this.getFieldCount() == 0) {
         this.populateScreen();
         this.setScrollFocus();
      }

      super.onVisibilityChange(visible);
   }

   @Override
   public void setModel(Object message) {
      super.setModel(message);
      this._message = (MMSMessageModel)message;
      this.deleteAll();
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         this.populateScreen();
         if (this.isDisplayed()) {
            this.setScrollFocus();
         }
      }

      this.setSecurityServiceColours(false);
   }

   protected final MMSMessageModel getMessageModel() {
      return this._message;
   }

   protected void populateScreen() {
      throw null;
   }

   @Override
   protected final Object invokeVerb(Verb verb, Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      contextObject.put(-7651695713744129224L, this._message);
      contextObject.setFlag(74);
      contextObject.setFlag(40);
      return super.invokeVerb(verb, contextObject);
   }

   public void setVolumeControl(VolumeControl volumeControl) {
      this._volumeControl = volumeControl;
      if (volumeControl != null) {
         volumeControl.setLevel(PersistentInteger.get(_previousVolumeId));
      }
   }

   @Override
   public int adjustVolume(int volumeLevelChange) {
      VolumeControl control = this._volumeControl;
      if (control != null) {
         int level = MathUtilities.clamp(0, control.getLevel() + volumeLevelChange * 11, 100);
         control.setLevel(level);
         PersistentInteger.set(_previousVolumeId, level);
         return level;
      } else {
         return super.adjustVolume(volumeLevelChange);
      }
   }

   @Override
   protected boolean keyClickedAndReleased(char key, int status, int time) {
      VolumeControl control = this._volumeControl;
      if (control != null) {
         if (!Character.isDigit(key)) {
            key = Keypad.getAltedChar(key);
         }

         if (key >= '1' && key <= '9') {
            int level = (key - '0') * 11;
            control.setLevel(level);
            PersistentInteger.set(_previousVolumeId, level);
            return true;
         }
      }

      return super.keyClickedAndReleased(key, status, time);
   }

   private void setScrollFocus() {
      Field topField = this.getInitialTopField();
      Manager mgr = topField.getManager();
      if (mgr.isStyle(281474976710656L)) {
         mgr.setVerticalScroll(topField.getTop());
      }
   }

   protected Field getInitialTopField() {
      Field fieldWithFocus = this.getFieldWithFocus();
      if (!(fieldWithFocus instanceof MMSViewField)) {
         return fieldWithFocus;
      }

      MMSViewField viewField = (MMSViewField)fieldWithFocus;
      return viewField.getInitialTopField();
   }

   protected final void closeMessage() {
      Screen activeScreen = UiApplication.getUiApplication().getActiveScreen();
      if (activeScreen instanceof MenuScreen) {
         UiApplication.getUiApplication().popScreen(activeScreen);
         activeScreen = UiApplication.getUiApplication().getActiveScreen();
      }

      if (activeScreen == this) {
         UiApplication.getUiApplication().popScreen(this);
      }
   }

   protected final boolean isOnHyperlink() {
      Field focusedField = this.getFieldWithFocus();
      if (focusedField instanceof CookieProvider) {
         Object cookie = CookieProviderUtilities.getDefaultCookie(((CookieProvider)focusedField).getCookieWithFocus());
         if (cookie instanceof RIMModel) {
            return true;
         }
      }

      focusedField = this.getLeafFieldWithFocus();
      if (focusedField instanceof CookieProvider) {
         Object cookie = CookieProviderUtilities.getDefaultCookie(((CookieProvider)focusedField).getCookieWithFocus());
         if (cookie instanceof RIMModel) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7131874474196788121L) {
         this.globalEventSystemLocked();
      } else if (guid == 6345609069135580235L) {
         this.globalEventSystemUnlocked();
      }

      if (object0 == this._message) {
         if (guid == 1202366544244619460L) {
            this.globalEventDismissViewerOfObject();
            return;
         }

         if (guid == -6275418955626563374L) {
            this.globalEventMarkViewedItemAsOpen();
            return;
         }
      }

      super.eventOccurred(guid, data0, data1, object0, object1);
   }

   @Override
   public void notifyOfOpenedModelChange(RIMModel oldModel, RIMModel newModel, Object moreContext) {
      if (this._message == oldModel && newModel instanceof MMSMessageModel) {
         if (super._application != Application.getApplication() || !Application.isEventDispatchThread()) {
            super._application.invokeLater(new ModelScreen$NotificationRunnable(this, oldModel, newModel, moreContext));
            return;
         }

         this.setModel(newModel);
      }
   }

   protected void globalEventDismissViewerOfObject() {
   }

   protected void globalEventSystemLocked() {
   }

   protected void globalEventSystemUnlocked() {
   }

   protected void globalEventMarkViewedItemAsOpen() {
   }

   @Override
   public void inHolster() {
   }

   @Override
   public void outOfHolster() {
   }

   @Override
   public void batteryGood() {
   }

   @Override
   public void batteryLow() {
   }

   @Override
   public void batteryStatusChange(int status) {
   }

   @Override
   public void powerOff() {
   }

   @Override
   public void powerUp() {
   }
}
