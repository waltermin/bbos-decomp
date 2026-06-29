package net.rim.device.cldc.impl.datarecovery;

interface OffOnEvent {
   long GUID;
   String STR;
   int INIT_OFFON;
   int RADIO_OFF;
   int RADIO_ON;
   int RADIO_CYCLE_REASON;
   int DATA_DETACH;
   int DATA_ATTACH;
}
