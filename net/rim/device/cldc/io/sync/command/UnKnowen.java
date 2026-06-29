package net.rim.device.cldc.io.sync.command;

import net.rim.device.cldc.io.sync.SyncCommand;

public final class UnKnowen extends SyncCommand {
   public UnKnowen() {
      this.setTag(0);
   }

   @Override
   public final boolean isValid() {
      return true;
   }
}
