package com.save.savetime.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.save.savetime.security.CustomUsernamePasswordAuthenticationToken;
import com.save.savetime.security.service.LoginService;
import com.save.savetime.security.service.JwtService;
import com.save.savetime.util.ContextUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import static com.save.savetime.common.Functions.getCookieValue;
import static com.save.savetime.common.Functions.setLogoutRedirectUrl;

/**
 * 인가
 * 확인헤서 토큰이 정상여부 확인 및 후처리
 * 다른 비즈니스 로직들 요청시
 *
 * Access Token 만료시 또는 불일치 할경우 
 * */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter{
	
	private LoginService loginService;
	private JwtService jwtService;

	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, LoginService loginService,
								  JwtService jwtService) {
		super(authenticationManager);
		this.loginService = loginService;
		this.jwtService = jwtService;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String username = "";
		List<String> passUrls = Arrays.asList(
				"/app/emailLogin"
				, "/app/certify/"
				, "/app/certify/signUp"
				, "/app/certify/findId"
				, "/app/findPw"
				, "/app/niceSuccess/"
				, "/app/signUp/"
				, "/app/signUpForm"
				, "/app/signUpAction"
				, "/app/signUpComplete/"
				, "/app/signUpSNS"
				, "/oauth2/authorization"
				, "/hospital/login"
				, "/pharmacy/login"
				, "/admin/login"
				, "/app/ajaxemaillogin"
				, "/js"
				, "/css"
				, "/webfonts"
		        , "/favicon"
		        , "/firebase"
				, "/modules"
				, "/obj");

		List<String> logoutAfterURL = Arrays.asList("/hospital", "/pharmacy", "/admin");

		//0. 확인이 필요없는 요청은 필터 처리를 건너뛰고 바로 다음 필터로 넘어감
		String requestURI = request.getRequestURI();
		if (passUrls.stream().anyMatch(requestURI::startsWith)
				|| logoutAfterURL.contains(requestURI)) {
			chain.doFilter(request, response);
			return;
		}

		//1. 쿠키확인
		String bearerAccessToken = getCookieValue(request, JwtProperties.ACCESS_TOKEN_STRING);

		//2. 전부 없을경우 비로그인 상태 리턴
//		if(bearerAccessToken == null || !bearerAccessToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
		if(bearerAccessToken == null){
			chain.doFilter(request, response);
			return;
		}

		//UTF-8 디코드('+' 문자 -> 띄어쓰기로 변경)
		bearerAccessToken = URLDecoder.decode(bearerAccessToken, "UTF-8");
		logger.info("accessToken : "+bearerAccessToken);
		
		//3. 헤더에서 AccessToken값을 불러온뒤 Bearer를 제거해준다
		String accessToken = bearerAccessToken.replace(JwtProperties.TOKEN_PREFIX, "");
		

		DecodedJWT decodedJWT = null;
		try {
			//4. Access 토큰 검증
			decodedJWT = JWT.require(Algorithm.HMAC512(JwtProperties.ACCESS_SECRET)).build().verify(accessToken);
			
			// @Token이 유효한데 세션값이 없을경우 방지_Token이 유효하면 세션 재셋팅
			String userType = decodedJWT.getClaim("usertype").asString();
			ContextUtil.setAttrToSession("userType", userType);

		} catch (Exception e) {
			//5. Access 토큰 검증 실패경우 -> Refresh 토큰 사용 검증
			logger.info("Access 토큰 검증 실패!!");
			try {
				//5. 쿠키확인
				String refreshToken = URLDecoder.decode(getCookieValue(request, JwtProperties.REFRESH_TOKEN_STRING), "UTF-8");
				refreshToken = refreshToken.replace(JwtProperties.TOKEN_PREFIX, "");

				//6. 정상 Refresh 토큰인지 확인하기 위해 디코딩 진행
				DecodedJWT refreshDecodedJWT = JWT.require(Algorithm.HMAC512(JwtProperties.REFRESH_SECRET)).build().verify(refreshToken);
				username = refreshDecodedJWT.getClaim("id").asString();

				// @Token이 유효한데 세션값이 없을경우 방지_Token이 유효하면 세션 재셋팅
				String userType = refreshDecodedJWT.getClaim("usertype").asString();
				ContextUtil.setAttrToSession("userType", userType);

				//7. refreshToken DB와 비교 검증 후 AccessToken재발급
				jwtService.validateRefreshTokenInDbAndReCreationAccessToken(refreshToken, response);

			} catch (Exception e2) {
				logger.info("Refresh 토큰 검증 실패!!");
				setLogoutRedirectUrl(requestURI, response);
				return;
//				Functions.setRedirectUrl(request.getRequestURI(), response);
//				chain.doFilter(request, response);
//				ContextUtil.setSessionAttribute(request, "expireTokenYn", "Y", 1800);
//				return;
			}
		}

		//Access Token이 살아있을경우에만
		if(decodedJWT != null){
			username = decodedJWT.getClaim("id").asString();
		}

		//username값이 존재할경우
		if(username != null) {

			UserDetails userDetails = loginService.loadUserByUsername(username);

			// 인증은 토큰 검증시 끝. 인증을 하기 위해서가 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해 
			// 아래와 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장!
			//MemberAccount memberPrincipalDetails = new MemberAccount(member);
			CustomUsernamePasswordAuthenticationToken authentication = new CustomUsernamePasswordAuthenticationToken(
							userDetails, //나중에 컨트롤러에서 DI해서 쓸 때 사용하기 편함.
							null, // 패스워드는 모르니까 null 처리, 어차피 지금 인증하는게 아니니까!!
							userDetails.getAuthorities());


			authentication.setDetails(new WebAuthenticationDetails(request));

			// 강제로 시큐리티의 세션에 접근하여 값 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
	
		chain.doFilter(request, response);
	}
}
