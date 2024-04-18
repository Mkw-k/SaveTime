package com.save.savetime.common;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Adapter
 */

@Target({ElementType.PARAMETER}) // 생성되는 위치
@Retention(RetentionPolicy.RUNTIME) // 실제 적용되고 유지되는 범위 (컴파일 후에도 참조 가능하도록 런타임)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : member")
public @interface AuthMember {
}
