package net.rim.device.apps.internal.memo;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.internal.i18n.CommonResource;

final class MemoList$NewMemoVerb extends Verb implements SetParameter, Copyable {
   private String _pattern;
   private MemoModelImpl _newMemo;

   MemoList$NewMemoVerb(String pattern) {
      super(627712, CommonResource.getBundle(), 10023);
      this._pattern = pattern;
      this._newMemo = null;
   }

   @Override
   public final Object invoke(Object context) {
      MemoEditScreen memoEditScreen = new MemoEditScreen(this._newMemo, this._pattern, -1, -1, true);
      return memoEditScreen.go();
   }

   @Override
   public final void setParameter(Object parameter) {
      if (parameter instanceof MemoModelImpl) {
         this._newMemo = (MemoModelImpl)parameter;
      }
   }

   @Override
   public final Object copy() {
      return new MemoList$NewMemoVerb("");
   }
}
