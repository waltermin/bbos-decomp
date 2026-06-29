package net.rim.device.apps.internal.bis.event;

import net.rim.device.apps.internal.bis.api.ui.CommandDescriptor;
import net.rim.device.apps.internal.bis.api.ui.Event;

public final class CommandEvent extends Event {
   private CommandDescriptor _descriptor;

   public CommandEvent(int rbID, int commandID, String[] paramNames) {
      super(rbID);
      this._descriptor = new CommandDescriptor(commandID, paramNames);
   }

   public final CommandDescriptor getDescriptor() {
      return this._descriptor;
   }
}
