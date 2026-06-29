package javax.xml.namespace;

public class QName {
   private final String namespaceURI;
   private final String localName;
   private final String prefix;

   public QName(String namespaceURI, String localName, String prefix) {
      if (namespaceURI == null) {
         this.namespaceURI = "";
      } else {
         this.namespaceURI = namespaceURI;
      }

      if (localName == null) {
         throw new Object("The local name cannot be null.");
      }

      this.localName = localName;
      if (prefix == null) {
         throw new Object("The prefix cannot be null.");
      }

      this.prefix = prefix;
   }

   public QName(String namespaceURI, String localName) {
      this(namespaceURI, localName, "");
   }

   public QName(String localName) {
      this("", localName, "");
   }

   public String getNamespaceURI() {
      return this.namespaceURI;
   }

   public String getLocalPart() {
      return this.localName;
   }

   public String getPrefix() {
      return this.prefix;
   }

   @Override
   public boolean equals(Object anObject) {
      if (!(anObject instanceof QName)) {
         return false;
      }

      QName qName = (QName)anObject;
      return this.namespaceURI.equals(qName.namespaceURI) && this.localName.equals(qName.localName);
   }

   @Override
   public int hashCode() {
      return this.namespaceURI.hashCode() ^ this.localName.hashCode();
   }

   @Override
   public String toString() {
      return this.namespaceURI.equals("")
         ? this.localName
         : ((StringBuffer)(new Object("{"))).append(this.namespaceURI).append("}").append(this.localName).toString();
   }

   public static QName valueOf(String aString) {
      if (aString != null && aString.length() != 0) {
         int startNsURI = aString.indexOf(123);
         if (startNsURI != 0) {
            return new QName("", aString, "");
         } else {
            int endNsURI = aString.indexOf(125);
            if (endNsURI == -1) {
               throw new Object(
                  ((StringBuffer)(new Object("A QName cannot be constructed from \""))).append(aString).append("\": missing closing \"}\".").toString()
               );
            } else if (endNsURI == aString.length() - 1) {
               throw new Object(
                  ((StringBuffer)(new Object("A QName cannot be constructed from \""))).append(aString).append("\": missing local name.").toString()
               );
            } else {
               return new QName(aString.substring(1, endNsURI), aString.substring(endNsURI + 1), "");
            }
         }
      } else {
         throw new Object("A QName cannot be constructed from null or an empty string.");
      }
   }
}
