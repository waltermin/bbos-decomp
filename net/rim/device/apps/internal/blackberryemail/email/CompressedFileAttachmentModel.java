package net.rim.device.apps.internal.blackberryemail.email;

import javax.microedition.io.InputConnection;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.ContentPartIDGenerator;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;

public class CompressedFileAttachmentModel extends AbstractEmailFileAttachment implements Persistable {
   private byte[] _compressedData;
   private static final int MAX_SIZE = 47616;

   public byte[] getData() {
      return this._compressedData;
   }

   public void setData(byte[] data) {
      this._compressedData = data;
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (!(target instanceof Object)) {
         return false;
      }

      RIMMessagingOutgoingMessage outgoingMessage = (RIMMessagingOutgoingMessage)target;
      String fileName = this.getDisplayName();
      if (fileName == null) {
         fileName = NativeAttachmentRequestProcessor$Helper.getUnknownFileName(null);
      }

      Parameters p = CMIMEUtilities.createContentDispositionParameters(outgoingMessage, fileName);
      if (p == null) {
         return false;
      }

      DataBuffer db = p.getDataBuffer();
      Parameters var11 = null;
      ContextObject contextObject = (ContextObject)context;
      ContentPartIDGenerator contentPartIDGenerator = (ContentPartIDGenerator)contextObject.get(-1943436819741481055L);
      int contentPartID = contentPartIDGenerator.generateContentPartID();
      CMIMEParameters parameters = (CMIMEParameters)(new Object(db, 2, 2));
      parameters.addCMIMEInteger((byte)-15, contentPartID);
      this.setContentPartId((short)contentPartID);
      parameters.addCMIMEInteger((byte)-13, (int)this.getFileSize());
      outgoingMessage.addAttachment(this.getData(), parameters, this.getContentType());
      return true;
   }

   @Override
   public Object clone(Object context) {
      CompressedFileAttachmentModel attachmentModel = new CompressedFileAttachmentModel();
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
      return new NativeAttachmentConnection(this._compressedData, this.getContentType(), this.getFile());
   }
}
