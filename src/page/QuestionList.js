import React, {useEffect, useState} from "react";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import apiClient from "../excption/setupAxiosInterceptors";

const QuestionList = () => {
    const [questions, setQuestions] = useState([]);
    const [currnetPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);

    const navigate = useNavigate();

    const goToPostPage = () => {
        navigate("/post");
    }

    useEffect(() => {
        fetchQuestions(currnetPage);
    }, [currnetPage]);

    const fetchQuestions = async (page) => {
        try {
            const response = await apiClient.get("/q/questions", {
                params: {
                    page: page,
                    size: 10,
                }
            })
            setQuestions(response.data.content);
            setTotalPages(response.data.totalPages);

        } catch (error) {
            console.error('Failed to fetch questions', error);
        }
    };

    const handleNextPage = () => {
        if (currnetPage < totalPages - 1) {
            setCurrentPage((prevPage) => prevPage + 1);
        }
        
    };

    const handlePreviousPage = () => {
        if (currnetPage > 0) {
            setCurrentPage((prevPage) => prevPage - 1);
        }
    };

    return (
        <div>
            <h1>질문 게시판</h1>

            {questions.length === 0 ? (
                <p>게시글이 없습니다.</p>
            ) : (
                <ul>
                    {questions.map((question) => (
                        <li key={question.id}>
                            {question.postNumber} <Link to={`/questions/${question.id}`}>{question.title}</Link> {question.userId}
                        </li>
                    ))}
                </ul>
            )}

            <div>
                <button onClick={handlePreviousPage} disabled={currnetPage === 0}>
                    이전
                </button>
                <span>{currnetPage + 1} / {totalPages}</span>
                <button onClick={handleNextPage} disabled={currnetPage === totalPages - 1}>
                    다음
                </button>
                <button onClick={goToPostPage}>글쓰기</button>
            </div>

        </div>
    )
}

export default QuestionList;