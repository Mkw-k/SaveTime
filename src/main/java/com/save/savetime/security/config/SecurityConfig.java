package com.save.savetime.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.save.savetime.security.CommonAuthFailureHandler;
import com.save.savetime.security.CustomAccessDeniedHandler;
import com.save.savetime.security.CustomAuthenticationEntryPoint;
import com.save.savetime.security.SessionListner;
import com.save.savetime.security.auth.OAuth2FailureHandler;
import com.save.savetime.security.auth.PrincipalOauth2UserService;
import com.save.savetime.security.factory.UrlResourcesMapFactoryBean;
import com.save.savetime.security.jwt.JwtAuthenticationFilter;
import com.save.savetime.security.jwt.JwtAuthorizationFilter;
import com.save.savetime.security.jwt.JwtLogoutSuccessHandler;
import com.save.savetime.security.metadatasource.UrlFilterInvocationSecurityMetadatsSource;
import com.save.savetime.security.service.JwtService;
import com.save.savetime.security.service.LoginService;
import com.save.savetime.security.service.SecurityResourceService;
import com.save.savetime.security.voter.IpAddressVoter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSessionListener;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 앱 로그인
 *
 * form에 있는 action을 시큐리티가 가로챔 (컨트롤러 라우터 만들 필요없음)
 * 로그인 성공하면 시큐리티에서 자동으로 세션 생성 (세션에 등록되는 Type : UserDetails)
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // security 적용
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 로그인 처리 핸들러
    // 1. 이메일로 로그인
    @Autowired
    LoginService loginService;
    // 2. 간편로그인
    @Autowired
    PrincipalOauth2UserService principalOauth2UserService;
    @Autowired
    SecurityResourceService securityResourceService;
    // 로그인 실패 핸들러
    // 1. 이메일로 로그인
    private final CommonAuthFailureHandler customFailureHandler;
    // 2. 간편 로그인
    private final OAuth2FailureHandler oAuth2FailureHandler;

    private final DataSource dataSource;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("SecurityConfig - 회원");
        log.info("SecurityConfig DataSource Connection : " + dataSource.getConnection().getMetaData().getURL());

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager(), jwtService);
        jwtAuthenticationFilter.setAuthenticationFailureHandler(customFailureHandler);


        http
            .csrf().disable() // 위조요청 방지
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .formLogin().disable()
                .httpBasic().disable()
                .addFilter(jwtAuthenticationFilter)
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), loginService, jwtService))
            /*.authorizeRequests() // 인가요청 확인
                // 통과
                .antMatchers("/api/fcm/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/dues/retrieve-dues").authenticated()
                .antMatchers("/dues/**").hasRole("MANAGER")
                .antMatchers("/login").permitAll()
                .antMatchers("/app/emailLogin").permitAll()
                .antMatchers("/app/certify/**").permitAll()
                .antMatchers("/").permitAll()
                // 해당 요청들 로그인 체크
                .antMatchers("/app/mypage").authenticated()
                .antMatchers("/login-success").authenticated()
                // /app 요청에 대해서는 ROLE 확인 -> 적용하기에 로그인을 안해도 확인가능한 페이지가 많음
                //.antMatchers("/app/**").hasRole("MEMBER")
                // 나머지 로그인 체크 X
                .anyRequest().permitAll()
            .and()*/
                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler("")) // 권한체크
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint("")) // 인증체크
                .accessDeniedPage("/denied")
            .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessHandler(new JwtLogoutSuccessHandler()) // JwtLogoutSuccessHandler 추가
                .logoutSuccessUrl("/login")
                .and()
            .rememberMe()   // 자동로그인
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(86400 * 30) // 유효시간 (86400 = 24시간)
                .userDetailsService(loginService)
                .tokenRepository(tokenRepository())
            .and()

            .oauth2Login()  // 간편로그인
                .loginPage("/Login")
                .defaultSuccessUrl("/login-success")
                .failureHandler(oAuth2FailureHandler) // 로그인 실패 핸들러
                .userInfoEndpoint() // 로그인 성공 후 사용자정보를 가져온다
                .userService(principalOauth2UserService); // 사용자정보 처리

        /*http
            .sessionManagement()
            .maximumSessions(10) // 최대허용개수
            .maxSessionsPreventsLogin(false) // 동시로그인차단 (true: 현재사용자 로그인불가, false: 기존사용자 세션만료)
            .expiredUrl("/");   // 세션만료 시 이동할 페이지
            */


        http
                .addFilterBefore(customFilterSecurityInterceptor(securityResourceService), FilterSecurityInterceptor.class);
    }

    @Bean
    public FilterSecurityInterceptor customFilterSecurityInterceptor(SecurityResourceService securityResourceService) throws Exception {

        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        filterSecurityInterceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource(securityResourceService));
        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
        filterSecurityInterceptor.setAuthenticationManager(authenticationManagerBean());
        return filterSecurityInterceptor;
    }

    private AccessDecisionManager affirmativeBased() {
        AffirmativeBased affirmativeBased = new AffirmativeBased(getAccessDecistionVoters());
        return affirmativeBased;
    }

    private List<AccessDecisionVoter<?>> getAccessDecistionVoters() {

        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        accessDecisionVoters.add(new IpAddressVoter(securityResourceService));
        accessDecisionVoters.add(roleVoter());
        return accessDecisionVoters;
    }

    @Bean
    public AccessDecisionVoter<? extends Object> roleVoter() {

        RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierachy());

        return roleHierarchyVoter;
    }

    @Bean
    public RoleHierarchyImpl roleHierachy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        return roleHierarchy;
    }

    @Bean
    public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource(SecurityResourceService securityResourceService) throws Exception {
        return new UrlFilterInvocationSecurityMetadatsSource(urlResourceMapFactoryBean(securityResourceService).getObject(), securityResourceService);
    }

    private UrlResourcesMapFactoryBean urlResourceMapFactoryBean(SecurityResourceService securityResourceService) {
        UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
        urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);

        return urlResourcesMapFactoryBean;
    }


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(loginService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //시큐리티가 로그인 과정에서 password를 가로챌때 어떤 해쉬로 암호화 했는지 확인
        return new BCryptPasswordEncoder();
    }

    // 세션타임 확장
    @Bean
    public HttpSessionListener httpSessionListener() {
        return new SessionListner();
    }

    // JDBC 기반 자동로그인
    @Bean
    @Transactional(readOnly = false)
    public PersistentTokenRepository tokenRepository() throws Exception {
        log.info("JdbcTokenRepository DataSource : " + dataSource.getConnection().getMetaData().getURL());
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**"
                , "/css/**"
                , "/webfonts/**"
                , "/favicon/**"
                , "/firebase/**"
                , "/firebase-messaging-sw.js"
                , "/asset/**"
                , "/oauth2/authorization"
                , "/modules"
                , "/obj"
        );

    }

}
