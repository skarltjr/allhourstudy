package com.allhour.allhourstudy.infra.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean  //모델 매퍼 매 번 새로만들긴 그러니까 빈으로 등록해서 사용
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
        //UNDERSCORE -> ex) studyEnrollResultByEmail랑 매핑을 해야하는데 Email이랑 매핑하는경우 등 잘
        // 못알아 먹을 수 있다. 그래서 무엇으로 구분할지 정해준다
        //CamelCase로 설정해뒀으니  언더스코어로 구분해라라고 지정해주면
        //언더스코어가 없으면 하나 즉 studyEnrollResultByEmail은 한 놈
        return modelMapper;
    }
}
