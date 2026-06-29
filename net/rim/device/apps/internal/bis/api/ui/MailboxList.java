package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.apps.internal.bis.data.Mailbox;

public final class MailboxList extends VerticalFieldManager {
   private Mailbox[] _mailboxes;
   private String _title;
   private NotificationMenuItemListener _menuItemListener;
   private ThemeAttributeSet _themeAttributesHeader;
   private int _themeGeneration;
   public static final int INSTANCE_MAILBOX_SELECTED = 65538;
   private static final String PARAM_MAILBOX_DESC = "description";
   private static final Tag TAG_HEADER = Tag.create("header");

   public MailboxList(String title, Mailbox[] mailboxes) {
      this._mailboxes = mailboxes;
      this._title = title;
      this.add(new MailboxList$HeaderField(this, this._title));

      for (int i = 0; mailboxes != null && i < mailboxes.length; i++) {
         Mailbox mailbox = mailboxes[i];
         this.add(new MailboxList$MailboxField(this, mailbox));
      }

      this.add(new MailboxList$HeaderField(this, ""));
   }

   public final void setMenuListener(NotificationMenuItemListener menuItemListener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
