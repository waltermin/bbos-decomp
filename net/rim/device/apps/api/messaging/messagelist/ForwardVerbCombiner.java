package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.VerbCombiner;

final class ForwardVerbCombiner implements VerbCombiner {
   Verb _parent;

   ForwardVerbCombiner(Verb parent) {
      this._parent = parent;
   }

   @Override
   public final boolean recognize(Object object) {
      return false;
   }

   @Override
   public final Verb createWrapperVerb(Verb[] verbs, Verb defaultVerb) {
      String[] descriptions = new Object[verbs.length];
      String name = this._parent.toString();

      for (int i = verbs.length - 1; i >= 0; i--) {
         descriptions[i] = verbs[i].toString();
      }

      return (Verb)(new Object(name, name, this._parent.getOrdering(), verbs, descriptions, defaultVerb));
   }
}
