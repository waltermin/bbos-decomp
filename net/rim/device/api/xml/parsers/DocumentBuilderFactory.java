package net.rim.device.api.xml.parsers;

import net.rim.device.api.xml.jaxp.DocumentBuilderFactoryImpl;

public class DocumentBuilderFactory {
   private boolean _isNamespaceAware = true;
   private boolean _expandEntityReferences = true;
   private boolean _isValidating = false;
   private boolean _isCoalescing = false;
   private boolean _ignoreComments = false;
   private boolean _ignoreWhitespace = false;
   private boolean _allowUndefinedNamespaces = false;

   protected DocumentBuilderFactory() {
   }

   public static DocumentBuilderFactory newInstance() {
      return new DocumentBuilderFactoryImpl();
   }

   public DocumentBuilder newDocumentBuilder() {
      throw null;
   }

   public void setNamespaceAware(boolean aware) {
      this._isNamespaceAware = aware;
   }

   public boolean isNamespaceAware() {
      return this._isNamespaceAware;
   }

   public void setValidating(boolean validate) {
      this._isValidating = validate;
   }

   public boolean isValidating() {
      return this._isValidating;
   }

   public void setIgnoringElementContentWhitespace(boolean ignore) {
      this._ignoreWhitespace = ignore;
   }

   public boolean isIgnoringElementContentWhitespace() {
      return this._ignoreWhitespace;
   }

   public void setExpandEntityReferences(boolean expand) {
      this._expandEntityReferences = expand;
   }

   public boolean isExpandEntityReferences() {
      return this._expandEntityReferences;
   }

   public void setIgnoringComments(boolean ignoreComments) {
      this._ignoreComments = ignoreComments;
   }

   public boolean isIgnoringComments() {
      return this._ignoreComments;
   }

   public void setCoalescing(boolean coalesce) {
      this._isCoalescing = coalesce;
   }

   public boolean isCoalescing() {
      return this._isCoalescing;
   }

   public void setAllowUndefinedNamespaces(boolean allow) {
      this._allowUndefinedNamespaces = allow;
   }

   public boolean getAllowUndefinedNamespaces() {
      return this._allowUndefinedNamespaces;
   }
}
