package net.rim.device.apps.api.framework.file;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public class ExplorerRegistry {
   private Hashtable _aliasMap = (Hashtable)(new Object());
   private AliasFileEntry[][] _globalAlias = new AliasFileEntry[7][];
   protected static final long REGKEY = -999583750915911489L;
   private static ExplorerRegistry _registry;
   private static ExplorerRegistry$AliasEntryComparator _comparator = new ExplorerRegistry$AliasEntryComparator(null);
   private static final int MAX_MEDIA_TYPES = 7;

   private ExplorerRegistry() {
      for (int i = 0; i < 7; i++) {
         this._globalAlias[i] = new AliasFileEntry[0];
      }
   }

   public static ExplorerRegistry getInstance() {
      if (_registry == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _registry = (ExplorerRegistry)ar.getOrWaitFor(-999583750915911489L);
         if (_registry == null) {
            _registry = new ExplorerRegistry();
            ar.put(-999583750915911489L, _registry);
         }
      }

      return _registry;
   }

   public void addGlobalAlias(AliasFileEntry entry, int mediaType) {
      if (mediaType >= 0 && mediaType < this._globalAlias.length && entry != null) {
         synchronized (this._globalAlias) {
            AliasFileEntry[] mediaTypeAlias = this._globalAlias[mediaType];
            int index = Arrays.binarySearch(mediaTypeAlias, entry, _comparator, 0, mediaTypeAlias.length);
            if (index < 0) {
               Arrays.insertAt(mediaTypeAlias, entry, -(++index));
            }
         }
      }
   }

   public void removeGlobalAlias(AliasFileEntry entry, int mediaType) {
      if (mediaType >= 0 && mediaType < this._globalAlias.length) {
         synchronized (this._globalAlias) {
            AliasFileEntry[] mediaTypeAlias = this._globalAlias[mediaType];
            int index = Arrays.binarySearch(mediaTypeAlias, entry, _comparator, 0, mediaTypeAlias.length);
            if (index >= 0) {
               Arrays.removeAt(mediaTypeAlias, index);
            }
         }
      }
   }

   public AliasFileEntry[] getGlobalAlias(int mediaType) {
      if (mediaType >= 0 && mediaType < this._globalAlias.length) {
         synchronized (this._globalAlias) {
            return this._globalAlias[mediaType];
         }
      } else {
         return null;
      }
   }

   public void addAlias(String path, AliasFileEntry newEntry) {
      synchronized (this._aliasMap) {
         Object obj = this._aliasMap.get(path);
         if (obj == null) {
            this._aliasMap.put(path, newEntry);
         } else if (!(obj instanceof AliasFileEntry)) {
            if (obj instanceof AliasFileEntry[]) {
               AliasFileEntry[] entries = (AliasFileEntry[])obj;
               int i = entries.length;

               while (--i >= 0) {
                  AliasFileEntry aliasEntry = entries[i];
                  if (newEntry.getName().equals(aliasEntry.getName())) {
                     entries[i] = newEntry;
                     return;
                  }
               }

               Arrays.add(entries, newEntry);
            }
         } else {
            AliasFileEntry aliasEntry = (AliasFileEntry)obj;
            if (newEntry.getName().equals(aliasEntry.getName())) {
               this._aliasMap.put(path, newEntry);
            } else {
               AliasFileEntry[] entries = new AliasFileEntry[]{aliasEntry, newEntry};
               this._aliasMap.put(path, entries);
            }
         }
      }
   }

   public void removeAlias(String path, AliasFileEntry entry) {
      synchronized (this._aliasMap) {
         Object obj = this._aliasMap.get(path);
         if (obj instanceof AliasFileEntry) {
            this._aliasMap.remove(path);
         } else if (obj instanceof AliasFileEntry[]) {
            Arrays.remove((AliasFileEntry[])obj, entry);
         }
      }
   }

   public AliasFileEntry[] getAlias(String path) {
      AliasFileEntry[] entries = null;
      Object obj = this._aliasMap.get(path);
      if (obj == null) {
         return entries;
      } else if (obj instanceof AliasFileEntry) {
         return new AliasFileEntry[]{(AliasFileEntry)obj};
      } else {
         return obj instanceof AliasFileEntry[] ? (AliasFileEntry[])obj : entries;
      }
   }

   public Enumeration aliasPaths() {
      return this._aliasMap.keys();
   }
}
