package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.apps.api.framework.verb.Verb;

public class EventVerb extends Verb {
   private Event _event;
   private RenderingApplication _renderingApplication;

   public EventVerb(Event event, RenderingApplication renderingApplication) {
      super(0);
      this._event = event;
      this._renderingApplication = renderingApplication;
   }

   @Override
   public Object invoke(Object parameter) {
      if (this._renderingApplication != null) {
         this._renderingApplication.eventOccurred(this._event);
      }

      return null;
   }
}
