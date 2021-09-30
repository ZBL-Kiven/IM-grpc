package com.zj.ccIm.annos;

import androidx.annotation.StringDef;

import com.zj.ccIm.core.Comment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * the value 1 means load history messages , 0 meas new
 */
@StringDef(value = {Comment.DELETE_OWNER_SESSION, Comment.DELETE_FANS_SESSION})
@Inherited
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface DeleteSessionType {}
