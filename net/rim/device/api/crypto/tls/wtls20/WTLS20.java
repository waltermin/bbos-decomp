package net.rim.device.api.crypto.tls.wtls20;

import java.io.IOException;
import javax.microedition.io.SecurityInfo;
import net.rim.device.api.crypto.tls.TLSAlertException;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.ssl.TLSException;
import net.rim.vm.Array;

public final class WTLS20 implements WTLSLayerConnection {
   private WTLSRecordProtocol _recordProtocol;
   private DataBuffer _out;
   private DataBuffer _in;

   @Override
   public final void makeConnection(
      WTLSDataTransport subConnection,
      String apn,
      String name,
      boolean openwaveMode,
      int clientIdMode,
      String clientIdValue,
      int ipAddress,
      int ipPort,
      boolean wap20Conformance
   ) {
      if (subConnection != null && name != null && apn != null) {
         int flags;
         if (openwaveMode) {
            flags = 1;
         } else {
            flags = 3;
         }

         if (wap20Conformance) {
            flags |= 4;
         }

         this._recordProtocol = new WTLSRecordProtocol(subConnection, apn, name, flags, clientIdMode, clientIdValue, ipAddress, ipPort);
         this._out = new DataBuffer();
         this._in = new DataBuffer();
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void write(byte[] data, int offset, int length) {
      if (data != null && offset <= length && length >= 0 && offset >= 0 && length + offset <= data.length) {
         if (length > 0) {
            this._out.setData(data, offset, length);
            this._recordProtocol.write(this._recordProtocol.getApplicationProtocolConstant(), this._out);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final int read(byte[] buffer, int length) {
      if (buffer == null || length < 0) {
         throw new IllegalArgumentException();
      }

      if (length <= 0) {
         return 0;
      }

      while (this._recordProtocol.read(this._in) != 4) {
      }

      int count = this._in.available();
      if (count > length) {
         Array.resize(buffer, count);
      }

      System.arraycopy(this._in.getArray(), this._in.getArrayPosition(), buffer, 0, count);
      return count;
   }

   @Override
   public final SecurityInfo getRIMSecurityInfo() {
      return this._recordProtocol == null ? null : this._recordProtocol.getSecurityInfo();
   }

   @Override
   public final byte getErrorDescription(TLSException e) {
      if (!(e instanceof TLSAlertException)) {
         if (e.getException() instanceof IOException) {
            return -1;
         } else {
            return (byte)(e.getException() instanceof RadioException ? -3 : -2);
         }
      } else {
         return ((TLSAlertException)e).getAlertDescription();
      }
   }
}
