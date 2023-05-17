import { Link, useNavigate  } from 'react-router-dom';
  

function Navbar() {
    const navigate = useNavigate();

    const stylesRight = {
      marginLeft: 'auto',
    };

  const stylesLeft  = {
      marginRight: 'auto',
    };

  const navStyle = {
      display: 'flex',
      justifyContent: 'space-between',
  }

  const handleButtonClick = () => {
    navigate("/register");
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light" style={navStyle}>
      <ul className="navbar-nav mr-auto" style={stylesLeft}>
        <li className="nav-item active">
          <Link className="nav-link" to="/">Home</Link>
        </li>
      </ul>
      <ul className="navbar-nav mr-auto" style={stylesRight}>
        <li className="nav-item active" >
            <button className="btn btn-outline-success my-2 my-sm-0" type="submit" onClick={handleButtonClick}>Register</button>
        </li>
      </ul>
    </nav>
  );
}

export default Navbar;