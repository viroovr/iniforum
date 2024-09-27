import './App.css';
import {BrowserRouter as Router, useNavigate} from 'react-router-dom';
import { useEffect, useState } from 'react';
import { setuoAxiosInterceptors } from './excption/setupAxiosInterceptors';
import { ToastContainer } from 'react-toastify';
import Navbar from './compontents/Navbar';
import AppRoutes from './compontents/AppRoutes';

function Root() {
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('jwtToken'));

  useEffect(() => {
    setuoAxiosInterceptors(navigate);

    const handleStorageChange = () => {
      setIsLoggedIn(!!localStorage.getItem('jwtToken'));
    }

    window.addEventListener('storage', handleStorageChange);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
    };
  }, []);


  return (
    <div>
      <Navbar isLoggedIn={isLoggedIn} />
      <AppRoutes onLogin={() => setIsLoggedIn(true)} onLogout={() => setIsLoggedIn(false)} />
      <ToastContainer />
    </div>
  );
}

function App() {
  return (
    <Router>
      <Root />
    </Router>
  );
}

export default App;
