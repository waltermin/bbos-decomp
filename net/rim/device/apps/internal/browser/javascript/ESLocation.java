package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.api.UrlRequestedInternalEvent;
import net.rim.device.apps.internal.browser.util.Frame;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.RedirectedObject;
import org.w3c.dom.html2.HTMLDocument;

final class ESLocation extends RedirectedObject {
   private HTMLDocument _domDoc;
   private JavaScriptEngine _scriptEngine;
   private Frame _frame;

   public ESLocation(JavaScriptEngine engine, HTMLDocument doc, Frame frame) {
      this(engine, doc, frame, doc.getURL());
   }

   public ESLocation(JavaScriptEngine engine, HTMLDocument doc, Frame frame, String url) {
      this.setGrowthIncrement(11);
      this._scriptEngine = engine;
      this._frame = frame;

      label34:
      try {
         URI uri = (URI)(new Object(url));
         this.addField(Names.hash, 4, this.makeField(uri.getFragment(), '#'));
         String authority = uri.getAuthority();
         int colonIndex = authority.indexOf(58);
         String port;
         String host;
         if (colonIndex != -1) {
            port = authority.substring(colonIndex + 1);
            host = authority.substring(0, colonIndex);
         } else {
            port = "";
            host = authority;
         }

         this.addField(Names.host, 28, JavaScriptEngine.makeStringValue(host));
         this.addField(Names.hostname, 28, JavaScriptEngine.makeStringValue(authority));
         this.addField(Names.href, 12, JavaScriptEngine.makeStringValue(url));
         this.addField(Names.pathname, 28, JavaScriptEngine.makeStringValue(uri.getPath()));
         this.addField(Names.port, 28, JavaScriptEngine.makeStringValue(port));
         String protocol = uri.getScheme();
         if (protocol.charAt(protocol.length() - 1) != ':') {
            protocol = ((StringBuffer)(new Object())).append(protocol).append(':').toString();
         }

         this.addField(Names.protocol, 28, JavaScriptEngine.makeStringValue(protocol));
         this.addField(Names.search, 28, this.makeField(uri.getQuery(), '?'));
         this.addHostFunction(new ESLocation$ReloadHostFunction(this));
         this.addHostFunction(new ESLocation$ReplaceHostFunction(this));
         this.addHostFunction(new ESLocation$ToStringHostFunction(this));
      } finally {
         break label34;
      }

      this._domDoc = doc;
   }

   public final void setUserBasedAction(boolean userBasedAction) {
      this._scriptEngine.setUserBasedAction(userBasedAction);
   }

   private final long makeField(String value, char startChar) {
      if (value != null && value.length() > 0) {
         value = ((StringBuffer)(new Object())).append(startChar).append(value).toString();
      }

      return JavaScriptEngine.makeStringValue(value);
   }

   final String resolveURL(String url) {
      if (url != null) {
         String oldUrl = this._domDoc.getURL();
         return URI.getAbsoluteURL(url, oldUrl);
      } else {
         return url;
      }
   }

   final void loadPage(String url, long clickID) {
      BrowserContent content = JavaScriptEngine.getInstance()._browserContent;
      RenderingApplication renderingApplication = content.getRenderingApplication();
      if (url != null && renderingApplication != null) {
         int flags = 1;
         if (content instanceof Object) {
            flags |= ((BrowserContentBaseImpl)content).getSharedFlags();
         }

         UrlRequestedEvent event = UrlRequestedInternalEvent.processUrlRequest(
            content, url, this._frame != null ? (this._frame.getParent() == null ? "_top" : this._frame.getName()) : null, null, null, true, flags, clickID
         );
         if (event != null) {
            renderingApplication.eventOccurred(event);
         }
      }
   }

   final void redirect(String url) {
      BrowserContent content = JavaScriptEngine.getInstance()._browserContent;
      RenderingApplication renderingApplication = content.getRenderingApplication();
      if (url != null && renderingApplication != null) {
         renderingApplication.eventOccurred((Event)(new Object(content, this._domDoc.getURL(), 2)));
         renderingApplication.eventOccurred((Event)(new Object(content, url, null, 2)));
      }
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      try {
         if (name == Names.href) {
            if (!this._scriptEngine.isUserBasedAction()) {
               this.redirect(Convert.toString(value));
               return true;
            }

            this.loadPage(Convert.toString(value), this._scriptEngine.getClickID());
         }
      } finally {
         return true;
      }

      return true;
   }
}
