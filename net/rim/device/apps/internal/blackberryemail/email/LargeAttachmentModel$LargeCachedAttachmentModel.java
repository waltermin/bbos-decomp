package net.rim.device.apps.internal.blackberryemail.email;

import javax.microedition.io.InputConnection;
import net.rim.device.api.util.Persistable;

public class LargeAttachmentModel$LargeCachedAttachmentModel extends LargeAttachmentModel implements Persistable {
   private byte[] _data;

   public byte[] getData() {
      return this._data;
   }

   public void setData(byte[] data) {
      this._data = data;
   }

   @Override
   public Object clone(Object arg0) {
      LargeAttachmentModel$LargeCachedAttachmentModel attachmentModel = new LargeAttachmentModel$LargeCachedAttachmentModel();
      attachmentModel.setContentPartId(this.getContentPartId());
      attachmentModel.setContentType(this.getContentType());
      attachmentModel.setDisplayName(this.getDisplayName());
      attachmentModel.setFile(this.getFile());
      attachmentModel.setFileSize(this.getFileSize());
      attachmentModel.setData(this.getData());
      return attachmentModel;
   }

   @Override
   public InputConnection getInputConnection() {
      return new NativeAttachmentConnection(this._data, this.getContentType(), this.getFile());
   }
}
