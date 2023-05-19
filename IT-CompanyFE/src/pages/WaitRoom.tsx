import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { LoginResponse } from '../model/login-response';

function WaitRoom() {
  const [data, setData] = useState(new LoginResponse());
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch('https://localhost:8081/api/v1/auth/getLoginResponse', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: "include"
        });

        if (response.ok) {
          const responseData = await response.json();
          setData(responseData);
          localStorage.setItem('loggedUser', JSON.stringify(responseData));
          navigate("/wait-room");
        } else {
          console.error('Error:', response.status);
        }
      } catch (error) {
        console.error('Error:', error);
      }
    };

    fetchData();
  }, []);

  return (
    <div className="container d-flex align-items-center justify-content-center vh-50">
      {data ? (
        <div className="card mt-5">
          <div className="card-body text-center">
            <h1 className="card-title">Welcome, {data.firstname} {data.lastname}!</h1>
            <p className="card-text">You have successfully logged in.</p>
            {/* Dodajte dodatne elemente ovisno o strukturi vaših podataka */}
          </div>
        </div>
      ) : (
        <p className="text-center">Loading...</p>
      )}
    </div>
  );
}

export default WaitRoom;
