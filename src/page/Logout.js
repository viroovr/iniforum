import { useEffect } from "react";
import apiClient from "../excption/setupAxiosInterceptors";
import { useNavigate } from "react-router-dom";

function Logout ({onLogout}) {
    const navigate = useNavigate();

    useEffect(() => {
        const handleLogout = async () => {
            try{
                await apiClient.post('/auth/logout');
                localStorage.removeItem('jwtToken');
                onLogout();
                navigate("/login");
            } catch (error) {
                console.error('Logout failed:', error);
            }
        };
        handleLogout();

    }, [onLogout]);

    
    
    return (
        <div>
            <h1>Logging Out..</h1>
        </div>
    );
}

export default Logout;