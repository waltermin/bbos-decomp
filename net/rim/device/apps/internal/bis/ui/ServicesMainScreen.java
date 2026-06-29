package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.LinkField;
import net.rim.device.apps.internal.bis.api.ui.MailboxList;
import net.rim.device.apps.internal.bis.api.ui.MailboxList$MailboxField;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class ServicesMainScreen extends UserSettingsScreen {
   private static final String PARAM_MAILBOX_DESC;

   public ServicesMainScreen() {
      super(30);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(57));
      UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
      Mailbox[] userMailboxes = userInfo.getMailboxes();
      if (userMailboxes != null && userMailboxes.length > 0) {
         this.addContentField((Field)(new Object(ApplicationResources.getString(56))));
         MailboxList mailboxList = new MailboxList(ApplicationResources.getString(205), userMailboxes);
         this.addContentField(mailboxList);
         mailboxList.setMenuListener(this);
      } else {
         this.addContentField((Field)(new Object(ApplicationResources.getString(271))));
         this.addContentLineBreak();
      }

      if (!userInfo.hasMaxMailboxes()) {
         if (!userInfo.isBBMail()) {
            LinkField addExistingEmailLink = new LinkField(ApplicationResources.getString(89), 0, false, 1152921504606846976L);
            this.addContentField(addExistingEmailLink);
            LinkEvent addExistingEmailEvent = new LinkEvent(89, 4);
            this.attachEventToField(addExistingEmailLink, addExistingEmailEvent);
            this.setDefaultEvent(addExistingEmailEvent);
         }

         if (!userInfo.hasHostedMailbox() && userInfo.isHostedMailboxEnabled()) {
            LinkField addBBMailLink = new LinkField(ApplicationResources.getString(90), 0, false, 1152921504606846976L);
            this.addContentField(addBBMailLink);
            LinkEvent addBBMailEvent = new LinkEvent(90, 5);
            this.attachEventToField(addBBMailLink, addBBMailEvent);
            if (userInfo.isBBMail()) {
               this.setDefaultEvent(addBBMailEvent);
            }
         }
      }

      LinkField endUserAgreementLink = new LinkField(ApplicationResources.getString(73), 0, false, 1152921504606846976L);
      this.addContentField(endUserAgreementLink);
      CommandEvent endUserAgreementEvent = new CommandEvent(73, 23, null);
      this.attachEventToField(endUserAgreementLink, endUserAgreementEvent);
      this.setHelp("index.wml");
   }

   @Override
   protected final MenuItem selectDefaultMenuItem(Menu menu) {
      return menu.getInstance() != 65538 ? this.findMenuItemForEvent(menu, this.getDefaultEvent()) : null;
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      Field selectedField = this.getLeafFieldWithFocus();
      if (selectedField instanceof MailboxList$MailboxField) {
         MailboxList$MailboxField mailboxField = (MailboxList$MailboxField)selectedField;
         inputMap.put("description", mailboxField.getMailbox().getDescription());
      }

      return true;
   }
}
