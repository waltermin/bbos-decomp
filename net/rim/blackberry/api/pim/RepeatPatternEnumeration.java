package net.rim.blackberry.api.pim;

import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Handle;

final class RepeatPatternEnumeration implements Enumeration {
   protected int _count;
   protected boolean _hasCount;
   protected long _startDate;
   protected long _beginning;
   protected long _ending;
   protected long _countDate;
   protected net.rim.device.apps.api.calendar.modelcontrollerinterface.Event _event;
   protected Recur$Handle _handle;

   RepeatPatternEnumeration(net.rim.device.apps.api.calendar.modelcontrollerinterface.Event event, long startDate, long beginning, long ending, int count) {
      this._event = event;
      this._startDate = startDate;
      this._beginning = beginning;
      this._ending = ending;
      this._handle = new Recur$Handle();
      this._count = count;
      this._countDate = findLastCount(this._event, this._startDate, this._count);
   }

   public static final long findLastCount(net.rim.device.apps.api.calendar.modelcontrollerinterface.Event event, long start, int count) {
      Recur recur = event.getRecurrenceCopy();
      if (count == 0) {
         return !recur.isFinite() ? 0 : recur.getEndDate();
      }

      boolean next = true;
      Recur$Handle handle = new Recur$Handle();
      long curDate = 0;

      for (int i = 0; i < count; i++) {
         if (handle._handle == 0) {
            next = event.getHandleAfterTime(handle, start - 1, TimeZone.getDefault());
         } else {
            next = event.getHandleAfter(handle, handle._handle);
         }

         if (!next) {
            break;
         }

         long evtTime = event.getStartFromHandle(handle._handle, TimeZone.getDefault());
         curDate = evtTime;
      }

      return curDate;
   }

   @Override
   public final boolean hasMoreElements() {
      boolean nextElement = false;
      boolean result = false;
      long recurHandle = 0;
      Recur$Handle h = new Recur$Handle();
      if (this._handle._handle == 0) {
         nextElement = this._event.getHandleAfterTime(h, this._beginning - 1, TimeZone.getDefault());
         recurHandle = h._handle;
      } else {
         nextElement = this._event.getHandleAfter(h, this._handle._handle);
         recurHandle = h._handle;
      }

      if (nextElement) {
         long evtTime = this._event.getStartFromHandle(recurHandle, TimeZone.getDefault());
         if ((this._ending == 0 || evtTime <= this._ending) && (this._count == 0 || this._count > 0 && evtTime <= this._countDate)) {
            result = true;
         }
      }

      return result;
   }

   @Override
   public final Object nextElement() {
      boolean next = false;
      if (this._hasCount && this._count == 0) {
         return null;
      }

      if (this._handle._handle == 0) {
         next = this._event.getHandleAfterTime(this._handle, this._beginning - 1, TimeZone.getDefault());
      } else {
         next = this._event.getHandleAfter(this._handle, this._handle._handle);
      }

      if (!next) {
         return null;
      }

      long evtTime = this._event.getStartFromHandle(this._handle._handle, TimeZone.getDefault());
      return this._ending != 0 && this._event.getStartFromHandle(this._handle._handle, TimeZone.getDefault()) > this._ending
            || this._count != 0 && (this._count <= 0 || evtTime > this._countDate)
         ? null
         : new Date(evtTime);
   }
}
