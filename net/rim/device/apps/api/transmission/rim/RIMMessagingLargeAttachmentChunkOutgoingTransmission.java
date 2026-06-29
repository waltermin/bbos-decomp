package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.DataBuffer;

public class RIMMessagingLargeAttachmentChunkOutgoingTransmission extends RIMMessagingTransmission {
   private int _refId;
   private int _contentPartId;
   private byte[] _data;
   private int _offset;
   private byte[] _componentIdData;

   public void init(byte[] data, int refId, int contentPartId, int offset, byte[] componentIdData) {
      this._refId = refId;
      this._contentPartId = contentPartId;
      this._data = data;
      this._offset = offset;
      this._componentIdData = componentIdData;
   }

   public void reset() {
      this._refId = -1;
      this._contentPartId = -1;
      this._data = null;
      this._offset = -1;
      this._componentIdData = null;
   }

   @Override
   public DataBuffer write() {
      DataBuffer result = (DataBuffer)(new Object(true));
      result.writeByte(5);
      CMIMEParameters parameters = new CMIMEParameters(result, 5, 1);
      parameters.addCMIMEInteger((byte)48, this._refId);
      parameters.addCMIMEInteger((byte)48, this._contentPartId);
      parameters.addCMIMEInteger((byte)48, this._offset);
      parameters.addCMIMEInteger((byte)48, this._data.length);
      parameters.add((byte)64, this._data);
      if (this._componentIdData != null) {
         parameters.add((byte)-128, this._componentIdData);
      }

      return result;
   }
}
