package net.rim.device.apps.api.vcal;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.vm.Array;

public final class VCal implements VerbProvider, PaintProvider {
   private Object _vcalObject;
   private VCal$OpenVcal _verb;
   public static final String MIME_TYPE_VCALENDAR = "text/x-vcalendar";
   public static final String MIME_TYPE_ICALENDAR = "text/calendar";
   public static final String MIME_SUBTYPE_ICALENDAR = "calendar";
   public static final String MIME_SUBTYPE_VCALENDAR = "x-vcalendar";

   public static final void register() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      VCalToEventConverter vCalToEventConverter = new VCalToEventConverter();
      ar.put(7277824740467201558L, vCalToEventConverter);
      EventToVCalBuilder eventToVCalConverter = new EventToVCalBuilder();
      ar.put(-4508956498379585150L, eventToVCalConverter);
   }

   VCal(Object vcalObject) {
      this._vcalObject = vcalObject;
      if (this._vcalObject instanceof Event || this._vcalObject instanceof TaskModel) {
         this._verb = new VCal$OpenVcal(this._vcalObject);
      }
   }

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      if (this._vcalObject instanceof Event) {
         Event e = (Event)this._vcalObject;
         String subject = e.getSubject();
         if ((subject == null || subject.trim().length() == 0) && context instanceof String) {
            subject = (String)context;
         }

         g.drawText(subject, x, y);
      }

      return 1;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (verbs != null) {
         Array.resize(verbs, verbs.length + 1);
         verbs[verbs.length - 1] = this._verb;
      }

      return this._verb;
   }
}
