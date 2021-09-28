package com.zj.ccIm.annos;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * the value 1 means load history messages , 0 meas new
 */
@IntDef(value = {0, 1})
@Inherited
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface MsgFetchType {}
