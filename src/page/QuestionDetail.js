import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import apiClient from "../excption/setupAxiosInterceptors";
import {jwtDecode} from "jwt-decode";
import CommentList from "./Comment/CommentList";
import "./styles/QuestionDetail.css";

const QuestionDetail = () => {
    const { id } = useParams();
    const [question, setQuestions] = useState(null);
    const [isAuthor, setIsAuthor] = useState(false);
    const [currentUserId, setCurrentUserId] = useState(null);
    
    const navigate = useNavigate();

    useEffect(() => {
        fetchQuestionDetails();
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
        const confirmDelete = window.confirm("정말로 이 게시글을 삭제하시겠습니까?");
        if(confirmDelete){
            try {
                await apiClient.delete(`/q/${id}`);
                navigate("/questions");
            } catch (error) {
                console.error("Error delete question", error);
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
        <div className="questions-container">
            {
                isAuthor &&  (
                    <div className="button-container">
                        <button onClick={handleEdit}>수정</button>
                        <button onClick={handleDelete}>삭제</button>
                    </div>
                )
            }
            <h1 className="question-title">{question.title}</h1>
            <p className="question-meta">작성자: {question.userId} 작성일: {new Date(question.createdDate).toLocaleDateString()} {new Date(question.createdDate).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</p>
            <div className="question-content">{question.content}</div>
            <div className="question-tags">
                <p>태그: {question.tag.split(',').map((t, index) => (
                        <span key={index} className="question-tag">{t.trim()}</span>
                    ))}</p>
            </div>

            <div className="comment-section">
                <h2>댓글</h2>
                <CommentList questionId={id} currentUserId={currentUserId}/>
            </div>
        </div>
    );

}

export default QuestionDetail;