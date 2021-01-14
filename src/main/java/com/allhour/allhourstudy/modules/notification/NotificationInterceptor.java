package com.allhour.allhourstudy.modules.notification;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {

    private final NotificationRepository notificationRepository;

    /** 목적 : notification의 check가 안된 알림이 존재하면 모든 요청에서 알 수 있도록*/

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && modelAndView != null && !modelAndView.getViewName().startsWith("redirect:") &&
                authentication.getPrincipal() instanceof UserAccount) {
            Account account = ((UserAccount) authentication.getPrincipal()).getAccount();
            int count = notificationRepository.countByAccountAndChecked(account, false);
            modelAndView.addObject("hasNotification",count>0);
        }
    }
}
