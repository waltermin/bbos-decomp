package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

public final class NotificationMenuItem extends MenuItem {
   private NotificationMenuItemListener _listener;
   private Event _event;

   public NotificationMenuItem(ResourceBundle bundle, int id, int ordinal, int priority) {
   }

   public NotificationMenuItem(String text, int ordinal, int priority) {
   }

   public final void setListener(NotificationMenuItemListener listener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void run() {
      if (this._listener != null) {
         this._listener.menuItemSelected(this);
      }
   }

   public final Event getEvent() {
      return this._event;
   }

   public final void setEvent(Event event) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
