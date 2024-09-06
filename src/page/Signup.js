import React, {useState} from "react";
import axios from 'axios';

function Signup() {
    const [userId, setUserId] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        const userData = {
            userId,
            email,
            password,
            name,
        };

        try {
            const response = await axios.post('http://localhost:8080/auth/signup', userData)
            console.log('회원가입 성공', response.data);
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
          <label>Name</label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </div>
        <button type="submit">회원가입</button>
            </form>
        </div>
    );
};

export default Signup;