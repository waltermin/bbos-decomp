package net.rim.device.apps.api.framework.verb;

import net.rim.device.api.system.Application;

public class RunnableVerbWrapper implements Runnable {
   private Application _app;
   private Verb _verbToInvoke;
   private Object _invocationParameter;
   private boolean _requestForeground;

   public RunnableVerbWrapper(Application app, Verb verbToInvoke, Object invocationParameter, boolean requestForeground) {
      this._app = app;
      this._verbToInvoke = verbToInvoke;
      this._invocationParameter = invocationParameter;
      this._requestForeground = requestForeground;
   }

   @Override
   public void run() {
      if (this._requestForeground) {
         this._app.requestForeground();
      }

      this._verbToInvoke.invoke(this._invocationParameter);
   }
}
