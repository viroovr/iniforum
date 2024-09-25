import React, {useState} from "react";
import { useNavigate } from "react-router-dom";
import axios from 'axios';

function Signup() {
    const [userId, setUserId] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [name, setName] = useState('');
    const [error, setError] = useState('');

    const navigate = useNavigate();
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (password !== confirmPassword) {
          setError("패스워드가 일치하지 않습니다.");
          return;
        }

        const userData = {
            userId,
            email,
            password,
            name,
        };

        try {
            const response = await axios.post('http://localhost:8080/auth/signup', userData)
            console.log('회원가입 성공', response.data);
            navigate("/login");
            
        } catch (error) {
            console.error('회원가입 실패 : ', error);
        }
        
    };

    return (
        <div>
            <h2>회원가입</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>아이디</label>
                    <input
                    type="text"
                    value={userId}
                    onChange={(e) => setUserId(e.target.value)}
                    required
                    />

                </div>
                <div>
                  <label>Email</label>
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
                <div>
                  <label>Password</label>
                  <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
                <div>
                  <label>Confirm Password</label>
                  <input
                    type="password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                  />
                </div>
        <div>
          <label>Name</label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
            {error && <p style={{ color: "red"}}>{error}</p>}
        </div>
        <button type="submit">회원가입</button>
            </form>
        </div>
    );
};

export default Signup;