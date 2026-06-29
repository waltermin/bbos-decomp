package net.rim.wica.transport.message;

import net.rim.wica.transport.Serializable;
import net.rim.wica.transport.message.commonheader.CommonHeaderV1;
import net.rim.wica.transport.message.data.DataStreamV1;
import net.rim.wica.transport.message.messageheader.MessageHeaderV1;

public interface TransportMessageV2 extends Serializable {
   CommonHeaderV1 getCommonHeader();

   MessageHeaderV1 getMessageHeader();

   DataStreamV1 getDataStream();

   TransportMessageV2 bundle(TransportMessageV2[] var1);

   TransportMessageV2[] debundle();

   int getMaxBundleSize();

   TransportMessageV2 cloneMessage();

   int size();
}
