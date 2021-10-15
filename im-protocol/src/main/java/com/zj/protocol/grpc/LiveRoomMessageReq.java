// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_api.proto

package com.zj.protocol.grpc;

/**
 * Protobuf type {@code app.LiveRoomMessageReq}
 */
public final class LiveRoomMessageReq extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:app.LiveRoomMessageReq)
    LiveRoomMessageReqOrBuilder {
private static final long serialVersionUID = 0L;
  // Use LiveRoomMessageReq.newBuilder() to construct.
  private LiveRoomMessageReq(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private LiveRoomMessageReq() {
    op_ = 0;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new LiveRoomMessageReq();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private LiveRoomMessageReq(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {
            int rawValue = input.readEnum();

            op_ = rawValue;
            break;
          }
          case 16: {

            roomId_ = input.readInt32();
            break;
          }
          case 24: {

            liveId_ = input.readInt64();
            break;
          }
          case 32: {

            liverIsMe_ = input.readBool();
            break;
          }
          case 40: {

            userId_ = input.readUInt64();
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return IMProtoc.internal_static_app_LiveRoomMessageReq_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return IMProtoc.internal_static_app_LiveRoomMessageReq_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            LiveRoomMessageReq.class, LiveRoomMessageReq.Builder.class);
  }

  /**
   * Protobuf enum {@code app.LiveRoomMessageReq.Op}
   */
  public enum Op
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>JOIN = 0;</code>
     */
    JOIN(0),
    /**
     * <code>LEAVE = 1;</code>
     */
    LEAVE(1),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>JOIN = 0;</code>
     */
    public static final int JOIN_VALUE = 0;
    /**
     * <code>LEAVE = 1;</code>
     */
    public static final int LEAVE_VALUE = 1;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static Op valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static Op forNumber(int value) {
      switch (value) {
        case 0: return JOIN;
        case 1: return LEAVE;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Op>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        Op> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Op>() {
            public Op findValueByNumber(int number) {
              return Op.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalStateException(
            "Can't get the descriptor of an unrecognized enum value.");
      }
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return LiveRoomMessageReq.getDescriptor().getEnumTypes().get(0);
    }

    private static final Op[] VALUES = values();

    public static Op valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private Op(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:app.LiveRoomMessageReq.Op)
  }

  public static final int OP_FIELD_NUMBER = 1;
  private int op_;
  /**
   * <code>.app.LiveRoomMessageReq.Op op = 1;</code>
   * @return The enum numeric value on the wire for op.
   */
  @java.lang.Override public int getOpValue() {
    return op_;
  }
  /**
   * <code>.app.LiveRoomMessageReq.Op op = 1;</code>
   * @return The op.
   */
  @java.lang.Override public LiveRoomMessageReq.Op getOp() {
    @SuppressWarnings("deprecation")
    LiveRoomMessageReq.Op result = LiveRoomMessageReq.Op.valueOf(op_);
    return result == null ? LiveRoomMessageReq.Op.UNRECOGNIZED : result;
  }

  public static final int ROOMID_FIELD_NUMBER = 2;
  private int roomId_;
  /**
   * <code>int32 roomId = 2;</code>
   * @return The roomId.
   */
  @java.lang.Override
  public int getRoomId() {
    return roomId_;
  }

  public static final int LIVEID_FIELD_NUMBER = 3;
  private long liveId_;
  /**
   * <code>int64 liveId = 3;</code>
   * @return The liveId.
   */
  @java.lang.Override
  public long getLiveId() {
    return liveId_;
  }

  public static final int LIVERISME_FIELD_NUMBER = 4;
  private boolean liverIsMe_;
  /**
   * <code>bool liverIsMe = 4;</code>
   * @return The liverIsMe.
   */
  @java.lang.Override
  public boolean getLiverIsMe() {
    return liverIsMe_;
  }

  public static final int USERID_FIELD_NUMBER = 5;
  private long userId_;
  /**
   * <code>uint64 userId = 5;</code>
   * @return The userId.
   */
  @java.lang.Override
  public long getUserId() {
    return userId_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (op_ != LiveRoomMessageReq.Op.JOIN.getNumber()) {
      output.writeEnum(1, op_);
    }
    if (roomId_ != 0) {
      output.writeInt32(2, roomId_);
    }
    if (liveId_ != 0L) {
      output.writeInt64(3, liveId_);
    }
    if (liverIsMe_ != false) {
      output.writeBool(4, liverIsMe_);
    }
    if (userId_ != 0L) {
      output.writeUInt64(5, userId_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (op_ != LiveRoomMessageReq.Op.JOIN.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(1, op_);
    }
    if (roomId_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, roomId_);
    }
    if (liveId_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(3, liveId_);
    }
    if (liverIsMe_ != false) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(4, liverIsMe_);
    }
    if (userId_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt64Size(5, userId_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof LiveRoomMessageReq)) {
      return super.equals(obj);
    }
    LiveRoomMessageReq other = (LiveRoomMessageReq) obj;

    if (op_ != other.op_) return false;
    if (getRoomId()
        != other.getRoomId()) return false;
    if (getLiveId()
        != other.getLiveId()) return false;
    if (getLiverIsMe()
        != other.getLiverIsMe()) return false;
    if (getUserId()
        != other.getUserId()) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + OP_FIELD_NUMBER;
    hash = (53 * hash) + op_;
    hash = (37 * hash) + ROOMID_FIELD_NUMBER;
    hash = (53 * hash) + getRoomId();
    hash = (37 * hash) + LIVEID_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getLiveId());
    hash = (37 * hash) + LIVERISME_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
        getLiverIsMe());
    hash = (37 * hash) + USERID_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getUserId());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static LiveRoomMessageReq parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static LiveRoomMessageReq parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static LiveRoomMessageReq parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static LiveRoomMessageReq parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static LiveRoomMessageReq parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static LiveRoomMessageReq parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static LiveRoomMessageReq parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static LiveRoomMessageReq parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static LiveRoomMessageReq parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static LiveRoomMessageReq parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static LiveRoomMessageReq parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static LiveRoomMessageReq parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(LiveRoomMessageReq prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code app.LiveRoomMessageReq}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:app.LiveRoomMessageReq)
          LiveRoomMessageReqOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return IMProtoc.internal_static_app_LiveRoomMessageReq_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return IMProtoc.internal_static_app_LiveRoomMessageReq_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              LiveRoomMessageReq.class, LiveRoomMessageReq.Builder.class);
    }

    // Construct using com.zj.protocol.grpc.LiveRoomMessageReq.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      op_ = 0;

      roomId_ = 0;

      liveId_ = 0L;

      liverIsMe_ = false;

      userId_ = 0L;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return IMProtoc.internal_static_app_LiveRoomMessageReq_descriptor;
    }

    @java.lang.Override
    public LiveRoomMessageReq getDefaultInstanceForType() {
      return LiveRoomMessageReq.getDefaultInstance();
    }

    @java.lang.Override
    public LiveRoomMessageReq build() {
      LiveRoomMessageReq result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public LiveRoomMessageReq buildPartial() {
      LiveRoomMessageReq result = new LiveRoomMessageReq(this);
      result.op_ = op_;
      result.roomId_ = roomId_;
      result.liveId_ = liveId_;
      result.liverIsMe_ = liverIsMe_;
      result.userId_ = userId_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof LiveRoomMessageReq) {
        return mergeFrom((LiveRoomMessageReq)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(LiveRoomMessageReq other) {
      if (other == LiveRoomMessageReq.getDefaultInstance()) return this;
      if (other.op_ != 0) {
        setOpValue(other.getOpValue());
      }
      if (other.getRoomId() != 0) {
        setRoomId(other.getRoomId());
      }
      if (other.getLiveId() != 0L) {
        setLiveId(other.getLiveId());
      }
      if (other.getLiverIsMe() != false) {
        setLiverIsMe(other.getLiverIsMe());
      }
      if (other.getUserId() != 0L) {
        setUserId(other.getUserId());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      LiveRoomMessageReq parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (LiveRoomMessageReq) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int op_ = 0;
    /**
     * <code>.app.LiveRoomMessageReq.Op op = 1;</code>
     * @return The enum numeric value on the wire for op.
     */
    @java.lang.Override public int getOpValue() {
      return op_;
    }
    /**
     * <code>.app.LiveRoomMessageReq.Op op = 1;</code>
     * @param value The enum numeric value on the wire for op to set.
     * @return This builder for chaining.
     */
    public Builder setOpValue(int value) {
      
      op_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>.app.LiveRoomMessageReq.Op op = 1;</code>
     * @return The op.
     */
    @java.lang.Override
    public LiveRoomMessageReq.Op getOp() {
      @SuppressWarnings("deprecation")
      LiveRoomMessageReq.Op result = LiveRoomMessageReq.Op.valueOf(op_);
      return result == null ? LiveRoomMessageReq.Op.UNRECOGNIZED : result;
    }
    /**
     * <code>.app.LiveRoomMessageReq.Op op = 1;</code>
     * @param value The op to set.
     * @return This builder for chaining.
     */
    public Builder setOp(LiveRoomMessageReq.Op value) {
      if (value == null) {
        throw new NullPointerException();
      }
      
      op_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>.app.LiveRoomMessageReq.Op op = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearOp() {
      
      op_ = 0;
      onChanged();
      return this;
    }

    private int roomId_ ;
    /**
     * <code>int32 roomId = 2;</code>
     * @return The roomId.
     */
    @java.lang.Override
    public int getRoomId() {
      return roomId_;
    }
    /**
     * <code>int32 roomId = 2;</code>
     * @param value The roomId to set.
     * @return This builder for chaining.
     */
    public Builder setRoomId(int value) {
      
      roomId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 roomId = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearRoomId() {
      
      roomId_ = 0;
      onChanged();
      return this;
    }

    private long liveId_ ;
    /**
     * <code>int64 liveId = 3;</code>
     * @return The liveId.
     */
    @java.lang.Override
    public long getLiveId() {
      return liveId_;
    }
    /**
     * <code>int64 liveId = 3;</code>
     * @param value The liveId to set.
     * @return This builder for chaining.
     */
    public Builder setLiveId(long value) {
      
      liveId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 liveId = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearLiveId() {
      
      liveId_ = 0L;
      onChanged();
      return this;
    }

    private boolean liverIsMe_ ;
    /**
     * <code>bool liverIsMe = 4;</code>
     * @return The liverIsMe.
     */
    @java.lang.Override
    public boolean getLiverIsMe() {
      return liverIsMe_;
    }
    /**
     * <code>bool liverIsMe = 4;</code>
     * @param value The liverIsMe to set.
     * @return This builder for chaining.
     */
    public Builder setLiverIsMe(boolean value) {
      
      liverIsMe_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bool liverIsMe = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearLiverIsMe() {
      
      liverIsMe_ = false;
      onChanged();
      return this;
    }

    private long userId_ ;
    /**
     * <code>uint64 userId = 5;</code>
     * @return The userId.
     */
    @java.lang.Override
    public long getUserId() {
      return userId_;
    }
    /**
     * <code>uint64 userId = 5;</code>
     * @param value The userId to set.
     * @return This builder for chaining.
     */
    public Builder setUserId(long value) {
      
      userId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint64 userId = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearUserId() {
      
      userId_ = 0L;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:app.LiveRoomMessageReq)
  }

  // @@protoc_insertion_point(class_scope:app.LiveRoomMessageReq)
  private static final LiveRoomMessageReq DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new LiveRoomMessageReq();
  }

  public static LiveRoomMessageReq getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<LiveRoomMessageReq>
      PARSER = new com.google.protobuf.AbstractParser<LiveRoomMessageReq>() {
    @java.lang.Override
    public LiveRoomMessageReq parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new LiveRoomMessageReq(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<LiveRoomMessageReq> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<LiveRoomMessageReq> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public LiveRoomMessageReq getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

