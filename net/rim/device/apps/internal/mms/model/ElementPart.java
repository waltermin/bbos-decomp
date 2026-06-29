package net.rim.device.apps.internal.mms.model;

import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;

final class ElementPart implements PresentationPart {
   private String _name;
   private int _type;
   private boolean _isEditable;
   private boolean _isForwardLocked;

   public ElementPart(String name, int type, boolean isEditable, boolean isForwardLocked) {
      this._name = name;
      this._type = type;
      this._isEditable = isEditable;
      this._isForwardLocked = isForwardLocked;
   }

   @Override
   public final void copyTo(MMSPresentationModel target) {
      target.addPresentationElement(this._name, this._type, this._isEditable, this._isForwardLocked);
   }

   @Override
   public final void writeData(SyncBuffer syncBuffer) {
      syncBuffer.addInt(3, this._isEditable ? 1 : 0, 1);
      syncBuffer.addInt(2, this._type, 4);
      syncBuffer.addField(1, this._name);
   }

   @Override
   public final int getTaggedFieldSize() {
      return 15 + this._name.length();
   }
}
