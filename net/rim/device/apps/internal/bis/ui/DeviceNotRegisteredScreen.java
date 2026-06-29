package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.FormattedTextField;
import net.rim.device.apps.internal.bis.event.CloseEvent;

public final class DeviceNotRegisteredScreen extends BasicScreen {
   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(262));
      this.addContentField(new LabelField(ApplicationResources.getString(263)));
      this.addContentLineBreak();
      this.addContentField(new FormattedTextField(ApplicationResources.getString(264)));
      Button okButton = new Button(ApplicationResources.getString(39));
      this.addButtonBarButtons(new Button[]{okButton}, false);
      CloseEvent okEvent = new CloseEvent(39);
      this.attachEventToField(okButton, okEvent);
      this.setDefaultEvent(okEvent);
   }
}
