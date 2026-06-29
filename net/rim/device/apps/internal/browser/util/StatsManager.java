package net.rim.device.apps.internal.browser.util;

import net.rim.device.cldc.io.utility.SessionStats;

public final class StatsManager {
   private SessionStats _sessionStats = new SessionStats();

   public final SessionStats getCurrentSessionsStats() {
      return this._sessionStats;
   }

   public final void reinit() {
      this._sessionStats = new SessionStats();
   }

   public final void setSessionStats(SessionStats stats) {
      this._sessionStats = stats;
   }

   public final void sessionOver() {
      this._sessionStats = null;
   }
}
