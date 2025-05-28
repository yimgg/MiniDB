package com.minidb.server;

import com.minidb.service.parser.Parser;
import com.minidb.service.parser.statement.Begin;
import com.minidb.service.tbm.BeginRes;
import com.minidb.service.tbm.TableManager;

public class Executor {
  private TableManager tableManager;
  private long xid;

  public Executor(TableManager tableManager) {
    this.tableManager = tableManager;
    this.xid = 0;
  }
  public void close() {
    if(xid != 0){
      System.out.println("abort transaction " + xid);
      tableManager.abort(xid);
    }
  }
  public byte[] execute(byte[] sql) throws Exception {
    return null;
  }

}
