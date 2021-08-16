// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_api.proto

package com.zj.protocol.grpc;

/**
 * Protobuf type {@code app.GetImMessageReq}
 */
public  final class GetImMessageReq extends
    com.google.protobuf.GeneratedMessageLite<
        GetImMessageReq, GetImMessageReq.Builder> implements
    // @@protoc_insertion_point(message_implements:app.GetImMessageReq)
    GetImMessageReqOrBuilder {
  private GetImMessageReq() {
  }
  public static final int GROUPID_FIELD_NUMBER = 1;
  private long groupId_;
  /**
   * <code>optional uint64 groupId = 1;</code>
   */
  public long getGroupId() {
    return groupId_;
  }
  /**
   * <code>optional uint64 groupId = 1;</code>
   */
  private void setGroupId(long value) {
    
    groupId_ = value;
  }
  /**
   * <code>optional uint64 groupId = 1;</code>
   */
  private void clearGroupId() {
    
    groupId_ = 0L;
  }

  public static final int OWNERID_FIELD_NUMBER = 2;
  private long ownerId_;
  /**
   * <pre>
   *大V的id
   * </pre>
   *
   * <code>optional uint64 ownerId = 2;</code>
   */
  public long getOwnerId() {
    return ownerId_;
  }
  /**
   * <pre>
   *大V的id
   * </pre>
   *
   * <code>optional uint64 ownerId = 2;</code>
   */
  private void setOwnerId(long value) {
    
    ownerId_ = value;
  }
  /**
   * <pre>
   *大V的id
   * </pre>
   *
   * <code>optional uint64 ownerId = 2;</code>
   */
  private void clearOwnerId() {
    
    ownerId_ = 0L;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (groupId_ != 0L) {
      output.writeUInt64(1, groupId_);
    }
    if (ownerId_ != 0L) {
      output.writeUInt64(2, ownerId_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSerializedSize;
    if (size != -1) return size;

    size = 0;
    if (groupId_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt64Size(1, groupId_);
    }
    if (ownerId_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt64Size(2, ownerId_);
    }
    memoizedSerializedSize = size;
    return size;
  }

  public static GetImMessageReq parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static GetImMessageReq parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static GetImMessageReq parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static GetImMessageReq parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static GetImMessageReq parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static GetImMessageReq parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static GetImMessageReq parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input);
  }
  public static GetImMessageReq parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static GetImMessageReq parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static GetImMessageReq parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }

  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(GetImMessageReq prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }

  /**
   * Protobuf type {@code app.GetImMessageReq}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageLite.Builder<GetImMessageReq, Builder> implements
      // @@protoc_insertion_point(builder_implements:app.GetImMessageReq)
          GetImMessageReqOrBuilder {
    // Construct using com.zj.protocol.grpc.GetImMessageReq.newBuilder()
    private Builder() {
      super(DEFAULT_INSTANCE);
    }


    /**
     * <code>optional uint64 groupId = 1;</code>
     */
    public long getGroupId() {
      return instance.getGroupId();
    }
    /**
     * <code>optional uint64 groupId = 1;</code>
     */
    public Builder setGroupId(long value) {
      copyOnWrite();
      instance.setGroupId(value);
      return this;
    }
    /**
     * <code>optional uint64 groupId = 1;</code>
     */
    public Builder clearGroupId() {
      copyOnWrite();
      instance.clearGroupId();
      return this;
    }

    /**
     * <pre>
     *大V的id
     * </pre>
     *
     * <code>optional uint64 ownerId = 2;</code>
     */
    public long getOwnerId() {
      return instance.getOwnerId();
    }
    /**
     * <pre>
     *大V的id
     * </pre>
     *
     * <code>optional uint64 ownerId = 2;</code>
     */
    public Builder setOwnerId(long value) {
      copyOnWrite();
      instance.setOwnerId(value);
      return this;
    }
    /**
     * <pre>
     *大V的id
     * </pre>
     *
     * <code>optional uint64 ownerId = 2;</code>
     */
    public Builder clearOwnerId() {
      copyOnWrite();
      instance.clearOwnerId();
      return this;
    }

    // @@protoc_insertion_point(builder_scope:app.GetImMessageReq)
  }
  protected final Object dynamicMethod(
      com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
      Object arg0, Object arg1) {
    switch (method) {
      case NEW_MUTABLE_INSTANCE: {
        return new GetImMessageReq();
      }
      case IS_INITIALIZED: {
        return DEFAULT_INSTANCE;
      }
      case MAKE_IMMUTABLE: {
        return null;
      }
      case NEW_BUILDER: {
        return new Builder();
      }
      case VISIT: {
        Visitor visitor = (Visitor) arg0;
        GetImMessageReq other = (GetImMessageReq) arg1;
        groupId_ = visitor.visitLong(groupId_ != 0L, groupId_,
            other.groupId_ != 0L, other.groupId_);
        ownerId_ = visitor.visitLong(ownerId_ != 0L, ownerId_,
            other.ownerId_ != 0L, other.ownerId_);
        if (visitor == com.google.protobuf.GeneratedMessageLite.MergeFromVisitor
            .INSTANCE) {
        }
        return this;
      }
      case MERGE_FROM_STREAM: {
        com.google.protobuf.CodedInputStream input =
            (com.google.protobuf.CodedInputStream) arg0;
        com.google.protobuf.ExtensionRegistryLite extensionRegistry =
            (com.google.protobuf.ExtensionRegistryLite) arg1;
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              default: {
                if (!input.skipField(tag)) {
                  done = true;
                }
                break;
              }
              case 8: {

                groupId_ = input.readUInt64();
                break;
              }
              case 16: {

                ownerId_ = input.readUInt64();
                break;
              }
            }
          }
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw new RuntimeException(e.setUnfinishedMessage(this));
        } catch (java.io.IOException e) {
          throw new RuntimeException(
              new com.google.protobuf.InvalidProtocolBufferException(
                  e.getMessage()).setUnfinishedMessage(this));
        } finally {
        }
      }
      case GET_DEFAULT_INSTANCE: {
        return DEFAULT_INSTANCE;
      }
      case GET_PARSER: {
        if (PARSER == null) {    synchronized (GetImMessageReq.class) {
            if (PARSER == null) {
              PARSER = new DefaultInstanceBasedParser(DEFAULT_INSTANCE);
            }
          }
        }
        return PARSER;
      }
    }
    throw new UnsupportedOperationException();
  }


  // @@protoc_insertion_point(class_scope:app.GetImMessageReq)
  private static final GetImMessageReq DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new GetImMessageReq();
    DEFAULT_INSTANCE.makeImmutable();
  }

  public static GetImMessageReq getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static volatile com.google.protobuf.Parser<GetImMessageReq> PARSER;

  public static com.google.protobuf.Parser<GetImMessageReq> parser() {
    return DEFAULT_INSTANCE.getParserForType();
  }
}

