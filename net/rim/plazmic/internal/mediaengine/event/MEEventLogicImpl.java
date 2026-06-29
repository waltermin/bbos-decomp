package net.rim.plazmic.internal.mediaengine.event;

public class MEEventLogicImpl extends EventLogicImpl {
   @Override
   protected long getKey(Event triggerEvent) {
      return (long)triggerEvent._eventParam << 32 | triggerEvent._eventParamLong << 16 | triggerEvent._event;
   }
}
