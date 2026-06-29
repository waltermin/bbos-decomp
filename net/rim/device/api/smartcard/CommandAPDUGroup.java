package net.rim.device.api.smartcard;

public class CommandAPDUGroup {
   private CommandAPDU[] _commandAPDUs;
   private int _numAPDUs;

   public CommandAPDUGroup() {
      this(null, 0);
   }

   public CommandAPDUGroup(CommandAPDU[] commandAPDUs) {
      this(commandAPDUs, commandAPDUs.length);
   }

   public CommandAPDUGroup(CommandAPDU[] commandAPDUs, int numAPDUs) {
      this.setAPDUs(commandAPDUs, numAPDUs);
   }

   public void setAPDUs(CommandAPDU[] commandAPDUs) {
      this.setAPDUs(commandAPDUs, commandAPDUs.length);
   }

   public void setAPDUs(CommandAPDU[] commandAPDUs, int numAPDUs) {
      if (numAPDUs >= 0 && (numAPDUs <= 0 || commandAPDUs != null)) {
         this._commandAPDUs = commandAPDUs;
         this._numAPDUs = numAPDUs;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getNumAPDUs() {
      return this._numAPDUs;
   }

   public CommandAPDU getAPDU(int index) {
      if (index >= 0 && index <= this._numAPDUs) {
         return this._commandAPDUs[index];
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }
}
