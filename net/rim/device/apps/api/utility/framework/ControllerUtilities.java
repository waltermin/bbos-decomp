package net.rim.device.apps.api.utility.framework;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.ApplicationKeyInvocableVerb;
import net.rim.device.apps.api.framework.verb.PopupVerbWrapper;
import net.rim.device.apps.api.framework.verb.SendKeyInvocableVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.WrapperVerb;
import net.rim.vm.Array;

public class ControllerUtilities {
   private ControllerUtilities() {
   }

   public static boolean invokeApplicationKeyVerb(Object object) {
      if (object instanceof VerbProvider) {
         VerbProvider verbProvider = (VerbProvider)object;
         Verb[] verbs = new Verb[0];
         ContextObject verbContext = new ContextObject();
         verbContext.setFlag(102);
         verbContext.setFlag(2);
         verbProvider.getVerbs(verbContext, verbs);
         int length = verbs.length;

         for (int index = 0; index < length; index++) {
            if (verbs[index] instanceof ApplicationKeyInvocableVerb) {
               verbs[index].invoke(verbContext);
               return true;
            }
         }
      }

      return false;
   }

   private static Object doInvokeSendKeyVerb(Verb verb) {
      if (verb instanceof PopupVerbWrapper) {
         return verb.invoke(null);
      }

      if (verb instanceof SendKeyInvocableVerb) {
         return verb.invoke(null);
      }

      if (verb instanceof WrapperVerb) {
         Verb innerVerb = ((WrapperVerb)verb).getInnerVerb();
         if (innerVerb instanceof SendKeyInvocableVerb) {
            return innerVerb.invoke(null);
         }
      }

      return null;
   }

   private static boolean isSendKeyInvocableVerb(Verb verb) {
      if (verb instanceof SendKeyInvocableVerb) {
         return true;
      }

      if (verb instanceof WrapperVerb) {
         Verb innerVerb = ((WrapperVerb)verb).getInnerVerb();
         if (innerVerb instanceof SendKeyInvocableVerb) {
            return true;
         }
      }

      return false;
   }

   public static boolean invokeSendKeyVerb(Object model) {
      return invokeSendKeyVerb(model, null);
   }

   public static boolean invokeSendKeyVerb(Object model, Object context) {
      if (!(model instanceof VerbProvider)) {
         return false;
      }

      ContextObject contextObject = ContextObject.clone(context);
      contextObject.setFlag(2, 119);
      Verb[] verbs = new Verb[0];
      ((VerbProvider)model).getVerbs(contextObject, verbs);
      Object verbResult = null;
      int sendKeyVerbCount = 0;
      Verb[] sendKeyVerbs = new Verb[verbs.length];

      for (int i = 0; i < verbs.length; i++) {
         if (isSendKeyInvocableVerb(verbs[i])) {
            sendKeyVerbs[sendKeyVerbCount++] = verbs[i];
         }
      }

      switch (sendKeyVerbCount) {
         case -1:
            Array.resize(sendKeyVerbs, sendKeyVerbCount);
            VerbToMenu verbToMenu = VerbToMenuFactory.createInstance();
            verbToMenu.clear();
            verbToMenu.addVerbs(sendKeyVerbs);
            verbToMenu.coalesce(-3072555018635390988L, null);
            sendKeyVerbs = verbToMenu.getVerbs();
            if (sendKeyVerbs != null) {
               for (int i = 0; i < sendKeyVerbs.length; i++) {
                  verbResult = doInvokeSendKeyVerb(sendKeyVerbs[i]);
                  if (verbResult != null) {
                     break;
                  }
               }
            }
            break;
         case 0:
         default:
            return false;
         case 1:
            verbResult = sendKeyVerbs[0].invoke(null);
      }

      if (verbResult != null && ContextObject.getFlag(verbResult, 39) && context instanceof ContextObject) {
         ContextObject.setFlag(context, 39);
      }

      return true;
   }
}
