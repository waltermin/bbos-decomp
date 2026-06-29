package net.rim.device.apps.internal.browser.core;

import java.util.Hashtable;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.bookmark.BookmarksFolderList;
import net.rim.device.apps.internal.browser.history.History;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.page.PageCache;
import net.rim.device.internal.browser.util.IdEncryptor;
import net.rim.vm.Array;
import net.rim.vm.Memory;

public final class BrowserSession {
   private BrowserConfigRecord _config;
   private History _history;
   private long _defaultBookmarksFolderID;
   private PageCache _pageCache;
   private int _sessionId;
   private String _hmac;
   private byte[] _hmacKey;
   private BSMManager _bsmManager;
   public static final long BROWSER_SESSION_REQUEST_COMPLETE;
   private static BrowserSession$ActiveConfigs _activeConfigs;

   private final String createHmacKey() {
      StringBuffer buff = (StringBuffer)(new Object(43));
      buff.append("20,");
      this._hmacKey = Memory.copyToRAMOnlyBytes(RandomSource.getBytes(20));

      for (int i = 0; i < 20; i++) {
         buff.append(NumberUtilities.intToHexDigit(this._hmacKey[i] >> 4));
         buff.append(NumberUtilities.intToHexDigit(this._hmacKey[i]));
      }

      return IdEncryptor.encrypt(buff.toString(), 1);
   }

   BrowserSession(String uid, History history) {
      this._config = BrowserConfigRecord.getDecodedConfig(uid, -1, null);
      this._hmac = this.createHmacKey();
      this._history = history;
      if (this._config != null && StringUtilities.strEqualIgnoreCase(this._config.getPropertyAsString(3), BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)) {
         this._bsmManager = new BSMManager(this);
      }

      if (!PersistentContent.isEncryptionEnabled() && this._config != null && this._config.getPropertyAsIntWithOverride((byte)2) == 2) {
         String key = StringUtilities.toLowerCase(uid, 1701707776);
         if (_activeConfigs._lastHistoryTable == null) {
            _activeConfigs._lastHistoryTable = (Hashtable)(new Object());
         }

         if (this._history == null) {
            this._history = (History)_activeConfigs._lastHistoryTable.get(key);
            if (this._history == null) {
               this._history = new History();
               _activeConfigs._lastHistoryTable.put(key, this._history);
            }
         } else {
            _activeConfigs._lastHistoryTable.put(key, this._history);
         }
      } else if (this._history == null) {
         this._history = new History();
      }

      this._sessionId = RandomSource.getInt();
      int flashSize = Memory.getFlashTotal();
      int pageSize = GeneralProperty.getCurrentPropertyAsInt(34);
      if (flashSize > 16777216 && pageSize > 0) {
         this._pageCache = new PageCache(pageSize);
      }
   }

   public static final BrowserSession getCurrentSession() {
      synchronized (_activeConfigs._syncLock) {
         return _activeConfigs._activeSessionId == -1 ? null : _activeConfigs._activeSessions[_activeConfigs._activeSessionId];
      }
   }

   public static final BrowserSession getOrCreateSession(String uid) {
      return getOrCreateSessionWithHistory(uid, null);
   }

   public static final BrowserSession getOrCreateSessionWithHistory(String uid, History history) {
      if (uid == null) {
         return null;
      }

      BrowserSession session;
      synchronized (_activeConfigs._syncLock) {
         for (int i = _activeConfigs._activeSessions.length - 1; i >= 0; i--) {
            if (_activeConfigs._activeSessions[i]._config != null
               && StringUtilities.strEqualIgnoreCase(_activeConfigs._activeSessions[i]._config.getUid(), uid, 1701707776)) {
               _activeConfigs._activeSessionId = i;
               return _activeConfigs._activeSessions[i];
            }
         }

         session = new BrowserSession(uid, history);
         if (session._config == null) {
            return null;
         }

         Arrays.add(_activeConfigs._activeSessions, session);
         _activeConfigs._activeSessionId = _activeConfigs._activeSessions.length - 1;
      }

      if (session != null) {
         BrowserDaemonRegistry.notifyBrowserConfigChangeListeners(true);
      }

      return session;
   }

   public static final BrowserSession getActiveSession(String uid) {
      synchronized (_activeConfigs._syncLock) {
         for (int i = _activeConfigs._activeSessions.length - 1; i >= 0; i--) {
            if (_activeConfigs._activeSessions[i]._config != null
               && StringUtilities.strEqualIgnoreCase(_activeConfigs._activeSessions[i]._config.getUid(), uid, 1701707776)) {
               return _activeConfigs._activeSessions[i];
            }
         }

         return null;
      }
   }

   public final byte[] getHmacKey() {
      return this._hmacKey;
   }

   public final String getHmac() {
      return this._hmac;
   }

   public final int getSessionId() {
      return this._sessionId;
   }

   public final boolean isBSMConnected() {
      return this._bsmManager == null ? false : this._bsmManager.isConnected();
   }

   public final String getBSMHost() {
      return this._bsmManager == null ? null : this._bsmManager.getBSMHost();
   }

   public final void doBSMConnect() {
      if (this._bsmManager != null) {
         this._bsmManager.connect();
      }
   }

   public final void notifyNoActiveBSMSession() {
      if (this._bsmManager != null) {
         this._bsmManager.endSession();
      }
   }

   public final void requestCompleted() {
      if (this._bsmManager != null) {
         this._bsmManager.sendAcks();
      }

      RIMGlobalMessagePoster.postGlobalEvent(4624362102524391658L);
   }

   public final void setDefaultBookmarksFolderID(long defaultBookmarksFolderID) {
      this._defaultBookmarksFolderID = defaultBookmarksFolderID;
   }

   public final void destroy() {
      int newId = -1;
      synchronized (_activeConfigs._syncLock) {
         for (int i = 0; i < _activeConfigs._activeSessions.length; i++) {
            if (_activeConfigs._activeSessions[i] == this) {
               Arrays.removeAt(_activeConfigs._activeSessions, i);
               if (_activeConfigs._activeSessionId == i) {
                  _activeConfigs._activeSessionId = _activeConfigs._activeSessions.length - 1;
               }

               newId = _activeConfigs._activeSessionId;
               break;
            }
         }
      }

      BrowserDaemonRegistry.notifyBrowserConfigChangeListeners(newId >= 0);
      this.cleanup();
   }

   public final void activate() {
      if (this._bsmManager != null) {
         this._bsmManager.startup();
      }
   }

   public final PageCache getPageCache() {
      return this._pageCache;
   }

   private final void cleanup() {
      if (this._pageCache != null) {
         this._pageCache.destroy(false);
      }
   }

   public static final void clearAllSessions() {
      synchronized (_activeConfigs._syncLock) {
         _activeConfigs._activeSessionId = -1;

         for (int i = _activeConfigs._activeSessions.length - 1; i >= 0; i--) {
            _activeConfigs._activeSessions[i].cleanup();
         }

         Array.resize(_activeConfigs._activeSessions, 0);
      }
   }

   public final BrowserConfigRecord getConfig() {
      return this._config;
   }

   public final History getHistory() {
      return this._history;
   }

   public final void refresh() {
      String key = StringUtilities.toLowerCase(this._config.getUid(), 1701707776);
      this._config = BrowserConfigRecord.getDecodedConfig(key, -1, null);
      if (_activeConfigs._lastHistoryTable != null) {
         _activeConfigs._lastHistoryTable.remove(key);
      }

      if (!PersistentContent.isEncryptionEnabled() && this._config != null && this._config.getPropertyAsIntWithOverride((byte)2) == 2) {
         if (_activeConfigs._lastHistoryTable == null) {
            _activeConfigs._lastHistoryTable = (Hashtable)(new Object(1));
         }

         _activeConfigs._lastHistoryTable.put(key, this._history);
      }
   }

   public static final void refresh(String uid) {
      if (uid != null) {
         BrowserSession session = getActiveSession(uid);
         if (session != null) {
            session.refresh();
         }
      } else {
         synchronized (_activeConfigs._syncLock) {
            for (int i = _activeConfigs._activeSessions.length - 1; i >= 0; i--) {
               _activeConfigs._activeSessions[i].refresh();
            }
         }
      }
   }

   public final long getDefaultBookmarksFolderID() {
      if (this._defaultBookmarksFolderID == 0) {
         this._defaultBookmarksFolderID = BookmarksFolderList.getDefaultFolderIDForConfig(this._config);
      }

      return this._defaultBookmarksFolderID;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _activeConfigs = (BrowserSession$ActiveConfigs)ar.getOrWaitFor(6353203939814877006L);
      if (_activeConfigs == null) {
         _activeConfigs = new BrowserSession$ActiveConfigs();
         ar.put(6353203939814877006L, _activeConfigs);
      }
   }
}
