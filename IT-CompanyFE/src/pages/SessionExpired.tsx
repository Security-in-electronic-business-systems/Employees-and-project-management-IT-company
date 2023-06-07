function SessionExpired() {

  return (
    <div className="container d-flex align-items-center justify-content-center vh-50">
        <div className="card mt-5">
          <div className="card-body text-center">
            <h1 className="card-title">Session expired, please login again!</h1>
            {/* Dodajte dodatne elemente ovisno o strukturi vaših podataka */}
          </div>
        </div>
    </div>
  );
}

export default SessionExpired;
