package net.rim.device.apps.api.framework.registration;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.vm.Array;

public class VerbRepository implements VerbRegistry, VerbFactory {
   private long[] _types = new long[5];
   private Verb[] _verbs = new Verb[5];
   private int _numVerbs;
   private static LongHashtable _factories = ApplicationRegistry.getApplicationRegistry().getLongHashtable(-8094929924439376137L);
   private static final int GROW_SIZE = 5;
   private static final long RIM_VERB_REPOSITORY_ID = -8094929924439376137L;

   public synchronized Verb[] getVerbs(long context) {
      Verb[] verbs = new Verb[0];

      for (int i = 0; i < this._numVerbs; i++) {
         if (context == this._types[i] || 4738722199580714034L == this._types[i]) {
            Arrays.add(verbs, this._verbs[i]);
         }
      }

      return verbs;
   }

   @Override
   public synchronized Verb[] getVerbs(Object context) {
      Verb[] verbs = new Verb[0];

      for (int i = 0; i < this._numVerbs; i++) {
         Verb verb = this._verbs[i];
         if (!Arrays.contains(verbs, verb)) {
            Arrays.add(verbs, verb);
         }
      }

      return verbs;
   }

   @Override
   public synchronized void register(Verb verb, long type) {
      if (this._types.length == this._numVerbs) {
         Array.resize(this._types, this._numVerbs + 5);
         Array.resize(this._verbs, this._numVerbs + 5);
      }

      this._types[this._numVerbs] = type;
      this._verbs[this._numVerbs] = verb;
      this._numVerbs++;
   }

   @Override
   public synchronized void deregister(Verb verb, long type) {
      for (int i = 0; i < this._numVerbs; i++) {
         if (this._verbs[i].equals(verb)) {
            System.arraycopy(this._verbs, i + 1, this._verbs, i, this._numVerbs - i - 1);
            System.arraycopy(this._types, i + 1, this._types, i, this._numVerbs - i - 1);
            this._numVerbs--;
            return;
         }
      }
   }

   public static VerbRepository getVerbRepository(long factoryType) {
      synchronized (_factories) {
         VerbRepository factory = (VerbRepository)_factories.get(factoryType);
         if (factory == null) {
            factory = new VerbRepository();
            _factories.put(factoryType, factory);
         }

         return factory;
      }
   }
}
