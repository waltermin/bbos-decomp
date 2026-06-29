package net.rim.device.apps.api.ui;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.SendKeyInvocableVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;

public class AppsMainScreen extends MainScreen {
   private ContextObject _context;
   private boolean _clickAndHoldKeySupport;
   private char _clickedKey = 0;
   protected ClickAndHoldKey _clickAndHoldKey;
   public static final long CLICK_AND_HOLD_GUID = -6860088460751500843L;

   public AppsMainScreen(long style) {
      super(style);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      this._clickAndHoldKey = (ClickAndHoldKey)ar.get(-6860088460751500843L);
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached && this._clickAndHoldKey == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         this._clickAndHoldKey = (ClickAndHoldKey)ar.get(-6860088460751500843L);
      }
   }

   protected ContextObject getContext() {
      return this._context;
   }

   protected void setSupportClickAndHoldKeyEvents(boolean supportClickAndHoldKeyEvents) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected boolean isClickAndHoldKey(char key) {
      return this._clickAndHoldKey == null ? false : this._clickAndHoldKey.validClickAndHold(key);
   }

   protected boolean keyClickedAndHeld(int keycode, int time) {
      return this._clickAndHoldKey != null ? this._clickAndHoldKey.doClickAndHold(keycode) : false;
   }

   protected boolean keyClickedAndReleased(char key, int status, int time) {
      return false;
   }

   protected boolean handleEndKey() {
      return false;
   }

   protected boolean handleSendKey() {
      Field field = this.getLeafFieldWithFocus();
      if (field == null) {
         return false;
      }

      Object cookie = field.getCookie();
      if (ControllerUtilities.invokeSendKeyVerb(cookie)) {
         return true;
      }

      ContextMenu cm = field.getContextMenu();
      MenuItem menuItem = cm.getDefaultItem();
      if (menuItem instanceof VerbMenuItem) {
         Verb verb = (Verb)((VerbMenuItem)menuItem).getVerb();
         if (verb instanceof SendKeyInvocableVerb) {
            verb.invoke(null);
            return true;
         }
      }

      return false;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (this._clickAndHoldKeySupport && this.isClickAndHoldKey(key)) {
         this._clickedKey = key;
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      return key == 17 && this.handleSendKey() ? true : super.keyDown(keycode, time);
   }

   @Override
   public boolean dispatchKeyEvent(int event, char key, int keycode, int time) {
      int keypress = Keypad.key(keycode);
      if (keypress == 18) {
         return event == 513 ? this.handleEndKey() : false;
      } else {
         return super.dispatchKeyEvent(event, key, keycode, time);
      }
   }

   @Override
   protected boolean keyRepeat(int keycode, int time) {
      char key = Keypad.map(keycode);
      if (key == this._clickedKey) {
         this._clickedKey = 0;
         return this.keyClickedAndHeld(keycode, time);
      } else {
         return super.keyRepeat(keycode, time);
      }
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      char key = Keypad.map(keycode);
      if (key == this._clickedKey) {
         this._clickedKey = 0;
         if (this.keyClickedAndReleased(key, Keypad.status(keycode), time)) {
            return true;
         }
      }

      return super.keyUp(keycode, time);
   }

   @Override
   public final Menu getMenu(int instance) {
      ContextObject menuContext = (ContextObject)this.getMenuContextObject();
      SystemEnabledMenu menu = new SystemEnabledMenu(menuContext, this, instance);
      Menu.setTargetScreen(this);
      menu.setInstance(instance);
      this.makeMenuWithContext(menu, instance);
      this.makeMenu(menu, instance);
      menu.coalesce(-3072555018635390988L, null);
      this.setDefaultMenuItem(menu, menuContext);
      menu.promoteVerbs();
      return menu;
   }

   protected ContextObject getMenuContextObject() {
      return (ContextObject)this.getContext();
   }

   protected void setDefaultMenuItem(SystemEnabledMenu menu, ContextObject menuContext) {
      Verb defaultVerb = (Verb)ContextObject.get(menuContext, -3185095355580406181L);
      if (defaultVerb != null) {
         menu.setDefault(defaultVerb);
      }
   }

   protected Object invokeVerb(Verb verb, Object parameter) {
      Object result = verb.invoke(parameter);
      this.verbInvoked(verb, parameter, result);
      return result;
   }

   protected void makeMenu(SystemEnabledMenu menu, int instance) {
   }

   public void setContext(ContextObject context) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected void verbInvoked(Verb verb, Object context, Object result) {
      if (ContextObject.getFlag(result, 39)) {
         UiEngine uiEngine = this.getUiEngine();
         if (uiEngine != null && !ContextObject.getFlag(result, 40)) {
            this.close();
         }
      }
   }

   public void setHelp(String helpTopic) {
      if (this._context == null) {
         this._context = new ContextObject();
      }

      this._context.put(244, helpTopic);
   }

   public void setHelp(int helpTopicID) {
      if (this._context == null) {
         this._context = new ContextObject();
      }

      this._context.put(244, new Integer(helpTopicID));
   }
}
