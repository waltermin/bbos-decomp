package javax.microedition.rms;

import net.rim.vm.Array;

class BaseRecordEnumeration extends RecordEventGenerator implements RecordEnumeration, RecordListener {
   private boolean _destroyed;
   protected RecordStore _recordStore;
   protected RecordEventGenerator _recordSource;
   protected boolean _keepUpdated;
   protected int[] _recordIds;
   protected boolean _inError;
   protected int _currentIndex;

   BaseRecordEnumeration(RecordStore recordStore, RecordEventGenerator recordSource, boolean listen) {
      this._recordStore = recordStore;
      this._recordSource = recordSource;
      this._recordIds = new int[0];
      this.keepUpdated(listen);
   }

   @Override
   void loadRecordIDs(int[] recordIds) {
      synchronized (this._recordIds) {
         Array.resize(recordIds, this._recordIds.length);
         System.arraycopy(this._recordIds, 0, recordIds, 0, this._recordIds.length);
      }
   }

   protected void mustBeValid() {
      if (this._destroyed) {
         throw new IllegalStateException();
      }
   }

   @Override
   public boolean isKeptUpdated() {
      this.mustBeValid();
      return this._keepUpdated;
   }

   @Override
   public void keepUpdated(boolean listen) {
      this.mustBeValid();
      if (this._keepUpdated != listen) {
         this._keepUpdated = listen;
         if (listen) {
            this._recordSource.addRecordListener(this);
         } else {
            this._recordSource.removeRecordListener(this);
         }

         RecordEventGenerator reg = this._recordSource;

         while (reg instanceof BaseRecordEnumeration) {
            BaseRecordEnumeration bre = (BaseRecordEnumeration)reg;
            if (listen) {
               this._recordSource.addRecordListener(bre);
            } else {
               this._recordSource.removeRecordListener(bre);
            }

            reg = bre._recordSource;
         }
      }
   }

   @Override
   public synchronized void addRecordListener(RecordListener listener) {
      this._recordStore.addRecordListener(listener);
   }

   @Override
   public synchronized void removeRecordListener(RecordListener listener) {
      this._recordStore.removeRecordListener(listener);
   }

   @Override
   public int numRecords() {
      this.mustBeValid();
      return this._recordIds.length;
   }

   @Override
   public byte[] nextRecord() {
      this.mustBeValid();
      synchronized (this._recordIds) {
         return this._recordStore.getRecord(this.nextRecordId());
      }
   }

   @Override
   public int nextRecordId() throws InvalidRecordIDException {
      if (this._inError) {
         throw new InvalidRecordIDException();
      }

      this.mustBeValid();
      this._currentIndex++;
      synchronized (this._recordIds) {
         if (this._currentIndex >= this._recordIds.length) {
            this._inError = true;
            throw new InvalidRecordIDException();
         } else {
            return this._recordIds[this._currentIndex];
         }
      }
   }

   @Override
   public boolean hasNextElement() {
      this.mustBeValid();
      return !this._inError && this._currentIndex + 1 < this._recordIds.length;
   }

   @Override
   public byte[] previousRecord() {
      this.mustBeValid();
      synchronized (this._recordIds) {
         return this._recordStore.getRecord(this.previousRecordId());
      }
   }

   @Override
   public int previousRecordId() throws InvalidRecordIDException {
      if (this._inError) {
         throw new InvalidRecordIDException();
      }

      this.mustBeValid();
      synchronized (this._recordIds) {
         if (this._currentIndex == -1) {
            this._currentIndex = this._recordIds.length;
         }

         this._currentIndex--;
         if (this._currentIndex < 0) {
            this._inError = true;
            throw new InvalidRecordIDException();
         } else {
            return this._recordIds[this._currentIndex];
         }
      }
   }

   @Override
   public boolean hasPreviousElement() {
      this.mustBeValid();
      return !this._inError && this._currentIndex == -1 && this._recordIds.length > 0 ? true : !this._inError && this._currentIndex - 1 >= 0;
   }

   @Override
   public void reset() {
      this.mustBeValid();
      this._inError = false;
      this._currentIndex = -1;
   }

   @Override
   public void rebuild() {
      this.mustBeValid();
      synchronized (this._recordIds) {
         if (this._recordSource instanceof BaseRecordEnumeration) {
            BaseRecordEnumeration bre = (BaseRecordEnumeration)this._recordSource;
            bre.rebuild();
         }

         this._recordSource.loadRecordIDs(this._recordIds);
         this._inError = false;
         this._currentIndex = -1;
      }
   }

   @Override
   public void destroy() {
      this._recordSource.removeRecordListener(this);
      this._recordSource = null;
      this._recordIds = null;
      this._destroyed = true;
   }

   @Override
   public void recordAdded(RecordStore recordStore, int recordId) {
      this.mustBeValid();
      this.notifyRecordAdded(recordStore, recordId);
   }

   @Override
   public void recordChanged(RecordStore recordStore, int recordId) {
      this.mustBeValid();
      this.notifyRecordChanged(recordStore, recordId);
   }

   @Override
   public void recordDeleted(RecordStore recordStore, int recordId) {
      this.mustBeValid();
      synchronized (this._recordIds) {
         int count = this._recordIds.length;

         for (int i = 0; i < count; i++) {
            if (this._recordIds[i] == recordId) {
               System.arraycopy(this._recordIds, i + 1, this._recordIds, i, count - i - 1);
               Array.resize(this._recordIds, count - 1);
               if (this._currentIndex != -1 && this._currentIndex >= i) {
                  this._currentIndex--;
               }

               this.notifyRecordDeleted(recordStore, recordId);
               break;
            }
         }
      }
   }
}
