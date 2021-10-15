// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_api.proto

package com.zj.protocol.grpc;

public interface LiveRoomMessageReqOrBuilder extends
    // @@protoc_insertion_point(interface_extends:app.LiveRoomMessageReq)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.app.LiveRoomMessageReq.Op op = 1;</code>
   * @return The enum numeric value on the wire for op.
   */
  int getOpValue();
  /**
   * <code>.app.LiveRoomMessageReq.Op op = 1;</code>
   * @return The op.
   */
  LiveRoomMessageReq.Op getOp();

  /**
   * <code>int32 roomId = 2;</code>
   * @return The roomId.
   */
  int getRoomId();

  /**
   * <code>int64 liveId = 3;</code>
   * @return The liveId.
   */
  long getLiveId();

  /**
   * <code>bool liverIsMe = 4;</code>
   * @return The liverIsMe.
   */
  boolean getLiverIsMe();

  /**
   * <code>uint64 userId = 5;</code>
   * @return The userId.
   */
  long getUserId();
}