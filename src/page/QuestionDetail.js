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
    const navigate = useNavigate();

    useEffect(() => {
        fetchQuestionDetails();
        fetchComments();
    }, [id]);

    const fetchQuestionDetails = async () => {
        try {
            const response = await apiClient.get(`/q/${id}`);
            setQuestions(response.data);
            checkIfUserIsAuthor(response.data);
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
        const token = localStorage.getItem("jwtToken");
        if (token) {
            const decodedToken = jwtDecode(token);
            const currentUserId = decodedToken.userId;
            if ( questionData && currentUserId === questionData.userId) {
                setIsAuthor(true);
            }
        }
    }

    const handleDelete = async () => {
        try {
            await apiClient.delete(`/q/${id}`);
            navigate("/questions");
        } catch (error) {
            console.error("Error delete question", error);
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
                </div>
                
            ))}
            
        </div>
    );

}

export default QuestionDetail;