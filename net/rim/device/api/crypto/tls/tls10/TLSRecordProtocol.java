package net.rim.device.api.crypto.tls.tls10;

import javax.microedition.io.StreamConnection;
import net.rim.device.api.crypto.MAC;
import net.rim.device.api.crypto.tls.ChangeCipherSpecProtocol;
import net.rim.device.api.crypto.tls.TLSByteArrayInputStream;
import net.rim.device.api.crypto.tls.TLSNoCopyByteArrayOutputStream;
import net.rim.device.api.crypto.tls.ssl30.SSLConnectionState;
import net.rim.device.api.crypto.tls.ssl30.SSLHandshakeProtocol;
import net.rim.device.api.crypto.tls.ssl30.SSLRecordProtocol;
import net.rim.device.cldc.io.utility.URL;

public class TLSRecordProtocol extends SSLRecordProtocol {
   public static final int LOCAL_VERSION = 769;

   public TLSRecordProtocol(StreamConnection subConnection, String name, boolean client) {
      super._subConnection = subConnection;
      super._connectionName = name;

      label26:
      try {
         super._connectionURL = (URL)(new Object(super._connectionName));
      } finally {
         break label26;
      }

      super._readStream = (TLSByteArrayInputStream)(new Object());
      super._writeStream = (TLSNoCopyByteArrayOutputStream)(new Object());
      super._currentRead = new SSLConnectionState(super._readStream);
      super._currentWrite = new SSLConnectionState(super._writeStream);
      super._pendingRead = new SSLConnectionState(super._readStream);
      super._pendingWrite = new SSLConnectionState(super._writeStream);
      super._alertProtocol = new TLSAlertProtocol(this);
      super._changeCipherSpecProtocol = (ChangeCipherSpecProtocol)(new Object(this));
      if (client) {
         super._handshakeProtocol = this.createNewHandshakeProtocol(this);
      } else {
         super._input = subConnection.openDataInputStream();
         super._output = subConnection.openOutputStream();
      }
   }

   @Override
   public void macHeader(MAC mac, byte[] data, int offset, int length) {
      if (mac == null || data == null || offset < 0 || length < 0 || data.length - length < offset) {
         throw new Object();
      }

      if (super._remoteVersion != this.getLocalVersion()) {
         super.macHeader(mac, data, offset, length);
      } else {
         mac.update(data, offset, length);
      }
   }

   @Override
   public String getProtocol() {
      return "TLS";
   }

   @Override
   public int getLocalVersion() {
      return 769;
   }

   @Override
   public SSLHandshakeProtocol createNewHandshakeProtocol(SSLRecordProtocol recordProtocol) {
      return new TLSHandshakeProtocol((TLSRecordProtocol)recordProtocol);
   }
}
