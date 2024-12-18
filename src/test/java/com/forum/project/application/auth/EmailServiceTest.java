//package com.forum.project.application.auth;
//
//import com.forum.project.application.security.RandomStringGenerator;
//import com.forum.project.domain.exception.ApplicationException;
//import com.forum.project.domain.exception.ErrorCode;
//import com.forum.project.presentation.config.TestRedisConfig;
//import com.forum.project.presentation.dtos.EmailVerification;
//import jakarta.mail.BodyPart;
//import jakarta.mail.MessagingException;
//import jakarta.mail.Multipart;
//import jakarta.mail.Session;
//import jakarta.mail.internet.MimeMessage;
//import jakarta.mail.internet.MimeMultipart;
//import org.jsoup.Jsoup;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//import redis.embedded.RedisServer;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@TestPropertySource(properties = "spring.mail.username=test@example.com")
//class EmailServiceTest {
//
//    private static final Logger log = LoggerFactory.getLogger(EmailServiceTest.class);
//
//    @Nested
//    @ExtendWith(SpringExtension.class)
//    @Import(TestRedisConfig.class)
//    @SpringBootTest
//    @ActiveProfiles("test")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    class IntegrationTest {
//
//        @Autowired
//        private RedisTemplate<String, Object> redisTemplate;
//
//        @MockBean
//        private JavaMailSender javaMailSender;
//
//        @Autowired
//        private EmailService emailService;
//
//        @Autowired
//        private TestRedisConfig testRedisConfig;
//
//        private final String REDIS_PREFIX = "verified:";
//        private final String code = "123456";
//        private final String fromEmail = "sender@example.com";
//        private final String toEmail = "receiver@example.com";
//
//        @AfterAll
//        public void tearDown() {
//            RedisServer redisServer = testRedisConfig.getRedisServer();
//            if (redisServer != null && redisServer.isActive()) {
//                log.info("Stopping Redis server after tests...");
//                redisServer.stop();
//            }
//        }
//
//        private void extractTextPart(BodyPart bodyPart, String code) throws MessagingException, IOException {
//            Object bodyPartContent = bodyPart.getContent();
//            if (bodyPartContent instanceof String htmlContent) {
//                String plainText = Jsoup.parse(htmlContent).text();
//                assertTrue(plainText.contains(code));
//                System.out.println("HTML Text: " + plainText);
//            } else if (bodyPartContent instanceof BodyPart) {
//                log.info("Found nested BodyPart inside text/plain. Recursively processing...");
//                extractTextPart((BodyPart) bodyPartContent, code);
//            } else if (bodyPartContent instanceof Multipart) {
//                log.info("Found nested Multipart inside text/plain. Processing each part...");
//                MimeMultipart nestedMultipart = (MimeMultipart) bodyPartContent;
//                for (int i = 0; i < nestedMultipart.getCount(); i++) {
//                    extractTextPart(nestedMultipart.getBodyPart(i), code);
//                }
//            } else if (bodyPartContent instanceof InputStream) {
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) bodyPartContent))) {
//                    StringBuilder sb = new StringBuilder();
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        sb.append(line).append("\n");
//                    }
//                    System.out.println("Text Content (from InputStream): " + sb.toString());
//                }
//            } else {
//                log.warn("Unexpected content type inside text/plain: {}", bodyPartContent.getClass().getName());
//            }
//        }
//
//        @Test
//        void sendVerificationCode_shouldStoreCodeInRedisAndSendEmail() throws MessagingException, IOException {
//            String redisKey = "verified:" + toEmail;
//            ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
//            MimeMessage mimeMessage = new MimeMessage((Session) null);
//            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//            emailService.sendVerificationCode(toEmail);
//
//            EmailVerification emailVerification = (EmailVerification) redisTemplate.opsForValue().get(redisKey);
//            assert emailVerification != null;
//            assertNotNull(emailVerification.getVerificationCode(), "Verification code should be stored in Redis.");
//
//            verify(javaMailSender, times(1)).send(captor.capture());
//            MimeMessage sentMessage = captor.getValue();
//            Object content = sentMessage.getContent();
//            Multipart multipart = (Multipart) content;
//
//            assertNotNull(sentMessage, "MimeMessage should not be null.");
//            assertTrue(sentMessage.getSubject().contains("Verification Email"));
//            for (int i = 0; i < multipart.getCount(); i++) {
//                BodyPart bodyPart = multipart.getBodyPart(i);
//                extractTextPart(bodyPart, emailVerification.getVerificationCode());
//            }
//        }
//
////        @Test
////        void testStoreVerificationCode() {
////
////            EmailVerification emailVerification = new EmailVerification(code, false);
////            ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);
////
////            when(redisTemplate.opsForValue()).thenReturn(valueOps);
////            doNothing().when(valueOps).set(REDIS_PREFIX + fromEmail, emailVerification, 3, TimeUnit.MINUTES);
////
////            emailService.storeVerificationCode(toEmail, code);
////
////            verify(redisTemplate).opsForValue();
//////            verify(valueOps, times(1))
//////                    .set(eq(REDIS_PREFIX + fromEmail), eq(emailVerification), eq(3L), eq(TimeUnit.MINUTES));
////        }
//    }
//
//    @Nested
//    @ExtendWith(MockitoExtension.class)
//    class UnitTest {
//        @Mock
//        private RedisTemplate<String, EmailVerification> redisTemplate;
//
//        @Mock
//        private JavaMailSender javaMailSender;
//
//        @InjectMocks
//        private EmailService emailService;
//
//        private final String REDIS_PREFIX = "verified:";
//        private final String code = "123456";
//        private final String fromEmail = "sender@example.com";
//        private final String toEmail = "receiver@example.com";
//
//        @BeforeEach
//        void setUp() {
//            ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
//        }
//
//        @Test
//        @DisplayName("sendVerificationCode - Redis 및 JavaMailSender를 올바르게 호출하는지 테스트")
//        void testSendVerificationCode() {
//            EmailVerification emailVerification = new EmailVerification(code, false);
//            ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);
//
//            try (MockedStatic<RandomStringGenerator> mockedStatic = mockStatic(RandomStringGenerator.class)) {
//                mockedStatic.when(() -> RandomStringGenerator.generateRandomString(any(Integer.class))).thenReturn(code);
//
//                MimeMessage mimeMessage = new MimeMessage((Session) null);
//                when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
//                when(redisTemplate.opsForValue()).thenReturn(valueOps);
//                doNothing().when(javaMailSender).send(mimeMessage);
//
//                emailService.sendVerificationCode(toEmail);
//
//                mockedStatic.verify(() -> RandomStringGenerator.generateRandomString(6));
//                verify(redisTemplate.opsForValue(), times(1))
//                        .set(eq(REDIS_PREFIX + toEmail), eq(emailVerification), eq(3L), eq(TimeUnit.MINUTES));
//                verify(javaMailSender, times(1)).send(mimeMessage);
//            }
//        }
//
//        @Test
//        void testStoreVerificationCode() {
//            EmailVerification emailVerification = new EmailVerification(code, false);
//            ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);
//
//            when(redisTemplate.opsForValue()).thenReturn(valueOps);
//            doNothing().when(valueOps).set(REDIS_PREFIX + fromEmail, emailVerification, 3, TimeUnit.MINUTES);
//
//            emailService.storeVerificationCode(fromEmail, code);
//
//            verify(redisTemplate).opsForValue();
//            verify(valueOps, times(1))
//                .set(eq(REDIS_PREFIX + fromEmail), eq(emailVerification), eq(3L), eq(TimeUnit.MINUTES));
//        }
//
//        @Test
//        void testVerifyCode_Success() {
//            EmailVerification emailVerification = new EmailVerification(code, false);
//            ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);
//
//            when(redisTemplate.opsForValue()).thenReturn(valueOps);
//            doNothing().when(valueOps).set(REDIS_PREFIX + fromEmail, emailVerification, 10, TimeUnit.MINUTES);
//            when(valueOps.get(REDIS_PREFIX + fromEmail)).thenReturn(emailVerification);
//
//            emailService.verifyCode(fromEmail, code);
//
//
//            verify(redisTemplate, times(2)).opsForValue();
//            verify(valueOps).get(REDIS_PREFIX + fromEmail);
//            verify(valueOps)
//                    .set(eq(REDIS_PREFIX + fromEmail), eq(emailVerification), eq(10L), eq(TimeUnit.MINUTES));
//
//            assertTrue(emailVerification.isVerified());
//            assertNotNull(valueOps.get(REDIS_PREFIX + fromEmail));
//            assertTrue(valueOps.get(REDIS_PREFIX + fromEmail).isVerified());
//            assertEquals(emailVerification.getVerificationCode(), code);
//            assertEquals(emailVerification.getVerificationCode(), code);
//        }
//
//        @Test
//        void testVerifyCode_nullEmailVerification_InvalidVerificationCodeException() {
//            EmailVerification emailVerification = null;
//            ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);
//
//            when(redisTemplate.opsForValue()).thenReturn(valueOps);
//            when(valueOps.get(REDIS_PREFIX + fromEmail)).thenReturn(emailVerification);
//
//            ApplicationException applicationException = assertThrows(ApplicationException.class,
//                    () -> emailService.verifyCode(fromEmail, code));
//
//            assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
//            verify(redisTemplate, times(1)).opsForValue();
//            verify(redisTemplate.opsForValue()).get(REDIS_PREFIX + fromEmail);
//        }
//
//        @Test
//        void testVerifyCode_notValidCode_InvalidVerificationCodeException() {
//            EmailVerification emailVerification = new EmailVerification("invalid", false);
//            ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);
//
//            when(redisTemplate.opsForValue()).thenReturn(valueOps);
//            when(valueOps.get(REDIS_PREFIX + fromEmail)).thenReturn(emailVerification);
//
//            ApplicationException applicationException = assertThrows(ApplicationException.class,
//                    () -> emailService.verifyCode(fromEmail, code));
//
//            assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
//            verify(redisTemplate, times(1)).opsForValue();
//            verify(redisTemplate.opsForValue()).get(REDIS_PREFIX + fromEmail);
//        }
//
//        @Test
//        void testVerifyEmail_Success() {
//            EmailVerification emailVerification = new EmailVerification(code, true);
//            ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);
//
//            when(redisTemplate.opsForValue()).thenReturn(valueOps);
//            when(valueOps.get(REDIS_PREFIX + fromEmail)).thenReturn(emailVerification);
//
//            assertDoesNotThrow(() -> emailService.verifyEmail(fromEmail));
//
//            assertTrue(emailVerification.isVerified());
//            verify(redisTemplate, times(1)).opsForValue();
//            verify(redisTemplate.opsForValue()).get(REDIS_PREFIX + fromEmail);
//        }
//
//        @Test
//        void testVerifyEmail_nullEmailVerification_InvalidVerificationCodeException() {
//            EmailVerification emailVerification = null;
//            ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);
//
//            when(redisTemplate.opsForValue()).thenReturn(valueOps);
//            when(valueOps.get(REDIS_PREFIX + fromEmail)).thenReturn(emailVerification);
//
//            ApplicationException applicationException = assertThrows(ApplicationException.class,
//                    () -> emailService.verifyEmail(fromEmail));
//
//            assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
//            verify(redisTemplate, times(1)).opsForValue();
//            verify(redisTemplate.opsForValue()).get(REDIS_PREFIX + fromEmail);
//        }
//
//        @Test
//        void testVerifyEmail_notVerified_InvalidVerificationCodeException() {
//            EmailVerification emailVerification = new EmailVerification(code, false);
//            ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);
//
//            when(redisTemplate.opsForValue()).thenReturn(valueOps);
//            when(valueOps.get(REDIS_PREFIX + fromEmail)).thenReturn(emailVerification);
//
//            ApplicationException applicationException = assertThrows(ApplicationException.class,
//                    () -> emailService.verifyEmail(fromEmail));
//
//            assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
//            verify(redisTemplate, times(1)).opsForValue();
//            verify(redisTemplate.opsForValue()).get(REDIS_PREFIX + fromEmail);
//        }
//    }
//}