package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.CloseEvent;

public final class InitializationFailedScreen extends BasicScreen {
   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(123));
      this.addContentField((Field)(new Object(ApplicationResources.getString(124))));
      Button okButton = new Button(ApplicationResources.getString(39));
      this.addButtonBarButtons(new Button[]{okButton}, false);
      CloseEvent okEvent = new CloseEvent(39);
      this.attachEventToField(okButton, okEvent);
      this.setDefaultEvent(okEvent);
   }
}
