import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import apiClient from "../excption/setupAxiosInterceptors";

const QuestionDetail = () => {
    const { id } = useParams();
    const [question, setQuestions] = useState(null);

    useEffect(() => {
        fetchQuestionDetails();
    }, [id]);

    const fetchQuestionDetails = async () => {
        try {
            const response = await apiClient.get(`/q/${id}`);
            setQuestions(response.data);
        } catch (error) {
            console.error("Error fetching question detail:", error);
        }
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
        </div>
    );

}

export default QuestionDetail;