package net.rim.device.api.browser.push;

import net.rim.device.api.util.StringUtilities;

public class PushSource {
   protected int _port;
   protected String _apn;
   protected String _apnUsername;
   protected String _apnPassword;
   public static final int MDS_SOURCE = 1;
   public static final int WAP_SOURCE = 2;
   public static final int SERIAL_SOURCE = 3;

   protected PushSource(int port, String apn, String apnUsername, String apnPassword) {
      this._port = port;
      this._apn = apn;
      this._apnUsername = apnUsername;
      this._apnPassword = apnPassword;
   }

   public int getSourceType() {
      throw null;
   }

   public void startPPGConnection(Pushlet _1) {
      throw null;
   }

   public void startListening(Pushlet _1) {
      throw null;
   }

   public void close() {
      throw null;
   }

   public void dataNetworkChanged(boolean _1) {
      throw null;
   }

   public int getPort() {
      return this._port;
   }

   public String getAPN() {
      return this._apn;
   }

   @Override
   public boolean equals(Object anObject) {
      if (this == anObject) {
         return true;
      }

      if (anObject instanceof PushSource) {
         PushSource other = (PushSource)anObject;
         if (this._port == other._port && StringUtilities.strEqualIgnoreCase(this._apn, other._apn)) {
            return true;
         }
      }

      return false;
   }

   public static PushSource[] getAllServices() {
      PushSource[] wap = WAPPushSource.getAllServices();
      PushSource[] mds = MDSPushSource.getAllServices();
      int length = wap != null ? wap.length : 0;
      if (mds != null) {
         length += mds.length;
      }

      int offset = 0;
      PushSource[] result = new PushSource[length];
      if (mds != null) {
         System.arraycopy(mds, 0, result, offset, mds.length);
         offset += mds.length;
      }

      if (wap != null) {
         System.arraycopy(wap, 0, result, offset, wap.length);
         offset += wap.length;
      }

      return result;
   }
}
