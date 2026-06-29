package net.rim.device.apps.internal.supl;

final class LocationId {
   private CellInformation cellInfo = new GsmCellInfo();
   private Status status = new Status();

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      this.cellInfo.encode(stuff);
      this.status.encode(stuff);
   }

   final void print() {
      System.out.println("Location ID: ");
      this.cellInfo.print();
      this.status.print();
   }
}
