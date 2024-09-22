import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom"
import apiClient from "../excption/setupAxiosInterceptors";

const QuestionEdit = () => {
    const {id} = useParams();
    const navigate = useNavigate();
    const [form, setForm] = useState({
        title: "",
        content: "",
        tag: ""
    });

    useEffect(() => {
        fetchQuestionDetails();
    }, [id]);

    const fetchQuestionDetails = async () => {
        try {
            const response = await apiClient.get(`/q/${id}`);
            setForm({
                title: response.data.title,
                content: response.data.content,
                tag: response.data.tag
            });
        } catch (error) {
            console.error("Error fetching question for edit: ", error);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await apiClient.put(`/q/${id}`, form);
            navigate("/questions");
        } catch (error) {
            console.error("Error updating question:", error);
        }
    };

    const handleChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value
        });
    };

    return (
        <div>
            <h1>질문 수정</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>제목:</label>
                    <input
                        type="text"
                        name="title"
                        value={form.title}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label>내용:</label>
                    <input
                        name="content"
                        value={form.content}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label>태그:</label>
                    <input
                        type="text"
                        name="tag"
                        value={form.tag}
                        onChange={handleChange}
                        required
                    />
                </div>
                <button type="submit">수정</button>
                <button type="button" onClick={() => navigate(`/questions/${id}`)}>취소</button>
            </form>
        </div>
    );

};

export default QuestionEdit;