package net.rim.device.api.servicebook.selector;

import java.util.Vector;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.StringUtilities;

public final class SRSelector implements GlobalEventListener {
   private PersistentObject _persistentAppData = RIMPersistentStore.getPersistentObject(2857166788728229964L);
   private Vector _appData;
   private Vector _callbacks;
   private ServiceBook _sb;
   public static final long SR_SELECTOR_GUID = 2857166788728229964L;

   public final synchronized int register(String name, long guid, String cid, SRSelectorCallback cb) {
      SRSelectorData data = this.findData(guid, cid);
      if (data != null) {
         if (data.cbIndex != -1 && this._callbacks.elementAt(data.cbIndex) == cb) {
            if (!name.equals(data.name)) {
               data.name = name;
               this.commit();
            }

            return data.defaultId;
         }

         data.name = name;
      } else {
         data = new SRSelectorData(name, guid, cid);
         this._appData.addElement(data);
      }

      if (data.cbIndex == -1) {
         data.cbIndex = this._callbacks.size();
         this._callbacks.addElement(cb);
      } else {
         this._callbacks.setElementAt(cb, data.cbIndex);
      }

      this.doCallback(data, false);
      this.commit();
      return data.defaultId;
   }

   public final int register(SRSelectorApp app, SRSelectorCallback cb) {
      return this.register(app.getName(), app.getGuid(), app.getCid(), cb);
   }

   public final synchronized boolean setDefault(long guid, String cid, int defaultId, boolean userSet) {
      SRSelectorData data = this.findData(guid, cid);
      if (data != null && data.cbIndex != -1) {
         ServiceRecord sr = this._sb.getRecordById(defaultId);
         if (sr != null && StringUtilities.strEqualIgnoreCase(cid, sr.getCid(), 1701707776)) {
            boolean doUpdate = data.defaultId != defaultId;
            data.userSet = userSet;
            data.defaultId = defaultId;
            this.commit();
            if (doUpdate) {
               this.sendUpdate(data);
            }

            return true;
         } else {
            return false;
         }
      } else {
         throw new Object();
      }
   }

   public final boolean setDefault(SRSelectorApp app, int defaultId, boolean userSet) {
      return this.setDefault(app.getGuid(), app.getCid(), defaultId, userSet);
   }

   public final synchronized int getDefault(long guid, String cid) {
      SRSelectorData data = this.findData(guid, cid);
      if (data != null && data.cbIndex != -1) {
         return data.defaultId;
      } else {
         throw new Object();
      }
   }

   public final synchronized boolean didUserSetDefault(long guid, String cid) {
      SRSelectorData data = this.findData(guid, cid);
      if (data != null && data.cbIndex != -1) {
         return data.userSet;
      } else {
         throw new Object();
      }
   }

   public final synchronized Vector getRegisteredApps() {
      Vector apps = (Vector)(new Object());

      for (int i = this._appData.size() - 1; i >= 0; i--) {
         SRSelectorData data = (SRSelectorData)this._appData.elementAt(i);
         if (data.cbIndex != -1) {
            apps.addElement(new SRSelectorApp(data.name, data.guid, data.cid));
         }
      }

      return apps;
   }

   public final synchronized void removeAllApps() {
      this._appData.removeAllElements();
      this._callbacks.removeAllElements();
      this.commit();
   }

   public final synchronized boolean removeApp(long guid, String cid) {
      for (int i = this._appData.size() - 1; i >= 0; i--) {
         SRSelectorData data = (SRSelectorData)this._appData.elementAt(i);
         if (data.guid == guid && StringUtilities.strEqualIgnoreCase(cid, data.cid, 1701707776)) {
            this._appData.removeElementAt(i);
            if (data.cbIndex != -1) {
               this._callbacks.removeElementAt(i);

               for (int j = this._appData.size() - 1; j >= 0; j--) {
                  data = (SRSelectorData)this._appData.elementAt(j);
                  if (data.cbIndex != -1 && data.cbIndex > i) {
                     data.cbIndex--;
                  }
               }
            }

            this.commit();
            return true;
         }
      }

      return false;
   }

   final synchronized void syncFinished(Vector objs) {
      boolean doCommit = false;
      if (objs != null) {
         boolean[] appTouched = new boolean[this._appData.size()];

         for (int i = objs.size() - 1; i >= 0; i--) {
            SRAppSyncObject obj = (SRAppSyncObject)objs.elementAt(i);
            SRSelectorData syncData = obj.getData();
            ServiceRecord sr = this._sb.getRecordByUidAndCid(obj.getUid(), syncData.cid);
            boolean hasApp = false;

            for (int j = this._appData.size() - 1; j >= 0; j--) {
               SRSelectorData data = (SRSelectorData)this._appData.elementAt(j);
               if (data.guid == syncData.guid && StringUtilities.strEqualIgnoreCase(data.cid, syncData.cid, 1701707776)) {
                  appTouched[j] = true;
                  hasApp = true;
                  if (sr != null && !data.userSet) {
                     data.defaultId = sr.getId();
                     data.userSet = syncData.userSet;
                     data.stashedUid = null;
                     data.stashedUserSet = false;
                     this.sendUpdate(data);
                  } else {
                     data.stashedUid = obj.getUid();
                     data.stashedUserSet = syncData.userSet;
                  }

                  doCommit |= true;
                  break;
               }
            }

            if (!hasApp) {
               syncData.defaultId = sr != null ? sr.getId() : -1;
               this._appData.addElement(syncData);
               this.sendUpdate(syncData);
               doCommit |= true;
            }
         }

         for (int i = appTouched.length - 1; i >= 0; i--) {
            if (!appTouched[i]) {
               SRSelectorData data = (SRSelectorData)this._appData.elementAt(i);
               if (data.cbIndex == -1) {
                  this._appData.removeElementAt(i);
                  doCommit |= true;
               }
            }
         }

         if (doCommit) {
            this.commit();
         }
      }
   }

   final Vector getAppData() {
      return this._appData;
   }

   @Override
   public final synchronized void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      boolean doCommit = false;
      boolean sbEvent = false;
      if (guid == 8288627527798139133L) {
         for (int i = this._appData.size() - 1; i >= 0; i--) {
            doCommit |= this.processSBChange(data0, (SRSelectorData)this._appData.elementAt(i));
         }

         sbEvent = true;
      } else if (guid == -4220058463650496006L) {
         for (int i = this._appData.size() - 1; i >= 0; i--) {
            doCommit |= this.processSBAdd(data0, (SRSelectorData)this._appData.elementAt(i));
         }

         sbEvent = true;
      } else if (guid == 2522898683889177438L) {
         for (int i = this._appData.size() - 1; i >= 0; i--) {
            doCommit |= this.processSBRemove(data0, (SRSelectorData)this._appData.elementAt(i));
         }

         sbEvent = true;
      }

      if (doCommit) {
         this.commit();
      }

      if (sbEvent) {
         this.notifyAll();
      }
   }

   private final void sendUpdate(SRSelectorData data) {
      if (data.cbIndex != -1) {
         label19:
         try {
            SRSelectorCallback cb = (SRSelectorCallback)this._callbacks.elementAt(data.cbIndex);
            cb.defaultChanged(data.defaultId);
         } finally {
            break label19;
         }
      }

      SRSelectorSyncCollection syncCollection = (SRSelectorSyncCollection)ApplicationRegistry.getApplicationRegistry().waitFor(-1080498432380349415L);
      syncCollection.elementAdded(data);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean doCallback(SRSelectorData data, boolean clearDefault) {
      int oldId = clearDefault ? -1 : data.defaultId;
      if (data.cbIndex != -1) {
         boolean var6 = false /* VF: Semaphore variable */;

         label53:
         try {
            var6 = true;
            SRSelectorCallback t = (SRSelectorCallback)this._callbacks.elementAt(data.cbIndex);
            data.defaultId = t.chooseNewDefault(this._sb, data.cid, oldId, clearDefault ? false : data.userSet);
            var6 = false;
         } finally {
            if (var6) {
               if (clearDefault) {
                  data.defaultId = -1;
               }
               break label53;
            }
         }
      } else if (clearDefault) {
         data.defaultId = -1;
      }

      if (oldId == data.defaultId && !clearDefault) {
         return false;
      }

      data.userSet = false;
      this.sendUpdate(data);
      return true;
   }

   private final void commit() {
      this._persistentAppData.commit();
   }

   private final boolean processSBChange(int sbId, SRSelectorData data) {
      ServiceRecord sr = this._sb.getRecordById(sbId);
      if (sr == null) {
         return false;
      } else {
         boolean sameCid = StringUtilities.strEqualIgnoreCase(data.cid, sr.getCid(), 1701707776);
         if (data.defaultId == sbId) {
            return sameCid ? this.processSBAdd(sbId, data) : this.processSBRemove(sbId, data);
         } else {
            return sameCid ? this.processSBAdd(sbId, data) : false;
         }
      }
   }

   private final boolean processSBAdd(int sbId, SRSelectorData data) {
      ServiceRecord sr = this._sb.getRecordById(sbId);
      if (data.userSet) {
         return false;
      } else if (sr == null) {
         return false;
      } else if (!StringUtilities.strEqualIgnoreCase(data.cid, sr.getCid(), 1701707776)) {
         return false;
      } else if (data.stashedUid != null && StringUtilities.strEqualIgnoreCase(data.stashedUid, sr.getUid(), 1701707776)) {
         data.defaultId = sbId;
         data.userSet = data.stashedUserSet;
         data.stashedUid = null;
         data.stashedUserSet = false;
         this.sendUpdate(data);
         return true;
      } else {
         return data.cbIndex != -1 ? this.doCallback(data, false) : false;
      }
   }

   private final boolean processSBRemove(int sbId, SRSelectorData data) {
      if (data.defaultId != sbId) {
         return false;
      }

      if (data.stashedUid != null) {
         ServiceRecord sr = this._sb.getRecordByUidAndCid(data.stashedUid, data.cid);
         if (sr != null) {
            data.defaultId = sr.getId();
            data.userSet = data.stashedUserSet;
            data.stashedUid = null;
            data.stashedUserSet = false;
            this.sendUpdate(data);
            return true;
         }
      }

      if (data.cbIndex != -1) {
         return this.doCallback(data, true);
      }

      data.defaultId = -1;
      data.userSet = false;
      this.sendUpdate(data);
      return true;
   }

   private final SRSelectorData findData(long guid, String cid) {
      for (int i = this._appData.size() - 1; i >= 0; i--) {
         SRSelectorData data = (SRSelectorData)this._appData.elementAt(i);
         if (guid == data.guid && StringUtilities.strEqualIgnoreCase(cid, data.cid, 1701707776)) {
            return data;
         }
      }

      return null;
   }

   public static final SRSelector getInstance() {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      return (SRSelector)reg.waitFor(2857166788728229964L);
   }

   public SRSelector() {
      if ((this._appData = (Vector)this._persistentAppData.getContents()) == null) {
         this._appData = (Vector)(new Object());
         this._persistentAppData.setContents(this._appData, 51);
      }

      for (int i = this._appData.size() - 1; i >= 0; i--) {
         SRSelectorData data = (SRSelectorData)this._appData.elementAt(i);
         data.cbIndex = -1;
      }

      this.commit();
      this._callbacks = (Vector)(new Object());
      this._sb = ServiceBook.getSB();
   }
}
