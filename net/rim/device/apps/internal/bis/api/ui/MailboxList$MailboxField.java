package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.resource.ImageCache;

public final class MailboxList$MailboxField extends LabelField {
   Mailbox _mailbox;
   private final MailboxList this$0;

   public MailboxList$MailboxField(MailboxList _1, Mailbox mailbox) {
      super(mailbox.getEmail(), 1152921504606846976L);
      this.this$0 = _1;
      this._mailbox = mailbox;
      if (!this._mailbox.isValid()) {
         this.setImage(ImageCache.getInvalidImage().getBitmap());
      }
   }

   public final Mailbox getMailbox() {
      return this._mailbox;
   }

   @Override
   public final boolean isFocusable() {
      return true;
   }

   @Override
   public final boolean isSelectionCopyable() {
      return false;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.setInstance(65538);
      Mailbox selectedMailbox = this._mailbox;
      String[] commandParams = new String[]{"description"};
      if (!selectedMailbox.isValid()) {
         NotificationMenuItem validateMenuItem = new NotificationMenuItem(ApplicationResources.getString(169), 20000, 1);
         menu.add(validateMenuItem);
         validateMenuItem.setListener(this.this$0._menuItemListener);
         validateMenuItem.setEvent(new CommandEvent(169, 13, commandParams));
      }

      NotificationMenuItem editMenuItem = new NotificationMenuItem(ApplicationResources.getString(162), 20000, 1);
      menu.add(editMenuItem);
      editMenuItem.setListener(this.this$0._menuItemListener);
      editMenuItem.setEvent(new CommandEvent(162, 7, commandParams));
      menu.setDefault(editMenuItem);
      NotificationMenuItem deleteMenuItem = new NotificationMenuItem(ApplicationResources.getString(163), 20000, 1);
      menu.add(deleteMenuItem);
      deleteMenuItem.setListener(this.this$0._menuItemListener);
      deleteMenuItem.setEvent(new CommandEvent(162, 9, commandParams));
      NotificationMenuItem filtersMenuItem = new NotificationMenuItem(ApplicationResources.getString(273), 20000, 1);
      menu.add(filtersMenuItem);
      filtersMenuItem.setListener(this.this$0._menuItemListener);
      filtersMenuItem.setEvent(new CommandEvent(273, 24, commandParams));
   }
}
