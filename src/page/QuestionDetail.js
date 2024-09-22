import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import apiClient from "../excption/setupAxiosInterceptors";
import {jwtDecode} from "jwt-decode";

const QuestionDetail = () => {
    const { id } = useParams();
    const [question, setQuestions] = useState(null);
    const [isAuthor, setIsAuthor] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        fetchQuestionDetails();
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
            <h1>{question.title}</h1>
            <p>작성자: {question.userId}</p>
            <p>내용: {question.content}</p>
            <p>태그: {question.tag}</p>
            <p>작성일: {new Date(question.createdDate).toLocaleString()}</p>
            {
                isAuthor &&  (
                    <div>
                        <button onClick={handleEdit}>수정</button>
                        <button onClick={handleDelete}>삭제</button>
                    </div>
                )
            }
            
        </div>
    );

}

export default QuestionDetail;