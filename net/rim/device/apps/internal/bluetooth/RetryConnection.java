package net.rim.device.apps.internal.bluetooth;

import net.rim.device.internal.proxy.Proxy;

final class RetryConnection implements Runnable {
   private BluetoothDeviceManagerImpl _bluetoothDeviceManagerImpl;
   private int _retryConnectionCount;
   private int _retryConnectionInvokeLaterId;
   public static final int RETRY_CONNECTION_RETRIES = 2;
   public static final int RETRY_CONNECTION_INTERVAL = 10;

   public RetryConnection(BluetoothDeviceManagerImpl bluetoothDeviceManagerImpl) {
      this._bluetoothDeviceManagerImpl = bluetoothDeviceManagerImpl;
      this._retryConnectionCount = 0;
      this._retryConnectionInvokeLaterId = -1;
   }

   public final boolean isActive() {
      return this._retryConnectionCount > 0;
   }

   public final void cancel() {
      synchronized (this) {
         if (this._retryConnectionInvokeLaterId != -1) {
            Proxy proxy = Proxy.getInstance();
            proxy.cancelInvokeLater(this._retryConnectionInvokeLaterId);
            this._retryConnectionInvokeLaterId = -1;
            this._retryConnectionCount = 0;
         }
      }
   }

   public final void submit(long time) {
      this.submit(time, 0);
   }

   public final void submit(long time, int retries) {
      synchronized (this) {
         if (this._retryConnectionInvokeLaterId == -1) {
            if (retries > 0) {
               this._retryConnectionCount = retries;
            }

            Proxy proxy = Proxy.getInstance();
            this._retryConnectionInvokeLaterId = proxy.invokeLaterInternal(this, time, false);
         }
      }
   }

   @Override
   public final void run() {
      synchronized (this) {
         this._retryConnectionInvokeLaterId = -1;
         if (this._bluetoothDeviceManagerImpl.tryConnection() && --this._retryConnectionCount > 0) {
            this.submit(10000);
         } else {
            this._retryConnectionCount = 0;
         }
      }
   }
}
