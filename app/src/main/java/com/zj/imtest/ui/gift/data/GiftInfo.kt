package com.zj.imtest.ui.gift.data

import com.zj.database.entity.GiftMessage
import com.zj.database.entity.MultiLanguage
import com.zj.imtest.ui.gift.i.GiftShaderIn
import kotlin.random.Random

class GiftInfo : GiftShaderIn {

    var id: Int? = -1

    var sort: Int = 0

    var multiLanguage: List<MultiLanguage>? = null

    var price: Int = 0

    /**
     * 0:diamond 1:coins
     * */
    var priceUnit: Int = 0

    /**
     * 0:normal   1:vip
     * */
    var giftType: Int = 0

    var image: String? = "https://img1.baidu.com/it/u=4286116802,1637698183&fm=253&fmt=auto&app=120&f=JPEG?w=400&h=400"

    var num = Random.nextInt(9) + 1

    var avatar: String? = "https://img0.baidu.com/it/u=4117713405,2961605581&fm=253&fmt=auto&app=138&f=JPEG?w=400&h=400"

    var senderId: Int? = 0

    var bundle: String? = "https://media.clipclaps.com/20220211/3/f/c/5/0/3fc505f56eb949c5803c150c00b311c4.lottie"

    override fun getUniqueId(): Int {
        return id ?: -1
    }

    override fun getSource(): String? {
        return bundle
    }

    override var duration: Int = 2500

    fun toReqBean(): GiftMessage {
        return GiftMessage().apply {
            this.giftImage = image
            this.giftId = getUniqueId()
            this.bundle = getSource()
            this.amount = num
            this.multiLanguage = multiLanguage

        }
    }

    override fun typeEquals(other: Any?): Boolean {
        return (other is GiftInfo && senderId == other.senderId && id == other.id)
    }
}