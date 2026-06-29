package net.rim.device.api.browser.push;

import net.rim.vm.Persistable;

final class PushOptions$PersistedPushOptions implements Persistable {
   boolean _enablePush;
   boolean _mdsEnablePush;
   boolean _wapEnablePush;
   boolean _allowOtherApplications;
   int[][] _filterModes = new int[3][3];
   int[][] _acceptModes = new int[3][3];
   String[][] _filterValues = new Object[3][3];
   int _dirtyMask;
}
