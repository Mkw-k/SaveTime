package com.save.savetime.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.save.savetime.model.dto.LoginRequestDto;
import com.save.savetime.model.dto.MemberAccount;
import com.save.savetime.model.dto.TokenDTO;
import com.save.savetime.model.dto.UserCommonDTO;
import com.save.savetime.service.common.JwtService;
import com.save.savetime.util.ContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 최초 토큰 생성 해주는 필터로 보임 - 로그인
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	// Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
	// 인증 요청시에 실행되는 함수 => /login
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		log.info("JwtAuthenticationFilter : 진입");

		// request에 있는 username과 password를 파싱해서 자바 Object로 받기
		ObjectMapper om = new ObjectMapper();
		LoginRequestDto loginRequestDto = null;
		try {
			loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
		} catch (Exception e) {
			// request에 있는 데이터를 LoginRequestDto 객체로 변환
			String userName = (String) request.getAttribute("userName");
			String password = (String) request.getAttribute("password");

			e.printStackTrace();
		}

		log.info("JwtAuthenticationFilter : "+loginRequestDto);
		//세션에 유저가 어떤 타입으로 들어왔는지 저장
		ContextUtil.setAttrToSession("userType", loginRequestDto.getUserType());

		// 유저네임패스워드 토큰 생성
		UsernamePasswordAuthenticationToken authenticationToken = 
				new UsernamePasswordAuthenticationToken(
						loginRequestDto.getUsername(),
						loginRequestDto.getPassword());

		log.info("JwtAuthenticationFilter : 토큰생성완료");
		
		// authenticate() 함수가 호출 되면 인증 프로바이더가 유저 디테일 서비스의
		// loadUserByUsername(토큰의 첫번째 파라메터) 를 호출하고
		// UserDetails를 리턴받아서 토큰의 두번째 파라메터(credential)과
		// UserDetails(DB값)의 getPassword()함수로 비교해서 동일하면
		// Authentication 객체를 만들어서 필터체인으로 리턴해준다.
		
		// Tip: 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
		// Tip: 인증 프로바이더의 디폴트 암호화 방식은 BCryptPasswordEncoder
		// 결론은 인증 프로바이더에게 알려줄 필요가 없음.
		Authentication authentication =
				authenticationManager.authenticate(authenticationToken);

		return authentication;
	}

	// 성공헀을경우 JWT Token 생성해서 response에 담아주기
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
											Authentication authResult) throws IOException {
		UserCommonDTO userCommonDTO = new UserCommonDTO();
		setUserCommonForAuthResult(authResult.getPrincipal(), userCommonDTO);

		String userType = (String) ContextUtil.getAttrFromSession("userType");

		//토큰 생성 및 쿠키에 저장
		TokenDTO token = JwtProvider.createTokenInResp(userCommonDTO.getMbId(), userType, response);

		//로그인 처리 - username에 해당하는 refresh Token이 존재할경우 삭제후 생성하여 DB에 저장
		//없을경우 그대로 저장
		jwtService.login(token);

	}

	/**
	 * UserCommon 객체 set
	 */
	private void setUserCommonForAuthResult(Object principal, UserCommonDTO userCommonDTO) {
		String mbName;
		String mbId;

		MemberAccount memberAccount = (MemberAccount) principal;
		mbName = memberAccount.getMember().getName();
		mbId = memberAccount.getUsername();

		userCommonDTO.setMbName(mbName);
		userCommonDTO.setMbId(mbId);
	}


}
