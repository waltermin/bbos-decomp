package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.system.Display;
import net.rim.device.apps.internal.browser.util.Frame;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLDocument;

public final class ESWindow extends RedirectedObject {
   protected JavaScriptTimer _timer;
   protected ESLocation _location;
   protected JavaScriptEngine _scriptEngine;
   protected ESHTMLDocument _esDocument;
   private GlobalObject _globalObject;
   private ESFrames _esFrames;
   private Object _syncObject = null;

   public ESWindow(JavaScriptEngine engine, HTMLDocument doc, GlobalObject globalObject, RenderingSession renderingSession, Frame frame) {
      super("Window", GlobalObject.getInstance().getObjectPrototype());
      this._globalObject = globalObject;
      globalObject.setRedirectionObject(this);
      this._location = new ESLocation(engine, doc, frame);
      this._esDocument = new ESHTMLDocument(engine, doc, this._location);
      this._timer = new JavaScriptTimer(engine);
      this._scriptEngine = engine;
      String windowName = null;
      if (frame != null) {
         windowName = frame.getName();
      }

      long emptyString = Value.makeStringValue("");
      this.addFieldSpecial(globalObject, Names.blackberry, 29, Value.makeObjectValue(engine._blackberry));
      this.addFieldSpecial(globalObject, Names.closed, 29, Value.makeBooleanValue(false));
      this.addFieldSpecial(globalObject, Names.defaultStatus, 28, emptyString);
      this.addFieldSpecial(globalObject, Names.document, 29, Value.makeObjectValue(this._esDocument));
      this.addFieldSpecial(globalObject, Names.frames, 29, Value.makeObjectValue(this._esFrames = new ESFrames(this, frame)));
      if (frame != null) {
         frame.setESFrames(this._esFrames);
      }

      this.addFieldSpecial(globalObject, Names.history, 29, Value.makeObjectValue(new ESHistory(engine)));
      this.addFieldSpecial(globalObject, Names.innerHeight, 28, Value.makeIntegerValue(Display.getHeight()));
      this.addFieldSpecial(globalObject, Names.innerWidth, 28, Value.makeIntegerValue(Display.getWidth()));
      this.addFieldSpecial(globalObject, Names.location, 12, Value.makeObjectValue(this._location));
      this.addFieldSpecial(globalObject, Names.navigator, 29, Value.makeObjectValue(ESNavigator.getInstance(renderingSession)));
      this.addFieldSpecial(globalObject, Names.name, 28, JavaScriptEngine.makeStringValue(windowName));
      this.addFieldSpecial(globalObject, Names.offscreenBuffering, 28, Value.makeBooleanValue(false));
      this.addFieldSpecial(globalObject, Names.opener, 29, Value.NULL);
      this.addFieldSpecial(globalObject, Names.outerHeight, 28, Value.makeIntegerValue(Display.getHeight()));
      this.addFieldSpecial(globalObject, Names.outerWidth, 28, Value.makeIntegerValue(Display.getWidth()));
      this.addFieldSpecial(globalObject, Names.pageXOffset, 29, Value.makeIntegerValue(0));
      this.addFieldSpecial(globalObject, Names.pageYOffset, 29, Value.makeIntegerValue(0));
      this.addFieldSpecial(globalObject, Names.self, 29, Value.makeObjectValue(this));
      this.addFieldSpecial(globalObject, Names.screen, 29, Value.makeObjectValue(ESScreen.getScreen()));
      this.addFieldSpecial(globalObject, Names.screenX, 29, Value.makeIntegerValue(0));
      this.addFieldSpecial(globalObject, Names.screenY, 29, Value.makeIntegerValue(0));
      this.addFieldSpecial(globalObject, Names.status, 12, emptyString);
      this.addFieldSpecial(globalObject, Names.window, 29, Value.makeObjectValue(this));
      if (frame != null && frame.getParent() != null && frame.getParent().getESFrames() != null) {
         label64:
         try {
            ESWindow window = null;
            if (ESWindow$SameOriginPolicy.applies(frame, frame.getParent())) {
               window = frame.getParent().getESFrames().getWindow();
               this._globalObject.setParent(window.getGlobalObject());
               this._globalObject.addField("parent", 29, Value.makeObjectValue(window));
               this.addFieldSpecial(globalObject, Names.parent, 29, Value.makeObjectValue(window));
               this.addFieldSpecial(globalObject, Names.top, 29, window.getField(Names.top));
               this.setSyncObject(window.getSyncObject());
            } else {
               ESGhostObject parent = new ESGhostObject(frame.getParent(), 0);
               this._globalObject.addField("parent", 29, Value.makeObjectValue(parent));
               this.addFieldSpecial(globalObject, Names.parent, 29, Value.makeObjectValue(parent));
               this.setSyncObject(frame.getParent().getESFrames().getWindow().getSyncObject());
            }
         } finally {
            break label64;
         }
      } else {
         this._syncObject = new Object();
         this.addFieldSpecial(globalObject, Names.parent, 29, Value.makeObjectValue(this));
         this.addFieldSpecial(globalObject, Names.top, 29, Value.makeObjectValue(this));
      }

      globalObject.addHostFunction(new ESHTMLImageElementPrototype$Constructor());
      globalObject.addHostFunction(new ESHTMLOptionElementPrototype$Constructor());
      if (JavaScriptRegistry._ajaxed) {
         globalObject.addHostFunction(new ESXMLHttpRequestPrototype$Constructor());
      }

      ESWindowPrototype windowPrototype = engine._windowPrototype;
      HostFunction[] hfs = windowPrototype.getHostFunctions();

      for (int i = hfs.length - 1; i >= 0; i--) {
         globalObject.addHostFunction(hfs[i]);
      }

      globalObject.addHostFunction(new ESWindow$ToStringHostFunction());
   }

   public final ESHTMLDocument getDocument() {
      return this._esDocument;
   }

   public final ESLocation getLocation() {
      return this._location;
   }

   public final GlobalObject getGlobalObject() {
      return this._globalObject;
   }

   private final void addFieldSpecial(GlobalObject global, String name, int attrib, long value) {
      global.addField(name, attrib, value);
      this.addField(name, attrib, value);
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      try {
         if (name == Names.location) {
            this._location.notifyFieldChanged(Names.href, value);
            return false;
         }

         if (name == Names.status) {
            System.out.println(((StringBuffer)(new Object("Status "))).append(Convert.toString(value)).toString());
            return true;
         }
      } finally {
         return true;
      }

      return true;
   }

   @Override
   public final long requestFieldValue(String name) {
      long tmp = this._globalObject.noRedirectGetField(name);
      if (Value.getType(tmp) != 2) {
         return tmp;
      }

      if (name == Names.length) {
         return this._esFrames.requestFieldValue(name);
      }

      tmp = this.noRedirectGetField(name);
      if (tmp == Value.UNDEFINED) {
         tmp = this._esFrames.getChildFrame(name);
         if (tmp == Value.UNDEFINED) {
            return this._esDocument.getElementById(name);
         }
      }

      return tmp;
   }

   public final void setSyncObject(Object syncObject) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final Object getSyncObject() {
      return this._syncObject;
   }

   final void cleanup() {
      if (this._timer != null) {
         this._timer.shutdown();
         this._timer = null;
      }
   }

   public final Frame getFrame() {
      return this._esFrames.getFrame();
   }

   public final JavaScriptEngine getJavaScriptEngine() {
      return this._scriptEngine;
   }
}
