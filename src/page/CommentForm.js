import { useState } from "react"
import apiClient from "../excption/setupAxiosInterceptors";
import { jwtDecode } from "jwt-decode";

const CommentForm = ({questionId, onCommentAdded}) => {
    const [content, setContent] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();

        const token = localStorage.getItem('jwtToken');
        const userId = jwtDecode(token).userId;

        const commentData = {
            content,
            userId
        }

        try {
            const response = await apiClient.post(`/q/${questionId}/comments`, commentData);
            onCommentAdded(response.data);
            setContent("");
        } catch(error) {
            console.error("Error adding comment", error);
        }

    };

    return (
        <form onSubmit={handleSubmit}>
            <textarea
                value={content}
                onChange={(e) => setContent(e.target.value)}
                placeholder="댓글을 입력해주세요."
                required
            />
            <button type="submit">댓글 작성</button>
        </form>
    );
};

export default CommentForm;