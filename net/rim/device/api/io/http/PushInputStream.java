package net.rim.device.api.io.http;

import java.io.DataInputStream;
import java.io.InputStream;

public class PushInputStream extends DataInputStream {
   public static final int DECLINE_REASON_USERREQ = 234;
   public static final int DECLINE_REASON_USERRFS = 235;
   public static final int DECLINE_REASON_USERPND = 236;
   public static final int DECLINE_REASON_USERDCR = 237;
   public static final int DECLINE_REASON_USERDCU = 238;
   public static final int CONNECTION_SMSC = 1;
   public static final int CONNECTION_IPV4 = 2;
   public static final int CONNECTION_IPPP_UID = 3;

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
