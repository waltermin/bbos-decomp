package net.rim.device.apps.internal.supl;

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
      Integer suplPort = (Integer)(new Object(this.this$0.options.getSuplPortNumber()));
      Fqdn slpAddress = this.this$0.options.getSlpAddress();
      StringBuffer strBuf = ((StringBuffer)(new Object())).append(slpAddress.domainName).append(":");
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
      System.out.println(((StringBuffer)(new Object("Opening connection to: "))).append(strBuf.toString()).toString());
      this.this$0.socketConnection = (SocketConnection)Connector.open(
         ((StringBuffer)(new Object("net.rim.device.cldc.io.socket://"))).append(tmp).toString(), 0, true
      );
      this.this$0.socketConnection.setSocketOption((byte)1, 1);
      if (!this.this$0.options.isTLSEnabled()) {
         this.this$0.os = this.this$0.socketConnection.openOutputStream();
         this.this$0.is = this.this$0.socketConnection.openInputStream();
      } else {
         this.this$0.tlsConnection = (TLS10Connection)(new Object(this.this$0.socketConnection, ((StringBuffer)(new Object("tls://"))).append(tmp).toString()));
         this.this$0.os = this.this$0.tlsConnection.openDataOutputStream();
         this.this$0.is = this.this$0.tlsConnection.openDataInputStream();
         if (!this.this$0.tlsConnection.isConnectionEstablished()) {
            System.out.println("TLS Connection not yet established");
         } else {
            System.out.println("TLS Connection open, details:");
            System.out.println(((StringBuffer)(new Object("Local address: "))).append(this.this$0.tlsConnection.getLocalAddress()).toString());
            System.out.println(((StringBuffer)(new Object("Local port: "))).append(this.this$0.tlsConnection.getLocalPort()).toString());
            System.out.println(((StringBuffer)(new Object("Remote address: "))).append(this.this$0.tlsConnection.getAddress()).toString());
            System.out.println(((StringBuffer)(new Object("Remote port: "))).append(this.this$0.tlsConnection.getPort()).toString());
         }
      }
   }

   final Ulp receiveUlpMsg() {
      byte[] rxBuf = new byte[65535];
      System.out.println("TLS ReceiveTask receiveUlpMsg");
      System.out.println("Blocking on TCP RX(1)");
      int ret = this.this$0.is.read(rxBuf, 0, 2);
      if (ret != 2) {
         if (ret == -1) {
            throw new Object();
         }

         System.out.println(((StringBuffer)(new Object("Returned with "))).append(ret).toString());
         return null;
      } else {
         System.out.println(((StringBuffer)(new Object("rxBuf[0] = "))).append(rxBuf[0]).append(" rxBuf[1] = ").append(rxBuf[1]).toString());
         int rxPduLen = (rxBuf[0] & 255) << 8 | rxBuf[1] & 255;
         System.out.println(((StringBuffer)(new Object("Decoding a received ULP message of length "))).append(rxPduLen).toString());
         if (rxPduLen > 0 && rxPduLen <= 65535) {
            System.out.println("Blocking on TCP RX(2)");
            ret = this.this$0.is.read(rxBuf, 2, rxPduLen - 2);
            System.out.println(((StringBuffer)(new Object("Rest of ULP message is "))).append(ret).append(" bytes").toString());
            if (ret != rxPduLen - 2) {
               throw new Object();
            }

            Ulp rxPdu = new Ulp();
            rxPdu.decode(rxBuf);

            for (int i = 0; i < rxPduLen; i++) {
               System.out.print(((StringBuffer)(new Object())).append(Integer.toHexString(255 & rxBuf[i])).append(" ").toString());
            }

            rxPdu.print();
            return rxPdu;
         } else {
            throw new Object();
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
