package net.rim.device.apps.internal.browser.wml;

import net.rim.device.apps.api.framework.model.ContextObject;

class Task {
   WMLBrowserContent _browserContent;
   WMLVariable _label;
   protected WMLContextManager _contextManager;
   private SetVar _setVar;

   Task(WMLBrowserContent browserContent, WMLContextManager context) {
      this._browserContent = browserContent;
      this._contextManager = context;
   }

   void setVerbLabel(WMLVariable label) {
      this._label = label;
   }

   void addSetVar(WMLVariable name, WMLVariable value) {
      if (this._setVar == null) {
         this._setVar = new SetVar(this._contextManager);
      }

      this._setVar.add(name, value);
   }

   void setBrowserContent(WMLBrowserContent browserContent) {
      this._browserContent = browserContent;
   }

   WMLBrowserContent getBrowserContent() {
      return this._browserContent;
   }

   void invoke(Object context) {
      boolean programmatic = false;
      if (context instanceof Object) {
         ContextObject contextObject = (ContextObject)context;
         programmatic = contextObject.getFlag(64);
      }

      if (this._browserContent.submit(!programmatic, null)) {
         String url = this.getURL();
         if (url != null) {
            if (this._setVar != null) {
               this._setVar.invoke();
            }

            this.loadPage(url, context);
         }
      }
   }

   protected String getURL() {
      throw null;
   }

   protected void loadPage(String _1, Object _2) {
      throw null;
   }
}
