package net.rim.device.cldc.io.simultcpdatagram;

public final class SimulTcpDatagramProperties {
   public int sourceAddress;
   public int destinationAddress;
   public int sourcePort;
   public int destinationPort;
   public int controlCode;
   public int controlDescription;
   public int socketID = -1;
   public int sequenceNumber;

   public SimulTcpDatagramProperties() {
   }

   public final void reset() {
      this.sourceAddress = 0;
      this.destinationAddress = 0;
      this.sourcePort = 0;
      this.destinationPort = 0;
      this.controlCode = 0;
      this.controlDescription = 0;
      this.socketID = -1;
      this.sequenceNumber = 0;
   }

   public SimulTcpDatagramProperties(
      int sourceAddress, int destinationAddress, int sourcePort, int destinationPort, int controlCode, int controlDescription, int socketID, int sequenceNumber
   ) {
      this.setData(sourceAddress, destinationAddress, sourcePort, destinationPort, controlCode, controlDescription, socketID, sequenceNumber);
   }

   public final void setData(
      int psourceAddress,
      int pdestinationAddress,
      int psourcePort,
      int pdestinationPort,
      int pcontrolCode,
      int pcontrolDescription,
      int psocketID,
      int psequenceNumber
   ) {
      this.sourceAddress = psourceAddress;
      this.destinationAddress = pdestinationAddress;
      this.sourcePort = psourcePort;
      this.destinationPort = pdestinationPort;
      this.controlCode = pcontrolCode;
      this.controlDescription = pcontrolDescription;
      this.socketID = psocketID;
      this.sequenceNumber = psequenceNumber;
   }
}
