package net.rim.wica.runtime.security.internal;

import net.rim.device.api.util.LongHashtable;
import net.rim.vm.Array;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.transport.security.SequenceProviderException;

final class SequenceProviderImpl$Sequencer {
   private LongHashtable _mapping = new LongHashtable();
   private long[] _persistable;
   private PersistenceService _persistence;

   SequenceProviderImpl$Sequencer(PersistenceService persistence) {
      this._persistence = persistence;
      this.load();
   }

   final synchronized int next(long key) throws SequenceProviderException {
      SequenceProviderImpl$OutgoingSequence sequence = (SequenceProviderImpl$OutgoingSequence)this._mapping.get(key);
      if (sequence != null) {
         int next = sequence.next();
         if (sequence.save()) {
            int index = sequence.getIndex();
            this._persistable[++index] = next + 32;
            this._persistence.storeOutgoingSequence(this._persistable);
            sequence.saved();
         }

         return next;
      } else {
         throw new SequenceProviderException("Key " + key + " not found.");
      }
   }

   final synchronized void add(long key, boolean reset) {
      boolean containsKey = this._mapping.containsKey(key);
      if (!containsKey || reset) {
         if (!containsKey) {
            int index = 0;
            if (this._persistable == null) {
               this._persistable = new long[2];
            } else {
               index = this._persistable.length;
               Array.resize(this._persistable, index + 2);
            }

            SequenceProviderImpl$OutgoingSequence sequence = new SequenceProviderImpl$OutgoingSequence(index, 0);
            this._mapping.put(key, sequence);
            this._persistable[index] = key;
            this._persistable[++index] = 32;
         } else {
            SequenceProviderImpl$OutgoingSequence sequence = (SequenceProviderImpl$OutgoingSequence)this._mapping.get(key);
            sequence.start(0);
            this._persistable[sequence.getIndex() + 1] = 32;
         }

         this._persistence.storeOutgoingSequence(this._persistable);
      }
   }

   final synchronized void remove(long key) {
      SequenceProviderImpl$OutgoingSequence sequence = (SequenceProviderImpl$OutgoingSequence)this._mapping.remove(key);
      if (sequence != null) {
         int index = sequence.getIndex();
         int newLength = this._persistable.length - 2;
         if (index != newLength) {
            int i = index;

            for (int j = i + 2; i < newLength; j += 2) {
               sequence = (SequenceProviderImpl$OutgoingSequence)this._mapping.get(this._persistable[j]);
               sequence.setIndex(i);
               this._persistable[i] = this._persistable[j];
               this._persistable[i + 1] = this._persistable[j + 1];
               i += 2;
            }
         }

         Array.resize(this._persistable, newLength);
         this._persistence.storeOutgoingSequence(this._persistable);
      }
   }

   final void load() {
      this._persistable = this._persistence.loadOutgoingSequence();
      if (this._persistable != null) {
         int length = this._persistable.length;

         for (int i = 0; i < length; i += 2) {
            long key = this._persistable[i];
            int sequenceId = (int)this._persistable[i + 1];
            SequenceProviderImpl$OutgoingSequence sequence = new SequenceProviderImpl$OutgoingSequence(i, sequenceId);
            this._mapping.put(key, sequence);
         }
      }
   }
}
