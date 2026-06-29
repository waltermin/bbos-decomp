package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing;

import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.Interactor;

public class TimingObject {
   private long _dur;
   private boolean _implicitDuration;
   private TimeContainer _parent;
   private int _id;
   private int _state;
   private int _fill;
   private Interactor _interactor;
   public static final int NOT_STARTED = 0;
   public static final int ACTIVE = 1;
   public static final int COMPLETE = 2;
   public static final int FILL_FREEZE = 1;
   public static final int FILL_REMOVE = 2;

   public TimingObject(int id, TimeContainer parent) {
      this._id = id;
      this._parent = parent;
      this._state = 0;
   }

   public void setInteractor(Interactor interactor) {
      this._interactor = interactor;
   }

   public Interactor getInteractor() {
      return this._interactor;
   }

   public void setImplicitDuration(boolean isImplicit) {
      this._implicitDuration = isImplicit;
   }

   public boolean isImplicitDuration() {
      return this._implicitDuration;
   }

   public long getDur() {
      return this._dur;
   }

   public void setDur(long dur) {
      if (dur < 0) {
         throw new Object();
      }

      this._dur = dur;
   }

   public int getId() {
      return this._id;
   }

   public int getFill() {
      return this._fill;
   }

   public void setFill(int fill) {
      switch (fill) {
         case 0:
            throw new Object();
         case 1:
         case 2:
         default:
            this._fill = fill;
      }
   }

   protected void removeFill() {
   }

   public int getState() {
      return this._state;
   }

   public boolean start(Event triggerEvent) {
      this._state = 1;
      if (this._parent != null) {
         this._parent.notifyChildStarted(this);
      }

      return true;
   }

   public boolean end(Event triggerEvent) {
      boolean finished = false;
      if (this._state != 2) {
         this._state = 2;
         finished = true;
      }

      return finished;
   }

   public void restart() {
      this._state = 0;
   }
}
