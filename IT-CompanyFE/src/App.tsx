import { Routes, Route } from 'react-router-dom';
import Register from './pages/Register';
import Navbar from './Navbar';
import { useEffect } from "react";
import Home from "./pages/Home";
import { Login } from "./pages/Login";
import { PasswordlessLogin } from "./pages/PasswordlessLogin";
import ViewAllEmployees from './pages/ViewAllEmployees';
import WaitRoom from './pages/WaitRoom';

function App(){
  useEffect(() => {
    document.title = "IT-Company";
  }, []);

  return (
    <>
      <Navbar/>
      <div>
        <Routes>
          <Route path="/ViewAll" element={<ViewAllEmployees/>} />
          <Route path="/register" element={<Register/>} />
          <Route path="/" element={<Home/>} />
          <Route path="/login" element={<Login/>} />
          <Route path="/passwordless-login" element={<PasswordlessLogin/>} />
          <Route path="/wait-room" element={<WaitRoom/>} />
        </Routes>
      </div>
    </>
  )
}
export default App;

