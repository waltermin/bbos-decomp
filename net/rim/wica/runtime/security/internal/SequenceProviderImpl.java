package net.rim.wica.runtime.security.internal;

import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.transport.security.SequenceProvider;

public class SequenceProviderImpl implements SequenceProvider {
   private SequenceProviderImpl$Sequencer _sequencer;
   private SequenceProviderImpl$Verifier _verifier;

   SequenceProviderImpl(PersistenceService persistence) {
      this._sequencer = new SequenceProviderImpl$Sequencer(persistence);
      this._verifier = new SequenceProviderImpl$Verifier(persistence);
   }

   void add(long key, boolean reset) {
      this._sequencer.add(key, reset);
      this._verifier.add(key, reset);
   }

   void remove(long key) {
      this._sequencer.remove(key);
      this._verifier.remove(key);
   }

   @Override
   public int next(long key) {
      return this._sequencer.next(key);
   }

   @Override
   public boolean verify(int sequence, long key) {
      return this._verifier.verify(sequence, key);
   }
}
