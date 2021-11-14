package com.zj.ccIm.error

import com.zj.im.chat.exceptions.IMException

class AuthenticationError(case: String) : IMException("you're kicked out by another device ,message = $case!")