import { useEffect } from "react";
import apiClient from "../excption/setupAxiosInterceptors";

function Logout ({onLogout}) {
    useEffect(() => {
        const handleLogout = async () => {
            try{
                await apiClient.post('/auth/logout');
                localStorage.removeItem('jwtToken');
                onLogout();
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