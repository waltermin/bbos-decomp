package net.rim.device.cldc.io.sms;

import java.util.Date;
import javax.wireless.messaging.Message;

public class MessageImpl implements Message {
   private String _address;
   private String _scAddress;
   Date _date;
   byte[] _buffer;
   int _encoding;

   void setBuffer(byte[] b) {
      this._buffer = b;
   }

   void setEncoding(int coding) {
      this._encoding = coding;
   }

   int getEncoding() {
      return this._encoding;
   }

   public byte[] getBuffer() {
      return this._buffer;
   }

   public String getSCAddress() {
      return this._scAddress;
   }

   public void setSCAddress(String value) {
      this._scAddress = value;
   }

   @Override
   public void setAddress(String addr) {
      this._address = addr;
   }

   @Override
   public Date getTimestamp() {
      return this._date;
   }

   @Override
   public String getAddress() {
      return this._address;
   }

   public MessageImpl(String address) {
      this._address = address;
   }
}
