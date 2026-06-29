package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.model.PresentationModelFactory;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;

class PDUPresentationElementField extends VerticalFieldManager implements PresentationElement, MMSAttachment {
   private boolean _isEditable;
   private MMSAttachment _attachment;
   private MMSProtocolDataUnit _pdu;

   public PDUPresentationElementField(MMSAttachment attachment, boolean isEditable) {
      this._attachment = attachment;
      this._isEditable = isEditable;
      this._pdu = new MMSProtocolDataUnit(this._attachment.getData());
      MMSAttachment smil = PresentationModelFactory.findPresentationAttachment(this._pdu.attachmentNames(), this._pdu);
      if (smil != null) {
         this.add(new BrowserPresentationElementField(smil, this._isEditable, this._pdu, this._pdu.isForwardLocked()));
      }
   }

   @Override
   public void copyTo(MMSPresentationModel target) {
      target.addPresentationElement(this.getName(), this.getType(), this.isEditable());
   }

   @Override
   public boolean canMove() {
      return false;
   }

   @Override
   public void move(boolean mode) {
      throw new IllegalArgumentException();
   }

   @Override
   public String getName() {
      return this._attachment.getName();
   }

   @Override
   public int getType() {
      return this._attachment.getType();
   }

   @Override
   public byte[] getData() {
      return this._attachment.getData();
   }

   @Override
   public String getCharset() {
      return this._attachment.getCharset();
   }

   @Override
   public int getDataSize() {
      return this._attachment.getDataSize();
   }
}
