package com.minidb.transport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * 传输器
 * 发送查询结果给客户端
 * 接收客户端的SQL命令
 * 处理二进制数据的传输
 * 确保数据传输的可靠性
 */
public class Transporter {
  private Socket socket;
  private BufferedReader reader;
  private BufferedWriter writer;

  public Transporter(Socket socket) throws IOException {
    this.socket = socket;
    this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  }
  /**
   * 发送数据
   * @param data
   * @throws IOException
   */
  public void send(byte[] data) throws IOException {
    String raw = hexEncode(data);
    writer.write(raw);
    writer.flush();
  }
  /**
   * 接收数据
   * @return
   * @throws IOException
   * @throws DecoderException
   */
  public byte[] receive() throws IOException, DecoderException {
    String raw = reader.readLine();
    if (raw == null) {
      close();
    }
    return hexDecode(raw);
  }
  public void close() throws IOException {
    socket.close();
    reader.close();
    writer.close();
  }
  private String hexEncode(byte[] buf ){
    return Hex.encodeHexString(buf,true)+"\n";
  }
  private byte[] hexDecode(String raw) throws DecoderException {
    return Hex.decodeHex(raw);
  }

}
