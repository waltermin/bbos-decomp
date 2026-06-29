package net.rim.device.apps.internal.help;

import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;

final class HelpVerbFactory implements VerbFactory {
   @Override
   public final Verb[] getVerbs(Object context) {
      if (context instanceof Object) {
         ContextObject contextObj = (ContextObject)context;
         Object topic = contextObj.get(244);
         if (!(topic instanceof Object)) {
            if (topic instanceof Object) {
               return new Object[]{new HelpVerb(topic.toString())};
            }
         } else {
            String topicStr = (String)topic;
            if (includeHelpVerb(topicStr)) {
               return new Object[]{new HelpVerb(topicStr)};
            }
         }
      }

      return null;
   }

   private static final boolean includeHelpVerb(String topic) {
      int slash = topic.indexOf(47);
      if (slash == -1 || topic.indexOf(58) != -1) {
         return true;
      } else {
         return slash > 0 && topic.startsWith("net_rim_bb_secureemail_help/")
            ? CodeModuleManager.getModuleHandle("net_rim_bb_pgp") != 0 || CodeModuleManager.getModuleHandle("net_rim_bb_smime") != 0
            : HelpScreen.getModuleName(topic.substring(0, slash)) != null;
      }
   }
}
