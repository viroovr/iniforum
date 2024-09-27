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
    const [fieldErrors, setFieldErrors] = useState({
      userId: '',
      email: '',
      password: '',
      confirmPassword: '',
      name: ''
    })

    const navigate = useNavigate();
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setFieldErrors({});

        const newFieldErrors = {};

        if (password !== confirmPassword) {
          newFieldErrors.confirmPassword = "패스워드가 일치하지 않습니다.";
        }
        
        if(!/^[a-zA-Z][a-zA-Z0-9]{3,19}/.test(userId)) {
          newFieldErrors.userId = "아이디는 알파벳으로 시작하고, 4~20자의 영문자와 숫자로 구성되어야 합니다."
        }

        if(!/^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+]).{8,30}$/.test(password)) {
          newFieldErrors.password = "비밀번호는 최소 8자 이상, 1개의 숫자, 1개의 알파벳, 1개의 특수문자를 포함해야 합니다.";
        }
        
        if(!/^[a-zA-Z가-힣]{1,50}$/.test(name)) {
          newFieldErrors.name = "이름은 한글 또는 영문만 포함해야 하며, 최대 50자입니다.";
        }

        if(Object.keys(newFieldErrors).length > 0) {
          setFieldErrors(newFieldErrors);
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
            setError(error.response.data.message || "회원가입에 실패했습니다.");
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
                    style={{boardColor: fieldErrors.userId ? 'red' : ''}}
                />
                {fieldErrors.userId && <p style={{ color: "red"}}>{fieldErrors.userId}</p>}
                </div>
                <div>
                  <label>Email</label>
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    style={{boardColor: fieldErrors.email ? 'red' : ''}}
                    required
                  />
                  {fieldErrors.email && <p style={{ color: "red"}}>{fieldErrors.email}</p>}
                </div>
                <div>
                  <label>Password</label>
                  <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    style={{boardColor: fieldErrors.password ? 'red' : ''}}
                    required
                  />
                  {fieldErrors.password && <p style={{ color: "red"}}>{fieldErrors.password}</p>}
                </div>
                <div>
                  <label>Confirm Password</label>
                  <input
                    type="password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    style={{boardColor: fieldErrors.confirmPassword ? 'red' : ''}}
                    required
                  />
                  {fieldErrors.confirmPassword && <p style={{ color: "red"}}>{fieldErrors.confirmPassword}</p>}
                </div>
                <div>
                  <label>Name</label>
                  <input
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    style={{boardColor: fieldErrors.name ? 'red' : ''}}
                    required
                  />
                    {fieldErrors.name && <p style={{ color: "red"}}>{fieldErrors.name}</p>}
                </div>
                {error && <p style={{ color: "red"}}>{error}</p>}
                <button type="submit">회원가입</button>
            </form>
        </div>
    );
};

export default Signup;