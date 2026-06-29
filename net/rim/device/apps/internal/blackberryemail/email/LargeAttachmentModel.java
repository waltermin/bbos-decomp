package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.ContentPartIDGenerator;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;

public class LargeAttachmentModel extends AbstractEmailFileAttachment implements Persistable {
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
      DataBuffer db = p.getDataBuffer();
      Parameters var12 = null;
      ContextObject contextObject = (ContextObject)context;
      ContentPartIDGenerator contentPartIDGenerator = (ContentPartIDGenerator)contextObject.get(-1943436819741481055L);
      int contentPartID = contentPartIDGenerator.generateContentPartID();
      CMIMEParameters parameters = (CMIMEParameters)(new Object(db, 2, 2));
      parameters.addCMIMEInteger((byte)-15, contentPartID);
      this.setContentPartId((short)contentPartID);
      byte[] data = new byte[0];
      parameters.addCMIMEInteger((byte)-13, (int)this.getFileSize());
      outgoingMessage.addAttachment(data, parameters, this.getContentType());
      return true;
   }

   @Override
   public Object clone(Object context) {
      LargeAttachmentModel attachmentModel = new LargeAttachmentModel();
      attachmentModel.setContentPartId(this.getContentPartId());
      attachmentModel.setContentType(this.getContentType());
      attachmentModel.setDisplayName(this.getDisplayName());
      attachmentModel.setFile(this.getFile());
      attachmentModel.setFileSize(this.getFileSize());
      return attachmentModel;
   }
}
