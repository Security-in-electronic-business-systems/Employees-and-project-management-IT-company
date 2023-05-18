function UnauthorizedAccess() {
  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card mt-5">
            <div className="card-body text-center">
              <h1 className="card-title">You don't have permission to access this page!</h1>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default UnauthorizedAccess;
