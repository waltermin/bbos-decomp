package net.rim.device.apps.internal.mms.model;

import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;

final class SlideBreakPart implements PresentationPart {
   private int _duration;
   private boolean _isEditable;

   public SlideBreakPart(int duration, boolean isEditable) {
      this._duration = duration;
      this._isEditable = isEditable;
   }

   @Override
   public final void copyTo(MMSPresentationModel target) {
      target.addSlideBreak(this._duration, this._isEditable);
   }

   @Override
   public final void writeData(SyncBuffer syncBuffer) {
      syncBuffer.addInt(3, this._isEditable ? 1 : 0, 1);
      syncBuffer.addInt(4, this._duration, 1);
   }

   @Override
   public final int getTaggedFieldSize() {
      return 3;
   }
}
