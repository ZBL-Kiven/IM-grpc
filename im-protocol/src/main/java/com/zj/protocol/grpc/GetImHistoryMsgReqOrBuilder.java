// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_api.proto

package com.zj.protocol.grpc;

public interface GetImHistoryMsgReqOrBuilder extends
    // @@protoc_insertion_point(interface_extends:app.GetImHistoryMsgReq)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>uint64 groupId = 1;</code>
   * @return The groupId.
   */
  long getGroupId();

  /**
   * <code>uint64 msgId = 2;</code>
   * @return The msgId.
   */
  long getMsgId();

  /**
   * <pre>
   *大V id
   * </pre>
   *
   * <code>uint64 ownerId = 3;</code>
   * @return The ownerId.
   */
  long getOwnerId();
}