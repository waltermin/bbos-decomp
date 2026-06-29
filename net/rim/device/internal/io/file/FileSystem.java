package net.rim.device.internal.io.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.file.FileSystemListener;
import net.rim.device.api.io.FileInfo;
import net.rim.device.api.io.file.FileIOException;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.UnsupportedOperationException;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class FileSystem {
   private Hashtable _registeredRoots = new Hashtable();
   private Object[] _fileSystemListeners = new Object[0];
   private WeakReference[] _fileSystemApps = new WeakReference[0];
   private Object[] _fileJournalListeners = new Object[0];
   private WeakReference[] _fileJournalApps = new WeakReference[0];
   private boolean[] _fileJournalEventPending = new boolean[0];
   private long _nextUSN;
   private String[] _entryPath = new String[100];
   private String[] _entryOldPath = new String[100];
   private long[] _entryUSN;
   private int[] _entryEvent = new int[100];
   private FileSystemJournalEntry[] _entryCache;
   private int _currentTail;
   private boolean _commitOnDelete;
   private Object _journalLock;
   private InputStream[][][] _openedInputStreams;
   private InputStream[][][] _openedRIMInputStreams;
   private OutputStream[][][] _openedOutputStreams;
   private OutputStream[][][] _openedRIMOutputStreams;
   private int[] _maxWriteSize;
   private int[] _maxReadSize;
   public static final int FILESYSTEM_SDCARD = 1;
   public static final int FILESYSTEM_INTERNAL_FLASH = 3;
   public static final int FILE_STATUS_SUCCESS = 0;
   public static String SDCARD_ROOT_STR = "SDCard/";
   private static final int JOURNAL_SIZE = 100;
   private static final int MAX_THIRD_PARTY_HANDLES = 8;
   private static final long FILE_SYSTEM_REGISTRY = -1463491272206122121L;
   private static FileSystem _fileSystem;

   private FileSystem() {
      this._entryUSN = new long[100];
      this._entryCache = new FileSystemJournalEntry[100];
      this._currentTail = -1;
      this._openedInputStreams = new InputStream[4][][];
      this._openedInputStreams[1] = new InputStream[0];
      this._openedRIMInputStreams = new InputStream[4][][];
      this._openedRIMInputStreams[1] = new InputStream[0];
      this._openedOutputStreams = new OutputStream[4][][];
      this._openedOutputStreams[1] = new OutputStream[0];
      this._openedRIMOutputStreams = new OutputStream[4][][];
      this._openedRIMOutputStreams[1] = new OutputStream[0];
      this._maxWriteSize = new int[4];
      this._maxReadSize = new int[4];
      this._commitOnDelete = true;
      this._journalLock = new Object();
   }

   private static final void assertPermissions() {
      ApplicationControl.assertFileApiAllowed(true);
      ApplicationControl.assertIPCAllowed(true);
   }

   public static final Enumeration getRoots() {
      return _fileSystem._registeredRoots.keys();
   }

   public static final int getMaxReadSize(int nativeRootId) {
      synchronized (_fileSystem) {
         if (_fileSystem._maxReadSize[nativeRootId] == 0) {
            FileSystemInfo fsInfo = new FileSystemInfo();
            int status = getFileSystemInfo(nativeRootId, fsInfo);
            if (status != 0) {
               throw new FileIOException(status);
            }

            _fileSystem._maxReadSize[nativeRootId] = fsInfo.getMaxReadLength();
         }

         return _fileSystem._maxReadSize[nativeRootId];
      }
   }

   public static final int getMaxWriteSize(int nativeRootId) {
      synchronized (_fileSystem) {
         if (_fileSystem._maxWriteSize[nativeRootId] == 0) {
            FileSystemInfo fsInfo = new FileSystemInfo();
            int status = getFileSystemInfo(nativeRootId, fsInfo);
            if (status != 0) {
               throw new FileIOException(status);
            }

            _fileSystem._maxWriteSize[nativeRootId] = fsInfo.getMaxWriteLength();
         }

         return _fileSystem._maxWriteSize[nativeRootId];
      }
   }

   public static final int mount(int nativeRootId, String rootName, Class clazz) {
      synchronized (_fileSystem) {
         if (nativeRootId != -1) {
            int result = mount0(nativeRootId);
            if (result != 0) {
               return result;
            }
         }

         if (_fileSystem._registeredRoots.put(rootName, clazz) == null) {
            _fileSystem.notifyRootChanged(0, rootName);
            return 0;
         } else {
            return 19;
         }
      }
   }

   public static final void unmount(int nativeRootId, String rootName) {
      synchronized (_fileSystem) {
         if (nativeRootId != -1) {
            closeAllStreams(nativeRootId);
            if (unmount0(nativeRootId) != 0) {
               return;
            }
         }

         if (_fileSystem._registeredRoots.remove(rootName) != null) {
            _fileSystem.notifyRootChanged(1, rootName);
         }
      }
   }

   public static final void flushAllStreams(int rootId) {
      throw new RuntimeException("cod2jar: invokevirtual: receiver not in world");
   }

   public static final void closeAllStreams(int rootId) {
      throw new RuntimeException("cod2jar: invokevirtual: receiver not in world");
   }

   public static final String getRootName(int root) {
      return root == 1 ? SDCARD_ROOT_STR : null;
   }

   public static final void rootChanged(String root) {
      synchronized (_fileSystem._journalLock) {
         _fileSystem.notifyRootChanged(1, root);
         _fileSystem.notifyRootChanged(0, root);
      }
   }

   public static final Class getRoot(String root) {
      return (Class)_fileSystem._registeredRoots.get(root);
   }

   public static final boolean addFileSystemListener(FileSystemListener listener, Application app, boolean weakRef) {
      assertPermissions();
      if (listener != null && app != null) {
         synchronized (_fileSystem._journalLock) {
            for (int i = 0; i < _fileSystem._fileSystemListeners.length; i++) {
               Object item = _fileSystem._fileSystemListeners[i];
               if (item == listener || item instanceof WeakReference && ((WeakReference)item).get() == listener) {
                  return false;
               }
            }

            Object addObj = listener;
            if (weakRef) {
               addObj = new WeakReference(listener);
            }

            Arrays.add(_fileSystem._fileSystemListeners, addObj);
            Arrays.add(_fileSystem._fileSystemApps, new WeakReference(app));
            return true;
         }
      } else {
         throw new NullPointerException();
      }
   }

   public static final void checkForDeadListeners() {
      synchronized (_fileSystem._journalLock) {
         for (int i = 0; i < _fileSystem._fileSystemListeners.length; i++) {
            Application app = (Application)_fileSystem._fileSystemApps[i].get();
            Object obj = _fileSystem._fileSystemListeners[i];
            if (obj instanceof WeakReference) {
               obj = ((WeakReference)obj).get();
            }

            if (obj == null || app == null || !app.isAlive()) {
               Arrays.removeAt(_fileSystem._fileSystemListeners, i);
               Arrays.removeAt(_fileSystem._fileSystemApps, i);
               i--;
            }
         }

         for (int i = 0; i < _fileSystem._fileJournalListeners.length; i++) {
            Application app = (Application)_fileSystem._fileJournalApps[i].get();
            Object obj = _fileSystem._fileJournalListeners[i];
            if (obj instanceof WeakReference) {
               obj = ((WeakReference)obj).get();
            }

            if (obj == null || app == null || !app.isAlive()) {
               Arrays.removeAt(_fileSystem._fileJournalListeners, i);
               Arrays.removeAt(_fileSystem._fileJournalApps, i);
               i--;
            }
         }
      }
   }

   public static final boolean removeFileSystemListener(FileSystemListener listener) {
      if (listener == null) {
         throw new NullPointerException();
      }

      synchronized (_fileSystem._journalLock) {
         int index = -1;

         for (int i = 0; i < _fileSystem._fileSystemListeners.length; i++) {
            Object item = _fileSystem._fileSystemListeners[i];
            if (item == listener || item instanceof WeakReference && ((WeakReference)item).get() == listener) {
               index = i;
               break;
            }
         }

         if (index == -1) {
            return false;
         }

         Arrays.removeAt(_fileSystem._fileSystemListeners, index);
         Arrays.removeAt(_fileSystem._fileSystemApps, index);
         return true;
      }
   }

   public static final boolean addJournalListener(FileSystemJournalListener listener, Application app, boolean weakRef) {
      assertPermissions();
      if (listener != null && app != null) {
         synchronized (_fileSystem._journalLock) {
            for (int i = 0; i < _fileSystem._fileJournalListeners.length; i++) {
               Object item = _fileSystem._fileJournalListeners[i];
               if (item == listener || item instanceof WeakReference && ((WeakReference)item).get() == listener) {
                  return false;
               }
            }

            Object addObj = listener;
            if (weakRef) {
               addObj = new WeakReference(listener);
            }

            Arrays.add(_fileSystem._fileJournalListeners, addObj);
            Arrays.add(_fileSystem._fileJournalApps, new WeakReference(app));
            Array.resize(_fileSystem._fileJournalEventPending, _fileSystem._fileJournalEventPending.length + 1);
            return true;
         }
      } else {
         throw new NullPointerException();
      }
   }

   public static final boolean removeJournalListener(FileSystemJournalListener listener) {
      if (listener == null) {
         throw new NullPointerException();
      }

      synchronized (_fileSystem._journalLock) {
         int index = -1;

         for (int i = 0; i < _fileSystem._fileJournalListeners.length; i++) {
            Object item = _fileSystem._fileJournalListeners[i];
            if (item == listener || item instanceof WeakReference && ((WeakReference)item).get() == listener) {
               index = i;
               break;
            }
         }

         if (index == -1) {
            return false;
         }

         Arrays.removeAt(_fileSystem._fileJournalListeners, index);
         Arrays.removeAt(_fileSystem._fileJournalApps, index);
         int newLength = _fileSystem._fileJournalEventPending.length - 1;
         System.arraycopy(_fileSystem._fileJournalEventPending, index + 1, _fileSystem._fileJournalEventPending, index, newLength - index);
         Array.resize(_fileSystem._fileJournalEventPending, newLength);
         return true;
      }
   }

   public static final FileSystemJournalEntry getJournalEntry(long usn) {
      return _fileSystem.getJournalEntryInternal(usn);
   }

   private final FileSystemJournalEntry getJournalEntryInternal(long usn) {
      synchronized (this._journalLock) {
         if (this._currentTail == -1) {
            return null;
         }

         if (usn > this._entryUSN[this._currentTail]) {
            return null;
         }

         int nextIndex = this._currentTail + 1;
         if (nextIndex >= 100) {
            nextIndex = 0;
         }

         if (usn < this._entryUSN[nextIndex]) {
            return null;
         }

         int index = (int)(usn % 100);
         if (this._entryCache[index] == null) {
            this._entryCache[index] = FileSystemJournalEntry.createEntry(usn, this._entryPath[index], this._entryOldPath[index], this._entryEvent[index]);
         }

         return this._entryCache[index];
      }
   }

   public static final long getNextJournalUSN() {
      return _fileSystem._nextUSN;
   }

   public static final void addFileJournalEntry(String path, int action) {
      _fileSystem.addFileJournalEntryInternal(path, path, action);
   }

   public static final void addFileJournalEntry(String path, String oldPath, int action) {
      _fileSystem.addFileJournalEntryInternal(path, oldPath, action);
   }

   public static final void suspendCommitOnDelete() {
      _fileSystem._commitOnDelete = false;
   }

   public static final void resumeCommitOnDelete() {
      _fileSystem._commitOnDelete = true;
   }

   private final void addFileJournalEntryInternal(String path, String oldPath, int action) {
      synchronized (this._journalLock) {
         this._currentTail++;
         if (this._currentTail >= 100) {
            this._currentTail = 0;
         }

         this._entryPath[this._currentTail] = path;
         this._entryOldPath[this._currentTail] = oldPath;
         this._entryUSN[this._currentTail] = this._nextUSN;
         this._entryEvent[this._currentTail] = action;
         this._entryCache[this._currentTail] = null;
         this._nextUSN += 1;
         this.notifyJournalChanged();
      }
   }

   private final void notifyRootChanged(int state, String rootName) {
      synchronized (this._journalLock) {
         for (int i = 0; i < this._fileSystemListeners.length; i++) {
            Application app = (Application)this._fileSystemApps[i].get();
            FileSystemListener listener = null;
            Object obj = this._fileSystemListeners[i];
            if (!(obj instanceof FileSystemListener)) {
               if (obj instanceof WeakReference) {
                  listener = (FileSystemListener)((WeakReference)obj).get();
               }
            } else {
               listener = (FileSystemListener)obj;
            }

            if (listener != null && app != null && app.isAlive()) {
               app.invokeLater(new RootChanged(listener, state, rootName));
            } else {
               Arrays.removeAt(_fileSystem._fileSystemListeners, i);
               Arrays.removeAt(_fileSystem._fileSystemApps, i);
               i--;
            }
         }
      }
   }

   private final void notifyJournalChanged() {
      synchronized (this._journalLock) {
         for (int i = 0; i < this._fileJournalListeners.length; i++) {
            Application app = (Application)this._fileJournalApps[i].get();
            FileSystemJournalListener listener = null;
            Object obj = this._fileJournalListeners[i];
            if (!(obj instanceof FileSystemJournalListener)) {
               if (obj instanceof WeakReference) {
                  listener = (FileSystemJournalListener)((WeakReference)obj).get();
               }
            } else {
               listener = (FileSystemJournalListener)obj;
            }

            if (listener == null || app == null || !app.isAlive()) {
               Arrays.removeAt(_fileSystem._fileJournalListeners, i);
               Arrays.removeAt(_fileSystem._fileJournalApps, i);
               int newLength = _fileSystem._fileJournalEventPending.length - 1;
               System.arraycopy(_fileSystem._fileJournalEventPending, i + 1, _fileSystem._fileJournalEventPending, i, newLength - i);
               Array.resize(_fileSystem._fileJournalEventPending, newLength);
               i--;
            } else if (!this._fileJournalEventPending[i]) {
               this._fileJournalEventPending[i] = true;
               app.invokeLater(new FileSystem$JournalChanged(this, listener));
            }
         }
      }
   }

   public static final boolean isSupported() {
      return InternalServices.isSoftwareCapable(0);
   }

   public static final boolean isFileSystemSupported(int fs) {
      if (isSupported()) {
         try {
            return isFileSystemSupported0(fs);
         } catch (UnsupportedOperationException var2) {
         }
      }

      return false;
   }

   public static final void registerInputStream(int rootId, InputStream in) {
      boolean rimApp = ControlledAccess.verifyRRISignatures(true);
      if (rimApp) {
         Arrays.add(_fileSystem._openedRIMInputStreams[rootId], in);
      } else {
         Arrays.add(_fileSystem._openedInputStreams[rootId], in);
      }
   }

   public static final void registerOutputStream(int rootId, OutputStream out) {
      boolean rimApp = ControlledAccess.verifyRRISignatures(true);
      if (rimApp) {
         Arrays.add(_fileSystem._openedRIMOutputStreams[rootId], out);
      } else {
         Arrays.add(_fileSystem._openedOutputStreams[rootId], out);
      }
   }

   public static final void deregisterOutputStream(int rootId, OutputStream out) {
      Arrays.remove(_fileSystem._openedOutputStreams[rootId], out);
      Arrays.remove(_fileSystem._openedRIMOutputStreams[rootId], out);
   }

   public static final void deregisterInputStream(int rootId, InputStream in) {
      Arrays.remove(_fileSystem._openedInputStreams[rootId], in);
      Arrays.remove(_fileSystem._openedRIMInputStreams[rootId], in);
   }

   public static final long open(int fs, String name, int mode) {
      boolean rimApp = ControlledAccess.verifyRRISignatures(true);
      return !rimApp && _fileSystem._openedInputStreams[fs].length + _fileSystem._openedOutputStreams[fs].length >= 8 ? 11 : open0(fs, name, mode);
   }

   public static final int delete(int fs, String name) {
      int result = delete0(fs, name);
      if (result == 0 && _fileSystem._commitOnDelete) {
         commit(fs, 0);
      }

      return result;
   }

   public static final int rename(int fs, String oldName, String newName) {
      int result = rename0(fs, oldName, newName);
      if (result == 0) {
         commit(fs, 0);
      }

      return result;
   }

   public static final int mkdir(int fs, String name) {
      int result = mkdir0(fs, name);
      if (result == 0) {
         commit(fs, 0);
      }

      return result;
   }

   public static final int rmdir(int fs, String name) {
      int result = rmdir0(fs, name);
      if (result == 0) {
         commit(fs, 0);
      }

      return result;
   }

   public static final native boolean isFileSystemSupported0(int var0);

   public static final native int close(int var0);

   private static final native long open0(int var0, String var1, int var2);

   public static final native long read(int var0, byte[] var1);

   public static final native int write(int var0, byte[] var1, int var2);

   private static final native int delete0(int var0, String var1);

   private static final native int rename0(int var0, String var1, String var2);

   public static final native int getFileSystemInfo(int var0, FileSystemInfo var1);

   public static final native long findFirst(int var0, String var1, FileInfo var2);

   public static final native int findNext(int var0, int var1, FileInfo var2);

   public static final native int findClose(int var0);

   public static final native int format(int var0);

   private static final native int mount0(int var0);

   private static final native int unmount0(int var0);

   private static final native int mkdir0(int var0, String var1);

   private static final native int rmdir0(int var0, String var1);

   public static final native long tell(int var0);

   public static final native int seek(int var0, long var1, int var3);

   public static final native int getFileInfo(int var0, FileInfo var1);

   public static final native int getFileInfo(int var0, String var1, FileInfo var2);

   public static final native int setFileAttrib(int var0, String var1, int var2);

   public static final native int commit(int var0, int var1);

   public static final native int truncate(int var0, long var1);

   public static final native long directorySize(int var0, String var1);

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      Object obj = registry.getOrWaitFor(-1463491272206122121L);
      if (!(obj instanceof FileSystem)) {
         _fileSystem = new FileSystem();
         registry.put(-1463491272206122121L, _fileSystem);
      } else {
         _fileSystem = (FileSystem)obj;
      }
   }
}
