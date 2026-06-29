package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Display;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLDocument;

final class ESHTMLDocument extends ESNode {
   private JavaScriptEngine _scriptEngine;
   protected HTMLDocument _domDoc;
   private ESLocation _location;

   public ESHTMLDocument(JavaScriptEngine engine, HTMLDocument doc, ESLocation location) {
      super(doc, Names.Document, engine._documentPrototype);
      this.setGrowthIncrement(19);
      this._domDoc = doc;
      this._scriptEngine = engine;
      this._location = location;
      ESObject emptyArray = new ESObject();
      emptyArray.setGrowthIncrement(1);
      emptyArray.addField(Names.length, 29, Value.makeIntegerValue(0));
      this.addField(Names.anchors, 29, Value.makeObjectValue(new ESCollection(doc.getAnchors())));
      this.addField(Names.applets, 29, Value.makeObjectValue(new ESCollection(doc.getApplets())));
      this.addField(Names.classes, 29, Value.makeObjectValue(new ESObject()));
      this.addField(Names.domain, 29, JavaScriptEngine.makeStringValue(doc.getDomain()));
      this.addField(Names.embeds, 29, Value.makeObjectValue(emptyArray));
      this.addField(Names.forms, 29, Value.makeObjectValue(new ESCollection(doc.getForms())));
      this.addField(Names.height, 29, Value.makeIntegerValue(Display.getHeight()));
      this.addField(Names.ids, 29, Value.makeObjectValue(new ESObject()));
      this.addField(Names.images, 29, Value.makeObjectValue(new ESCollection(doc.getImages())));
      this.addField(Names.lastModified, 29, Value.makeIntegerValue(0));
      this.addField(Names.links, 29, Value.makeObjectValue(new ESCollection(doc.getLinks())));
      this.addField(Names.location, 12, Value.makeObjectValue(location));
      this.addField(Names.plugins, 29, Value.makeObjectValue(emptyArray));
      this.addField(Names.referrer, 28, JavaScriptEngine.makeStringValue(doc.getReferrer()));
      this.addField(Names.tags, 28, Value.makeObjectValue(new ESObject()));
      this.addField(Names.URL, 12, Value.makeStringValue(doc.getURL()));
      this.addField(Names.width, 29, Value.makeIntegerValue(Display.getWidth()));
      long tempValue = Value.makeIntegerValue(0);
      this.addField(Names.alinkColor, 28, tempValue);
      this.addField(Names.bgColor, 28, tempValue);
      this.addField(Names.fgColor, 28, tempValue);
      this.addField(Names.linkColor, 28, tempValue);
      this.addField(Names.vlinkColor, 28, tempValue);
      this.addField(Names.docType, 29, engine.lookupElementToESObjectLong(doc.getDoctype()));
      this.addField(Names.implementation, 29, Value.makeObjectValue(new ESDOMImplementation(doc)));
      this.addField(Names.documentElement, 29, engine.lookupElementToESObjectLong(doc.getDocumentElement()));
      this.setGrowthIncrement(1);
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      try {
         if (name == Names.cookie) {
            this._domDoc.setCookie(Convert.toString(value));
         } else {
            if (name == Names.location || name == Names.URL) {
               this._location.redirect(Convert.toString(value));
               return false;
            }

            if (name == Names.title) {
               this._domDoc.setTitle(Convert.toString(value));
               return true;
            }
         }
      } finally {
         return true;
      }

      return true;
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.title) {
         return JavaScriptEngine.makeStringValue(this._domDoc.getTitle());
      }

      if (name == Names.cookie) {
         return JavaScriptEngine.makeStringValue(this._domDoc.getCookie());
      }

      if (name == Names.body) {
         return this._scriptEngine.lookupElementToESObjectLong(this._domDoc.getBody());
      }

      long result = this.noRedirectGetField(name);
      if (result == Value.UNDEFINED) {
         result = this.getElementById(name);
         if (result != Value.DEFAULT && result != Value.UNDEFINED) {
            this.addField(name, 29, result);
            return result;
         }
      }

      return super.requestFieldValue(name);
   }

   final long getElementById(String name) {
      NodeList list = this._domDoc.getElementsByName(name);
      return list != null && list.getLength() != 0 ? this._scriptEngine.lookupElementToESObjectLong(list) : Value.DEFAULT;
   }

   public final JavaScriptEngine getEngine() {
      return this._scriptEngine;
   }

   public final ESLocation getLocation() {
      return this._location;
   }
}
