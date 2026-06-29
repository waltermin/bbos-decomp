package net.rim.device.api.io.http;

import java.io.DataInputStream;
import java.io.InputStream;

public class PushInputStream extends DataInputStream {
   public static final int DECLINE_REASON_USERREQ;
   public static final int DECLINE_REASON_USERRFS;
   public static final int DECLINE_REASON_USERPND;
   public static final int DECLINE_REASON_USERDCR;
   public static final int DECLINE_REASON_USERDCU;
   public static final int CONNECTION_SMSC;
   public static final int CONNECTION_IPV4;
   public static final int CONNECTION_IPPP_UID;

   public PushInputStream(InputStream in) {
   }

   public String getSource() {
      throw null;
   }

   public int getConnectionType() {
      throw null;
   }

   public void decline(int _1) {
      throw null;
   }

   public void accept() {
      throw null;
   }

   public boolean isChannelEncrypted() {
      throw null;
   }
}
