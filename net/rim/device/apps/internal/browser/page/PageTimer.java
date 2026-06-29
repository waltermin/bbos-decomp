package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

public class PageTimer implements Runnable {
   protected int _value;
   protected BrowserContentImpl _browserContent;
   protected int _id;
   protected long _startTime;
   protected boolean _started;
   protected String _prompt;
   protected Application _invokeApp;

   public PageTimer(int value, BrowserContentImpl browserContent) {
      this._value = value;
      this._browserContent = browserContent;
   }

   public void setPrompt(String prompt) {
      this._prompt = prompt;
   }

   public void start() {
      if (!this._started) {
         int time = 0;
         if (this._value >= 0) {
            time = this._value * 1000;
            this._startTime = System.currentTimeMillis();
            this._invokeApp = Application.getApplication();
            if (this._invokeApp != null) {
               this._started = true;
               if (time == 0) {
                  this._invokeApp.invokeLater(this);
                  return;
               }

               this._id = this._invokeApp.invokeLater(this, time, false);
            }
         }
      }
   }

   public void stop() {
      if (this._started) {
         this._invokeApp.cancelInvokeLater(this._id);
         this._started = false;
         this._invokeApp = null;
      }
   }

   @Override
   public void run() {
      if (this._started) {
         this._started = false;
         if (this._prompt != null && Dialog.ask(3, this._prompt) == -1) {
            return;
         }

         this._browserContent.invokeOnTimer();
      }
   }
}
