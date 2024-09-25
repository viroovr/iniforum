import React, {useState} from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../excption/setupAxiosInterceptors";

function Post() {
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [tag, setTag] = useState("");
    const navigate = useNavigate();


    const handleSubmit = async (e) => {
        e.preventDefault();
        const newQuestion = {
            title,
            content,
            tag
        };
    
        try {
            await apiClient.post("/q/post", newQuestion);
            navigate("/questions");
        } catch (error) {
            console.error("Failed to post question", error);
        }
    };

    return (
        <div>

        <h2>글 작성</h2>
        <form onSubmit={handleSubmit}>
            <label>제목</label>
            <input
                type="text"
                placeholder="Title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
            />
            <label>내용</label>
            <textarea
                placeholder="Content"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                style={{width: '100%', resize: 'vertical'}}
             
            />
            <label>태그</label>
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

export default Post;