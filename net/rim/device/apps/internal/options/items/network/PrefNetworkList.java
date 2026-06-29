package net.rim.device.apps.internal.options.items.network;

import net.rim.device.internal.system.NetworkInfo;

final class PrefNetworkList {
   private NetworkInfo[] _netInfos;
   private int _index;
   private boolean _isListChanged;

   PrefNetworkList(NetworkInfo[] netInfos) {
      this._netInfos = netInfos;
      this._isListChanged = false;
      if (this._netInfos == null) {
         this._netInfos = new NetworkInfo[0];
      }
   }

   public final void add(int index, NetworkInfo netInfo) {
      int clearFlag = 80;
      int catFlag = NetworkOptionsUtils.clearFlag(255, clearFlag);
      if (this._netInfos == null) {
         this._netInfos = new NetworkInfo[1];
         this._netInfos[0] = new NetworkInfo();
         this._netInfos[0].setName(netInfo.getName());
         this._netInfos[0].setNetworkId(netInfo.getNetworkId());
         this._netInfos[0].setCategory(catFlag | netInfo.getCategory());
         this._index = 0;
         this._isListChanged = true;
      } else {
         int size = this._netInfos.length;
         if (index > size) {
            index = size;
         }

         NetworkInfo[] tempNetInfos = new NetworkInfo[size + 1];
         System.arraycopy(this._netInfos, index, tempNetInfos, index + 1, size - index);
         tempNetInfos[index] = new NetworkInfo();
         tempNetInfos[index].setName(NetworkOptionsUtils.getPredefinedNetworkName(netInfo.getNetworkId()));
         tempNetInfos[index].setNetworkId(netInfo.getNetworkId());
         tempNetInfos[index].setCategory(catFlag | netInfo.getCategory());
         if (index != 0) {
            System.arraycopy(this._netInfos, 0, tempNetInfos, 0, index);
         }

         this._netInfos = tempNetInfos;
         this._index = index;
         this._isListChanged = true;
      }
   }

   public final void remove(int index) {
      int size = this.getListSize();
      if (size > 0) {
         if (index >= size) {
            index = size - 1;
         }

         NetworkInfo[] tempNetInfos = new NetworkInfo[size - 1];
         System.arraycopy(this._netInfos, 0, tempNetInfos, 0, index);
         if (index + 1 < size) {
            System.arraycopy(this._netInfos, index + 1, tempNetInfos, index, size - (index + 1));
         }

         this._netInfos = tempNetInfos;
         this._isListChanged = true;
         size = this.getListSize();
         if (size == 0) {
            this._index = -1;
            return;
         }

         if (index < size) {
            this._index = index;
            return;
         }

         this._index = size - 1;
      }
   }

   public final void change(int oldIndex, int newIndex, NetworkInfo netInfo) {
      if (this._netInfos != null && this._netInfos.length > oldIndex) {
         if (oldIndex == newIndex) {
            this._netInfos[newIndex].setNetworkId(netInfo.getNetworkId());
            this._netInfos[newIndex].setName(NetworkOptionsUtils.getPredefinedNetworkName(netInfo.getNetworkId()));
         } else {
            this.remove(oldIndex);
            this.add(newIndex, netInfo);
         }

         this._isListChanged = true;
      }
   }

   public final NetworkInfo[] getList() {
      return this._netInfos;
   }

   public final void setList(NetworkInfo[] netInfos) {
      this._netInfos = netInfos;
   }

   public final int getListSize() {
      return this._netInfos != null ? this._netInfos.length : 0;
   }

   public final NetworkInfo getItem(int index) {
      return this._netInfos != null && this._netInfos.length > index ? this._netInfos[index] : null;
   }

   public final int isItemInList(NetworkInfo netInfo) {
      if (this._netInfos != null && this._netInfos.length > 0) {
         int size = this._netInfos.length;

         while (--size >= 0) {
            if (this.isEqual(this.getItem(size), netInfo)) {
               return size;
            }
         }
      }

      return -1;
   }

   public final boolean isEqual(NetworkInfo net1, NetworkInfo net2) {
      if (net1 == null || net2 == null || net1.getNetworkId() != net2.getNetworkId()) {
         return false;
      } else if (NetworkOptionsUtils.is3GSupported()) {
         boolean umts = (net1.getCategory() & 64) == (net2.getCategory() & 64);
         boolean gprs = (net1.getCategory() & 16) == (net2.getCategory() & 16);
         return umts && gprs;
      } else {
         return true;
      }
   }

   public final int getIndex() {
      return this._index;
   }

   public final void setIndex(int index) {
      int size = this.getListSize();
      this._index = index < size ? index : size - 1;
   }

   public final boolean isListChanged() {
      return this._isListChanged;
   }
}
