package com.minidb.service.parser;

import java.util.ArrayList;
import java.util.List;

import com.minidb.common.Error;
import com.minidb.service.parser.statement.Abort;
import com.minidb.service.parser.statement.Begin;
import com.minidb.service.parser.statement.Commit;
import com.minidb.service.parser.statement.Create;
import com.minidb.service.parser.statement.Delete;
import com.minidb.service.parser.statement.Drop;
import com.minidb.service.parser.statement.Insert;
import com.minidb.service.parser.statement.Select;
import com.minidb.service.parser.statement.Show;
import com.minidb.service.parser.statement.SingleExpression;
import com.minidb.service.parser.statement.Update;
import com.minidb.service.parser.statement.Where;

/**
 * 1. 解析begin
 * 2. 解析commit
 * 3. 解析abort
 * 4. 解析create
 * 5. 解析drop
 * 6. 解析insert
 * 7. 解析update
 * 8. 解析delete
 * 9. 解析select
 * 10. 解析show
 * 11. 解析where
 */
public class Parser {
  public static Object parse(byte[] statement) throws Exception {
    Tokenizer tokenizer = new Tokenizer(statement);
    String token = tokenizer.peek();
    tokenizer.pop();
    Object stat = null;

    Exception statErr = null;
    try {
      switch (token) {
        case "begin":
          stat = parseBegin(tokenizer);
          break;
        case "commit":
          stat = parseCommit(tokenizer);
          break;
        case "abort":
          stat = parseAbort(tokenizer);
          break;
        case "create":
          stat = parseCreate(tokenizer);
          break;
        case "drop":
          stat = parseDrop(tokenizer);
          break;
        case "insert":
          stat = parseInsert(tokenizer);
          break;
        case "update":
          stat = parseUpdate(tokenizer);
          break;
        case "delete":
          stat = parseDelete(tokenizer);
          break;
        case "select":
          stat = parseSelect(tokenizer);
          break;
        case "show":
          stat = parseShow(tokenizer);
          break;
        default:
          throw Error.InvalidCommandException;
      }
    } catch (Exception e) {
      statErr = e;
    }
    try {
      String next = tokenizer.peek();
      if (!"".equals(next)) {
        byte[] errStat = tokenizer.errStat();
        statErr = new RuntimeException("Invalid statement" + new String(errStat));
      }
    } catch (Exception e) {
      e.printStackTrace();
      byte[] errStat = tokenizer.errStat();
      statErr = new RuntimeException("Invalid statement" + new String(errStat));
    }
    if (statErr != null) {
      throw statErr;
    }
    return stat;
  }

  /**
   * 事务解析
   * 1. 解析isolation
   * 2. 解析level
   * 3. 解析read
   * 4. 解析committed
   * 5. 解析repeatable
   * 6. 返回Begin对象
   * @param tokenizer
   * @return
   * @throws Exception
   */
  private static Object parseBegin(Tokenizer tokenizer) throws Exception {
    String isolation = tokenizer.peek();
    Begin begin = new Begin();
    if ("".equals(isolation)) {
      return begin;
    }
    if (!"isolation".equals(isolation)) {
      throw Error.InvalidCommandException;
    }
    tokenizer.pop();
    String level = tokenizer.peek();
    if (!"level".equals(level)) {
      throw Error.InvalidCommandException;
    }
    tokenizer.pop();
    String tmp1 = tokenizer.peek();
    if ("read".equals(tmp1)) {
      tokenizer.pop();
      String tmp2 = tokenizer.peek();
      if ("committed".equals(tmp2)) {
        tokenizer.pop();
        if (!"".equals(tokenizer.peek())) {
          throw Error.InvalidCommandException;
        }
        begin.isRepeatableRead = true;
        return begin;
      } else {
        throw Error.InvalidCommandException;
      }
    } else if ("repeatable".equals(tmp1)) {
      tokenizer.pop();
      String tmp2 = tokenizer.peek();
      if ("read".equals(tmp2)) {
        begin.isRepeatableRead = true;
        tokenizer.pop();
        if (!"".equals(tokenizer.peek())) {
          throw Error.InvalidCommandException;
        }
        return begin;
      } else {
        throw Error.InvalidCommandException;
      }
    } else {
      throw Error.InvalidCommandException;
    }
  }

  private static Object parseCommit(Tokenizer tokenizer) throws Exception {
    if (!"".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    return new Commit();
  }

  private static Object parseAbort(Tokenizer tokenizer) throws Exception {
    if (!"".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    return new Abort();
  }

  /**
   * 解析create
   * 1. 解析tableName
   * 2. 解析fieldName
   * 3. 解析fieldType
   * 4. 解析index
   * 5. 返回Create对象
   * 
   * @param tokenizer
   * @return
   * @throws Exception
   */
  private static Object parseCreate(Tokenizer tokenizer) throws Exception {
    Create create = new Create();
    if (!"table".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    tokenizer.pop();
    String tableName = tokenizer.peek();
    if (!isName(tableName)) {
      throw Error.InvalidCommandException;
    }
    create.tableName = tableName;
    List<String> fieldName = new ArrayList<>();
    List<String> fieldType = new ArrayList<>();
    while (true) {
      tokenizer.pop();
      String field = tokenizer.peek();
      if ("(".equals(field)) {
        break;
      }
      if (!isName(field)) {
        throw Error.InvalidCommandException;
      }
      tokenizer.pop();
      String type = tokenizer.peek();
      if (!isType(type)) {
        throw Error.InvalidCommandException;
      }

      fieldName.add(field);
      fieldType.add(type);
      tokenizer.pop();
      String next = tokenizer.peek();
      if (",".equals(next)) {
        continue;
      } else if ("".equals(next)) {
        throw Error.InvalidCommandException;
      } else if (")".equals(next)) {
        break;
      } else {
        throw Error.InvalidCommandException;
      }
    }
    create.fieldName = fieldName.toArray(new String[fieldName.size()]);
    create.fieldType = fieldType.toArray(new String[fieldType.size()]);
    tokenizer.pop();
    if (!"index".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    tokenizer.pop();
    if (!"(".equals(tokenizer.peek())) { // 先检查左括号
      throw Error.InvalidCommandException;
    }
    List<String> index = new ArrayList<>();
    while (true) {
      tokenizer.pop();
      String idx = tokenizer.peek();
      if (")".equals(idx)) {
        break;
      }
      if (!isName(idx)) {
        throw Error.InvalidCommandException;
      }
      index.add(idx);
    }
    create.index = index.toArray(new String[index.size()]);
    tokenizer.pop();
    if (!"".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    return create;
  }

  private static Object parseDrop(Tokenizer tokenizer) throws Exception {
    Drop drop = new Drop();
    if (!"table".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    tokenizer.pop();
    String tableName = tokenizer.peek();
    if (!isName(tableName)) {
      throw Error.InvalidCommandException;
    }

    tokenizer.pop();
    if (!"".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    drop.tableName = tableName;
    return drop;
  }

  private static Object parseInsert(Tokenizer tokenizer) throws Exception {
    Insert insert = new Insert();
    if (!"insert".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    tokenizer.pop();
    String tableName = tokenizer.peek();
    if (!isName(tableName)) {
      throw Error.InvalidCommandException;
    }
    insert.tableName = tableName;
    tokenizer.pop();
    if (!"values".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    List<String> values = new ArrayList<>();
    while (true) {
      tokenizer.pop();
      String value = tokenizer.peek();
      if ("".equals(value)) {
        break;
      } else {
        values.add(value);
      }
    }
    insert.values = values.toArray(new String[values.size()]);
    return insert;
  }

  /**
   * 解析update
   * 1. 解析tableName
   * 2. 解析set
   * 3. 解析fieldName
   * 4. 解析value
   * 5. 解析where
   * 6. 返回Update对象
   * TODO: 未来修改为多个字段一起修改
   * 
   * @param tokenizer
   * @return
   * @throws Exception
   */
  private static Object parseUpdate(Tokenizer tokenizer) throws Exception {
    Update update = new Update();
    update.tableName = tokenizer.peek();
    tokenizer.pop();
    if (!"set".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    tokenizer.pop();
    update.fieldName = tokenizer.peek();
    tokenizer.pop();
    if (!"=".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    tokenizer.pop();
    update.value = tokenizer.peek();
    tokenizer.pop();
    String tmp = tokenizer.peek();
    if ("".equals(tmp)) {
      update.where = null;
      return update;
    }
    update.where = parseWhere(tokenizer);
    return update;
  }

  private static Object parseDelete(Tokenizer tokenizer) throws Exception {
    Delete delete = new Delete();
    if (!"from".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    tokenizer.pop();
    String tableName = tokenizer.peek();
    if (!isName(tableName)) {
      throw Error.InvalidCommandException;
    }
    delete.tableName = tableName;
    tokenizer.pop();
    delete.where = parseWhere(tokenizer);
    return delete;
  }

  /**
   * 解析select
   * 1. 解析fields
   * 2. 解析from
   * 3. 解析where
   * 4. 返回Select对象
   * 
   * @param tokenizer
   * @return
   * @throws Exception
   */
  private static Object parseSelect(Tokenizer tokenizer) throws Exception {
    Select read = new Select();
    List<String> fields = new ArrayList<>();
    String asterisk = tokenizer.peek();
    if ("*".equals(asterisk)) {
      tokenizer.pop();
      fields.add(asterisk);
    } else {
      while (true) {
        String field = tokenizer.peek();
        if (!isName(field)) {
          throw Error.InvalidCommandException;
        }
        fields.add(field);
        tokenizer.pop();
        if (",".equals(tokenizer.peek())) {
          tokenizer.pop();
        } else {
          break;
        }
      }
    }
    read.fields = fields.toArray(new String[fields.size()]);
    if (!"from".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    tokenizer.pop();
    String tableName = tokenizer.peek();
    if (!isName(tableName)) {
      throw Error.InvalidCommandException;
    }
    read.tableName = tableName;
    tokenizer.pop();
    String tmp = tokenizer.peek();
    if ("".equals(tmp)) {
      read.where = null;
      return read;
    }
    read.where = parseWhere(tokenizer);
    return read;
  }

  private static Where parseWhere(Tokenizer tokenizer) throws Exception {
    Where where = new Where();
    if (!"where".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    tokenizer.pop();
    SingleExpression singleExpression1 = parseSingleExpression(tokenizer);
    where.singleExpression1 = singleExpression1;
    String logicOperator = tokenizer.peek();
    if ("".equals(logicOperator)) {
      where.logicOperator = logicOperator;
      return where;
    }
    if (!isLogicOp(logicOperator)) {
      throw Error.InvalidCommandException;
    }
    where.logicOperator = logicOperator;
    SingleExpression singleExpression2 = parseSingleExpression(tokenizer);
    where.singleExpression2 = singleExpression2;
    if (!"".equals(tokenizer.peek())) {
      throw Error.InvalidCommandException;
    }
    return where;
  }

  private static SingleExpression parseSingleExpression(Tokenizer tokenizer) throws Exception {
    SingleExpression singleExpression = new SingleExpression();
    String field = tokenizer.peek();
    if (!isName(field)) {
      throw Error.InvalidCommandException;
    }
    singleExpression.field = field;
    tokenizer.pop();
    String op = tokenizer.peek();
    if (!isCmpOp(op)) {
      throw Error.InvalidCommandException;
    }
    singleExpression.compareOperator = op;
    tokenizer.pop();
    singleExpression.value = tokenizer.peek();
    tokenizer.pop();
    return singleExpression;
  }

  private static Object parseShow(Tokenizer tokenizer) throws Exception {
    String tmp = tokenizer.peek();
    if ("tables".equals(tmp)) {
      return new Show();
    }
    throw Error.InvalidCommandException;
  }

  private static boolean isName(String name) {
    return !(name.length() == 1 && !Tokenizer.isAlphaBeta(name.getBytes()[0]));
  }

  private static boolean isType(String tp) {
    return ("int32".equals(tp) || "int64".equals(tp) ||
        "string".equals(tp));
  }

  private static boolean isCmpOp(String op) {
    return ("=".equals(op) || ">".equals(op) || "<".equals(op));
  }

  private static boolean isLogicOp(String op) {
    return ("and".equals(op) || "or".equals(op));
  }

}
