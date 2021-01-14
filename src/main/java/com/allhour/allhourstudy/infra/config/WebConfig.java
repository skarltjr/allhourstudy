package com.allhour.allhourstudy.infra.config;

import com.allhour.allhourstudy.modules.notification.NotificationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final NotificationInterceptor notificationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**목적 : static요소들에도 적용하지않도록*/

                                 // StaticResourceLocation.values()가 각각 배열로 값을 갖기때문에 배열들을 배열로갖고있는것을
        List<String> staticList = Arrays.stream(StaticResourceLocation.values())
                                //2차원배열을 1차원배열로 펴주듯이
                .flatMap(StaticResourceLocation::getPatterns)
                                // 리스트로
                .collect(Collectors.toList());
        staticList.add("/node_modules/**");

        registry.addInterceptor(notificationInterceptor)
                .excludePathPatterns(staticList); // parameter로 스트링 리스트만받는다
    }
}
