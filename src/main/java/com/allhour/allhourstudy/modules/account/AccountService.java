package com.allhour.allhourstudy.modules.account;

import com.allhour.allhourstudy.infra.config.AppProperties;
import com.allhour.allhourstudy.infra.mail.EmailMessage;
import com.allhour.allhourstudy.infra.mail.EmailService;
import com.allhour.allhourstudy.modules.account.form.*;
import com.allhour.allhourstudy.modules.tag.Tag;
import com.allhour.allhourstudy.modules.zone.Zone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public Account processNewAccount(SignUpForm signUpForm) {
        Account account = saveNewAccount(signUpForm);
        // 이메일인증은 나중에 할 수도있으니 먼저 save
        sendSignUpConfirmEmail(account);
        return account;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = modelMapper.map(signUpForm, Account.class);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.generateEmailCheckToken();
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account account) {
        Context context = new Context(); // model에 내용담아주듯이
        context.setVariable("link", "/check-email-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message","All H our Study 서비스 사용을 위해 링크를 클릭해주세요");
        context.setVariable("host",appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage build = EmailMessage.builder()
                .to(account.getEmail())
                .subject("All H OUR STUDY 회원 가입 인증")
                .message(message)
                 .build();

        emailService.sendEmail(build);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(account);
    }

    public Account getAccount(String nickname) {
        Account byNickname = accountRepository.findByNickname(nickname);
        if (byNickname == null) {
            throw  new IllegalArgumentException(nickname + "에 해당하는 유저가 없습니다");
        }
        return byNickname;
    }

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile, account);
        accountRepository.save(account);
        //merge
    }

    public void updatePassword(Account account, PasswordForm passwordForm) {
        account.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }

    public void updateNickname(Account account, NicknameForm nicknameForm) {
        account.setNickname(nicknameForm.getNickname());
        accountRepository.save(account);
        login(account);
    }

    public void sendLoginLink(Account account) {

        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "이메일로 로그인하기");
        context.setVariable("message","이메일로 로그인하려면 클릭해주세요");
        context.setVariable("host",appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage build = EmailMessage.builder()
                .to(account.getEmail())
                .subject("All H OUR STUDY - 로그인 링크")
                .message(message)
                .build();

        emailService.sendEmail(build);
    }

    public Set<Tag> getTags(Account account) {
        Account byId = accountRepository.findById(account.getId()).orElseThrow();
        return byId.getTags();
    }

    public void addTag(Account account, Tag byTitle) {
        Account newAccount = accountRepository.findById(account.getId()).orElseThrow();
        newAccount.getTags().add(byTitle);
        //dirty checking
    }

    public void removeTag(Account account, Tag tag) {
        Account byId = accountRepository.findById(account.getId()).orElseThrow();
        byId.getTags().remove(tag);
    }

    public Set<Zone> getZones(Account account) {
        Account account1 = accountRepository.findById(account.getId()).orElseThrow();
        return account1.getZones();
    }

    public void addZone(Account account, Zone zone) {
        Account account1 = accountRepository.findById(account.getId()).orElseThrow();
        account1.getZones().add(zone);
    }

    public void removeZone(Account account, Zone zone) {
        Account account1 = accountRepository.findById(account.getId()).orElseThrow();
        account1.getZones().remove(zone);
    }
}
