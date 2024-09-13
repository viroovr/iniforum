import './App.css';
import {BrowserRouter as Router, Route, Routes, Link} from 'react-router-dom';
import Login from './page/Login';
import Signup from './page/Signup';
import Logout from './page/Logout';
import Post from './page/Post';
import QuestionList from './page/QuestionList';
import QuestionDetail from './page/QuestionDetail';

function App() {
  return (
    <Router>
      <div>
        <nav>
          <ul>
            <li>
              <Link to="/login">로그인</Link>
            </li>
            <li>
              <Link to="/signup">회원가입</Link>
            </li>
            <li>
              <Link to="/logout">Logout</Link>
            </li>
            <li>
              <Link to="/questions">Questions</Link>
            </li>
          </ul>
        </nav>
      </div>
      <Routes>
        <Route path="/login" element={<Login />}></Route>
        <Route path="/signup" element={<Signup />}></Route>
        <Route path="/logout" element={<Logout />}></Route>
        <Route path="/questions" element={<QuestionList />} />
        <Route path="/questions/:id" element={<QuestionDetail />} />
        <Route path="/post" element={<Post />} />
      </Routes>
    </Router>
  );
}

export default App;
