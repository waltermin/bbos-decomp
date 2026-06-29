package net.rim.wica.runtime.security.internal;

final class SequenceProviderImpl$OutgoingSequence {
   private int _sequenceId;
   private int _lastSave;
   private int _index;
   private static final int BLOCK_SIZE;

   SequenceProviderImpl$OutgoingSequence(int index, int sequenceId) {
      this._index = index;
      this._sequenceId = sequenceId;
   }

   final void start(int sequenceId) {
      this._sequenceId = sequenceId;
      this._lastSave = this._sequenceId;
   }

   final int getIndex() {
      return this._index;
   }

   final void setIndex(int index) {
      this._index = index;
   }

   final void saved() {
      this._lastSave = this._sequenceId;
   }

   final boolean save() {
      return this._sequenceId - this._lastSave >= 32;
   }

   final int next() {
      return this._sequenceId++;
   }
}
