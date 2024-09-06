import './App.css';
import {BrowserRouter as Router, Route, Routes, Link} from 'react-router-dom';
import Login from './page/Login';
import Signup from './page/Signup';

function App() {
  return (
    <Router>
      <div>
        <nav>
          <ul>
            <li>
              <Link to="/auth/login">Login</Link>
            </li>
            <li>
              <Link to="/auth/signup">Signup</Link>
            </li>
          </ul>
        </nav>
      </div>
      <Routes>
        <Route path="/auth/login" element={<Login />}></Route>
        <Route path="/auth/signup" element={<Signup />}></Route>
      </Routes>
    </Router>
  );
}

export default App;
