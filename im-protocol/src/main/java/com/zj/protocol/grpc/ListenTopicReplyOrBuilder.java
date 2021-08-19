// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_api.proto

package com.zj.protocol.grpc;

public interface ListenTopicReplyOrBuilder extends
    // @@protoc_insertion_point(interface_extends:app.ListenTopicReply)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   *具体的topic key
   * </pre>
   *
   * <code>string topic = 1;</code>
   * @return The topic.
   */
  java.lang.String getTopic();
  /**
   * <pre>
   *具体的topic key
   * </pre>
   *
   * <code>string topic = 1;</code>
   * @return The bytes for topic.
   */
  com.google.protobuf.ByteString
      getTopicBytes();

  /**
   * <pre>
   *由业务推送过来的json信息，根据业务不同内部的格式不同
   * </pre>
   *
   * <code>string data = 2;</code>
   * @return The data.
   */
  java.lang.String getData();
  /**
   * <pre>
   *由业务推送过来的json信息，根据业务不同内部的格式不同
   * </pre>
   *
   * <code>string data = 2;</code>
   * @return The bytes for data.
   */
  com.google.protobuf.ByteString
      getDataBytes();
}
