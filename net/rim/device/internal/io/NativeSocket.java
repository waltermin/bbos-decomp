package net.rim.device.internal.io;

public final class NativeSocket {
   private int _socketId;
   private Object _operationLock;
   private int _operationResult;
   private static final int SOC_ERROR_TIMEOUT;
   public static final int SOC_PENDING;
   public static final int SOC_OK;
   public static final int SOCK_STREAM;
   public static final int SOCK_DGRAM;
   public static final int SOCK_RAW;
   public static final int IPPROTO_IP;
   public static final int IPPROTO_ICMP;
   public static final int IPPROTO_TCP;
   public static final int IPPROTO_UDP;
   private static final long LOW_DWORD;
   private static final int TIMEOUT;

   public NativeSocket() {
      this._socketId = -1;
      this._operationLock = new Object();
   }

   private NativeSocket(int socketId) {
      this._socketId = socketId;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void create(int tunnel, int socketType, int protocolType, int localPort) {
      NativeSocketEventDispatcher.addListener(this);
      boolean var18 = false /* VF: Semaphore variable */;

      try {
         var18 = true;
         long result;
         int returnCode;
         synchronized (this._operationLock) {
            result = allocate0(socketType, protocolType);
            returnCode = (int)(result & 4294967295L);
            if (returnCode == 0) {
               try {
                  this._operationResult = -100;
                  this._operationLock.wait(120000);
               } catch (InterruptedException var20) {
               }

               returnCode = this._operationResult;
            }
         }

         if (returnCode != 1) {
            checkError(returnCode);
         }

         this._socketId = (int)(result >> 32);
         synchronized (this._operationLock) {
            result = bindIPv4_0(this._socketId, tunnel, localPort);
            returnCode = (int)(result & 4294967295L);
            if (returnCode == 0) {
               try {
                  this._operationResult = -100;
                  this._operationLock.wait(120000);
               } catch (InterruptedException var19) {
               }

               returnCode = this._operationResult;
            }
         }

         checkError(returnCode);
         var18 = false;
      } finally {
         if (var18) {
            NativeSocketEventDispatcher.removeListener(this);
         }
      }

      NativeSocketEventDispatcher.removeListener(this);
   }

   public static final native boolean isMultiRATSupported();

   public final int getSocketId() {
      return this._socketId;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void connectIPv4(int ipv4Addr, int destPort) {
      NativeSocketEventDispatcher.addListener(this);
      boolean var13 = false /* VF: Semaphore variable */;

      try {
         var13 = true;
         int returnCode;
         synchronized (this._operationLock) {
            long result = connectIPv4_0(this._socketId, ipv4Addr, destPort);
            returnCode = (int)(result & 4294967295L);
            if (returnCode == 0) {
               try {
                  this._operationResult = -100;
                  this._operationLock.wait(120000);
               } catch (InterruptedException var14) {
               }

               returnCode = this._operationResult;
            }
         }

         if (returnCode != 1) {
            checkError(returnCode);
            var13 = false;
         } else {
            var13 = false;
         }
      } finally {
         if (var13) {
            NativeSocketEventDispatcher.removeListener(this);
         }
      }

      NativeSocketEventDispatcher.removeListener(this);
   }

   public final void listen(int backlog) {
      checkError(listen0(this._socketId, backlog));
   }

   public final NativeSocket accept() {
      long result = accept0(this._socketId);
      checkError(result & 4294967295L);
      return new NativeSocket((int)(result >> 32));
   }

   public final void shutdown() {
      checkError(shutdown0(this._socketId));
   }

   public final void close() {
      checkError(close0(this._socketId));
   }

   public final int send(byte[] data, int offset, int length, int flags) {
      long result = send0(this._socketId, data, offset, length, flags);
      checkError(result & 4294967295L);
      return (int)(result >> 32);
   }

   public final int send(int aByte, int flags) {
      long result = send0(this._socketId, aByte, flags);
      checkError(result & 4294967295L);
      return (int)(result >> 32);
   }

   public final int sendToIPv4(byte[] data, int offset, int length, int flags, int ipv4Addr, int port) {
      long result = sendToIPv4_0(this._socketId, data, offset, length, flags, ipv4Addr, port);
      checkError(result & 4294967295L);
      return (int)(result >> 32);
   }

   public final int available() {
      int result = available0(this._socketId);
      if (result < 0) {
         throw new SocketIOException(-6);
      } else {
         return result;
      }
   }

   public final int receive(byte[] data, int offset, int length, int flags) {
      long result = receive0(this._socketId, data, offset, length, flags);
      checkError(result & 4294967295L);
      return (int)(result >> 32);
   }

   public final int receiveFrom0(byte[] data, int offset, int length, int flags) {
      long result = receiveFrom0(this._socketId, data, offset, length, flags);
      checkError(result & 4294967295L);
      return (int)(result >> 32);
   }

   public final void setSocketOption(int socketLevel, int socketOption, int value) {
      checkError(setOption0(this._socketId, socketLevel, socketOption, value));
   }

   public final int getSocketOption(int socketLevel, int socketOption) {
      long result = getOption0(this._socketId, socketLevel, socketOption);
      checkError(result & 4294967295L);
      return (int)(result >> 32);
   }

   public final void socketConnected(int resultCode) {
      synchronized (this._operationLock) {
         this._operationResult = resultCode;
         this._operationLock.notify();
      }
   }

   public final void socketDisconnected() {
      synchronized (this._operationLock) {
         this._operationResult = -1;
         this._operationLock.notify();
      }
   }

   public final void flush() {
      checkError(flush0(this._socketId));
   }

   private static final native long allocate0(int var0, int var1);

   private static final native int bindIPv4_0(int var0, int var1, int var2);

   private static final native int connectIPv4_0(int var0, int var1, int var2);

   private static final native int listen0(int var0, int var1);

   private static final native long accept0(int var0);

   private static final native int shutdown0(int var0);

   private static final native int close0(int var0);

   private static final native long send0(int var0, int var1, int var2);

   private static final native long send0(int var0, byte[] var1, int var2, int var3, int var4);

   private static final native int sendToIPv4_0(int var0, byte[] var1, int var2, int var3, int var4, int var5, int var6);

   private static final native int available0(int var0);

   private static final native long receive0(int var0, byte[] var1, int var2, int var3, int var4);

   private static final native long receiveFrom0(int var0, byte[] var1, int var2, int var3, int var4);

   private static final native int setOption0(int var0, int var1, int var2, int var3);

   private static final native long getOption0(int var0, int var1, int var2);

   private static final native void getPeerName0(int var0);

   private static final native void getSockName0(int var0);

   private static final native int flush0(int var0);

   public static final void checkError(long result) {
      if (result != 1 && result != 0) {
         throw new SocketIOException((int)result);
      }
   }
}
