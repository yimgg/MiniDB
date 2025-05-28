package com.minidb.service.tbm;

import com.minidb.service.parser.statement.Begin;

public interface TableManager {
  BeginRes begin(Begin begin);

  byte[] commit(long xid) throws Exception;

  byte[] abort(long xid);
}
