package net.rim.device.cldc.io.sync.command;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntVector;
import net.rim.device.cldc.io.sync.SyncCommand;
import net.rim.device.internal.synchronization.ota.util.LengthEncoding;

public class Resume extends SyncCommand {
   private IntVector _commands;

   public Resume() {
      this.setTag(13);
   }

   public final void addCommand(int aCommandTag) {
      if (this._commands == null) {
         this._commands = (IntVector)(new Object(5));
      }

      this._commands.addElement(aCommandTag);
   }

   @Override
   public final void writeParametersTo(DataBuffer douts) {
      if (this._commands != null) {
         int xIndex = this._commands.size();
         if (xIndex != 0) {
            douts.write(100);
            LengthEncoding.write(douts, xIndex);

            do {
               douts.write(this._commands.elementAt(--xIndex));
            } while (xIndex > -1);
         }
      }
   }

   @Override
   public final boolean isValid() {
      return true;
   }

   @Override
   public final void reset() {
      super.reset();
      if (this._commands != null) {
         this._commands.setSize(0);
      }
   }
}
