package net.rim.device.apps.internal.profiles;

import java.util.Vector;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.AlertListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.internal.system.ActiveMedia;
import net.rim.device.internal.system.ActiveMediaObservable;
import net.rim.device.internal.system.AlertPlayer;
import net.rim.device.internal.ui.AlertStatus;

public final class AlertEngine extends Thread implements AlertListener, AlertStatus, PlayerListener, ActiveMedia {
   private boolean _wasInHolster;
   private boolean _holsterStateChanged;
   private boolean _alertInProgress;
   private int _alertType;
   private int _state = 0;
   private int _nextState = 0;
   private int _beeps;
   private int _vibrates;
   private int _vibrateDuration;
   private AlertPlayer _tune;
   private int _startVolume;
   private int _endVolume;
   private int _volumeIndex;
   private long _timeStarted;
   private long _timeAlertEnded;
   private int _repeatCount = -1;
   private int _repeatDelay = -1;
   private int _waitStatus;
   private boolean _alertBeingForced;
   private boolean _forced;
   private int _stopCondition = -1;
   private int _alertLevel = -1;
   private long _sourceIdLong = -1;
   private AudioRouter _audioRouter;
   private boolean _dropAPIStopEndEvents;
   private Vector _listeners;
   private boolean _repeatingEscalation;
   private int _currentVolume;
   private int _timeUpdateCount;
   private int _previewDuration;
   private boolean _playerStarted;
   private boolean _playerError;
   private boolean _playingFallbackTune;
   private int _pendingEventId = -1;
   private int _interruptible = 0;
   private static long ALERT_ENGINE_GUID = -8252744704328301837L;
   private static final int DEFAULT_BEEP_PAUSE;
   private static final int[] _vibrateDurationValues = new int[]{
      1000, 1000, 750, 1866989824, 727916, 2781953, 1093300993, 1929445492, 996951338, 7618954, -1910540799, 1979777154
   };
   private static final int WAIT_THRESHOLD;
   private static final int READY_STATE;
   private static final int NEW_ALARM;
   private static final int VIBRATE_STATE;
   private static final int TONE_STATE;
   private static final int ALERT_COMPLETED;
   private static final int AUDIO_CHAR;
   private static final int PLAYER_CHAR;
   private static final int VIBRATE_CHAR;
   private static final int SPACE_CHAR;
   private static final short[] BLACKBERRY_1 = new short[]{2000, 100, 3000, 200, 4000, 200, 5000, 100, 3, -12278, 1000, 0, 1000, 0, 750, 0};
   private static int[] _volumes = AlertConsequence.getVolumeLevels(0);
   private static int _headsetLowVolume = AlertConsequence.getLowVolume(1);
   private static final int WAIT_FOR_AUDIO;
   private static final int WAIT_FOR_VIBRATE;
   private static final int WAIT_FOR_PLAYER;
   private static final int VOLUME_INCREMENT;
   private static final int SECONDS_BETWEEN_INCREMENT;
   private static final int INITIAL_VOLUME;
   private static final int MAX_VOLUME;
   private static final int MAX_PREVIEW_DURATION;

   public final boolean isPlayingForSource(long sourceIdLong) {
      return this._sourceIdLong != -1 && sourceIdLong == this._sourceIdLong;
   }

   public final void startNewAlert(int alertType, AlertPlayer tune, int beeps, int vibrates, int startVolume, int endVolume, long sourceIdLong) {
      this.startNewAlert(alertType, tune, beeps, vibrates, startVolume, endVolume, sourceIdLong, 3, -1, 2000, -1, 2);
   }

   public final void startNewAlert(
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
      int stopCondition,
      int interruptible
   ) {
      synchronized (this) {
         this._forced = false;
         if (this._alertInProgress) {
            if (alertLevel == -1 || this._alertLevel == -1) {
               this._alertBeingForced = true;
               this._forced = true;
               this.logDebug(1179603523);
               this.cancelCurrentAlert(1128353347);
            } else if (alertLevel < this._alertLevel) {
               this._dropAPIStopEndEvents = true;
               this.cancelCurrentAlert(1331053906);
            } else {
               if (System.currentTimeMillis() - this._timeStarted < 60000) {
                  this.logDebug(1095518544);
                  return;
               }

               this.cancelCurrentAlert(1414485332);
            }
         } else {
            this._dropAPIStopEndEvents = false;
         }

         if ((alertType == 1 || alertType == 4) && tune == null) {
            this.logError(1111573588);
         } else {
            this.closePlayer();
            this._waitStatus = 0;
            this._alertType = alertType;
            this._tune = tune;
            this._beeps = beeps;
            this._repeatCount = repeatCount;
            this._repeatDelay = repeatDelay;
            this._vibrates = vibrates;
            if (this._vibrates > 0) {
               this._vibrateDuration = _vibrateDurationValues[this._vibrates - 1];
            }

            this._startVolume = startVolume;
            this._endVolume = endVolume;
            this._state = 1;
            this._alertInProgress = true;
            this._alertLevel = alertLevel;
            this._wasInHolster = DeviceInfo.isInHolster();
            this._timeStarted = System.currentTimeMillis();
            this._timeAlertEnded = 0;
            this._sourceIdLong = sourceIdLong;
            this._stopCondition = stopCondition;
            this.logDebug(1411391536 + alertType);
            this.logDebug(1461723184 + beeps);
            this._holsterStateChanged = false;
            this._volumeIndex = 1;
            this._playingFallbackTune = false;
            this._playerStarted = false;
            this._playerError = false;
            this._interruptible = interruptible;
            if (this._sourceIdLong == 0) {
               this._previewDuration = 0;
            }

            if (this._repeatCount > 0 && this._startVolume != this._endVolume) {
               this._repeatingEscalation = true;
               this._timeUpdateCount = 0;
               this._currentVolume = 55;
            } else {
               this._repeatingEscalation = false;
            }

            this.notify();
         }
      }
   }

   public final void addListener(AlertEngineListener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   public final void removeListener(Object listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   public final void stopAlert(long sourceIdLong) {
      synchronized (this) {
         if (this._sourceIdLong == sourceIdLong) {
            this._waitStatus = 0;
            this.cancelCurrentAlert(1128353347);
         }
      }
   }

   public final void closePlayer() {
      if (this._tune != null) {
         label21:
         try {
            this._tune.stopAlert();
         } finally {
            break label21;
         }

         this._tune = null;
      }
   }

   public final void cancelPendingEvents() {
      if (this._pendingEventId != -1) {
         Application.getApplication().cancelInvokeLater(this._pendingEventId);
         this._pendingEventId = -1;
      }
   }

   public final void startNewAlertLater(
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
      this.cancelPendingEvents();
      new AlertEngine$TriggerAlertPlayerLater(
         this, alertType, tune, beeps, vibrates, startVolume, endVolume, sourceIdLong, alertLevel, repeatCount, repeatDelay, stopCondition
      );
   }

   public final boolean isAlertInProgress() {
      synchronized (this) {
         return this._alertInProgress;
      }
   }

   @Override
   public final boolean isInProgress() {
      return this.isAlertInProgress();
   }

   @Override
   public final void playerUpdate(Player player, String event, Object eventData) {
      int reason = -1;
      if (event.equals("error")) {
         if (!this._playerStarted && this._sourceIdLong != 0) {
            this._playerError = true;
         }

         if (eventData instanceof Object) {
            EventLogger.logEvent(
               6982943375119825480L,
               ((StringBuffer)(new Object("AlertEngine: playerUpdate - "))).append(event).append(" : ").append(eventData).toString().getBytes(),
               2
            );
         }

         reason = 1414546776;
      } else if (event.equals("stopped")) {
         reason = 1313424460;
      } else if (event.equals("endOfMedia")) {
         reason = 1397051715;
      } else if (!event.equals("com.rim.timeUpdate")) {
         if (event.equals("started")) {
            this._playerStarted = true;
         }
      } else {
         if (this._sourceIdLong == 0) {
            this._previewDuration++;
            if (this._previewDuration > 15) {
               reason = 1397051715;
               this._previewDuration = 0;

               label155:
               try {
                  player.removePlayerListener(this);
                  player.stop();
               } finally {
                  break label155;
               }
            }
         }

         if (reason == -1 && this._repeatingEscalation && this._currentVolume != 100) {
            this._timeUpdateCount++;
            if (this._timeUpdateCount == 3) {
               this._currentVolume += 15;
               this._timeUpdateCount = 0;
               if (this._currentVolume > 100) {
                  this._currentVolume = 100;
               }

               VolumeControl vc = (VolumeControl)player.getControl("javax.microedition.media.control.VolumeControl");
               if (vc != null) {
                  vc.setLevel(this._currentVolume);
               }
            }
         }
      }

      if (reason != -1) {
         if ((event.equals("endOfMedia") || reason == 1414546776) && this._tune != null) {
            label145:
            try {
               this._tune.stopAlert();
            } finally {
               break label145;
            }
         }

         this.onSingleAlertDone(48, reason);
      }
   }

   @Override
   public final void vibrateDone(int reason) {
      this.logDebug(1162757206);
      this.onSingleAlertDone(13824, convertSingleAlertReason(reason));
   }

   @Override
   public final void buzzerDone(int reason) {
      this.logDebug(1162757186);
      this.onSingleAlertDone(48, convertSingleAlertReason(reason));
   }

   @Override
   public final boolean isAudioInUse() {
      return false;
   }

   @Override
   public final int codecUsed() {
      return -1;
   }

   @Override
   public final boolean isForce() {
      return Alert.isSingleSharedAudioChannel();
   }

   @Override
   public final boolean isAlert() {
      return true;
   }

   @Override
   public final void audioDone(int reason) {
      this.logDebug(1162757185);
      this.onSingleAlertDone(2162688, convertSingleAlertReason(reason));
   }

   private final void closeAlert(int reason) {
      this._timeAlertEnded = System.currentTimeMillis();
      this._stopCondition = -1;
      if (this._alertInProgress) {
         long sourceId = this._sourceIdLong;
         this._alertInProgress = false;
         this._sourceIdLong = -1;
         boolean phoneAlert = sourceId - VoiceServices.getCurrentLineId() == 2868625504212929964L;
         if (phoneAlert || Alert.isSingleSharedAudioChannel() && sourceId != 0) {
            ActiveMediaObservable.getInstance();
            ActiveMediaObservable.setInactive(this);
         }

         Vector listeners = this._listeners;
         if (listeners != null) {
            for (int i = listeners.size() - 1; i >= 0; i--) {
               AlertEngineListener listener = (AlertEngineListener)listeners.elementAt(i);
               listener.alertDone(sourceId, reason);
            }
         }
      }
   }

   public static final AlertEngine getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      AlertEngine engine = (AlertEngine)registry.getOrWaitFor(ALERT_ENGINE_GUID);
      if (engine == null) {
         engine = new AlertEngine();
         registry.put(ALERT_ENGINE_GUID, engine);
         registry.put(-979292548706894995L, engine);
      }

      return engine;
   }

   private final boolean isStopCondition() {
      if (this._wasInHolster != DeviceInfo.isInHolster()) {
         this._holsterStateChanged = true;
      }

      if (this._stopCondition > -1) {
         if ((this._stopCondition & 2) == 2 && this._holsterStateChanged && DeviceInfo.isInHolster()) {
            this.logDebug(1229476692);
            return true;
         } else if ((this._stopCondition & 1) == 1 && this._holsterStateChanged && !DeviceInfo.isInHolster()) {
            this.logDebug(1330139988);
            return true;
         } else {
            this._wasInHolster = DeviceInfo.isInHolster();
            return false;
         }
      } else {
         return this._wasInHolster != DeviceInfo.isInHolster();
      }
   }

   private final void cancelCurrentAlert(int reason) {
      this._state = 0;
      this._nextState = 0;
      this._repeatCount = -1;
      this.logDebug(reason);
      if ((this._waitStatus & 13824) != 0) {
         Alert.stopVibrate();
      }

      if ((this._waitStatus & 2162688) != 0) {
         Alert.stopAudio();
      }

      this.closePlayer();
      if (this._waitStatus == 0) {
         this.logDebug(1128353347);
         this.closeAlert(reason);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      int beepcount = -1;
      boolean waitState = true;
      long timeToWait = -1;
      long originalTimeToWait = 2000;
      synchronized (this) {
         while (true) {
            boolean wasNotified = true;
            boolean var26 = false /* VF: Semaphore variable */;

            label467:
            try {
               var26 = true;
               waitState = true;

               while (waitState) {
                  if (timeToWait > 0) {
                     this.wait(timeToWait);
                  } else {
                     this.wait();
                  }

                  if (this._alertBeingForced) {
                     this._alertInProgress = true;
                  }

                  waitState = false;
                  if (this._waitStatus != 0) {
                     this.logDebug(1463896404);
                     this.logDebug(this._waitStatus + 1461723168);
                     waitState = true;
                     timeToWait = -1;
                  }

                  if (this._timeAlertEnded != 0) {
                     timeToWait = originalTimeToWait - (System.currentTimeMillis() - this._timeAlertEnded);
                     this.logDebug(1464814405);
                     this.logDebug(this._waitStatus + 1461723168);
                     if (timeToWait > 0) {
                        waitState = true;
                     }
                  }
               }

               this._timeAlertEnded = 0;
               var26 = false;
            } finally {
               if (var26) {
                  this.logError(1229870162);
                  wasNotified = false;
                  break label467;
               }
            }

            originalTimeToWait = 2000;
            timeToWait = -1;
            this._alertBeingForced = false;
            long idleTime = DeviceInfo.getIdleTime() + 1;
            if (this._wasInHolster != DeviceInfo.isInHolster()) {
               idleTime = 0;
            }

            boolean stopFlag = false;
            long timeElapsed = System.currentTimeMillis() - this._timeStarted;
            int timeDelay = this._forced ? 6000 : 1000;
            if (this._stopCondition != -1) {
               stopFlag = idleTime * 1000 + timeDelay < timeElapsed && this.isStopCondition();
            } else {
               stopFlag = idleTime * 1000 + timeDelay < timeElapsed || this.isStopCondition();
            }

            if (stopFlag) {
               beepcount = -1;
               this.cancelCurrentAlert(1313424460);
            } else if (wasNotified && this._alertInProgress) {
               this.logDebug(1464749908);
               if (this._state == 1) {
                  this.logDebug(1313167169);
                  beepcount = this._beeps;
                  this._nextState = this._state = 99;
                  if (this._alertType == 4) {
                     this._state = 2;
                     this._nextState = 3;
                     beepcount = this._vibrates;
                  } else if (this._alertType == 1) {
                     this._state = 3;
                  } else if (this._alertType == 2) {
                     this._state = 2;
                     beepcount = this._vibrates;
                  }
               }

               this.logDebug(1461723184 + beepcount);
               this.logDebug(1398022192 + this._state);
               if (beepcount > 0) {
                  if (this._audioRouter.getSink() == 0 && Audio.isHeadsetConnected() && this._startVolume > 0) {
                     short[] tuneData = BLACKBERRY_1;
                     this.logDebug(1398035009);
                     Alert.startAudio(tuneData, _headsetLowVolume);
                     this._waitStatus |= 2162688;
                  }

                  if (this._state == 2) {
                     this.logDebug(1398035030);
                     Alert.startVibrate(this._vibrateDuration);
                     this._waitStatus |= 13824;
                     beepcount--;
                  } else if (this._state == 3) {
                     boolean audioToPlay = true;

                     while (audioToPlay && beepcount > 0) {
                        int volume = 0;
                        if (this._playerError) {
                           if (this._playingFallbackTune || this._sourceIdLong == 0) {
                              this.cancelCurrentAlert(1414546776);
                              this.logError(1129530964);
                              break;
                           }

                           beepcount = this._beeps;
                           if (this._repeatCount != 99) {
                              this._repeatCount++;
                           }

                           if (!this.getFallbackTune()) {
                              this.cancelCurrentAlert(1414546776);
                              break;
                           }

                           this._playingFallbackTune = true;
                        }

                        if (beepcount == this._beeps) {
                           volume = this._startVolume;
                           this._volumeIndex = 0;

                           while (this._volumeIndex < _volumes.length && _volumes[this._volumeIndex] != this._startVolume) {
                              this._volumeIndex++;
                           }
                        } else if (this._volumeIndex < _volumes.length - 1 && _volumes[this._volumeIndex] < this._endVolume) {
                           this._volumeIndex++;
                           volume = _volumes[this._volumeIndex];
                        } else {
                           volume = this._endVolume;
                        }

                        if (volume > 0) {
                           this.logDebug(1398035010);

                           try {
                              if (this._repeatingEscalation) {
                                 volume = this._currentVolume;
                              }

                              this._playerStarted = false;
                              this._playerError = false;
                              boolean phoneAlert = this._sourceIdLong - VoiceServices.getCurrentLineId() == 2868625504212929964L;
                              if (phoneAlert || Alert.isSingleSharedAudioChannel() && this._sourceIdLong != 0) {
                                 ActiveMediaObservable.getInstance();
                                 ActiveMediaObservable.setActive(this);
                              }

                              this._tune.startAlert(volume, this._interruptible);
                           } catch (Throwable var28) {
                              String error = e.getMessage() == null
                                 ? ((StringBuffer)(new Object("AlertEngine: run - "))).append(e.toString()).toString()
                                 : e.getMessage();
                              if (this._tune == null) {
                                 error = ((StringBuffer)(new Object())).append(error).append(".  The tune is null.").toString();
                              }

                              EventLogger.logEvent(6982943375119825480L, error.getBytes(), 2);
                              this._playerError = true;
                              if (this._repeatCount != -1 && this._repeatCount != 99 && !this._playingFallbackTune && this._sourceIdLong != 0) {
                                 this._repeatCount--;
                              }
                              continue;
                           }

                           this._waitStatus |= 48;
                           audioToPlay = false;
                        }

                        beepcount--;
                     }
                  }
               }

               if (beepcount <= 0) {
                  this.logDebug(1397051715);
                  if (this._nextState != 99) {
                     beepcount = this._beeps;
                  }

                  this._state = this._nextState;
                  this._nextState = 99;
               }

               if (this._state == 99) {
                  beepcount = -1;
                  this.logDebug(1146048069);
                  this._state = 0;
                  this._nextState = 0;
                  if (this._repeatCount != 99) {
                     this._repeatCount--;
                  }

                  if (this._repeatCount > 0) {
                     this.logDebug(1380274260);
                     this._state = 1;
                     originalTimeToWait = this._repeatDelay;
                     timeToWait = this._repeatDelay;
                  }

                  this.doneAlert(1397051715);
               }
            }
         }
      }
   }

   private final void logError(int eventId) {
      EventLogger.logEvent(6982943375119825480L, eventId, 2);
   }

   private final void logDebug(int eventId) {
      EventLogger.logEvent(6982943375119825480L, eventId, 5);
   }

   private final boolean getFallbackTune() {
      boolean switchedTune = false;
      this.closePlayer();
      if (TuneManager.isTuneManagerAvailable()) {
         byte profileIdentifier = Profiles.getInstance().getEnabled().getIdentifier();
         String[] defaultTuneNames = Profiles.getDefaultTuneNames(this._sourceIdLong, profileIdentifier);
         String defaultTune = DeviceInfo.isInHolster() ? defaultTuneNames[0] : defaultTuneNames[1];
         this._tune = TuneManager.getTuneManager().getTune(defaultTune);
         if (this._tune != null) {
            this.logDebug(1398227280);
            return true;
         }

         this.logError(1146375750);
      }

      return switchedTune;
   }

   private final void doneAlert(int reason) {
      if (!this._alertBeingForced) {
         this._timeAlertEnded = System.currentTimeMillis();
      }

      this.logDebug(1397051715);
      if (this._waitStatus == 0) {
         if (this._state == 0) {
            this.closeAlert(reason);
            return;
         }

         this.logDebug(1313821769);
         this.notify();
      }
   }

   private static final int convertSingleAlertReason(int simpleReason) {
      switch (simpleReason) {
         case 1:
            return 1313424460;
         case 3:
            return 1398034256;
         default:
            return 1397051715;
      }
   }

   private final void onSingleAlertDone(int waitFlag, int reason) {
      synchronized (this) {
         if (this._dropAPIStopEndEvents && reason == 1398034256) {
            this.logDebug(1229410884);
         } else {
            this._waitStatus &= ~waitFlag;
            if (reason != 1397051715 && !this._alertBeingForced && this.isStopCondition()) {
               this.cancelCurrentAlert(reason);
            }

            this.doneAlert(reason);
         }
      }
   }

   private AlertEngine() {
      this._audioRouter = AudioRouter.getInstance();
   }
}
