package org.xml.sax.helpers;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

final class NamespaceSupport$Context {
   Hashtable prefixTable;
   Hashtable uriTable;
   Hashtable elementNameTable;
   Hashtable attributeNameTable;
   String defaultNS;
   boolean declsOK;
   private Vector declarations;
   private boolean declSeen;
   private NamespaceSupport$Context parent;
   private final NamespaceSupport this$0;

   NamespaceSupport$Context(NamespaceSupport _1) {
      this.this$0 = _1;
      this.defaultNS = null;
      this.declsOK = true;
      this.declarations = null;
      this.declSeen = false;
      this.parent = null;
      this.copyTables();
   }

   final void setParent(NamespaceSupport$Context parent) {
      this.parent = parent;
      this.declarations = null;
      this.prefixTable = parent.prefixTable;
      this.uriTable = parent.uriTable;
      this.elementNameTable = parent.elementNameTable;
      this.attributeNameTable = parent.attributeNameTable;
      this.defaultNS = parent.defaultNS;
      this.declSeen = false;
      this.declsOK = true;
   }

   final void clear() {
      this.parent = null;
      this.prefixTable = null;
      this.uriTable = null;
      this.elementNameTable = null;
      this.attributeNameTable = null;
      this.defaultNS = null;
   }

   final void declarePrefix(String prefix, String uri) {
      if (!this.declsOK) {
         throw new IllegalStateException("can't declare any more prefixes in this context");
      }

      if (!this.declSeen) {
         this.copyTables();
      }

      if (this.declarations == null) {
         this.declarations = new Vector();
      }

      prefix = prefix.intern();
      uri = uri.intern();
      if ("".equals(prefix)) {
         if ("".equals(uri)) {
            this.defaultNS = null;
         } else {
            this.defaultNS = uri;
         }
      } else {
         this.prefixTable.put(prefix, uri);
         this.uriTable.put(uri, prefix);
      }

      this.declarations.addElement(prefix);
   }

   final String[] processName(String qName, boolean isAttribute) {
      this.declsOK = false;
      Hashtable table;
      if (isAttribute) {
         table = this.attributeNameTable;
      } else {
         table = this.elementNameTable;
      }

      String[] name = (String[])table.get(qName);
      if (name != null) {
         return name;
      }

      name = new String[]{null, null, qName.intern()};
      int index = qName.indexOf(58);
      if (index == -1) {
         if (isAttribute) {
            if (qName == "xmlns" && this.this$0.namespaceDeclUris) {
               name[0] = "http://www.w3.org/xmlns/2000/";
            } else {
               name[0] = "";
            }
         } else if (this.defaultNS == null) {
            name[0] = "";
         } else {
            name[0] = this.defaultNS;
         }

         name[1] = name[2];
      } else {
         String prefix = qName.substring(0, index);
         String local = qName.substring(index + 1);
         String uri;
         if ("".equals(prefix)) {
            uri = this.defaultNS;
         } else {
            uri = (String)this.prefixTable.get(prefix);
         }

         if (uri == null || !isAttribute && "xmlns".equals(prefix)) {
            return null;
         }

         name[0] = uri;
         name[1] = local.intern();
      }

      table.put(name[2], name);
      return name;
   }

   final String getURI(String prefix) {
      if ("".equals(prefix)) {
         return this.defaultNS;
      } else {
         return this.prefixTable == null ? null : (String)this.prefixTable.get(prefix);
      }
   }

   final String getPrefix(String uri) {
      return this.uriTable == null ? null : (String)this.uriTable.get(uri);
   }

   final Enumeration getDeclaredPrefixes() {
      return this.declarations == null ? NamespaceSupport.EMPTY_ENUMERATION : this.declarations.elements();
   }

   final Enumeration getPrefixes() {
      return this.prefixTable == null ? NamespaceSupport.EMPTY_ENUMERATION : this.prefixTable.keys();
   }

   private final Hashtable cloneHashtable(Hashtable t) {
      Hashtable newT = new Hashtable();
      Enumeration e = t.keys();

      while (e.hasMoreElements()) {
         Object key = e.nextElement();
         newT.put(key, t.get(key));
      }

      return newT;
   }

   private final void copyTables() {
      if (this.prefixTable != null) {
         this.prefixTable = this.cloneHashtable(this.prefixTable);
      } else {
         this.prefixTable = new Hashtable();
      }

      if (this.uriTable != null) {
         this.uriTable = this.cloneHashtable(this.uriTable);
      } else {
         this.uriTable = new Hashtable();
      }

      this.elementNameTable = new Hashtable();
      this.attributeNameTable = new Hashtable();
      this.declSeen = true;
   }
}
