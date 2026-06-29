package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.util.IntHashtable;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventLogic;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.TimingObject;

public class SMILModel {
   private boolean _hasAudio;
   private boolean _hasAnimation;
   private boolean _hasVideo;
   private Hashtable _metaData = new Hashtable();
   private IntHashtable _regions = new IntHashtable();
   private IntHashtable _timingObjects = new IntHashtable();
   private Event _startEvent;
   private Region _rootLayout;
   private EventLogic _logic;

   public boolean hasAudio() {
      return this._hasAudio;
   }

   public void setHasAudio(boolean hasAudio) {
      this._hasAudio = hasAudio;
   }

   public boolean hasAnimation() {
      return this._hasAnimation;
   }

   public void setHasAnimation(boolean hasAnimation) {
      this._hasAnimation = hasAnimation;
   }

   public boolean hasVideo() {
      return this._hasVideo;
   }

   public void setHasVideo(boolean hasVideo) {
      this._hasVideo = hasVideo;
   }

   public Region getRegion(int id) {
      return (Region)this._regions.get(id);
   }

   public TimingObject getTimingObject(int id) {
      return (TimingObject)this._timingObjects.get(id);
   }

   public Enumeration getAllTimingObjects() {
      return this._timingObjects.elements();
   }

   public String getMetaData(String name) {
      return (String)this._metaData.get(name);
   }

   public void addRegion(int id, Region region) {
      this._regions.put(id, region);
   }

   public void addTimingObject(int id, TimingObject timingObject) {
      this._timingObjects.put(id, timingObject);
   }

   public void addMetaData(String name, String data) {
      this._metaData.put(name, data);
   }

   public Event getStartEvent() {
      return this._startEvent;
   }

   public void setStartEvent(Event start) {
      this._startEvent = start;
   }

   public Region getRootLayout() {
      return this._rootLayout;
   }

   public void setRootLayout(Region rootLayout) {
      this._rootLayout = rootLayout;
   }

   public EventLogic getEventLogic() {
      return this._logic;
   }

   public void setEventLogic(EventLogic logic) {
      this._logic = logic;
   }
}
