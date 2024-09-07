import React, {useState} from 'react';
import { useNavigate} from "react-router-dom";
import axios from 'axios';

function Login() {
    const [userId, setUserId] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/auth/login', {userId, password});
            const token = response.data
            console.log(response.data);
            localStorage.setItem('jwtToken', token);
            if (response.status === 200) {
                navigate("/questions");
            }
        } catch(error) {
            console.error('로그인 실패', error);
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
                <button type='submit'>로그인</button>

            </form>
        </div>
    );

};

export default Login;