package org.xml.sax.helpers;

import org.xml.sax.Locator;

public class LocatorImpl implements Locator {
   private String publicId;
   private String systemId;
   private int lineNumber;
   private int columnNumber;

   public void setColumnNumber(int columnNumber) {
      this.columnNumber = columnNumber;
   }

   public void setLineNumber(int lineNumber) {
      this.lineNumber = lineNumber;
   }

   public void setSystemId(String systemId) {
      this.systemId = systemId;
   }

   public void setPublicId(String publicId) {
      this.publicId = publicId;
   }

   @Override
   public int getColumnNumber() {
      return this.columnNumber;
   }

   @Override
   public int getLineNumber() {
      return this.lineNumber;
   }

   @Override
   public String getSystemId() {
      return this.systemId;
   }

   @Override
   public String getPublicId() {
      return this.publicId;
   }

   public LocatorImpl(Locator locator) {
      this.setPublicId(locator.getPublicId());
      this.setSystemId(locator.getSystemId());
      this.setLineNumber(locator.getLineNumber());
      this.setColumnNumber(locator.getColumnNumber());
   }

   public LocatorImpl() {
   }
}
