import { Routes, Route } from 'react-router-dom';
import Register from './pages/Register';
import Navbar from './Navbar';
import { useEffect } from "react";
import Home from "./pages/Home";
import { Login } from "./pages/Login";
import { PasswordlessLogin } from "./pages/PasswordlessLogin";
import ViewAllEmployees from './pages/ViewAllEmployees';
import WaitRoom from './pages/WaitRoom';
import CreateProject from './pages/CreateProject';
import Projects from './pages/Projects';
import Profil from './pages/Profil';
import SessionExpired from './pages/SessionExpired';
import TokenExpired from './pages/TokenExpired';import RegisterAdmin from './pages/RegisterAdmin';
import ViewRegistrationRequests from './pages/ViewRegistrationRequests';
import HMACVerification from './pages/HMACVerification';
import RolePermissions from './pages/RolePermissions';
0

function App(){
  useEffect(() => {
    document.title = "IT-Company";
  }, []);

  return (
    <>
      <Navbar/>
      <div>
        <Routes>
          <Route path="/hmac" element={<HMACVerification/>} />
          <Route path="/ViewAllRegistrationRequests" element={<ViewRegistrationRequests/>} />
          <Route path="/ViewAll" element={<ViewAllEmployees/>} />
          <Route path="/ViewAllProjects" element={<Projects/>} />
          <Route path="/profil" element={<Profil/>} />
          <Route path="/createProject" element={<CreateProject/>} />
          <Route path="/register" element={<Register/>} />
          <Route path="/registerAdmin" element={<RegisterAdmin/>} />
          <Route path="/" element={<Home/>} />
          <Route path="/login" element={<Login/>} />
          <Route path="/passwordless-login" element={<PasswordlessLogin/>} />
          <Route path="/wait-room" element={<WaitRoom/>} />
          <Route path="/session-expired" element={<SessionExpired/>} />
          <Route path="/token-expired" element={<TokenExpired/>} />
          <Route path="/permissions" element={<RolePermissions/>} />
        </Routes>
      </div>
    </>
  )
}
export default App;

