package net.rim.tid.im.spellcheck;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.InvokableAction;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.itie.IComponent;

public class SpellCheckAction implements InvokableAction {
   private int _actionId;
   private boolean _isDefault;

   public SpellCheckAction(int actionId, boolean isDefault) {
      this._actionId = actionId;
      this._isDefault = isDefault;
   }

   @Override
   public int getActionId() {
      return this._actionId;
   }

   @Override
   public boolean isDefault() {
      return this._isDefault;
   }

   @Override
   public void actionPerformed(Object source) {
      switch (this._actionId) {
         case 5:
            break;
         case 6:
            if (SpellCheckUtilities.activateSpellCheckIM()) {
               InputContext ic = InputContext.getInstance();
               SLControlObject co = (SLControlObject)ic.getInputMethodControlObject();
               if (co != null) {
                  co.actionPerformed(41, source);
               }
            }
            break;
         case 7:
            IComponent comp = InputContext.getInstance().getInputComponent();
            if (comp instanceof Field) {
               continueSpellCheck((Field)comp);
               return;
            }
            break;
         case 8:
         default:
            IComponent compx = InputContext.getInstance().getInputComponent();
            if (compx instanceof Field) {
               stopSpellCheck((Field)compx);
               return;
            }
      }
   }

   private static void continueSpellCheck(Field field) {
      SLControlObject co = getSLControlObject();
      if (co != null) {
         co.actionPerformed(60, field);
      }
   }

   private static void stopSpellCheck(Field field) {
      SLControlObject co = getSLControlObject();
      if (co != null) {
         co.actionPerformed(42, field);
      }
   }

   private static SLControlObject getSLControlObject() {
      return (SLControlObject)InputContext.getInstance().getInputMethodControlObject();
   }
}
