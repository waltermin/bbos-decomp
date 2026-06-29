package net.rim.device.apps.internal.vad;

import java.util.Calendar;
import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.CollectionLock;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.AudioHeadsetListener;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.PhoneCallListener;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.tts.SynthesizerQueueItem;
import net.rim.device.apps.api.tts.TTSEngineEvent;
import net.rim.device.apps.api.tts.TTSListener;
import net.rim.device.apps.api.tts.TTSManager;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.apps.internal.phone.pattern.SmartDialingOptions;
import net.rim.device.apps.internal.vad.resource.VSTStrings;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.media.MediaStreamingManager;
import net.rim.device.internal.media.MediaStreamingManager$StreamingSession;
import net.rim.device.internal.system.ActiveMedia;
import net.rim.device.internal.system.ActiveMediaObservable;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.EventDispatcher;
import net.rim.device.internal.system.InternalLowMemoryManager;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.vad.VADLanguageSetting;
import net.rim.device.internal.vad.VADNatives;
import net.rim.device.internal.vad.VADParameters;
import net.rim.device.internal.vad.VADPhoneInfo;
import net.rim.device.internal.vad.VADUserEventListener;
import net.rim.device.internal.vad.VADUserEvents;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.vm.Array;
import net.rim.vm.Memory;
import net.rim.vm.Message;

final class VADEngineManager
   extends EventDispatcher
   implements CollectionListener,
   SystemListener2,
   AudioHeadsetListener,
   PhoneCallListener,
   VADUserEventListener,
   PersistentContentListener,
   ActiveMedia,
   RealtimeClockListener,
   TTSManager {
   private ResourceBundleFamily _rbf = ResourceBundle.getBundle(6972709272210014688L, "net.rim.device.apps.internal.vad.resource.VAD");
   private ResourceBundle _rb;
   private String[] _phoneNumberTypeStrings;
   private VADFile[] _files = new VADFile[12];
   private int _engineState;
   private int _previousEngineState;
   private VADApplication _app;
   private AddressBook _addressBook;
   private Object[] _addressBookCards;
   private String[] _glueDefinedLabels;
   private int _queuedEvent;
   private VADPhoneInfo _phoneInfo;
   private PersistentObject _persistentObject;
   private VADPersistentData _persistentData;
   private VADEngineManager$VADSyncItem _syncItem;
   private int _activateRetries;
   private int _progressBase;
   private int _headsetButtonInvokeLater = -1;
   private MediaStreamingManager$StreamingSession _audioSession;
   private SynthesizerQueueItem _ttsEvents = null;
   private TTSListener[] _ttsListeners = new Object[0];
   private boolean _playingTTS;
   private boolean _cancelCurrentTTS;
   boolean _isDialling = false;
   private boolean _hasEnoughMemory = true;
   private int[] _availableLanguages;
   private String[] _vstStrings;
   private boolean _languageChanged;
   private String _encoding;
   private Object _ticket;
   private static final long VAD_OPTIONS_DATA_KEY = -7489694800188007771L;
   static final int ENGINE_OK = 0;
   static final int ENGINE_STARTING = 1;
   static final int ENGINE_ERROR_IT_POLICY = 2;
   static final int ENGINE_ERROR_OOM = 3;
   static final int ENGINE_ERROR_UNKNOWN = 4;
   static final int ENGINE_ERROR_NO_AUDIO = 5;
   static final int ENGINE_ERROR_CONTENT_PROTECTION = 6;
   static final int ENGINE_ERROR_VERSION_MISMATCH = 7;
   static final int ENGINE_ERROR_STARTING_CALL = 8;
   private static final int FILE_INVALID = 0;
   private static final int FILE_LVR = 1;
   private static final int FILE_PRM = 2;
   private static final int FILE_STR = 3;
   private static final int FILE_ELVIS = 4;
   private static final int FILE_ENROLL_IDX = 5;
   private static final int FILE_VRE_CN = 6;
   private static final int FILE_VST_PARAM = 7;
   private static final int FILE_VR_VERSION = 8;
   private static final int FILE_GRAMMAR_DUMP = 9;
   private static final int FILE_TEMP_ELVIS = 10;
   private static final int FILE_AUDIO_DATA = 11;
   private static final int NUM_FILES = 12;
   private static final int ENGINE_STATE_NONE = 0;
   private static final int ENGINE_STATE_INITIALIZING = 1;
   private static final int ENGINE_STATE_READY = 2;
   private static final int ENGINE_STATE_STOPPED = 3;
   private static final int ENGINE_STATE_PROCESSING_EVENT = 4;
   private static final int ENGINE_STATE_REBUILDING_ADDRESS_BOOK = 5;
   private static final int ENGINE_STATE_STARTING = 6;
   private static final int ENGINE_STATE_STOPPING = 7;
   private static final int ENGINE_STATE_PROCESSING_EXIT_EVENT = 8;
   private static final int ENGINE_STATE_VERSION_MISMATCH = 9;
   private static final int MAX_ADDRESS_BOOK_ENTRIES = 5000;
   private static final boolean DEBUG_FS = false;
   private static final boolean DEBUG_PB = false;
   private static final int TTS_ACTION_START = 0;
   private static final int TTS_ACTION_DONE = 1;
   private static final int TTS_ACTION_INTERRUPT = 2;
   private static final String[] LANGUAGE_EXTENSIONS = new Object[]{
      null,
      "en_US",
      null,
      null,
      null,
      "en_GB",
      "fr",
      "de",
      "es_MX",
      "pt_BR",
      "fr_CA",
      "it",
      "es",
      null,
      null,
      null,
      null,
      null,
      "zh_CN",
      null,
      null,
      "zh_HK",
      "nl",
      null,
      null,
      null,
      "pl",
      null
   };
   private static final int[] LOCALE_IDS = new int[]{
      0,
      1701729619,
      0,
      0,
      0,
      1701726018,
      1718747136,
      1684340736,
      1702055256,
      1886667346,
      1718764353,
      1769209856,
      1702035456,
      0,
      0,
      0,
      0,
      0,
      2053653326,
      0,
      0,
      2053654603,
      1852571648,
      0,
      0,
      0,
      1886126080,
      0,
      -805044216,
      67108864,
      1953066601,
      -805175268,
      16842752,
      1,
      0,
      16777216,
      16843009,
      257,
      16843008,
      -805044213,
      775162112,
      774909491,
      3420721,
      -805044208,
      1867910144,
      1147495273,
      1768710505,
      26478,
      -805044199,
      1699878656,
      1918985587,
      1226860643,
      1867325550,
      1852795252,
      1685343264,
      46,
      -805044130,
      1701719296,
      1769090676,
      1701064301,
      1701013878,
      1886413102,
      1852386931,
      1852990836,
      1982753889,
      1915642977,
      1970238309,
      778396530,
      4473174,
      1952804397,
      1835627054,
      1986356270,
      778396521,
      1936748641,
      1953392942,
      1634628197,
      1635135084,
      1701981796,
      1920298867,
      1445881187,
      17473,
      -804650960,
      1,
      2,
      11,
      22,
      23,
      33,
      34,
      37,
      38,
      40,
      41,
      42,
      43,
      68,
      76,
      77,
      105,
      106,
      107,
      121,
      161,
      229,
      236,
      237,
      238,
      239,
      240,
      241,
      242,
      243
   };
   private static final boolean[] LANGUAGE_UTF8_ENCODING = new boolean[]{
      false,
      false,
      true,
      true,
      true,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      true,
      true,
      true,
      true,
      true,
      true,
      true,
      false,
      false,
      false,
      true,
      true,
      true
   };
   private static final int[] LABEL_TYPES = new int[]{
      3,
      1,
      5,
      6,
      8,
      -805044222,
      49952,
      -804651007,
      51,
      207814912,
      -1957459347,
      12198851,
      2781953,
      1093300993,
      1979777140,
      6646639,
      1802466817,
      185683045,
      67109888,
      15474537
   };
   private static String VAD_RESOURCE_PREFIX = "net_rim_vad_engine_resource__";

   protected final MediaStreamingManager$StreamingSession getSession() {
      return this._audioSession;
   }

   final int getVADLanguageForLocale(Locale locale) {
      switch (locale.getCode()) {
         case 1684340736:
            return 7;
         case 1701726018:
            return 5;
         case 1702035456:
            return 12;
         case 1718747136:
            return 6;
         case 1769209856:
            return 11;
         case 1852571648:
            return 22;
         case 1886126080:
            return 26;
         case 1886650368:
            return 13;
         case 1886667346:
            return 9;
         case 1920270336:
            return 17;
         case 2053636096:
         case 2053653326:
         case 2053654603:
            return 18;
         default:
            return 1;
      }
   }

   final void nextLanguage() {
      int language = this._persistentData._parameters._language;
      int i = Arrays.getIndex(this._availableLanguages, language);
      if (++i == this._availableLanguages.length) {
         i = 0;
      }

      this.setLanguage(this._availableLanguages[i]);
   }

   final String getVSTString(int id) {
      return this._vstStrings[id];
   }

   final String getResourceString(int id) {
      try {
         return this._rb.getString(id);
      } finally {
         return this._rbf.getString(id);
      }
   }

   final void setApplication(VADApplication app) {
      app.addListener(31, this);
      app.addSystemListener(this);
      app.addRadioListener(this);
      app.addRealtimeClockListener(this);
      Audio.addListener(app, this);
      VADUserEvents.addListener(app, this);
      this._app = app;
   }

   final void writeFileData(int handle, Object[] data) {
      if (this._hasEnoughMemory) {
         int size = 0;

         for (int i = data.length - 1; i >= 0; i--) {
            if (data[i] instanceof byte[]) {
               size += ((byte[])data[i]).length * 4;
            }
         }

         if (!ensureAvailableFlash(size)) {
            this._hasEnoughMemory = false;
            this._app.uiError(3);
            this._engineState = 2;
            this.deactivate();
         } else {
            this._persistentData.setFileData(handle, data);
            this._persistentObject.commit();
         }
      }
   }

   final Object[] getFileData(int handle) {
      return this._persistentData.getFileData(handle);
   }

   final VADParameters getParameters() {
      return this._persistentData._parameters;
   }

   final void commitPersistentData() {
      this._persistentObject.commit();
      this._syncItem.fireSyncItemUpdated();
   }

   final boolean isAddressBookEmpty() {
      return this._persistentData._addressBookEmpty && this._persistentData._incrementalNameEncodings == null;
   }

   final boolean sendEvent(int event) {
      switch (this._engineState) {
         case -1:
         case 2:
         case 5:
            VADEventLog.log(1163284052, event);
            int rc = VADNatives.sendEvent(event);
            if (this.checkError("sendEvent", rc)) {
               return false;
            } else {
               this._previousEngineState = this._engineState;
               if (event == 28) {
                  this._engineState = 8;
                  return true;
               }

               this._engineState = 4;
               return true;
            }
         case 0:
         case 1:
         case 3:
         case 7:
         case 8:
         case 9:
         default:
            return false;
         case 4:
         case 6:
            if (this._queuedEvent == -1) {
               VADEventLog.log(1163284821, event);
               this._queuedEvent = event;
               return false;
            } else {
               if (event == 28) {
                  VADEventLog.log(1163284821, event);
                  this._queuedEvent = event;
               }

               return false;
            }
      }
   }

   @Override
   public final boolean isForce() {
      return false;
   }

   @Override
   public final boolean isAlert() {
      return false;
   }

   @Override
   public final void cancelTTS() {
      this._cancelCurrentTTS = true;
   }

   @Override
   public final boolean startTTSEngine(TTSListener listener) {
      this._playingTTS = true;
      this.addTTSListener(listener, listener.getPriority());
      switch (this._engineState) {
         case 1:
         case 5:
         case 6:
            break;
         case 2:
         case 4:
            listener.notifyTTSEngineListener((TTSEngineEvent)(new Object(1)));
            break;
         case 3:
         case 7:
         default:
            ActiveMediaObservable.setActive(this);
            int rc = this.startEngine();
            switch (rc) {
               case 0:
                  break;
               default:
                  ActiveMediaObservable.setInactive(this);
                  return false;
            }
      }

      return true;
   }

   @Override
   public final boolean stopTTSEngine(TTSListener listener) {
      if (this._ttsListeners.length > 1) {
         this.removeTTSListener(listener);
         listener.notifyTTSEngineListener((TTSEngineEvent)(new Object(2)));
         return true;
      } else {
         this.stopEngine(false);
         return true;
      }
   }

   @Override
   public final boolean playTTS(SynthesizerQueueItem item) {
      ApplicationControl.assertMediaPermitted(true, CommonResource.getBundle(), 10177);
      if (item.getText().length() > 200) {
         return false;
      }

      this._playingTTS = true;
      this._cancelCurrentTTS = false;
      switch (this._engineState) {
         case 0:
         case 2:
         case 5:
            this._ttsEvents = item;
            this.playNextTTS(0);
            return true;
         case 3:
         default:
            ActiveMediaObservable.setActive(this);
            int rc = this.startEngine();
            switch (rc) {
               case 0:
                  break;
               default:
                  ActiveMediaObservable.setInactive(this);
                  return false;
            }
         case 1:
         case 4:
         case 6:
            this._ttsEvents = item;
            return true;
      }
   }

   @Override
   public final void setTTSVolume(int volume) {
      if (this._audioSession != null) {
         this._audioSession.setVolume(volume);
      }
   }

   @Override
   public final void setTTSSpeed(int speed) {
      this._persistentData._parameters._ttsSpeed = speed;
      int rc = VADNatives.setParameters(this._persistentData._parameters);
      this.checkError("setParameters", rc);
   }

   @Override
   public final boolean isIdleForTTS() {
      return this._ttsEvents == null;
   }

   @Override
   public final int codecUsed() {
      return 7;
   }

   @Override
   public final boolean isAudioInUse() {
      return true;
   }

   @Override
   public final void vadEvent(int event, int context) {
      switch (event) {
         case -1:
         case 1:
            break;
         case 0:
         default:
            this.activate(context);
            return;
         case 2:
            this.deactivate();
            return;
         case 3:
            ActiveMediaObservable.setInactive(this);
            return;
         case 4:
            switch (this._engineState) {
               case 1:
                  return;
               case 3:
               default:
                  this._app.uiInitializing();
                  int rc = this.startEngine();
                  if (rc != 0) {
                     this._app.uiError(rc);
                     return;
                  }
               case 2:
                  this.sendEvent(24);
                  return;
            }
         case 5:
            switch (this._engineState) {
               case 1:
                  break;
               case 3:
               default:
                  int rc = this.startEngine();
                  if (rc != 0) {
                     return;
                  }
               case 2:
                  this.sendEvent(23);
            }
      }
   }

   @Override
   public final void reset(Collection collection) {
      this._persistentData._rebuildAddressBook = true;
      this._persistentData._incrementalNameEncodings = null;
      this._persistentObject.commit();
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.addIncrementalName(element);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.addIncrementalName(newElement);
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this._persistentData._rebuildAddressBookScheduled = true;
      this._persistentObject.commit();
   }

   @Override
   public final void powerOff() {
      this.deactivate();
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerOffRequested(int reason) {
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void fastReset() {
      this._app.uiExit();
      this.initEngine();
   }

   @Override
   public final void backlightStateChange(boolean on) {
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }

   @Override
   public final void headsetButtonClick(int button, int time) {
      if (button == 0 || button == 1) {
         if (!Phone.getInstance().isActive()) {
            if (this._engineState == 3) {
               if (this._headsetButtonInvokeLater == -1) {
                  this._headsetButtonInvokeLater = this._app.invokeLater(new VADEngineManager$HeadsetButtonRunnable(this), 1500, false);
                  return;
               }
            } else {
               this.deactivate();
            }
         }
      }
   }

   @Override
   public final void headsetButtonUnclick(int button, int time) {
      if (button == 0 || button == 1) {
         if (this._headsetButtonInvokeLater != -1) {
            this._app.cancelInvokeLater(this._headsetButtonInvokeLater);
            this._headsetButtonInvokeLater = -1;
         }
      }
   }

   @Override
   public final void headsetInserted(int type) {
   }

   @Override
   public final void headsetRemoved() {
   }

   @Override
   public final void callIncoming(int callId) {
      this.deactivate();
   }

   @Override
   public final void callDisplayUpdated(int callId) {
   }

   @Override
   public final void callWaiting(int callId) {
   }

   @Override
   public final void callInitiated(int callId) {
      this.deactivate();
   }

   @Override
   public final void callConnected(int callId) {
   }

   @Override
   public final void callFailed(int callId, int error) {
   }

   @Override
   public final void callDelivered(int callId) {
   }

   @Override
   public final void callManipulateFailed(int callId, int error) {
   }

   @Override
   public final void callDisconnected(int callId) {
   }

   @Override
   public final void callHeld(int callId) {
   }

   @Override
   public final void callResumed(int callId) {
   }

   @Override
   public final void callAdded(int callId) {
   }

   @Override
   public final void callRemoved(int callId) {
   }

   @Override
   public final void callTransferred(int status, int reason) {
   }

   @Override
   public final void callTransferStateUpdated(int callId, int state) {
   }

   @Override
   public final void callVoicePrivacyUpdated(int callId, boolean on) {
   }

   @Override
   public final void callOTAStatusUpdated(int callId, int status) {
   }

   @Override
   public final void dtmfData(int dtmf) {
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      boolean encrypt = getEncryptFlag();

      for (int i = this._files.length - 1; i >= 0; i--) {
         VADFile file = this._files[i];
         if (file instanceof VADContentProtectedFile) {
            ((VADContentProtectedFile)file).reCrypt(encrypt);
         }
      }

      if (this._persistentData._incrementalNameEncodings != null) {
         for (int i = this._persistentData._incrementalNameEncodings.length - 1; i >= 0; i--) {
            if (!PersistentContent.checkEncoding(this._persistentData._incrementalNameEncodings[i], false, encrypt)) {
               this._persistentData._incrementalNameEncodings[i] = PersistentContent.reEncode(this._persistentData._incrementalNameEncodings[i], false, encrypt);
            }
         }
      }

      this._persistentObject.commit();
   }

   @Override
   public final void clockUpdated() {
      if ((this._persistentData._rebuildAddressBookScheduled || this._persistentData._rebuildAddressBook)
         && this._engineState == 3
         && DeviceInfo.getIdleTime() >= 600
         && !Backlight.isEnabled()) {
         Calendar cal = Calendar.getInstance();
         if (cal.get(11) == 2 && cal.get(12) == 0) {
            this._persistentData._rebuildAddressBook = true;
            this.startEngine();
         }
      }
   }

   private final void stopEngine(boolean callStarting) {
      if (this._engineState != 3) {
         this._ticket = null;
         VADEventLog.log(1398034256);
         this._app.invokeLater(new VADEngineManager$2(this), 250, false);
         int rc = VADNatives.stop();
         if (!this.checkError("stop", rc)) {
            this._engineState = 7;
            this._addressBookCards = null;
            this._queuedEvent = -1;
            this._glueDefinedLabels = null;
            this._audioSession.release();
            if (callStarting) {
               this._app.invokeLater(new VADEngineManager$RemoveSourceRunnable(this), 5000, false);
            } else {
               AudioRouter.getInstance().removeSource(1);
            }
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void dispatch(Message message, Object listener) {
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      int data1 = message.getData1();
      int dataLength = message.getDataLength();
      Object object0 = message.getObject0();
      Object object1 = message.getObject1();
      byte[] data = null;

      try {
         switch (message.getEvent()) {
            case 7936:
               String fileName = (String)object0;
               int handle = this.getFileHandle(fileName);
               if (this._files[handle].open(fileName)) {
                  VADNatives.operationComplete(handle);
               } else {
                  VADNatives.operationComplete(0);
               }
               break;
            case 7937:
               this.checkFileHandle(data0);
               this._files[data0].close();
               VADNatives.operationComplete(0);
               break;
            case 7938:
               String var40 = object0;
               int var36 = this.getFileHandle((String)var40);
               boolean b = false;
               if (var36 != 0) {
                  b = this._files[var36].exists();
               }

               VADNatives.operationComplete(b ? 1 : 0);
               break;
            case 7939:
               String var39 = object0;
               int var35 = this.getFileHandle((String)var39);
               if (var35 != 0) {
                  this._files[var35].delete();
               }

               VADNatives.operationComplete(0);
               break;
            case 7940:
               String var38 = object0;
               int var34 = this.getFileHandle((String)var38);
               int size = 0;
               if (var34 != 0) {
                  size = this._files[var34].size();
               }

               VADNatives.writeInt(subMessage, size);
               VADNatives.operationComplete(0);
               break;
            case 7941:
               data = (byte[])object1;
               this.checkFileHandle(data0);
               this._files[data0].write(data, data1);
               VADNatives.operationComplete(dataLength);
               break;
            case 7942:
               this.checkFileHandle(data0);
               this._files[data0].read(subMessage, data1, dataLength);
               VADNatives.operationComplete(dataLength);
               break;
            case 7944:
               String fileName1 = (String)object0;
               String fileName2 = (String)object1;
               int handle1 = this.getFileHandle(fileName1);
               int handle2 = this.getFileHandle(fileName2);
               if (handle1 != 0 && handle2 != 0) {
                  this._files[handle2].copy(this._files[handle1]);
                  this._files[handle1].delete();
               }

               VADNatives.operationComplete(0);
               break;
            case 7945:
               String fileName = object0;
               int handle = this.getFileHandle((String)fileName);
               int location = 0;
               if (handle != 0) {
                  VADFile var10000 = this._files[handle];
                  if (this._files[handle] instanceof VADMemoryMappedResourceFile) {
                     VADMemoryMappedResourceFile file = (VADMemoryMappedResourceFile)var10000;
                     location = file.getMemoryMapLocation();
                  }
               }

               VADNatives.operationComplete(location);
               break;
            case 7952:
               String number = (String)object0;
               String name = (String)object1;
               ContextObject context = (ContextObject)(new Object());
               if (name != null && name.length() != 0) {
                  ContextObject.setFlag(context, 117);
               }

               PhoneUtilities.setPrivateFlag(context, 81);
               if (ApplicationManager.getApplicationManager().isSystemLocked()) {
                  ContextObject.setPrivateFlag(context, 4936088360624690805L, 91);
               }

               VoiceServices.getVoiceApplication().requestForeground(new VADEngineManager$1(this, number, context), null);
               this.stopEngine(true);
               break;
            case 7953:
            case 7954:
            case 7955:
            case 7956:
            case 7957:
            case 7959:
               if (this._playingTTS) {
                  VADEventLog.log(1163413844);
                  this.playNextTTS(1);
                  break;
               }
            case 7960:
               VADEventLog.log(1163413844, message.getEvent());

               for (int i = this._files.length - 1; i >= 0; i--) {
                  if (this._files[i] != null) {
                     this._files[i].close();
                  }
               }

               this._app.uiExit();
               this.stopEngine(false);
               break;
            case 7958:
               VADEventLog.log(1094863956);
               this._app.setUIProgress(100);
               this._progressBase = 0;
               this._persistentData._rebuildAddressBook = false;
               this._persistentData._rebuildAddressBookScheduled = false;
               this._persistentObject.commit();
               this._addressBookCards = null;
               this._engineState = 2;
               if (this._queuedEvent != -1) {
                  this.handleQueuedEvent();
               } else {
                  this._app.uiExit();
                  this.stopEngine(false);
               }
               break;
            case 7971:
               this._app.uiUpdate((String)(new Object((byte[])object0, this._encoding)), (byte[][][])((byte[][])object1));
               VADNatives.operationComplete(0);
               break;
            case 7984:
               VADNatives.operationComplete(LABEL_TYPES.length + 4);
               break;
            case 7985:
               byte[][][] labels = new byte[0][][];

               for (int i = 0; i < LABEL_TYPES.length; i++) {
                  String label = this.getTypeString(LABEL_TYPES[i]);
                  Arrays.add(labels, label.getBytes(this._encoding));
                  switch (LABEL_TYPES[i]) {
                     case 1:
                     case 3:
                        String x = ((StringBuffer)(new Object())).append(label).append(" 1").toString();
                        Arrays.add(labels, x.getBytes(this._encoding));
                        x = ((StringBuffer)(new Object())).append(label).append(" 2").toString();
                        Arrays.add(labels, x.getBytes(this._encoding));
                        break;
                  }
               }

               VADNatives.writeNameData(subMessage, (byte[][])labels);
               VADNatives.operationComplete(labels.length);
               break;
            case 7986:
               if (this._addressBookCards == null) {
                  VADNatives.operationComplete(0);
               } else {
                  VADNatives.operationComplete(this._addressBookCards.length);
               }

               this._progressBase = 0;
               break;
            case 7987:
               if (this._addressBookCards == null) {
                  VADNatives.operationComplete(0);
               } else {
                  int offset = data0;
                  int length = data1;
                  this._app.setUIProgress((this._progressBase + offset) * 33 / this._addressBookCards.length);
                  if (offset + length >= this._addressBookCards.length) {
                     length = this._addressBookCards.length - offset;
                     this._progressBase = this._progressBase + this._addressBookCards.length;
                  }

                  byte[][][] names = new byte[length][][];

                  for (int i = 0; i < length; i++) {
                     Object obj = this._addressBookCards[i + offset];
                     if (obj instanceof byte[]) {
                        names[i] = (byte[][])((byte[])obj);
                     } else {
                        if (!(obj instanceof Object)) {
                           throw new Object();
                        }

                        byte[] namex = obj.toString().getBytes(this._encoding);
                        this._addressBookCards[i + offset] = namex;
                        names[i] = (byte[][])namex;
                     }
                  }

                  VADNatives.writeNameData(subMessage, (byte[][])names);
                  VADNatives.operationComplete(length);
               }
               break;
            case 7988:
            case 7989:
            case 7992:
            case 7993:
               VADNatives.operationComplete(0);
               break;
            case 7990:
               if (this._persistentData._incrementalNameEncodings == null) {
                  VADNatives.operationComplete(0);
               } else {
                  VADNatives.operationComplete(this._persistentData._incrementalNameEncodings.length);
               }
               break;
            case 7991:
               if (this._persistentData._incrementalNameEncodings == null) {
                  VADNatives.operationComplete(0);
               } else {
                  int offset = data0;
                  int length = data1;
                  if (offset + length > this._persistentData._incrementalNameEncodings.length) {
                     length = this._persistentData._incrementalNameEncodings.length - offset;
                  }

                  byte[][][] names = new byte[length][][];

                  for (int i = 0; i < length; i++) {
                     names[i] = (byte[][])PersistentContent.decodeByteArray(this._persistentData._incrementalNameEncodings[i + offset]);
                  }

                  VADNatives.writeNameData(subMessage, (byte[][])names);
                  VADNatives.operationComplete(length);
               }
               break;
            case 7994:
               String name = (String)(new Object((byte[])object0, this._encoding));
               if (this._addressBook != null && name != null && name.length() != 0) {
                  StringBuffer sb = (StringBuffer)(new Object(name));
                  StringUtilities.convertToOriginal(sb, 0, sb.length());
                  name = sb.toString();
                  Object[] lookup = this._addressBook.lookup(name.toLowerCase(), 6);
                  if (lookup == null) {
                     VADNatives.operationComplete(0);
                  } else {
                     byte[] types = new byte[0];
                     byte[][][] numbers = new byte[0][][];
                     boolean checkForCompanyName = lookup.length > 1;
                     String companyName = null;

                     label413:
                     for (int i = 0; i < lookup.length; i++) {
                        AddressCardModel card = (AddressCardModel)lookup[i];
                        Object[] models = card.getPhoneNumberModels();
                        byte[] home2Number = null;
                        byte[] work2Number = null;

                        for (int j = 0; j < models.length; j++) {
                           if (checkForCompanyName) {
                              CompanyInfoModel companyInfo = card.getCompanyInfo();
                              if (companyInfo != null) {
                                 companyName = companyInfo.getCompanyName();
                              }
                           }

                           PhoneNumberModel pnm = (PhoneNumberModel)models[j];
                           int modelType = pnm.getType();
                           if (modelType != 7) {
                              int vstNumberType;
                              if (companyName == null) {
                                 switch (modelType) {
                                    case -1:
                                       vstNumberType = 4 + this._glueDefinedLabels.length;
                                       Arrays.add(this._glueDefinedLabels, this.getTypeString(modelType));
                                       break;
                                    case 0:
                                    case 1:
                                       vstNumberType = 2;
                                       break;
                                    case 2:
                                       work2Number = pnm.toString().getBytes(this._encoding);
                                       continue;
                                    case 3:
                                    default:
                                       vstNumberType = 1;
                                       break;
                                    case 4:
                                       home2Number = pnm.toString().getBytes(this._encoding);
                                       continue;
                                    case 5:
                                       vstNumberType = 3;
                                 }
                              } else {
                                 switch (modelType) {
                                    case 2:
                                       modelType = 1;
                                       break;
                                    case 4:
                                       modelType = 3;
                                 }

                                 String glueDefinedLabel = ((StringBuffer)(new Object()))
                                    .append(companyName)
                                    .append(' ')
                                    .append(this.getTypeString(modelType))
                                    .toString();
                                 int index = Arrays.getIndex(this._glueDefinedLabels, glueDefinedLabel);
                                 if (index == -1) {
                                    index = this._glueDefinedLabels.length;
                                    Arrays.add(this._glueDefinedLabels, glueDefinedLabel);
                                 }

                                 vstNumberType = 4 + index;
                              }

                              Arrays.add(types, (byte)vstNumberType);
                              Arrays.add(numbers, pnm.toString().getBytes(this._encoding));
                              if (types.length == 30) {
                                 break label413;
                              }
                           }
                        }

                        if (home2Number != null) {
                           Arrays.add(types, (byte)1);
                           Arrays.add(numbers, home2Number);
                           if (types.length == 30) {
                              break;
                           }
                        }

                        if (work2Number != null) {
                           Arrays.add(types, (byte)2);
                           Arrays.add(numbers, work2Number);
                           if (types.length == 30) {
                              break;
                           }
                        }
                     }

                     VADNatives.writePhonebookData(subMessage, types, (byte[][])numbers);
                     VADNatives.operationComplete(types.length);
                  }
               } else {
                  VADNatives.operationComplete(0);
               }
               break;
            case 7995:
               this.populatePhoneInfo();
               VADNatives.writePhoneInfo(subMessage, this._phoneInfo);
               VADNatives.operationComplete(0);
               break;
            case 7996:
               String label;
               switch (data0) {
                  case 0:
                     int index = data0 - 4;
                     if (index >= 0 && index < this._glueDefinedLabels.length) {
                        label = this._glueDefinedLabels[index];
                        break;
                     }

                     label = "";
                     break;
                  case 1:
                  default:
                     label = this.getTypeString(3);
                     break;
                  case 2:
                     label = this.getTypeString(1);
                     break;
                  case 3:
                     label = this.getTypeString(5);
               }

               if (dataLength > 1) {
                  label = ((StringBuffer)(new Object())).append(label).append(" ").append(data1 + 1).toString();
               }

               VADNatives.writeLabelData(subMessage, label.getBytes(this._encoding));
               VADNatives.operationComplete(0);
               break;
            case 8000:
               VADEventLog.log(1146048069);
               switch (this._engineState) {
                  case 1:
                     String[] info = VADLanguageSetting.getInstance().getVersionInfo();
                     if (info != null && info.length > 2 && info[2].endsWith("0238")) {
                        this._engineState = 3;
                     } else {
                        this._engineState = 9;
                     }

                     return;
                  case 4:
                     this._engineState = this._previousEngineState;
                     if (this._engineState != 5) {
                        if (this._playingTTS) {
                           this.playNextTTS(2);
                        } else {
                           this.handleQueuedEvent();
                        }

                        return;
                     }

                     return;
                  case 6:
                     this._engineState = 2;
                     if (this._playingTTS) {
                        this.ttsEngineStarted();
                        this.playNextTTS(0);
                        return;
                     } else {
                        if (!this._persistentData._rebuildAddressBook && Locale.getSystemNameOrder() == this._persistentData._nameOrder) {
                           this.handleQueuedEvent();
                        } else {
                           this.rebuildAddressBook();
                        }

                        return;
                     }
                  case 7:
                     this._engineState = 3;
                     if (this._playingTTS) {
                        this.ttsEngineStopped();
                     }

                     return;
                  default:
                     return;
               }
            default:
               VADEventLog.log(((StringBuffer)(new Object("Unknown event: "))).append(Integer.toHexString(message.getEvent())).toString());
               return;
         }
      } catch (Throwable var33) {
         VADEventLog.log(((StringBuffer)(new Object("Message handing error: "))).append(ex).toString());
         System.out.println("Stack trace:");
         ex.printStackTrace();
         VADNatives.operationComplete(0);
         return;
      }
   }

   private final void populatePhoneInfo() {
      int service = RadioInfo.getNetworkService();
      if (service == 0) {
         this._phoneInfo._coverage = 0;
      } else if ((service & 1) != 0) {
         this._phoneInfo._coverage = 1;
      } else if ((service & 2) != 0) {
         if ((CoverageInfo.getCoverageStatus() & 6) != 0) {
            this._phoneInfo._coverage = 7;
         } else {
            this._phoneInfo._coverage = 2;
         }
      }

      int rssi = RadioInfo.getSignalLevel();
      if (rssi == -256) {
         this._phoneInfo._signalStrength = 0;
      } else if (rssi >= -77) {
         this._phoneInfo._signalStrength = 5;
      } else if (rssi >= -86) {
         this._phoneInfo._signalStrength = 4;
      } else if (rssi >= -92) {
         this._phoneInfo._signalStrength = 3;
      } else if (rssi >= -101) {
         this._phoneInfo._signalStrength = 2;
      } else {
         this._phoneInfo._signalStrength = 1;
      }

      int level = DeviceInfo.getBatteryLevel();
      if (level >= 75) {
         this._phoneInfo._batteryLevel = 5;
      } else if (level >= 25) {
         this._phoneInfo._batteryLevel = 3;
      } else if (level >= 5) {
         this._phoneInfo._batteryLevel = 1;
      } else {
         this._phoneInfo._batteryLevel = 0;
      }
   }

   static final VADEngineManager getInstance() {
      boolean init = false;
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      VADEngineManager manager;
      synchronized (dispatchManager) {
         manager = (VADEngineManager)dispatchManager.getDispatcher(31);
         if (manager == null) {
            manager = new VADEngineManager();
            init = true;
            dispatchManager.setDispatcher(31, manager);
         }
      }

      if (init) {
         manager.init();
      }

      return manager;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final boolean attemptFlashRecovery(int size) {
      int originalSize = size;
      boolean retval = true;
      if (size > Memory.getFlashFree()) {
         VADEngineManager$VADLowMemoryListener lmmListener = new VADEngineManager$VADLowMemoryListener();
         boolean var13 = false /* VF: Semaphore variable */;

         try {
            label106: {
               var13 = true;
               LowMemoryManager.addLowMemoryFailedListener(lmmListener);

               for (int i = 0; i < 8; i++) {
                  synchronized (RIMPersistentStore.getSynchObject()) {
                     synchronized (CollectionLock.getGlobalLock()) {
                        Memory.recoverFlash(size);
                        InternalLowMemoryManager.exactPoll();
                     }
                  }

                  if (lmmListener._lmm_failed || originalSize <= Memory.getFlashFree()) {
                     Memory.persistentGC();
                     if (originalSize <= Memory.getFlashFree()) {
                        var13 = false;
                        break label106;
                     }
                  }

                  size += 16384;
               }

               var13 = false;
            }
         } finally {
            if (var13) {
               LowMemoryManager.removeLowMemoryFailedListener(lmmListener);
            }
         }

         LowMemoryManager.removeLowMemoryFailedListener(lmmListener);
         retval = !lmmListener._lmm_failed;
         if (retval) {
            retval = originalSize <= Memory.getFlashFree();
         }
      }

      return retval;
   }

   private final void activate(int context) {
      if (!Phone.getInstance().isActive() && !this._isDialling) {
         this._hasEnoughMemory = true;
         if (context != 0 || !ApplicationManager.getApplicationManager().isSystemLocked() || Security.getInstance().getAllowOutgoingCallWhileLocked()) {
            switch (this._engineState) {
               case 3:
                  ActiveMediaObservable.setActive(this);
                  this._app.uiInitializing();
                  int rc = this.startEngine();
                  switch (rc) {
                     case 0:
                        if (AudioRouter.getInstance().canEnableSink(1, 2, true) && context != 2) {
                           return;
                        }
                        break;
                     case 3:
                     case 5:
                        if (++this._activateRetries < 5) {
                           this._app.invokeLater(new VADEngineManager$ActivateRunnable(this, context), 500, false);
                           return;
                        }
                     default:
                        ActiveMediaObservable.setInactive(this);
                        this._app.uiError(rc);
                        return;
                  }
               case 2:
               case 6:
                  this._activateRetries = 0;
                  this.sendEvent(25);
                  return;
               case 9:
                  this._app.uiError(7);
                  return;
            }
         }
      }
   }

   private final void deactivate() {
      this.sendEvent(28);
   }

   private final void handleQueuedEvent() {
      if (this._queuedEvent != -1 && this.sendEvent(this._queuedEvent)) {
         this._queuedEvent = -1;
      }
   }

   private final void playNextTTS(int action) {
      if (action == 2) {
         this._ttsEvents.updateState(4);
         if (this._cancelCurrentTTS) {
            this.sayText("");
            this._ttsEvents.updateState(8);
            this._ttsEvents = null;
         } else {
            if (!this.shouldInterrupt()) {
               return;
            }

            this._ttsEvents.updateState(8);
            this._ttsEvents = null;
         }
      } else if (action == 1) {
         this._ttsEvents.updateState(16);
         this._ttsEvents = null;
      }

      if (this._ttsEvents != null) {
         this.setTTSVolume(this._ttsEvents.getTTSEngineListener().getVolume());
         if (this._persistentData._parameters._ttsSpeed != this._ttsEvents.getTTSEngineListener().getSpeakingRate()) {
            this.setTTSSpeed(this._ttsEvents.getTTSEngineListener().getSpeakingRate());
         }

         this.sayText(this._ttsEvents.getText());
      } else {
         this.prodNext();
      }
   }

   private final void prodNext() {
      for (int i = 0; i < this._ttsListeners.length; i++) {
         if (this._ttsListeners[i].hasSomethingToSay()) {
            this._ttsListeners[i].playNext();
            return;
         }
      }
   }

   private final boolean shouldInterrupt() {
      for (int i = 0; i < this._ttsListeners.length; i++) {
         if (this._ttsListeners[i].equals(this._ttsEvents.getTTSEngineListener())) {
            return false;
         }

         if (this._ttsListeners[i].hasSomethingToSay()) {
            return true;
         }
      }

      return false;
   }

   private final void sayText(String text) {
      try {
         text = ((StringBuffer)(new Object())).append(text).append('\u0000').toString();
         byte[] bytes = text.getBytes(this._encoding);
         this._previousEngineState = this._engineState;
         this._engineState = 4;
         int rc = VADNatives.playTTS(bytes);
         System.out.println(((StringBuffer)(new Object("VADNatives.playTTS rc="))).append(rc).toString());
         this.checkError("PLAY_TTS", rc);
      } finally {
         return;
      }
   }

   public static final boolean ensureAvailableFlash(int size) {
      boolean flashRecovered = attemptFlashRecovery(size);
      if (!flashRecovered) {
         Memory.emergencyGC();
         flashRecovered = attemptFlashRecovery(size);
      }

      return flashRecovered;
   }

   private final void ttsEngineStarted() {
      for (int i = 0; i < this._ttsListeners.length; i++) {
         this._ttsListeners[i].notifyTTSEngineListener((TTSEngineEvent)(new Object(1)));
      }
   }

   private final void loadResources(int language) {
      int code = LOCALE_IDS[language];
      if (code == 2053654603) {
         code = 2053653326;
      }

      Locale locale = Locale.get(code);
      this._rb = this._rbf.getBundle(locale);
      this._phoneNumberTypeStrings = AbstractPhoneNumberModel.getPhoneNumberTypeStrings(locale);
   }

   private final void ttsEngineStopped() {
      for (int i = 0; i < this._ttsListeners.length; i++) {
         this._ttsListeners[i].notifyTTSEngineListener((TTSEngineEvent)(new Object(2)));
         this.removeTTSListener(this._ttsListeners[i]);
      }
   }

   private final void loadLanguageFiles() {
      int language = this._persistentData._parameters._language;
      if (Arrays.getIndex(this._availableLanguages, language) == -1) {
         language = this._availableLanguages[0];
         this._persistentData._parameters._language = language;
         this._persistentObject.commit();
      }

      if (LANGUAGE_UTF8_ENCODING[language]) {
         this._encoding = "UTF-8";
      } else {
         this._encoding = "ISO-8859-1";
      }

      String file = ((StringBuffer)(new Object())).append(VAD_RESOURCE_PREFIX).append(LANGUAGE_EXTENSIONS[language]).toString();
      Resource resource = Resource$Internal.getResourceClass(file);
      if (resource == null) {
         throw new Object(((StringBuffer)(new Object("Could not load language resources from "))).append(file).toString());
      }

      this._files[1] = new VADResourceFile(this.getResource(resource, "lvr.bin"));
      this._files[2] = new VADMemoryMappedResourceFile(this.getResource(resource, "prm.bin"), 0);
      this._files[3] = new VADMemoryMappedResourceFile(this.getResource(resource, "str.bin"), 1);
      this.loadResources(language);
      byte[] b = ((VADResourceFile)this._files[3]).getData();
      this._vstStrings = VSTStrings.loadStrings(b, this._encoding, false);
      if (this._vstStrings == null) {
         if (language == 1) {
            throw new Object("Language resources are corrupt");
         }

         this.setLanguage(1);
         this.loadLanguageFiles();
      }

      this._languageChanged = false;
   }

   private final void init() {
      VADEventLog.init();
      this._persistentObject = RIMPersistentStore.getPersistentObject(-7489694800188007771L);
      synchronized (this._persistentObject) {
         this._persistentData = (VADPersistentData)this._persistentObject.getContents();
         if (this._persistentData == null) {
            this._persistentData = new VADPersistentData();
            this._persistentData._parameters._language = this.getVADLanguageForLocale(Locale.getDefault());
            this._persistentObject.setContents(this._persistentData, 51, false);
            this._persistentObject.commit();
         }
      }

      this.loadLanguages();
      this.loadLanguageFiles();
      this._files[4] = new VADContentProtectedFile(this, 4);
      this._files[5] = new VADPersistentFile(this, 5);
      this._files[6] = new VADPersistentFile(this, 6);
      this._files[8] = new VADPersistentFile(this, 8);
      this._files[7] = new VADPersistentFile(this, 7);
      this._files[10] = new VADTemporaryFile();
      this._files[9] = new VADTemporaryFile();
      this._files[11] = new VADAudioFile();
      if (!this._files[4].exists()) {
         this._persistentData._rebuildAddressBook = true;
      }

      this._addressBook = AddressBookServices.getAddressBook(true);
      if (this._addressBook != null) {
         this._addressBook.addCollectionListener(this);
      }

      this._phoneInfo = (VADPhoneInfo)(new Object());
      this._syncItem = new VADEngineManager$VADSyncItem(this);
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(this._syncItem);
      }

      PersistentContent.addWeakListener(this);
      VADOptionsScreen.init();
      new VADEngineManager$VADLanguageSettingImpl(this);
      this.initEngine();
   }

   private final byte[][][] getResource(Resource resource, String name) {
      byte[][][] array = new byte[0][][];
      int offset = 0;

      while (true) {
         byte[] data = resource.getResource(((StringBuffer)(new Object("__"))).append(name).append('@').append(offset).toString());
         if (data == null) {
            if (offset == 0) {
               data = resource.getResource(name);
               if (data == null) {
                  throw new Object(((StringBuffer)(new Object("Missing resource "))).append(name).toString());
               }

               Arrays.add(array, data);
            }

            return array;
         }

         Arrays.add(array, data);
         offset += data.length;
      }
   }

   private final void addTTSListener(TTSListener listener, int priority) {
      this.updateTTSListenerQueue(listener, priority);
   }

   private final void removeTTSListener(TTSListener listener) {
      Arrays.remove(this._ttsListeners, listener);
   }

   private final int getFileHandle(String name) {
      if (name.startsWith("lvr_")) {
         return 1;
      } else if (name.startsWith("prm_")) {
         return 2;
      } else if (name.startsWith("str_")) {
         return 3;
      } else if (name.startsWith("elvis_")) {
         return 4;
      } else if (name.startsWith("tempelvis_")) {
         return 10;
      } else if (name.startsWith("enroll_idx")) {
         return 5;
      } else if (name.startsWith("vre_cn")) {
         return 6;
      } else if (name.startsWith("vst_param")) {
         return 7;
      } else if (name.startsWith("vr_version")) {
         return 8;
      } else if (name.startsWith("grammardump")) {
         return 9;
      } else {
         return name.startsWith("vad_rec") ? 11 : 0;
      }
   }

   private final void updateTTSListenerQueue(TTSListener item, int priority) {
      int pos = this.findTTSListenerPosition(priority);
      Arrays.insertAt(this._ttsListeners, item, pos);
   }

   private final int findTTSListenerPosition(int priority) {
      int position = 0;

      for (position = 0; position < this._ttsListeners.length; position++) {
         int current = this._ttsListeners[position].getPriority();
         if (priority > current) {
            return position;
         }
      }

      return position;
   }

   private final void setLanguage(int language) {
      if (Arrays.getIndex(this._availableLanguages, language) != -1) {
         if (this._persistentData._parameters._language != language) {
            VADEventLog.log(1279348295, language);
            this._persistentData._parameters._language = language;
            this._persistentData._incrementalNameEncodings = null;
            this._persistentObject.commit();
            if (this._engineState == 3) {
               this.loadResources(language);
            }

            this._languageChanged = true;
            this._persistentData._rebuildAddressBook = true;
         }
      }
   }

   private final void loadLanguages() {
      this._availableLanguages = new int[0];
      int numLanguages = LANGUAGE_EXTENSIONS.length;

      for (int i = 0; i < numLanguages; i++) {
         if (LANGUAGE_EXTENSIONS[i] != null
            && CodeModuleManager.getModuleHandle(((StringBuffer)(new Object())).append(VAD_RESOURCE_PREFIX).append(LANGUAGE_EXTENSIONS[i]).toString()) != 0) {
            Arrays.add(this._availableLanguages, i);
         }
      }

      if (this._availableLanguages.length == 0) {
         throw new Object("No VAD language resources present");
      }
   }

   private final void rebuildAddressBook() {
      if (this._engineState == 2 && this._hasEnoughMemory) {
         this._engineState = 5;
         this._app.uiRebuildingAddressBook();
         VADEventLog.log(1094992978);
         this._addressBookCards = new Object[5000];
         int index = 0;
         if (this._addressBook != null) {
            Enumeration e = this._addressBook.getAddressCards();

            while (e.hasMoreElements()) {
               Object o = e.nextElement();
               if (o instanceof Object) {
                  AddressCardModel card = (AddressCardModel)o;
                  if (card.getNumPhoneNumberModels() != 0) {
                     this._addressBookCards[index++] = card;
                     if (index == 5000) {
                        VADEventLog.log(1094863175);
                        break;
                     }
                  }
               }
            }
         }

         Array.resize(this._addressBookCards, index);
         this._persistentData._incrementalNameEncodings = null;
         this._persistentData._addressBookEmpty = this._addressBookCards.length == 0;
         this._persistentData._nameOrder = Locale.getSystemNameOrder();
         this._persistentObject.commit();
         this.sendEvent(22);
      }
   }

   private final void checkFileHandle(int handle) {
      if (handle < 1 || handle > 12) {
         throw new Object();
      }
   }

   private final void addIncrementalName(Object o) {
      if (!this._persistentData._rebuildAddressBook) {
         if (this._persistentData._incrementalNameEncodings == null) {
            this._persistentData._incrementalNameEncodings = new Object[0];
         }

         if (o instanceof Object) {
            AddressCardModel card = (AddressCardModel)o;
            if (card.getNumPhoneNumberModels() != 0) {
               if (this._persistentData._incrementalNameEncodings.length > 100) {
                  this._persistentData._rebuildAddressBook = true;
                  this._persistentData._incrementalNameEncodings = null;
               } else {
                  label44:
                  try {
                     byte[] name = card.toString().getBytes(this._encoding);
                     Object nameEncoding = PersistentContent.encode(name, false, getEncryptFlag());
                     Arrays.add(this._persistentData._incrementalNameEncodings, nameEncoding);
                  } finally {
                     break label44;
                  }

                  this._persistentData._rebuildAddressBookScheduled = true;
               }
            }
         }

         this._persistentObject.commit();
      }
   }

   private final String getTypeString(int phoneNumberType) {
      return this._phoneNumberTypeStrings[phoneNumberType];
   }

   private final boolean checkError(String command, int rc) {
      switch (rc) {
         case 4:
            return false;
         default:
            VADEventLog.log(((StringBuffer)(new Object("Error in "))).append(command).append(": ").append(rc).toString());
            return true;
      }
   }

   private final void updateParameters() {
      SmartDialingOptions smartDialing = SmartDialingOptions.getOptions();
      if (smartDialing.getCountryCode() == 1) {
         this._persistentData._parameters._location = 2;
      } else {
         this._persistentData._parameters._location = 15;
      }

      String corporatePhoneNumber = smartDialing.getCorporatePhoneNumber();
      if (corporatePhoneNumber != null && corporatePhoneNumber.length() != 0) {
         this._persistentData._parameters._extension = smartDialing.getCorporateExtensionLength();
      } else {
         this._persistentData._parameters._extension = 0;
      }

      int rc = VADNatives.setParameters(this._persistentData._parameters);
      this.checkError("setParameters", rc);
   }

   static final boolean getEncryptFlag() {
      return !Security.getInstance().isAddressBookExcludedFromContentProtection();
   }

   private final void initEngine() {
      this.updateParameters();
      VADEventLog.log(1229867348);
      int rc = VADNatives.initialize();
      if (!this.checkError("init", rc)) {
         this._queuedEvent = -1;
         this._engineState = 1;
      }
   }

   private final int startEngine() {
      VADEventLog.log(1398035028);
      if (ITPolicy.getBoolean(21, 8, false)) {
         return 2;
      }

      this._ticket = PersistentContent.getTicket();
      if (this._ticket == null && getEncryptFlag()) {
         return 6;
      }

      this.updateParameters();
      if (this._languageChanged) {
         this.loadLanguageFiles();
      }

      VADUserEvents.sendEvent(1, 0);
      MediaStreamingManager asm = MediaStreamingManager.getInstance();
      this._audioSession = asm.reserveSession(7, -1);
      if (this._audioSession == null) {
         return 5;
      }

      this._audioSession.setVolume(100);
      int rc = VADNatives.start(this._audioSession.getChannel());
      if (this.checkError("start", rc)) {
         this._audioSession.release();
         switch (rc) {
            case 1:
               return 3;
            default:
               return 4;
         }
      } else {
         AudioRouter.getInstance().addSource(1);
         this._engineState = 6;
         this._glueDefinedLabels = new Object[0];
         return 0;
      }
   }
}
