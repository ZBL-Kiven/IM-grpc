@file:Suppress("unused")

package com.zj.database.entity


class TextContent {
    var text: String? = null
}

class ImgContent : BaseFileContent()

class VideoContent : BaseFileContent() {
    var thumbnail: String? = null
    var duration: Long = 0
}

class AudioContent : BaseFileContent() {
    var duration: Long = 0
}

class CCVideoContent : BaseFileContent() {
    var videoId: String? = null
    var videoTitle: String? = null
    var videoDescribe: String? = null
    var duration: Double = 0.0
    var imgPreviewRemoteStorageUrl: String? = null
}

class QuestionContent {

    var textContent: TextContent? = null

    //0 正常   1 已回复   2 已过期
    var questionStatus: Int = 0
    var questionId: Int = 0
    var spark: Int = 0
    var diamond: Int = 0
    var published: Boolean = false
    var sendTime: Long = 0
    var expireTime: Long = 0
    var answerMsgType: String? = null
    var contentType: String? = null
}

class EmotionMessage {
    var id = 0
    var emotionId = 0
    var icon: String? = null
    var url: String? = null
}

class GiftMessage {
    var giftId: Int = 0
    var giftImage: String? = ""
    var amount: Int = 1
    var multiLanguage: MutableList<MultiLanguage>? = null
    var bundle: String? = null

    fun getName(area: String): String? {
        return multiLanguage?.first { it.area == area }?.name
    }
}

data class MultiLanguage(var area: String = "", var name: String = "")

class SenderInfo {
    var senderId: Int = -1
    var senderName: String? = null
    var senderAvatar: String? = null
    var senderPlatform: String? = null
}

open class BaseFileContent {
    var url: String? = null
    var width: Int = 0
    var height: Int = 0
}