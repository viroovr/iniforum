import { useState } from "react"
import apiClient from "../../excption/setupAxiosInterceptors";

const CommentEditForm = ({commentId, commentContent, questionId, onEditSuccess, onCancel}) => {
    const [content, setContent] = useState(commentContent);

    const handleSubmit = async (e) => {
        try {
            await apiClient.put(`/q/${questionId}/comments/${commentId}`, {
                content:content
            });
            onEditSuccess();
            onCancel();
        } catch (error) {
            console.error("Error editing comment", error);
        }
    }

    return(
        <form onSubmit={handleSubmit}>
            <textarea
                value={content}
                onChange = {(e) => setContent(e.target.value)}
                required
            />
            <button type="submit">수정</button>
            <button type="button" onClick={onCancel}>취소</button>
        </form>
    )
}

export default CommentEditForm;