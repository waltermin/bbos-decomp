package net.rim.device.apps.internal.mms.verbs;

import java.io.ByteArrayInputStream;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventInstance;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.model.MMSAttachmentImpl;

public class MMSSendVCalVerb extends MMSComposeVerb implements UniqueIDProvider, DefaultProvider {
   private int _callingProcessId;
   private MMSPresentationModel _presentation;
   private static final long UNIQUE_KEY;

   public MMSSendVCalVerb() {
      this(1267024, 48);
   }

   @Override
   public String toString(Object context) {
      return this._presentation == null ? super.toString(context) : CommonResources.getString(800);
   }

   public MMSSendVCalVerb(int menuOrdering, int stringID) {
      super(menuOrdering, stringID);
   }

   public void setPresentationModel(MMSPresentationModel presentation) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public long getLUID(Object context) {
      return 1270373357196762926L;
   }

   @Override
   public Object invoke(Object context) {
      if (context instanceof Object) {
         ContextObject co = (ContextObject)context;
         Object o = ContextObject.get(co, 3696141428889703675L);
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         Converter converter = (Converter)ar.get(-4508956498379585150L);
         if (converter != null && converter.canConvert(o)) {
            label46:
            try {
               CalendarProxy.closeCalendar();
               byte[] data = converter.convert(o, co);
               String name = null;
               if (o instanceof Object) {
                  EventInstance ee = (EventInstance)o;
                  name = ((StringBuffer)(new Object())).append(ee.getEventInstance().getSubject()).append(".vcs").toString();
                  ContextObject.put(context, -4886909117188079897L, name);
               }

               if (this._presentation != null) {
                  int type = MMSUtilities.getMIMEType("text/x-vcalendar");
                  MMSAttachment attachment = new MMSAttachmentImpl(name, type, data, null);
                  if (attachment != null) {
                     this._presentation.addPresentationElement(attachment, true);
                     if (this._presentation instanceof Object) {
                        Manager mgr = (Manager)this._presentation;
                        mgr.setDirty(true);
                     }
                  }
               } else {
                  ContextObject.put(co, -4241241545455759532L, "text/x-vcalendar");
                  ByteArrayInputStream inputStream = (ByteArrayInputStream)(new Object(data));
                  ContextObject.put(co, 5473606008898265655L, inputStream);
                  super.invoke(co);
               }
            } finally {
               break label46;
            }

            ApplicationManager.getApplicationManager().requestForeground(this._callingProcessId);
            this._callingProcessId = 0;
            return null;
         }
      }

      return null;
   }

   @Override
   public Object getDefault(Object current, Object context) {
      return this._presentation == null ? null : this;
   }

   @Override
   public Object updateDefault(Object newdefault, Object context) {
      return null;
   }
}
