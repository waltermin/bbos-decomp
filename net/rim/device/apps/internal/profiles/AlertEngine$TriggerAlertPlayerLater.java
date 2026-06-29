package net.rim.device.apps.internal.profiles;

import net.rim.device.api.system.Application;
import net.rim.device.internal.system.AlertPlayer;

final class AlertEngine$TriggerAlertPlayerLater implements Runnable {
   private AlertPlayer _tuneLater;
   private int _alertTypeLater;
   private int _beepsLater;
   private int _vibratesLater;
   private int _startVolumeLater;
   private int _endVolumeLater;
   private long _sourceIdLongLater;
   private int _alertLevelLater;
   private int _repeatCountLater;
   private int _repeatDelayLater;
   private int _stopConditionLater;
   private final AlertEngine this$0;

   public AlertEngine$TriggerAlertPlayerLater(
      AlertEngine _1,
      int alertType,
      AlertPlayer tune,
      int beeps,
      int vibrates,
      int startVolume,
      int endVolume,
      long sourceIdLong,
      int alertLevel,
      int repeatCount,
      int repeatDelay,
      int stopCondition
   ) {
      this.this$0 = _1;
      this._tuneLater = tune;
      this._alertTypeLater = alertType;
      this._beepsLater = beeps;
      this._vibratesLater = vibrates;
      this._startVolumeLater = startVolume;
      this._endVolumeLater = endVolume;
      this._sourceIdLongLater = sourceIdLong;
      this._alertLevelLater = alertLevel;
      this._repeatCountLater = repeatCount;
      this._repeatDelayLater = repeatDelay;
      this._stopConditionLater = stopCondition;
      _1._pendingEventId = Application.getApplication().invokeLater(this, 500, false);
   }

   @Override
   public final void run() {
      this.this$0
         .startNewAlert(
            this._alertTypeLater,
            this._tuneLater,
            this._beepsLater,
            this._vibratesLater,
            this._startVolumeLater,
            this._endVolumeLater,
            this._sourceIdLongLater,
            this._alertLevelLater,
            this._repeatCountLater,
            this._repeatDelayLater,
            this._stopConditionLater,
            1
         );
      this.this$0._pendingEventId = -1;
   }
}
