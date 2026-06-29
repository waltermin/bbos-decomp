package net.rim.device.cldc.impl.tunnel;

public interface TunnelEvent {
   long GUID;
   int INIT_FACTORY;
   int TUNNEL_OPEN;
   int TUNNEL_CLOSE;
   int TUNNEL_KICK;
   int TUNNEL_RESET;
   int TUNNEL_STANDBY;
   int PDP_STATUS;
   int NETWORK_STATUS;
   int TUNNEL_ACTIVATE;
   int TUNNEL_DEACTIVATE;
   int STATUS_ACTIVE;
   int STATUS_INACTIVE;
   int STATUS_NON_CRITICAL;
   int STATUS_NO_RESPONSE;
   int STATUS_CRITICAL;
   int STATUS_UPDATE;
   int THREAD_START;
   int THREAD_STOP;
   int THREAD_ACTION;
   int THREAD_WAIT;
}
