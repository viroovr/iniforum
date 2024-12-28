//package com.forum.project.application.question;
//
//import com.forum.project.application.converter.CommentDtoConverterFactory;
//import com.forum.project.application.security.jwt.TokenService;
//import com.forum.project.domain.comment.Comment;
//import com.forum.project.domain.like.CommentLike;
//import com.forum.project.domain.like.CommentLikeRepository;
//import com.forum.project.domain.comment.CommentRepository;
//import com.forum.project.presentation.comment.RequestCommentDto;
//import com.forum.project.presentation.comment.ResponseCommentDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(SpringExtension.class)
//class CommentServiceTest {
//    @Mock
//    private CommentRepository commentRepository;
//
//    @Mock
//    private CommentLikeRepository commentLikeRepository;
//
//    @Mock
//    private CommentDtoConverterFactory converter;
//
//    @Mock
//    private TokenService tokenService;
//
//    @InjectMocks
//    private CommentService commentService;
//
//    private final LocalDateTime dateTime = LocalDateTime.of(2024, 12, 16, 12, 30, 0);
//    private final RequestCommentDto requestCommentDto = new RequestCommentDto("testContent");
//    private final ResponseCommentDto responseCommentDto = new ResponseCommentDto(1L, "testContent", "testId", dateTime, 0L);
//
//    @BeforeEach
//    void setUp() {
//        doCallRealMethod().when(converter).fromRequestDtoToEntity(requestCommentDto);
//        doCallRealMethod().when(converter).toResponseCommentDto(any(Comment.class));
//    }
//
//    @Test
//    void testAddComment_Success() {
//        String userId = "testId";
//        String accessToken = "access-token";
//        Long questionId = 1L;
//        when(tokenService.getLoginId(accessToken)).thenReturn(userId);
//
//        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
//        when(commentRepository.save(commentArgumentCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        ResponseCommentDto result = commentService.addComment(questionId, requestCommentDto, accessToken);
//
//        Comment capturedComment = commentArgumentCaptor.getValue();
//
//        assertEquals(capturedComment.getQuestionId(), questionId);
//        assertEquals(capturedComment.getUserId(), userId);
//    }
//
//    @Test
//    void testGetCommentsByQuestionId_Success() {
//        Long questionId = 1L;
//        List<Comment> response = List.of(
//                new Comment(1L, "testContent1", "testId1", dateTime, questionId, 0L),
//                new Comment(2L, "testContent2", "testId2", dateTime, questionId, 0L)
//        );
//        when(commentRepository.findByQuestionId(questionId)).thenReturn(response);
//
//        List<ResponseCommentDto> result = commentService.getCommentsByQuestionId(questionId);
//
//        assertEquals(result.get(0).getId(), response.get(0).getId());
//        assertEquals(result.get(1).getId(), response.get(1).getId());
//    }
//
//    @Test
//    void testDeleteComment_Success() {
//        String userId = "testId";
//        String accessToken = "access-token";
//        Long commentId = 1L;
//        Comment comment = new Comment(commentId, "testContent", "testId", dateTime, 1L, 0L);
//        when(tokenService.getLoginId(accessToken)).thenReturn(userId);
//        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
//        doNothing().when(commentRepository).deleteById(commentId);
//
//        commentService.deleteComment(commentId, accessToken);
//
//        verify(commentRepository).findById(commentId);
//        verify(commentRepository).deleteById(commentId);
//    }
//
//    @Test
//    void testUpdateComment_Success() {
//        String userId = "testId";
//        String accessToken = "access-token";
//        Long commentId = 1L;
//        Comment comment = new Comment(commentId, "testContent", userId, dateTime, 1L, 0L);
//        when(tokenService.getLoginId(accessToken)).thenReturn(userId);
//
//        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
//
//        ArgumentCaptor<Comment> argumentCaptor = ArgumentCaptor.forClass(Comment.class);
//        when(commentRepository.save(argumentCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        ResponseCommentDto result = commentService.updateComment(commentId, requestCommentDto, accessToken);
//
//        Comment capturedComment = argumentCaptor.getValue();
//        assertEquals(capturedComment.getContent(), requestCommentDto.getContent());
//        assertEquals(result.getId(), commentId);
//        assertEquals(result.getUserId(), userId);
//        assertEquals(result.getContent(), requestCommentDto.getContent());
//    }
//
//    @Test
//    void testLikeComment_Success() {
//        Long userId = 1L;
//        String accessToken = "access-token";
//        Long commentId = 1L;
//        Comment comment = new Comment(commentId, "testContent", "testId", dateTime, 1L, 0L);
//        when(tokenService.getId(accessToken)).thenReturn(userId);
//
//        when(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);
//        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
//
//        ArgumentCaptor<CommentLike> argumentCaptor = ArgumentCaptor.forClass(CommentLike.class);
//        when(commentLikeRepository.save(argumentCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
//        commentService.likeComment(commentId, accessToken);
//
//        CommentLike commentLike = argumentCaptor.getValue();
//        assertEquals(commentLike.getUserId(), userId);
//        assertEquals(commentLike.getCommentId(), commentId);
//        verify(commentRepository).findById(commentId);
//        verify(commentLikeRepository).existsByCommentIdAndUserId(commentId, userId);
//        verify(commentLikeRepository).save(any(CommentLike.class));
//    }
//}