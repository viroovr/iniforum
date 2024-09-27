import React, {useEffect, useState} from "react";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import apiClient from "../excption/setupAxiosInterceptors";
import QuestionSearch from "./QuestionSearch";

const QuestionList = () => {
    const [questions, setQuestions] = useState([]);
    const [currnetPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [searchKeyword, setSearchKeyword] = useState('');

    const navigate = useNavigate();

    const goToPostPage = () => {
        navigate("/post");
    }

    useEffect(() => {
        fetchQuestions(currnetPage, searchKeyword);
    }, [currnetPage, searchKeyword]);

    const fetchQuestions = async (page, keyword) => {
        try {
            const response = await apiClient.get("/q/questions", {
                params: {
                    page: page,
                    size: 10,
                    keyword: keyword
                }
            })
            setQuestions(response.data.content);
            setTotalPages(response.data.totalPages);

        } catch (error) {
            console.error('Failed to fetch questions', error);
            navigate("/login");
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

    const handleSearch = (keyword) => {
        setSearchKeyword(keyword);
        setCurrentPage(0);
    }

    return (
        <div>
            <h1>질문 게시판</h1>

            {questions.length === 0 ? (
                <p>게시글이 없습니다.</p>
            ) : (
                <ul>
                    {questions.map((question) => (
                        <li key={question.id}>
                            <Link to={`/questions/${question.id}`}>{question.title}</Link> {question.userId}
                        </li>
                    ))}
                </ul>
            )}
            <QuestionSearch onSearch={handleSearch} />
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