package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.FormattedTextField;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;

public final class ServiceBooksScreen extends UserSettingsScreen {
   public ServiceBooksScreen() {
      super(15);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(121));
      this.addContentField(new FormattedTextField(ApplicationResources.getString(120)));
      Button cancelButton = new Button(ApplicationResources.getString(28));
      Button sendButton = new Button(ApplicationResources.getString(121));
      this.addButtonBarButtons(new Button[]{cancelButton, sendButton}, false, 1);
      this.attachEventToField(cancelButton, new BackEvent(28));
      CommandEvent sendEvent = new CommandEvent(121, 14, null);
      this.attachEventToField(sendButton, sendEvent);
      this.setDefaultEvent(sendEvent);
      this.setHelp("32170.wml");
   }
}
