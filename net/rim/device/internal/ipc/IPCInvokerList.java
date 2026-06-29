package net.rim.device.internal.ipc;

import net.rim.device.api.system.Application;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.proxy.Proxy;

public class IPCInvokerList {
   private IPCInvoker[] _list = new IPCInvoker[0];

   public int size() {
      return this._list.length;
   }

   public IPCInvoker elementAt(int index) {
      IPCInvoker element = null;
      if (index < this._list.length && index >= 0) {
         element = this._list[index];
      }

      return element;
   }

   public int indexOf(Object listener) {
      for (int index = 0; index < this._list.length; index++) {
         Object currListener = this._list[index].getListener();
         if (listener.equals(currListener)) {
            return index;
         }
      }

      return -1;
   }

   public boolean add(Object listener) {
      Application app = null;

      try {
         app = Application.getApplication();
      } catch (IllegalStateException e) {
         app = Proxy.getInstance();
      }

      return this.add(app, listener);
   }

   public boolean add(Application app, Object listener) {
      if (listener != null && !Arrays.contains(this._list, listener)) {
         this.add(IPCInvokerFactory.getStrongIPCInvoker(app, listener));
         return true;
      } else {
         return false;
      }
   }

   public boolean addWeak(Application app, Object listener) {
      if (listener != null && !Arrays.contains(this._list, listener)) {
         this.add(IPCInvokerFactory.getIPCInvoker(app, listener));
         return true;
      } else {
         return false;
      }
   }

   protected void add(IPCInvoker entry) {
      Arrays.add(this._list, entry);
   }

   public void clear() {
      this._list = new IPCInvoker[0];
   }

   public void remove(int index) {
      Arrays.removeAt(this._list, index);
   }

   public int remove(Object listener) {
      int index = Arrays.getIndex(this._list, listener);
      if (index > -1) {
         this.remove(index);
      }

      return index;
   }

   public void removeDeadIPCInvokers() {
      for (int i = 0; i < this._list.length; i++) {
         if (!this.isAlive(i)) {
            this.remove(i);
            i--;
         }
      }
   }

   public boolean invoke(int index, IPCRunnable runnable) {
      boolean success = this._list[index].invoke(runnable);
      if (!success) {
         this.remove(index);
         return false;
      } else {
         return true;
      }
   }

   public IPCResult invokeResultWait(int index, IPCBlockingReturnRunnable runnable) {
      return this._list[index].invokeResultWait(runnable);
   }

   public void invokeAll(IPCRunnable runnable) {
      for (int i = 0; i < this._list.length; i++) {
         if (!this.invoke(i, runnable)) {
            i--;
         }
      }
   }

   public boolean isAlive(int index) {
      return this._list[index].isAlive();
   }
}
