package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import java.util.Vector;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.api.util.StringUtilities;
import org.xml.sax.Attributes;

class GranularPolicyElement {
   protected String _xmlTag;
   protected GranularPolicyElement _parentElement;
   protected SimpleSortingVector _childElements;
   protected Comparator _childElementComparator;
   protected static final boolean DEBUG = true;

   protected GranularPolicyElement(String xmlTag, GranularPolicyElement parentElement) {
      this._xmlTag = xmlTag;
      this._parentElement = parentElement;
      this._childElements = (SimpleSortingVector)(new Object());
      this._childElements.setSortComparator(new GranularPolicyElement$GranularPolicyComparator(null));
      this._childElements.setSort(true);
   }

   public final String getXMLTag() {
      return this._xmlTag;
   }

   public GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      return new GranularPolicyIgnoredElement(localName, this);
   }

   public GranularPolicyElement handleEndElement(String localName, StringBuffer characterAccumulator) {
      if (StringUtilities.strEqualIgnoreCase(localName, this.getXMLTag())) {
         this._parentElement.addChildElement(this);
         return this._parentElement;
      }

      if (characterAccumulator.length() > 0) {
         this.addProperty(localName, characterAccumulator);
      }

      return this;
   }

   public void addProperty(String name, StringBuffer value) {
   }

   public void addChildElement(GranularPolicyElement childElement) {
      this._childElements.addElement(childElement);
   }

   public final Vector getChildElements() {
      return this._childElements;
   }

   public final void debugPrint(int indent) {
      for (int i = 0; i < indent; i++) {
         System.out.print("  ");
      }

      System.out.println(this.getDebugPrintInfo());
      indent++;
      int numChildElements = this._childElements.size();

      for (int i = 0; i < numChildElements; i++) {
         ((GranularPolicyElement)this._childElements.elementAt(i)).debugPrint(indent);
      }
   }

   public String getDebugPrintInfo() {
      return this.getXMLTag();
   }

   protected static final boolean getBooleanPropertyValue(StringBuffer value) {
      return StringUtilities.strEqualIgnoreCase(value.toString(), "true");
   }

   protected static final int getIntegerPropertyValue(StringBuffer value) {
      return NumberUtilities.parseInt(value.toString(), 0, value.length(), 10);
   }

   protected void logWarning(String warningString) {
      String eventLoggerString = ((StringBuffer)(new Object("UGPW: "))).append(warningString).toString();
      EventLogger.logEvent(234044482576569793L, eventLoggerString.getBytes());
      System.out.println(warningString);
   }
}
