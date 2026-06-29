package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class AddFilterScreen extends AbstractFilterScreen {
   protected ApplicationDescriptor _application = ApplicationDescriptor.currentApplicationDescriptor();

   @Override
   public final void refresh(Hashtable screenParams) {
      this.eraseScreenData();
      Mailbox mailbox = ClientSessionState.getInstance().getMailboxToModify();
      this.setTitle(ApplicationResources.getString(283));
      this.addHeaderInfo(null, mailbox.getEmail());
      this.addFilterOperator(null, null);
      this.addContainsArea(null);
      this.addAction(false, false, false);
      Button cancelButton = new Button(ApplicationResources.getString(28));
      Button saveButton = new Button(ApplicationResources.getString(29));
      this.addButtonBarButtons(new Button[]{cancelButton, saveButton}, false, 1);
      LinkEvent cancelEvent = new LinkEvent(28, 43);
      this.attachEventToField(cancelButton, cancelEvent);
      CommandEvent saveEvent = new CommandEvent(29, 27, new String[]{"filtername", "sendalert", "headersonly", "levelone", "filteroperator", "filtervalue"});
      this.attachEventToField(saveButton, saveEvent);
      this.setDefaultEvent(saveEvent);
      this.setCloseEvent(cancelEvent);
      this.setHelp("224491.wml");
   }
}
