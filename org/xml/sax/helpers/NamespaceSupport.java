package org.xml.sax.helpers;

import java.util.Enumeration;
import java.util.Vector;

public class NamespaceSupport {
   private NamespaceSupport$Context[] contexts;
   private NamespaceSupport$Context currentContext;
   private int contextPos;
   private boolean namespaceDeclUris;
   public static final String XMLNS;
   public static final String NSDECL;
   private static final Enumeration EMPTY_ENUMERATION = ((Vector)(new Object())).elements();

   public NamespaceSupport() {
      this.reset();
   }

   public void reset() {
      this.contexts = new NamespaceSupport$Context[32];
      this.namespaceDeclUris = false;
      this.contextPos = 0;
      this.contexts[this.contextPos] = this.currentContext = new NamespaceSupport$Context(this);
      this.currentContext.declarePrefix("xml", "http://www.w3.org/XML/1998/namespace");
   }

   public void pushContext() {
      int max = this.contexts.length;
      this.contexts[this.contextPos].declsOK = false;
      this.contextPos++;
      if (this.contextPos >= max) {
         NamespaceSupport$Context[] newContexts = new NamespaceSupport$Context[max * 2];
         System.arraycopy(this.contexts, 0, newContexts, 0, max);
         max *= 2;
         this.contexts = newContexts;
      }

      this.currentContext = this.contexts[this.contextPos];
      if (this.currentContext == null) {
         this.contexts[this.contextPos] = this.currentContext = new NamespaceSupport$Context(this);
      }

      if (this.contextPos > 0) {
         this.currentContext.setParent(this.contexts[this.contextPos - 1]);
      }
   }

   public void popContext() {
      this.contexts[this.contextPos].clear();
      this.contextPos--;
      if (this.contextPos < 0) {
         throw new Object();
      }

      this.currentContext = this.contexts[this.contextPos];
   }

   public boolean declarePrefix(String prefix, String uri) {
      if (!prefix.equals("xml") && !prefix.equals("xmlns")) {
         this.currentContext.declarePrefix(prefix, uri);
         return true;
      } else {
         return false;
      }
   }

   public String[] processName(String qName, String[] parts, boolean isAttribute) {
      String[] myParts = this.currentContext.processName(qName, isAttribute);
      if (myParts == null) {
         return null;
      }

      parts[0] = myParts[0];
      parts[1] = myParts[1];
      parts[2] = myParts[2];
      return parts;
   }

   public String getURI(String prefix) {
      return this.currentContext.getURI(prefix);
   }

   public Enumeration getPrefixes() {
      return this.currentContext.getPrefixes();
   }

   public String getPrefix(String uri) {
      return this.currentContext.getPrefix(uri);
   }

   public Enumeration getPrefixes(String uri) {
      Vector prefixes = (Vector)(new Object());
      Enumeration allPrefixes = this.getPrefixes();

      while (allPrefixes.hasMoreElements()) {
         String prefix = (String)allPrefixes.nextElement();
         if (uri.equals(this.getURI(prefix))) {
            prefixes.addElement(prefix);
         }
      }

      return prefixes.elements();
   }

   public Enumeration getDeclaredPrefixes() {
      return this.currentContext.getDeclaredPrefixes();
   }

   public void setNamespaceDeclUris(boolean value) {
      if (this.contextPos != 0) {
         throw new Object();
      }

      if (value != this.namespaceDeclUris) {
         this.namespaceDeclUris = value;
         if (value) {
            this.currentContext.declarePrefix("xmlns", "http://www.w3.org/xmlns/2000/");
         } else {
            this.contexts[this.contextPos] = this.currentContext = new NamespaceSupport$Context(this);
            this.currentContext.declarePrefix("xml", "http://www.w3.org/XML/1998/namespace");
         }
      }
   }

   public boolean isNamespaceDeclUris() {
      return this.namespaceDeclUris;
   }
}
