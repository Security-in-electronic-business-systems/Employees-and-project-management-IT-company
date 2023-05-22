import { SyntheticEvent, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { FaChalkboardTeacher, FaEnvelope, FaPhone, FaSearchLocation, FaUser, FaUserTag } from "react-icons/fa";

function Profil() {
  const [name, setName] = useState("");
  const [surname, setSurname] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [title, setTitle] = useState("");
  const [country, setCountry] = useState("");
  const [city, setCity] = useState("");
  const [street, setStreet] = useState("");
  const [number, setNumber] = useState("");
  const [role, setRole] = useState("");
  const [isEditMode, setIsEditMode] = useState(false); // Stanje za praćenje da li je uključen način izmene

  const navigate = useNavigate();

  // Simulacija dohvatanja podataka o korisniku
  useEffect(() => {
    const fetchUserData = async () => {
      const userData = await fetch("https://localhost:8081/api/v1/user/get", {
        method: "GET",
        credentials: "include",
      }).then((response) => response.json());

      // Postavite podatke o korisniku u odgovarajuća stanja
      setName(userData.firstname);
      setSurname(userData.lastname);
      setEmail(userData.email);
      setPhoneNumber(userData.phoneNumber);
      setTitle(userData.title);
      setCountry(userData.address.country);
      setCity(userData.address.city);
      setStreet(userData.address.street);
      setNumber(userData.address.number);
      setRole(userData.role);
    };

    fetchUserData();
  }, []);

  const handleSubmit = async (event: SyntheticEvent) => {
    event.preventDefault();


    await fetch("https://localhost:8081/api/v1/user/update", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        email: email,
        firstname: name,
        lastname: surname,
        phoneNumber: phoneNumber,
        title: title,
        address: { country: country, city: city, street: street, number: number },
        role: role,
      }),
    })
      .then((response) => {
        if (response.ok) {
          setIsEditMode(false); // Isključite način izmene nakon uspešnog čuvanja
        }
      })
      .catch((error) => console.log(error));
  };



 

  // Funkcija za rukovanje prelaskom nazad na pregled korisnika
  const handleCancel = () => {
    setIsEditMode(false); // Isključite način izmene
    navigate("/profil"); // Navigirajte nazad na pregled korisnika
  };

  return (
    <form className="col-md-6 mx-auto" onSubmit={handleSubmit}>
      <div className="row mb-3">
        <div className="col">
          <label className="form-label" htmlFor="name">
            <FaUser className="me-2" /> Name
          </label>
          <input
            className="form-control"
            id="name"
            value={name}
            onChange={(event) => setName(event.target.value)}
            required
            disabled={!isEditMode} // Onemogućite unos u načinu prikaza
          />
        </div>
        <div className="col">
          <label className="form-label" htmlFor="surname">
            <FaUser className="me-2" /> Surname
          </label>
          <input
            className="form-control"
            id="surname"
            value={surname}
            onChange={(event) => setSurname(event.target.value)}
            required
            disabled={!isEditMode} // Onemogućite unos u načinu prikaza
          />
        </div>
      </div>
      <div className="mb-3">
        <label className="form-label" htmlFor="email">
          <FaEnvelope className="me-2" /> Email address
        </label>
        <input
          className="form-control"
          id="email"
          type="email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          required
          disabled // Onemogućite unos e-pošte
        />
      </div>

      <div className="mb-3">
        <label className="form-label" htmlFor="phoneNumber">
          <FaPhone className="me-2" /> Phone number
        </label>
        <input
          className="form-control"
          id="phoneNumber"
          type="text"
          value={phoneNumber}
          onChange={(event) => setPhoneNumber(event.target.value)}
          required
          disabled={!isEditMode} // Onemogućite unos u načinu prikaza
        />
      </div>
      <div className="row mb-3">
        <div className="col">
          <label className="form-label">
            <FaChalkboardTeacher className="me-2" />Professional title
          </label>
          <input
            className="form-control"
            id="title"
            type="text"
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            required
            disabled={!isEditMode} // Onemogućite unos u načinu prikaza
          />
        </div>
        <div className="col">
          <label className="form-label">
            <FaUserTag className="me-2" />Role
          </label>
          <select
            className="form-control"
            id="role"
            onChange={(event) => setRole(event.target.value)}
            required
            disabled={!isEditMode} // Onemogućite unos u načinu prikaza
          >
            <option value="SOFTWARE_ENGINEER">SOFTWARE_ENGINEER</option>
            <option value="PROJECT_MANAGER">PROJECT_MANAGER</option>
            <option value="HR_MANAGER">HR_MANAGER</option>
          </select>
        </div>
      </div>
      <div className="row mb-3">
        <div className="col">
          <label className="form-label" htmlFor="country">
            <FaSearchLocation className="me-2" /> Country
          </label>
          <input
            className="form-control"
            id="country"
            value={country}
            onChange={(event) => setCountry(event.target.value)}
            required
            disabled={!isEditMode} // Onemogućite unos u načinu prikaza
          />
        </div>
        <div className="col">
          <label className="form-label" htmlFor="city">
            {" "}
            City
          </label>
          <input
            className="form-control"
            id="city"
            value={city}
            onChange={(event) => setCity(event.target.value)}
            required
            disabled={!isEditMode} // Onemogućite unos u načinu prikaza
          />
        </div>
        <div className="col">
          <label className="form-label" htmlFor="street">
            Street
          </label>
          <input
            className="form-control"
            id="street"
            value={street}
            onChange={(event) => setStreet(event.target.value)}
            required
            disabled={!isEditMode} // Onemogućite unos u načinu prikaza
          />
        </div>
        <div className="col">
          <label className="form-label" htmlFor="number">
            Number
          </label>
          <input
            className="form-control"
            id="number"
            value={number}
            onChange={(event) => setNumber(event.target.value)}
            required
            disabled={!isEditMode} // Onemogućite unos u načinu prikaza
          />
        </div>
      </div>
      {!isEditMode && (
        <button className="btn btn-primary me-2" onClick={() => setIsEditMode(true)}>
          Edit
        </button>
      )}
      {isEditMode && (
        <>
          <button className="btn btn-primary me-2" type="submit">
            Save
          </button>
          <button className="btn btn-secondary" onClick={handleCancel}>
            Cancel
          </button>
        </>
      )}
    </form>
  );
}

export default Profil;
