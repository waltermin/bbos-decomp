package net.rim.device.apps.internal.vad;

import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.vad.VADParameters;
import net.rim.vm.Persistable;

final class VADPersistentData implements Persistable {
   VADParameters _parameters = new VADParameters();
   IntHashtable _fileData = new IntHashtable();
   Object[] _incrementalNameEncodings;
   boolean _rebuildAddressBook;
   boolean _rebuildAddressBookScheduled;
   boolean _addressBookEmpty;
   int _nameOrder;

   final void setFileData(int handle, Object[] data) {
      this._fileData.put(handle, data);
   }

   final Object[] getFileData(int handle) {
      Object obj = this._fileData.get(handle);
      if (obj instanceof byte[]) {
         obj = new Object[]{obj};
      }

      return (Object[])obj;
   }
}
