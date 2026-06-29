package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.PersistentInteger;
import net.rim.vm.WeakReference;

final class GeneralProperty$GeneralPropertyData {
   WeakReference[] _generalPropertyListeners;
   BitSet _changedOptions = (BitSet)(new Object());
   int _quitHandle;
   int _executeHandle;
   int _clearHandle;
   int _deleteHandle;
   int _javascriptLocationHandle;
   int _defaultCharsetModeHandle;
   int _defaultFontSizeHandle;
   int _minimumFontSizeHandle;
   int _minimumFontStyleHandle;
   int _doNotCacheSecurePagesHandle;
   int _confirmLeaveModifiedPageHandle;
   int _animationCountHandle;
   int _emulationModeStringIndex;
   int _cookieAcceptHandle;
   int _rawDataCacheSizeHandle;
   int _pageCacheSizeHandle;
   int _defaultWapConfigDeterminedHandle;
   int _defaultMdsConfigDeterminedHandle;
   int _menuDelayTimeHandle;
   int _audioPlayerVolumeHandle;
   int _imageQualityHandle;
   int _defaultViewHandle;
   int _audioPathHandsetHandle;
   int _audioPathHandsfreeHandle;
   int _audioPathBluetoothHandle;
   int _audioPathHeadsetHandle;
   int _enableMobileCursorHandle;
   int _audioPathHeadsetHandsfreeHandle;
   int _audioPathBluetootha2dpHandle;
   WeakReference _emulationModeStringWR = (WeakReference)(new Object(null));
   int _showFullScreenHandle;
   final Object _persistentSync = new Object();
   static final long KEY;

   public GeneralProperty$GeneralPropertyData() {
      int minFontSize = 6;
      int defaultFontSize = 8;
      if (StringUtilities.strEqualIgnoreCase(Locale.getDefault().getLanguage(), "zh", 1701707776)) {
         minFontSize = 9;
         defaultFontSize = 9;
      }

      this._clearHandle = PersistentInteger.getId(4988251933819705505L, 0);
      this._executeHandle = PersistentInteger.getId(-5753227574626190370L, 0);
      this._quitHandle = PersistentInteger.getId(7423466083446598127L, 0);
      this._deleteHandle = PersistentInteger.getId(1811994284924603625L, 1);
      this._defaultCharsetModeHandle = PersistentInteger.getId(4252401799533787592L, 0);
      this._javascriptLocationHandle = PersistentInteger.getId(-7627500914324187861L, 0);
      this._doNotCacheSecurePagesHandle = PersistentInteger.getId(4546604513431963091L, 0);
      this._confirmLeaveModifiedPageHandle = PersistentInteger.getId(-8340817021720941990L, 1);
      this._animationCountHandle = PersistentInteger.getId(4610658888840416077L, 3);
      this._defaultFontSizeHandle = PersistentInteger.getId(-4436752438281299535L, defaultFontSize);
      this._minimumFontSizeHandle = PersistentInteger.getId(5055303530972575550L, minFontSize);
      this._minimumFontStyleHandle = PersistentInteger.getId(4256969707186071607L, 0);
      this._showFullScreenHandle = PersistentInteger.getId(-2530822912849957228L, 0);
      this._cookieAcceptHandle = PersistentInteger.getId(3101209072466186167L, 1);
      this._rawDataCacheSizeHandle = PersistentInteger.getId(-7550178878175683098L, 2048);
      this._pageCacheSizeHandle = PersistentInteger.getId(-2742488999105498644L, 1);
      this._defaultWapConfigDeterminedHandle = PersistentInteger.getId(-5579567576086342582L, 0);
      this._defaultMdsConfigDeterminedHandle = PersistentInteger.getId(7207860161686815871L, 0);
      this._menuDelayTimeHandle = PersistentInteger.getId(-1191945014529594429L, GeneralProperty.MENU_DELAY_TIME_DEFAULT);
      this._audioPlayerVolumeHandle = PersistentInteger.getId(-3164916047754673306L, 70);
      this._imageQualityHandle = PersistentInteger.getId(-2542665004339389283L, 1);
      this._defaultViewHandle = PersistentInteger.getId(8322570537492212114L, 0);
      this._audioPathHandsetHandle = PersistentInteger.getId(-3140942203161326241L, 70);
      this._audioPathHandsfreeHandle = PersistentInteger.getId(2631578963231385528L, 70);
      this._audioPathBluetoothHandle = PersistentInteger.getId(-9057183150407874039L, 70);
      this._audioPathHeadsetHandle = PersistentInteger.getId(7806606057975565055L, 70);
      this._audioPathHeadsetHandsfreeHandle = PersistentInteger.getId(-7231668217665147568L, 70);
      this._audioPathBluetootha2dpHandle = PersistentInteger.getId(-8832867053176672756L, 70);
      this._enableMobileCursorHandle = PersistentInteger.getId(2111923133048289738L, 1);
   }
}
