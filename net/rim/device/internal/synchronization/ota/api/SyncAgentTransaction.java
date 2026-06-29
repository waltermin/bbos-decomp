package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.util.Persistable;

public class SyncAgentTransaction implements Persistable {
   private int _syncCommandTag;
   private SyncAgentUrl _syncAgentUrl;
   private SyncApplicationChange _syncApplicationChange;
   private int _minutesUntilExecution = 0;

   public SyncAgentTransaction(int aSyncCommandTag, SyncAgentUrl aSyncAgentUrl, SyncApplicationChange aSyncApplicationChange) {
      this._syncCommandTag = aSyncCommandTag;
      this._syncAgentUrl = aSyncAgentUrl;
      this._syncApplicationChange = aSyncApplicationChange;
   }

   public SyncAgentTransaction(int aSyncCommandTag, SyncAgentUrl aSyncAgentUrl, int minutesFromNow) {
      this._syncCommandTag = aSyncCommandTag;
      this._syncAgentUrl = aSyncAgentUrl;
      this._minutesUntilExecution = minutesFromNow;
   }

   int execute() {
      SyncAgentConnection aSyncAgentConnection = SyncAgentConnections.getConnectionBy(this._syncAgentUrl);
      return aSyncAgentConnection != null ? aSyncAgentConnection.onSessionEvent(this._syncCommandTag, null) : 405;
   }

   boolean isScheduledForFutureExecution() {
      return this._minutesUntilExecution > 0;
   }

   boolean tick() {
      this._minutesUntilExecution--;
      return this.isScheduledForFutureExecution();
   }

   void scheduleTransactionFor(int minutesFromNow) {
      this._minutesUntilExecution = minutesFromNow;
   }
}
