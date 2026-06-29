package net.rim.device.apps.internal.memo;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.memo.resources.MemoResources;
import net.rim.device.internal.i18n.CommonResource;

final class DeleteMemoVerb extends Verb {
   private MemoModelImpl _memo;
   private char _keyPressed;

   public DeleteMemoVerb(MemoModelImpl memo) {
      super(627760, CommonResource.getBundle(), 17);
      this._memo = memo;
   }

   public DeleteMemoVerb(MemoModelImpl memo, char keyPressed) {
      this(memo);
      this._keyPressed = keyPressed;
   }

   @Override
   public final Object invoke(Object parameter) {
      if ((MemoOptions.getOptions().getConfirmDelete() || this._keyPressed == '\b') && Dialog.ask(2, MemoResources.getString(152), -1) != 3) {
         return null;
      }

      MemoCollectionImpl.getInstance().remove(this._memo);
      return !ContextObject.getFlag(parameter, 5) ? new ContextObject(39, 40) : null;
   }
}
