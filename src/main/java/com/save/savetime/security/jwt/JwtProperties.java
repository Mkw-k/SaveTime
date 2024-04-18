package com.save.savetime.security.jwt;

public interface JwtProperties {
//	String ACCESS_SECRET = "b3e9581b83256993f51b5a6437e445ca4c66e60083e8d92a062644890fee3749"; // 우리 서버만 알고 있는 비밀값 -> 변경해야함 256비트 키임
	String ACCESS_SECRET = "ee1afe251ba9d0de482df54a128997d3202bbbf31795f4e43ef4b83912b75ee9caa726530890451540e2708e07f2a001639400deb0d675b4a4a982d10b8a31c5"; // 우리 서버만 알고 있는 비밀값
	String REFRESH_SECRET = "ee1afe251ba9d0de482df54a128997d3202bbbf31795f4e43ef4b83912b75ee9caa726530890451540e2708e07f2a001639400deb0d675b4a4a982d10b8a31c5";
	int ACCESS_EXPIRATION_TIME = 10*(60*1000); // 10분
	int REFRESH_EXPIRATION_TIME = 86400000*10; // 10일 (1/1000초)
	String TOKEN_PREFIX = "Bearer ";
	String ACCESS_TOKEN_STRING = "Authorization";
	String REFRESH_TOKEN_STRING = "ReAuthorization";
}
