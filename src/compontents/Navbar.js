import { Link } from "react-router-dom"
import './Navbar.css';

const Navbar = ( {isLoggedIn} ) => {
    return (
        <nav className="navbar">
            <div className="navbar-container">
                <h1 className="navbar-title">Ini Forum</h1>
                <ul className="navbar-links">
                    {!isLoggedIn ? (
                        <>
                            <li><Link to="/login">로그인</Link></li>
                            <li><Link to="/signup">회원가입</Link></li>
                        </>
                    ): (
                        <li><Link to="/logout">로그아웃</Link></li>
                    )}
                    <li>
                        <Link to="/questions">게시판</Link>
                    </li>
                </ul>
          </div>
        </nav>
    )
}

export default Navbar;