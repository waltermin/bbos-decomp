package net.rim.device.internal.io;

import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

public final class NativeSocketEventDispatcher extends EventDispatcher {
   private int _protocolDaemonPid;
   private static final int JVM_DEVICE_SOCKET;
   private static final int SOC_CONNECT_CNF;
   private static final int SOC_CLOSE_CNF;
   private static final int SOC_CONNECT_IND;
   private static final int SOC_SEND_CNF;
   private static final int SOC_RECEIVE_IND;
   private static final int SOC_DISCONNECT_IND;
   private static final int SOC_READY_IND;

   private NativeSocketEventDispatcher(int protocolDaemonPid) {
      this._protocolDaemonPid = protocolDaemonPid;
   }

   @Override
   public final int getNotifyProcessId(Message message) {
      return this._protocolDaemonPid;
   }

   public static final void addListener(Object obj) {
      ProtocolDaemon.getInstance().addListener(39, obj);
   }

   public static final void removeListener(Object obj) {
      ProtocolDaemon.getInstance().removeListener(39, obj);
   }

   public static final void register(int protocolDaemonPid) {
      if (NativeSocket.isMultiRATSupported()) {
         EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
         synchronized (dispatchManager) {
            if (dispatchManager.getDispatcher(39) == null) {
               dispatchManager.setDispatcher(39, new NativeSocketEventDispatcher(protocolDaemonPid));
            }
         }
      }
   }

   @Override
   public final void dispatch(Message message, Object listener) {
      if (!(listener instanceof NativeSocket)) {
         if (!(listener instanceof BoundNativeSocketListener)) {
            if (listener instanceof NativeSocketConnectionListener) {
               NativeSocketConnectionListener socketListener = (NativeSocketConnectionListener)listener;
               int socketId = message.getData0();
               switch (message.getEvent()) {
                  case 9985:
                     socketListener.socketConnected(socketId);
                     return;
                  case 9990:
                     socketListener.socketDisconnected(socketId);
                     return;
                  case 9991:
                     socketListener.socketWriteReady(socketId);
               }
            }
         } else {
            BoundNativeSocketListener socket = (BoundNativeSocketListener)listener;
            if (socket.getSocketId() == message.getData0()) {
               switch (message.getEvent()) {
                  case 9988:
                     break;
                  case 9989:
                  default:
                     socket.socketDataReady();
                     return;
                  case 9990:
                     socket.socketDisconnected();
                     return;
                  case 9991:
                     socket.socketWriteReady();
                     return;
               }
            }
         }
      } else {
         NativeSocket socket = (NativeSocket)listener;
         if (socket.getSocketId() == message.getData0()) {
            switch (message.getEvent()) {
               case 9985:
                  socket.socketConnected(message.getSubMessage());
                  return;
               case 9990:
                  socket.socketDisconnected();
                  return;
            }
         }
      }
   }
}
