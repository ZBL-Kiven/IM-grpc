package com.zj.database.entity


class TextContent {
    var text: String? = null
}

class ImgContent : BaseFileContent()

class VideoContent : BaseFileContent() {
    var thumbnail: String? = null
}

class AudioContent : BaseFileContent()

class CCVideoContent : BaseFileContent() {
    var videoId: String? = null
    var videoTitle: String? = null
    var videoDescribe: String? = null
    var imgPreviewRemoteStorageUrl: String? = null
}

class QuestionContent : BaseFileContent() {

    var textContent: TextContent? = null

    //0 正常   1 已回复   2 已过期
    var questionStatus: Int = 0
    var questionId: Int = 0
    var spark: Int = 0
    var diamond: Int = 0
    var published: Boolean = true
    var sendTime: Long = 0
    var expireTime: Long = 0
    var answerMsgType: String? = null
    var contentType: String? = null
}


class SenderInfo : BaseFileContent() {
    var senderId: Int = -1
    var senderName: String? = null
    var senderAvatar: String? = null
    var senderPlatform: String? = null
}

open class BaseFileContent {
    var url: String? = null
    var width: Int = 0
    var height: Int = 0
    var duration: Long = 0
}