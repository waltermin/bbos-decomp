package org.w3c.dom.html2;

public interface HTMLLabelElement extends HTMLElement {
   HTMLFormElement getForm();

   String getAccessKey();

   void setAccessKey(String var1);

   String getHtmlFor();

   void setHtmlFor(String var1);
}
