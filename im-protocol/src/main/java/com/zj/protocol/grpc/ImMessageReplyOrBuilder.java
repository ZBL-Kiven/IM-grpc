// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_api.proto

package com.zj.protocol.grpc;

public interface ImMessageReplyOrBuilder extends
    // @@protoc_insertion_point(interface_extends:app.ImMessageReply)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   **
   * 0 代表是消息 1 代表是服务器收到请求的回执
   * </pre>
   *
   * <code>int32 type = 3;</code>
   * @return The type.
   */
  int getType();

  /**
   * <code>.app.ImMessage imMessage = 1;</code>
   * @return Whether the imMessage field is set.
   */
  boolean hasImMessage();
  /**
   * <code>.app.ImMessage imMessage = 1;</code>
   * @return The imMessage.
   */
  ImMessage getImMessage();
  /**
   * <code>.app.ImMessage imMessage = 1;</code>
   */
  ImMessageOrBuilder getImMessageOrBuilder();

  /**
   * <code>.app.ImMessageReply.ReqContext reqContext = 2;</code>
   * @return Whether the reqContext field is set.
   */
  boolean hasReqContext();
  /**
   * <code>.app.ImMessageReply.ReqContext reqContext = 2;</code>
   * @return The reqContext.
   */
  ImMessageReply.ReqContext getReqContext();
  /**
   * <code>.app.ImMessageReply.ReqContext reqContext = 2;</code>
   */
  ImMessageReply.ReqContextOrBuilder getReqContextOrBuilder();
}
