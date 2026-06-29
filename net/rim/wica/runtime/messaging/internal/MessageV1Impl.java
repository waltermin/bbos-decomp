package net.rim.wica.runtime.messaging.internal;

import java.util.Vector;
import net.rim.wica.runtime.messaging.MessageException;
import net.rim.wica.transport.message.TransportMessageException;
import net.rim.wica.transport.message.TransportMessageV1;
import net.rim.wica.transport.message.commonheader.CommonHeaderV1;
import net.rim.wica.transport.message.data.DataStreamV1;
import net.rim.wica.transport.message.messageheader.MessageHeaderV1;

public class MessageV1Impl extends MessageImpl {
   private TransportMessageV1 _transportMessage;

   public MessageV1Impl(TransportMessageV1 tm) {
      this._transportMessage = tm;
      super._agId = this.getCommonHeader().getSenderId();
   }

   public TransportMessageV1 getTransportMessage() {
      return this._transportMessage;
   }

   @Override
   protected CommonHeaderV1 getCommonHeader() {
      return this._transportMessage.getCommonHeader();
   }

   @Override
   protected MessageHeaderV1 getMessageHeader() {
      return this._transportMessage.getMessageHeader();
   }

   @Override
   protected DataStreamV1 getDataStream() {
      return this._transportMessage.getDataStream();
   }

   @Override
   public MessageImpl[] debundle() {
      int bundleSize = this.getCommonHeader().getMessageCount();
      if (bundleSize == 1) {
         return new MessageV1Impl[]{this};
      }

      try {
         TransportMessageV1[] tma = this._transportMessage.debundle();
         MessageV1Impl[] ma = new MessageV1Impl[bundleSize];

         for (int i = 0; i < bundleSize; i++) {
            ma[i] = new MessageV1Impl(tma[i]);
         }

         return ma;
      } catch (TransportMessageException e) {
         throw new MessageException("Failed to dedundle message.", e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public MessageImpl bundle(Vector messages) {
      int bundleSize = messages.size();
      TransportMessageV1[] tma = new TransportMessageV1[bundleSize];

      try {
         for (int i = 0; i < bundleSize; i++) {
            MessageV1Impl next = (MessageV1Impl)messages.elementAt(i);
            tma[i] = next.getTransportMessage();
         }

         TransportMessageV1 bundle = tma[0].bundle(tma);
         return new MessageV1Impl(bundle);
      } catch (Throwable var7) {
         throw new MessageException(e);
      }
   }

   @Override
   public int getVersion() {
      return 1;
   }

   @Override
   public byte[] serialize() {
      return this._transportMessage.serialize();
   }
}
