import { Route, Routes } from "react-router-dom"
import Login from "../page/Login"
import Logout from "../page/Logout"
import Post from "../page/Post"
import QuestionDetail from "../page/QuestionDetail"
import QuestionEdit from "../page/QuestionEdit"
import QuestionList from "../page/QuestionList"
import Signup from "../page/Signup"
import ProfileForm from "../page/ProfileForm"

const AppRoutes = ({ onLogin, onLogout}) => {
    return (
        <Routes>
          <Route path="/login" element={<Login onLogin={onLogin}/>}></Route>
          <Route path="/signup" element={<Signup />}></Route>
          <Route path="/logout" element={<Logout onLogout={onLogout} />}></Route>
          <Route path="/questions" element={<QuestionList />} />
          <Route path="/questions/:id" element={<QuestionDetail />} />
          <Route path="/post" element={<Post />} />
          <Route path='/:id/edit' element={<QuestionEdit />} />
          <Route path='/profile' element={<ProfileForm />} />
      </Routes>
    )
};

export default AppRoutes;