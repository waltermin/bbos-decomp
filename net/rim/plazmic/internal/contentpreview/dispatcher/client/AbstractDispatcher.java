package net.rim.plazmic.internal.contentpreview.dispatcher.client;

import net.rim.plazmic.internal.contentpreview.dispatcher.Dispatcher;
import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherServiceException;
import net.rim.plazmic.internal.contentpreview.dispatcher.NoSuchSessionException;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.DequeueThemeActivationRequest;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.DequeueThemeRegistrationRequest;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.DispatcherServiceFailure;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.GetControlPanelPort;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.GetPlaybackCommandPort;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.GetSpecificSession;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.LogMessage;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.Model;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.NoSuchSession;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.SessionOk;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.SessionPort;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.SessionReady;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.ThemeRequest;
import net.rim.plazmic.internal.contentpreview.dispatcher.message.VoidMessage;

public class AbstractDispatcher implements Dispatcher {
   public static final String rcsid;

   protected Model request(Model _1) {
      throw null;
   }

   @Override
   public int getControlPanelPort(String sessionName) {
      Model response = this.request(new GetControlPanelPort(sessionName));
      if (!(response instanceof SessionPort)) {
         if (!(response instanceof NoSuchSession)) {
            throw createServiceException(response);
         }

         String msg = ((NoSuchSession)response).getMessage();
         throw new NoSuchSessionException(msg);
      } else {
         return ((SessionPort)response).getPort();
      }
   }

   @Override
   public int getPlaybackCommandPort(String sessionName) {
      Model response = this.request(new GetPlaybackCommandPort(sessionName));
      if (!(response instanceof SessionPort)) {
         if (!(response instanceof NoSuchSession)) {
            throw createServiceException(response);
         }

         String msg = ((NoSuchSession)response).getMessage();
         throw new NoSuchSessionException(msg);
      } else {
         return ((SessionPort)response).getPort();
      }
   }

   public String sessionReady(int pin) {
      Model response = this.request(new SessionReady(pin));
      if (!(response instanceof SessionOk)) {
         if (!(response instanceof NoSuchSession)) {
            throw createServiceException(response);
         }

         String msg = ((NoSuchSession)response).getMessage();
         throw new NoSuchSessionException(msg);
      } else {
         return ((SessionOk)response).getSessionName();
      }
   }

   @Override
   public void log(String sessionName, int type, String message, String[] data) {
      Model response = this.request(new LogMessage(sessionName, type, message, data));
      if (!(response instanceof VoidMessage)) {
         if (!(response instanceof NoSuchSession)) {
            throw createServiceException(response);
         }

         String msg = ((NoSuchSession)response).getMessage();
         throw new NoSuchSessionException(msg);
      }
   }

   public String dequeueThemeRegistrationRequest(int pin) {
      Model response = this.request(new DequeueThemeRegistrationRequest(pin));
      if (!(response instanceof ThemeRequest)) {
         throw createServiceException(response);
      } else {
         return ((ThemeRequest)response).getThemeName();
      }
   }

   public String dequeueThemeActivationRequest(int pin) {
      Model response = this.request(new DequeueThemeActivationRequest(pin));
      if (!(response instanceof ThemeRequest)) {
         throw createServiceException(response);
      } else {
         return ((ThemeRequest)response).getThemeName();
      }
   }

   @Override
   public String getSession(int pin) {
      Model response = this.request(new GetSpecificSession(pin));
      if (!(response instanceof SessionOk)) {
         if (!(response instanceof NoSuchSession)) {
            throw createServiceException(response);
         }

         String msg = ((NoSuchSession)response).getMessage();
         throw new NoSuchSessionException(msg);
      } else {
         return ((SessionOk)response).getSessionName();
      }
   }

   protected static DispatcherServiceException createServiceException(Model response) {
      String msg;
      if (!(response instanceof DispatcherServiceFailure)) {
         msg = ((StringBuffer)(new Object("unexpected response "))).append(response).toString();
      } else {
         msg = ((DispatcherServiceFailure)response).getMessage();
      }

      return new DispatcherServiceException(msg);
   }
}
