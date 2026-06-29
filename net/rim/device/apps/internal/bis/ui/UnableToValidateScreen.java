package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class UnableToValidateScreen extends BasicScreen {
   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(175));
      this.addContentField((Field)(new Object(ApplicationResources.getString(176))));
      Button noButton = new Button(ApplicationResources.getString(31));
      Button yesButton = new Button(ApplicationResources.getString(32));
      this.addButtonBarButtons(new Button[]{noButton, yesButton}, false, 1);
      String invalidAccountDescription = ClientSessionState.getInstance().getMailboxToModify().getDescription();
      this.attachEventToField(noButton, new BackEvent(31));
      CommandEvent yesEvent = new CommandEvent(32, 7, new Object[]{invalidAccountDescription});
      this.attachEventToField(yesButton, yesEvent);
      this.setDefaultEvent(yesEvent);
   }
}
