package net.rim.device.api.hrt;

import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.system.RadioInternal;

public final class HostRoutingTable implements Persistable, GlobalEventListener {
   private boolean _dirty;
   private HostRoutingInfo[] _hris = new HostRoutingInfo[0];
   private int _activeIndex;
   private long _curNpc;
   private HostRoutingInfo _wifiHRI;
   public int _regTTL;
   public long _regTTLExpiry;
   public static final int NULL_ACTIVE_INDEX = -1;
   private static final int NO_ACTIVE_INDEX = -2;
   public static final long GUID_DEVICE_REGISTERED = -8927980184023446756L;
   public static final long GUID_HRT_EVENT_NO_ACTIVE_HRI = -3864212166794284297L;
   public static final long GUID_HRT_EVENT_NULL_NPC_RECEIVED = -2686242019764477137L;
   public static final long GUID_HRT_EVENT_NEW_ACTIVE_HRI = -6531073315810526672L;
   public static final long GUID_HRT_EVENT_ACTIVE_HRI_MODIFIED = 2200641410611652722L;
   public static final long GUID_HRT_EVENT_NEW_WIFI_HRI = -2283956412806126038L;
   public static final long GUID_HRT_EVENT_WIFI_HRI_MODIFIED = 8951540267497860657L;
   public static final long GUID_HRT_EVENT_WIFI_HRI_REMOVED = 6830133996698118599L;

   public final void init() {
      this._activeIndex = -1;
      this._curNpc = -1;
   }

   public final boolean isDirty() {
      boolean dirty = this._dirty;
      if (!dirty) {
         for (int i = this._hris.length - 1; i >= 0; i--) {
            dirty |= this._hris[i].isDirty();
         }
      }

      return dirty;
   }

   public final void setDirty(boolean flag) {
      this._dirty = flag;

      for (int i = this._hris.length - 1; i >= 0; i--) {
         this._hris[i].setDirty(flag);
      }
   }

   public final boolean isValid() {
      return this._activeIndex >= 0 ? this._hris[this._activeIndex].isValid() : false;
   }

   public final void setHris(HostRoutingInfo[] hris) {
      this.setHris(hris, false);
   }

   public final synchronized void setHris(HostRoutingInfo[] hris, boolean suppressNoActiveHriEvent) {
      this._dirty = true;
      this._activeIndex = -2;
      if (hris == null) {
         hris = new HostRoutingInfo[0];
      }

      HostRoutingInfo oldWifiHRI = this._wifiHRI;
      HostRoutingInfo newWifiHRI = null;

      for (int i = 0; i < hris.length; i++) {
         if ((hris[i].getArt() & 8) != 0) {
            newWifiHRI = hris[i];
            break;
         }
      }

      this._hris = hris;
      this._wifiHRI = newWifiHRI;
      if (newWifiHRI == null && oldWifiHRI != null) {
         RIMGlobalMessagePoster.postGlobalEvent(6830133996698118599L, 0, 0, oldWifiHRI, null);
      } else if (newWifiHRI != null) {
         if (oldWifiHRI != null) {
            RIMGlobalMessagePoster.postGlobalEvent(8951540267497860657L, 0, 0, oldWifiHRI, newWifiHRI);
         } else if (newWifiHRI != null) {
            RIMGlobalMessagePoster.postGlobalEvent(-2283956412806126038L, 0, 0, newWifiHRI, null);
         }
      }

      this.commit(suppressNoActiveHriEvent);
   }

   public final HostRoutingInfo[] getHris() {
      return this._hris;
   }

   public final int getNumHris() {
      return this._hris.length;
   }

   public final synchronized boolean addHri(HostRoutingInfo hri) {
      if (this.findHriByNpcAndArt(hri.getNpc(), hri.getArt()) != -1) {
         return false;
      }

      if ((hri.getArt() & 8) != 0) {
         if (this._wifiHRI != null) {
            return false;
         }

         this._wifiHRI = hri;
         RIMGlobalMessagePoster.postGlobalEvent(-2283956412806126038L, 0, 0, this._wifiHRI, null);
      }

      Arrays.add(this._hris, hri);
      this._dirty = true;
      this.commit();
      return true;
   }

   public final synchronized boolean removeHri(HostRoutingInfo hri) {
      return this.removeHri(this.findHri(hri));
   }

   public final synchronized boolean removeHri(int index) {
      HostRoutingInfo oldHRI = this._hris[index];
      if (oldHRI == this._wifiHRI) {
         RIMGlobalMessagePoster.postGlobalEvent(6830133996698118599L, 0, 0, oldHRI, null);
         this._wifiHRI = null;
      }

      Arrays.removeAt(this._hris, index);
      this._dirty = true;
      if (this._activeIndex > index) {
         this._activeIndex--;
      } else if (this._activeIndex == index) {
         this._activeIndex = -2;
      }

      this.commit();
      return true;
   }

   public final synchronized void removeAll() {
      if (this._wifiHRI != null) {
         RIMGlobalMessagePoster.postGlobalEvent(6830133996698118599L, 0, 0, this._wifiHRI, null);
         this._wifiHRI = null;
      }

      this._hris = new HostRoutingInfo[0];
      this._activeIndex = -2;
      this._dirty = true;
      this.commit();
   }

   public final int getActiveIndex() {
      return this._activeIndex;
   }

   public final HostRoutingInfo getActiveHri() {
      return this._activeIndex >= 0 ? this._hris[this._activeIndex] : null;
   }

   public final int findHriByNpcAndArt(long npc, int art) {
      for (int i = this._hris.length - 1; i >= 0; i--) {
         if (this._hris[i].getNpc() == npc && (this._hris[i].getArt() & art) != 0) {
            return i;
         }
      }

      return -1;
   }

   public final boolean setActiveIndex() {
      return this.setActiveIndex(false);
   }

   public final synchronized boolean setActiveIndex(boolean suppressNoActiveHriEvent) {
      return this.setActiveIndex(HRUtils.getNpcForActiveNetwork(), suppressNoActiveHriEvent);
   }

   public final boolean setActiveIndex(long npc) {
      return this.setActiveIndex(npc, false);
   }

   public final synchronized boolean setActiveIndex(long npc, boolean suppressNoActiveHriEvent) {
      int oldIndex = this._activeIndex;
      long oldNpc = this._curNpc;
      int activeWAFs = RadioInfo.getActiveWAFs();
      int art = 0;
      if ((activeWAFs & 1) != 0) {
         if (HRUtils.isPretendCDMA()) {
            art = 16;
         } else {
            int rats3GPP = RadioInternal.get3GPPSupportedRats();
            int networkService = RadioInfo.getNetworkService();
            if ((rats3GPP & 4) != 0 && (networkService & 16384) != 0) {
               art = 4;
            } else if ((rats3GPP & 2) != 0 && (networkService & 4096) != 0) {
               art = 2;
            } else {
               art = 1;
            }
         }
      } else if ((activeWAFs & 2) != 0) {
         art = 16;
      } else if ((activeWAFs & 8) != 0) {
         art = 32;
      }

      this._curNpc = npc;
      int newIndex = this.findHriByNpcAndArt(npc, art);
      if (newIndex == -1) {
         newIndex = this.findHriByNpcAndArt(npc & 252, art);
         if (newIndex == -1) {
            this._activeIndex = newIndex;
            if (npc != -1) {
               if (newIndex != oldIndex || this._curNpc != oldNpc) {
                  RIMGlobalMessagePoster.postGlobalEvent(-3864212166794284297L, suppressNoActiveHriEvent ? 1 : 0, 0, this, null);
                  return false;
               }
            } else {
               RIMGlobalMessagePoster.postGlobalEvent(-2686242019764477137L, 1, 0, this, null);
            }

            return false;
         }
      }

      this._activeIndex = newIndex;
      if (newIndex != oldIndex) {
         RIMGlobalMessagePoster.postGlobalEvent(-6531073315810526672L, 2, 0, this, null);
         return true;
      }

      if (this._hris[newIndex].isDirty()) {
         RIMGlobalMessagePoster.postGlobalEvent(2200641410611652722L, 3, 0, this, null);
      }

      return true;
   }

   public final void commit() {
      this.commit(false);
   }

   public final synchronized void commit(boolean suppressNoActiveHriEvent) {
      this.setActiveIndex(suppressNoActiveHriEvent);
      this.setDirty(false);
      PersistentObject.commit(this);
   }

   public final long getTtlExpiry() {
      return this._regTTLExpiry;
   }

   public final void setTtl(int ttl) {
      this._regTTL = ttl;
      this._regTTLExpiry = this._regTTL > 0 ? System.currentTimeMillis() + (long)this._regTTL * 1000 : 0;
      this.setDirty(true);
   }

   public final int getTtl() {
      return this._regTTL;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -254931370837867202L) {
         long npc = data0 & 4294967295L | (long)data1 << 32;
         this.setActiveIndex(npc);
      }
   }

   public HostRoutingTable() {
      this.init();
   }

   private final int findHri(HostRoutingInfo hri) {
      for (int i = this._hris.length - 1; i >= 0; i--) {
         if (this._hris[i] == hri) {
            return i;
         }
      }

      return -1;
   }
}
