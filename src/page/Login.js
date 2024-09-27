import React, {useState} from 'react';
import { useNavigate} from "react-router-dom";
import InputField from '../compontents/InputField';
import ErrorMessage from '../compontents/ErrorMessage';
import useLogin from '../hooks/useLogin';
import '../compontents/styles/Login.css';

function Login({ onLogin }) {
    const [userId, setUserId] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();
    
    const { error, login } = useLogin(onLogin, navigate);

    const handleSubmit = async (e) => {
        e.preventDefault();
        login(userId, password);
    };

    return (
        <div className='login-container'>
            <form className="login-form" onSubmit={handleSubmit}>
                <h2>로그인</h2>
                <div className='input-fields'>
                    <InputField
                        label="아이디"
                        type='text'
                        value={userId}
                        onChange={(e) => setUserId(e.target.value)}
                        className="form-input"
                        required
                    />
                    <InputField
                        label="패스워드"
                        type='password'
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="form-input"
                        required
                    />
                </div>
                {error && <ErrorMessage message={error} className="error-message" />}
                <button className="login-button" type='submit'>로그인</button>
            </form>
        </div>
    );

};

export default Login;