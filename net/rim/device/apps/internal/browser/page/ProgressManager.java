package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class ProgressManager {
   private ProgressManager$TimerDoneEvent _timerDoneEvent;
   private ProgressManager$TimerTickEvent _timerTickEvent;
   private int _timerTickInvokeLater = -1;
   private int _progressState;
   private String _label;
   private int _value;
   private int _maxValue;
   private boolean _labelSetInTick;
   private BrowserImpl _browser;
   private boolean _finishEarly;
   private ProgressManager$ResetIdleTime _resetIdle;
   private int _resetIdleTimer = -1;
   private long _startIdleKickTimestamp;
   private long _lastStateStartTimestamp = 0;
   private MessageFormat _loadingLabel;
   private MessageFormat _loadingImageLabel;
   private String[] _loadingValues = new Object[2];
   private int _loadingNumToReadCache;
   private static final int MAX_INTERVAL;
   private static final int MAX_IDLE_KICK_TIME;
   public static final int TICK_CONTENT_READ;
   public static final int TICK_TIMER_FIRED;
   public static final int TICK_STATE_CHANGE;
   public static final int TICK_RESET;
   public static final int TICK_COMPLETION_TIMER_FIRED;
   public static final int FULL_BAR;
   public static final int ONE_PERCENT;
   public static final int EMPTY_BAR;
   private static final int PROGRESS_NONE;
   private static final int PROGRESS_INITIAL;
   private static final int PROGRESS_TEXT;
   private static final int PROGRESS_IMAGES;
   private static final int PROGRESS_IMAGE_FRAGMENTS;
   private static final int PROGRESS_COMPLETED;
   private static final int ADVANCE_BAR;
   private static final int DELAY_ON_COMPLETE;
   private static final int BACKLIGHT_TIME;
   private static final int RESET_IDLE_TIME;
   private static final int ADVANCE_DELAY;

   public ProgressManager(BrowserImpl browser) {
      this._timerDoneEvent = new ProgressManager$TimerDoneEvent(this);
      this._timerTickEvent = new ProgressManager$TimerTickEvent(this);
      this._resetIdle = new ProgressManager$ResetIdleTime(this);
      this._browser = browser;
      this.reset();
   }

   private final void updateTimestamp(String oldLabel, String newLabel) {
      long currentTimestamp = System.currentTimeMillis();
      long interval = currentTimestamp - this._lastStateStartTimestamp;
      this._lastStateStartTimestamp = currentTimestamp;
      if (interval >= 30000 && this._lastStateStartTimestamp != 0 && oldLabel != null && newLabel != null) {
         StringBuffer buffer = (StringBuffer)(new Object());
         buffer.append('[');
         buffer.append(oldLabel);
         buffer.append(" -> ");
         buffer.append(newLabel);
         buffer.append("] ");
         buffer.append(interval / 1000);
         buffer.append(" sec.");
         EventLogger.logEvent(1907089860548946979L, buffer.toString().getBytes(), 0);
      }
   }

   public final void update(int type) {
      String oldLabel = this._label;
      if (this._finishEarly) {
         this._progressState = 5;
         this._finishEarly = false;
      }

      boolean repaintImmediate = this._progressState < 3;
      if (this._progressState == 5 && type != 6) {
         this._value = 10000;
         Application.getApplication().invokeLater(this._timerDoneEvent, 80, false);
         repaintImmediate = true;
      }

      if (type == 6) {
         if (this._progressState == 5) {
            this.reset();
         }
      } else if (type == 2) {
         int remaining = this._maxValue - this._value;
         if (remaining > 0) {
            int delta = remaining / 100 * 6;
            if (delta == 0) {
               delta = 6;
            }

            this._value = Math.min(this._value + delta, this._maxValue);
         }
      } else if (type == 1 && this._progressState != 5) {
         int remaining = this._maxValue - this._value;
         if (remaining > 0) {
            int delta = remaining / 100 * 6;
            if (delta == 0) {
               delta = 6;
            }

            this._value = Math.min(this._value + delta, this._maxValue);
         }
      }

      BrowserScreen screen = this._browser.getBrowserScreen();
      if (screen != null) {
         PageFooterField footer = screen.getFooterField();
         if (footer != null) {
            Page page = screen.getPage();
            synchronized (Application.getEventLock()) {
               if (screen.getFieldWithFocus() == footer && page != null) {
                  Manager fieldsManager = page.getContentManager();
                  if (fieldsManager != null) {
                     fieldsManager.setFocus();
                  }
               }

               if (this._progressState == 0) {
                  footer.hideFooter();
               } else {
                  footer.showFooter();
               }

               footer.reset(this._label, 0, 10000, this._value, repaintImmediate);
            }
         }
      }

      this.updateTimestamp(oldLabel, this._label);
   }

   public final boolean setContentReadProgress(int numRead, int numToRead, boolean inBytes) {
      int oldValue = this._value;
      if (this._progressState == 2 || this._progressState == 3) {
         if (numToRead <= 0) {
            return false;
         }

         if (numRead > numToRead) {
            numRead = numToRead;
            this._value = 10000;
            this._maxValue = 10000;
         } else {
            long tempValue = (long)10000 * numRead;
            this._value = (int)(tempValue / numToRead);
            this._maxValue = this._value;
         }

         if ((this._labelSetInTick || oldValue == this._value) && oldValue + 100 >= this._value) {
            this._value = oldValue;
            this._maxValue = oldValue;
         } else {
            if (numToRead != this._loadingNumToReadCache) {
               this._loadingNumToReadCache = numToRead;
               if (inBytes) {
                  this._loadingValues[1] = ((StringBuffer)(new Object()))
                     .append(String.valueOf(numToRead > 1024 ? numToRead / 1024 : 1))
                     .append('k')
                     .toString();
               } else {
                  this._loadingValues[1] = String.valueOf(numToRead);
               }
            }

            if (inBytes) {
               this._loadingValues[0] = ((StringBuffer)(new Object())).append(String.valueOf(numRead / 1024)).append('k').toString();
            } else {
               this._loadingValues[0] = String.valueOf(numRead);
            }

            if (this._progressState == 2 && this._loadingLabel != null) {
               this._label = this._loadingLabel.format(this._loadingValues);
               this._labelSetInTick = true;
            } else if (this._progressState == 3 && this._loadingImageLabel != null) {
               this._label = this._loadingImageLabel.format(this._loadingValues);
               this._labelSetInTick = true;
            }
         }
      }

      return oldValue != this._value;
   }

   final void reset() {
      this._progressState = 0;
      this._label = null;
      this._value = 0;
      this._maxValue = 0;
      this._finishEarly = false;
      if (this._resetIdleTimer != -1) {
         if (this._startIdleKickTimestamp != 0) {
            this._startIdleKickTimestamp = 0;
            Application.getApplication().cancelInvokeLater(this._resetIdleTimer);
         }

         this._resetIdleTimer = -1;
      }

      if (this._timerTickInvokeLater != -1) {
         Application.getApplication().cancelInvokeLater(this._timerTickInvokeLater);
         this._timerTickInvokeLater = -1;
      }

      this.update(5);
   }

   public final void finishEarly() {
      this._finishEarly = true;
   }

   public final void clearValue() {
      this._value = 0;
   }

   public final void setLabel(String label) {
      String oldLabel = this._label;
      if (this._progressState != 0) {
         this._label = label;
         this._labelSetInTick = false;
      }

      this.updateTimestamp(oldLabel, label);
   }

   public final void changeState(int state) {
      if (this._timerTickInvokeLater != -1 && state != 1 && state != 9 && state != 7 && state != 6) {
         Application.getApplication().cancelInvokeLater(this._timerTickInvokeLater);
         this._timerTickInvokeLater = -1;
      }

      switch (state) {
         case -1:
         case 5:
            break;
         case 0:
         default:
            if (this._progressState == 0) {
               this._maxValue = 10000;
               this._label = getLabel(state);
               this._progressState = 0;
               this._value = 0;
            } else {
               if (this._loadingNumToReadCache > 0) {
                  if (this._progressState == 2 && this._loadingLabel != null) {
                     this._loadingValues[0] = this._loadingValues[1];
                     this._label = this._loadingLabel.format(this._loadingValues);
                  } else if (this._progressState == 3 && this._loadingImageLabel != null) {
                     this._loadingValues[0] = this._loadingValues[1];
                     this._label = this._loadingImageLabel.format(this._loadingValues);
                  }
               }

               this._progressState = 5;
            }
            break;
         case 1:
         case 4:
         case 6:
         case 7:
         case 9:
            if (Backlight.isEnabled()) {
               Backlight.enable(true, 45);
            }

            if (this._resetIdleTimer == -1) {
               this._resetIdleTimer = Application.getApplication().invokeLater(this._resetIdle, 15000, true);
               this._startIdleKickTimestamp = System.currentTimeMillis();
            }

            this._label = getLabel(state);
            if (this._progressState != 1) {
               this._maxValue = 10000;
               this._timerTickInvokeLater = Application.getApplication().invokeLater(this._timerTickEvent, 1000, true);
               this._progressState = 1;
               this._value = 0;
            }
            break;
         case 2:
            if (Backlight.isEnabled()) {
               Backlight.enable(true, 45);
            }

            if (this._resetIdleTimer == -1) {
               this._resetIdleTimer = Application.getApplication().invokeLater(this._resetIdle, 15000, true);
               this._startIdleKickTimestamp = System.currentTimeMillis();
            }

            this._maxValue = 10000;
            this._label = getLabel(state);
            this._loadingLabel = (MessageFormat)(new Object(BrowserResources.getString(741)));
            this._loadingValues[0] = null;
            this._loadingValues[1] = null;
            this._loadingNumToReadCache = 0;
            this._progressState = 2;
            this._value = 0;
            break;
         case 3:
            if (this._progressState == 3) {
               return;
            }

            this._maxValue = 0;
            this._label = getLabel(state);
            this._loadingImageLabel = (MessageFormat)(new Object(BrowserResources.getString(742)));
            this._loadingValues[0] = null;
            this._loadingValues[1] = null;
            this._loadingNumToReadCache = 0;
            this._progressState = 3;
            this._value = 0;
            break;
         case 8:
            this._progressState = 5;
      }

      this.update(3);
   }

   private static final String getLabel(int state) {
      int id = -1;
      switch (state) {
         case -1:
            break;
         case 0:
         default:
            id = 134;
            break;
         case 1:
            id = 135;
            break;
         case 2:
            id = 136;
            break;
         case 3:
            id = 137;
            break;
         case 4:
            id = 138;
            break;
         case 5:
            id = 139;
            break;
         case 6:
            id = 497;
            break;
         case 7:
            id = 498;
            break;
         case 8:
            id = 887;
            break;
         case 9:
            id = 901;
      }

      if (id != -1) {
         String result = BrowserResources.getString(id);
         if (result != null && result.length() > 0) {
            return result;
         }
      }

      return null;
   }
}
