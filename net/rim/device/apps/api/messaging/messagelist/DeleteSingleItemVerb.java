package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

public final class DeleteSingleItemVerb extends Verb {
   private int _menuResId;
   private Object _target;
   private Object _context;

   public DeleteSingleItemVerb(int ordering, int menuResId) {
      super(ordering);
      this._menuResId = menuResId;
      this.setParameters(null, null);
   }

   public final void setParameters(Object target, Object context) {
      this._target = target;
      this._context = context;
   }

   public final void setTarget(Object target) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void resetParameters() {
      this._target = null;
      this._context = null;
   }

   private final boolean doPrompt(boolean yesNo, int resId) {
      int defaultChoice = yesNo ? -1 : 3;
      int proceedChoice = yesNo ? 4 : 3;
      int type = yesNo ? 3 : 2;
      return Dialog.ask(type, CommonResources.getString(resId), defaultChoice) == proceedChoice;
   }

   private final boolean proceedWithDeleteOfMainMessage(ContextObject context) {
      boolean confirmed = false;
      boolean confirm = MessageListOptions.getOptions().getFlag(4);
      if (this._target instanceof DeleteConfirmationModel) {
         DeleteConfirmationModel confirmer = (DeleteConfirmationModel)this._target;
         context.setFlag(73);
         if (!confirmer.proceedWithDelete(context, false)) {
            return false;
         }

         confirmed = !context.getFlag(73);
      }

      return !confirm || confirmed || this.doPrompt(false, 3000);
   }

   @Override
   public final Object invoke(Object parameter) {
      if (this._target == null) {
         return null;
      }

      ActionProvider actionProvider = (ActionProvider)this._target;
      boolean deleteWithCurrentContext = false;
      boolean deleteWithSavedContext = false;
      ContextObject context = ContextObject.clone(this._context);
      if (ContextObject.getFlag(context, 52)) {
         if (this.doPrompt(false, 3005)) {
            deleteWithCurrentContext = true;
         }
      } else if (!context.getFlag(22) && !context.getFlag(78)) {
         if (this.proceedWithDeleteOfMainMessage(context)) {
            deleteWithCurrentContext = true;
         }
      } else if (actionProvider.perform(3456946836994320775L, null)) {
         if (this.doPrompt(false, 3005)) {
            deleteWithSavedContext = true;
         }
      } else if (this.proceedWithDeleteOfMainMessage(context)) {
         deleteWithCurrentContext = true;
         if (actionProvider.perform(3103370408204507200L, context) && this.doPrompt(true, 3008)) {
            deleteWithSavedContext = true;
         }
      }

      ContextObject invokeContextObject = new ContextObject();
      if (deleteWithCurrentContext || deleteWithSavedContext) {
         invokeContextObject.setFlag(39);
         invokeContextObject.setFlag(40);
         if (deleteWithSavedContext) {
            context.setFlag(52);
         }

         actionProvider.perform(6780594967363292755L, context);
         this.setParameters(null, null);
      }

      return invokeContextObject;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(this._menuResId);
   }
}
