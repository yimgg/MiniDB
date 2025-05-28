package com.minidb.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.minidb.service.tbm.TableManager;
import com.minidb.transport.Encoder;
import com.minidb.transport.Packager;
import com.minidb.transport.Transporter;
import com.minidb.transport.Package;
public class HandleSocket implements Runnable {
  private Socket socket;
  private TableManager tableManager;

  public HandleSocket(Socket socket, TableManager tableManager) {
    this.socket = socket;
    this.tableManager = tableManager;
  }

  // 处理请求
  @Override
  public void run() {
    InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
    System.out.println("Client connected: " + address.getAddress().getHostAddress() + ":" + address.getPort());
    Packager packager = null;
    try {
      Transporter transporter = new Transporter(socket);
      Encoder encoder = new Encoder();
      packager = new Packager(transporter, encoder);
    } catch (IOException e) {
      e.printStackTrace();
      try {
        socket.close();
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
    Executor executor = new Executor(tableManager);
    while(true) {
       Package pkg = null;
      try {
        pkg = packager.receive();
      } catch (Exception e) {
        e.printStackTrace();
      }
      byte[] sql = pkg.getData();
      byte[] result = null;
      Exception e = null;
      try {
        result = executor.execute(sql);
      } catch (Exception e1) {
        e = e1;
        e.printStackTrace();
      }
      pkg = new Package(result, e);
      try {
        packager.send(pkg);
      } catch (Exception e1) {
        e1.printStackTrace();
        break;
      }
    }
    executor.close();
    try {
      packager.close();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

}
