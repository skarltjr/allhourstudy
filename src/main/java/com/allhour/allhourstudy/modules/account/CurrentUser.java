package com.allhour.allhourstudy.modules.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentUser {
}

/**
 * 목적!! 인증된 account를 사용하기위해 @CurrentUser(=Authen~) Account account로 인증정보를!! account에 담겠다
 * 컨트롤러에서 @CurrentUser Account account로 받고 여기서  / 인증정보로부터 사용하고자하는 account 프로퍼티를 뽑아내기 위해선
 * account라는 property를 가진 매개체가 필요하다. -> userAccount
 * 따라서
 * 1. 로그인 할 때 login()에서 시큐리티 컨텍스트 안에 authentication에 인증정보를 저장하는데 이 때 principal로 nickname이 아닌
 * account를 갖고있는 userAccount라는 매개체를 저장
 *
 * 2. 결국 @AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account") Account account가 되는데
 * 만약 현재 인증이 된 상태라면 principal로 UserAccount가 저장되어있고 거기서 account property를 뽑아온다.
 * */