package net.rim.device.apps.internal.memo;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.internal.i18n.CommonResource;

final class EditMemoVerb extends Verb implements SetParameter {
   private MemoModelImpl _memo;

   EditMemoVerb(MemoModelImpl memo) {
      super(627744, CommonResource.getBundle(), 16);
      this._memo = memo;
   }

   @Override
   public final Object invoke(Object parameter) {
      new MemoEditScreen(this._memo, 0, 0, false).go();
      return !ContextObject.getFlag(parameter, 5) ? new Object(39, 40) : null;
   }

   @Override
   public final void setParameter(Object parameter) {
      if (parameter instanceof MemoModelImpl) {
         this._memo = (MemoModelImpl)parameter;
      }
   }
}
