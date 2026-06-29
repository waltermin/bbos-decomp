package net.rim.device.apps.internal.explorer.file;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.verbs.FileExplorerVerb;
import net.rim.device.apps.internal.explorer.file.verbs.RenameFileVerb;

final class ExplorerVerbFactory implements VerbFactory {
   @Override
   public final Verb[] getVerbs(Object context) {
      if (ContextObject.getFlag(context, 5)) {
         return new Verb[]{new FileSelectionVerb(context)};
      }

      if (ContextObject.getFlag(context, 45)) {
         return new Verb[]{new FileExplorerVerb(context)};
      }

      Object obj = ContextObject.get(context, 2765042845091913199L);
      return obj instanceof String ? new Verb[]{new RenameFileVerb(context, null)} : null;
   }
}
