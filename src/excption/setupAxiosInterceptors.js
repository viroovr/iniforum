import axios from "axios";
import {toast} from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true
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
        async (error) => {
            const originalRequest = error.config;
            if (error.response && error.response.status === 401 && !originalRequest._retry) {
                originalRequest._retry = true;
                try{
                    const response = await axios.post('http://localhost:8080/auth/refresh', {}, {
                        withCredentials: true
                    });
                    const {accessToken} = response.data;
                    console.log(accessToken);
                    localStorage.setItem('jwtToken', accessToken);
                    originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;
                    return apiClient(originalRequest);
                } catch (error) {
                    console.error("Failed to refresh access token", error);
                }
                
            }
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
