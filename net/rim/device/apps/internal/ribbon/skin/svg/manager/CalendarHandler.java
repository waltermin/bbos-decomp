package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DescriptionProvider;
import net.rim.device.apps.api.pim.TimeBasedObjectProvider;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;

class CalendarHandler extends Handler implements RealtimeClockListener {
   Duration[] _calEvents = new Object[this.MaxEntries];
   private long _nextUpdateTime = Long.MAX_VALUE;

   CalendarHandler(ModelInteractorImpl mi, UiApplication app) {
      super(mi, app);
   }

   @Override
   public void clockUpdated() {
      long curTime = System.currentTimeMillis();
      if (curTime > this._nextUpdateTime) {
         this.updateLater();
      }
   }

   @Override
   public void invoke(int index) {
      if (index >= 0 && index < super.MaxEntries) {
         Duration event = this._calEvents[index];
         if (event instanceof Object) {
            ContextObject context = ContextObject.castOrCreate(null);
            context.setFlag(64);
            ((ActionProvider)event).perform(6099736323056465049L, context);
         }
      }
   }

   @Override
   public void update(boolean forceScreenUpdate) {
      if (super.MaxEntries >= 1) {
         synchronized (super._modelInteractor) {
            for (int i = 0; i < super.MaxEntries; i++) {
               this._calEvents[i] = null;
            }

            if (super._nodes != null) {
               for (int i = super._nodes.length - 1; i >= 0; i--) {
                  if (super._nodes[i] != null) {
                     super._nodes[i].setString(super.BLANK);
                  }
               }
            }

            for (int i = 1; i <= super.MaxEntries; i++) {
               this.setDisplayable(((StringBuffer)(new Object("calendar"))).append(i).append("hotspot").toString(), false);
            }

            TimeZone tz = TimeZone.getDefault();
            long curTime = System.currentTimeMillis();
            TimeBasedObjectProvider calProvider = CalendarProxy.getInstance().getCalendarDatabase();
            Vector calEvents = (Vector)(new Object());
            int numToGetBefore = 16;
            int numToGetAfter = 16;
            int numAllDays = 0;
            boolean finished = false;

            while (!finished) {
               calProvider.getElementsStartingAround(curTime, numToGetBefore, numToGetAfter, tz, calEvents);
               int numAfter = 0;
               long latestStart = Long.MIN_VALUE;
               long start1 = 0;
               long start2 = 0;
               int size = calEvents.size();
               int nextEventIdx = 0;

               for (int i = 0; i < size; i++) {
                  Duration event = (Duration)calEvents.elementAt(i);
                  if (event != null) {
                     long start = event.getStart(tz);
                     if (start + event.getDuration(tz) >= curTime) {
                        if (start > curTime) {
                           numAfter++;
                        }

                        if (start > latestStart) {
                           latestStart = start;
                        }

                        if (event.isAllDay()) {
                           numAllDays++;
                        } else {
                           this._calEvents[nextEventIdx++] = event;
                           if (nextEventIdx >= super.MaxEntries) {
                              break;
                           }
                        }
                     }
                  }
               }

               if (this._calEvents[super.MaxEntries - 1] == null && numAfter >= numToGetAfter && numAllDays < 50) {
                  curTime = latestStart + 1;
                  numToGetBefore = 0;
                  numToGetAfter = 2 * numToGetAfter;
               } else {
                  finished = true;
               }
            }

            this._nextUpdateTime = Long.MAX_VALUE;

            for (int i = 0; i < super.MaxEntries && this._calEvents[i] != null; i++) {
               long endingTime = this._calEvents[i].getStart(tz) + this._calEvents[i].getDuration(tz);
               if (endingTime < this._nextUpdateTime) {
                  this._nextUpdateTime = endingTime;
               }
            }

            for (int counter = 0; counter < super.MaxEntries; counter++) {
               Duration event = this._calEvents[counter];
               if (event != null) {
                  if (super._nodes != null) {
                     char[] time = this.formattedTime(event.getStart(tz)).toCharArray();
                     if (super._nodes[counter * 3] != null) {
                        super._nodes[counter * 3].setString(time);
                     }

                     if (event instanceof Object) {
                        DescriptionProvider eventDescription = (DescriptionProvider)event;
                        String subject = eventDescription.getStringForField(5649235763655597796L);
                        if (super._nodes[counter * 3 + 1] != null && subject != null) {
                           TextNode node = super._nodes[counter * 3 + 1];
                           node.setString(subject.toCharArray());
                        }

                        String location = eventDescription.getStringForField(9164664086580876244L);
                        if (super._nodes[counter * 3 + 2] != null && location != null) {
                           TextNode node = super._nodes[counter * 3 + 2];
                           node.setString(location.toCharArray());
                        }
                     }
                  }

                  this.setDisplayable(((StringBuffer)(new Object("calendar"))).append(counter + 1).append("hotspot").toString(), true);
               }
            }
         }

         if (forceScreenUpdate) {
            super._application.repaint();
         }
      }
   }
}
