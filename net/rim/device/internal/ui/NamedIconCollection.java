package net.rim.device.internal.ui;

public class NamedIconCollection extends IconCollection {
   private String _name;

   public NamedIconCollection(String name, int columns, int rows, String moduleName) {
      super(columns, rows, moduleName);
      this._name = name;
   }

   public String getName() {
      return this._name;
   }
}
