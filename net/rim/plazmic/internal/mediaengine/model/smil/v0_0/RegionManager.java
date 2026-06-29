package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

import java.util.Enumeration;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.event.EventLogic;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.MediaObject;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.TimingObject;
import net.rim.plazmic.internal.mediaengine.service.EventResolver;
import net.rim.plazmic.mediaengine.MediaListener;

public class RegionManager implements MediaListener, Interactor {
   private SMILModel _model;
   private EventEngine _engine;
   private EventResolver _resolver;
   private SMILPlayer _player;

   public void setPlayer(SMILPlayer player) {
      this._player = player;
   }

   public void setEngine(EventEngine engine) {
      this._engine = engine;
   }

   public void setEventResolver(EventResolver resolver) {
      this._resolver = resolver;
   }

   public void setModel(SMILModel model) {
      this._model = model;
      if (this._model != null) {
         Enumeration timingObjects = this._model.getAllTimingObjects();

         while (timingObjects.hasMoreElements()) {
            TimingObject to = (TimingObject)timingObjects.nextElement();
            to.setInteractor(this);
         }
      }
   }

   @Override
   public void mediaEvent(Object sender, int event, int id, Object eventData) {
      if (this._engine == null || this._resolver == null) {
         throw new IllegalStateException();
      }

      if (this._model != null) {
         Event triggerEvent = (Event)eventData;
         TimingObject timeObject = this._model.getTimingObject(id);
         switch (event) {
            case 0:
               break;
            case 1:
            default:
               if (timeObject instanceof MediaObject) {
                  MediaObject mo = (MediaObject)timeObject;
                  if (mo.getMediaType() == 1) {
                     this._player.setCurrentAudio(mo);
                  } else if (mo.getMediaType() == 3) {
                     this._player.setCurrentVideo(mo);
                  }
               }

               if (timeObject.start(triggerEvent)) {
                  this.fireEvent(1, id, triggerEvent._time);
                  return;
               }
               break;
            case 2:
               if (timeObject.end(triggerEvent)) {
                  this.fireEvent(2, id, triggerEvent._time);
               }
         }
      }
   }

   @Override
   public void fireEvent(int type, int id, long time) {
      Event event = this._engine.getEventInstance();
      event._sender = this;
      event._listener = this._resolver;
      event._event = type;
      event._eventParam = id;
      event._time = time;
      this._engine.postEvent(event, true);
      this._engine.releaseEventInstance(event);
   }

   @Override
   public EventLogic getEventLogic() {
      EventLogic logic = null;
      if (this._resolver != null) {
         logic = (EventLogic)this._resolver.getEventLogic();
      }

      return logic;
   }
}
