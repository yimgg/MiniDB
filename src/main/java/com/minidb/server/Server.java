package com.minidb.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.minidb.common.Error;
import com.minidb.service.tbm.TableManager;
import com.minidb.utils.Panic;

public class Server {
  private int port;
  private TableManager tableManager;

  public Server(int port, TableManager tableManager) {
    this.port = port;
    this.tableManager = tableManager;
  }

  public void start() {
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      Panic.panic(Error.ServerSocketException);
      return;
    }
    System.out.println("Server started on port " + port);
    // 创建一个线程池
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    // 监听端口号
    try {
      while (true) { 
        Socket socket = serverSocket.accept();
        Runnable worker = new HandleSocket(socket, tableManager);
        threadPool.execute(worker);
      }
    } catch (IOException e) {
      Panic.panic(Error.ServerSocketException);
    } finally {
      try {
        serverSocket.close();
      } catch (IOException e) {
        Panic.panic(Error.ServerSocketException);
      }
    }
  }
}
