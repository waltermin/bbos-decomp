package net.rim.device.apps.api.ribbon.indicators;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.internal.deviceoptions.SMSOptions;
import net.rim.device.internal.system.SIMCardEfHandler;
import net.rim.device.internal.ui.IconCollection;

public final class VoicemailIconManager {
   private int _cphsIndicators;
   private byte[] _indicatorCounts;
   private VoicemailIconManager$VoicemailIconIndicator[] _indicators;
   private IntIntHashtable _lineCountHashtable;
   private boolean _mwisEnabled;
   private byte _mwisRFUMask = -1;
   public static final long VOICEMAIL_INDICATOR_UPDATED;
   private static final boolean _isCDMA = RadioInfo.getNetworkType() == 4;
   private static final long VOICE_MAIL_ICON_MANAGER;
   private static VoicemailIconManager _instance;
   private static IconCollection _icons = IconCollection.get("net_rim_Voicemail", 1);

   public static final VoicemailIconManager getInstance() {
      return _instance;
   }

   private VoicemailIconManager() {
   }

   private final void initialize() {
      this.loadIndicatorState();
      IndicatorManager manager = IndicatorManager.getInstance();
      if (manager != null) {
         int numIndicators = this._indicatorCounts.length;
         this._indicators = new VoicemailIconManager$VoicemailIconIndicator[numIndicators];

         for (int i = 0; i < numIndicators; i++) {
            this._indicators[i] = new VoicemailIconManager$VoicemailIconIndicator(this, i, null);
            manager.addIndicator(this._indicators[i]);
         }

         manager.updateIndicators();
      }
   }

   private final int getIndicatorCount(int index) {
      byte[] indicatorCounts = this._indicatorCounts;
      if (indicatorCounts != null && (index == 0 || index < indicatorCounts.length)) {
         int count = indicatorCounts[index];
         if (count == 0 && index == 0 && this._cphsIndicators != 0) {
            return -1;
         } else {
            return _isCDMA && count == 1 ? -1 : count;
         }
      } else {
         return 0;
      }
   }

   public final int getVoicemailCount() {
      int count = this.getIndicatorCount(0);
      if (this._lineCountHashtable != null && !this._lineCountHashtable.isEmpty()) {
         IntEnumeration keys = this._lineCountHashtable.keys();

         while (keys.hasMoreElements()) {
            int key = keys.nextElement();
            int newLineCount = this._lineCountHashtable.get(key);
            if (count > 0) {
               if (newLineCount > 0) {
                  count += newLineCount;
               }
            } else {
               count = newLineCount;
            }
         }
      }

      return count;
   }

   public final void setVoicemailCount(int line, int count) {
      if (line != 1 && line != 2) {
         if (count == 0) {
            if (this._lineCountHashtable != null) {
               this._lineCountHashtable.remove(line);
               return;
            }
         } else {
            if (this._lineCountHashtable == null) {
               this._lineCountHashtable = (IntIntHashtable)(new Object());
            }

            this._lineCountHashtable.put(line, count);
         }
      } else {
         this._indicatorCounts[0] = (byte)count;
         int cphsMask = line == 1 ? 1 : 2;
         if (count == 0) {
            this._cphsIndicators &= ~cphsMask;
         } else {
            this._cphsIndicators |= cphsMask;
         }

         updateSIMCard(this._cphsIndicators);
      }
   }

   public final boolean isIndicatorOn() {
      if ((this._cphsIndicators & 3) != 0) {
         return true;
      }

      byte[] indicatorCounts = this._indicatorCounts;
      int i = indicatorCounts.length;

      while (--i >= 0) {
         if (indicatorCounts[i] != 0) {
            return true;
         }
      }

      return this._lineCountHashtable != null && !this._lineCountHashtable.isEmpty();
   }

   public final boolean isIndicatorOn(int line) {
      if (line == 1) {
         return (this._cphsIndicators & 1) != 0;
      } else if (line == 2) {
         return (this._cphsIndicators & 2) != 0;
      } else {
         return this._lineCountHashtable != null ? this._lineCountHashtable.containsKey(line) : false;
      }
   }

   private final void onIndicatorsUpdated() {
      IndicatorManager indicatorManager = IndicatorManager.getInstance();
      if (indicatorManager != null) {
         indicatorManager.updateIndicators();
      }

      RIMGlobalMessagePoster.postGlobalEvent(6291453494459897456L);
   }

   private final void loadIndicatorState() {
      int imsi = 0;
      if (SIMCard.isSupported() && !_isCDMA) {
         label23:
         try {
            this._cphsIndicators = SIMCard.getWaitingIndicators();
         } finally {
            break label23;
         }

         imsi = SIMCard.getIMSICRC();
      }

      this._indicatorCounts = SMSOptions.getVoicemailIndicators(imsi);
      this.onIndicatorsUpdated();
   }

   private final void clearIndicatorState() {
      this._cphsIndicators = 0;
      this._indicatorCounts = SMSOptions.getVoicemailIndicators(_isCDMA ? 0 : SIMCard.getIMSICRC());
      this.onIndicatorsUpdated();
   }

   private final void setCPHSIndicators(int cphsIndicators) {
      this._cphsIndicators = cphsIndicators;
      int imsi = _isCDMA ? 0 : SIMCard.getIMSICRC();
      byte[] indicatorCounts = this._indicatorCounts;
      if ((this._cphsIndicators & 1) == 0 && (this._cphsIndicators & 2) == 0) {
         indicatorCounts[0] = 0;
         SMSOptions.setVoicemailIndicators(indicatorCounts, imsi);
      } else if ((this._cphsIndicators & 8) == 0) {
         indicatorCounts[1] = 0;
         SMSOptions.setVoicemailIndicators(indicatorCounts, imsi);
      }

      updateSIMCard(this._cphsIndicators);
      this.onIndicatorsUpdated();
   }

   private final void setIndicator(int indicator, int count) {
      try {
         byte profileID = (byte)(((indicator & 96) >> 5) + 1);
         byte indicationType = (byte)(indicator & 3);
         switch (indicationType) {
            case -1:
               break;
            case 0:
            default:
               if (count == 0) {
                  this._cphsIndicators &= -4;
               } else {
                  this._cphsIndicators |= 1;
               }

               updateSIMCard(this._cphsIndicators);
               break;
            case 1:
               if (count == 0) {
                  this._cphsIndicators &= -9;
               } else {
                  this._cphsIndicators |= 8;
               }

               updateSIMCard(this._cphsIndicators);
         }

         this._indicatorCounts[indicationType] = (byte)count;
         if (this._mwisEnabled) {
            ((SIMCardEfHandler)(new Object())).startTask(new VoicemailIconManager$UpdateMwisTask(this, profileID, indicationType, (byte)count), true);
         }

         SMSOptions.setVoicemailIndicators(this._indicatorCounts, _isCDMA ? 0 : SIMCard.getIMSICRC());
         this.onIndicatorsUpdated();
      } finally {
         return;
      }
   }

   public final void initializeMWIS() {
      ((SIMCardEfHandler)(new Object())).startTask(new VoicemailIconManager$ReadMwisTask(this, null), true);
   }

   public static final boolean isMwisEnabled() {
      return _instance._mwisEnabled;
   }

   private static final void updateSIMCard(int cphsIndicators) {
      if (SIMCard.isSupported() && !_isCDMA) {
         try {
            SIMCard.setWaitingIndicators(cphsIndicators);
         } finally {
            return;
         }
      }
   }

   public static final void setCPHSState(int cphsIndicators) {
      _instance.setCPHSIndicators(cphsIndicators);
   }

   public static final int getCPHSState() {
      return _instance._cphsIndicators;
   }

   public static final void setState(int indicator, int count) {
      _instance.setIndicator(indicator, count);
   }

   public static final void simUpdate(boolean ok) {
      if (ok) {
         _instance.loadIndicatorState();
      } else {
         _instance.clearIndicatorState();
      }
   }

   public static final IconCollection getIconCollection() {
      return _icons;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (VoicemailIconManager)ar.getOrWaitFor(-7196076389835624577L);
      if (_instance == null) {
         _instance = new VoicemailIconManager();
         ar.put(-7196076389835624577L, _instance);
         _instance.initialize();
      }
   }
}
