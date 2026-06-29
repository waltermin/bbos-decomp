package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.DataBuffer;

public class RIMMessagingIncomingMoreRequest extends RIMMessagingMessage {
   private int contentPartId = Integer.MIN_VALUE;
   private int refId = Integer.MIN_VALUE;
   private int offset = Integer.MIN_VALUE;
   private int length = Integer.MIN_VALUE;
   private byte[] serverComponentId;
   private static final int INDEX_MESSAGE_REF_ID = 0;
   private static final int INDEX_CONTENT_ID = 1;
   private static final int INDEX_OFFSET = 2;
   private static final int INDEX_LENGTH = 3;

   public byte[] getComponentId() {
      return this.serverComponentId;
   }

   public int getContentPartId() {
      return this.contentPartId;
   }

   public int getOffset() {
      return this.offset;
   }

   public int getLength() {
      return this.length;
   }

   public int getMessageRefId() {
      return this.refId;
   }

   @Override
   public void read(DataBuffer packetDataBuffer) {
      byte[] data = new byte[packetDataBuffer.getLength() - 1];
      System.arraycopy(packetDataBuffer.getArray(), packetDataBuffer.getArrayStart() + 1, data, 0, data.length);
      CMIMEParameters moreReqParams = new CMIMEParameters((DataBuffer)(new Object()), 5, 1);
      moreReqParams.getDataBuffer().setData(data, 0, data.length);
      moreReqParams.read();
      this.contentPartId = CMIMEUtilities.getGMEInteger((byte[][][])moreReqParams.get((byte)48), 1);
      this.refId = CMIMEUtilities.getGMEInteger((byte[][][])moreReqParams.get((byte)48), 0);
      this.serverComponentId = moreReqParams.getFirst((byte)-128);
      this.offset = CMIMEUtilities.getGMEInteger((byte[][][])moreReqParams.get((byte)48), 2);
      this.length = CMIMEUtilities.getGMEInteger((byte[][][])moreReqParams.get((byte)48), 3);
   }
}
