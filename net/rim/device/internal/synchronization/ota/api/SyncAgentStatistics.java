package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.util.Persistable;
import net.rim.vm.Persistence;

public final class SyncAgentStatistics implements Persistable {
   private int _totalNumberOfOperations;
   private int _totalNumberOfExecutedOperations;
   private int _totalNumberOfFailedOperations;
   private int _previousTotalNumberOfExecutedOperations;
   private int _previousRemainingOperations;
   private int _totalNumberOfHits;
   private int _token;
   private int _status = 1;
   private SyncAgentUrl _syncAgentUrl;
   private boolean _allowIntegrityChecks;
   private boolean _removedDueToFailure;
   public static final int SUCCESS;
   public static final int GENERAL_FAILURE;

   public SyncAgentStatistics(SyncAgentUrl aUrl, boolean allowIntegrityChecks) {
      this.softReset();
      this._syncAgentUrl = aUrl;
      this._allowIntegrityChecks = allowIntegrityChecks;
      this._removedDueToFailure = false;
   }

   public final boolean isIntegrityChecksAllowed() {
      return this._allowIntegrityChecks;
   }

   public final void setStatus(int status) {
      this._status = status;
   }

   public final int getStatus() {
      return this._status;
   }

   public final int getTotalNumberOf100PercentHits() {
      return this._totalNumberOfHits;
   }

   public final int getTotalNumberOfOperations() {
      return this._totalNumberOfOperations;
   }

   public final int getTotalNumberOfExecutedOperations() {
      return this._totalNumberOfExecutedOperations;
   }

   public final int getTotalNumberOfSucceededOperations() {
      return this.getTotalNumberOfExecutedOperations() - this.getTotalNumberOfFailedOperations();
   }

   public final int getTotalNumberOfFailedOperations() {
      return this._totalNumberOfFailedOperations;
   }

   public final int getPercentage() {
      return this._totalNumberOfOperations == 0 ? 100 : this._totalNumberOfExecutedOperations * 100 / this._totalNumberOfOperations;
   }

   public final boolean isValid() {
      return this._totalNumberOfOperations != -1;
   }

   public final SyncAgentUrl getSyncAgentUrl() {
      return this._syncAgentUrl;
   }

   public final synchronized void setRemainingNumberOfOperations(int aRemainingNumberOfOperations, int aToken) {
      if (this._token == aToken) {
         if (aRemainingNumberOfOperations == 0) {
            Persistence.commit(this, true);
            SyncAgent.getSingletonInstance().notifyListenersWith(17, this);
         }
      } else {
         this._token = aToken;
         if (this._totalNumberOfOperations != -1 && aRemainingNumberOfOperations != 0) {
            int xNumberOfExecutedOps = this._totalNumberOfExecutedOperations - this._previousTotalNumberOfExecutedOperations;
            int xExpectedRemainingNumberOfOperations = this._previousRemainingOperations - xNumberOfExecutedOps;
            if (aRemainingNumberOfOperations >= xExpectedRemainingNumberOfOperations) {
               this._totalNumberOfOperations += aRemainingNumberOfOperations - xExpectedRemainingNumberOfOperations;
            } else {
               this._totalNumberOfExecutedOperations += xExpectedRemainingNumberOfOperations - aRemainingNumberOfOperations;
            }

            this._previousRemainingOperations = aRemainingNumberOfOperations;
            this._previousTotalNumberOfExecutedOperations = this._totalNumberOfExecutedOperations;
         } else {
            this._totalNumberOfOperations = aRemainingNumberOfOperations;
            this._previousRemainingOperations = aRemainingNumberOfOperations;
         }

         if (this._totalNumberOfOperations == 0) {
            this._totalNumberOfHits++;
         }

         Persistence.commit(this, true);
         SyncAgent.getSingletonInstance().notifyListenersWith(17, this);
      }
   }

   public final void incrementNumberOfFailedOperations() {
      this._totalNumberOfFailedOperations++;
   }

   public final void incrementNumberOfExecutedOperations() {
      this._totalNumberOfExecutedOperations++;
      boolean xFireUpdate = false;
      if (this.isComplete()) {
         this._totalNumberOfHits++;
         xFireUpdate = true;
      } else {
         xFireUpdate = this._totalNumberOfExecutedOperations % 50 == 0;
      }

      if (xFireUpdate) {
         Persistence.commit(this, true);
         SyncAgent.getSingletonInstance().notifyListenersWith(17, this);
      }
   }

   public final boolean isComplete() {
      return this._totalNumberOfExecutedOperations == this._totalNumberOfOperations;
   }

   private final void softReset() {
      this._totalNumberOfOperations = -1;
      this._totalNumberOfExecutedOperations = 0;
      this._totalNumberOfFailedOperations = 0;
      this._previousTotalNumberOfExecutedOperations = 0;
      this._previousRemainingOperations = 0;
      this._totalNumberOfHits = 0;
      this._token = 0;
   }

   @Override
   public final int hashCode() {
      return this._syncAgentUrl.hashCode();
   }

   public final boolean removedDueToFailure() {
      return this._removedDueToFailure;
   }

   public final void setRemovedDueToFailure(boolean value) {
      this._removedDueToFailure = value;
   }

   public final void reset() {
      this.softReset();
      Persistence.commit(this, true);
      SyncAgent.getSingletonInstance().notifyListenersWith(17, this);
   }
}
