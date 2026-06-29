package net.rim.ecmascript.runtime;

class GlobalProperties$PackageTable {
   String name;
   GlobalProperties$PackageTable[] children;

   GlobalProperties$PackageTable(String moniker, GlobalProperties$PackageTable[] kids) {
      this.name = moniker;
      this.children = kids;
   }
}
