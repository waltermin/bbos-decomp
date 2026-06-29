package net.rim.wica.transport.message;

import net.rim.wica.transport.Serializable;
import net.rim.wica.transport.message.commonheader.CommonHeaderV1;
import net.rim.wica.transport.message.data.DataStreamV1;
import net.rim.wica.transport.message.messageheader.MessageHeaderV1;

public interface TransportMessageV1 extends Serializable {
   CommonHeaderV1 getCommonHeader();

   MessageHeaderV1 getMessageHeader();

   DataStreamV1 getDataStream();

   TransportMessageV1 bundle(TransportMessageV1[] var1);

   TransportMessageV1[] debundle();

   int getMaxBundleSize();

   TransportMessageV1 cloneMessage();

   int size();
}
