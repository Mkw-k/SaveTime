package com.save.savetime.security.auth;

import com.save.savetime.util.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 약관동의 후 -> 간편로그인 DB 등록시 실패 핸들러
 */
@Slf4j
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //간편로그인 회원가입이 안되었을 경우 (최초)
        //provider 정보를 가지고 회원가입 동의 페이지로 이동 처리
        String exceptionMsg = exception.toString(); // 형식 : ...exceptionType,provider=kakao,providerId=kakao_jkdlFQUIOEUWIOE...
        String exceptionMsgSplit[] = exceptionMsg.split(",");
        String provider = "";
        String providerId = "";
        String name = "";

        log.info("Error@@" + exceptionMsg);

        for (String msg : exceptionMsgSplit) {
            String msgSplit[] = msg.split("=");
            if (msgSplit[0].equals("provider")) provider = msgSplit[1];
            if (msgSplit[0].equals("providerId")) providerId = msgSplit[1];
            if (msgSplit[0].equals("name")) name = msgSplit[1];
        }

        //pass 토큰값 추가 + 카카오 리턴값 세션추가
        HttpSession session = request.getSession();
        String passToken = (String) session.getAttribute("passToken");

        //수정_SNS로그인에 실패했을경우(가입이 안되있는경우) 가입절차 실행_230524
        //provider : kkoSignUp, naverSignUp

        //모드 셋팅
        //FIXME else로 되어있기 때문에 추후에 수정 필요함 - 이부분에서 문제가 발생할수 있음 
        String mode = "";
        if ("kakao".equals(provider)){
            mode = "kkoSignUp";
        }else {
            mode = "naverSignUp";
        }

        String url = "/certify/" + mode;

        //signUpSNS 에서 사용하기 위한 세션값 셋팅
        //TODO 왜 그냥 세션으로는 안되는건지????
        ContextUtil.setAttrToSession("provider", provider);
        ContextUtil.setAttrToSession("id", providerId);
        ContextUtil.setAttrToSession("name", URLDecoder.decode(name, StandardCharsets.UTF_8));
        ContextUtil.setAttrToSession("passToken", passToken);
        ContextUtil.setAttrToSession("remoteAddr", request.getRemoteAddr());

        /*String url = "/app/signUpSNSAction?provider=" + provider + "&providerId=" + providerId;
        url += "&name=" + URLEncoder.encode(name, "UTF-8"); // 한글 인코딩
        url += "&passToken=" + passToken; // 한글 인코딩*/
        setDefaultFailureUrl(url);

        //setDefaultFailureUrl("/"); // 루트 이동

        super.onAuthenticationFailure(request, response, exception);
    }
}