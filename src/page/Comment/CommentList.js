import { useEffect, useState } from "react";
import apiClient from "../../excption/setupAxiosInterceptors";
import CommentEditForm from "./CommentEditForm";
import { useNavigate } from "react-router-dom";
import CommentForm from "./CommentForm";

const CommentList = ({questionId, currentUserId}) => {
    const [comments, setComment] = useState([]);
    const [editingCommentId, setEditingCommentId] = useState(null);
    const [editingCommentContent, setEditingCommentContent] = useState("");
    const navigate = useNavigate();

    const handleEditComment = (comment) => {
        setEditingCommentContent(comment.content);
        setEditingCommentId(comment.id);
    }

    useEffect(() => {
        fetchComments();
    }, [questionId]);

    const fetchComments = async () => {
        try {
            const response = await apiClient.get(`/q/${questionId}/comments`);
            setComment(response.data);
        } catch (error) {
            console.error("Error fetching comments:", error);
        }
    };

    const handleDeleteComment = async (commentId) => {
        const confirmDelete = window.confirm("정말로 이 댓글을 삭제하시겠습니까?");
        if(confirmDelete) {
            try {
                await apiClient.delete(`/q/${questionId}/comments/${commentId}`);
                setComment(prevComments => prevComments.filter(comment => comment.id !== commentId));
                navigate(`/questions/${questionId}`);
            } catch (error) {
                console.error("Error deleting comment", error);
            }
        }
    }


    const handleCommentAdded = (newComment) => {
        setComment(prevComments => [...prevComments, newComment]);
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

    const formatDate = (createdDate) => {
        const now = new Date();
        const createdTime = new Date(createdDate);
        const timeDifference = now - createdTime;

        const seconds = Math.floor(timeDifference / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);

        if(hours < 24) {
            return hours > 0 ? `${hours}시간 전` : `${minutes}분 전`;
        } else {
            return `${days}일 전`;
        }
    };


    return (
        <div>
            <CommentForm questionId={questionId} onCommentAdded={handleCommentAdded}/>
            {comments.map((comment) => (
                <div key={comment.id}>
                    <p>작성자: {comment.userId} {formatDate(comment.createdDate)}</p> 
                    <p>{comment.content}</p>
                    <p>추천수: {comment.likeCount}</p>
                    <button onClick={() => handleLikeComment(comment.id)}>추천</button>
                    {comment.userId === currentUserId && (
                        <div>
                            <button onClick={() => handleEditComment(comment)}>수정</button>
                            <button onClick={() => handleDeleteComment(comment.id)}>삭제</button>
                        </div>
                    )}
                </div>
            ))}
            {editingCommentId && (
                <CommentEditForm
                    commentId={editingCommentId}
                    commentContent={editingCommentContent}
                    questionId={questionId}
                    onEditSuccess={fetchComments}
                    onCancel={()=>setEditingCommentId(null)}
                />
            )}
        </div>
    )
};

export default CommentList;