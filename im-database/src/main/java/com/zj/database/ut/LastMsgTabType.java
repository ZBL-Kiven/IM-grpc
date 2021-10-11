package com.zj.database.ut;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * the value 1 means load history messages , 0 meas new
 */
@StringDef(value = {Constance.KEY_OF_PRIVATE_OWNER, Constance.KEY_OF_PRIVATE_FANS, Constance.KEY_OF_SESSIONS})
@Inherited
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface LastMsgTabType {}