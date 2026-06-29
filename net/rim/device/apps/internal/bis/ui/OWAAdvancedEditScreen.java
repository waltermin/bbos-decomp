package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.HeadingField;
import net.rim.device.apps.internal.bis.api.ui.InputHintLabelField;
import net.rim.device.apps.internal.bis.api.ui.NotificationMenuItem;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class OWAAdvancedEditScreen extends UserSettingsScreen {
   private BasicEditField _owaURLEdit;
   private EmailAddressEditField _emailAccountEdit;
   public static final String PARAM_SERVER = "server";
   public static final String PARAM_SSL = "ssl";

   public OWAAdvancedEditScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      String server = "";
      String account = "";
      if (mailboxToEdit.getServer() != null && mailboxToEdit.getServer().lastIndexOf(47) > -1) {
         String fullURL = mailboxToEdit.getServer();
         server = fullURL.substring(0, fullURL.lastIndexOf(47));
         account = fullURL.substring(fullURL.lastIndexOf(47) + 1, fullURL.length());
         if (mailboxToEdit.getUseSSL() != null && mailboxToEdit.getUseSSL()) {
            server = "https://" + server;
         } else {
            server = "http://" + server;
         }
      }

      String title = MessageFormat.format(ApplicationResources.getString(150), new String[]{mailboxToEdit.getDescription()});
      this.setTitle(title);
      this.addContentField(new HeadingField(ApplicationResources.getString(129)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(140)));
      this._owaURLEdit = new BasicEditField(117440512);
      this._owaURLEdit.setText(server);
      this.addContentField(this._owaURLEdit, true);
      this.addContentField(new InputHintLabelField(ApplicationResources.getString(53)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(134)));
      this._emailAccountEdit = new EmailAddressEditField(null, account);
      this.addContentField(this._emailAccountEdit, true);
      this.addContentLineBreak();
      String seconds = "";
      if (mailboxToEdit.getTimeout() != null) {
         int timeoutValue = mailboxToEdit.getTimeout();
         if (timeoutValue > 1000) {
            timeoutValue /= 1000;
         }

         seconds = MessageFormat.format(ApplicationResources.getString(141), new String[]{" " + timeoutValue});
      }

      this.addContentFieldRow(new Field[]{new BoldLabelField(ApplicationResources.getString(144)), new LabelField(seconds)});
      Button cancel = new Button(ApplicationResources.getString(28));
      Button save = new Button(ApplicationResources.getString(29));
      this.addButtonBarButtons(new Button[]{cancel, save}, false, 1);
      this.attachEventToField(cancel, new LinkEvent(28, 7));
      CommandEvent saveEvent = new CommandEvent(29, 22, new String[]{"server", "ssl"});
      this.attachEventToField(save, saveEvent);
      this.setDefaultEvent(saveEvent);
      this.setHelp("130176.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      String owaURL = this._owaURLEdit.getText();
      if (owaURL != null && owaURL.length() != 0 && (owaURL.startsWith("http://") || owaURL.startsWith("https://"))) {
         String emailAccount = this._emailAccountEdit.getText();
         if (emailAccount != null && emailAccount.length() != 0) {
            String ssl = "false";
            if (owaURL.startsWith("https")) {
               ssl = "true";
            }

            String server = owaURL.substring(owaURL.indexOf("://") + 3, owaURL.length());
            if (!server.endsWith("/")) {
               server = server + "/";
            }

            server = server + emailAccount;
            inputMap.put("server", server);
            inputMap.put("ssl", ssl);
            return true;
         } else {
            this.setError(ApplicationResources.getString(210));
            return false;
         }
      } else {
         this.setError(ApplicationResources.getString(209));
         return false;
      }
   }

   @Override
   protected final void addCustomMenuItems(Menu menu, int instance) {
      super.addCustomMenuItems(menu, instance);
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      LinkEvent generalLinkEvent = new LinkEvent(208, 25);
      NotificationMenuItem generalMenuItem = new NotificationMenuItem(generalLinkEvent.getLabel(), 9998, 9998);
      menu.add(generalMenuItem);
      this.attachEventToMenuItem(generalMenuItem, generalLinkEvent);
      menu.add(MenuItem.separator(9999));
   }
}
