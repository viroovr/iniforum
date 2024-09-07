import React, {useEffect, useState} from "react";
import axios from "axios";

function Posts() {
    const [questions, setQuestions] = useState([]);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [tag, setTag] = useState("");

    const token = localStorage.getItem('jwtToken');

    const api = axios.create({
        baseURL: 'http://localhost:8080',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    
    useEffect(() => {
        const fetchQuestion = async () => {
            try {
                const response = await api.get("/q/questions");
                setQuestions(response.data);  // 바로 데이터를 설정
            } catch (error) {
                console.error("Failed to fetch questions", error);
            }
        };
    
        fetchQuestion();
    }, []);  // 종속성 배열에 아무것도 없으므로, 처음 렌더링 때만 실행


    const handleSubmit = async (e) => {
        e.preventDefault();
        const newQuestion = {
            title,
            content,
            tag
        };
    
        try {
            await api.post("/q/post", newQuestion);
            const response = await api.get("/q/questions");
            setQuestions(response.data);
        } catch (error) {
            console.error("Failed to post question", error);
        }
    };

    return (
        <div>
            <h1>질문 목록</h1>
            <ul>
                {questions.map((question) => (
                    <li key={question.id}>
                        {question.title} - {question.userId} - {question.tag}
                    </li>
                ))}
            </ul>

        <h2>Post a Question</h2>
        <form onSubmit={handleSubmit}>
            <input
                type="text"
                placeholder="Title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
            />
             <input
                type="text"
                placeholder="Content"
                value={content}
                onChange={(e) => setContent(e.target.value)}
            />
            <input
                type="text"
                placeholder="Tag"
                value={tag}
                onChange={(e) => setTag(e.target.value)}
            />
            <button type="submit">게시글 작성</button>

        </form>
        </div>
    );


};

export default Posts;