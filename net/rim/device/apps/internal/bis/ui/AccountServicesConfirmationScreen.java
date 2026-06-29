package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.FormattedTextField;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class AccountServicesConfirmationScreen extends UserSettingsScreen {
   public AccountServicesConfirmationScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(324));
      String email = ClientSessionState.getInstance().getIntegrationEmail();
      this.addContentField(new FormattedTextField(this.getString(321, new Object[]{email})));
      this.addContentLineBreak();
      if (ClientSessionState.getInstance().getSynchAddressBook()) {
         this.addContentField((Field)(new Object(ApplicationResources.getString(322))));
      }

      if (ClientSessionState.getInstance().getSynchCalendar()) {
         this.addContentField((Field)(new Object(ApplicationResources.getString(323))));
      }

      this.addContentLineBreak();
      LinkEvent okEvent = new LinkEvent(39, 7);
      Button okButton = new Button(ApplicationResources.getString(15));
      this.addButtonBarButtons(new Button[]{okButton}, false, 0);
      this.attachEventToField(okButton, okEvent);
      this.setDefaultEvent(okEvent);
      this.setCloseEvent(okEvent);
      this.setHelp(null);
   }

   @Override
   protected final String getString(int key, Object[] params) {
      String text = ApplicationResources.getString(key);
      return params != null ? MessageFormat.format(text, params) : text;
   }
}
