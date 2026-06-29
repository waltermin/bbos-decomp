package net.rim.device.api.io;

import java.io.IOException;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.util.CyclicQueue;

final class SendPacketThread extends Thread {
   private CyclicQueue _requests = new CyclicQueue(8);

   @Override
   public final void run() {
      while (true) {
         try {
            SendPacketThread$SPTRequest request;
            synchronized (this._requests) {
               if (this._requests.isEmpty()) {
                  this._requests.wait();
               }

               request = (SendPacketThread$SPTRequest)this._requests.dequeue();
            }

            if (!(request._sendObj instanceof DatagramTransportBase)) {
               if (request._sendObj instanceof DatagramConnection) {
                  ((DatagramConnection)request._sendObj).send(request._datagram);
               }
            } else {
               ((DatagramTransportBase)request._sendObj).send(request._datagram);
            }
         } catch (IOException var10) {
         } catch (Throwable var11) {
         } finally {
            SendPacketThread$SPTRequest request = null;
         }
      }
   }

   public final void addRequest(Object sendObj, Datagram datagram) {
      synchronized (this._requests) {
         this._requests.enqueue(new SendPacketThread$SPTRequest(sendObj, datagram));
         this._requests.notify();
      }
   }
}
