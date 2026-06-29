package net.rim.wica.runtime.messaging.internal;

import java.util.Vector;
import net.rim.wica.runtime.comm.Response;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.ReadableDataStream;
import net.rim.wica.runtime.messaging.WritableDataStream;
import net.rim.wica.transport.message.commonheader.CommonHeaderV1;
import net.rim.wica.transport.message.data.DataStreamV1;
import net.rim.wica.transport.message.messageheader.MessageHeaderV1;

public class MessageImpl implements Message {
   protected long _agId;
   protected String _serviceId;
   protected String _messageName;
   protected int _destinationType;
   protected int _securityMode;
   private Response _response;

   public int getVersion() {
      throw null;
   }

   public MessageImpl[] debundle() {
      throw null;
   }

   public long getDeviceId() {
      return this.getCommonHeader().getSenderId();
   }

   public void setDeviceId(long deviceId) {
      this.getCommonHeader().setSenderId(deviceId);
   }

   public MessageImpl bundle(Vector _1) {
      throw null;
   }

   protected DataStreamV1 getDataStream() {
      throw null;
   }

   protected MessageHeaderV1 getMessageHeader() {
      throw null;
   }

   protected CommonHeaderV1 getCommonHeader() {
      throw null;
   }

   public int getDestinationType() {
      return this._destinationType;
   }

   public int getSecurityMode() {
      return this._securityMode;
   }

   public Response getResponse() {
      return this._response;
   }

   public boolean hasResponse() {
      return this._response != null;
   }

   public void setResponse(Response response) {
      this._response = response;
   }

   public boolean keepLast() {
      return this.getMessageHeader().keepLast();
   }

   public boolean hasMore() {
      return this.getCommonHeader().hasMore();
   }

   public int getMessageCount() {
      return this.getCommonHeader().getMessageCount();
   }

   public boolean isBundle() {
      return this.getCommonHeader().isBundle();
   }

   public boolean isNotification() {
      return this.getMessageHeader().isNotification();
   }

   public boolean backgroundProcessingEnabled() {
      return this.getMessageHeader().backgroundProcessingEnabled();
   }

   @Override
   public void setMessageName(String messageName) {
      this._messageName = messageName;
   }

   @Override
   public String getMessageName() {
      return this._messageName;
   }

   @Override
   public void setMessageCode(int messageCode) {
      this.getMessageHeader().setMessageCode(messageCode);
   }

   @Override
   public int getMessageCode() {
      return this.getMessageHeader().getMessageCode();
   }

   @Override
   public void setDestinationType(int destinationType) {
      this._destinationType = destinationType;
   }

   @Override
   public void setSecurityMode(int securityMode) {
      if (securityMode != 2 && securityMode != 0 && securityMode != 1) {
         throw new Object();
      }

      this._securityMode = securityMode;
   }

   @Override
   public ReadableDataStream openReadableDataStream() {
      DataStreamV1 dataStream = this.getDataStream();
      dataStream.rewind();
      return new ReadableDataStream(dataStream);
   }

   @Override
   public WritableDataStream openWritableDataStream() {
      return new WritableDataStream(this.getDataStream());
   }

   @Override
   public void setServiceID(String serviceId) {
      this._serviceId = serviceId;
   }

   @Override
   public String getServiceID() {
      return this._serviceId;
   }

   @Override
   public void setWicletID(long wicletId) {
      this.getCommonHeader().setWicletId(wicletId);
   }

   @Override
   public long getWicletID() {
      return this.getCommonHeader().getWicletId();
   }

   @Override
   public void setAGID(long agId) {
      this._agId = agId;
   }

   @Override
   public long getAGID() {
      return this._agId;
   }

   @Override
   public byte[] serialize() {
      throw null;
   }

   @Override
   public String toString() {
      StringBuffer str = (StringBuffer)(new Object("Message[ "));
      if (this._agId != 0) {
         str.append("ServerId=").append(this._agId).append(",");
      }

      if (this._serviceId != null) {
         str.append("ServiceId=").append(this._serviceId).append(",");
      }

      if (this._messageName != null) {
         str.append("MessageName=").append(this._messageName).append(",");
      }

      str.append("Version=").append(this.getVersion());
      CommonHeaderV1 ch = this.getCommonHeader();
      MessageHeaderV1 mh = this.getMessageHeader();
      str.append(" Common Headers[")
         .append("MessageCount=")
         .append(ch.getMessageCount())
         .append(",SenderId=")
         .append(ch.getSenderId())
         .append(",WicletId=")
         .append(ch.getWicletId())
         .append(",HaveMore=")
         .append(ch.hasMore())
         .append("]");
      str.append(" Message Headers[")
         .append("MessageCode=")
         .append(mh.getMessageCode())
         .append(",Notification=")
         .append(mh.isNotification())
         .append(",Background=")
         .append(this.backgroundProcessingEnabled())
         .append(",KeepLast=")
         .append(this.keepLast())
         .append(",PayloadLength=")
         .append(mh.getMessageLength())
         .append("]");
      str.append("]");
      return str.toString();
   }
}
