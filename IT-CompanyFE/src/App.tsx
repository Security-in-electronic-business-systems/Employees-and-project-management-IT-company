
import { Routes, Route } from 'react-router-dom';
import Register from './pages/Register';
import Navbar from './Navbar';

function App(){

  return (
    <>
      <Navbar/>
      <div>
        <Routes>
          <Route path="/register" element={<Register/>} />
        </Routes>
      </div>
    </>
  )
}

export default App;
