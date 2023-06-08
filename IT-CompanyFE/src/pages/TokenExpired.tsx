import { useEffect } from 'react';

function TokenExpired() {
  useEffect(() => {
    const loggedUser = localStorage.getItem('loggedUser');

    if (loggedUser) {
      localStorage.setItem('loggedUser', '');
    }
  }, []);

  return (
    <div className="container d-flex align-items-center justify-content-center vh-50">
      <div className="card mt-5">
        <div className="card-body text-center">
          <h1 className="card-title">Link is not valid!</h1>
          {/* Dodajte dodatne elemente ovisno o strukturi vaših podataka */}
        </div>
      </div>
    </div>
  );
}

export default TokenExpired;
