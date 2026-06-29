package net.rim.device.cldc.io.simultcpdatagram;

import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.Connection;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.nativebase.NativeConnectionBase;

public final class Protocol extends NativeConnectionBase {
   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      this.setTimeout(0);
      return super.openPrim(name, mode, timeouts);
   }

   @Override
   protected final boolean checkNetwork() {
      switch (RadioInfo.getNetworkType()) {
         case 2:
            return false;
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            return true;
      }
   }

   @Override
   public final int getMaximumLength() {
      return super._transport.getMaximumLength();
   }

   @Override
   public final int getNominalLength() {
      return this.getMaximumLength();
   }

   public final Transport getTransport() {
      return (Transport)super._transport;
   }

   @Override
   public final DatagramBase receiveDatagramBase() throws IOException {
      if (!super._isActive) {
         throw new IOException();
      }

      DatagramBase receivedDatagram = null;
      synchronized (super._datagrams) {
         if (super._datagrams.isEmpty()) {
            label43:
            try {
               super._datagrams.wait(super._timeout);
            } finally {
               break label43;
            }

            if (super._datagrams.isEmpty()) {
               if (super._isActive) {
                  throw new InterruptedIOException();
               }

               throw new IOException();
            }
         }

         return (SimulTcpDatagramBase)super._datagrams.dequeue();
      }
   }
}
