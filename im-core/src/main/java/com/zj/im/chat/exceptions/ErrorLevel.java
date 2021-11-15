package com.zj.im.chat.exceptions;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {IMException.ERROR_LEVEL_ALERT, IMException.ERROR_LEVEL_DEADLY, IMException.ERROR_LEVEL_REINSTALL})
@Target(value = ElementType.PARAMETER)
@Inherited
public @interface ErrorLevel {}
