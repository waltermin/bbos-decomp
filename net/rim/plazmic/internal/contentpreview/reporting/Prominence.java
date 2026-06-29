package net.rim.plazmic.internal.contentpreview.reporting;

public final class Prominence {
   private String _name;
   private int _level;
   public static final String rcsid;
   public static final Prominence LOW = new Prominence("LOW", 1);
   public static final Prominence MEDIUM_LOW = new Prominence("MEDIUM_LOW", 2);
   public static final Prominence MEDIUM = new Prominence("MEDIUM", 3);
   public static final Prominence MEDIUM_HIGH = new Prominence("MEDIUM_HIGH", 4);
   public static final Prominence HIGH = new Prominence("HIGH", 5);

   private Prominence(String name, int level) {
      this._name = name;
      this._level = level;
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object("Prominence("))).append(this._name).append(")").toString();
   }
}
