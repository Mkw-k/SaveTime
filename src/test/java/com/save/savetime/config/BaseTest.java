package com.save.savetime.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
//@AutoConfigureRestDocs
//@Import({RestDocsAutoConfiguration.class, RestDocsConfiguration.class})
//@Import(RestDocsConfiguration.class)
//@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test") //application-test라는 프로파일을 사용함
@Disabled
public class BaseTest{
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    //@Autowired
    //protected RestDocumentationResultHandler restDocs;
}