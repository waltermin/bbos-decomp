package javax.microedition.xml.rpc;

import javax.xml.namespace.QName;

public class Element extends Type {
   public final int value = 9;
   public final Type contentType;
   public final QName name;
   public final int minOccurs;
   public final boolean isOptional;
   public final int maxOccurs;
   public final boolean isNillable;
   public final boolean isArray;
   public static final int UNBOUNDED;

   public Element(QName qname, Type type, int minOccurs, int maxOccurs, boolean nillable) {
      super(9);
      if (qname == null) {
         throw new Object("the qualified name for this Element must be specified");
      }

      if (type == null) {
         throw new Object("the content type of this Element must be specified");
      }

      if (type instanceof Element) {
         throw new Object("the content type of this Element must be either a simple type or a complex type");
      }

      if (minOccurs < 0) {
         throw new Object("the minimum number of occurrences must be non-negative");
      }

      if (maxOccurs <= 0 && maxOccurs != -1) {
         throw new Object("the maximum number of occurrences must be non-negative");
      }

      if (minOccurs > maxOccurs && maxOccurs != -1) {
         throw new Object("the minimum number of occurrences must not be greater than the maximum number of occurrences");
      }

      this.contentType = type;
      this.name = qname;
      this.minOccurs = minOccurs;
      this.maxOccurs = maxOccurs;
      this.isNillable = nillable;
      this.isOptional = minOccurs == 0;
      this.isArray = maxOccurs > 1 || maxOccurs == -1;
   }

   public Element(QName qname, Type type) {
      this(qname, type, 1, 1, false);
   }
}
