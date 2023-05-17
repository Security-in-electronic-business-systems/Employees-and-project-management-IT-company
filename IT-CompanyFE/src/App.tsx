import { useEffect } from "react";
import ViewAllEmployees from "./pages/ViewAllEmployees";
import { Route, Routes } from "react-router-dom";



function App(){
  useEffect(() => {
    document.title = "IT";
  }, []);

  return (
    <>
      <div>
        <Routes>
          <Route path="/ViewAll" element={<ViewAllEmployees/>} />
        </Routes>
      </div>
    </>
  )
}

export default App;