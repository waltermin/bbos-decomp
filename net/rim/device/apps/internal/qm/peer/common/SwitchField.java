package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.framework.model.ActionProvider;

final class SwitchField extends LabelField {
   SwitchField$SwitchMenuItem _switchMenuItem;
   public static final int ACTION_INVOKE = 1;

   SwitchField(long style, NotificationMessage cookie) {
      super(cookie.toString(), style);
      this.setCookie(cookie);
      this._switchMenuItem = new SwitchField$SwitchMenuItem(this);
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (!this.isInCurrentConversationForTypingNotification()) {
         contextMenu.addItem(this._switchMenuItem);
         contextMenu.setDefaultItem(this._switchMenuItem);
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      char keyPress = Keypad.map(keycode);
      if (keyPress == '\n' && !this.isInCurrentConversationForTypingNotification()) {
         this._switchMenuItem.run();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean result = false;
      if (action == 1) {
         if (!this.isInCurrentConversationForTypingNotification()) {
            this._switchMenuItem.run();
         }

         result = true;
      }

      return result;
   }

   private final boolean isInCurrentConversationForTypingNotification() {
      Object cookie = this.getCookie();
      if (cookie instanceof TypingNotificationMessage) {
         Conversation conversation = ((TypingNotificationMessage)cookie).getConversation();
         Screen scr = this.getScreen();
         if (scr != null) {
            Object scrCookie = scr.getCookie();
            if (scrCookie instanceof Object) {
               return ((ActionProvider)scrCookie).perform(NotificationMessageField.MATCH, conversation);
            }
         }
      }

      return false;
   }
}
