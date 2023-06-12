import {  SyntheticEvent, useState } from "react";
import Toast from "react-bootstrap/esm/Toast";
import { useNavigate } from "react-router-dom";

export function QRLog() {
    const [email, setEmail] = useState("");
    const [toastMessage, setToastMessage] = useState("");
    const [showToast, setShowToast] = useState(false);
    const navigate = useNavigate();
    const handleSubmit = async (event: SyntheticEvent) => {
        event.preventDefault();
        await fetch("https://localhost:8081/api/v1/auth/qr", {
          method: "POST",
          headers: {
            "Accept": "application/json",
            "Content-Type": "application/json" 
          },
          credentials: "include",
          body: JSON.stringify({
            email: email,
          }),
        })
        .then((response) => {
          return response.text();
        })
        .then((text) => {
          setToastMessage(text);
          setShowToast(true);
          setTimeout(() => {
          setShowToast(false);
          }, 3000);
          navigate("/login");
        })
          .catch((error) => {
            console.error("Fetch error:", error);
          });
    }

return (
    <div className="d-flex align-items-center justify-content-center" style={{ height: "80vh" }}>
    <form className="col-md-3" onSubmit={handleSubmit}>
        <blockquote className="blockquote text-center">
        <p className="mb-0">QR Login</p>
        </blockquote>
        <div className="mb-3">
        <label className="form-label">Email</label>
        <input
            className="form-control"
            id="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
        />
        </div>
        <button type="submit" className="btn btn-primary btn-sm">Confirm</button>
    </form>
    <Toast show={showToast} onClose={() => setShowToast(false)} delay={3000} autohide className="custom-toast">
                <Toast.Header closeButton={false}>
                <strong className="me-auto">Toast Poruka</strong>
                </Toast.Header>
                <Toast.Body>{toastMessage}</Toast.Body>
            </Toast>
    </div>
  );
}