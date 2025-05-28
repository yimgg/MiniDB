package com.minidb.service.tbm;

import com.minidb.service.parser.statement.Begin;

public class TableManagerImpl implements TableManager {

  @Override
  public BeginRes begin(Begin begin) {
    BeginRes res = new BeginRes();
    int level = begin.isRepeatableRead ? 1 : 0;
    res.xid = vm.begin(level);
    res.result = "begin".getBytes();
    return res;
  }

  @Override
  public byte[] commit(long xid) throws Exception {
    vm.commit(xid);
    return "commit".getBytes();
  }

  @Override
  public byte[] abort(long xid) {
    vm.abort(xid);
    return "abort".getBytes();
  }

}
