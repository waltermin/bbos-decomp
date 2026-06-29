package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextProxy;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.internal.i18n.CommonResource;

public class BrowserProperty implements IBrowserProperty, Confirmation, FieldChangeListener, AccessibleContextProxy {
   protected boolean _restrictedAccess = true;

   public boolean openProductionBackdoor(int backdoorCode) {
      return false;
   }

   protected Verb getVerbs(Verb[] _1) {
      throw null;
   }

   public void setAccessType(boolean restrictedAccess) {
      this._restrictedAccess = restrictedAccess;
   }

   protected MainScreen generateScreen(String title) {
      AppsMainScreen screen = new BrowserProperty$BrowserPropertyScreen(this);
      screen.setTitle(title);
      return screen;
   }

   @Override
   public Verb getOpenVerb(boolean restrictedAccess) {
      return new OpenOptionVerb(this, restrictedAccess);
   }

   @Override
   public void saveProperty() {
      throw null;
   }

   @Override
   public void fieldChanged(Field field, int context) {
   }

   @Override
   public boolean confirm(Verb verb, Object context) {
      boolean result = true;
      Screen s = UiApplication.getUiApplication().getActiveScreen();
      if (s.isDirty()) {
         switch (Dialog.ask(1, CommonResource.getString(10003))) {
            case -1:
               result = false;
               break;
            case 1:
               this.saveProperty();
               return true;
         }
      }

      return result;
   }

   @Override
   public AccessibleContext getAccessibleContext() {
      return (AccessibleContext)(new Object(this.getLabel()));
   }

   @Override
   public Screen getScreen(boolean _1) {
      throw null;
   }

   @Override
   public String getLabel() {
      throw null;
   }
}
