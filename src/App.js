import './App.css';
import {BrowserRouter as Router, Route, Routes, Link} from 'react-router-dom';
import Login from './page/Login';
import Signup from './page/Signup';
import Posts from './page/Posts';
import Logout from './page/Logout';

function App() {
  return (
    <Router>
      <div>
        <nav>
          <ul>
            <li>
              <Link to="/login">Login</Link>
            </li>
            <li>
              <Link to="/signup">Signup</Link>
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
        <Route path="/questions" element={<Posts />} />
      </Routes>
    </Router>
  );
}

export default App;
