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
import EnginerProfil from './pages/EnginerProfil';
import ProjectManagerProfil from './pages/ProjectManagerProfil';
import CareerPage from './pages/CareerPage';
import EmployeeProjects from './pages/EmployeeProjects';
import ManagerProjects from './pages/ManagerProjects';
import Protected from './authorization/Protected';


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
          <Route path="/ViewAllRegistrationRequests" element={<Protected role={"ADMINISTRATOR"}><ViewRegistrationRequests/></Protected>} />
          <Route path="/ViewAll" element={<Protected role={"ADMINISTRATOR"}><ViewAllEmployees/></Protected>} />
          <Route path="/ViewAllProjects" element={<Protected role={"ADMINISTRATOR"}><Projects/></Protected>} />
          <Route path="/profil" element={<Profil/>} />
          <Route path="/createProject" element={<Protected role={"ADMINISTRATOR"}><CreateProject/></Protected>} />
          <Route path="/register" element={<Register/>} />
          <Route path="/registerAdmin" element={<Protected role={"ADMINISTRATOR"}><RegisterAdmin/></Protected>} />

          <Route path="/" element={<Home/>} />
          <Route path="/login" element={<Login/>} />
          <Route path="/passwordless-login" element={<PasswordlessLogin/>} />
          <Route path="/wait-room" element={<WaitRoom/>} />
          <Route path="/session-expired" element={<SessionExpired/>} />
          <Route path="/token-expired" element={<TokenExpired/>} />
          <Route path="/permissions" element={<Protected role={"ADMINISTRATOR"}><RolePermissions/></Protected>} />
          <Route path="/enginerProfil" element={<Protected role={"SOFTWARE_ENGINEER"}><EnginerProfil/></Protected>} />
          <Route path="/projectManagerProfil" element={<Protected role={"PROJECT_MANAGER"}><ProjectManagerProfil/></Protected>} />
          <Route path="/careerPage" element={<Protected role={"SOFTWARE_ENGINEER"}><CareerPage/></Protected>} />
          <Route path="/employeeProjects" element={<Protected role={"SOFTWARE_ENGINEER"}><EmployeeProjects/></Protected>} />
          <Route path="/managerProjects" element={<Protected role={"PROJECT_MANAGER"}><ManagerProjects/></Protected>} />
        </Routes>
      </div>
    </>
  )
}
export default App;

