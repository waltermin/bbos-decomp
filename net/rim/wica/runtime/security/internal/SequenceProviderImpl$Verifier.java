package net.rim.wica.runtime.security.internal;

import net.rim.device.api.util.LongHashtable;
import net.rim.vm.Array;
import net.rim.wica.runtime.persistence.PersistableObject;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.transport.security.SequenceProviderException;

final class SequenceProviderImpl$Verifier {
   private LongHashtable _mapping = (LongHashtable)(new Object());
   private PersistableObject _wrapper;
   private long[] _persistable;
   private PersistenceService _persistence;

   SequenceProviderImpl$Verifier(PersistenceService persistence) {
      this._persistence = persistence;
      this.load();
   }

   final synchronized boolean verify(int sequenceId, long key) {
      SequenceProviderImpl$IncomingSequence sequence = (SequenceProviderImpl$IncomingSequence)this._mapping.get(key);
      if (sequence != null) {
         boolean verified = sequence.verify(sequenceId);
         if (verified) {
            int index = sequence.getIndex();
            this._persistable[++index] = sequence.getWindowStart();
            this._persistable[++index] = sequence.getWindow();
            this._persistence.storeIncomingSequence(this._wrapper);
         }

         return verified;
      } else {
         throw new SequenceProviderException(((StringBuffer)(new Object("Key "))).append(key).append(" not found.").toString());
      }
   }

   final void add(long key, boolean reset) {
      boolean containsKey = this._mapping.containsKey(key);
      if (!containsKey || reset) {
         if (!containsKey) {
            int index = 0;
            if (this._persistable == null) {
               this._persistable = new long[3];
               this._wrapper = new PersistableObject(this._persistable);
            } else {
               index = this._persistable.length;
               Array.resize(this._persistable, index + 3);
            }

            this._persistable[index] = key;
            SequenceProviderImpl$IncomingSequence sequence = new SequenceProviderImpl$IncomingSequence(index, 0, 0);
            this._mapping.put(key, sequence);
         } else {
            SequenceProviderImpl$IncomingSequence sequence = (SequenceProviderImpl$IncomingSequence)this._mapping.get(key);
            sequence.start(0, 0);
            int index = sequence.getIndex();
            this._persistable[++index] = 0;
            this._persistable[++index] = 0;
         }

         this._persistence.storeIncomingSequence(this._wrapper);
      }
   }

   final void remove(long key) {
      SequenceProviderImpl$IncomingSequence sequence = (SequenceProviderImpl$IncomingSequence)this._mapping.remove(key);
      if (sequence != null) {
         int index = sequence.getIndex();
         int newLength = this._persistable.length - 3;
         if (index != newLength) {
            int i = index;

            for (int j = i + 3; i < newLength; j += 3) {
               sequence = (SequenceProviderImpl$IncomingSequence)this._mapping.get(this._persistable[j]);
               sequence.setIndex(i);
               this._persistable[i] = this._persistable[j];
               this._persistable[i + 1] = this._persistable[j + 1];
               this._persistable[i + 2] = this._persistable[j + 2];
               i += 3;
            }
         }

         Array.resize(this._persistable, newLength);
         this._persistence.storeIncomingSequence(this._wrapper);
      }
   }

   final void load() {
      this._persistable = this._persistence.loadIncomingSequence();
      if (this._persistable != null) {
         this._wrapper = new PersistableObject(this._persistable);
         int length = this._persistable.length;

         for (int i = 0; i < length; i += 3) {
            long key = this._persistable[i];
            int windowStart = (int)this._persistable[i + 1];
            long window = this._persistable[i + 2];
            SequenceProviderImpl$IncomingSequence sequence = new SequenceProviderImpl$IncomingSequence(i, windowStart, window);
            this._mapping.put(key, sequence);
         }
      }
   }
}
