package net.rim.device.api.xml.jaxp;

import java.io.InputStream;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

public class DocumentBuilderImpl extends DocumentBuilder {
   private boolean _isNamespaceAware = true;
   private boolean _isCoalescing;
   private boolean _isIgnoringComments;
   private boolean _isExpandEntityReferences;
   private boolean _isIgnoringElementContentWhitespace;
   private EntityResolver _entityResolver;
   private ErrorHandler _errorHandler;
   private boolean _allowUndefinedNamespaces;

   @Override
   public boolean getAllowUndefinedNamespaces() {
      return this._allowUndefinedNamespaces;
   }

   @Override
   public void setAllowUndefinedNamespaces(boolean allowUndefinedNamespaces) {
      this._allowUndefinedNamespaces = allowUndefinedNamespaces;
   }

   DOMInternalRepresentation newIR() {
      DOMInternalRepresentation ir = new DOMInternalRepresentation();
      ir.setNamespaceAware(this._isNamespaceAware);
      ir.setCoalescing(this._isCoalescing);
      ir.setIgnoringComments(this._isIgnoringComments);
      ir.setExpandingEntities(this._isExpandEntityReferences);
      ir.setIgnoringWhitespace(this._isIgnoringElementContentWhitespace);
      ir.setEntityResolver(this._entityResolver);
      ir.setErrorHandler(this._errorHandler);
      return ir;
   }

   DocumentBuilderImpl(DocumentBuilderFactory factory) {
      this._isCoalescing = factory.isCoalescing();
      this._isIgnoringComments = factory.isIgnoringComments();
      this._isExpandEntityReferences = factory.isExpandEntityReferences();
      this._isIgnoringElementContentWhitespace = factory.isIgnoringElementContentWhitespace();
      this._entityResolver = null;
   }

   @Override
   public Document parse(InputSource is) {
      SAXParserImpl sax = new SAXParserImpl();
      sax.setAllowUndefinedNamespaces(this._allowUndefinedNamespaces);
      sax.setNamespaceAware(this._isNamespaceAware);
      return this.newIR().parse(is, sax);
   }

   public Document parse(InputStream is, IntHashtable tagTables, IntHashtable attrStartTables, IntHashtable attrTables) {
      SAXParserImpl sax = new SAXParserImpl();
      sax.setAllowUndefinedNamespaces(this._allowUndefinedNamespaces);
      sax.setNamespaceAware(this._isNamespaceAware);
      return this.newIR().parse(is, sax, tagTables, attrStartTables, attrTables);
   }

   @Override
   public boolean isNamespaceAware() {
      return this._isNamespaceAware;
   }

   public void setNamespaceAware(boolean aware) {
      this._isNamespaceAware = aware;
   }

   @Override
   public boolean isValidating() {
      return false;
   }

   @Override
   public void setEntityResolver(EntityResolver er) {
      this._entityResolver = er;
   }

   @Override
   public void setErrorHandler(ErrorHandler eh) {
      this._errorHandler = eh;
   }

   @Override
   public Document newDocument() {
      DOMInternalRepresentation ir = new DOMInternalRepresentation();
      ir.setNamespaceAware(this._isNamespaceAware);
      ir.addDocument();
      return (Document)ir.getNode(ir.getDocument());
   }

   @Override
   public DOMImplementation getDOMImplementation() {
      return DOMImplementationImpl.getInstance();
   }
}
