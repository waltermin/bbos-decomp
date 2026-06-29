package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.system.DeviceIDs;

public final class PinChangeDetectedScreen extends BasicScreen {
   @Override
   public final void refresh(Hashtable screenParams) {
      String newPin = DeviceIDs.getInstance().getPIN();
      String oldPin = ClientSessionState.getInstance().getTempCurrentPin();
      this.addContentField(new LabelField(MessageFormat.format(ApplicationResources.getString(185), new String[]{oldPin})));
      this.addContentLineBreak();
      this.addContentField(new LabelField(MessageFormat.format(ApplicationResources.getString(186), new String[]{newPin})));
      this.addContentLineBreak();
      Button yesButton = new Button(ApplicationResources.getString(32));
      Button noButton = new Button(ApplicationResources.getString(31));
      this.addButtonBarButtons(new Button[]{noButton, yesButton}, false, 1);
      CommandEvent noEvent = new CommandEvent(31, 20, null);
      this.attachEventToField(noButton, noEvent);
      CommandEvent yesEvent = new CommandEvent(32, 19, null);
      this.attachEventToField(yesButton, yesEvent);
      this.setDefaultEvent(yesEvent);
   }
}
