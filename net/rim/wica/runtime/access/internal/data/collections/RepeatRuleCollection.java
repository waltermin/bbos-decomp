package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.wica.common.builtindata.componentdefn.RepeatRuleCompDef;
import net.rim.wica.runtime.access.internal.data.enumeration.FrequencyEnumConverter;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;
import net.rim.wica.runtime.access.internal.data.handlers.LongFieldHandler;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;

public class RepeatRuleCollection extends AccessInnerDataCollection {
   public RepeatRuleCollection(WicletEx wiclet) {
      super(wiclet, RepeatRuleCompDef.getInstance());
      this.initFieldHandlers();
   }

   private void initFieldHandlers() {
      super._intFieldHandlers = new IntHashtable(3);
      super._intFieldHandlers.put(0, new RepeatRuleCollection$FrequencyHandler(null));
      super._intFieldHandlers.put(1, new RepeatRuleCollection$IntervalHandler(null));
      super._longFieldHandlers = new IntHashtable(1);
      super._longFieldHandlers.put(2, new RepeatRuleCollection$EndHandler(null));
   }

   @Override
   protected void loadItem(long dataHandle) {
      Recur recurrence = (Recur)this.getDBItemFromHandle(dataHandle);
      if (recurrence != null) {
         super._loadedItems.addElement(this.getHandle(dataHandle));
         this.loadItemFromModel(dataHandle, recurrence);
      }
   }

   private void loadItemFromModel(long dataHandle, Recur recurrence) {
      this.setIntFieldValue(dataHandle, 0, ((IntFieldHandler)super._intFieldHandlers.get(0)).getValue(recurrence));
      this.setIntFieldValue(dataHandle, 1, ((IntFieldHandler)super._intFieldHandlers.get(1)).getValue(recurrence));
      this.setLongFieldValue(dataHandle, 2, ((LongFieldHandler)super._longFieldHandlers.get(2)).getValue(recurrence));
   }

   @Override
   public Object getDBItemFromHandle(long dataHandle) {
      if (super._owners != null) {
         AccessInnerDataCollection$OwnerField owner = (AccessInnerDataCollection$OwnerField)super._owners.get(this.getHandle(dataHandle));
         if (owner != null) {
            DataCollection ownerDC = super._wiclet.getDataCollection((int)(owner._dataHandle >> 32));
            if (ownerDC instanceof EventCollection) {
               Object ownerInstance = ((EventCollection)ownerDC).getDBItemFromHandle(owner._dataHandle);
               if (ownerInstance instanceof Event) {
                  return ((Event)ownerInstance).getReadOnlyRecurrence();
               }
            }
         }
      }

      return null;
   }

   public void saveRepeatRuleToEvent(long rrHandle, Event event) {
      int recurFreq = this.getIntFieldValue(rrHandle, 0);
      if (recurFreq != 0) {
         Recur recur = event.getRecurrenceCopy();
         if (recur != null) {
            recur.setRecurType((byte)FrequencyEnumConverter.commonToDevice(recurFreq));
            recur.setRecurPeriod(this.getIntFieldValue(rrHandle, 1));
            recur.setEndDate(this.getLongFieldValue(rrHandle, 2));
            event.setRecurrence(recur);
         }
      }
   }
}
