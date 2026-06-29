package net.rim.device.apps.internal.bis.launch.protocol;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.Arrays;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLToObjectHandler extends DefaultHandler {
   protected Hashtable _elementToObjectMap = new Hashtable();
   protected Hashtable _elementToHandlerMap = new Hashtable();
   private String _startTag;
   private String[] _requiredElements;
   private boolean _allowNotRequiredElements;
   private String _currentTag;
   private String _currentValue;
   private XMLToObjectHandler _currentHandler;
   private String _currentHandlerTag;

   public XMLToObjectHandler(String startTag, String[] requiredElements, boolean allowNotRequiredElements) {
      this._startTag = startTag;
      this._requiredElements = requiredElements;
      this._allowNotRequiredElements = allowNotRequiredElements;
   }

   public void setElementHandler(String element, XMLToObjectHandler handler) {
      this._elementToHandlerMap.put(element, handler);
   }

   public Object getResult() {
      return this._elementToObjectMap;
   }

   protected boolean isFinished(String closedTag) {
      return closedTag.equals(this._startTag);
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      if (this._currentHandler != null) {
         this._currentHandler.characters(ch, start, length);
      } else {
         this._currentValue = ch != null && ch.length > 0 ? new String(ch, start, length) : null;
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (this._currentTag == null) {
         if (!this._startTag.equals(qName)) {
            throw new SAXException("Encountered unexpected element during parsing: " + qName);
         }

         this._currentTag = qName;
      } else {
         this._currentTag = qName;
         if (this._currentHandler != null) {
            this._currentHandler.startElement(uri, localName, qName, attributes);
         } else {
            if (!this._allowNotRequiredElements && !Arrays.contains(this._requiredElements, qName)) {
               throw new SAXException("Encountered unexpected element during parsing" + qName);
            }

            XMLToObjectHandler elementHandler = (XMLToObjectHandler)this._elementToHandlerMap.get(this._currentTag);
            if (elementHandler != null) {
               this._currentHandler = elementHandler;
               this._currentHandlerTag = this._currentTag;
               this._currentHandler.startElement(uri, localName, qName, attributes);
            }
         }
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (this._currentHandler != null) {
         this._currentHandler.endElement(uri, localName, qName);
         if (this._currentHandler.isFinished(qName)) {
            Object handlerResult = this._currentHandler.getResult();
            Object existingValue = this._elementToObjectMap.get(this._currentHandlerTag);
            if (existingValue != null) {
               if (!(existingValue instanceof Vector)) {
                  Vector valueList = new Vector();
                  valueList.addElement(existingValue);
                  valueList.addElement(handlerResult);
                  this._elementToObjectMap.put(this._currentHandlerTag, valueList);
               } else {
                  ((Vector)existingValue).addElement(handlerResult);
               }
            } else if (handlerResult != null) {
               this._elementToObjectMap.put(this._currentHandlerTag, handlerResult);
            }

            this._currentHandler.reset();
            this._currentHandler = null;
            this._currentHandlerTag = null;
            return;
         }
      } else {
         if (this.isFinished(qName)) {
            return;
         }

         Object existingValue = this._elementToObjectMap.get(this._currentTag);
         if (existingValue != null) {
            if (!(existingValue instanceof Vector)) {
               Vector valueList = new Vector();
               valueList.addElement(existingValue);
               valueList.addElement(this._currentValue);
               this._elementToObjectMap.put(this._currentTag, valueList);
               return;
            }

            ((Vector)existingValue).addElement(this._currentValue);
            return;
         }

         if (this._currentValue != null) {
            this._elementToObjectMap.put(this._currentTag, this._currentValue);
            this._currentValue = null;
         }
      }
   }

   public void reset() {
      this._elementToObjectMap.clear();
   }

   public static String getStringValue(Hashtable hashtable, Object key) {
      return (String)hashtable.get(key);
   }
}
