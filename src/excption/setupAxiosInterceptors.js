import axios from "axios";
import {toast} from 'react-toastify'
import { useNavigate } from "react-router-dom";
import 'react-toastify/dist/ReactToastify.css';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080'
});


export const setuoAxiosInterceptors = (navigate) => {
    apiClient.interceptors.request.use(
        (config) => {
            const token = localStorage.getItem('jwtToken');
            if (token) {
                config.headers['Authorization'] = `Bearer ${token}`;
            }
            return config;
        },
        (error) => Promise.reject(error)
    );

    apiClient.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response && error.response.status === 403) {
                localStorage.removeItem('jwtToken');
                if (!toast.isActive('login-required')){
                    toast.error("로그인이 필요합니다.", {toastId: 'login-required'});
                    navigate("/login");
                }
            }
            return Promise.reject(error);
        }
    );
};

export default apiClient;
