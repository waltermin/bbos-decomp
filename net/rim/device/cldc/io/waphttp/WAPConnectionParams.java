package net.rim.device.cldc.io.waphttp;

import net.rim.device.api.hrt.DAC;
import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.hrt.IPv4UdpDAC;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.internal.browser.wap.WAPServiceRecord;

public final class WAPConnectionParams {
   String _authUsername;
   String _authPassword;
   boolean _connectionMode = true;
   boolean _secureAccess;
   int _srcPort;
   int _wapServerAddress;
   String _wapServerAPN;
   int _wapServerPort;
   boolean _useSessionResume;
   int _wtlsClientIdType;
   String _wtlsClientIdValue;
   int _wtlsMode;
   int _wap20Conformance;
   int _timeout = 30000;

   public final int getServer() {
      return this._wapServerAddress;
   }

   public final int getDestPort() {
      return this._wapServerPort;
   }

   public final String getAPN() {
      return this._wapServerAPN;
   }

   public final boolean loadFrom(String uid) {
      ServiceRecord rec = ServiceBook.getSB().getRecordByUidAndCid(uid, WAPServiceRecord.SERVICE_CID);
      if (rec == null) {
         return false;
      }

      HostRoutingTable hrt = rec.getAttachedHrt();
      if (hrt == null) {
         return false;
      }

      HostRoutingInfo hri = hrt.getHris()[0];
      if (hri instanceof GprsHRI) {
         this._wapServerAPN = ((GprsHRI)hri).getApn();
      } else {
         this._wapServerAPN = "";
      }

      WAPServiceRecord record = WAPServiceRecord.getRecord(rec);
      this._connectionMode = true;
      this._secureAccess = record.getSecureAccess() == 1;
      this._useSessionResume = record.getSessionResume() == 1;
      this._authUsername = record.getAuthUsername();
      this._authPassword = record.getAuthPassword();
      this._wtlsMode = record.getWtlsModeValue();
      this._wtlsClientIdType = record.getWtlsClientIdType();
      this._wtlsClientIdValue = record.getWtlsClientIdValue();
      this._wap20Conformance = record.getWAP20Conformance();
      DAC dac = (DAC)hri.getDac();
      if (!(dac instanceof IPv4UdpDAC)) {
         return false;
      } else {
         long[] addresses = ((IPv4UdpDAC)dac).getAddresses();
         if (addresses != null && addresses.length != 0) {
            this._wapServerAddress = IPv4UdpDAC.addr2IpAddress(addresses[0]);
            this._srcPort = IPv4UdpDAC.addr2SrcPort(addresses[0]);
            this._wapServerPort = IPv4UdpDAC.addr2DstPort(addresses[0]);
            return true;
         } else {
            return false;
         }
      }
   }
}
