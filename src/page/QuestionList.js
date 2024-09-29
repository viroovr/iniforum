import React, {useEffect, useState} from "react";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import apiClient from "../excption/setupAxiosInterceptors";
import QuestionSearch from "./QuestionSearch";
import "./styles/QuestionList.css"

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

    const formatDate = (createdDate) => {
        const now = new Date();
        const createdTime = new Date(createdDate);
        const timeDifference = now - createdTime;

        const seconds = Math.floor(timeDifference / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);

        if(hours < 24) {
            if (hours > 0) {
                return `${hours}시간 전`;
            } else {
                if (minutes < 1) {
                    return `${seconds}초 전`;
                } else {
                    return `${minutes}분 전`;
                }
                
            }
        } else {
            return `${days}일 전`;
        }
    };

    return (
        <div className="questions-container">
            <h1>질문 게시판</h1>

            {questions.length === 0 ? (
                <p>게시글이 없습니다.</p>
            ) : (
                <table className="table">
                    <thead>
                        <tr>
                            <th>작성자</th>
                            <th>제목</th>
                            <th>작성일</th>
                            <th>태그</th>
                        </tr>
                    </thead>
                    <tbody>
                        {questions.map((question) => (
                            <tr key={question.id}>
                                <td>{question.userId}</td>
                                <td>
                                    <Link to={`/questions/${question.id}`}>{question.title}</Link>
                                </td>
                                <td>{formatDate(question.createdDate)}</td>
                                <td>{question.tag}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
            <QuestionSearch onSearch={handleSearch} />
            <div className="pagination">
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