import { SyntheticEvent, useState } from "react";
import { FaCode, FaLink, FaPhone } from "react-icons/fa";
import Toast from 'react-bootstrap/Toast';
import '../App.css';


export function HMACVerification() {
    const [code, setCode] = useState("");
    const [number, setNumber] = useState("");
    const [link, setLink] = useState("");
    const [showToast, setShowToast] = useState(false);
    const [toastMessage, setToastMessage] = useState("");

    const handleSubmit = async (event: SyntheticEvent) => {
        event.preventDefault();

        await fetch("https://localhost:8081/api/v1/user/registration/hmac", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            credentials: "include",
            body: JSON.stringify({
              code: code,
              link: link,
              phoneNumber: number,
            }),
          })
            .then((response) => {
              return response.text(); // Pristupanje telu odgovora kao tekstu
            })
            .then((text) => {
                setToastMessage(text);
                setShowToast(true);
                setTimeout(() => {
                  setShowToast(false);
                }, 3000);
            })
            .catch((error) => console.log(error));
        };


    return(
    <div>

            <form className="col-md-6 mx-auto" onSubmit={handleSubmit}>
            <blockquote className="blockquote text-center">
                <p className="mb-0">Check integrity of verification link!</p>
                <footer className="blockquote-footer">Be safe</footer>
            </blockquote>

            <div className="mb-3">
                <label className="form-label" htmlFor="email">
                <FaCode className="me-2" /> HMAC code
                </label>
                <input 
                className="form-control"
                id="code" 
                type="text"
                value={code}
                onChange={(event) => setCode(event.target.value)} required
                />
            </div>

            <div className="mb-3">
                <label className="form-label" htmlFor="email">
                <FaLink className="me-2" /> Link
                </label>
                <input 
                className="form-control"
                id="link" 
                type="text"
                value={link}
                onChange={(event) => setLink(event.target.value)} required
                />
            </div>

            <div className="mb-3">
                <label className="form-label" >
                <FaPhone className="me-2" /> Phone number
                </label>
                <input 
                className="form-control"
                id="password" 
                type="text"
                value={number}
                onChange={(event) => setNumber(event.target.value)} required
                />
            </div>

            <button type="submit" className="btn btn-primary">Submit</button>

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

export default HMACVerification;

