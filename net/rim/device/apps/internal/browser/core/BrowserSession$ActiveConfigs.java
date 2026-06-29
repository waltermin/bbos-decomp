package net.rim.device.apps.internal.browser.core;

import java.util.Hashtable;

final class BrowserSession$ActiveConfigs {
   BrowserSession[] _activeSessions = new BrowserSession[0];
   int _activeSessionId = -1;
   final Object _syncLock = new Object();
   Hashtable _lastHistoryTable;
   static final long KEY = 6353203939814877006L;
}
