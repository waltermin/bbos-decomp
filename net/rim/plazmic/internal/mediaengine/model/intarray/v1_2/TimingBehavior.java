package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.event.Event;

public interface TimingBehavior extends Behavior {
   int ALWAYS = 0;
   int WHEN_NOT_ACTIVE = 1;
   int NEVER = 2;
   int INDEFINITE = -1;
   int FREEZE = 0;
   int REMOVE = 1;

   void setStartedTime(int var1);

   void end();

   boolean getFirstEndTrigger(Event var1);

   boolean getNextEndTrigger(Event var1);

   int getDuration();

   void setDuration(int var1);

   int getRestart();

   void setRestart(int var1);

   int getRepeatCount();

   void setRepeatCount(int var1);

   int getRepeatDur();

   void setRepeatDur(int var1);

   int getFill();

   void setFill(int var1);
}
