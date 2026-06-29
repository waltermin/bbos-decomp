package net.rim.device.cldc.io.gme;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.servicebook.ServiceRecord;

public final class GMEDatagram extends DatagramBase {
   private GMEDatagramInfo _info;
   private ServiceRecord[] _serviceRecordOverride;
   public static final int NO_ERROR;
   public static final int CMIME_GME_FAILURE;
   public static final int MORE_REQUEST_FAILURE;
   public static final int TRPT_DATAGRAM_REFUSED;
   public static final int GME_TRANSACTION_FAILURE_AT_SERVICE;
   public static final int GME_TRANSACTION_UNHANDLED_GME_CMD;
   public static final int GME_TRANSACTION_BAD_GME_FORMAT;
   public static final int GME_TRANSACTION_TIMEOUT;
   public static final int GME_TRANSACTION_INVALID_ADDRESS;
   public static final int GME_TRANSACTION_DECRYPTION_ERROR;
   public static final int GME_TRANSACTION_GENERAL_FAILURE;
   public static final int GME_TRANSACTION_DEFAULT_ERROR;
   public static final int GME_MESSAGE_DECODE_RESOURCE_FAILURE;
   public static final int GME_MESSAGE_STREAM_DECRYPT_FAILURE;
   public static final int GME_MESSAGE_STREAM_DECODE_FAILURE;
   public static final int GME_MESSAGE_MAILBOX_RESOURCE_FAILURE;
   public static final int GME_MESSAGE_MAIL_CREATE_FAILURE;
   public static final int GME_MESSAGE_MAIL_SUBMIT_FAILURE;
   public static final int GME_MESSAGE_BAD_RECIPIENT;
   public static final int GME_MESSAGE_MESSAGE_BODY_PROBLEM;
   public static final int GME_MESSAGE_BAD_MESSAGE_EXTRAS;
   public static final int GME_MESSAGE_BAD_ATTACHMENT;
   public static final int GME_MESSAGE_COULDNT_GET_ORIGINAL_MESSAGE;
   public static final int GME_MESSAGE_INVALID_MORE_PART_ID;
   public static final int GME_MESSAGE_INVALID_MORE_MSG_ID;
   public static final int GME_MESSAGE_COULDNT_GET_MORE;
   public static final int GME_MESSAGE_GENERAL_FAILURE;
   public static final int GME_MESSAGE_DEFAULT_ERROR;
   public static final int GME_MESSAGE_RETRY_ON_FAILURE_CODE;
   public static final int DSN_SEND_FAILURE;
   public static final int DSN_MESSAGE_FAILED;
   public static final int NUM_TRANSPORT_ERRORS;

   public GMEDatagram(String addr, int length) {
      this(null, 0, length, addr);
   }

   public GMEDatagram(byte[] data, int off, int len) {
      this(data, off, len, null);
   }

   public GMEDatagram(byte[] data, int off, int len, String addr) {
      super(data, off, len, addr);
      this._info = new GMEDatagramInfo();
   }

   GMEDatagram(byte[] data, int off, int len, GMEAddress address, GMEDatagramInfo info) {
      super(data, off, len, address);
      this._info = info;
      if (this._info == null) {
         this._info = new GMEDatagramInfo();
      }
   }

   public final int getTransactionId() {
      return this._info.transId;
   }

   public final int getCommandByte() {
      return this._info.cmdByte;
   }

   public final void setCommandByte(int i) {
      this._info.cmdByte = i;
   }

   @Override
   public final void reset() {
      super.reset();
      this.simpleReset();
   }

   @Override
   public final void simpleReset() {
      super.simpleReset();
      this._info.reset();
      this._serviceRecordOverride = null;
   }

   public final ServiceRecord getBoundServiceRecord() {
      return this._info.boundSr;
   }

   public final boolean wasBoundServiceRecordDisallowed() {
      return this._info.boundSrDisallowed;
   }

   public final boolean isFromPeer() {
      return this._info.fromPeer;
   }

   public final boolean wasEncrypted() {
      return this._info.wasEnterpriseEncrypted || this._info.wasWeaklyEncrypted;
   }

   public final boolean wasDatagramSecure() {
      return this._info.wasEnterpriseEncrypted && !this._info.wasWeaklyEncrypted;
   }

   public final boolean usedGlobalScramblingKey() {
      return this._info.usedGlobalScramblingKey;
   }

   public final ServiceRecord[] getServiceRecordOverride() {
      return this._serviceRecordOverride;
   }

   public final void setServiceRecordOverride(ServiceRecord[] serviceRecordOverride) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final String getKeyId() {
      return this._info.keyId;
   }

   final GMEDatagramInfo getDatagramInfo() {
      return this._info;
   }

   @Override
   public final void copy(DatagramBase datagram) {
      GMEDatagram dg = (GMEDatagram)datagram;
      super.copy(datagram);
      this._info.copyFrom(dg._info);
      this._serviceRecordOverride = ((GMEDatagram)datagram)._serviceRecordOverride;
   }

   public final GMEAddress getGMEAddress() {
      return (GMEAddress)this.getAddressBase();
   }

   @Override
   protected final DatagramAddressBase newAddressBase() {
      return new GMEAddress();
   }
}
