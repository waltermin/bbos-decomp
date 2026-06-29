package net.rim.device.cldc.io.mdp;

import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.datarecovery.DataRecovery;

public final class Protocol extends DatagramConnectionBase {
   private DataRecovery _dataRecovery;
   private boolean _generateActivityReport;

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      super.openPrim(name, mode, timeouts);
      switch (RadioInfo.getNetworkType()) {
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            this.setFlag(128, true);
         case 2:
            this._dataRecovery = DataRecovery.getInstance();
            EventLogger.logEvent(super._transport.GUID, 1229874030, 0);
            return this;
      }
   }

   @Override
   public final int getProperties(String name) {
      return 1;
   }

   @Override
   protected final boolean isAddressed(DatagramAddressBase addressBase) {
      return super._receiveFilter.equals(addressBase);
   }

   @Override
   public final void send(Datagram datagram) {
      super.send(datagram);
      if (this._generateActivityReport) {
         this._dataRecovery.fileReport(0);
      }
   }

   @Override
   public final byte[] setup(int callType, Object context) {
      switch (callType) {
         case 1462989746:
            return super.setup(callType, context);
         case 1462989747:
         default:
            this._generateActivityReport ^= true;
            return null;
      }
   }
}
