import { useEffect } from "react";
import {Route, Routes} from "react-router-dom"
import Home from "./pages/Home";
import { Login } from "./pages/Login";




function App(){
  useEffect(() => {
    document.title = "IT-Company";
  }, []);

  return (
    <>
      <div>
        <Routes>
          <Route path="/" element={<Home/>} />
          <Route path="/login" element={<Login/>} />
        </Routes>
      </div>
    </>
  )
}

export default App;