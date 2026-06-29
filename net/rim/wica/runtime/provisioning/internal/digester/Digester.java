package net.rim.wica.runtime.provisioning.internal.digester;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.xml.jaxp.WBXMLParser;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.wica.runtime.provisioning.internal.CodeBook;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class Digester extends DefaultHandler {
   protected StringBuffer _bodyText;
   protected PeekableStack _bodyTexts;
   protected CodeBook _codebook;
   protected ErrorHandler _errorHandler = null;
   private EntityResolver _entityResolver;
   protected String _match = "";
   protected Object _root = null;
   protected Hashtable _rules;
   protected PeekableStack _stack;
   private Rule _invokeParentChildRule;
   private Rule _setAttributesRule;
   protected static final int DEBUG = 0;
   protected static SAXParserFactory factory = null;

   public Digester() {
      this._bodyTexts = new PeekableStack();
      this._rules = (Hashtable)(new Object());
      this._stack = new PeekableStack();
      this._bodyText = (StringBuffer)(new Object());
   }

   public Digester(CodeBook codebook) {
      this();
      this.setCodebook(codebook);
   }

   public void addInvokeElementBodyOnParentRule(String pattern) {
      this.addRule(pattern, new InvokeElementBodyOnParentRule(pattern, this));
   }

   public void addInvokeParentChildRule(String pattern) {
      if (this._invokeParentChildRule == null) {
         this._invokeParentChildRule = new InvokeParentChildRule(this);
      }

      this.addRule(pattern, this._invokeParentChildRule);
   }

   public void addObjectCreate(String pattern, Class clazz) {
      this.addRule(pattern, new ObjectCreateRule(this, clazz));
   }

   public void addRule(String pattern, Rule rule) {
      Vector list = (Vector)this._rules.get(pattern);
      if (list == null) {
         list = (Vector)(new Object(4));
         this._rules.put(pattern, list);
      }

      list.addElement(rule);
   }

   public void addSetAttributesRule(String pattern) {
      if (this._setAttributesRule == null) {
         this._setAttributesRule = new SetAttributesRule(this);
      }

      this.addRule(pattern, this._setAttributesRule);
   }

   @Override
   public void characters(char[] buffer, int start, int length) {
      this._bodyText.append(buffer, start, length);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void endDocument() {
      if (this.getCount() > 1) {
      }

      while (this.getCount() > 1) {
         this.pop();
      }

      Enumeration keys = this._rules.keys();

      while (keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         Vector vRules = (Vector)this._rules.get(key);
         Rule r = null;
         int i = 0;

         for (int j = vRules.size(); i < j; i++) {
            r = (Rule)vRules.elementAt(i);

            try {
               r.finish();
            } catch (Throwable var9) {
               throw new Object(e.getMessage());
            }
         }
      }

      this.clear();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void endElement(String namespaceURI, String localName, String qName) {
      Vector vRules = this.getRules(this._match);
      if (vRules != null) {
         String lBodyText = this._bodyText.toString();
         Rule r = null;
         int i = 0;

         for (int j = vRules.size(); i < j; i++) {
            r = (Rule)vRules.elementAt(i);

            try {
               r.body(lBodyText);
            } catch (Throwable var15) {
               throw new Object(e.getMessage());
            }
         }
      }

      this._bodyText = (StringBuffer)this._bodyTexts.pop();
      if (vRules != null) {
         Rule r = null;

         for (int i = vRules.size() - 1; i >= 0; i--) {
            r = (Rule)vRules.elementAt(i);

            try {
               r.end();
            } catch (Throwable var14) {
               throw new Object(e.getMessage());
            }
         }
      }

      int slash = this._match.lastIndexOf(47);
      if (slash >= 0) {
         this._match = this._match.substring(0, slash);
      } else {
         this._match = "";
      }
   }

   @Override
   public void error(SAXParseException exception) {
      if (this._errorHandler != null) {
         this._errorHandler.error(exception);
      }
   }

   @Override
   public void fatalError(SAXParseException exception) {
      if (this._errorHandler != null) {
         this._errorHandler.fatalError(exception);
      }
   }

   @Override
   public InputSource resolveEntity(String publicId, String systemId) {
      return this._entityResolver != null ? this._entityResolver.resolveEntity(publicId, systemId) : null;
   }

   public CodeBook getCodebook() {
      return this._codebook;
   }

   public ErrorHandler getErrorHandler() {
      return this._errorHandler;
   }

   public SAXParser getParser() {
      try {
         if (factory == null) {
            factory = SAXParserFactory.newInstance();
         }

         factory.setAllowUndefinedNamespaces(true);
         return factory.newSAXParser();
      } finally {
         ;
      }
   }

   public Object getRoot() {
      return this._root;
   }

   public boolean hasCodeBook() {
      return this.getCodebook() != null;
   }

   public Object parse(InputStream input) {
      if (this.hasCodeBook()) {
         WBXMLParser binaryParser = (WBXMLParser)(new Object(input));
         binaryParser.setTagTable(0, this._codebook.getTags());
         binaryParser.setAttrStartTable(0, this._codebook.getAttribs());
         binaryParser.setAttrValueTable(0, this._codebook.getValues());
         binaryParser.parse(this);
      } else {
         this.getParser().parse(input, this);
      }

      return this._root;
   }

   public Object peek() {
      try {
         return this._stack.peek();
      } finally {
         ;
      }
   }

   public Object peek(int n) {
      try {
         return this._stack.peek(n);
      } finally {
         ;
      }
   }

   public Object pop() {
      try {
         return this._stack.pop();
      } finally {
         ;
      }
   }

   public void push(Object object) {
      if (this._stack.size() == 0) {
         this._root = object;
      }

      this._stack.push(object);
   }

   public void setCodebook(CodeBook codebook) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setErrorHandler(ErrorHandler errorHandler) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setEntityResolver(EntityResolver entityResolver) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void startElement(String namespaceURI, String localName, String qName, Attributes list) {
      this._bodyTexts.push(this._bodyText);
      this._bodyText.setLength(0);
      if (this._match.length() > 0) {
         this._match = ((StringBuffer)(new Object())).append(this._match).append("/").append(localName).toString();
      } else {
         this._match = localName;
      }

      Vector vRules = this.getRules(this._match);
      if (vRules != null) {
         Rule r = null;
         int i = 0;

         for (int j = vRules.size(); i < j; i++) {
            r = (Rule)vRules.elementAt(i);

            try {
               r.begin(list);
            } catch (Throwable var11) {
               throw new Object(e.getMessage());
            }
         }
      }
   }

   @Override
   public String toString() {
      StringBuffer buf = (StringBuffer)(new Object(this.getClass().getName()));
      if (!this._stack.isEmpty()) {
         buf.append("[stack=").append(this._stack.peek().toString()).append(']');
      }

      return buf.toString();
   }

   @Override
   public void warning(SAXParseException exception) {
      if (this._errorHandler != null) {
         this._errorHandler.warning(exception);
      }
   }

   private void clear() {
      this._match = "";
      this._bodyTexts.clear();
      this._stack.clear();
   }

   private int getCount() {
      return this._stack.size();
   }

   private Vector getRules(String rule) {
      Vector rulesList = (Vector)this._rules.get(rule);
      if (rulesList == null) {
         int lastIndexOfSlash = rule.lastIndexOf(47);
         String revisedRule = rule.substring(lastIndexOfSlash + 1, rule.length());
         rulesList = (Vector)this._rules.get(revisedRule);
      }

      return rulesList;
   }
}
