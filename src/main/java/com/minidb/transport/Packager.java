package com.minidb.transport;

public class Packager {
  private Transporter transporter;
  private Encoder encoder;

  public Packager(Transporter transporter, Encoder encoder) {
    this.transporter = transporter;
    this.encoder = encoder;
  }
  public Package receive() throws Exception {
    byte[] data = transporter.receive();
    return encoder.decode(data);
  }
  public void send(Package pkg) throws Exception {
    encoder.encode(pkg);
    transporter.send(pkg.getData());
  }
  public void close() throws Exception {
    transporter.close();
  }
  

}
