package net.rim.device.cldc.io.gme;

import java.util.Vector;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.DataBuffer;
import net.rim.vm.Array;

final class GMEDatagramInfo {
   public int headerLength;
   public boolean requestACK;
   public int transId;
   public int cmdByte;
   public GMEAddress address;
   public String keyId;
   public HostRoutingInfo dstHri;
   public boolean compress;
   public boolean encrypt;
   public String source;
   public Vector cryptoAliases = (Vector)(new Object(1));
   public DatagramStatusListener listener;
   public boolean passUpSubLayerEvents;
   public int retryCount;
   public boolean peer2PeerKeyAdded;
   public int errorEventCode;
   public Object errorEventContext;
   public DataBuffer dataBuffer;
   public boolean failWhenMissing;
   public ServiceRecord serviceRecord;
   public ServiceRecord[] serviceRecords;
   public ServiceRecord boundSr;
   public boolean boundSrDisallowed;
   public boolean fromPeer;
   public DatagramAddressBase srcAddr;
   public boolean wasEnterpriseEncrypted;
   public boolean wasWeaklyEncrypted;
   public boolean usedGlobalScramblingKey;
   public DatagramConnectionBase subConnection;

   public GMEDatagramInfo() {
      this.reset();
   }

   public final void reset() {
      this.dstHri = null;
      this.encrypt = false;
      this.compress = false;
      this.headerLength = 0;
      this.requestACK = false;
      this.source = null;
      this.address = null;
      this.cryptoAliases.removeAllElements();
      this.listener = null;
      this.transId = 0;
      this.passUpSubLayerEvents = true;
      this.retryCount = 0;
      this.cmdByte = 3;
      this.boundSr = null;
      this.boundSrDisallowed = false;
      this.fromPeer = false;
      this.srcAddr = null;
      this.wasEnterpriseEncrypted = false;
      this.wasWeaklyEncrypted = false;
      this.usedGlobalScramblingKey = false;
      this.peer2PeerKeyAdded = false;
      this.errorEventCode = -1;
      this.errorEventContext = null;
      this.dataBuffer = null;
      this.keyId = null;
      this.failWhenMissing = false;
      this.serviceRecord = null;
      this.serviceRecords = null;
      this.subConnection = null;
   }

   public final void copyFrom(GMEDatagramInfo info) {
      this.dstHri = info.dstHri;
      this.encrypt = info.encrypt;
      this.compress = info.compress;
      this.headerLength = info.headerLength;
      this.requestACK = info.requestACK;
      this.source = info.source;
      this.address = info.address;
      this.listener = info.listener;
      this.transId = info.transId;
      this.passUpSubLayerEvents = info.passUpSubLayerEvents;
      this.retryCount = info.retryCount;
      this.cmdByte = info.cmdByte;
      this.boundSr = info.boundSr;
      this.boundSrDisallowed = info.boundSrDisallowed;
      this.fromPeer = info.fromPeer;
      this.srcAddr = info.srcAddr;
      this.wasEnterpriseEncrypted = info.wasEnterpriseEncrypted;
      this.wasWeaklyEncrypted = info.wasWeaklyEncrypted;
      this.usedGlobalScramblingKey = info.usedGlobalScramblingKey;
      this.peer2PeerKeyAdded = info.peer2PeerKeyAdded;
      this.errorEventCode = info.errorEventCode;
      this.errorEventContext = info.errorEventContext;
      this.dataBuffer = info.dataBuffer;
      this.keyId = info.keyId;
      this.failWhenMissing = info.failWhenMissing;
      this.serviceRecord = info.serviceRecord;
      this.serviceRecords = info.serviceRecords;
      this.subConnection = info.subConnection;
      this.cryptoAliases.removeAllElements();

      for (int i = 0; i < info.cryptoAliases.size(); i++) {
         this.cryptoAliases.addElement(info.cryptoAliases.elementAt(i));
      }
   }

   public final void setServiceRecord(ServiceRecord sr, int i) {
      if (i == 0) {
         this.serviceRecord = sr;
      } else {
         if (this.serviceRecords == null) {
            this.serviceRecords = new ServiceRecord[i];
         } else if (this.serviceRecords.length < i) {
            Array.resize(this.serviceRecords, i);
         }

         this.serviceRecords[i - 1] = sr;
      }
   }

   public final ServiceRecord getServiceRecord(int i) {
      if (i == 0) {
         return this.serviceRecord;
      } else {
         return this.serviceRecords != null && this.serviceRecords.length >= i ? this.serviceRecords[i - 1] : null;
      }
   }
}
