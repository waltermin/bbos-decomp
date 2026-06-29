package net.rim.device.apps.internal.browser.wappush;

import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.hotkeys.HotKeyCheck;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.DeleteSingleItemVerb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.internal.browser.push.BrowserPushResources;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.device.apps.internal.browser.wappush.verbs.ChangeStatusVerb;
import net.rim.device.apps.internal.browser.wappush.verbs.FollowLinkVerb;
import net.rim.device.apps.internal.messaging.MessageHotkeys;
import net.rim.device.internal.ui.UiInternal;

public final class WAPPushViewerScreen extends ModelScreen implements GlobalEventListener {
   private ChangeStatusVerb _markUnopenedVerb;
   private ChangeStatusVerb _markOpenedVerb;
   private FollowLinkVerb _followLinkVerb;
   private Object[] _globalEventListeners;

   public WAPPushViewerScreen(Object context) {
      super(0, null, ContextObject.clone(context));
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (object0 == super._model) {
         if (guid == 1202366544244619460L) {
            UiApplication.getUiApplication().popScreen(this);
         } else {
            super.eventOccurred(guid, data0, data1, object0, object1);
         }
      } else {
         super.eventOccurred(guid, data0, data1, object0, object1);
      }

      if (this._globalEventListeners != null) {
         for (int i = this._globalEventListeners.length - 1; i >= 0; i--) {
            ((GlobalEventListener)this._globalEventListeners[i]).eventOccurred(guid, data0, data1, object0, object1);
         }
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         UiApplication.getUiApplication().addGlobalEventListener(this);
      } else {
         UiApplication.getUiApplication().removeGlobalEventListener(this);
         this._globalEventListeners = null;
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   public final void setModel(Object model) {
      Asserts.productionArgumentAssert(model instanceof WAPPushModel);
      super.setModel(model);
      this.deleteAll();
      this.populateScreen();
   }

   protected final void populateScreen() {
      WAPPushModel message = (WAPPushModel)super._model;
      ContextObject contextObject = ContextObject.clone(super._context);
      if (ContextObject.getFlag(super._context, 64)) {
         contextObject.put(-442409970680484936L, this);
      }

      this.setTitle((Field)(new Object(BrowserPushResources.getString(12))));
      StringBuffer textBuffer = (StringBuffer)(new Object());
      if (message.getConnectionType() == 1) {
         textBuffer.append(BrowserPushResources.getString(46));
         textBuffer.append(' ');
         String address = message.getConnectionSource();
         if (address != null) {
            textBuffer.append(message.getConnectionSource());
         }

         textBuffer.append('\n');
      }

      if (message instanceof SICModel) {
         SICModel sic = (SICModel)message;
         textBuffer.append(BrowserPushResources.getString(13));
         textBuffer.append(' ');
         textBuffer.append(sic.getMessage());
         textBuffer.append('\n');
      }

      textBuffer.append(BrowserPushResources.getString(14));
      textBuffer.append(' ');
      textBuffer.append(message.getURL());
      textBuffer.append('\n');
      this.add(new WAPPushViewerScreen$WAPPushActiveRichTextField(this, textBuffer.toString(), 18014398509481984L));
   }

   protected final synchronized void addGlobalEventListener(GlobalEventListener listener) {
      this._globalEventListeners = ListenerUtilities.addListener(this._globalEventListeners, listener);
   }

   private final void initializeCachedVerbs() {
      if (this._markUnopenedVerb == null) {
         this._markUnopenedVerb = new ChangeStatusVerb((WAPPushModel)super._model, 1, 10);
         this._markOpenedVerb = new ChangeStatusVerb((WAPPushModel)super._model, 0, 11);
         this._followLinkVerb = new FollowLinkVerb((WAPPushModel)super._model);
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      this.initializeCachedVerbs();
      super.makeMenu(menu, instance);
      Verb defaultVerb = null;
      WAPPushModel message = (WAPPushModel)super._model;
      if (message.getStatus() == 0) {
         menu.add(this._markUnopenedVerb);
      } else {
         menu.add(this._markOpenedVerb);
      }

      menu.add(this._followLinkVerb);
      defaultVerb = this._followLinkVerb;
      VerbFactory outerVerbFactory = (VerbFactory)((LongHashtable)super._context).get(-2846768035584909703L);
      menu.add(outerVerbFactory.getVerbs(null));
      VerbRepository verbRepository = VerbRepository.getVerbRepository(3433073725342984424L);
      menu.add(verbRepository.getVerbs(null));
      ContextObject contextObject = ContextObject.castOrCreate(super._context);
      boolean oldValue = contextObject.getFlag(2);
      contextObject.setFlag(2);
      if (!oldValue) {
         contextObject.clearFlag(2);
      }

      if (defaultVerb != null) {
         menu.setDefault(defaultVerb);
      }

      menu.coalesce(-3072555018635390988L, null);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      char keyPress = UiInternal.map(keycode);
      int status = Keypad.status(keycode);
      if (super.keyDown(keycode, time)) {
         return true;
      }

      if (keyPress != 127 && Keypad.getAltedChar(keyPress) != 127) {
         int k = MessageHotkeys.map(keycode);
         switch (k) {
            case 140:
               HotKeyCheck hotkey = (HotKeyCheck)ContextObject.get(super._context, -7922982350060060892L);
               if (hotkey != null && hotkey.invokeHotkey(k, super._context)) {
                  return true;
               } else {
                  char key = UiInternal.map(keycode);
                  switch (key) {
                     case ' ':
                        int direction = (status & 2) == 0 ? 512 : 256;
                        this.scroll(direction);
                        return true;
                     default:
                        return false;
                  }
               }
            case 141:
            default:
               Field f = this.getField(0);
               f.setFocus();
               return true;
            case 142:
               this.scroll(2);
               return true;
         }
      } else {
         DeleteSingleItemVerb deleteVerb = (DeleteSingleItemVerb)(new Object(611472, 1000));
         deleteVerb.setParameters(super._model, super._context);
         super._returnValue = deleteVerb.invoke(null);
         if (ContextObject.getFlag(super._returnValue, 39)) {
            UiApplication.getUiApplication().popScreen(this);
         }

         return true;
      }
   }

   static final Object access$000(WAPPushViewerScreen x0) {
      return x0._model;
   }
}
