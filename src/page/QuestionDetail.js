import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import apiClient from "../excption/setupAxiosInterceptors";
import {jwtDecode} from "jwt-decode";
import CommentForm from "./CommentForm";

const QuestionDetail = () => {
    const { id } = useParams();
    const [question, setQuestions] = useState(null);
    const [isAuthor, setIsAuthor] = useState(false);
    const [comments, setComment] = useState([]);
    const [currentUserId, setCurrentUserId] = useState(null);
    const [editingCommentId, setEditingCommentId] = useState(null);
    const [editingCommentContent, setEditingCommentContent] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
    
        fetchQuestionDetails();
        fetchComments();
    }, [id]);


    const fetchQuestionDetails = async () => {
        try {
            const response = await apiClient.get(`/q/${id}`);
            checkIfUserIsAuthor(response.data);
            setQuestions(response.data);
        } catch (error) {
            console.error("Error fetching question detail:", error);
        }
    };

    const fetchComments = async () => {
        try {
            const response = await apiClient.get(`/q/${id}/comments`);
            setComment(response.data);
        } catch (error) {
            console.error("Error fetching comments:", error);
        }
    };

    const habdleCommentAdded = (newComment) => {
        setComment(prevComments => [...prevComments, newComment]);
    }

    const checkIfUserIsAuthor = (questionData) => {
        const token = localStorage.getItem('jwtToken');
        if (token) {
            const decodedToken = jwtDecode(token);
            setCurrentUserId(decodedToken.userId);
            if ( questionData && decodedToken.userId === questionData.userId) {
                setIsAuthor(true);
            }
        }
    }

    const handleDelete = async () => {
        const confirmDelete = window.confirm("정말로 이 댓글을 삭제하시겠습니까?");
        if(confirmDelete){
            try {
                await apiClient.delete(`/q/${id}`);
                navigate("/questions");
            } catch (error) {
                console.error("Error delete question", error);
            }
        }
    }

    const handleCommentDelete = async (commentId) => {
        const confirmDelete = window.confirm("정말로 이 게시글을 삭제하시겠습니까?");
        if(confirmDelete) {
            try {
                await apiClient.delete(`/q/${id}/comments/${commentId}`);
                setComment(prevComments => prevComments.filter(comment => comment.id !== commentId));
                navigate(`/questions/${id}`);
            } catch (error) {
                console.error("Error deleting comment", error);
            }
        }
    }

    const handleCommentEditClick = (comment) => {
        setEditingCommentContent(comment.content);
        setEditingCommentId(comment.id)
    }

    const handleCommentEditSubmit = async (e) => {
        try {
            console.log(currentUserId);
            await apiClient.put(`/q/${id}/comments/${editingCommentId}`, {
                content:editingCommentContent,
                userId: currentUserId
            });
            setComment(prevComments => prevComments.filter(comment => comment.id !== editingCommentId));
            setEditingCommentId(null);
            setEditingCommentContent("");
        } catch (error) {
            console.error("Error editing comment", error);
        }
    }

    const handleLikeComment = async (commentId) => {
        try {
            await apiClient.post(`/q/comments/${commentId}/like`);
            fetchComments();
        } catch (e) {
            if(e.response && e.response.status === 400) {
                alert("이미 이 댓글에 추천하셨습니다.");
            } else {
                console.error("Error liking commnet:",e);
            }
        }
    }

    const handleEdit = () => {
        navigate(`/${id}/edit`);
    };

    if (!question) {
        return <p>Loading...</p>;
    }

    return (
        <div>
            {
                isAuthor &&  (
                    <div>
                        <button onClick={handleEdit}>수정</button>
                        <button onClick={handleDelete}>삭제</button>
                    </div>
                )
            }
            <h1>{question.title}</h1>
            <p>작성자: {question.userId}</p>
            <p>내용: {question.content}</p>
            <p>태그: {question.tag}</p>
            <p>작성일: {new Date(question.createdDate).toLocaleString()}</p>

            <h2>댓글</h2>
            <CommentForm questionId={id} onCommentAdded={habdleCommentAdded}/>
            {comments.map((comment) => (
                <div key={comment.id}>
                    <p>작성자: {comment.userId} 작성일: {new Date(comment.createdDate).toLocaleString()}</p> 
                    <p>{comment.content}</p>
                    <p>추천수: {comment.likeCount}</p>
                    <button onClick={() => handleLikeComment(comment.id)}>추천</button>
                    {comment.userId === currentUserId && (
                        <div>
                            <button onClick={() => handleCommentEditClick(comment)}>수정</button>
                            <button onClick={() => handleCommentDelete(comment.id)}>삭제</button>
                        </div>
                    )}
                </div>
                
            ))}

            {editingCommentId && (
                <div>
                    <h2>댓글 수정</h2>
                    <form onSubmit={handleCommentEditSubmit}>
                        <textarea
                            value={editingCommentContent}
                            onChange = {(e) => setEditingCommentContent(e.target.value)}
                            required
                        />
                        <button type="submit">수정</button>
                        <button type="button" onClick={() => setEditingCommentId(null)}>취소</button>
                    </form>
                </div>
            )}
            
        </div>
    );

}

export default QuestionDetail;