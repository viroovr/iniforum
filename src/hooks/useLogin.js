import axios from "axios";
import { useState } from "react"

const useLogin = (onLogin, navigate) => {
    const [error, setError] = useState('');

    const login = async (userId, password) => {
        try {
            const response = await axios.post('http://localhost:8080/auth/login', {userId, password},
                {withCredentials: true}
            );
            const {accessToken} = response.data;
            localStorage.setItem('jwtToken', accessToken);
            onLogin();
            if (response.status === 200) {
                navigate("/questions");
            }
        } catch (error) {
            console.error("로그인 실패", error)
            setError("유저 아이디나 패스워드가 일치하지 않습니다.");
        }
    };

    return {error, login};
}

export default useLogin;