package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.HeadingField;
import net.rim.device.apps.internal.bis.api.ui.NotificationMenuItem;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class AdvancedEditScreen extends UserSettingsScreen {
   private BasicEditField _serverEdit;
   private CheckboxField _sslCheckbox;
   public static final String PARAM_SERVER = "server";
   public static final String PARAM_SSL = "ssl";

   public AdvancedEditScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      String title = MessageFormat.format(ApplicationResources.getString(150), new Object[]{mailboxToEdit.getDescription()});
      this.setTitle(title);
      this.addContentField(new HeadingField(ApplicationResources.getString(129)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(44)));
      this._serverEdit = (BasicEditField)(new Object(117440512));
      if (mailboxToEdit.getServer() != null) {
         this._serverEdit.setText(mailboxToEdit.getServer());
      }

      this.addContentField(this._serverEdit, true);
      this.addContentLineBreak();
      String serverTypeLabel = ApplicationResources.getString(211);
      String serverType = mailboxToEdit.getProtocol();
      if (serverType != null) {
         serverType = ((StringBuffer)(new Object(" "))).append(serverType.toUpperCase()).toString();
      }

      LabelField serverTypeField = new BoldLabelField(((StringBuffer)(new Object())).append(serverTypeLabel).append(serverType).toString());
      this.addContentFieldRow(new Object[]{serverTypeField});
      this.addContentLineBreak();
      BoldLabelField portLabel = new BoldLabelField(ApplicationResources.getString(212));
      LabelField portValueLabel = (LabelField)(new Object(mailboxToEdit.getPort() != null ? mailboxToEdit.getPort().toString() : ""));
      this.addContentFieldRow(new Object[]{portLabel, portValueLabel});
      this.addContentLineBreak();
      this._sslCheckbox = (CheckboxField)(new Object(ApplicationResources.getString(213), mailboxToEdit.getUseSSL() != null ? mailboxToEdit.getUseSSL() : false));
      this.addContentField(this._sslCheckbox);
      this.addContentLineBreak();
      String seconds = "";
      if (mailboxToEdit.getTimeout() != null) {
         int timeoutValue = mailboxToEdit.getTimeout();
         if (timeoutValue > 1000) {
            timeoutValue /= 1000;
         }

         seconds = MessageFormat.format(ApplicationResources.getString(141), new Object[]{((StringBuffer)(new Object(" "))).append(timeoutValue).toString()});
      }

      this.addContentFieldRow(new Object[]{new BoldLabelField(ApplicationResources.getString(144)), new Object(seconds)});
      Button cancel = new Button(ApplicationResources.getString(28));
      Button save = new Button(ApplicationResources.getString(29));
      this.addButtonBarButtons(new Button[]{cancel, save}, false, 1);
      LinkEvent cancelEvent = new LinkEvent(28, 7);
      this.attachEventToField(cancel, cancelEvent);
      CommandEvent saveEvent = new CommandEvent(29, 22, new String[]{"server", "ssl"});
      this.attachEventToField(save, saveEvent);
      this.setDefaultEvent(saveEvent);
      this.setCloseEvent(cancelEvent);
      this.setHelp("118715.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      String emailServer = this._serverEdit.getText();
      if (emailServer != null && emailServer.length() != 0) {
         boolean useSSL = this._sslCheckbox.getChecked();
         inputMap.put("server", emailServer);
         inputMap.put("ssl", useSSL ? "true" : "false");
         return true;
      } else {
         this.setError(ApplicationResources.getString(187));
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
