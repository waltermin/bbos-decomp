package net.rim.device.apps.internal.messaging;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.framework.hotkeys.HotKeyCheck;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.messaging.messagelist.DeleteSingleItemVerb;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.internal.system.InternalServices;

final class AnonymousMessagesImpl$AnonymousMessageViewerScreen extends ModelScreen {
   private AnonymousMessagesImpl$AnonymousMessageModel _message;
   private ActiveRichTextField _bodyField;
   private Field _fieldToGetFocus;

   AnonymousMessagesImpl$AnonymousMessageViewerScreen(Object context) {
      super(288239172244733952L, null, ContextObject.clone(context));
      this.setScrollBehaviourView(false);
      this.getMainManager().setVerticalQuantization(1);
      ContextObject.put(context, -6581931217101110672L, this);
      this.getMainManager().setTag(Tag.create("email-viewer-screen"));
   }

   public final void setModel(AnonymousMessagesImpl$AnonymousMessageModel message) {
      super.setModel(message);
      this._message = message;
      Manager header = new VerticalFieldManager();
      header.setTag(Tag.create("email-subject-area"));
      Field f = new EditField(MessageResources.getString(80), MessageResources.getString(this._message._opened ? 84 : 83), 1000000, 9007199254740992L);
      f.setTag(Tag.create("email-header-text"));
      Manager statusHeader = null;
      statusHeader = new VerticalFieldManager();
      statusHeader.setTag(Tag.create("email-header-area"));
      statusHeader.add(f);
      DateField date = new DateField(null, this._message._payload._time, 1161928703861588022L);
      date.setTag(Tag.create("email-timestamp-text"));
      String sender = this._message._payload._sender;
      if (sender != null) {
         f = new EditField(MessageResources.getString(81), sender, 1000000, 9007199254740992L);
         f.setTag(Tag.create("email-header-text"));
         header.add(f);
         this._fieldToGetFocus = header;
      }

      String body = this._message._payload._body;
      f = new EditField(null, this._message._payload._subject, 1000000, 9007199254740992L);
      f.setTag(Tag.create("email-subject-text"));
      if (this._fieldToGetFocus == null) {
         this._fieldToGetFocus = header;
      }

      header.add(f);
      header.add(date);
      this.add(statusHeader);
      this.add(header);
      this._bodyField = new ActiveRichTextField(body);
      this._bodyField.setAdjustAlignments(true);
      this._bodyField.setTag(Tag.create("email-body-area"));
      this.add(this._bodyField);
      this._bodyField.setFocus();
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         if (this._fieldToGetFocus != null) {
            Field f = this.getLeafFieldWithFocus();
            if (f != null) {
               while (f.getManager() != null && (f.getManager().getStyle() & 281474976710656L) == 0) {
                  f = f.getManager();
               }

               f.getManager().setVerticalScroll(this._fieldToGetFocus.getTop());
               return;
            }
         }
      } else {
         this._message = null;
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      if (instance == 65536 && this.isScrollBehaviourView()) {
         menu.add(MenuItem.getPrefab(16));
      }

      super.makeMenu(menu, instance);
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (instance == 65536) {
         DeleteSingleItemVerb deleteSingleItemVerb = new DeleteSingleItemVerb(611472, 1000);
         deleteSingleItemVerb.setParameters(this._message, ContextObject.castOrCreate(super._context));
         menu.add(deleteSingleItemVerb);
      }

      if (instance != 0) {
         menu.setAlignment(12884901888L, 34359738368L);
      } else {
         menu.add(new AnonymousMessagesImpl$AnonymousMessageMarkVerb(this._message, !this._message._opened));
         if (!this._message._saved) {
            menu.add(new AnonymousMessagesImpl$AnonymousMessageSaveVerb(this._message));
         }

         VerbFactory outerVerbFactory = (VerbFactory)ContextObject.get(super._context, -2846768035584909703L);
         if (outerVerbFactory != null) {
            menu.add(outerVerbFactory.getVerbs(null));
         }

         VerbRepository verbRepository = VerbRepository.getVerbRepository(3433073725342984424L);
         if (verbRepository != null) {
            menu.add(verbRepository.getVerbs(null));
         }

         verbRepository = VerbRepository.getVerbRepository(-6481681929958323011L);
         if (verbRepository != null) {
            menu.add(verbRepository.getVerbs(null));
         }

         menu.coalesce(-3072555018635390988L, null);
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      char key = Keypad.map(keycode);
      int status = Keypad.status(keycode);
      if (key != 127 && Keypad.getAltedChar(key) != 127) {
         int k = MessageHotkeys.map(keycode);
         switch (k) {
            case 140:
               HotKeyCheck hotkey = (HotKeyCheck)ContextObject.get(super._context, -7922982350060060892L);
               if (hotkey != null && hotkey.invokeHotkey(k, super._context)) {
                  return true;
               } else {
                  switch (key) {
                     case '\n': {
                        int direction = (status & 1) == 0 ? 512 : 256;
                        this.scroll(direction);
                        return true;
                     }
                     case ' ': {
                        int direction = (status & 2) == 0 ? 512 : 256;
                        this.scroll(direction);
                        return true;
                     }
                     default:
                        if (InternalServices.isReducedFormFactor() && (status & 1) == 0) {
                           int directionx = 256;
                           switch (Keypad.key(keycode)) {
                              case 66:
                                 return this.dispatchTrackwheelEvent(519, 1, 0, time);
                              case 71:
                              case 77:
                                 directionx = 512;
                              case 85:
                                 this.scroll(directionx);
                                 return true;
                              case 84:
                                 return this.dispatchTrackwheelEvent(519, -1, 0, time);
                           }
                        }

                        return super.keyDown(keycode, time);
                  }
               }
            case 141:
            default:
               Field f = this.getField(0);
               f.setFocus();
               return true;
            case 142:
               this._bodyField.setCursorPosition(this._bodyField.getTextLength());
               this._bodyField.setFocus();
               return true;
         }
      } else {
         DeleteSingleItemVerb verb = new DeleteSingleItemVerb(611472, 1000);
         verb.setParameters(this._message, super._context);
         Object _returnValue = verb.invoke(null);
         if (ContextObject.getFlag(_returnValue, 39)) {
            UiApplication.getUiApplication().popScreen(this);
         }

         return true;
      }
   }
}
