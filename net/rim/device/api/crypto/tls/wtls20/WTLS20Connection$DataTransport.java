package net.rim.device.api.crypto.tls.wtls20;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramConnectionBase;

class WTLS20Connection$DataTransport implements WTLSDataTransport {
   private final WTLS20Connection this$0;

   WTLS20Connection$DataTransport(WTLS20Connection _1) {
      this.this$0 = _1;
   }

   @Override
   public void write(byte[] data, int offset, int length) {
      if (data != null && length >= 0 && data.length >= length) {
         this.this$0.confirmConnectionOpen();
         Datagram datagram = this.this$0._subConnection.newDatagram(data, length);
         this.this$0._subConnection.send(datagram);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public byte[] read(int timeout) {
      this.this$0.confirmConnectionOpen();
      if (this.this$0._subConnection instanceof DatagramConnectionBase) {
         ((DatagramConnectionBase)this.this$0._subConnection).setTimeout(timeout);
      }

      Datagram d = this.this$0._subConnection.newDatagram(0);
      this.this$0._subConnection.receive(d);
      return d.getData();
   }

   @Override
   public void status(int newStatus) {
   }
}
