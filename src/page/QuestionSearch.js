import axios from "axios";
import { useState } from "react";
import apiClient from "../excption/setupAxiosInterceptors";

const QuestionSearch = ({ onSearch }) => {
    const [keyword, setKeywords] = useState('');

    const handleSearch = () => {
        onSearch(keyword);
    };


    return (
        <div>
            <input
                type="text"
                value={keyword}
                onChange={(e) => setKeywords(e.target.value)}
                placeholder="검색어를 입력하세요"
            />
            <button onClick={handleSearch}>검색</button>
            
        </div>
    );
};
export default QuestionSearch;