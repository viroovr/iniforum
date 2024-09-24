import React, {useEffect, useState} from 'react';
import { useNavigate} from "react-router-dom";
import axios from 'axios';

function Login({ onLogin }) {
    const [userId, setUserId] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();


    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/auth/login', {userId, password}, 
                {
                    withCredentials: true
                }
            );
            const {accessToken} = response.data;
            console.log(accessToken);
            localStorage.setItem('jwtToken', accessToken);
            onLogin();
            if (response.status === 200) {
                navigate("/questions");
            }
        } catch(error) {
            console.error('로그인 실패', error);
            setError("유저 아이디나 패스워드가 일치하지 않습니다.");
        }
    };

    return (
        <div>
            <h2>로그인</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>아이디</label>
                    <input
                        type='text'
                        value={userId}
                        onChange={(e) => setUserId(e.target.value)}
                        required
                        />
                </div>
                <div>
                    <label>패스워드</label>
                    <input
                        type='password'
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        />
                </div>
                {error && <p style={{ color: "red" }}>{error}</p>}
                <button type='submit'>로그인</button>

            </form>
        </div>
    );

};

export default Login;