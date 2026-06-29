package net.rim.device.api.ui;

public interface FieldVisitor {
   int PRE = 1;
   int POST = 2;
   int LEAF = 3;

   boolean visit(Field var1, int var2);
}
