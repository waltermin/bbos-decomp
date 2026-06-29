package net.rim.device.apps.internal.phone.api.ui;

import java.util.Vector;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;

public class PhoneAwareScreen extends AppsMainScreen implements PhoneEventListener {
   protected Verb _closeVerb;
   protected UiApplication _app;

   public void onAppActivated(Object context, char key) {
   }

   public void show() {
      this._app.pushScreen(this);
   }

   public void shutDown() {
   }

   protected boolean isActiveMode(Vector currentCalls) {
      if (currentCalls != null && currentCalls.size() > 0) {
         return true;
      } else {
         int state = VoiceServices.getPhoneState();
         if (state != 0 && state != 1 && state != 6) {
            System.out.println(((StringBuffer)(new Object("Kept active mode due to state = "))).append(state).toString());
            return true;
         } else {
            return false;
         }
      }
   }

   protected UiApplication getApp() {
      return this._app;
   }

   protected void refreshOnCorrectThread(Runnable runnable) {
      this._app.invokeLater(runnable);
   }

   protected void startListening() {
      VoiceServices.addPhoneEventListener(this);
   }

   public void stopListening() {
      VoiceServices.removePhoneEventListener(this);
   }

   protected void setCloseVerb(Verb closeVerb) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected void onEvent(int _1, int _2, Object _3) {
      throw null;
   }

   protected Menu getMenu() {
      return new PhoneAppMenu();
   }

   protected void loadCloseVerb(SystemEnabledMenu menu, int instance) {
      if (instance == 0) {
         if (menu != null && !ApplicationManager.getApplicationManager().isSystemLocked() && this._closeVerb != null) {
            menu.add(this._closeVerb);
         }
      }
   }

   protected void loadScreenVerbs(SystemEnabledMenu menu, int instance) {
      this.loadCloseVerb(menu, instance);
   }

   protected boolean handleKeyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public void phoneEventNotify(int eventId, int param1, Object param2) {
      this.onEvent(eventId, param1, param2);
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached) {
         this.startListening();
      } else {
         this.stopListening();
      }

      super.onUiEngineAttached(attached);
   }

   public PhoneAwareScreen(UiApplication app, Object context, long style) {
      super(style | 562949953421312L | 1073741824);
      this.getDelegate().getField(0).setTag(null);
      this.setDefaultClose(false);
      if (app != null) {
         this._app = app;
      } else {
         this._app = UiApplication.getUiApplication();
      }
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      this.loadScreenVerbs(menu, instance);
   }

   @Override
   public void close() {
      if (this._closeVerb != null) {
         this._closeVerb.invoke(null);
      }
   }
}
