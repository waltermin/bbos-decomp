package net.rim.device.apps.internal.supl;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.crypto.tls.tls10.TLS10Connection;
import net.rim.device.internal.provisioning.SuplHandler;

final class SuplSession$TLSReceiveThread extends Thread {
   private SuplSession parent;
   private byte[] rxBuf;
   private final SuplSession this$0;

   SuplSession$TLSReceiveThread(SuplSession _1, SuplSession parentSession) {
      this.this$0 = _1;
      this.rxBuf = new byte[65535];
      this.parent = parentSession;
   }

   private final void startConnection() {
      Integer suplPort = new Integer(this.this$0.options.getSuplPortNumber());
      Fqdn slpAddress = this.this$0.options.getSlpAddress();
      StringBuffer strBuf = new StringBuffer().append(slpAddress.domainName).append(":");
      strBuf.append(suplPort.toString()).append(";deviceside=true");
      String tmp = SuplHandler.getApn();
      if (tmp != null && tmp != "") {
         strBuf.append(";apn=").append(tmp);
      }

      tmp = SuplHandler.getUsername();
      if (tmp != null && tmp != "") {
         strBuf.append(";tunnelauthusername=").append(tmp);
      }

      tmp = SuplHandler.getPassword();
      if (tmp != null && tmp != "") {
         strBuf.append(";tunnelauthpassword=").append(tmp);
      }

      tmp = strBuf.toString();
      System.out.println("Opening connection to: " + strBuf.toString());
      this.this$0.socketConnection = (SocketConnection)Connector.open("net.rim.device.cldc.io.socket://" + tmp, 0, true);
      this.this$0.socketConnection.setSocketOption((byte)1, 1);
      if (!this.this$0.options.isTLSEnabled()) {
         this.this$0.os = this.this$0.socketConnection.openOutputStream();
         this.this$0.is = this.this$0.socketConnection.openInputStream();
      } else {
         this.this$0.tlsConnection = new TLS10Connection(this.this$0.socketConnection, "tls://" + tmp);
         this.this$0.os = this.this$0.tlsConnection.openDataOutputStream();
         this.this$0.is = this.this$0.tlsConnection.openDataInputStream();
         if (!this.this$0.tlsConnection.isConnectionEstablished()) {
            System.out.println("TLS Connection not yet established");
         } else {
            System.out.println("TLS Connection open, details:");
            System.out.println("Local address: " + this.this$0.tlsConnection.getLocalAddress());
            System.out.println("Local port: " + this.this$0.tlsConnection.getLocalPort());
            System.out.println("Remote address: " + this.this$0.tlsConnection.getAddress());
            System.out.println("Remote port: " + this.this$0.tlsConnection.getPort());
         }
      }
   }

   final Ulp receiveUlpMsg() throws IOException {
      byte[] rxBuf = new byte[65535];
      System.out.println("TLS ReceiveTask receiveUlpMsg");
      System.out.println("Blocking on TCP RX(1)");
      int ret = this.this$0.is.read(rxBuf, 0, 2);
      if (ret != 2) {
         if (ret == -1) {
            throw new IOException();
         }

         System.out.println("Returned with " + ret);
         return null;
      } else {
         System.out.println("rxBuf[0] = " + rxBuf[0] + " rxBuf[1] = " + rxBuf[1]);
         int rxPduLen = (rxBuf[0] & 255) << 8 | rxBuf[1] & 255;
         System.out.println("Decoding a received ULP message of length " + rxPduLen);
         if (rxPduLen > 0 && rxPduLen <= 65535) {
            System.out.println("Blocking on TCP RX(2)");
            ret = this.this$0.is.read(rxBuf, 2, rxPduLen - 2);
            System.out.println("Rest of ULP message is " + ret + " bytes");
            if (ret != rxPduLen - 2) {
               throw new IOException();
            }

            Ulp rxPdu = new Ulp();
            rxPdu.decode(rxBuf);

            for (int i = 0; i < rxPduLen; i++) {
               System.out.print(Integer.toHexString(255 & rxBuf[i]) + " ");
            }

            rxPdu.print();
            return rxPdu;
         } else {
            throw new IOException();
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      System.out.println("Starting TLS Receive thread");

      try {
         this.startConnection();
      } catch (Throwable var7) {
         if (this.parent != null) {
            this.parent.connectionStatusEvent(false);
         }

         System.out.println("Error establishing connection, killing TLS Receive thread");
         System.out.println(e.toString());
         return;
      }

      this.parent.connectionStatusEvent(true);

      while (true) {
         try {
            Ulp receivedUlpPdu = this.receiveUlpMsg();
            this.parent.receivedUlpPduEvent(receivedUlpPdu);
         } catch (Throwable var6) {
            System.out.println("Connection likely closed, killing TLS Receive thread");
            e.printStackTrace();
            System.out.println(e.toString());
            return;
         }
      }
   }
}
