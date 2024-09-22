import './App.css';
import {BrowserRouter as Router, Route, Routes, Link, useNavigate} from 'react-router-dom';
import Login from './page/Login';
import Signup from './page/Signup';
import Logout from './page/Logout';
import Post from './page/Post';
import QuestionList from './page/QuestionList';
import QuestionDetail from './page/QuestionDetail';
import { useEffect, useState } from 'react';
import apiClient, { setuoAxiosInterceptors } from './excption/setupAxiosInterceptors';
import { ToastContainer } from 'react-toastify';
import QuestionEdit from './page/QuestionEdit';

function Root() {
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('jwtToken'));

  useEffect(() => {
    setuoAxiosInterceptors(navigate);
  }, [navigate]);

  useEffect(() => {
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
      <div>
        <nav>
          <ul>
            {!isLoggedIn ? (
              <>
              <li><Link to="/login">로그인</Link></li>
              <li><Link to="/signup">회원가입</Link></li>
              </>
            ): (
              <>
              <li><Link to="/logout">로그아웃</Link></li>
              </>
            )}
            
            <li>
              <Link to="/questions">게시판</Link>
            </li>
          </ul>
        </nav>
      </div>
      <Routes>
          <Route path="/login" element={<Login onLogin={() => setIsLoggedIn(true)}/>}></Route>
          <Route path="/signup" element={<Signup />}></Route>
          <Route path="/logout" element={<Logout onLogout={() => setIsLoggedIn(false)} />}></Route>
          <Route path="/questions" element={<QuestionList />} />
          <Route path="/questions/:id" element={<QuestionDetail />} />
          <Route path="/post" element={<Post />} />
          <Route path='/:id/edit' element={<QuestionEdit />} />
      </Routes>
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
